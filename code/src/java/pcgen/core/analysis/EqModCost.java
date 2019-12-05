/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.core.analysis;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.Equipment;
import pcgen.core.EquipmentModifier;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.bonus.BonusObj;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.spell.Spell;
import pcgen.util.Delta;

public final class EqModCost
{
    private static final String S_CHARGES = "CHARGES";

    private EqModCost()
    {
    }

    public static BigDecimal addItemCosts(EquipmentModifier eqMod, final PlayerCharacter aPC, final String bonusType,
            final int qty, final Equipment parent)
    {
        double val = 0;

        Set<String> typesToGetBonusesFor = new HashSet<>();

        for (BonusObj bonus : eqMod.getBonusList(parent))
        {
            boolean meetsAll = true;

            if (bonus.getBonusName().equals(bonusType))
            {
                StringTokenizer aTok = new StringTokenizer(bonus.toString().substring(bonusType.length()), "|", false);
                final String bType = aTok.nextToken();
                aTok = new StringTokenizer(bType.substring(5), ".", false);

                StringBuilder typeString = new StringBuilder("TYPE");

                while (aTok.hasMoreTokens())
                {
                    final String sub_type = aTok.nextToken();
                    meetsAll = parent.isType(sub_type);

                    if (!meetsAll)
                    {
                        break;
                    }

                    typeString.append(".").append(sub_type);
                }

                if (meetsAll)
                {
                    typesToGetBonusesFor.add(typeString.toString());
                }
            }
        }

        for (String typeString : typesToGetBonusesFor)
        {
            val += eqMod.bonusTo(aPC, bonusType, typeString, parent);
        }

        return new BigDecimal(val * qty);
    }

    public static boolean getCostDouble(EquipmentModifier eqMod)
    {
        //
        // Uninitialized?
        //
        Boolean costdouble = eqMod.get(ObjectKey.COST_DOUBLE);
        if (costdouble == null)
        {
            if (eqMod.isType("MagicalEnhancement") || eqMod.isType("BaseMaterial"))
            {
                return false;
            }

            if (eqMod.isIType(Type.MAGIC))
            {
                return true;
            }

            for (Prerequisite preReq : eqMod.getPrerequisiteList())
            {
                if ("TYPE".equalsIgnoreCase(preReq.getKind())
                        && ((preReq.getKey().equalsIgnoreCase("EQMODTYPE=MagicalEnhancement"))
                        || (preReq.getKey().equalsIgnoreCase("EQMODTYPE.MagicalEnhancement"))))
                {
                    return true;
                }
            }
            return false;
        }

        return costdouble;
    }

    public static String getCost(EquipmentModifier eqMod, final String listEntry)
    {
        String costFormula = eqMod.getSafe(FormulaKey.COST).toString();
        String modChoice = "";

        while (costFormula.contains("%SPELLLEVEL"))
        {
            final int idx = costFormula.indexOf("%SPELLLEVEL");

            if (modChoice.isEmpty())
            {
                final int iLevel = EqModSpellInfo.getSpellInfo(listEntry, "SPELLLEVEL");

                if (iLevel == 0)
                {
                    modChoice = "0.5";
                } else
                {
                    modChoice = Integer.toString(iLevel);
                }
            }

            costFormula = costFormula.substring(0, idx) + modChoice + costFormula.substring(idx + 11);
        }

        costFormula = replaceCostSpellCost(costFormula, listEntry);
        costFormula = replaceCostSpellXPCost(costFormula, listEntry);
        costFormula = replaceCostCasterLevel(costFormula, listEntry);
        costFormula = replaceCostCharges(costFormula, listEntry);
        costFormula = replaceCostChoice(costFormula, listEntry);

        return costFormula;
    }

