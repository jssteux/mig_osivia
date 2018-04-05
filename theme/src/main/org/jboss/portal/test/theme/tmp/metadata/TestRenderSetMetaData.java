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
package org.jboss.portal.test.theme.tmp.metadata;

import junit.framework.TestCase;

import org.jboss.portal.common.net.media.MediaType;
import org.jboss.portal.common.net.media.SubtypeDef;
import org.jboss.portal.common.net.media.TypeDef;
import org.jboss.portal.theme.deployment.jboss.RenderSetMetaDataFactory;
import org.jboss.portal.theme.metadata.RenderSetMetaData;
import org.jboss.portal.theme.metadata.RendererSetMetaData;
import org.jboss.xb.binding.JBossXBException;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

/**
 * Test cases for the render set meta data <p/> <p/> exmaple of a render set descriptor: <p/> <portal-renderSet>
 * <renderSet name="divRenderer"> <set content-type="text/html"> <region-renderer>org.jboss.portal.theme.impl.render.div.DivRegionRenderer</region-renderer>
 * <window-renderer>org.jboss.portal.theme.impl.render.div.DivWindowRenderer</window-renderer>
 * <portlet-renderer>org.jboss.portal.theme.impl.render.div.DivPortletRenderer</portlet-renderer>
 * <decoration-renderer>org.jboss.portal.theme.impl.render.div.DivDecorationRenderer</decoration-renderer> </set>
 * </renderSet> <renderSet name="emptyRenderer"> <set content-type="text/html"> <region-renderer>org.jboss.portal.theme.impl.render.empty.EmptyRegionRenderer</region-renderer>
 * <window-renderer>org.jboss.portal.theme.impl.render.empty.EmptyWindowRenderer</window-renderer>
 * <portlet-renderer>org.jboss.portal.theme.impl.render.empty.EmptyPortletRenderer</portlet-renderer>
 * <decoration-renderer>org.jboss.portal.theme.impl.render.empty.EmptyDecorationRenderer</decoration-renderer> </set>
 * </renderSet> </portal-renderSet> <p/> </p>
 *
 * @author <a href="mailto:mholzner@novell.com">Martin Holzner</a>
 * @version $Revision: 10337 $
 */
public class TestRenderSetMetaData extends TestCase
{

   public void testBuildRenderSetMD() throws IOException, ParserConfigurationException, SAXException
   {
      try
      {
         StringBuffer testString = new StringBuffer();
         testString.append("<portal-renderSet>");

         testString.append("<renderSet name=\"divRenderer\">");

         testString.append("<set content-type=\"text/html\">");
         testString.append("<region-renderer>DivRegionRenderer</region-renderer>");
         testString.append("<window-renderer>DivWindowRenderer</window-renderer>");
         testString.append("<portlet-renderer>DivPortletRenderer</portlet-renderer>");
         testString.append("<decoration-renderer>DivDecorationRenderer</decoration-renderer>");
         testString.append("</set>");

         testString.append("</renderSet>");


         testString.append("<renderSet name=\"emptyRenderer\">");

         testString.append("<set content-type=\"text/html\">");
         testString.append("<region-renderer>EmptyRegionRendererHTML</region-renderer>");
         testString.append("<window-renderer>EmptyWindowRendererHTML</window-renderer>");
         testString.append("<portlet-renderer>EmptyPortletRendererHTML</portlet-renderer>");
         testString.append("<decoration-renderer>EmptyDecorationRendererHTML</decoration-renderer>");
         testString.append("</set>");

         testString.append("<set content-type=\"application/xhtml+xml\">");
         testString.append("<region-renderer>EmptyRegionRendererXHTML</region-renderer>");
         testString.append("<window-renderer>EmptyWindowRendererXHTML</window-renderer>");
         testString.append("<portlet-renderer>EmptyPortletRendererXHTML</portlet-renderer>");
         testString.append("<decoration-renderer>EmptyDecorationRendererXHTML</decoration-renderer>");
         testString.append("</set>");

         testString.append("</renderSet>");

         testString.append("</portal-renderSet>");

         List setList = (List)UnmarshallerFactory.newInstance().newUnmarshaller().unmarshal(new StringReader(testString.toString()), new RenderSetMetaDataFactory(), null);
         assertNotNull(setList);
         assertTrue(setList.size() == 2);

         RenderSetMetaData renderSetMD = (RenderSetMetaData)setList.get(0);
         assertEquals("divRenderer", renderSetMD.getName());
         assertNotNull(renderSetMD.getRendererSet());
         assertTrue(renderSetMD.getRendererSet().size() == 1);
         RendererSetMetaData rendererSetMD = (RendererSetMetaData)renderSetMD.getRendererSet().get(0);
         assertEquals(MediaType.TEXT_HTML, rendererSetMD.getMediaType());
         assertEquals("DivPortletRenderer", rendererSetMD.getPortletRenderer());

         renderSetMD = (RenderSetMetaData)setList.get(1);
         assertEquals("emptyRenderer", renderSetMD.getName());
         assertNotNull(renderSetMD.getRendererSet());
         assertTrue(renderSetMD.getRendererSet().size() == 2);

         rendererSetMD = (RendererSetMetaData)renderSetMD.getRendererSet().get(0);
         assertEquals(MediaType.TEXT_HTML, rendererSetMD.getMediaType());
         assertEquals("EmptyPortletRendererHTML", rendererSetMD.getPortletRenderer());

         rendererSetMD = (RendererSetMetaData)renderSetMD.getRendererSet().get(1);
         assertEquals(MediaType.TEXT_HTML, rendererSetMD.getMediaType());
         assertEquals("EmptyDecorationRendererXHTML", rendererSetMD.getDecorationRenderer());
      }
      catch (Exception e)
      {
         assertFalse("unexpected exception occured" + e.getMessage(), true);
      }
   }

