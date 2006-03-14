/*
 * SystemLoader.java
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

import java.util.List;
import java.util.Observer;
import java.util.Set;

/** 
 * <code>SystemLoader</code> is an abstract factory class that hides
 * the implementation details of the actual loader.  The initialize method
 * creates an instance of the underlying loader and calls abstract methods to
 * do the loading of system files.
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision$
 */
public interface SystemLoader
{
	/**
	 * This is the delimiter for Tabs.
	 */
	public static final String TAB_DELIM = "\t";

	/**
	 * This method sets a List of campaigns  selected by the user.
	 * @param l List containing the chosen campaign source files
	 */
	public void setChosenCampaignSourcefiles(List l);

	/**
	 * This method gets a List of campaigns previously or currently
	 * selected by the user.
	 * @return List containing the chosen campaign source files
	 */
	public List getChosenCampaignSourcefiles();

	/**
	 * This method indicates whether custom items have been successfully
	 * loaded
	 * @return boolean true if custom items are loaded, else false
	 */
	public boolean isCustomItemsLoaded();

	/**
	 * This method gets the set of sources loaded by the loader.
	 * @return Set containing the names of the sources loaded.
	 */
	public Set getSources();

	/**
	 * This method empties whatever lists the implementation has
	 * loaded and stored in its own implementation.
	 */
	public void emptyLists();

	/**
	 * This method initialize the SystemLoader with in whatever ways
	 * are required prior to performing any actual loads.
	 * @throws PersistenceLayerException
	 */
	public void initialize() throws PersistenceLayerException;

	/**
	 * This method actually loads the given list of selected campaigns
	 * @param aSelectedCampaignsList List of Campaign objects to load
	 * @throws PersistenceLayerException if an error occurs in the
	 *         persistence layer
	 */
	public void loadCampaigns(List aSelectedCampaignsList)
		throws PersistenceLayerException;

	/**
	 * Loads a file containing game system information and adds details
	 * to an array. Eventually these end up in the various array list
	 * properties of <code>Global</code>.
	 * <p>
	 * Different types of files are determined by the <code>type</code>
	 * parameter. The valid <code>type</code>'s are in LstConstants.java
	 * <p>
	 * The file is opened and read. Lines are parsed by an object
	 * of the relevant type (based on <code>type</code> above), and
	 * then added to the array list.
	 *
	 * @param fileName    name of the file to load from
	 * @param fileType    type of the file (see above for types).
	 * @param aList       <code>ArrayList</code> with existing data.
	 *                    The new data is appended to this.
	 * @throws PersistenceLayerException
	 */
	public void loadFileIntoList(String fileName, int fileType, List aList)
		throws PersistenceLayerException;

	/**
	 * This method loads the .MOD items
	 * @param flagDisplayError boolean whether errors should be displayed
	 */
	public void loadModItems(boolean flagDisplayError);

	/**
	 * Check for an updated set of campaigns
	 * and update Globals.campaignList accordingly
	 * @see pcgen.core.Globals#getCampaignList
	 */
	public void refreshCampaigns();

	/**
	 * Add an Observer
	 * @param o
	 */
	public void addObserver(Observer o);
	
	/**
	 * Delete an Observer
	 * @param o
	 */
	public void deleteObserver(Observer o);
	
	/**
	 * Notify Observers
	 */
	public void notifyObservers();

}
