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
import pcgen.cdom.base.Categorized;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.reference.ManufacturableFactory;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.cdom.reference.SimpleReferenceManufacturer;
import pcgen.cdom.reference.TransparentCategorizedFactory;
import pcgen.cdom.reference.TransparentCategorizedReferenceManufacturer;
import pcgen.cdom.reference.TransparentFactory;
import pcgen.cdom.reference.UnconstructedValidator;

/**
 * The Class {@code GameReferenceContext} is a ReferenceContext which is
 * capable of delegating its transparent references to references built later 
 * in the process.  Transparent references are a new concept, they are basically 
 * references that allow later resolution to other references (meaning in the 
 * long run, they delegate to another reference of the same general type, though 
 * they can be created before the delegate target is created)
 *
 * 
 */
public class GameReferenceContext extends AbstractReferenceContext
{
	private final Map<Class<?>, ReferenceManufacturer<?>> map = new HashMap<>();

	private final DoubleKeyMap<Class<?>, String, TransparentCategorizedReferenceManufacturer<? extends Loadable>> catmap = new DoubleKeyMap<>();

	private GameReferenceContext()
	{
	}
	
	@Override
	public <T extends Loadable> ReferenceManufacturer<T> getManufacturer(
			Class<T> cl)
	{
		if (Categorized.class.isAssignableFrom(cl))
		{
			throw new InternalError(cl
					+ " is categorized but was fetched without a category");
		}
		@SuppressWarnings("unchecked")
		ReferenceManufacturer<T> mfg = (ReferenceManufacturer<T>) map.get(cl);
		if (mfg == null)
		{
			mfg = constructReferenceManufacturer(cl);
			map.put(cl, mfg);
		}
		return mfg;
	}

	@Override
	protected <T extends Loadable> ReferenceManufacturer<T> constructReferenceManufacturer(
		Class<T> cl)
	{
		return new SimpleReferenceManufacturer<>(new TransparentFactory<>(cl));
	}

	@Override
	public Collection<ReferenceManufacturer<?>> getAllManufacturers()
	{
		ArrayList<ReferenceManufacturer<?>> returnList = new ArrayList<>(
                map.values());
		for (Class<?> cl : catmap.getKeySet())
		{
			returnList.addAll(catmap.values(cl));
		}
		return returnList;
	}

	@Override
	public <T extends Categorized<T>> ReferenceManufacturer<T> getManufacturer(
			Class<T> cl, Class<? extends Category<T>> catClass, String cat)
	{
		@SuppressWarnings("unchecked")
		TransparentCategorizedReferenceManufacturer<T> mfg = (TransparentCategorizedReferenceManufacturer<T>) catmap
				.get(cl, cat);
		if (mfg == null)
		{
			mfg = new TransparentCategorizedReferenceManufacturer<>(new TransparentCategorizedFactory<>(cl, cat), catClass, cat);
			catmap.put(cl, cat, mfg);
		}
		return mfg;
	}

	@Override
	public boolean validate(UnconstructedValidator validator)
	{
		return true;
	}

	@Override
	public <T extends Categorized<T>> ReferenceManufacturer<T> getManufacturer(
			Class<T> cl, Category<T> cat)
	{
		Class<? extends Category<T>> catClass = getGenericClass(cat);
		return getManufacturer(cl, catClass, cat.getKeyName());
	}

	@Override
	<T extends CDOMObject> T performCopy(T obj, String copyName)
	{
		throw new UnsupportedOperationException(
				"GameReferenceContext cannot copy objects");
	}

	@Override
	public <T extends CDOMObject> T performMod(T obj)
	{
		throw new UnsupportedOperationException(
				"GameReferenceContext cannot mod objects");
	}

	@Override
	public <T extends Loadable> boolean hasManufacturer(Class<T> cl)
	{
		return false;
	}

	@Override
	protected <T extends Categorized<T>> boolean hasManufacturer(
			Class<T> cl, Category<T> cat)
	{
		return false;
	}

	@Override
	public <T extends Loadable> ReferenceManufacturer<T> getManufacturer(
			ManufacturableFactory<T> factory)
	{
		throw new UnsupportedOperationException(
				"GameReferenceContext cannot provide a factory based manufacturer");
	}

	/**
	 * Return a new GameReferenceContext. This ReferenceContext is initialized as per
	 * the rules of AbstractReferenceContext.
	 * 
	 * @return A new GameReferenceContext
	 */
	public static GameReferenceContext createGameReferenceContext()
	{
		GameReferenceContext context = new GameReferenceContext();
		context.initialize();
		return context;
	}

}
