/*
 * Domain.java
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
 *
 * Created on April 21, 2001, 2:15 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.core;

import pcgen.core.character.CharacterSpell;
import pcgen.core.chooser.ChooserUtilities;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.spell.Spell;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <code>Domain</code>.
 *
 * @author   Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision$
 */
public final class Domain extends PObject
{
	private AbilityStore abilityStore = new AbilityStore();
	private boolean isLocked;

	/**
	 * Add an Ability or list of Abilities to the Abilities that the domain grants.
	 * The Ability may be a single Ability, or a list of Abilitiess separated by "|"
	 * or ",".  The AbilityList may be interspersed with "CATEGORY=FOO" entries.
	 * This will change the category of all Abilities entered by this call until
	 * a further Category entry is found. In addition, if .CLEAR is received,
	 * the existing ability store will be cleared.  This string must begin with a
	 * "CATEGORY=" statement.
	 *
	 * @param  abilities
	 */
	public void addAbility(final String abilities)
	{
		abilityStore.addAbilityInfo(abilities, "", "|,", false, false);
	}

	/**
	 * @see #addAbility(String)
	 *
	 * this is a variation of addAbilityToList, it ignores "CATEGORY=" entries
	 * All abilities added by this method are assumed to be category FEAT.
	 *
	 * @param  feats
	 */
	public void addFeat(final String feats)
	{
		abilityStore.addAbilityInfo(feats, "FEAT", "|,", true, false);
	}

	/**
	 * Sets the locked flag on a PC
	 * @param aBool
	 * @param pc
	 */
	public void setIsLocked(final boolean aBool, final PlayerCharacter pc)
	{
		if (isLocked == aBool)
		{
			return;
		}

		isLocked = aBool;

		if (aBool)
		{
			final PlayerCharacter aPC    = pc;
			final CharacterDomain aCD    = aPC.getCharacterDomainForDomain(keyName);
			PCClass               aClass = null;

			if (aCD != null)
			{
				if (aCD.isFromPCClass())
				{
					aClass = aPC.getClassKeyed(aCD.getObjectName());

					if (aClass != null)
					{
						int maxLevel;

						for (maxLevel = 0; maxLevel < 10; maxLevel++)
						{
							if (aClass.getCastForLevel(maxLevel, aPC) == 0)
							{
								break;
							}
						}

						if (maxLevel > 0)
						{
							addSpellsToClassForLevels(aClass, 0, maxLevel - 1);
						}

						if ((maxLevel > 1) && (aClass.getKnownSpellsFromSpecialty() == 0))
						{
							final List<Spell> aList = Globals.getSpellsIn(-1, "", keyName);

							for ( Spell gcs : aList )
							{
								if (gcs.levelForKey("DOMAIN", keyName, aPC) < maxLevel)
								{
									if (aClass.getKnownSpellsFromSpecialty() == 0)
									{
										aClass.setKnownSpellsFromSpecialty(1);
										break;
									}
								}
							}
						}
					}
				}
			}

			final List<PCSpell> spellList = getSpellList();

			if ((aClass != null) && (spellList != null) && !spellList.isEmpty())
			{
				for ( PCSpell pcSpell : spellList )
				{
					final Spell aSpell = Globals.getSpellKeyed(pcSpell.getKeyName());

					if (aSpell == null)
					{
						return;
					}

					final int times = Integer.parseInt(pcSpell.getTimesPerDay());

					final String book = pcSpell.getSpellbook();

					if (PrereqHandler.passesAll(pcSpell.getPreReqList(), aPC, this))
					{
						final List<CharacterSpell> aList = aClass.getSpellSupport()
							.getCharacterSpell(aSpell, book, -1);

						if (aList.isEmpty())
						{
							final CharacterSpell cs = new CharacterSpell(this, aSpell);
							cs.addInfo(1, times, book);
							aClass.getSpellSupport().addCharacterSpell(cs);
						}
					}
				}
			}

			// sage_sam stop here
			String choiceString = getChoiceString();

			if (
				(choiceString.length() > 0) &&
				!aPC.isImporting() &&
				!choiceString.startsWith("FEAT|"))
			{
				ChooserUtilities.modChoices(
					this,
					new ArrayList(),
					new ArrayList(),
					true,
					aPC,
					true);
			}

			if (!aPC.isImporting())
			{
				globalChecks(aPC);
				activateBonuses(aPC);
			}
		}
	}

	public String getSpellKey()
	{
		return "DOMAIN|" + keyName;
	}

