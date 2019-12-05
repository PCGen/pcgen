/*
 * Missing License Header, Copyright 2016 (C) Andrew Maitland <amaitland@users.sourceforge.net>
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
 */
package pcgen.core.analysis;

import java.util.Collection;
import java.util.StringTokenizer;

import pcgen.core.Ability;
import pcgen.core.AbilityUtilities;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PCStat;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.RuleConstants;
import pcgen.core.bonus.BonusObj;
import pcgen.core.prereq.PrereqHandler;
import pcgen.util.Logging;

public final class BonusCalc
{

    private BonusCalc()
    {
    }

    /**
     * Gets the bonuses to a given stat.
     *
     * @param po
     * @param stat the Stat to get the bonus for
     * @param aPC  the Player Character that the bonus will apply to
     * @return the bonus to the given stat.
     */
    public static int getStatMod(PObject po, PCStat stat, final PlayerCharacter aPC)
    {
        return (int) BonusCalc.charBonusTo(po, "STAT", stat.getKeyName(), aPC);
    }

    public static double bonusTo(PObject po, String aType, String aName, final Object obj,
            final Collection<BonusObj> aBonusList, final PlayerCharacter aPC)
    {
        if ((aBonusList == null) || (aBonusList.isEmpty()))
        {
            return 0;
        }

        double retVal = 0;

        aType = aType.toUpperCase();
        aName = aName.toUpperCase();

        final String aTypePlusName = aType + '.' + aName + '.';

        if (!BonusCalc.dontRecurse && (po instanceof Ability) && (AbilityUtilities.isFeat(obj))
                && !Globals.checkRule(RuleConstants.FEATPRE))
        {
            // SUCK!  This is horrid, but bonusTo is actually recursive with respect to
            // passesPreReqToGain and there is no other way to do this without decomposing the
            // dependencies.  I am loathe to break working code.
            // This addresses bug #709677 -- Feats give bonuses even if you no longer qualify
            BonusCalc.dontRecurse = true;

            boolean returnZero = false;

            if (!po.qualifies(aPC, po))
            {
                returnZero = true;
            }

            BonusCalc.dontRecurse = false;

            if (returnZero)
            {
                return 0;
            }
        }

        int iTimes = 1;

        if (aPC != null && "VAR".equals(aType))
        {
            iTimes = Math.max(1, aPC.getConsolidatedAssociationList(po).size());
        }

        for (BonusObj bonus : aBonusList)
        {
            String bString = bonus.toString().toUpperCase();

            if (aPC != null && !aPC.getConsolidatedAssociationList(po).isEmpty())
            {
                int span = 4;
                int idx = bString.indexOf("%VAR");

                if (idx == -1)
                {
                    idx = bString.indexOf("%LIST|");
                    span = 5;
                }

                if (idx >= 0)
                {
                    final String firstPart = bString.substring(0, idx);
                    final String secondPart = bString.substring(idx + span);

                    for (String assoc : aPC.getConsolidatedAssociationList(po))
                    {
                        final String xString = (firstPart + assoc + secondPart).toUpperCase();

                        retVal +=
                                BonusCalc.calcBonus(po, xString, aType, aName, aTypePlusName, obj, iTimes, bonus, aPC);
                    }
                }
            } else
            {
                retVal += BonusCalc.calcBonus(po, bString, aType, aName, aTypePlusName, obj, iTimes, bonus, aPC);
            }
        }

        return retVal;
    }

    /**
     * a boolean for whether something should recurse, default is false.
     */
    private static boolean dontRecurse = false;

    /**
     * Apply the bonus to a PC, pass through object's default bonuslist
     *
     * @param po
     * @param aType
     * @param aName
     * @param aPC
     * @return the bonus
     */
    public static double charBonusTo(PObject po, final String aType, final String aName, final PlayerCharacter aPC)
    {
        return bonusTo(po, aType, aName, aPC, po.getBonusList(aPC), aPC);
    }

