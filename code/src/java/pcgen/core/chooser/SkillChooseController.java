/*
 * Missing License Header, Copyright 2016 (C) Andrew Maitland <amaitland@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.core.chooser;

import java.util.Objects;

import pcgen.core.Ability;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.analysis.SkillRankControl;

public class SkillChooseController extends ChooseController<Ability>
{
    private final Skill skill;
    private final PlayerCharacter pc;

    public SkillChooseController(Skill sk, PlayerCharacter aPC)
    {
        Objects.requireNonNull(sk, "Skill cannot be null for SkillChooseController");
        skill = sk;
        pc = aPC;
    }

    @Override
    public int getPool()
    {
        return SkillRankControl.getTotalRank(pc, skill).intValue() - pc.getAssociationList(skill).size();
    }

    @Override
    public boolean isMultYes()
    {
        return true;
    }

    @Override
    public int getTotalChoices()
    {
        return Integer.MAX_VALUE;
    }
}
