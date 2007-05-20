package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.util.Logging;

/**
 * Class deals with SUBRACE Token
 */
public class SubraceToken implements PCTemplateLstToken
{

	public String getTokenName()
	{
		return "SUBRACE";
	}

	public boolean parse(PCTemplate template, String value)
	{
		String subrace = value;
		char firstChar = value.charAt(0);
		if (firstChar == 'y' || firstChar =='Y')
		{
			if (value.equalsIgnoreCase("YES"))
			{
				subrace = template.getDisplayName();
			}
			else 
			{
				// 514 abbreviation cleanup
//				Logging.errorPrint("You should use 'YES' as the " + getTokenName());
//				Logging.errorPrint("Abbreviations will fail after PCGen 5.12");
			}
		}

		template.setSubRace(subrace);
		return true;
	}
}
