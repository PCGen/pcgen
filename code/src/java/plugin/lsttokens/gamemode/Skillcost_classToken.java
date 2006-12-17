package plugin.lsttokens.gamemode;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with SKILLCOST_CLASS Token
 */
public class Skillcost_classToken implements GameModeLstToken
{

	public String getTokenName()
	{
		return "SKILLCOST_CLASS";
	}

	public boolean parse(GameMode gameMode, String value)
	{
		try
		{
			gameMode.setSkillCost_Class(Integer.parseInt(value));
			return true;
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
	}
}
