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

import org.jboss.portal.common.invocation.AttributeResolver;
import org.jboss.portal.server.ServerRequest;
import org.jboss.portal.theme.LayoutService;
import org.jboss.portal.theme.PageService;
import org.jboss.portal.theme.RuntimeContext;
import org.jboss.portal.theme.ThemeService;
import org.jboss.portal.theme.deployment.jboss.PortalLayoutMetaDataFactory;
import org.jboss.portal.theme.deployment.jboss.RenderSetMetaDataFactory;
import org.jboss.portal.theme.impl.LayoutServiceImpl;
import org.jboss.portal.theme.impl.PageServiceImpl;
import org.jboss.portal.theme.impl.ThemeServiceImpl;
import org.jboss.portal.theme.metadata.PortalLayoutMetaData;
import org.jboss.portal.theme.metadata.RenderSetMetaData;
import org.jboss.portal.theme.page.PageResult;
import org.jboss.portal.theme.page.WindowContext;
import org.jboss.portal.theme.page.WindowResult;
import org.jboss.system.ServiceMBeanSupport;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.xml.sax.SAXException;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:mholzner@novell.com">Martin Holzner</a>
 * @version $Revision: 11754 $
 */
public class TestHelper
{
   public static PageService createPageService() throws Exception
   {
      LayoutService layoutService = new LayoutServiceImpl();
      ((ServiceMBeanSupport)layoutService).create();
      ((ServiceMBeanSupport)layoutService).start();

      ThemeService themeService = new ThemeServiceImpl();
      ((ServiceMBeanSupport)themeService).create();
      ((ServiceMBeanSupport)themeService).start();

      PageService pageService = new PageServiceImpl();
      pageService.setLayoutService(layoutService);
      pageService.setThemeService(themeService);
      ((ServiceMBeanSupport)pageService).create();
      ((ServiceMBeanSupport)pageService).start();

      return pageService;
   }

   public static void destroyPageService(PageService pageService)
   {
      ((ServiceMBeanSupport)pageService.getLayoutService()).stop();
      ((ServiceMBeanSupport)pageService.getThemeService()).stop();
      ((ServiceMBeanSupport)pageService).stop();
      ((ServiceMBeanSupport)pageService.getLayoutService()).destroy();
      ((ServiceMBeanSupport)pageService.getThemeService()).destroy();
      ((ServiceMBeanSupport)pageService).destroy();
   }

   public static RuntimeContext createRuntimeContext()
   {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      return new RuntimeContext("TestAppId", new TestContext(), "TestContextPath", loader);
   }

   public static WindowResult createWindowResult(String windowTitle, String content)
   {
      // setup the window result
      return createWindowResult(windowTitle, content, Collections.EMPTY_MAP);
   }

   public static WindowResult createWindowResult(String title, String content, Map windowProps)
   {
      // setup the window result
      Map actionsMap = Collections.EMPTY_MAP;
      return new WindowResult(title, content, actionsMap, windowProps, null, null, null);
   }

   public static RenderSetMetaData createRenderSetMD() throws Exception
   {
      List renderSets = (List)UnmarshallerFactory.newInstance().newUnmarshaller().unmarshal(createRenderSetDescriptor(), new RenderSetMetaDataFactory(), null);
      return (RenderSetMetaData)renderSets.get(0);
   }

   public static List createRenderSetMDList() throws Exception
   {
      return (List)UnmarshallerFactory.newInstance().newUnmarshaller().unmarshal(createRenderSetDescriptor(), new RenderSetMetaDataFactory(), null);
   }

