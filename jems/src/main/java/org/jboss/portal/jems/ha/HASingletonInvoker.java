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
package org.jboss.portal.jems.ha;

import org.jboss.ha.singleton.HASingletonSupport;
import org.jboss.invocation.Invocation;
import org.jboss.naming.Util;

import javax.management.ObjectName;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class HASingletonInvoker extends HASingletonSupport implements HASingletonInvokerMBean
{
   /** The proxy factory. */
   private ObjectName proxyFactory;

   /** The cached proxy. */
   private Proxy proxy;

   /** Where is bound the ha proxy. */
   private String jndiName;

   /** The callback service which this service depends on. */
   private Callback callback;

   /** The JNDI properties. */
   private Properties jndiProperties;

   /** How many time we retry on fail over. */
   private int maxRetries;

   /** How long we wait before trying a jndi lookup during fail over. */
   private long retryWaitingTimeMS;

   public long getRetryWaitingTimeMS()
   {
      return retryWaitingTimeMS;
   }

   public void setRetryWaitingTimeMS(long retryWaitingTimeMS)
   {
      this.retryWaitingTimeMS = retryWaitingTimeMS;
   }

   public int getMaxRetries()
   {
      return maxRetries;
   }

   public void setMaxRetries(int maxRetries)
   {
      this.maxRetries = maxRetries;
   }

   public Properties getJNDIProperties()
   {
      return jndiProperties;
   }

   public void setJNDIProperties(Properties jndiProperties)
   {
      this.jndiProperties = jndiProperties;
   }

   public ObjectName getProxyFactory()
   {
      return proxyFactory;
   }

   public void setProxyFactory(ObjectName proxyFactory)
   {
      this.proxyFactory = proxyFactory;
   }

   public Callback getCallback()
   {
      return callback;
   }

   public void setCallback(Callback callback)
   {
      this.callback = callback;
   }

   public String getJNDIName()
   {
      return jndiName;
   }

   public void setJNDIName(String jndiName)
   {
      this.jndiName = jndiName;
   }

   public Proxy lookupProxy()
   {
      if (proxy == null)
      {
         try
         {
            InitialContext ctx = new InitialContext(jndiProperties);
            proxy = (Proxy)ctx.lookup(jndiName);
         }
         catch (NamingException e)
         {
            log.error("Proxy " + jndiName + " not bound");
         }
      }
      return proxy;
   }

   public Object invoke(String methodName, Class[] types, Object[] args) throws Exception
   {
      int retries = maxRetries;
      while (retries-- > 0)
      {
         try
         {
            Proxy proxy = lookupProxy();
            return proxy.invoke(methodName, types, args);
         }
         catch (Throwable t)
         {
            if (!(t instanceof Error))
            {
               proxy = null;
               if (retries > 0)
               {
                  log.error("Cannot invoke proxy will retry " + retries + " times", t);
                  try
                  {
                     log.debug("Sleeping for " + retryWaitingTimeMS + " before trying to fail over");
                     Thread.sleep(retryWaitingTimeMS);
                  }
                  catch (InterruptedException ignore)
                  {
                     // Get out of the loop
                     retries = 0;
                  }
               }
               else
               {
                  log.error("Cannot invoke proxy will no more retry", t);
               }
            }
            else
            {
               throw (Error)t;
            }
         }
      }
      // Todo : throw exception
      return null;
   }

   protected void startService() throws Exception
   {
      callback.setInvoker(this);

      //
      super.startService();
   }

   protected void stopService() throws Exception
   {
      super.stopService();

      //
      callback.setInvoker(null);
   }

   public void startSingleton()
   {
      String name = callback.getDisplayName();

      //
      try
      {

         // Start the service.
         log.debug("Starting singleton " + name);
         callback.startSingleton();
         log.debug("Singleton " + name + " started");

         //
         log.debug("Binding singleton proxy " + name);
         Object proxy = server.getAttribute(proxyFactory, "Proxy");
         Util.bind(new InitialContext(), jndiName, proxy);
         log.debug("Singleton proxy " + name + " bound");
      }
      catch (Exception e)
      {
         log.error("Not able to start singleton " + name + " bound", e);
      }
   }

   public void stopSingleton()
   {
      // Stop the singleton
      if (callback != null)
      {
         //
         String name = callback.getDisplayName();

         //
         log.debug("Stopping singleton proxy " + name);
         callback.stopSingleton();
         log.debug("Singleton " + name + " stopped");
      }
   }

   public Object invoke(Invocation mi) throws Exception
   {
      Method m = mi.getMethod();
      Object[] args = mi.getArguments();
      return callback.invoke(m.getName(), m.getParameterTypes(), args);
   }
}
