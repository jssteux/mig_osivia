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
package org.jboss.portal.test.server.response;

import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.test.framework.driver.DriverCommand;
import org.jboss.portal.test.framework.driver.DriverResponse;
import org.jboss.portal.test.framework.driver.remote.TestContext;
import org.jboss.portal.test.framework.driver.response.EndTestResponse;
import org.jboss.portal.test.framework.driver.response.ErrorResponse;
import org.jboss.portal.test.framework.server.driver.AbstractTest;
import org.jboss.portal.test.server.Utils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * To rewrite.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class EncodeResponseTest extends AbstractTest
{

   /** . */
   private static final String content = Utils.compute(0, 10000);

   public EncodeResponseTest()
   {
      super("EncodeResponseTest", "/test");
   }

   public DriverResponse execute(ServerInvocation invocation, TestContext testContext) throws IOException
   {
      if (testContext.isRequestCount(0))
      {
         HttpServletResponse resp = invocation.getServerContext().getClientResponse();
         resp.setContentType("text/html");
         resp.setCharacterEncoding("utf-8");
         PrintWriter pw = resp.getWriter();
         pw.print(content);
         pw.close();
         return new EndTestResponse();
//         return new SendResponseResponse();
      }
      else
      {
         return new ErrorResponse();
      }
   }

   public DriverResponse execute(DriverCommand driverCommand, TestContext testContext) throws Exception
   {
      if (testContext.isRequestCount(1))
      {
//         SendResponseCommand src = (SendResponseCommand)driverCommand;
//         byte[] bytes = src.getBytes();
//         String s = new String(bytes, "utf-8");
//         String result = Utils.compareString(content, s);
//         ExtendedAssert.assertEquals(null, result);
         return new EndTestResponse();
      }
      else
      {
         return new ErrorResponse();
      }
   }
}
