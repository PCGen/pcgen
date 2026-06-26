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

import org.junit.jupiter.api.Test;

/**
 * Pins the intern contract of {@link Region#getConstant(String)}.
 *
 * Fixture names are namespaced ("RegionTest_*") to avoid colliding with
 * data-load fixtures registered by other tests sharing the JVM.
 */
class RegionTest
{

	/** Two calls with the same name return the same interned instance. */
	@Test
	void getConstantInternsByName()
	{
		assertSame(Region.getConstant("RegionTest_Alpha"), Region.getConstant("RegionTest_Alpha"));
	}

	/** The intern map is case-insensitive (CaseInsensitiveMap). */
	@Test
	void getConstantInternsCaseInsensitively()
	{
		assertSame(Region.getConstant("RegionTest_Beta"), Region.getConstant("regiontest_beta"));
	}

	/** Distinct names produce distinct instances. */
	@Test
	void getConstantDistinguishesNames()
	{
		assertNotSame(Region.getConstant("RegionTest_Gamma"), Region.getConstant("RegionTest_Delta"));
	}
}
