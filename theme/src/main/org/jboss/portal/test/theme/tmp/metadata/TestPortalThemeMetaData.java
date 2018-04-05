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
import org.jboss.portal.theme.deployment.jboss.PortalThemeMetaDataFactory;
import org.jboss.portal.theme.metadata.PortalThemeMetaData;
import org.jboss.portal.theme.metadata.ThemeLinkMetaData;
import org.jboss.portal.theme.metadata.ThemeScriptMetaData;
import org.jboss.xb.binding.JBossXBException;
import org.jboss.xb.binding.UnmarshallerFactory;

import java.io.StringReader;
import java.util.List;

/**
 * Testcase for the portal theme meta data parsing from the descriptor
 * <p/>
 * example descriptor:
 * <p/>
 * <themes> <theme> <name>industrial</name> <link rel="stylesheet" id="main_css" href="/themes/industrial/portal_style.css"
 * type="text/css" /> <link rel="shortcut icon" href="/themes/industrial/images/favicon.ico" /> </theme> <theme>
 * <name>Nphalanx</name> <link rel="stylesheet" id="main_css" href="/themes/phalanx/portal_style.css" type="text/css" />
 * <link rel="shortcut icon" href="/themes/phalanx/images/favicon.ico" /> </theme> <theme> <name>mission-critical</name>
 * <link rel="stylesheet" id="main_css" href="/themes/mission-critical/portal_style.css" type="text/css" /> <link
 * rel="shortcut icon" href="/themes/mission-critical/images/favicon.ico" /> </theme> <theme> <name>Maple</name> <link
 * rel="stylesheet" id="main_css" href="/themes/maple/portal_style.css" type="text/css"/> <link rel="shortcut icon"
 * href="/themes/maple/images/favicon.ico"/> </theme> </themes> *
 *
 * @author <a href="mailto:mholzner@novell.com">Martin Holzner</a>
 * @version $Revision: 8784 $
 */
public class TestPortalThemeMetaData extends TestCase
{

   public void testBuildThemeMetaDataList() throws JBossXBException
   {
      try
      {
         // create a descriptor for the tests
         StringBuffer testString = new StringBuffer();
         testString.append("<themes>");

         testString.append("<theme>");
         testString.append("<name>industrial</name>");
         testString.append("<link rel=\"stylesheet\" id=\"main_css\" href=\"/themes/industrial/portal_style.css\" type=\"text/css\" />");
         testString.append("<link rel=\"shortcut icon\" href=\"/themes/industrial/images/favicon.ico\" />");
         testString.append("</theme>");

         testString.append("<theme>");
         testString.append("<name>mission-critical</name>");
         testString.append("<link rel=\"stylesheet\" id=\"main_css\" href=\"/themes/mission-critical/portal_style.css\" type=\"text/css\" />");
         testString.append("<link rel=\"shortcut icon\" href=\"/themes/mission-critical/images/favicon.ico\" />");
         testString.append("<script id=\"scriptWithBody\" type=\"text/javascript\">some script content here...</script>");
         testString.append("<script src=\"srcURL\" id=\"scriptWithNoBody\" type=\"text/javascript\"/>");
         testString.append("</theme>");

         testString.append("</themes>");

         List themes = (List)UnmarshallerFactory.newInstance().newUnmarshaller().unmarshal(new StringReader(testString.toString()), new PortalThemeMetaDataFactory(), null);

         assertTrue(themes.size() == 2);

         PortalThemeMetaData meta = (PortalThemeMetaData)themes.get(0);
         assertEquals("industrial", meta.getName());
         assertTrue(meta.getLinks().size() == 2);
         assertTrue(meta.getScripts().size() == 0);

         List links = meta.getLinks();
         ThemeLinkMetaData link = (ThemeLinkMetaData)links.get(0);
         assertEquals("/themes/industrial/portal_style.css", link.getHref());
         assertEquals("text/css", link.getType());

         // second link
         link = (ThemeLinkMetaData)links.get(1);
         assertEquals("/themes/industrial/images/favicon.ico", link.getHref());
         assertEquals("shortcut icon", link.getRel());

         // test the second theme
         meta = (PortalThemeMetaData)themes.get(1);
         assertEquals("mission-critical", meta.getName());
         assertTrue(meta.getLinks().size() == 2);
         assertTrue(meta.getScripts().size() == 2);

         ThemeScriptMetaData script = (ThemeScriptMetaData)meta.getScripts().get(0);
         assertTrue(script.getSrc() == null);
         assertEquals("scriptWithBody", script.getId());
         assertEquals("text/javascript", script.getType());

         script = (ThemeScriptMetaData)meta.getScripts().get(1);
         assertEquals("srcURL", script.getSrc());
         assertEquals("scriptWithNoBody", script.getId());
      }
      catch (Exception e)
      {
         assertFalse("unexpected exception occured", true);
      }
   }

   public void testThemeMetaDataNoName() throws JBossXBException
   {
      StringBuffer testString = new StringBuffer();
      testString.append("<themes>");

      // no name defined
      testString.append("<theme>");
//      testString.append("<name>industrial</name>");
      testString.append("<link rel=\"stylesheet\" id=\"main_css\" href=\"/themes/industrial/portal_style.css\" type=\"text/css\" />");
      testString.append("<link rel=\"shortcut icon\" href=\"/themes/industrial/images/favicon.ico\" />");
      testString.append("</theme>");

      testString.append("</themes>");

      List themes = (List)UnmarshallerFactory.newInstance().newUnmarshaller().unmarshal(new StringReader(testString.toString()), new PortalThemeMetaDataFactory(), null);
      PortalThemeMetaData meta = (PortalThemeMetaData)themes.get(0);
      assertTrue(meta.getName() == null);
   }

   public void testThemeMetaDataNoLinks() throws JBossXBException
   {
      StringBuffer testString = new StringBuffer();
      testString.append("<themes>");

      // no link, no script defined
      testString.append("<theme>");
      testString.append("<name>industrial</name>");
      testString.append("</theme>");

      testString.append("</themes>");

      List themes = (List)UnmarshallerFactory.newInstance().newUnmarshaller().unmarshal(new StringReader(testString.toString()), new PortalThemeMetaDataFactory(), null);
      PortalThemeMetaData meta = (PortalThemeMetaData)themes.get(0);
      assertTrue(meta.getLinks().size() == 0);
      assertTrue(meta.getScripts().size() == 0);
   }
}
