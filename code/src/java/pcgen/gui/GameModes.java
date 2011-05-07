/*
 * GameModes.java
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
 *
 * August 23, 2002 -- Byngl
 * Major overhaul to check system/gameModes and use properties to build menu
 *
 */
package pcgen.gui;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.*;
import pcgen.gui.sources.SourceSelectionUtils;
import pcgen.gui.utils.Utility;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

/**
 * Provide a panel with most of the rule configuration options for a campaign.
 * This lots of unrelated entries.
 *
 * @author  Mario Bonassin
 * @version $Revision$
 */
public final class GameModes extends JMenu
{
	static final long serialVersionUID = -6751569845505079621L;
	private static String in_stdrdCampaign = PropertyFactory.getString("in_stdrdCampaign"); // Title for the standard campaign menu item
	private AbstractList<JRadioButtonMenuItem> campaignMenuItems = new ArrayList<JRadioButtonMenuItem>();
	private AbstractList<Campaign> campaigns = new ArrayList<Campaign>();
	private ButtonGroup gameModeGroup = null;
	private CheckBoxListener checkBoxHandler = new CheckBoxListener();
	private JRadioButtonMenuItem[] gameModeNames = null;
	private String[] in_modeName = null; // Text for menu entry
	private String[] in_useMode = null; // Tool tips

	/** Creates new form Options */
	GameModes()
	{
		try
		{
			jbInit();
			setText(PropertyFactory.getString("in_mnuSettingsCampaign"));
			setMnemonic(PropertyFactory.getMnemonic("in_mn_mnuSettingsCampaign"));
			Utility.setDescription(this, PropertyFactory.getString("in_mnuSettingsCampaignTip"));
			updateMenu();
		}
		catch (Exception e)
		{
			Logging.errorPrint("Exception while initing the form", e);
		}
	}

	private void getMenuInfo(int gameModeCount)
	{
		in_useMode = new String[gameModeCount];
		in_modeName = new String[gameModeCount];

		for (int i = 0; i < gameModeCount; ++i)
		{
			final GameMode gameMode = SystemCollections.getUnmodifiableGameModeList().get(i);
			in_modeName[i] = gameMode.getMenuEntry();
			in_useMode[i] = gameMode.getMenuToolTip();
		}
	}

	private void jbInit() throws Exception
	{
		final int gameModeCount = SystemCollections.getUnmodifiableGameModeList().size();

		if (SystemCollections.getUnmodifiableGameModeList().size() == 0)
		{
			return;
		}

		getMenuInfo(gameModeCount);
		gameModeNames = new JRadioButtonMenuItem[gameModeCount];

		//
		// In order for a game mode to show up, there must be a directory in system/gameModes
		// which contains statsandchecks.lst and miscinfo.lst
		//
		// Format of lines in system/gameModes/[blah]/miscinfo.lst:
		//
		// Text displayed on menu.
		// gamemode1  MENUENTRY:D&D|Standard
		// gamemode2  MENUENTRY:D&D|More Options|Even More Options|No More Options
		// gamemode3  MENUENTRY:D&D|~Non-Standard
		//
		// Creates:
		//  D&D --> Standard
		//          More Options --> Even More Options --> No More Options
		//          ------------
		//          Non-Standard
		//
		gameModeGroup = new ButtonGroup();

		for (int i = 0; i < in_modeName.length; ++i)
		{
			final StringTokenizer aTok = new StringTokenizer(in_modeName[i], "|", false);
			JMenu mnuLevel = this;
			JMenu firstSubMenu = null;

			while (aTok.hasMoreTokens())
			{
				String aName = aTok.nextToken();

				//
				// Add a separator if the name starts with '~'
				//
				if (aName.charAt(0) == '~')
				{
					mnuLevel.addSeparator();
					aName = aName.substring(1);
				}

				//
				// If there are more tokens, then add a JMenu with this description
				// unless one already exists.
				//
				if (aTok.hasMoreTokens())
				{
					//
					// Look for a JMenu with the same description
					//
					JMenu mnu = null;

					for (int j = 0; j < mnuLevel.getItemCount(); ++j)
					{
						Object anObj = mnuLevel.getItem(j);

						if (anObj instanceof JMenu)
						{
							if (((JMenu) anObj).getText().equals(aName))
							{
								mnu = (JMenu) anObj;

								break;
							}
						}
					}

					//
					// Not found, add one
					//
					if (mnu == null)
					{
						mnu = new JMenu(aName);
						mnuLevel.add(mnu);
					}

					if (firstSubMenu == null)
					{
						firstSubMenu = mnu;
					}

					mnuLevel = mnu;
				}

				//
				// Reached the end of the list. Add a game mode description here
				//
				else
				{
					gameModeNames[i] = new JRadioButtonMenuItem(aName, false);
					gameModeGroup.add(mnuLevel.add(gameModeNames[i]));
					Utility.setDescription(gameModeNames[i], in_useMode[i]);
					gameModeNames[i].addActionListener(checkBoxHandler);
				}
			}

			// Add any menu items from campaigns which match this game mode
			boolean firstCampaignEntry = true;
			for (Campaign aCamp : Globals.getCampaignList())
			{
				if (aCamp.getSafe(ObjectKey.SHOW_IN_MENU)
					&& aCamp.containsInList(ListKey.GAME_MODE, (SystemCollections.getUnmodifiableGameModeList().get(i)).getName()))
				{
					if (firstSubMenu == null)
					{
						// This game mode only had a single menu item - no sub menus.
						// So create a sub-menu for it, rename the original menu item
						// to 'standard' and move it to this new sub-menu.
						firstSubMenu = new JMenu(gameModeNames[i].getText());
						mnuLevel.remove(gameModeNames[i]);
						mnuLevel.add(firstSubMenu);
						Utility.setTextAndMnemonic(firstSubMenu, firstSubMenu.getText());
						gameModeNames[i].setText(in_stdrdCampaign);
						firstSubMenu.add(gameModeNames[i]);
					}

					if (firstCampaignEntry)
					{
						firstCampaignEntry = false;
						firstSubMenu.addSeparator();
					}

					JRadioButtonMenuItem campaignMenuItem = new JRadioButtonMenuItem(aCamp.getDisplayName());
					gameModeGroup.add(firstSubMenu.add(campaignMenuItem));
					String description = "";
					if (aCamp.getSizeOfListFor(ListKey.INFO_TEXT) > 0)
					{
						for (String info : aCamp.getListFor(ListKey.INFO_TEXT))
						{
							description += info + "\n";
						}
					}
					Utility.setDescription(campaignMenuItem, description);
					campaigns.add(aCamp);
					campaignMenuItems.add(campaignMenuItem);
					campaignMenuItem.addActionListener(checkBoxHandler);
				}
			}
		}

		//
		// Look for &'s in the menu text...translate into mnemonic. NOTE "&&" translates to "&"
		//
		for (int i = 0; i < gameModeNames.length; ++i)
		{
			Utility.setTextAndMnemonic(gameModeNames[i], gameModeNames[i].getText());
		}
	}

