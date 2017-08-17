/*
 * Copyright 2014 (c) Tom Parker <thpr@users.sourceforge.net>
 * derived from BonusManager
 * Copyright 2009 (c) Tom Parker <thpr@users.sourceforge.net>
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
package pcgen.core.display;

import pcgen.core.BonusManager;
import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;

public final class BonusDisplay
{

	private BonusDisplay()
	{
	}

	/**
	 * Returns a String which can be used to display in the GUI
	 * 
	 * @return name
	 */
	public static String getBonusDisplayName(BonusManager.TempBonusInfo ti)
	{
		final StringBuilder buffer = new StringBuilder(50);
	
		buffer.append(ti.source);
		buffer.append(" [");
	
		Object targetObj = ti.target;
	
		if (targetObj instanceof PlayerCharacter)
		{
			buffer.append("PC");
		}
		else if (targetObj instanceof Equipment)
		{
			buffer.append(((Equipment) targetObj).getName());
		}
		else
		{
			buffer.append("NONE");
		}
	
		buffer.append(']');
	
		return buffer.toString();
	}

}
