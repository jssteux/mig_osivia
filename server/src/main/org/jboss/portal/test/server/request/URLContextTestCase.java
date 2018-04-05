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
package org.jboss.portal.test.server.request;

import junit.framework.TestCase;
import org.jboss.portal.server.request.URLContext;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class URLContextTestCase extends TestCase
{

   URLContext a = URLContext.newInstance(false, false);
   URLContext b = URLContext.newInstance(true, false);
   URLContext c = URLContext.newInstance(false, true);
   URLContext d = URLContext.newInstance(true, true);

   public void testFactory()
   {
      assertFalse(a.isSecure());
      assertFalse(a.isAuthenticated());
      assertEquals(0, a.getMask());

      //
      assertTrue(b.isSecure());
      assertFalse(b.isAuthenticated());
      assertEquals(URLContext.SEC_MASK, b.getMask());

      //
      assertFalse(c.isSecure());
      assertTrue(c.isAuthenticated());
      assertEquals(URLContext.AUTH_MASK, c.getMask());

      //
      assertTrue(d.isSecure());
      assertTrue(d.isAuthenticated());
      assertEquals(URLContext.AUTH_MASK | URLContext.SEC_MASK, d.getMask());
   }

   public void testTransformation()
   {
      assertEquals(a, a.asNonSecured());
      assertEquals(a, a.asNonAuthenticated());
      assertEquals(b, a.asSecured());
      assertEquals(c, a.asAuthenticated());

      //
      assertEquals(a, b.asNonSecured());
      assertEquals(b, b.asNonAuthenticated());
      assertEquals(b, b.asSecured());
      assertEquals(d, b.asAuthenticated());

      //
      assertEquals(c, c.asNonSecured());
      assertEquals(a, c.asNonAuthenticated());
      assertEquals(d, c.asSecured());
      assertEquals(c, c.asAuthenticated());

      //
      assertEquals(c, d.asNonSecured());
      assertEquals(b, d.asNonAuthenticated());
      assertEquals(d, d.asSecured());
      assertEquals(d, d.asAuthenticated());
   }

}
