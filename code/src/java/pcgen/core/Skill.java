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

import pcgen.cdom.enumeration.ObjectKey;

/**
 * <code>Skill</code>.
 * 
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision$
 */
public final class Skill extends PObject
{
	@Override
	public void globalChecks(PlayerCharacter aPC)
	{
		doBaseChecks(aPC);
		activateBonuses(aPC);
	}

	public String getKeyStatAbb()
	{
		PCStat keyStat = get(ObjectKey.KEY_STAT);
		return keyStat == null ? "" : keyStat.getAbb();
	}
}