	public Domain clone()
	{
		Domain aObj = null;

		try
		{
			aObj                = (Domain) super.clone();
			aObj.abilityStore = new AbilityStore();
			Iterator<Categorisable> it = abilityStore.getKeyIterator("ALL");
			while (it.hasNext())
			{
				Categorisable catObj = it.next();
				aObj.addFeat("CATEGORY="+catObj.getCategory()+"|"+catObj.getKeyName());
			}
			//aObj.abilityStore   = (AbilityStore) abilityStore.clone();
			aObj.isLocked       = false;

			// aObj.isLocked = isLocked;
		}
		catch (CloneNotSupportedException exc)
		{
			ShowMessageDelegate.showMessageDialog(
				exc.getMessage(),
				Constants.s_APPNAME,
				MessageType.ERROR);
		}

		return aObj;
	}

	/**
	 * (non-Javadoc)
	 * Only compares the name.
	 * @param obj
	 * @return TRUE if equals, else FALSE
	 * @see Object#equals
	 */
	public boolean equals(final Object obj)
	{
		if (obj != null)
		{
			if (obj.getClass() == this.getClass())
			{
				return ((Domain) obj).getKeyName().equals(this.getKeyName());
			}
		}

		return false;
	}

	/**
	 * Only uses the name for hashCode.
	 *
	 * @return a hashcode for this Domain object
	 */
	public int hashCode()
	{
		final int result;
		result = ((getKeyName() != null) ? getKeyName().hashCode() : 0);

		return result;
	}

	/**
	 * Returns TRUE if PC qualifies for a domain
	 * @param pc
	 * @return TRUE if PC qualifies for a domain
	 */
	public boolean qualifiesForDomain(final PlayerCharacter pc)
	{
		return PrereqHandler.passesAll(this.getPreReqList(), pc, this);
	}

	/**
	 * Get an iterator for the AbilityInfo objects that hold represent
	 * the Feats granted by this domain
	 *
	 * @return  An Iterator over a group of AbilityInfo objects.
	 */
	public Iterator<Categorisable> getFeatIterator()
	{
		return abilityStore.getNameIterator("FEAT");
	}

	/**
	 * Gets the number of feats
	 * @return number of feat
	 */
	public int getNumberOfFeats()
	{
		return abilityStore.getUnmodifiableList("FEAT").size();
	}

	public Iterator<Categorisable> getAbilityIterator(final String aCategory)
	{
		return abilityStore.getNameIterator(aCategory);
	}
	
	void addSpellsToClassForLevels(
		final PCClass aClass,
		final int     minLevel,
		final int     maxLevel)
	{
		if (aClass == null)
		{
			return;
		}

		for (int aLevel = minLevel; aLevel <= maxLevel; aLevel++)
		{
			final List<Spell> domainSpells = Globals.getSpellsIn(aLevel, "", keyName);

			for ( Spell spell : domainSpells )
			{
				final List<CharacterSpell>  slist  = aClass.getSpellSupport()
					.getCharacterSpell(spell, Globals.getDefaultSpellBook(), aLevel);
				boolean     flag   = true;

				for ( CharacterSpell cs1 : slist )
				{
					flag = !(cs1.getOwner().equals(this));

					if (!flag)
					{
						break;
					}
				}

				if (flag)
				{
					final CharacterSpell cs = new CharacterSpell(this, spell);
					cs.addInfo(aLevel, 1, Globals.getDefaultSpellBook());
					aClass.getSpellSupport().addCharacterSpell(cs);
				}
			}
		}
	}

	/**
	 * This method gets the text used in outputting source files (.pcc files)
	 * @return String containing properly formatted pcc text for this domain.
	 */
	public String getPCCText()
	{
		final StringBuffer txt = new StringBuffer(200);
		txt.append(super.getPCCText(true));

		//
		// For custom domains, we need to save the domain list as it won't be saved with the spell (unless the spell is also custom)
		// in which case it will be saved in the spell's DOMAIN tag
		//
		boolean bFirst = true;
		final SpellSupport ss = getSpellSupport();
		for (Iterator<?> e = Globals.getSpellMap().values().iterator(); e.hasNext();)
		{
			final Object obj = e.next();

			if (obj instanceof Spell)
			{
				String spellName = obj.toString();
				if (ss.containsInfoFor("DOMAIN", spellName))
				{
					if (bFirst)
					{
						txt.append('\t').append("SPELLLEVEL:DOMAIN");
						bFirst = false;
					}
					final int spellLevel = ss.getInfo("DOMAIN", spellName).level;
					txt.append('|').append(getKeyName()).append('=').append(spellLevel).append('|').append(obj.toString());
				}
			}
		}

		// Granted feats
		StringBuffer featString = new StringBuffer();
		for (Iterator<Categorisable> iter = getFeatIterator(); iter.hasNext();)
		{
			Ability grantedFeat = (Ability)iter.next();
			if (featString.length() > 0)
			{
				featString.append("|");
			}
			featString.append(grantedFeat.getKeyName());
		}
		if (featString.length() > 0)
		{
			txt.append('\t').append("FEAT:").append(featString);
		}
		return txt.toString();
	}

}
