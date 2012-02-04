/*
 * Options.java
 * Copyright 2001 (C) Mario Bonassin
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
 * Created on April 24, 2001, 10:06 PM
 */
package pcgen.gui;

import pcgen.core.Globals;
import pcgen.gui.sources.SourceSelectionDialog;
import pcgen.gui.utils.IconUtilitities;
import pcgen.gui.utils.Utility;
import pcgen.util.Logging;
import pcgen.system.LanguageBundle;

import javax.swing.JMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Provide a panel with most of the rule configuration options for a campaign.
 * This lots of unrelated entries.
 *
 * @author  Mario Bonassin
 * @version $Revision$
 */
final class Options extends JMenu // extends Preferences
{
	//
	// Used to create the entries for the "set all stats to" menu
	private PrefsMenuListener prefsMenuHandler = new PrefsMenuListener();

	private SourcesMenuListener sourcesMenuHandler = new SourcesMenuListener();

	private GameModes modesMenu;

	/** Creates new form Options */
	public Options()
	{
		setText(LanguageBundle.getString("in_mnuSettings"));
		setMnemonic(LanguageBundle.getMnemonic("in_mn_mnuSettings"));
		Utility.setDescription(this, LanguageBundle.getString("in_mnuSettingsTip"));

		IconUtilitities.maybeSetIcon(this, "Preferences16.gif");

		try
		{
			jbInit();
		}
		catch (Exception e)
		{
			Logging.errorPrint("Error while initing form", e);
		}
	}

	private void addCampMenu(JMenu parent)
	{
		modesMenu = new GameModes();
		parent.add(modesMenu);
	}

	private void addPreferencesMenu(JMenu parent)
	{
		parent.add(Utility.createMenuItem("mnuSettingsPreferences", prefsMenuHandler, null, null, "Preferences16.gif",
		        true));
	}

	private void jbInit() throws Exception
	{
		addCampMenu(this); // campaigns
		addPreferencesMenu(this); // Preferences dialog
		addQuickSourcesMenu(this); // Sources dialog
	}

	private void addQuickSourcesMenu(JMenu parent)
	{
		parent.add(Utility.createMenuItem("mnuSettingsSources", sourcesMenuHandler, null, null, "",
	        true));
	}

	/**
	 * Show the preferences pane.
	 */
	private static final class PrefsMenuListener implements ActionListener
	{
		public void actionPerformed(ActionEvent actionEvent)
		{
			PCGen_Frame1.setMessageAreaText("Preferences...");

			if (PCGen_Frame1.getCharacterPane() != null)
			{
				// Called for side-effect - ignore return
				PCGen_Frame1.getCharacterPane().getCurrentPC();
			}

			PreferencesDialog.show(Globals.getRootFrame());

			PCGen_Frame1.restoreMessageAreaText();
		}
	}

	/**
	 * Show the sources pane.
	 */
	private static final class SourcesMenuListener implements ActionListener
	{
		public void actionPerformed(ActionEvent actionEvent)
		{
			PCGen_Frame1.setMessageAreaText(LanguageBundle
				.getString("in_qsrc_messageText"));

			SourceSelectionDialog dialog =
					new SourceSelectionDialog(PCGen_Frame1.getInst(), true);
			dialog.setVisible(true);

			PCGen_Frame1.restoreMessageAreaText();
		}
	}

	/**
	 * Gets the game mode menu.
	 * 
	 * @return the game mode menu
	 */
	GameModes getGameModeMenu()
	{
		return modesMenu;
	}
}