    public static double equipBonusTo(Equipment po, final String aType, final String aName, final PlayerCharacter aPC)
    {
        return bonusTo(po, aType, aName, po, po.getBonusList(po), aPC);
    }

    /**
     * calcBonus adds together all the bonuses for aType of aName.
     *
     * @param po
     * @param bString       Either the entire BONUS:COMBAT|AC|2 string or part of a %LIST or %VAR bonus section
     * @param aType         Such as "COMBAT"
     * @param aName         Such as "AC"
     * @param aTypePlusName "COMBAT.AC."
     * @param obj           The object to get the bonus from
     * @param iTimes        multiply bonus * iTimes
     * @param aBonusObj
     * @param aPC
     * @return the value of the bonus
     */
    private static double calcBonus(PObject po, final String bString, final String aType, final String aName,
            String aTypePlusName, final Object obj, final int iTimes, final BonusObj aBonusObj, final PlayerCharacter aPC)
    {
        final StringTokenizer aTok = new StringTokenizer(bString, "|");

        if (aTok.countTokens() < 3)
        {
            Logging.errorPrint("Badly formed BONUS:" + bString);

            return 0;
        }

        String aString = aTok.nextToken();

        if (!aString.equalsIgnoreCase(aType) || aString.endsWith("%LIST") || aName.equals("ALL"))
        {
            return 0;
        }

        final String aList = aTok.nextToken();

        if (!aList.equals("LIST") && !aList.equals("ALL") && (!aList.toUpperCase().contains(aName.toUpperCase())))
        {
            return 0;
        }

        if (aList.equals("ALL") && ((aName.contains("STAT=")) || (aName.contains("TYPE=")) || (aName.contains("LIST"))
                || (aName.contains("VAR"))))
        {
            return 0;
        }

        if (aTok.hasMoreTokens())
        {
            aString = aTok.nextToken();
        }

        double iBonus = 0;

        if (obj instanceof PlayerCharacter)
        {
            iBonus = ((PlayerCharacter) obj).getVariableValue(aString, "").doubleValue();
        } else if (obj instanceof Equipment)
        {
            iBonus = ((Equipment) obj).getVariableValue(aString, "", aPC).doubleValue();
        } else
        {
            try
            {
                iBonus = Float.parseFloat(aString);
            } catch (NumberFormatException e)
            {
                //Should this be ignored?
                Logging.errorPrint("calcBonus NumberFormatException in BONUS: " + aString, e);
            }
        }

        final String possibleBonusTypeString = aBonusObj.getTypeString();

        // must meet criteria before adding any bonuses
        if (obj instanceof PlayerCharacter)
        {
            if (!aBonusObj.qualifies((PlayerCharacter) obj, po))
            {
                return 0;
            }
        } else
        {
            if (!PrereqHandler.passesAll(aBonusObj, (Equipment) obj, aPC))
            {
                return 0;
            }
        }

        double bonus = 0;

        String bonusTypeString = null;

        final StringTokenizer bTok = new StringTokenizer(aList, ",");

        if (aList.equalsIgnoreCase("LIST"))
        {
            bTok.nextToken();
        } else if (aList.equalsIgnoreCase("ALL"))
        {
            // aTypePlusName looks like: "SKILL.ALL."
            // so we need to reset it to "SKILL.Hide."
            aTypePlusName = aType + '.' + aName + '.';
            bonus = iBonus;
            bonusTypeString = possibleBonusTypeString;
        }

        while (bTok.hasMoreTokens())
        {
            if (bTok.nextToken().equalsIgnoreCase(aName))
            {
                bonus += iBonus;
                bonusTypeString = possibleBonusTypeString;
            }
        }

        if (obj instanceof Equipment)
        {
            ((Equipment) obj).setBonusStackFor(bonus * iTimes, aTypePlusName + bonusTypeString);
        }

        // The "ALL" subtag is used to build the stacking bonusMap
        // not to get a bonus value, so just return
        if (aList.equals("ALL"))
        {
            return 0;
        }

        return bonus * iTimes;
    }

}
