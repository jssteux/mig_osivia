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

import org.jboss.portal.common.text.CharBuffer;
import org.jboss.portal.common.text.CharWriter;
import org.jboss.portal.common.text.FastURLEncoder;
import org.jboss.portal.common.util.IteratorStatus;
import org.jboss.portal.test.theme.model.RenderedObject;
import org.jboss.portal.test.theme.model.WindowObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 11068 $
 */
public class ObjectURL
{

   /** . */
   private RenderedObject object;

   /** . */
   private Map params;

   /** . */
   private RequestContext requestContext;


   public ObjectURL(RenderedObject object, RequestContext requestContext)
   {
      this.object = object;
      this.requestContext = requestContext;
      this.params = new HashMap();
   }

   public void setParameter(String name, String value)
   {
      params.put(name, value);
   }

   public String toString()
   {
      CharWriter url = new CharBuffer();

      //
      url.append(requestContext.request.getContextPath());
      url.append(requestContext.request.getServletPath());

      FastURLEncoder encoder = FastURLEncoder.getUTF8Instance();
      //
      if (object instanceof WindowObject)
      {
         WindowObject window = (WindowObject)object;
         url.append("/window/");
         encoder.encode(window.getId(), url);
      }
      else
      {
         throw new IllegalStateException();
      }

      //
      for (IteratorStatus i = new IteratorStatus(params.entrySet().iterator()); i.hasNext();)
      {
         Map.Entry entry = (Map.Entry)i.next();
         String key = (String)entry.getKey();
         String value = (String)entry.getValue();
         url.append(i.isFirst() ? '?' : '&');
         encoder.encode(key, url);
         url.append('=');
         encoder.encode(value, url);
      }

      return url.toString();
   }
}
