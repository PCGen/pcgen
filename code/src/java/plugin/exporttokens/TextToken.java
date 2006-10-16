/*
 * TextToken.java
 * Copyright 2006 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 * Created on October 15, 2003, 10:23 PM
 *
 * Current Ver: $Revision: 199 $
 * Last Editor: $Author: nuance $
 * Last Edited: $Date: 2006-03-15 10:11:06 +1100 (Wed, 15 Mar 2006) $
 *
 */
package plugin.exporttokens;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.StringTokenizer;

import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;
import pcgen.util.Logging;

/**
 * <code>TextToken</code> produces the output for the output token TEXT.
 * Possible tag formats are:<pre>
 * TEXT.x.y
 * </pre>
 * Where x is the action and y is the export tag to be processed.
 */
public class TextToken extends Token
{
	/** Token Name */
	public static final String TOKENNAME = "TEXT";

	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	public String getTokenName()
	{
		return TOKENNAME;
	}

	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
	{
		String retString = "";
		
		StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
		aTok.nextToken(); //this should be VAR

		String action = "";
		String varName = "";
		if (aTok.hasMoreElements())
		{
			action = aTok.nextToken();
		}
		if (aTok.hasMoreElements())
		{
			varName = aTok.nextToken();
		}
		while (aTok.hasMoreElements())
		{
			varName += "." + aTok.nextToken();
		}

		StringWriter writer = new StringWriter();
		BufferedWriter bw = new BufferedWriter(writer);
		eh.replaceToken(varName, bw, pc);
		try
		{
			bw.flush();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		retString = writer.getBuffer().toString();

		if (action.equalsIgnoreCase("UPPER")
			|| action.equalsIgnoreCase("UPPERCASE"))
		{
			retString = retString.toUpperCase();
		}
		else if (action.equalsIgnoreCase("LOWER")
			|| action.equalsIgnoreCase("LOWERCASE"))
		{
			retString = retString.toLowerCase();
		}
		else if (action.equalsIgnoreCase("NUMSUFFIX"))
		{
			int intVal = Integer.parseInt(retString);
			if (intVal % 10 == 1 && intVal % 100 != 11)
			{
				retString = "st";
			}
			else if (intVal % 10 == 2 && intVal % 100 != 12)
			{
				retString = "nd";
			}
			else if (intVal % 10 == 3 && intVal % 100 != 13)
			{
				retString = "rd";
			}
			else
			{
				retString = "th";
			}
		}
		return retString;
	}
}
