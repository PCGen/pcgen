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
 * Created 09-Aug-2008 19:00:53
 */

package pcgen.core.term;

import java.util.Collection;

import pcgen.cdom.content.CNAbility;
import pcgen.cdom.enumeration.Nature;
import pcgen.core.AbilityCategory;
import pcgen.core.PlayerCharacter;

public class PCCountAbilitiesNatureVirtualTermEvaluator extends BasePCCountAbilitiesNatureTermEvaluator
        implements TermEvaluator
{
    public PCCountAbilitiesNatureVirtualTermEvaluator(String originalText, AbilityCategory abCat, boolean visible,
            boolean hidden)
    {
        this.originalText = originalText;
        this.abCat = abCat;
        this.visible = visible;
        this.hidden = hidden;
    }

    @Override
    Collection<CNAbility> getAbilities(PlayerCharacter pc)
    {
        return pc.getPoolAbilities(abCat, Nature.VIRTUAL);
    }

    @Override
    public boolean isSourceDependant()
    {
        return false;
    }

    public boolean isStatic()
    {
        return false;
    }
}
