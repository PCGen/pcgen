package plugin.lsttokens.statsandchecks.alignment;

import pcgen.core.PCAlignment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with ALIGNMENTNAME Token
 */
public class AlignmentnameToken extends AbstractToken implements
		CDOMPrimaryToken<PCAlignment>
{

	@Override
	public String getTokenName()
	{
		return "ALIGNMENTNAME";
	}

	public boolean parse(LoadContext context, PCAlignment align, String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
		/*
		 * Warning: setName is not editor friendly, and this is a gate to
		 * additional alignments being added in Campaigns (vs. Game Modes)
		 */
		align.setName(value);
		return true;
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
