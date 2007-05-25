/*
 * SkillpointsToken.java
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
package pcgen.io.exporttoken;

import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.io.ExportHandler;
import pcgen.util.BigDecimalHelper;
import pcgen.util.Logging;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Deal with the Tokens:
 * 
 * SKILLPOINTS
 * SKILLPOINTS.TOTAL
 * SKILLPOINTS.UNUSED
 * SKILLPOINTS.USED
 */
public class SkillpointsToken extends Token
{
	/** Token name */
	public static final String TOKENNAME = "SKILLPOINTS";

	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	@Override
	public String getTokenName()
	{
		return TOKENNAME;
	}

	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	@Override
	public String getToken(String tokenSource, PlayerCharacter pc,
		ExportHandler eh)
	{
		final StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
		String bString;
		int classNum = -1; 

		bString = aTok.nextToken();

		if (aTok.hasMoreTokens())
		{
			bString = aTok.nextToken();
		}

		if (aTok.hasMoreTokens())
		{
			String pcclass = aTok.nextToken();
			try
			{
				classNum = Integer.parseInt(pcclass);
			}
			catch (NumberFormatException e)
			{
				Logging.errorPrint("Expected class number in " + tokenSource
					+ " but got " + pcclass + ".");
			}
		}

		if (bString.startsWith("SKILLPOINTS"))
		{
			bString = "TOTAL";
		}

		float aTotalSkillPoints = 0;

		if ("TOTAL".equals(bString) || "UNUSED".equals(bString))
		{
			if (classNum >= 0)
			{
				aTotalSkillPoints += getUnusedSkillPoints(pc, classNum);
			}
			else
			{
				aTotalSkillPoints += getUnusedSkillPoints(pc);
			}
		}

		if ("TOTAL".equals(bString) || "USED".equals(bString))
		{
			if (classNum >= 0)
			{
				aTotalSkillPoints += getUsedSkillPoints(pc, classNum);
			}
			else
			{
				aTotalSkillPoints += getUsedSkillPoints(pc);
			}
		}

		return BigDecimalHelper.trimZeros(new BigDecimal(aTotalSkillPoints));
	}

	/**
	 * Get unused skill points for the PC
	 * @param pc
	 * @return unused skill points for the PC
	 */
	public static int getUnusedSkillPoints(PlayerCharacter pc)
	{
		float usedPoints = 0;

		for (PCClass pcClass : pc.getClassList())
		{
			if (pcClass.getSkillPool(pc) > 0)
			{
				usedPoints += pcClass.getSkillPool(pc);
			}
		}

		return (int) usedPoints;
	}

	/**
	 * Get unused skill points for the PC
	 * @param pc
	 * @return unused skill points for the PC
	 */
	public static int getUnusedSkillPoints(PlayerCharacter pc, int classNum)
	{
		float usedPoints = 0;

		if (classNum < 0 || classNum >= pc.getClassList().size())
		{
			return 0;
		}
		PCClass pcClass = pc.getClassList().get(classNum);
		if (pcClass.getSkillPool(pc) > 0)
		{
			usedPoints += pcClass.getSkillPool(pc);
		}

		return (int) usedPoints;
	}

	/**
	 * Get the used skill points for the PC
	 * @param pc
	 * @return the used skill points for the PC
	 */
	public static int getUsedSkillPoints(PlayerCharacter pc)
	{
		float usedPoints = 0;
		final List<Skill> skillList = new ArrayList<Skill>(pc.getSkillList());
		for (Skill aSkill : skillList)
		{
			if ((aSkill.getRank().doubleValue() > 0)
				|| (aSkill.getOutputIndex() != 0))
			{
				for (String classRanks : aSkill.getRankList())
				{
					int index = classRanks.indexOf(':');
					String className = classRanks.substring(0, index);
					float ranks =
							Float.valueOf(classRanks.substring(index + 1))
								.floatValue();
					PCClass pcClass = pc.getClassKeyed(className);

					usedPoints += (ranks * aSkill.costForPCClass(pcClass, pc));
				}
			}
		}

		return (int) usedPoints;
	}

	/**
	 * Get the used skill points for the PC
	 * @param pc
	 * @return the used skill points for the PC
	 */
	public static int getUsedSkillPoints(PlayerCharacter pc, int classNum)
	{
		if (classNum < 0 || classNum >= pc.getClassList().size())
		{
			return 0;
		}
		PCClass targetClass = pc.getClassList().get(classNum);
		float usedPoints = 0;
		final List<Skill> skillList = new ArrayList<Skill>(pc.getSkillList());
		for (Skill aSkill : skillList)
		{
			if ((aSkill.getRank().doubleValue() > 0)
				|| (aSkill.getOutputIndex() != 0))
			{
				for (String classRanks : aSkill.getRankList())
				{
					int index = classRanks.indexOf(':');
					String className = classRanks.substring(0, index);
					float ranks =
							Float.valueOf(classRanks.substring(index + 1))
								.floatValue();
					PCClass pcClass = pc.getClassKeyed(className);
					if (targetClass == pcClass)
					{
						usedPoints += (ranks * aSkill.costForPCClass(pcClass, pc));
					}
				}
			}
		}

		return (int) usedPoints;
	}
}
