package pcgen.cdom.helper;

import pcgen.cdom.base.ChoiceActor;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.analysis.SkillRankControl;

public class ClassSkillChoiceActor implements ChoiceActor<Skill>
{

	private final PCClass source;
	private final Integer applyRank;

	public ClassSkillChoiceActor(PCClass obj, Integer autoRank)
	{
		applyRank = autoRank;
		source = obj;
	}

	public void applyChoice(Skill choice, PlayerCharacter pc)
	{
		Skill pcSkill = pc.addSkill(choice);
		pc.addAssoc(source, AssociationListKey.CSKILL, pcSkill);
		if (applyRank != null)
		{
			SkillRankControl.modRanks(applyRank, source, false, pc, pcSkill);
		}
	}

}
