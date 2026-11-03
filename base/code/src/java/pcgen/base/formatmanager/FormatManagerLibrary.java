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
 * A FormatManagerLibrary can return a FormatManager given a Format name.
 */
public interface FormatManagerLibrary
{
	/**
	 * Returns a FormatManager for the format identified by the given Format
	 * name.
	 * 
	 * Note: Reserves the right to throw an exception if hasFormatManager returns false.
	 * 
	 * @param parent
	 * 	          The parent Format name requesting the FormatManager, if any
	 * @param formatName
	 *            The Format name for which the FormatManager should be returned
	 * @return A FormatManager for the format identified by the given Format
	 *         name
	 */
	public FormatManager<?> getFormatManager(Optional<String> parent, String formatName);

	/**
	 * Returns whether a FormatManager for the format identified by the given Format name
	 * exists or can be built in this FormatManagerLibrary.
	 * 
	 * @param formatName
	 *            The Format name for which the FormatManager should be checked if it
	 *            exists or can be built
	 * @return true if a FormatManager for the format identified by the given Format name
	 *         exists or can be built in this FormatManagerLibrary; false otherwise
	 */
	public boolean hasFormatManager(String formatName);
}
