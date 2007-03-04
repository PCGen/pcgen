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

	public enum JepCountEnum
	{
		ABILITIES 
		{
			/**
			 * Count a character's abiltiies.
			 * 
			 * @param pc The character being counted.
			 * @param params The parameters determining which abilities get counted.
			 * @return A Double with the number of matching abilities.
			 * @throws ParseException If any invalid parameters are encountered.
			 */
			public Object count(PlayerCharacter pc, Object[] params) throws ParseException
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
		},
		CLASSES
		{
			/**
			 * Count a character's Classes.
			 * 
			 * @param pc The character being counted.
			 * @param params The parameters determining which classes get counted.
			 * @return A Double with the number of matching classes.
			 * @throws ParseException If any invalid parameters are encountered.
			 */

			public Object count(PlayerCharacter pc, Object[] params) throws ParseException
			{
				return Double.valueOf("0.0");
			}
		},
		DOMAINS
		{
			/**
			 * Count a character's Domains.
			 * 
			 * @param pc The character being counted.
			 * @param params The parameters determining which domains get counted.
			 * @return A Double with the number of matching domains.
			 * @throws ParseException If any invalid parameters are encountered.
			 */
			public Object count(PlayerCharacter pc, Object[] params) throws ParseException
			{
				return Double.valueOf("0.0");
			}
		},
		EQUIPMENT
		{
			/**
			 * Count a character's Equipment.
			 * 
			 * @param pc The character being counted.
			 * @param params The parameters determining which equipment gets counted.
			 * @return A Double with the number of matching pieces of equipment.
			 * @throws ParseException If any invalid parameters are encountered.
			 */
			public Object count(PlayerCharacter pc, Object[] params) throws ParseException
			{
				return Double.valueOf("0.0");
			}
		},
		FOLLOWERS
		{
			/**
			 * Count a character's Followers.
			 * 
			 * @param pc The character being counted.
			 * @param params The parameters determining which followers get counted.
			 * @return A Double with the number of matching followers.
			 * @throws ParseException If any invalid parameters are encountered.
			 */
			public Object count(PlayerCharacter pc, Object[] params) throws ParseException
			{
				return Double.valueOf("0.0");
			}
		},
		LANGUAGES
		{
			/**
			 * Count a character's Languages.
			 * 
			 * @param pc The character being counted.
			 * @param params The parameters determining which languages get counted.
			 * @return A Double with the number of matching languages.
			 * @throws ParseException If any invalid parameters are encountered.
			 */

			public Object count(PlayerCharacter pc, Object[] params) throws ParseException
			{
				return Double.valueOf("0.0");
			}
		},
		RACESUBTYPES
		{
			/**
			 * Count a character's Race Subtypes.
			 * 
			 * @param pc The character being counted.
			 * @param params The parameters determining which Race subtypes get counted.
			 * @return A Double with the number of matching Race subtypes.
			 * @throws ParseException If any invalid parameters are encountered.
			 */
			public Object count(PlayerCharacter pc, Object[] params) throws ParseException
			{
				return Double.valueOf("0.0");
			}
		},
		SKILLS
		{
			/**
			 * Count a character's Skills.
			 * 
			 * @param pc The character being counted.
			 * @param params The parameters determining which skills get counted.
			 * @return A Double with the number of matching skills.
			 * @throws ParseException If any invalid parameters are encountered.
			 */
			public Object count(PlayerCharacter pc, Object[] params) throws ParseException
			{
				return Double.valueOf("0.0");
			}
		},
		SPELLBOOKS
		{
			/**
			 * Count a character's Spell Books.
			 * 
			 * @param pc The character being counted.
			 * @param params The parameters determining which spell books get counted.
			 * @return A Double with the number of matching spell books.
			 * @throws ParseException If any invalid parameters are encountered.
			 */
			public Object count(PlayerCharacter pc, Object[] params) throws ParseException
			{
				return Double.valueOf("0.0");
			}
		},
		SPELLS
		{
			/**
			 * Count a character's Spells.
			 * 
			 * @param pc The character being counted.
			 * @param params The parameters determining which spells get counted.
			 * @return A Double with the number of matching spells.
			 * @throws ParseException If any invalid parameters are encountered.
			 */
			public Object count(PlayerCharacter pc, Object[] params) throws ParseException
			{
				return Double.valueOf("0.0");
			}
		},
		SPELLSINBOOK
		{
			/**
			 * Count the spells character's Book.
			 * 
			 * @param pc The character being counted.
			 * @param params The parameters determining which book get counted.
			 * @return A Double with the number of matching spells.
			 * @throws ParseException If any invalid parameters are encountered.
			 */
			public Object count(PlayerCharacter pc, Object[] params) throws ParseException
			{
				return Double.valueOf("0.0");
			}
		},
		SPELLSKNOWN
		{
			/**
			 * Count a character's known spells.
			 * 
			 * @param pc The character being counted.
			 * @param params The parameters determining which spells get counted.
			 * @return A Double with the number of matching spells.
			 * @throws ParseException If any invalid parameters are encountered.
			 */
			public Object count(PlayerCharacter pc, Object[] params) throws ParseException
			{
				return Double.valueOf("0.0");
			}
		},
		TEMPLATES
		{
			/**
			 * Count a character's Templates.
			 * 
			 * @param pc The character being counted.
			 * @param params The parameters determining which templates get counted.
			 * @return A Double with the number of matching templates.
			 * @throws ParseException If any invalid parameters are encountered.
			 */
			public Object count(PlayerCharacter pc, Object[] params) throws ParseException
			{
				return Double.valueOf("0.0");
			}
		};
		
		abstract public Object count(PlayerCharacter pc, Object[] params) throws ParseException;

	}

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
			JepCountEnum CountEnum = JepCountEnum.valueOf((String) params[0]);
			
			Double result = (Double) CountEnum.count(pc, params); 
			
			inStack.push(result);

		}
		else
		{
			throw new ParseException("Invalid parameter type");
		}
	}	
}
