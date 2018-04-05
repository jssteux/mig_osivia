/*
* JBoss, a division of Red Hat
* Copyright 2006, Red Hat Middleware, LLC, and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package org.jboss.portal.test.theme.tmp.render;

import junit.framework.TestCase;

import org.jboss.portal.common.net.media.MediaType;
import org.jboss.portal.common.util.CLResourceLoader;
import org.jboss.portal.common.util.LoaderResource;
import org.jboss.portal.common.util.MarkupInfo;
import org.jboss.portal.server.ServerRequest;
import org.jboss.portal.test.theme.tmp.TestHelper;
import org.jboss.portal.theme.LayoutService;
import org.jboss.portal.theme.PortalLayout;
import org.jboss.portal.theme.PortalRenderSet;
import org.jboss.portal.theme.PortalTheme;
import org.jboss.portal.theme.RuntimeContext;
import org.jboss.portal.theme.ThemeConstants;
import org.jboss.portal.theme.ThemeService;
import org.jboss.portal.theme.deployment.jboss.PortalLayoutMetaDataFactory;
import org.jboss.portal.theme.deployment.jboss.PortalThemeMetaDataFactory;
import org.jboss.portal.theme.deployment.jboss.RenderSetMetaDataFactory;
import org.jboss.portal.theme.impl.LayoutServiceImpl;
import org.jboss.portal.theme.impl.ThemeServiceImpl;
import org.jboss.portal.theme.impl.render.dynamic.DynaConstants;
import org.jboss.portal.theme.metadata.PortalLayoutMetaData;
import org.jboss.portal.theme.metadata.PortalThemeMetaData;
import org.jboss.portal.theme.metadata.RenderSetMetaData;
import org.jboss.portal.theme.page.PageResult;
import org.jboss.portal.theme.page.WindowContext;
import org.jboss.portal.theme.page.WindowResult;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:royrusso@yahoo.com">Roy Russo</a>
 * @version $Revision: 10337 $
 */
public class TestRenderedMarkup extends TestCase
{
   private LayoutService layoutService;
   private ThemeService themeService;
   private RuntimeContext runtimeContext;

   private PortalRenderSet renderSet;
   private PortalLayout layout;
   private PortalTheme theme;


   private WindowResult markupResult;

   private static final String TEST_PAGE_NAME = "TestPage";

   /** How many windows to create on left region * */
   private static final int LEFT_WINDOWS = 3;

   /** How many windows to create on center region * */
   private static final int CENTER_WINDOWS = 3;

   protected void setUp() throws Exception
   {
      themeService = new ThemeServiceImpl();
      layoutService = new LayoutServiceImpl();

      runtimeContext = TestHelper.createRuntimeContext();
      LoaderResource res = new CLResourceLoader().getResource("xml/portal-layouts.xml");
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();

      List portalLayouts = (List)unmarshaller.unmarshal(res.asInputStream(), new PortalLayoutMetaDataFactory(), null);
      for (Iterator i = portalLayouts.iterator(); i.hasNext();)
      {
         PortalLayoutMetaData layoutMD = (PortalLayoutMetaData)i.next();
         layoutService.addLayout(runtimeContext, layoutMD);
      }
      layout = layoutService.getLayout("generic", true);

      res = new CLResourceLoader().getResource("xml/portal-renderSet.xml");
      unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      List renderSets = (List)unmarshaller.unmarshal(res.asInputStream(), new RenderSetMetaDataFactory(), null);
      for (Iterator i = renderSets.iterator(); i.hasNext();)
      {
         RenderSetMetaData renderSetMD = (RenderSetMetaData)i.next();
         layoutService.addRenderSet(runtimeContext, renderSetMD);
      }
      renderSet = layoutService.getRenderSet("divRenderer", MediaType.TEXT_HTML);

      res = new CLResourceLoader().getResource("xml/portal-themes.xml");
      unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();

      List themes = (List)unmarshaller.unmarshal(res.asInputStream(), new PortalThemeMetaDataFactory(), null);
      for (Iterator i = themes.iterator(); i.hasNext();)
      {
         PortalThemeMetaData themeMD = (PortalThemeMetaData)i.next();
         themeService.addTheme(runtimeContext, themeMD);
      }
      theme = themeService.getTheme("renaissance", true);
   }

