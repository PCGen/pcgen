package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;

/**
 * Class deals with REMOVABLE Token
 */
public class RemovableToken implements PCTemplateLstToken
{

	public String getTokenName()
	{
		return "REMOVABLE";
	}

	public boolean parse(PCTemplate template, String value)
	{
		boolean set;
		char firstChar = value.charAt(0);
		if (firstChar == 'y' || firstChar =='Y')
		{
			// 514 abbreviation cleanup
//			if (value.length() > 1 && !value.equalsIgnoreCase("YES"))
//			{
//				Logging.errorPrint("You should use 'YES' or 'NO' as the " + getTokenName());
//				Logging.errorPrint("Abbreviations will fail after PCGen 5.12");
//			}
			set = true;
		}
		else 
		{
			// 514 abbreviation cleanup
//			if (firstChar != 'N' && firstChar != 'n'
//				&& !value.equalsIgnoreCase("NO"))
//			{
//				Logging.errorPrint("You should use 'YES' or 'NO' as the "
//						+ getTokenName());
//				Logging.errorPrint("Abbreviations will fail after PCGen 5.12");
//			}
			set = false;
		}
		template.setRemovable(set);
		return true;
	}
}
