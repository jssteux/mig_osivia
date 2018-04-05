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

import org.jboss.portal.common.NotYetImplemented;
import org.jboss.portal.common.util.TypedMap;
import org.jboss.portal.theme.Orientation;
import org.jboss.portal.theme.render.renderer.RegionRendererContext;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The definition of a region.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8951 $
 */
public class RegionObject extends RenderedObject
{

   /** . */
   PageObject page;

   /** . */
   private final String id;

   /** . */
   final LinkedHashMap windows;

   RegionObject(String name)
   {
      this.id = name;
      this.windows = new LinkedHashMap();
   }

   public String getId()
   {
      return id;
   }

   public WindowObject getWindow(String id)
   {
      if (id == null)
      {
         throw new IllegalArgumentException();
      }

      //
      return (WindowObject)windows.get(id);
   }

   public WindowObject addWindow(String id)
   {
      if (page.getWindow(id) != null)
      {
         throw new IllegalArgumentException("Duplicate window id " + id);
      }

      //
      WindowObject window = new WindowObject(id);

      //
      windows.put(id, window);
      window.region = this;

      //
      window.context = page.contextFactory.createContext(window);

      //
      return window;
   }

   public RegionRendererContext getRendererContext()
   {
      return rendererContext;
   }

   private static final TypedMap.Converter WINDOW_CONVERTER = new TypedMap.Converter()
   {
      protected Object getInternal(Object o) throws IllegalArgumentException, ClassCastException
      {
         throw new IllegalArgumentException("read only");
      }

      protected Object getExternal(Object o)
      {
         WindowObject window = (WindowObject)o;
         return window.getRendererContext();
      }

      protected boolean equals(Object o, Object o1)
      {
         return o.equals(o1);
      }
   };

   private static final TypedMap.Converter TRIVIAL_CONVERTER = new TypedMap.Converter()
   {
      protected Object getInternal(Object o) throws IllegalArgumentException, ClassCastException
      {
         return o;
      }
      protected Object getExternal(Object o)
      {
         return o;
      }
      protected boolean equals(Object o, Object o1)
      {
         return o.equals(o1);
      }
   };

   private RegionRendererContext rendererContext = new RegionRendererContext()
   {
      public String getId()
      {
         return id;
      }

      public Collection getWindows()
      {
         return new TypedMap(windows, TRIVIAL_CONVERTER, WINDOW_CONVERTER).values();
      }

      public Orientation getOrientation()
      {
         throw new NotYetImplemented();
      }

      public String getCSSId()
      {
         throw new NotYetImplemented();
      }

      public String getProperty(String name)
      {
         return RegionObject.this.getProperty(name);
      }

      public Map getProperties()
      {
         return RegionObject.this.getProperties();
      }
   };
}
