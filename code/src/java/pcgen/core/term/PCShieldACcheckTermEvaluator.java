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
 * Created 03-Aug-2008 22:38:25
 */

package pcgen.core.term;

import pcgen.cdom.util.CControl;
import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.util.Logging;

public class PCShieldACcheckTermEvaluator extends BasePCTermEvaluator implements TermEvaluator
{

    public PCShieldACcheckTermEvaluator(String originalText)
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

        Float maxCheck = 0.0f;

        for (Equipment eq : pc.getEquipmentOfType("Shield", 1))
        {
            maxCheck += eq.preFormulaAcCheck(pc);
        }

        return maxCheck;
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
