package pcgen.cdom.facet;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.facet.analysis.StatLockFacet;
import pcgen.cdom.facet.analysis.UnlockedStatFacet;
import pcgen.core.PCStat;

public class StatCalcFacet
{

	private StatValueFacet statValueFacet = FacetLibrary
		.getFacet(StatValueFacet.class);
	private StatLockFacet statLockFacet = FacetLibrary
		.getFacet(StatLockFacet.class);
	private UnlockedStatFacet unlockedStatFacet = FacetLibrary
		.getFacet(UnlockedStatFacet.class);
	private VariableCheckingFacet variableCheckingFacet = FacetLibrary
		.getFacet(VariableCheckingFacet.class);
	private BonusCheckingFacet bonusCheckingFacet = FacetLibrary
		.getFacet(BonusCheckingFacet.class);

	/**
	 * Calculate the total for the requested stat. If equipment or temporary
	 * bonuses should be excluded, getPartialStatFor should be used instead.
	 * 
	 * @param stat
	 *            The abbreviation of the stat to be calculated
	 * @return The value of the stat
	 */
	public int getTotalStatFor(CharID id, PCStat stat)
	{
		int y = getBaseStatFor(id, stat);

		// Only check for a lock if the stat hasn't been unlocked
		if (!unlockedStatFacet.contains(id, stat))
		{
			Number val = statLockFacet.getLockedStat(id, stat);
			if (val != null)
			{
				return val.intValue();
			}
		}

		y += bonusCheckingFacet.getBonus(id, "STAT", stat.getAbb());

		return y;
	}

	public int getBaseStatFor(CharID id, PCStat stat)
	{
		// Only check for a lock if the stat hasn't been unlocked
		if (!unlockedStatFacet.contains(id, stat))
		{
			Number val = statLockFacet.getLockedStat(id, stat);
			if (val != null)
			{
				return val.intValue();
			}
		}

		int z =
				variableCheckingFacet.getVariableValue(id,
					"BASE." + stat.getAbb()).intValue();

		if (z != 0)
		{
			return z;
		}
		Integer score = statValueFacet.get(id, stat);
		return score == null ? 0 : score;
	}

	public int getStatModFor(CharID id, PCStat stat)
	{
		return variableCheckingFacet.getVariableValue(id,
			stat.getSafe(FormulaKey.STAT_MOD).toString(),
			"STAT:" + stat.getAbb()).intValue();
	}

	public int getModFornumber(CharID id, int aNum, PCStat stat)
	{
		return variableCheckingFacet.getVariableValue(id,
			stat.getSafe(FormulaKey.STAT_MOD).toString(),
			Integer.toString(aNum)).intValue();
	}

}
