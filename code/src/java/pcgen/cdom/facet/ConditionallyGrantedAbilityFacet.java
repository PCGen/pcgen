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
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * An ConditionalAbilityFacet is a DataFacet that contains information about
 * Ability objects that are contained in a PlayerCharacter because the PC did
 * pass prerequisites
 */
public class ConditionallyGrantedAbilityFacet extends
		AbstractListFacet<CategorizedAbilitySelection>
{

	private ConditionalAbilityFacet conditionalAbilityFacet;

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