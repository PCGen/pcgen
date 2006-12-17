package plugin.lsttokens.companionmod;

import pcgen.core.character.CompanionMod;
import pcgen.persistence.lst.CompanionModLstToken;

/**
 * Class deals with MASTERBONUSRACE Token
 */
public class MasterbonusraceToken implements CompanionModLstToken
{

	public String getTokenName()
	{
		return "MASTERBONUSRACE";
	}

	public boolean parse(CompanionMod cmpMod, String value)
	{
		cmpMod.getClassMap().put(value.toUpperCase(), "1");
		return true;
	}
}
