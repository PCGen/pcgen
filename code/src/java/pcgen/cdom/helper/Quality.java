/*
 * Copyright 2007, 2008 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.helper;

public class Quality
{

	private final String quality;
	private final String value;

	public Quality(String key, String val)
	{
		quality = key;
		value = val;
	}

	public String getQuality()
	{
		return quality;
	}

	public String getValue()
	{
		return value;
	}

	@Override
	public int hashCode()
	{
		return quality.hashCode() ^ value.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof Quality)
		{
			Quality other = (Quality) o;
			return quality.equals(other.quality) && value.equals(other.value);
		}
		return false;
	}

	@Override
	public String toString()
	{
		return quality + ": " + value;
	}
}
