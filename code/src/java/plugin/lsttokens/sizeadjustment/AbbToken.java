package plugin.lsttokens.sizeadjustment;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.SizeAdjustment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.util.Logging;

/**
 * Class deals with ABB Token for size adjustment
 */
public class AbbToken extends AbstractToken implements
		CDOMPrimaryToken<SizeAdjustment>, DeferredToken<SizeAdjustment>
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

	public boolean parse(LoadContext context, SizeAdjustment size, String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
		/*
		 * Warning: RegisterAbbreviation is not editor friendly, and this is a
		 * gate to additional SizeAdjustments being added in Campaigns (vs. Game
		 * Modes)
		 */
		context.ref.registerAbbreviation(size, value);
		return true;
	}

	public String[] unparse(LoadContext context, SizeAdjustment size)
	{
		String abb = context.ref.getAbbreviation(size);
		if (abb == null)
		{
			return null;
		}
		return new String[] { abb };
	}

	public Class<SizeAdjustment> getTokenClass()
	{
		return SizeAdjustment.class;
	}

	public Class<SizeAdjustment> getDeferredTokenClass()
	{
		return SizeAdjustment.class;
	}

	public boolean process(LoadContext context, SizeAdjustment size)
	{
		String abb = size.get(StringKey.ABB);
		if (abb == null)
		{
			Logging.errorPrint("Expected SizeAdjustment to "
					+ "have an Abbreviation, but " + size.getDisplayName()
					+ " did not");
			return false;
		}
		if (abb.length() > 1)
		{
			Logging.errorPrint("Expected SizeAdjustment to have a "
					+ "single character Abbreviation, but "
					+ size.getDisplayName() + " had: " + abb);
			return false;
		}
		return false;
	}
}
