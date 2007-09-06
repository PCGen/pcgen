/*
 * VisibleToken.java
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

import pcgen.core.AbilityCategory;
import pcgen.persistence.lst.AbilityCategoryLstToken;
import pcgen.util.Logging;

/**
 * Handles the VISIBLE token on an ABILITYCATEGORY line.
 * 
 * @author boomer70 <boomer70@yahoo.com>
 * 
 * @since 5.11.1
 */
public class VisibleToken implements AbilityCategoryLstToken
{
	/**
	 * @see pcgen.persistence.lst.AbilityCategoryLstToken#parse(pcgen.core.AbilityCategory, java.lang.String)
	 */
	public boolean parse(final AbilityCategory aCat, final String aValue)
	{
		if ((aValue.length() > 0) && (aValue.charAt(0) == 'Y'))
		{
			if (!aValue.equals("YES"))
			{
				Logging
					.deprecationPrint("Abbreviation used in VISIBLE in AbilityCategory");
				Logging.deprecationPrint(" " + aValue
					+ " is not a valid value for VISIBLE");
				Logging
					.deprecationPrint(" Valid values in AbilityCategory are NO, QUALIFY and YES");
				Logging
					.deprecationPrint(" assuming you meant YES, please use YES (exact String, upper case) in the LST file");
				Logging.deprecationPrint(" This will break after PCGen 5.14");
			}
			aCat.setVisible(AbilityCategory.VISIBLE_YES);
		}
		else if ((aValue.length() > 0) && (aValue.charAt(0) == 'Q'))
		{
			if (!aValue.equals("QUALIFY"))
			{
				Logging
					.deprecationPrint("Abbreviation used in VISIBLE in AbilityCategory");
				Logging.deprecationPrint(" " + aValue
					+ " is not a valid value for VISIBLE");
				Logging
					.deprecationPrint(" Valid values in AbilityCategory are NO, QUALIFY and YES");
				Logging
					.deprecationPrint(" assuming you meant QUALIFY, please use QUALIFY (exact String, upper case) in the LST file");
				Logging.deprecationPrint(" This will break after PCGen 5.14");
			}
			aCat.setVisible(AbilityCategory.VISIBLE_QUALIFIED);
		}
		else if ((aValue.length() > 0) && (aValue.charAt(0) == 'N'))
		{
			if (!aValue.equals("NO")) {
				Logging
					.deprecationPrint("Abbreviation used in VISIBLE in AbilityCategory");
				Logging.deprecationPrint(" " + aValue
					+ " is not a valid value for VISIBLE");
				Logging
					.deprecationPrint(" Valid values in AbilityCategory are NO, QUALIFY and YES");
				Logging
					.deprecationPrint(" assuming you meant NO, please use NO (exact String, upper case) in the LST file");
				Logging.deprecationPrint(" This will break after PCGen 5.14");
			}
			aCat.setVisible(AbilityCategory.VISIBLE_NO);
		}
		else
		{
			return false;
		}
		return true;
	}

	/**
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	public String getTokenName()
	{
		return "VISIBLE"; //$NON-NLS-1$
	}

}
