/*
 * Copyright 2008 (C) Jasper Spaans <jasperspaans@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.enumeration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Collection;
import java.util.HashSet;

import org.junit.jupiter.api.Test;


/**
 * The Class {@code TypeTest} tests that the Type
 * class is functioning correctly.
 *
 * Fixture names are namespaced ("TypeTest_*") so they don't collide with
 * data-load fixtures registered by other tests sharing the JVM.
 */
class TypeTest
{
	/**
	 * Test whether type can be sorted, by adding it to a hashset.
	 * Added to check fix on Bug with tracker nr. 2413116
	 */
	@Test
	void testSortable()
	{
		assertDoesNotThrow( () -> {
			Collection<Type> typeset = new HashSet<>();
			typeset.add(Type.getConstant("testitem 1"));
			typeset.add(Type.getConstant("testitem 2"));
		}, "type can't be sorted by adding to hashset") ;
	}

	/** Two calls with the same name return the same interned instance. */
	@Test
	void getConstantInternsByName()
	{
		assertSame(Type.getConstant("TypeTest_Alpha"), Type.getConstant("TypeTest_Alpha"));
	}

	/** The intern map is case-insensitive (CaseInsensitiveMap). */
	@Test
	void getConstantInternsCaseInsensitively()
	{
		assertSame(Type.getConstant("TypeTest_Beta"), Type.getConstant("typetest_beta"));
	}

	/** Distinct names produce distinct instances. */
	@Test
	void getConstantDistinguishesNames()
	{
		assertNotSame(Type.getConstant("TypeTest_Gamma"), Type.getConstant("TypeTest_Delta"));
	}

}
