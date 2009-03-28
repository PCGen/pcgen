package plugin.lsttokens.statsandchecks.stat;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCStat;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with PENALTYVAR Token
 */
public class RolledToken extends AbstractToken implements
		CDOMPrimaryToken<PCStat>
{

	@Override
	public String getTokenName()
	{
		return "ROLLED";
	}

	public boolean parse(LoadContext context, PCStat stat, String value)
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
		context.getObjectContext().put(stat, ObjectKey.ROLLED, set);
		return true;
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
