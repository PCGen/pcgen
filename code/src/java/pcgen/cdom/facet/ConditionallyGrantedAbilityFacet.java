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
import pcgen.cdom.helper.CategorizedAbilitySelection;

/**
 * ConditionallyGrantedAbilityFacet is a DataFacet that contains information
 * about Ability objects that are contained in a Player Character because the
 * Player Character did pass prerequisites for the conditional Ability.
 * 
 * @author Thomas Parker (thpr [at] yahoo.com)
 */
public class ConditionallyGrantedAbilityFacet extends
		AbstractListFacet<CategorizedAbilitySelection>
{

	private ConditionalAbilityFacet conditionalAbilityFacet;

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
		Collection<CategorizedAbilitySelection> current = getSet(id);
		Collection<CategorizedAbilitySelection> qualified = conditionalAbilityFacet
				.getQualifiedSet(id);
		List<CategorizedAbilitySelection> toRemove = new ArrayList<CategorizedAbilitySelection>(
				current);
		toRemove.removeAll(qualified);
		List<CategorizedAbilitySelection> toAdd = new ArrayList<CategorizedAbilitySelection>(
				qualified);
		toAdd.removeAll(current);
		for (CategorizedAbilitySelection cas : toRemove)
		{
			remove(id, cas);
		}
		for (CategorizedAbilitySelection cas : toAdd)
		{
			add(id, cas);
		}
	}

	/**
	 * Overrides the default behavior of AbstractListFacet, since we need to
	 * ensure we are storing the conditionally granted abilities by their
	 * identity (Ability has old behavior in .equals and Abilities are still
	 * cloned)
	 * 
	 * @see pcgen.cdom.facet.AbstractListFacet#getComponentSet()
	 */
	@Override
	protected Set<CategorizedAbilitySelection> getComponentSet()
	{
		return new WrappedMapSet<CategorizedAbilitySelection>(
				IdentityHashMap.class);
	}

	public void setConditionalAbilityFacet(
		ConditionalAbilityFacet conditionalAbilityFacet)
	{
		this.conditionalAbilityFacet = conditionalAbilityFacet;
	}
}