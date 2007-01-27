package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with HIDDENEQUIPTYPES Token
 */
public class HiddenequiptypesToken implements GameModeLstToken
{

	public String getTokenName()
	{
		return "HIDDENEQUIPTYPES";
	}

	public boolean parse(GameMode gameMode, String value, URI source)
	{
		gameMode.setHiddenEquipmentTypes(value);
		return true;
	}
}
