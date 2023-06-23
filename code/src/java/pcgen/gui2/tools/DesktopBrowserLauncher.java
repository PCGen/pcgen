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
import java.net.URI;

import pcgen.gui3.GuiUtility;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

/**
 * Provide a utility method to open files with {@link Desktop}.
 */
public final class DesktopBrowserLauncher
{

	private static final Desktop DESKTOP = Desktop.getDesktop();

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
	 * View a URI in a browser
	 * p.s. JDK 20 will deprecate all public constructors of java.net.URL
	 *
	 * @param uri URI to display in browser.
	 * @throws IOException if the URL is bad or the browser can not be launched
	 */
	@SuppressWarnings({"ThrowInsideCatchBlockWhichIgnoresCaughtException", "PMD.PreserveStackTrace"})
	public static void viewInBrowser(URI uri) throws IOException
	{
		if (Desktop.isDesktopSupported() && DESKTOP.isSupported(Action.BROWSE))
		{
			DESKTOP.browse(uri);
		}
		else
		{
			Dialog<ButtonType> alert = GuiUtility.runOnJavaFXThreadNow(() ->  new Alert(Alert.AlertType.WARNING));
			Logging.debugPrint("Unable to browse to " + uri);
			alert.setTitle(LanguageBundle.getString("in_err_browser_err"));
			alert.setContentText(LanguageBundle.getFormattedString("in_err_browser_uri", uri));
			GuiUtility.runOnJavaFXThreadNow(alert::showAndWait);
		}
	}
}
