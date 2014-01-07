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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Category;
import pcgen.cdom.content.AbilitySelection;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.facet.base.AbstractSingleSourceListFacet;
import pcgen.cdom.helper.CategorizedAbilitySelection;
import pcgen.core.Ability;

/**
 * DirectAbilityInputFacet is a Facet that tracks the Abilities that are added
 * with indirect grants via %LIST that have been granted to a Player Character.
 * 
 * @author Thomas Parker (thpr [at] yahoo.com)
 */
public class DirectAbilityInputFacet
		extends
		AbstractSingleSourceListFacet<CategorizedAbilitySelection, DirectAbilityInputFacet.DAIFSource>
{
	public void add(CharID id, CDOMObject owner, Category<Ability> category,
		Nature nature, AbilitySelection as)
	{
		if (owner == null)
		{
			throw new IllegalArgumentException("Owner Object may not be null");
		}
		if (category == null)
		{
			throw new IllegalArgumentException("Category may not be null");
		}
		if (nature == null)
		{
			throw new IllegalArgumentException("Nature may not be null");
		}
		if (as == null)
		{
			throw new IllegalArgumentException(
				"AbilitySelection to add may not be null");
		}
		DAIFSource source = new DAIFSource(owner, category, nature, as);
		CategorizedAbilitySelection cas =
				new CategorizedAbilitySelection(owner, category,
					as.getObject(), nature, as.getSelection());
		add(id, cas, source);
	}

	public void remove(CharID id, CDOMObject owner, Category<Ability> category,
		Nature nature, AbilitySelection as)
	{
		if (owner == null)
		{
			throw new IllegalArgumentException("Owner Object may not be null");
		}
		if (category == null)
		{
			throw new IllegalArgumentException("Category may not be null");
		}
		if (nature == null)
		{
			throw new IllegalArgumentException("Nature may not be null");
		}
		if (as == null)
		{
			throw new IllegalArgumentException(
				"AbilitySelection to add may not be null");
		}
		removeOne(id, new DAIFSource(owner, category, nature, as));
	}

	protected class DAIFSource
	{

		private final CDOMObject owner;
		private final Category<Ability> category;
		private final Nature nature;
		private final AbilitySelection abSelection;

		public DAIFSource(CDOMObject owner, Category<Ability> category,
			Nature nature, AbilitySelection as)
		{
			this.owner = owner;
			this.category = category;
			this.nature = nature;
			this.abSelection = as;
		}

		@Override
		public int hashCode()
		{
			return System.identityHashCode(abSelection);
		}

		@Override
		public boolean equals(Object o)
		{
			if (o instanceof DAIFSource)
			{
				DAIFSource other = (DAIFSource) o;
				//Yes, instance identity on abSelection
				return (other.abSelection == abSelection)
					&& owner.equals(other.owner)
					&& category.equals(other.category)
					&& nature.equals(other.nature);
			}
			return false;
		}
	}
}
