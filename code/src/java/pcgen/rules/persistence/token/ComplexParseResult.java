/*
 * Copyright (c) 2009 Mark Jeffries <motorviper@users.sourceforge.net>
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
 */
package pcgen.rules.persistence.token;

import java.util.LinkedList;
import java.util.logging.Level;

import pcgen.util.Logging;

/**
 * Class that implements ParseResult for providing more complicated feedback.
 * See plugin.lsttokens.pcclass.ExchangeLevelToken.
 */
public class ComplexParseResult implements ParseResult
{
	private LinkedList<QueuedMessage> _queuedMessages =
			new LinkedList<QueuedMessage>();

	public ComplexParseResult()
	{
	}

	public ComplexParseResult(String error)
	{
		addErrorMessage(error);
	}

	public ComplexParseResult(ComplexParseResult toCopy)
	{
		addMessages(toCopy);
	}

	public void addErrorMessage(String msg)
	{
		addParseMessage(Logging.LST_ERROR, msg);
	}

    public void addWarningMessage(String msg)
    {
        addParseMessage(Logging.LST_WARNING, msg);
    }

    public void addInfoMessage(String msg)
    {
        addParseMessage(Logging.LST_INFO, msg);
    }

	protected void addParseMessage(Level lvl, String msg)
	{
		_queuedMessages.add(new QueuedMessage(lvl, msg));
	}

	public void addMessages(ComplexParseResult pr)
	{
		for (QueuedMessage msg : pr._queuedMessages)
		{
			_queuedMessages.add(msg);
		}
	}

	public void printMessages()
	{
		for (QueuedMessage msg : _queuedMessages)
		{
			Logging.log(msg.level, msg.message, msg.stackTrace);
		}
	}

	public void addMessagesToLog()
	{
		for (QueuedMessage msg : _queuedMessages)
		{
			Logging.addParseMessage(msg.level, msg.message, msg.stackTrace);
		}
	}

	public boolean passed()
	{
		for (QueuedMessage msg : _queuedMessages)
		{
			if (msg.level == Logging.LST_ERROR)
			{
				return false;
			}
		}
		return true;
	}
}