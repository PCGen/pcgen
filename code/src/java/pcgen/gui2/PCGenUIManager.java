/*
 * PCGenUIManager.java
 * Copyright 2008 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Jul 14, 2008, 8:43:48 PM
 */
package pcgen.gui2;

import gmgen.GMGenSystem;

import java.awt.Window;

import javax.swing.SwingUtilities;
import org.apache.commons.lang.SystemUtils;

import pcgen.gui2.dialog.PreferencesDialog;
import pcgen.gui2.plaf.MacGUIHandler;
import pcgen.system.Main;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public final class PCGenUIManager
{

	private static PCGenFrame pcgenFrame = null;

	private PCGenUIManager()
	{
	}

	public static void refreshUITree()
	{
		for (Window window : Window.getWindows())
		{
			SwingUtilities.updateComponentTreeUI(window);
		}
	}

	private boolean checkBounds()
	{
		return false;
	}

	public static void initializeGUI()
	{
		if (SystemUtils.IS_OS_MAC_OSX)
		{
			MacGUIHandler.initialize();
		}
		pcgenFrame = new PCGenFrame();
	}

	public static void startGUI()
	{
		SwingUtilities.invokeLater(new Runnable()
		{

			public void run()
			{
				pcgenFrame.startPCGenFrame();
			}

		});
	}

	public static void displayPreferencesDialog()
	{
		PreferencesDialog.show(pcgenFrame);
	}

	public static void displayAboutDialog()
	{
		pcgenFrame.showAboutDialog();
	}

	public static void closePCGen()
	{
		if (!pcgenFrame.closeAllCharacters())
		{
			return;
		}

		pcgenFrame.dispose();
		Main.shutdown();
	}

	public static void displayGmGen()
	{
		if (GMGenSystem.inst == null)
		{
			new GMGenSystem();
		}
		else
		{
			GMGenSystem.inst.setVisible(true);
		}
	}

}
