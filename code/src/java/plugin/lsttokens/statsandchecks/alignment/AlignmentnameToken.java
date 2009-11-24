package plugin.lsttokens.statsandchecks.alignment;

import pcgen.core.PCAlignment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with ALIGNMENTNAME Token
 */
public class AlignmentnameToken extends AbstractNonEmptyToken<PCAlignment> implements
		CDOMPrimaryToken<PCAlignment>
{

	@Override
	public String getTokenName()
	{
		return "ALIGNMENTNAME";
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context,
		PCAlignment align, String value)
	{
		/*
		 * Warning: setName is not editor friendly, and this is a gate to
		 * additional alignments being added in Campaigns (vs. Game Modes)
		 */
		align.setName(value);
		return ParseResult.SUCCESS;
	}

	public String[] unparse(LoadContext context, PCAlignment align)
	{
		String abb = align.getDisplayName();
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
