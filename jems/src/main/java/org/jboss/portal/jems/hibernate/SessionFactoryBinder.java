/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2006, Red Hat Middleware, LLC, and individual                    *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/
package org.jboss.portal.jems.hibernate;

import bsh.EvalError;
import bsh.Interpreter;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.exception.SQLGrammarException;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.jboss.portal.common.io.IOTools;
import org.jboss.portal.common.net.URLTools;
import org.jboss.portal.common.util.CLResourceLoader;
import org.jboss.portal.common.util.LoaderResource;
import org.jboss.portal.jems.as.system.AbstractJBossService;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.Collection;
import java.util.Iterator;

/**
 * Configures and binds the hibernate session factory.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @author <a href="mailto:boleslaw.dawidowicz@jboss.com">Boleslaw Dawidowicz</a>
 * @version $Revision: 12426 $
 */
public class SessionFactoryBinder extends AbstractJBossService implements HibernateProvider
{

   /** doCheck result - schema ok */
   private static final int RESULT_NONE = 0;

   /** doCheck result - schema need updates */
   private static final int RESULT_UPDATE = 1;

   /** doCheck result - schema not exist */
   private static final int RESULT_CREATE = 2;

   /** . */
   private String configLocation;

   /** . */
   private String setupLocation;

   /** If true checks the schema existence on start and create it if necessary. */
   private boolean doChecking;

   /** The session factory. */
   private SessionFactory sessionFactory;

   /** Where we configure hibernate. */
   private URL configURL;

   /** . */
   private LoaderResource setupResource;

   /** The hibernate configuration object. */
   protected Configuration config;

   /**
    *
    */
   private String dialectName;

   /**
    *
    */
   protected String jndiName;

   public boolean getDoChecking()
   {
      return doChecking;
   }

   public void setDoChecking(boolean doChecking)
   {
      this.doChecking = doChecking;
   }

   public String getConfigLocation()
   {
      return configLocation;
   }

   public void setConfigLocation(String configLocation)
   {
      this.configLocation = configLocation;
   }

   public String getSetupLocation()
   {
      return setupLocation;
   }

   public void setSetupLocation(String setupLocation)
   {
      this.setupLocation = setupLocation;
   }

   public URL getConfigURL()
   {
      return configURL;
   }

   public LoaderResource getSetupResource()
   {
      return setupResource;
   }

   public Configuration getConfig()
   {
      return config;
   }

   public SessionFactory getSessionFactory()
   {
      return sessionFactory;
   }

   public String getDialectName()
   {
      return dialectName;
   }

   public String getJNDIName()
   {
      return jndiName;
   }

   public void setJNDIName(String jndiName)
   {
      this.jndiName = jndiName;
   }

   /** During this step the hibernate configuration is created. */
   protected void createService() throws Exception
   {
      // Setup URLs
      if (configLocation == null)
      {
         throw new Exception("The config location is null");
      }
      configURL = Thread.currentThread().getContextClassLoader().getResource(configLocation);
      if (configURL == null)
      {
         throw new Exception("The config " + configLocation + " does not exist");
      }
      if (!URLTools.exists(configURL))
      {
         throw new Exception("The config " + configURL + " does not exist");
      }

      //
      if (setupLocation != null)
      {
         setupResource = new CLResourceLoader().getResource(setupLocation);
      }

      // Perform configuration
      config = new Configuration();
      config.configure(configURL);

      // Force transaction manager lookup class and JTA env
      setPropertyIfAbsent("transaction.auto_close_session", "true");
      setPropertyIfAbsent("transaction.flush_before_completion", "true");
      setPropertyIfAbsent("hibernate.transaction.flush_before_completion", "true");
      setPropertyIfAbsent("hibernate.transaction.factory_class", "org.hibernate.transaction.JTATransactionFactory");
      setPropertyIfAbsent("hibernate.transaction.manager_lookup_class", "org.hibernate.transaction.JBossTransactionManagerLookup");

      // Set JNDI name if present and absent
      if (jndiName != null)
      {
         setPropertyIfAbsent("hibernate.session_factory_name", jndiName);
      }
   }

   private void setPropertyIfAbsent(String name, String value)
   {
      if (config.getProperty(name) == null)
      {
         config.setProperty(name, value);
      }
   }

