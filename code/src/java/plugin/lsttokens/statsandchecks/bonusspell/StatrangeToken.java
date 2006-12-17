package plugin.lsttokens.statsandchecks.bonusspell;

import java.util.Map;

import pcgen.persistence.lst.BonusSpellLoader;
import pcgen.persistence.lst.BonusSpellLstToken;

/**
 * Class deals with STATRANGE Token
 */
public class StatrangeToken implements BonusSpellLstToken
{

	public String getTokenName()
	{
		return "STATRANGE";
	}

	public boolean parse(Map<String, String> bonus, String value)
	{
		bonus.put(BonusSpellLoader.STAT_RANGE, value);
		return true;
	}
}
