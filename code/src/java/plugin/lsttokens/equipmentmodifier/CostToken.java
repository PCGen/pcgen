package plugin.lsttokens.equipmentmodifier;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.core.EquipmentModifier;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Deals with COST token
 */
public class CostToken implements CDOMPrimaryToken<EquipmentModifier>
{

	public String getTokenName()
	{
		return "COST";
	}

	public boolean parse(LoadContext context, EquipmentModifier mod,
			String value)
	{
		context.getObjectContext().put(mod, FormulaKey.COST,
				FormulaFactory.getFormulaFor(value));
		return true;
	}

	public String[] unparse(LoadContext context, EquipmentModifier mod)
	{
		Formula f = context.getObjectContext().getFormula(mod, FormulaKey.COST);
		if (f == null)
		{
			return null;
		}
		return new String[] { f.toString() };
	}

	public Class<EquipmentModifier> getTokenClass()
	{
		return EquipmentModifier.class;
	}
}
