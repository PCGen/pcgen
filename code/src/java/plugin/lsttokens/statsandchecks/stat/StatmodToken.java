package plugin.lsttokens.statsandchecks.stat;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.PCStat;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryParserToken;
import pcgen.rules.persistence.token.ErrorParsingWrapper;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with STATMOD Token
 */
public class StatmodToken extends ErrorParsingWrapper<PCStat> implements CDOMPrimaryParserToken<PCStat>
{

	public String getTokenName()
	{
		return "STATMOD";
	}

	public ParseResult parseToken(LoadContext context, PCStat stat, String value)
	{
		if (value == null || value.length() == 0)
		{
			return new ParseResult.Fail(getTokenName() + " arguments may not be empty");
		}
		context.getObjectContext().put(stat, StringKey.STAT_MOD, value);
		return ParseResult.SUCCESS;
	}

	public String[] unparse(LoadContext context, PCStat stat)
	{
		String target = context.getObjectContext().getString(stat,
				StringKey.STAT_MOD);
		if (target == null)
		{
			return null;
		}
		return new String[] { target };
	}

	public Class<PCStat> getTokenClass()
	{
		return PCStat.class;
	}
}