   public void testCreateMarkup() throws Exception
   {
      //
      Map pageProps = new HashMap();
      pageProps.put(DynaConstants.RESOURCE_BASE_URL, "/portal-ajax");
      pageProps.put(DynaConstants.SERVER_BASE_URL, "http://localhost:8080/portal-ajax/ajax");

      //
      Map portalProps = new HashMap();
      portalProps.put(ThemeConstants.PORTAL_PROP_RENDERSET, renderSet.getName());

      //
      MarkupInfo streamInfo = new MarkupInfo(MediaType.TEXT_HTML, "utf-8");
      ServerRequest serverRequest = TestHelper.createServerRequest();

      // pageresult obj
      PageResult pageResult = new PageResult(TEST_PAGE_NAME, pageProps);

      // create window ctx/results and add to page result
      for (int i = 0; i < LEFT_WINDOWS; i++)
      {
         String title = "WindowA_" + i + " Title";
         String content = "<p>WindowA_" + i + " Content</p>";

         WindowContext wCtx = new WindowContext("WindowA_" + i, "left", "" + i, createWindowMarkup(title, content));
         pageResult.addWindowContext(wCtx);

         markupResult = createWindowMarkup(title, content);

         assertEquals(markupResult.getContent(), content);
         assertEquals(markupResult.getTitle(), title);
      }
      for (int i = 0; i < CENTER_WINDOWS; i++)
      {
         String title = "WindowB_" + i + " Title";
         String content = "<p>WindowB_" + i + " Content</p>";

         WindowContext wCtx = new WindowContext("WindowB_" + i, "center", "" + i, createWindowMarkup(title, content));
         pageResult.addWindowContext(wCtx);

         markupResult = createWindowMarkup(title, content);

         assertEquals(markupResult.getContent(), content);
         assertEquals(markupResult.getTitle(), title);
      }

//      RenderContext renderCtx = new RenderContext(layout, streamInfo, markupResult, serverRequest);
//
//      // test renderer
//      assertEquals(renderCtx.getRegionRenderer(), renderCtx.getRegionRenderer(renderSet.getName()));
//      assertEquals(renderCtx.getLayoutRegionNames(), layout.getLayoutInfo().getRegionNames());
//      assertNull(renderCtx.getRegionOrientation());
//      assertNull(renderCtx.getRegionID());
//      assertTrue(renderSet.isAjaxEnabled());
//      assertEquals(renderCtx.getDecorationRenderer().getClass(), new DynaDecorationRenderer(new DivDecorationRenderer()).getClass());
//      assertEquals(renderCtx.getPortletRenderer().getClass(), new DynaPortletRenderer(new DivPortletRenderer()).getClass());
//      assertEquals(renderCtx.getRegionRenderer().getClass(), new DynaRegionRenderer(new DivRegionRenderer()).getClass());
//      assertEquals(renderCtx.getWindowRenderer().getClass(), new DynaWindowRenderer(new DivWindowRenderer()).getClass());
//
//      // test fragment + page props
//      assertNotNull(renderCtx.getMarkupFragment());
//      assertNotNull(renderCtx.getPageProperties());
//
//      // test window renderers
//      assertEquals(renderCtx.getWindowRenderer(), renderCtx.getWindowRenderer(markupResult));
//      assertEquals(renderCtx.getPortletRenderer(), renderCtx.getPortletRenderer(markupResult));
//      assertEquals(renderCtx.getDecorationRenderer(), renderCtx.getDecorationRenderer(markupResult));
//      assertNotNull(renderCtx.getRegionRenderer());
   }

   /**
    * Generates the WindowResult for our windowcontext
    *
    * @param sTitle
    * @param sContent
    * @return window markup result
    */
   private WindowResult createWindowMarkup(String sTitle, String sContent)
   {
      return new WindowResult(sTitle, sContent, Collections.EMPTY_MAP, new HashMap(), null, null, null);
   }
}
