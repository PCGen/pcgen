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
			if (value.length() > 1 && !value.equalsIgnoreCase("YES"))
			{
				Logging.deprecationPrint("You should use 'YES' or 'NO' as the " + getTokenName());
				Logging.deprecationPrint("Abbreviations will fail after PCGen 5.14");
			}
			set = true;
		}
		else 
		{
			if (firstChar != 'N' && firstChar != 'n'
				&& !value.equalsIgnoreCase("NO"))
			{
				Logging.deprecationPrint("You should use 'YES' or 'NO' as the "
						+ getTokenName());
				Logging.deprecationPrint("Abbreviations will fail after PCGen 5.14");
			}
			set = false;
		}
		cat.setFinessable(set);
		return true;
	}
}
