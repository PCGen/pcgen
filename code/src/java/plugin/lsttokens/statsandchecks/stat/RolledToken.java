package plugin.lsttokens.statsandchecks.stat;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCStat;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryParserToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with PENALTYVAR Token
 */
public class RolledToken extends AbstractNonEmptyToken<PCStat> implements
		CDOMPrimaryParserToken<PCStat>
{

	@Override
	public String getTokenName()
	{
		return "ROLLED";
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context, PCStat stat,
		String value)
	{
		Boolean set;
		char firstChar = value.charAt(0);
		if (firstChar == 'y' || firstChar == 'Y')
		{
			if (value.length() > 1 && !value.equalsIgnoreCase("YES"))
			{
				return new ParseResult.Fail("You should use 'YES' as the "
						+ getTokenName() + ": " + value);
			}
			set = Boolean.TRUE;
		}
		else
		{
			if (firstChar != 'N' && firstChar != 'n')
			{
				return new ParseResult.Fail("You should use 'YES' or 'NO' as the "
						+ getTokenName() + ": " + value);
			}
			if (value.length() > 1 && !value.equalsIgnoreCase("NO"))
			{
				return new ParseResult.Fail("You should use 'YES' or 'NO' as the "
						+ getTokenName() + ": " + value);
			}
			set = Boolean.FALSE;
		}
		context.getObjectContext().put(stat, ObjectKey.ROLLED, set);
		return ParseResult.SUCCESS;
	}

	public String[] unparse(LoadContext context, PCStat stat)
	{
		Boolean b = context.getObjectContext()
				.getObject(stat, ObjectKey.ROLLED);
		if (b == null)
		{
			return null;
		}
		return new String[] { b.booleanValue() ? "YES" : "NO" };
	}

	public Class<PCStat> getTokenClass()
	{
		return PCStat.class;
	}
}
