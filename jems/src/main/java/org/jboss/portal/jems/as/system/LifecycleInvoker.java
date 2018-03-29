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

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

/**
 * A jmx method invoker based on the lifecycle.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class LifecycleInvoker extends AbstractJBossService
{

   /** . */
   private ObjectName target;

   /** . */
   private String onCreate;

   /** . */
   private String onStart;

   /** . */
   private String onStop;

   /** . */
   private String onDestroy;

   public ObjectName getTarget()
   {
      return target;
   }

   public void setTarget(ObjectName target)
   {
      this.target = target;
   }

   public String getOnCreate()
   {
      return onCreate;
   }

   public void setOnCreate(String onCreate)
   {
      this.onCreate = onCreate;
   }

   public String getOnStart()
   {
      return onStart;
   }

   public void setOnStart(String onStart)
   {
      this.onStart = onStart;
   }

   public String getOnStop()
   {
      return onStop;
   }

   public void setOnStop(String onStop)
   {
      this.onStop = onStop;
   }

   public String getOnDestroy()
   {
      return onDestroy;
   }

   public void setOnDestroy(String onDestroy)
   {
      this.onDestroy = onDestroy;
   }

   private void invoke(String methodName)
   {
      try
      {
         log.debug("About to invoke " + methodName + " on mbean " + target);
         server.invoke(target, methodName, new Object[0], new String[0]);
      }
      catch (InstanceNotFoundException e)
      {
         log.debug("The mbean " + target + " does not exist and " + methodName + " cannot be invoked");
      }
      catch (MBeanException e)
      {
         Throwable t = e.getCause();
         log.error("The mbean " + target + " threw an exception during the invocation of the method " + methodName, t);
      }
      catch (ReflectionException e)
      {
         log.error("The mbean " + target + " invocation of method " + methodName, e);
      }
   }

   protected void createService() throws Exception
   {
      if (onCreate != null)
      {
         invoke(onCreate);
      }
   }

   protected void startService() throws Exception
   {
      if (onStart != null)
      {
         invoke(onStart);
      }
   }

   protected void stopService() throws Exception
   {
      if (onStop != null)
      {
         invoke(onStop);
      }
   }

   protected void destroyService() throws Exception
   {
      if (onDestroy != null)
      {
         invoke(onDestroy);
      }
   }
}
