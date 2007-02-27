package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.util.Logging;

/**
 * Class deals with BONUSSKILLPOINTS Token
 */
public class BonusskillpointsToken implements PCTemplateLstToken
{

	public String getTokenName()
	{
		return "BONUSSKILLPOINTS";
	}

	// additional skill points per level
	public boolean parse(PCTemplate template, String value)
	{
		try
		{
			int skillCount = Integer.parseInt(value);
			if (skillCount <= 0) 
			{
				Logging.errorPrint(getTokenName()
					+ " must be an integer greater than zero");
				return false;
			}
			template.setBonusSkillsPerLevel(skillCount);
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
		return true;
	}
}