   public void testRenderSetMetaDataNoMime() throws JBossXBException
   {
      StringBuffer testString = new StringBuffer();
      // no content type attribute in the set
      testString.append("<portal-renderSet>");
      testString.append("<renderSet name=\"divRenderer\">");
      testString.append("<set>");
      testString.append("<region-renderer>DivRegionRenderer</region-renderer>");
      testString.append("<window-renderer>DivWindowRenderer</window-renderer>");
      testString.append("<portlet-renderer>DivPortletRenderer</portlet-renderer>");
      testString.append("<decoration-renderer>DivDecorationRenderer</decoration-renderer>");
      testString.append("</set>");
      testString.append("</renderSet>");
      testString.append("</portal-renderSet>");
      try
      {
         List setList = (List)UnmarshallerFactory.newInstance().newUnmarshaller().unmarshal(new StringReader(testString.toString()), new RenderSetMetaDataFactory(), null);
         RenderSetMetaData renderSetMD = (RenderSetMetaData)setList.get(0);
         RendererSetMetaData rendererSetMD = (RendererSetMetaData)renderSetMD.getRendererSet().get(0);
         assertFalse("expected exception did not occur", true);
      }
      catch (JBossXBException e)
      {
         // expected
      }
   }

   public void testRenderSetMetaDataNoWindowRenderer() throws JBossXBException
   {
      StringBuffer testString = new StringBuffer();
      // no window renderer defined in the set
      testString.append("<portal-renderSet>");
      testString.append("<renderSet name=\"divRenderer\">");
      testString.append("<set content-type=\"text/html\">");
      testString.append("<region-renderer>DivRegionRenderer</region-renderer>");
//      testString.append("<window-renderer>DivWindowRenderer</window-renderer>");
      testString.append("<portlet-renderer>DivPortletRenderer</portlet-renderer>");
      testString.append("<decoration-renderer>DivDecorationRenderer</decoration-renderer>");
      testString.append("</set>");
      testString.append("</renderSet>");
      testString.append("</portal-renderSet>");

      List setList = (List)UnmarshallerFactory.newInstance().newUnmarshaller().unmarshal(new StringReader(testString.toString()), new RenderSetMetaDataFactory(), null);
      RenderSetMetaData renderSetMD = (RenderSetMetaData)setList.get(0);
      RendererSetMetaData rendererSetMD = (RendererSetMetaData)renderSetMD.getRendererSet().get(0);
      assertTrue(rendererSetMD.getWindowRenderer() == null);
   }

   public void testRenderSetMetaDataEmpty() throws JBossXBException
   {
      StringBuffer testString = new StringBuffer();
      // empty render set descriptor
      testString.append("<portal-renderSet>");
      testString.append("</portal-renderSet>");

      List setList = (List)UnmarshallerFactory.newInstance().newUnmarshaller().unmarshal(new StringReader(testString.toString()), new RenderSetMetaDataFactory(), null);
      try
      {
         RenderSetMetaData renderSetMD = (RenderSetMetaData)setList.get(0);
         assertFalse("expected exception did not occur", true);
      }
      catch (IndexOutOfBoundsException ioe)
      {
         // expected
      }
   }
}
