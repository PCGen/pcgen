/*
 * KitAbilities.java
 * Copyright 2005 (C) Andrew Wilson <nuance@sourceforge.net>
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
 * Created on 10 September 2005
 *
 * $Id$
 */
package pcgen.core.kit;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.AbilityInfo;
import pcgen.core.AbilityStore;
import pcgen.core.AbilityUtilities;
import pcgen.core.Categorisable;
import pcgen.core.Globals;
import pcgen.core.Kit;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;

/**
 * <code>KitAbiltiies</code>.
 *
 * @author   Andrew Wilson <nuance@sourceforge.net>
 * @version  $Revision$
 */
public final class KitAbilities extends BaseKit implements Serializable, Cloneable
{
	// Only change the UID when the serialized form of the class has also changed
	private static final long  serialVersionUID = 1;

	private AbilityStore abilityStore     = new AbilityStore();
	private boolean            free             = false;
	private String       stringRep;

	// These members store the state of an instance of this class.  They are
	// not cloned.
	private transient List<Ability> theAbilities = new ArrayList<Ability>();
	private transient List<AbilityInfo> abilitiesToAdd = null;

	/**
	 * Constructor that takes a | separated list of Abilities, with Interspersed
	 * CATEGORY=FOO entries.
	 *
	 * @param  abString         the string containing the Abilities and
	 *                          Categories
	 * @param  defaultCategory  the default Category
	 * @param  lockCategory     Whether the initial category a subsequent
	 *                          CATEGORY= tag will be acted on or if it is an
	 *                          error
	 */
	public KitAbilities(
		final String abString,
		String       defaultCategory,
		boolean      lockCategory)
	{
		abilityStore.addAbilityInfo(abString, defaultCategory, "|", lockCategory, false);

		final StringBuffer info = new StringBuffer();

		if ((choiceCount != 1) || (abilityStore.size() != 1))
		{
			info.append(choiceCount).append(" of ");
		}

		boolean firstDone = false;

		for (Iterator<Categorisable> it = this.getIterator(); it.hasNext();)
		{
			if (firstDone)
			{
				info.append("; ");
			}
			else
			{
				firstDone = true;
			}

			info.append(it.next().getKeyName());
		}

		if (free)
		{
			info.append(" (free)");
		}

		stringRep = info.toString();
	}

	/**
	 * Get an Iterator over the AbilityInfo Objects stored in this KitAbilities
	 * object
	 *
	 * @return  the AbilityInfo Iterator
	 */
	public Iterator<Categorisable> getIterator()
	{
		return abilityStore.getKeyIterator("ALL");
	}

	/**
	 * Set whether the kit is free.
	 *
	 * @param  argFree  true if the kit is free
	 */
	public void setFree(final boolean argFree)
	{
		free = argFree;
	}

	/**
	 * Returns a string representation of the object.
	 *
	 * @return  the string representation of the object
	 *
	 * @see     Object#toString()
	 */
	@Override
	public String toString()
	{
		return stringRep;
	}

