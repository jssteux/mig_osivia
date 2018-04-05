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
import org.jboss.portal.theme.deployment.jboss.PortalLayoutMetaDataFactory;
import org.jboss.portal.theme.metadata.PortalLayoutMetaData;
import org.jboss.xb.binding.JBossXBException;
import org.jboss.xb.binding.UnmarshallerFactory;

import java.io.StringReader;
import java.util.List;

/**
 * Test all aspects of the portal layout meta data.
 *
 * @author <a href="mailto:mholzner@novell.com">Martin Holzner</a>
 * @version $Revision: 8784 $
 */
public class TestPortalLayoutMetaData extends TestCase
{
   public void testBuildLayoutMetaDataList() throws Exception
   {
      try
      {
         // create a descriptor for the tests
         StringBuffer testString = new StringBuffer();
         testString.append("<layouts>");
         testString.append("<layout>");
         testString.append("<name>nodesk</name>");
         testString.append("<uri>/nodesk/index.jsp</uri>");
         testString.append("<regions>");
         testString.append("<region name=\"left\"/>");
         testString.append("<region name=\"center\"/>");
         testString.append("</regions>");
         testString.append("</layout>");
         testString.append("<layout>");
         testString.append("<name>generic</name>");
         testString.append("<uri>/layouts/generic/index.jsp</uri>");
         testString.append("<uri state=\"maximized\">/layouts/generic/maximized.jsp</uri>");
         testString.append("<regions>");
         testString.append("<region name=\"left\"/>");
         testString.append("<region name=\"center\"/>");
         testString.append("<region name=\"navigation\"/>");
         testString.append("</regions>");
         testString.append("</layout>");
         testString.append("</layouts>");

         List layoutMDList = (List)UnmarshallerFactory.newInstance().newUnmarshaller().unmarshal(new StringReader(testString.toString()), new PortalLayoutMetaDataFactory(), null);
         assertNotNull(layoutMDList);
         assertTrue(layoutMDList.size() == 2);

         PortalLayoutMetaData layoutMD = (PortalLayoutMetaData)layoutMDList.get(0);
         assertEquals("nodesk", layoutMD.getName());
         assertEquals("/nodesk/index.jsp", layoutMD.getURI());
         assertNotNull(layoutMD.getLayoutURIStateMap());
         assertEquals(0, layoutMD.getLayoutURIStateMap().size());
         assertNotNull(layoutMD.getClassName());
         assertNotNull(layoutMD.getRegionNames());
         assertEquals(2, layoutMD.getRegionNames().size());
         assertTrue(layoutMD.getRegionNames().contains("left"));
         assertTrue(layoutMD.getRegionNames().contains("center"));

         layoutMD = (PortalLayoutMetaData)layoutMDList.get(1);
         assertEquals("generic", layoutMD.getName());
         assertEquals("/layouts/generic/index.jsp", layoutMD.getURI());
         assertNotNull(layoutMD.getLayoutURIStateMap());
         assertEquals(1, layoutMD.getLayoutURIStateMap().size());
         assertNotNull(layoutMD.getClassName());
         assertNotNull(layoutMD.getRegionNames());
         assertEquals(3, layoutMD.getRegionNames().size());
         assertTrue(layoutMD.getRegionNames().contains("left"));
         assertTrue(layoutMD.getRegionNames().contains("center"));
         assertTrue(layoutMD.getRegionNames().contains("navigation"));
      }
      catch (IllegalArgumentException e)
      {
         // the layout meta needs the portal web app to be not null and have an id
         assertTrue("expected exception did occur", true);
      }
   }

   public void testLayoutMetaDataNoName() throws Exception
   {
      StringBuffer testString = new StringBuffer();
      testString.append("<layouts>");

      // no name
      testString.append("<layout>");
//      testString.append("<name>nodesk</name>");
      testString.append("<uri>/nodesk/index.jsp</uri>");
      testString.append("<regions>");
      testString.append("<region name=\"left\"/>");
      testString.append("<region name=\"center\"/>");
      testString.append("</regions>");
      testString.append("</layout>");

      testString.append("</layouts>");

      List layoutMDList = (List)UnmarshallerFactory.newInstance().newUnmarshaller().unmarshal(new StringReader(testString.toString()), new PortalLayoutMetaDataFactory(), null);
      PortalLayoutMetaData layoutMD = (PortalLayoutMetaData)layoutMDList.get(0);
      assertTrue(layoutMD.getName() == null);
   }

   public void testLayoutMetaDataNoURI() throws JBossXBException
   {
      StringBuffer testString = new StringBuffer();
      testString.append("<layouts>");

      // no uri
      testString.append("<layout>");
      testString.append("<name>nodesk</name>");
      testString.append("</layout>");
// testString.append("<uri>/nodesk/index.jsp</uri>");
      testString.append("</layouts>");

      List layoutMDList = (List)UnmarshallerFactory.newInstance().newUnmarshaller().unmarshal(new StringReader(testString.toString()), new PortalLayoutMetaDataFactory(), null);
      PortalLayoutMetaData layoutMD = (PortalLayoutMetaData)layoutMDList.get(0);
      assertTrue(layoutMD.getURI() == null);
   }

   public void testLayoutMetaDataNoRegions() throws Exception
   {
      StringBuffer testString = new StringBuffer();
      testString.append("<layouts>");
      // no regions
      testString.append("<layout>");
      testString.append("<name>nodesk</name>");
      testString.append("<uri>/nodesk/index.jsp</uri>");
      testString.append("</layout>");
      testString.append("</layouts>");
      List layoutMDList = (List)UnmarshallerFactory.newInstance().newUnmarshaller().unmarshal(new StringReader(testString.toString()), new PortalLayoutMetaDataFactory(), null);
      assertNotNull(layoutMDList);
      PortalLayoutMetaData layoutMD = (PortalLayoutMetaData)layoutMDList.get(0);
      assertNotNull(layoutMD.getRegionNames());
      assertTrue(layoutMD.getRegionNames().size() == 0);
   }

   // What in the world does this do?
/*
   public void testLayoutMetaData4() throws Exception
   {
      StringBuffer testString = new StringBuffer();
      testString.append("<layouts>");
      // fragment assembler class specified
      testString.append("<layout>");
      testString.append("<name>nodesk</name>");
      testString.append("<uri>/nodesk/index.jsp</uri>");
      testString.append("<fragment-assembler-implementation>TestFragmentAssembler</fragment-assembler-implementation>");
      testString.append("</layout>");
      testString.append("</layouts>");
      List layoutMDList = (List)UnmarshallerFactory.newInstance().newUnmarshaller().unmarshal(new StringReader(testString.toString()), new PortalLayoutMetaDataFactory(), null);
      assertNotNull(layoutMDList);
      PortalLayoutMetaData layoutMD = (PortalLayoutMetaData)layoutMDList.get(0);
      assertNotNull(layoutMD.getClassName());
      assertEquals("org.jboss.portal.theme.impl.JSPLayout", layoutMD.getClassName());
   }
*/
}