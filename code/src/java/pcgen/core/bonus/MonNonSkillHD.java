/*
 * MonNonSkillHD.java
 * Copyright 2004 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 * Created on Oct 16, 2004
 *
 * $Id: MonNonSkillHD.java,v 1.4 2005/11/13 20:01:22 binkley Exp $
 *
 */
package pcgen.core.bonus;

/**
 * <code>MonNonSkillHD</code> defines the MonNonSkillHD tag which
 * allows the LST code to specify how many of a monster's hit dice
 * do not gain skills.
 *
 * Last Editor: $Author: binkley $
 * Last Edited: $Date: 2005/11/13 20:01:22 $
 *
 * @author	James Dempsey <jdempsey@users.sourceforge.net>
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.4 $
 */

final class MonNonSkillHD extends MultiTagBonusObj
{
	private static final String[] bonusHandled =
		{
			"MONNONSKILLHD"
		};
	private static final String[] bonusTags =
		{
			"NUMBER",
			"LOCKNUMBER"
		};

	String[] getBonusesHandled()
	{
		return bonusHandled;
	}

	String getBonusTag(final int tagNumber)
	{
		return bonusTags[tagNumber];
	}

	int getBonusTagLength()
	{
		return bonusTags.length;
	}
}
