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
 * Created 09-Aug-2008 22:52:41
 */

package pcgen.core.term;

import pcgen.cdom.content.CNAbility;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.RuleConstants;

public class PCBonusLangTermEvaluator extends BasePCTermEvaluator implements TermEvaluator
{
    public PCBonusLangTermEvaluator(String originalText)
    {
        this.originalText = originalText;
    }

    @Override
    public Float resolve(PlayerCharacter pc)
    {
        int nml = pc.getDisplay().totalNonMonsterLevels();
        if ((nml > 1) || (nml > 0 && pc.getDisplay().totalHitDice() > 0))
        {
            if (!Globals.checkRule(RuleConstants.INTBONUSLANG))
            {
                return 0.0f;
            }
        }
        int count = pc.getBonusLanguageCount();
        CNAbility a = pc.getBonusLanguageAbility();
        int currentLangCount = pc.getDetailedAssociationCount(a);
        int result = count - currentLangCount;
        return (float) result;
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
