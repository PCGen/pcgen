package plugin.lsttokens.pcclass;

import pcgen.cdom.content.HitDie;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCClass;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with HD Token
 */
public class HdToken implements CDOMPrimaryToken<PCClass>
{

	public String getTokenName()
	{
		return "HD";
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
	{
		try
		{
			Integer in = Integer.valueOf(value);
			if (in.intValue() <= 0)
			{
				Logging.errorPrint(getTokenName() + " must be an integer > 0");
				return false;
			}
			context.getObjectContext().put(pcc, ObjectKey.LEVEL_HITDIE,
					new HitDie(in));
			return true;
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint(getTokenName()
					+ " expected an integer.  Tag must be of the form: "
					+ getTokenName() + ":<int>");
			return false;
		}
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		HitDie lpf = context.getObjectContext().getObject(pcc,
				ObjectKey.LEVEL_HITDIE);
		if (lpf == null)
		{
			return null;
		}
		if (lpf.getDie() <= 0)
		{
			context.addWriteMessage(getTokenName() + " must be an integer > 0");
			return null;
		}
		return new String[] { Integer.toString(lpf.getDie()) };
	}

	public Class<PCClass> getTokenClass()
	{
		return PCClass.class;
	}
}
