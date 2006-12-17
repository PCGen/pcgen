package plugin.lsttokens.gamemode.wieldcategory;

import pcgen.core.character.WieldCategory;
import pcgen.persistence.lst.WieldCategoryLstToken;
import java.util.StringTokenizer;

/**
 * Class deals with DAMAGEMULT Token
 */
public class DamagemultToken implements WieldCategoryLstToken
{

	public String getTokenName()
	{
		return "DAMAGEMULT";
	}

	public boolean parse(WieldCategory cat, String value)
	{
		// The damage multiplier based on
		// number of hands used to wield weapon
		// dString is of form:
		// 1=1,2=1.5
		StringTokenizer dTok = new StringTokenizer(value, ",");

		while (dTok.hasMoreTokens())
		{
			String cString = dTok.nextToken();

			// cString is of form: 2=1.5
			StringTokenizer cTok = new StringTokenizer(cString, "=");

			if (cTok.countTokens() < 2)
			{
				continue;
			}

			String hands = cTok.nextToken();
			int numHands = 1;
			try
			{
				numHands = Integer.parseInt(hands);
			}
			catch (NumberFormatException ex)
			{
				return false;
			}
			String mult = cTok.nextToken();
			float multiplier = 0.0f;
			try
			{
				multiplier = Float.parseFloat(mult);
			}
			catch (NumberFormatException ex)
			{
				return false;
			}
			cat.addDamageMult(numHands, multiplier);
		}
		return true;
	}
}
