package plugin.lsttokens.equipmentmodifier;

import pcgen.cdom.enumeration.EqModNameOpt;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.EquipmentModifier;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deals with NAMEOPT token
 */
public class NameoptToken implements CDOMPrimaryToken<EquipmentModifier>
{

	public String getTokenName()
	{
		return "NAMEOPT";
	}

	public boolean parse(LoadContext context, EquipmentModifier mod,
			String value)
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " cannot be empty");
			return false;
		}
		String optString = value;
		if (optString.startsWith("TEXT"))
		{
			if (optString.length() < 6 || optString.charAt(4) != '=')
			{
				Logging.errorPrint(getTokenName()
						+ " has invalid TEXT argument: " + value);
				return false;
			}
			optString = "TEXT";
			context.getObjectContext().put(mod, StringKey.NAME_TEXT,
					value.substring(5));
		}
		try
		{
			context.getObjectContext().put(mod, ObjectKey.NAME_OPT,
					EqModNameOpt.valueOf(optString));
			return true;
		}
		catch (IllegalArgumentException iae)
		{
			Logging.errorPrint("Invalid Naming Option provided in "
					+ getTokenName() + ": " + value);
			return false;
		}
	}

	public String[] unparse(LoadContext context, EquipmentModifier mod)
	{
		EqModNameOpt opt = context.getObjectContext().getObject(mod,
				ObjectKey.NAME_OPT);
		String text = context.getObjectContext().getString(mod,
				StringKey.NAME_TEXT);
		if (opt == null)
		{
			if (text == null)
			{
				return null;
			}
			else
			{
				context.addWriteMessage("Cannot have both NAME_TEXT without "
						+ "NAME_OPT in EquipmentModifier");
				return null;
			}
		}
		String retString;
		if (opt.equals(EqModNameOpt.TEXT))
		{
			if (text == null)
			{
				context.addWriteMessage("Must have NAME_TEXT with "
						+ "NAME_OPT TEXT in EquipmentModifier");
				return null;
			}
			else
			{
				retString = "TEXT=" + text;
			}
		}
		else
		{
			if (text == null)
			{
				retString = opt.toString();
			}
			else
			{
				context.addWriteMessage("Cannot have NAME_TEXT without "
						+ "NAME_OPT TEXT in EquipmentModifier");
				return null;
			}
		}
		return new String[] { retString };
	}

	public Class<EquipmentModifier> getTokenClass()
	{
		return EquipmentModifier.class;
	}
}
