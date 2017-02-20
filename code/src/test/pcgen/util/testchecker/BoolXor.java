/**
 * BoolXor.java
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
 * Creates a new check by logically exclusive oring two other testcheckers
 * together
 */
public class BoolXor extends TestChecker
{

	TestChecker tc1, tc2;

	/**
	 * Constructor
	 * @param tc1
	 * @param tc2
	 */
	public BoolXor(TestChecker tc1, TestChecker tc2)
	{
		this.tc1 = tc1;
		this.tc2 = tc2;
	}

    @Override
	public boolean check(Object obj)
	{
		return this.tc1.check(obj) ^ this.tc2.check(obj);
	}

    @Override
	public StringBuilder scribe(StringBuilder buf)
	{
		buf.append("(");
		this.tc1.scribe(buf);
		buf.append(" xor ");
		this.tc2.scribe(buf);
		buf.append(")");
		return buf;
	}
}
