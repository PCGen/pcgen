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
package pcgen.rules.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import pcgen.base.util.DoubleKeyMap;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CategorizedCDOMObject;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.Identified;
import pcgen.cdom.reference.CategorizedManufacturer;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.cdom.reference.TransparentCategorizedReferenceManufacturer;
import pcgen.cdom.reference.TransparentReferenceManufacturer;
import pcgen.cdom.reference.UnconstructedValidator;

public class GameReferenceContext extends AbstractReferenceContext
{
	private final Map<Class<?>, TransparentReferenceManufacturer<?>> map = new HashMap<Class<?>, TransparentReferenceManufacturer<?>>();

	private final DoubleKeyMap<Class<?>, String, TransparentCategorizedReferenceManufacturer<? extends CDOMObject>> catmap = new DoubleKeyMap<Class<?>, String, TransparentCategorizedReferenceManufacturer<? extends CDOMObject>>();

	@Override
	public <T extends Identified> ReferenceManufacturer<T> getManufacturer(
			Class<T> cl)
	{
		if (CategorizedCDOMObject.class.isAssignableFrom(cl))
		{
			throw new InternalError(cl
					+ " is categorized but was fetched without a category");
		}
		TransparentReferenceManufacturer<T> mfg = (TransparentReferenceManufacturer<T>) map
				.get(cl);
		if (mfg == null)
		{
			mfg = new TransparentReferenceManufacturer<T>(cl);
			map.put(cl, mfg);
		}
		return mfg;
	}

	@Override
	public Collection<TransparentReferenceManufacturer<?>> getAllManufacturers()
	{
		ArrayList<TransparentReferenceManufacturer<?>> returnList = new ArrayList<TransparentReferenceManufacturer<?>>(
				map.values());
		for (Class<?> cl : catmap.getKeySet())
		{
			returnList.addAll(catmap.values(cl));
		}
		return returnList;
	}

	public <T extends CDOMObject & CategorizedCDOMObject<T>> CategorizedManufacturer<T> getManufacturer(
			Class<T> cl, String cat)
	{
		TransparentCategorizedReferenceManufacturer<T> mfg = (TransparentCategorizedReferenceManufacturer<T>) catmap
				.get(cl, cat);
		if (mfg == null)
		{
			mfg = new TransparentCategorizedReferenceManufacturer<T>(cl, cat);
			catmap.put(cl, cat, mfg);
		}
		return mfg;
	}

	
	
	@Override
	public boolean validate(UnconstructedValidator validator)
	{
		return true;
	}

	public <T extends CDOMObject & CategorizedCDOMObject<T>> CategorizedManufacturer<T> getManufacturer(
			Class<T> cl, Category<T> cat)
	{
		return getManufacturer(cl, cat.getKeyName());
	}

	public <T extends CDOMObject & CategorizedCDOMObject<T>> Category<T> getCategoryFor(
			Class<T> cl, String string)
	{
		throw new UnsupportedOperationException(
				"GameReferenceContext cannot reference Categories");
	}

	public <T extends CDOMObject> T performCopy(T obj, String copyName)
	{
		throw new UnsupportedOperationException(
				"GameReferenceContext cannot copy objects");
	}

	public <T extends CDOMObject> T performMod(T obj)
	{
		throw new UnsupportedOperationException(
				"GameReferenceContext cannot mod objects");
	}

	@Override
	public <T extends CDOMObject> boolean hasManufacturer(Class<T> cl)
	{
		return false;
	}

	@Override
	protected <T extends CDOMObject & CategorizedCDOMObject<T>> boolean hasManufacturer(
			Class<T> cl, Category<T> cat)
	{
		return false;
	}
}
