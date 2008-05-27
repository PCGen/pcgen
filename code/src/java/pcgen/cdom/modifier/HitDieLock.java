/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.modifier;

import pcgen.cdom.content.AbstractHitDieModifier;
import pcgen.cdom.content.HitDie;

public class HitDieLock extends AbstractHitDieModifier
{

	private final HitDie hitDie;

	public HitDieLock(HitDie die)
	{
		super();
		if (die == null)
		{
			throw new IllegalArgumentException("Die for HitDieLock cannot be null");
		}
		hitDie = die;
	}

	@Override
	public HitDie applyModifier(HitDie hd, Object context)
	{
		return hitDie;
	}

	@Override
	public String getLSTformat()
	{
		return Integer.toString(hitDie.getDie());
	}

	@Override
	public int hashCode()
	{
		return hitDie.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof HitDieLock
			&& ((HitDieLock) o).hitDie.equals(hitDie);
	}

}
