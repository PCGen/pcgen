package plugin.lsttokens.gamemode;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with HIDDENSKILLTYPES Token
 */
public class HiddenskilltypesToken implements GameModeLstToken
{

	public String getTokenName()
	{
		return "HIDDENSKILLTYPES";
	}

	public boolean parse(GameMode gameMode, String value)
	{
		gameMode.setHiddenSkillTypes(value);
		return true;
	}
}
