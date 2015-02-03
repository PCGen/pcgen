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
package pcgen.base.util;

import org.junit.Test;

public class HashMapToInstanceListTest extends HashMapToListTest
{

	@Override
	protected AbstractMapToList<Integer, Character> getMapToList()
	{
		return new HashMapToInstanceList<Integer, Character>();
	}

	@Override
	@Test
	public void testInstanceBehavior()
	{
		AbstractMapToList<Integer, Character> dkm = getMapToList();
		Character ca = Character.valueOf('a');
		Character cb = Character.valueOf('b');
		Character cc = Character.valueOf('c');
		Character ca1 = new Character('a');
		Integer i1 = Integer.valueOf(1);
		dkm.addToListFor(i1, ca);
		dkm.addToListFor(i1, cb);
		dkm.addToListFor(i1, cc);
		Integer i2 = Integer.valueOf(2);
		dkm.addToListFor(i2, ca);
		dkm.addToListFor(i2, ca);
		Integer i3 = Integer.valueOf(3);
		dkm.addToListFor(i3, cb);
		dkm.addToListFor(i3, cc);
		assertTrue(dkm.containsInList(i1, ca));
		assertFalse(dkm.containsInList(i1, ca1));
		assertFalse(dkm.removeFromListFor(i1, ca1));
		assertTrue(dkm.containsInList(i1, ca));

		assertTrue(dkm.containsInList(i2, ca));
		assertFalse(dkm.containsInList(i2, ca1));
		assertFalse(dkm.removeFromListFor(i2, ca1));
		assertTrue(dkm.containsInList(i2, ca));
		assertTrue(dkm.removeFromListFor(i2, ca));
		// There were two
		assertTrue(dkm.containsInList(i2, ca));
	}

}
