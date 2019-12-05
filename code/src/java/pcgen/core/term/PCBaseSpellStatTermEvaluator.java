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
 * Created 03-Aug-2008 22:55:01
 */

package pcgen.core.term;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;

public class PCBaseSpellStatTermEvaluator extends BasePCTermEvaluator implements TermEvaluator
{

    private final String source;

    public PCBaseSpellStatTermEvaluator(String originalText, String source)
    {
        this.originalText = originalText;
        this.source = source;
    }

    @Override
    public Float resolve(PlayerCharacter pc)
    {
        PCClass pcClass = pc.getClassKeyed(source);

        if (pcClass == null)
        {
            pcClass =
                    Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(PCClass.class, source);
        }
        //null safe to pass in
        return (float) getBaseSpellStatBonus(pc, pcClass);
    }

    private int getBaseSpellStatBonus(PlayerCharacter pc, PCClass pcClass)
    {
        if (pcClass == null)
        {
            return 0;
        }

        int baseSpellStat = 0;
        CDOMSingleRef<PCStat> ssref = pcClass.get(ObjectKey.SPELL_STAT);
        if (ssref != null)
        {
            PCStat ss = ssref.get();
            baseSpellStat = pc.getTotalStatFor(ss);
            baseSpellStat += (int) pc.getTotalBonusTo("STAT", "BASESPELLSTAT");
            baseSpellStat += (int) pc.getTotalBonusTo("STAT", "BASESPELLSTAT;CLASS=" + pcClass.getKeyName());
            baseSpellStat += (int) pc.getTotalBonusTo("STAT", "CAST." + ss.getKeyName());
            baseSpellStat = pc.getModForNumber(baseSpellStat, ss);
        }
        return baseSpellStat;
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
