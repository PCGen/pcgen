/*
 * Copyright 2012 Vincent Lhote
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
 * Created on 5 sept. 2012
 *
 * Current Ver: $Revision$ <br>
 * Last Editor: $Author$ <br>
 * Last Edited: $Date$
 */
package pcgen.gui2.tools;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * Provide an utility method to open files with {@link Desktop}.
 * Fall back on {@link BrowserLauncher} if {@link Desktop#isDesktopSupported()} is {@code false}.
 * Non package elements should use {@link Utility#viewInBrowser} which is public.
 * 
 * @author Vincent Lhote
 *
 */
class DesktopBrowserLauncher
{

	private static Boolean isDesktopSupported = null;
	private static Boolean isBrowseSupported = null;
	private static Desktop desktop;

	/**
	 * Opens an external program to browse an URI.
	 * 
	 * @param uri the URI to browse
	 * @throws IOException if {@link Desktop} is not supported and {@link BrowserLauncher#openURL} throws an exception
	 */
	static final void browse(URI uri) throws IOException
	{
		if (isDesktopSupported())
		{
			getDesktop().browse(uri);
		}
		else
		{
			// legacy
			BrowserLauncher.openURL(uri.toURL());
		}

	}

	/**
	 * @return desktop instance
	 */
	private static Desktop getDesktop()
	{
		if (desktop == null)
		{
			desktop = Desktop.getDesktop();
		}
		return desktop;
	}

	/**
	 * Opens an external program to browse a file.
	 * 
	 * @param file the file to browse
	 * @throws IOException if {@link Desktop} is not supported and {@link BrowserLauncher#openURL} throws an exception
	 */
	static final void browse(File file) throws IOException
	{
		browse(file.toURI());
	}

	/**
	 * @return {@code true} if Desktop is supported
	 * @see Desktop#isDesktopSupported()
	 */
	private static boolean isDesktopSupported()
	{
		if (isDesktopSupported == null)
		{
			isDesktopSupported = Desktop.isDesktopSupported();
		}
		return isDesktopSupported;
	}

	/**
	 * @return {@code true} if {@link #browse} is supported
	 * @see Desktop#isSupported(Action)
	 */
	static boolean isBrowseSupported()
	{
		if (isBrowseSupported == null)
		{
			isBrowseSupported =
					isDesktopSupported()
						&& getDesktop().isSupported(Action.BROWSE);
		}
		return isBrowseSupported;
	}
}
