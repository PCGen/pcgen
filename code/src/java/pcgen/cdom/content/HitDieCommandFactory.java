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
package pcgen.cdom.content;

import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.LSTWriteable;
import pcgen.rules.persistence.TokenUtilities;

public class HitDieCommandFactory extends ConcretePrereqObject implements
		Comparable<HitDieCommandFactory>, LSTWriteable
{

	private final LSTWriteable owner;

	private final AbstractHitDieModifier modifier;

	public HitDieCommandFactory(LSTWriteable cl, AbstractHitDieModifier mod)
	{
		owner = cl;
		modifier = mod;
	}

	public AbstractHitDieModifier getModifier()
	{
		return modifier;
	}

	public String getLSTformat()
	{
		return owner.getLSTformat();
	}

	@Override
	public int hashCode()
	{
		return owner.hashCode() * 29 + modifier.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof HitDieCommandFactory))
		{
			return false;
		}
		HitDieCommandFactory lcf = (HitDieCommandFactory) o;
		return modifier.equals(lcf.modifier) && owner.equals(lcf.owner);
	}

	public int compareTo(HitDieCommandFactory arg0)
	{
		int i = TokenUtilities.WRITEABLE_SORTER.compare(owner, arg0.owner);
		if (i == 0)
		{
			// TODO Need to fix this - AbstractHitDieModifier should be
			// comparable?
			throw new UnsupportedOperationException();
		}
		return i;
	}

}
