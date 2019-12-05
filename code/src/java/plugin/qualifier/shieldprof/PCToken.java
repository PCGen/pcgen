/*
 * Copyright 2012 (C) James Dempsey
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
package plugin.qualifier.shieldprof;

import java.util.ArrayList;
import java.util.Collection;

import pcgen.cdom.helper.ProfProvider;
import pcgen.core.PlayerCharacter;
import pcgen.core.ShieldProf;
import pcgen.rules.persistence.token.AbstractPCQualifierToken;

/**
 * The Class {@code PCToken} provides limiting a chooser selection
 * by those shield proficiencies held by the character.
 * e.g. CHOOSE:SHIELDPROFICIENCY|PC
 */
public class PCToken extends AbstractPCQualifierToken<ShieldProf>
{

    @Override
    protected Collection<ShieldProf> getPossessed(PlayerCharacter pc)
    {
        // Not used as we have overridden allow below, so return an empty set
        return new ArrayList<>();
    }

    @Override
    public Class<? super ShieldProf> getReferenceClass()
    {
        return ShieldProf.class;
    }

    @Override
    public boolean allow(PlayerCharacter pc, ShieldProf po)
    {
        Collection<ProfProvider<ShieldProf>> providers = pc.getShieldProfList();
        for (ProfProvider<ShieldProf> profProvider : providers)
        {
            if (profProvider.providesProficiency(po))
            {
                return true;
            }
        }

        return false;
    }

}
