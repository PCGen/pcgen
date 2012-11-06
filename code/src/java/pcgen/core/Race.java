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
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.Gender;
import pcgen.cdom.enumeration.Handed;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.RaceSubType;
import pcgen.cdom.enumeration.RaceType;
import pcgen.core.facade.GenderFacade;
import pcgen.core.facade.HandedFacade;
import pcgen.core.facade.RaceFacade;
import pcgen.core.facade.util.DefaultListFacade;
import pcgen.core.facade.util.ListFacade;

/**
 * <code>Race</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @author Michael Osterlie
 * @version $Revision$
 */
public final class Race extends PObject implements RaceFacade
{

	private static final DefaultListFacade<GenderFacade> genderList =
			new DefaultListFacade<GenderFacade>();
	private static final DefaultListFacade<HandedFacade> handList =
			new DefaultListFacade<HandedFacade>();

	static
	{
		Gender[] genders = Gender.values();
		for (final Gender gender : genders)
		{
			genderList.addElement(gender);
		}
		for (Handed handed : Handed.values())
		{
			handList.addElement(handed);
		}
	}

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
	public ListFacade<GenderFacade> getGenders()
	{
		return genderList;
	}

    @Override
	public ListFacade<HandedFacade> getHands()
	{
		return handList;
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
	public String getMovement()
	{
		List<Movement> movements = getListFor(ListKey.MOVEMENT);
		if (movements != null && !movements.isEmpty())
		{
			return movements.get(0).toString();
		}
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
    @Override
	public String getRaceType()
	{
		RaceType rt = getSafe(ObjectKey.RACETYPE);
		return rt == null ? "" : rt.toString();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getRaceSubTypes()
	{
		List<String> subTypeNames = new ArrayList<String>();
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
}
