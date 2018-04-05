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

import org.jboss.portal.theme.impl.render.dynamic.DynaRenderOptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class RenderedObject
{

   /** . */
   protected final Map properties;

   public RenderedObject()
   {
      this.properties = new HashMap();
   }

   public final String getProperty(String name)
   {
      return (String)properties.get(name);
   }

   public final void setProperty(String name, String value)
   {
      properties.put(name, value);
   }

   public final Map getProperties()
   {
      return Collections.unmodifiableMap(properties);
   }

   public DynaRenderOptions getRenderOptions()
   {
      return DynaRenderOptions.getOptions(properties);
   }

   public void setRenderOptions(DynaRenderOptions renderOptions)
   {
      renderOptions.setOptions(properties);
   }
}
