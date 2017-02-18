/**
 * CompareGreaterThan.java
 * Copyright 2005 (c) Andrew Wilson <nuance@sourceforge.net>
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
 *
 */

package pcgen.util.testchecker;

import pcgen.util.TestChecker;

/**
 * Is the value greater than another value?
 */
public class CompareGreaterThan extends TestChecker
{
	private Comparable<Object> comp;

	/**
	 * Constructor
	 * @param comp The first operand of the compare
	 */
	public CompareGreaterThan(final Comparable<Object> comp)
	{
		this.comp = comp;
	}

    @Override
	public boolean check(final Object obj)
	{
		return comp.compareTo(obj) < 0;
	}

    @Override
	public StringBuilder scribe(final StringBuilder buf)
	{
		buf.append("a value greater than <");
		buf.append(this.comp);
		buf.append(">");
		return buf;
	}
}