   /** During this step the session factory is created and the content creation is triggered. */
   protected void startService() throws Exception
   {
      // Detect the dialect if necessary
      dialectName = config.getProperty(Environment.DIALECT);
      if (dialectName == null)
      {
         String dataSourceJNDI = config.getProperty(Environment.DATASOURCE);
         log.debug("Detecting dialect with datasource " + dataSourceJNDI + " ...");
         DataSource ds = (DataSource)new InitialContext().lookup(dataSourceJNDI);
         Connection conn = null;
         try
         {
            conn = ds.getConnection();
            DatabaseMetaData meta = conn.getMetaData();
            String databaseName = meta.getDatabaseProductName();
            int databaseMajorVersion = getDatabaseMajorVersion(meta);
            dialectName = DialectFactory.determineDialect(databaseName, databaseMajorVersion);
            config.setProperty(Environment.DIALECT, dialectName);
            log.debug("Detected dialect " + dialectName + ", database is (" + databaseName + "," + databaseMajorVersion + ")");
         }
         finally
         {
            IOTools.safeClose(conn);
         }
      }
      log.debug("Using dialect " + dialectName);
      if ("org.hibernate.dialect.HSQLDialect".equals(dialectName))
      {
         log.warn("You are using the file based HSQL database, this is not recommended on a production environment and will not work properly on a clustered environment.");
      }
      
      //
      createSessionFactory();

      if (doChecking)
      {
         //check the schema
         int check = doCheck();
         switch (check)
         {
            case RESULT_NONE:
               break;
            case RESULT_UPDATE:
               updateSchema();
               break;
            case RESULT_CREATE:
               createSchema();
               createContent();
               break;
         }
      }
   }

   /** During this step the session factory is destroyed. */
   protected void stopService() throws Exception
   {
      destroySessionFactory();
   }

   /** During this step the hibernate config is unreferenced. */
   protected void destroyService() throws Exception
   {
      config = null;
   }

   public int doCheck()
   {
      Session session = null;
      int numOfChecks = 0;
      int bad = 0;
      try
      {
         session = sessionFactory.openSession();
         Collection values = sessionFactory.getAllClassMetadata().values();
         numOfChecks = values.size();
         for (Iterator i = values.iterator(); i.hasNext();)
         {
            ClassMetadata cmd = (ClassMetadata)i.next();
            Query query = session.createQuery("from " + cmd.getEntityName());
            query.setFirstResult(0);
            query.setMaxResults(0);
            try
            {
               query.list();
            }
            catch (SQLGrammarException e)
            {
               // We consider that exception means that the schema does not exist
               bad++;
            }
         }
      }
      finally
      {
         IOTools.safeClose(session);
      }
      // There was no sql grammar exception - schema is ok!
      if (bad == 0)
      {
         log.debug("The schema was checked as valid");
         //do nothing
         return RESULT_NONE;
      }
      // There is no existing valid schema;
      else if (bad == numOfChecks)
      {
         log.debug("The schema was checked as not exists");
         // Totaly invalid schema
         return RESULT_CREATE;
      }
      // Schema is partialy corrupted
      else if (bad < numOfChecks)
      {
         // Schema needs updates;
         log.debug("The schema was checked as need updates");
         return RESULT_UPDATE;
      }

      // If here something gone wrong...
      log.debug("The schema was checked as need to be created");
      return RESULT_CREATE;
   }


   public void createSchema()
   {
      log.debug("Creating database schema");
      try
      {
         SchemaExport export = new SchemaExport(config);
         export.create(false, true);
      }
      catch (Exception e)
      {
         log.error("Cannot create schema", e);
      }
   }

   public void destroySchema()
   {
      log.debug("Destroying database schema");
      try
      {
         SchemaExport export = new SchemaExport(config);
         export.drop(false, true);
      }
      catch (Exception e)
      {
         log.error("Cannot destroy schema", e);
      }
   }

   public void createContent()
   {
      if (setupResource != null)
      {
         if (setupResource.exists())
         {
            try
            {
               log.info("Creating database content");
               String script = setupResource.asString("UTF-8");

               // Create an interpreter and configures it
               Interpreter interpreter = new Interpreter();
               interpreter.setClassLoader(Thread.currentThread().getContextClassLoader());
               interpreter.setOut(System.out);
               interpreter.set("SessionFactory", sessionFactory);
               interpreter.eval(script);
            }
            catch (EvalError e)
            {
               log.error("Error in the bsh script", e);
            }
            catch (IllegalStateException e)
            {
               log.error("Cannot load setup script", e);
            }
         }
         else
         {
            log.warn("There is a setup URL but the not valid " + setupResource);
         }
      }
   }

   public void updateSchema()
   {
      log.debug("Updating database schema");
      SchemaUpdate update = new SchemaUpdate(config);
      update.execute(false, true);
   }

   /** Create the session factory. */
   protected void createSessionFactory() throws Exception
   {
      sessionFactory = config.buildSessionFactory();
   }

   /** Close the session factory if it is not null. */
   protected void destroySessionFactory()
   {
      if (sessionFactory != null)
      {
         sessionFactory.close();
         sessionFactory = null;
      }
      else
      {
         log.debug("No session factory to close");
      }
   }

   private int getDatabaseMajorVersion(DatabaseMetaData meta)
   {
      try
      {
         Method gdbmvMethod = DatabaseMetaData.class.getMethod("getDatabaseMajorVersion", null);
         return ((Integer)gdbmvMethod.invoke(meta, null)).intValue();
      }
      catch (NoSuchMethodException nsme)
      {
         return 0;
      }
      catch (Throwable t)
      {
         log.debug("could not get database version from JDBC metadata");
         return 0;
      }
   }

}

