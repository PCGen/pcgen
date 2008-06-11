/*
 * KitStartpackLoader.java
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
 * Created on March 6, 2006
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */

package pcgen.persistence.lst;

import java.net.URI;
import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.Kit;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

/**
 * Deals with loading start packs for kits
 */
public class KitStartpackLoader
{
	/**
	 * Parse the line
	 * @param kit
	 * @param colString
	 * @throws PersistenceLayerException
	 */
	public static void parseLine(LoadContext context, Kit kit, String colString, URI source)
		throws PersistenceLayerException
	{
		final StringTokenizer colToken =
				new StringTokenizer(colString, SystemLoader.TAB_DELIM);

		kit.setName(colToken.nextToken());
		Map<String, LstToken> tokenMap =
				TokenStore.inst().getTokenMap(KitStartpackLstToken.class);
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
			if (context.processToken(kit, key, value))
			{
				context.commit();
			}
			else if (tokenMap.containsKey(key))
			{
				KitStartpackLstToken tok = (KitStartpackLstToken) tokenMap
						.get(key);
				LstUtils.deprecationCheck(tok, kit, value);
				if (!tok.parse(kit, value))
				{
					Logging.errorPrint("Error parsing Kit:"
							+ kit.getDisplayName() + " token " + token
							+ " from " + source.toString());
				}
			}
			else if (!PObjectLoader.parseTag(kit, token))
			{
				Logging.replayParsedMessages();
			}
			Logging.clearParseMessages();
		}
	}
}
