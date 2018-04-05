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
package org.jboss.portal.test.server;

import org.jboss.portal.server.RequestController;
import org.jboss.portal.server.ServerException;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.test.framework.driver.DriverCommand;
import org.jboss.portal.test.framework.driver.DriverResponse;
import org.jboss.portal.test.framework.driver.TestDriverException;
import org.jboss.portal.test.framework.driver.command.StartTestCommand;
import org.jboss.portal.test.framework.driver.remote.RemoteTestDriverServer;
import org.jboss.portal.test.framework.server.driver.AbstractTest;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class TestRequestController extends RemoteTestDriverServer implements RequestController
{

   /** . */
   private String currentTestId;

   public TestRequestController()
   {
   }

   public DriverResponse invoke(String testId, DriverCommand cmd) throws TestDriverException
   {
      DriverResponse response = super.invoke(testId, cmd);

      // Save the current id for http invocations later, see handle method
      if (cmd instanceof StartTestCommand)
      {
         currentTestId = testId;
      }

      return response;
   }

   public void handle(ServerInvocation invocation) throws ServerException
   {
      AbstractTest test = (AbstractTest)getDriver(currentTestId);

      //
      test.execute(invocation);
   }
}
