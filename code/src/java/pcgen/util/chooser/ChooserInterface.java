/*
 * Copyright 2002 (C) Jonas Karlsson
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

import java.util.List;

/**
 * This interface for a dialog accepts a list of available items, a choice
 * limit, and some additional flags and switches. The user can
 * select and remove values until the required number of
 * choices have been made. The dialog is always modal, so a
 * call to show() will block program execution.
 *
 * TODO: Make these lists use generics.
 */
public interface ChooserInterface
{
	/**
	 * set allow duplicates flag
	 * @param aBool
	 */
	void setAllowsDups(boolean aBool);

	/**
	 * Set available list
	 * @param availableList
	 */
	void setAvailableList(List availableList);

	/**
	 * Set cost column number
	 * @param costColumnNumber
	 */
	void setCostColumnNumber(int costColumnNumber);

	/**
	 * Set message text
	 * @param messageText
	 */
	void setMessageText(String messageText);

	/**
	 * Set negative allowed flag
	 * @param argFlag
	 */
	void setNegativeAllowed(final boolean argFlag);

	/**
	 * Get pool
	 * @return pool
	 */
	int getPool();

	/**
	 * Set pool flag
	 * @param poolFlag
	 */
	void setPoolFlag(boolean poolFlag);

	/**
	 * set selected list
	 * @param selectedList
	 */
	void setSelectedList(List selectedList);

	/**
	 * Get selected list
	 * @return selected list
	 */
	List getSelectedList();

	/**
	 * Set selected list terminator
	 * @param aString
	 */
	void setSelectedListTerminator(String aString);

	/**
	 * Set title
	 * @param title
	 */
	void setTitle(String title);

	/**
	 * Set unique list
	 * @param uniqueList
	 */
	void setUniqueList(List uniqueList);

	/**
	 * Set visible flag
	 * @param b
	 */
	void setVisible(boolean b);

	/**
	 * Show
	 */
	void show();

	/**
	 * Set the list of available column names
	 * @param availableColumnNames
	 */
	public void setAvailableColumnNames(List<String> availableColumnNames);

	void setChoicesPerUnit(int choicesPerUnitCost);

	void setTotalChoicesAvail(int maxTotalChoices);

	void setPickAll(boolean b);
	
	boolean pickAll();
}
