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
package org.jboss.portal.jems.as.system;

import org.jboss.mx.interceptor.Interceptor;
import org.jboss.mx.server.Invocation;
import org.jboss.mx.server.registry.MBeanEntry;
import org.jboss.mx.util.MBeanProxyExt;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class POJOInjection
{

   /** The dispatcher. */
   private final Interceptor dispatcher;

   /** The setter invocation. */
   private final Invocation setter;

   /** The mbean server. */
   private final MBeanServer server;

   /** The pojo object name. */
   private final ObjectName pojoName;

   /** The injection status. */
   private boolean injected;

   public POJOInjection(MBeanProxyExt pojoHandler, Interceptor dispatcher, Invocation setter)
   {
      this.dispatcher = dispatcher;
      this.setter = setter;
      this.server = pojoHandler.getMBeanProxyMBeanServer();
      this.pojoName = pojoHandler.getMBeanProxyObjectName();
      this.injected = false;
   }

   public ObjectName getPOJOName()
   {
      return pojoName;
   }

   public synchronized void resolve()
   {
      if (!injected)
      {
         if (server.isRegistered(pojoName))
         {
            try
            {
               // Get the mbean entry
               MBeanEntry entry = (MBeanEntry)server.invoke(new ObjectName("JMImplementation:type=MBeanRegistry"), "get", new Object[]{pojoName}, new String[]{ObjectName.class.getName()});

               //
               if (entry != null)
               {
                  // Get the managed resource (aka the service)
                  Object pojo = entry.getInvoker().getResource();

                  // Replace the proxy by the service
                  setter.getArgs()[0] = pojo;

                  // Dispatch the invocation finally
                  dispatcher.invoke(setter);

                  //
                  this.injected = true;
               }
            }
            catch (InstanceNotFoundException ignore)
            {
               // It means that between the registration check and the get
               // the service was removed 
            }
            catch (Throwable e)
            {
               e.printStackTrace();
            }
         }
      }
      else
      {
         if (!server.isRegistered(pojoName))
         {
            try
            {
               // Replace the proxy by the service
               setter.getArgs()[0] = null;

               // Dispatch the invocation finally
               dispatcher.invoke(setter);

               //
               this.injected = false;
            }
            catch (Throwable e)
            {
               e.printStackTrace();
            }
         }
      }
   }
}
