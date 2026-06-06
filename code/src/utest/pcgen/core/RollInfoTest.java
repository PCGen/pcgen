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

class RollInfoTest
{
	@Test
	void simpleRollRoundTrips()
	{
		assertEquals("3d6", new RollInfo("3d6").toString());
	}

	@Test
	void singleDieDefaultsTimesToOne()
	{
		assertEquals("1d8", new RollInfo("d8").toString());
	}

	@Test
	void positiveModifierRoundTrips()
	{
		assertEquals("1d20+5", new RollInfo("1d20+5").toString());
	}

	@Test
	void negativeModifierRoundTrips()
	{
		assertEquals("1d20-2", new RollInfo("1d20-2").toString());
	}

	@Test
	void keepTopRoundTrips()
	{
		assertEquals("4d6/3", new RollInfo("4d6/3").toString());
	}

	@Test
	void keepBottomRoundTrips()
	{
		assertEquals("4d6\\1", new RollInfo("4d6\\1").toString());
	}

	@Test
	void explicitKeepListRoundTrips()
	{
		assertEquals("4d6|1,3", new RollInfo("4d6|1,3").toString());
	}

	@Test
	void rerollBelowRoundTrips()
	{
		assertEquals("3d6m2", new RollInfo("3d6m2").toString());
	}

	@Test
	void rerollAboveRoundTrips()
	{
		assertEquals("3d6M5", new RollInfo("3d6M5").toString());
	}

	@Test
	void totalFloorRoundTrips()
	{
		assertEquals("1d8t2", new RollInfo("1d8t2").toString());
	}

	@Test
	void totalCeilingRoundTrips()
	{
		assertEquals("1d8T6", new RollInfo("1d8T6").toString());
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

	/**
	 * Regression: parser used to call {@code nextToken(" ")} in the modifier
	 * and total-clamp branches, which ignored the {@code t}/{@code T}
	 * delimiters and greedily consumed them into the integer. So a roll like
	 * {@code 1d20+5t2} threw {@link NumberFormatException} on {@code "5t2"}.
	 */
	@Test
	void modifierFollowedByTotalClampRoundTrips()
	{
		assertEquals("1d20+5t2", new RollInfo("1d20+5t2").toString());
		assertEquals("1d20+5T18", new RollInfo("1d20+5T18").toString());
		assertEquals("1d20-3t1", new RollInfo("1d20-3t1").toString());
		assertEquals("1d8m2M7+1t2T6", new RollInfo("1d8m2M7+1t2T6").toString());
	}

	@Test
	void emptyStringThrows()
	{
		assertThrows(IllegalArgumentException.class, () -> new RollInfo(""));
	}

	@Test
	void garbageThrows()
	{
		assertThrows(IllegalArgumentException.class, () -> new RollInfo("garbage"));
	}

	@Test
	void zeroSidesThrows()
	{
		assertThrows(IllegalArgumentException.class, () -> new RollInfo("1d0"));
	}

	@Test
	void keepTopExceedingTimesThrows()
	{
		assertThrows(IllegalArgumentException.class, () -> new RollInfo("3d6/4"));
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
}
