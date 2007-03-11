package plugin.lsttokens.deprecated;

import pcgen.core.PCTemplate;
import pcgen.core.PObject;
import pcgen.persistence.lst.DeprecatedToken;
import pcgen.persistence.lst.PCTemplateLstToken;

/**
 * Class deals with COST Token
 */
public class CostToken implements PCTemplateLstToken, DeprecatedToken
{

	public String getTokenName()
	{
		return "COST";
	}

	public boolean parse(PCTemplate template, String value)
	{
		template.setCost(value);
		return true;
	}

	public String getMessage(PObject obj, String value)
	{
		return "Template's COST Token is unused";
	}
}
