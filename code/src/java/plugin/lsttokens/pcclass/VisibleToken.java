package plugin.lsttokens.pcclass;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCClass;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;
import pcgen.util.enumeration.Visibility;

/**
 * Class deals with VISIBLE Token
 */
public class VisibleToken implements CDOMPrimaryToken<PCClass>
{

	public String getTokenName()
	{
		return "VISIBLE";
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
	{
		Visibility vis;
		if (value.equals("NO"))
		{
			vis = Visibility.HIDDEN;
		}
		else if (value.equalsIgnoreCase("Y") || value.equals("YES"))
		{
			vis = Visibility.DEFAULT;
		}
		else
		{
			Logging.errorPrint("Can't understand Visibility: " + value);
			return false;
		}
		context.getObjectContext().put(pcc, ObjectKey.VISIBILITY, vis);
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		Visibility vis = context.getObjectContext().getObject(pcc,
				ObjectKey.VISIBILITY);
		if (vis == null)
		{
			return null;
		}
		String visString;
		if (vis.equals(Visibility.DEFAULT))
		{
			visString = "YES";
		}
		else if (vis.equals(Visibility.HIDDEN))
		{
			visString = "NO";
		}
		else
		{
			context.addWriteMessage("Visibility " + vis
					+ " is not a valid Visibility for a PCClass");
			return null;
		}
		return new String[] { visString };
	}

	public Class<PCClass> getTokenClass()
	{
		return PCClass.class;
	}
}
