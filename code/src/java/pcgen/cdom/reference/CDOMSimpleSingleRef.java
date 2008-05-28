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

import java.util.Collection;
import java.util.Collections;

import pcgen.cdom.base.PrereqObject;

public class CDOMSimpleSingleRef<T extends PrereqObject> extends
		CDOMSingleRef<T>
{

	private T referencedObject = null;

	public CDOMSimpleSingleRef(Class<T> cl, String nm)
	{
		super(cl, nm);
	}

	@Override
	public boolean contains(T obj)
	{
		if (referencedObject == null)
		{
			throw new IllegalStateException(
				"Cannot ask for contains: Reference has not been resolved");
		}
		return referencedObject.equals(obj);
	}

	@Override
	public T resolvesTo()
	{
		if (referencedObject == null)
		{
			throw new IllegalStateException(
				"Cannot ask for resolution: Reference has not been resolved");
		}
		return referencedObject;
	}

	@Override
	public String getPrimitiveFormat()
	{
		return getName();
	}

	@Override
	public String getLSTformat()
	{
		return getName();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof CDOMSimpleSingleRef)
		{
			CDOMSimpleSingleRef<?> ref = (CDOMSimpleSingleRef<?>) o;
			return getReferenceClass().equals(ref.getReferenceClass())
				&& getName().equals(ref.getName());
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return getReferenceClass().hashCode() ^ getName().hashCode();
	}

	@Override
	public void addResolution(T obj)
	{
		if (referencedObject == null)
		{
			if (obj.getClass().equals(getReferenceClass()))
			{
				referencedObject = obj;
			}
			else
			{
				throw new IllegalArgumentException("Cannot resolve a "
					+ getReferenceClass().getSimpleName() + " Reference to a "
					+ obj.getClass().getSimpleName());
			}
		}
		else
		{
			throw new IllegalStateException(
				"Cannot resolve a Single Reference twice");
		}
	}

	@Override
	public Collection<T> getContainedObjects()
	{
		return Collections.singleton(referencedObject);
	}
}
