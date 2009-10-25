package plugin.lsttokens.sizeadjustment;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.SizeAdjustment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryParserToken;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

/**
 * Class deals with ABB Token for size adjustment
 */
public class AbbToken extends AbstractNonEmptyToken<SizeAdjustment> implements
		CDOMPrimaryParserToken<SizeAdjustment>, DeferredToken<SizeAdjustment>
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
	public ParseResult parseNonEmptyToken(LoadContext context, SizeAdjustment size, String value)
	{
		/*
		 * Warning: RegisterAbbreviation is not editor friendly, and this is a
		 * gate to additional SizeAdjustments being added in Campaigns (vs. Game
		 * Modes)
		 */
		context.ref.registerAbbreviation(size, value);
		return ParseResult.SUCCESS;
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
