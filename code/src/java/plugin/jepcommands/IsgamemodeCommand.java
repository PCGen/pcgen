/*
 * IsgamemodeCommand.java
 * Copyright 2008 (C) James Dempsey
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
package plugin.jepcommands;

import java.util.Stack;

import org.nfunk.jep.ParseException;

import pcgen.core.SettingsHandler;
import pcgen.util.PCGenCommand;

/**
 * <p>
 * If class; extends PostfixMathCommand. This class accepts one agrument.
 * The argument is a game mode name. This is compared to the current game mode
 * and a 1 returned if it is, or a 0 if not.
 * </p>
 * <p>
 * So, given isgamemode("35e"), 1 is returned if the game mode is 35e and 0
 * otherwise.
 * </p>
 * 
 * 
 */
public class IsgamemodeCommand extends PCGenCommand
{

	/**
	 * <p>
	 * Initializes the command, notably to set the number of parameters to 1.
	 * </p>
	 */
	public IsgamemodeCommand()
	{
		super();
		numberOfParameters = 1;
	}

	/**
	 * Gets the name of the function handled by this class.
	 * @return The name of the function.
	 */
	@Override
	public String getFunctionName()
	{
		return "ISGAMEMODE";
	}

	/**
	 * <p>
	 * Evaluates the parameter, which must be a string.
	 * The argument is a game mode name. This is compared to the current game mode
	 * and a 1 returned if it is, or a 0 if not.
	 * </p>
	 * 
	 * @param stack Stack of incoming arguments.
	 * 
	 * @throws ParseException the parse exception
	 */
	@SuppressWarnings("unchecked") //Uses JEP, which doesn't use generics
	@Override
	public void run(final Stack stack) throws ParseException
	{
		// Check if stack is null
		if (null == stack)
		{
			throw new ParseException("Stack argument null");
		}

		final String gameModeKey;

		final Object param1 = stack.pop();

		if (param1 instanceof String)
		{
			gameModeKey = (String) param1;
		}
		else
		{
			throw new ParseException("Invalid parameter type for Parameter 1");
		}

		// push the result on the inStack
		stack.push(SettingsHandler.getGame().getName().equalsIgnoreCase(gameModeKey) ? 1 : 0);
	}
}
