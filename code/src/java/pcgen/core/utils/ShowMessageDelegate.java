/*
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
package pcgen.core.utils;

import java.util.Observable;
import java.util.logging.Level;

import pcgen.util.Logging;

/**
 * This is a facade for gui objects in the core code.
 */
public final class ShowMessageDelegate extends Observable
{
	private static final ShowMessageDelegate INSTANCE = new ShowMessageDelegate();

	private ShowMessageDelegate()
	{
	}

	public static void showMessageDialog(final Object message, final String title, final MessageType messageType)
	{
		showMessageDialog(new MessageWrapper(message, title, messageType));
	}

	public static void showMessageDialog(final MessageWrapper messageWrapper)
	{
		INSTANCE.setChanged();
		INSTANCE.notifyObservers(messageWrapper);
		if (INSTANCE.countObservers() == 0 && messageWrapper.getMessage() != null
			&& !messageWrapper.getMessage().toString().isEmpty())
		{
			// No GUI is attached (headless/batch): log the message at the level
			// matching its type so it still reaches the user and the log file.
			Logging.log(toLoggingLevel(messageWrapper.getMessageType()),
					messageWrapper.getTitle() + ": " + messageWrapper.getMessage());
		}
	}

	private static Level toLoggingLevel(final MessageType messageType)
	{
		if (messageType == MessageType.ERROR)
		{
			return Logging.ERROR;
		}
		if (messageType == MessageType.WARNING)
		{
			return Logging.WARNING;
		}
		return Logging.INFO;
	}

	/**
	 * @return Returns the instance.
	 */
	public static ShowMessageDelegate getInstance()
	{
		return INSTANCE;
	}

}
