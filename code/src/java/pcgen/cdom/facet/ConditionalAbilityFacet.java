/*
 * Copyright (c) Thomas Parker, 2010.
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.facet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractSingleSourceListFacet;
import pcgen.cdom.helper.CNAbilitySelection;

/**
 * ConditionalAbilityFacet is a DataFacet that contains information about
 * conditionally granted Ability objects that are contained in a Player
 * Character. All conditionally granted abilities (regardless of whether they
 * are granted to the Player Character) are stored here.
 * ConditionallyGrantedAbilityFacet performs the calculation to determine which
 * are active / granted to the Player Character.
 */
public class ConditionalAbilityFacet extends AbstractSingleSourceListFacet<CNAbilitySelection, Object>
{
    private PrerequisiteFacet prerequisiteFacet;

    /**
     * Returns a non-null copy of the Set of objects the character qualifies for
     * in this ConditionalAbilityFacet for the Player Character represented by
     * the given CharID. This method returns an empty Collection if the Player
     * Character identified by the given CharID qualifies for none of the
     * objects in this ConditionalAbilityFacet.
     * <p>
     * This method is value-semantic in that ownership of the returned
     * Collection is transferred to the class calling this method. Modification
     * of the returned Collection will not modify this ConditionalAbilityFacet
     * and modification of this ConditionalAbilityFacet will not modify the
     * returned Collection. Modifications to the returned Collection will also
     * not modify any future or previous objects returned by this (or other)
     * methods on ConditionalAbilityFacet. If you wish to modify the information
     * stored in this ConditionalAbilityFacet, you must use the add*() and
     * remove*() methods of ConditionalAbilityFacet.
     *
     * @param id The CharID representing the Player Character for which the
     *           items in this AbstractQualifiedListFacet should be returned.
     * @return A non-null Set of objects the Player Character represented by the
     * given CharID qualifies for in this AbstractQualifiedListFacet
     */
    public Collection<CNAbilitySelection> getQualifiedSet(CharID id)
    {
        List<CNAbilitySelection> set = new ArrayList<>();
        Map<CNAbilitySelection, Object> cached = getCachedMap(id);
        if (cached != null)
        {
            for (Map.Entry<CNAbilitySelection, Object> me : cached.entrySet())
            {
                CNAbilitySelection cnas = me.getKey();
                if (prerequisiteFacet.qualifies(id, cnas, me.getValue()))
                {
                    set.add(cnas);
                }
            }
        }
        return set;
    }

    /**
     * Check if the character is allowed to take the ability selection.
     *
     * @param id  The CharID representing the Player Character.
     * @param cas The ability selection to be checked.
     * @return true if the character qualifies for the selection, false if not.
     */
    public boolean isQualified(CharID id, CNAbilitySelection cas)
    {
        Map<CNAbilitySelection, Object> cached = getCachedMap(id);
        if (cached != null)
        {
            Object source = cached.get(cas);
            //null gate b/c we may not have cas
            if (source != null)
            {
                return prerequisiteFacet.qualifies(id, cas, source);
            }
        }
        return false;
    }

    public void setPrerequisiteFacet(PrerequisiteFacet prerequisiteFacet)
    {
        this.prerequisiteFacet = prerequisiteFacet;
    }

}
