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
package org.jboss.portal.test.theme;

import org.jboss.portal.test.theme.model.RenderedObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class RequestContext
{

   /** . */
   public static final int ACTION_PHASE = 0;

   /** . */
   public static final int RENDER_PHASE = 0;

   /** . */
   private final RenderedObject target;

   /** . */
   private final TestPhase phase;

   /** . */
   private final boolean async;

   /** . */
   final HttpServletRequest request;

   /** . */
   final HttpServletResponse response;

   /** . */
   final Map parameters;

   public RequestContext(RenderedObject target, TestPhase phase, HttpServletRequest request, HttpServletResponse response)
   {
      boolean async = "true".equals(request.getHeader("ajax"));
      Map parameters = new HashMap();
      for (Enumeration e = request.getParameterNames(); e.hasMoreElements();)
      {
         String name = (String)e.nextElement();
         String value = request.getParameter(name);
         parameters.put(name, value);
      }

      //
      this.target = target;
      this.phase = phase;
      this.request = request;
      this.response = response;
      this.async = async;
      this.parameters = Collections.unmodifiableMap(parameters);
   }

   public String getParameter(String name)
   {
      return (String)parameters.get(name);
   }

   public Set getParameterNames()
   {
      return parameters.keySet();
   }

   public Map getParameterMap()
   {
      return parameters;
   }

   public RenderedObject getTarget()
   {
      return target;
   }

   public boolean isAsync()
   {
      return async;
   }

   public TestPhase getPhase()
   {
      return phase;
   }

   public void end()
   {
   }

   /** @throws IllegalArgumentException if the provided object is null */
   public ObjectURL createURL(RenderedObject obj) throws IllegalArgumentException
   {
      if (obj == null)
      {
         throw new IllegalArgumentException("No object provided");
      }
      return new ObjectURL(obj, this);
   }
}
