/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class UmultLst implements CDOMPrimaryToken<CDOMObject>
{

	public String getTokenName()
	{
		return "UMULT";
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		if (Constants.LST_DOT_CLEAR.equals(value))
		{
			context.getObjectContext().put(obj, IntegerKey.UMULT, null);
		}
		else
		{
			try
			{
				Integer i = Integer.valueOf(value);
				if (i.intValue() <= 0)
				{
					Logging.errorPrint("Invalid " + getTokenName() + ": "
							+ value);
					Logging.errorPrint("  Expecting a positive integer");
					return false;
				}
				context.getObjectContext().put(obj, IntegerKey.UMULT, i);
			}
			catch (NumberFormatException nfe)
			{
				Logging.errorPrint("Invalid " + getTokenName() + ": " + value);
				Logging.errorPrint("  Expecting an integer");
				return false;
			}
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Integer mult = context.getObjectContext().getInteger(obj,
				IntegerKey.UMULT);
		if (mult == null)
		{
			return null;
		}
		if (mult.intValue() <= 0)
		{
			context.addWriteMessage(getTokenName() + " must be an integer > 0");
			return null;
		}
		return new String[] { mult.toString() };
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
