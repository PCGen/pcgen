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

import java.util.List;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ListKey;
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
		DataFacetChangeListener<CDOMObject>, LevelChangeListener,
		BonusChangeListener
{
	private static final Class<SizeAdjustment> SIZEADJUSTMENT_CLASS = SizeAdjustment.class;
	private final Class<?> thisClass = getClass();

	private TemplateFacet templateFacet;
	private RaceFacet raceFacet;
	private FormulaResolvingFacet formulaResolvingFacet;
	private BonusCheckingFacet bonusCheckingFacet;
	private LevelFacet levelFacet;

	public int racialSizeInt(CharID id)
	{
		SizeFacetInfo info = getInfo(id);
		return info == null ? SizeUtilities.getDefaultSizeInt()
				: info.racialSizeInt;
	}

	private int calcRacialSizeInt(CharID id)
	{
		SizeFacetInfo info = getConstructingInfo(id);

		int iSize = SizeUtilities.getDefaultSizeInt();
		Race race = raceFacet.get(id);
		if (race != null)
		{
			// get the base size for the race
			iSize = formulaResolvingFacet.resolve(id, race.getSafe(FormulaKey.SIZE), "")
					.intValue();

			// now check and see if a template has set the
			// size of the character in question
			// with something like SIZE:L
			for (PCTemplate template : templateFacet.getSet(id))
			{
				Formula sizeFormula = template.get(FormulaKey.SIZE);
				if (sizeFormula != null)
				{
					iSize = formulaResolvingFacet.resolve(id, sizeFormula,
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
		return info == null ? SizeUtilities.getDefaultSizeInt() : info.sizeInt;
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
			iSize += (int) bonusCheckingFacet.getBonus(id, "SIZEMOD", "NUMBER");

			// Now see if there is a HD advancement in size
			// (Such as for Dragons)
			iSize += sizesToAdvance(id, race);

			//
			// Must still be be a valid size
			//
			int maxIndex = Globals.getContext().ref
					.getConstructedObjectCount(SIZEADJUSTMENT_CLASS) - 1;
			iSize = Math.min(maxIndex, Math.max(0, iSize));
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
				fireDataFacetChangeEvent(id, oldSize,
						DataFacetChangeEvent.DATA_REMOVED);
			}
			fireDataFacetChangeEvent(id, newSize,
					DataFacetChangeEvent.DATA_ADDED);
		}
	}

	int sizesToAdvance(CharID id, Race race)
	{
		return sizesToAdvance(race, levelFacet.getMonsterLevelCount(id));
	}
	
	int sizesToAdvance(Race race, int monsterLevelCount)
	{
		List<Integer> hda = race.getListFor(ListKey.HITDICE_ADVANCEMENT);
		int steps = 0;
		if (hda != null)
		{
			int limit = race.maxHitDiceAdvancement();
			for (Integer hitDie : hda)
			{
				if (monsterLevelCount <= hitDie)
				{
					break;
				}
				if (hitDie < limit)
				{
					steps++;
				}
			}
		}
		return steps;
	}

	public SizeAdjustment getSizeAdjustment(CharID id)
	{
		SizeFacetInfo info = getInfo(id);
		return info == null ? SizeUtilities.getDefaultSizeAdjustment()
				: info.sizeAdj;
	}

	public String getSizeAbb(CharID id)
	{
		return getSizeAdjustment(id).getAbbreviation();
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

	private static class SizeFacetInfo
	{
		public int sizeInt;
		public int racialSizeInt;
		public SizeAdjustment sizeAdj;
		
		@Override
		public int hashCode()
		{
			return sizeInt ^ racialSizeInt * 29;
		}

		@Override
		public boolean equals(Object o)
		{
			if (o == this)
			{
				return true;
			}
			if (o instanceof SizeFacetInfo)
			{
				SizeFacetInfo sfi = (SizeFacetInfo) o;
				return (sizeInt == sfi.sizeInt)
					&& (racialSizeInt == sfi.racialSizeInt)
					&& sizeAdj.equals(sizeAdj);
			}
			return false;
		}
	}

	@Override
	public void dataAdded(DataFacetChangeEvent<CDOMObject> dfce)
	{
		update(dfce.getCharID());
	}

	@Override
	public void dataRemoved(DataFacetChangeEvent<CDOMObject> dfce)
	{
		update(dfce.getCharID());
	}

	@Override
	public void levelChanged(LevelChangeEvent lce)
	{
		update(lce.getCharID());
	}

	@Override
	public void bonusChange(BonusChangeEvent bce)
	{
		update(bce.getCharID());
	}

	public void setTemplateFacet(TemplateFacet templateFacet)
	{
		this.templateFacet = templateFacet;
	}

	public void setRaceFacet(RaceFacet raceFacet)
	{
		this.raceFacet = raceFacet;
	}

	public void setFormulaResolvingFacet(FormulaResolvingFacet formulaResolvingFacet)
	{
		this.formulaResolvingFacet = formulaResolvingFacet;
	}

	public void setBonusCheckingFacet(BonusCheckingFacet bonusCheckingFacet)
	{
		this.bonusCheckingFacet = bonusCheckingFacet;
	}

	public void setLevelFacet(LevelFacet levelFacet)
	{
		this.levelFacet = levelFacet;
	}
}