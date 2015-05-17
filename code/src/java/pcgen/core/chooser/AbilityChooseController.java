/**
 * 
 */
package pcgen.core.chooser;

import java.math.BigDecimal;
import java.util.List;

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

	public AbilityChooseController(Ability a, AbilityCategory cat,
			PlayerCharacter aPC, ChoiceManagerList<?> cont)
	{
		if (a == null)
		{
			throw new IllegalArgumentException(
					"Ability cannot be null for AbilityChooseController");
		}
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
			return availPool == 0 && getCost() == 0 ? 1 : availPool;
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
				pc.adjustAbilities(AbilityCategory.FEAT, new BigDecimal(cost * (basePriorCost - baseTotalCost)));
			}
		}
	}
}