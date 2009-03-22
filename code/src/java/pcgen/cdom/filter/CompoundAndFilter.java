/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.filter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import pcgen.cdom.base.ChoiceFilterUtilities;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrimitiveChoiceFilter;
import pcgen.core.PlayerCharacter;

public class CompoundAndFilter<T> implements PrimitiveChoiceFilter<T>
{

	private final Class<T> refClass;
	private final Set<PrimitiveChoiceFilter<T>> set = new HashSet<PrimitiveChoiceFilter<T>>();

	public CompoundAndFilter(Collection<PrimitiveChoiceFilter<T>> coll)
	{
		if (coll == null)
		{
			throw new IllegalArgumentException();
		}
		if (coll.isEmpty())
		{
			throw new IllegalArgumentException();
		}
		refClass = coll.iterator().next().getReferenceClass();
		set.addAll(coll);
	}

	public String getLSTformat()
	{
		return ChoiceFilterUtilities.joinLstFormat(set, Constants.PIPE);
	}

	public Class<T> getReferenceClass()
	{
		return refClass;
	}

	public boolean allow(PlayerCharacter pc, T obj)
	{
		for (PrimitiveChoiceFilter<T> cs : set)
		{
			if (!cs.allow(pc, obj))
			{
				return false;
			}
		}
		return true;
	}
}
