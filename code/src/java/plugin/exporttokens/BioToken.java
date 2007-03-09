/*
 * BioToken.java
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
package plugin.exporttokens;

import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;

/**
 * Handles Tokens
 *
 * BIO
 * BIO,text delimiter
 * BIO[.beforevalue[.aftervalue]]
 */
public class BioToken extends Token
{
	/** The tokenname implemented by this class. */
	public static final String TOKENNAME = "BIO";

	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	public String getTokenName()
	{
		return TOKENNAME;
	}

	/**
	 * TODO  Could expand Token itself or even create a sub class of Token so that
	 * the beforeValue and afterValue can be handled more cleanly. 
	 *
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	public String getToken(String tokenSource, PlayerCharacter pc,
		ExportHandler eh)
	{
		String beforeValue = "";
		String afterValue = "";

		if (tokenSource.length() <= 3 || tokenSource.charAt(3) == ',')
		{
			if (tokenSource.length() > 4)
			{
				afterValue = tokenSource.substring(4);
			}
		}
		else
		{
			String[] tokens = tokenSource.split("\\.");
			if (tokens.length > 1)
				beforeValue = tokens[1];
			if (tokens.length > 2)
				afterValue = tokens[2];
		}

		return beforeValue
			+ pc.getBio().replaceAll("\n", afterValue + "\n" + beforeValue)
			+ afterValue;
	}
}
