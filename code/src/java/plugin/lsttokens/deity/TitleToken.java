package plugin.lsttokens.deity;

import pcgen.core.Deity;
import pcgen.persistence.lst.DeityLstToken;

/**
 * Class deals with TITLE Token
 */
public class TitleToken implements DeityLstToken
{

	public String getTokenName()
	{
		return "TITLE";
	}

	public boolean parse(Deity deity, String value)
	{
		deity.setTitle(value);
		return true;
	}
}
