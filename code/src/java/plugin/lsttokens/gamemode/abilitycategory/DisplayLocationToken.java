/*
 * DisplayLocationToken.java
 * Copyright 2007 (C) James Dempsey <jdemspey@users.sourceforge.net>
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
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package plugin.lsttokens.gamemode.abilitycategory;

import pcgen.core.AbilityCategory;
import pcgen.persistence.lst.AbilityCategoryLstToken;

/**
 * Handles the DISPLAYLOCATION token on an ABILITYCATEGORY line.
 * 
 * @author James Dempsey <jdemspey@users.sourceforge.net>
 * 
 * @since 5.13.0
 */
public class DisplayLocationToken implements AbilityCategoryLstToken
{

	/**
	 * @see pcgen.persistence.lst.AbilityCategoryLstToken#parse(pcgen.core.AbilityCategory, java.lang.String)
	 */
	public boolean parse(final AbilityCategory aCat, final String aValue)
	{
		aCat.setDisplayLocation(aValue);
		return true;
	}

	/**
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	public String getTokenName()
	{
		return "DISPLAYLOCATION"; //$NON-NLS-1$
	}
}
