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
package org.jboss.portal.theme.impl.render.div;

import org.jboss.portal.theme.render.AbstractObjectRenderer;
import org.jboss.portal.theme.render.RenderException;
import org.jboss.portal.theme.render.RendererContext;
import org.jboss.portal.theme.render.renderer.WindowRenderer;
import org.jboss.portal.theme.render.renderer.WindowRendererContext;

import java.io.PrintWriter;

/**
 * Implementation of a WindowRenderer, based on div tags.
 *
 * @author <a href="mailto:mholzner@novell.com>Martin Holzner</a>
 * @version $LastChangedRevision: 8784 $, $LastChangedDate: 2007-10-27 19:01:46 -0400 (Sat, 27 Oct 2007) $
 * @see org.jboss.portal.theme.render.renderer.WindowRenderer
 */
public class DivWindowRenderer extends AbstractObjectRenderer
   implements WindowRenderer
{
   public void render(RendererContext rendererContext, WindowRendererContext wrc) throws RenderException
   {
      PrintWriter out = rendererContext.getWriter();
      out.print("<div class=\"portlet-container\">");
      out.print("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");

      //
      out.print("<tr><td class=\"portlet-titlebar-left\"></td>");
      out.print("<td class=\"portlet-titlebar-center\">");
      rendererContext.render(wrc.getDecoration());
      out.print("</td><td class=\"portlet-titlebar-right\"></td></tr>");

      //
      out.print("<tr><td class=\"portlet-content-left\"></td>");
      out.print("<td class=\"portlet-body\"><div class=\"portlet-content-center\">");
      rendererContext.render(wrc.getPortlet());
      out.print("</div></td><td class=\"portlet-content-right\"></td></tr>");

      //
      out.print("<tr><td class=\"portlet-footer-left\"></td>");
      out.print("<td class=\"portlet-footer-center\"></td>");
      out.print("<td class=\"portlet-footer-right\"></td></tr>");
      out.print("</table></div>");
   }
}
