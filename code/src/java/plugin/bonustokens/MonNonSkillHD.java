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
 * $Id$
 *
 */
package plugin.bonustokens;

import pcgen.core.bonus.MultiTagBonusObj;

/**
 * <code>MonNonSkillHD</code> defines the MonNonSkillHD tag which
 * allows the LST code to specify how many of a monster's hit dice
 * do not gain skills.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author	James Dempsey <jdempsey@users.sourceforge.net>
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision$
 */

public final class MonNonSkillHD extends MultiTagBonusObj
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

	protected String[] getBonusesHandled()
	{
		return bonusHandled;
	}

	protected String getBonusTag(final int tagNumber)
	{
		return bonusTags[tagNumber];
	}

	protected int getBonusTagLength()
	{
		return bonusTags.length;
	}
}
