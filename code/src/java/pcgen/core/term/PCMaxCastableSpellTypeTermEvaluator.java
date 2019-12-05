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
 * Created 09-Aug-2008 23:19:38
 */

package pcgen.core.term;

import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;

public class PCMaxCastableSpellTypeTermEvaluator extends BasePCTermEvaluator implements TermEvaluator
{
    private final String typeKey;

    public PCMaxCastableSpellTypeTermEvaluator(String originalText, String typeKey)
    {
        this.originalText = originalText;
        this.typeKey = typeKey;
    }

    @Override
    public Float resolve(PlayerCharacter pc)
    {
        float max = 0.0f;
        for (PCClass spClass : pc.getDisplay().getClassSet())
        {
            if (typeKey.equalsIgnoreCase(spClass.getSpellType()))
            {
                int cutoff = pc.getSpellSupport(spClass).getHighestLevelSpell();
                if (pc.getSpellSupport(spClass).hasCastList())
                {
                    for (int i = 0;i < cutoff;i++)
                    {
                        if (pc.getSpellSupport(spClass).getCastForLevel(i, pc) != 0)
                        {
                            max = Math.max(max, i);
                        }
                    }
                } else
                {
                    for (int i = 0;i < cutoff;i++)
                    {
                        if (pc.getSpellSupport(spClass).getKnownForLevel(i, pc) != 0)
                        {
                            max = Math.max(max, i);
                        }
                    }
                }
            }
        }

        return max;
    }

    @Override
    public boolean isSourceDependant()
    {
        return true;
    }

    public boolean isStatic()
    {
        return false;
    }
}
