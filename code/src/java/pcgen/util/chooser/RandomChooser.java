/*
 * RandomChooser.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 */
package pcgen.util.chooser;

import java.util.ArrayList;
import java.util.List;

import pcgen.core.Globals;

/**
 * An implementation of the Chooser Interface that does not display a GUI but
 * simply selects a random choice from the available list of options.
 *
 * @author    Aaron Divinsky
 * @version $Revision$
 */
public final class RandomChooser implements ChooserInterface
{
	/** The list of available items */
	private List theAvailableList = new ArrayList();

	/** The list of selected items */
	private List theSelectedList = new ArrayList();

	/** The list of unique items */
	private List theUniqueList = new ArrayList();

	/** Whether or not to allow duplicate choices */
	private boolean theAllowDuplicatesFlag = false;

	/** Whether or not to force mPool=0 when closing */
	// TODO - Should this be used?
	private boolean theZeroPoolFlag = true;

	/** The column containing the cost for an item */
	private int theCostColumnNumber = -1;

	/** The choices remaining */
	private int thePool;

	/**
	 * Chooser constructor.
	 */
	public RandomChooser()
	{
	}

	/**
	 * Sets the AllowsDups attribute of the Chooser object
	 *
	 * @param aBool  The new AllowsDups value
	 */
	public void setAllowsDups(boolean aBool)
	{
		theAllowDuplicatesFlag = aBool;
	}

	/**
	 * Sets the AvailableList attribute of the Chooser object
	 *
	 * @param availableList  The new AvailableList value
	 */
	public void setAvailableList(List availableList)
	{
		theAvailableList = new ArrayList(availableList);
	}

	/**
	 * Sets the CostColumn attribute of the Chooser object
	 *
	 * @param costColumnNumber  The new CostColumnNumber value
	 */
	public void setCostColumnNumber(final int costColumnNumber)
	{
		theCostColumnNumber = costColumnNumber;
	}

	/**
	 * Sets the message text.
	 *
	 * @param argMessageText  java.lang.String
	 */
	public void setMessageText(String argMessageText)
	{
	}

	public void setNegativeAllowed(final boolean argFlag)
	{
		// This is not used.
	}

	/**
	 * Sets the mPool attribute of the Chooser object.
	 *
	 * @param anInt  The new mPool value
	 * author       Matt Woodard
	 */
	public void setPool(final int anInt)
	{
		thePool = anInt;
	}

	/**
	 * Returns the mPool attribute of the Chooser object.
	 * author Dmitry Jemerov
	 * @return mPool
	 */
	public int getPool()
	{
		return thePool;
	}

	/**
	 * Sets the mPoolFlag attribute of the Chooser object
	 *
	 * @param poolFlag  The new PoolFlag value
	 * author          Matt Woodard
	 */
	public void setPoolFlag(boolean poolFlag)
	{
		theZeroPoolFlag = poolFlag;
	}

	/**
	 * Sets the SelectedList attribute of the Chooser object
	 *
	 * @param selectedList  The new SelectedList value
	 * author              Matt Woodard
	 */
	public void setSelectedList(List selectedList)
	{
		theSelectedList.addAll(selectedList);
	}

	/**
	 * Returns the selected item list
	 *
	 * @return   java.util.ArrayList
	 * author   Matt Woodard
	 */
	public ArrayList getSelectedList()
	{
		return new ArrayList(theSelectedList);
	}

	public void setSelectedListTerminator(String aString)
	{
	}

	/**
	 * Sets the UniqueList attribute of the Chooser object
	 *
	 * @param uniqueList  The new UniqueList value
	 * author            Matt Woodard
	 */
	public void setUniqueList(List uniqueList)
	{
		theUniqueList = uniqueList;
	}

	/**
	 * This method is to allow the user to make the choices.  We will make
	 * our selections here.
	 * @param b true to show, false to hide
	 */
	public void setVisible(boolean b)
	{
		while (thePool > 0)
		{
			selectAvailable();
		}
	}

	/**
	 * Sets the available column name list
	 *
	 * @param availableColumnNames  The new AvailableColumnNames value
	 * author                      Matt Woodard
	 */
	public void setAvailableColumnNames(List<String> availableColumnNames)
	{
	}

	/**
	 * Sets the selected column name list
	 * @param selectedColumnNames  java.util.List
	 * author                     Matt Woodard
	 */
	public void setSelectedColumnNames(List selectedColumnNames)
	{
	}

	/**
	 * Selects an available item - invoked when the add button is pressed
	 */
	private void selectAvailable()
	{
		setMessageText(null);

		if (getPool() <= 0)
		{
			return;
		}

		final Object addObj =
				theAvailableList.get(Globals.getRandomInt(theAvailableList
					.size() - 1));
		if (theUniqueList.contains(addObj))
		{
			return;
		}

		for (int i = 0, count = theSelectedList.size(); i < count; i++)
		{
			final Object anObj = theSelectedList.get(i);

			if (addObj.equals(anObj) && !theAllowDuplicatesFlag)
			{
				return;
			}
		}

		//
		// Make sure there are enough points remaining...
		//
		//		final String[] fields = addString.split("\t");
		//
		//		int adjustment = 1;
		//		if (fields.length > 1)b
		//		{
		//			adjustment = Integer.parseInt(fields[theCostColumnNumber]);
		//		}
		//		if ((getPool() - adjustment) < 0)
		//		{
		//			if (!canGoNegative)
		//			{
		//				return;
		//			}
		//		}

		theSelectedList.add(addObj);

		//		setPool(getPool() - adjustment);
		setPool(getPool() - 1);
	}

	public void setTitle(String title)
	{
		// Do nothing
	}

	public void show()
	{
		// Do nothing
	}
}
