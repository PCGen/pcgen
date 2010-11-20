package plugin.lsttokens.statsandchecks.check;

import pcgen.core.PCCheck;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with CHECKNAME Token
 */
public class ChecknameToken extends AbstractNonEmptyToken<PCCheck> implements
		CDOMPrimaryToken<PCCheck>
{

	@Override
	public String getTokenName()
	{
		return "CHECKNAME";
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context, PCCheck check,
		String value)
	{
		/*
		 * Warning: setName is not editor friendly, and this is a gate to
		 * additional checks being added in Campaigns (vs. Game Modes)
		 */
		check.setName(value);
		return ParseResult.SUCCESS;
	}

	public String[] unparse(LoadContext context, PCCheck check)
	{
		String name = check.getDisplayName();
		if (name == null)
		{
			return null;
		}
		return new String[] { name };
	}

	public Class<PCCheck> getTokenClass()
	{
		return PCCheck.class;
	}

}
