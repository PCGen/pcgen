/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
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
 */
package pcgen.cdom.enumeration;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GroupingStateTest
{

	@Test
	public void testAddConsistent()
	{
		for (GroupingState gs1 : GroupingState.values())
		{
			for (GroupingState gs2 : GroupingState.values())
			{
				assertEquals(gs1.add(gs2), gs2.add(gs1));
			}
		}
	}

	@Test
	public void testInvalidImmutable()
	{
		GroupingState invalid = GroupingState.INVALID;
		for (GroupingState gs2 : GroupingState.values())
		{
			assertEquals(invalid.add(gs2), invalid);
			assertEquals(gs2.add(invalid), invalid);
		}
		assertEquals(invalid, invalid.negate());
		assertEquals(invalid, invalid.reduce());
	}

	@Test
	public void testEmptyWeak()
	{
		GroupingState empty = GroupingState.EMPTY;
		for (GroupingState gs2 : GroupingState.values())
		{
			assertEquals(empty.add(gs2), gs2);
			assertEquals(gs2.add(empty), gs2);
		}
	}
}
