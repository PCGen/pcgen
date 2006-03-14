/*
 * KitSpells.java
 * Copyright 2001 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on September 23, 2002, 9:29 PM
 *
 * $Id: KitSpells.java,v 1.28 2006/02/07 15:40:53 karianna Exp $
 */
package pcgen.core.kit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import pcgen.core.Ability;
import pcgen.core.CharacterDomain;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.Kit;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.character.CharacterSpell;
import pcgen.core.spell.Spell;
import pcgen.gui.CharacterInfo;
import pcgen.gui.PCGen_Frame1;
import pcgen.util.Logging;

/**
 * <code>KitSpells</code>.
 *
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.28 $
 */
public final class KitSpells extends BaseKit implements Serializable, Cloneable
{
	// Only change the UID when the serialized form of the class has also changed
	private static final long serialVersionUID = 1;

	private HashMap spellMap = new HashMap();
	private String countFormula = "";

	private transient List theSpells = null;

	public KitSpells()
	{
		// Empty Constructor
	}

	public void setCountFormula(final String argCountFormula)
	{
		countFormula = argCountFormula;
	}

	public String getCountFormula()
	{
		return countFormula;
	}

	public List getClasses()
	{
		Set keySet = spellMap.keySet();
		ArrayList ret = new ArrayList(keySet.size());
		ret.addAll(keySet);
		return ret;
	}

	public List getSpellBooks(final String className)
	{
		return (List)spellMap.get(className);
	}

	public void addSpell(final String aClass, final String aSpellBook,
						 final String argSpell, final List metamagicFeats,
						 final String countStr)
	{
		// Check to see if we have any spellbooks for this class.
		String classKey = aClass;
		if (aClass == null)
		{
			classKey = "Default";
		}
		List spellBooks = (List)spellMap.get(classKey);
		KitSpellBook spellBook = null;
		if (spellBooks == null)
		{
			// We don't have a spell book list for this class
			// create an empty book and add it to the list
			spellBook = new KitSpellBook(classKey, aSpellBook);
			spellBooks = new ArrayList();
			spellBooks.add(spellBook);
			// Associate the list with this class.
			spellMap.put(classKey, spellBooks);
		}
		else
		{
			// We already have some spell books for this class.
			// Check to see if we have this one.
			for (Iterator i = spellBooks.iterator(); i.hasNext(); )
			{
				KitSpellBook ksb = (KitSpellBook)i.next();
				if (ksb.getName().equals(aSpellBook))
				{
					spellBook = ksb;
					break;
				}
			}
			if (spellBook == null)
			{
				spellBook = new KitSpellBook(classKey, aSpellBook);
				spellBooks.add(spellBook);
			}
		}
		spellBook.addSpell(argSpell, metamagicFeats, countStr);
	}

	public String toString()
	{
		final StringBuffer info = new StringBuffer();
		final java.util.Set classes = spellMap.keySet();
		for (Iterator i = classes.iterator(); i.hasNext(); )
		{
			final String className = (String)i.next();
			if (!"Default".equals(className))
			{
				info.append(className);
			}
			List spellBooks = (List)spellMap.get(className);
			for (Iterator j = spellBooks.iterator(); j.hasNext(); )
			{
				KitSpellBook ksb = (KitSpellBook)j.next();
				info.append(" " + ksb);
			}
		}

		return info.toString();
	}

	public boolean testApply(Kit aKit, PlayerCharacter aPC, List warnings)
	{
		theSpells = null;

		for (Iterator j = getClasses().iterator(); j.hasNext(); )
		{
			String className = (String)j.next();
			PCClass aClass = findDefaultSpellClass(className, aPC);
			if (aClass == null)
			{
				warnings.add("SPELLS: Character does not have " + className
							+ " spellcasting class.");
			   return false;
			}
			List spellBooks = getSpellBooks(className);
			for (Iterator k = spellBooks.iterator(); k.hasNext(); )
			{
				KitSpellBook sb = (KitSpellBook)k.next();
				List aSpellList = new ArrayList();
				final String bookName = sb.getName();
				if (!aClass.getMemorizeSpells() &&
					!bookName.equals(Globals.getDefaultSpellBook()))
				{
					warnings.add("SPELLS: " + aClass.getName()
								 + " can only add to " +
								 Globals.getDefaultSpellBook());
					return false;
				}
				for (Iterator w = sb.getSpells().iterator(); w.hasNext(); )
				{
					List spells = (List)w.next();
					for (Iterator q = spells.iterator(); q.hasNext(); )
					{
						KitSpellBookEntry sbe = (KitSpellBookEntry) (q.next());
						final String spellName = sbe.getName();

						if (spellName.startsWith("LEVEL="))
						{
							List allSpells = Globals.getSpellsIn(
								Integer.parseInt(spellName.substring(6)),
								aClass.getName(),
								"");
							for (Iterator s = allSpells.iterator();
								 s.hasNext(); )
							{
								aSpellList.add(new KitSpellBookEntry(aClass.
									getName(), sbe.getBookName(),
									(String) s.next(), null));
							}
						}
						else
						{
							final Spell aSpell = Globals.getSpellNamed(
								spellName);

							if (aSpell != null)
							{
								aSpellList.add(sbe);
							}
							else
							{
								warnings.add(
									"SPELLS: Non-existant spell \"" +
									spellName + "\"");
							}
						}
					}
				}

				final String choiceFormula = getCountFormula();
				int numberOfChoices;

				if (choiceFormula.length() == 0)
				{
					numberOfChoices = aSpellList.size();
				}
				else
				{
					numberOfChoices = aPC.getVariableValue(choiceFormula, "").intValue();
				}

				//
				// Can't choose more entries than there are...
				//
				if (numberOfChoices > aSpellList.size())
				{
					numberOfChoices = aSpellList.size();
				}

				if (numberOfChoices == 0)
				{
					continue;
				}

				List xs;

				if (numberOfChoices == aSpellList.size())
				{
					xs = aSpellList;
				}
				else
				{
					//
					// Force user to make enough selections
					//
					while (true)
					{
						xs = Globals.getChoiceFromList(
								"Choose " + className + " spell(s) for " + bookName,
								aSpellList,
								new ArrayList(),
								numberOfChoices);

						if (xs.size() != 0)
						{
							break;
						}
					}
				}

				//
				// Add to list of things to add to the character
				//
				for (Iterator e = xs.iterator(); e.hasNext();)
				{
					final KitSpellBookEntry obj = (KitSpellBookEntry)e.next();

					if (obj != null)
					{
						obj.setPCClass(aClass);
						if (theSpells == null)
						{
							theSpells = new ArrayList();
						}
						theSpells.add(obj);
					}
					else
					{
						warnings.add(
							"SPELLS: Non-existant spell \"" + obj.toString() + "\"");
					}
				}

			}

		}
		if (theSpells != null && theSpells.size() > 0)
		{
			return true;
		}
		return false;
	}

