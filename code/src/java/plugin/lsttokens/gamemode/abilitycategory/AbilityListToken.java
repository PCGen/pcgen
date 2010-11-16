/*
 * AbilityListToken.java
 * Copyright 2008 (C) James Dempsey
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
 * Created on 15/12/2008 10:35:06 PM
 *
 * $Id: $
 */
package plugin.lsttokens.gamemode.abilitycategory;

import java.util.StringTokenizer;

import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.persistence.lst.AbilityCategoryLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

/**
 * The Class <code>AbilityListToken</code> is responsible for parsing
 * the ABILITYLIST token. This allows the specific named abilities to be
 * included in a 'child' ability category. The list may also specify 
 * ability subsets, e.g. Weapon Focus(Sap) to be included.
 * <p>
 * Note: This tag is additive with the TYPE tag and may be used instead 
 * of or in addition to the TYPE tag. The abilities included in the 
 * category will be the sum of the sets defined by the two tags.
 * <p>      
 * 
 * Last Editor: $Author: $
 * Last Edited: $Date:  $
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision:  $
 */
public class AbilityListToken implements AbilityCategoryLstToken
{

	private static final Class<Ability> ABILITY_CLASS = Ability.class;

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.AbilityCategoryLstToken#parse(pcgen.core.AbilityCategory, java.lang.String)
	 */
	public boolean parse(LoadContext context, final AbilityCategory aCat, final String aValue)
	{
		final StringTokenizer tok = new StringTokenizer(aValue, "|"); //$NON-NLS-1$
		boolean errorFlagged = false;
		while (tok.hasMoreTokens())
		{
			String keyVal = tok.nextToken();
			if (aCat.isAllAbilityTypes() && !errorFlagged)
			{
				Logging.log(Logging.LST_WARNING,
					"Use of ABILITYLIST along with TYPE:* in category "
						+ aCat.getDisplayName() + " is redundant. Listed Keys "
						+ aValue + " will be ignored");
				errorFlagged = true;
			}
			
			ReferenceManufacturer<Ability> mfg = context.ref.getManufacturer(
					ABILITY_CLASS, aCat.getKeyName());
			aCat.addAbilityKey(mfg.getReference(keyVal));
		}
		return true;
	}

	/**
	 * Gets the token name.
	 * 
	 * @return the token name
	 * 
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	public String getTokenName()
	{
		return "ABILITYLIST"; //$NON-NLS-1$
	}
}
