/*
 * Copyright 2016 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.format.inttest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import pcgen.base.format.ArrayFormatManager;
import pcgen.base.format.compound.Compound;
import pcgen.base.format.compound.CompoundFormatManager;
import pcgen.base.format.compound.DirectCompound;
import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.util.Indirect;
import pcgen.base.util.NamedIndirect;

/**
 * Integration testing between FormatManager objects
 */
public class FormatManagerIntTest
{
	@Test
	public void testInvalidConvertSimple()
	{
		CompoundFormatManager<Number> compoundManager =
				new CompoundFormatManager<>(FormatUtilities.NUMBER_MANAGER, '|');
		compoundManager.addSecondary(FormatUtilities.BOOLEAN_MANAGER, "Allowed", false);
		compoundManager.addSecondary(FormatUtilities.STRING_MANAGER, "Level", true);
		ArrayFormatManager<Compound> manager =
				new ArrayFormatManager<>(compoundManager, '\n', ',');
		assertThrows(IllegalArgumentException.class, () -> manager.convert("|"));
		assertThrows(IllegalArgumentException.class, () -> manager.convert("3"));
	}

	@Test
	public void testInvalidConvertSeparatorIssues()
	{
		CompoundFormatManager<Number> compoundManager =
				new CompoundFormatManager<>(FormatUtilities.NUMBER_MANAGER, '|');
		compoundManager.addSecondary(FormatUtilities.BOOLEAN_MANAGER, "Allowed", false);
		compoundManager.addSecondary(FormatUtilities.STRING_MANAGER, "Level", true);
		ArrayFormatManager<Compound> manager =
				new ArrayFormatManager<>(compoundManager, '\n', ',');
		assertThrows(IllegalArgumentException.class, () -> manager.convert("3|LEVEL=Hard|"));
		assertThrows(IllegalArgumentException.class, () -> manager.convert("3|LEVEL=Hard||ALLOWED=false"));
		assertThrows(IllegalArgumentException.class, () -> manager.convert("3||LEVEL=Hard|ALLOWED=false"));
		assertThrows(IllegalArgumentException.class, () -> manager.convert("3,,4|LEVEL=Hard|ALLOWED=false"));
		assertThrows(IllegalArgumentException.class, () -> manager.convert(",3,4|LEVEL=Hard|ALLOWED=false"));
		assertThrows(IllegalArgumentException.class, () -> manager.convert("3,4,|LEVEL=Hard|ALLOWED=false"));
	}

	@Test
	public void testInvalidConvertAssociationIssues()
	{
		CompoundFormatManager<Number> compoundManager =
				new CompoundFormatManager<>(FormatUtilities.NUMBER_MANAGER, '|');
		compoundManager.addSecondary(FormatUtilities.BOOLEAN_MANAGER, "Allowed", false);
		compoundManager.addSecondary(FormatUtilities.STRING_MANAGER, "Level", true);
		ArrayFormatManager<Compound> manager =
				new ArrayFormatManager<>(compoundManager, '\n', ',');
		assertThrows(IllegalArgumentException.class, () -> manager.convert("3|LEVEL=Hard|SOUND=Bell"));
	}

	@Test
	public void testInvalidConvertIndirectSimple()
	{
		CompoundFormatManager<Number> compoundManager =
				new CompoundFormatManager<>(FormatUtilities.NUMBER_MANAGER, '|');
		compoundManager.addSecondary(FormatUtilities.BOOLEAN_MANAGER, "Allowed", false);
		compoundManager.addSecondary(FormatUtilities.STRING_MANAGER, "Level", true);
		ArrayFormatManager<Compound> manager =
				new ArrayFormatManager<>(compoundManager, '\n', ',');
		assertThrows(IllegalArgumentException.class, () -> manager.convertIndirect("|"));
		assertThrows(IllegalArgumentException.class, () -> manager.convertIndirect("3"));
	}

	@Test
	public void testInvalidConvertIndirectSeparator()
	{
		CompoundFormatManager<Number> compoundManager =
				new CompoundFormatManager<>(FormatUtilities.NUMBER_MANAGER, '|');
		compoundManager.addSecondary(FormatUtilities.BOOLEAN_MANAGER, "Allowed", false);
		compoundManager.addSecondary(FormatUtilities.STRING_MANAGER, "Level", true);
		ArrayFormatManager<Compound> manager =
				new ArrayFormatManager<>(compoundManager, '\n', ',');
		assertThrows(IllegalArgumentException.class, () -> manager.convertIndirect("3|LEVEL=Hard|"));
		assertThrows(IllegalArgumentException.class, () -> manager.convertIndirect("3|LEVEL=Hard||ALLOWED=false"));
		assertThrows(IllegalArgumentException.class, () -> manager.convertIndirect("3||LEVEL=Hard|ALLOWED=false"));
		assertThrows(IllegalArgumentException.class, () -> manager.convertIndirect("3,,4|LEVEL=Hard|ALLOWED=false"));
		assertThrows(IllegalArgumentException.class, () -> manager.convertIndirect(",3,4|LEVEL=Hard|ALLOWED=false"));
		assertThrows(IllegalArgumentException.class, () -> manager.convertIndirect("3,4,|LEVEL=Hard|ALLOWED=false"));
	}

