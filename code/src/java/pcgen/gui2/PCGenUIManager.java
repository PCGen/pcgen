/*
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
 */
package pcgen.gui2;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import pcgen.cdom.base.Constants;
import pcgen.gui2.dialog.PreferencesDialog;
import pcgen.gui3.GuiAssertions;
import pcgen.gui3.application.DesktopHandler;
import pcgen.gui3.preferences.PCGenPreferencesModel;
import pcgen.system.Main;
import pcgen.util.Logging;

import javafx.application.Platform;


/**
 * The PCGenUIManager is responsible for starting up and shutting down PCGen's
 * main window. This class also provides static methods for outside UI
 * frameworks such as the application toolbar for Macs.
 *
 * @see pcgen.gui2.PCGenFrame
 */
public final class PCGenUIManager
{

	private static PCGenFrame pcgenFrame = null;

	private PCGenUIManager()
	{
	}

	public static void initializeGUI()
	{
		DesktopHandler.initialize();
		pcgenFrame = new PCGenFrame(new UIContext());
		String className = UIManager.getSystemLookAndFeelClassName();
		try
		{
			UIManager.setLookAndFeel(className);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
		{
			Logging.errorPrint("system look and feel not found", e);
		}

	}

	public static void startGUI()
	{
		GuiAssertions.assertIsNotOnGUIThread();
		Platform.setImplicitExit(false);
		SwingUtilities.invokeLater(pcgenFrame::startPCGenFrame);
	}

	public static void displayPreferencesDialog()
	{
		GuiAssertions.assertIsSwingThread();
		PreferencesDialog prefsDialog;
		prefsDialog = new PreferencesDialog(pcgenFrame, PCGenPreferencesModel.buildRoot(), Constants.APPLICATION_NAME);
		prefsDialog.setVisible(true);
	}

	public static void displayAboutDialog()
	{
		pcgenFrame.showAboutDialog();
	}

	public static void closePCGen()
	{
		if (pcgenFrame != null)
		{
			if (!pcgenFrame.closeAllCharacters())
			{
				return;
			}

			pcgenFrame.dispose();
		}
		Main.shutdown(true);
	}
}
