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

import org.apache.log4j.Logger;
import org.hibernate.cache.Cache;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.CacheProvider;
import org.hibernate.cache.OptimisticTreeCache;
import org.hibernate.cache.TreeCache;
import org.jboss.mx.util.MBeanProxy;
import org.jboss.mx.util.MBeanServerLocator;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.util.Properties;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 9317 $
 */
public class JMXTreeCacheProvider implements CacheProvider
{

   /** Our logger. */
   private static final Logger log = Logger.getLogger(JMXTreeCacheProvider.class);

   public boolean isMinimalPutsEnabledByDefault()
   {
      return true;
   }

   public Cache buildCache(String regionName, Properties properties) throws CacheException
   {
      try
      {
         String objectNameAsString = properties.getProperty("hibernate.cache.object_name");
         log.debug("Uses tree cache provider with object name " + objectNameAsString);

         //
         ObjectName providerName = ObjectName.getInstance(objectNameAsString);

         //
         MBeanServer server = MBeanServerLocator.locateJBoss();
         TreeCacheProvider provider = (TreeCacheProvider)MBeanProxy.get(TreeCacheProvider.class, providerName, server);
         org.jboss.cache.TreeCache cache = provider.getTreeCache();
         
         //Select the proper type of hibernate cache to be used
         Cache hibernateCache = null;
         if(cache.getNodeLockingScheme().equalsIgnoreCase("OPTIMISTIC"))
         {
            log.debug("Selecting Optimistic Cache");
            hibernateCache = new OptimisticTreeCache(cache, regionName);
         }
         else
         {            
            log.debug("Selecting regular Tree Cache");
            hibernateCache = new TreeCache(cache, regionName, cache.getTransactionManager());
         }
         
         return hibernateCache;
      }
      catch (Exception e)
      {
         throw new CacheException(e);
      }
   }

   public long nextTimestamp()
   {
      return System.currentTimeMillis() / 100;
   }

   public void start(Properties properties) throws CacheException
   {
   }

   public void stop()
   {
   }
}