   private static Reader createRenderSetDescriptor()
      throws IOException, SAXException, ParserConfigurationException
   {
      StringBuffer testString = new StringBuffer();
      testString.append("<portal-renderSet>");
      testString.append("<renderSet name=\"emptyRenderer\">");
      testString.append("<set content-type=\"text/html\">");
      testString.append("<region-renderer>org.jboss.portal.theme.impl.render.empty.EmptyRegionRenderer</region-renderer>");
      testString.append("<window-renderer>org.jboss.portal.theme.impl.render.empty.EmptyWindowRenderer</window-renderer>");
      testString.append("<portlet-renderer>org.jboss.portal.theme.impl.render.empty.EmptyPortletRenderer</portlet-renderer>");
      testString.append("<decoration-renderer>org.jboss.portal.theme.impl.render.empty.EmptyDecorationRenderer</decoration-renderer>");
      testString.append("</set>");
      testString.append("</renderSet>");

      testString.append("<renderSet name=\"divRenderer\">");
      testString.append("<set content-type=\"text/html\">");
      testString.append("<region-renderer>org.jboss.portal.theme.impl.render.div.DivRegionRenderer</region-renderer>");
      testString.append("<window-renderer>org.jboss.portal.theme.impl.render.div.DivWindowRenderer</window-renderer>");
      testString.append("<portlet-renderer>org.jboss.portal.theme.impl.render.div.DivPortletRenderer</portlet-renderer>");
      testString.append("<decoration-renderer>org.jboss.portal.theme.impl.render.div.DivDecorationRenderer</decoration-renderer>");
      testString.append("</set>");
      testString.append("</renderSet>");

      testString.append("</portal-renderSet>");

      return new StringReader(testString.toString());
   }

   public static PortalLayoutMetaData createLayoutMD() throws Exception
   {
      // create a descriptor for the tests
      StringBuffer testString = new StringBuffer();
      testString.append("<layouts>");
      testString.append("<layout>");
      testString.append("<name>TestLayout</name>");
      testString.append("<uri>/test/index.jsp</uri>");
      testString.append("<regions>");
      testString.append("<region name=\"left\"/>");
      testString.append("<region name=\"center\"/>");
      testString.append("</regions>");
      testString.append("</layout>");
      testString.append("</layouts>");
      List layoutMDList = (List)UnmarshallerFactory.newInstance().newUnmarshaller().unmarshal(new StringReader(testString.toString()), new PortalLayoutMetaDataFactory(), null);
      return (PortalLayoutMetaData)layoutMDList.get(0);
   }

   public static PageResult createPageResult(AttributeResolver navCtx, String pageName,
                                             Map pageProperties, Map portalProperties, WindowContext[] windows)
   {
      PageResult pageResult = new PageResult(pageName, pageProperties);
      for (int i = 0; i < windows.length; i++)
      {
         pageResult.addWindowContext(windows[i]);
      }
      return pageResult;
   }

   public static ServerRequest createServerRequest()
   {
      // final HttpServletRequest clientRequest = new TestHttpServletRequest();
      return new ServerRequest(null);
   }

   public static class TestContext implements ServletContext
   {
      public TestContext()
      {

      }

      public ServletContext getContext(String string)
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public int getMajorVersion()
      {
         return 0;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public int getMinorVersion()
      {
         return 0;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getMimeType(String string)
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public Set getResourcePaths(String string)
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public URL getResource(String string) throws MalformedURLException
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public InputStream getResourceAsStream(String string)
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public RequestDispatcher getRequestDispatcher(String string)
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public RequestDispatcher getNamedDispatcher(String string)
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public Servlet getServlet(String string) throws ServletException
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public Enumeration getServlets()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public Enumeration getServletNames()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public void log(String string)
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public void log(Exception exception, String string)
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public void log(String string, Throwable throwable)
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getRealPath(String string)
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getServerInfo()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getInitParameter(String string)
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public Enumeration getInitParameterNames()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public Object getAttribute(String string)
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public Enumeration getAttributeNames()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public void setAttribute(String string, Object object)
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public void removeAttribute(String string)
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getServletContextName()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }
   }


   // request
   public static class TestHttpServletRequest implements HttpServletRequest
   {
      private Map attributes;

      public TestHttpServletRequest()
      {
         attributes = new HashMap();
      }

