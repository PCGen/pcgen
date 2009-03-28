package plugin.lsttokens.sizeadjustment;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.SizeAdjustment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with ISDEFAULTSIZE Token
 */
public class IsdefaultsizeToken extends AbstractToken implements
		CDOMPrimaryToken<SizeAdjustment>
{

	@Override
	public String getTokenName()
	{
		return "ISDEFAULTSIZE";
	}

	public boolean parse(LoadContext context, SizeAdjustment size, String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
		Boolean set;
		char firstChar = value.charAt(0);
		if (firstChar == 'y' || firstChar == 'Y')
		{
			if (value.length() > 1 && !value.equalsIgnoreCase("YES"))
			{
				Logging.errorPrint("You should use 'YES' as the "
						+ getTokenName() + ": " + value);
				return false;
			}
			set = Boolean.TRUE;
		}
		else
		{
			if (firstChar != 'N' && firstChar != 'n')
			{
				Logging.errorPrint("You should use 'YES' or 'NO' as the "
						+ getTokenName() + ": " + value);
				return false;
			}
			if (value.length() > 1 && !value.equalsIgnoreCase("NO"))
			{
				Logging.errorPrint("You should use 'YES' or 'NO' as the "
						+ getTokenName() + ": " + value);
				return false;
			}
			set = Boolean.FALSE;
		}
		context.getObjectContext().put(size, ObjectKey.IS_DEFAULT_SIZE, set);
		return true;
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
