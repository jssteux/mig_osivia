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
package org.jboss.portal.test.theme.dyna.tests.refresh;

import org.jboss.portal.common.util.MapBuilder;
import org.jboss.portal.test.framework.driver.DriverResponse;
import org.jboss.portal.test.framework.driver.response.EndTestResponse;
import org.jboss.portal.test.framework.driver.web.response.PerformClickResponse;
import org.jboss.portal.test.theme.DynaTest;
import org.jboss.portal.test.theme.DynaTestContext;
import org.jboss.portal.test.theme.ObjectURL;
import org.jboss.portal.test.theme.RequestContext;
import org.jboss.portal.test.theme.TestPhase;
import org.jboss.portal.test.theme.model.PageObject;
import org.jboss.portal.test.theme.model.RegionObject;
import org.jboss.portal.test.theme.model.RenderedObject;
import org.jboss.portal.test.theme.model.WindowObject;
import org.jboss.portal.theme.impl.render.dynamic.DynaRenderOptions;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class FormPartialRefreshWindowTest extends DynaTest
{

   /** . */
   private DynaTestContext dynaTestContext;

   /** . */
   private final Boolean regionPartialRefresh;

   /** . */
   private final Boolean windowPartialRefresh;

   /** . */
   private final boolean async;

   public FormPartialRefreshWindowTest(
      String id,
      Boolean regionPartialRefresh,
      Boolean windowPartialRefresh,
      boolean async)
   {
      super(id);

      //
      this.regionPartialRefresh = regionPartialRefresh;
      this.windowPartialRefresh = windowPartialRefresh;
      this.async = async;
   }

   public void init(DynaTestContext dynaTestContext)
   {
      PageObject page = dynaTestContext.getPage();

      //
      RegionObject regionA = page.createRegion("RegionA");
      regionA.setRenderOptions(DynaRenderOptions.getOptions(null, regionPartialRefresh));

      //
      WindowObject window0 = regionA.addWindow("0");
      window0.setRenderOptions(DynaRenderOptions.getOptions(null, windowPartialRefresh));

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
               WindowObject window = dynaTestContext.getPage().getWindow("0");
               ObjectURL url = requestContext.createURL(window);
               url.setParameter("bar", "bar_value_1");
               String markup = "<form action=\"" + url + "\" method=\"post\">" +
                  "<input type=\"hidden\" name=\"foo\" value=\"foo_value_1\">" +
                  "<input id=\"link\" type=\"submit\">" +
                  "</form>";
               window.setMarkup(markup);
               return new PerformClickResponse("link");
            case 1:
               window = dynaTestContext.getPage().getWindow("0");
               url = requestContext.createURL(window);
               url.setParameter("bar", "bar_value_2");
               markup = "<form action=\"" + url + "\" method=\"post\">" +
                  "<input type=\"hidden\" name=\"foo\" value=\"foo_value_2\">" +
                  "<input id=\"link\" type=\"submit\">" +
                  "</form>";
               window.setMarkup(markup);
               return new PerformClickResponse("link");
            case 2:
               return new EndTestResponse();
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
               assertEquals("0", window.getId());
               assertEquals(async, requestContext.isAsync());
               assertEquals(MapBuilder.hashMap().put("foo", "foo_value_1").put("bar", "bar_value_1").get(), requestContext.getParameterMap());
               return null;
            case 2:
               target = requestContext.getTarget();
               assertNotNull(target);
               assertTrue(target instanceof WindowObject);
               window = (WindowObject)target;
               assertEquals("0", window.getId());
               assertEquals(async, requestContext.isAsync());
               assertEquals(MapBuilder.hashMap().put("foo", "foo_value_2").put("bar", "bar_value_2").get(), requestContext.getParameterMap());
               return null;
            default:
               fail();
         }
      }

      //
      return new EndTestResponse();
   }
}
