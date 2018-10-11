/*
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
package pcgen.gui2.plaf;

import java.awt.Desktop;
import java.awt.desktop.AboutEvent;
import java.awt.desktop.AboutHandler;
import java.awt.desktop.PreferencesEvent;
import java.awt.desktop.PreferencesHandler;
import java.awt.desktop.QuitEvent;
import java.awt.desktop.QuitHandler;
import java.awt.desktop.QuitResponse;

import pcgen.gui2.PCGenUIManager;

/**
 * {@code MacGUI} initializes Mac-specific GUI elements.
 */
public final class MacGUIHandler
{
	private static MacGUIHandler theAdapter;

	private MacGUIHandler()
	{
	}

	/**
	 * Initialize the Mac-specific properties.
	 * Create an ApplicationAdapter to listen for Help, Prefs, and Quit.
	 */
	public static void initialize()
	{
		if (theAdapter != null)
		{
			// we have already initialized.
			return;
		}

		// set up the Application menu
		theAdapter = new MacGUIHandler();
		MacGUIHandler.initialize();
		Desktop theDesktop = Desktop.getDesktop();
		theDesktop.setAboutHandler(new OSXAboutHandler());
		theDesktop.setPreferencesHandler(new OSXPreferencesHandler());
		theDesktop.setQuitHandler(new OSXQuitHandler());
	}

	private static class OSXAboutHandler implements AboutHandler
	{
		@Override
		public void handleAbout(final AboutEvent aboutEvent)
		{
			PCGenUIManager.displayAboutDialog();
		}
	}

	private static class OSXPreferencesHandler implements PreferencesHandler
	{
		@Override
		public void handlePreferences(final PreferencesEvent preferencesEvent)
		{
			PCGenUIManager.displayPreferencesDialog();
		}
	}

	private static class OSXQuitHandler implements QuitHandler
	{
		@Override
		public void handleQuitRequestWith(final QuitEvent quitEvent, final QuitResponse quitResponse)
		{
			PCGenUIManager.closePCGen();
		}
	}

}
