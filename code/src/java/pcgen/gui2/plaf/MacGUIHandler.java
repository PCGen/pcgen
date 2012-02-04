/*
 * MacGUI.java
 * Copyright 2006 (C) Tod Milam <twmilam@yahoo.com>
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
 * Created on January 18, 2006
 */
package pcgen.gui2.plaf;

import com.apple.eawt.*;
import pcgen.gui2.PCGenUIManager;

/**
 * <code>MacGUI</code> initializes Mac-specific GUI elements.
 *
 * @author Tod Milam <twmilam@yahoo.com>
 * @version $Revision: 1828 $
 */
public class MacGUIHandler extends ApplicationAdapter
{

	private static MacGUIHandler myObj = null;
	private static com.apple.eawt.Application theApp = null;

	private MacGUIHandler()
	{
	}

	/**
	 * Initialize the Mac-specific properties.
	 * Create an ApplicationAdapter to listen for Help, Prefs, and Quit.
	 */
	public static void initialize()
	{
		if (myObj != null)
		{
			// we have already initialized.
			return;
		}

		// set up the Application menu
		myObj = new MacGUIHandler();
		theApp = new com.apple.eawt.Application();
		theApp.addApplicationListener(myObj);
		theApp.setEnabledPreferencesMenu(true);
	}  // end static initialize method

	/**
	 * Called when user select "About" from the application menu.
	 */
	public void handleAbout(ApplicationEvent ae)
	{
		PCGenUIManager.displayAboutDialog();
		ae.setHandled(true);
	}  // end handleAbout

	/**
	 * Called when user select "Preferences" from the application menu.
	 */
	public void handlePreferences(ApplicationEvent ae)
	{
		PCGenUIManager.displayPreferencesDialog();
		ae.setHandled(true);
	}  // end handlePreferences

	/**
	 * Called when user select "Quit" from the application menu.
	 */
	public void handleQuit(ApplicationEvent ae)
	{
		ae.setHandled(false);
		PCGenUIManager.closePCGen();
	}  // end handleQuit
}  // end class MacGUI

