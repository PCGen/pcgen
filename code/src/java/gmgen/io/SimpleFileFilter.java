/*
 *  GMGen - A role playing utility
 *  Copyright (C) 2003 Devon D Jones
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package gmgen.io;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * This class is used to set the file filters in add/save dialogs.
 * @author Expires 2003
 * @version 2.10
 */
public class SimpleFileFilter extends FileFilter
{
	/** The description to go along with the extensions. */
	private String description;

	/** The extensions of the wanted files. */
	private String[] extensions;

	/**
	 * Creates an instance of this class taking in an extension.
	 * @param ext a file extension.
	 */
	public SimpleFileFilter(String ext)
	{
		this(new String[]{ ext }, null);
	}

	/**
	 * Creates an instance of this class given some extensions
	 * and a description.
	 * @param exts the used extensions.
	 * @param desc the description of the filter.
	 */
	public SimpleFileFilter(String[] exts, String desc)
	{
		//clone and lowercase the extensions
		extensions = new String[exts.length];

		for (int i = exts.length - 1; i >= 0; i--)
		{
			extensions[i] = exts[i].toLowerCase();
		}

		String workingDesc = ((desc == null) ? "Unknown File" : desc);

		StringBuilder strbDesc = new StringBuilder(workingDesc + " (");

		for (int i = 0; i < extensions.length; i++)
		{
			if (i > 0)
			{
				strbDesc.append(", ");
			}

			strbDesc.append("*." + extensions[i]);
		}

		strbDesc.append(")");
		description = strbDesc.toString();
	}

	/**
	 * Gets the description of the filter.
	 * @return the description.
	 */
    @Override
	public String getDescription()
	{
		return description;
	}

	/**
	 * Checks the file for validity.
	 * @param f the file that is chosen.
	 * @return true / false on user acceptance.
	 */
    @Override
	public boolean accept(final File f)
	{
		//we always allow directories, regardless of their extension
		if (f.isDirectory())
		{
			return true;
		}

		//ok if its a regular file, so check the extension
		String name = f.getName().toLowerCase();

		for (int i = extensions.length - 1; i >= 0; i--)
		{
			if (name.endsWith(extensions[i]))
			{
				return true;
			}
		}

		return false;
	}
}
