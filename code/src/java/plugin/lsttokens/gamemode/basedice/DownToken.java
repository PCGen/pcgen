package plugin.lsttokens.gamemode.basedice;

import java.util.Map;

import pcgen.persistence.lst.BaseDiceLoader;
import pcgen.persistence.lst.BaseDiceLstToken;

/**
 * Class deals with DOWN Token
 */
public class DownToken implements BaseDiceLstToken
{

	public String getTokenName()
	{
		return "DOWN";
	}

	public boolean parse(Map<String, String> baseDice, String value)
	{
		baseDice.put(BaseDiceLoader.DOWN, value);
		return true;
	}
}
