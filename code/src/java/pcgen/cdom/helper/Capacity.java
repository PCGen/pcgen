/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.helper;

import java.math.BigDecimal;

public class Capacity
{

	public static final BigDecimal UNLIMITED = new BigDecimal(-1);
	/*
	 * CONSIDER Need to flesh out how this works; depends on how Capacity
	 * interacts with the core... - Tom Parker 3/1/07
	 */
	public static final Capacity ANY = new Capacity(null, UNLIMITED);

	private final String type;

	private final BigDecimal limit;

	public Capacity(String typ, BigDecimal cap)
	{
		type = typ;
		limit = cap;
	}

	public BigDecimal getCapacity()
	{
		return limit;
	}

	public String getType()
	{
		return type;
	}

	public static Capacity getTotalCapacity(BigDecimal d)
	{
		return new Capacity(null, d);
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Capacity: ");
		sb.append(type == null ? "Total" : type);
		sb.append('=');
		sb.append(UNLIMITED.equals(limit) ? "UNLIMITED" : limit);
		return sb.toString();
	}

	@Override
	public int hashCode()
	{
		return type == null ? 0 : type.hashCode() ^ limit.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof Capacity)
		{
			Capacity other = (Capacity) o;
			if (type == null)
			{
				if (other.type != null)
				{
					return false;
				}
			}
			else
			{
				if (!type.equals(other.type))
				{
					return false;
				}
			}
			return limit.equals(other.limit);
		}
		return false;
	}
}
