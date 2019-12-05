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
 * Created 04-Aug-2008 02:25:32
 */

package pcgen.core.term;

import pcgen.cdom.base.Constants;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.character.CharacterSpell;

public class PCCasterLevelTotalTermEvaluator extends BasePCTermEvaluator implements TermEvaluator
{

    public PCCasterLevelTotalTermEvaluator(String originalText)
    {
        this.originalText = originalText;
    }

    // Makes no sense without a spell
    @Override
    public Float resolve(PlayerCharacter pc)
    {
        return 0.0f;
    }

    @Override
    public Float resolve(PlayerCharacter pc, final CharacterSpell aSpell)
    {

        int iLev = 0;

        for (PCClass pcClass : pc.getDisplay().getClassSet())
        {
            if (!pcClass.getSpellType().equals(Constants.NONE))
            {
                final String classKey = pcClass.getKeyName();

                final int pcBonus = (int) pc.getTotalBonusTo("PCLEVEL", classKey);
                final int castBonus = (int) pc.getTotalBonusTo("CASTERLEVEL", classKey);
                final int iClass = (castBonus == 0) ? pc.getDisplay().getLevel(pcClass) : 0;

                String spellType = pcClass.getSpellType();

                iLev += pc.getTotalCasterLevelWithSpellBonus(aSpell, (aSpell == null) ? null : aSpell.getSpell(),
                        spellType, classKey, iClass + pcBonus);
            }
        }

        return (float) iLev;
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
