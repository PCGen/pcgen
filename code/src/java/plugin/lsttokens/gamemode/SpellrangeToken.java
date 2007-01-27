package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with SPELLRANGE Token
 */
public class SpellrangeToken implements GameModeLstToken
{

	public String getTokenName()
	{
		return "SPELLRANGE";
	}

	public boolean parse(GameMode gameMode, String value, URI source)
	{
		gameMode.setSpellRangeFormula(value);
		return true;
	}
}
