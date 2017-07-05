/*
 * Copyright 2002 (C) Greg Bingleman <byngl@hotmail.com>
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
package plugin.bonustokens;

import pcgen.core.bonus.MultiTagBonusObj;

/**
 * Handles the BONUS:SPELL token.
 */
public final class Spell extends MultiTagBonusObj
{
	private static final String[] BONUS_TAGS = {"DC"};

	/**
	 * Return the bonus tag handled by this class.
	 * @return The bonus handled by this class.
	 */
	@Override
	public String getBonusHandled()
	{
		return "SPELL";
	}

	/**
	 * Get by index, an individual spell attribute that may be bonused.
	 * @param tagNumber the index of the spell attribute.
	 * @see pcgen.core.bonus.MultiTagBonusObj#getBonusTag(int)
	 * @return The type of SizeMOD.
	 */
	@Override
	protected String getBonusTag(final int tagNumber)
	{
		return BONUS_TAGS[tagNumber];
	}

	/**
	 * Get the number of spell attributes that may be bonused.
	 * @see pcgen.core.bonus.MultiTagBonusObj#getBonusTag(int)
	 * @return The number of spell attributes.
	 */
	@Override
	protected int getBonusTagLength()
	{
		return BONUS_TAGS.length;
	}
}
