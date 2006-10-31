/*
 * ProhibitedListToken.java
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
 *
 * Created on December 15, 2003, 12:21 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package plugin.exporttokens;

import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;
import pcgen.util.CollectionUtilities;

import java.util.ArrayList;
import java.util.List;

//PROHIBITEDLIST
public class ProhibitedListToken extends Token
{
	public static final String TOKENNAME = "PROHIBITEDLIST";

	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	public String getTokenName()
	{
		return TOKENNAME;
	}

	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
	{
		return getProhibitedListToken(tokenSource, pc);
	}

	public static String getProhibitedListToken(String tokenSource, PlayerCharacter pc)
	{
		int k = tokenSource.lastIndexOf(',');

		String jointext;
		if (k >= 0)
		{
			jointext = tokenSource.substring(k + 1);
		}
		else
		{
			jointext = ",";
		}

		List<String> stringList = new ArrayList<String>();

		for ( PCClass pcClass : pc.getClassList() )
		{
			if (pcClass.getLevel() > 0)
			{
				if (pcClass.getProhibitedSchools() != null)
				{
					/*
					 * CONSIDER This was changed from adding
					 * pcClass.getProhibitedString() directly into stringList,
					 * which was adding a string which was "," delimited into
					 * the List, which gets joined by ", " below... SOOOOO, in
					 * THEORY, it is easier to add the individual items here and
					 * let the join happen in one step below - but it will
					 * change some spacing!! I hope this doesn't break anything :) -
					 * thpr 10/29/06
					 */
					stringList.addAll(pcClass.getProhibitedSchools());
				}
			}
		}

		return CollectionUtilities.joinStringRepresentations(stringList, jointext);
	}
}
