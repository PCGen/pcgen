package plugin.lsttokens.pcclass;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.PCClass;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with XTRAFEATS Token
 */
public class XtrafeatsToken implements CDOMPrimaryToken<PCClass>
{

	/**
	 * Get Token name
	 * 
	 * @return token name
	 */
	public String getTokenName()
	{
		return "XTRAFEATS";
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
	{
		int featCount;
		try
		{
			featCount = Integer.parseInt(value);
			if (featCount <= 0)
			{
				Logging.errorPrint("Number in " + getTokenName()
						+ " must be greater than zero: " + value);
				return false;
			}
			context.obj.put(pcc, IntegerKey.START_FEATS, featCount);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Invalid Number in " + getTokenName() + ": "
					+ value);
			return false;
		}
	}

	public String[] unparse(LoadContext context, PCClass obj)
	{
		Integer feats = context.getObjectContext().getInteger(obj,
				IntegerKey.START_FEATS);
		if (feats == null)
		{
			return null;
		}
		if (feats.intValue() <= 0)
		{
			context.addWriteMessage(getTokenName() + " must be an integer > 0");
			return null;
		}
		return new String[] { feats.toString() };
	}

	public Class<PCClass> getTokenClass()
	{
		return PCClass.class;
	}
}
