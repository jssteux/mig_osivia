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

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class TestPhase
{

   /** . */
   public static final int ACTION_LIFE_CYCLE = 0;

   /** . */
   public static final int RENDER_LIFE_CYCLE = 1;

   /** . */
   public static final int AJAX_LIFE_CYCLE = 2;

   /** . */
   private int count;

   /** . */
   private int lifeCycle;

   private TestPhase(int count, int lifeCycle)
   {
      this.count = count;
      this.lifeCycle = lifeCycle;
   }

   public static int getActionLifeCycle()
   {
      return ACTION_LIFE_CYCLE;
   }

   public static int getRenderLifeCycle()
   {
      return RENDER_LIFE_CYCLE;
   }

   public int getCount()
   {
      return count;
   }

   public int getLifeCycle()
   {
      return lifeCycle;
   }

   public boolean equals(Object obj)
   {
      if (obj == this)
      {
         return true;
      }
      if (obj instanceof TestPhase)
      {
         TestPhase that = (TestPhase)obj;
         return count == that.count && lifeCycle == that.lifeCycle;
      }
      return false;
   }

   public static TestPhase createAction(int count)
   {
      return new TestPhase(count, ACTION_LIFE_CYCLE);
   }

   public static TestPhase createRender(int count)
   {
      return new TestPhase(count, RENDER_LIFE_CYCLE);
   }

   public static TestPhase createAjax(int count)
   {
      return new TestPhase(count, AJAX_LIFE_CYCLE);
   }
}
