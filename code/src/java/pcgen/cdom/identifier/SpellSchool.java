/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.identifier;

import pcgen.cdom.base.Identified;

public class SpellSchool implements Identified
{

	private String name;

	public String getDisplayName()
	{
		return name;
	}

	public String getKeyName()
	{
		return name;
	}

	public String getLSTformat()
	{
		return name;
	}

	public boolean isInternal()
	{
		return false;
	}

	public boolean isType(String type)
	{
		return false;
	}

	public void setName(String s)
	{
		name = s;
	}

	public String toString()
	{
		return name;
	}

	@Override
	public boolean equals(Object other)
	{
		return other instanceof SpellSchool
			&& name.equals(((SpellSchool) other).name);
	}

	@Override
	public int hashCode()
	{
		return name.hashCode();
	}

}
