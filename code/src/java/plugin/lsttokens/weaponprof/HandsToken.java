package plugin.lsttokens.weaponprof;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.WeaponProf;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with HANDS Token
 */
public class HandsToken implements CDOMPrimaryToken<WeaponProf>
{

	public String getTokenName()
	{
		return "HANDS";
	}

	public boolean parse(LoadContext context, WeaponProf prof, String value)
	{
		int hands;
		if ("1IFLARGERTHANWEAPON".equals(value))
		{
			hands = Constants.HANDS_SIZEDEPENDENT;
		}
		else
		{
			try
			{
				hands = Integer.parseInt(value);
			}
			catch (NumberFormatException nfe)
			{
				Logging.errorPrint("Misunderstood " + getTokenName() + ": "
					+ value);
				return false;
			}
			if (hands < 0)
			{
				Logging.errorPrint(getTokenName() + " value: " + value
					+ " must be greater than or equal to zero");
				return false;
			}
		}
		context.getObjectContext().put(prof, IntegerKey.HANDS,
			Integer.valueOf(hands));
		return true;
	}

	public String[] unparse(LoadContext context, WeaponProf prof)
	{
		Integer i =
				context.getObjectContext().getInteger(prof, IntegerKey.HANDS);
		/*
		 * Not a required Token, so it's possible it was never set. If so, don't
		 * write anything.
		 */
		if (i == null)
		{
			return null;
		}
		String hands;
		int intValue = i.intValue();
		if (intValue == Constants.HANDS_SIZEDEPENDENT)
		{
			hands = "1IFLARGERTHANWEAPON";
		}
		else if (intValue < 0)
		{
			context.addWriteMessage(getTokenName()
				+ " must be greater than or equal to zero or special value "
				+ Constants.HANDS_SIZEDEPENDENT + " for 1IFLARGERTHANWEAPON");
			return null;
		}
		else
		{
			hands = i.toString();
		}
		return new String[]{hands};
	}

	public Class<WeaponProf> getTokenClass()
	{
		return WeaponProf.class;
	}
}
