/**
 * 
 */
package pcgen.core.chooser;

import pcgen.core.Ability;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.analysis.SkillRankControl;

public class SkillChooseController extends ChooseController<Ability>
{
	private final Skill skill;
	private final PlayerCharacter pc;

	public SkillChooseController(Skill sk, PlayerCharacter aPC,
			ChoiceManagerList<?> cont)
	{
		if (sk == null)
		{
			throw new IllegalArgumentException(
					"Ability cannot be null for AbilityChooseController");
		}
		skill = sk;
		pc = aPC;
	}

	@Override
	public int getPool()
	{
		return SkillRankControl.getTotalRank(pc, skill).intValue()
			- pc.getAssociationList(skill).size();
	}

	@Override
	public boolean isMultYes()
	{
		return true;
	}

	@Override
	public int getTotalChoices()
	{
		return Integer.MAX_VALUE;
	}
}