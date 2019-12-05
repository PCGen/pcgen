/**
 * Copyright 2010 (C) James Dempsey
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.gui2.facade;

import pcgen.facade.core.TodoFacade;
import pcgen.util.enumeration.Tab;

/**
 * The Class {@code TodoFacadeImpl} represents a task
 * to be done for a character.
 */
@SuppressWarnings("TodoComment")
public class TodoFacadeImpl implements TodoFacade
{

    private final Tab tab;
    private final String fieldName;
    private final String messageKey;
    private final int order;
    private String subTabName;

    /**
     * Create a new todo task.
     *
     * @param tab        The tab on which the task exists
     * @param fieldName  The field on which the task exists
     * @param messageKey The il8n property key of the task details.
     */
    TodoFacadeImpl(Tab tab, String fieldName, String messageKey, int order)
    {
        this.tab = tab;
        this.fieldName = fieldName;
        this.messageKey = messageKey;
        this.order = order;
    }

    /**
     * Create a new todo task.
     *
     * @param tab        The tab on which the task exists
     * @param fieldName  The field on which the task exists
     * @param messageKey The il8n property key of the task details.
     * @param subTabName The internal name of the sub tab where the task can be completed.
     * @param order      The value for use when sorting the messages, low appears higher in the displayed list.
     */
    TodoFacadeImpl(Tab tab, String fieldName, String messageKey, String subTabName, int order)
    {
        this.tab = tab;
        this.fieldName = fieldName;
        this.messageKey = messageKey;
        this.subTabName = subTabName;
        this.order = order;
    }

    @Override
    public String getFieldName()
    {
        return fieldName;
    }

    @Override
    public String getMessageKey()
    {
        return messageKey;
    }

    @Override
    public Tab getTab()
    {
        return tab;
    }

    @Override
    public int compareTo(TodoFacade that)
    {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        // quick check for the same object
        if (this == that)
        {
            return EQUAL;
        }

        // Sort first by tab
        if (this.tab != that.getTab())
        {
            return this.tab.compareTo(that.getTab());
        }

        // Then sort by the order
        if (that instanceof TodoFacadeImpl)
        {
            if (this.order > ((TodoFacadeImpl) that).order)
            {
                return AFTER;
            }
            if (this.order < ((TodoFacadeImpl) that).order)
            {
                return BEFORE;
            }
            return this.fieldName.compareTo(((TodoFacadeImpl) that).fieldName);
        }

        return EQUAL;
    }

    @Override
    public String getSubTabName()
    {
        return subTabName;
    }

}
