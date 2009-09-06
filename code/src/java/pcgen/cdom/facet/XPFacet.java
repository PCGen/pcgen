/*
 * Copyright (c) Thomas Parker, 2009.
 * 
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

import pcgen.base.formula.Formula;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.CharID;
import pcgen.core.Globals;
import pcgen.core.LevelInfo;
import pcgen.util.Logging;

public class XPFacet
{

	private final Class<?> thisClass = getClass();

	private LevelFacet levelFacet = FacetLibrary.getFacet(LevelFacet.class);
	private FormulaResolvingFacet resolveFacet = FacetLibrary
			.getFacet(FormulaResolvingFacet.class);

	public void setEarnedXP(CharID id, int earnedXP)
	{
		FacetCache.set(id, thisClass, earnedXP);
	}

	public Integer getEarnedXP(CharID id)
	{
		Integer earnedXP = (Integer) FacetCache.get(id, thisClass);
		return earnedXP == null ? 0 : earnedXP;
	}

	/**
	 * Returns the number of experience points needed for level
	 * 
	 * @param level
	 *            character level to calculate experience for
	 * @param id
	 *            the ID of the PC that we are asking about (ECL of character
	 *            can affect the result)
	 * 
	 * @return The experience points needed
	 */
	public int minXPForLevel(int level, CharID id)
	{
		LevelInfo lInfo = Globals.getLevelInfo().get(String.valueOf(level));

		if (lInfo == null)
		{
			lInfo = Globals.getLevelInfo().get("LEVEL");
		}

		if ((level > 0) && (lInfo != null))
		{
			Formula f = FormulaFactory.getFormulaFor(lInfo
					.getMinXPVariable(level));
			return resolveFacet.resolve(id, f, "").intValue();
		}
		// do something sensible if no level info
		return 0;
	}

	public int getXP(CharID id)
	{
		// Add the effect of LEVELADJ when
		// showing our external notion of XP.
		return getEarnedXP(id) + getLAXP(id);
	}

	private int getLAXP(CharID id)
	{
		// Why +1? Adjustments are deltas, not absolute
		// levels, so are not subject to the "back off one"
		// element of the * algorithm in minXPForLevel. This
		// still means that levelAdjustment of 0 gives you 0
		// XP, but we need LA of 1 to give us 1,000 XP.
		return minXPForLevel(levelFacet.getLevelAdjustment(id) + 1, id);
	}

	public void setXP(CharID id, int xp)
	{
		// Remove the effect of LEVELADJ when storing our
		// internal notion of experience
		int realXP = xp - getLAXP(id);

		if (realXP < 0)
		{
			Logging.errorPrint("ERROR: too little experience: " + realXP);
			realXP = 0;
		}

		setEarnedXP(id, realXP);
	}

}
