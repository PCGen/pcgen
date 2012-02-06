/*
 * Copyright 2012 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.core.display;

import java.util.List;

import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.PlayerCharacterUtilities;
import pcgen.util.Delta;

public class UnarmedDamageDisplay
{

	/**
	 * Get the unarmed damage string for this PC as adjusted by the booleans
	 * passed in.
	 * 
	 * @param pc
	 * @param includeStrBonus
	 * @param adjustForPCSize
	 * @return the unarmed damage string
	 */
	public static String getUnarmedDamageString(PlayerCharacter pc,
		final boolean includeStrBonus, final boolean adjustForPCSize)
	{
		String retString = "2|1d2";

		for (PCClass pcClass : pc.getClassSet())
		{
			retString =
					PlayerCharacterUtilities.getBestUDamString(retString,
						pcClass.getUdamForLevel(pc.getLevel(pcClass), pc,
							adjustForPCSize));
		}

		int sizeInt = pc.sizeInt();
		for (List<String> unarmedDamage : pc.getUnarmedDamage())
		{
			String aDamage;
			if (unarmedDamage.size() == 1)
			{
				aDamage = unarmedDamage.get(0);
			}
			else
			{
				aDamage = unarmedDamage.get(sizeInt);
			}
			retString =
					PlayerCharacterUtilities.getBestUDamString(retString,
						aDamage);
		}
		//Test against the default for the race
		String pObjDamage = pc.getUDamForRace();
		retString =
				PlayerCharacterUtilities.getBestUDamString(retString,
					pObjDamage);

		// string is in form sides|damage, just return damage portion
		StringBuilder ret =
				new StringBuilder(
					retString.substring(retString.indexOf('|') + 1));
		if (includeStrBonus)
		{
			int sb = (int) pc.getStatBonusTo("DAMAGE", "TYPE.MELEE");
			sb += (int) pc.getStatBonusTo("DAMAGE", "TYPE=MELEE");
			if (sb != 0)
			{
				ret.append(Delta.toString(sb));
			}
		}
		return ret.toString();
	}

}
