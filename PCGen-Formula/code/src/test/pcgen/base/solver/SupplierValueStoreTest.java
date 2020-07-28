/*
 * Copyright 2016-20 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.solver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import pcgen.base.formatmanager.FormatUtilities;

public class SupplierValueStoreTest
{

	@Test
	public void testIllegalGetDefault()
	{
		SupplierValueStore svs = new SupplierValueStore();
		assertThrows(NullPointerException.class, () -> svs.getValueFor(null));
		assertThrows(NullPointerException.class, () -> svs.getValueFor("NUMBER"));
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Test
	public void testIllegalAddSolverFormatGenerics()
	{
		SupplierValueStore svs = new SupplierValueStore();
		Supplier<Number> setNumber = () -> 9;
		//intentionally break generics
		svs.addSolverFormat(FormatUtilities.STRING_MANAGER, (Supplier) setNumber);
		assertFalse(svs.validateDefaults().get(),
			"Should not be able to add Format with mismatch");
	}

	@Test
	public void testIllegalAddSolverFormatDouble()
	{
		SupplierValueStore svs = new SupplierValueStore();
		svs.addSolverFormat(FormatUtilities.NUMBER_MANAGER, () -> 108);
		assertThrows(IllegalArgumentException.class, () -> svs.addSolverFormat(FormatUtilities.NUMBER_MANAGER, () -> 9));
	}

	@Test
	public void testAddSolverFormat()
	{
		SupplierValueStore svs = new SupplierValueStore();
		assertThrows(NullPointerException.class, () -> svs.addSolverFormat(null, () -> 9));
		assertThrows(NullPointerException.class, () -> svs.addSolverFormat(FormatUtilities.NUMBER_MANAGER, null));
		//But this is safe
		Supplier<? extends Number> default108 = () -> 108;
		svs.addSolverFormat(FormatUtilities.NUMBER_MANAGER, default108);
		assertEquals(108, svs.getValueFor("NUMBER"));
		assertThrows(IllegalArgumentException.class, () -> svs.addSolverFormat(FormatUtilities.NUMBER_MANAGER, () -> 111));
		//But you can set it to the same thing (maybe?)
		svs.addSolverFormat(FormatUtilities.NUMBER_MANAGER, default108);
		assertTrue(svs.validateDefaults().get());
	}
}
