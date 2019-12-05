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
 * Created 03 October 2008
 */

package pcgen.core.term;

import pcgen.core.PlayerCharacter;
import pcgen.core.character.CharacterSpell;
import pcgen.core.spell.Spell;

public abstract class BaseEQTermEvaluator
{
    protected String originalText;

    public String evaluate(PlayerCharacter pc)
    {
        return "0.0";
    }

    public String evaluate(PlayerCharacter pc, final Spell aSpell)
    {
        return "0.0";
    }

    public Float resolve(PlayerCharacter pc)
    {
        return TermUtil.convertToFloat(originalText, evaluate(pc));
    }

    public Float resolve(PlayerCharacter pc, final CharacterSpell aSpell)
    {
        return TermUtil.convertToFloat(originalText, evaluate(pc, aSpell == null ? null : aSpell.getSpell()));
    }
}
