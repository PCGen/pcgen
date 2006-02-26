package plugin.lsttokens.companionmod;

import pcgen.core.character.CompanionMod;
import pcgen.persistence.lst.CompanionModLstToken;

/**
 * Class deals with RACETYPE: Token
 */
public class RaceTypeToken implements CompanionModLstToken {

	public String getTokenName() {
		return "RACETYPE";
	}

	public boolean parse(CompanionMod cmpMod, String value)
	{
		cmpMod.setRaceType(value);
		return true;
	}
}
