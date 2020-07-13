/*
 * Copyright 2016 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.formatmanager;

import java.util.Optional;

import pcgen.base.util.FormatManager;

/**
 * A FormatManagerFactory converts strings identifying a Format into the
 * appropriate FormatManager.
 * 
 * For compound / complex formats (e.g. ARRAY) it is expected that there is one
 * FormatManagerFactory to handle all of those scenarios (since they can be
 * composed from the "native" FormatMangaer objects.
 */
public interface FormatManagerFactory
{

	/**
	 * Returns the FormatManager for the given format name based on the contents
	 * of the given FormatManagerLibrary.
	 * 
	 * @param parentFormat
	 *            The name of the parent format if one exists
	 * @param subFormat
	 *            The name of the subformat if one exists
	 * @param library
	 *            The FormatManagerLibrary used to store valid FormatManager
	 *            objects
	 * @return The FormatManager for the given format name based on the contents
	 *         of the given FormatManagerLibrary
	 */
	public FormatManager<?> build(Optional<String> parentFormat,
		Optional<String> subFormat, FormatManagerLibrary library);

	/**
	 * Returns the first level format of the FormatManager this
	 * FormatManagerFactory produces.
	 * 
	 * If this FormatManagerFactory produces "native" FormatManager objects
	 * (e.g. for Number.class or String.class) then this value should match the
	 * format name (NUMBER or STRING, respectively). If this
	 * FormatManagerFactory returns a complex / compound object, then this
	 * should return the name of the immediate class created by the
	 * FormatManager.
	 * 
	 * For example, if the FormatManager produces ARRAY[x], then this should
	 * return "ARRAY", not ARRAY[STRING] or any specific value. In theory this
	 * should therefore correlate in a 1:1 fashion (though not as literal
	 * strings) with the response to .getClass().getName() on an item returned
	 * by a FormatManager returned by this FormatManagerFactory.
	 * 
	 * Said another way, the response to this method ignores .getComponentType()
	 * on an array class if such would be returned by a FormatManager returned
	 * by this FormatManagerFactory.
	 * 
	 * @return The first level format of the FormatManager this
	 *         FormatManagerFactory produces
	 */
	public String getBuilderBaseFormat();

}
