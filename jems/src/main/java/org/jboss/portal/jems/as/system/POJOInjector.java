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

import javax.management.MBeanServerNotification;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class POJOInjector implements NotificationListener
{

   /** . */
   private final Map injections;

   public POJOInjector()
   {
      this.injections = new HashMap();
   }

   public void addInjection(POJOInjection injection)
   {
      injections.put(injection.getPOJOName(), injection);

      //
      injection.resolve();
   }

   public void handleNotification(Notification notification, Object object)
   {
      String type = notification.getType();
      if (MBeanServerNotification.REGISTRATION_NOTIFICATION.equals(type) || MBeanServerNotification.UNREGISTRATION_NOTIFICATION.equals(type))
      {
         MBeanServerNotification msn = (MBeanServerNotification)notification;
         ObjectName name = msn.getMBeanName();
         POJOInjection injection = (POJOInjection)injections.get(name);
         if (injection != null)
         {
            injection.resolve();
         }
      }
   }
}
