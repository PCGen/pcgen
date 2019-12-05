/**
 * Copyright (c) 2008 Andrew Wilson <nuance@users.sourceforge.net>.
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * <p>
 * Created 09-Aug-2008 19:37:53
 */

package pcgen.core.term;

import pcgen.cdom.content.CNAbility;
import pcgen.core.PlayerCharacter;

public abstract class BasePCCountAbilitiesTypeTermEvaluator extends BasePCCountAbilitiesTermEvaluator
{
    protected String[] types;

    @Override
    public Float resolve(PlayerCharacter pc)
    {
        Float count = 0.0f;

        for (CNAbility anAbility : getAbilities(pc))
        {
            // for each feat, look to see if it has any of the required types.
            for (String type : types)
            {
                if (anAbility.getAbility().isType(type))
                {
                    count += countVisibleAbility(pc, anAbility, visible, hidden, false);

                    break;
                }
            }
        }

        return count;
    }
}
