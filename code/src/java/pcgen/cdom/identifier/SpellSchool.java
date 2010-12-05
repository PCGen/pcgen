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

import java.net.URI;

import pcgen.cdom.base.Loadable;

public class SpellSchool implements Loadable, Comparable<SpellSchool>
{

	private URI sourceURI;
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

	public void setName(String newName)
	{
		name = newName;
	}

	public void setKeyName(String key)
	{
		name = key;
	}

	@Override
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

	public int compareTo(SpellSchool other)
	{
		return name.compareTo(other.name);
	}

	public URI getSourceURI()
	{
		return sourceURI;
	}

	public void setSourceURI(URI source)
	{
		sourceURI = source;
	}

}
