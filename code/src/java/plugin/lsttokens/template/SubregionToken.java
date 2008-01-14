package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.util.Logging;

/**
 * Class deals with SUBREGION Token
 */
public class SubregionToken implements PCTemplateLstToken
{

	public String getTokenName()
	{
		return "SUBREGION";
	}

	public boolean parse(PCTemplate template, String value)
	{
		String subregion = value;
		char firstChar = value.charAt(0);
		if (firstChar == 'y' || firstChar == 'Y')
		{
			if (value.equalsIgnoreCase("YES"))
			{
				subregion = template.getDisplayName();
			}
			else
			{
				Logging.deprecationPrint("You should use 'YES' as the "
					+ getTokenName());
				Logging
					.deprecationPrint("Abbreviations will fail after PCGen 5.14");
				Logging
						.deprecationPrint("If your "
								+ getTokenName()
								+ " starts with a 'Y' then please ignore this message, "
								+ "it is alerting those taking advantage of an "
								+ "abbreviation system that is being removed from PCGen");
			}
		}
		template.setSubRegion(subregion);
		return true;
	}
}
