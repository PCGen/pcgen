/*
 * Copyright (c) 2016-7 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.base.format.dice;

import pcgen.base.util.BasicIndirect;
import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;

/**
 * DiceFormat is a FormatManager for Dice objects.
 */
public class DiceFormat implements FormatManager<Dice>
{

	@Override
	public Dice convert(String inputStr)
	{
		int dLoc = inputStr.indexOf("d");
		if (dLoc == -1)
		{
			throw new IllegalArgumentException(
				inputStr + " is not valid for Dice");
		}
		int quantity;
		if (dLoc == 0)
		{
			quantity = 1;
		}
		else
		{
			quantity = Integer.parseInt(inputStr.substring(0, dLoc));
		}
		int sides = Integer.parseInt(inputStr.substring(dLoc + 1));
		Die d = new Die(sides);
		return new Dice(quantity, d);
	}

	@Override
	public Indirect<Dice> convertIndirect(String inputStr)
	{
		return new BasicIndirect<>(this, convert(inputStr));
	}

	@Override
	public String unconvert(Dice d)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(d.getQuantity());
		sb.append('d');
		sb.append(d.getDie().getSides());
		return sb.toString();
	}

	@Override
	public Class<Dice> getManagedClass()
	{
		return Dice.class;
	}

	@Override
	public String getIdentifierType()
	{
		return "DICE";
	}

	@Override
	public FormatManager<?> getComponentManager()
	{
		return null;
	}
	
	@Override
	public int hashCode()
	{
		return 74538940;
	}
	
	@Override
	public boolean equals(Object o)
	{
		return (o == this) || (o instanceof DiceFormat);
	}


}
