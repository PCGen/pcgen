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
 * Created 04-Aug-2008 16:43:40
 */

package pcgen.core.term;

import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.spell.Spell;

public class PCCLTermEvaluator extends BasePCTermEvaluator implements TermEvaluator
{
    private final String classKey;

    public PCCLTermEvaluator(String originalText, String aClass)
    {
        this.originalText = originalText;
        this.classKey = aClass;
    }

    @Override
    public Float resolve(PlayerCharacter pc)
    {
        return TermUtil.convertToFloat(originalText, evaluate(pc));
    }

    @Override
    public String evaluate(PlayerCharacter pc)
    {
        final PCClass aClass = pc.getClassKeyed(classKey);

        if (aClass != null)
        {
            return String.valueOf(pc.getDisplay().getLevel(aClass));
        }

        return "0";
    }

    @Override
    public String evaluate(PlayerCharacter pc, Spell aSpell)
    {
        return evaluate(pc);
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
