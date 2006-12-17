package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;

/**
 * Class deals with NONPP Token
 */
public class NonppToken implements PCTemplateLstToken
{

	public String getTokenName()
	{
		return "NONPP";
	}

	public boolean parse(PCTemplate template, String value)
	{
		try
		{
			template.setNonProficiencyPenalty(Integer.parseInt(value));
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
		return true;
	}
}
