package plugin.lsttokens.spell;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.Globals;
import pcgen.core.spell.Spell;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with PPCOST Token
 */
public class PpcostToken implements CDOMPrimaryToken<Spell>
{

	public String getTokenName()
	{
		return "PPCOST";
	}

	public boolean parse(LoadContext context, Spell spell, String value)
	{
		try
		{
			Integer ppCost = Integer.valueOf(value);
			if (ppCost.intValue() < 0)
			{
				Logging.errorPrint(getTokenName()
						+ " requires a positive Integer");
				return false;
			}
			context.getObjectContext().put(spell, IntegerKey.PP_COST, ppCost);
			Globals.setSpellPPCost(true);
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

	public String[] unparse(LoadContext context, Spell spell)
	{
		Integer i = context.getObjectContext().getInteger(spell,
				IntegerKey.PP_COST);
		if (i == null)
		{
			return null;
		}
		if (i.intValue() < 0)
		{
			context.addWriteMessage(getTokenName()
					+ " requires a positive Integer");
			return null;
		}
		return new String[] { i.toString() };
	}

	public Class<Spell> getTokenClass()
	{
		return Spell.class;
	}
}
