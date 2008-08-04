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

import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.helper.StatLock;
import pcgen.core.bonus.BonusObj;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;

/**
 * <code>Race</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @author Michael Osterlie
 * @version $Revision$
 */
public final class Race extends PObject
{
	private String favoredClass = Constants.EMPTY_STRING;
	private String monsterClass = null;
	private int monsterClassLevels = 0;
	
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

	public void setBonusInitialFeats(final BonusObj bon)
	{
		addBonusList(bon);
	}

	public String getDisplayVision(final PlayerCharacter aPC)
	{
		if (aPC == null)
		{
			return "";
		}
		Collection<CDOMReference<Vision>> mods = getListMods(Vision.VISIONLIST);
		if (mods == null)
		{
			return "";
		}

		StringBuilder visionString = new StringBuilder(25);
		for (CDOMReference<Vision> ref : mods)
		{
			for (Vision v : ref.getContainedObjects())
			{
				if (visionString.length() > 0)
				{
					visionString.append(';');
				}
				visionString.append(v.toString(aPC));
			}
		}

		return visionString.toString();
	}

	public Point2D.Double getFace()
	{
		BigDecimal width = get(ObjectKey.FACE_WIDTH);
		BigDecimal height = get(ObjectKey.FACE_HEIGHT);
		if (width == null && height == null)
		{
			return null;
		}
		return new Point2D.Double(width.doubleValue(), height.doubleValue());
	}

	public void setFavoredClass(final String newClass)
	{
		favoredClass = newClass;
	}

	public String getFavoredClass()
	{
		return favoredClass;
	}

	public void setMonsterClass(final String string)
	{
		monsterClass = string;
	}

	public String getMonsterClass()
	{
		return monsterClass;
	}

	public void setMonsterClassLevels(final int num)
	{
		monsterClassLevels = num;
	}

	public int getMonsterClassLevels()
	{
		return monsterClassLevels;
	}

