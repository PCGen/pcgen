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
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JFileChooser;

import pcgen.system.PCGenSettings;

import org.apache.commons.lang3.SystemUtils;

/**
 * Provide an utility method to open files with {@link Desktop}.
 */
public final class  DesktopBrowserLauncher
{

	private static final Desktop DESKTOP = Desktop.getDesktop();
	private static final Boolean IS_BROWSE_SUPPORTED =
			Desktop.isDesktopSupported() && DESKTOP.isSupported(Action.BROWSE);

	private DesktopBrowserLauncher()
	{
	}

	/**
	 * View a file (should be browsable) in a browser.
	 *
	 * @param file Path of the file to display in browser.
	 * @throws IOException if file doesn't exist
	 */
	public static void viewInBrowser(File file) throws IOException
	{
		viewInBrowser(file.toURI());
	}

	/**
	 * View a URL in a browser
	 *
	 * @param url URL to display in browser.
	 * @throws IOException if the URL is bad or the browser can not be launched
	 */
	public static void viewInBrowser(URL url) throws IOException
	{
		try
		{
			viewInBrowser(url.toURI());
		}
		catch (final URISyntaxException e)
		{
			throw new MalformedURLException(e.getMessage());
		}
	}

	/**
	 * View a URI in a browser.
	 *
	 * @param uri URI to display in browser.
	 * @throws IOException if browser can not be launched
	 */
	private static void viewInBrowser(URI uri) throws IOException
	{
		// Windows tends to lock up or not actually
		// display anything unless we've specified a
		// default browser, so at least make the user
		// aware that (s)he needs one. If they don't
		// pick one and it doesn't work, at least they
		// might know enough to try selecting one the
		// next time.
		if (!IS_BROWSE_SUPPORTED && SystemUtils.IS_OS_WINDOWS
				&& (PCGenSettings.getBrowserPath() == null))
		{
			selectDefaultBrowser();
		}

		DESKTOP.browse(uri);
	}

	/**
	 * Sets the default browser.
	 */
	public static void selectDefaultBrowser()
	{
		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Find and select your preferred html browser.");

		if (SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_MAC_OSX)
		{
			fc.putClientProperty("JFileChooser.appBundleIsTraversable", "never");
		}

		if (PCGenSettings.getBrowserPath() != null)
		{
			fc.setCurrentDirectory(new File(PCGenSettings.getBrowserPath()));
		}

		final int returnVal = fc.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			final File file = fc.getSelectedFile();
			PCGenSettings.OPTIONS_CONTEXT.setProperty(PCGenSettings.BROWSER_PATH, file.getAbsolutePath());
		}
	}
}
