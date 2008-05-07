/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.base;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import pcgen.cdom.enumeration.AssociationKey;

public class AssociationSupport implements AssociatedObject
{
	private Map<AssociationKey<?>, Object> associationMap;

	public <T> void setAssociation(AssociationKey<T> name, T value)
	{
		if (associationMap == null)
		{
			associationMap = new HashMap<AssociationKey<?>, Object>();
		}
		associationMap.put(name, value);
	}

	public <T> T getAssociation(AssociationKey<T> name)
	{
		return (T) (associationMap == null ? null : associationMap.get(name));
	}

	public Collection<AssociationKey<?>> getAssociationKeys()
	{
		return new HashSet<AssociationKey<?>>(associationMap.keySet());
	}

	public boolean hasAssociations()
	{
		return associationMap != null && !associationMap.isEmpty();
	}

	@Override
	public int hashCode()
	{
		return associationMap == null ? 0 : associationMap.size();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}
		if (o instanceof AssociationSupport)
		{
			AssociationSupport other = (AssociationSupport) o;
			if (associationMap == null || associationMap.isEmpty())
			{
				return other.associationMap == null
					|| other.associationMap.isEmpty();
			}
			return associationMap.equals(other.associationMap);
		}
		return false;
	}
}
