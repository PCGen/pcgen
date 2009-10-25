package plugin.lsttokens.sizeadjustment;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.SizeAdjustment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with ISDEFAULTSIZE Token
 */
public class IsdefaultsizeToken extends AbstractNonEmptyToken<SizeAdjustment> implements
		CDOMPrimaryToken<SizeAdjustment>
{

	@Override
	public String getTokenName()
	{
		return "ISDEFAULTSIZE";
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context,
		SizeAdjustment size, String value)
	{
		Boolean set;
		char firstChar = value.charAt(0);
		if (firstChar == 'y' || firstChar == 'Y')
		{
			if (value.length() > 1 && !value.equalsIgnoreCase("YES"))
			{
				new ParseResult.Fail("You should use 'YES' as the "
						+ getTokenName() + ": " + value);
			}
			set = Boolean.TRUE;
		}
		else
		{
			if (firstChar != 'N' && firstChar != 'n')
			{
				new ParseResult.Fail("You should use 'YES' or 'NO' as the "
						+ getTokenName() + ": " + value);
			}
			if (value.length() > 1 && !value.equalsIgnoreCase("NO"))
			{
				new ParseResult.Fail("You should use 'YES' or 'NO' as the "
						+ getTokenName() + ": " + value);
			}
			set = Boolean.FALSE;
		}
		context.getObjectContext().put(size, ObjectKey.IS_DEFAULT_SIZE, set);
		return ParseResult.SUCCESS;
	}

	public String[] unparse(LoadContext context, SizeAdjustment size)
	{
		Boolean b = context.getObjectContext().getObject(size,
				ObjectKey.IS_DEFAULT_SIZE);
		if (b == null)
		{
			return null;
		}
		return new String[] { b.booleanValue() ? "YES" : "NO" };
	}

	public Class<SizeAdjustment> getTokenClass()
	{
		return SizeAdjustment.class;
	}
}
