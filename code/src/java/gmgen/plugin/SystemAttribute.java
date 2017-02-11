/*
 *  Initiative - A role playing utility to track turns
 *  Copyright (C) 2002 Devon D Jones
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package gmgen.plugin;

public class SystemAttribute
{
	private final String description;
	private final String name;
	private SystemDie die;
	private int value;

	private SystemAttribute(String name, int value, String description, SystemDie die)
	{
		this.name = name;
		this.value = value;
		this.description = description;
		this.die = die;
	}

	public SystemAttribute(String name, int value)
	{
		this(name, value, "", new SystemDie(0));
	}

	public String getDescription()
	{
		return description;
	}

	public void setDie(SystemDie die)
	{
		this.die = die;
	}

	public int getModifier()
	{
		return (value / 2) - 5;
	}

	public String getName()
	{
		return name;
	}

	public void setValue(final int value)
	{
		this.value = value;
	}

	public int getValue()
	{
		return value;
	}

	public int check()
	{
		return check(0);
	}

	public int check(final int mod)
	{
		return die.roll() + this.getModifier() + mod;
	}

}
