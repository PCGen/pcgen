/*
 * PersistenceManager.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on February 22, 2002, 10:29 PM
 *
 * $Id$
 */
package pcgen.persistence;

import java.net.URI;
import java.util.Collection;
import java.util.List;

import pcgen.core.Campaign;
import pcgen.core.GameMode;
import pcgen.core.SettingsHandler;
import pcgen.persistence.lst.LstSystemLoader;

/** <code>PersistenceManager</code> is a factory class that hides
 * the implementation details of the actual loader.  The initialize method
 * creates an instance of the underlying loader and calls methods to
 * do the loading of system files.
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision$
 */
public final class PersistenceManager
{
	private static final SystemLoader instance = new LstSystemLoader();
	private static boolean initialized;
	private static final PersistenceManager managerInstance =
			new PersistenceManager();

	/**
	 * Private to make it impossible to create another instance
	 */
	private PersistenceManager()
	{
		// Empty Constructor
	}

	/**
	 * Get an instance of this manager
	 * @return an instance of this manager
	 */
	public static PersistenceManager getInstance()
	{
		return managerInstance;
	}

	/**
	 * Set the source files for the chosen campaign for the current game mode.
	 * @param l
	 */
	public void setChosenCampaignSourcefiles(List<URI> l)
	{
		instance.setChosenCampaignSourcefiles(l, SettingsHandler.getGame());
	}

	/**
	 * Set the source files for the chosen campaign for the specific game mode.
	 * @param l
	 * @param game The game mode.
	 */
	public void setChosenCampaignSourcefiles(List<URI> l, GameMode game)
	{
		instance.setChosenCampaignSourcefiles(l, game);
	}

	/**
	 * Get the chosen campaign source files for the current game mode.
	 * @return the chosen campaign source files
	 */
	public List<URI> getChosenCampaignSourcefiles()
	{
		return instance.getChosenCampaignSourcefiles(SettingsHandler.getGame());
	}

	/**
	 * Empty the lists
	 */
	public void emptyLists()
	{
		instance.emptyLists();
	}

	/////////////////////////////////////////////////////////////////////
	// Static methods
	////////////////////////////////////////////////////////////////////

	/**
	 * Initialize the SystemLoader with the appropriate loader classes
	 * Right now this is hardcoded to LstSystemLoader, but the eventual
	 * intention is to allow any system loader to be plugged in so that
	 * the data can be loaded from .lst files or XML files,
	 * or possibly even a database.
	 * @throws PersistenceLayerException
	 */
	public void initialize() throws PersistenceLayerException
	{
		// Bug 638568 -- sage_sam, 18 Feb 2003
		// Make sure the manager is only initialized once.
		if (!initialized)
		{
			instance.initialize();
			initialized = true;
		}
	}

	public void clear()
	{
		instance.markAllUnloaded();
	}

	public boolean isLoaded(Campaign campaign)
	{
		return instance.isLoaded(campaign);
	}

	public Collection<Campaign> getLoadedCampaigns()
	{
		return instance.getLoadedCampaigns();
	}

}
