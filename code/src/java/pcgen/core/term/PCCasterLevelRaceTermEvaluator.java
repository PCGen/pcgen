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
 * Created 04-Aug-2008 01:18:35
 */

package pcgen.core.term;

import pcgen.cdom.base.Constants;
import pcgen.core.PlayerCharacter;
import pcgen.core.character.CharacterSpell;

public class PCCasterLevelRaceTermEvaluator extends BasePCTermEvaluator implements TermEvaluator
{
    private final String source;

    public PCCasterLevelRaceTermEvaluator(String originalText, String source)
    {
        this.originalText = originalText;
        this.source = "RACE." + source;
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

        final float lev = pc.getTotalCasterLevelWithSpellBonus(aSpell, aSpell.getSpell(), Constants.NONE, source, 0);
        return Math.max(lev, 0.0f);
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
