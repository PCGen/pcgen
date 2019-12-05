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
 * Created 05-Oct-2008 20:57:45
 */

package pcgen.core.term;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.core.WeaponProf;

public class PCSizeIntEQTermEvaluator extends BasePCTermEvaluator implements TermEvaluator
{
    private final String source;

    public PCSizeIntEQTermEvaluator(String expressionString, String source)
    {
        this.originalText = expressionString;
        this.source = source;
    }

    @Override
    public Float resolve(PlayerCharacter pc)
    {
        int modSize = 0;

        final Equipment eq = pc.getEquipmentNamed(source);

        if (eq != null)
        {
            CDOMSingleRef<WeaponProf> ref = eq.get(ObjectKey.WEAPON_PROF);

            if (ref != null)
            {
                String profName = ref.get().getKeyName();

                modSize = (int) pc.getTotalBonusTo("WEAPONPROF=" + profName, "PCSIZE");
            }

            // loops for each equipment type
            for (String eqType : eq.typeList())
            {

                // get the type bonus (ex TYPE.MARTIAL)
                final int i = (int) pc.getTotalBonusTo("WEAPONPROF=TYPE." + eqType, "PCSIZE");

                // get the highest bonus
                if (modSize < i)
                {
                    modSize = i;
                }
            }
        }

        return (float) pc.sizeInt() + modSize;
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