	@Test
	public void testInvalidConvertIndirectBadAssociations()
	{
		CompoundFormatManager<Number> compoundManager =
				new CompoundFormatManager<>(FormatUtilities.NUMBER_MANAGER, '|');
		compoundManager.addSecondary(FormatUtilities.BOOLEAN_MANAGER, "Allowed", false);
		compoundManager.addSecondary(FormatUtilities.STRING_MANAGER, "Level", true);
		ArrayFormatManager<Compound> manager =
				new ArrayFormatManager<>(compoundManager, '\n', ',');
		assertThrows(IllegalArgumentException.class, () -> manager.convert("3|LEVEL=Hard|SOUND=Bell"));
	}

	@Test
	public void testConvertNoArray()
	{
		CompoundFormatManager<Number> compoundManager =
				new CompoundFormatManager<>(FormatUtilities.NUMBER_MANAGER, '|');
		compoundManager.addSecondary(FormatUtilities.BOOLEAN_MANAGER, "Allowed", false);
		compoundManager.addSecondary(FormatUtilities.STRING_MANAGER, "Level", true);
		ArrayFormatManager<Compound> manager =
				new ArrayFormatManager<>(compoundManager, '\n', ',');
		Compound[] c = manager.convert("3|LEVEL=Hard");
		assertEquals(1, c.length);
		assertEquals("3", c[0].getPrimaryUnconverted());
		assertEquals("Hard", c[0].getSecondary("Level").getUnconverted());
		assertEquals("Hard", c[0].getSecondary("LEVEL").getUnconverted());
		/*
		 * For now unassigned optional items return null
		 * 
		 * There is an interesting debate to be had here depending on how optional items
		 * are used
		 */
		assertNull(c[0].getSecondary("Allowed"));
		assertEquals("3|LEVEL=Hard", manager.unconvert(c));
		Compound[] c2 = manager.convert("4|LEVEL=Easy|ALLOWED=False");
		assertEquals(1, c2.length);
		assertEquals("4", c2[0].getPrimaryUnconverted());
		assertEquals("Easy", c2[0].getSecondary("Level").getUnconverted());
		assertEquals("false", c2[0].getSecondary("ALLOWED").getUnconverted());
		assertEquals("4|ALLOWED=false|LEVEL=Easy", manager.unconvert(c2));
	}

	@Test
	public void testConvertCompoundArray()
	{
		CompoundFormatManager<Number> compoundManager =
				new CompoundFormatManager<>(FormatUtilities.NUMBER_MANAGER, '|');
		compoundManager.addSecondary(FormatUtilities.BOOLEAN_MANAGER, "Allowed", false);
		compoundManager.addSecondary(FormatUtilities.STRING_MANAGER, "Level", true);
		ArrayFormatManager<Compound> manager =
				new ArrayFormatManager<>(compoundManager, '\n', ',');
		Compound[] c = manager.convert("3,4|LEVEL=Hard");
		assertEquals(2, c.length);
		assertEquals("3", c[0].getPrimaryUnconverted());
		assertEquals("Hard", c[0].getSecondary("Level").getUnconverted());
		assertEquals("4", c[1].getPrimaryUnconverted());
		assertEquals("Hard", c[1].getSecondary("Level").getUnconverted());
		/*
		 * For now unassigned optional items return null
		 * 
		 * There is an interesting debate to be had here depending on how optional items
		 * are used
		 */
		assertNull(c[0].getSecondary("Allowed"));
		assertEquals("3,4|LEVEL=Hard", manager.unconvert(c));
		Compound[] c2 = manager.convert("4,5|LEVEL=Easy|ALLOWED=False");
		assertEquals(2, c2.length);
		assertEquals("4", c2[0].getPrimaryUnconverted());
		assertEquals("Easy", c2[0].getSecondary("Level").getUnconverted());
		assertEquals("false", c2[0].getSecondary("ALLOWED").getUnconverted());
		assertEquals("4,5|ALLOWED=false|LEVEL=Easy", manager.unconvert(c2));
	}

