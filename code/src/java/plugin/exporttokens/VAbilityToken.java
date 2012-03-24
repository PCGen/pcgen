/*
 * VAbilityToken.java
 * Copyright 2006 (C) James Dempsey
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
 * Created on 21 Nov 2006
 *
 * $$Id: $$
 */

package plugin.exporttokens;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.cdom.enumeration.Nature;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.io.exporttoken.AbilityToken;

/**
 * <code>VAbilityToken</code> deals with the VABILITY output 
 * token.
 *
 * Last Editor: $Author:  $
 * Last Edited: $Date:  $
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: $
 */
public class VAbilityToken extends AbilityToken
{
	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	@Override
	public String getTokenName()
	{
		return "VABILITY";
	}

	/**
	 * @see pcgen.io.exporttoken.AbilityToken#getAbilityList(pcgen.core.PlayerCharacter, pcgen.core.AbilityCategory)
	 */
	@Override
	protected List<Ability> getAbilityList(PlayerCharacter pc,
		final AbilityCategory aCategory)
	{
		final List<Ability> abilityList = new ArrayList<Ability>();
		Collection<AbilityCategory> allCats =
				SettingsHandler.getGame().getAllAbilityCategories();
		for (AbilityCategory aCat : allCats)
		{
			if (aCat.getParentCategory().equals(aCategory))
			{
				abilityList.addAll(pc.getAbilityList(aCat, Nature.VIRTUAL));
			}
		}
		return abilityList;
	}
}
