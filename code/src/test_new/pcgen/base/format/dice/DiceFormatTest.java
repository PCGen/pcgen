/*
 * Copyright (c) 2017 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.format.dice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.TestConstants;
import pcgen.base.format.StringManager;

import org.junit.jupiter.api.Test;

class DiceFormatTest
{
	@Test
	public void testConvertFailNull()
	{
		assertThrows(NullPointerException.class,
				() -> TestConstants.DICE_MANAGER.convert(null));
	}

	@Test
	public void testConvertFailNotNumeric()
	{
		assertThrows(IllegalArgumentException.class,
			() -> TestConstants.DICE_MANAGER.convert("SomeString"));
	}

	@Test
	public void testUnconvertFailNull()
	{
		assertThrows(NullPointerException.class,
				() -> TestConstants.DICE_MANAGER.unconvert(null));
	}

	@Test
	public void testConvertIndirectFailNull()
	{
		assertThrows(NullPointerException.class,
				() -> TestConstants.DICE_MANAGER.convertIndirect(null));
	}

	@Test
	public void testConvertIndirectFailNotNumeric()
	{
		assertThrows(IllegalArgumentException.class,
				() -> TestConstants.DICE_MANAGER.convertIndirect("SomeString"));
	}

	@Test
	public void testConvert()
	{
		assertEquals(new Dice(1, new Die(1)), TestConstants.DICE_MANAGER.convert("1"));
		assertEquals(new Dice(1, new Die(1)), TestConstants.DICE_MANAGER.convert("1d1"));
		assertEquals(new Dice(3, new Die(8)), TestConstants.DICE_MANAGER.convert("3d8"));
		assertEquals(new Dice(1, new Die(4)), TestConstants.DICE_MANAGER.convert("d4"));
	}

	@Test
	public void testUnconvert()
	{
		assertEquals("1", TestConstants.DICE_MANAGER.unconvert(new Dice(1, new Die(1))));
		assertEquals("3d6", TestConstants.DICE_MANAGER.unconvert(new Dice(3, new Die(6))));
		assertEquals("1d4", TestConstants.DICE_MANAGER.unconvert(new Dice(1, new Die(4))));
	}

	@Test
	public void testConvertIndirect()
	{
		assertEquals(new Dice(1, new Die(1)), TestConstants.DICE_MANAGER.convertIndirect("1").get());
		assertEquals(new Dice(1, new Die(1)), TestConstants.DICE_MANAGER.convertIndirect("1d1").get());
		assertEquals(new Dice(3, new Die(8)), TestConstants.DICE_MANAGER.convertIndirect("3d8").get());
		assertEquals(new Dice(1, new Die(4)), TestConstants.DICE_MANAGER.convertIndirect("d4").get());
	}

	@Test
	public void testGetIdentifier()
	{
		assertEquals("DICE", TestConstants.DICE_MANAGER.getIdentifierType());
	}

	@Test
	public void testHashCodeEquals()
	{
		assertEquals(new DiceFormat().hashCode(), TestConstants.DICE_MANAGER.hashCode());
		assertNotEquals(new Object(), TestConstants.DICE_MANAGER);
		assertNotEquals(TestConstants.DICE_MANAGER, new StringManager());
		assertEquals(TestConstants.DICE_MANAGER, new DiceFormat());
	}

	@Test
	public void testGetComponent()
	{
		assertTrue(TestConstants.DICE_MANAGER.getComponentManager().isEmpty());
	}
}