	public void apply(PlayerCharacter aPC)
	{
		for (Iterator i = theSpells.iterator(); i.hasNext(); )
		{
			KitSpellBookEntry sbe = (KitSpellBookEntry)i.next();
			updatePCSpells(aPC, sbe, aPC.getClassKeyed(sbe.getPCClass().getKeyName()));
		}
	}

	public Object clone()
	{
		KitSpells aClone = (KitSpells)super.clone();
		aClone.spellMap = spellMap;
		aClone.countFormula = countFormula;
		return aClone;
	}

	private PCClass findDefaultSpellClass(final String aClassName, PlayerCharacter aPC)
	{
		if ("Default".equals(aClassName))
		{
			List spellcastingClasses = aPC.getSpellClassList();
			for (Iterator i = spellcastingClasses.iterator(); i.hasNext(); )
			{
				Object obj = i.next();
				if (obj instanceof PCClass)
				{
					return (PCClass) obj;
				}
			}
			return null;
		}
		return aPC.getClassNamed(aClassName);
	}
	/**
	 * Add spells from this Kit to the PC
	 *
	 * @param  pc       The PC to add the spells to
	 * @param  aSpell   A Spell to add to the PC
	 * @param  pcClass  The class instance the spells are to be added to.
	 */
	private void updatePCSpells(
		final PlayerCharacter pc,
		final KitSpellBookEntry           aSpell,
		final PCClass         pcClass)
	{
		Spell spell = Globals.getSpellNamed(aSpell.getName());

		int spLevel = 99;

		// Check to see if we have any domains that have this spell.

		PObject owner = null;
		List cdl = pc.getCharacterDomainList();
		if (cdl != null)
		{
			for (Iterator i = cdl.iterator(); i.hasNext(); )
			{
				Domain domain = ((CharacterDomain)i.next()).getDomain();
				final String key = domain.getSpellKey();
				int newLevel = spell.getFirstLevelForKey(key, pc);
				if (newLevel > 0 && newLevel < spLevel)
				{
					spLevel = newLevel;
					owner = domain;
				}
			}
		}

		if (spLevel == 99)
		{
			spLevel = spell.getFirstLevelForKey(pcClass.getSpellKey(), pc);
			owner = pcClass;
		}

		if (spLevel < 0)
		{
			Logging.errorPrint(
				"SPELLS: " + pcClass.getName() + " cannot cast spell \"" +
				aSpell.getName() + "\"");

			return;
		}


		final CharacterSpell cs = new CharacterSpell(owner, spell);
		final List modifierList = aSpell.getModifiers();
		int adjustedLevel = spLevel;
		List metamagicFeatList = new ArrayList();
		for (Iterator i = modifierList.iterator(); i.hasNext(); )
		{
			final String featName = (String)i.next();
			Ability anAbility = pc.getFeatNamed(featName);
			if (anAbility != null)
			{
				adjustedLevel += anAbility.getAddSpellLevel();
				metamagicFeatList.add(anAbility);
			}
		}
		if (metamagicFeatList.size() <= 0)
		{
			metamagicFeatList = null;
		}
		pc.addSpellBook(aSpell.getBookName());

		for (int numTimes = 0; numTimes < aSpell.getCopies(); numTimes++)
		{
			final String aString = pc.addSpell(cs, metamagicFeatList, pcClass.getName(),
											   aSpell.getBookName(), adjustedLevel,
											   spLevel);
			if (aString.length() != 0)
			{
				Logging.errorPrint("Add spell failed:" + aString);
				return;
			}
		}
		final CharacterInfo pane = PCGen_Frame1.getCharacterPane();
		pane.setPaneForUpdate(pane.infoSpells());
		pane.refresh();
	}

	public String getObjectName()
	{
		return "Spells";
	}
}
