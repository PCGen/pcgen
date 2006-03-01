/*
 * Vision.java
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
 * Created on December 13, 2002, 9:19 AM
 *
 * Current Ver: $Revision: 1.7 $
 * Last Editor: $Author: jdempsey $
 * Last Edited: $Date: 2006/01/29 00:08:05 $
 *
 */
package plugin.bonustokens;

import pcgen.core.bonus.MultiTagBonusObj;


/**
 * <code>Vision</code> deals with bonuses to a character's vision
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 */
public final class Vision extends MultiTagBonusObj
{
	private static final String[] bonusHandled =
		{
			"VISION"
		};
	private static final String[] bonusTags =
		{
			"NORMAL",
			"LOW-LIGHT",
			"DARKVISION",
			"BLINDSIGHT",
			"TREMORSENSE",
			"ECHOSENSE",
			"X-RAY"
		};

	/**
	 * @see pcgen.core.bonus.BonusObj#getBonusesHandled()
	 */
	protected String[] getBonusesHandled()
	{
		return bonusHandled;
	}

	/**
	 * @see pcgen.core.bonus.MultiTagBonusObj#getBonusTag(int)
	 */
	protected String getBonusTag(final int tagNumber)
	{
		return bonusTags[tagNumber];
	}

	/**
	 * @see pcgen.core.bonus.MultiTagBonusObj#getBonusTagLength()
	 */
	protected int getBonusTagLength()
	{
		return bonusTags.length;
	}
}
