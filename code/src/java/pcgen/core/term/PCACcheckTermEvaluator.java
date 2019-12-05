/*
 * Copyright (c) 2008 Andrew Wilson <nuance@users.sourceforge.net>.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package pcgen.core.term;

import pcgen.cdom.util.CControl;
import pcgen.core.PlayerCharacter;
import pcgen.util.Logging;

public class PCACcheckTermEvaluator extends BasePCTermEvaluator implements TermEvaluator
{

    PCACcheckTermEvaluator(final String originalText)
    {
        this.originalText = originalText;
    }

    @Override
    public Float resolve(PlayerCharacter pc)
    {
        if (pc.hasControl(CControl.EQACCHECK))
        {
            Logging.errorPrint(
                    originalText + " term is deprecated (does not function)" + " when EQACCHECK CodeControl is used");
        }
        int maxCheck = pc.getEquipmentOfType("Armor", 1).stream().mapToInt(eq -> eq.preFormulaAcCheck(pc)).sum();

        maxCheck += pc.getEquipmentOfType("Shield", 1).stream().mapToInt(eq -> eq.preFormulaAcCheck(pc)).sum();

        return (float) maxCheck;
    }

    @Override
    public boolean isSourceDependant()
    {
        return false;
    }

    public static boolean isStatic()
    {
        return false;
    }
}
