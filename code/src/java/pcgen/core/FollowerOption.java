/*
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
package pcgen.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.QualifyingObject;
import pcgen.cdom.list.CompanionList;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceUtilities;

/**
 * This class represents a possible choice for a follower. This is basically a
 * Race with a "FOLLOWERADJUSTMENT" that modifies the owner's effective level
 * when selecting a follower of this type. Prereqs can also be specified
 */
public class FollowerOption extends ConcretePrereqObject implements Comparable<FollowerOption>, QualifyingObject
{
    private int theAdjustment = 0;
    private final CDOMReference<Race> ref;
    private final CDOMSingleRef<CompanionList> list;

    public FollowerOption(CDOMReference<Race> race, CDOMSingleRef<CompanionList> listref)
    {
        Objects.requireNonNull(race, "Cannot have FollowerOption with null race");
        Objects.requireNonNull(listref, "Cannot have FollowerOption with null list reference");
        ref = race;
        list = listref;
    }

    /**
     * Returns the race associated with this option. If this option represents a
     * group of races this method will return null.
     *
     * @return The Race associated or null
     */
    public Race getRace()
    {
        Collection<Race> races = ref.getContainedObjects();
        return races.size() == 1 ? races.iterator().next() : null;
    }

    public CDOMReference<Race> getRaceRef()
    {
        return ref;
    }

    /**
     * Sets the variable adjustment for a master selecting this option. For
     * example an adjustment of -3 would mean the master's level would be 3
     * lower for purposes of applying companion mods.
     *
     * @param anAdjustment Amount to modify the master's level by
     */
    public void setAdjustment(final int anAdjustment)
    {
        theAdjustment = anAdjustment;
    }

    /**
     * Returns the adjustment to the master's level for this option.
     *
     * @return The adjustment to the master's level
     */
    public int getAdjustment()
    {
        return theAdjustment;
    }

    /**
     * This method is overridden to also check that a master has enough
     * effective levels to have a positive level after applying any adjustment
     * for this follower. For example, if a follower has an adjustment of -3
     * then the master must have at least 4 levels to qualify for this follower
     * (4 - 3 &gt; 0)
     */
    @Override
    public boolean qualifies(final PlayerCharacter aPC, Object source)
    {
        if (theAdjustment != 0)
        {
            final int lvl = aPC.getEffectiveCompanionLevel(list.get());
            if (lvl + theAdjustment <= 0)
            {
                return false;
            }
        }

        return super.qualifies(aPC, source);
    }

    /**
     * Compares this FollowerOption to another. This uses the race name of the
     * option to do the comparison.
     *
     * @param anO The FollowerOption to compare to.
     * @return The comparison between the objects
     */
    @Override
    public int compareTo(FollowerOption anO)
    {
        return ReferenceUtilities.compareRefs(ref, anO.ref);
    }

    public CDOMSingleRef<CompanionList> getListRef()
    {
        return list;
    }

    public Collection<FollowerOption> getExpandedOptions()
    {
        final List<FollowerOption> options = new ArrayList<>();
        if (ref.getObjectCount() == 1)
        {
            options.add(this);
            return options;
        }
        for (Race r : ref.getContainedObjects())
        {
            final FollowerOption opt = new FollowerOption(CDOMDirectSingleRef.getRef(r), list);
            opt.setAdjustment(getAdjustment());
            opt.addAllPrerequisites(getPrerequisiteList());
            options.add(opt);
        }
        return options;
    }
}
