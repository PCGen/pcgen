/*
 * UnitSetToken.java
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
 */
package plugin.exporttokens.deprecated;

import pcgen.core.Globals;
import pcgen.core.display.CharacterDisplay;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.AbstractExportToken;

/**
 * {@code UnitSetToken}.
 *
 * Token formats are:
 * 
 * UNITSET
 * UNITSET.HEIGHTUNIT
 * UNITSET.DISTANCEUNIT
 * UNITSET.WEIGHTUNIT
 */
public class UnitSetToken extends AbstractExportToken
{
	@Override
	public String getTokenName()
	{
		return "UNITSET";
	}

	@Override
	public String getToken(String tokenSource, CharacterDisplay display, ExportHandler eh)
	{
		String retString = "";

		if ("UNITSET".equals(tokenSource))
		{
			retString = Globals.getGameModeUnitSet().getDisplayName();
		}
		else if ("UNITSET.HEIGHTUNIT".equals(tokenSource))
		{
			retString = Globals.getGameModeUnitSet().getHeightUnit();
		}
		else if ("UNITSET.DISTANCEUNIT".equals(tokenSource))
		{
			retString = Globals.getGameModeUnitSet().getDistanceUnit();
		}
		else if ("UNITSET.WEIGHTUNIT".equals(tokenSource))
		{
			retString = Globals.getGameModeUnitSet().getWeightUnit();
		}

		return retString;
	}
}
