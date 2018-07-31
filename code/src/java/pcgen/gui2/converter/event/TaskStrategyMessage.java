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

import javax.swing.event.EventListenerList;

public final class TaskStrategyMessage
{

	private TaskStrategyMessage()
	{
		super();
	}

	public static void sendMessage(Object owner, String string)
	{
		Object[] listeners = LISTENER_LIST.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == TaskStrategyListener.class)
			{
				((TaskStrategyListener) listeners[i + 1]).processMessage(owner, string);
			}
		}
	}

	public static void sendStatus(Object source, String string)
	{
		Object[] listeners = LISTENER_LIST.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == TaskStrategyListener.class)
			{
				((TaskStrategyListener) listeners[i + 1]).processStatus(source, string);
			}
		}
	}

	public static void sendActiveItem(Object source, String string)
	{
		Object[] listeners = LISTENER_LIST.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == TaskStrategyListener.class)
			{
				((TaskStrategyListener) listeners[i + 1]).processActiveItem(source, string);
			}
		}
	}

	private static final EventListenerList LISTENER_LIST = new EventListenerList();

	public static void addTaskStrategyListener(TaskStrategyListener listener)
	{
		LISTENER_LIST.add(TaskStrategyListener.class, listener);
	}

	public static synchronized TaskStrategyListener[] getTaskStrategyListeners()
	{
		return LISTENER_LIST.getListeners(TaskStrategyListener.class);
	}

	public static void removeTaskStrategyListener(TaskStrategyListener listener)
	{
		LISTENER_LIST.remove(TaskStrategyListener.class, listener);
	}

}
