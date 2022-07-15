/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class NamedValueTest
{

	@Test
	public void testNullConstructor()
	{
		assertThrows(NullPointerException.class, () -> new NamedValue(null, 1.0));
		assertThrows(NullPointerException.class, () -> new NamedValue(null));
	}

	@Test
	public void testBasics()
	{
		NamedValue nv = new NamedValue("Foo");
		assertEquals("Foo", nv.getName());
		assertEquals(0.0, nv.getWeight());
		nv.addWeight(4.3);
		assertEquals(4.3, nv.getWeight(), 10.0e-8);
		nv.addWeight(2.1);
		assertEquals(6.4, nv.getWeight(), 10.0e-8);
		nv.removeWeight(3.3);
		assertEquals(3.1, nv.getWeight(), 10.0e-8);
	}

	@Test
	public void testToString()
	{
		NamedValue nv = new NamedValue("Foo", 2.0);
		assertEquals("Foo:2.0", nv.toString());
	}
}
