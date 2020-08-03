/*
 * Copyright 2016 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.format.compound;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;
import pcgen.base.util.SimpleValueStore;

/**
 * Test the CompoundFormatManager class
 */
public class CompoundFormatManagerTest
{
	@Test
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void testUnconvertFailObject()
	{
		CompoundFormatManager<Number> manager =
				new CompoundFormatManager<>(FormatUtilities.NUMBER_MANAGER, '|');
		//Yes generics are being violated in order to do this test
		FormatManager formatManager = manager;
		assertThrows(ClassCastException.class, () -> formatManager.unconvert(new Object()));
	}

	@Test
	public void testConstructor()
	{
		assertThrows(NullPointerException.class, () -> new CompoundFormatManager<>(null, '|'));
	}

	@Test
	public void testRoundRobinIdentifier()
	{
		CompoundFormatManager<Number> manager =
				new CompoundFormatManager<>(FormatUtilities.NUMBER_MANAGER, '|');
		assertEquals("COMPOUND[NUMBER]", manager.getIdentifierType());
		manager.addSecondary(FormatUtilities.STRING_MANAGER, "Level", true);
		assertEquals("COMPOUND[NUMBER,STRING=Level]",
			manager.getIdentifierType());
		manager.addSecondary(FormatUtilities.BOOLEAN_MANAGER, "Allowed", false);
		assertEquals("COMPOUND[NUMBER,BOOLEAN?=Allowed,STRING=Level]",
			manager.getIdentifierType());
	}

	@Test
	public void testInvalidConvertSimpleFail()
	{
		CompoundFormatManager<Number> manager =
				new CompoundFormatManager<>(FormatUtilities.NUMBER_MANAGER, '|');
		manager.addSecondary(FormatUtilities.BOOLEAN_MANAGER, "Allowed", false);
		manager.addSecondary(FormatUtilities.STRING_MANAGER, "Level", true);
		assertThrows(IllegalArgumentException.class, () -> manager.convert(null));
		assertThrows(IllegalArgumentException.class, () -> manager.convert(""));
		assertThrows(IllegalArgumentException.class, () -> manager.convert("|"));
		assertThrows(IllegalArgumentException.class, () -> manager.convert("3"));
	}
	
	@Test
	public void testInvalidConvertBadSeparator()
	{
		CompoundFormatManager<Number> manager =
				new CompoundFormatManager<>(FormatUtilities.NUMBER_MANAGER, '|');
		manager.addSecondary(FormatUtilities.BOOLEAN_MANAGER, "Allowed", false);
		manager.addSecondary(FormatUtilities.STRING_MANAGER, "Level", true);
		assertThrows(IllegalArgumentException.class, () -> manager.convert("3|LEVEL=Hard|"));
		assertThrows(IllegalArgumentException.class, () -> manager.convert("3|LEVEL=Hard||ALLOWED=false"));
		assertThrows(IllegalArgumentException.class, () -> manager.convert("3||LEVEL=Hard|ALLOWED=false"));
	}
	
	@Test
	public void testInvalidConvertBadAssociation()
	{
		CompoundFormatManager<Number> manager =
				new CompoundFormatManager<>(FormatUtilities.NUMBER_MANAGER, '|');
		manager.addSecondary(FormatUtilities.BOOLEAN_MANAGER, "Allowed", false);
		manager.addSecondary(FormatUtilities.STRING_MANAGER, "Level", true);
		assertThrows(IllegalArgumentException.class, () -> manager.convert("3|LEVEL=Hard|SOUND=Bell"));
	}
	
