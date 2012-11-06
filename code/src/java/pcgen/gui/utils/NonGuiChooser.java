/*
 * NonGuiChooser.java
 * Copyright 2006 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 6/05/2006
 *
 * $Id:  $
 *
 */
package pcgen.gui.utils;

import java.util.ArrayList;
import java.util.List;

import pcgen.util.chooser.ChooserInterface;

/**
 * <code>NonGuiChooser</code> is quick fix for running chooser dependant code
 * in a non-GUI environment. It is assumed that this will only be created
 * when the answer is already known.
 *
 * Last Editor: $Author:  $
 * Last Edited: $Date:  $
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: $
 */
public final class NonGuiChooser implements ChooserInterface
{
	static final long serialVersionUID = -2148735105737308335L;

	/** The list of selected items */
	private List mSelectedList = new ArrayList();

	private int selectionsPerUnitCost = 1;
	
	private int totalSelectionsAvailable = 1;
	
	private int effectiveUsed = 0;
	
	private boolean pickAll = false;
	
	/**
	 * Chooser constructor.
	 */
	public NonGuiChooser()
	{
		// Do Nothing
	}

	/**
	 * Sets the AllowsDups attribute of the Chooser object
	 *
	 * @param aBool  The new AllowsDups value
	 */
    @Override
	public void setAllowsDups(boolean aBool)
	{
		// Do Nothing
	}

	/**
	 * Sets the AvailableList attribute of the Chooser object
	 *
	 * @param availableList  The new AvailableList value
	 */
    @Override
	public void setAvailableList(List availableList)
	{
		// Do Nothing
	}

	/**
	 * Sets the CostColumn attribute of the Chooser object
	 *
	 * @param costColumnNumber  The new CostColumnNumber value
	 */
    @Override
	public void setCostColumnNumber(final int costColumnNumber)
	{
		// Do Nothing
	}

	/**
	 * Sets the message text.
	 *
	 * @param argMessageText  java.lang.String
	 */
    @Override
	public void setMessageText(String argMessageText)
	{
		// Do Nothing
	}

	/**
	 * @see pcgen.util.chooser.ChooserInterface#setNegativeAllowed(boolean)
	 */
    @Override
	public void setNegativeAllowed(final boolean argFlag)
	{
		// Do Nothing
	}

	/**
	 * Sets the mPool attribute of the Chooser object.
	 *
	 * @param anInt  The new mPool value
	 */
	public void setPool(final int anInt)
	{
		//TODO Remove
	}

	/**
	 * Returns the mPool attribute of the Chooser object.
	 * @return mPool
	 */
    @Override
	public int getPool()
	{
		return getEffectivePool();
	}

	/**
	 * Sets the mPoolFlag attribute of the Chooser object
	 *
	 * @param poolFlag  The new PoolFlag value
	 */
    @Override
	public void setPoolFlag(boolean poolFlag)
	{
		// Do Nothing
	}

	/**
	 * Sets the SelectedList attribute of the Chooser object
	 *
	 * @param selectedList  The new SelectedList value
	 */
    @Override
	public void setSelectedList(List selectedList)
	{
		mSelectedList = selectedList;
		for (Object obj : mSelectedList)
		{
			//TODO need to update effectiveUsed more accurately than 1:1
			effectiveUsed++;
		}
	}

	/**
	 * Returns the selected item list
	 *
	 * @return   java.util.ArrayList
	 */
    @Override
	public List getSelectedList()
	{
		return new ArrayList(mSelectedList);
	}

	/**
	 * @see pcgen.util.chooser.ChooserInterface#setSelectedListTerminator(java.lang.String)
	 */
    @Override
	public void setSelectedListTerminator(String aString)
	{
		// Do Nothing
	}

	/**
	 * Sets the UniqueList attribute of the Chooser object
	 *
	 * @param uniqueList  The new UniqueList value
	 */
    @Override
	public void setUniqueList(List uniqueList)
	{
		// Do Nothing
	}

	/**
	 * Overrides the default setVisible method to ensure controls
	 * are updated before showing the dialog.
	 *
	 * @param b
	 */
    @Override
	public void setVisible(boolean b)
	{
		throw new UnsupportedOperationException(
			"NonGuiCHooser cannot be shown.");
	}

	/**
	 * @see pcgen.util.chooser.ChooserInterface#setTitle(java.lang.String)
	 */
    @Override
	public void setTitle(String title)
	{
		// Do Nothing
	}

	/**
	 * @see pcgen.util.chooser.ChooserInterface#show()
	 */
    @Override
	public void show()
	{
		setVisible(true);
	}

	/**
	 * @see pcgen.util.chooser.ChooserInterface#setAvailableColumnNames(java.util.List)
	 */
    @Override
	public void setAvailableColumnNames(List<String> availableColumnNames)
	{
		// Do Nothing
	}

    @Override
	public void setChoicesPerUnit(int cost)
	{
		selectionsPerUnitCost = cost;
	}

    @Override
	public void setTotalChoicesAvail(int avail)
	{
		totalSelectionsAvailable = avail;
	}

    @Override
	public void setPickAll(boolean b)
	{
		pickAll = b;
	}
	
    @Override
	public boolean pickAll()
	{
		return pickAll;
	}
	
	public int getEffectivePool()
	{
		return selectionsPerUnitCost * totalSelectionsAvailable
				- effectiveUsed;
	}
}
