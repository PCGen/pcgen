/*
 * PaperInfoToken.java
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

import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;

//PAPERINFO
public class PaperInfoToken extends Token
{
	public static final String TOKENNAME = "PAPERINFO";

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
	public String getToken(String tokenSource, PlayerCharacter pc,
		ExportHandler eh)
	{
		return getPaperInfoToken(tokenSource);
	}

	public static String getPaperInfoToken(String tokenSource)
	{
		String oString = tokenSource;
		String sourceText = tokenSource.substring(10);

		int infoType = -1;

		if (sourceText.startsWith("NAME"))
		{
			infoType = Constants.PAPERINFO_NAME;
		}
		else if (sourceText.startsWith("HEIGHT"))
		{
			infoType = Constants.PAPERINFO_HEIGHT;
		}
		else if (sourceText.startsWith("WIDTH"))
		{
			infoType = Constants.PAPERINFO_WIDTH;
		}
		else if (sourceText.startsWith("MARGIN"))
		{
			sourceText = sourceText.substring(6);

			if (sourceText.startsWith("TOP"))
			{
				infoType = Constants.PAPERINFO_TOPMARGIN;
			}
			else if (sourceText.startsWith("BOTTOM"))
			{
				infoType = Constants.PAPERINFO_BOTTOMMARGIN;
			}
			else if (sourceText.startsWith("LEFT"))
			{
				infoType = Constants.PAPERINFO_LEFTMARGIN;
			}
			else if (sourceText.startsWith("RIGHT"))
			{
				infoType = Constants.PAPERINFO_RIGHTMARGIN;
			}
		}

		if (infoType >= 0)
		{
			int offs = sourceText.indexOf('=');
			String info = Globals.getPaperInfo(infoType);

			if (info == null)
			{
				if (offs >= 0)
				{
					oString = sourceText.substring(offs + 1);
				}
			}
			else
			{
				oString = info;
			}
		}

		return oString;
	}
}
