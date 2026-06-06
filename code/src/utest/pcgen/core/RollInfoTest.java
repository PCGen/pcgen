/*
 * Copyright 2026 (C) Vest <Vest@users.noreply.github.com>
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
package pcgen.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class RollInfoTest
{
	/**
	 * Round-trip cases covering each notation element alone, combinations
	 * exercising the {@code toString()} emit-block order, the implicit
	 * times=1 (no leading number), and the keep-list shape normalisations
	 * that collapse to {@code /n} or {@code \n}.
	 */
	@ParameterizedTest(name = "{0}")
	@ValueSource(strings = {
			// Singletons
			"3d6",
			"1d20+5",
			"1d20-2",
			"4d6/3",
			"4d6\\1",
			"4d6|1,3",
			"3d6m2",
			"3d6M5",
			"1d8t2",
			"1d8T6",
			// Combinations exercising toString() emit-block order
			"4d6/3+2",
			"2d20\\1+3",
			"1d8m2+1",
			"1d8M7-1",
			"1d20+5t2",
			"1d20+5T18",
			"1d20-3t1",
			"1d8m2M7+1t2T6",
			// keepList contiguous runs render as /n or \n
			"4d6/2",
			"4d6\\2",
	})
	void roundTrips(String roll)
	{
		assertEquals(roll, new RollInfo(roll).toString());
	}

	@ParameterizedTest(name = "{0} -> {1}")
	@CsvSource({
			"d8,            1d8",        // implicit times=1
			"'4d6|1,2,3,4', 4d6",        // all kept -> no spec
			"'4d6|1,2',     4d6\\2",     // contiguous-bottom -> backslash form
			"'4d6|3,4',     4d6/2",      // contiguous-top -> slash form
			"'4d6|1,4',     '4d6|1,4'",  // discontinuous -> stays explicit
	})
	void normalisesToCanonicalForm(String input, String canonical)
	{
		assertEquals(canonical, new RollInfo(input).toString());
	}

	@Test
	void implicitSidesOneAcceptedByParser()
	{
		// "4" with no 'd' is documented as legal: times=4, sides=1.
		RollInfo info = new RollInfo("4");
		assertEquals(4, info.getTimes());
		assertEquals(1, info.getSides());
	}

	@Test
	void exposesSidesAndTimes()
	{
		RollInfo info = new RollInfo("4d6");
		assertEquals(6, info.getSides());
		assertEquals(4, info.getTimes());
	}

	@Test
	void copyConstructorScalesTimes()
	{
		RollInfo source = new RollInfo("1d6");
		RollInfo copy = new RollInfo(source, 4);
		assertEquals(4, copy.getTimes());
		assertEquals(6, copy.getSides());
		assertEquals(1, source.getTimes(), "source must not be mutated");
	}

	@Test
	void copyConstructorPreservesAllFields()
	{
		RollInfo source = new RollInfo("4d6/3+2");
		RollInfo copy = new RollInfo(source, 1);
		assertEquals(source.toString(), copy.toString());
	}

	@Test
	void copyConstructorClonesKeepList()
	{
		RollInfo source = new RollInfo("4d6/3");
		RollInfo copy = new RollInfo(source, 1);
		assertEquals("4d6/3", copy.toString());
	}

	/**
	 * Regression: the copy constructor used to clone {@code keepList} as-is
	 * even when {@code timesMultiplier > 1}, leaving an undersized array that
	 * caused {@link RollInfo#toString()} to read past the end. Scaling drops
	 * the keep-list because there is no canonical way to extend it.
	 */
	@Test
	void copyConstructorWithKeepListAndMultiplierDropsKeepList()
	{
		RollInfo source = new RollInfo("4d6/3");
		RollInfo copy = new RollInfo(source, 2);
		assertEquals("8d6", copy.toString());
	}

	@ParameterizedTest(name = "rejects: {0}")
	@ValueSource(strings = {
			"",          // empty
			"garbage",   // no digits / no d
			"1d0",       // sides < 1
			"3d6/4",     // keepTop > times
			"3d6\\4",    // keepBottom > times
			"1d-6",      // negative sides
	})
	void rejectsBadInput(String roll)
	{
		assertThrows(IllegalArgumentException.class, () -> new RollInfo(roll));
	}

	@Test
	void validateRollStringReturnsEmptyOnSuccess()
	{
		assertEquals("", RollInfo.validateRollString("3d6"));
	}

	@Test
	void validateRollStringReturnsErrorOnFailure()
	{
		String err = RollInfo.validateRollString("garbage");
		assertEquals(false, err.isEmpty(), "expected non-empty error message");
	}

	/**
	 * {@link RollInfo#validateRollString} and the {@code RollInfo(String)}
	 * constructor must agree: validate-empty iff the constructor accepts.
	 */
	@ParameterizedTest(name = "{0}")
	@ValueSource(strings = {"3d6", "1d20+5", "4d6/3", "garbage", "1d0", ""})
	void validateAgreesWithConstructor(String roll)
	{
		boolean validatorOk = RollInfo.validateRollString(roll).isEmpty();
		boolean constructorOk;
		try
		{
			new RollInfo(roll);
			constructorOk = true;
		}
		catch (IllegalArgumentException e)
		{
			constructorOk = false;
		}
		assertEquals(validatorOk, constructorOk);
	}
}
