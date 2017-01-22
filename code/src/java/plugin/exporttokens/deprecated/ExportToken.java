/*
 * ExportToken.java
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
package plugin.exporttokens.deprecated;

import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;
import pcgen.system.PCGenPropBundle;

import java.text.DateFormat;
import java.util.Date;

/**
 * Deals with Tokens:
 * 
 * EXPORT
 * EXPORT.DATE
 * EXPORT.TIME
 * EXPORT.VERSION
 */
public class ExportToken extends Token
{
	/** Token Name */
	public static final String TOKENNAME = "EXPORT";

	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	@Override
	public String getTokenName()
	{
		return TOKENNAME;
	}

	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	@Override
	public String getToken(String tokenSource, PlayerCharacter pc,
		ExportHandler eh)
	{
		String exportString = "";

		if ("EXPORT.DATE".equals(tokenSource))
		{
			exportString = DateFormat.getDateInstance().format(new Date());
		}
		else if ("EXPORT.TIME".equals(tokenSource))
		{
			exportString = DateFormat.getTimeInstance().format(new Date());
		}
		else if ("EXPORT.VERSION".equals(tokenSource))
		{
			exportString = PCGenPropBundle.getVersionNumber();
		}

		return exportString;
	}
}
