/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.facet;

import java.math.BigDecimal;
import java.util.regex.Pattern;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.CharID;
import pcgen.core.SettingsHandler;
import pcgen.core.SizeAdjustment;
import pcgen.util.enumeration.Load;

public class LoadFacet
{
	private static final Formula LOADSCORE_FORMULA = FormulaFactory
			.getFormulaFor("LOADSCORE");
	private final FormulaResolvingFacet resolveFacet = FacetLibrary
			.getFacet(FormulaResolvingFacet.class);
	private final TotalWeightFacet totalWeightFacet = FacetLibrary
			.getFacet(TotalWeightFacet.class);
	private final SizeFacet sizeFacet = FacetLibrary.getFacet(SizeFacet.class);
	private final BonusCheckingFacet bonusFacet = FacetLibrary
			.getFacet(BonusCheckingFacet.class);

	public Load getLoadType(CharID id)
	{
		Float weight = totalWeightFacet.getTotalWeight(id);
		double dbl = weight / getMaxLoad(id).doubleValue();

		Float lightMult = SettingsHandler.getGame().getLoadInfo()
				.getLoadMultiplier("LIGHT");
		if (lightMult != null && dbl <= lightMult.doubleValue())
		{
			return Load.LIGHT;
		}

		Float mediumMult = SettingsHandler.getGame().getLoadInfo()
				.getLoadMultiplier("MEDIUM");
		if (mediumMult != null && dbl <= mediumMult.doubleValue())
		{
			return Load.MEDIUM;
		}

		Float heavyMult = SettingsHandler.getGame().getLoadInfo()
				.getLoadMultiplier("HEAVY");
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
		final BigDecimal loadValue = SettingsHandler.getGame().getLoadInfo()
				.getLoadScoreValue(loadScore);
		String formula = SettingsHandler.getGame().getLoadInfo()
				.getLoadModifierFormula();
		if (formula != null)
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
		double mult =
				SettingsHandler.getGame().getLoadInfo().getSizeAdjustment(sadj)
					.doubleValue();
		mult += bonusFacet.getBonus(id, "LOADMULT", "TYPE=SIZE");
		return mult;
	}

}
