/*
 * LockedStat.java
 * Copyright 2013 (C) James Dempsey
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
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package plugin.bonustokens;

import pcgen.core.Globals;
import pcgen.core.PCStat;
import pcgen.core.bonus.BonusObj;
import pcgen.core.bonus.util.MissingObject;
import pcgen.rules.context.LoadContext;

/**
 * This is the class that implements the LockedStat bonuses.
 * BONUS:LOCKEDSTAT|x|y
 * 
 * <br/>
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public final class LockedStat extends BonusObj
{
	@Override
	protected boolean parseToken(LoadContext context, final String token)
	{
		PCStat stat = context.ref.getAbbreviatedObject(PCStat.class, token);

		if (stat != null)
		{
			addBonusInfo(stat);
		}
		else
		{
			addBonusInfo(new MissingObject(token));
		}

		return true;
	}

	@Override
	protected String unparseToken(final Object obj)
	{
		if (obj instanceof MissingObject)
		{
			return ((MissingObject) obj).getObjectName();
		}

		return ((PCStat) obj).getAbb();
	}

	/**
	 * Return the bonus tag handled by this class.
	 * @return The bonus handled by this class.
	 */
	@Override
	public String getBonusHandled()
	{
		return "LOCKEDSTAT";
	}

	@Override
	public String getDescription()
	{
		final PCStat pcstat =
				Globals.getContext().ref.getAbbreviatedObject(PCStat.class,
					getBonusInfo());
		if (pcstat != null)
		{
			return pcstat.getName() + " (locked)";
		}
		return super.getDescription();
	}
	
	
}
