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
 * Created 04-Aug-2008 17:11:19
 */

package pcgen.core.term;

import pcgen.core.display.CharacterDisplay;
import pcgen.core.spell.Spell;

public class PCVerbatimTextTermEvaluator extends BasePCDTermEvaluator implements TermEvaluator
{

    public PCVerbatimTextTermEvaluator(String originalText)
    {
        this.originalText = originalText;
    }

    @Override
    public String evaluate(CharacterDisplay display)
    {
        return originalText;
    }

    @Override
    public String evaluate(CharacterDisplay display, final Spell aSpell)
    {
        return originalText;
    }

    @Override
    public Float resolve(CharacterDisplay display)
    {
        return 0.0f;
    }

    @Override
    public boolean isSourceDependant()
    {
        return false;
    }

    public boolean isStatic()
    {
        return true;
    }
}
