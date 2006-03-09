/*
   GNU Lesser General Public License
   MutableFilter
   Copyright (C) 2000-2003 Howard Kistler
   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   License as published by the Free Software Foundation; either
   version 2.1 of the License, or (at your option) any later version.
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.
   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package gmgen.gui;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/** Class for providing JFileChooser with a FileFilter
 */
public class MutableFilter extends FileFilter
{
	private String descriptor;
	private String[] acceptableExtensions;

	public String getDescription()
	{
		return descriptor;
	}

	public boolean accept(File file)
	{
		if (file.isDirectory())
		{
			return true;
		}

		String fileName = file.getName();
		String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();

		if (fileExt != null)
		{
			for (int i = 0; i < acceptableExtensions.length; i++)
			{
				if (fileExt.equals(acceptableExtensions[i]))
				{
					return true;
				}
			}

			return false;
		}
		return false;
	}
}