    private static String replaceCostCasterLevel(String costFormula, final String listEntry)
    {
        String modChoice = "";

        while (costFormula.contains("%CASTERLEVEL"))
        {
            final int idx = costFormula.indexOf("%CASTERLEVEL");

            if (modChoice.isEmpty())
            {
                final int iCasterLevel = EqModSpellInfo.getSpellInfo(listEntry, "CASTERLEVEL");
                modChoice = Integer.toString(iCasterLevel);

                //
                // Tack on the item creation multiplier, if there is one
                //
                final String castClassKey = EqModSpellInfo.getSpellInfoString(listEntry, "CASTER");

                if (!castClassKey.isEmpty())
                {
                    final PCClass castClass = Globals.getContext().getReferenceContext()
                            .silentlyGetConstructedCDOMObject(PCClass.class, castClassKey);

                    if (castClass != null)
                    {
                        final StringBuilder multiple = new StringBuilder(200);
                        String aString = castClass.get(StringKey.ITEMCREATE);

                        if (aString != null && !aString.isEmpty())
                        {
                            final StringTokenizer aTok = new StringTokenizer(aString, "+-*/()", true);

                            //
                            // This is to support older versions of the
                            // ITEMCREATE tag
                            // that allowed 0.5, because it used to be just a
                            // multiple
                            //
                            if (aTok.countTokens() == 1)
                            {
                                multiple.append(iCasterLevel).append('*').append(aString);
                            } else
                            {
                                while (aTok.hasMoreTokens())
                                {
                                    aString = aTok.nextToken();

                                    if (aString.equals("CL"))
                                    {
                                        multiple.append(iCasterLevel);
                                    } else
                                    {
                                        multiple.append(aString);
                                    }
                                }
                            }

                            modChoice = multiple.toString();
                        }
                    }
                }
            }

            costFormula = costFormula.substring(0, idx) + "(" + modChoice + ")" + costFormula.substring(idx + 12);
        }

        return costFormula;
    }

    private static String replaceCostCharges(String costFormula, final String listEntry)
    {
        String modChoice = "";

        while (costFormula.contains("%" + S_CHARGES))
        {
            final int idx = costFormula.indexOf("%" + S_CHARGES);

            if (modChoice.isEmpty())
            {
                modChoice = Integer.toString(EqModSpellInfo.getSpellInfo(listEntry, S_CHARGES));
            }

            costFormula = costFormula.substring(0, idx) + modChoice + costFormula.substring(idx + 8);
        }

        return costFormula;
    }

    private static String replaceCostSpellCost(String costFormula, final String listEntry)
    {
        String modChoice = "";

        while (costFormula.contains("%SPELLCOST"))
        {
            final int idx = costFormula.indexOf("%SPELLCOST");

            if (modChoice.isEmpty())
            {
                final String spellName = EqModSpellInfo.getSpellInfoString(listEntry, "SPELLNAME");
                final Spell aSpell = Globals.getContext().getReferenceContext()
                        .silentlyGetConstructedCDOMObject(Spell.class, spellName);

                if (aSpell != null)
                {
                    modChoice = aSpell.getSafe(ObjectKey.COST).toString();
                }
            }

            costFormula = costFormula.substring(0, idx) + modChoice + costFormula.substring(idx + 10);
        }

        return costFormula;
    }

    private static String replaceCostChoice(String costFormula, final String listEntry)
    {
        String modChoice = "";

        while (costFormula.contains("%CHOICE"))
        {
            final int idx = costFormula.indexOf("%CHOICE");

            if (modChoice.isEmpty())
            {
                final int offs = listEntry.lastIndexOf('|');
                int modValue = 0;

                try
                {
                    modValue = Delta.parseInt(listEntry.substring(offs + 1));
                } catch (NumberFormatException exc)
                {
                    // TODO: Should this really be ignored?
                }

                modChoice = Integer.toString(modValue);
            }

            costFormula = costFormula.substring(0, idx) + modChoice + costFormula.substring(idx + 7);
        }

        return costFormula;
    }

    private static String replaceCostSpellXPCost(String costFormula, final String listEntry)
    {
        String modChoice = "";

        while (costFormula.contains("%SPELLXPCOST"))
        {
            final int idx = costFormula.indexOf("%SPELLXPCOST");

            if (modChoice.isEmpty())
            {
                final String spellName = EqModSpellInfo.getSpellInfoString(listEntry, "SPELLNAME");
                final Spell aSpell = Globals.getContext().getReferenceContext()
                        .silentlyGetConstructedCDOMObject(Spell.class, spellName);

                if (aSpell != null)
                {
                    modChoice = Integer.toString(aSpell.getSafe(IntegerKey.XP_COST));
                }
            }

            costFormula = costFormula.substring(0, idx) + modChoice + costFormula.substring(idx + 12);
        }

        return costFormula;
    }

}
