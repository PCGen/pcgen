/*
 * Skill.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
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
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PersistentTransitionChoice;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Region;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.analysis.SkillRankControl;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;

/**
 * <code>Skill</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision$
 */
public final class Skill extends PObject
{

    /** Constructor */
	public Skill()
	{
		// Empty Constructor
	}

	/**
	 * Get an iterator for the sub types
	 * 
	 * @return iterator for the sub types
	 */
	public Iterator<Type> getSubtypeIterator()
	{
		List<Type> ret = getSafeListFor(ListKey.TYPE);
		PCStat keystat = get(ObjectKey.KEY_STAT);
		if (keystat == null)
		{
			ret.remove(Type.NONE);
		}
		else
		{
			// skip the keystat
			ret.remove(Type.getConstant(keystat.getDisplayName()));
			/*
			 * TODO This is magic, and makes tremendous assumptions about the
			 * DATA - BAD BAD BAD
			 */
		}
		return ret.iterator();
	}

	@Override
	public Skill clone()
	{
		Skill newSkill = null;

		try
		{
			newSkill = (Skill) super.clone();
		}
		catch (CloneNotSupportedException exc)
		{
			ShowMessageDelegate.showMessageDialog(exc.getMessage(),
					Constants.s_APPNAME, MessageType.ERROR);
		}

		return newSkill;
	}

	/**
	 * Get the bonus to a skill rank
	 * 
	 * @param aPC
	 * @return bonus to skill rank
	 */
	public double getSkillRankBonusTo(PlayerCharacter aPC)
	{
		double bonus = aPC.getTotalBonusTo("SKILLRANK", getKeyName());
		for (Type singleType : getTrueTypeList(false))
		{
			bonus += aPC.getTotalBonusTo("SKILLRANK", "TYPE." + singleType);
		}

		updateAdds(aPC, bonus);

		return bonus;
	}

	private void updateAdds(PlayerCharacter aPC, double bonus)
	{
		// Check for ADDs
		List<PersistentTransitionChoice<?>> adds = getListFor(ListKey.ADD);
		if (adds != null)
		{
			int iCount = 0;
			for (PersistentTransitionChoice<?> ptc : adds)
			{
				iCount += aPC.getAssocCount(ptc, AssociationListKey.ADD);
			}

			if (CoreUtility.doublesEqual(SkillRankControl.getRank(aPC, this).doubleValue() + bonus,
					0.0))
			{
				//
				// There was a total (because we've applied the ADD's, but now
				// there isn't.
				// Need to remove the ADDed items
				//
				if (iCount != 0)
				{
					removeAdds(aPC);
				}
			}
			else
			{
				//
				// There wasn't a total (because we haven't applied the ADDs),
				// but now there is
				// Need to apply the ADDed items
				//
				if (iCount == 0)
				{
					addAdds(aPC);
				}
			}
		}
	}

	@Override
	protected void globalChecks(final boolean flag, final PlayerCharacter aPC)
	{
		aPC.setDirty(true);
		for (TransitionChoice<Kit> kit : getSafeListFor(ListKey.KIT_CHOICE))
		{
			kit.act(kit.driveChoice(aPC), this, aPC);
		}
		TransitionChoice<Region> region = get(ObjectKey.REGION_CHOICE);
		if (region != null)
		{
			region.act(region.driveChoice(aPC), this, aPC);
		}

		if (flag)
		{
			getChoices(getSafe(StringKey.CHOICE_STRING), aPC);
		}
		activateBonuses(aPC);
	}

	/**
	 * Get the key attribute's description
	 * 
	 * @return description
	 */
	public String getKeyStatFromStats()
	{
		PCStat stat = get(ObjectKey.KEY_STAT);
		if (stat == null)
		{
			if (Globals.getGameModeHasPointPool())
			{
				List<PCStat> statList = getKeyStatList(null);
				StringBuilder sb = new StringBuilder();
				boolean needSlash = false;
				for (PCStat s : statList)
				{
					if (needSlash)
					{
						sb.append('/');
					}
					sb.append(s.getAbb());
				}
				return sb.toString();
			}
			else
			{
				return "";
			}
		}
		else
		{
			return stat.getAbb();
		}
	}

	/**
	 * Get a list of PCStat's that apply a SKILL bonus to this skill. Generates
	 * (optionally, if typeList is non-null) a list of String's types
	 * 
	 * @param typeList
	 * @return List of stats that apply
	 */
	public List<PCStat> getKeyStatList(List<Type> typeList)
	{
		List<PCStat> aList = new ArrayList<PCStat>();
		if (Globals.getGameModeHasPointPool())
		{
			for (Type aType : getTrueTypeList(false))
			{
				List<PCStat> statList = SettingsHandler.getGame()
						.getUnmodifiableStatList();
				for (int idx = statList.size() - 1; idx >= 0; --idx)
				{
					final PCStat stat = statList.get(idx);
					//
					// Get a list of all BONUS:SKILL|TYPE.<type>|x for this
					// skill that would come from current stat
					//
					List<BonusObj> bonusList = getBonusListOfType(stat, Bonus
							.getBonusTypeFromName("SKILL"), "TYPE." + aType);
					if (bonusList.size() > 0)
					{
						for (int iCount = bonusList.size() - 1; iCount >= 0; --iCount)
						{
							aList.add(stat);
						}
						if ((typeList != null) && !typeList.contains(aType))
						{
							typeList.add(aType);
						}
					}
				}
			}
		}
		return aList;
	}

	//
	// Get a list of all BonusObj's from passed stat that apply a bonus of the
	// passed type and name
	//
	private static List<BonusObj> getBonusListOfType(final PCStat aStat,
			final int iType, final String aName)
	{
		final List<BonusObj> aList = new ArrayList<BonusObj>();

		for (BonusObj bonus : aStat.getSafeListFor(ListKey.BONUS))
		{
			if (bonus.getTypeOfBonusAsInt() != iType)
			{
				continue;
			}

			if (bonus.getBonusInfoList().size() > 1)
			{
				final StringTokenizer aTok = new StringTokenizer(bonus
						.getBonusInfo(), ",");

				while (aTok.hasMoreTokens())
				{
					final String aBI = aTok.nextToken();

					if (aBI.equalsIgnoreCase(aName))
					{
						aList.add(bonus);
					}
				}
			}
			else if (bonus.getBonusInfo().equalsIgnoreCase(aName))
			{
				aList.add(bonus);
			}
		}

		return aList;
	}

	public String getKeyStatAbb()
	{
		PCStat keyStat = get(ObjectKey.KEY_STAT);
		return keyStat == null ? "" : keyStat.getAbb();
	}
}
