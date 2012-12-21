package plugin.exporttokens;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.analysis.OutputNameFormatting;
import pcgen.core.analysis.SkillModifier;
import pcgen.core.analysis.SkillRankControl;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;

/**
 * @author karianna
 * Class deals with SKILLLISTMODS Token
 */
public class SkillListModsToken extends Token
{

	/** Token name */
	public static final String TOKENNAME = "SKILLLISTMODS";

	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	@Override
	public String getTokenName()
	{
		return TOKENNAME;
	}

	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	@Override
	public String getToken(String tokenSource, PlayerCharacter pc,
		ExportHandler eh)
	{
		StringBuilder returnString = new StringBuilder();
		boolean needcomma = false;

		for (Skill aSkill : pc.getSkillListInOutputOrder())
		{
			int modSkill = -1;

			if (aSkill.get(ObjectKey.KEY_STAT) != null)
			{
				modSkill =
						SkillModifier.modifier(aSkill, pc).intValue()
							- pc.getStatModFor(aSkill.get(ObjectKey.KEY_STAT));
			}

			if ((SkillRankControl.getTotalRank(pc, aSkill).intValue() > 0) || (modSkill > 0))
			{
				//final
				int temp =
						SkillModifier.modifier(aSkill, pc).intValue()
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
