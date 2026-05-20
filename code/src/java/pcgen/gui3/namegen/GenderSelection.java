/*
 * Copyright 2026 Vest <Vest@users.noreply.github.com>
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
 */
package pcgen.gui3.namegen;

import java.util.List;

/**
 * Pure helpers for the random-name UI's gender-selection rules. Kept
 * out of the FX controller so the logic can be exercised without
 * spinning up a JavaFX runtime.
 */
final class GenderSelection
{
	private GenderSelection()
	{
	}

	/**
	 * Pick a gender to select after the available list changes. Order:
	 * keep {@code previous} if still valid, then fall back to
	 * {@code preferred} (caller-supplied initial gender) if valid, then
	 * the first {@code available} entry. Returns the empty string when
	 * no gender is available.
	 */
	static String chooseSticky(List<String> available, String previous, String preferred)
	{
		if (available.isEmpty())
		{
			return "";
		}
		boolean previousStillValid = previous != null && available.contains(previous);
		if (previousStillValid)
		{
			return previous;
		}
		boolean preferredStillValid = preferred != null && available.contains(preferred);
		if (preferredStillValid)
		{
			return preferred;
		}
		return available.get(0);
	}
}
