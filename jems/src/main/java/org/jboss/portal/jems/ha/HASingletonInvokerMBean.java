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

import org.jboss.ha.singleton.HASingletonMBean;

import javax.management.ObjectName;
import java.util.Properties;

/**
 * The ha singleton invoker service extends the ha singleton service and adds the capability to invoke the singleton
 * service from the cluster.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public interface HASingletonInvokerMBean extends HASingletonMBean
{

   /** Get the callback of the invoker. */
   HASingletonInvoker.Callback getCallback();

   /** @see #getCallback() */
   void setCallback(HASingletonInvoker.Callback callback);

   /** Return the JNDI name under which the proxy is bound. */
   String getJNDIName();

   /** @see #getJNDIName() */
   void setJNDIName(String jndiName);

   /** Return the name of the proxy factory to use. */
   ObjectName getProxyFactory();

   /** @see #getProxyFactory() */
   void setProxyFactory(ObjectName proxyFactory);

   /** Return the JNDI configuration to lookup the proxy. */
   Properties getJNDIProperties();

   /** @see #getJNDIProperties() */
   void setJNDIProperties(Properties jndiProperties);

   /** Return the max number of retries when an invocation is done. */
   int getMaxRetries();

   /** @see #getMaxRetries() */
   void setMaxRetries(int maxRetries);

   /** Return the time that the thread will sleep before looking up a proxy during fail over. */
   long getRetryWaitingTimeMS();

   /** @see #getRetryWaitingTimeMS() */
   void setRetryWaitingTimeMS(long retryWaitingTimeMS);

   /** Return the proxy, if it is cached then it will return the cached proxy otherwise it will perform a JNDI lookup. */
   HASingletonInvoker.Proxy lookupProxy();

   /**
    * Invoke the service through the invoker. When the service proxy throws an exception the invoker waits for a
    * specific duration and then perform a new lookup for a certain amount of time.
    */
   Object invoke(String methodName, Class[] types, Object[] args) throws Exception;

   /** Contract layer that defines the interactions between the service and the ha invoker. */
   interface Callback
   {
      /**
       * Set the invoker on the client, if the invoker is not null then the invoker is available otherwise if it is null
       * then the invoker is not available.
       */
      void setInvoker(HASingletonInvoker invoker);

      /** Start the singleton. */
      void startSingleton();

      /** Stop the singleton. */
      void stopSingleton();

      /** Invocation callback */
      Object invoke(String methodName, Class[] types, Object[] args) throws Exception;

      /** Provide a simple display name for logging purpose. */
      String getDisplayName();
   }

   /** The proxy interface bound in JNDI. */
   interface Proxy
   {
      Object invoke(String methodName, Class[] types, Object[] args) throws Exception;
   }
}
