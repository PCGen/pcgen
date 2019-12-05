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

import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.Ability;
import pcgen.core.Equipment;
import pcgen.core.EquipmentModifier;
import pcgen.core.spell.Spell;
import pcgen.util.Delta;

public final class EqModSpellInfo
{
    private static final String S_CHARGES = "CHARGES";

    private EqModSpellInfo()
    {
    }

    public static String getSpellInfoString(final String listEntry, final String desiredInfo)
    {
        final int offs = listEntry.indexOf(desiredInfo + "[");
        final int offs2 = listEntry.indexOf(']', offs + 1);

        if ((offs >= 0) && (offs2 > offs))
        {
            return listEntry.substring(offs + desiredInfo.length() + 1, offs2);
        }

        return "";
    }

    public static int getSpellInfo(final String listEntry, final String desiredInfo)
    {
        int modValue = 0;
        final String info = getSpellInfoString(listEntry, desiredInfo);

        if (!info.isEmpty())
        {
            try
            {
                modValue = Delta.parseInt(info);
            } catch (NumberFormatException exc)
            {
                // TODO: Should this really be ignored?
            }
        }

        return modValue;
    }

    public static void setRemainingCharges(Equipment parent, EquipmentModifier eqMod, final int remainingCharges)
    {
        if (parent.hasAssociations(eqMod))
        {
            List<String> assoc = parent.removeAllAssociations(eqMod);
            String listEntry = assoc.get(0);
            String chargeInfo = EqModSpellInfo.getSpellInfoString(listEntry, S_CHARGES);

            if (!chargeInfo.isEmpty())
            {
                chargeInfo = S_CHARGES + '[' + chargeInfo + ']';

                final int idx = listEntry.indexOf(chargeInfo);
                listEntry = listEntry.substring(0, idx) + listEntry.substring(idx + chargeInfo.length());
                listEntry += (S_CHARGES + '[' + Integer.toString(remainingCharges) + ']');
                assoc.set(0, listEntry);
            }
            for (String s : assoc)
            {
                parent.addAssociation(eqMod, s);
            }
        }
    }

    public static int getRemainingCharges(Equipment parent, EquipmentModifier eqMod)
    {
        if (parent.hasAssociations(eqMod))
        {
            return EqModSpellInfo.getSpellInfo(parent.getFirstAssociation(eqMod), S_CHARGES);
        }

        return 0;
    }

    public static int getUsedCharges(Equipment parent, EquipmentModifier eqMod)
    {
        return eqMod.get(IntegerKey.MAX_CHARGES) - getRemainingCharges(parent, eqMod);
    }

    /**
     * Here be dragons
     * <p>
     * Builds up a big mad string representing the spell info and then stores it
     * in the first entry of associated.
     * <p>
     * TODO store this a separate fields or as a spell object or some other way
     * that doesn't involve turning this into a string and then parsing the
     * string when we want to do something with the info.
     *
     * @param parent              TODO
     * @param spellCastingClass   a PCClass Object, the class that this spell will be cast as
     * @param theSpell            a Spell Object
     * @param spellVariant        a string
     * @param spellType           arcane, divine, etc.
     * @param spellLevel          an int the level of the spell
     * @param spellCasterLevel    Caster level the spell is cast at
     * @param spellMetamagicFeats Any metamagic feats applied
     * @param charges             how many times can it be cast
     */
    public static void setSpellInfo(Equipment parent, EquipmentModifier eqMod, final CDOMObject spellCastingClass,
            final Spell theSpell, final String spellVariant, final String spellType, final int spellLevel,
            final int spellCasterLevel, final Object[] spellMetamagicFeats, final int charges)
    {
        final StringBuilder spellInfo = new StringBuilder(100);
        spellInfo.append("SPELLNAME[").append(theSpell.getKeyName()).append("] ");
        spellInfo.append("CASTER[").append(spellCastingClass.getKeyName()).append("] ");

        if (!spellVariant.isEmpty())
        {
            spellInfo.append("VARIANT[").append(spellVariant).append("] ");
        }

        spellInfo.append("SPELLTYPE[").append(spellType).append("] ");
        spellInfo.append("SPELLLEVEL[").append(spellLevel).append("] ");
        spellInfo.append("CASTERLEVEL[").append(spellCasterLevel).append("] ");

        if (charges > 0)
        {
            spellInfo.append(S_CHARGES).append('[').append(charges).append("] ");
        }

        if ((spellMetamagicFeats != null) && (spellMetamagicFeats.length > 0))
        {
            /*
             * Have considered whether this needs to be expanded to include
             * Category. These are actually Feats and the information is only
             * used by toString()
             */
            spellInfo.append("METAFEATS[");

            for (int i = 0;i < spellMetamagicFeats.length;i++)
            {
                final Ability aFeat = (Ability) spellMetamagicFeats[i];

                if (i != 0)
                {
                    spellInfo.append(", ");
                }

                spellInfo.append(aFeat.getKeyName());
            }

            spellInfo.append("] ");
        }

        parent.addAssociation(eqMod, spellInfo.toString());
    }
}