	public void testUnconvertInvalid()
	{
		CompoundFormatManager<Number> compoundManager =
				new CompoundFormatManager<>(FormatUtilities.NUMBER_MANAGER, '|');
		compoundManager.addSecondary(FormatUtilities.BOOLEAN_MANAGER, "Allowed", false);
		compoundManager.addSecondary(FormatUtilities.STRING_MANAGER, "School", true);
		CompoundFormatManager<String> manager2 =
				new CompoundFormatManager<>(FormatUtilities.STRING_MANAGER, '|');
		Compound c = new DirectCompound(1, compoundManager);
		c.addSecondary(new NamedIndirect<String>("School", FormatUtilities.STRING_MANAGER, "Illusion"));
		manager2.addSecondary(FormatUtilities.BOOLEAN_MANAGER, "Visible", true);
		assertThrows(IllegalArgumentException.class, () -> manager2.unconvert(c));
	}

	@Test
	public void testConvertIndirectNoArray()
	{
		CompoundFormatManager<Number> compoundManager =
				new CompoundFormatManager<>(FormatUtilities.NUMBER_MANAGER, '|');
		compoundManager.addSecondary(FormatUtilities.BOOLEAN_MANAGER, "Allowed", false);
		compoundManager.addSecondary(FormatUtilities.STRING_MANAGER, "Level", true);
		ArrayFormatManager<Compound> manager =
				new ArrayFormatManager<>(compoundManager, '\n', ',');
		Indirect<Compound[]> in = manager.convertIndirect("3|LEVEL=Hard");
		Compound[] c = in.get();
		assertEquals(1, c.length);
		assertEquals("3", c[0].getPrimaryUnconverted());
		assertEquals("Hard", c[0].getSecondary("Level").getUnconverted());
		assertEquals("Hard", c[0].getSecondary("LEVEL").getUnconverted());
		/*
		 * For now unassigned optional items return null
		 * 
		 * There is an interesting debate to be had here depending on how optional items
		 * are used
		 */
		assertNull(c[0].getSecondary("Allowed"));
		assertEquals("3|LEVEL=Hard", manager.unconvert(c));
		assertEquals("3|LEVEL=Hard", in.getUnconverted());
		Indirect<Compound[]> in2 = manager.convertIndirect("4|LEVEL=Easy|ALLOWED=False");
		Compound[] c2 = in2.get();
		assertEquals(1, c2.length);
		assertEquals("4", c2[0].getPrimaryUnconverted());
		assertEquals("Easy", c2[0].getSecondary("Level").getUnconverted());
		assertEquals("false", c2[0].getSecondary("ALLOWED").getUnconverted());
		assertEquals("4|ALLOWED=false|LEVEL=Easy", manager.unconvert(c2));
		assertEquals("4|ALLOWED=false|LEVEL=Easy", in2.getUnconverted());
	}

	@Test
	public void testConvertIndirectCompoundArray()
	{
		CompoundFormatManager<Number> compoundManager =
				new CompoundFormatManager<>(FormatUtilities.NUMBER_MANAGER, '|');
		compoundManager.addSecondary(FormatUtilities.BOOLEAN_MANAGER, "Allowed", false);
		compoundManager.addSecondary(FormatUtilities.STRING_MANAGER, "Level", true);
		ArrayFormatManager<Compound> manager =
				new ArrayFormatManager<>(compoundManager, '\n', ',');
		Indirect<Compound[]> i = manager.convertIndirect("3,4|LEVEL=Hard");
		Compound[] c = i.get();
		assertEquals(2, c.length);
		assertEquals("3", c[0].getPrimaryUnconverted());
		assertEquals("Hard", c[0].getSecondary("Level").getUnconverted());
		assertEquals("4", c[1].getPrimaryUnconverted());
		assertEquals("Hard", c[1].getSecondary("Level").getUnconverted());
		/*
		 * For now unassigned optional items return null
		 * 
		 * There is an interesting debate to be had here depending on how optional items
		 * are used
		 */
		assertNull(c[0].getSecondary("Allowed"));
		assertEquals("3,4|LEVEL=Hard", manager.unconvert(c));
		assertEquals("3,4|LEVEL=Hard", i.getUnconverted());
		Indirect<Compound[]> i2 = manager.convertIndirect("4,5|LEVEL=Easy|ALLOWED=False");
		Compound[] c2 = i2.get();
		assertEquals(2, c2.length);
		assertEquals("4", c2[0].getPrimaryUnconverted());
		assertEquals("Easy", c2[0].getSecondary("Level").getUnconverted());
		assertEquals("false", c2[0].getSecondary("ALLOWED").getUnconverted());
		assertEquals("4,5|ALLOWED=false|LEVEL=Easy", manager.unconvert(c2));
		assertEquals("4,5|ALLOWED=false|LEVEL=Easy", i2.getUnconverted());
	}

}
