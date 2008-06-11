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

import pcgen.cdom.base.PrereqObject;

public class CDOMTransparentAllRef<T extends PrereqObject> extends
		CDOMGroupRef<T> implements TransparentReference<T>
{

	private CDOMGroupRef<T> subReference = null;

	public CDOMTransparentAllRef(Class<T> cl)
	{
		super(cl, "ALL");
	}

	@Override
	public boolean contains(T obj)
	{
		if (subReference == null)
		{
			throw new IllegalStateException("Cannot ask for contains: "
					+ getReferenceClass().getName() + " Reference " + getName()
					+ " has not been resolved");
		}
		return subReference.contains(obj);
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
		if (o instanceof CDOMTransparentAllRef)
		{
			CDOMTransparentAllRef<?> ref = (CDOMTransparentAllRef<?>) o;
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
		throw new IllegalStateException(
				"Cannot resolve a Transparent Reference");
	}

	public void resolve(ReferenceManufacturer<T, ?> rm)
	{
		if (rm.getCDOMClass().equals(getReferenceClass()))
		{
			subReference = rm.getAllReference();
		}
		else
		{
			throw new IllegalArgumentException("Cannot resolve a "
					+ getReferenceClass().getSimpleName() + " Reference to a "
					+ rm.getCDOMClass().getSimpleName());
		}
	}

	@Override
	public Collection<T> getContainedObjects()
	{
		return subReference.getContainedObjects();
	}

	@Override
	public int getObjectCount()
	{
		return subReference == null ? 0 : subReference.getObjectCount();
	}
}
