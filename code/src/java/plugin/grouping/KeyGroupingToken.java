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
 * KeyGroupingToken parses the "GROUP=x" Grouping to be able to create an appropriate
 * GroupingCollection.
 *
 * @param <T> The Format of the object type returned during processing by this
 *            KeyGroupingToken
 */
public class KeyGroupingToken<T extends Loadable & PCGenScoped> implements GroupingDefinition<T>
{

    @Override
    public String getIdentification()
    {
        return "KEY";
    }

    @Override
    public Class<Object> getUsableLocation()
    {
        return Object.class;
    }

    @Override
    public GroupingCollection<T> process(LoadContext context, GroupingInfo<T> info)
    {
        KeyGrouping<T> keyGrouping = new KeyGrouping<>(info);
        if (info.hasChild())
        {
            GroupingCollection<?> childCollection =
                    ChoiceSetLoadUtilities.getDynamicGroup(context, info.getChild());
            keyGrouping.setChild(childCollection);
        }
        return keyGrouping;
    }

    @Override
    public boolean requiresDirect()
    {
        return false;
    }

    /**
     * KeyGrouping serves as the GroupingCollection for the "GROUP=x" Grouping.
     *
     * @param <T> The Format of the object type contained by this KeyGrouping
     */
    private static class KeyGrouping<T extends Loadable> implements GroupingCollection<T>
    {
        /**
         * The GroupingInfo used to determine the format and GROUP: of objects contained
         * by this KeyGrouping.
         */
        private final GroupingInfo<T> groupingInfo;

        /**
         * The child GroupingCollection providing additional grouping information.
         */
        private GroupingCollection<?> childGrouping;

        /**
         * Constructs a new KeyGrouping from the given GroupingInfo.
         *
         * @param info The GroupingInfo used to determine the format and KEY of objects
         *             contained by this KeyGrouping
         */
        public KeyGrouping(GroupingInfo<T> info)
        {
            String value = info.getValue();
            if ((value == null) || value.isEmpty())
            {
                throw new IllegalArgumentException("KEY must have value following =");
            }
            this.groupingInfo = Objects.requireNonNull(info);
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
            if (!o.getKeyName().equals(groupingInfo.getValue()))
            {
                return;
            }
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
