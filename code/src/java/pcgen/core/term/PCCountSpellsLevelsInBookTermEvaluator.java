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
 * Created 07-Aug-2008 23:00:55
 */

package pcgen.core.term;

import java.util.List;

import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.character.CharacterSpell;

public class PCCountSpellsLevelsInBookTermEvaluator extends BasePCTermEvaluator implements TermEvaluator
{
    final int classNum;
    final int sbookNum;

    public PCCountSpellsLevelsInBookTermEvaluator(String originalText, int[] nums)
    {
        this.originalText = originalText;
        classNum = nums[0];
        sbookNum = nums[1];
    }

    @Override
    public Float resolve(PlayerCharacter pc)
    {
        String bookName = Globals.getDefaultSpellBook();

        if (sbookNum > 0)
        {
            bookName = pc.getDisplay().getSpellBookNames().get(sbookNum);
        }

        final PObject pObj = pc.getSpellClassAtIndex(classNum);

        if (pObj != null)
        {
            for (int levelNum = 0;levelNum >= 0;++levelNum)
            {
                final List<CharacterSpell> aList = pc.getCharacterSpells(pObj, null, bookName, levelNum);

                if (aList.isEmpty())
                {
                    return (float) levelNum;
                }
            }
        }

        return 0.0f;
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
