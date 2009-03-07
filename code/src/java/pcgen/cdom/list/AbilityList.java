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
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Ability;
import pcgen.core.Ability.Nature;

/**
 * AbilityList is a CDOMListObject designed to reference a List of Ability
 * objects.
 */
public class AbilityList extends CDOMListObject<Ability>
{

	/**
	 * Stores references to the "master" set of lists that are unique for a
	 * given Category/Nature combination.
	 */
	public static final DoubleKeyMap<Category<Ability>, Ability.Nature, CDOMReference<AbilityList>> MASTER_MAP = new DoubleKeyMap<Category<Ability>, Ability.Nature, CDOMReference<AbilityList>>();

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

	/**
	 * Retrieves a reference to the "master" list for a given Category/Nature
	 * combination. The appropriate reference and list are constructed if they
	 * do not already exist.
	 * 
	 * @param category
	 *            The Ability Category for which the "master" AbilityList should
	 *            be returned.
	 * @param nature
	 *            The Ability Nature for which the "master" AbilityList should
	 *            be returned.
	 * @return A reference to the "master" list for a given Category/Nature
	 *         combination.
	 */
	public static CDOMReference<AbilityList> getAbilityListReference(
			Category<Ability> category, Nature nature)
	{
		CDOMReference<AbilityList> list = MASTER_MAP.get(category, nature);
		if (list == null)
		{
			AbilityList al = new AbilityList();
			al.setName("*" + category.toString() + ":" + nature.toString());
			al.put(ObjectKey.ABILITY_CAT, category);
			al.put(ObjectKey.ABILITY_NATURE, nature);
			list = CDOMDirectSingleRef.getRef(al);
			MASTER_MAP.put(category, nature, list);
		}
		return list;
	}

	/**
	 * Returns a Collection of references to all of the "master" AbilityList
	 * objects retrieved through the getAbilityListReference method.
	 * 
	 * This method is value-semantic in that ownership of the returned
	 * Collection is transferred to the class calling this method. Modification
	 * of the returned Collection will not modify the "master" AbilityList
	 * collection and modification of the "master" AbilityList collection
	 * through subsequent calls of getAbilityListReference will not modify the
	 * returned Collection.
	 * 
	 * This method will not return null, even if getAbilityListReference was
	 * never called.
	 * 
	 * @return A Collection of references to all of the "master" AbilityList
	 *         objects retrieved through the getAbilityListReference method.
	 */
	public static Collection<CDOMReference<AbilityList>> getAbilityLists()
	{
		List<CDOMReference<AbilityList>> list = new ArrayList<CDOMReference<AbilityList>>();
		for (Category<Ability> cat : MASTER_MAP.getKeySet())
		{
			list.addAll(MASTER_MAP.values(cat));
		}
		return list;
	}

}
