/*
 * SpellsToken.java
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

import java.util.ArrayList;
import java.util.StringTokenizer;

import pcgen.core.Globals;
import pcgen.core.Kit;
import pcgen.core.kit.KitSpells;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.persistence.lst.KitLstToken;

public class SpellsToken extends KitLstToken
{
	/**
	 * Gets the name of the tag this class will parse.
	 *
	 * @return Name of the tag this class handles
	 */
	public String getTokenName()
	{
		return "SPELLS";
	}

	/**
	 * Handles the SPELLS tag for a kit.
	 * @param aKit the Kit object to add this information to
	 * @param value the token string
	 * @return true if parse OK
	 * @throws PersistenceLayerException
	 */
	public boolean parse(Kit aKit, String value)
		throws PersistenceLayerException
	{
		final KitSpells kSpells = new KitSpells();
		final StringTokenizer colToken = new StringTokenizer(value, SystemLoader.TAB_DELIM);

		String colString = colToken.nextToken();
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
					countStr = field.substring(field.indexOf("=")+1);
					field = field.substring(0,field.indexOf("="));
				}
				final StringTokenizer subTok = new StringTokenizer(field, "[]");
				final String spell = subTok.nextToken();
				ArrayList featList = new ArrayList();
				while (subTok.hasMoreTokens())
				{
					featList.add(subTok.nextToken());
				}
				kSpells.addSpell(castingClass, spellbook, spell, featList, countStr);
			}
		}
		while (colToken.hasMoreTokens())
		{
			colString = colToken.nextToken();

			if (colString.startsWith("COUNT:"))
			{
				kSpells.setCountFormula(colString.substring(6));
			}
			else
			{
				if (parseCommonTags(kSpells, colString) == false)
				{
					throw new PersistenceLayerException(
						"Unknown KitSpells info " + " \"" + colString + "\"");
				}
			}
		}

		aKit.addObject(kSpells);
		return true;
	}
}
