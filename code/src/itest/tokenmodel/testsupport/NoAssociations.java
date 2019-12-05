/*
 * Copyright (c) 2014 Tom Parker <thpr@users.sourceforge.net>
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
package tokenmodel.testsupport;

import pcgen.cdom.content.CNAbility;
import pcgen.cdom.helper.CNAbilitySelection;
import pcgen.core.PlayerCharacter;

public class NoAssociations implements AssocCheck, CASAssocCheck
{

    private final PlayerCharacter pc;

    public NoAssociations(PlayerCharacter pc)
    {
        this.pc = pc;
    }

    @Override
    public boolean check(CNAbility g)
    {
        if (pc.getDetailedAssociationCount(g) == 0)
        {
            return true;
        } else
        {
            System.err.println("Incorrect Association Count");
            return false;
        }
    }

    @Override
    public boolean check(CNAbilitySelection cas)
    {
        return cas.getSelection() == null;
    }

}
