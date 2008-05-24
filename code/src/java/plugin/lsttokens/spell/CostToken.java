package plugin.lsttokens.spell;

import java.math.BigDecimal;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.spell.Spell;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with COST Token
 */
public class CostToken implements CDOMPrimaryToken<Spell>
{

	public String getTokenName()
	{
		return "COST";
	}

	public boolean parse(LoadContext context, Spell spell, String value)
	{
		try
		{
			BigDecimal cost = new BigDecimal(value);
			if (cost.compareTo(BigDecimal.ZERO) <= 0)
			{
				Logging.errorPrint(getTokenName()
						+ " requires a positive Integer");
				return false;
			}
			context.getObjectContext().put(spell, ObjectKey.COST, cost);
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
		BigDecimal i = context.getObjectContext().getObject(spell,
				ObjectKey.COST);
		if (i == null)
		{
			return null;
		}
		if (i.compareTo(BigDecimal.ZERO) <= 00)
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
