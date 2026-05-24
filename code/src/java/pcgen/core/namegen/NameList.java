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
package pcgen.core.namegen;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Immutable named bag of weighted values, picked from at name-generation
 * time. Replaces the legacy {@code DDList}.
 */
public record NameList(String id, String title, List<WeightedDataValue> values)
{
	public NameList
	{
		values = List.copyOf(values);
	}

	/**
	 * Pick one value by weighted random. Zero-weight entries are skipped
	 * so authors can disable an entry without removing it.
	 *
	 * @return the chosen value, or {@code null} if the list has no
	 *         positive-weight entries
	 */
	public WeightedDataValue pick()
	{
		int total = values.stream().mapToInt(WeightedDataValue::getWeight).filter(w -> w > 0).sum();
		if (total <= 0)
		{
			return null;
		}
		int roll = ThreadLocalRandom.current().nextInt(total) + 1;
		int running = 0;
		for (WeightedDataValue v : values)
		{
			int w = v.getWeight();
			if (w <= 0)
			{
				continue;
			}
			running += w;
			if (roll <= running)
			{
				return v;
			}
		}
		return values.get(values.size() - 1);
	}
}
