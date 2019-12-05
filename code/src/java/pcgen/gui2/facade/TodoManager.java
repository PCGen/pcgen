/**
 * Copyright James Dempsey, 2011
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
import pcgen.facade.util.DefaultListFacade;
import pcgen.facade.util.ListFacade;

/**
 * The Class {@code TodoManager} tracks an unordered list of user tasks.
 * Each task is a TodoFacade instance which should have enough information to
 * direct the user to the field where they can achieve the task.
 */
@SuppressWarnings("TodoComment")
public class TodoManager
{
    private final DefaultListFacade<TodoFacade> todoList;

    TodoManager()
    {
        todoList = new DefaultListFacade<>();
    }

    synchronized ListFacade<TodoFacade> getTodoList()
    {
        return todoList;
    }

    /**
     * Add a task to be done to the todo list, if it is not already there.
     *
     * @param item The item to be added
     */
    synchronized void addTodo(TodoFacade item)
    {
        if (findTodoByMessage(item.getMessageKey(), item.getFieldName()) == null)
        {
            todoList.addElement(item);
        }
    }

    /**
     * Remove a task to be done from the todo list.
     *
     * @param messageKey The message key of the item to be removed
     */
    synchronized void removeTodo(String messageKey)
    {
        todoList.removeElement(findTodoByMessage(messageKey, null));
    }

    /**
     * Remove a task to be done from the todo list.
     *
     * @param messageKey The message key of the item to be removed
     * @param fieldName  The field name of the item to be removed, may be null to match any.
     */
    synchronized void removeTodo(String messageKey, String fieldName)
    {
        todoList.removeElement(findTodoByMessage(messageKey, fieldName));
    }

    /**
     * Search the todo list for an item with the specified messageKey.
     *
     * @param messageKey The key to be searched for
     * @param fieldName  The field name to be searched for, may be null to match any.
     * @return The TodoFacade item, or null if none found.
     */
    private TodoFacade findTodoByMessage(String messageKey, String fieldName)
    {
        if (messageKey != null)
        {
            for (TodoFacade item : todoList)
            {
                if (messageKey.equals(item.getMessageKey()))
                {
                    if ((fieldName == null) || fieldName.equals(item.getFieldName()))
                    {
                        return item;
                    }
                }
            }
        }

        return null;
    }

}
