/*
 * Copyright 2018 (C) Tom Parker <thpr@users.sourceforge.net>
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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formatmanager.SimpleFormatManagerLibrary;
import pcgen.testsupport.TestSupport;

/**
 * Test the CompoundFormatFactory class
 */
public class CompoundFormatFactoryTest
{
	@Test
	public void testBuild()
	{
		SimpleFormatManagerLibrary library = new SimpleFormatManagerLibrary();
		library.addFormatManager(FormatUtilities.NUMBER_MANAGER);
		library.addFormatManager(FormatUtilities.BOOLEAN_MANAGER);
		library.addFormatManager(FormatUtilities.STRING_MANAGER);
		assertEquals("COMPOUND", TestSupport.COMPOUND_MANAGER.getBuilderBaseFormat());
		TestSupport.COMPOUND_MANAGER.build(Optional.empty(), Optional.of("NUMBER,STRING=Level"), library);
	}

	@Test
	public void testBuildNoFormat()
	{
		SimpleFormatManagerLibrary library = new SimpleFormatManagerLibrary();
		library.addFormatManager(FormatUtilities.BOOLEAN_MANAGER);
		library.addFormatManager(FormatUtilities.STRING_MANAGER);
		assertEquals("COMPOUND", TestSupport.COMPOUND_MANAGER.getBuilderBaseFormat());
		assertThrows(IllegalArgumentException.class, () -> TestSupport.COMPOUND_MANAGER.build(Optional.empty(), Optional.of("NUMBER,STRING=Level"), library));
	}

	@Test
	public void testBuildBadSyntax()
	{
		SimpleFormatManagerLibrary library = new SimpleFormatManagerLibrary();
		library.addFormatManager(FormatUtilities.BOOLEAN_MANAGER);
		library.addFormatManager(FormatUtilities.STRING_MANAGER);
		assertEquals("COMPOUND", TestSupport.COMPOUND_MANAGER.getBuilderBaseFormat());
		assertThrows(IllegalArgumentException.class, () -> TestSupport.COMPOUND_MANAGER.build(Optional.empty(), Optional.of(",NUMBER,STRING=Level"), library));
		assertThrows(IllegalArgumentException.class, () -> TestSupport.COMPOUND_MANAGER.build(Optional.empty(), Optional.of("NUMBER,STRING=Level,"), library));
		assertThrows(IllegalArgumentException.class, () -> TestSupport.COMPOUND_MANAGER.build(Optional.empty(), Optional.of("NUMBER,,STRING=Level"), library));
		assertThrows(IllegalArgumentException.class, () -> TestSupport.COMPOUND_MANAGER.build(Optional.empty(), Optional.of("NUMBER,STRING==Level"), library));
	}

}
