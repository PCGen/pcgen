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
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.cdom.reference.TransparentReferenceManufacturer;

public class GameReferenceContext extends AbstractReferenceContext
{
	private final Map<Class<?>, TransparentReferenceManufacturer<? extends CDOMObject>> map = new HashMap<Class<?>, TransparentReferenceManufacturer<? extends CDOMObject>>();

	private final DoubleKeyMap<Class<?>, Category<?>, TransparentReferenceManufacturer<? extends CDOMObject>> catmap = new DoubleKeyMap<Class<?>, Category<?>, TransparentReferenceManufacturer<? extends CDOMObject>>();

	@Override
	public <T extends CDOMObject> ReferenceManufacturer<T, ? extends CDOMSingleRef<T>> getManufacturer(
			Class<T> cl)
	{
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
	public Collection<TransparentReferenceManufacturer<? extends CDOMObject>> getAllManufacturers()
	{
		ArrayList<TransparentReferenceManufacturer<? extends CDOMObject>> returnList = new ArrayList<TransparentReferenceManufacturer<? extends CDOMObject>>(
				map.values());
		for (Class<?> cl : catmap.getKeySet())
		{
			returnList.addAll(catmap.values(cl));
		}
		return returnList;
	}

	@Override
	public <T extends CDOMObject & CategorizedCDOMObject<T>> ReferenceManufacturer<T, ? extends CDOMSingleRef<T>> getManufacturer(
			Class<T> cl, Category<T> cat)
	{
		TransparentReferenceManufacturer<T> mfg = (TransparentReferenceManufacturer<T>) catmap
				.get(cl, cat);
		if (mfg == null)
		{
			mfg = new TransparentReferenceManufacturer<T>(cl);
			catmap.put(cl, cat, mfg);
		}
		return mfg;
	}
}
