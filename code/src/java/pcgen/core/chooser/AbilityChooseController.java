/*
 * Missing License Header, Copyright 2016 (C) Andrew Maitland <amaitland@users.sourceforge.net>
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
package pcgen.core.chooser;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.PlayerCharacter;

public class AbilityChooseController extends ChooseController<Ability>
{
	private final Ability ability;
	private final AbilityCategory ac;
	private final PlayerCharacter pc;
	ChoiceManagerList<?> ccm;

	public AbilityChooseController(Ability a, AbilityCategory cat, PlayerCharacter aPC, ChoiceManagerList<?> cont)
	{
		Objects.requireNonNull(a, "Ability cannot be null for AbilityChooseController");
		ability = a;
		ac = cat;
		pc = aPC;
		ccm = cont;
	}

	@Override
	public int getPool()
	{
		if (isMultYes())
		{
			int availPool = pc.getAvailableAbilityPool(ac).intValue();
			return ((availPool == 0) && (getCost() == 0)) ? 1 : availPool;
		}
		return 1;
	}

	@Override
	public boolean isMultYes()
	{
		return ability.getSafe(ObjectKey.MULTIPLE_ALLOWED);
	}

	@Override
	public boolean isStackYes()
	{
		return ability.getSafe(ObjectKey.STACKS);
	}

	@Override
	public double getCost()
	{
		return ability.getSafe(ObjectKey.SELECTION_COST).doubleValue();
	}

	@Override
	public int getTotalChoices()
	{
		return isMultYes() ? Integer.MAX_VALUE : 1;
	}

	@Override
	public void adjustPool(List<? extends Ability> selected)
	{
		if (AbilityCategory.FEAT.equals(ac))
		{
			double cost = getCost();
			if (cost > 0)
			{
				int preChooserChoices = ccm.getPreChooserChoices();
				int choicesPerUnitCost = ccm.getChoicesPerUnitCost();
				int basePriorCost = ((preChooserChoices + (choicesPerUnitCost - 1)) / choicesPerUnitCost);
				int baseTotalCost = ((selected.size() + (choicesPerUnitCost - 1)) / choicesPerUnitCost);
				pc.adjustAbilities(AbilityCategory.FEAT, BigDecimal.valueOf(cost * (basePriorCost - baseTotalCost)));
			}
		}
	}
}
