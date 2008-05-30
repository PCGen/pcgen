package plugin.lsttokens.pcclass;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.core.PCClass;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with CRFORMULA Token
 */
public class CrformulaToken implements CDOMPrimaryToken<PCClass>
{

	public String getTokenName()
	{
		return "CRFORMULA";
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
	{
		context.getObjectContext().put(pcc, FormulaKey.CR,
				FormulaFactory.getFormulaFor(value));
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		Formula f = context.getObjectContext().getFormula(pcc, FormulaKey.CR);
		if (f == null)
		{
			return null;
		}
		return new String[] { f.toString() };
	}

	public Class<PCClass> getTokenClass()
	{
		return PCClass.class;
	}
}
