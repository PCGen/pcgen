/*
 * SourceSelectionUtils.java
 * Copyright 2008 (C) James Dempsey
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
 * Created on 09/11/2008 11:07:19 AM
 *
 * $Id$
 */

package pcgen.gui.sources;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.base.Constants;
import pcgen.core.Campaign;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui.PCGen_Frame1;
import pcgen.gui.PersistenceObserver;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.PersistenceManager;
import pcgen.util.Logging;
import pcgen.system.LanguageBundle;
import pcgen.util.SwingWorker;



/**
 * The Class <code>SourceSelectionUtils</code> holds common
 * functionality for source selection and loading.
 * 
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public final class SourceSelectionUtils
{

	/**
	 * Switch game modes.
	 * 
	 * @param gameMode the game mode
	 */
	public static void changeGameMode(String gameMode)
	{
		SettingsHandler.setGame(gameMode);
		Globals.createEmptyRace();
		PCGen_Frame1.getInst().getPcgenMenuBar().getGameModeMenu().updateMenu();
		PCGen_Frame1.getInst().getMainSource().changedGameMode();

		InfoPanel infoPanel = PCGen_Frame1.getInst().getInfoPanel();
		if (infoPanel != null)
		{
			infoPanel.refreshDisplay();
		}
	}
	
	/**
	 * Unload all loaded sources.
	 * 
	 * @return true, if successful
	 */
	public static boolean unloadSources()
	{
		PCGen_Frame1 parent = PCGen_Frame1.getInst();

		if (Logging.isDebugMode()) //don't force PC closure if we're in debug mode, per request
		{
			ShowMessageDelegate.showMessageDialog(
				"PCs are not closed in debug mode.  "
					+ "Please be aware that they may not function correctly "
					+ "until campaign data is loaded again.",
				Constants.APPLICATION_NAME,
				MessageType.WARNING);
		}
		else
		{
			parent.closeAllPCs();

			if (PCGen_Frame1.getBaseTabbedPane().getTabCount() > PCGen_Frame1.FIRST_CHAR_TAB) // All non-player tabs will be first
			{
				ShowMessageDelegate.showMessageDialog(
					"Can't unload campaigns until all PC's are closed.",
					Constants.APPLICATION_NAME,
					MessageType.INFORMATION);

				return false;
			}
			PCGen_Frame1.setCharacterPane(null);
		}

		Globals.emptyLists();
		PersistenceManager.getInstance().clear();

		parent.enableLstEditors(false);

		PCGen_Frame1.enableDisableMenuItems();
		PCGen_Frame1.getInst().getMainSource().campaignsUnloaded();
		
		return true;
	}
	

	/**
	 * Load the source materials.
	 * 
	 * @param selectedCampaigns the sources to be loaded.
	 */
	public static boolean loadSources(final List<Campaign> selectedCampaigns)
	{
		PCGen_Frame1.getInst().closeAllPCs();

		if (PCGen_Frame1.getBaseTabbedPane().getTabCount() > PCGen_Frame1.FIRST_CHAR_TAB) // All non-player tabs will be first
		{
			ShowMessageDelegate.showMessageDialog(
				LanguageBundle.getString("in_campaignChangeError"),
				Constants.APPLICATION_NAME,
				MessageType.INFORMATION);

			return false;
		}

		final SwingWorker worker = new SwingWorker()
		{
			String oldStatus;

			public Object construct()
			{
				oldStatus = doCampaignLoad(selectedCampaigns);
				return "";
			}

			@Override
			public void finished()
			{
				showCampaignsLoaded(oldStatus);
			}
		};
		worker.start();
		
		return true;
	}

	/**
	 * Load the specified campaigns. Will unload any existing data and 
	 * load the supplied data. It is intended that this not be run in the swing thread.
	 *  
	 * @param selectedCampaigns The sources to be loaded.
	 * @return The status displayed before we changed it to say sources were being loaded.
	 * @throws PersistenceLayerException If the files cannot be loaded.
	 */
	private static String doCampaignLoad(List<Campaign> selectedCampaigns)
	{
		// Unload the existing campaigns and load our selected campaign
		Globals.emptyLists();
		PersistenceManager pManager = PersistenceManager.getInstance();
		pManager.clear();
		pManager.setChosenCampaignSourcefiles(new ArrayList<URI>());

		// Show that we are loading...
		String oldStatus = PCGen_Frame1.getInst().getMainSource()
			.showLoadingSources();
		final PersistenceObserver observer = new PersistenceObserver();
		pManager.addObserver( observer );
		Logging.registerHandler( observer.getHandler() );
		try
		{
			pManager.loadCampaigns(selectedCampaigns);
		}
		catch (PersistenceLayerException e)
		{
			Logging.errorPrint("Failed to load campaigns", e);
			ShowMessageDelegate.showMessageDialog(
				e.getMessage(),
				Constants.APPLICATION_NAME,
				MessageType.WARNING);
		}
		Logging.removeHandler( observer.getHandler() );
		pManager.deleteObserver( observer );
		return oldStatus;
	}

	/**
	 * Update the display to show that loading campaigns has finished.
	 * 
	 * @param oldStatus The status we should change the display to.
	 */
	private static void showCampaignsLoaded(String oldStatus)
	{
		pcgen.gui.PCGen_Frame1.getInst().getMainSource().updateLoadedCampaignsUI();

		// Show that we are done
		PCGen_Frame1.getInst().getMainSource().showSourcesLoaded(
			oldStatus);
		InfoPanel infoPanel = PCGen_Frame1.getInst().getInfoPanel();
		if (infoPanel != null)
		{
			infoPanel.refreshDisplay();
		}

		PCGen_Frame1 parent = pcgen.gui.PCGen_Frame1.getInst();
		if ((parent != null) && Globals.displayListsHappy())
		{
			parent.enableNew(true);
			parent.enableLstEditors(true);
		}
	}

	
	/**
	 * Refresh the PCC files from disc, also refreshes the main 
	 * source panel, if displayed.
	 */
	public static void refreshSources()
	{
		PCGen_Frame1.getInst().getMainSource().refreshCampaigns();
	}


	/**
	 * Given a name make a safe file name from it. 
	 * @param name The name to be converted e.g. .pcc
	 * @param extension The extension the filename must have
	 * @return A safe filename
	 */
	public static String sanitiseFilename(String name, String extension)
	{
		String filename = name.replaceAll("[^A-Za-z0-9\\.-]", "_");
		if (extension != null && extension.length() > 0
			&& !filename.toLowerCase().endsWith(extension.toLowerCase()))
		{
			filename += extension;
		}
		return filename;
	}

	/**
	 * Returns a decoded path relative to the pcc file location or vendor data dir, if applicable.
	 * Returns the provided path otherwise.
	 * 
	 * @param absPath
	 * @return
	 */
	public static String convertPathToDataPath(String absPath)
	{
		String testpath = new File(absPath).toURI().getPath();
		String dataFolder = SettingsHandler.getPccFilesLocation().toURI().getPath();
		if (testpath.startsWith(dataFolder))
		{
			return "@"+testpath.substring(dataFolder.length()-1);
		}
		
		if (SettingsHandler.getPcgenVendorDataDir() != null)
		{
			String vendorDataFolder = SettingsHandler.getPcgenVendorDataDir().toURI().getPath();
			if (testpath.startsWith(vendorDataFolder))
			{
				return "&"+testpath.substring(vendorDataFolder.length()-1);
			}
		}
		return absPath;
	}
	
}
