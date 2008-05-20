/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.reference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrereqObject;

public final class CDOMAllRef<T extends PrereqObject> extends CDOMGroupRef<T>
{
	private List<T> referencedList = null;

	public CDOMAllRef(Class<T> cl)
	{
		super(cl, Constants.ALLREF_LST + ": " + cl.getSimpleName());
	}

	@Override
	public String getLSTformat()
	{
		return Constants.ALLREF_LST;
	}

	@Override
	public String getPrimitiveFormat()
	{
		return Constants.ALLREF_LST;
	}

	@Override
	public boolean contains(T obj)
	{
		if (referencedList == null)
		{
			throw new IllegalStateException(
				"Cannot ask for contains: Reference has not been resolved");
		}
		return referencedList.contains(obj);
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof CDOMAllRef
			&& getReferenceClass().equals(((CDOMAllRef) o).getReferenceClass());
	}

	@Override
	public int hashCode()
	{
		return getReferenceClass().hashCode();
	}

	@Override
	public void addResolution(T obj)
	{
		if (obj.getClass().equals(getReferenceClass()))
		{
			if (referencedList == null)
			{
				referencedList = new ArrayList<T>();
			}
			referencedList.add(obj);
		}
		else
		{
			throw new IllegalArgumentException("Cannot resolve a "
				+ getReferenceClass().getSimpleName() + " Reference to a "
				+ obj.getClass().getSimpleName());
		}
	}

	@Override
	public int getObjectCount()
	{
		return referencedList == null ? 0 : referencedList.size();
	}

	@Override
	public Collection<T> getContainedObjects()
	{
		return Collections.unmodifiableList(referencedList);
	}
}
