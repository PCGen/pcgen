package plugin.lsttokens.equipmentmodifier;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.EquipmentModifier;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deals with COSTDOUBLE token
 */
public class CostdoubleToken implements CDOMPrimaryToken<EquipmentModifier>
{

	public String getTokenName()
	{
		return "COSTDOUBLE";
	}

	public boolean parse(LoadContext context, EquipmentModifier mod,
			String value)
	{
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
		context.getObjectContext().put(mod, ObjectKey.COST_DOUBLE, set);
		return true;
	}

	public String[] unparse(LoadContext context, EquipmentModifier mod)
	{
		Boolean stacks = context.getObjectContext().getObject(mod,
				ObjectKey.COST_DOUBLE);
		if (stacks == null)
		{
			return null;
		}
		return new String[] { stacks.booleanValue() ? "YES" : "NO" };
	}

	public Class<EquipmentModifier> getTokenClass()
	{
		return EquipmentModifier.class;
	}
}
