/*
 * Copyright 2006 (C) Tom Parker <thpr@sourceforge.net>
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
 * 
 * Created on October 29, 2006.
 * 
 * Current Ver: $Revision: 1111 $ Last Editor: $Author: boomer70 $ Last Edited:
 * $Date: 2006-06-22 21:22:44 -0400 (Thu, 22 Jun 2006) $
 */
package pcgen.cdom.choiceset;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.PlayerCharacter;
import pcgen.rules.persistence.TokenUtilities;

public class ReferenceChoiceSet<T extends PrereqObject> implements
		PrimitiveChoiceSet<T>
{

	private final Set<CDOMReference<T>> set;

	public ReferenceChoiceSet(Collection<? extends CDOMReference<T>> col)
	{
		super();
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
		set = new HashSet<CDOMReference<T>>(col);
	}

	public String getLSTformat()
	{
		Set<CDOMReference<?>> sortedSet = new TreeSet<CDOMReference<?>>(
				TokenUtilities.REFERENCE_SORTER);
		sortedSet.addAll(set);
		return ReferenceUtilities.joinLstFormat(sortedSet, Constants.COMMA);
	}

	public Class<T> getChoiceClass()
	{
		return set == null ? null : set.iterator().next().getReferenceClass();
	}

	public Set<T> getSet(PlayerCharacter pc)
	{
		Set<T> returnSet = new HashSet<T>();
		for (CDOMReference<T> ref : set)
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
		if (o instanceof ReferenceChoiceSet)
		{
			ReferenceChoiceSet<?> other = (ReferenceChoiceSet<?>) o;
			return set.equals(other.set);
		}
		return false;
	}
}
