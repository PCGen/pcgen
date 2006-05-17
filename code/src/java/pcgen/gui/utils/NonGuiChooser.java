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

	/** The choices remaining */
	private int mPool;

	/**
	 * Chooser constructor.
	 */
	public NonGuiChooser()
	{
	}

	/**
	 * Sets the AllowsDups attribute of the Chooser object
	 *
	 * @param aBool  The new AllowsDups value
	 */
	public void setAllowsDups(boolean aBool)
	{
	}

	/**
	 * Sets the AvailableList attribute of the Chooser object
	 *
	 * @param availableList  The new AvailableList value
	 */
	public void setAvailableList(List availableList)
	{
	}

	/**
	 * Sets the CostColumn attribute of the Chooser object
	 *
	 * @param costColumnNumber  The new CostColumnNumber value
	 */
	public void setCostColumnNumber(final int costColumnNumber)
	{
	}

	/**
	 * Sets the message text.
	 *
	 * @param argMessageText  java.lang.String
	 */
	public void setMessageText(String argMessageText)
	{
	}

	/**
	 * @see pcgen.util.chooser.ChooserInterface#setNegativeAllowed(boolean)
	 */
	public void setNegativeAllowed(final boolean argFlag)
	{
	}

	/**
	 * Sets the mPool attribute of the Chooser object.
	 *
	 * @param anInt  The new mPool value
	 */
	public void setPool(final int anInt)
	{
		mPool = anInt;
	}

	/**
	 * Returns the mPool attribute of the Chooser object.
	 * @return mPool
	 */
	public int getPool()
	{
		return mPool;
	}

	/**
	 * Sets the mPoolFlag attribute of the Chooser object
	 *
	 * @param poolFlag  The new PoolFlag value
	 */
	public void setPoolFlag(boolean poolFlag)
	{
	}

	/**
	 * Sets the SelectedList attribute of the Chooser object
	 *
	 * @param selectedList  The new SelectedList value
	 */
	public void setSelectedList(List selectedList)
	{
		mSelectedList = selectedList;
	}

	/**
	 * Returns the selected item list
	 *
	 * @return   java.util.ArrayList
	 */
	public ArrayList getSelectedList()
	{
		return new ArrayList(mSelectedList);
	}

	/**
	 * @see pcgen.util.chooser.ChooserInterface#setSelectedListTerminator(java.lang.String)
	 */
	public void setSelectedListTerminator(String aString)
	{
	}

	/**
	 * Sets the UniqueList attribute of the Chooser object
	 *
	 * @param uniqueList  The new UniqueList value
	 */
	public void setUniqueList(List uniqueList)
	{
	}

	/**
	 * Overrides the default setVisible method to ensure controls
	 * are updated before showing the dialog.
	 *
	 * @param b
	 */
	public void setVisible(boolean b)
	{
		throw new UnsupportedOperationException("NonGuiCHooser cannot be shown.");
	}

	/**
	 * @see pcgen.util.chooser.ChooserInterface#setTitle(java.lang.String)
	 */
	public void setTitle(String title)
	{
	}

	/**
	 * @see pcgen.util.chooser.ChooserInterface#show()
	 */
	public void show()
	{
		setVisible(true);
	}

	/**
	 * @see pcgen.util.chooser.ChooserInterface#setAvailableColumnNames(java.util.List)
	 */
	public void setAvailableColumnNames(List availableColumnNames)
	{
	}
}
