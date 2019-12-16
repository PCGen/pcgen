/*
 * HPRollToken.java
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
 */
package plugin.exporttokens;

import java.util.StringTokenizer;

import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.display.CharacterDisplay;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;

/**
 * Deals with:
 * 
 * HPROLL.x
 * HPROLL.x.ROLL
 * HPROLL.x.STAT
 * HPROLL.x.TOTAL
 */
public class HPRollToken extends Token
{
	/** Token name */
	public static final String TOKENNAME = "HPROLL";

	@Override
	public String getTokenName()
	{
		return TOKENNAME;
	}

	@Override
	public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
	{
		StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
		String bString;
		String retString = "";
		bString = aTok.nextToken();

		int levelOffset = Integer.parseInt(aTok.nextToken()) - 1;

		if (aTok.hasMoreTokens())
		{
			bString = aTok.nextToken();
		}

		if (bString.startsWith("HPROLL"))
		{
			bString = "ROLL";
		}

		if ((levelOffset >= pc.getDisplay().getLevelInfoSize()) || (levelOffset < 0))
		{
			return "0";
		}

        switch (bString)
        {
            case "ROLL":
                retString = Integer.toString(getRollToken(pc, levelOffset));
                break;
            case "STAT":
                retString = Integer.toString(getStatToken(pc.getDisplay()));
                break;
            case "TOTAL":
                retString = Integer.toString(getTotalToken(pc, levelOffset));
                break;
        }

		return retString;
	}

	/**
	 * Get the HPROLL.ROLL token
	 * @param pc
	 * @param level
	 * @return the HPROLL.ROLL token
	 */
	public static int getRollToken(PlayerCharacter pc, int level)
	{
		CharacterDisplay display = pc.getDisplay();
		int classLevel = display.getLevelInfoClassLevel(level) - 1;
		int hpRoll = 0;

		PCClass pcClass = pc.getClassKeyed(display.getLevelInfoClassKeyName(level));

		if (pcClass != null)
		{
			PCClassLevel pcl = display.getActiveClassLevel(pcClass, classLevel);
			Integer hp = display.getHP(pcl);
			hpRoll = hp == null ? 0 : hp;
		}

		return hpRoll;
	}

	/**
	 * Get the HPROLL.STAT token
	 * @param display
	 * @return the HPROLL.STAT token
	 */
	public static int getStatToken(CharacterDisplay display)
	{
		return (int) display.getStatBonusTo("HP", "BONUS");
	}

	/**
	 * Get the HPROLL.TOTAL token
	 * @param pc
	 * @param level
	 * @return the HPROLL.TOTAL token
	 */
	public static int getTotalToken(PlayerCharacter pc, int level)
	{
		return getRollToken(pc, level) + getStatToken(pc.getDisplay());
	}
}
