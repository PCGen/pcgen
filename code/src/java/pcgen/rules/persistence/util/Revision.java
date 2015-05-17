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
package pcgen.rules.persistence.util;

public class Revision implements Comparable<Revision>
{
	private final int primarySequence;
	private final int secondarySequence;
	private final int tertiarySequence;

	public Revision(int a, int b, int c)
	{
		primarySequence = a;
		secondarySequence = b;
		tertiarySequence = c;
	}

	@Override
	public int compareTo(Revision r)
	{
		if (primarySequence > r.primarySequence)
		{
			return -1;
		}
		else if (primarySequence < r.primarySequence)
		{
			return 1;
		}
		else if (secondarySequence > r.secondarySequence)
		{
			return -1;
		}
		else if (secondarySequence < r.secondarySequence)
		{
			return 1;
		}
		else if (tertiarySequence > r.tertiarySequence)
		{
			return -1;
		}
		else if (tertiarySequence < r.tertiarySequence)
		{
			return 1;
		}
		return 0;
	}

	@Override
	public String toString()
	{
		return primarySequence + "." + secondarySequence + "-"
				+ tertiarySequence;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj == this || obj instanceof Revision
				&& compareTo((Revision) obj) == 0;
	}

	@Override
	public int hashCode()
	{
		return primarySequence * secondarySequence + tertiarySequence;
	}
	
	
}
