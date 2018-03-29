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

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.Oracle9Dialect;
import org.hibernate.dialect.OracleDialect;
import org.hibernate.usertype.UserType;

/**
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class OracleStringUserType implements UserType
{

   private static final int[] SQL_TYPES = {Types.VARCHAR};
   
   private static final String EMPTY_STRING = "__EMPTY_STRING__";

   public Object assemble(Serializable arg0, Object arg1) throws HibernateException
   {
      return arg0;
   }

   public Object deepCopy(Object value) throws HibernateException
   {
      return value;
   }

   public Serializable disassemble(Object value) throws HibernateException
   {
      return (Serializable)value;
   }

   public boolean equals(Object arg0, Object arg1) throws HibernateException
   {
      if (arg0 == arg1)
      {
         return true;
      }
      if ((arg0 == null) || (arg1 == null))
      {
         return false;
      }
      return arg0.equals(arg1);
   }

   public int hashCode(Object arg0) throws HibernateException
   {
      return arg0.hashCode();
   }

   public boolean isMutable()
   {
      return false;
   }

   public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException
   {
      String dbValue = (String) Hibernate.STRING.nullSafeGet(rs, names[0]);

      if (dbValue == null)
      {
         return null;
      }
      else if (dbValue.equals(EMPTY_STRING))
      {
         return "";
      }
      else
      {
         return dbValue;
      }
   }

   public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException
   {
      if (value.equals(""))
      {
         Hibernate.STRING.nullSafeSet(st, EMPTY_STRING, index);
      }
      else
      {
         Hibernate.STRING.nullSafeSet(st, value, index);
      }
   }

   public Object replace(Object arg0, Object arg1, Object arg2) throws HibernateException
   {
      return arg0;
   }

   public Class returnedClass()
   {
      return String.class;
   }

   public int[] sqlTypes()
   {
      return SQL_TYPES;
   }

}

