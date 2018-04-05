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
package org.jboss.portal.test.theme.tmp;

import junit.framework.TestCase;

import org.jboss.portal.common.net.media.MediaType;
import org.jboss.portal.common.util.CLResourceLoader;
import org.jboss.portal.common.util.LoaderResource;
import org.jboss.portal.theme.LayoutException;
import org.jboss.portal.theme.PortalLayout;
import org.jboss.portal.theme.PortalRenderSet;
import org.jboss.portal.theme.RuntimeContext;
import org.jboss.portal.theme.ServerRegistrationID;
import org.jboss.portal.theme.deployment.jboss.PortalLayoutMetaDataFactory;
import org.jboss.portal.theme.deployment.jboss.RenderSetMetaDataFactory;
import org.jboss.portal.theme.impl.LayoutServiceImpl;
import org.jboss.portal.theme.impl.render.div.DivDecorationRenderer;
import org.jboss.portal.theme.impl.render.div.DivPortletRenderer;
import org.jboss.portal.theme.impl.render.div.DivRegionRenderer;
import org.jboss.portal.theme.impl.render.div.DivWindowRenderer;
import org.jboss.portal.theme.impl.render.dynamic.DynaDecorationRenderer;
import org.jboss.portal.theme.impl.render.dynamic.DynaPortletRenderer;
import org.jboss.portal.theme.impl.render.dynamic.DynaRegionRenderer;
import org.jboss.portal.theme.impl.render.dynamic.DynaWindowRenderer;
import org.jboss.portal.theme.impl.render.empty.EmptyDecorationRenderer;
import org.jboss.portal.theme.impl.render.empty.EmptyPortletRenderer;
import org.jboss.portal.theme.impl.render.empty.EmptyRegionRenderer;
import org.jboss.portal.theme.impl.render.empty.EmptyWindowRenderer;
import org.jboss.portal.theme.metadata.PortalLayoutMetaData;
import org.jboss.portal.theme.metadata.RenderSetMetaData;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;

import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:mholzner@novell.com">Martin Holzner</a>
 * @author <a href="mailto:roy@jboss.org">Roy Russo</a>
 * @version $Revision: 10337 $
 */
public class TestLayoutServiceImpl extends TestCase
{
   private LayoutServiceImpl layoutService;
   private RuntimeContext runtimeContext;
   private List portalLayouts;
   private List renderSets;

   protected void setUp() throws Exception
   {
      layoutService = new LayoutServiceImpl();

      layoutService.create();
      layoutService.start();

      runtimeContext = TestHelper.createRuntimeContext();

      LoaderResource res = new CLResourceLoader().getResource("xml/portal-layouts.xml");
      assertNotNull("Layout xml could not be loaded", res);
      Unmarshaller unmarshaller = null;
      unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      portalLayouts = (List)unmarshaller.unmarshal(res.asInputStream(), new PortalLayoutMetaDataFactory(), null);

      for (Iterator i = portalLayouts.iterator(); i.hasNext();)
      {
         PortalLayoutMetaData layoutMD = (PortalLayoutMetaData)i.next();
         layoutService.addLayout(runtimeContext, layoutMD);
      }

      res = new CLResourceLoader().getResource("xml/portal-renderSet.xml");
      unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      renderSets = (List)unmarshaller.unmarshal(res.asInputStream(), new RenderSetMetaDataFactory(), null);
      for (Iterator i = renderSets.iterator(); i.hasNext();)
      {
         RenderSetMetaData renderSetMD = (RenderSetMetaData)i.next();
         layoutService.addRenderSet(runtimeContext, renderSetMD);
      }
   }

   protected void tearDown() throws Exception
   {
      layoutService = null;
      renderSets = null;
   }

   public void testCreateLayouts()
   {
      try
      {
         assertTrue(layoutService.getLayouts().size() == 3);

         PortalLayout layout = layoutService.getLayout("generic", true);
         assertTrue(layout.getLayoutInfo().getName().equals("generic"));
         layout = layoutService.getLayout("nodesk", true);
         assertTrue(layout.getLayoutInfo().getName().equals("nodesk"));
         layout = layoutService.getLayout("phalanx", true);
         assertTrue(layout.getLayoutInfo().getName().equals("phalanx"));
      }
      catch (Exception e)
      {
         assertFalse("unexpected exception occured", true);
         e.printStackTrace();
      }
   }

