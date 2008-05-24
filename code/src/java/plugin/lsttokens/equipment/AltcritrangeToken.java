package plugin.lsttokens.equipment;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.inst.EquipmentHead;
import pcgen.core.Equipment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deals with ALTCRITRANGE token
 */
public class AltcritrangeToken implements CDOMPrimaryToken<Equipment>
{

	public String getTokenName()
	{
		return "ALTCRITRANGE";
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		try
		{
			Integer cr = Integer.valueOf(value);
			if (cr.intValue() < 0)
			{
				Logging.errorPrint(getTokenName() + " cannot be < 0");
				return false;
			}
			context.getObjectContext().put(eq.getEquipmentHead(2),
					IntegerKey.CRIT_RANGE, cr);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			Logging.deprecationPrint(getTokenName() + " expected "
					+ "an integer.  " + "Tag should be of the form: "
					+ getTokenName() + ":<int>");
			Logging.deprecationPrint("   ...Assuming Zero");
			context.getObjectContext().put(eq.getEquipmentHead(2),
					IntegerKey.CRIT_RANGE, 0);
			return true;
			// Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
			// + " expected an integer. " + "Tag must be of the form: "
			// + getTokenName() + ":<int>");
			// return false;
		}
	}

	public String[] unparse(LoadContext context, Equipment eq)
	{
		EquipmentHead head = eq.getEquipmentHeadReference(2);
		if (head == null)
		{
			return null;
		}
		Integer mult = context.getObjectContext().getInteger(head,
				IntegerKey.CRIT_RANGE);
		if (mult == null)
		{
			return null;
		}
		return new String[] { mult.toString() };
	}

	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}
}
