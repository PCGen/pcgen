/*
 * Race.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on April 21, 2001, 2:15 PM
 *
 * $Id$
 */
package pcgen.core;

import java.util.ArrayList;
import java.util.List;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.ChooseDriver;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.base.ChooseSelectionActor;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.RaceSubType;
import pcgen.cdom.enumeration.RaceType;
import pcgen.facade.core.RaceFacade;

/**
 * {@code Race}.
 *
 * @author Bryan McRoberts &lt;merton_monk@users.sourceforge.net&gt;
 * @author Michael Osterlie
 */
public final class Race extends PObject implements RaceFacade, ChooseDriver
{

	/**
	 * Checks if this race's advancement is limited.
	 * 
	 * @return <tt>true</tt> if this race advances unlimitedly.
	 */
	public boolean isAdvancementUnlimited()
	{
		List<Integer> hda = getListFor(ListKey.HITDICE_ADVANCEMENT);
		return hda == null
				|| Integer.MAX_VALUE == hda.get(hda.size() - 1).intValue();
	}

	/**
	 * Overridden to only consider the race's name.
	 * @return hash code
	 */
	@Override
	public int hashCode()
	{
		return getKeyName().hashCode();
	}

	public int maxHitDiceAdvancement()
	{
		List<Integer> hda = getListFor(ListKey.HITDICE_ADVANCEMENT);
		return hda == null ? 0 : hda.get(hda.size() - 1);
	}

    @Override
	public String getSize()
	{
		Formula formula = get(FormulaKey.SIZE);
		if (formula != null)
		{
			return formula.toString();
		}
		return null;
	}
	
	@Override
	public String getRaceType()
	{
		RaceType rt = getSafe(ObjectKey.RACETYPE);
		return rt == null ? "" : rt.toString();
	}
	
	@Override
	public List<String> getRaceSubTypes()
	{
		List<String> subTypeNames = new ArrayList<>();
		List<RaceSubType> rst = getListFor(ListKey.RACESUBTYPE);
		if (rst != null)
		{
		    for (RaceSubType subtype : rst)
		    {
		    	subTypeNames.add(subtype.toString());
		    }
		}
		return subTypeNames;
	}

	@Override
	public ChooseInformation<?> getChooseInfo()
	{
		return get(ObjectKey.CHOOSE_INFO);
	}

	@Override
	public Formula getSelectFormula()
	{
		return getSafe(FormulaKey.SELECT);
	}

	@Override
	public List<ChooseSelectionActor<?>> getActors()
	{
		return getListFor(ListKey.NEW_CHOOSE_ACTOR);
	}

	@Override
	public String getFormulaSource()
	{
		return getKeyName();
	}

	@Override
	public Formula getNumChoices()
	{
		return getSafe(FormulaKey.NUMCHOICES);
	}
}
