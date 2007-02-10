package plugin.lsttokens.gamemode.wieldcategory;

import pcgen.core.character.WieldCategory;
import pcgen.persistence.lst.WieldCategoryLstToken;
import pcgen.util.Logging;

/**
 * Class deals with FINESSABLE Token
 */
public class FinessableToken implements WieldCategoryLstToken
{

	public String getTokenName()
	{
		return "FINESSABLE";
	}

	public boolean parse(WieldCategory cat, String value)
	{
		boolean set;
		char firstChar = value.charAt(0);
		if (firstChar == 'y' || firstChar =='Y')
		{
			if (!value.equalsIgnoreCase("YES"))
			{
				Logging.errorPrint("You should use 'YES' or 'NO' as the " + getTokenName());
				Logging.errorPrint("Abbreviations will fail after PCGen 5.12");
			}
			set = true;
		}
		else 
		{
			if (value.equalsIgnoreCase("NO"))
			{
				Logging.errorPrint("You should use 'YES' or 'NO' as the "
						+ getTokenName());
				Logging.errorPrint("Abbreviations will fail after PCGen 5.12");
			}
			set = false;
		}
		cat.setFinessable(set);
		return true;
	}
}
