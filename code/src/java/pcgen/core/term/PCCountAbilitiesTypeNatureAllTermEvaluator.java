/**
 * Copyright (c) 2008 Andrew Wilson <nuance@users.sourceforge.net>.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created 06-Aug-2008 23:12:32
 */

package pcgen.core.term;

import java.util.Collection;

import pcgen.cdom.content.CNAbility;
import pcgen.cdom.enumeration.Nature;
import pcgen.core.AbilityCategory;
import pcgen.core.PlayerCharacter;

public class PCCountAbilitiesTypeNatureAllTermEvaluator extends BasePCCountAbilitiesTypeTermEvaluator
		implements TermEvaluator
{
	public PCCountAbilitiesTypeNatureAllTermEvaluator(String originalText, AbilityCategory abCat, String[] types,
		boolean visible, boolean hidden)
	{
		this.originalText = originalText;
		this.abCat = abCat;
		this.types = types;
		this.visible = visible;
		this.hidden = hidden;
	}

	@Override
	Collection<CNAbility> getAbilities(PlayerCharacter pc)
	{
		return pc.getPoolAbilities(abCat, Nature.NORMAL);
	}

	@Override
	public boolean isSourceDependant()
	{
		return false;
	}

	public boolean isStatic()
	{
		return false;
	}
}
