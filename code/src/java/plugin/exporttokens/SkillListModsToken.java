package plugin.exporttokens;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
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
	public String getTokenName()
	{
		return TOKENNAME;
	}

	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	public String getToken(String tokenSource, PlayerCharacter pc,
		ExportHandler eh)
	{
		StringBuffer returnString = new StringBuffer();
		boolean needcomma = false;

		for (Skill aSkill : pc.getSkillListInOutputOrder())
		{
			int modSkill = -1;

			if (aSkill.get(ObjectKey.KEY_STAT) != null)
			{
				modSkill =
						aSkill.modifier(pc).intValue()
							- pc.getStatList().getStatModFor(
								aSkill.getKeyStatAbb());
			}

			if ((aSkill.getTotalRank(pc).intValue() > 0) || (modSkill > 0))
			{
				//final
				int temp =
						aSkill.modifier(pc).intValue()
							+ aSkill.getTotalRank(pc).intValue();

				if (needcomma)
				{
					returnString.append(", ");
				}
				needcomma = true;

				
				returnString.append(aSkill.getOutputName()).append(temp >= 0 ? " +" : " ")
					.append(Integer.toString(temp));
			}
		}

		return returnString.toString();
	}

}
