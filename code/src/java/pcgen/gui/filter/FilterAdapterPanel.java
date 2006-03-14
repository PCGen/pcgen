/*
 * FilterAdapterPanel.java
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

import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;

import javax.swing.JPanel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <code>FilterAdapterPanel</code>
 *
 * @author Thomas Behr
 * @version $Revision$
 */
public abstract class FilterAdapterPanel extends JPanel implements Filterable
{
	private List availableFilters = new ArrayList(0);
	private List removedFilters = new ArrayList(0);
	private List selectedFilters = new ArrayList(0);
	private String kitFilter = "";
	private int filterMode = FilterConstants.MATCH_ALL;

	/**
	 * Selector<br>
	 * implementation of Filterable interface
	 *
	 * <br>author: Thomas Behr
	 *
	 * @return a list with the available filters for this Filterable
	 */
	public final List getAvailableFilters()
	{
		return availableFilters;
	}

	/**
	 * sets the filter mode<br>
	 * implementation of Filterable interface
	 *
	 * <br>author: Thomas Behr
	 *
	 * @param mode   the mode to be set
	 */
	public final void setFilterMode(int mode)
	{
		filterMode = mode;
	}

	/**
	 * returns the filter mode<br>
	 * implementation of Filterable interface
	 *
	 * <br>author: Thomas Behr
	 *
	 * @return the filter mode
	 */
	public final int getFilterMode()
	{
		return filterMode;
	}

	/**
	 * Selector
	 * implementation of Filterable interface
	 *
	 * <br>author: Thomas Behr
	 *
	 * @return a list with the removed filters for this Filterable
	 */
	public final List getRemovedFilters()
	{
		return removedFilters;
	}

	/**
	 * Selector
	 * implementation of Filterable interface
	 *
	 * <br>author: Thomas Behr
	 *
	 * @return a list with the selected filters for this Filterable
	 */
	public final List getSelectedFilters()
	{
		return selectedFilters;
	}

	/**
	 * convenience method<br>
	 * adds a filter to the list of available filters for this Filterable
	 *
	 * <br>author: Thomas Behr
	 *
	 * @param filter   the filter to be registered
	 */
	public final void registerFilter(PObjectFilter filter)
	{
		if (filter != null)
		{
			availableFilters.add(filter);
		}
	}

	/**
	 * specifies whether the "match any" option should be available<br>
	 * implementation of Filterable interface
	 *
	 * <br>author: Thomas Behr
	 *
	 * @return <code>true</code>, if "match any" option is available;<br>
	 *         <code>false</code>, otherwise
	 */
	public boolean isMatchAnyEnabled()
	{
		return false;
	}

	/**
	 * specifies whether the "negate/reverse" option should be available<br>
	 * implementation of Filterable interface
	 *
	 * <br>author: Thomas Behr
	 *
	 * @return <code>true</code>, if "negate/reverse" option is available;<br>
	 *         <code>false</code>, otherwise
	 */
	public boolean isNegateEnabled()
	{
		return false;
	}

	/**
	 * returns the selection mode<br>
	 * implementation of Filterable interface
	 *
	 * <br>author: Thomas Behr
	 *
	 * @return the selection mode for this Filterable
	 */
	public int getSelectionMode()
	{
		return FilterConstants.DEMO_MODE;
	}

	/**
	 * initializes all available filters for this Filterable<br>
	 * implementation of Filterable interface
	 *
	 * <br>author: Thomas Behr
	 */
	public abstract void initializeFilters();

	/**
	 * re-applies the selected filters;
	 * has to be called after changes to the filter selection<br>
	 * implementation of Filterable interface
	 *
	 * <br>author: Thomas Behr
	 */
	public abstract void refreshFiltering();

	/**
	 * Set kit filter
	 * @param argKitFilter
	 */
	public final void setKitFilter(String argKitFilter)
	{
		kitFilter = argKitFilter;
	}

	/**
	 * Get Kit Filter
	 * @return kit filter
	 */
	public final String getKitFilter()
	{
		return kitFilter;
	}

	/**
	 * apply all selected filters in the chosen mode for a specific PObject
	 *
	 * <br>author: Thomas Behr 10-02-02
	 * @param aPC The character the filtering is to be applied to.
	 * @param pObject - the PObject to test for filter acceptance
	 * @return TRUE or FALSE
	 */
	protected final boolean accept(PlayerCharacter aPC, PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		final int mode = getFilterMode();
		PObjectFilter filter;

		for (Iterator it = getSelectedFilters().iterator(); it.hasNext();)
		{
			filter = (PObjectFilter) it.next();

			if ((mode == FilterConstants.MATCH_ALL) && !filter.accept(aPC, pObject))
			{
				return false;
			}
			else if ((mode == FilterConstants.MATCH_ALL_NEGATE) && !filter.accept(aPC, pObject))
			{
				return true;
			}
			else if ((mode == FilterConstants.MATCH_ANY) && filter.accept(aPC, pObject))
			{
				return true;
			}
			else if ((mode == FilterConstants.MATCH_ANY_NEGATE) && filter.accept(aPC, pObject))
			{
				return false;
			}
		}

		if ((mode == FilterConstants.MATCH_ALL) || (mode == FilterConstants.MATCH_ANY_NEGATE))
		{
			return true;
		}
		else if ((mode == FilterConstants.MATCH_ANY) || (mode == FilterConstants.MATCH_ALL_NEGATE))
		{
			// if no filters at all are selected, we accept of course
			return getSelectedFilters().size() == 0;
		}

		return true;
	}
}
