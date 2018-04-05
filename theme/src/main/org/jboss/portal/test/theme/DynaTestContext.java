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

import org.apache.log4j.Logger;
import org.jboss.mx.util.MBeanProxy;
import org.jboss.mx.util.MBeanServerLocator;
import org.jboss.portal.common.net.media.MediaType;
import org.jboss.portal.common.util.IteratorStatus;
import org.jboss.portal.common.util.MarkupInfo;
import org.jboss.portal.test.framework.driver.DriverCommand;
import org.jboss.portal.test.framework.driver.DriverResponse;
import org.jboss.portal.test.framework.driver.TestDriverException;
import org.jboss.portal.test.framework.driver.command.StartTestCommand;
import org.jboss.portal.test.framework.driver.response.FailureResponse;
import org.jboss.portal.test.framework.driver.web.WebTestCase;
import org.jboss.portal.test.framework.junit.ThrowableDecoder;
import org.jboss.portal.test.theme.model.ObjectChange;
import org.jboss.portal.test.theme.model.PageObject;
import org.jboss.portal.test.theme.model.RenderedObject;
import org.jboss.portal.test.theme.model.RenderedObjectContext;
import org.jboss.portal.test.theme.model.RenderedObjectContextFactory;
import org.jboss.portal.test.theme.model.WindowObject;
import org.jboss.portal.theme.LayoutService;
import org.jboss.portal.theme.PortalLayout;
import org.jboss.portal.theme.PortalRenderSet;
import org.jboss.portal.theme.PortalTheme;
import org.jboss.portal.theme.ThemeConstants;
import org.jboss.portal.theme.ThemeService;
import org.jboss.portal.theme.impl.render.dynamic.DynaConstants;
import org.jboss.portal.theme.impl.render.dynamic.JSONMarshaller;
import org.jboss.portal.theme.impl.render.dynamic.response.UpdatePageStateResponse;
import org.jboss.portal.theme.page.PageResult;
import org.jboss.portal.theme.page.Region;
import org.jboss.portal.theme.page.WindowContext;
import org.jboss.portal.theme.page.WindowResult;
import org.jboss.portal.theme.render.RenderException;
import org.jboss.portal.theme.render.RendererContext;
import org.jboss.portal.theme.render.ThemeContext;
import org.jboss.portal.theme.render.renderer.PageRendererContext;
import org.jboss.portal.theme.render.renderer.RegionRendererContext;
import org.jboss.portal.theme.render.renderer.WindowRendererContext;
import org.jboss.portal.web.ServletContainerFactory;
import org.jboss.portal.web.ServletContextDispatcher;
import org.jboss.portal.web.spi.ServletContainerContext;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 11754 $
 */
public class DynaTestContext extends WebTestCase
{

   private final RenderedObjectContextFactory contextFactory = new RenderedObjectContextFactory()
   {
      public RenderedObjectContext createContext(final RenderedObject object)
      {
         return new RenderedObjectContext()
         {
            public void addChange(ObjectChange change)
            {
               records.add(new ChangeRecord(object, change));
            }
         };
      }
   };

   private final Logger log;

   /** . */
   private PageObject page;

   /** The changes. */
   private final List records = new ArrayList();

   /** . */
   final DynaTest test;

   /** . */
   private final TestServlet servlet;

   /** . */
   ServletContainerFactory servletContainerFactory;

   /** . */
   LayoutService layoutService;

   /** . */
   ThemeService themeService;

   /** . */
   PortalRenderSet renderSet;

   /** . */
   PortalLayout layout;

   /** . */
   PortalTheme theme;

   public DynaTestContext(TestServlet servlet, DynaTest test, String path)
   {
      super(test.getId(), path);

      //
      this.log = Logger.getLogger(test.getClass());
      this.servlet = servlet;
      this.test = test;
   }

   public DriverResponse invoke(String testId, DriverCommand cmd) throws TestDriverException
   {
      if (cmd instanceof StartTestCommand)
      {
         // Setup on servlet
         servlet.testContext = this;

         // Init services
         try
         {
            MBeanServer mbeanServer = MBeanServerLocator.locateJBoss();
            servletContainerFactory = (ServletContainerFactory)MBeanProxy.get(ServletContainerContext.class, new ObjectName("portal:service=ServletContainerFactory"), mbeanServer);
            layoutService = (LayoutService)MBeanProxy.get(LayoutService.class, new ObjectName("portal:service=LayoutService"), mbeanServer);
            themeService = (ThemeService)MBeanProxy.get(ThemeService.class, new ObjectName("portal:service=ThemeService"), mbeanServer);
            renderSet = layoutService.getRenderSet("testRenderer", MediaType.TEXT_HTML);
            layout = layoutService.getLayout("generic", true);
            theme = themeService.getTheme("testTheme", true);

            //
            page = new PageObject(contextFactory);
            page.setProperty(ThemeConstants.PORTAL_PROP_THEME, theme.getThemeInfo().getName());
            page.setProperty(ThemeConstants.PORTAL_PROP_RENDERSET, renderSet.getName());
            page.setProperty(ThemeConstants.PORTAL_PROP_LAYOUT, layout.getLayoutInfo().getName());

            //
            // RegionObject testContextRegion = page.createRegion("TestContextRegion");
            // testContextRegion.setRenderOptions(DynaRenderOptions.getOptions(null, Boolean.FALSE));
            // WindowObject testContextWindow = testContextRegion.addWindow("TestContextWindow");
            // testContextWindow.setRenderOptions(DynaRenderOptions.getOptions(null, Boolean.FALSE));

            //
            test.init(this);
         }
         catch (Exception e)
         {
            return new FailureResponse(e);
         }
      }

      //
      return super.invoke(testId, cmd);
   }

