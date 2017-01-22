/*
 * SizeModToken.java
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
 *
 *
 */
package plugin.exporttokens;

import pcgen.cdom.util.CControl;
import pcgen.core.PlayerCharacter;
import pcgen.core.SizeAdjustment;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;

/**
 * SIZEMOD for export
 */
public class SizeModToken extends Token
{
	/** Token name */
	public static final String TOKENNAME = "SIZEMOD";

	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	@Override
	public String getTokenName()
	{
		return TOKENNAME;
	}

	//TODO: This should really be in the Size token as SIZE.MOD
	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	@Override
	public String getToken(String tokenSource, PlayerCharacter pc,
		ExportHandler eh)
	{
		return Integer.toString(getSizeModToken(pc));
	}

	/**
	 * Get value for SIZEMOD
	 * @param pc
	 * @return value for SIZEMOD
	 */
	public static int getSizeModToken(PlayerCharacter pc)
	{
		String sizeModDef = pc.getControl(CControl.SIZEMODDEFENSE);
		if (sizeModDef == null)
		{
			return (int) pc.getSizeAdjustmentBonusTo("COMBAT", "AC");
		}
		SizeAdjustment sa = pc.getSizeAdjustment();
		Object o = pc.getLocal(sa, sizeModDef);
		return ((Number) o).intValue();
	}
}
