/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.util;

public final class NamedValue
{
	public final String name;

	private double weight;

	public NamedValue(String s)
	{
		name = s;
	}

	public NamedValue(String s, double d)
	{
		this(s);
		weight = d;
	}

	public double getWeight()
	{
		return weight;
	}

	public void addWeight(double d)
	{
		weight += d;
		// CONSIDER what if less than zero?
	}
	
	@Override
	public String toString()
	{
		return name + ":" + weight;
	}
	
}