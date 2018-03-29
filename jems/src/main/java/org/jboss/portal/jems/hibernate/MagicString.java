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
package org.jboss.portal.jems.hibernate;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * An hibernate custom type that handles the fact that oracle stores an empty string as null. That types changes the
 * semantic of string with the fact that a returned null string is equals to an empty string.
 * <p/>
 * I did not found a good name for that class so I choosed MagicString but it has nothing magic.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class MagicString implements UserType, Serializable
{

   private static final int[] TYPES = {Types.VARCHAR};

   public int[] sqlTypes()
   {
      return TYPES;
   }

   public Class returnedClass()
   {
      return String.class;
   }

   public boolean equals(Object x, Object y)
   {
      if (x == y)
      {
         return true;
      }
      if (x == null)
      {
         return ((String)y).length() == 0;
      }
      else if (y == null)
      {
         return ((String)x).length() == 0;
      }
      return x.equals(y);
   }

   public Object deepCopy(Object value)
   {
      return value;
   }

   public boolean isMutable()
   {
      return false;
   }

   public Object nullSafeGet(ResultSet resultSet, String[] names, Object owner) throws HibernateException, SQLException
   {
      String value = (String)Hibernate.STRING.nullSafeGet(resultSet, names[0]);
      if (value != null)
      {
         return value;
      }
      else
      {
         return "";
      }
   }

   public void nullSafeSet(PreparedStatement statement, Object value, int index) throws HibernateException, SQLException
   {
      Hibernate.STRING.nullSafeSet(statement, value, index);
   }

   public Object assemble(Serializable cached, Object owner) throws HibernateException
   {
      return cached;
   }

   public Serializable disassemble(Object value)
   {
      return (Serializable)value;
   }

   public int hashCode(Object object) throws HibernateException
   {
      return object.hashCode();
   }

   public Object replace(Object original, Object target, Object owner) throws HibernateException
   {
      return original;
   }
}