	@Test
	public void testConvert()
	{
		CompoundFormatManager<Number> manager =
				new CompoundFormatManager<>(FormatUtilities.NUMBER_MANAGER, '|');
		manager.addSecondary(FormatUtilities.BOOLEAN_MANAGER, "Allowed", false);
		manager.addSecondary(FormatUtilities.STRING_MANAGER, "Level", true);
		Compound c = manager.convert("3|LEVEL=Hard");
		assertEquals("3", c.getPrimaryUnconverted());
		assertEquals(3, c.getPrimary());
		assertEquals("Hard", c.getSecondary("Level").getUnconverted());
		assertEquals("Hard", c.getSecondary("LEVEL").getUnconverted());
		/*
		 * For now unassigned optional items return null
		 * 
		 * There is an interesting debate to be had here depending on how
		 * optional items are used
		 */
		assertNull(c.getSecondary("Allowed"));
		assertEquals("3|LEVEL=Hard", manager.unconvert(c));
		Compound c2 = manager.convert("4|LEVEL=Easy|ALLOWED=False");
		assertEquals("4", c2.getPrimaryUnconverted());
		assertEquals(4, c2.getPrimary());
		assertEquals("Easy", c2.getSecondary("Level").getUnconverted());
		assertEquals("false", c2.getSecondary("ALLOWED").getUnconverted());
		assertEquals("4|ALLOWED=false|LEVEL=Easy", manager.unconvert(c2));
		CompoundFormatManager<String> manager2 =
				new CompoundFormatManager<>(FormatUtilities.STRING_MANAGER, '|');
		manager.addSecondary(FormatUtilities.BOOLEAN_MANAGER, "Allowed", false);
		manager.addSecondary(FormatUtilities.NUMBER_MANAGER, "Level", true);
		//Incompatible
		assertThrows(IllegalArgumentException.class, () -> manager2.unconvert(c2));
	}

	@Test
	public void testConvertIndirect()
	{
		CompoundFormatManager<Number> manager =
				new CompoundFormatManager<>(FormatUtilities.NUMBER_MANAGER, '|');
		manager.addSecondary(FormatUtilities.BOOLEAN_MANAGER, "Allowed", false);
		manager.addSecondary(FormatUtilities.STRING_MANAGER, "Level", true);
		assertThrows(IllegalArgumentException.class, () -> manager.convertIndirect(null));
		assertThrows(IllegalArgumentException.class, () -> manager.convertIndirect(""));
		assertThrows(IllegalArgumentException.class, () -> manager.convertIndirect("|"));
		assertThrows(IllegalArgumentException.class, () -> manager.convertIndirect("3"));
		assertThrows(IllegalArgumentException.class, () -> manager.convertIndirect("3|LEVEL=Hard|SOUND=Bell"));
		
		Indirect<Compound> in = manager.convertIndirect("3|LEVEL=Hard");
		Compound c = in.get();
		assertEquals("3", c.getPrimaryUnconverted());
		assertEquals(3, c.getPrimary());
		assertEquals("Hard", c.getSecondary("Level").getUnconverted());
		assertEquals("Hard", c.getSecondary("LEVEL").getUnconverted());
		/*
		 * For now unassigned optional items return null
		 * 
		 * There is an interesting debate to be had here depending on how
		 * optional items are used
		 */
		assertNull(c.getSecondary("Allowed"));
		assertEquals("3|LEVEL=Hard", manager.unconvert(c));
		assertEquals("3|LEVEL=Hard", in.getUnconverted());
		Indirect<Compound> in2 =
				manager.convertIndirect("4|LEVEL=Easy|ALLOWED=False");
		Compound c2 = in2.get();
		assertEquals("4", c2.getPrimaryUnconverted());
		assertEquals(4, c2.getPrimary());
		assertEquals("Easy", c2.getSecondary("Level").getUnconverted());
		assertEquals("false", c2.getSecondary("ALLOWED").getUnconverted());
		assertEquals("4|ALLOWED=false|LEVEL=Easy", manager.unconvert(c2));
		assertEquals("4|ALLOWED=false|LEVEL=Easy", in2.getUnconverted());
	}

