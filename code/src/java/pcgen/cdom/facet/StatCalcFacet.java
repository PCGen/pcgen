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
package pcgen.cdom.facet;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.facet.analysis.NonStatStatFacet;
import pcgen.cdom.facet.analysis.NonStatToStatFacet;
import pcgen.cdom.facet.analysis.StatLockFacet;
import pcgen.cdom.facet.analysis.StatMaxValueFacet;
import pcgen.cdom.facet.analysis.StatMinValueFacet;
import pcgen.cdom.facet.analysis.UnlockedStatFacet;
import pcgen.core.PCStat;

public class StatCalcFacet
{

    private StatValueFacet statValueFacet = FacetLibrary.getFacet(StatValueFacet.class);
    private StatLockFacet statLockFacet = FacetLibrary.getFacet(StatLockFacet.class);
    private UnlockedStatFacet unlockedStatFacet = FacetLibrary.getFacet(UnlockedStatFacet.class);
    private NonStatStatFacet nonStatStatFacet = FacetLibrary.getFacet(NonStatStatFacet.class);
    private NonStatToStatFacet nonStatToStatFacet = FacetLibrary.getFacet(NonStatToStatFacet.class);
    private StatMinValueFacet statMinValueFacet = FacetLibrary.getFacet(StatMinValueFacet.class);
    private StatMaxValueFacet statMaxValueFacet = FacetLibrary.getFacet(StatMaxValueFacet.class);

    private VariableCheckingFacet variableCheckingFacet = FacetLibrary.getFacet(VariableCheckingFacet.class);
    private BonusCheckingFacet bonusCheckingFacet = FacetLibrary.getFacet(BonusCheckingFacet.class);

    /**
     * Calculate the total for the requested stat. If equipment or temporary
     * bonuses should be excluded, getPartialStatFor should be used instead.
     *
     * @param stat The abbreviation of the stat to be calculated
     * @return The value of the stat
     */
    public int getTotalStatFor(CharID id, PCStat stat)
    {
        int y = getBaseStatFor(id, stat);

        // Check for a non stat, but only if it hasn't been reset to a stat
        if (!nonStatToStatFacet.contains(id, stat))
        {
            if (nonStatStatFacet.contains(id, stat))
            {
                return 10;
            }
        }

        int minStatValue = Integer.MIN_VALUE;
        Number val = statMinValueFacet.getStatMinValue(id, stat);
        if (val != null)
        {
            minStatValue = val.intValue();
        }

        int maxStatValue = Integer.MAX_VALUE;
        val = statMaxValueFacet.getStatMaxValue(id, stat);
        if (val != null)
        {
            maxStatValue = val.intValue();
        }

        // Only check for a lock if the stat hasn't been unlocked
        if (!unlockedStatFacet.contains(id, stat))
        {
            val = statLockFacet.getLockedStat(id, stat);
            if (val != null)
            {
                int total = val.intValue() + (int) bonusCheckingFacet.getBonus(id, "LOCKEDSTAT", stat.getKeyName());
                total = Math.min(maxStatValue, total);
                return Math.max(minStatValue, total);
            }
        }

        y += bonusCheckingFacet.getBonus(id, "STAT", stat.getKeyName());

        y = Math.min(maxStatValue, y);
        return Math.max(minStatValue, y);
    }

    public int getBaseStatFor(CharID id, PCStat stat)
    {
        // Check for a non stat, but only if it hasn't been reset to a stat
        if (!nonStatToStatFacet.contains(id, stat))
        {
            if (nonStatStatFacet.contains(id, stat))
            {
                return 10;
            }
        }

        int minStatValue = Integer.MIN_VALUE;
        Number val = statMinValueFacet.getStatMinValue(id, stat);
        if (val != null)
        {
            minStatValue = val.intValue();
        }

        int maxStatValue = Integer.MAX_VALUE;
        val = statMaxValueFacet.getStatMaxValue(id, stat);
        if (val != null)
        {
            maxStatValue = val.intValue();
        }

        // Only check for a lock if the stat hasn't been unlocked
        if (!unlockedStatFacet.contains(id, stat))
        {
            val = statLockFacet.getLockedStat(id, stat);
            if (val != null)
            {
                int base = Math.min(maxStatValue, val.intValue());
                return Math.max(minStatValue, base);
            }
        }

        int z = variableCheckingFacet.getVariableValue(id, "BASE." + stat.getKeyName()).intValue();

        if (z != 0)
        {
            z = Math.min(maxStatValue, z);
            return Math.max(minStatValue, z);
        }
        Number score = statValueFacet.get(id, stat);
        int base = Math.min(maxStatValue, score == null ? 0 : score.intValue());
        return Math.max(minStatValue, base);
    }

    public int getStatModFor(CharID id, PCStat stat)
    {
        return variableCheckingFacet
                .getVariableValue(id, stat.getSafe(FormulaKey.STAT_MOD).toString(), "STAT:" + stat.getKeyName()).intValue();
    }

    public int getModFornumber(CharID id, int aNum, PCStat stat)
    {
        return variableCheckingFacet
                .getVariableValue(id, stat.getSafe(FormulaKey.STAT_MOD).toString(), Integer.toString(aNum)).intValue();
    }

    /**
     * Retrieve the value of the stat with just the included bonuses.
     * This may exclude things such as temporary or equipment bonuses at the caller's discretion.
     *
     * @param stat             The stat to calculate the bonus for.
     * @param id               The id of the character being processed.
     * @param partialStatBonus The precalculated bonuses to be included.
     * @return The stat value.
     */
    public int getPartialStatFor(CharID id, PCStat stat, int partialStatBonus)
    {
        int statValue = getBaseStatFor(id, stat);

        statValue += partialStatBonus;
        Number val = statMinValueFacet.getStatMinValue(id, stat);
        if (val != null)
        {
            statValue = Math.max(val.intValue(), statValue);
        }

        val = statMaxValueFacet.getStatMaxValue(id, stat);
        if (val != null)
        {
            statValue = Math.min(val.intValue(), statValue);
        }
        return statValue;
    }

}
