package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;

/**
 * Class deals with QUALIFY Token
 */
public class QualifyToken implements PCTemplateLstToken
{

	public String getTokenName()
	{
		return "QUALIFY";
	}

	public boolean parse(PCTemplate template, String value)
	{
		template.setQualifyString(value);
		return true;
	}
}
