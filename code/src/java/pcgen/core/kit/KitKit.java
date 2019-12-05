/*
 * Copyright 2005 (C) Aaron Divinsky <boomer70@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.core.kit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pcgen.cdom.base.Constants;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Kit;
import pcgen.core.PlayerCharacter;

/**
 * Applies the Kit
 */
public class KitKit extends BaseKit
{
    private List<CDOMSingleRef<Kit>> availableKits = new ArrayList<>();

    // These members store the state of an instance of this class.  They are
    // not cloned.
    private Map<Kit, List<BaseKit>> appliedKits = new HashMap<>();

    /**
     * Actually applies the kit to this PC.
     *
     * @param aPC The PlayerCharacter the alignment is applied to
     */
    @Override
    public void apply(PlayerCharacter aPC)
    {
        for (Map.Entry<Kit, List<BaseKit>> me : appliedKits.entrySet())
        {
            me.getKey().processKit(aPC, me.getValue());
        }
    }

    /**
     * Test applying this kit to the character.
     *
     * @param aPC      PlayerCharacter The character to apply the kit to.
     * @param aKit     Kit The kit that has requested the application of the kit.
     * @param warnings List The warign list to be populated if anything fails.
     */
    @Override
    public boolean testApply(Kit aKit, PlayerCharacter aPC, List<String> warnings)
    {
        appliedKits = new HashMap<>();
        for (CDOMSingleRef<Kit> ref : availableKits)
        {
            Kit addedKit = ref.get();
            ArrayList<BaseKit> thingsToAdd = new ArrayList<>();
            addedKit.testApplyKit(aPC, thingsToAdd, warnings, true);
            appliedKits.put(addedKit, thingsToAdd);
        }
        return true;
    }

    @Override
    public String getObjectName()
    {
        return "Kit";
    }

    @Override
    public String toString()
    {
        return ReferenceUtilities.joinLstFormat(availableKits, Constants.PIPE);
    }

    public void addKit(CDOMSingleRef<Kit> ref)
    {
        availableKits.add(ref);
    }

    public List<CDOMSingleRef<Kit>> getKits()
    {
        return availableKits;
    }
}
