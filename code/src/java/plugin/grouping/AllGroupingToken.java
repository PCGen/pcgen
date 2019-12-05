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
package plugin.grouping;

import java.util.Objects;
import java.util.function.Consumer;

import pcgen.cdom.base.Loadable;
import pcgen.cdom.formula.PCGenScoped;
import pcgen.cdom.grouping.GroupingCollection;
import pcgen.cdom.grouping.GroupingDefinition;
import pcgen.cdom.grouping.GroupingInfo;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.ChoiceSetLoadUtilities;

/**
 * AllGroupingToken parses the "ALL" Grouping to be able to create an appropriate
 * GroupingCollection.
 *
 * @param <T> The Format of the object type returned during processing by this
 *            AllGroupingToken
 */
public class AllGroupingToken<T extends Loadable> implements GroupingDefinition<T>
{

    @Override
    public String getIdentification()
    {
        return "ALL";
    }

    @Override
    public Class<Object> getUsableLocation()
    {
        return Object.class;
    }

    @Override
    public GroupingCollection<T> process(LoadContext context, GroupingInfo<T> info)
    {
        if ((info.getCharacteristic() != null) && (!info.getCharacteristic().isEmpty()))
        {
            throw new IllegalArgumentException("Instructions using = prohibited for ALL Grouping");
        }
        AllGrouping<T> allGrouping = new AllGrouping<>(info);
        if (info.hasChild())
        {
            GroupingCollection<?> childCollection =
                    ChoiceSetLoadUtilities.getDynamicGroup(context, info.getChild());
            allGrouping.setChild(childCollection);
        }
        return allGrouping;
    }

    @Override
    public boolean requiresDirect()
    {
        return false;
    }

    /**
     * AllGrouping serves as the GroupingCollection for the "ALL" Grouping.
     *
     * @param <T> The Format of the object type contained by this AllGrouping
     */
    private static class AllGrouping<T extends Loadable> implements GroupingCollection<T>
    {

        /**
         * The GroupingInfo used to determine the format of objects contained by this
         * AllGrouping.
         */
        private GroupingInfo<T> groupingInfo;

        /**
         * The child GroupingCollection providing additional grouping information.
         */
        private GroupingCollection<?> childGrouping;

        /**
         * Constructs a new AllGrouping from the given GroupingInfo.
         *
         * @param groupingInfo The GroupingInfo used to determine the format of objects contained
         *                     by this AllGrouping
         */
        public AllGrouping(GroupingInfo<T> groupingInfo)
        {
            this.groupingInfo = Objects.requireNonNull(groupingInfo);
        }

        public void setChild(GroupingCollection<?> childCollection)
        {
            childGrouping = childCollection;
        }

        @Override
        public String getInstructions()
        {
            return groupingInfo.getInstructions();
        }

        @Override
        public void process(PCGenScoped o, Consumer<PCGenScoped> consumer)
        {
            if (childGrouping == null)
            {
                consumer.accept(o);
            } else
            {
                GroupingInfo<?> childInfo = groupingInfo.getChild();
                for (PCGenScoped childObject : o.getChildren(childInfo.getObjectType()))
                {
                    childGrouping.process(childObject, consumer);
                }
            }
        }
    }
}
