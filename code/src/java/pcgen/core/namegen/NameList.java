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

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Immutable named bag of weighted values, picked from at name-generation
 * time. Replaces the legacy {@code DDList}.
 *
 * <p>Cumulative weights are precomputed at construction so {@link #pick()}
 * is O(log n) per call — important because long lists (city names,
 * surnames) are picked from inside generation loops.
 */
public final class NameList
{
	private final String id;
	private final String title;
	private final List<WeightedDataValue> values;
	private final WeightedDataValue[] positiveEntries;
	private final int[] cumulativeWeights;
	private final int totalWeight;

	public NameList(String id, String title, List<WeightedDataValue> values)
	{
		this.id = id;
		this.title = title;
		this.values = List.copyOf(values);
		WeightedDataValue[] positives = this.values.stream()
				.filter(v -> v.getWeight() > 0)
				.toArray(WeightedDataValue[]::new);
		int[] cumulative = new int[positives.length];
		int running = 0;
		for (int i = 0; i < positives.length; i++)
		{
			running += positives[i].getWeight();
			cumulative[i] = running;
		}
		this.positiveEntries = positives;
		this.cumulativeWeights = cumulative;
		this.totalWeight = running;
	}

	public String id()
	{
		return id;
	}

	public String title()
	{
		return title;
	}

	public List<WeightedDataValue> values()
	{
		return values;
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
		if (totalWeight <= 0)
		{
			return null;
		}
		int roll = ThreadLocalRandom.current().nextInt(totalWeight) + 1;
		int idx = Arrays.binarySearch(cumulativeWeights, roll);
		if (idx < 0)
		{
			idx = -idx - 1;
		}
		return positiveEntries[idx];
	}
}
