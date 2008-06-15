/*
 * Copyright 2007, 2008 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.choiceset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import pcgen.cdom.base.CDOMListObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.list.DomainSpellList;
import pcgen.core.PlayerCharacter;
import pcgen.core.spell.Spell;
import pcgen.rules.persistence.TokenUtilities;

public class SpellReferenceChoiceSet implements PrimitiveChoiceSet<CDOMListObject<Spell>>
{
	private final Set<CDOMReference<? extends CDOMListObject<Spell>>> set;

	public SpellReferenceChoiceSet(Collection<CDOMReference<? extends CDOMListObject<Spell>>> col)
	{
		if (col == null)
		{
			throw new IllegalArgumentException(
					"Choice Collection cannot be null");
		}
		if (col.isEmpty())
		{
			throw new IllegalArgumentException(
					"Choice Collection cannot be empty");
		}
		set = new HashSet<CDOMReference<? extends CDOMListObject<Spell>>>(col);
	}

	public String getLSTformat()
	{
		Set<CDOMReference<?>> sortedSet = new TreeSet<CDOMReference<?>>(
				TokenUtilities.REFERENCE_SORTER);
		sortedSet.addAll(set);
		StringBuilder sb = new StringBuilder();
		List<CDOMReference<?>> domainList = new ArrayList<CDOMReference<?>>();
		boolean needComma = false;
		for (CDOMReference<?> ref : sortedSet)
		{
			if (DomainSpellList.class.equals(ref.getReferenceClass()))
			{
				domainList.add(ref);
			}
			else
			{
				if (needComma)
				{
					sb.append(Constants.COMMA);
				}
				sb.append(ref.getLSTformat());
				needComma = true;
			}
		}
		for (CDOMReference<?> ref : domainList)
		{
			if (needComma)
			{
				sb.append(Constants.COMMA);
			}
			sb.append("DOMAIN.");
			sb.append(ref.getLSTformat());
			needComma = true;
		}
		return sb.toString();
	}

	public Class<CDOMListObject> getChoiceClass()
	{
		return CDOMListObject.class;
	}

	public Set<CDOMListObject<Spell>> getSet(PlayerCharacter pc)
	{
		Set<CDOMListObject<Spell>> returnSet = new HashSet<CDOMListObject<Spell>>();
		for (CDOMReference<? extends CDOMListObject<Spell>> ref : set)
		{
			returnSet.addAll(ref.getContainedObjects());
		}
		return returnSet;
	}

	@Override
	public int hashCode()
	{
		return set.size();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}
		if (o instanceof SpellReferenceChoiceSet)
		{
			SpellReferenceChoiceSet other = (SpellReferenceChoiceSet) o;
			return set.equals(other.set);
		}
		return false;
	}

}
