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
package org.jboss.portal.test.framework.server.driver;

import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.test.framework.driver.DriverResponse;
import org.jboss.portal.test.framework.driver.http.HTTPTestCase;
import org.jboss.portal.test.framework.driver.remote.TestContext;
import org.jboss.portal.test.framework.driver.response.ErrorResponse;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public abstract class AbstractTest extends HTTPTestCase
{


   protected AbstractTest(String testCaseId, String path)
   {
      super(testCaseId, path);
   }

   /**
    *
    */
   public void execute(ServerInvocation invocation)
   {
      try
      {
         DriverResponse response = execute(invocation, context);
         context.setResponse(response);
      }
      catch (Exception e)
      {
         context.setResponse(new ErrorResponse(e));
      }
   }

   /**
    *
    */
   public DriverResponse execute(ServerInvocation invocation, TestContext testContext) throws Exception
   {
      return new ErrorResponse("No default implementation");
   }
}
