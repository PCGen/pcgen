package plugin.lsttokens.statsandchecks.alignment;

import pcgen.core.PCAlignment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with ABB Token for pc alignment
 */
public class AbbToken extends AbstractToken implements
		CDOMPrimaryToken<PCAlignment>
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

	public boolean parse(LoadContext context, PCAlignment al, String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
		/*
		 * Warning: RegisterAbbreviation is not editor friendly, and this is a
		 * gate to additional alignments being added in Campaigns (vs. Game
		 * Modes)
		 */
		context.ref.registerAbbreviation(al, value);
		return true;
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
