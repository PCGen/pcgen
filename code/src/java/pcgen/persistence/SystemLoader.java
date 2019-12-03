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

/**
 * {@code SystemLoader} is an abstract factory class that hides
 * the implementation details of the actual loader.  The initialize method
 * creates an instance of the underlying loader and calls abstract methods to
 * do the loading of system files.
 *
 */
public interface SystemLoader
{
	/**
	 * This is the delimiter for Tabs.
	 */
    String TAB_DELIM = "\t";

	/**
	 * This method sets a List of campaigns selected for a particular game 
	 * mode by the user.
	 * @param l List containing the chosen campaign source files
	 * @param game The game mode
	 */
    void setChosenCampaignSourcefiles(List<URI> l, GameMode game);

	/**
	 * This method gets a List of campaigns previously or currently
	 * selected for a particular game mode by the user.
	 * @param game The game mode
	 * @return List containing the chosen campaign source files
	 */
    List<URI> getChosenCampaignSourcefiles(GameMode game);
}
