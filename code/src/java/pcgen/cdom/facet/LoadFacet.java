package pcgen.cdom.facet;

import java.util.regex.Pattern;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.CharID;
import pcgen.core.SizeAdjustment;
import pcgen.core.SystemCollections;
import pcgen.util.enumeration.Load;

public class LoadFacet
{
	private static final Formula LOADSCORE_FORMULA = FormulaFactory
			.getFormulaFor("LOADSCORE");
	private static final FormulaResolvingFacet resolveFacet = FacetLibrary
			.getFacet(FormulaResolvingFacet.class);
	private static final TotalWeightFacet totalWeightFacet = FacetLibrary
			.getFacet(TotalWeightFacet.class);
	private static final SizeFacet sizeFacet = FacetLibrary
			.getFacet(SizeFacet.class);
	private BonusCheckingFacet bonusFacet = FacetLibrary
			.getFacet(BonusCheckingFacet.class);

	public Load getLoadType(CharID id)
	{
		Float weight = totalWeightFacet.getTotalWeight(id);
		double dbl = weight / getMaxLoad(id).doubleValue();

		Float lightMult = SystemCollections.getLoadInfo().getLoadMultiplier(
				"LIGHT");
		if (lightMult != null && dbl <= lightMult.doubleValue())
		{
			return Load.LIGHT;
		}

		Float mediumMult = SystemCollections.getLoadInfo().getLoadMultiplier(
				"MEDIUM");
		if (mediumMult != null && dbl <= mediumMult.doubleValue())
		{
			return Load.MEDIUM;
		}

		Float heavyMult = SystemCollections.getLoadInfo().getLoadMultiplier(
				"HEAVY");
		if (heavyMult != null && dbl <= heavyMult.doubleValue())
		{
			return Load.HEAVY;
		}

		return Load.OVERLOAD;
	}

	public Float getMaxLoad(CharID id)
	{
		return getMaxLoad(id, 1.0);
	}

	public Float getMaxLoad(CharID id, double mult)
	{
		int loadScore = resolveFacet.resolve(id, LOADSCORE_FORMULA, "")
				.intValue();
		final Float loadValue = SystemCollections.getLoadInfo()
				.getLoadScoreValue(loadScore);
		String formula = SystemCollections.getLoadInfo()
				.getLoadModifierFormula();
		if (formula.length() != 0)
		{
			formula = formula.replaceAll(Pattern.quote("$$SCORE$$"), Double
					.toString(loadValue.doubleValue() * mult
							* getLoadMultForSize(id)));
			return (float) resolveFacet.resolve(id,
					FormulaFactory.getFormulaFor(formula), "").intValue();
		}
		return new Float(loadValue.doubleValue() * mult
				* getLoadMultForSize(id));
	}

	public double getLoadMultForSize(CharID id)
	{
		SizeAdjustment sadj = sizeFacet.getSizeAdjustment(id);
		double mult = sadj.getLoadMultiplier();
		mult += bonusFacet.getBonus(id, "LOADMULT", "TYPE=SIZE");
		return mult;
	}

}
