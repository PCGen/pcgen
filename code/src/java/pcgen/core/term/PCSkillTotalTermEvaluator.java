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
 * Created 09-Aug-2008 13:29:52
 */

package pcgen.core.term;

import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.analysis.SkillModifier;
import pcgen.core.analysis.SkillRankControl;

public class PCSkillTotalTermEvaluator extends BasePCTermEvaluator implements TermEvaluator
{
    private final String total;

    public PCSkillTotalTermEvaluator(String originalText, String total)
    {
        this.originalText = originalText;
        this.total = total;
    }

    @Override
    public Float resolve(PlayerCharacter pc)
    {
        Skill aSkill = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(Skill.class, total);

        Float totalRank = SkillRankControl.getTotalRank(pc, aSkill);
        totalRank += SkillModifier.modifier(aSkill, pc);

        return (aSkill == null) ? 0.0f : totalRank;
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
