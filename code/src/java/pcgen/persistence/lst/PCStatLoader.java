/*
 * PCStatLoader.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on August 12, 2002, 10:29 PM
 *
 * $Id$
 */
package pcgen.persistence.lst;

import pcgen.core.PCStat;
import pcgen.core.SettingsHandler;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

import java.net.URI;
import java.util.Map;
import java.util.StringTokenizer;

/**
 *
 * @author  Bryan McRoberts <merton_monk@yahoo.com>
 * @version $Revision$
 */
public final class PCStatLoader extends LstLineFileLoader
{
	/** Creates a new instance of PCStatLoader */
	public PCStatLoader()
	{
		// Empty Constructor
	}

	/**
	 * @see pcgen.persistence.lst.LstLineFileLoader#parseLine(java.net.URL, java.lang.String)
	 */
	@Override
	public void parseLine(LoadContext context, String lstLine, URI sourceURI)
		throws PersistenceLayerException
	{
		PCStat stat = new PCStat();

		final StringTokenizer colToken =
				new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);

		Map<String, LstToken> tokenMap =
				TokenStore.inst().getTokenMap(PCStatLstToken.class);
		while (colToken.hasMoreTokens())
		{
			final String token = colToken.nextToken().trim();
			final int colonLoc = token.indexOf(':');
			if (colonLoc == -1)
			{
				Logging.errorPrint("Invalid Token - does not contain a colon: "
						+ token);
				continue;
			}
			else if (colonLoc == 0)
			{
				Logging.errorPrint("Invalid Token - starts with a colon: "
						+ token);
				continue;
			}

			String key = token.substring(0, colonLoc);
			String value = (colonLoc == token.length() - 1) ? null : token
					.substring(colonLoc + 1);
			if (context.processToken(stat, key, value))
			{
				context.commit();
			}
			else if (tokenMap.containsKey(key))
			{
				PCStatLstToken tok = (PCStatLstToken) tokenMap.get(key);
				LstUtils.deprecationCheck(tok, stat, value);
				if (!tok.parse(stat, value))
				{
					Logging.errorPrint("Error parsing PCStat "
							+ stat.getDisplayName() + ':'
							+ sourceURI.toString() + ':' + token + "\"");
				}
			}
			else if (!PObjectLoader.parseTag(stat, token))
			{
				Logging.replayParsedMessages();
			}
			Logging.clearParseMessages();
		}

		SettingsHandler.getGame().addToStatList(stat);
	}
}
