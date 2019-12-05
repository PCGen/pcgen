/*
 * Copyright 2005 (C) Aaron Divinsky <boomer70@yahoo.com>
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
package pcgen.core.kit;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import pcgen.base.formula.Formula;
import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Kit;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.pclevelinfo.PCLevelInfo;

/**
 * KitStat
 */
public class KitStat extends BaseKit
{
    private final Map<CDOMSingleRef<PCStat>, Formula> statMap = new HashMap<>();

    @Override
    public String toString()
    {
        Set<String> set = new TreeSet<>();
        for (Map.Entry<CDOMSingleRef<PCStat>, Formula> me : statMap.entrySet())
        {
            set.add(me.getKey().getLSTformat(false) + '=' + me.getValue());
        }
        return StringUtil.join(set, Constants.PIPE);
    }

    @Override
    public boolean testApply(Kit aKit, PlayerCharacter aPC, List<String> warnings)
    {
        for (Map.Entry<CDOMSingleRef<PCStat>, Formula> me : statMap.entrySet())
        {
            PCStat mapStat = me.getKey().get();
            int sVal = me.getValue().resolve(aPC, "").intValue();
            for (PCStat currentStat : aPC.getStatSet())
            {
                if (!aPC.isNonAbility(currentStat) && currentStat.equals(mapStat))
                {
                    aPC.setStat(currentStat, sVal);
                    if ("INT".equals(currentStat.getKeyName()))
                    {
                        recalculateSkillPoints(aPC);
                    }
                    break;
                }
            }
        }
        return true;
    }

    @Override
    public void apply(PlayerCharacter aPC)
    {
        testApply(null, aPC, null);
    }

    @Override
    public String getObjectName()
    {
        return "Stats";
    }

    private void recalculateSkillPoints(PlayerCharacter aPC)
    {
        final Collection<PCClass> classes = aPC.getClassSet();
        aPC.calcActiveBonuses();
        if (classes != null && !classes.isEmpty())
        {
            for (PCClass pcClass : classes)
            {
                aPC.setSkillPool(pcClass, 0);
                // We don't limit this to MOD_TO_SKILLS classes as they may manually include the INT bonus in the
                // skills.
                for (int j = 0;j < aPC.getLevelInfoSize();j++)
                {
                    final PCLevelInfo pcl = aPC.getLevelInfo(j);
                    if (pcl.getClassKeyName().equals(pcClass.getKeyName()))
                    {
                        final int spMod = aPC.recalcSkillPointMod(pcClass, j + 1);
                        int alreadySpent = pcl.getSkillPointsGained(aPC) - pcl.getSkillPointsRemaining();
                        pcl.setSkillPointsGained(aPC, spMod);
                        pcl.setSkillPointsRemaining(pcl.getSkillPointsGained(aPC) - alreadySpent);
                        Integer currentPool = aPC.getSkillPool(pcClass);
                        int newSkillPool = (currentPool == null ? 0 : currentPool) + spMod;
                        aPC.setSkillPool(pcClass, newSkillPool);
                    }
                }
            }
        }
    }

    public void addStat(CDOMSingleRef<PCStat> stat, Formula statValue)
    {
        if (statMap.put(stat, statValue) != null)
        {
            throw new IllegalArgumentException("Cannot redefine stat: " + stat);
        }
    }

    public boolean isEmpty()
    {
        return statMap.isEmpty();
    }
}
