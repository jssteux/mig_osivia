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

import org.apache.log4j.Logger;
import org.jboss.mx.util.MBeanProxy;
import org.jboss.mx.util.MBeanProxyCreationException;
import org.jboss.mx.util.ObjectNameFactory;
import org.jboss.portal.jems.as.JMX;
import org.jboss.system.ServiceController;
import org.jboss.system.ServiceControllerMBean;
import org.jboss.system.ServiceMBean;

import javax.management.AttributeChangeNotification;
import javax.management.MBeanServer;
import javax.management.MBeanServerNotification;
import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Adapts JBoss lifecycle event handling and multiplex event from different sources into one source.
 * <p/>
 * It requires an MBeanServer and a ServiceController plugged into the MBeanServer.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class LifeCycleAdapter
{

   /** The name of the server delegate. */
   private static final ObjectName MBEAN_SERVER_DELEGATE = ObjectNameFactory.create("JMImplementation:type=MBeanServerDelegate");

   /** The logger. */
   protected Logger log;

   /** The service controller name. */
   protected ServiceControllerMBean controller;

   /** The server. */
   protected MBeanServer server;

   /** The delegated listener. */
   private Listener listener;

   /** The watched mbeans. */
   private Set watched;

   /** Create the rule for the given server. */
   public LifeCycleAdapter(MBeanServer server)
   {
      try
      {
         this.log = Logger.getLogger(getClass());
         this.controller = (ServiceControllerMBean)MBeanProxy.get(ServiceControllerMBean.class, ServiceController.OBJECT_NAME, server);
         this.server = server;
         this.listener = new Listener();
         this.watched = Collections.synchronizedSet(new HashSet());
      }
      catch (MBeanProxyCreationException ignored)
      {
      }
   }

   /**
    * Start the adapter.
    *
    * @throws IllegalStateException if the service controller is missing
    */
   public void start() throws IllegalStateException
   {
      JMX.addNotificationListener(server, MBEAN_SERVER_DELEGATE, listener, listener, null);
      if (!JMX.addNotificationListener(server, ServiceControllerMBean.OBJECT_NAME, listener, listener, null))
      {
         throw new IllegalStateException("The service controller is not here");
      }
   }

   /** Stop the adapter */
   public void stop()
   {
      JMX.removeNotificationListener(server, ServiceControllerMBean.OBJECT_NAME, listener);
      JMX.removeNotificationListener(server, MBEAN_SERVER_DELEGATE, listener);
      server = null;
      watched.clear();
   }

   public void addStateListener(ObjectName name)
   {
      try
      {
         server.addNotificationListener(name, listener, listener, name);
         watched.add(name);
      }
      catch (Exception e)
      {
         log.error("Cannot become a listener of " + name, e);
      }
   }

   public void removeStateListener(ObjectName name)
   {
      try
      {
         server.removeNotificationListener(name, listener);
         watched.remove(name);
      }
      catch (Exception ignored)
      {
      }
   }

//   protected void registered(ObjectName name)
//   {
//   }
//
//   protected void unregistered(ObjectName name)
//   {
//   }

   protected void created(ObjectName name)
   {
   }

   protected void starting(ObjectName name)
   {
   }

   protected void started(ObjectName name)
   {
   }

   protected void stopping(ObjectName name)
   {
   }

   protected void stopped(ObjectName name)
   {
   }

   protected void destroyed(ObjectName name)
   {
   }

   private class Listener implements NotificationListener, NotificationFilter
   {
      /** The serialVersionUID */
      private static final long serialVersionUID = 4049015398272819164L;

      public boolean isNotificationEnabled(Notification notification)
      {
         return true;
      }

      public void handleNotification(Notification notification, Object handback)
      {
         String type = notification.getType();
         if (type == MBeanServerNotification.REGISTRATION_NOTIFICATION)
         {
            MBeanServerNotification msn = (MBeanServerNotification)notification;
            ObjectName name = msn.getMBeanName();
            // registered(name);
         }
         else if (type == MBeanServerNotification.UNREGISTRATION_NOTIFICATION)
         {
            MBeanServerNotification msn = (MBeanServerNotification)notification;
            ObjectName name = msn.getMBeanName();
            // unregistered(name);
         }
         else if (type == AttributeChangeNotification.ATTRIBUTE_CHANGE)
         {
            AttributeChangeNotification acn = (AttributeChangeNotification)notification;
            ObjectName name = (ObjectName)handback;
            if ("State".equals(acn.getAttributeName()))
            {
               int state = ((Integer)acn.getNewValue()).intValue();
               switch (state)
               {
                  case ServiceMBean.STARTING:
                     starting(name);
                     break;
                  case ServiceMBean.STARTED:
                     started(name);
                     break;
                  case ServiceMBean.STOPPING:
                     stopping(name);
                     break;
                  case ServiceMBean.STOPPED:
                     stopped(name);
                     break;
                  default:
               }
            }
         }
         else if (type == ServiceMBean.CREATE_EVENT)
         {
            ObjectName name = (ObjectName)notification.getUserData();
            if (watched.contains(name))
            {
               created(name);
            }
         }
         else if (type == ServiceMBean.DESTROY_EVENT)
         {
            ObjectName name = (ObjectName)notification.getUserData();
            if (watched.contains(name))
            {
               destroyed(name);
            }
         }
      }
   }
}
