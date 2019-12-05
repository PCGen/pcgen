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

import java.util.List;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Kit;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;

/**
 * Deals with apply RACE via a KIT
 */
public class KitRace extends BaseKit
{
    private CDOMSingleRef<Race> theRace = null;

    /**
     * Actually applies the race to this PC.
     *
     * @param aPC The PlayerCharacter the alignment is applied to
     */
    @Override
    public void apply(PlayerCharacter aPC)
    {
        // We want to level up as quietly as possible for kits.
        boolean tempShowHP = SettingsHandler.getShowHPDialogAtLevelUp();
        SettingsHandler.setShowHPDialogAtLevelUp(false);
        aPC.setRace(theRace.get());
        SettingsHandler.setShowHPDialogAtLevelUp(tempShowHP);
    }

    /**
     * testApply
     *
     * @param aPC      PlayerCharacter
     * @param aKit     Kit
     * @param warnings List
     */
    @Override
    public boolean testApply(Kit aKit, PlayerCharacter aPC, List<String> warnings)
    {
        apply(aPC);
        return true;
    }

    @Override
    public String getObjectName()
    {
        return "Race";
    }

    @Override
    public String toString()
    {
        return theRace.getLSTformat(false);
    }

    public void setRace(CDOMSingleRef<Race> ref)
    {
        theRace = ref;
    }

    public CDOMReference<Race> getRace()
    {
        return theRace;
    }
}
