/*
 * Copyright (c) Thomas Parker, 2010.
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
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import pcgen.base.util.WrappedMapSet;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractListFacet;
import pcgen.cdom.helper.CNAbilitySelection;
import pcgen.util.Logging;

/**
 * ConditionallyGrantedAbilityFacet is a DataFacet that contains information
 * about Ability objects that are contained in a Player Character because the
 * Player Character did pass prerequisites for the conditional Ability.
 * 
 */
public class ConditionallyGrantedAbilityFacet extends
		AbstractListFacet<CharID, CNAbilitySelection>
{

	private ConditionalAbilityFacet conditionalAbilityFacet;
	
	/** Best guess of the current recursion level for debugging purposes only.*/
	private static int depth = 0;

	/**
	 * Performs a global update of conditionally granted Abilities for a Player
	 * Character.
	 * 
	 * @param id
	 *            The CharID identifying the Player Character for which a global
	 *            update of conditionally granted Abilities should be performed.
	 */
	public void update(CharID id)
	{
		depth++;
		Collection<CNAbilitySelection> current = getSet(id);
		Collection<CNAbilitySelection> qualified = conditionalAbilityFacet
				.getQualifiedSet(id);
		List<CNAbilitySelection> toRemove = new ArrayList<>(
                current);
		toRemove.removeAll(qualified);
		List<CNAbilitySelection> toAdd = new ArrayList<>(
                qualified);
		toAdd.removeAll(current);
		if (!toAdd.isEmpty() || !toRemove.isEmpty())
		{
			if (Logging.isDebugMode())
			{
				Logging.debugPrint("CGAF at depth " + depth + " removing "
						+ toRemove + " adding " + toAdd);
			}
		}
		for (CNAbilitySelection cas : toRemove)
		{
			// Things could have changed, so we make sure
			if (!conditionalAbilityFacet.isQualified(id, cas) && contains(id, cas))
			{
				if (Logging.isDebugMode())
				{
					Logging.debugPrint("CGAF at depth " + depth + " removing "
						+ cas);
				}
				remove(id, cas);
			}
		}
		for (CNAbilitySelection cas : toAdd)
		{
			// Things could have changed, so we make sure
			if (conditionalAbilityFacet.isQualified(id, cas) && !contains(id, cas))
			{
				if (Logging.isDebugMode())
				{
					Logging.debugPrint("CGAF at depth " + depth + " adding "
						+ cas);
				}
				add(id, cas);
			}
		}

		if (!toAdd.isEmpty() || !toRemove.isEmpty())
		{
			if (Logging.isDebugMode())
			{
				Logging.debugPrint("CGAF at depth " + depth + " completed.");
			}
		}
		depth--;
	}

	/**
	 * Overrides the default behavior of AbstractListFacet, since we need to
	 * ensure we are storing the conditionally granted abilities by their
	 * identity (Ability has old behavior in .equals and Abilities are still
	 * cloned)
	 * 
	 * @see pcgen.cdom.facet.base.AbstractListFacet#getComponentSet()
	 */
	@Override
	protected Set<CNAbilitySelection> getComponentSet()
	{
		return new WrappedMapSet<>(
                IdentityHashMap.class);
	}

	public void setConditionalAbilityFacet(
		ConditionalAbilityFacet conditionalAbilityFacet)
	{
		this.conditionalAbilityFacet = conditionalAbilityFacet;
	}
}
