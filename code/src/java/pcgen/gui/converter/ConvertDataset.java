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
package pcgen.gui.converter;

import gmgen.pluginmgr.PluginLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Campaign;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.MessageWrapper;
import pcgen.core.utils.ShowMessageConsoleObserver;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui.utils.NonGuiChooser;
import pcgen.gui.utils.NonGuiChooserRadio;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.LstSystemLoader;
import pcgen.rules.context.EditorLoadContext;
import pcgen.util.Logging;
import pcgen.util.chooser.ChooserFactory;

public class ConvertDataset
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

		Globals.setUseGUI(false);

		ShowMessageConsoleObserver messageObserver = new ShowMessageConsoleObserver();
		ShowMessageDelegate.getInstance().addObserver(messageObserver);

		PluginLoader ploader = PluginLoader.inst();
		ploader.startSystemPlugins(Constants.s_SYSTEM_TOKENS);

		try
		{
			SettingsHandler.readOptionsProperties();
			SettingsHandler.getOptionsFromProperties(null);
		}
		catch (Exception e)
		{
			e.printStackTrace(System.err);

			String message = e.getMessage();

			if ((message == null) || (message.length() == 0))
			{
				message = "Unknown error whilst reading options.ini";
			}

			message += "\n\nIt MAY be possible to fix this problem by deleting your options.ini file.";
			ShowMessageDelegate.showMessageDialog(new MessageWrapper(message,
					"PCGen - Error processing Options.ini", MessageType.ERROR));

			System.exit(0);
		}

		if (args.length == 0)
		{
			Logging.errorPrint("You must specify Campaign Files (PCC files) "
					+ "on the command line");
		}

		ChooserFactory.setInterfaceClassname(NonGuiChooser.class.getName());
		ChooserFactory.setRadioInterfaceClassname(NonGuiChooserRadio.class
				.getName());

		LstSystemLoader loader = new LstSystemLoader();
		try
		{
			loader.loadGameModes();
			// Load the initial campaigns
			loader.loadPCCFilesInDirectory(SettingsHandler.getPccFilesLocation()
				.getAbsolutePath());
			loader.loadPCCFilesInDirectory(SettingsHandler.getPcgenVendorDataDir()
				.getAbsolutePath());

			Globals.sortPObjectListByName(Globals.getCampaignList());

			boolean first = true;
			String outputDirectory = null;
			String rootDirectory = null;
			List<Campaign> list = new ArrayList<Campaign>();
			List<String> modes = null;
			for (String fn : args)
			{
				if (first)
				{
					if (fn.startsWith("-outfile="))
					{
						FileHandler ch = new FileHandler(fn.substring(9));
						Logger.getLogger("pcgen").addHandler(ch);
						Logger.getLogger("plugin").addHandler(ch);
						continue;
					}
					if (fn.startsWith("-outdir="))
					{
						outputDirectory = fn.substring(8);
						continue;
					}
					if (fn.startsWith("-rootdir="))
					{
						rootDirectory = fn.substring(9);
						continue;
					}
					if (fn.equalsIgnoreCase("-warning"))
					{
						Logger.getLogger("pcgen").setLevel(Logging.LST_INFO);
						Logger.getLogger("plugin").setLevel(Logging.LST_INFO);
						continue;
					}
				}
				if (outputDirectory == null)
				{
					System.err.println("Must Specify output directory");
					System.exit(1);
				}
				if (rootDirectory == null)
				{
					System.err.println("Must Specify root directory");
					System.exit(1);
				}
				Campaign c = Globals.getCampaignByURI(new File(rootDirectory
						+ File.separator + fn).toURI(), true);
				if (c == null)
				{
					System.err.println("Can't find: " + fn);
				}
				else
				{
					list.add(c);
					if (first)
					{
						modes = c.getListFor(ListKey.GAME_MODE);
					}
					else
					{
						modes.retainAll(c.getSafeListFor(ListKey.GAME_MODE));
					}
				}
				first = false;
			}
			if (modes.size() < 1)
			{
				System.err.println("Campaigns don't have compatible Game Mode");
			}
			else
			{
				SettingsHandler.setGame(modes.get(0));
				EditorLoadContext lc = new EditorLoadContext();
				new BatchConverter(lc, outputDirectory, rootDirectory, list)
						.process();
			}
		}
		catch (PersistenceLayerException e)
		{
			ShowMessageDelegate.showMessageDialog(e.getMessage(),
					Constants.s_APPNAME, MessageType.WARNING);
		}
		catch (SecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
