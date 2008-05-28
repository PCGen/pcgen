package plugin.lsttokens.race;

import pcgen.base.formula.Formula;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.formula.FixedSizeFormula;
import pcgen.core.Race;
import pcgen.core.SizeAdjustment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with SIZE Token
 */
public class SizeToken implements CDOMPrimaryToken<Race>
{

	public String getTokenName()
	{
		return "SIZE";
	}

	public boolean parse(LoadContext context, Race race, String value)
	{
		SizeAdjustment size = context.ref.getAbbreviatedObject(
				SizeAdjustment.class, value);
		Formula sizeFormula;
		if (size == null)
		{
			Logging.errorPrint("Error parsing " + getTokenName() + ": " + value
					+ " is not a Size for this Game Mode");
			return false;
			//sizeFormula = FormulaFactory.getFormulaFor(value);
		}
		else
		{
			sizeFormula = new FixedSizeFormula(size);
		}
		context.getObjectContext().put(race, FormulaKey.SIZE, sizeFormula);
		return true;
	}

	public String[] unparse(LoadContext context, Race race)
	{
		Formula res = context.getObjectContext().getFormula(race,
				FormulaKey.SIZE);
		if (res == null)
		{
			return null;
		}
		return new String[] { res.toString() };
	}

	public Class<Race> getTokenClass()
	{
		return Race.class;
	}
}
