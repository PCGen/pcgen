/*
 * SkilltablehiddencolumnsToken.java
 * Copyright 2006 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on August 15, 2006, 11:42 PM
 *
 * Current Ver: $Revision: 1297 $
 * Last Editor: $Author: boomer70 $
 * Last Edited: $Date: 2006-08-15 21:09:09 -0400 (Tue, 15 Aug 2006) $
 *
 */
package plugin.lsttokens.gamemode.tab;

import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.GameMode;
import pcgen.persistence.lst.TabLoader;
import pcgen.persistence.lst.TabLstToken;
import pcgen.util.enumeration.Tab;

/**
 * Class deals with SKILLTABLEHIDDENCOLUMNS Token
 */
public class SkilltablehiddencolumnsToken implements TabLstToken {

	public String getTokenName()
	{
		return "SKILLTABLEHIDDENCOLUMNS";
	}

	public boolean parse(GameMode gameMode, Map<String, String> tab, String value)
	{
		final Tab aTab = GameMode.getTab(tab.get(TabLoader.TAB));
		if (aTab != Tab.SKILLS)
		{
			return false;
		}

		for(int i = 0; i < 6; ++i)
		{
			gameMode.setSkillTabColumnVisible(i, true);
		}
		final StringTokenizer commaTok = new StringTokenizer(value, ",");
		while (commaTok.hasMoreTokens())
		{
			String commaToken = commaTok.nextToken();
			try
			{
				gameMode.setSkillTabColumnVisible(Integer.parseInt(commaToken), false);
			}
			catch (NumberFormatException nfe)
			{
				return false;
			}
		}
		return true;
	}
}
