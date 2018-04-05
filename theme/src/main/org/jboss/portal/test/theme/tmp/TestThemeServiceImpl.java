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
import org.jboss.portal.common.util.CLResourceLoader;
import org.jboss.portal.common.util.LoaderResource;
import org.jboss.portal.theme.PortalTheme;
import org.jboss.portal.theme.RuntimeContext;
import org.jboss.portal.theme.ThemeException;
import org.jboss.portal.theme.ThemeService;
import org.jboss.portal.theme.deployment.jboss.PortalThemeMetaDataFactory;
import org.jboss.portal.theme.impl.ThemeServiceImpl;
import org.jboss.portal.theme.metadata.PortalThemeMetaData;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:mholzner@novell.com">Martin Holzner</a>
 * @author <a href="mailto:roy@jboss.org">Roy Russo</a>
 * @version $Revision: 8784 $
 */
public class TestThemeServiceImpl extends TestCase
{
   private ThemeService themeService;
   private RuntimeContext runtimeContext;
   List themes;

   protected void setUp() throws Exception
   {
      themeService = new ThemeServiceImpl();

      runtimeContext = TestHelper.createRuntimeContext();
      LoaderResource res = new CLResourceLoader().getResource("xml/portal-themes.xml");
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();

      themes = (List)unmarshaller.unmarshal(res.asInputStream(), new PortalThemeMetaDataFactory(), null);
      for (Iterator i = themes.iterator(); i.hasNext();)
      {
         PortalThemeMetaData themeMD = (PortalThemeMetaData)i.next();
         themeService.addTheme(runtimeContext, themeMD);
      }
   }

   protected void tearDown() throws Exception
   {
      themeService = null;
   }

   public void testCreateTheme() throws IOException, ParserConfigurationException, SAXException
   {
      try
      {
         assertTrue(themeService.getThemes().size() == 5);

         PortalTheme theme = themeService.getTheme("industrial", false);
         assertTrue(theme.getThemeInfo().getName().equals("industrial"));
         theme = themeService.getTheme("Nphalanx", false);
         assertTrue(theme.getThemeInfo().getName().equals("Nphalanx"));
         theme = themeService.getTheme("mission-critical", false);
         assertTrue(theme.getThemeInfo().getName().equals("mission-critical"));
         theme = themeService.getTheme("Maple", false);
         assertTrue(theme.getThemeInfo().getName().equals("Maple"));
         theme = themeService.getTheme("renaissance", false);
         assertTrue(theme.getThemeInfo().getName().equals("renaissance"));
      }
      catch (Exception e)
      {
         assertTrue("unexpected exception occured", false);
      }
   }

   public void testGetThemeInfos()
   {
      PortalTheme theme = themeService.getTheme("industrial", false);
      assertEquals(theme.getThemeInfo().getName(), "industrial");
      assertTrue(theme.getThemeInfo().getScripts().size() == 0);
      assertTrue(theme.getThemeInfo().getLinks().size() == 2);

      theme = themeService.getTheme("Nphalanx", false);
      assertEquals(theme.getThemeInfo().getName(), "Nphalanx");
      assertTrue(theme.getThemeInfo().getScripts().size() == 0);
      assertTrue(theme.getThemeInfo().getLinks().size() == 1);
   }

   public void testRemoveTheme() throws ThemeException
   {
      assertNotNull(themeService.getTheme("industrial", false));
      themeService.removeTheme(themeService.getTheme("industrial", false));
      assertNull(themeService.getTheme("industrial", false));
   }
}