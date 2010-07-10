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
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.reference.CategorizedReferenceManufacturer;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.cdom.reference.SimpleReferenceManufacturer;
import pcgen.core.Ability;
import pcgen.core.SettingsHandler;
import pcgen.persistence.PersistenceLayerException;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

public class RuntimeReferenceContext extends AbstractReferenceContext
{
	private final Map<Class<?>, ReferenceManufacturer<?>> map = new HashMap<Class<?>, ReferenceManufacturer<?>>();

	private final DoubleKeyMap<Class<?>, Category<?>, CategorizedReferenceManufacturer<?>> catmap = new DoubleKeyMap<Class<?>, Category<?>, CategorizedReferenceManufacturer<?>>();

	@Override
	public <T extends Identified> ReferenceManufacturer<T> getManufacturer(
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
			mfg = new SimpleReferenceManufacturer<T>(cl);
			map.put(cl, mfg);
		}
		return mfg;
	}

	@Override
	public Collection<ReferenceManufacturer<?>> getAllManufacturers()
	{
		ArrayList<ReferenceManufacturer<?>> returnList = new ArrayList<ReferenceManufacturer<?>>(
				map.values());
		for (Class<?> cl : catmap.getKeySet())
		{
			returnList.addAll(catmap.values(cl));
		}
		return returnList;
	}

	public <T extends CDOMObject & CategorizedCDOMObject<T>> ReferenceManufacturer<T> getManufacturer(
			Class<T> cl, Category<T> cat)
	{
		CategorizedReferenceManufacturer<T> mfg = (CategorizedReferenceManufacturer<T>) catmap
				.get(cl, cat);
		if (mfg == null)
		{
			mfg = new CategorizedReferenceManufacturer<T>(cl, cat);
			catmap.put(cl, cat, mfg);
			if (cat != null)
			{
				Category<T> parent = cat.getParentCategory();
				if (parent != null)
				{
					CategorizedReferenceManufacturer<T> parentMfg = (CategorizedReferenceManufacturer<T>) catmap
							.get(cl, parent);
					if (parentMfg == null)
					{
						Category parentCat = parent;
						parentMfg = new CategorizedReferenceManufacturer<T>(cl,
								parentCat);
						catmap.put(cl, cat, parentMfg);
					}
					mfg.setParentCRM(parentMfg);
				}
			}
		}
		return mfg;
	}

	public <T extends CDOMObject & CategorizedCDOMObject<T>> ReferenceManufacturer<T> getManufacturer(
			Class<T> cl, String category)
	{
		Category<T> cat = getCategoryFor(cl, category);
		if (cat == null)
		{
			Logging.errorPrint("Cannot find " + cl.getSimpleName()
					+ " Category " + category);
			return null;
		}
		ReferenceManufacturer manufacturer = getManufacturer(cl, cat);
		return manufacturer;
	}

	public <T extends CDOMObject & CategorizedCDOMObject<T>> Category<T> getCategoryFor(
			Class<T> cl, String s)
	{
		if (cl.equals(Ability.class))
		{
			return (Category) SettingsHandler.getGame()
					.silentlyGetAbilityCategory(s);
		}
		else
		{
			return null;
		}
	}


	/**
	 * This method will perform a single .COPY operation.
	 * @param context TODO
	 * @param copyName String name of the target object
	 * @param baseName String name of the object to copy
	 *
	 * @throws PersistenceLayerException 
	 */
	public <T extends CDOMObject> T performCopy(T object, String copyName)
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
			String message = PropertyFactory.getFormattedString(
				"Errors.LstFileLoader.CopyNotSupported", //$NON-NLS-1$
				object.getClass().getName(), object.getKeyName(), copyName);
			Logging.errorPrint(message);
		}
		return null;
	}

	public <T extends CDOMObject> T performMod(T obj)
	{
		return obj;
	}

	@Override
	public <T extends CDOMObject> boolean hasManufacturer(Class<T> cl)
	{
		return map.containsKey(cl);
	}

	@Override
	protected <T extends CDOMObject & CategorizedCDOMObject<T>> boolean hasManufacturer(
			Class<T> cl, Category<T> cat)
	{
		return catmap.containsKey(cl, cat);
	}

}
