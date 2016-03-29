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

import pcgen.base.format.BooleanManager;
import pcgen.base.format.NumberManager;
import pcgen.base.format.OrderedPairManager;
import pcgen.base.format.StringManager;

/**
 * FormatManagerLibraryUtilities are utility methods for FormatManagerLibrary
 * objects.
 */
public class FormatManagerLibraryUtilities
{

	private FormatManagerLibraryUtilities()
	{
		//Don't construct utility class
	}

	/**
	 * Initializes the given SimpleFormatManagerLibrary with the known
	 * FormatManager / FormatManagerFactory objects in the base library.
	 * 
	 * @param library
	 *            The SimpleFormatManagerLibrary to be loaded with the known
	 *            FormatManager / FormatManagerFactory objects
	 */
	public static void loadDefaultFormats(SimpleFormatManagerLibrary library)
	{
		library.addFormatManager(new NumberManager());
		library.addFormatManager(new StringManager());
		library.addFormatManager(new BooleanManager());
		library.addFormatManager(new OrderedPairManager());
		library.addFormatManagerBuilder(new ArrayFormatFactory());
	}
}
