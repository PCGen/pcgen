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
 * Current Ver: $Revision: 1.3 $
 * Last Editor: $Author: binkley $
 * Last Edited: $Date: 2005/10/18 20:23:58 $
 *
 */
package plugin.exporttokens;

import pcgen.core.Constants;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;

import java.util.ArrayList;
import java.util.Iterator;
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
		String retString = "";
		int i;
		int k = tokenSource.lastIndexOf(',');

		if (k >= 0)
		{
			tokenSource = tokenSource.substring(k + 1);
		}
		else
		{
			tokenSource = ", ";
		}

		List stringList = new ArrayList();

		for (Iterator iter = pc.getClassList().iterator(); iter.hasNext();)
		{
			PCClass pcClass = (PCClass) iter.next();

			if (pcClass.getLevel() > 0)
			{
				if (!pcClass.getProhibitedString().equals(Constants.s_NONE))
				{
					stringList.add(pcClass.getProhibitedString());
				}
			}
		}

		for (i = 0; i < stringList.size(); ++i)
		{
			retString += stringList.get(i);

			if (i < (stringList.size() - 1))
			{
				retString += tokenSource;
			}
		}

		return retString;
	}
}
