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
package org.jboss.portal.test.server;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class Utils
{

   public static String RANGE_0_255 = computeFrom0To255();

   public static String RANGE_256_512 = computeFrom256To512();

   private static String computeFrom0To255()
   {
      return compute(0, 256);
   }

   private static String computeFrom256To512()
   {
      return compute(256, 512);
   }

   public static String compute(int from, int to)
   {
      if (from < 0)
      {
         throw new IllegalArgumentException();
      }
      if (from > to)
      {
         throw new IllegalArgumentException();
      }
      StringBuffer tmp = new StringBuffer();
      for (int i = from; i < to; i++)
      {
         char c = (char)i;
         tmp.append(c);
      }
      return tmp.toString();
   }

   public static String compareString(String s1, String s2)
   {
      if (s1 == null)
      {
         return "s1 is null";
      }
      if (s2 == null)
      {
         return "s2 is null";
      }
      if (s1.length() != s2.length())
      {
         return "lengths don't match " + s1.length() + "!=" + s2.length();
      }
      for (int i = s1.length() - 1; i >= 0; i--)
      {
         char c1 = s1.charAt(i);
         char c2 = s2.charAt(i);
         if (c1 != c2)
         {
            return "char at position " + i + " are diffrent " + (int)c1 + "!=" + (int)c2;
         }
      }
      return null;
   }

}
