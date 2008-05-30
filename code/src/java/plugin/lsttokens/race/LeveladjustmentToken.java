package plugin.lsttokens.race;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.core.Race;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with LEVELADJUSTMENT Token
 */
public class LeveladjustmentToken implements CDOMPrimaryToken<Race>
{

	public String getTokenName()
	{
		return "LEVELADJUSTMENT";
	}

	public boolean parse(LoadContext context, Race race, String value)
	{
		context.getObjectContext().put(race, FormulaKey.LEVEL_ADJUSTMENT,
				FormulaFactory.getFormulaFor(value));
		return true;
	}

	public String[] unparse(LoadContext context, Race race)
	{
		Formula f = context.getObjectContext().getFormula(race,
				FormulaKey.LEVEL_ADJUSTMENT);
		if (f == null)
		{
			return null;
		}
		return new String[] { f.toString() };
	}

	public Class<Race> getTokenClass()
	{
		return Race.class;
	}
}
