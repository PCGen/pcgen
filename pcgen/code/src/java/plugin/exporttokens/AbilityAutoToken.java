/*
 * AbilityAutoToken.java
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
 */

package plugin.exporttokens;

import java.util.Collection;

import pcgen.base.util.HashMapToList;
import pcgen.base.util.MapToList;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.enumeration.Nature;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.io.exporttoken.AbilityToken;

/**
 * {@code AbilityAutoToken} deals with the ABILITYAUTO output
 * token.
 */
public class AbilityAutoToken extends AbilityToken
{

	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	@Override
	public String getTokenName()
	{
		return "ABILITYAUTO";
	}

	/**
	 * @see pcgen.io.exporttoken.AbilityToken#getAbilityList(pcgen.core.PlayerCharacter, pcgen.core.AbilityCategory)
	 */
	@Override
	protected MapToList<Ability, CNAbility> getAbilityList(PlayerCharacter pc,
		final AbilityCategory aCategory)
	{
		final MapToList<Ability, CNAbility> listOfAbilities = new HashMapToList<>();
		Collection<AbilityCategory> allCats =
				SettingsHandler.getGame().getAllAbilityCategories();
		for (AbilityCategory aCat : allCats)
		{
			if (AbilityCategory.ANY.equals(aCategory) || aCat.getParentCategory().equals(aCategory))
			{
				for (CNAbility cna : pc.getPoolAbilities(aCat, Nature.AUTOMATIC))
				{
					listOfAbilities.addToListFor(cna.getAbility(), cna);
				}
			}
		}
		return listOfAbilities;
	}

	@Override
	protected Nature getTargetNature()
	{
		return Nature.AUTOMATIC;
	}
	
}
