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

package gmgen.util;

import com.apple.eawt.AppEvent;
import com.apple.eawt.Application;
import com.apple.eawt.PreferencesHandler;
import com.apple.eawt.QuitHandler;
import com.apple.eawt.QuitResponse;
import gmgen.GMGenSystem;

/**
 * An adaptor class to deal with Apple Macintosh OSX issues 
 */
public final class OSXAdapter
{
	// reference to the app where the existing quit, about, prefs code is
	private static OSXAdapter theAdapter;
	private static GMGenSystem mainApp;

	private OSXAdapter()
	{
	}

	public static void initialize(GMGenSystem inApp)
	{
		if (theAdapter != null)
		{
			return;
		}
		mainApp = inApp;
		theAdapter = new OSXAdapter();
		Application osxApplication = Application.getApplication();
		osxApplication.setPreferencesHandler(new OSXPreferencesHandler());
		osxApplication.setQuitHandler(new OSXQuitHandler());
	}


	private static class OSXPreferencesHandler implements PreferencesHandler
	{
		@Override
		public void handlePreferences(final AppEvent.PreferencesEvent preferencesEvent)
		{
			mainApp.mPreferencesActionPerformedMac();
		}
	}

	private static class OSXQuitHandler implements QuitHandler
	{
		@Override
		public void handleQuitRequestWith(final AppEvent.QuitEvent quitEvent, final QuitResponse quitResponse)
		{
			mainApp.exitFormMac();
		}
	}


}