	@Test
	public void testInitializeFrom()
	{
		SimpleValueStore valueStore = new SimpleValueStore();
		CompoundFormatManager<Number> manager =
				new CompoundFormatManager<>(FormatUtilities.NUMBER_MANAGER, '|');
		manager.addSecondary(FormatUtilities.BOOLEAN_MANAGER, "Allowed", false);
		manager.addSecondary(FormatUtilities.STRING_MANAGER, "Level", true);
		Compound c = manager.convert("3|LEVEL=Hard");
		valueStore.addValueFor(FormatUtilities.NUMBER_MANAGER.getIdentifierType(), 3);
		valueStore.addValueFor(FormatUtilities.STRING_MANAGER.getIdentifierType(), "Hard");
		Compound value = manager.initializeFrom(valueStore);
		assertEquals("Hard", value.getSecondary("LEVEL").getUnconverted());
		assertEquals(c, value);
		Compound c2 = manager.convert("4|LEVEL=Easy");
		valueStore.addValueFor(FormatUtilities.NUMBER_MANAGER.getIdentifierType(), 4);
		valueStore.addValueFor(FormatUtilities.STRING_MANAGER.getIdentifierType(), "Easy");
		value = manager.initializeFrom(valueStore);
		assertEquals(c2, value);
	}

	@Test
	public void testEquals()
	{
		CompoundFormatManager<Number> manager =
				new CompoundFormatManager<>(FormatUtilities.NUMBER_MANAGER, '|');
		manager.addSecondary(FormatUtilities.BOOLEAN_MANAGER, "Allowed", false);
		manager.addSecondary(FormatUtilities.STRING_MANAGER, "Level", true);
		CompoundFormatManager<Number> altmanager =
				new CompoundFormatManager<>(FormatUtilities.NUMBER_MANAGER, '|');
		altmanager.addSecondary(FormatUtilities.BOOLEAN_MANAGER, "Allowed", false);
		altmanager.addSecondary(FormatUtilities.STRING_MANAGER, "Level", false);
		
		Compound c = manager.convert("3|Level=Hard");
		Indirect<Compound> ic = manager.convertIndirect("3|Level=Hard");
		
		assertTrue(c.equals(c));
		assertTrue(ic.equals(ic));

		assertFalse(c.equals(1));
		assertFalse(ic.equals(1));

		assertFalse(manager.equals(altmanager));

		assertTrue(manager.convert("3|Level=Hard").equals(manager.convert("3|Level=Hard")));
		assertTrue(manager.convertIndirect("3|LEVEL=Hard").equals(manager.convertIndirect("3|Level=Hard")));

		assertTrue(altmanager.convert("3").equals(altmanager.convert("3")));
		assertTrue(altmanager.convertIndirect("3").equals(altmanager.convertIndirect("3")));

		//Format managers are different
		assertFalse(manager.convert("3|LEVEL=Hard").equals(altmanager.convert("3|Level=Hard")));
		assertFalse(manager.convertIndirect("3|LEVEL=Hard").equals(altmanager.convertIndirect("3|Level=Hard")));

		assertFalse(manager.convert("4|Level=Hard").equals(manager.convert("3|Level=Hard")));
		assertFalse(manager.convert("3|Level=Hard").equals(manager.convertIndirect("3|Level=Hard")));
		assertFalse(manager.convertIndirect("3|Level=Hard").equals(manager.convert("3|Level=Hard")));
		assertFalse(manager.convertIndirect("4|LEVEL=Hard").equals(manager.convertIndirect("3|Level=Hard")));

		assertFalse(manager.convert("3|LEVEL=Hard").equals(manager.convert("3|Level=Hard|Allowed=True")));
		assertFalse(manager.convertIndirect("3|LEVEL=Hard").equals(manager.convertIndirect("3|Level=Hard|Allowed=True")));

		assertFalse(altmanager.convert("3|LEVEL=Hard").equals(altmanager.convert("3|Allowed=True")));
		assertFalse(altmanager.convertIndirect("3|LEVEL=Hard").equals(altmanager.convertIndirect("3|Allowed=True")));

		assertFalse(altmanager.convert("3").equals(altmanager.convert("3|Allowed=True")));
		assertFalse(altmanager.convertIndirect("3").equals(altmanager.convertIndirect("3|Allowed=True")));

		assertFalse(altmanager.convert("3|LEVEL=Hard").equals(altmanager.convert("3")));
		assertFalse(altmanager.convertIndirect("3|LEVEL=Hard").equals(altmanager.convertIndirect("3")));
	}

