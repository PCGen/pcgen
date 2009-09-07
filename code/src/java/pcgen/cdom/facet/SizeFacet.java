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
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.facet.BonusChangeFacet.BonusChangeEvent;
import pcgen.cdom.facet.BonusChangeFacet.BonusChangeListener;
import pcgen.cdom.facet.LevelFacet.LevelChangeEvent;
import pcgen.cdom.facet.LevelFacet.LevelChangeListener;
import pcgen.core.Globals;
import pcgen.core.PCTemplate;
import pcgen.core.Race;
import pcgen.core.SizeAdjustment;
import pcgen.core.analysis.SizeUtilities;

public class SizeFacet extends AbstractDataFacet<SizeAdjustment> implements
		DataFacetChangeListener<CDOMObject>, LevelChangeListener, BonusChangeListener
{
	private static final Class<SizeAdjustment> SIZEADJUSTMENT_CLASS = SizeAdjustment.class;
	private final Class<?> thisClass = getClass();

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
		SizeFacetInfo info = getInfo(id);
		return info == null ? 0 : info.racialSizeInt;
	}

	public int calcRacialSizeInt(CharID id)
	{
		SizeFacetInfo info = getConstructingInfo(id);

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
		info.racialSizeInt = iSize;
		return iSize;
	}

	public int sizeInt(CharID id)
	{
		SizeFacetInfo info = getInfo(id);
		return info == null ? 0 : info.sizeInt;
	}

	public void update(CharID id)
	{
		SizeFacetInfo info = getConstructingInfo(id);
		int iSize = calcRacialSizeInt(id);

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

		info.sizeInt = iSize;
		SizeAdjustment oldSize = info.sizeAdj;
		SizeAdjustment newSize = Globals.getContext().ref.getItemInOrder(
				SIZEADJUSTMENT_CLASS, sizeInt(id));
		info.sizeAdj = newSize;
		if (oldSize != newSize)
		{
			if (oldSize != null)
			{
				fireDataFacetChangeEvent(id, oldSize, DataFacetChangeEvent.DATA_REMOVED);
			}
			fireDataFacetChangeEvent(id, newSize, DataFacetChangeEvent.DATA_ADDED);
		}
	}

	public SizeAdjustment getSizeAdjustment(CharID id)
	{
		SizeFacetInfo info = getInfo(id);
		return info == null ? SizeUtilities.getDefaultSizeAdjustment()
				: info.sizeAdj;
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

	private SizeFacetInfo getConstructingInfo(CharID id)
	{
		SizeFacetInfo rci = getInfo(id);
		if (rci == null)
		{
			rci = new SizeFacetInfo();
			FacetCache.set(id, thisClass, rci);
		}
		return rci;
	}

	private SizeFacetInfo getInfo(CharID id)
	{
		return (SizeFacetInfo) FacetCache.get(id, thisClass);
	}

	private class SizeFacetInfo
	{
		public int sizeInt;
		public int racialSizeInt;
		public SizeAdjustment sizeAdj;
	}

	public void dataAdded(DataFacetChangeEvent<CDOMObject> dfce)
	{
		update(dfce.getCharID());
	}

	public void dataRemoved(DataFacetChangeEvent<CDOMObject> dfce)
	{
		update(dfce.getCharID());
	}

	public void levelChanged(LevelChangeEvent lce)
	{
		update(lce.getCharID());
	}

	public void bonusChange(BonusChangeEvent bce)
	{
		update(bce.getCharID());
	}
}