/*
 * Copyright 2007, 2008 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.formula;

import pcgen.base.formula.Formula;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.SizeAdjustment;

public class FixedSizeFormula implements Formula
{

	private final SizeAdjustment size;

	public FixedSizeFormula(SizeAdjustment s)
	{
		size = s;
	}

	@Override
	public String toString()
	{
		return size.get(StringKey.ABB);
	}

	@Override
	public int hashCode()
	{
		return size.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof FixedSizeFormula
			&& size.equals(((FixedSizeFormula) o).size);
	}

	public Integer resolve(PlayerCharacter pc, String source)
	{
		return Globals.sizeInt(size.getAbbreviation());
	}
}
