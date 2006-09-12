package plugin.lsttokens.template;

import java.util.StringTokenizer;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;


/**
 * Class deals with LEVEL Token
 */
public class LevelToken implements PCTemplateLstToken {

	public String getTokenName() {
		return "LEVEL";
	}

	public boolean parse(PCTemplate template, String value) 
	{
		if (".CLEAR".equals(value))
		{
			template.clearLevelAbilities();
			return true;
		}
		
		final StringTokenizer tok = new StringTokenizer(value, ":");
		final String levelStr = tok.nextToken();
		final int level;
		try
		{
			level = Integer.parseInt(levelStr);
		}
		catch (NumberFormatException ex)
		{
			// TODO - Add error message.
			return false;
		}
		final String typeStr = tok.nextToken();

		template.addLevelAbility(level, typeStr, tok.nextToken());
		return true;
	}
}
