package plugin.lsttokens.template;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.core.PCTemplate;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with LEVELADJUSTMENT Token
 */
public class LeveladjustmentToken implements CDOMPrimaryToken<PCTemplate>
{

	public String getTokenName()
	{
		return "LEVELADJUSTMENT";
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
	{
		context.getObjectContext().put(template, FormulaKey.LEVEL_ADJUSTMENT,
				FormulaFactory.getFormulaFor(value));
		return true;
	}

	public String[] unparse(LoadContext context, PCTemplate pct)
	{
		Formula f = context.getObjectContext().getFormula(pct,
				FormulaKey.LEVEL_ADJUSTMENT);
		if (f == null)
		{
			return null;
		}
		return new String[] { f.toString() };
	}

	public Class<PCTemplate> getTokenClass()
	{
		return PCTemplate.class;
	}
}
