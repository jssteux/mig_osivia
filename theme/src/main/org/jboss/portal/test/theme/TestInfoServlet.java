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

import org.jboss.mx.util.MBeanProxy;
import org.jboss.mx.util.MBeanServerLocator;
import org.jboss.portal.test.framework.TestParametrization;
import org.jboss.portal.test.framework.driver.TestDriverContainer;
import org.jboss.portal.test.framework.driver.command.StartTestCommand;
import org.jboss.portal.test.framework.driver.remote.RemoteTestDriver;
import org.jboss.portal.test.framework.driver.remote.TestContext;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

/** @author <a href="mailto:roy@jboss.org">Roy Russo</a> */

public class TestInfoServlet extends HttpServlet
{

   protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
   {
      try
      {
         proceed(request, response);
      }
      catch (Exception e)
      {
         throw new ServletException(e);
      }
   }

   public void proceed(HttpServletRequest request, HttpServletResponse response) throws Exception
   {
      String pathInfo = request.getPathInfo();

      //
      MBeanServer mbeanServer = MBeanServerLocator.locateJBoss();
      TestDriverContainer testServer = (TestDriverContainer)MBeanProxy.get(TestDriverContainer.class, new ObjectName("portal.test:service=TestDriverServer"), mbeanServer);

      //
      RemoteTestDriver driver = (RemoteTestDriver)testServer.getDriver(pathInfo.substring(1));
      driver.invoke("", new StartTestCommand(new TestParametrization()));
      driver.pushContext("", new TestContext(0, null, new TestParametrization(), new HashMap()));

//
//      //
//      if (pathInfo.startsWith("/testsuite"))
//      {
//         // Get services
//         response.setContentType("text/html");
//         PrintWriter writer = response.getWriter();
//
//         writer.println("<html>");
//         writer.println("<body>");
//         writer.println("<table id=\"suiteTable\" cellpadding=\"1\" cellspacing=\"1\" border=\"1\">");
//         writer.println("<tbody>");
//         writer.println("<tr><td><b>Test Suite </b> </td> </tr>");
//
//         //
//         for (Iterator i = suite.keySet().iterator();i.hasNext();)
//         {
//            String testId = (String)i.next();
//            String url = request.getContextPath() + request.getServletPath() + "/init/" + FastURLEncoder.DEFAULT_ENCODER.encode(testId);
//            writer.println("<tr><td><a href=\"" + url + "\">Test " + testId + "</a></td></tr>");
//         }
//
//         writer.println("</tbody>");
//         writer.println("</table>");
//         writer.println("</body>");
//         writer.println("</html>");
//      }
//      else if (pathInfo.startsWith("/init"))
//      {
//         String testId = pathInfo.substring("/init/".length());
//         DynaTest test = (DynaTest)suite.get(testId);
//
//         //
//         TestContext testContext = new TestContext(test);
//         testContext.init();
//
//         //
//         getServletContext().setAttribute("TestContext", testContext);
//
//         //
//         if (selenium)
//         {
//            response.setContentType("text/html");
//            PrintWriter writer = response.getWriter();
//            writer.println("<html>");
//            writer.println("<body>");
//
//            writer.println("<table cellpadding=\"1\" cellspacing=\"1\" border=\"1\">");
//            writer.println("<tbody>");
//            writer.println("<tr><td colspan=\"3\">" + testId + "</td></tr>");
//
//            writer.println("<tr><td>open</td><td>" + request.getContextPath() + request.getServletPath() + "/invoke" + "</td><td>&nbsp;</td></tr>");
//
//
//            writer.println("</tbody>");
//            writer.println("</table>");
//
//            writer.println("</body>");
//            writer.println("</html>");
//            // testContext.userAgentCommands
//         }
//         else
//         {
//            String url = response.encodeRedirectURL(request.getContextPath() + request.getServletPath() + "/invoke");
//            response.sendRedirect(url);
//         }
//      }
   }
}
