/*
 * DeityToken.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on March 3, 2006
 *
 * Current Ver: $Revision: $
 * Last Editor: $Author: $
 * Last Edited: $Date: $
 */
package plugin.lsttokens.kit;

import java.util.StringTokenizer;

import pcgen.core.Kit;
import pcgen.core.kit.KitDeity;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.persistence.lst.KitLstToken;
import pcgen.util.Logging;

/**
 * Handles the DEITY, DOMAIN, and COUNT tags for a Kit.
 */
public class DeityToken extends KitLstToken
{
	/**
	 * Gets the name of the tag this class will parse.
	 *
	 * @return Name of the tag this class handles
	 */
	public String getTokenName()
	{
		return "DEITY";
	}

	/**
	 * Handles parsing the DEITY, DOMAIN, and COUNT tags for a Kit.
	 *
	 * @param aKit the Kit object to add this information to
	 * @param value the token string
	 * @return true if parse OK
	 * @throws PersistenceLayerException
	 */
	public boolean parse(Kit aKit, String value)
		throws PersistenceLayerException
	{
		final StringTokenizer colToken = new StringTokenizer(value, SystemLoader.TAB_DELIM);
		KitDeity kDeity = new KitDeity(colToken.nextToken());

		while (colToken.hasMoreTokens())
		{
			String colString = colToken.nextToken();

			if (colString.startsWith("DEITY:"))
			{
				Logging.errorPrint("Ignoring second DEITY tag \"" + colString +
								   "\" in DeityToken.parse\"");
			}
			else if (colString.startsWith("DOMAIN:"))
			{
				colString = colString.substring(7);
				final StringTokenizer pTok = new StringTokenizer(colString, "|");
				while (pTok.hasMoreTokens())
				{
					final String domain = pTok.nextToken();
					kDeity.addDomain(domain);
				}
			}
			else if (colString.startsWith("COUNT:"))
			{
				kDeity.setCountFormula(colString.substring(6));
			}
			else
			{
				if (parseCommonTags(kDeity, colString) == false)
				{
					Logging.errorPrint("Invalid DEITY tag \"" + colString + "\"");
				}
			}
		}
		aKit.addObject(kDeity);
		return true;
	}
}
