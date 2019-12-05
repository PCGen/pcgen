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
 */
package pcgen.core.analysis;

import pcgen.core.PlayerCharacter;
import pcgen.core.display.CharacterDisplay;
import pcgen.util.Delta;
import pcgen.util.enumeration.AttackType;

public final class AttackInfo
{

    private AttackInfo()
    {
    }

    /**
     * Get Attack Information for a PC.
     * This will return the attack token value requested for the supplied
     * character. Note the return value is always a formatted string such as +8
     * or +8/+3
     *
     * @param pc         The character to retrieve the attack value for.
     * @param attackType The type of attack to be returned
     *                   - GRAPPLE, MELLEE, RANGED or UNARMED
     * @param modifier   The modified to the attack - BASE, EPIC, MISC,
     *                   SIZE, STAT, TOTAL or an empty string
     * @return The token value.
     */
    public static String getAttackInfo(PlayerCharacter pc, AttackType attackType, String modifier)
    {
        if (modifier.equals("TOTAL"))
        {
            if (attackType.equals(AttackType.RANGED))
            {
                int total = getTotalToken(pc, attackType);
                return pc.getAttackString(AttackType.RANGED, total);
            } else if (attackType.equals(AttackType.UNARMED))
            {
                int total = getTotalToken(pc, AttackType.MELEE);
                // TODO: Is this correct for 3.0 also?
                return pc.getAttackString(AttackType.MELEE, total);
                //return pc.getAttackString(Constants.ATTACKSTRING_UNARMED, total);
            } else
            {
                int total = getTotalToken(pc, attackType);
                return pc.getAttackString(AttackType.MELEE, total);
            }
        }
        return getSubToken(pc, attackType, modifier);
    }

    /**
     * Get total ATTACK token
     *
     * @param pc
     * @param at
     * @return total ATTACK token
     */
    public static int getTotalToken(PlayerCharacter pc, AttackType at)
    {
        final int tohitBonus =
                (int) pc.getTotalBonusTo("TOHIT", "TOHIT") + (int) pc.getTotalBonusTo("TOHIT", "TYPE." + at);
        final int totalBonus =
                (int) pc.getTotalBonusTo("COMBAT", "TOHIT") + (int) pc.getTotalBonusTo("COMBAT", "TOHIT." + at);
        return tohitBonus + totalBonus;
    }

    public static String getSubToken(PlayerCharacter pc, AttackType attackType, String modifier)
    {
        switch (modifier)
        {
            case "BASE":
                return Delta.toString(getBaseToken(pc));
            case "EPIC":
                return Delta.toString(getEpicToken(pc));
            case "MISC":
                return Delta.toString(getMiscToken(pc, attackType));
            case "SIZE":
                return Delta.toString(getSizeToken(pc, attackType));
            case "STAT":
                return Delta.toString(getStatToken(pc.getDisplay(), attackType));
            case "TOTAL":
                // TOTAL is handled in getParsedToken()
                //int total = getTotalToken(pc, "MELEE");
                //return pc.getAttackString(Constants.ATTACKSTRING_MELEE, total);
                break;
            default:
                return pc.getAttackString(attackType);
        }
        return "";
    }

    /**
     * Get the base ATTACK token
     *
     * @param pc
     * @return base ATTACK token
     */
    public static int getBaseToken(PlayerCharacter pc)
    {
        return pc.baseAttackBonus();
    }

    /**
     * Get the epic ATTACK token
     *
     * @param pc
     * @return epic ATTACK token
     */
    public static int getEpicToken(PlayerCharacter pc)
    {
        return (int) pc.getBonusDueToType("COMBAT", "TOHIT", "EPIC");
    }

    /**
     * Get the misc ATTACK token
     *
     * @param pc
     * @param at
     * @return misc ATTACK token
     */
    public static int getMiscToken(PlayerCharacter pc, AttackType at)
    {
        int tohitBonus = ((int) pc.getTotalBonusTo("TOHIT", "TOHIT") + (int) pc.getTotalBonusTo("TOHIT", "TYPE." + at))
                - (int) pc.getDisplay().getStatBonusTo("TOHIT", "TYPE." + at)
                - (int) pc.getSizeAdjustmentBonusTo("TOHIT", "TOHIT");
        int miscBonus =
                ((int) pc.getTotalBonusTo("COMBAT", "TOHIT") + (int) pc.getTotalBonusTo("COMBAT", "TOHIT." + at))
                        - (int) pc.getDisplay().getStatBonusTo("COMBAT", "TOHIT." + at)
                        - (int) pc.getSizeAdjustmentBonusTo("COMBAT", "TOHIT")
                        - (int) pc.getSizeAdjustmentBonusTo("COMBAT", "TOHIT." + at)
                        - (int) pc.getBonusDueToType("COMBAT", "TOHIT", "EPIC");
        return miscBonus + tohitBonus;
    }

    /**
     * Get the size ATTACK token
     *
     * @param pc
     * @param aType
     * @return size ATTACK token
     */
    public static int getSizeToken(PlayerCharacter pc, AttackType aType)
    {
        int tohitBonus = (int) pc.getSizeAdjustmentBonusTo("TOHIT", "TOHIT")
                + (int) pc.getSizeAdjustmentBonusTo("TOHIT", "TYPE." + aType);
        int sizeBonus = (int) pc.getSizeAdjustmentBonusTo("COMBAT", "TOHIT")
                + (int) pc.getSizeAdjustmentBonusTo("COMBAT", "TOHIT." + aType);

        return sizeBonus + tohitBonus;
    }

    /**
     * get stat ATTACK token
     *
     * @return stat ATTACK token
     */
    public static int getStatToken(CharacterDisplay display, AttackType at)
    {
        final int tohitBonus = (int) display.getStatBonusTo("TOHIT", "TYPE." + at);
        final int statBonus = (int) display.getStatBonusTo("COMBAT", "TOHIT." + at);

        return statBonus + tohitBonus;
    }

}
