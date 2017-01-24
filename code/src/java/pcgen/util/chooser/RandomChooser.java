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

import pcgen.base.util.RandomUtil;
import pcgen.facade.core.ChooserFacade;
import pcgen.facade.core.InfoFacade;
import pcgen.facade.util.ListFacade;

/**
 * An implementation of the Chooser Interface that does not display a GUI but
 * simply selects a random choice from the available list of options.
 *
 * @author    Aaron Divinsky
 */
public final class RandomChooser implements ChooserInterface, ChoiceHandler
{
	/** The list of available items */
	private List theAvailableList = new ArrayList();

	/** The list of selected items */
	private List theSelectedList = new ArrayList();

	/** The list of unique items */
	private List theUniqueList = new ArrayList();

	/** Whether or not to allow duplicate choices */
	private boolean theAllowDuplicatesFlag = false;

	private int selectionsPerUnitCost = 1;
	
	private int totalSelectionsAvailable = 1;

	private boolean pickAll = false;
	
	/**
	 * Chooser constructor.
	 */
	public RandomChooser()
	{
		//Empty constructor
	}

	/**
	 * Sets the AllowsDups attribute of the Chooser object
	 *
	 * @param aBool  The new AllowsDups value
	 */
    @Override
	public void setAllowsDups(boolean aBool)
	{
		theAllowDuplicatesFlag = aBool;
	}

	/**
	 * Sets the AvailableList attribute of the Chooser object
	 *
	 * @param availableList  The new AvailableList value
	 */
    @Override
	public void setAvailableList(List availableList)
	{
		theAvailableList = new ArrayList(availableList);
	}

	/**
	 * Sets the CostColumn attribute of the Chooser object
	 *
	 * @param costColumnNumber  The new CostColumnNumber value
	 */
    @Override
	public void setCostColumnNumber(final int costColumnNumber)
	{
	}

	/**
	 * Sets the message text.
	 *
	 * @param argMessageText  java.lang.String
	 */
    @Override
	public void setMessageText(String argMessageText)
	{
		//This is not used.
	}

    @Override
	public void setNegativeAllowed(final boolean argFlag)
	{
		// This is not used.
	}

	/**
	 * Returns the mPool attribute of the Chooser object.
	 * author Dmitry Jemerov
	 * @return mPool
	 */
    @Override
	public int getPool()
	{
		return totalSelectionsAvailable - theSelectedList.size();
	}

	/**
	 * Sets the mPoolFlag attribute of the Chooser object
	 *
	 * @param poolFlag  The new PoolFlag value
	 * author          Matt Woodard
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
	 * author              Matt Woodard
	 */
    @Override
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
    @Override
	public ArrayList getSelectedList()
	{
		return new ArrayList(theSelectedList);
	}

    @Override
	public void setSelectedListTerminator(String aString)
	{
		//This is not used.
	}

	/**
	 * Sets the UniqueList attribute of the Chooser object
	 *
	 * @param uniqueList  The new UniqueList value
	 * author            Matt Woodard
	 */
    @Override
	public void setUniqueList(List uniqueList)
	{
		theUniqueList = uniqueList;
	}

	/**
	 * This method is to allow the user to make the choices.  We will make
	 * our selections here.
	 * @param b true to show, false to hide
	 */
    @Override
	public void setVisible(boolean b)
	{
		while (getEffectivePool() > 0 && !theAvailableList.isEmpty())
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
    @Override
	public void setAvailableColumnNames(List<String> availableColumnNames)
	{
		//This is not used.
	}

	/**
	 * Selects an available item - invoked when the add button is pressed
	 */
	private void selectAvailable()
	{
		setMessageText(null);

		if (getEffectivePool() <= 0)
		{
			return;
		}

		final Object addObj =
				theAvailableList.get(RandomUtil.getRandomInt(theAvailableList
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
	}

    @Override
	public void setTitle(String title)
	{
		// Do nothing
	}

    @Override
	public void show()
	{
		// Do nothing
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
				- theSelectedList.size();
	}

	@Override
	public boolean makeChoice(ChooserFacade chooserFacade)
	{
		while (chooserFacade.getRemainingSelections().get() > 0
			&& !chooserFacade.getAvailableList().isEmpty())
		{
			ListFacade<InfoFacade> availableList = chooserFacade.getAvailableList();
			final InfoFacade addObj =
					availableList.getElementAt(RandomUtil
						.getRandomInt(availableList.getSize() - 1));
			chooserFacade.addSelected(addObj);
		}
		
		if (chooserFacade.getRemainingSelections().get() == 0
			|| !chooserFacade.isRequireCompleteSelection())
		{
			chooserFacade.commit();
			return true;
		}

		chooserFacade.rollback();
		return false;
	}
	
}
