package plugin.lsttokens.equipmentmodifier;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.EquipmentModifier;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deals with PLUS token
 */
public class PlusToken implements CDOMPrimaryToken<EquipmentModifier>
{

	public String getTokenName()
	{
		return "PLUS";
	}

	public boolean parse(LoadContext context, EquipmentModifier mod,
			String value)
	{
		try
		{
			Integer plus = Integer.valueOf(value);
			if (plus.intValue() == 0)
			{
				Logging.errorPrint(getTokenName()
						+ " must be an integer not equal to 0");
				return false;
			}
			context.getObjectContext().put(mod, IntegerKey.PLUS, plus);
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

	public String[] unparse(LoadContext context, EquipmentModifier mod)
	{
		Integer plus = context.getObjectContext().getInteger(mod,
				IntegerKey.PLUS);
		if (plus == null)
		{
			return null;
		}
		if (plus.intValue() == 0)
		{
			context.addWriteMessage(getTokenName()
					+ " must be an integer not equal to 0");
			return null;
		}
		return new String[] { plus.toString() };
	}

	public Class<EquipmentModifier> getTokenClass()
	{
		return EquipmentModifier.class;
	}
}
