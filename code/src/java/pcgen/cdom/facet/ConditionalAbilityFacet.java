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
import java.util.List;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.helper.CategorizedAbilitySelection;

/**
 * ConditionalAbilityFacet is a DataFacet that contains information about
 * conditionally granted Ability objects that are contained in a Player
 * Character. All conditionally granted abilities (regardless of whether they
 * are granted to the Player Character) are stored here.
 * ConditionallyGrantedAbilityFacet performs the calculation to determine which
 * are active / granted to the Player Character.
 * 
 * @author Thomas Parker (thpr [at] yahoo.com)
 */
public class ConditionalAbilityFacet extends
		AbstractListFacet<CategorizedAbilitySelection>
{
	private PrerequisiteFacet prerequisiteFacet;

	/**
	 * Overrides the default behavior of AbstractListFacet, since we need to
	 * ensure we are storing the conditionally granted abilities as a raw list
	 * (can appear more than once) rather than a set.
	 * 
	 * @see pcgen.cdom.facet.AbstractListFacet#getComponentSet()
	 */
	@Override
	protected Collection<CategorizedAbilitySelection> getComponentSet()
	{
		return new ArrayList<CategorizedAbilitySelection>();
	}

	/**
	 * Returns a non-null copy of the Set of objects the character qualifies for
	 * in this ConditionalAbilityFacet for the Player Character represented by
	 * the given CharID. This method returns an empty Collection if the Player
	 * Character identified by the given CharID qualifies for none of the
	 * objects in this ConditionalAbilityFacet.
	 * 
	 * This method is value-semantic in that ownership of the returned
	 * Collection is transferred to the class calling this method. Modification
	 * of the returned Collection will not modify this ConditionalAbilityFacet
	 * and modification of this ConditionalAbilityFacet will not modify the
	 * returned Collection. Modifications to the returned Collection will also
	 * not modify any future or previous objects returned by this (or other)
	 * methods on ConditionalAbilityFacet. If you wish to modify the information
	 * stored in this ConditionalAbilityFacet, you must use the add*() and
	 * remove*() methods of ConditionalAbilityFacet.
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            items in this AbstractQualifiedListFacet should be returned.
	 * @return A non-null Set of objects the Player Character represented by the
	 *         given CharID qualifies for in this AbstractQualifiedListFacet
	 */
	public Collection<CategorizedAbilitySelection> getQualifiedSet(CharID id)
	{
		List<CategorizedAbilitySelection> set = new ArrayList<CategorizedAbilitySelection>();
		Collection<CategorizedAbilitySelection> cached = getCachedSet(id);
		if (cached != null)
		{
			for (CategorizedAbilitySelection cas : cached)
			{
				if (prerequisiteFacet.qualifies(id, cas, cas.getSource()))
				{
					set.add(cas);
				}
			}
		}
		return set;
	}

	/**
	 * Removes all information for the given source from this
	 * ConditionalAbilityFacet for the PlayerCharacter represented by the given
	 * CharID.
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

	public void setPrerequisiteFacet(PrerequisiteFacet prerequisiteFacet)
	{
		this.prerequisiteFacet = prerequisiteFacet;
	}

}
