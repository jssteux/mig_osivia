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

import org.jboss.portal.common.io.IOTools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 8784 $
 */
public final class ByteArrayBlob implements Blob
{

   /** . */
   byte[] _bytes;

   public ByteArrayBlob(InputStream in) throws IOException
   {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      IOTools.copy(in, out);
      _bytes = out.toByteArray();
   }

   public ByteArrayBlob(ByteArrayBlob that)
   {
      _bytes = that.getBytes(0, that._bytes.length);
   }

   public ByteArrayBlob(byte[] _bytes)
   {
      this._bytes = _bytes;
   }

   public ByteArrayBlob(Blob blob) throws SQLException
   {
      _bytes = blob.getBytes(1, (int)blob.length());
   }

   public long length()
   {
      return _bytes.length;
   }

   public byte[] getBytes(long pos, int length)
   {
      byte[] bytes = new byte[length];
      // Why are we passing in 'pos'? It results in AOOBE.
      //System.arraycopy(_bytes, (int)pos, bytes, 0, length);
      System.arraycopy(_bytes, 0, bytes, 0, length);
      return bytes;
   }

   public InputStream getBinaryStream()
   {
      return new ByteArrayInputStream(_bytes);
   }

   /*
    * For JDK 1.6
    * @see java.sql.Blob#getBinaryStream()
    */
   public InputStream getBinaryStream(long pos, long length)
   {
      throw new UnsupportedOperationException();
   }

   /*
   * For JDK 1.6
   * @see java.sql.Blob#free()
   */
   public void free()
   {
      throw new UnsupportedOperationException();
   }

   public int setBytes(long pos, byte[] bytes)
   {
      throw new UnsupportedOperationException();
   }

   public int setBytes(long pos, byte[] bytes, int offset, int len)
   {
      throw new UnsupportedOperationException();
   }

   public OutputStream setBinaryStream(long pos)
   {
      throw new UnsupportedOperationException();
   }

   public long position(byte pattern[], long start)
   {
      throw new UnsupportedOperationException();
   }

   public long position(Blob pattern, long start)
   {
      throw new UnsupportedOperationException();
   }

   public void truncate(long len)
   {
      throw new UnsupportedOperationException();
   }

   public static ByteArrayBlob get(Blob blob) throws IOException, SQLException
   {
      if (blob instanceof ByteArrayBlob)
      {
         return (ByteArrayBlob)blob;
      }
      else
      {
         InputStream binaryStream = blob.getBinaryStream();
         return new ByteArrayBlob(binaryStream);
      }
   }

   public static ByteArrayBlob create(Blob blob) throws IOException, SQLException
   {
      if (blob instanceof ByteArrayBlob)
      {
         return new ByteArrayBlob((ByteArrayBlob)blob);
      }
      else
      {
         InputStream binaryStream = blob.getBinaryStream();
         return new ByteArrayBlob(binaryStream);
      }
   }


}
