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
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * An ConditionalAbilityFacet is a DataFacet that contains information about
 * Ability objects that are contained in a PlayerCharacter because the PC did
 * pass prerequisites
 */
public class ConditionalAbilityFacet extends
		AbstractListFacet<CategorizedAbilitySelection>
{
	private PrerequisiteFacet prerequisiteFacet;

	@Override
	protected Collection<CategorizedAbilitySelection> getComponentSet()
	{
		return new ArrayList<CategorizedAbilitySelection>();
	}

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
