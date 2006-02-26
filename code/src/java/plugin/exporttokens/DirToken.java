/*
 * DirToken.java
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
 * Current Ver: $Revision: 1.4 $
 * Last Editor: $Author: karianna $
 * Last Edited: $Date: 2005/11/16 15:54:28 $
 *
 */
package plugin.exporttokens;

import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;
import pcgen.util.Logging;

import java.util.StringTokenizer;

/**
 * Handle the DirToken which allows the output of the various paths
 * selected in preferences. Syntax is:
 * DIR.PCGEN
 * DIR.TEMPLATES
 * DIR.PCG
 * DIR.HTML
 * DIR.TEMP
 *
 * Last Editor: $Author: karianna $
 * Last Edited: $Date: 2005/11/16 15:54:28 $
 *
 * @version $Revision: 1.4 $
 */
public class DirToken extends Token
{
	/** The Token Name */
	public static final String TOKENNAME = "DIR";

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
		aTok.nextToken();

		String dirType = "";

		if (aTok.hasMoreTokens())
		{
			dirType = aTok.nextToken();
		}

		if ("PCGEN".equals(dirType))
		{
			retString = getPCGenToken();
		}
		else if ("TEMPLATES".equals(dirType))
		{
			retString = getTemplatesToken();
		}
		else if ("PCG".equals(dirType))
		{
			retString = getPcgToken();
		}
		else if ("HTML".equals(dirType))
		{
			retString = getHtmlToken();
		}
		else if ("TEMP".equals(dirType))
		{
			retString = getTempToken();
		}
		else
		{
			Logging.errorPrint("DIR: Unknown Dir: " + dirType);
			retString = dirType;
		}

		return retString;
	}

	/**
	 * Get the HTML sub token
	 * @return HTML sub token
	 */
	public static String getHtmlToken()
	{
		return SettingsHandler.getHTMLOutputSheetPath();
	}

	/**
	 * Get the PCGEN sub token
	 * @return PCGEN sub token
	 */
	public static String getPCGenToken()
	{
		return SettingsHandler.getPcgenSystemDir().getAbsolutePath();
	}

	/**
	 * Get the PCG sub token
	 * @return PCG sub token
	 */
	public static String getPcgToken()
	{
		return SettingsHandler.getPcgPath().getAbsolutePath();
	}

	/**
	 * Get the TEMP sub token
	 * @return TEMP sub token
	 */
	public static String getTempToken()
	{
		return SettingsHandler.getTempPath().getAbsolutePath();
	}

	/**
	 * Get the TEMPLATES sub token
	 * @return TEMPLATES sub token
	 */
	public static String getTemplatesToken()
	{
		return SettingsHandler.getPcgenOutputSheetDir().getAbsolutePath();
	}
}
