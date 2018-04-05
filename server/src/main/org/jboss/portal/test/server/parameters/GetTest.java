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
package org.jboss.portal.test.server.parameters;

import org.jboss.portal.common.junit.ExtendedAssert;
import org.jboss.portal.server.AbstractServerURL;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerInvocationContext;
import org.jboss.portal.test.framework.driver.DriverResponse;
import org.jboss.portal.test.framework.driver.http.response.InvokeGetResponse;
import org.jboss.portal.test.framework.driver.remote.TestContext;
import org.jboss.portal.test.framework.driver.response.EndTestResponse;
import org.jboss.portal.test.framework.server.driver.AbstractTest;
import org.jboss.portal.test.server.Utils;

import java.util.Map;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class GetTest extends AbstractTest
{

   public GetTest()
   {
      super("GetTest", "/test");
   }

   public DriverResponse execute(final ServerInvocation invocation, TestContext testContext)
   {
      if (testContext.isRequestCount(0))
      {
         AbstractServerURL url = new AbstractServerURL();
         url.setPortalRequestPath("/index.html");
         url.setParameterValue("a", "b");
         url.setParameterValues("c", new String[]{"d", "e"});
         url.setParameterValues("encoding", new String[]{Utils.RANGE_0_255});
         String s = invocation.getResponse().renderURL(url);
         return new InvokeGetResponse(s);
      }
      else
      {
         ServerInvocationContext context = invocation.getServerContext();
         Map queryParameterMap = context.getQueryParameterMap();
         Map bodyParameterMap = context.getBodyParameterMap();

         //
         ExtendedAssert.assertNotNull(queryParameterMap);
         ExtendedAssert.assertNull(bodyParameterMap);

         String[] a = (String[])queryParameterMap.get("a");
         ExtendedAssert.assertNotNull(a);
         ExtendedAssert.assertEquals(new String[]{"b"}, a);

         //
         String[] c = (String[])queryParameterMap.get("c");
         ExtendedAssert.assertNotNull(c);
         ExtendedAssert.assertEquals(new String[]{"d", "e"}, c);

         //
         String[] f = (String[])queryParameterMap.get("encoding");
         ExtendedAssert.assertNotNull(f);
         ExtendedAssert.assertEquals(new String[]{Utils.RANGE_0_255}, f);
         return new EndTestResponse();
      }
   }
}
