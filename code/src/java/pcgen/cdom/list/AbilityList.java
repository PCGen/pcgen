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

import pcgen.base.util.DoubleKeyMap;
import pcgen.cdom.base.CDOMListObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;

/**
 * AbilityList is a CDOMListObject designed to reference a List of Ability
 * objects.
 */
public class AbilityList extends CDOMListObject<Ability>
{

    private CDOMSingleRef<AbilityCategory> category;
    private Nature nature;

    /**
     * Stores references to the "master" set of lists that are unique for a
     * given Category/Nature combination.
     */
    public static final DoubleKeyMap<CDOMSingleRef<AbilityCategory>, Nature, CDOMReference<AbilityList>> MASTER_MAP =
            new DoubleKeyMap<>();

    /**
     * Returns the Ability Class object (Ability.class)
     *
     * @return the Ability Class object (Ability.class)
     */
    @Override
    public Class<Ability> getListClass()
    {
        return Ability.class;
    }

    /**
     * Lists never have a Type, so this returns false
     */
    @Override
    public boolean isType(String type)
    {
        return false;
    }

    /**
     * Retrieves a reference to the "master" list for a given Category/Nature
     * combination. The appropriate reference and list are constructed if they
     * do not already exist.
     *
     * @param category The Ability Category for which the "master" AbilityList should
     *                 be returned.
     * @param nature   The Ability Nature for which the "master" AbilityList should
     *                 be returned.
     * @return A reference to the "master" list for a given Category/Nature
     * combination.
     */
    public static CDOMReference<AbilityList> getAbilityListReference(CDOMSingleRef<AbilityCategory> category,
            Nature nature)
    {
        CDOMReference<AbilityList> ref = MASTER_MAP.get(category, nature);
        if (ref == null)
        {
            AbilityList list = new AbilityList();
            list.setName("*" + category + ":" + nature);
            list.category = category;
            list.nature = nature;
            ref = CDOMDirectSingleRef.getRef(list);
            MASTER_MAP.put(category, nature, ref);
        }
        return ref;
    }

    public CDOMSingleRef<AbilityCategory> getCategory()
    {
        return category;
    }

    public Nature getNature()
    {
        return nature;
    }

}
