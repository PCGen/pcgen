package plugin.exporttokens;

import java.util.Iterator;

import pcgen.core.Constants;
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
	public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
	{
		String retString = "";
		
		int i = 0;

		for (Iterator e = pc.getSkillListInOutputOrder().iterator(); e.hasNext();)
		{
			// final
			Skill aSkill = (Skill) e.next();
			int modSkill = -1;

			if (aSkill.getKeyStat().compareToIgnoreCase(Constants.s_NONE) != 0)
			{
				modSkill = aSkill.modifier(pc).intValue() - pc.getStatList().getStatModFor(aSkill.getKeyStat());
			}

			if ((aSkill.getTotalRank(pc).intValue() > 0) || (modSkill > 0))
			{
				//final
				int temp = aSkill.modifier(pc).intValue() + aSkill.getTotalRank(pc).intValue();

				if (i > 0)
				{
					retString += ", ";
				}

				retString += aSkill.getOutputName() + " +" + Integer.toString(temp);
				++i;
			}
		}
		
		return retString;
	}
	
}