	public boolean isNonAbility(final int statIdx)
	{
		final List<PCStat> statList =
				SettingsHandler.getGame().getUnmodifiableStatList();

		if ((statIdx < 0) || (statIdx >= statList.size()))
		{
			return true;
		}

		// An unlock will always override a lock, so check it first
		if (isUnlocked(statIdx))
		{
			return false;
		}

		PCStat stat = statList.get(statIdx);
		for (StatLock sl : getSafeListFor(ListKey.STAT_LOCKS))
		{
			if (sl.getLockedStat().equals(stat))
			{
				if (sl.getLockValue().toString().equals("10"))
				{
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Takes an integer input which it uses to access Games mode's 
	 * "statlist" array. Test if that stat has been unlocked via a 
	 * DEFINE|UNLOCK 
	 * 
	 * @param statIdx
	 *            index number of the stat in question
	 * 
	 * @return Whether this has been unlocked
	 */
	public boolean isUnlocked(final int statIdx)
	{
		final List<PCStat> statList =
				SettingsHandler.getGame().getUnmodifiableStatList();

		if ((statIdx < 0) || (statIdx >= statList.size()))
		{
			return false;
		}

		return containsInList(ListKey.UNLOCKED_STATS, statList.get(statIdx));
	}

	/**
	 * Retrieve Unarmed Damage according to the Race
	 * @return UDAM damage die (ie 1d3)
	 */
	public String getUdam()
	{
		/*
		 * TODO This has pc == null, which could be a problem
		 */
		final int iSize = getSafe(FormulaKey.SIZE).resolve(null, "").intValue();
		final SizeAdjustment defAdj =
				SettingsHandler.getGame().getDefaultSizeAdjustment();
		final SizeAdjustment sizAdj =
				SettingsHandler.getGame().getSizeAdjustmentAtIndex(iSize);
		if ((defAdj != null) && (sizAdj != null))
		{
			return Globals.adjustDamage("1d3", defAdj.getAbbreviation(), sizAdj
				.getAbbreviation());
		}
		return "1d3";
	}

	/**
	 * Produce a tailored PCC output, used for saving custom races.
	 * @return PCC Text
	 */
	@Override
	public String getPCCText()
	{
		// 29 July 2003 : sage_sam corrected order
		final StringBuffer txt = new StringBuffer(super.getPCCText());

		txt.append("\t");
		txt.append(StringUtil.joinToStringBuffer(Globals.getContext().unparse(
				this), "\t"));
		txt.append("\t");

		if ((favoredClass != null) && (favoredClass.length() > 0))
		{
			txt.append("\tFAVCLASS:").append(favoredClass);
		}

		if ((getChooseLanguageAutos() != null)
			&& (getChooseLanguageAutos().length() > 0))
		{
			txt.append("\tCHOOSE:LANGAUTO:").append(getChooseLanguageAutos());
		}

		if ((getNaturalWeapons() != null) && (getNaturalWeapons().size() > 0))
		{
			final StringBuffer buffer = new StringBuffer();

			for (Equipment natEquip : getNaturalWeapons())
			{
				if (buffer.length() != 0)
				{
					buffer.append('|');
				}

				String eqName = natEquip.getName();
				int index = eqName.indexOf(" (Natural/Primary)");

				if (index >= 0)
				{
					eqName =
							eqName.substring(0, index)
								+ eqName.substring(index
									+ " (Natural/Primary)".length());
				}

				index = eqName.indexOf(" (Natural/Secondary)");

				if (index >= 0)
				{
					eqName =
							eqName.substring(0, index)
								+ eqName.substring(index
									+ " (Natural/Secondary)".length());
				}

				buffer.append(eqName).append(',');
				buffer.append(natEquip.getType(false)).append(',');

				if (!natEquip.getSafe(ObjectKey.ATTACKS_PROGRESS))
				{
					buffer.append('*');
				}

				buffer
					.append(
						(int) natEquip.bonusTo(null, "WEAPON", "ATTACKS", true) + 1)
					.append(',');
				buffer.append(natEquip.getDamage(null));
			}

			txt.append("\tNATURALATTACKS:").append(buffer.toString());
		}

		if (monsterClass != null && !"(None)".equals(monsterClass))
		{
			txt.append("\tMONSTERCLASS:").append(monsterClass);
			txt.append(':').append(monsterClassLevels);
		}

		return txt.toString();
	}

	@Override
	public Race clone()
	{
		Race aRace = null;

		try
		{
			aRace = (Race) super.clone();
			aRace.favoredClass = favoredClass;
		}
		catch (CloneNotSupportedException exc)
		{
			ShowMessageDelegate.showMessageDialog(exc.getMessage(),
				Constants.s_APPNAME, MessageType.ERROR);
		}

		return aRace;
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

	boolean canBeAlignment(final String aString)
	{
		if (hasPrerequisites())
		{
			for (Prerequisite prereq : getPrerequisiteList())
			{
				if ("ALIGN".equalsIgnoreCase(prereq.getKind()))
				{
					String alignStr = aString;
					final String[] aligns =
							SettingsHandler.getGame().getAlignmentListStrings(
								false);
					try
					{
						final int align = Integer.parseInt(alignStr);
						alignStr = aligns[align];
					}
					catch (NumberFormatException ex)
					{
						// Do Nothing
					}
					String desiredAlignment = prereq.getKey();
					try
					{
						final int align = Integer.parseInt(desiredAlignment);
						desiredAlignment = aligns[align];
					}
					catch (NumberFormatException ex)
					{
						// Do Nothing
					}

					return desiredAlignment.equalsIgnoreCase(alignStr);
				}
			}
		}

		return true;
	}

	int maxHitDiceAdvancement()
	{
		List<Integer> hda = getListFor(ListKey.HITDICE_ADVANCEMENT);
		return hda == null ? 0 : hda.get(hda.size() - 1);
	}

	int sizesAdvanced(final int HD)
	{
		List<Integer> hda = getListFor(ListKey.HITDICE_ADVANCEMENT);
		if (hda == null)
		{
			return 0;
		}
		else
		{
			int steps = 0;
			for (Integer hitDie : hda)
			{
				if (HD <= hitDie)
				{
					break;
				}
				steps++;
			}
			return steps;
		}
	}
}
