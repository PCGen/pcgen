/*
 *  gmgen.plugin.dicebag.gui - DESCRIPTION OF PACKAGE
 *  Copyright (C) 2003 RossLodge
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 *  The author of this program grants you the ability to use this code
 *  in conjunction with code that is covered under the Open Gaming License
 *
 *  DiceBagPluginModel.java
 *
 *  Created on Oct 17, 2003, 2:54:51 PM
 */
package plugin.dicebag.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * <p>The model for the pluging -- basically a linked list of open
 * dice bags.  This is an <code>Observable</code> class that sends
 * out messages to observers (views).</p>
 *
 * @author Ross M. Lodge
 *
 */
public class DiceBagPluginModel extends Observable
{
	/** The currently active bag. */
	private DiceBagModel m_activeBag;

	/** A list of the open dice-bags */
	private List m_diceBags = new ArrayList();

	/**
	 * <p>Default (and only) constructor.  Creates an empty model.</p>
	 */
	public DiceBagPluginModel()
	{
		// Empty Constructor
	}

	/**
	 * <p>Sets the currently active bag.</p>
	 *
	 * @param model Model that's active
	 */
	public void setActiveBag(DiceBagModel model)
	{
		m_activeBag = model;
	}

	/**
	 * <p>Returns the currently active bag.</p>
	 *
	 * @return The currently active bag.
	 */
	public DiceBagModel getActiveBag()
	{
		return m_activeBag;
	}

	/**
	 * <p>Adds a new dice bag.  Generates a <code>DICE_BAG_ADDED</code> message.</p>
	 */
	public void addNewDicebag()
	{
		DiceBagModel bag = new DiceBagModel();
		m_diceBags.add(bag);
		setChanged();
		notifyObservers(new DiceBagMessage(DiceBagMessage.DICE_BAG_ADDED, bag));
	}

	/**
	 * <p>Closes the requested dice bag; generates a <code>DICE_BAG_REMOVED</code>
	 * message.</p>
	 *
	 * @param bag Bag to close
	 */
	public void closeDiceBag(DiceBagModel bag)
	{
		m_diceBags.remove(bag);
		setChanged();
		notifyObservers(new DiceBagMessage(DiceBagMessage.DICE_BAG_REMOVED, bag));
	}

	/**
	 * <p>Loads a dice bag from file.
	 * Generates a <code>DICE_BAG_ADDED</code> message.</p>
	 *
	 * @param f File to load
	 * @return TRUE or FALSE
	 */
	public boolean loadDiceBag(File f)
	{
		boolean returnValue = false;
		DiceBagModel bag = new DiceBagModel(f);
		returnValue = m_diceBags.add(bag);
		setChanged();
		notifyObservers(new DiceBagMessage(DiceBagMessage.DICE_BAG_ADDED, bag));

		return returnValue;
	}

	/**
	 * <p>Saves a dice bag to a file; generates the <code>DICE_BAG_SAVED</code>
	 * message.</p>
	 *
	 * @param bag bag to save
	 * @param f File to save to.
	 */
	public void saveDiceBag(DiceBagModel bag, File f)
	{
		bag.saveToFile(f);
		setChanged();
		notifyObservers(new DiceBagMessage(DiceBagMessage.DICE_BAG_SAVED, bag));
	}

	/**
	 * <p>Saves the specified bag to its current file.</p>
	 *
	 * @param bag Bag to save.
	 */
	public void saveDiceBag(DiceBagModel bag)
	{
		saveDiceBag(bag, new File(bag.getFilePath()));
	}
}
