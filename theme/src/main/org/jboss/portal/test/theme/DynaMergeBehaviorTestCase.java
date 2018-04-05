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

import junit.framework.TestCase;
import org.jboss.portal.theme.impl.render.dynamic.DynaMergeBehavior;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class DynaMergeBehaviorTestCase extends TestCase
{

   public void testForRegion()
   {
      assertEquals(true, DynaMergeBehavior.mergeForRegion(Boolean.TRUE, Boolean.TRUE));
      assertEquals(false, DynaMergeBehavior.mergeForRegion(Boolean.TRUE, Boolean.FALSE));
      assertEquals(true, DynaMergeBehavior.mergeForRegion(Boolean.TRUE, null));
      assertEquals(false, DynaMergeBehavior.mergeForRegion(Boolean.FALSE, Boolean.TRUE));
      assertEquals(false, DynaMergeBehavior.mergeForRegion(Boolean.FALSE, Boolean.FALSE));
      assertEquals(false, DynaMergeBehavior.mergeForRegion(Boolean.FALSE, null));
      assertEquals(false, DynaMergeBehavior.mergeForRegion(null, Boolean.TRUE));
      assertEquals(false, DynaMergeBehavior.mergeForRegion(null, Boolean.FALSE));
      assertEquals(false, DynaMergeBehavior.mergeForRegion((Boolean)null, null));
   }

   public void testForWindow()
   {
      assertEquals(true, DynaMergeBehavior.mergeForWindow(Boolean.TRUE, Boolean.TRUE));
      assertEquals(false, DynaMergeBehavior.mergeForWindow(Boolean.TRUE, Boolean.FALSE));
      assertEquals(true, DynaMergeBehavior.mergeForWindow(Boolean.TRUE, null));
      assertEquals(false, DynaMergeBehavior.mergeForWindow(Boolean.FALSE, Boolean.TRUE));
      assertEquals(false, DynaMergeBehavior.mergeForWindow(Boolean.FALSE, Boolean.FALSE));
      assertEquals(false, DynaMergeBehavior.mergeForWindow(Boolean.FALSE, null));
      assertEquals(false, DynaMergeBehavior.mergeForWindow(null, Boolean.TRUE));
      assertEquals(false, DynaMergeBehavior.mergeForWindow(null, Boolean.FALSE));
      assertEquals(false, DynaMergeBehavior.mergeForWindow((Boolean)null, null));
   }
}
