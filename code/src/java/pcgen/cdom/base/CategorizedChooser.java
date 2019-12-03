/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
 * Copyright James Dempsey, 2012
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

public interface CategorizedChooser<T> extends Chooser<T>
{
	/**
	 * Decodes a given String into a choice of the appropriate type. The String
	 * format to be passed into this method is defined solely by the return
	 * result of the encodeChoice method. There is no guarantee that the
	 * encoding is human readable, simply that the encoding is uniquely
	 * identifying such that this method is capable of decoding the String into
	 * the choice object.
	 * 
	 * @param context
	 *            The LoadContext used to decode the persistentFormat
	 * @param persistentFormat
	 *            The String which should be decoded to provide the choice of
	 *            the appropriate type.
	 * @param category
	 *            The fixed category of the choice.
	 * 
	 * @return A choice object of the appropriate type that was encoded in the
	 *         given String.
	 */
    T decodeChoice(LoadContext context, String persistentFormat, Category<?> category);

}
