package plugin.exporttokens;

import java.util.List;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SkillFilter;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.analysis.OutputNameFormatting;
import pcgen.core.analysis.SkillModifier;
import pcgen.core.analysis.SkillRankControl;
import pcgen.core.display.SkillDisplay;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;

/**
 * Class deals with SKILLLISTMODS Token
 */
public class SkillListModsToken extends Token
{

	/** Token name */
	public static final String TOKENNAME = "SKILLLISTMODS";

	@Override
	public String getTokenName()
	{
		return TOKENNAME;
	}

	@Override
	public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
	{
		StringBuilder returnString = new StringBuilder();
		boolean needcomma = false;

		final List<Skill> pcSkills = SkillDisplay.getSkillListInOutputOrder(pc);
		pcSkills.removeIf(sk -> !pc.includeSkill(sk, SkillFilter.Usable) || !sk.qualifies(pc, null));

		for (Skill aSkill : pcSkills)
		{
			int modSkill = -1;

			CDOMSingleRef<PCStat> statref = aSkill.get(ObjectKey.KEY_STAT);
			if (statref != null)
			{
				modSkill = SkillModifier.modifier(aSkill, pc) - pc.getStatModFor(statref.get());
			}

			if ((SkillRankControl.getTotalRank(pc, aSkill).intValue() > 0) || (modSkill > 0))
			{
				//final
				int temp = SkillModifier.modifier(aSkill, pc)
					+ SkillRankControl.getTotalRank(pc, aSkill).intValue();

				if (needcomma)
				{
					returnString.append(", ");
				}
				needcomma = true;

				returnString.append(OutputNameFormatting.getOutputName(aSkill)).append(temp >= 0 ? " +" : " ")
					.append(Integer.toString(temp));
			}
		}

		return returnString.toString();
	}

}
