package plugin.lsttokens.statsandchecks.stat;

import pcgen.core.PCStat;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with STATNAME Token
 */
public class StatnameToken extends AbstractNonEmptyToken<PCStat> implements
		CDOMPrimaryToken<PCStat>
{

	@Override
	public String getTokenName()
	{
		return "STATNAME";
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context, PCStat stat,
		String value)
	{
		/*
		 * Warning: setName is not editor friendly, and this is a gate to
		 * additional stats being added in Campaigns (vs. Game Modes)
		 */
		stat.setName(value);
		return ParseResult.SUCCESS;
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
