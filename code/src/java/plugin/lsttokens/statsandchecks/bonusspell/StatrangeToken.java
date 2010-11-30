package plugin.lsttokens.statsandchecks.bonusspell;

import pcgen.cdom.content.BonusSpellInfo;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with STATRANGE Token
 */
public class StatrangeToken implements CDOMPrimaryToken<BonusSpellInfo>
{

	public String getTokenName()
	{
		return "STATRANGE";
	}

	public Class<BonusSpellInfo> getTokenClass()
	{
		return BonusSpellInfo.class;
	}

	public ParseResult parseToken(LoadContext context, BonusSpellInfo bsi,
			String value)
	{
		try
		{
			int intValue = Integer.valueOf(value).intValue();
			if (intValue < 1)
			{
				return new ParseResult.Fail(getTokenName()
						+ " must be an integer >= " + 1);
			}
			bsi.setStatRange(intValue);
			return ParseResult.SUCCESS;
		}
		catch (NumberFormatException nfe)
		{
			return new ParseResult.Fail(getTokenName()
					+ " expected an integer.  Tag must be of the form: "
					+ getTokenName() + ":<int>");
		}
	}

	public String[] unparse(LoadContext context, BonusSpellInfo bsi)
	{
		int range = bsi.getStatRange();
		if (range == 0)
		{
			return null;
		}
		return new String[] { String.valueOf(range) };
	}

}
