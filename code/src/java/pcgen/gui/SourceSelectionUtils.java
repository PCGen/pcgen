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
 * $Id: $
 */

package pcgen.gui;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.base.Constants;
import pcgen.core.Campaign;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.PersistenceManager;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;



/**
 * The Class <code>SourceSelectionUtils</code> holds common
 * functionality for source selection and loading.
 * 
 * Last Editor: $Author: $
 * Last Edited: $Date:  $
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision:  $
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
			ShowMessageDelegate.showMessageDialog("PCs are not closed in debug mode.  " + "Please be aware that they may not function correctly "
			+ "until campaign data is loaded again.",
				Constants.s_APPNAME, MessageType.WARNING);
		}
		else
		{
			parent.closeAllPCs();

			if (PCGen_Frame1.getBaseTabbedPane().getTabCount() > PCGen_Frame1.FIRST_CHAR_TAB) // All non-player tabs will be first
			{
				ShowMessageDelegate.showMessageDialog("Can't unload campaigns until all PC's are closed.", Constants.s_APPNAME,
					MessageType.INFORMATION);

				return false;
			}
			PCGen_Frame1.setCharacterPane(null);
		}

		Globals.emptyLists();
		PersistenceManager.getInstance().emptyLists();
		//PersistenceManager.getInstance().setChosenCampaignSourcefiles(new ArrayList<URI>());

		for (Campaign aCamp : Globals.getCampaignList())
		{
			aCamp.setIsLoaded(false);
		}

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
	public static boolean loadSources(List<Campaign> selectedCampaigns)
	{
		String oldStatus = "";
		try
		{
			PCGen_Frame1.getInst().closeAllPCs();

			if (PCGen_Frame1.getBaseTabbedPane().getTabCount() > PCGen_Frame1.FIRST_CHAR_TAB) // All non-player tabs will be first
			{
				ShowMessageDelegate.showMessageDialog(PropertyFactory.getString("in_campaignChangeError"),
					Constants.s_APPNAME, MessageType.INFORMATION);

				return false;
			}

			// Unload the existing campaigns and load our selected campaign
			Globals.emptyLists();
			PersistenceManager pManager = PersistenceManager.getInstance();
			pManager.emptyLists();
			pManager.setChosenCampaignSourcefiles(new ArrayList<URI>());

			for (Campaign aCamp : Globals.getCampaignList())
			{
				aCamp.setIsLoaded(false);
			}

			// Show that we are loading...
			oldStatus = PCGen_Frame1.getInst().getMainSource()
				.showLoadingSources();
			//PersistenceObserver observer = new PersistenceObserver();
			//pManager.addObserver( observer );
			pManager.loadCampaigns(selectedCampaigns);
			//pManager.deleteObserver( observer );

		}
		catch (PersistenceLayerException e)
		{
			ShowMessageDelegate.showMessageDialog(e.getMessage(), Constants.s_APPNAME, MessageType.WARNING);
		}

		pcgen.gui.PCGen_Frame1.getInst().getMainSource().updateLoadedCampaignsUI();

		// Show that we are done
		PCGen_Frame1.getInst().getMainSource().showSourcesLoaded(
			oldStatus);

		PCGen_Frame1 parent = pcgen.gui.PCGen_Frame1.getInst();
		if ((parent != null) && Globals.displayListsHappy())
		{
			parent.enableNew(true);
			parent.enableLstEditors(true);
		}
		
		return true;
	}

}
