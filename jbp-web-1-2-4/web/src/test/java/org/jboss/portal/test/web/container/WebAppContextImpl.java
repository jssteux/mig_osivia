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
package org.jboss.portal.test.web.container;

import org.jboss.portal.web.spi.WebAppContext;

import javax.servlet.ServletContext;
import java.io.InputStream;
import java.io.IOException;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public class WebAppContextImpl implements WebAppContext
{

   /** . */
   private final String contextPath;

   /** . */
   boolean started;

   public WebAppContextImpl(String contextPath)
   {
      this.contextPath = contextPath;
      this.started = false;
   }

   public void start() throws Exception
   {
      this.started = true;
   }

   public void stop()
   {
      this.started = false;
   }

   public ServletContext getServletContext()
   {
      throw new UnsupportedOperationException();
   }

   public ClassLoader getClassLoader()
   {
      throw new UnsupportedOperationException();
   }

   public String getContextPath()
   {
      return contextPath;
   }

   public boolean importFile(String parentDirRelativePath, String name, InputStream source, boolean overwrite) throws IOException
   {
      throw new UnsupportedOperationException();
   }
}
