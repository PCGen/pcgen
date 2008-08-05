package pcgen.core.analysis;

import java.util.List;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.helper.StatLock;
import pcgen.core.PCStat;
import pcgen.core.PObject;
import pcgen.core.SettingsHandler;

public class RaceStat
{

	public static boolean isUnlocked(final int statIdx, PObject po)
	{
		final List<PCStat> statList = SettingsHandler.getGame()
				.getUnmodifiableStatList();

		if ((statIdx < 0) || (statIdx >= statList.size()))
		{
			return false;
		}

		return po.containsInList(ListKey.UNLOCKED_STATS, statList.get(statIdx));
	}

	public static boolean isNonAbility(final int statIdx, PObject po)
	{
		final List<PCStat> statList = SettingsHandler.getGame()
				.getUnmodifiableStatList();

		if ((statIdx < 0) || (statIdx >= statList.size()))
		{
			return true;
		}

		// An unlock will always override a lock, so check it first
		if (isUnlocked(statIdx, po))
		{
			return false;
		}

		PCStat stat = statList.get(statIdx);
		for (StatLock sl : po.getSafeListFor(ListKey.STAT_LOCKS))
		{
			if (sl.getLockedStat().equals(stat))
			{
				if (sl.getLockValue().toString().equals("10"))
				{
					return true;
				}
			}
		}

		return false;
	}

}
