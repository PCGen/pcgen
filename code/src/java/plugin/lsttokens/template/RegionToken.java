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
		//CONSIDER This prohibits any Region that starts with Y ... too general? - thpr 10/27/06
		if (value.toUpperCase().startsWith("Y"))
		{
			region = template.getDisplayName();
		}

		template.setRegion(region);
		return true;
	}
}
