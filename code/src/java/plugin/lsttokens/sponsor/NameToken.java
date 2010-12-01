package plugin.lsttokens.sponsor;

import pcgen.cdom.content.Sponsor;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with NAME Token
 */
public class NameToken implements CDOMPrimaryToken<Sponsor>
{

	public String getTokenName()
	{
		return "NAME";
	}

	public Class<Sponsor> getTokenClass()
	{
		return Sponsor.class;
	}

	public ParseResult parseToken(LoadContext context, Sponsor s, String value)
	{
		s.setName(value);
		return ParseResult.SUCCESS;
	}

	public String[] unparse(LoadContext context, Sponsor s)
	{
		// TODO Need to unparse
		return null;
	}

}
