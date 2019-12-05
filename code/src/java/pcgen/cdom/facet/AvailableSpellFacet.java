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

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractSubScopeFacet;
import pcgen.cdom.list.ClassSpellList;
import pcgen.cdom.list.DomainSpellList;
import pcgen.core.spell.Spell;
import pcgen.util.Logging;

/**
 * AvailableSpellFacet is a Facet that tracks the Available Spells (and target
 * objects) that are contained in a Player Character.
 */
public class AvailableSpellFacet extends AbstractSubScopeFacet<CDOMList<Spell>, Integer, Spell>
{

    /**
     * Returns a non-null HashMapToList indicating the spell levels and sources
     * of those spell levels available to a Player Character for a given Spell.
     * <p>
     * This may return multiple spell levels because it is possible for a spell
     * to be accessible to a Player Character at multiple levels since it may be
     * available from multiple sources. This also returns the spell lists
     * associated with the given level, since it is possible for a multi-class
     * character to have access to the same spell at different levels. By
     * returning the source as well as the spell levels, such scenarios can be
     * appropriately distinguished.
     * <p>
     * This method is value-semantic in that ownership of the returned
     * HashMapToList is transferred to the class calling this method.
     * Modification of the returned HashMapToList will not modify this
     * AvailableSpellFacet and modification of this AvailableSpellFacet will not
     * modify the returned HashMapToList. Modifications to the returned
     * HashMapToList will also not modify any future or previous objects
     * returned by this (or other) methods on AvailableSpellFacet. If you wish
     * to modify the information stored in this AvailableSpellFacet, you must
     * use the add*() and remove*() methods of AvailableSpellFacet.
     *
     * @param id The CharID identifying the Player Character for which the
     *           spell levels should be returned
     * @param sp The Spell for which the spell levels should be returned
     * @return A non-null HashMapToList indicating the spell levels and sources
     * of those spell levels available to a Player Character for a given
     * Spell.
     */
    public HashMapToList<CDOMList<Spell>, Integer> getSpellLevelInfo(CharID id, Spell sp)
    {
        HashMapToList<CDOMList<Spell>, Integer> levelInfo = new HashMapToList<>();
        Map<CDOMList<Spell>, Map<Integer, Map<Spell, Set<Object>>>> listMap =
                (Map<CDOMList<Spell>, Map<Integer, Map<Spell, Set<Object>>>>) getCache(id);
        if (listMap == null)
        {
            return levelInfo;
        }
        for (Entry<CDOMList<Spell>, Map<Integer, Map<Spell, Set<Object>>>> me : listMap.entrySet())
        {
            CDOMList<Spell> list = me.getKey();
            //Check to ensure we don't use SPELLS:
            if (!(list instanceof ClassSpellList) && !(list instanceof DomainSpellList))
            {
                continue;
            }
            Map<Integer, Map<Spell, Set<Object>>> levelMap = me.getValue();
            for (Map.Entry<Integer, Map<Spell, Set<Object>>> lme : levelMap.entrySet())
            {
                Integer level = lme.getKey();
                Map<Spell, Set<Object>> spellMap = lme.getValue();
                if (spellMap.containsKey(sp))
                {
                    levelInfo.addToListFor(list, level);
                } else
                {
                    for (Spell spell : spellMap.keySet())
                    {
                        if (spell.getKeyName().equals(sp.getKeyName()))
                        {
                            if (Logging.isLoggable(Logging.INFO))
                            {
                                Logging.log(Logging.INFO, "Found alternate spell of same key: " + spell + " from "
                                        + spell.getSource() + " rather than " + sp.getSource());
                            }
                            levelInfo.addToListFor(list, level);
                        }
                    }
                }
            }
        }
        return levelInfo;
    }
}
