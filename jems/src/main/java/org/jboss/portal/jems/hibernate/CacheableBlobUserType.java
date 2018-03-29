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

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public class CacheableBlobUserType implements UserType
{

   /** . */
   private static final int[] SQL_TYPES = {Types.BLOB};

   public int[] sqlTypes()
   {
      return SQL_TYPES;
   }

   public Class returnedClass()
   {
      return Blob.class;
   }

   public boolean equals(Object x, Object y) throws HibernateException
   {
      if (x == y)
      {
         return true;
      }
      if (x != null && y != null)
      {
         try
         {
            ByteArrayBlob _x = ByteArrayBlob.get((Blob)x);
            ByteArrayBlob _y = ByteArrayBlob.get((Blob)y);
            return java.util.Arrays.equals(_x._bytes, _y._bytes);
         }
         catch (Exception e)
         {
            throw new HibernateException(e);
         }
      }
      return false;
   }

   public int hashCode(Object object) throws HibernateException
   {
      return object.hashCode();
   }

   public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException
   {
      Blob blob = rs.getBlob(names[0]);
      return rs.wasNull() ? null : new ByteArrayBlob(blob);
   }

   public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException
   {
      if (value == null)
      {
         st.setNull(index, Types.BLOB);
      }
      else
      {
         st.setBlob(index, (Blob)value);
      }
//      if (value==null) {
//         st.setNull(index, Types.BLOB);
//      }
//      else {
//
//         if (value instanceof SerializableBlob) {
//            value = ( (SerializableBlob) value ).getWrappedBlob();
//         }
//
//         final boolean useInputStream = session.getFactory().getDialect().useInputStreamToInsertBlob() &&
//            (value instanceof BlobImpl);
//
//         if ( useInputStream ) {
//            BlobImpl blob = (BlobImpl) value;
//            st.setBinaryStream( index, blob.getBinaryStream(), (int) blob.length() );
//         }
//         else {
//            st.setBlob(index, (Blob) value);
//         }
//
//      }
   }

   public Object deepCopy(Object value) throws HibernateException
   {
      if (value == null)
      {
         return null;
      }
      try
      {
         return ByteArrayBlob.create((Blob)value);
      }
      catch (Exception e)
      {
         throw new HibernateException(e);
      }
   }

   public boolean isMutable()
   {
      return true;
   }

   public Serializable disassemble(Object value) throws HibernateException
   {
      if (value == null)
      {
         return null;
      }
      return ((ByteArrayBlob)value)._bytes;
   }

   public Object assemble(Serializable cached, Object owner) throws HibernateException
   {
      if (cached == null)
      {
         return null;
      }
      return new ByteArrayBlob((byte[])cached);
   }

   public Object replace(Object original, Object target, Object owner) throws HibernateException
   {
      return original;
   }

}
