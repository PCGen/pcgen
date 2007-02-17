/*
 * CountCommand.java
 * Copyright 2006 (C) James Dempsey
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
 *
 * Created on 23/11/2006
 *
 * $Id: $
 */

package plugin.jepcommands;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.nfunk.jep.ParseException;

import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.VariableProcessor;
import pcgen.util.Logging;
import pcgen.util.PCGenCommand;
import pcgen.util.enumeration.View;

/**
 * <code>CountCommand</code> deals with the count() JEP command.
 * The first parameter will be the type of objetc being counted 
 * and further parameters will specify the criteria. 
 *
 * Last Editor: $Author:  $
 * Last Edited: $Date:  $
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: $
 */
public class CountCommand extends PCGenCommand
{

	/**
	 * Constructor
	 */
	public CountCommand()
	{
		numberOfParameters = -1;
	}

	/**
	 * @see pcgen.util.PCGenCommand#getFunctionName()
	 */
	@Override
	public String getFunctionName()
	{
		return "COUNT";
	}

	/**
	 * Runs count on the inStack. The parameter is popped
	 * off the <code>inStack</code>, and the variable's value is
	 * pushed back to the top of <code>inStack</code>.
	 * @param inStack
	 * @throws ParseException
	 */
	@Override
	@SuppressWarnings("unchecked") //Uses JEP, which doesn't use generics
	public void run(Stack inStack) throws ParseException
	{
		// check the stack
		checkStack(inStack);

		// get the parameters from the stack
		//
		// have to do this in reverse order...this is a stack afterall
		//
		Object params[] = new Object[curNumberOfParameters];
		for (int i = curNumberOfParameters - 1; i >= 0; i--)
		{
			params[i] = inStack.pop();
		}

		if ((params[0] instanceof String))
		{
			// Grab the character under scrutiny
			PlayerCharacter pc = null;
			if (parent instanceof VariableProcessor)
			{
				pc = ((VariableProcessor) parent).getPc();
			}
			else if (parent instanceof PlayerCharacter)
			{
				pc = (PlayerCharacter) parent;
			}
			if (pc == null)
			{
				throw new ParseException("Invalid parent (no PC): "
					+ parent.getClass().getName());
			}

			// Count the requested object type.
			Object result = null;
			final String countType = (String) params[0];
			if ("ABILITIES".equals(countType))
			{
				result = countAbilities(pc, params);
			}

			inStack.push(result);
		}
		else
		{
			throw new ParseException("Invalid parameter type");
		}
	}

	/**
	 * Count a character's abiltiies.
	 * 
	 * @param pc The character being counted.
	 * @param params The parameters determining which abilities get counted.
	 * @return A Double with the number of matching abilities.
	 * @throws ParseException If any invalid parameters are encountered.
	 */
	private Object countAbilities(PlayerCharacter pc, Object[] params)
		throws ParseException
	{
		if (params.length < 2)
		{
			throw new ParseException(
				"Count of abilities had too few parameters.");
		}

		String visibility = "VISIBLE";
		String category = null;
		String nature = "NORMAL";

		// Parse the parameters passed in
		for (int i = 1; i < params.length; i++)
		{
			if (!(params[i] instanceof String))
			{
				throw new ParseException(
					"Invalid parameter type for parameter #" + (i + 1) + " - "
						+ String.valueOf(params[i]));
			}

			String[] keyValue = ((String) params[i]).split("=");
			if ("CATEGORY".equalsIgnoreCase(keyValue[0]))
			{
				category = keyValue[1];
			}
			else if ("VISIBILITY".equalsIgnoreCase(keyValue[0]))
			{
				visibility = keyValue[1];
			}
			else if ("NATURE".equalsIgnoreCase(keyValue[0]))
			{
				nature = keyValue[1];
			}
			else
			{
				throw new ParseException(
					"Invalid parameter key for parameter #" + (i + 1) + " - "
						+ String.valueOf(params[i]));
			}
		}

		// Fetch the requested list of abilities
		final AbilityCategory aCategory =
				SettingsHandler.getGame().getAbilityCategory(category);
		if (aCategory == null)
		{
			throw new ParseException("Invalid category specified "
				+ String.valueOf(category));
		}
		final List<Ability> abilities = new ArrayList<Ability>();
		if ("ALL".equals(nature))
		{
			abilities.addAll(pc.getRealAbilityList(aCategory));
			abilities.addAll(pc.getAutomaticAbilityList(aCategory));
			abilities.addAll(pc.getVirtualAbilityList(aCategory));
		}
		else if ("VIRTUAL".equals(nature))
		{
			abilities.addAll(pc.getVirtualAbilityList(aCategory));
		}
		else if ("AUTO".equals(nature))
		{
			abilities.addAll(pc.getAutomaticAbilityList(aCategory));
		}
		else
		{
			abilities.addAll(pc.getRealAbilityList(aCategory));
		}

		// Count those abilities that match the visibility level
		View view = View.getViewFromName(visibility);
		int count = 0;
		for (Ability ability : abilities)
		{
			if (ability.getVisibility().isVisibileTo(view, true))
			{
				count++;
			}
		}

		return new Double(count);
	}
}
