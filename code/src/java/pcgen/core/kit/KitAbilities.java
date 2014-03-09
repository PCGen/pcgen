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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.helper.CNAbilitySelection;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.AbilityUtilities;
import pcgen.core.Globals;
import pcgen.core.Kit;
import pcgen.core.PlayerCharacter;

/**
 * <code>KitAbiltiies</code>.
 *
 * @author   Andrew Wilson <nuance@sourceforge.net>
 * @version  $Revision$
 */
public final class KitAbilities extends BaseKit
{
	private Boolean free = null;
	private Integer choiceCount;
	private List<CDOMReference<Ability>> abilities =
			new ArrayList<CDOMReference<Ability>>();

	// These members store the state of an instance of this class.  They are
	// not cloned.
	private transient List<CNAbilitySelection> abilitiesToAdd = null;
	private AbilityCategory category;

	/**
	 * Set whether the kit is free.
	 *
	 * @param  argFree  true if the kit is free
	 */
	public void setFree(Boolean argFree)
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
		StringBuilder sb = new StringBuilder();

		if ((choiceCount != null) || (abilities.size() != 1))
		{
			sb.append(getSafeCount()).append(" of ");
		}

		boolean firstDone = false;

		for (CDOMReference<Ability> ref : abilities)
		{
			if (firstDone)
			{
				sb.append("; ");
			}
			firstDone = true;

			String choice = ref.getChoice();
			for (Ability a : ref.getContainedObjects())
			{
				if (a != null)
				{
					sb.append(a.getKeyName());
					if (choice != null)
					{
						sb.append(" (");
						sb.append(choice);
						sb.append(')');
					}
				}
			}
		}

		if (isFree())
		{
			sb.append(" (free)");
		}

		return sb.toString();
	}

	@Override
	public boolean testApply(Kit aKit, PlayerCharacter aPC,
		List<String> warnings)
	{
		abilitiesToAdd = new ArrayList<CNAbilitySelection>();
		double minCost = Double.MAX_VALUE;
		List<AbilitySelection> available = new ArrayList<AbilitySelection>();
		for (CDOMReference<Ability> ref : abilities)
		{
			String choice = ref.getChoice();
			for (Ability a : ref.getContainedObjects())
			{
				if (a == null)
				{
					warnings.add("ABILITY: " + ref + " could not be found.");
					minCost = 0;
					continue;
				}
				
				if (a.getCost() < minCost)
				{
					minCost = a.getCost();
				}
				if (choice == null)
				{
					available.add(new AbilitySelection(a, ""));
				}
				else
				{
					available.add(new AbilitySelection(a, choice));
				}
			}
		}

		int numberOfChoices = getSafeCount();
		// Can't choose more entries than there are...
		// TODO this fails if SELECT != 1
		if (numberOfChoices > available.size())
		{
			numberOfChoices = available.size();
		}

		/*
		 * this section needs to be rewritten once we determine how
		 * the new Ability Pools are going to work
		 */

		boolean tooManyAbilities = false;
		// Don't allow choosing of more than allotted number of abilities
		int maxChoices =
				minCost > 0.0d ? aPC.getAvailableAbilityPool(category)
					.divide(new BigDecimal(minCost)).intValue()
					: numberOfChoices;
		if (!isFree() && numberOfChoices > maxChoices)
		{
			numberOfChoices = maxChoices;
			tooManyAbilities = true;
		}

		if (!isFree() && numberOfChoices == 0)
		{
			warnings.add("ABILITY: Not enough " + category.getPluralName()
				+ " available to take \"" + this + "\"");
			return false;
		}

		List<AbilitySelection> selected;

		if (numberOfChoices == available.size())
		{
			selected = available;
		}
		else
		{
			selected = new ArrayList<AbilitySelection>();
			// Force user to make enough selections
			while (true)
			{
				selected =
						Globals.getChoiceFromList("Choose abilities",
							available, new ArrayList<AbilitySelection>(),
							numberOfChoices, aPC);

				if (selected.size() != 0)
				{
					break;
				}
			}
		}

		// Add to list of things to add to the character
		for (AbilitySelection as : selected)
		{
			Ability ability = as.ability;
			if (isFree())
			{
				// Need to pay for it first
				if (free)
				{
					aPC.adjustAbilities(category, new BigDecimal(1));
				}
			}
			if (ability.getCost() > aPC.getAvailableAbilityPool(category).doubleValue())
			{
				tooManyAbilities = true;
			}
			else
			{
				CNAbility cna = new CNAbility(category, ability, Nature.NORMAL);
				CNAbilitySelection cnas = new CNAbilitySelection(cna, as.selection);
				abilitiesToAdd.add(cnas);
				AbilityUtilities.modAbility(aPC, cnas);
			}
		}

		if (tooManyAbilities)
		{
			warnings
				.add("ABILITY: Some Abilities were not granted -- not enough remaining feats");
			return false;
		}

		return true;
	}

	@Override
	public void apply(PlayerCharacter aPC)
	{
		for (CNAbilitySelection cnas : abilitiesToAdd)
		{
			AbilityUtilities.modAbility(aPC, cnas);
			
			if (isFree())
			{
				aPC.adjustAbilities(category, new BigDecimal(1));
			}
		}
	}

	/**
	 * Returns if the skill will be purchased for free.
	 * @return <code>true</code> if the skill will be free
	 */
	public boolean isFree()
	{
		return free != null && free;
	}

	@Override
	public String getObjectName()
	{
		return "Abilities";
	}

	public Boolean getFree()
	{
		return free;
	}

	public void setCount(Integer quan)
	{
		choiceCount = quan;
	}

	public Integer getCount()
	{
		return choiceCount;
	}

	public int getSafeCount()
	{
		return choiceCount == null ? 1 : choiceCount;
	}

	public void addAbility(CDOMReference<Ability> ref)
	{
		abilities.add(ref);
	}

	public Collection<CDOMReference<Ability>> getAbilityKeys()
	{
		Set<CDOMReference<Ability>> wc = new TreeSet<CDOMReference<Ability>>(
				ReferenceUtilities.REFERENCE_SORTER);
		wc.addAll(abilities);
		return wc;
	}

	private static class AbilitySelection implements Comparable<AbilitySelection>
	{
		public final Ability ability;
		public final String selection;

		public AbilitySelection(Ability a, String sel)
		{
			ability = a;
			selection = sel;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(ability.getDisplayName());
			if (selection != null)
			{
				sb.append(" (").append(selection).append(')');
			}
			return sb.toString();
		}

        @Override
		public int compareTo(AbilitySelection o)
		{
			int base = ability.compareTo(o.ability);
			if (base != 0)
			{
				return base;
			}
			if (selection == null)
			{
				return o.selection == null ? 0 : -1;
			}
			return o.selection == null ? 1 : selection
					.compareToIgnoreCase(o.selection);
		}
	}

	public void setCategory(AbilityCategory ac)
	{
		category = ac;
	}

	public AbilityCategory getCategory()
	{
		return category;
	}
}
