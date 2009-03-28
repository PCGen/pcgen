package plugin.lsttokens.statsandchecks.stat;

import pcgen.core.PCStat;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with STATNAME Token
 */
public class StatnameToken extends AbstractToken implements
		CDOMPrimaryToken<PCStat>
{

	@Override
	public String getTokenName()
	{
		return "STATNAME";
	}

	public boolean parse(LoadContext context, PCStat stat, String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
		/*
		 * Warning: setName is not editor friendly, and this is a gate to
		 * additional stats being added in Campaigns (vs. Game Modes)
		 */
		stat.setName(value);
		return true;
	}

	public String[] unparse(LoadContext context, PCStat stat)
	{
		String abb = stat.getDisplayName();
		if (abb == null)
		{
			return null;
		}
		return new String[] { abb };
	}

	public Class<PCStat> getTokenClass()
	{
		return PCStat.class;
	}
}
