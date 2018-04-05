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

package org.jboss.portal.test.theme.tmp.servlet;

import org.jboss.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;

/** @author <a href="mailto:roy@jboss.org">Roy Russo</a> */

public class TestAJAXServlet extends HttpServlet
{
   private final static Logger log = Logger.getLogger(TestAJAXServlet.class);

   public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException
   {
      doPost(req, resp);
   }

   public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException
   {
      Enumeration paramNames = req.getParameterNames();
      String response = "";
      while (paramNames.hasMoreElements())
      {
         String name = (String)paramNames.nextElement();
         // action: windowremove|windowmove
         if ("action".equalsIgnoreCase(name))
         {
            response = req.getParameter(name);
         }
         System.out.println("Parameter: " + name + " = " + req.getParameter(name));
      }

      sendResp(resp, response);

/*
      if(req.getParameter("action") != null)
      {
         if(req.getParameter("action").equals(PERSIST))
         {
            if(req.getParameter("positionNo") == null
               || req.getParameter("windowId") == null
               || req.getParameter("oldRegionId") == null
               || req.getParameter("newRegionId") == null)
            {
               resp.sendError(400, "Not enought parameters");
               return;
            }

            if(!testMode)
            {
               persistance.persistPosition(req.getParameter("positionNo"),
                                           req.getParameter("windowId"), req
                     .getParameter("oldRegionId"), req
                     .getParameter("newRegionId"));
            }

            sendResp(resp, "Position persisted");
         }
         else if(req.getParameter("action").equals(TEST_MODE))
         {
            if(req.getParameter("mode") != null)
            {
               try
               {
                  testMode = Boolean.valueOf(req.getParameter("mode"))
                        .booleanValue();
               }
               catch(RuntimeException e)
               {
                  resp.sendError(400, "Bad arguments");
                  return;
               }

               sendResp(resp, ((testMode) ? "Test mode ON" : "Test mode OFF"));

            }
            else
            {
               resp.sendError(400, "Not enought parameters");
               return;
            }
         }
         else
         {
            resp.sendError(400, "Unknown ajax call");
            return;
         }
      }
*/
   }

   private void sendResp(HttpServletResponse resp, String respData)
      throws IOException
   {
      resp.setHeader("Expires", "Mon, 26 Jul 1997 05:00:00 GMT");
      resp.setDateHeader("Last-Modified", new Date().getTime());
      resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
      resp.addHeader("Cache-Control", "post-check=0, pre-check=0");
      resp.setContentType("text/html");

      resp.getWriter().write(respData);
   }
}
