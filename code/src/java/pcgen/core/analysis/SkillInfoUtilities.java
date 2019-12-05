/*
 * Copyright 2009 (C) Tom Parker <thpr@users.sourceforge.net>
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Globals;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.bonus.BonusObj;
import pcgen.core.bonus.BonusUtilities;

public final class SkillInfoUtilities
{

    private SkillInfoUtilities()
    {
    }

    /**
     * Get the key attribute's description
     *
     * @return description
     */
    public static String getKeyStatFromStats(PlayerCharacter pc, Skill sk)
    {
        CDOMSingleRef<PCStat> stat = sk.get(ObjectKey.KEY_STAT);
        if (stat == null)
        {
            if (Globals.getGameModeHasPointPool())
            {
                List<PCStat> statList = SkillInfoUtilities.getKeyStatList(pc, sk, null);
                StringBuilder sb = new StringBuilder(50);
                boolean needSlash = false;
                for (PCStat s : statList)
                {
                    if (needSlash)
                    {
                        sb.append('/');
                    }
                    sb.append(s.getKeyName());
                }
                return sb.toString();
            } else
            {
                return "";
            }
        } else
        {
            return stat.get().getKeyName();
        }
    }

    /**
     * Get a list of PCStat's that apply a SKILL bonus to this skill. Generates
     * (optionally, if typeList is non-null) a list of String's types
     *
     * @param typeList
     * @return List of stats that apply
     */
    public static List<PCStat> getKeyStatList(PlayerCharacter pc, Skill sk, List<Type> typeList)
    {
        List<PCStat> aList = new ArrayList<>();
        if (Globals.getGameModeHasPointPool())
        {
            for (Type aType : sk.getTrueTypeList(false))
            {
                for (PCStat stat : pc.getDisplay().getStatSet())
                {
                    //
                    // Get a list of all BONUS:SKILL|TYPE.<type>|x for this
                    // skill that would come from current stat
                    //
                    List<BonusObj> bonusList = BonusUtilities.getBonusFromList(stat.getSafeListFor(ListKey.BONUS),
                            "SKILL", "TYPE." + aType);
                    if (!bonusList.isEmpty())
                    {
                        for (int iCount = bonusList.size() - 1;iCount >= 0;--iCount)
                        {
                            aList.add(stat);
                        }
                        if ((typeList != null) && !typeList.contains(aType))
                        {
                            typeList.add(aType);
                        }
                    }
                }
            }
        }
        return aList;
    }

    /**
     * Get an iterator for the sub types
     *
     * @return iterator for the sub types
     */
    public static Iterator<Type> getSubtypeIterator(Skill sk)
    {
        List<Type> ret = sk.getSafeListFor(ListKey.TYPE);
        CDOMSingleRef<PCStat> keystat = sk.get(ObjectKey.KEY_STAT);
        if (keystat == null)
        {
            ret.remove(Type.NONE);
        } else
        {
            // skip the keystat
            ret.remove(Type.getConstant(keystat.get().getDisplayName()));
            /*
             * TODO This is magic, and makes tremendous assumptions about the
             * DATA - BAD BAD BAD
             */
        }
        return ret.iterator();
    }

}
