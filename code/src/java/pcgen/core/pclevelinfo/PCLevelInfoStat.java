/*
 * Copyright 2001 (C) Greg Bingleman <byngl@hotmail.com>
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
package pcgen.core.pclevelinfo;

import java.io.Serializable;

import pcgen.core.PCStat;

/**
 * ???
 */
public final class PCLevelInfoStat implements Serializable
{
	private final PCStat stat;
	private int mod = 0;

	PCLevelInfoStat(final PCStat pcstat, final int argMod)
	{
		super();
		stat = pcstat;
		mod = argMod;
	}

	@Override
	public String toString()
	{
		return stat.getKeyName() + "=" + Integer.toString(mod);
	}

	public PCStat getStat()
	{
		return stat;
	}

	public int getStatMod()
	{
		return mod;
	}

	void modifyStat(final int argMod)
	{
		mod += argMod;
	}
}
