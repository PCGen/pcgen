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
import pcgen.core.Globals;
import pcgen.core.PCTemplate;
import pcgen.core.Race;
import pcgen.core.SizeAdjustment;

public class SizeFacet
{
	private static final Class<SizeAdjustment> SIZEADJUSTMENT_CLASS = SizeAdjustment.class;

	private TemplateFacet templateFacet = FacetLibrary
			.getFacet(TemplateFacet.class);
	private RaceFacet raceFacet = FacetLibrary.getFacet(RaceFacet.class);
	private FormulaResolvingFacet resolveFacet = FacetLibrary
			.getFacet(FormulaResolvingFacet.class);
	private BonusCheckingFacet bonusFacet = FacetLibrary
			.getFacet(BonusCheckingFacet.class);
	private LevelFacet levelFacet = FacetLibrary.getFacet(LevelFacet.class);

	public int racialSizeInt(CharID id)
	{
		int iSize = 0;

		Race race = raceFacet.get(id);
		if (race != null)
		{
			// get the base size for the race
			iSize = resolveFacet.resolve(id, race.getSafe(FormulaKey.SIZE), "")
					.intValue();

			// now check and see if a template has set the
			// size of the character in question
			// with something like SIZE:L
			for (PCTemplate template : templateFacet.getSet(id))
			{
				Formula sizeFormula = template.get(FormulaKey.SIZE);
				if (sizeFormula != null)
				{
					iSize = resolveFacet.resolve(id, sizeFormula,
							template.getKeyName()).intValue();
				}
			}
		}

		return iSize;
	}

	public int sizeInt(CharID id)
	{
		int iSize = racialSizeInt(id);

		Race race = raceFacet.get(id);
		if (race != null)
		{
			// Now check and see if a class has modified
			// the size of the character with something like:
			// BONUS:SIZEMOD|NUMBER|+1
			iSize += (int) bonusFacet.getBonus(id, "SIZEMOD", "NUMBER");

			// Now see if there is a HD advancement in size
			// (Such as for Dragons)
			for (int i = 0; i < race.sizesAdvanced(levelFacet
					.getMonsterLevelCount(id)); ++i)
			{
				++iSize;
			}

			//
			// Must still be between 0 and 8
			//
			if (iSize < 0)
			{
				iSize = 0;
			}

			int maxIndex = Globals.getContext().ref
					.getConstructedObjectCount(SIZEADJUSTMENT_CLASS) - 1;
			if (iSize > maxIndex)
			{
				iSize = maxIndex;
			}
		}

		return iSize;
	}

	public SizeAdjustment getSizeAdjustment(CharID id)
	{
		return Globals.getContext().ref.getItemInOrder(SIZEADJUSTMENT_CLASS,
				sizeInt(id));
	}

	public String getSizeAbb(CharID id)
	{
		final SizeAdjustment sa = getSizeAdjustment(id);

		if (sa != null)
		{
			return sa.getAbbreviation();
		}

		return " ";
	}
}