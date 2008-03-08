/**
 * SpellsChoiceManager.java
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
 * Current Version: $Revision: 285 $
 * Last Editor:     $Author: nuance $
 * Last Edited:     $Date: 2006-03-17 15:19:49 +0000 (Fri, 17 Mar 2006) $
 *
 * Copyright 2006 Andrew Wilson <nuance@sourceforge.net>
 */
package pcgen.core.chooser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.SpellSupport;
import pcgen.core.character.CharacterSpell;
import pcgen.core.spell.Spell;
import pcgen.util.Logging;

/**
 * This is the chooser that deals with choosing a spell.
 */
public class SpellsChoiceManager extends
		AbstractBasicPObjectChoiceManager<Spell>
{
	/**
	 * Make a new spell chooser.
	 * 
	 * @param aPObject
	 * @param choiceString
	 * @param aPC
	 */
	public SpellsChoiceManager(PObject aPObject, String choiceString,
			PlayerCharacter aPC)
	{
		super(aPObject, choiceString, aPC);
		setTitle("Spell choice");
	}

	/**
	 * Parse the Choice string and build a list of available choices.
	 * 
	 * @param aPc
	 * @param availableList
	 * @param selectedList
	 */
	@Override
	public void getChoices(final PlayerCharacter aPc,
			final List<Spell> availableList, final List<Spell> selectedList)
	{
		Map<String, ?> spellMap = Globals.getSpellMap();
		Set<Spell> masterSet = new HashSet<Spell>();
		for (String item : getChoiceList())
		{
			List<Spell> masterList = null;
			StringTokenizer st = new StringTokenizer(item, ",");
			while (st.hasMoreTokens())
			{
				String token = st.nextToken();
				List<Spell> localList = new ArrayList<Spell>();
				if (token.startsWith("DOMAIN=") || token.startsWith("DOMAIN."))
				{
					appendSpells(new TypeKeyFilter("DOMAIN",
							token.substring(7), null), aPc, localList);
				}
				else if (token.startsWith("CLASS=")
						|| token.startsWith("CLASS."))
				{
					appendSpells(new TypeKeyFilter("CLASS", token.substring(6),
							null), aPc, localList);
				}
				else if (token.startsWith("DOMAINLIST="))
				{
					int bracketLoc = token.indexOf('[');
					String domainName;
					Restriction r = null;
					if (bracketLoc == -1)
					{
						domainName = token.substring(11);
					}
					else
					{
						if (!token.endsWith("]"))
						{
							Logging.errorPrint("Invalid entry in "
									+ "CHOOSE:SPELLS: " + token
									+ " did not have matching brackets");
						}
						domainName = token.substring(11, bracketLoc);
						r = getRestriction("DOMAIN:" + domainName, token
								.substring(bracketLoc + 1, token.length() - 1),
								aPc);
					}
					appendSpells(new TypeKeyFilter("DOMAIN", domainName, r),
							aPc, localList);
				}
				else if (token.startsWith("CLASSLIST="))
				{
					int bracketLoc = token.indexOf('[');
					String className;
					Restriction r = null;
					if (bracketLoc == -1)
					{
						className = token.substring(10);
					}
					else
					{
						if (!token.endsWith("]"))
						{
							Logging.errorPrint("Invalid entry in "
									+ "CHOOSE:SPELLS: " + token
									+ " did not have matching brackets");
						}
						className = token.substring(10, bracketLoc);
						r = getRestriction("CLASS:" + className, token
								.substring(bracketLoc + 1, token.length() - 1),
								aPc);
					}
					appendSpells(new TypeKeyFilter("CLASS", className, r), aPc,
							localList);
				}
				else if (token.startsWith("SCHOOL="))
				{
					appendSpells(new SchoolFilter(token.substring(7)), aPc,
							localList);
				}
				else if (token.startsWith("SUBSCHOOL="))
				{
					appendSpells(new SubSchoolFilter(token.substring(10)), aPc,
							localList);
				}
				else if (token.startsWith("DESCRIPTOR="))
				{
					appendSpells(new DescriptorFilter(token.substring(11)),
							aPc, localList);
				}
				else if (token.startsWith("SPELLBOOK="))
				{
					appendSpells(new SpellBookFilter(token.substring(10)), aPc,
							localList);
				}
				else if (token.startsWith("PROHIBITED="))
				{
					String prohibited = token.substring(11);
					boolean pro;
					if ("YES".equals(prohibited))
					{
						pro = true;
					}
					else if ("NO".equals(prohibited))
					{
						pro = false;
					}
					else
					{
						continue;
					}
					appendSpells(new ProhibitedFilter(pro), aPc, localList);
				}
				else if (token.startsWith("TYPE=") || token.startsWith("TYPE."))
				{
					appendSpells(new TypeFilter(token.substring(5)), aPc,
							localList);
				}
				else
				{
					for (String aKey : spellMap.keySet())
					{
						Object obj = spellMap.get(aKey);
						if (obj instanceof ArrayList)
						{
							localList.addAll((ArrayList) obj);
						}
						else if (obj instanceof Spell)
						{
							localList.add((Spell) obj);
						}
					}
				}
				if (masterList == null)
				{
					masterList = localList;
				}
				else
				{
					masterList.retainAll(localList);
				}
			}
			masterSet.addAll(masterList);
		}
		availableList.addAll(masterSet);

		List<String> associatedChoices = new ArrayList<String>();
		pobject.addAssociatedTo(associatedChoices);
		for (String choice : associatedChoices)
		{
			Spell spell = Globals.getSpellKeyed(choice);
			selectedList.add(spell);
		}
		setPreChooserChoices(selectedList.size());
	}

	private Restriction getRestriction(String item, String restrString,
			PlayerCharacter pc)
	{
		StringTokenizer restr = new StringTokenizer(restrString, ";");
		int levelMax = Integer.MAX_VALUE;
		int levelMin = Integer.MIN_VALUE;
		Boolean known = null;
		while (restr.hasMoreTokens())
		{
			String tok = restr.nextToken();
			if (tok.startsWith("LEVELMAX="))
			{
				levelMax = pc.getVariableValue(tok.substring(9), item)
						.intValue();
			}
			else if (tok.startsWith("LEVELMIN="))
			{
				levelMin = pc.getVariableValue(tok.substring(9), item)
						.intValue();
			}
			else if ("KNOWN=YES".equals(tok))
			{
				known = Boolean.TRUE;
			}
			else if ("KNOWN=NO".equals(tok))
			{
				known = Boolean.FALSE;
			}
			else
			{
				Logging.errorPrint("Unknown restriction: " + tok + " on item: "
						+ item + " in CHOOSE:SPELLS");
				continue;
			}
		}
		return new Restriction(levelMin, levelMax, known);
	}

	private void appendSpells(SpellFilter sf, PlayerCharacter pc,
			List<Spell> availableList)
	{
		Map<String, ?> spellMap = Globals.getSpellMap();
		for (String aKey : spellMap.keySet())
		{
			final Object obj = spellMap.get(aKey);

			if (obj instanceof ArrayList)
			{
				for (Spell aSpell : (ArrayList<Spell>) obj)
				{
					sf.conditionallyAdd(aSpell, pc, availableList);
				}
			}
			else if (obj instanceof Spell)
			{
				final Spell aSpell = (Spell) obj;
				sf.conditionallyAdd(aSpell, pc, availableList);
			}
		}
	}

	private interface SpellFilter
	{
		void conditionallyAdd(Spell spell, PlayerCharacter pc,
				List<Spell> availableList);
	}

	private class TypeKeyFilter implements SpellFilter
	{

		private final String listtype;
		private final String listname;
		private final Restriction res;
		private final String defaultbook;

		public TypeKeyFilter(String ltype, String listkey, Restriction r)
		{
			defaultbook = Globals.getDefaultSpellBook();
			listtype = ltype;
			listname = listkey;
			res = r;
		}

		public void conditionallyAdd(Spell spell, PlayerCharacter pc,
				List<Spell> availableList)
		{
			String listkey = listtype + "|" + listname;
			LEVEL: for (int level : spell.levelForKey(listkey, pc))
			{
				if (level < 0)
				{
					continue;
				}
				if (res != null)
				{
					if (level > res.maxLevel || level < res.minLevel)
					{
						continue;
					}
					if (res.knownRequired)
					{
						boolean found = false;
						for (PCClass cl : pc.getClassList())
						{
							SpellSupport ss = cl.getSpellSupport();
							List<CharacterSpell> csl = ss.getCharacterSpell(
									spell, defaultbook, -1);
							if (csl != null && !csl.isEmpty())
							{
								/*
								 * Going to assume here that the level doesn't
								 * need to be rechecked... ?? - thpr Feb 26, 08
								 */
								found = true;
							}
						}
						if (!found)
						{
							continue;
						}
					}
				}
				availableList.add(spell);
				break LEVEL;
			}
		}
	}

	private class TypeFilter implements SpellFilter
	{
		private final String type;

		public TypeFilter(String spelltype)
		{
			type = spelltype;
		}

		public void conditionallyAdd(Spell spell, PlayerCharacter pc,
				List<Spell> availableList)
		{
			if (spell.isType(type))
			{
				availableList.add(spell);
			}
		}
	}

	private class SchoolFilter implements SpellFilter
	{
		private final String school;

		public SchoolFilter(String sch)
		{
			school = sch;
		}

		public void conditionallyAdd(Spell spell, PlayerCharacter pc,
				List<Spell> availableList)
		{
			if (spell.getSchools().contains(school))
			{
				availableList.add(spell);
			}
		}
	}

	private class SubSchoolFilter implements SpellFilter
	{
		private final String subschool;

		public SubSchoolFilter(String subsch)
		{
			subschool = subsch;
		}

		public void conditionallyAdd(Spell spell, PlayerCharacter pc,
				List<Spell> availableList)
		{
			if (spell.getSubschools().contains(subschool))
			{
				availableList.add(spell);
			}
		}
	}

	private class DescriptorFilter implements SpellFilter
	{
		private final String descriptor;

		public DescriptorFilter(String des)
		{
			descriptor = des;
		}

		public void conditionallyAdd(Spell spell, PlayerCharacter pc,
				List<Spell> availableList)
		{
			if (spell.descriptorContains(descriptor))
			{
				availableList.add(spell);
			}
		}
	}

	private class SpellBookFilter implements SpellFilter
	{
		private final String spellbookname;

		public SpellBookFilter(String sb)
		{
			spellbookname = sb;
		}

		public void conditionallyAdd(Spell spell, PlayerCharacter pc,
				List<Spell> availableList)
		{
			if (pc.hasSpellInSpellbook(spell, spellbookname))
			{
				availableList.add(spell);
			}
		}
	}

	private class ProhibitedFilter implements SpellFilter
	{
		private final boolean prohibited;

		public ProhibitedFilter(boolean pro)
		{
			prohibited = pro;
		}

		public void conditionallyAdd(Spell spell, PlayerCharacter pc,
				List<Spell> availableList)
		{
			for (PCClass cl : pc.getClassList())
			{
				if (prohibited == cl.isProhibited(spell, pc))
				{
					availableList.add(spell);
				}
			}
		}
	}

	private class Restriction
	{
		public final int minLevel;
		public final int maxLevel;
		public final boolean knownRequired;
		public final boolean isKnown;

		public Restriction(int levelMin, int levelMax, Boolean known)
		{
			minLevel = levelMin;
			maxLevel = levelMax;
			if (known == null)
			{
				knownRequired = false;
				isKnown = false;
			}
			else
			{
				knownRequired = true;
				isKnown = known.booleanValue();
			}
		}

	}
}
