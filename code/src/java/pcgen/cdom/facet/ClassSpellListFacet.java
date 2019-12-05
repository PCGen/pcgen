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

import pcgen.cdom.base.CDOMListObject;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.spell.Spell;

/**
 * ClassSpellListFacet tracks the Spell Lists granted to a Player Character by a
 * CDOMObject. This may be a static SpellList or a choice of a SpellLists
 * available to the Player Character.
 */
public class ClassSpellListFacet
{
    private final PlayerCharacterTrackingFacet trackingFacet =
            FacetLibrary.getFacet(PlayerCharacterTrackingFacet.class);

    private SpellListFacet spellListFacet;

    /*
     * While it would be ideal to listen to ClassLevelFacet and trigger off that
     * change, it is actually a problem to do so today because subclasses need
     * to be deployed before that can occur, so this process has to be
     * "out of facet events" for the moment - thpr
     */

    /**
     * Process the given PCClass, and add the appropriate SpellList objects to
     * the Player Character. This may be a single SpellList or a choice of
     * SpellLists. If it is a choice, drive the choice of the SpellList.
     *
     * @param id  The CharID identifying the Player Character on which the
     *            SpellLists should be applied
     * @param pcc The PCClass on which the SpellLists will be added to the
     *            Player Character.
     */
    public void process(CharID id, PCClass pcc)
    {
        TransitionChoice<CDOMListObject<Spell>> csc = pcc.get(ObjectKey.SPELLLIST_CHOICE);
        if (csc == null)
        {
            addDefaultSpellList(id, pcc);
        } else
        {
            PlayerCharacter pc = trackingFacet.getPC(id);
            for (CDOMListObject<Spell> st : csc.driveChoice(pc))
            {
                spellListFacet.add(id, st, pcc);
            }
        }
    }

    /**
     * Adds the default spell list for the PCClass to the Player Character.
     *
     * @param id  The CharID identifying the Player Character on which the
     *            default SpellList for the given PCClass will be added
     * @param pcc The PCClass for which the default SpellList will be added to
     *            the PlayerCharacte identified by the given CharID.
     */
    public void addDefaultSpellList(CharID id, PCClass pcc)
    {
        spellListFacet.add(id, pcc.get(ObjectKey.CLASS_SPELLLIST), pcc);
    }

    public void setSpellListFacet(SpellListFacet spellListFacet)
    {
        this.spellListFacet = spellListFacet;
    }

}
