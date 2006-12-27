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

import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.Kit;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
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
	public static void parseLine(Kit kit, String colString)
		throws PersistenceLayerException
	{
		final StringTokenizer colToken =
				new StringTokenizer(colString, SystemLoader.TAB_DELIM);

		kit.setName(colToken.nextToken());
		Map<String, LstToken> tokenMap =
				TokenStore.inst().getTokenMap(KitStartpackLstToken.class);
		while (colToken.hasMoreTokens())
		{
			colString = colToken.nextToken();

			if (PObjectLoader.parseTag(kit, colString))
			{
				// Here if PObjectLoader has processed tag--nothing else to do
				continue;
			}

			// We will find the first ":" for the "controlling" line token
			final int idxColon = colString.indexOf(':');
			String key = "";
			try
			{
				key = colString.substring(0, idxColon);
			}
			catch (StringIndexOutOfBoundsException e)
			{
				// TODO Handle Exception
			}
			KitStartpackLstToken token =
					(KitStartpackLstToken) tokenMap.get(key);

			if (token != null)
			{
				final String value = colString.substring(idxColon + 1);
				LstUtils.deprecationCheck(token, kit, value);
				if (!token.parse(kit, value))
				{
					Logging.errorPrint("Error parsing Kit Startpack tag "
						+ kit.getDisplayName() + ':' + colString + "\"");
				}
			}
			else
			{
				Logging.errorPrint("Unknown Kit Startpack info: \"" + colString
					+ "\"");
			}

		}
	}
}
