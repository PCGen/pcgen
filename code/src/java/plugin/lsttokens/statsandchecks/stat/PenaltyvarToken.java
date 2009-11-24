package plugin.lsttokens.statsandchecks.stat;

import pcgen.core.PCStat;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ErrorParsingWrapper;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with PENALTYVAR Token
 */
public class PenaltyvarToken extends ErrorParsingWrapper<PCStat> implements CDOMPrimaryToken<PCStat>
{

	public String getTokenName()
	{
		return "PENALTYVAR";
	}

	public ParseResult parseToken(LoadContext context, PCStat obj, String value)
	{
		// TODO Need to figure out what to do here...
		return ParseResult.SUCCESS;
	}

	public String[] unparse(LoadContext context, PCStat obj)
	{
		// TODO Need to figure out what to do here...
		return null;
	}

	public Class<PCStat> getTokenClass()
	{
		return PCStat.class;
	}
}
