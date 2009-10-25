package plugin.lsttokens.statsandchecks.alignment;

import pcgen.core.PCAlignment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryParserToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with ABB Token for pc alignment
 */
public class AbbToken extends AbstractNonEmptyToken<PCAlignment> implements
		CDOMPrimaryParserToken<PCAlignment>
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
	protected ParseResult parseNonEmptyToken(LoadContext context,
		PCAlignment al, String value)
	{
		/*
		 * Warning: RegisterAbbreviation is not editor friendly, and this is a
		 * gate to additional alignments being added in Campaigns (vs. Game
		 * Modes)
		 */
		context.ref.registerAbbreviation(al, value);
		return ParseResult.SUCCESS;
	}

	public String[] unparse(LoadContext context, PCAlignment al)
	{
		String abb = context.ref.getAbbreviation(al);
		if (abb == null)
		{
			return null;
		}
		return new String[] { abb };
	}

	public Class<PCAlignment> getTokenClass()
	{
		return PCAlignment.class;
	}
}
