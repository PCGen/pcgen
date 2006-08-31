/*
 * Filterable.java
 * Copyright 2002 (C) Thomas Behr <ravenlock@gmx.de>
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
 * Created on February 9, 2002, 2:30 PM
 */
package pcgen.gui.filter;

import java.util.List;

import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;

/**
 * <code>Filterable</code>
 *
 * @author Thomas Behr
 * @version $Revision$
 */
public interface Filterable
{
	/**
	 * Selector
	 * @return List
	 */
	List<PObjectFilter> getAvailableFilters();

	/**
	 * sets the filter mode<br>
	 * recognized values are
	 * <ul>
	 * <li>    MATCH_ALL
	 * <li> MATCH_ALL_NEGATE
	 * <li> MATCH_ANY
	 * <li> MATCH_ANY_NEGATE
	 * </ul>
	 * as defined in the FilterConstants interface
	 *
	 * <br>author: Thomas Behr
	 *
	 * @param mode   the mode to be set
	 *
	 */
	void setFilterMode(int mode);

	/**
	 * returns the filter mode<br>
	 * possible values are
	 * <ul>
	 * <li>    MATCH_ALL
	 * <li> MATCH_ALL_NEGATE
	 * <li> MATCH_ANY
	 * <li> MATCH_ANY_NEGATE
	 * </ul>
	 * as defined in the FilterConstants interface
	 *
	 * <br>author: Thomas Behr
	 *
	 * @return the filter mode for this Filterable
	 */
	int getFilterMode();

	/**
	 * specifies whether the "match any" option should be available
	 * @return TRUE or FALSE
	 */
	boolean isMatchAnyEnabled();

	/**
	 * Selector
	 * (we need this one for saving filter settings)
	 * @return name
	 */
	String getName();

	/**
	 * specifies whether the "negate/reverse" option should be available
	 * @return TRUE if negate enabled
	 */
	boolean isNegateEnabled();

	/**
	 * Selector
	 * @return List of removed filters
	 */
	List<PObjectFilter> getRemovedFilters();

	/**
	 * Selector
	 * @return Listof selected filters
	 */
	List<PObjectFilter> getSelectedFilters();

	/**
	 * returns the filter seletion mode<br>
	 * possible values are
	 * <ul>
	 * <li>    DISABLED_MODE
	 * <li> DEMO_MODE
	 * <li> SINGLE_SINGLE_MODE
	 * <li> SINGLE_MULTI_MODE
	 * <li> MULTI_MULTI_MODE
	 * </ul>
	 * as defined in the FilterConstants interface
	 *
	 * <br>author: Thomas Behr
	 *
	 * @return the selection mode for this Filterable
	 */
	int getSelectionMode();

	/**
	 * initializes all available filters for this Filterable
	 *
	 * <br>author: Thomas Behr
	 */
	void initializeFilters();

	/**
	 * re-applies the selected filters;
	 * has to be called after changes to the filter selection
	 *
	 * <br>author: Thomas Behr
	 */
	void refreshFiltering();

	/**
	 * Apply all selected filters in the chosen mode for a specific PObject
	 *
	 * @param aPC The character the filtering is to be applied to.
	 * @param pObject - the PObject to test for filter acceptance
	 * @return TRUE or FALSE
	 */
	boolean accept(final PlayerCharacter aPC, final PObject pObject);
}
