/*
 * PcgFileFilter.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on May 10, 2001 09:01
 */
package pcgen.gui2;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import org.apache.commons.lang3.StringUtils;

/**
 *  This class filters out non-pcg files.
 *
 * @author     Jonas Karlsson &lt;jujutsunerd@users.sourceforge.net&gt;
 */
final class PcgFileFilter extends FileFilter
{

	/**
	 *  Returns a description of this class
	 *
	 * @return    The Description
	 */
	@Override
	public String getDescription()
	{
		return "Pcg files only";
	}

	/**
	 *  Accept all directories and all pcg files
	 *
	 * @param  f  The file to be checked
	 * @return    Whether the file is accepted
	 */
	@Override
	public boolean accept(File f)
	{
		if (f.isDirectory())
		{
			return true;
		}
		return StringUtils.endsWithIgnoreCase(f.getName(), ".pcg");
	}

}
