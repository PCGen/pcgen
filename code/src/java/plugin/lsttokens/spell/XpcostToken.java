package plugin.lsttokens.spell;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.spell.Spell;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with XPCOST Token
 */
public class XpcostToken implements CDOMPrimaryToken<Spell>
{

	/**
	 * Get the token name
	 * 
	 * @return token name
	 */
	public String getTokenName()
	{
		return "XPCOST";
	}

	public boolean parse(LoadContext context, Spell spell, String value)
	{
		try
		{
			Integer xpCost = Integer.valueOf(value);
			if (xpCost.intValue() < 0)
			{
				Logging.errorPrint(getTokenName()
						+ " requires a positive Integer");
				return false;
			}
			context.getObjectContext().put(spell, IntegerKey.XP_COST, xpCost);
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
				IntegerKey.XP_COST);
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
