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
 * Created 09-Aug-2008 13:13:10
 */

package pcgen.core.term;

import pcgen.core.PlayerCharacter;
import pcgen.core.spell.Spell;

public class PCSizeTermEvaluator extends BasePCTermEvaluator implements TermEvaluator
{
    public PCSizeTermEvaluator(String originalText)
    {
        this.originalText = originalText;
    }

    // This makes no sense as part of a numeric evaluator
    @Override
    public Float resolve(PlayerCharacter pc)
    {
        return TermUtil.convertToFloat(originalText, evaluate(pc));
    }

    @Override
    public String evaluate(PlayerCharacter pc)
    {
        return String.valueOf(pc.sizeInt());
    }

    @Override
    public String evaluate(PlayerCharacter pc, Spell aSpell)
    {
        return String.valueOf(pc.sizeInt());
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
