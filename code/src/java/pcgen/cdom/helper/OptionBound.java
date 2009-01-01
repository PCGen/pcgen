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
import pcgen.core.PlayerCharacter;

public class OptionBound
{
	private Formula minOption;
	private Formula maxOption;

	public OptionBound(Formula min, Formula max)
	{
		minOption = min;
		maxOption = max;
	}

	public boolean isOption(PlayerCharacter pc, int val)
	{
		if (minOption == null || minOption.resolve(pc, "").intValue() <= val)
		{
			if (maxOption == null
					|| maxOption.resolve(pc, "").intValue() >= val)
			{
				return true;
			}
		}
		return false;
	}

	public Formula getOptionMin()
	{
		return minOption;
	}

	public Formula getOptionMax()
	{
		return maxOption;
	}
}