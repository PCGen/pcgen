package plugin.lsttokens.statsandchecks.stat;

import pcgen.core.PCStat;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with ABB Token for pc stat
 */
public class AbbToken extends AbstractToken implements CDOMPrimaryToken<PCStat>
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

	public boolean parse(LoadContext context, PCStat stat, String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
		if (value.length() != 3)
		{
			Logging.errorPrint("Stat " + stat.getDisplayName() + " found with "
					+ getTokenName() + ":" + value
					+ " should be 3 characters long!");
		}
		/*
		 * Warning: RegisterAbbreviation is not editor friendly, and this is a
		 * gate to additional stats being added in Campaigns (vs. Game Modes)
		 */
		context.ref.registerAbbreviation(stat, value.toUpperCase());
		return true;
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