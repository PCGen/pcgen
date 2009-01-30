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
import java.util.List;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
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

	/**
	 * Retrieve Unarmed Damage according to the Race
	 * @param pc TODO
	 * @return UDAM damage die (ie 1d3)
	 */
	public String getUdam(PlayerCharacter pc)
	{
		final int iSize = getSafe(FormulaKey.SIZE).resolve(pc, "").intValue();
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

	@Override
	public Race clone()
	{
		Race aRace = null;

		try
		{
			aRace = (Race) super.clone();
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
	
	@Override
	public PObject getActiveEquivalent(PlayerCharacter pc)
	{
		if (pc.getRace().getKeyName().equals(getKeyName()))
		{
			return pc.getRace();
		}
		return this;
	}

}
