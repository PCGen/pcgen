package plugin.lsttokens.sponsor;

import pcgen.cdom.content.Sponsor;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with TEXT Token
 */
public class TextToken implements CDOMPrimaryToken<Sponsor>
{

	public String getTokenName()
	{
		return "TEXT";
	}

	public Class<Sponsor> getTokenClass()
	{
		return Sponsor.class;
	}

	public ParseResult parseToken(LoadContext context, Sponsor s, String value)
	{
		s.setText(value);
		return ParseResult.SUCCESS;
	}

	public String[] unparse(LoadContext context, Sponsor s)
	{
		// TODO Need to unparse
		return null;
	}

}
