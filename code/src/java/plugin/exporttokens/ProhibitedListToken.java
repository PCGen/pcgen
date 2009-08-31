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

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.SpellProhibitor;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;

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
	public String getToken(String tokenSource, PlayerCharacter pc,
		ExportHandler eh)
	{
		return getProhibitedListToken(tokenSource, pc);
	}

	public static String getProhibitedListToken(String tokenSource,
		PlayerCharacter pc)
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

		Set<String> set = new TreeSet<String>();
		for (PCClass pcClass : pc.getClassSet())
		{
			if (pcClass.getLevel(pc) > 0)
			{
				for (SpellProhibitor sp : pcClass
					.getSafeListFor(ListKey.PROHIBITED_SPELLS))
				{
					set.addAll(sp.getValueList());
				}

				List<SpellProhibitor> prohibList =
						pc.getAssocList(pcClass,
							AssociationListKey.PROHIBITED_SCHOOLS);
				if (prohibList != null)
				{
					for (SpellProhibitor sp : prohibList)
					{
						set.addAll(sp.getValueList());
					}
				}
			}
		}

		return StringUtil.join(set, jointext);
	}
}
