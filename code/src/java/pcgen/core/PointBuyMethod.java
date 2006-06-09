/*
 * PointBuyMethod.java
 * Copyright 2002 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on August 17, 2002, 11:45 PM
 *
 * $Id$
 */
package pcgen.core;

import pcgen.core.bonus.BonusObj;
import pcgen.core.bonus.BonusUtilities;
import pcgen.core.prereq.PrereqHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * <code>PointBuyMethod</code>.
 *
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision$
 */
public final class PointBuyMethod
{
	private String methodName = "";
	private String pointFormula = "0";
	private ArrayList<BonusObj> bonusList = null;

	public PointBuyMethod(final String argMethodName, final String argPointFormula)
	{
		methodName = argMethodName;
		pointFormula = argPointFormula;
	}

	public String getMethodName()
	{
		return methodName;
	}

	public String getPointFormula()
	{
		return pointFormula;
	}

	public void setPointFormula(final String argFormula)
	{
		pointFormula = argFormula;
	}

	public String toString()
	{
		return methodName;
	}

	public String getDescription()
	{
		String desc = methodName;
		if (!pointFormula.equals("0"))
		{
			desc += " (" + pointFormula + ')';
		}
		return desc;
	}

	public void addBonusList(final BonusObj aBonus)
	{
		if (bonusList == null)
		{
			bonusList = new ArrayList<BonusObj>();
		}
		bonusList.add(aBonus);
	}

	public List<BonusObj> getBonusList()
	{
		return bonusList;
	}

	public List<BonusObj> getBonusListOfType(final String aType, final String aName)
	{
		return BonusUtilities.getBonusFromList(getBonusList(), aType, aName);
	}

	/**
	 * returns all BonusObj's that are "active"
	 * @return active bonuses
	 */
	public List<BonusObj> getActiveBonuses()
	{
		final List<BonusObj> aList = new ArrayList<BonusObj>();

		List<BonusObj> aBonusList = getBonusList();
		if (aBonusList != null)
		{
			for (Iterator<BonusObj> ab = aBonusList.iterator(); ab.hasNext();)
			{
				final BonusObj aBonus = ab.next();

				if (aBonus.isApplied())
				{
					aList.add(aBonus);
				}
			}
		}

		return aList;
	}

	/**
	 * Sets all the BonusObj's to "active"
	 * @param aPC
	 */
	public void activateBonuses(final PlayerCharacter aPC)
	{
		List aBonusList = getBonusList();
		if (aBonusList == null)
		{
			return;
		}
		for (Iterator ab = aBonusList.iterator(); ab.hasNext();)
		{
			final BonusObj aBonus = (BonusObj) ab.next();
			aBonus.setApplied(false);

			if (aBonus.hasPreReqs())
			{
				//TODO: This is a hack to avoid VARs etc in feat defs being qualified
				// for when Bypass feat prereqs is selected. Should we be passing in
				// the BonusObj here to allow it to be referenced in Qualifies statements?
				if (PrereqHandler.passesAll(aBonus.getPrereqList(), aPC, null))
				{
					aBonus.setApplied(true);
				}
				else
				{
					aBonus.setApplied(false);
				}
			}
			else
			{
				aBonus.setApplied(true);
			}
		}
	}
/*
	public void deactivateBonuses()
	{
		if (bonusList != null)
		{
			for (Iterator ab = getBonusList().iterator(); ab.hasNext();)
			{
				final BonusObj aBonus = (BonusObj) ab.next();
				aBonus.setApplied(false);
			}
		}
	}
*/
}
