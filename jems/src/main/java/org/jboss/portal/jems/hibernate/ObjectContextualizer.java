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

import org.hibernate.SessionFactory;
import org.hibernate.event.EventListeners;
import org.hibernate.event.PostLoadEvent;
import org.hibernate.event.PostLoadEventListener;
import org.hibernate.impl.SessionFactoryImpl;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class ObjectContextualizer
{

   public Object context;

   public ObjectContextualizer(Object context)
   {
      this.context = context;
   }

   public void attach(SessionFactory sessionFactory)
   {
      EventListeners listeners = ((SessionFactoryImpl)sessionFactory).getEventListeners();
      PostLoadEventListener[] lels1 = listeners.getPostLoadEventListeners();
      PostLoadEventListener[] lels2 = new PostLoadEventListener[lels1.length + 1];
      System.arraycopy(lels1, 0, lels2, 1, lels1.length);
      lels2[0] = new PostLoadEventListener()
      {
         public void onPostLoad(PostLoadEvent postLoadEvent)
         {
            Object entity = postLoadEvent.getEntity();
            if (entity instanceof ContextObject)
            {
               ContextObject object = (ContextObject)entity;
               object.setContext(context);
            }
         }
      };
      listeners.setPostLoadEventListeners(lels2);
   }
}
