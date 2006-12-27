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
import pcgen.util.Logging;

import java.net.URL;
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
	 * @see pcgen.persistence.lst.LstLineFileLoader#parseLine(java.lang.String, java.net.URL)
	 */
	@Override
	public void parseLine(String lstLine, URL sourceURL)
		throws PersistenceLayerException
	{
		PCStat stat = new PCStat();

		final StringTokenizer colToken =
				new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);

		Map<String, LstToken> tokenMap =
				TokenStore.inst().getTokenMap(PCStatLstToken.class);
		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken().trim();
			final int idxColon = colString.indexOf(':');
			String key = "";
			try
			{
				key = colString.substring(0, idxColon);
			}
			catch (Exception e)
			{
				// TODO Handle Exception
			}
			PCStatLstToken token = (PCStatLstToken) tokenMap.get(key);

			if (token != null)
			{
				final String value = colString.substring(idxColon + 1).trim();
				LstUtils.deprecationCheck(token, stat, value);
				if (!token.parse(stat, value))
				{
					Logging.errorPrint("Error parsing check "
						+ stat.getDisplayName() + ':' + sourceURL.toString()
						+ ':' + colString + "\"");
				}
			}
			else if (PObjectLoader.parseTag(stat, colString))
			{
				continue;
			}
			else
			{
				Logging.errorPrint("Illegal stat info '" + lstLine + "' in "
					+ sourceURL.toString());
			}
		}

		SettingsHandler.getGame().addToStatList(stat);
	}
}
