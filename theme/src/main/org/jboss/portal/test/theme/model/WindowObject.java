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
package org.jboss.portal.test.theme.model;

import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;
import org.jboss.portal.common.util.IteratorStatus;
import org.jboss.portal.theme.render.renderer.DecorationRendererContext;
import org.jboss.portal.theme.render.renderer.PortletRendererContext;
import org.jboss.portal.theme.render.renderer.WindowRendererContext;

import java.util.Collection;
import java.util.Map;

/**
 * The definition of a window.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class WindowObject extends RenderedObject
{

   /** . */
   private final String id;

   /** . */
   RegionObject region;

   /** . */
   String title;

   /** . */
   String markup;

   /** . */
   RenderedObjectContext context;

   public WindowObject(String id)
   {
      this.id = id;
      this.title = "";
      this.markup = "";
   }

   public String getId()
   {
      return id;
   }

   public String getMarkup()
   {
      return markup;
   }

   public RegionObject getRegion()
   {
      return region;
   }

   public void setMarkup(String markup)
   {
      context.addChange(new WindowMarkupChange(this.markup, markup));

      //
      this.markup = markup;
   }

   public String getTitle()
   {
      return title;
   }

   public void setTitle(String title)
   {
      this.title = title;
   }

   public WindowRendererContext getRendererContext()
   {
      return rendererContext;
   }

   private WindowRendererContext rendererContext = new WindowRendererContext()
   {
      public String getId()
      {
         return id;
      }

      public String getOrder()
      {
         for (IteratorStatus i = new IteratorStatus(region.windows.values().iterator()); i.hasNext();)
         {
            if (this == i.next())
            {
               return Integer.toString(i.getIndex());
            }
         }
         throw new IllegalStateException("Should not happen");
      }

      public WindowState getWindowState()
      {
         return WindowState.NORMAL;
      }

      public Mode getMode()
      {
         return Mode.VIEW;
      }

      public DecorationRendererContext getDecoration()
      {
         return decorationRendererContext;
      }

      public PortletRendererContext getPortlet()
      {
         return portletRendererContext;
      }

      public String getProperty(String name)
      {
         return WindowObject.this.getProperty(name);
      }

      public Map getProperties()
      {
         return WindowObject.this.getProperties();
      }
   };

   private DecorationRendererContext decorationRendererContext = new DecorationRendererContext()
   {
      public String getTitle()
      {
         return title;
      }

      public Collection getTriggerableActions(String familyName)
      {
         return null;
      }

      public String getProperty(String name)
      {
         return WindowObject.this.getProperty(name);
      }

      public Map getProperties()
      {
         return WindowObject.this.getProperties();
      }
   };

   private PortletRendererContext portletRendererContext = new PortletRendererContext()
   {
      public String getMarkup()
      {
         return markup;
      }

      public String getProperty(String name)
      {
         return WindowObject.this.getProperty(name);
      }

      public Map getProperties()
      {
         return WindowObject.this.getProperties();
      }
   };
}