	public void updateMenu()
	{
		boolean bFound = false;

		for (;;)
		{
			if (in_modeName != null)
			{
				for (int i = 0; i < in_modeName.length; ++i)
				{
					if (Globals.isInGameMode(
							(SystemCollections.getUnmodifiableGameModeList().get(i)).getName()))
					{
						gameModeNames[i].setSelected(true);
						bFound = true;

						break;
					}
				}
			}

			//
			// If couldn't find game mode, then attempt to set it to DnD mode
			//
			if (bFound)
			{
				break;
			}

			SettingsHandler.setGame(Constants.GAMEMODE_EDITION_THREE_POINT_ZERO);
			bFound = true;
		}

		if (Globals.getRootFrame() != null)
		{
			PCGen_Frame1.getInst().setGameModeTitle();
			PCGen_Frame1.getInst().refreshCharInfoTabs();
		}
	}

	/**
	 * This class is used to respond to clicks on the check boxes.
	 */
	private final class CheckBoxListener implements ActionListener
	{
		public void actionPerformed(ActionEvent actionEvent)
		{
			final Object source = actionEvent.getSource();
			String tempGameMode = Constants.GAMEMODE_EDITION_THREE_POINT_ZERO;
			int campaignNum = -1;

			if (source == null)
			{
				return;
			}

			// Was a campaign rather than a game mode selected?
			campaignNum = campaignMenuItems.indexOf(source);

			((JRadioButtonMenuItem) source).requestFocus();

			for (int i = 0; i < gameModeNames.length; ++i)
			{
				if (source == gameModeNames[i])
				{
					tempGameMode = (SystemCollections.getUnmodifiableGameModeList().get(i)).getName();
				}
			}

			// Now check for a campaign selection which may have switched game mode
			List<Campaign> selectedCampaigns = new ArrayList<Campaign>();
			if (campaignNum >= 0)
			{
				// We can now specify multiple game modes in a PCC file.
				// We assume here that the 1st one is the primary one.
				List<String> gameModeList = (campaigns.get(campaignNum)).getSafeListFor(ListKey.GAME_MODE);
				tempGameMode = gameModeList.get(0);
				selectedCampaigns.add(campaigns.get(campaignNum));
			}

			if (!Globals.isInGameMode(tempGameMode))
			{
				SourceSelectionUtils.changeGameMode(tempGameMode);
			}

			// Now we deal with a campaign selection
			if (!selectedCampaigns.isEmpty())
			{
				SourceSelectionUtils.loadSources(selectedCampaigns);
			}
		}
	}
}
