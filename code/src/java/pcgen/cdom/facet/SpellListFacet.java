/*
 * Copyright (c) Thomas Parker, 2012.
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

import pcgen.cdom.base.CDOMList;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.core.spell.Spell;

/**
 * SpellListFacet maintains a list of spell lists for a Player Character.
 */
public class SpellListFacet extends AbstractSourcedListFacet<CharID, CDOMList<Spell>>
{

    /**
     * Add the given spell list with the given source to the list of spell lists
     * stored in this SpellListFacet for the Player Character represented by the
     * given CharID.
     *
     * @param id     The CharID representing the Player Character for which the
     *               given spell list should be added
     * @param obj    The spell list to be added to the list of spell lists stored
     *               in this SpellListFacet for the Player Character represented by
     *               the given CharID
     * @param source The source for the given spell list
     */
    @Override
    public void add(CharID id, CDOMList<Spell> obj, Object source)
    {
        if (obj == null)
        {
            /*
             * TODO This null check is here primarily to protect the test cases
             * in the "test" directory that create a class, but do not give that
             * class the default spell list associated with that class. Note if
             * the TO-DO mentioned in AbstractReferenceContext about limiting
             * spell list creation to only spell casting classes, then this null
             * check becomes more important :) - thpr
             */
            return;
        }
        super.add(id, obj, source);
    }
}
