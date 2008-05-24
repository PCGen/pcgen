package plugin.lsttokens.equipment;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.inst.EquipmentHead;
import pcgen.core.Equipment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deals with ALTCRITMULT token
 */
public class AltcritmultToken implements CDOMPrimaryToken<Equipment>
{

	public String getTokenName()
	{
		return "ALTCRITMULT";
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		Integer cm = null;
		if ((value.length() > 0) && (value.charAt(0) == 'x'))
		{
			try
			{
				cm = Integer.valueOf(value.substring(1));
				if (cm.intValue() <= 0)
				{
					Logging.errorPrint(getTokenName() + " cannot be <= 0");
					return false;
				}
			}
			catch (NumberFormatException nfe)
			{
				Logging.errorPrint(getTokenName()
						+ " was expecting an Integer: " + value);
				return false;
			}
		}
		else if ("-".equals(value))
		{
			cm = Integer.valueOf(-1);
		}
		if (cm == null)
		{
			Logging.errorPrint(getTokenName()
					+ " was expecting x followed by an integer "
					+ "or the special value '-' (representing no value)");
			return false;
		}
		EquipmentHead altHead = eq.getEquipmentHead(2);
		context.getObjectContext().put(altHead, IntegerKey.CRIT_MULT, cm);
		return true;
	}

	public String[] unparse(LoadContext context, Equipment eq)
	{
		EquipmentHead head = eq.getEquipmentHeadReference(2);
		if (head == null)
		{
			return null;
		}
		Integer mult = context.getObjectContext().getInteger(head,
				IntegerKey.CRIT_MULT);
		if (mult == null)
		{
			return null;
		}
		int multInt = mult.intValue();
		String retString;
		if (multInt == -1)
		{
			retString = "-";
		}
		else
		{
			retString = "x" + multInt;
		}
		return new String[] { retString };
	}

	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}
}
