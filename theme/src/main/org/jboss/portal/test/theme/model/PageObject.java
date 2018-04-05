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

import org.jboss.portal.common.util.TypedMap;
import org.jboss.portal.theme.render.renderer.PageRendererContext;
import org.jboss.portal.theme.render.renderer.RegionRendererContext;
import org.jboss.portal.theme.render.renderer.WindowRendererContext;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * The definition of a page.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8951 $
 */
public class PageObject extends RenderedObject
{

   /** . */
   final Map regions;

   /** . */
   final RenderedObjectContextFactory contextFactory;

   public PageObject(RenderedObjectContextFactory contextFactory)
   {
      this.regions = new HashMap();
      this.contextFactory = contextFactory;
   }

   public RegionObject createRegion(String name)
   {
      if (regions.containsKey(name))
      {
         throw new IllegalArgumentException("A region with name " + name + " already exists");
      }

      //
      RegionObject region = new RegionObject(name);

      //
      regions.put(name, region);
      region.page = this;

      //
      return region;
   }

   public WindowObject getWindow(String id)
   {
      if (id == null)
      {
         throw new IllegalArgumentException();
      }

      //
      for (Iterator i = regions.values().iterator(); i.hasNext();)
      {
         RegionObject region = (RegionObject)i.next();
         WindowObject window = region.getWindow(id);
         if (window != null)
         {
            return window;
         }
      }

      //
      return null;
   }

   public RegionObject getRegion(String id)
   {
      if (id == null)
      {
         throw new IllegalArgumentException();
      }

      //
      return (RegionObject)regions.get(id);
   }

   public PageRendererContext getRendererContext()
   {
      return rendererContext;
   }

   private static final TypedMap.Converter REGION_CONVERTER = new TypedMap.Converter()
   {
      protected Object getInternal(Object o) throws IllegalArgumentException, ClassCastException
      {
         throw new IllegalArgumentException("read only");
      }

      protected Object getExternal(Object o)
      {
         RegionObject region = (RegionObject)o;
         return region.getRendererContext();
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

   private PageRendererContext rendererContext = new PageRendererContext()
   {
      public Collection getRegions()
      {
         return new TypedMap(regions, TRIVIAL_CONVERTER, REGION_CONVERTER).values();
      }

      public RegionRendererContext getRegion(String regionName)
      {
         RegionObject region = PageObject.this.getRegion(regionName);
         return region != null ? region.getRendererContext() : null;
      }

      public WindowRendererContext getWindow(String windowId)
      {
         WindowObject window = PageObject.this.getWindow(windowId);
         return window != null ? window.getRendererContext() : null;
      }

      public String getProperty(String name)
      {
         return PageObject.this.getProperty(name);
      }

      public Map getProperties()
      {
         return PageObject.this.getProperties();
      }
   };
}
