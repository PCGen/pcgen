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

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import pcgen.testsupport.NoPublicZeroArgConstructorMap;
import pcgen.testsupport.NoZeroArgConstructorMap;
import pcgen.testsupport.StrangeMap;

/**
 * Test the GenericMapToList class constructors.
 * 
 * Additional tests are in other classes which test specific implementations.
 * 
 * @see GenericMapToListHashTest
 * @see GenericMapToListIdentityHashTest
 * @see GenericMapToListTreeTest
 */
public class GenericMapToListTest
{
	@Test
	public void testConstructorNoZeroArg()
	{
		assertThrows(ReflectiveOperationException.class, () -> new GenericMapToList<>(NoZeroArgConstructorMap.class));
	}

	@Test
	public void testConstructorPrivate()
	{
		assertThrows(NoSuchMethodException.class, () -> new GenericMapToList<>(NoPublicZeroArgConstructorMap.class));
	}

	@Test
	public void testBadClassInConstructor()
	{
		assertThrows(NoSuchMethodException.class, () -> new GenericMapToList<>(StrangeMap.class));
	}
}
