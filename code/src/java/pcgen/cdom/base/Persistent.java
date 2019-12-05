/*
 * Copyright 2008-14 (C) Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.base;

import pcgen.rules.context.LoadContext;

public interface Persistent<T>
{
    /**
     * Encodes the given choice into a String sufficient to uniquely identify
     * the choice. This may not sufficiently encode to be stored into a file or
     * format which restricts certain characters (such as URLs), it simply
     * encodes into an identifying String. There is no guarantee that this
     * encoding is human readable, simply that the encoding is uniquely
     * identifying such that the decodeChoice method of the Chooser is capable
     * of decoding the String into the choice object.
     *
     * @param item The choice which should be encoded into a String sufficient to
     *             identify the choice.
     * @return A String sufficient to uniquely identify the choice.
     */
    String encodeChoice(T item);

    /**
     * Decodes a given String into a choice of the appropriate type. The String
     * format to be passed into this method is defined solely by the return
     * result of the encodeChoice method. There is no guarantee that the
     * encoding is human readable, simply that the encoding is uniquely
     * identifying such that this method is capable of decoding the String into
     * the choice object.
     *
     * @param context          The LoadContext used to decode the choice
     * @param persistentFormat The String which should be decoded to provide the choice of
     *                         the appropriate type.
     * @return A choice object of the appropriate type that was encoded in the
     * given String.
     */
    T decodeChoice(LoadContext context, String persistentFormat);

}
