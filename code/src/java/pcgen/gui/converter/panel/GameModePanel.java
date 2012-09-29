/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.gui.converter.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.core.SystemCollections;
import pcgen.gui.converter.event.ProgressEvent;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.persistence.CampaignFileLoader;
import pcgen.system.ConfigurationSettings;
import pcgen.util.Logging;

public class GameModePanel extends ConvertSubPanel
{

	JComboBoxEx gameModeCombo;

	private SpringLayout layout = new SpringLayout();

	private final CampaignFileLoader campaignFileLoader;

	/**
	 * Create a new instance of GameModePanel
	 * @param campaignFileLoader The loader to read in campaigns
	 */
	public GameModePanel(CampaignFileLoader campaignFileLoader)
	{
		this.campaignFileLoader = campaignFileLoader;
	}

	@Override
	public boolean autoAdvance(CDOMObject pc)
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see pcgen.gui.converter.panel.ConvertSubPanel#returnAllowed()
	 */
	@Override
	public boolean returnAllowed()
	{
		return true;
	}
	@Override
	public boolean performAnalysis(CDOMObject pc)
	{
		File sourceDir = pc.get(ObjectKey.DIRECTORY);
		String name = sourceDir.getAbsolutePath();
		if (!name.equals(ConfigurationSettings.getPccFilesDir())
			&& !name.equals(ConfigurationSettings.getVendorDataDir()))
		{
			// User has selected another path - we need to load the sources from there.
			Logging.log(Logging.INFO, "Loading campaigns from " + sourceDir);
			Globals.clearCampaignsForRefresh();
			campaignFileLoader.setAlternateSourceFolder(sourceDir);
			campaignFileLoader.execute();
		}
		
		GameMode gameMode = pc.get(ObjectKey.GAME_MODE);
		if (gameMode != null)
		{
			SettingsHandler.setGame(gameMode.getName());
		}
//		Globals.emptyLists();
		Globals.sortPObjectListByName(Globals.getCampaignList());

		Globals.createEmptyRace();

		return saveGameMode(pc);
	}

	private boolean saveGameMode(CDOMObject pc)
	{
		boolean advance = pc.get(ObjectKey.GAME_MODE) != null;
		if (advance)
		{
			fireProgressEvent(ProgressEvent.ALLOWED);
		}
		else
		{
			fireProgressEvent(ProgressEvent.NOT_ALLOWED);
		}
		return advance;
	}

	private void getSelection(CDOMObject pc)
	{
		pc.put(ObjectKey.GAME_MODE, (GameMode) gameModeCombo.getSelectedItem());
	}

	@Override
	public void setupDisplay(JPanel panel, final CDOMObject pc)
	{
		panel.setLayout(layout);
		JLabel introLabel =
				new JLabel("Please select the Game Mode to Convert:");
		panel.add(introLabel);
		layout.putConstraint(SpringLayout.NORTH, introLabel, 50,
			SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.WEST, introLabel, 25,
			SpringLayout.WEST, panel);

		List<GameMode> games = SystemCollections.getUnmodifiableGameModeList();
		gameModeCombo = new JComboBoxEx(games.toArray());
		gameModeCombo.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				getSelection(pc);
				saveGameMode(pc);
			}
		});
		GameMode currGame = SettingsHandler.getGame();
		if (pc.get(ObjectKey.GAME_MODE) != null)
		{
			currGame = pc.get(ObjectKey.GAME_MODE);
		}
		gameModeCombo.setSelectedItem(currGame.getDisplayName());
		getSelection(pc);
		saveGameMode(pc);

		panel.add(gameModeCombo);
		layout.putConstraint(SpringLayout.NORTH, gameModeCombo, 20,
			SpringLayout.SOUTH, introLabel);
		layout.putConstraint(SpringLayout.WEST, gameModeCombo, 25,
			SpringLayout.WEST, panel);
	}
}
