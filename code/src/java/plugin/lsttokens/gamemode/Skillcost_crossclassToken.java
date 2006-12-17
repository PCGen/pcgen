package plugin.lsttokens.gamemode;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with SKILLCOST_CROSSCLASS Token
 */
public class Skillcost_crossclassToken implements GameModeLstToken
{

	public String getTokenName()
	{
		return "SKILLCOST_CROSSCLASS";
	}

	public boolean parse(GameMode gameMode, String value)
	{
		try
		{
			gameMode.setSkillCost_CrossClass(Integer.parseInt(value));
			return true;
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
	}
}
