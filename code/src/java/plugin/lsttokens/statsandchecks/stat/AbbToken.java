package plugin.lsttokens.statsandchecks.stat;

import pcgen.core.PCStat;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryParserToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with ABB Token for pc stat
 */
public class AbbToken extends AbstractNonEmptyToken<PCStat> implements CDOMPrimaryParserToken<PCStat>
{

	/**
	 * Return token name
	 * 
	 * @return token name
	 */
	@Override
	public String getTokenName()
	{
		return "ABB";
	}

	@Override
	public ParseResult parseNonEmptyToken(LoadContext context, PCStat stat, String value)
	{
		if (value.length() != 3)
		{
			return new ParseResult.Fail("Stat " + stat.getDisplayName() + " found with "
					+ getTokenName() + ":" + value
					+ " should be 3 characters long!");
		}
		/*
		 * Warning: RegisterAbbreviation is not editor friendly, and this is a
		 * gate to additional stats being added in Campaigns (vs. Game Modes)
		 */
		context.ref.registerAbbreviation(stat, value.toUpperCase());
		return ParseResult.SUCCESS;
	}

	public String[] unparse(LoadContext context, PCStat stat)
	{
		String abb = context.ref.getAbbreviation(stat);
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