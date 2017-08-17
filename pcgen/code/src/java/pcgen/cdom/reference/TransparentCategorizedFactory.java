/*
 * Copyright 2010 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.reference;

import pcgen.cdom.base.Categorized;

public class TransparentCategorizedFactory<T extends Categorized<T>>
		implements ManufacturableFactory<T>
{

	private final Class<T> refClass;
	private final String category;

	public TransparentCategorizedFactory(Class<T> objClass, String categoryName)
	{
		if (objClass == null)
		{
			throw new IllegalArgumentException("Reference Class for "
					+ getClass().getName() + " cannot be null");
		}
		try
		{
			objClass.newInstance();
		}
		catch (InstantiationException e)
		{
			throw new IllegalArgumentException("Class for "
					+ getClass().getName()
					+ " must possess a zero-argument constructor", e);
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalArgumentException("Class for "
					+ getClass().getName()
					+ " must possess a public zero-argument constructor", e);
		}
		refClass = objClass;
		category = categoryName;
	}

	@Override
	public CDOMGroupRef<T> getAllReference()
	{
		return new CDOMTransparentAllRef<>(refClass);
	}

	@Override
	public CDOMGroupRef<T> getTypeReference(String... types)
	{
		return new CDOMTransparentTypeRef<>(refClass, types);
	}

	@Override
	public CDOMSingleRef<T> getReference(String key)
	{
		return new CDOMTransparentCategorizedSingleRef<>(refClass, category,
                key);
	}

	@Override
	public T newInstance()
	{
		throw new UnsupportedOperationException("Cannot construct a new object");
	}

	@Override
	public boolean isMember(T item)
	{
		return refClass.equals(item.getClass());
	}

	@Override
	public Class<T> getReferenceClass()
	{
		return refClass;
	}

	@Override
	public String getReferenceDescription()
	{
		return getReferenceClass().getSimpleName() + ' ' + category;
	}

	@Override
	public boolean resolve(ReferenceManufacturer<T> rm, String name,
			CDOMSingleRef<T> value, UnconstructedValidator validator)
	{
		throw new UnsupportedOperationException(
				"Resolution should not occur on Transparent object");
	}

	@Override
	public boolean populate(ReferenceManufacturer<T> parentCrm,
			ReferenceManufacturer<T> rm, UnconstructedValidator validator)
	{
		// No work to do?
		return true;
	}

	@Override
	public ManufacturableFactory<T> getParent()
	{
		throw new UnsupportedOperationException(
				"Resolution of Parent should not occur on Transparent object");
	}
}
