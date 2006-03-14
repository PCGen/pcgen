/*
 * KitFundsLoader.java
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
import pcgen.core.kit.KitFunds;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 * Handles the parsing of a Kit line starting with FUNDS
 * 
 * @author Aaron Divinsky <boomer70@yahoo.com>
 * 
 */
public class KitFundsLoader
{
	public static void parseLine(Kit kit, String colString)
			throws PersistenceLayerException
	{
		final StringTokenizer colToken = new StringTokenizer(colString,
				SystemLoader.TAB_DELIM);

		KitFunds kitFunds = new KitFunds(colToken.nextToken());

		Map tokenMap = TokenStore.inst().getTokenMap(KitFundsLstToken.class);
		while (colToken.hasMoreTokens())
		{
			colString = colToken.nextToken();

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
			KitFundsLstToken token = (KitFundsLstToken) tokenMap.get(key);

			if (token != null)
			{
				final String value = colString.substring(idxColon + 1);
				LstUtils.deprecationCheck(token, kit, value);
				if (!token.parse(kitFunds, value))
				{
					Logging
							.errorPrint("Error parsing Kit Funds tag "
									+ kitFunds.getObjectName() + ':'
									+ colString + "\"");
				}
			}
			else if (BaseKitLoader.parseCommonTags(kitFunds, colString))
			{
				continue;
			}
			else
			{
				Logging.errorPrint("Unknown Kit Funds info: \"" + colString
						+ "\"");
			}
		}
		kit.addObject(kitFunds);
	}

}
