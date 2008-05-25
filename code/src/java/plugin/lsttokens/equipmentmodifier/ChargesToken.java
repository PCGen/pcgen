package plugin.lsttokens.equipmentmodifier;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.EquipmentModifier;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deals with CHARGES token
 */
public class ChargesToken implements CDOMPrimaryToken<EquipmentModifier>
{

	public String getTokenName()
	{
		return "CHARGES";
	}

	public boolean parse(LoadContext context, EquipmentModifier mod,
			String value)
	{
		int pipeLoc = value.indexOf(Constants.PIPE);
		if (pipeLoc == -1)
		{
			Logging
					.errorPrint(getTokenName()
							+ " has no | : must be of format <min charges>|<max charges>: "
							+ value);
			return false;
		}
		if (value.lastIndexOf(Constants.PIPE) != pipeLoc)
		{
			Logging
					.errorPrint(getTokenName()
							+ " has two | : must be of format <min charges>|<max charges>: "
							+ value);
			return false;
		}
		String minChargeString = value.substring(0, pipeLoc);
		int minCharges;
		try
		{
			minCharges = Integer.parseInt(minChargeString);
			if (minCharges < 0)
			{
				Logging.errorPrint(getTokenName()
						+ " min charges must be >= zero: " + value);
				return false;
			}
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint(getTokenName()
					+ " min charges is not an integer: " + value);
			return false;
		}

		String maxChargeString = value.substring(pipeLoc + 1);
		int maxCharges;
		try
		{
			maxCharges = Integer.parseInt(maxChargeString);
			/*
			 * No need to test max for negative, since min was tested and there
			 * is a later test for max >= min
			 */
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint(getTokenName()
					+ " max charges is not an integer: " + value);
			return false;
		}

		if (minCharges > maxCharges)
		{
			Logging.errorPrint(getTokenName()
					+ " max charges must be >= min charges: " + value);
			return false;
		}

		context.getObjectContext().put(mod, IntegerKey.MIN_CHARGES,
				Integer.valueOf(minCharges));
		context.getObjectContext().put(mod, IntegerKey.MAX_CHARGES,
				Integer.valueOf(maxCharges));
		return true;
	}

	public String[] unparse(LoadContext context, EquipmentModifier mod)
	{
		Integer max = context.getObjectContext().getInteger(mod,
				IntegerKey.MAX_CHARGES);
		Integer min = context.getObjectContext().getInteger(mod,
				IntegerKey.MIN_CHARGES);
		if (max == null && min == null)
		{
			return null;
		}
		if (max == null || min == null)
		{
			context
					.addWriteMessage("EquipmentModifier requires both MAX_CHARGES and MIN_CHARGES for "
							+ getTokenName() + " if one of the two is present");
			return null;
		}
		int minInt = min.intValue();
		if (minInt < 0)
		{
			context
					.addWriteMessage("EquipmentModifier requires MIN_CHARGES be > 0");
			return null;
		}
		if (max.intValue() < minInt)
		{
			context
					.addWriteMessage("EquipmentModifier requires MAX_CHARGES be "
							+ "greater than MIN_CHARGES for " + getTokenName());
			return null;
		}
		return new String[] { min + Constants.PIPE + max };
	}

	public Class<EquipmentModifier> getTokenClass()
	{
		return EquipmentModifier.class;
	}
}
