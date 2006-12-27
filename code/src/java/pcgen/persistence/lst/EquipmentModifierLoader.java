/*
 * EquipmentModifierLoader.java
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
 * Created on February 22, 2002, 10:29 PM
 *
 * $Id$
 */
package pcgen.persistence.lst;

import java.net.URL;
import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.EquipmentModifier;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision$
 */
public final class EquipmentModifierLoader
{
	/** Creates a new instance of EquipmentModifierLoader */
	private EquipmentModifierLoader()
	{
		// Empty Constructor
	}

	/**
	 * Parse the line
	 * @param obj
	 * @param inputLine
	 * @param sourceURL
	 * @param lineNum
	 * @throws PersistenceLayerException
	 */
	public static void parseLine(EquipmentModifier obj, String inputLine,
		URL sourceURL, int lineNum) throws PersistenceLayerException
	{
		if (obj == null)
		{
			return;
		}

		final StringTokenizer colToken =
				new StringTokenizer(inputLine, SystemLoader.TAB_DELIM);
		int col = -1;

		if (!obj.isNewItem())
		{
			col = 1; // just force it past required fields since .MOD doesn't specify them
			colToken.nextToken(); // skip name
		}

		Map<String, LstToken> tokenMap =
				TokenStore.inst().getTokenMap(EquipmentModifierLstToken.class);
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
			EquipmentModifierLstToken token =
					(EquipmentModifierLstToken) tokenMap.get(key);

			col++;

			if (col == 0)
			{
				obj.setName(colString.replace('|', ' '));
			}
			else if (token != null)
			{
				final String value = colString.substring(idxColon + 1);
				LstUtils.deprecationCheck(token, obj, value);
				if (!token.parse(obj, value))
				{
					Logging.errorPrint("Error parsing ability "
						+ obj.getDisplayName() + ':' + sourceURL.getFile()
						+ ':' + colString + "\"");
				}
			}
			else if (PObjectLoader.parseTag(obj, colString))
			{
				continue;
			}
			//else if (colString.startsWith(Constants.s_TAG_TYPE))
			//{
			//	obj.setType(colString.substring(Constants.s_TAG_TYPE.length()));
			//}
			else
			{
				Logging.errorPrint("Illegal equipment modifier info "
					+ String.valueOf(sourceURL) + ":"
					+ Integer.toString(lineNum) + " \"" + colString + "\"");
			}
		}
	}
}
