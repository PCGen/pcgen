package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.util.Logging;

/**
 * Class deals with ISMONSTER Token
 */
public class IsmonsterToken implements PCClassLstToken
{

	public String getTokenName()
	{
		return "ISMONSTER";
	}

	public boolean parse(final PCClass pcclass, final String value,
		final int level)
	{
		if (value.startsWith("Y")) //$NON-NLS-1$
		{
			pcclass.setMonsterFlag(true);
		}
		else if (value.startsWith("N")) //$NON-NLS-1$
		{
			pcclass.setMonsterFlag(false);
		}
		else
		{
			// TODO - I8NL
			Logging.errorPrint("Unknown option " + value + " in "
				+ getTokenName());
		}
		return true;
	}
}