	@Test
	public void testHashCode()
	{
		CompoundFormatManager<Number> manager =
				new CompoundFormatManager<>(FormatUtilities.NUMBER_MANAGER, '|');
		manager.addSecondary(FormatUtilities.BOOLEAN_MANAGER, "Allowed", false);
		manager.addSecondary(FormatUtilities.STRING_MANAGER, "Level", true);
		CompoundFormatManager<Number> altmanager =
				new CompoundFormatManager<>(FormatUtilities.NUMBER_MANAGER, '|');
		altmanager.addSecondary(FormatUtilities.BOOLEAN_MANAGER, "Allowed", false);
		altmanager.addSecondary(FormatUtilities.STRING_MANAGER, "Level", false);

		assertNotEquals(manager.hashCode(), altmanager.hashCode());

		assertEquals(manager.convert("3|Level=Hard").hashCode(), manager.convert("3|Level=Hard").hashCode());
		assertEquals(manager.convertIndirect("3|LEVEL=Hard").hashCode(), manager.convertIndirect("3|Level=Hard").hashCode());

		assertEquals(altmanager.convert("3").hashCode(), altmanager.convert("3").hashCode());
		assertEquals(altmanager.convertIndirect("3").hashCode(), altmanager.convertIndirect("3").hashCode());

		//Format managers are different
		assertNotEquals(manager.convert("3|LEVEL=Hard").hashCode(), altmanager.convert("3|Level=Hard").hashCode());
		assertNotEquals(manager.convertIndirect("3|LEVEL=Hard").hashCode(), altmanager.convertIndirect("3|Level=Hard").hashCode());

		assertNotEquals(manager.convert("4|Level=Hard").hashCode(), manager.convert("3|Level=Hard").hashCode());
		assertNotEquals(manager.convert("3|Level=Hard").hashCode(), manager.convertIndirect("3|Level=Hard").hashCode());
		assertNotEquals(manager.convertIndirect("3|Level=Hard").hashCode(), manager.convert("3|Level=Hard").hashCode());
		assertNotEquals(manager.convertIndirect("4|LEVEL=Hard").hashCode(), manager.convertIndirect("3|Level=Hard").hashCode());

		assertNotEquals(manager.convert("3|LEVEL=Hard").hashCode(), manager.convert("3|Level=Hard|Allowed=True").hashCode());
		assertNotEquals(manager.convertIndirect("3|LEVEL=Hard").hashCode(), manager.convertIndirect("3|Level=Hard|Allowed=True").hashCode());

		assertNotEquals(altmanager.convert("3|LEVEL=Hard").hashCode(), altmanager.convert("3|Allowed=True").hashCode());
		assertNotEquals(altmanager.convertIndirect("3|LEVEL=Hard").hashCode(), altmanager.convertIndirect("3|Allowed=True").hashCode());

		assertNotEquals(altmanager.convert("3").hashCode(), altmanager.convert("3|Allowed=True").hashCode());
		assertNotEquals(altmanager.convertIndirect("3").hashCode(), altmanager.convertIndirect("3|Allowed=True").hashCode());

		assertNotEquals(altmanager.convert("3|LEVEL=Hard").hashCode(), altmanager.convert("3").hashCode());
		assertNotEquals(altmanager.convertIndirect("3|LEVEL=Hard").hashCode(), altmanager.convertIndirect("3").hashCode());
	}
}
