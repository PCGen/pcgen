/*
 * Copyright 2026 (C) Vest <Vest@users.noreply.github.com>
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
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.enumeration;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Pins the intern contract of {@link DisplayLocation#getConstant(String)} and
 * the strict lookup of {@link DisplayLocation#valueOf(String)}.
 *
 * Fixture names are namespaced ("DisplayLocationTest_*") to avoid colliding
 * with data-load fixtures registered by other tests sharing the JVM.
 */
class DisplayLocationTest
{

	/** Two calls with the same name return the same interned instance. */
	@Test
	void getConstantInternsByName()
	{
		assertSame(DisplayLocation.getConstant("DisplayLocationTest_Alpha"),
			DisplayLocation.getConstant("DisplayLocationTest_Alpha"));
	}

	/** The intern map is case-insensitive (CaseInsensitiveMap). */
	@Test
	void getConstantInternsCaseInsensitively()
	{
		assertSame(DisplayLocation.getConstant("DisplayLocationTest_Beta"),
			DisplayLocation.getConstant("displaylocationtest_beta"));
	}

	/** Distinct names produce distinct instances. */
	@Test
	void getConstantDistinguishesNames()
	{
		assertNotSame(DisplayLocation.getConstant("DisplayLocationTest_Gamma"),
			DisplayLocation.getConstant("DisplayLocationTest_Delta"));
	}

	/** valueOf returns the already-interned instance — the sibling method NOT touched in this PR. */
	@Test
	void valueOfReturnsInternedInstance()
	{
		DisplayLocation registered = DisplayLocation.getConstant("DisplayLocationTest_Epsilon");
		assertSame(registered, DisplayLocation.valueOf("DisplayLocationTest_Epsilon"));
	}

	/** valueOf throws on an unknown name (its lookup-or-throw contract). */
	@Test
	void valueOfThrowsOnUnknownName()
	{
		assertThrows(IllegalArgumentException.class,
			() -> DisplayLocation.valueOf("DisplayLocationTest_NeverRegistered"));
	}
}
