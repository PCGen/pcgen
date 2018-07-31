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
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.PCStat;

/**
 * A StatLock object represents a PCStat locked to a specific value (that value
 * may be variable, as it is represented by a Formula)
 */
public class StatLock
{

	/**
	 * The PCStat to be locked
	 */
	private final CDOMSingleRef<PCStat> lockedStat;

	/**
	 * The Formula indicating the value to which the PCStat should be locked
	 */
	private final Formula lockValue;

	/**
	 * Constructs a new StatLock, identifying the PCStat to be locked to the
	 * given Formula
	 * 
	 * @param stat
	 *            The PCStat to be locked
	 * 
	 * @param formula
	 *            The Formula indicating the value to which the PCStat should be
	 *            locked
	 */
	public StatLock(CDOMSingleRef<PCStat> stat, Formula formula)
	{
		if (stat == null)
		{
			throw new IllegalArgumentException("PCStat for LockStat may not be null");
		}
		if (formula == null)
		{
			throw new IllegalArgumentException("Formula for LockStat may not be null");
		}
		lockedStat = stat;
		lockValue = formula;
	}

	/**
	 * Returns the PCStat that should be locked
	 * 
	 * @return the PCStat that should be locked
	 */
	public PCStat getLockedStat()
	{
		return lockedStat.get();
	}

	public String getLSTformat()
	{
		return lockedStat.getLSTformat(false);
	}

	/**
	 * Returns the Formula indicating the value to which the PCStat identified
	 * in this StatLock should be locked
	 * 
	 * @return The Formula indicating the value to which the PCStat identified
	 *         in this StatLock should be locked
	 */
	public Formula getLockValue()
	{
		return lockValue;
	}

	/**
	 * Consistent-with-equals implementation of hashCode
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return lockValue.hashCode();
	}

	/**
	 * Returns true if the given Object is a StatLock object with equal PCStat
	 * and lock value (Formula)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof StatLock)
		{
			StatLock other = (StatLock) obj;
			return lockValue.equals(other.lockValue) && lockedStat.equals(other.lockedStat);
		}
		return false;
	}
}
