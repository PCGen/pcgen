/*
 * AbilityPool.java
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
 * Last Editor: $Author: nuance $
 * Last Edited: $Date$
 *
 */
package plugin.bonustokens;

import java.util.Collection;

import pcgen.core.AbilityCategory;
import pcgen.core.SettingsHandler;
import pcgen.core.bonus.BonusObj;


/**
 * Handles <code>BONUS:ABILITYPOOL|&lt;ability category&gt;|&lt;number&gt;
 * </code> token
 *
 * @author  boomer70 <boomer70@yahoo.com>
 * 
 * @since 5.11.1
 */
public final class AbilityPool extends BonusObj
{
	private static final String[] bonusHandled =
		{
			"ABILITYPOOL" //$NON-NLS-1$
		};

	/**
	 * @see pcgen.core.bonus.BonusObj#parseToken(java.lang.String)
	 */
	@Override
	protected boolean parseToken(final String token)
	{
		final AbilityCategory cat = SettingsHandler.getGame().getAbilityCategory(token);
		if ( cat != null )
		{
			addBonusInfo(token);
			return true;
		}

		return false;
	}

	/**
	 * @see pcgen.core.bonus.BonusObj#unparseToken(java.lang.Object)
	 */
	@Override
	protected String unparseToken(final Object obj)
	{
		return (String) obj;
	}

	/**
	 * @see pcgen.core.bonus.BonusObj#getBonusesHandled()
	 */
	@Override
	protected String[] getBonusesHandled()
	{
		return bonusHandled;
	}
}
