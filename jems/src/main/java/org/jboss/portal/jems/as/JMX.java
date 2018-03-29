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

import org.apache.log4j.Logger;
import org.jboss.mx.util.MBeanProxy;
import org.jboss.mx.util.MBeanProxyCreationException;
import org.jboss.mx.util.MBeanServerLocator;
import org.jboss.mx.util.ObjectNameConverter;

import javax.management.InstanceNotFoundException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Properties;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public final class JMX
{

   private static final Logger log = Logger.getLogger(JMX.class);

   public static ObjectName extend(ObjectName name, Properties keyProperties)
   {
      try
      {
         Hashtable table = name.getKeyPropertyList();
         table.putAll(keyProperties);
         return ObjectNameConverter.convert(name.getDomain(), table);
      }
      catch (MalformedObjectNameException e)
      {
         log.error("", e);
         throw new RuntimeException();
      }
   }

   /** ObjectName comparator based on canonical name lexicography. */
   public static Comparator OBJECT_NAME_COMPARATOR = new Comparator()
   {
      public int compare(Object o1, Object o2)
      {
         ObjectName n1 = (ObjectName)o1;
         ObjectName n2 = (ObjectName)o2;
         return n1.getCanonicalName().compareTo(n2.getCanonicalName());
      }
   };

   public static void safeUnregister(MBeanServer server, ObjectName name)
   {
      if (server != null)
      {
         if (name != null)
         {
            try
            {
               server.unregisterMBean(name);
            }
            catch (InstanceNotFoundException e)
            {
               log.error("MBean " + name + " nto here");
            }
            catch (MBeanRegistrationException e)
            {
               log.error("MBean threw an exception during unregistration", e.getTargetException());
            }
         }
         else
         {
            log.error("Cannot unregister a null MBean");
         }
      }
      else
      {
         log.error("Cannot unregister with a null MBeanServer");
      }
   }

   /**
    * Retrieves the MBeanProxy associated with the given class and name from the specified MBeanServer.
    *
    * @param clazz  the interface implemented by the MBean which is to be retrieved
    * @param name   the MBean's ObjectName
    * @param server the MBeanServer from which to retrieve the MBeanProxy
    * @return a MBeanProxy for the specified MBean if it exists
    * @throws RuntimeException if the MBean couldn't be retrieved
    */
   public static Object getMBeanProxy(Class clazz, ObjectName name, MBeanServer server)
   {
      try
      {
         return MBeanProxy.get(clazz, name, server);
      }
      catch (MBeanProxyCreationException e)
      {
         String message = "Couldn't retrieve '" + name.getCanonicalName() + "' MBean with class " + clazz.getName();
         log.error(message, e);
         throw new RuntimeException(message, e);
      }
   }

   /**
    * Retrieves the MBeanProxy associated with the given class and name from the JBoss microkernel as returned by
    * <code>MBeanServerLocator.locateJBoss()</code>.
    *
    * @param clazz the interface implemented by the MBean which is to be retrieved
    * @param name  a String representation of the MBean's ObjectName
    * @return a MBeanProxy for the specified MBean if it exists
    * @throws IllegalArgumentException if the given name is not a valid ObjectName
    * @throws RuntimeException         if the MBean couldn't be retrieved
    * @see #getMBeanProxy(Class, javax.management.ObjectName, javax.management.MBeanServer)
    * @since 2.4
    */
   public static Object getMBeanProxy(Class clazz, String name)
   {
      ObjectName objecName;
      try
      {
         objecName = new ObjectName(name);
      }
      catch (MalformedObjectNameException e)
      {
         throw new IllegalArgumentException("'" + name + "' is not a valid ObjectName");
      }
      MBeanServer server = MBeanServerLocator.locateJBoss();
      return getMBeanProxy(clazz, objecName, server);
   }

   public static boolean addNotificationListener(
      MBeanServer server,
      ObjectName name,
      NotificationListener listener,
      NotificationFilter filter,
      Object handback)
   {
      try
      {
         server.addNotificationListener(name, listener, filter, handback);
         return true;
      }
      catch (InstanceNotFoundException e)
      {
         return false;
      }
   }

   public static boolean removeNotificationListener(
      MBeanServer server,
      ObjectName name,
      NotificationListener listener)
   {
      try
      {
         server.removeNotificationListener(name, listener);
         return true;
      }
      catch (InstanceNotFoundException e)
      {
         log.error("Cannot remove notification listener", e);
         return false;
      }
      catch (ListenerNotFoundException e)
      {
         log.error("Cannot remove notification listener", e);
         return false;
      }
   }
}
