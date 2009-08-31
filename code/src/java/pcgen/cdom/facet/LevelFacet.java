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
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;

public class LevelFacet
{
	private ClassFacet classFacet = FacetLibrary.getFacet(ClassFacet.class);
	private TemplateFacet templateFacet = FacetLibrary
			.getFacet(TemplateFacet.class);
	private RaceFacet raceFacet = FacetLibrary.getFacet(RaceFacet.class);
	private FormulaResolvingFacet resolveFacet = FacetLibrary
			.getFacet(FormulaResolvingFacet.class);

	public int getNonMonsterLevelCount(CharID id)
	{
		int totalLevels = 0;

		for (PCClass pcClass : classFacet.getClassSet(id))
		{
			if (!pcClass.isMonster())
			{
				totalLevels += classFacet.getLevel(id, pcClass);
			}
		}

		return totalLevels;
	}

	public int getMonsterLevelCount(CharID id)
	{
		int totalLevels = 0;

		for (PCClass pcClass : classFacet.getClassSet(id))
		{
			if (pcClass.isMonster())
			{
				totalLevels += classFacet.getLevel(id, pcClass);
			}
		}

		return totalLevels;
	}

	public int getLevelAdjustment(CharID id)
	{
		Formula raceLA = raceFacet.get(id).getSafe(FormulaKey.LEVEL_ADJUSTMENT);
		int levelAdj = resolveFacet.resolve(id, raceLA, "").intValue();

		for (PCTemplate template : templateFacet.getSet(id))
		{
			Formula templateLA = template.getSafe(FormulaKey.LEVEL_ADJUSTMENT);
			levelAdj += resolveFacet.resolve(id, templateLA, "").intValue();
		}

		return levelAdj;
	}

	public int getECL(CharID id)
	{
		return getNonMonsterLevelCount(id) + getMonsterLevelCount(id)
				+ getLevelAdjustment(id);
	}

	public int getTotalLevels(CharID id)
	{
		int totalLevels = 0;

		totalLevels += getNonMonsterLevelCount(id);

		// Monster hit dice count towards total levels
		// sage_sam changed 03 Dec 2002 for Bug #646816
		totalLevels += getMonsterLevelCount(id);

		return totalLevels;
	}

}
