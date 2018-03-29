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
package org.jboss.portal.jems.as;

import org.jboss.portal.jems.as.system.AbstractJBossService;

/**
 * A service that binds an injected service to the JNDI registry.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class JNDIBinder extends AbstractJBossService
{

   /** The jndi name. */
   private String jndiName;

   /** The injected service that will be bound. */
   private Object targetObject;

   /** The jndi binding object. */
   private JNDI.Binding jndiBinding;

   public String getJNDIName()
   {
      return jndiName;
   }

   public void setJNDIName(String jndiName)
   {
      this.jndiName = jndiName;
   }

   public Object getTargetObject()
   {
      return targetObject;
   }

   public void setTargetObject(Object targetObject)
   {
      this.targetObject = targetObject;
   }

   protected void createService() throws Exception
   {
      if (jndiName == null)
      {
         throw new IllegalStateException("No jndi name provided");
      }
   }

   protected void startService() throws Exception
   {
      if (targetObject == null)
      {
         throw new IllegalStateException("No target object to bind");
      }

      //
      log.debug("About to bind service" + targetObject + " to JNDI with name " + jndiName);
      jndiBinding = new JNDI.Binding(jndiName, this);
      jndiBinding.bind();
      log.debug("Service" + targetObject + " bound to JNDI with name " + jndiName);
   }

   protected void stopService() throws Exception
   {
      if (jndiBinding != null)
      {
         jndiBinding.unbind();
         jndiBinding = null;
      }
   }
}
