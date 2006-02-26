/*
 * PFileChooser.java
 * Copyright 2003 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on February 8, 2003, 1:39 PM
 *
 * @(#) $Id: PFileChooser.java,v 1.8 2005/10/18 20:23:42 binkley Exp $
 */
package pcgen.gui;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * <code>PFileChooser</code>
 *
 * Overrides JFileChooser methods so changing directory does not
 * set the selected file to null. Instead a new File object is created
 * for the file name in the new directory.
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.8 $
 */
final class PFileChooser extends JFileChooser
{
	static final long serialVersionUID = 7343797508008231821L;

	PFileChooser()
	{
		super();
		setAcceptAllFileFilterUsed(false);
	}

	public void addChoosableFileFilter(final String ext, final String desc)
	{
		super.addChoosableFileFilter(new FileFilter()
			{
				public boolean accept(File f)
				{
					if (f != null)
					{
						if (f.isDirectory())
						{
							return true;
						}

						String extension = getExtension(f);

						if (ext == null)
						{
							return true;
						}
						else if ((extension != null) && ext.equals(extension))
						{
							return true;
						}
					}

					return false;
				}

				public String getDescription()
				{
					return desc;
				}
			});
	}

	private String getExtension(File f)
	{
		if (f != null)
		{
			String filename = f.getName();
			int i = filename.lastIndexOf('.');

			if ((i > 0) && (i < (filename.length() - 1)))
			{
				return filename.substring(i + 1).toLowerCase();
			}
		}

		return null;
	}
}
