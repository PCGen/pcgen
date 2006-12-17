package plugin.lsttokens.companionmod;

import pcgen.core.character.CompanionMod;
import pcgen.persistence.lst.CompanionModLstToken;

/**
 * Class deals with COPYMASTERCHECK Token
 */
public class CopymastercheckToken implements CompanionModLstToken
{

	public String getTokenName()
	{
		return "COPYMASTERCHECK";
	}

	public boolean parse(CompanionMod cmpMod, String value)
	{
		cmpMod.setCopyMasterCheck(value);
		return true;
	}
}
