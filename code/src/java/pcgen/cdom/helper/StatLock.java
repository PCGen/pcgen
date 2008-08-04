package pcgen.cdom.helper;

import pcgen.base.formula.Formula;
import pcgen.core.PCStat;

public class StatLock
{

	private final PCStat lockedStat;
	private final Formula lockValue;

	public StatLock(PCStat stat, Formula f)
	{
		lockedStat = stat;
		lockValue = f;
	}

	public PCStat getLockedStat()
	{
		return lockedStat;
	}

	public Formula getLockValue()
	{
		return lockValue;
	}

	@Override
	public int hashCode()
	{
		return lockValue.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof StatLock)
		{
			StatLock other = (StatLock) o;
			return lockValue.equals(other.lockValue)
					&& lockedStat.equals(other.lockedStat);
		}
		return false;
	}
}
