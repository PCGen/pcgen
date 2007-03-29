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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import org.nfunk.jep.ParseException;

import pcgen.core.Ability;
import pcgen.core.PlayerCharacter;
import pcgen.core.VariableProcessor;
import pcgen.util.Logging;
import pcgen.util.PCGenCommand;
import pcgen.util.ParameterTree;
import pcgen.util.enumeration.Visibility;


/**
 * <code>CountCommand</code> deals with the count() JEP command.
 * The first parameter will be the type of object being counted 
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

	public enum JepAbilityCountEnum
	{
		CATEGORY,
		NATURE,
		TYPE,
		VISIBILITY
	}

	public enum JepCountEnum
	{
		ABILITIES
		{
			public HashMap<Ability.Nature, Set<Ability>> abdata;

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

				abdata = pc.getAbilitiesSet();

				ParameterTree pt = null;

				for (int i = 1; i < params.length; i++)
				{
					try
					{
						if (pt == null)
						{
							pt = ParameterTree.makeTree((String) params[i]);
						}
						else
						{
							ParameterTree npt = new ParameterTree(ParameterTree.andString);
							npt.setLeftTree(pt);
							pt  = npt;
							npt = ParameterTree.makeTree((String) params[i]);
							pt.setRightTree(npt);
						}
					}
					catch (ParseException pe)
					{
						Logging.errorPrint("Malformed parameter to count(ABILITY) " + (String) params[i], pe);
					}
				}

				Set<Ability> filtered = FilterAbilities(pt);
				
//				Logging.errorPrint("Number remaining: " + filtered.size());
				
				double accum = 0;
				
				for (Ability ab: filtered)
				{
					double ac = ab.getAssociatedCount();					
					accum += (ac <= 1.01) ? 1 : ab.getAssociatedCount();
				}
				return Double.valueOf(accum);
			}

			//@SuppressWarnings("unchecked") //Uses JEP, which doesn't use generics
			public Set<Ability> FilterAbilities (ParameterTree pt)
			{
				String c = pt.getContents();
//				System.err.println(c);
				
				if (c.equalsIgnoreCase(ParameterTree.orString) || c.equalsIgnoreCase(ParameterTree.andString))
				{
					Set<Ability> a = FilterAbilities(pt.getLeftTree());
					Set<Ability> b = FilterAbilities(pt.getRightTree());
					if (c.equalsIgnoreCase(ParameterTree.orString))
					{
						a.addAll(b);
					}
					else
					{
						a.retainAll(b);
					}
					return a;
				}
				
				String[] keyValue = c.split("=");

				JepAbilityCountEnum en = JepAbilityCountEnum.valueOf(keyValue[0]);
				Set<Ability> cs = null;
				Ability a;

				switch (en)
				{
					case CATEGORY:
						String cat = keyValue[1];
						cs = new HashSet<Ability>(abdata.get(Ability.Nature.ANY));
						
						Iterator It = cs.iterator();
						
						while (It.hasNext())
						{
							a = (Ability) It.next();
							if (!a.getCategory().equalsIgnoreCase(cat))
							{
								It.remove();
							}
						}
						break;
						
					case NATURE: ;
						Ability.Nature n;
						try
						{
							n  = Ability.Nature.valueOf(keyValue[1]);
						}
						catch (IllegalArgumentException ex)
						{
							Logging.errorPrint("Bad paramter to count(\"Ability\"), no such NATURE " + c);
							n = Ability.Nature.ANY;
						}
						cs = new HashSet<Ability>(abdata.get(n));
						break;

					case TYPE: ;
						String ty = keyValue[1];
						cs = new HashSet<Ability>(abdata.get(Ability.Nature.ANY));

						It = cs.iterator();

						while (It.hasNext())
						{
							a = (Ability) It.next();
							if (!a.isType(ty))
							{
								It.remove();
							}
						}
						break;

					case VISIBILITY :
						Visibility vi;
						cs = new HashSet<Ability>(abdata.get(Ability.Nature.ANY));

						try
						{
							vi  = Visibility.valueOf(keyValue[1]);

							It = cs.iterator();
							
							while(It.hasNext())
							{
								a = (Ability) It.next();
								if (!a.getVisibility().equals(vi))
								{
									It.remove();
								}
							}
						}
						catch (IllegalArgumentException ex)
						{
							Logging.errorPrint("Bad paramter to count(\"Ability\"), no such Visibility " + keyValue[1]);
						}


				}

//				System.err.println("--- start ---");
//				System.err.println(keyValue[0]);
//				System.err.println(keyValue[1]);
//
//				for (Ability ab : cs)
//				{
//					System.err.println(ab);
//				}
//				
//				System.err.println("--- end ---");
				return cs;
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
