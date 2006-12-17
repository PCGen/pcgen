package plugin.lsttokens.deity;

import pcgen.core.Deity;
import pcgen.persistence.lst.DeityLstToken;

/**
 * Class deals with SYMBOL Token
 */
public class SymbolToken implements DeityLstToken
{

	public String getTokenName()
	{
		return "SYMBOL";
	}

	public boolean parse(Deity deity, String value)
	{
		deity.setHolyItem(value);
		return true;
	}
}
