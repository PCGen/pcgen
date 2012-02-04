/*
 * RaceTypeFilter.java
 * Copyright 2007 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 * Created on November 27, 2007
 */
package pcgen.gui.filter;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.RaceType;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.system.LanguageBundle;

/**
 * <code>RaceTypeFilter</code> is a filter for races by race type.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
final class RaceTypeFilter extends AbstractPObjectFilter
{
	private RaceType raceType;

	/**
	 * Create a new RaceTypeFilter for a race type name
	 * @param raceTypeName The name to be matched.
	 */
	RaceTypeFilter(RaceType raceTypeName)
	{
		super(LanguageBundle.getString("in_filterRaceType"), raceTypeName.toString()); //$NON-NLS-1$
		raceType = raceTypeName;
	}

	
	/* (non-Javadoc)
	 * @see pcgen.gui.filter.AbstractPObjectFilter#accept(pcgen.core.PlayerCharacter, pcgen.core.PObject)
	 */
	@Override
	public boolean accept(PlayerCharacter aPC, PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		if (pObject instanceof Race)
		{
			return raceType.equals(pObject.get(ObjectKey.RACETYPE));
		}

		return true;
	}
}
