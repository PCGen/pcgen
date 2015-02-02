/*
 * Copyright 2009 (C) Tom Parker <thpr@users.sourceforge.net>
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
import java.util.List;

import pcgen.base.util.DoubleKeyMapToList;
import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.CategorizedCDOMObject;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.CDOMTransparentCategorizedSingleRef;
import pcgen.cdom.reference.Qualifier;
import pcgen.cdom.reference.UnconstructedValidator;
import pcgen.core.Campaign;

public class LoadValidator implements UnconstructedValidator
{
	private static final Class<CategorizedCDOMObject> CATEGORIZED_CDOM_OBJECT_CLASS = CategorizedCDOMObject.class;
	private final List<Campaign> campaignList;
	private HashMapToList<Class<?>, String> simpleMap;
	private DoubleKeyMapToList<Class<?>, String, String> categoryMap;

	public LoadValidator(List<Campaign> campaigns)
	{
		campaignList = new ArrayList<Campaign>(campaigns);
	}

	@Override
	public <T> boolean allow(Class<T> cl, String s)
	{
		if (simpleMap == null)
		{
			buildSimpleMap();
		}
		List<String> list = simpleMap.getListFor(cl);
		if (list != null)
		{
			for (String key : list)
			{
				if (key.equalsIgnoreCase(s))
				{
					return true;
				}
			}
		}
		return false;
	}

	private void buildSimpleMap()
	{
		simpleMap = new HashMapToList<Class<?>, String>();
		for (Campaign c : campaignList)
		{
			for (Qualifier q : c.getSafeListFor(ListKey.FORWARDREF))
			{
				Class<? extends Loadable> qcl = q.getQualifiedClass();
				if (!CATEGORIZED_CDOM_OBJECT_CLASS.isAssignableFrom(qcl))
				{
					simpleMap.addToListFor(qcl, q.getQualifiedReference()
							.getLSTformat(false));
				}
			}
		}
	}

	private void buildCategoryMap()
	{
		categoryMap = new DoubleKeyMapToList<Class<?>, String, String>();
		for (Campaign c : campaignList)
		{
			for (Qualifier q : c.getSafeListFor(ListKey.FORWARDREF))
			{
				Class<? extends Loadable> qcl = q.getQualifiedClass();
				if (CATEGORIZED_CDOM_OBJECT_CLASS.isAssignableFrom(qcl))
				{
					CDOMSingleRef<?> ref = q.getQualifiedReference();
					String cat = ((CDOMTransparentCategorizedSingleRef<?>) ref)
							.getLSTCategory();
					categoryMap.addToListFor(qcl, cat, ref.getLSTformat(false));
				}
			}
		}
	}

	@Override
	public <T extends Loadable & CategorizedCDOMObject<T>> boolean allow(
			Class<T> cl, Category<T> cat, String s)
	{
		if (categoryMap == null)
		{
			buildCategoryMap();
		}
		List<String> list = categoryMap.getListFor(cl, cat.getKeyName());
		if (list != null)
		{
			for (String key : list)
			{
				if (key.equalsIgnoreCase(s))
				{
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean allowDuplicates(Class<?> cl)
	{
		for (Campaign c : campaignList)
		{
			if (c.containsInList(ListKey.DUPES_ALLOWED, cl))
			{
				return true;
			}
		}
		return false;
	}

}
