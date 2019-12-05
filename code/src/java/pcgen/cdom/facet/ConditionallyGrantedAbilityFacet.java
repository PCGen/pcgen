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
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractListFacet;
import pcgen.cdom.helper.CNAbilitySelection;

/**
 * ConditionallyGrantedAbilityFacet is a DataFacet that contains information
 * about Ability objects that are contained in a Player Character because the
 * Player Character did pass prerequisites for the conditional Ability.
 */
public class ConditionallyGrantedAbilityFacet extends AbstractListFacet<CharID, CNAbilitySelection>
{

    private ConditionalAbilityFacet conditionalAbilityFacet;

    private static boolean entered = false;

    private static boolean redo = false;

    /**
     * Performs a global update of conditionally granted Abilities for a Player
     * Character.
     *
     * @param id The CharID identifying the Player Character for which a global
     *           update of conditionally granted Abilities should be performed.
     */
    public void update(CharID id)
    {
        if (entered)
        {
            redo = true;
            return;
        }
        entered = true;
        Collection<CNAbilitySelection> current = getSet(id);
        Collection<CNAbilitySelection> qualified = conditionalAbilityFacet.getQualifiedSet(id);
        List<CNAbilitySelection> toRemove = new ArrayList<>(current);
        toRemove.removeAll(qualified);
        List<CNAbilitySelection> toAdd = new ArrayList<>(qualified);
        toAdd.removeAll(current);
        for (CNAbilitySelection cas : toRemove)
        {
            // Things could have changed, so we make sure
            if (!conditionalAbilityFacet.isQualified(id, cas) && contains(id, cas))
            {
                remove(id, cas);
            }
        }
        for (CNAbilitySelection cas : toAdd)
        {
            // Things could have changed, so we make sure
            if (conditionalAbilityFacet.isQualified(id, cas) && !contains(id, cas))
            {
                add(id, cas);
            }
        }

        entered = false;
        if (redo)
        {
            redo = false;
            update(id);
        }
    }

    /**
     * Overrides the default behavior of AbstractListFacet, since we need to
     * ensure we are storing the conditionally granted abilities by their
     * identity (Ability has old behavior in .equals and Abilities are still
     * cloned)
     */
    @Override
    protected Set<CNAbilitySelection> getComponentSet()
    {
        return Collections.newSetFromMap(new IdentityHashMap<>());
    }

    public void setConditionalAbilityFacet(ConditionalAbilityFacet conditionalAbilityFacet)
    {
        this.conditionalAbilityFacet = conditionalAbilityFacet;
    }
}
