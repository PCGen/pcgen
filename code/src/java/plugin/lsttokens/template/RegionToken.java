package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;

/**
 * Class deals with REGION Token
 */
public class RegionToken implements PCTemplateLstToken
{

	public String getTokenName()
	{
		return "REGION";
	}

	public boolean parse(PCTemplate template, String value)
	{
		String region = value;
		if (value.equalsIgnoreCase("YES"))
		{
			region = template.getDisplayName();
		}
		template.setRegion(region);
		return true;
	}
}