   public void testGetLayoutInfos()
   {
      PortalLayout layout = layoutService.getLayout("nodesk", true);
      assertTrue(layout.getLayoutInfo().getName().equals("nodesk"));
      assertTrue(layout.getLayoutInfo().getURI().equals("/nodesk/index.jsp"));
      assertTrue(layout.getLayoutInfo().getRegionNames().size() == 2);

      layout = layoutService.getLayout("phalanx", true);
      assertTrue(layout.getLayoutInfo().getName().equals("phalanx"));
      assertNotNull(layout.getLayoutInfo().getURI());

      layout = layoutService.getLayout("generic", true);
      assertTrue(layout.getLayoutInfo().getName().equals("generic"));
      assertTrue(layout.getLayoutInfo().getRegionNames().size() == 3);
      List regions = layout.getLayoutInfo().getRegionNames();
      assertTrue(regions.get(0).equals("left"));
      assertTrue(regions.get(1).equals("center"));
      assertTrue(regions.get(2).equals("navigation"));
   }

   public void testRemoveLayouts()
   {
      try
      {
         assertNotNull(layoutService.getLayout("generic", false));
         layoutService.removeLayouts(runtimeContext.getAppId());
         assertNull(layoutService.getLayout("generic", false));

         for (Iterator i = portalLayouts.iterator(); i.hasNext();)
         {
            PortalLayoutMetaData layoutMD = (PortalLayoutMetaData)i.next();
            layoutService.addLayout(runtimeContext, layoutMD);
         }

         ServerRegistrationID id = ServerRegistrationID.createPortalLayoutID(runtimeContext.getAppId(), "generic");
         layoutService.removeLayout(id);
         assertNull(layoutService.getLayout("generic", false));
      }
      catch (Exception e)
      {
         assertFalse("unexpected exception occured", true);
         e.printStackTrace();
      }
   }

   public void testCreateRenderSets()
   {
      PortalRenderSet renderSet = layoutService.getRenderSet("divRenderer", MediaType.TEXT_HTML);
      assertEquals(renderSet.getName(), "divRenderer");

      renderSet = layoutService.getRenderSet("emptyRenderer", MediaType.TEXT_HTML);
      assertEquals(renderSet.getName(), "emptyRenderer");
   }

   public void testGetRenderSetInfos() throws Exception
   {
      PortalRenderSet renderSet = layoutService.getRenderSet("divRenderer", MediaType.TEXT_HTML);
      assertEquals(renderSet.getName(), "divRenderer");
      assertTrue(renderSet.isAjaxEnabled());
      assertEquals(renderSet.getDecorationRenderer().getClass(), new DynaDecorationRenderer(new DivDecorationRenderer()).getClass());
      assertEquals(renderSet.getPortletRenderer().getClass(), new DynaPortletRenderer(new DivPortletRenderer()).getClass());
      assertEquals(renderSet.getRegionRenderer().getClass(), new DynaRegionRenderer(new DivRegionRenderer()).getClass());
      assertEquals(renderSet.getWindowRenderer().getClass(), new DynaWindowRenderer(new DivWindowRenderer()).getClass());

      renderSet = layoutService.getRenderSet("emptyRenderer", MediaType.TEXT_HTML);
      assertEquals(renderSet.getName(), "emptyRenderer");
      assertFalse(renderSet.isAjaxEnabled());
      assertEquals(renderSet.getDecorationRenderer().getClass(), new EmptyDecorationRenderer().getClass());
      assertEquals(renderSet.getPortletRenderer().getClass(), new EmptyPortletRenderer().getClass());
      assertEquals(renderSet.getRegionRenderer().getClass(), new EmptyRegionRenderer().getClass());
      assertEquals(renderSet.getWindowRenderer().getClass(), new EmptyWindowRenderer().getClass());
   }

   public void testRemoveRenderSets() throws LayoutException
   {
      assertNotNull(layoutService.getRenderSet("divRenderer", MediaType.TEXT_HTML));
      ServerRegistrationID id = ServerRegistrationID.createPortalLayoutID(runtimeContext.getAppId(), "divRenderer");
      layoutService.removeRenderSets(id.getName(0));
      assertNull(layoutService.getRenderSet("divRenderer", MediaType.TEXT_HTML));
   }
}
