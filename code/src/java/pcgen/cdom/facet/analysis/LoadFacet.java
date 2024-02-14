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
package pcgen.cdom.facet.analysis;

import java.math.BigDecimal;
import java.util.regex.Pattern;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.BonusCheckingFacet;
import pcgen.cdom.facet.FormulaResolvingFacet;
import pcgen.cdom.facet.PlayerCharacterTrackingFacet;
import pcgen.core.Globals;
import pcgen.core.RuleConstants;
import pcgen.core.SettingsHandler;
import pcgen.core.SizeAdjustment;
import pcgen.util.enumeration.Load;

/**
 * LoadFacet calculates information about the Load for a Player Character. The
 * underlying Load information used for these calculations is defined in the
 * Game Mode LST files.
 *
 */
public class LoadFacet
{
	private static final Formula LOADSCORE_FORMULA = FormulaFactory.getFormulaFor("LOADSCORE");

	private FormulaResolvingFacet formulaResolvingFacet;
	private TotalWeightFacet totalWeightFacet;
	private PlayerCharacterTrackingFacet pcFacet;
	private BonusCheckingFacet bonusCheckingFacet;

	/**
	 * Returns the Load for the Player Character identified by the given CharID.
	 *
	 * @param id
	 *            The CharID identifying the Player Character for which the Load
	 *            should be returned
	 * @return The Load for the Player Character identified by the given CharID
	 */
	public Load getLoadType(CharID id)
	{
		Float weight = totalWeightFacet.getTotalWeight(id);
		double dbl = weight / getMaxLoad(id).doubleValue();

		if (!Globals.checkRule(RuleConstants.SYS_LDPACSK))
		{
			return Load.LIGHT;
		}
		Float lightMult = SettingsHandler.getGameAsProperty().get().getLoadInfo().getLoadMultiplier("LIGHT");
		if (lightMult != null && dbl <= lightMult.doubleValue())
		{
			return Load.LIGHT;
		}

		Float mediumMult = SettingsHandler.getGameAsProperty().get().getLoadInfo().getLoadMultiplier("MEDIUM");
		if (mediumMult != null && dbl <= mediumMult.doubleValue())
		{
			return Load.MEDIUM;
		}

		Float heavyMult = SettingsHandler.getGameAsProperty().get().getLoadInfo().getLoadMultiplier("HEAVY");
		if (heavyMult != null && dbl <= heavyMult.doubleValue())
		{
			return Load.HEAVY;
		}

		return Load.OVERLOAD;
	}

	/**
	 * Returns the maximum Load for the Player Character identified by the given
	 * CharID.
	 *
	 * @param id
	 *            The CharID identifying the Player Character for which the
	 *            maximum Load should be returned.
	 * @return The Maximum load for the Player Character identified by the given
	 *         CharID
	 */
	public Float getMaxLoad(CharID id)
	{
		return getMaxLoad(id, 1.0);
	}

	/**
	 * Returns the maximum Load for the Player Character identified by the given
	 * CharID, multiplied by the given multiplier.
	 *
	 * @param id
	 *            The CharID identifying the Player Character for which the
	 *            maximum Load should be returned.
	 * @param mult
	 *            The multiplier by which the maximum Load will be multiplied.
	 * @return The Maximum load for the Player Character identified by the given
	 *         CharID, multiplied by the given multiplier
	 */
	public Float getMaxLoad(CharID id, double mult)
	{
		int loadScore = formulaResolvingFacet.resolve(id, LOADSCORE_FORMULA, "").intValue();
		final BigDecimal loadValue = SettingsHandler.getGameAsProperty().get().getLoadInfo().getLoadScoreValue(loadScore);
		String formula = SettingsHandler.getGameAsProperty().get().getLoadInfo().getLoadModifierFormula();
		if (formula != null)
		{
			formula = formula.replaceAll(Pattern.quote("$$SCORE$$"),
				Double.toString(loadValue.doubleValue() * mult * getLoadMultForSize(id)));
			return (float) formulaResolvingFacet.resolve(id, FormulaFactory.getFormulaFor(formula), "").intValue();
		}
		return (float) (loadValue.doubleValue() * mult * getLoadMultForSize(id));
	}

	/**
	 * Returns the Load Multiplier for the size of the Player Character
	 * identified by the given CharID.
	 *
	 * @param id
	 *            The CharID identifying the Player Character for which the Load
	 *            Multiplier will be returned.
	 * @return The Load Multiplier for the size of the Player Character
	 *         identified by the given CharID
	 */
	private double getLoadMultForSize(CharID id)
	{
		SizeAdjustment sadj = pcFacet.getPC(id).getSizeAdjustment();
		double mult = SettingsHandler.getGameAsProperty().get().getLoadInfo().getSizeAdjustment(sadj).doubleValue();
		mult += bonusCheckingFacet.getBonus(id, "LOADMULT", "TYPE=SIZE");
		return mult;
	}

	public void setFormulaResolvingFacet(FormulaResolvingFacet formulaResolvingFacet)
	{
		this.formulaResolvingFacet = formulaResolvingFacet;
	}

	public void setTotalWeightFacet(TotalWeightFacet totalWeightFacet)
	{
		this.totalWeightFacet = totalWeightFacet;
	}

	public void setPlayerCharacterTrackingFacet(PlayerCharacterTrackingFacet pcFacet)
	{
		this.pcFacet = pcFacet;
	}

	public void setBonusCheckingFacet(BonusCheckingFacet bonusCheckingFacet)
	{
		this.bonusCheckingFacet = bonusCheckingFacet;
	}

}
