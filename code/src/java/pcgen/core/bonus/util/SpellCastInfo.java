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
package pcgen.core.bonus.util;

/**
 * Extracted from SpellCastMult, SpellKnown and SpellCast.
 */
public class SpellCastInfo
{
	private String classString = "";
	private String level;
	private String type;

	/**
	 * Constructor
	 * @param argType
	 * @param argLevel
	 */
	public SpellCastInfo(final String argType, final String argLevel)
	{
		if (argType.startsWith("TYPE"))
		{
			type = argType.substring(5);
		}
		else if (argType.startsWith("CLASS"))
		{
			classString = argType.substring(6);
		}
		level = argLevel;
	}

	/**
	 * Get the level
	 * @return level
	 */
	public String getLevel()
	{
		return level;
	}

	/**
	 * Get the PC Class name
	 * @return the PC Class name
	 */
	public String getPcClassName()
	{
		return classString;
	}

	/**
	 * Get the type
	 * @return type
	 */
	public String getType()
	{
		return type;
	}
}
