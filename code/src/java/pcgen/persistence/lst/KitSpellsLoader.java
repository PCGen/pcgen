/*
 * KitSpellsLoader.java
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

import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.Globals;
import pcgen.core.Kit;
import pcgen.core.kit.KitSpells;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

public class KitSpellsLoader
{
	public static void parseLine(Kit kit, String colString)
			throws PersistenceLayerException
	{
		final StringTokenizer colToken = new StringTokenizer(colString,
				SystemLoader.TAB_DELIM);

		final KitSpells kitSpells = new KitSpells();
		colString = colToken.nextToken();
		final StringTokenizer aTok = new StringTokenizer(colString, "|");

		String spellbook = Globals.getDefaultSpellBook();
		String castingClass = null;
		while (aTok.hasMoreTokens())
		{
			String field = aTok.nextToken();
			if (field.startsWith("SPELLBOOK="))
			{
				spellbook = field.substring(10);
			}
			else if (field.startsWith("CLASS="))
			{
				castingClass = field.substring(6);
			}
			else
			{
				String countStr = null;
				if (field.indexOf("=") != -1)
				{
					countStr = field.substring(field.indexOf("=") + 1);
					field = field.substring(0, field.indexOf("="));
				}
				final StringTokenizer subTok = new StringTokenizer(field, "[]");
				final String spell = subTok.nextToken();
				ArrayList featList = new ArrayList();
				while (subTok.hasMoreTokens())
				{
					featList.add(subTok.nextToken());
				}
				kitSpells.addSpell(castingClass, spellbook, spell, featList,
						countStr);
			}
		}
		Map tokenMap = TokenStore.inst().getTokenMap(KitSpellsLstToken.class);
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
			KitSpellsLstToken token = (KitSpellsLstToken) tokenMap.get(key);

			if (token != null)
			{
				final String value = colString.substring(idxColon + 1);
				LstUtils.deprecationCheck(token, kit, value);
				if (!token.parse(kitSpells, value))
				{
					Logging.errorPrint("Error parsing Kit Spells tag "
							+ kitSpells.getObjectName() + ':' + colString
							+ "\"");
				}
			}
			else if (BaseKitLoader.parseCommonTags(kitSpells, colString))
			{
				continue;
			}
			else
			{
				Logging.errorPrint("Unknown Kit Spells info: \"" + colString
						+ "\"");
			}

		}
		kit.addObject(kitSpells);
	}
}
