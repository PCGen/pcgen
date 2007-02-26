package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.util.Logging;

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
		char firstChar = value.charAt(0);
		if (firstChar == 'y' || firstChar =='Y')
		{
			if (value.equalsIgnoreCase("YES"))
			{
				region = template.getDisplayName();
			}
			else 
			{
				Logging.errorPrint("You should use 'YES' as the " + getTokenName());
				Logging.errorPrint("Abbreviations will fail after PCGen 5.12");
			}
		}

		template.setRegion(region);
		return true;
	}
}
