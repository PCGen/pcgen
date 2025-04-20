/*
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
package pcgen.io.exporttoken;

import java.text.MessageFormat;
import java.util.*;

import pcgen.cdom.enumeration.MapKey;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;

/**
 * The Abstract Token class for Export Tokens
 */
public abstract class Token
{
	/** Constant for subtoken separator */
	public static final String SUBTOKENSEP = "."; //$NON-NLS-1$

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

	/**
	 * This method takes a tokenizer and tries to return an integer value for
	 * the next available token.  If the next value doesn't exist or is not
	 * an integer the default value is returned.
	 * @param tok The StringTokenizer to pull the token from
	 * @param defaultVal The value to return if we can't get an integer
	 * @return the integer value of the next token or the defaultVal if we can't
	 * make an integer from the next token.
	 */
	protected static int getIntToken(StringTokenizer tok, int defaultVal)
	{
		int retInt = defaultVal;
		if (tok.hasMoreTokens())
		{
			retInt = getIntToken(tok.nextToken(), defaultVal);
		}
		return retInt;
	}

	/**
	 * This is a utility method to safely get an int value from a token.  If the
	 * token does not represent an integer the default value will be returned
	 * instead.
	 * @param token Integer token
	 * @param defaultVal Value to return if this is not an integer
	 * @return int value or default value if not an int
	 */
	protected static int getIntToken(String token, int defaultVal)
	{
		int retInt = defaultVal;
		try
		{
			retInt = Integer.parseInt(token);
		}
		catch (NumberFormatException e)
		{
			// Handled.  We return the default value in this case.
		}
		return retInt;
	}

	public String getInfoToken(String token, PObject po) {
		// looking for a token in the form of RACE.INFO.TAG where
		// RACE indicate which token map to check for a INFO label of TAG to return
		int i = token.indexOf(".INFO.");
		String ts = token;
		if (i>0)
			ts = token.substring(i+6).toUpperCase();
		else
			return token;
		Set<MapKey<?, ?>> keys = po.getMapKeys();
		for (MapKey<?, ?> key : keys) {
			Map<?, ?> key2 = po.getMapFor(key);
			for(Object k : key2.keySet()) {
				if (k.toString().equals(ts)) {
					MessageFormat m = (MessageFormat) key2.get(k);
					return m.toPattern();
				}
			}
		}
		return token;
	}
}
