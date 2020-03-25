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
 */
package plugin.exporttokens.deprecated;

import pcgen.core.display.CharacterDisplay;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.AbstractExportToken;

/**
 * Handles BIO Output Token
 *
 * BIO
 * BIO,text delimiter
 * BIO[.beforevalue[.aftervalue]]
 */
public class BioToken extends AbstractExportToken
{
	@Override
	public String getTokenName()
	{
		return "BIO";
	}

	@Override
	public String getToken(String tokenSource, CharacterDisplay display, ExportHandler eh)
	{
		String beforeValue = "";
		String afterValue = "";

		// TODO - What is the point of the first part of this if clause?  The token has 
		// to at least contain 'BIO' which is a length of 3....  Is this to do with a 
		// possibly reentrant EH?
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
			{
				beforeValue = tokens[1];
			}
			if (tokens.length > 2)
			{
				afterValue = tokens[2];
			}
		}

		return beforeValue + display.getBio().replaceAll("\n", afterValue + '\n' + beforeValue) + afterValue;
	}
}
