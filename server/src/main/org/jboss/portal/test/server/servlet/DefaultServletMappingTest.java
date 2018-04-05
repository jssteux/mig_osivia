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
package org.jboss.portal.test.server.servlet;

import org.jboss.portal.common.junit.ExtendedAssert;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerInvocationContext;
import org.jboss.portal.test.framework.driver.DriverResponse;
import org.jboss.portal.test.framework.driver.http.response.InvokeGetResponse;
import org.jboss.portal.test.framework.driver.remote.TestContext;
import org.jboss.portal.test.framework.driver.response.EndTestResponse;
import org.jboss.portal.test.framework.driver.response.ErrorResponse;
import org.jboss.portal.test.framework.server.driver.AbstractTest;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class DefaultServletMappingTest extends AbstractTest
{

   public DefaultServletMappingTest()
   {
      super("DefaultServletMappingTest", "/test-servlet-defaultservletmapping");
   }

   public DriverResponse execute(ServerInvocation invocation, TestContext testContext)
   {
      final ServerInvocationContext ctx = invocation.getServerContext();
      switch (testContext.getRequestCount())
      {
         case 0:
            ExtendedAssert.assertEquals("/test-servlet-defaultservletmapping", ctx.getPortalContextPath());
            // Here we test with "/" because on the exact context path tomcat will perform a redirect to the
            // the same context appending a trailing / to it
            ExtendedAssert.assertEquals("/", ctx.getPortalRequestPath());
            return new InvokeGetResponse("/test-servlet-defaultservletmapping/");
         case 1:
            ExtendedAssert.assertEquals("/test-servlet-defaultservletmapping", ctx.getPortalContextPath());
            ExtendedAssert.assertEquals("/", ctx.getPortalRequestPath());
            return new InvokeGetResponse("/test-servlet-defaultservletmapping/a");
         case 2:
            ExtendedAssert.assertEquals("/test-servlet-defaultservletmapping", ctx.getPortalContextPath());
            ExtendedAssert.assertEquals("/a", ctx.getPortalRequestPath());
            return new EndTestResponse();
      }
      return new ErrorResponse();
   }
}
