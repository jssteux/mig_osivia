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

import org.jboss.portal.test.framework.driver.DriverResponse;
import org.jboss.portal.test.theme.model.PageObject;
import org.jboss.portal.test.theme.model.RegionObject;
import org.jboss.portal.test.theme.model.RenderedObject;
import org.jboss.portal.test.theme.model.WindowObject;
import org.jboss.portal.theme.impl.render.dynamic.DynaRenderOptions;

import java.util.Collections;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class MyTest extends DynaTest
{

   /** . */
   private DynaTestContext dynaTestContext;

   public MyTest(String id)
   {
      super(id);
   }

   public void init(DynaTestContext dynaTestContext)
   {
      PageObject page = dynaTestContext.getPage();

      //
      RegionObject regionA = page.createRegion("RegionA");
      regionA.setRenderOptions(DynaRenderOptions.getOptions(Boolean.FALSE, Boolean.FALSE));

      //
      RegionObject regionB = page.createRegion("RegionB");
      regionB.setRenderOptions(DynaRenderOptions.getOptions(Boolean.FALSE, Boolean.TRUE));

      //
      RegionObject regionC = page.createRegion("RegionC");
      regionC.setRenderOptions(DynaRenderOptions.getOptions(Boolean.TRUE, Boolean.FALSE));

      //
      RegionObject regionD = page.createRegion("RegionD");
      regionD.setRenderOptions(DynaRenderOptions.getOptions(Boolean.TRUE, Boolean.TRUE));

      //
      regionA.addWindow("0");
      regionA.addWindow("1");
      regionB.addWindow("2");
      regionB.addWindow("3");
      regionC.addWindow("4");
      regionC.addWindow("5");
      regionD.addWindow("6");
      regionD.addWindow("7");

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
               WindowObject window = dynaTestContext.getPage().getWindow("7");
               ObjectURL url = requestContext.createURL(window);
               url.setParameter("abc", "def");
               String markup = "<a id=\"bilto\" href=\"" + url + "\">Click Me</a>";
               window.setMarkup(markup);
               break;
            case 1:
               window = dynaTestContext.getPage().getWindow("7");
               url = requestContext.createURL(window);
               url.setParameter("abc", "ghi");
               markup = "<a id=\"bilto\" href=\"" + url + "\">Click Me</a>";
               window.setMarkup(markup);
               break;
            case 2:
               break;
            default:
               fail();
         }
      }
      else
      {
         switch (phase.getCount())
         {
            case 1:
               RenderedObject target = requestContext.getTarget();
               assertNotNull(target);
               assertTrue(target instanceof WindowObject);
               WindowObject window = (WindowObject)target;
               assertEquals("7", window.getId());
               assertTrue(requestContext.isAsync());
               assertEquals(Collections.singletonMap("abc", "def"), requestContext.getParameterMap());
               break;
            case 2:
               target = requestContext.getTarget();
               assertNotNull(target);
               assertTrue(target instanceof WindowObject);
               window = (WindowObject)target;
               assertEquals("7", window.getId());
               assertTrue(requestContext.isAsync());
               assertEquals(Collections.singletonMap("abc", "ghi"), requestContext.getParameterMap());
               break;
            default:
               fail();
         }
      }

      //
      return null;
   }
}
