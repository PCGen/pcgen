package plugin.lsttokens.statsandchecks.stat;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.core.PCStat;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ErrorParsingWrapper;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with STATMOD Token
 */
public class StatmodToken extends ErrorParsingWrapper<PCStat> implements CDOMPrimaryToken<PCStat>
{

	public String getTokenName()
	{
		return "STATMOD";
	}

	public ParseResult parseToken(LoadContext context, PCStat stat, String value)
	{
		if (value == null || value.length() == 0)
		{
			return new ParseResult.Fail(getTokenName() + " arguments may not be empty");
		}
		Formula formula = FormulaFactory.getFormulaFor(value);
		if (!formula.isValid())
		{
			return new ParseResult.Fail("Formula in " + getTokenName()
					+ " was not valid: " + formula.toString());
		}
		context.getObjectContext().put(stat, FormulaKey.STAT_MOD, formula);
		return ParseResult.SUCCESS;
	}

	public String[] unparse(LoadContext context, PCStat stat)
	{
		Formula target = context.getObjectContext().getFormula(stat,
			FormulaKey.STAT_MOD);
		if (target == null)
		{
			return null;
		}
		return new String[] { target.toString() };
	}

	public Class<PCStat> getTokenClass()
	{
		return PCStat.class;
	}
}
