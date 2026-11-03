/*
 * Copyright 2018 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.lang;

import java.math.BigDecimal;
import java.util.Comparator;

/**
 * NumberComparator is a Comparator for Number values, since Number does not natively
 * implement Comparable. This attempts some direct comparisons based on certain object
 * types (those delegations are the fastest method of doing the comparison). If those
 * optimizations are not available, a deep comparison is attempted through BigDecimal.
 */
public class NumberComparator implements Comparator<Number>
{

	@Override
	public int compare(Number o1, Number o2)
	{
		if ((o1 instanceof Integer) && (o2 instanceof Integer))
		{
			return ((Integer) o1).compareTo((Integer) o2);
		}
		if ((o1 instanceof Double) && (o2 instanceof Double))
		{
			return ((Double) o1).compareTo((Double) o2);
		}
		BigDecimal bd1 = new BigDecimal(o1.toString());
		BigDecimal bd2 = new BigDecimal(o2.toString());
		return bd1.compareTo(bd2);
	}

}
