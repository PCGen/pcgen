/*
 * Copyright (c) 2006, 2009.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2.0 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 * 
 * Created on Mar 10, 2006
 */
package pcgen.gui.converter.panel;

import gmgen.pluginmgr.PluginLoader;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.core.SettingsHandler;
import pcgen.gui.converter.UnstretchingGridLayout;
import pcgen.gui.converter.event.ProgressEvent;
import pcgen.persistence.lst.LstSystemLoader;

public class StartupPanel extends ConvertSubPanel
{

	private final JPanel message;
	
	private final LstSystemLoader loader;

	public StartupPanel(LstSystemLoader sl)
	{
		loader = sl;
		message = new JPanel();
		message.setLayout(new UnstretchingGridLayout(0, 1));
		message.add(new JLabel("Welcome to the PCGen 5.16 Data Converter..."));
		message.add(new JLabel("Loading Game Modes and Campaign Information."));
		message.add(new JLabel("Next button will become active "
				+ "when initialization is complete."));
	}

	@Override
	public boolean performAnalysis(CDOMObject pc)
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				SettingsHandler.readOptionsProperties();
				SettingsHandler.getOptionsFromProperties(null);
				PluginLoader ploader = PluginLoader.inst();
				ploader.startSystemPlugins(Constants.s_SYSTEM_TOKENS);
				loader.loadGameModes();
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
