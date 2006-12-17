package plugin.lsttokens.deity;

import pcgen.core.Deity;
import pcgen.persistence.lst.DeityLstToken;

/**
 * Class deals with DEITYWEAP Token
 */
public class DeityweapToken implements DeityLstToken
{

	public String getTokenName()
	{
		return "DEITYWEAP";
	}

	public boolean parse(Deity deity, String value)
	{
		deity.setFavoredWeapon(value);
		return true;
	}
}
