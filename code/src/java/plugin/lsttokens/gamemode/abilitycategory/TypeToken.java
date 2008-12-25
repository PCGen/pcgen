/*
 * TypeToken.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 * Last Editor: $Author: $
 * Last Edited: $Date$
 */
package plugin.lsttokens.gamemode.abilitycategory;

import java.util.StringTokenizer;

import pcgen.core.AbilityCategory;
import pcgen.persistence.lst.AbilityCategoryLstToken;
import pcgen.util.Logging;

/**
 * Handles the TYPE token on an ABILITYCATEGORY line.
 * 
 * @author boomer70 <boomer70@yahoo.com>
 * 
 * @since 5.11.1
 */
public class TypeToken implements AbilityCategoryLstToken
{

	/**
	 * @see pcgen.persistence.lst.AbilityCategoryLstToken#parse(pcgen.core.AbilityCategory, java.lang.String)
	 */
	public boolean parse(final AbilityCategory aCat, final String aValue)
	{
		final StringTokenizer tok = new StringTokenizer(aValue, "."); //$NON-NLS-1$
		boolean errorFlagged = false;
		while (tok.hasMoreTokens())
		{
			String typeVal = tok.nextToken();
			if ("*".equals(typeVal))
			{
				if (!aCat.getAbilityTypes().isEmpty() && !errorFlagged)
				{
					Logging.log(Logging.LST_WARNING,
						"Use of named types along with TYPE:* in category "
							+ aCat.getDisplayName()
							+ " is redundant. Named types " + aCat.getAbilityTypes() + " will be ignored");
					errorFlagged = true;
				}
				if (!aCat.getAbilityKeys().isEmpty() && !errorFlagged)
				{
					Logging.log(Logging.LST_WARNING,
						"Use of ABILITYLIST along with TYPE:* in category "
							+ aCat.getDisplayName()
							+ " is redundant. Listed Keys "
							+ aCat.getAbilityKeys() + " will be ignored");
					errorFlagged = true;
				}
				aCat.setAllAbilityTypes(true);
			}
			else
			{
				aCat.addAbilityType(typeVal);
				if (aCat.isAllAbilityTypes() && !errorFlagged)
				{
					Logging.log(Logging.LST_WARNING,
						"Use of named types along with TYPE:* in category "
							+ aCat.getDisplayName()
							+ " is redundant. Named types " + aCat.getAbilityTypes() + "will be ignored");
					errorFlagged = true;
				}
			}
		}
		return true;
	}

	/**
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	public String getTokenName()
	{
		return "TYPE"; //$NON-NLS-1$
	}
}
