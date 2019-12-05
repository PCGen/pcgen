/*
 * Copyright 2009 (C) Tom Parker <thpr@users.sourceforge.net>
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
package pcgen.core.analysis;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMObjectUtilities;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Kit;
import pcgen.core.PlayerCharacter;

public final class AddObjectActions
{

    private AddObjectActions()
    {
    }

    public static void doBaseChecks(CDOMObject po, final PlayerCharacter aPC)
    {
        aPC.setDirty(true);
        for (TransitionChoice<Kit> kit : po.getSafeListFor(ListKey.KIT_CHOICE))
        {
            kit.act(kit.driveChoice(aPC), po, aPC);
        }
    }

    public static void globalChecks(CDOMObject po, final PlayerCharacter aPC)
    {
        doBaseChecks(po, aPC);
        CDOMObjectUtilities.addAdds(po, aPC);
        CDOMObjectUtilities.checkRemovals(po, aPC);
        po.activateBonuses(aPC);
    }

}
