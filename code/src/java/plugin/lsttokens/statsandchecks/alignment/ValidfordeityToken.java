package plugin.lsttokens.statsandchecks.alignment;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCAlignment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryParserToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with VALIDFORDEITY Token
 */
public class ValidfordeityToken extends AbstractNonEmptyToken<PCAlignment> implements
		CDOMPrimaryParserToken<PCAlignment>
{

	@Override
	public String getTokenName()
	{
		return "VALIDFORDEITY";
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context,
		PCAlignment al, String value)
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
		context.getObjectContext().put(al, ObjectKey.VALID_FOR_DEITY, set);
		return ParseResult.SUCCESS;
	}

	public String[] unparse(LoadContext context, PCAlignment al)
	{
		Boolean b = context.getObjectContext().getObject(al,
				ObjectKey.VALID_FOR_DEITY);
		if (b == null)
		{
			return null;
		}
		return new String[] { b.booleanValue() ? "YES" : "NO" };
	}

	public Class<PCAlignment> getTokenClass()
	{
		return PCAlignment.class;
	}
}
