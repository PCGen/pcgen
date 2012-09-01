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

import gmgen.pluginmgr.PluginLoader;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.core.SettingsHandler;
import pcgen.gui.converter.UnstretchingGridLayout;
import pcgen.gui.converter.event.ProgressEvent;
import pcgen.persistence.CampaignFileLoader;
import pcgen.persistence.GameModeFileLoader;

public class StartupPanel extends ConvertSubPanel
{

	private final JPanel message;
	private final JProgressBar progressBar;
	private final GameModeFileLoader gameModeFileLoader;
	private final CampaignFileLoader campaignFileLoader;

	
	/**
	 * Create a new instance of StartupPanel
	 * @param gameModeFileLoader
	 * @param campaignFileLoader
	 */
	public StartupPanel(GameModeFileLoader gameModeFileLoader,
		CampaignFileLoader campaignFileLoader)
	{
		this.gameModeFileLoader = gameModeFileLoader;
		this.campaignFileLoader = campaignFileLoader;
		message = new JPanel();
		message.setLayout(new UnstretchingGridLayout(0, 1));
		message.add(new JLabel("Welcome to the PCGen 6.0 Data Converter..."));
		message.add(new JLabel(" "));
		message.add(new JLabel("Loading Game Modes and Campaign Information."));
		message.add(new JLabel(" "));

        progressBar = new JProgressBar(0, 5);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);

        message.add(progressBar);
		message.add(new JLabel(" "));
	}

	@Override
	public boolean performAnalysis(CDOMObject pc)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				SettingsHandler.readOptionsProperties();
		        progressBar.setValue(1);
				SettingsHandler.getOptionsFromProperties(null);
		        progressBar.setValue(2);
				PluginLoader ploader = PluginLoader.inst();
				ploader.startSystemPlugins(Constants.SYSTEM_TOKENS);
		        progressBar.setValue(3);
		        gameModeFileLoader.execute();
		        progressBar.setValue(4);
		        campaignFileLoader.execute();
		        progressBar.setValue(5);
		        
				message.add(new JLabel("Initialization complete, press next button to continue..."));
				message.revalidate();
		        
				fireProgressEvent(ProgressEvent.ALLOWED);
			}
		}).start();
		return true;
	}

	@Override
	public boolean autoAdvance(CDOMObject pc)
	{
		return false;
	}

	@Override
	public void setupDisplay(JPanel panel, CDOMObject pc)
	{
		panel.add(message);
		panel.setPreferredSize(new Dimension(800, 500));
	}

	@Override
	public boolean isLast()
	{
		return false;
	}

}
