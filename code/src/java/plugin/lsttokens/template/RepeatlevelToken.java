package plugin.lsttokens.template;

import java.util.StringTokenizer;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.util.Logging;

/**
 * Class deals with REPEATLEVEL Token
 */
public class RepeatlevelToken implements PCTemplateLstToken
{

	public String getTokenName()
	{
		return "REPEATLEVEL";
	}

	public boolean parse(PCTemplate template, String value)
	{
		//
		// x|y|z:level:<level assigned item>
		//
		final int endRepeat = value.indexOf(':');
		if (endRepeat <= 0)
		{
			Logging.errorPrint("Invalid " + getTokenName() + " (no colon) : "
				+ value);
			return false;
		}
		final int endLevel = value.indexOf(':', endRepeat + 1);
		if (endLevel <= 0)
		{
			Logging.errorPrint("Invalid " + getTokenName()
				+ " (only one colon) : " + value);
			return false;
		}
		String repeatSec = value.substring(0, endRepeat);
		final StringTokenizer repeatToken = new StringTokenizer(repeatSec, "|");
		if (repeatToken.countTokens() != 3)
		{
			Logging.errorPrint("Invalid " + getTokenName()
				+ " (repeat section " + repeatSec
				+ " does not have two pipes) : " + value);
			return false;
		}
		try
		{
			final int lvlIncrement = Integer.parseInt(repeatToken.nextToken());
			if (lvlIncrement <= 0)
			{
				Logging.errorPrint("Invalid level increment in "
					+ getTokenName() + ": must be > 0 : " + value);
				return false;
			}
			final int consecutive = Integer.parseInt(repeatToken.nextToken());
			if (consecutive < 0)
			{
				Logging.errorPrint("Invalid consecutive setting in "
					+ getTokenName() + ": must be >= 0 : " + value);
				return false;
			}
			final int maxLevel = Integer.parseInt(repeatToken.nextToken());
			if (maxLevel <= 0)
			{
				Logging.errorPrint("Invalid max level in " + getTokenName()
					+ ": must be > 0 : " + value);
				return false;
			}
			int iLevel =
					Integer.parseInt(value.substring(endRepeat + 1, endLevel));
			if (iLevel <= 0)
			{
				Logging.errorPrint("Invalid start level in " + getTokenName()
					+ ": must be > 0 : " + value);
				return false;
			}

			int count = consecutive;
			for (; iLevel <= maxLevel; iLevel += lvlIncrement)
			{
				if ((consecutive == 0) || (count != 0))
				{
					final StringTokenizer tok =
							new StringTokenizer(value.substring(endLevel + 1));
					final String type = tok.nextToken();

					template.addLevelAbility(iLevel, type, tok.nextToken());
				}
				if (consecutive != 0)
				{
					if (count == 0)
					{
						count = consecutive;
					}
					else
					{
						--count;
					}
				}
			}

			return true;
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Invalid " + getTokenName()
				+ ": number error encountered in :" + value);
			return false;
		}
	}
}