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
 * Created 03-Oct-2008 02:42:03
 */

package pcgen.core.term;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.util.CControl;
import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.util.Logging;

public class EQSpellFailureTermEvaluator extends BaseEQTermEvaluator implements TermEvaluator
{
    public EQSpellFailureTermEvaluator(String expressionString)
    {
        this.originalText = expressionString;
    }

    @Override
    public Float resolve(Equipment eq, boolean primary, PlayerCharacter pc)
    {
        return TermUtil.convertToFloat(originalText, evaluate(eq, primary, pc));
    }

    @Override
    public String evaluate(Equipment eq, boolean primary, PlayerCharacter pc)
    {
        if (pc.hasControl(CControl.EQSPELLFAILURE))
        {
            Logging.errorPrint("EQSPELLFAIL term is disabled " + "when EQSPELLFAILURE control is used");
        }
        return String.valueOf(eq.getSafe(IntegerKey.SPELL_FAILURE));
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
