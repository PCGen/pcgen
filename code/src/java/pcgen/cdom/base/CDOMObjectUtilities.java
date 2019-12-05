/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.base;

import java.util.List;

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.PlayerCharacter;

import org.apache.commons.collections4.ListUtils;

/**
 * CDOMObjectUtilities is a utility class designed to provide utility methods
 * when working with pcgen.cdom.base.CDOMObject Objects
 */
public final class CDOMObjectUtilities
{

    private CDOMObjectUtilities()
    {
        // Utility class should not be constructed
    }

    /**
     * Compares the Keys of two CDOMObjects. Returns a negative integer if the
     * key for the first object sorts before the key for the second object. Note
     * that null sorts last (though a CDOMObject should never return null from a
     * call to getKeyName(), this is error protected). This comparison is case
     * sensitive.
     *
     * @param cdo1 The first CDOMObject, for which the key will be compared
     * @param cdo2 The second CDOMObject, for which the key will be compared
     * @return a negative integer if the key for the first object sorts before
     * the key for the second object; a positive integer if the key for
     * the first object sorts after the key for the second object, or 0
     * if the keys are equal
     */
    public static int compareKeys(Loadable cdo1, Loadable cdo2)
    {
        String base = cdo1.getKeyName();
        if (base == null)
        {
            return (cdo2.getKeyName() == null) ? 0 : -1;
        } else
        {
            return (cdo2.getKeyName() == null) ? 1 : base.compareTo(cdo2.getKeyName());
        }
    }

    public static void addAdds(CDOMObject cdo, PlayerCharacter pc)
    {
        if (!pc.isAllowInteraction())
        {
            return;
        }
        List<PersistentTransitionChoice<?>> addList = ListUtils.emptyIfNull(cdo.getListFor(ListKey.ADD));
        addList.forEach(tc -> driveChoice(cdo, tc, pc));
    }

    public static void removeAdds(CDOMObject cdo, PlayerCharacter pc)
    {
        if (!pc.isAllowInteraction())
        {
            return;
        }
        List<PersistentTransitionChoice<?>> addList = ListUtils.emptyIfNull(cdo.getListFor(ListKey.ADD));
        addList.forEach(tc -> tc.remove(cdo, pc));
    }

    public static void checkRemovals(CDOMObject cdo, PlayerCharacter pc)
    {
        if (!pc.isAllowInteraction())
        {
            return;
        }
        List<PersistentTransitionChoice<?>> removeList = ListUtils.emptyIfNull(cdo.getListFor(ListKey.REMOVE));
        removeList.forEach(tc -> driveChoice(cdo, tc, pc));
    }

    public static void restoreRemovals(CDOMObject cdo, PlayerCharacter pc)
    {
        if (!pc.isAllowInteraction())
        {
            return;
        }
        List<PersistentTransitionChoice<?>> removeList = ListUtils.emptyIfNull(cdo.getListFor(ListKey.REMOVE));
        removeList.forEach(tc -> tc.remove(cdo, pc));
    }

    private static <T> void driveChoice(CDOMObject cdo, TransitionChoice<T> tc, final PlayerCharacter pc)
    {
        tc.act(tc.driveChoice(pc), cdo, pc);
    }
}
