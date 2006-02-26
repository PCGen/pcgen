/*
 * Token.java
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
 * Current Ver: $Revision: 1.13 $
 * Last Editor: $Author: karianna $
 * Last Edited: $Date: 2006/02/13 15:47:26 $
 *
 */
package pcgen.io.exporttoken;

import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;

import java.util.StringTokenizer;

/**
 * The Abstract Token class for Export Tokens
 */
public abstract class Token
{

	/**
	 * True if the token is UTF-8 encoded
	 * @return True if the token is UTF-8 encoded
	 */
	public boolean isEncoded()
	{
		return true;
	}

	/**
	 * Get Token name
	 * @return token name
	 */
	public abstract String getTokenName();

	/**
	 * Get the value of the supplied output token.
	 *
	 * @param tokenSource The full source of the token e.g. SKILL.0.MISC
	 * @param pc The character to retrieve the value for.
	 * @param eh The ExsportHandler that is managing the export
	 * 						(may be null for a once off conversion).
	 * @return The value of the token.
	 */
	public abstract String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh);

	protected static int getIntToken(StringTokenizer tok, int defaultVal)
	{
		int retInt = defaultVal;
		if (tok.hasMoreTokens())
		{
			retInt = getIntToken(tok.nextToken(), defaultVal);
		}
		return retInt;
	}

	protected static int getIntToken(String token, int defaultVal)
	{
		int retInt = defaultVal;
		try
		{
			retInt = Integer.parseInt(token);
		}
		catch (NumberFormatException e)
		{
		    // TODO - This exception needs to be handled
		}
		return retInt;
	}

	/**
	 * Replaces any end-of-line occurrences in the supplied string with
	 * the supplied delimiter.
	 *
	 * @param sString The string to be converted.
	 * @param sDelim The delimiter to be used instead of EOL.
	 * @return The converted string.
	 */
	public static String replaceWithDelimiter(String sString, String sDelim)
	{
		final StringTokenizer bTok = new StringTokenizer(sString, "\r\n", false);
		StringBuffer retValue = new StringBuffer();

		while (bTok.hasMoreTokens())
		{
			retValue.append(bTok.nextToken());

			if (bTok.hasMoreTokens())
			{
				retValue.append(sDelim);
			}
		}

		return retValue.toString();
	}
}