      public String getAuthType()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public Cookie[] getCookies()
      {
         return new Cookie[0];  //To change body of implemented methods use File | Settings | File Templates.
      }

      public long getDateHeader(String string)
      {
         return 0;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getHeader(String string)
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public Enumeration getHeaders(String string)
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public Enumeration getHeaderNames()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public int getIntHeader(String string)
      {
         return 0;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getMethod()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getPathInfo()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getPathTranslated()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getContextPath()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getQueryString()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getRemoteUser()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public boolean isUserInRole(String string)
      {
         return false;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public Principal getUserPrincipal()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getRequestedSessionId()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getRequestURI()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public StringBuffer getRequestURL()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getServletPath()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public HttpSession getSession(boolean b)
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public HttpSession getSession()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public boolean isRequestedSessionIdValid()
      {
         return false;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public boolean isRequestedSessionIdFromCookie()
      {
         return false;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public boolean isRequestedSessionIdFromURL()
      {
         return false;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public boolean isRequestedSessionIdFromUrl()
      {
         return false;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public Object getAttribute(String key)
      {
         return attributes.get(key);
      }

      public Enumeration getAttributeNames()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getCharacterEncoding()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public void setCharacterEncoding(String string) throws UnsupportedEncodingException
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public int getContentLength()
      {
         return 0;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getContentType()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public ServletInputStream getInputStream() throws IOException
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getParameter(String string)
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public Enumeration getParameterNames()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String[] getParameterValues(String string)
      {
         return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
      }

      public Map getParameterMap()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getProtocol()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getScheme()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getServerName()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public int getServerPort()
      {
         return 0;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public BufferedReader getReader() throws IOException
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getRemoteAddr()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getRemoteHost()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public void setAttribute(String key, Object value)
      {
         attributes.put(key, value);
      }

      public void removeAttribute(String key)
      {
         attributes.remove(key);
      }

      public Locale getLocale()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public Enumeration getLocales()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public boolean isSecure()
      {
         return false;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public RequestDispatcher getRequestDispatcher(String string)
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getRealPath(String string)
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public int getRemotePort()
      {
         return 0;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getLocalName()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getLocalAddr()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public int getLocalPort()
      {
         return 0;  //To change body of implemented methods use File | Settings | File Templates.
      }
   }

   // response
   public static class TestHttpServletResponse implements HttpServletResponse
   {
      private StringWriter writer;

      public TestHttpServletResponse()
      {

      }

      public void addCookie(Cookie cookie)
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public boolean containsHeader(String string)
      {
         return false;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String encodeURL(String string)
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String encodeRedirectURL(String string)
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String encodeUrl(String string)
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String encodeRedirectUrl(String string)
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public void sendError(int i, String string) throws IOException
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public void sendError(int i) throws IOException
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public void sendRedirect(String string) throws IOException
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public void setDateHeader(String string, long l)
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public void addDateHeader(String string, long l)
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public void setHeader(String string, String string1)
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public void addHeader(String string, String string1)
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public void setIntHeader(String string, int i)
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public void addIntHeader(String string, int i)
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public void setStatus(int i)
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public void setStatus(int i, String string)
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getCharacterEncoding()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getContentType()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public ServletOutputStream getOutputStream() throws IOException
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public PrintWriter getWriter() throws IOException
      {
         writer = new StringWriter();
         return new PrintWriter(writer);
      }

      public String getResult()
      {
         return writer.toString();
      }

      public void setCharacterEncoding(String string)
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public void setContentLength(int i)
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public void setContentType(String string)
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public void setBufferSize(int i)
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public int getBufferSize()
      {
         return 0;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public void flushBuffer() throws IOException
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public void resetBuffer()
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public boolean isCommitted()
      {
         return false;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public void reset()
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public void setLocale(Locale locale)
      {
         //To change body of implemented methods use File | Settings | File Templates.
      }

      public Locale getLocale()
      {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getResponseMarkup()
      {
         return null;  //To change body of created methods use File | Settings | File Templates.
      }
   }

}
