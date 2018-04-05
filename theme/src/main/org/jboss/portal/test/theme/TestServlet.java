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
import org.jboss.portal.test.framework.driver.TestDriverContainer;
import org.jboss.portal.test.theme.dyna.tests.dnd.DnDTest;
import org.jboss.portal.test.theme.model.WindowObject;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** @author <a href="mailto:roy@jboss.org">Roy Russo</a> */

public class TestServlet extends HttpServlet
{

   /** . */
   private TestDriverContainer testServer;

   /** . */
   DynaTestContext testContext;

   public void init() throws ServletException
   {
      try
      {
         MBeanServer mbeanServer = MBeanServerLocator.locateJBoss();
         testServer = (TestDriverContainer)MBeanProxy.get(TestDriverContainer.class, new ObjectName("portal.test:service=TestDriverServer"), mbeanServer);

//         addTest(new LinkPartialRefreshWindowTest("A", Boolean.TRUE, Boolean.TRUE, true));
//         addTest(new LinkPartialRefreshWindowTest("B", Boolean.TRUE, Boolean.FALSE, false));
//         addTest(new LinkPartialRefreshWindowTest("C", Boolean.TRUE, null, true));
//         addTest(new LinkPartialRefreshWindowTest("D", Boolean.FALSE, Boolean.TRUE, false));
//         addTest(new LinkPartialRefreshWindowTest("E", Boolean.FALSE, Boolean.FALSE, false));
//         addTest(new LinkPartialRefreshWindowTest("F", Boolean.FALSE, null, false));
//         addTest(new LinkPartialRefreshWindowTest("G", null, Boolean.TRUE, true));
//         addTest(new LinkPartialRefreshWindowTest("H", null, Boolean.FALSE, false));
//         addTest(new LinkPartialRefreshWindowTest("I", null, null, false));

         // todo use test parametrization
//         addTest(new FormPartialRefreshWindowTest("A", Boolean.TRUE, Boolean.TRUE, true));
//         addTest(new FormPartialRefreshWindowTest("B", Boolean.TRUE, Boolean.FALSE, false));
//         addTest(new FormPartialRefreshWindowTest("C", Boolean.TRUE, null, true));
//         addTest(new FormPartialRefreshWindowTest("D", Boolean.FALSE, Boolean.TRUE, false));
//         addTest(new FormPartialRefreshWindowTest("E", Boolean.FALSE, Boolean.FALSE, false));
//         addTest(new FormPartialRefreshWindowTest("F", Boolean.FALSE, null, false));
//         addTest(new FormPartialRefreshWindowTest("G", null, Boolean.TRUE, true));
//         addTest(new FormPartialRefreshWindowTest("H", null, Boolean.FALSE, false));
//         addTest(new FormPartialRefreshWindowTest("I", null, null, false));

//         addTest(new FormGetRefreshIsNotPartialWindowTest("A", Boolean.TRUE, Boolean.TRUE));
//         addTest(new FormGetRefreshIsNotPartialWindowTest("B", Boolean.TRUE, Boolean.FALSE));
//         addTest(new FormGetRefreshIsNotPartialWindowTest("C", Boolean.TRUE, null));
//         addTest(new FormGetRefreshIsNotPartialWindowTest("D", Boolean.FALSE, Boolean.TRUE));
//         addTest(new FormGetRefreshIsNotPartialWindowTest("E", Boolean.FALSE, Boolean.FALSE));
//         addTest(new FormGetRefreshIsNotPartialWindowTest("F", Boolean.FALSE, null));
//         addTest(new FormGetRefreshIsNotPartialWindowTest("G", null, Boolean.TRUE));
//         addTest(new FormGetRefreshIsNotPartialWindowTest("H", null, Boolean.FALSE));
//         addTest(new FormGetRefreshIsNotPartialWindowTest("I", null, null));

         addTest(new DnDTest("A"));
      }
      catch (Exception e)
      {
         throw new ServletException(e);
      }
   }

   private void addTest(DynaTest dynaTest) throws Exception
   {
      testServer.addDriver(new DynaTestContext(this, dynaTest, "http://localhost:8080/theme-test/selenium/invoke/"));
   }

   /** Generates a portal page with windows, using the theme api. */
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
      if (pathInfo != null)
      {
         if (pathInfo.startsWith("/window/"))
         {
            String windowId = pathInfo.substring("/window/".length());
            WindowObject window = testContext.getPage().getWindow(windowId);
            TestPhase phase = TestPhase.createAction(testContext.getRequestCount());
            RequestContext requestContext = new RequestContext(window, phase, request, response);
            testContext.invoke(requestContext);
         }
         else if ("/ajax".equals(pathInfo))
         {
            TestPhase phase = TestPhase.createAjax(testContext.getRequestCount());
            RequestContext requestContext = new RequestContext(null, phase, request, response);
            testContext.invoke(requestContext);
         }
      }

      //
      TestPhase phase = TestPhase.createRender(testContext.getRequestCount());
      RequestContext requestContext = new RequestContext(null, phase, request, response);
      testContext.invoke(requestContext);
   }
}
