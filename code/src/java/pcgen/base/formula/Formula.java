/*
 * Copyright (c) 2006 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.base.formula;

import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * A Formula is a mathematical formula which requires a context to resolve.
 */
public interface Formula
{
	public Number resolve(PlayerCharacter pc, String source);

	public boolean isStatic();

	public Number resolve(Equipment equipment, boolean primary,
			PlayerCharacter apc, String string);
}
