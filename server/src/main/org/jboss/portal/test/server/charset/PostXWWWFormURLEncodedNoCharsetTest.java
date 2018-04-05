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
package org.jboss.portal.test.server.charset;

import org.jboss.portal.common.http.HttpRequest;
import org.jboss.portal.common.junit.ExtendedAssert;
import org.jboss.portal.server.AbstractServerURL;
import org.jboss.portal.server.PortalConstants;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerInvocationContext;
import org.jboss.portal.test.framework.driver.DriverResponse;
import org.jboss.portal.test.framework.driver.http.response.InvokePostResponse;
import org.jboss.portal.test.framework.driver.remote.TestContext;
import org.jboss.portal.test.framework.driver.response.EndTestResponse;
import org.jboss.portal.test.framework.server.driver.AbstractTest;

import javax.servlet.http.HttpServletRequest;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class PostXWWWFormURLEncodedNoCharsetTest extends AbstractTest
{


   public PostXWWWFormURLEncodedNoCharsetTest()
   {
      super("PostXWWWFormURLEncodedNoCharsetTest", "/test");
   }

   public DriverResponse execute(final ServerInvocation invocation, TestContext testContext)
   {
      if (testContext.isRequestCount(0))
      {
         AbstractServerURL url = new AbstractServerURL();
         url.setPortalRequestPath("/index.html");
         String s = invocation.getResponse().renderURL(url);
         InvokePostResponse post = new InvokePostResponse(s);
         post.setContentType(PortalConstants.APPLICATION_X_WWW_FORM_URLENCODED);
         post.setBody(new HttpRequest.Form());
         return post;
      }
      else
      {
         ServerInvocationContext context = invocation.getServerContext();
         HttpServletRequest req = context.getClientRequest();
         ExtendedAssert.assertEquals(PortalConstants.APPLICATION_X_WWW_FORM_URLENCODED, context.getMediaType());
         ExtendedAssert.assertEquals(PortalConstants.UTF_8.name(), req.getCharacterEncoding());
         return new EndTestResponse();
      }
   }
}
