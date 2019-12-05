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
 * Created 09-Aug-2008 13:07:38
 */

package pcgen.core.term;

import pcgen.cdom.enumeration.MovementType;
import pcgen.core.display.CharacterDisplay;
import pcgen.core.spell.Spell;

public class PCMovementTermEvaluator extends BasePCDTermEvaluator implements TermEvaluator
{
    private final MovementType movement;

    public PCMovementTermEvaluator(String originalText, String movement)
    {
        this.originalText = originalText;
        this.movement = MovementType.getConstant(movement);
    }

    @Override
    public Float resolve(CharacterDisplay display)
    {
        return TermUtil.convertToFloat(originalText, evaluate(display));
    }

    @Override
    public String evaluate(CharacterDisplay display)
    {
        return String.valueOf(display.movementOfType(movement));
    }

    @Override
    public String evaluate(CharacterDisplay display, Spell aSpell)
    {
        return String.valueOf(display.movementOfType(movement));
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
