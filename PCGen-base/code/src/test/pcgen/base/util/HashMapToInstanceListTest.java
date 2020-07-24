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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import pcgen.testsupport.TestSupport;

public class HashMapToInstanceListTest extends HashMapToListTest
{

	@Override
	protected AbstractMapToList<Integer, Character> getMapToList()
	{
		return new HashMapToInstanceList<>();
	}

	@Override
	@Test
	public void testInstanceBehavior()
	{
		AbstractMapToList<Integer, Character> dkm = getMapToList();
		Character ca = TestSupport.CONST_A;
		Character cb = TestSupport.CONST_B;
		Character cc = TestSupport.CONST_C;
		Character ca1 = new Character(TestSupport.CONST_A.charValue());
		dkm.addToListFor(TestSupport.I1, ca);
		dkm.addToListFor(TestSupport.I1, cb);
		dkm.addToListFor(TestSupport.I1, cc);
		dkm.addToListFor(TestSupport.I2, ca);
		dkm.addToListFor(TestSupport.I2, ca);
		dkm.addToListFor(TestSupport.I3, cb);
		dkm.addToListFor(TestSupport.I3, cc);
		assertTrue(dkm.containsInList(TestSupport.I1, ca));
		assertFalse(dkm.containsInList(TestSupport.I1, ca1));
		assertFalse(dkm.removeFromListFor(TestSupport.I1, ca1));
		assertTrue(dkm.containsInList(TestSupport.I1, ca));

		assertTrue(dkm.containsInList(TestSupport.I2, ca));
		assertFalse(dkm.containsInList(TestSupport.I2, ca1));
		assertFalse(dkm.removeFromListFor(TestSupport.I2, ca1));
		assertTrue(dkm.containsInList(TestSupport.I2, ca));
		assertTrue(dkm.removeFromListFor(TestSupport.I2, ca));
		// There were two
		assertTrue(dkm.containsInList(TestSupport.I2, ca));
	}

}
