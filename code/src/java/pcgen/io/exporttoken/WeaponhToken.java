/*
 * WeaponhToken.java
 * Copyright 2003 (C) Devon Jones <soulcatcher@evilsoft.org>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on December 15, 2003, 12:21 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.io.exporttoken;

import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;

import java.util.StringTokenizer;
import pcgen.core.Globals;
import pcgen.core.WeaponProf;
import pcgen.util.PropertyFactory;

/**
 * <code>WeaponhToken</code>.
 *
 * @author	binkley
 * @version	$Revision$
 */
public class WeaponhToken extends WeaponToken
{
	/** Weaponh token */
	public static final String TOKEN_NAME = "WEAPONH";

	/**
	 * Gets the token name
	 *
	 * @return The token name.
	 * @see	pcgen.io.exporttoken.Token#getTokenName()
	 */
	public String getTokenName()
	{
		return TOKEN_NAME;
	}

	/**
	 * Get the value of the supplied output token.
	 *
	 * @param tokenSource The full source of the token
	 * @param pc The character to retrieve the value for.
	 * @param eh The ExportHandler that is managing the export
	 * 						(may be null for a once off conversion).
	 * @return The value of the token.
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
	{
		StringTokenizer aTok = new StringTokenizer(tokenSource, ".", false);
		//Weapono Token
		aTok.nextToken();

		Equipment eq = getWeaponEquipment(pc);

		if(eq != null) {
			return getWeaponToken(pc, eq, aTok);
		}
		else if (eh != null && eh.getExistsOnly())
		{
			eh.setNoMoreItems(true);
			if (eh.getCheckBefore())
			{
				eh.setCanWrite(false);
			}
		}
		return "";
	}

	/**
	 * Create a fake Unarmed Strike equipment so we don't need it in the .lst files anymore
	 *
	 * @param pc The character used to generate the size.
	 * @return The Unarmed Strike equipment.
	 */
	public static Equipment getWeaponEquipment(PlayerCharacter pc)
	{
		// Creating a fake Unarmed Strike equipment so we
		// don't need it in the .lst files anymore
		WeaponProf wp = Globals.getWeaponProfKeyed("Unarmed Strike");
		if (wp == null)
		{
			wp = new WeaponProf();
			wp.setName(PropertyFactory.getString("Equipment.UnarmedStrike"));
			wp.setKeyName("Unarmed Strike");
			wp.setTypeInfo("Simple");
			Globals.addWeaponProf(wp);
		}
		Equipment eq = new Equipment();
		eq.setName(PropertyFactory.getString("Equipment.UnarmedStrike"));
		eq.setKeyName("KEY_Unarmed Strike");
		eq.setProfName("Unarmed Strike");
		eq.setOutputName(PropertyFactory.getString("Equipment.UnarmedStrike"));
		eq.setTypeInfo("Weapon.Melee.Simple.Unarmed.Subdual.Standard.Monk.Bludgeoning");
		eq.setWield("Light");
		eq.setCost("0", true);
		eq.setWeight("0");
		eq.setDamage("1d1");
		eq.setCritMult(2);
		eq.setCritRange("1");
		eq.setModifiersAllowed(false);
		eq.setModifiersRequired(false);
		eq.setSize(pc.getSize(), true);

		return eq;
	}
}

