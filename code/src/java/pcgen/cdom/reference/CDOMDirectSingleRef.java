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

import pcgen.cdom.base.CDOMObject;

public class CDOMDirectSingleRef<T extends CDOMObject> extends
		CDOMSingleRef<T>
{

	private final T referencedObject;

	public CDOMDirectSingleRef(T obj)
	{
		super((Class<T>) obj.getClass(), obj.getLSTformat());
		referencedObject = obj;
	}

	@Override
	public boolean contains(T obj)
	{
		return referencedObject.equals(obj);
	}

	@Override
	public T resolvesTo()
	{
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
		return referencedObject.getLSTformat();
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof CDOMDirectSingleRef
			&& referencedObject.equals(
				((CDOMDirectSingleRef<?>) o).referencedObject);
	}

	@Override
	public int hashCode()
	{
		return getReferenceClass().hashCode();
	}

	@Override
	public void addResolution(T obj)
	{
		throw new IllegalStateException("Cannot resolve a Direct Reference");
	}

	@Override
	public Collection<T> getContainedObjects()
	{
		return Collections.singleton(referencedObject);
	}

	public static <R extends CDOMObject> CDOMDirectSingleRef<R> getRef(R obj)
	{
		return new CDOMDirectSingleRef<R>(obj);
	}
}
