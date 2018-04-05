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
package org.jboss.portal.test.theme.dyna.tests.dnd;

import org.jboss.portal.test.framework.driver.DriverResponse;
import org.jboss.portal.test.framework.driver.response.EndTestResponse;
import org.jboss.portal.test.framework.driver.web.response.DnDResponse;
import org.jboss.portal.test.theme.DynaTest;
import org.jboss.portal.test.theme.DynaTestContext;
import org.jboss.portal.test.theme.RequestContext;
import org.jboss.portal.test.theme.TestPhase;
import org.jboss.portal.test.theme.model.PageObject;
import org.jboss.portal.test.theme.model.RegionObject;
import org.jboss.portal.test.theme.model.WindowObject;
import org.jboss.portal.theme.impl.render.dynamic.DynaRenderOptions;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class DnDTest extends DynaTest
{

   /** . */
   private DynaTestContext dynaTestContext;

   public DnDTest(String id)
   {
      super(id);
   }

   public void init(DynaTestContext dynaTestContext)
   {
      PageObject page = dynaTestContext.getPage();
      page.setRenderOptions(DynaRenderOptions.getOptions(Boolean.TRUE, Boolean.FALSE));

      //
      RegionObject regionA = page.createRegion("RegionB");
      regionA.setRenderOptions(DynaRenderOptions.getOptions(Boolean.TRUE, Boolean.FALSE));

      //
      WindowObject window0 = regionA.addWindow("0");
      window0.setRenderOptions(DynaRenderOptions.getOptions(Boolean.TRUE, Boolean.FALSE));

      //
      this.dynaTestContext = dynaTestContext;
   }

   public DriverResponse invoke(RequestContext requestContext)
   {
      TestPhase phase = requestContext.getPhase();
      if (phase.getLifeCycle() == TestPhase.RENDER_LIFE_CYCLE)
      {
         switch (phase.getCount())
         {
            case 0:
               return new DnDResponse("test.decoration.0", 0, -40);
            default:
               fail();
         }
      }
      else if (phase.getLifeCycle() == TestPhase.AJAX_LIFE_CYCLE)
      {
         switch (phase.getCount())
         {
            case 1:

               String windowId = requestContext.getParameter("windowId");
               String fromRegion = requestContext.getParameter("fromRegion");
               String toRegion = requestContext.getParameter("toRegion");

               System.out.println("windowId = " + windowId);
               System.out.println("fromRegion = " + fromRegion);
               System.out.println("toRegion = " + toRegion);

               return new EndTestResponse();
            default:
               fail();
         }
      }
      else
      {
         switch (phase.getCount())
         {
            default:
               fail();
         }
      }

      //
      return new EndTestResponse();
   }
}
