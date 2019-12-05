/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.content.fact;

import java.util.Objects;

import pcgen.base.util.FormatManager;
import pcgen.base.util.ObjectContainer;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.GroupDefinition;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.grouping.GroupingCollection;
import pcgen.cdom.grouping.GroupingDefinition;
import pcgen.cdom.grouping.GroupingInfo;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.ChoiceSetLoadUtilities;

/**
 * A FactGroupDefinition is a GroupDefinition built around a Fact, specifically
 * relying upon a FactInfo. This is effectively a factory that can generate an
 * appropriate FactGroup for a given value, based upon the FactInfo provided at
 * construction.
 *
 * @param <T> The Type of object upon which this FactGroupDefintion can be used
 *            (the host of the fact)
 * @param <F> The Type of object this FactGroupDefinition contains (the content
 *            of the fact)
 */
public class FactGroupDefinition<T extends CDOMObject, F> implements GroupDefinition<T>, GroupingDefinition<T>
{

    /**
     * The underlying FactInfo indicating static information about the Fact for
     * which this FactGroupDefintion can create Primitives
     */
    private final FactInfo<T, F> def;

    /**
     * Constructs a new FactGroupDefinition with the given FactInfo.
     *
     * @param fi The FactInfo underlying this FactGroupDefinition
     * @throws IllegalArgumentException if the given FactInfo is null
     */
    public FactGroupDefinition(FactInfo<T, F> fi)
    {
        Objects.requireNonNull(fi, "Fact Info cannot be null");
        def = fi;
    }

    @Override
    public String getPrimitiveName()
    {
        return def.getFactName();
    }

    @Override
    public GroupingState getGroupingState()
    {
        return GroupingState.ANY;
    }

    @Override
    public Class<T> getReferenceClass()
    {
        return def.getUsableLocation();
    }

    @Override
    public FormatManager<?> getFormatManager()
    {
        return def.getFormatManager();
    }

    @Override
    public ObjectContainer<T> getPrimitive(LoadContext context, String value)
    {
        return new FactGroup<>(context, def, value);
    }

    @Override
    public String getIdentification()
    {
        return def.getFactName();
    }

    @Override
    public Class<?> getUsableLocation()
    {
        return def.getUsableLocation();
    }

    @Override
    public GroupingCollection<T> process(LoadContext context, GroupingInfo<T> info)
    {
        FactGrouping<T, F> groupGrouping = new FactGrouping<>(def, info);
        if (info.hasChild())
        {
            GroupingCollection<?> childCollection =
                    ChoiceSetLoadUtilities.getDynamicGroup(context, info.getChild());
            groupGrouping.setChild(childCollection);
        }
        return groupGrouping;
    }

    @Override
    public boolean requiresDirect()
    {
        //FACTs can't go to a parent - they are precise by their definitions
        return true;
    }
}
