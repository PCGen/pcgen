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

import pcgen.gui2.PCGenUIManager;

import com.apple.eawt.AboutHandler;
import com.apple.eawt.AppEvent;
import com.apple.eawt.Application;
import com.apple.eawt.PreferencesHandler;
import com.apple.eawt.QuitHandler;
import com.apple.eawt.QuitResponse;

/**
 * {@code MacGUI} initializes Mac-specific GUI elements.
 */
public final class MacGUIHandler
{
	private static MacGUIHandler theAdapter;
	private static Application theApp;

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
		theApp = Application.getApplication();
		theApp.setAboutHandler(new OSXAboutHandler());
		theApp.setPreferencesHandler(new OSXPreferencesHandler());
		theApp.setQuitHandler(new OSXQuitHandler());
	}


	private static class OSXAboutHandler implements AboutHandler
	{
		@Override
		public void handleAbout(final AppEvent.AboutEvent aboutEvent)
		{
			PCGenUIManager.displayAboutDialog();
		}
	}

	private static class OSXPreferencesHandler implements PreferencesHandler
	{
		@Override
		public void handlePreferences(final AppEvent.PreferencesEvent preferencesEvent)
		{
			PCGenUIManager.displayPreferencesDialog();
		}
	}

	private static class OSXQuitHandler implements QuitHandler
	{
		@Override
		public void handleQuitRequestWith(final AppEvent.QuitEvent quitEvent, final QuitResponse quitResponse)
		{
			PCGenUIManager.closePCGen();
		}
	}

}

