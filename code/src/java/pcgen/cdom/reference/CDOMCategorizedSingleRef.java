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

import pcgen.cdom.base.CategorizedCDOMObject;
import pcgen.cdom.base.Category;
import pcgen.core.SourceEntry;

public class CDOMCategorizedSingleRef<T extends CategorizedCDOMObject<T>>
		extends CDOMSingleRef<T> implements CategorizedCDOMReference<T>
{

	private T referencedObject = null;

	private final Category<T> category;

	public CDOMCategorizedSingleRef(Class<T> cl, Category<T> cat, String nm)
	{
		super(cl, nm);
		if (cat == null)
		{
			throw new IllegalArgumentException(
					"Cannot built CDOMCategorizedSingleRef with null category");
		}
		category = cat;
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
	public String getLSTformat()
	{
		return getName();
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
	public boolean equals(Object o)
	{
		if (o instanceof CDOMCategorizedSingleRef)
		{
			CDOMCategorizedSingleRef<?> ref = (CDOMCategorizedSingleRef) o;
			return getReferenceClass().equals(ref.getReferenceClass())
				&& getName().equals(ref.getName())
				&& category.equals(ref.category);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return getReferenceClass().hashCode() ^ getName().hashCode();
	}

	public Category<T> getCDOMCategory()
	{
		return category;
	}

	public void setCDOMCategory(Category<T> cat)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void addResolution(T obj)
	{
		if (referencedObject != null)
		{
			throw new IllegalStateException(
				"Cannot resolve a Single Reference twice");
		}
		if (!obj.getClass().equals(getReferenceClass()))
		{
			throw new IllegalArgumentException("Cannot resolve a "
				+ getReferenceClass().getSimpleName() + " Reference to a "
				+ obj.getClass().getSimpleName());
		}
		if (!category.equals(obj.getCDOMCategory()))
		{
			throw new IllegalArgumentException("Cannot resolve a "
				+ getReferenceClass().getSimpleName() + " "
				+ obj.getCDOMCategory() + " Reference to category " + category);
		}
		referencedObject = obj;
	}

	public SourceEntry getSourceEntry()
	{
		throw new UnsupportedOperationException("Don't do this");
	}

	public String getDisplayName()
	{
		throw new UnsupportedOperationException("Don't do this");
	}

	public String getKeyName()
	{
		throw new UnsupportedOperationException("Don't do this");
	}

	public void setKeyName(String aKey)
	{
		throw new UnsupportedOperationException("Don't do this");
	}

	public void setName(String aName)
	{
		throw new UnsupportedOperationException("Don't do this");
	}

	@Override
	public Collection<T> getContainedObjects()
	{
		return Collections.singleton(referencedObject);
	}
}