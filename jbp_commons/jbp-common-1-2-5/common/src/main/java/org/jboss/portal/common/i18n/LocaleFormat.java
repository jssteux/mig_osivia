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
package org.jboss.portal.common.i18n;

import org.jboss.portal.common.util.ConversionException;
import org.jboss.portal.common.text.CharWriter;

import java.io.IOException;
import java.util.Locale;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 7228 $
 */
public interface LocaleFormat
{

   /** . */
   final LocaleFormat RFC3066_LANGUAGE_TAG_NO_CACHE = new RFC3066LanguageTagLocaleFormat();

   /** . */
   final LocaleFormat DEFAULT_NO_CACHE = new DefaultLocaleFormat();

   /** . */
   final LocaleFormat RFC3066_LANGUAGE_TAG = new CachingLocaleFormat(new RFC3066LanguageTagLocaleFormat());

   /** . */
   final LocaleFormat DEFAULT = new CachingLocaleFormat(new DefaultLocaleFormat());

   Locale getLocale(String value) throws ConversionException;

   String toString(Locale locale) throws ConversionException;

   void write(Locale locale, CharWriter writer) throws IOException, ConversionException;

}
