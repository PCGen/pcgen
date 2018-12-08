/*
 * Copyright (c) 2014 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.format;

import java.math.BigDecimal;

import junit.framework.TestCase;
import pcgen.base.math.OrderedPair;

/**
 * Test the OrderedPairManager class
 */
public class OrderedPairManagerTest extends TestCase
{
	private OrderedPairManager manager = new OrderedPairManager();

	public void testConvertFailNull()
	{
		try
		{
			manager.convert(null);
			fail("null value should fail");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//expected
		}
	}

	public void testConvertFailNotNumeric()
	{
		try
		{
			manager.convert("SomeString");
			fail("null value should fail");
		}
		catch (IllegalArgumentException e)
		{
			//expected
		}
	}

	public void testUnconvertFailNull()
	{
		try
		{
			manager.unconvert(null);
			fail("null value should fail");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//expected
		}
	}

	public void testConvertIndirectFailNull()
	{
		try
		{
			manager.convertIndirect(null);
			fail("null value should fail");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//expected
		}
	}

	public void testConvertIndirectFailNotNumeric()
	{
		try
		{
			manager.convertIndirect("SomeString");
			fail("null value should fail");
		}
		catch (IllegalArgumentException e)
		{
			//expected
		}
	}

	public void testConvert()
	{
		assertEquals(new OrderedPair(1, 1), manager.convert("1,1"));
		assertEquals(new OrderedPair(-3, 4), manager.convert("-3,4"));
		assertEquals(new OrderedPair(new BigDecimal("1.4"), new BigDecimal("6.5")), manager.convert("1.4,6.5"));
	}

	public void testUnconvert()
	{
		assertEquals("1,2", manager.unconvert(new OrderedPair(1, 2)));
		assertEquals("-3,4", manager.unconvert(new OrderedPair(-3, 4)));
		assertEquals("1.4,6.5", manager.unconvert(new OrderedPair(1.4, 6.5)));
	}

	public void testConvertIndirect()
	{
		assertEquals(new OrderedPair(1, 1), manager.convertIndirect("1,1")
			.get());
		assertEquals(new OrderedPair(-3, 4), manager.convertIndirect("-3,4")
			.get());
		assertEquals(new OrderedPair(new BigDecimal("1.4"), new BigDecimal("6.5")), manager
			.convertIndirect("1.4,6.5").get());
	}

	public void testGetIdentifier()
	{
		assertEquals("ORDEREDPAIR", manager.getIdentifierType());
	}

	public void testHashCodeEquals()
	{
		assertEquals(new OrderedPairManager().hashCode(), manager.hashCode());
		assertFalse(manager.equals(new Object()));
		assertFalse(manager.equals(new StringManager()));
		assertTrue(manager.equals(new OrderedPairManager()));
	}

	public void testGetComponent()
	{
		assertTrue(manager.getComponentManager().isEmpty());
	}

	public void testIsDirect()
	{
		assertTrue(manager.isDirect());
	}
}

