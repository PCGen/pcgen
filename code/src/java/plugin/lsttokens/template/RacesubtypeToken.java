package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;

/**
 * Class deals with RACESUBTYPE Token
 */
public class RacesubtypeToken implements PCTemplateLstToken
{

	public String getTokenName()
	{
		return "RACESUBTYPE";
	}

	public boolean parse(PCTemplate template, String value)
	{
		template.addSubTypeString(value);
		return true;
	}
}
