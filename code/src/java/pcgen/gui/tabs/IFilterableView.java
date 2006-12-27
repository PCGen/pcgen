/*
 * IFilterableView.java
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
 *
 * Current Ver: $Revision$
 * Last Editor: $Author: $
 * Last Edited: $Date$
 */
package pcgen.gui.tabs;

import java.util.List;

/**
 * This interface defines the relationship between a filterable component and 
 * the <code>FilterPanel</code>.
 *
 * @author boomer70 <boomer70@yahoo.com>
 * 
 * @since 5.11.1
 */
public interface IFilterableView
{
	/**
	 * Return the list of view type choice strings.
	 * 
	 * @return A list of view type choices.
	 */
	List<String> getViewChoices();

	/**
	 * Return the index into the View Choices list to use as the default choice.
	 * 
	 * @return An index.  Must be >= 0 and less than getViewChoices().size().
	 */
	int getInitialChoice();

	/**
	 * This method is called when the user changes the View Choice.
	 * 
	 * @param newView The new view choice index.
	 */
	void viewChanged(final int newView);

	/**
	 * This method is called when the user sets a new Quick Filter string.
	 * 
	 * @param aFilter The filter string.
	 */
	void setQFilter(final String aFilter);

	/**
	 * This method is called when the Quick Filter is cleared.
	 */
	void clearQFilter();
}
