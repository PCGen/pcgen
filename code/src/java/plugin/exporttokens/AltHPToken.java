/*
 * AltHPToken.java
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
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;

/**
 * Class deals with ALTHP Token
 */
@Deprecated
public class AltHPToken extends Token
{
	/** Name of Token */
	public static final String TOKENNAME = "ALTHP";

	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	@Override
	public String getTokenName()
	{
		return TOKENNAME;
	}

	/**
	 * TODO: Move this into HPToken as HP.ALT
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	@Override
	public String getToken(String tokenSource, PlayerCharacter pc,
		ExportHandler eh)
	{
		return Integer.toString(getAltHPToken(pc));
	}

	/**
	 * @param pc
	 * @return token
	 */
	public static int getAltHPToken(PlayerCharacter pc)
	{
		int i;
		String solverValue = pc.getControl(CControl.ALTHP);
		if (solverValue != null)
		{
			Object val = pc.getGlobal(solverValue);
			i = ((Number) val).intValue();
		}
		else
		{
			i = (int) pc.getTotalBonusTo("HP", "ALTHP");
		}
		return i;
	}
}
