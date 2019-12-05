/*
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
 *
 */
package pcgen.persistence;

import java.net.URI;
import java.util.List;

import pcgen.core.GameMode;
import pcgen.core.SettingsHandler;
import pcgen.persistence.lst.LstSystemLoader;

/**
 * {@code PersistenceManager} is a factory class that hides
 * the implementation details of the actual loader.  The initialize method
 * creates an instance of the underlying loader and calls methods to
 * do the loading of system files.
 */
public final class PersistenceManager
{
    private static final SystemLoader INSTANCE = new LstSystemLoader();
    private static final PersistenceManager MANAGER_INSTANCE = new PersistenceManager();

    private PersistenceManager()
    {
    }

    /**
     * Get an instance of this manager
     *
     * @return an instance of this manager
     */
    public static PersistenceManager getInstance()
    {
        return MANAGER_INSTANCE;
    }

    /**
     * Set the source files for the chosen campaign for the current game mode.
     *
     * @param l CODE-1889 to remove use of this method
     */
    public void setChosenCampaignSourcefiles(List<URI> l)
    {
        INSTANCE.setChosenCampaignSourcefiles(l, SettingsHandler.getGame());
    }

    /**
     * Set the source files for the chosen campaign for the specific game mode.
     *
     * @param l
     * @param game The game mode.
     *             <p>
     *             CODE-1889 to remove use of this method
     */
    public void setChosenCampaignSourcefiles(List<URI> l, GameMode game)
    {
        INSTANCE.setChosenCampaignSourcefiles(l, game);
    }

    /**
     * Get the chosen campaign source files for the current game mode.
     *
     * @return the chosen campaign source files
     * <p>
     * CODE-1889 to remove use of this method
     */
    public List<URI> getChosenCampaignSourcefiles()
    {
        return INSTANCE.getChosenCampaignSourcefiles(SettingsHandler.getGame());
    }

}
