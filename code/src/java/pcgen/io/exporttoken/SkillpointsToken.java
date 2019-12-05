/*
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
 *
 *
 */
package pcgen.io.exporttoken;

import java.math.BigDecimal;
import java.util.StringTokenizer;

import pcgen.cdom.enumeration.SkillCost;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.display.CharacterDisplay;
import pcgen.io.ExportHandler;
import pcgen.util.BigDecimalHelper;
import pcgen.util.Logging;

/**
 * Deal with the Tokens:
 * <p>
 * SKILLPOINTS
 * SKILLPOINTS.TOTAL
 * SKILLPOINTS.UNUSED
 * SKILLPOINTS.USED
 */
public class SkillpointsToken extends Token
{
    /**
     * Token name
     */
    public static final String TOKENNAME = "SKILLPOINTS";

    @Override
    public String getTokenName()
    {
        return TOKENNAME;
    }

    @Override
    public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
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
            } catch (NumberFormatException e)
            {
                Logging.errorPrint("Expected class number in " + tokenSource + " but got " + pcclass + ".");
            }
        }

        if (bString.startsWith("SKILLPOINTS"))
        {
            bString = "TOTAL";
        }

        float aTotalSkillPoints = 0;

        if ("TOTAL".equals(bString) || "UNUSED".equals(bString))
        {
            aTotalSkillPoints += (classNum >= 0) ? getUnusedSkillPoints(pc, classNum) : getUnusedSkillPoints(pc);
        }

        if ("TOTAL".equals(bString) || "USED".equals(bString))
        {
            aTotalSkillPoints += (classNum >= 0) ? getUsedSkillPoints(pc, classNum) : getUsedSkillPoints(pc);
        }

        return BigDecimalHelper.trimZeros(new BigDecimal(String.valueOf(aTotalSkillPoints)));
    }

    /**
     * Get unused skill points for the PC
     *
     * @param pc
     * @return unused skill points for the PC
     */
    public static int getUnusedSkillPoints(PlayerCharacter pc)
    {
        float usedPoints = 0;

        for (PCClass pcClass : pc.getDisplay().getClassSet())
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
     *
     * @param pc
     * @return unused skill points for the PC
     */
    public static int getUnusedSkillPoints(PlayerCharacter pc, int classNum)
    {
        float usedPoints = 0;

        if (classNum < 0 || classNum >= pc.getDisplay().getClassCount())
        {
            return 0;
        }
        PCClass pcClass = pc.getDisplay().getClassList().get(classNum);
        if (pcClass.getSkillPool(pc) > 0)
        {
            usedPoints += pcClass.getSkillPool(pc);
        }

        return (int) usedPoints;
    }

    /**
     * Get the used skill points for the PC
     *
     * @param pc
     * @return the used skill points for the PC
     */
    public static int getUsedSkillPoints(PlayerCharacter pc)
    {
        float usedPoints = 0;
        for (Skill aSkill : pc.getDisplay().getSkillSet())
        {
            for (PCClass pcc : pc.getSkillRankClasses(aSkill))
            {
                Double rank = pc.getSkillRankForClass(aSkill, pcc);
                if (rank == null)
                {
                    rank = 0.0d;
                }
                SkillCost skillCost = pc.getSkillCostForClass(aSkill, pcc);
                usedPoints += (rank * skillCost.getCost());
            }
        }

        return (int) usedPoints;
    }

    /**
     * Get the used skill points for the PC
     *
     * @param pc
     * @return the used skill points for the PC
     */
    public static int getUsedSkillPoints(PlayerCharacter pc, int classNum)
    {
        CharacterDisplay display = pc.getDisplay();
        if (classNum < 0 || classNum >= display.getClassCount())
        {
            return 0;
        }
        PCClass targetClass = display.getClassList().get(classNum);
        float usedPoints = 0;
        for (Skill aSkill : display.getSkillSet())
        {
            Integer outputIndex = pc.getSkillOrder(aSkill);
            if ((pc.getRank(aSkill).doubleValue() > 0) || (outputIndex != null && outputIndex != 0))
            {
                Double rank = pc.getSkillRankForClass(aSkill, targetClass);
                if (rank == null)
                {
                    rank = 0.0d;
                }
                SkillCost skillCost = pc.getSkillCostForClass(aSkill, targetClass);
                usedPoints += (rank * skillCost.getCost());
            }
        }

        return (int) usedPoints;
    }
}
