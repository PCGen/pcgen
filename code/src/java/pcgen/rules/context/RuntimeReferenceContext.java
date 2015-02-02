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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CategorizedCDOMObject;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.reference.CDOMFactory;
import pcgen.cdom.reference.ManufacturableFactory;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.cdom.reference.SimpleReferenceManufacturer;
import pcgen.persistence.PersistenceLayerException;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

public class RuntimeReferenceContext extends AbstractReferenceContext
{
	private final Map<Class<?>, ReferenceManufacturer<?>> map = new HashMap<Class<?>, ReferenceManufacturer<?>>();

	private final Map<ManufacturableFactory<?>, ReferenceManufacturer<?>> mfgmap = new HashMap<ManufacturableFactory<?>, ReferenceManufacturer<?>>();

	@Override
	public <T extends Loadable> ReferenceManufacturer<T> getManufacturer(
			Class<T> cl)
	{
		if (CategorizedCDOMObject.class.isAssignableFrom(cl))
		{
			throw new InternalError(cl
					+ " is categorized but was fetched without a category");
		}
		ReferenceManufacturer<T> mfg = (ReferenceManufacturer<T>) map
				.get(cl);
		if (mfg == null)
		{
			mfg = new SimpleReferenceManufacturer<T>(new CDOMFactory<T>(cl));
			map.put(cl, mfg);
		}
		return mfg;
	}

	@Override
	public Collection<ReferenceManufacturer<?>> getAllManufacturers()
	{
		ArrayList<ReferenceManufacturer<?>> returnList = new ArrayList<ReferenceManufacturer<?>>(
				map.values());
		returnList.addAll(mfgmap.values());
		return returnList;
	}

	@Override
	public <T extends Loadable & CategorizedCDOMObject<T>> ReferenceManufacturer<T> getManufacturer(
			Class<T> cl, Category<T> cat)
	{
		if (cat == null)
		{
			ReferenceManufacturer<T> mfg = (ReferenceManufacturer<T>) map
					.get(cl);
			if (mfg == null)
			{
				mfg = new SimpleReferenceManufacturer<T>(new CDOMFactory<T>(cl));
				map.put(cl, mfg);
			}
			return mfg;
		}
		return getManufacturer(cat);
	}

	@Override
	public <T extends Loadable> ReferenceManufacturer<T> getManufacturer(
			ManufacturableFactory<T> factory)
	{
		ReferenceManufacturer<T> rm = (ReferenceManufacturer<T>) mfgmap.get(factory);
		if (rm == null)
		{
			rm = new SimpleReferenceManufacturer<T>(factory);
			mfgmap.put(factory, rm);
		}
		return rm;
	}

	@Override
	public <T extends Loadable & CategorizedCDOMObject<T>> ReferenceManufacturer<T> getManufacturer(
			Class<T> cl, Class<? extends Category<T>> catClass, String category)
	{
		Category<T> cat = silentlyGetConstructedCDOMObject(catClass, category);
		if (cat == null)
		{
			Logging.errorPrint("Cannot find " + cl.getSimpleName()
					+ " Category " + category);
			return null;
		}
		return getManufacturer(cl, cat);
	}

	/**
	 * This method will perform a single .COPY operation.
	 * @param object the object to copy
	 * @param copyName String name of the target object
	 *
	 * @throws PersistenceLayerException 
	 */
	@Override
	<T extends CDOMObject> T performCopy(T object, String copyName)
	{
		try
		{
			T clone = (T) object.clone();
			clone.setName(copyName);
			clone.put(StringKey.KEY_NAME, copyName);
			importObject(clone);
			return clone;
		}
		catch (CloneNotSupportedException e)
		{
			String message = LanguageBundle.getFormattedString(
				"Errors.LstFileLoader.CopyNotSupported", //$NON-NLS-1$
				object.getClass().getName(), object.getKeyName(), copyName);
			Logging.errorPrint(message);
		}
		return null;
	}

	@Override
	public <T extends CDOMObject> T performMod(T obj)
	{
		return obj;
	}

	@Override
	public <T extends Loadable> boolean hasManufacturer(Class<T> cl)
	{
		return map.containsKey(cl);
	}

	@Override
	protected <T extends CDOMObject & CategorizedCDOMObject<T>> boolean hasManufacturer(
			Class<T> cl, Category<T> cat)
	{
		if (cat == null)
		{
			return map.containsKey(cl);
		}
		return mfgmap.containsKey(cat);
	}

}
