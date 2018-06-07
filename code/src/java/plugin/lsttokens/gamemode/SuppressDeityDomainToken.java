/*
 * Copyright 2018 (C) Thomas Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;
import pcgen.util.Logging;

/**
 * Token for SUPPRESSDEITYDOMAIN
 */
public class SuppressDeityDomainToken implements GameModeLstToken
{

	@Override
	public String getTokenName()
	{
		return "SUPPRESSDEITYDOMAIN";
	}

	@Override
	public boolean parse(GameMode gameMode, String value, URI source)
	{
		Boolean suppressDeityDomain;
		char firstChar = value.charAt(0);
		if (firstChar == 'y' || firstChar == 'Y')
		{
			if (value.length() > 1 && !value.equalsIgnoreCase("YES"))
			{
				Logging.errorPrint(
					"You should use 'YES' as the " + getTokenName() + ": " + value);
				return false;
			}
			suppressDeityDomain = Boolean.TRUE;
		}
		else
		{
			if (firstChar != 'N' && firstChar != 'n')
			{
				Logging.errorPrint("You should use 'YES' or 'NO' as the " + getTokenName()
					+ ": " + value);
				return false;
			}
			if (value.length() > 1 && !value.equalsIgnoreCase("NO"))
			{
				Logging.errorPrint("You should use 'YES' or 'NO' as the " + getTokenName()
					+ ": " + value);
				return false;
			}
			suppressDeityDomain = Boolean.FALSE;
		}
		gameMode.hasDeityDomain(!suppressDeityDomain);
		return true;
	}
}
