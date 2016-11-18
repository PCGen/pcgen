/*
 * FakeWindowsLookAndFeel.java
 * Copyright 2002,2003 (C) B. K. Oxley (binkley)
 * <binkley@alumni.rice.edu>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * Created on February 13th, 2002.
 */
package pcgen.gui2.plaf;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import java.util.Enumeration;
import java.util.ResourceBundle;

/**
 * Support Windows95 L&amp;F on non-Windows platforms.  This is
 * ridiculously simple.
 *
 * @author &lt;a href="mailto:binkley@alumni.rice.edu"&gt;B. K. Oxley (binkley)&lt;/a&gt;
 */
public class FakeWindowsLookAndFeel extends WindowsLookAndFeel
{
	/**
	 * Support Windows95 L&amp;F on non-Windows platforms.  Simple
	 * return {@code true}.
	 *
	 * @return boolean {@code true} always
	 */
	@Override
	public boolean isSupportedLookAndFeel()
	{
		return true;
	}

	@Override
	public void initialize()
	{
		// This hack convinces JDK 1.4 to use the Win2K UI
		// instead of the Win95 one.  Should be configurable!
		// XXX --bko
		String osVersion = System.getProperty("os.version");
		System.setProperty("os.version", "5.0");
		super.initialize();
		System.setProperty("os.version", osVersion);
	}

	@Override
	protected void initComponentDefaults(UIDefaults table)
	{
		super.initComponentDefaults(table);
		loadResourceBundle(table);

		Class<?> wlafClass = WindowsLookAndFeel.class;
		Object[] defaults =
		{

			// These are all the icons defined in the
			// WindowsLookAndFeel.  We redefine them here
			// because of the way they are defined in that
			// class: in terms of the return value of
			// getClass().  I.e., getClass() just returns
			// the handle to the invoking class, which now
			// is FakeWindowsLookAndFeel.  That means that
			// the icons are searched for in the
			// FakeWindows look and feel package, which is
			// not where they really are.  Since we've
			// just called the superclass method, the
			// icons have been installed incorrectly in
			// the table.  Reinstall them using the
			// correct class.
			"Tree.openIcon", makeIcon(wlafClass, "icons/TreeOpen.gif"), "Tree.closedIcon",
			makeIcon(wlafClass, "icons/TreeClosed.gif"), "Tree.leafIcon",
			LookAndFeel.makeIcon(wlafClass, "icons/TreeLeaf.gif"),

			"FileChooser.newFolderIcon", LookAndFeel.makeIcon(wlafClass, "icons/NewFolder.gif"),
			"FileChooser.upFolderIcon", LookAndFeel.makeIcon(wlafClass, "icons/UpFolder.gif"),
			"FileChooser.homeFolderIcon", LookAndFeel.makeIcon(wlafClass, "icons/HomeFolder.gif"),
			"FileChooser.detailsViewIcon", LookAndFeel.makeIcon(wlafClass, "icons/DetailsView.gif"),
			"FileChooser.listViewIcon", LookAndFeel.makeIcon(wlafClass, "icons/ListView.gif"),

			"FileView.directoryIcon", LookAndFeel.makeIcon(wlafClass, "icons/Directory.gif"), "FileView.fileIcon",
			LookAndFeel.makeIcon(wlafClass, "icons/File.gif"), "FileView.computerIcon",
			LookAndFeel.makeIcon(wlafClass, "icons/Computer.gif"), "FileView.hardDriveIcon",
			LookAndFeel.makeIcon(wlafClass, "icons/HardDrive.gif"), "FileView.floppyDriveIcon",
			LookAndFeel.makeIcon(wlafClass, "icons/FloppyDrive.gif"),

			"OptionPane.errorIcon", makeIcon(wlafClass, "icons/Error.gif"), "OptionPane.informationIcon",
			makeIcon(wlafClass, "icons/Inform.gif"), "OptionPane.warningIcon", makeIcon(wlafClass, "icons/Warn.gif"),
			"OptionPane.questionIcon", makeIcon(wlafClass, "icons/Question.gif"),
		};

		table.putDefaults(defaults);
	}

	private void loadResourceBundle(UIDefaults table)
	{
		ResourceBundle bundle = ResourceBundle.getBundle("com.sun.java.swing.plaf.windows.resources.windows");
		Enumeration<String> e = bundle.getKeys();

		while (e.hasMoreElements())
		{
			String key = e.nextElement();
			table.put(key, bundle.getObject(key));
		}
	}
}
