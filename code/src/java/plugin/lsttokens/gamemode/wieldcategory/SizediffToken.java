package plugin.lsttokens.gamemode.wieldcategory;

import pcgen.core.character.WieldCategory;
import pcgen.persistence.lst.WieldCategoryLstToken;

/**
 * Class deals with SIZEDIFF Token
 */
public class SizediffToken implements WieldCategoryLstToken
{

	public String getTokenName()
	{
		return "SIZEDIFF";
	}

	public boolean parse(WieldCategory cat, String value)
	{
		// Number of size categories Object Size diff
		try
		{
			cat.setSizeDiff(Integer.parseInt(value));
			return true;
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
	}
}
