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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.formula.PCGenScoped;
import pcgen.cdom.grouping.GroupingCollection;
import pcgen.cdom.grouping.GroupingDefinition;
import pcgen.cdom.grouping.GroupingInfo;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.ChoiceSetLoadUtilities;

/**
 * GroupGroupingToken parses the "GROUP=x" Grouping to be able to create an appropriate
 * GroupingCollection.
 *
 * @param <T> The Format of the object type returned during processing by this
 *            GroupGroupingToken
 */
public class GroupGroupingToken<T extends Loadable> implements GroupingDefinition<T>
{

    @Override
    public String getIdentification()
    {
        return "GROUP";
    }

    @Override
    public Class<Object> getUsableLocation()
    {
        return Object.class;
    }

    @Override
    public GroupingCollection<T> process(LoadContext context, GroupingInfo<T> info)
    {
        GroupGrouping<T> groupGrouping = new GroupGrouping<>(info);
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
        return false;
    }

    /**
     * GroupGrouping serves as the GroupingCollection for the "GROUP=x" Grouping.
     *
     * @param <T> The Format of the object type contained by this GroupGrouping
     */
    private static class GroupGrouping<T extends Loadable> implements GroupingCollection<T>
    {

        /**
         * The GroupingInfo used to determine the format and GROUP: of objects contained
         * by this GroupGrouping.
         */
        private GroupingInfo<T> groupingInfo;

        /**
         * The child GroupingCollection providing additional grouping information.
         */
        private GroupingCollection<?> childGrouping;

        /**
         * Constructs a new GroupGrouping from the given GroupingInfo.
         *
         * @param info The GroupingInfo used to determine the format and GROUP: of objects
         *             contained by this GroupGrouping
         */
        public GroupGrouping(GroupingInfo<T> info)
        {
            this.groupingInfo = Objects.requireNonNull(info);
            String value = info.getValue();
            if ((value == null) || value.isEmpty())
            {
                throw new IllegalArgumentException("GROUP must have value following =");
            }
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
            if ((o instanceof CDOMObject) && ((CDOMObject) o).containsInList(ListKey.GROUP, groupingInfo.getValue()))
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
}
