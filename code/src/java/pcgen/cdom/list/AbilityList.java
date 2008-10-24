/*
 * Copyright 2007, 2008 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.base.util.DoubleKeyMap;
import pcgen.cdom.base.CDOMListObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Category;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Ability;
import pcgen.core.Ability.Nature;

/**
 * AbilityList is a CDOMListObject designed to reference a List of Ability
 * objects.
 */
public class AbilityList extends CDOMListObject<Ability>
{

	
	public static final DoubleKeyMap<Category<Ability>, Ability.Nature, CDOMReference<AbilityList>> map =
			new DoubleKeyMap<Category<Ability>, Ability.Nature, CDOMReference<AbilityList>>();
	
	/**
	 * Returns the Ability Class object (Ability.class)
	 * 
	 * @return the Ability Class object (Ability.class)
	 */
	public Class<Ability> getListClass()
	{
		return Ability.class;
	}

	/**
	 * Lists never have a Type, so this returns false
	 */
	@Override
	public boolean isType(String str)
	{
		return false;
	}

	public static CDOMReference<AbilityList> getAbilityListReference(Category<Ability> category,
		Nature nature)
	{
		CDOMReference<AbilityList> list = map.get(category, nature);
		if (list == null)
		{
			AbilityList al = new AbilityList();
			al.setName("*" + category.toString() + ":" + nature.toString());
			list = CDOMDirectSingleRef.getRef(al);
			map.put(category, nature, list);
		}
		return list;
	}

	public static Collection<CDOMReference<AbilityList>> getAbilityLists()
	{
		List<CDOMReference<AbilityList>> list = new ArrayList<CDOMReference<AbilityList>>();
		for (Category<Ability> cat : map.getKeySet())
		{
			list.addAll(map.values(cat));
		}
		return list;
	}

}
