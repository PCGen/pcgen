package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.util.Logging;

/**
 * Class deals with LEVELSPERFEAT Token
 */
public class LevelsperfeatToken implements PCTemplateLstToken
{

	public String getTokenName()
	{
		return "LEVELSPERFEAT";
	}

	// how many levels per feat.
	public boolean parse(PCTemplate template, String value)
	{
		try
		{
			final int newLevels = Integer.parseInt(value);

			if (newLevels >= 0)
			{
				template.setLevelsPerFeat(newLevels);
			}
			else
			{
				Logging.errorPrint("Levels Per Feat must be "
					+ "greater than or equal to zero: " + value);
				return false;
			}
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
		return true;
	}
}