   public PageObject getPage()
   {
      return page;
   }

   void invoke(RequestContext requestContext) throws RenderException, IOException
   {
      // The optional action phase has generated a response already, so we don't do anything
      if (context.getResponse() != null)
      {
         return;
      }

      // Response
      DriverResponse response;

      //
      try
      {
         // Clear recorded changes now
         records.clear();

         // Invoke and get response
         response = test.invoke(requestContext);

         // Modify the test request context
         // page.getWindow("TestContextWindow").setMarkup("<div id=\"RequestCount\">" + context.getRequestCount() + "</div>");

         // Update the page if async
         if (requestContext.getPhase().getLifeCycle() == TestPhase.RENDER_LIFE_CYCLE)
         {
            if (requestContext.isAsync())
            {
               handleAsync(requestContext);
            }
            else
            {
               renderPage(requestContext);
            }

            // Clear recorded changes now
            records.clear();
         }
         else
         {
            // Nothing special to do for now
         }
      }
      catch (Throwable t)
      {
         log.error("Test failed", t);

         //
         response = ThrowableDecoder.decode(t);
      }

      // Set response on context
      if (response != null)
      {
         context.setResponse(response);
      }
   }

   int getRequestCount()
   {
      return context.getRequestCount();
   }

   private void handleAsync(RequestContext requestContext) throws RenderException, IOException
   {
      MarkupInfo markupInfo = new MarkupInfo(MediaType.TEXT_HTML, "UTF-8");
      ServletContextDispatcher dispatcher = new ServletContextDispatcher(requestContext.request, requestContext.response, servletContainerFactory.getServletContainer());
      ThemeContext themeContext = new ThemeContext(theme, themeService);

      //
      UpdatePageStateResponse resp = new UpdatePageStateResponse(null);

      //
      page.setProperty(DynaConstants.RESOURCE_BASE_URL, requestContext.request.getContextPath() + "/dyna");
      page.setProperty(DynaConstants.SERVER_BASE_URL, requestContext.request.getContextPath() + requestContext.request.getServletPath());

      //
      for (int i = 0; i < records.size(); i++)
      {
         ChangeRecord record = (ChangeRecord)records.get(i);

         // For now only that use case exist, so it's ok...
         WindowObject window = (WindowObject)record.source;

         //
         StringWriter buffer = new StringWriter();
         RendererContext rendererCtx = layout.getRenderContext(themeContext, markupInfo, dispatcher, buffer);
         rendererCtx.pushObjectRenderContext(page.getRendererContext());
         rendererCtx.pushObjectRenderContext(window.getRegion().getRendererContext());
         rendererCtx.render(window.getRendererContext());

         //
         resp.addFragment(window.getId(), buffer.toString());
      }

      //
      JSONMarshaller marshaller = new JSONMarshaller();
      requestContext.response.setContentType("text/xml");
      marshaller.write(resp, requestContext.response.getWriter());
   }

   /** Renders the page fully in the resposne provided by the request context. */
   private void renderPage(RequestContext requestContext) throws RenderException
   {
      requestContext.response.setContentType("text/html");

      MarkupInfo markupInfo = new MarkupInfo(MediaType.TEXT_HTML, "UTF-8");
      ServletContextDispatcher dispatcher = new ServletContextDispatcher(requestContext.request, requestContext.response, servletContainerFactory.getServletContainer());
      ThemeContext themeContext = new ThemeContext(theme, themeService);

      // Build a page structure
      PageResult pageRenderCtx = createResult(page.getRendererContext());

      //
      pageRenderCtx.getProperties().put(DynaConstants.RESOURCE_BASE_URL, requestContext.request.getContextPath() + "/dyna");
      pageRenderCtx.getProperties().put(DynaConstants.SERVER_BASE_URL, requestContext.request.getContextPath() + requestContext.request.getServletPath());

      //
      RendererContext rendererCtx = layout.getRenderContext(themeContext, markupInfo, dispatcher);
      rendererCtx.render(pageRenderCtx);
   }

   /** Necessary for now, until the JSPLayout does not cast the page render context to page result. */
   private PageResult createResult(PageRendererContext prc)
   {
      PageResult result = new PageResult("page");

      //
      result.getProperties().putAll(prc.getProperties());

      //
      for (Iterator i = prc.getRegions().iterator(); i.hasNext();)
      {
         RegionRendererContext rrc = (RegionRendererContext)i.next();

         //
         for (IteratorStatus j = new IteratorStatus(rrc.getWindows().iterator()); j.hasNext();)
         {
            WindowRendererContext wrc = (WindowRendererContext)j.next();

            //
            WindowResult wr = new WindowResult(
               wrc.getDecoration().getTitle(),
               wrc.getPortlet().getMarkup(),
               new HashMap(),
               wrc.getProperties(),
               null,
               wrc.getWindowState(),
               wrc.getMode());
            WindowContext wc = new WindowContext(wrc.getId(), rrc.getId(), Integer.toString(j.getIndex()), wr);
            result.addWindowContext(wc);
         }

         //
         Region region = result.getRegion2(rrc.getId());
         if (region != null)
         {
            region.getProperties().putAll(rrc.getProperties());
         }
      }

      //
      result.setLayoutState(null);

      //
      return result;
   }

   private class ChangeRecord
   {

      /** . */
      private RenderedObject source;

      /** . */
      private ObjectChange change;

      public ChangeRecord(RenderedObject source, ObjectChange change)
      {
         this.source = source;
         this.change = change;
      }
   }
}
