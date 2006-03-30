/*
   GNU Lesser General Public License
   ImageFileChooser
   Copyright (C) 2000-2002  Frits Jalvingh & Howard Kistler
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

import javax.swing.JFileChooser;
import java.io.File;

/** Class for providing a chooser that lets the user select an image to insert
 */
public class ImageFileChooser extends JFileChooser
{
	/**
	 * Constructor that takes a default directory to start in, specified as a File
	 * @param fileCurrentDirectory with the default path
	 */
	public ImageFileChooser(File fileCurrentDirectory)
	{
		this.setCurrentDirectory(fileCurrentDirectory);
		this.setAccessory(new ImageFileChooserPreview(this));
	}

	/** Constructor that takes a default directory to start in, specified as a String
	 * @param strCurrentPath current directory path.
	 */
	public ImageFileChooser(String strCurrentPath)
	{
		this(new File(strCurrentPath));
	}
}