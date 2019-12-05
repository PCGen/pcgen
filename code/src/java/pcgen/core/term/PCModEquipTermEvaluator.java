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
 * Created 09-Aug-2008 12:17:26
 */

package pcgen.core.term;

import pcgen.cdom.util.CControl;
import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.io.exporttoken.EqToken;
import pcgen.util.Logging;

public class PCModEquipTermEvaluator extends BasePCTermEvaluator implements TermEvaluator
{
    private final String modEq;

    public PCModEquipTermEvaluator(String originalText, String modEq)
    {
        this.originalText = originalText;
        this.modEq = modEq;
    }

    @Override
    public Float resolve(PlayerCharacter pc)
    {
        return (float) process(pc);
    }

    private int process(PlayerCharacter pc)
    {
        if (modEq.equals("AC"))
        {
            return pc.modToACFromEquipment();
        }
        if (modEq.equals("ACCHECK"))
        {
            if (pc.hasControl(CControl.PCACCHECK))
            {
                Logging.errorPrint("Term MODEQUIPACCHECK is not supported " + "when PCACCHECK code control is used");
            } else
            {
                return pc.processOldAcCheck();
            }
        }
        if (modEq.equals("MAXDEX"))
        {
            if (pc.hasControl(CControl.PCMAXDEX))
            {
                Logging.errorPrint("Term MODEQUIPMAXDEX is not supported " + "when PCMAXDEX code control is used");
            } else
            {
                return pc.processOldMaxDex();
            }
        }
        if (modEq.equals("SPELLFAILURE"))
        {
            if (pc.hasControl(CControl.PCSPELLFAILURE))
            {
                Logging.errorPrint(
                        "Term MODEQUIPSPELLFAILURE is not supported " + "when PCSPELLFAILURE code control is used");
            } else
            {
                int bonus = 0;
                for (Equipment eq : pc.getEquippedEquipmentSet())
                {
                    bonus += EqToken.getSpellFailureTokenInt(pc, eq);
                }
                return bonus + (int) pc.getTotalBonusTo("MISC", "SPELLFAILURE");
            }
        }
        return 0;
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
