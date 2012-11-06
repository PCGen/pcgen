/*
 *  Initiative - A role playing utility to track turns
 *  Copyright (C) 2002 Devon D Jones
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 *  The author of this program grants you the ability to use this code
 *  in conjunction with code that is covered under the Open Gaming License
 *
 *  InitFileFilter.java
 *
 *  Created on August 29, 2002, 6:21 PM
 */
package plugin.initiative;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * File filter for .init files
 *@author     devon
 *@since    April 7, 2003
 */
public class InitFileFilter extends FileFilter
{
	/**  Creates a new instance of InitFileFilter */
	public InitFileFilter()
	{
		// Empty Constructor
	}

	/**
	 *  Gets the description attribute of the InitFileFilter object
	 *
	 *@return    The description value
	 */
    @Override
	public String getDescription()
	{
		return "Initiative File (*.init)";
	}

	/**
	 *  Checks to see if a file passes the filter
	 *
	 *@param  file  A file object to test
	 *@return       true or false if it passes the filter
	 */
    @Override
	public boolean accept(File file)
	{
		if (!file.isDirectory())
		{
			String name = file.getName();

			return name.endsWith(".init");
		}

		return true;
	}
}
