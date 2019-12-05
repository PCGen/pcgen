/*
 * Copyright (c) 2006, 2009.
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 *
 */
package pcgen.gui2.converter.event;

import java.util.stream.IntStream;

import javax.swing.event.EventListenerList;

public final class TaskStrategyMessage
{
    private TaskStrategyMessage()
    {
    }

    public static void sendStatus(Object source, String string)
    {
        Object[] listeners = LISTENER_LIST.getListenerList();
        IntStream.iterate(listeners.length - 2, i -> i >= 0, i -> i - 2)
                .filter(i -> listeners[i] == TaskStrategyListener.class)
                .forEach(i -> ((TaskStrategyListener) listeners[i + 1]).processStatus(source, string));
    }

    private static final EventListenerList LISTENER_LIST = new EventListenerList();

    public static void addTaskStrategyListener(TaskStrategyListener listener)
    {
        LISTENER_LIST.add(TaskStrategyListener.class, listener);
    }
}
