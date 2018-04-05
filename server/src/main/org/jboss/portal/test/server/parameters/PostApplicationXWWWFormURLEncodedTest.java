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

import org.jboss.portal.common.http.HttpRequest;
import org.jboss.portal.common.junit.ExtendedAssert;
import org.jboss.portal.server.AbstractServerURL;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerInvocationContext;
import org.jboss.portal.test.framework.driver.DriverResponse;
import org.jboss.portal.test.framework.driver.http.response.InvokePostResponse;
import org.jboss.portal.test.framework.driver.remote.TestContext;
import org.jboss.portal.test.framework.driver.response.EndTestResponse;
import org.jboss.portal.test.framework.server.driver.AbstractTest;
import org.jboss.portal.test.server.Utils;

import java.util.Map;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class PostApplicationXWWWFormURLEncodedTest extends AbstractTest
{


   public PostApplicationXWWWFormURLEncodedTest()
   {
      super("PostApplicationXWWWFormURLEncodedTest", "/test");
   }

   public DriverResponse execute(final ServerInvocation invocation, TestContext testContext)
   {
      if (testContext.isRequestCount(0))
      {
         AbstractServerURL url = new AbstractServerURL();
         url.setPortalRequestPath("/index.html");
         HttpRequest.Form body = new HttpRequest.Form();

         //
         url.setParameterValue("a", "b");
         url.setParameterValues("c", new String[]{"d", "e"});

         //
         url.setParameterValues("f", new String[]{"g"});
         body.addParameter("f", new String[]{"_g"});
         url.setParameterValues("h", new String[]{"i", "j"});
         body.addParameter("h", new String[]{"_i", "_j"});
         url.setParameterValues("encoding_1", new String[]{Utils.RANGE_0_255});
         body.addParameter("encoding_1", new String[]{Utils.RANGE_0_255});
         url.setParameterValues("encoding_2", new String[]{Utils.RANGE_256_512});
         body.addParameter("encoding_2", new String[]{Utils.RANGE_256_512});

         //
         body.addParameter("k", new String[]{"l"});
         body.addParameter("m", new String[]{"n", "o"});

         //
         String s = invocation.getResponse().renderURL(url);
         InvokePostResponse result = new InvokePostResponse(s);
         result.setContentType(InvokePostResponse.APPLICATION_X_WWW_FORM_URLENCODED + "; charset=UTF-8");
         result.setBody(body);
         return result;
      }
      else
      {
         ServerInvocationContext context = invocation.getServerContext();
         Map queryParameterMap = context.getQueryParameterMap();
         Map bodyParameterMap = context.getBodyParameterMap();

         //
         ExtendedAssert.assertNotNull(queryParameterMap);
         ExtendedAssert.assertNotNull(bodyParameterMap);

         //
         String[] qa = (String[])queryParameterMap.get("a");
         String[] ba = (String[])bodyParameterMap.get("a");
         ExtendedAssert.assertNotNull(qa);
         ExtendedAssert.assertNull(ba);
         ExtendedAssert.assertEquals(new String[]{"b"}, qa);

         //
         String[] qc = (String[])queryParameterMap.get("c");
         String[] bc = (String[])bodyParameterMap.get("c");
         ExtendedAssert.assertNotNull(qc);
         ExtendedAssert.assertNull(bc);
         ExtendedAssert.assertEquals(new String[]{"d", "e"}, qc);

         //
         String[] qf = (String[])queryParameterMap.get("f");
         String[] bf = (String[])bodyParameterMap.get("f");
         ExtendedAssert.assertNotNull(qf);
         ExtendedAssert.assertNotNull(bf);
         ExtendedAssert.assertEquals(new String[]{"g"}, qf);
         ExtendedAssert.assertEquals(new String[]{"_g"}, bf);

         //
         String[] qh = (String[])queryParameterMap.get("h");
         String[] bh = (String[])bodyParameterMap.get("h");
         ExtendedAssert.assertNotNull(qh);
         ExtendedAssert.assertNotNull(bh);
         ExtendedAssert.assertEquals(new String[]{"i", "j"}, qh);
         ExtendedAssert.assertEquals(new String[]{"_i", "_j"}, bh);

         //
         String[] qencoding1 = (String[])queryParameterMap.get("encoding_1");
         String[] bencoding1 = (String[])bodyParameterMap.get("encoding_1");
         ExtendedAssert.assertNotNull(qencoding1);
         ExtendedAssert.assertNotNull(bencoding1);
         ExtendedAssert.assertEquals(new String[]{Utils.RANGE_0_255}, qencoding1);
         ExtendedAssert.assertEquals(new String[]{Utils.RANGE_0_255}, bencoding1);

         //
         String[] qencoding2 = (String[])queryParameterMap.get("encoding_2");
         String[] bencoding2 = (String[])bodyParameterMap.get("encoding_2");
         ExtendedAssert.assertNotNull(qencoding2);
         ExtendedAssert.assertNotNull(bencoding2);
         ExtendedAssert.assertEquals(new String[]{Utils.RANGE_256_512}, qencoding2);
         ExtendedAssert.assertEquals(new String[]{Utils.RANGE_256_512}, bencoding2);

         //
         String[] qk = (String[])queryParameterMap.get("k");
         String[] bk = (String[])bodyParameterMap.get("k");
         ExtendedAssert.assertNull(qk);
         ExtendedAssert.assertNotNull(bk);
         ExtendedAssert.assertEquals(new String[]{"l"}, bk);

         //
         String[] qm = (String[])queryParameterMap.get("m");
         String[] bm = (String[])bodyParameterMap.get("m");
         ExtendedAssert.assertNull(qm);
         ExtendedAssert.assertNotNull(bm);
         ExtendedAssert.assertEquals(new String[]{"n", "o"}, bm);
         return new EndTestResponse();
      }
   }
}
