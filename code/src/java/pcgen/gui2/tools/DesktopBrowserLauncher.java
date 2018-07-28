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
 */
package pcgen.gui2.tools;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.IOException;
import java.net.URI;

/**
 * Provide an utility method to open files with {@link Desktop}.
 * Non package elements should use {@link Utility#viewInBrowser} which is public.
 * 
 *
 */
final class DesktopBrowserLauncher
{

	private static final Desktop DESKTOP = Desktop.getDesktop();
	private static final Boolean IS_BROWSE_SUPPORTED =
			Desktop.isDesktopSupported() && DESKTOP.isSupported(Action.BROWSE);

	private DesktopBrowserLauncher()
	{
	}

	/**
	 * @see Desktop#isDesktopSupported()
	 * @throws IOException if {@link Desktop} is not supported and throws an exception
	 */
	static void browse(final URI uri) throws IOException
	{
		DESKTOP.browse(uri);
	}

	/**
	 * @return {@code true} if {@link #browse} is supported
	 * @see Desktop#isSupported(Action)
	 */
	static boolean isBrowseSupported()
	{
		return IS_BROWSE_SUPPORTED;
	}
}
