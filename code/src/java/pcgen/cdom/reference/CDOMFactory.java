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

import pcgen.base.lang.UnreachableError;
import pcgen.cdom.base.Loadable;
import pcgen.util.Logging;

public class CDOMFactory<T extends Loadable> implements
		ManufacturableFactory<T>
{

	private final Class<T> refClass;

	public CDOMFactory(Class<T> objClass)
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
	}

	@Override
	public CDOMGroupRef<T> getAllReference()
	{
		return new CDOMAllRef<T>(refClass);
	}

	@Override
	public CDOMGroupRef<T> getTypeReference(String... types)
	{
		return new CDOMTypeRef<T>(refClass, types);
	}

	@Override
	public CDOMSingleRef<T> getReference(String key)
	{
		return new CDOMSimpleSingleRef<T>(refClass, key);
	}

	@Override
	public T newInstance()
	{
		try
		{
			return refClass.newInstance();
		}
		catch (InstantiationException e)
		{
			throw new UnreachableError("Class was tested at "
				+ "construction to ensure it had a public, "
				+ "zero-argument constructor");
		}
		catch (IllegalAccessException e)
		{
			throw new UnreachableError("Class was tested at "
				+ "construction to ensure it had a public, "
				+ "zero-argument constructor");
		}
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
		return refClass.getSimpleName();
	}

	@Override
	public boolean resolve(ReferenceManufacturer<T> rm, String name,
			CDOMSingleRef<T> value, UnconstructedValidator validator)
	{
		boolean returnGood = true;
		T activeObj = rm.getObject(name);
		if (activeObj == null)
		{
			// Wasn't constructed!
			if (name.charAt(0) != '*' && !report(validator, name))
			{
				Logging.errorPrint("Unconstructed Reference: "
						+ getReferenceDescription() + " " + name);
				rm.fireUnconstuctedEvent(value);
				returnGood = false;
			}
			activeObj = rm.buildObject(name);
		}
		value.addResolution(activeObj);
		return returnGood;
	}

	private boolean report(UnconstructedValidator validator, String key)
	{
		return validator != null && validator.allow(getReferenceClass(), key);
	}

	@Override
	public boolean populate(ReferenceManufacturer<T> parentCrm,
			ReferenceManufacturer<T> rm, UnconstructedValidator validator)
	{
		// Nothing to do
		return true;
	}

	@Override
	public ManufacturableFactory<T> getParent()
	{
		return null;
	}

}
