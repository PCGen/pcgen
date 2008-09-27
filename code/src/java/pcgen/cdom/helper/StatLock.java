/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
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