	public boolean testApply(Kit aKit, PlayerCharacter aPC, List<String> warnings)
	{
		theAbilities = new ArrayList<Ability>();
		abilitiesToAdd = null;

		if (theAbilities == null)
		{
			return false;
		}

		final HashMap<String, AbilityInfo> nameMap    = new HashMap<String, AbilityInfo>();
		final HashMap<String, AbilityInfo> catMap     = new HashMap<String, AbilityInfo>();
		boolean useNameMap = true;
		AbilityCategory abilityCat = AbilityCategory.FEAT;

		for (Iterator<Categorisable> kAbInnerIt = getIterator(); kAbInnerIt.hasNext();)
		{
			final AbilityInfo info = (AbilityInfo) kAbInnerIt.next();

			if (!info.qualifies(aPC))
			{
				continue;
			}

			if (info.getAbility() != null)
			{
				AbilityInfo abI = nameMap.put(info.toString(), info);
				catMap.put(info.getCategory() + " " + info.toString(), info);
				abilityCat =
						SettingsHandler.getGame().getAbilityCategory(
							info.getCategory());

				if (abI != null)
				{
					useNameMap = false;
				}
			}
			else
			{
				warnings.add("ABILITY: Non-existant Ability \"" + info.getKeyName()
							 + "\"");
			}
		}

		int numberOfChoices = getChoiceCount();

		// Can't choose more entries than there are...
		if (numberOfChoices > nameMap.size())
		{
			numberOfChoices = nameMap.size();
		}

		/*
		 * this section needs to be rewritten once we determine how
		 * the new Ability Pools are going to work
		 */

		boolean tooManyAbilities = false;
		int     abilitiesChosen  = 0;
		// Don't allow choosing of more than allotted number of abilities
		if (!free
			&& (numberOfChoices > (aPC.getAvailableAbilityPool(abilityCat)
				.intValue() - abilitiesChosen)))
		{
			numberOfChoices =
					aPC.getAvailableAbilityPool(abilityCat).intValue()
						- abilitiesChosen;
			tooManyAbilities = true;
		}

		if (numberOfChoices == 0)
		{
			return false;
		}

		List<String> choices = useNameMap ?	new ArrayList<String>(nameMap.keySet()) : new ArrayList<String>(catMap.keySet());
		List<String> xs;

		if (numberOfChoices == nameMap.size())
		{
			xs = choices;
		}
		else
		{
			// Force user to make enough selections
			while (true)
			{
				xs = Globals.getChoiceFromList(
						"Choose abilities",
						choices,
						new ArrayList<String>(),
						numberOfChoices);

				if (xs.size() != 0)
				{
					break;
				}
			}
		}

		// Add to list of things to add to the character
		for (Iterator<String> e = xs.iterator(); e.hasNext();)
		{
			if (abilitiesToAdd == null)
			{
				abilitiesToAdd = new ArrayList<AbilityInfo>();
			}
			final String  choice = e.next();
			AbilityInfo ability = useNameMap ?
				nameMap.get(choice):
				catMap.get(choice);

			if (ability != null)
			{
				abilitiesToAdd.add(ability);
				++abilitiesChosen;
				final AbilityCategory abilityCategory =
						SettingsHandler.getGame().getAbilityCategory(
							ability.getCategory());
				if (free)
				{
					// Need to pay for it first
					if (free)
					{
						aPC.adjustAbilities(abilityCategory, new BigDecimal(1));
					}
				}
				Iterator<String> choicesIter = ability.getChoicesIterator();
				String abChoice = "";
				do
				{
					if (choicesIter.hasNext())
					{
						abChoice = choicesIter.next();
					}
					AbilityUtilities.modAbility(aPC, null,
						ability.getAbility(), abChoice, true, abilityCategory);
				}
				while (choicesIter.hasNext());
			}
			else
			{
				warnings.add("ABILITY: Non-existant Ability \"" + choice + "\"");
			}
		}

		if (tooManyAbilities)
		{
			warnings.add("ABILITY: Some Abilities were not granted -- not enough remaining feats");
			return false;
		}

		return true;
	}

	public void apply(PlayerCharacter aPC)
	{
		for ( AbilityInfo ability : abilitiesToAdd )
		{
			final AbilityCategory abilityCategory =
					SettingsHandler.getGame().getAbilityCategory(
						ability.getCategory());
			Iterator<String> choicesIter = ability.getChoicesIterator();
			String choice = "";
			do
			{
				if (choicesIter.hasNext())
				{
					choice = choicesIter.next();
				}
				AbilityUtilities.modAbility(aPC, null, ability.getAbility(),
					choice, true, abilityCategory);
			}
			while (choicesIter.hasNext());

			if (free)
			{
				aPC.adjustAbilities(abilityCategory, new BigDecimal(1));
			}
		}
	}

	@Override
	public KitAbilities clone()
	{
		return (KitAbilities) super.clone();
	}

	public String getObjectName()
	{
		return "Abilities";
	}
}
