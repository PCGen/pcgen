/*
 * Copyright (c) Thomas Parker, 2009.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.facet;

import java.util.ArrayList;
import java.util.Collection;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.helper.CategorizedAbilitySelection;

/**
 * DirectAbilityFacet is a Facet that tracks the CategorizedAbilitySelection
 * that have been granted to a Player Character.
 * 
 * @author Thomas Parker (thpr [at] yahoo.com)
 */
public class DirectAbilityFacet extends
		AbstractListFacet<CategorizedAbilitySelection>
{

	/**
	 * Overrides the default behavior of AbstractListFacet, since we need to
	 * ensure we are storing a full list of the conditionally granted abilities
	 * (allowing duplicates).
	 * 
	 * @see pcgen.cdom.facet.AbstractListFacet#getComponentSet()
	 */
	@Override
	protected Collection<CategorizedAbilitySelection> getComponentSet()
	{
		return new ArrayList<CategorizedAbilitySelection>();
	}

	/**
	 * Removes all information for the given source from this DirectAbilityFacet
	 * for the PlayerCharacter represented by the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which items
	 *            from the given source will be removed
	 * @param source
	 *            The source for the objects to be removed from the list of
	 *            items stored for the Player Character identified by the given
	 *            CharID
	 */
	public void removeAllFromSource(CharID id, Object source)
	{
		Collection<CategorizedAbilitySelection> cached = getCachedSet(id);
		if (cached != null)
		{
			for (CategorizedAbilitySelection cas : new ArrayList<CategorizedAbilitySelection>(
					cached))
			{
				if (cas.getSource().equals(source))
				{
					remove(id, cas);
				}
			}
		}
	}
}
