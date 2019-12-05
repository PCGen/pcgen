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

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.SubstitutionClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PCClassLoader;
import pcgen.persistence.lst.SourceEntry;
import pcgen.persistence.lst.utils.DeferredLine;
import pcgen.util.Logging;

public final class SubstitutionLevelSupport
{

    private SubstitutionLevelSupport()
    {
    }

    private static boolean levelArrayQualifies(int level, final PlayerCharacter pc, final String aLine,
            final SourceEntry tempSource, CDOMObject source)
    {
        final PCClassLoader classLoader = new PCClassLoader();
        PCClass dummyClass = new PCClass();

        try
        {
            classLoader.parseLine(Globals.getContext(), dummyClass, aLine, tempSource);
        } catch (PersistenceLayerException e)
        {
            Logging.errorPrint("Unable to parse line from levelArray: " + aLine);
        }
        return dummyClass.getOriginalClassLevel(level).qualifies(pc, source);
    }

    /**
     * Apply the level mods to a class
     *
     * @param aClass
     */
    public static void applyLevelArrayModsToLevel(SubstitutionClass sc, final PCClass aClass, final int aLevel,
            final PlayerCharacter aPC)
    {
        List<DeferredLine> levelArray = sc.getListFor(ListKey.SUB_CLASS_LEVEL);
        if (levelArray == null)
        {
            return;
        }

        List<DeferredLine> newLevels = new ArrayList<>();
        for (DeferredLine line : levelArray)
        {
            String aLine = line.lstLine;
            final int modLevel = Integer.parseInt(aLine.substring(0, aLine.indexOf('\t')));

            if (aLevel == modLevel)
            {
                if (levelArrayQualifies(aLevel, aPC, aLine, line.source, aClass))
                {
                    newLevels.add(line);
                }
            }
        }

        // find all qualifying level lines for this level
        // and put into newLevels list.
        if (!newLevels.isEmpty())
        {
            aPC.setSubstitutionLevel(aClass, sc.getOriginalClassLevel(aLevel));
        }
    }

    public static boolean qualifiesForSubstitutionLevel(PCClass cl, SubstitutionClass sc, PlayerCharacter pc, int level)
    {
        List<DeferredLine> levelArray = sc.getListFor(ListKey.SUB_CLASS_LEVEL);
        if (levelArray == null)
        {
            return false;
        }

        for (DeferredLine line : levelArray)
        {
            String aLine = line.lstLine;
            final int modLevel = Integer.parseInt(aLine.substring(0, aLine.indexOf('\t')));

            if (level == modLevel)
            {
                if (!levelArrayQualifies(level, pc, aLine, line.source, cl))
                {
                    return false;
                }
            }
        }

        return true;
    }

}
