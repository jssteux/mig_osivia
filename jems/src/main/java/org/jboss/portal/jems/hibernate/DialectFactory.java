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

import org.hibernate.HibernateException;

import java.util.HashMap;
import java.util.Map;

/**
 * Ripped off hibernate 3.1 that is not in 3.0. A factory for generating Dialect instances.
 *
 * @author Steve Ebersole
 * @version $Revision: 11473 $
 */
public class DialectFactory
{


   private DialectFactory()
   {
   }

   /**
    * Determine the appropriate Dialect to use given the database product name and major version.
    *
    * @param databaseName         The name of the database product (obtained from metadata).
    * @param databaseMajorVersion The major version of the database product (obtained from metadata).
    * @return An appropriate dialect instance.
    */
   public static String determineDialect(String databaseName, int databaseMajorVersion)
   {
      if (databaseName == null)
      {
         throw new HibernateException("Hibernate Dialect must be explicitly set");
      }

      DatabaseDialectMapper mapper = (DatabaseDialectMapper)MAPPERS.get(databaseName);
      if (mapper == null)
      {
         throw new HibernateException("Hibernate Dialect must be explicitly set for database: " + databaseName);
      }

      return mapper.getDialectClass(databaseMajorVersion);
   }

   /**
    * For a given database product name, instances of DatabaseDialectMapper know which Dialect to use for different
    * versions.
    */
   public static interface DatabaseDialectMapper
   {
      public String getDialectClass(int majorVersion);
   }

   /** A simple DatabaseDialectMapper for dialects which are independent of the underlying database product version. */
   public static class VersionInsensitiveMapper implements DatabaseDialectMapper
   {
      private String dialectClassName;

      public VersionInsensitiveMapper(String dialectClassName)
      {
         this.dialectClassName = dialectClassName;
      }

      public String getDialectClass(int majorVersion)
      {
         return dialectClassName;
      }
   }

   private static final Map MAPPERS = new HashMap();

   static
   {
      // TODO : this is the stuff it'd be nice to move to a properties file or some other easily user-editable place
      MAPPERS.put("HSQL Database Engine", new VersionInsensitiveMapper("org.hibernate.dialect.HSQLDialect"));
      MAPPERS.put("DB2/NT", new VersionInsensitiveMapper("org.hibernate.dialect.DB2Dialect"));
      MAPPERS.put("MySQL", new VersionInsensitiveMapper("org.hibernate.dialect.MySQLDialect"));
      MAPPERS.put("PostgreSQL", new VersionInsensitiveMapper("org.hibernate.dialect.PostgreSQLDialect"));
      MAPPERS.put("Microsoft SQL Server Database", new VersionInsensitiveMapper("org.hibernate.dialect.SQLServerDialect"));
      MAPPERS.put("Microsoft SQL Server", new VersionInsensitiveMapper("org.hibernate.dialect.SQLServerDialect"));
      MAPPERS.put("Sybase SQL Server", new VersionInsensitiveMapper("org.hibernate.dialect.SybaseDialect"));
      MAPPERS.put("Informix Dynamic Server", new VersionInsensitiveMapper("org.hibernate.dialect.InformixDialect"));

      MAPPERS.put(
         "Oracle",
         new DatabaseDialectMapper()
         {
            public String getDialectClass(int majorVersion)
            {
               return majorVersion > 8
                  ? "org.hibernate.dialect.Oracle9Dialect"
                  : "org.hibernate.dialect.OracleDialect";
            }
         }
      );
   }
}
