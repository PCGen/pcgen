package plugin.lsttokens.template;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.formula.FixedSizeFormula;
import pcgen.core.PCTemplate;
import pcgen.core.SizeAdjustment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with SIZE Token
 */
public class SizeToken implements CDOMPrimaryToken<PCTemplate>
{

	public String getTokenName()
	{
		return "SIZE";
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
	{
		SizeAdjustment size = context.ref.getAbbreviatedObject(
				SizeAdjustment.class, value);
		Formula sizeFormula;
		if (size == null)
		{
			sizeFormula = FormulaFactory.getFormulaFor(value);
		}
		else
		{
			sizeFormula = new FixedSizeFormula(size);
		}
		context.getObjectContext().put(template, FormulaKey.SIZE, sizeFormula);
		return true;
	}

	public String[] unparse(LoadContext context, PCTemplate template)
	{
		Formula res = context.getObjectContext().getFormula(template,
				FormulaKey.SIZE);
		if (res == null)
		{
			return null;
		}
		return new String[] { res.toString() };
	}

	public Class<PCTemplate> getTokenClass()
	{
		return PCTemplate.class;
	}
}
