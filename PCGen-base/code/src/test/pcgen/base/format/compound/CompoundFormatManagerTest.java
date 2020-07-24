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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
}
