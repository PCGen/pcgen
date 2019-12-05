/*
 * Copyright 2018 (C) Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.content.fact;

import java.util.function.Consumer;

import pcgen.base.util.Indirect;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.FactKey;
import pcgen.cdom.formula.PCGenScoped;
import pcgen.cdom.grouping.GroupingCollection;
import pcgen.cdom.grouping.GroupingInfo;

/**
 * A FactGrouping is a GroupingCollection that contains objects of a specific type (e.g.
 * Skill) that contain a specific value in a given FACT
 *
 * @param <T> The Type of object contained in this FactGrouping
 * @param <F> The Type of the Fact being checked in this FactGrouping
 */
public class FactGrouping<T extends CDOMObject, F> implements GroupingCollection<T>
{

    /**
     * Contains the underlying FactInfo for this FactGrouping (identifying the "rules of
     * the road").
     */
    private final FactInfo<T, F> def;

    /**
     * The Indirect containing the fact to be matched.
     */
    private final Indirect<F> toMatch;

    /**
     * The GroupingInfo that can identify which objects are to be part of this
     * FactGrouping.
     */
    private final GroupingInfo<T> info;

    /**
     * The child GroupingCollection providing additional grouping information.
     */
    private GroupingCollection<?> childGrouping;

    /**
     * Constructs a new FactGrouping from the given context, FactInfo and value.
     *
     * @param fi   The FactInfo indicating the underlying characteristics of the Fact that
     *             this FactGroup will check
     * @param info The GroupingInfo of the value that this FactGrouping will be looking for
     */
    public FactGrouping(FactInfo<T, F> fi, GroupingInfo<T> info)
    {
        if (fi.getUsableLocation().equals(CDOMObject.class))
        {
            throw new IllegalArgumentException("FactGrouping cannot be global");
        }
        String infoKey = info.getCharacteristic();
        if ((infoKey == null) || infoKey.isEmpty())
        {
            throw new IllegalArgumentException("FactGrouping must have value following =");
        }
        if (!fi.getFactName().equalsIgnoreCase(infoKey))
        {
            throw new IllegalArgumentException(
                    "FactGrouping expected grouping type of " + fi.getFactName() + " but it was " + infoKey);
        }
        String infoValue = info.getValue();
        if ((infoValue == null) || infoValue.isEmpty())
        {
            throw new IllegalArgumentException("FACT must have value following =");
        }
        def = fi;
        this.info = info;
        toMatch = def.getFormatManager().convertIndirect(infoValue);
        if (toMatch == null)
        {
            throw new IllegalArgumentException(
                    "Failed to convert " + infoValue + " as a " + def.getFormatManager().getManagedClass().getSimpleName());
        }
    }

    /**
     * Sets the child GroupingCollection to this FactGrouping.
     *
     * @param childCollection The child GroupingCollection for this FactGrouping
     */
    public void setChild(GroupingCollection<?> childCollection)
    {
        childGrouping = childCollection;
    }

    @Override
    public String getInstructions()
    {
        return info.getInstructions();
    }

    @Override
    public void process(PCGenScoped o, Consumer<PCGenScoped> consumer)
    {
        //Cast can not fail (as only CDOMObject can have facts)
        CDOMObject cdo = (CDOMObject) o;
        FactKey<F> fk = def.getFactKey();
        if (toMatch.get().equals(cdo.get(fk)))
        {
            if (childGrouping == null)
            {
                consumer.accept(o);
            } else
            {
                GroupingInfo<?> childInfo = info.getChild();
                for (PCGenScoped childObject : o.getChildren(childInfo.getObjectType()))
                {
                    childGrouping.process(childObject, consumer);
                }
            }
        }
    }
}
