/**
 * CompareEqualString.java
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
 * Is the value equal using string compare
 */
public class CompareEqualString extends TestChecker
{
	private String str;

	/**
	 * Constructor
	 * @param str
	 */
	public CompareEqualString(String str)
	{
		this.str = str;
	}

    @Override
	public boolean check(Object obj)
	{
		return obj instanceof String && ((Comparable<String>) obj).compareTo(this.str) == 0;
	}

    @Override
	public StringBuilder scribe(StringBuilder buf)
	{
		buf.append("a String matching \"");
		buf.append(this.str);
		buf.append("\"");
		return buf;
	}
}
