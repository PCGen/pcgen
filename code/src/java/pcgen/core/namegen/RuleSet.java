/*
 * Copyright 2003 (C) Devon Jones
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
 * Immutable bag of {@link Rule} alternatives keyed by display title.
 * The {@code usage} field carries the legacy {@code "final"} marker that
 * tells the facade which rulesets the user picks directly versus the
 * shared building-block rulesets ({@code "private"}).
 *
 * <p>Cumulative weights are precomputed at construction so {@link #pick()}
 * is O(log n) per call.
 */
public final class RuleSet
{
	private final String id;
	private final String title;
	private final String usage;
	private final List<Rule> rules;
	private final Rule[] positiveRules;
	private final int[] cumulativeWeights;
	private final int totalWeight;

	public RuleSet(String id, String title, String usage, List<Rule> rules)
	{
		this.id = id;
		this.title = title;
		this.usage = usage;
		this.rules = List.copyOf(rules);
		Rule[] positives = this.rules.stream()
				.filter(r -> r.weight() > 0)
				.toArray(Rule[]::new);
		int[] cumulative = new int[positives.length];
		int running = 0;
		for (int i = 0; i < positives.length; i++)
		{
			running += positives[i].weight();
			cumulative[i] = running;
		}
		this.positiveRules = positives;
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

	public String usage()
	{
		return usage;
	}

	public List<Rule> rules()
	{
		return rules;
	}

	/** Title is what the combo boxes show for a ruleset. */
	@Override
	public String toString()
	{
		return title;
	}

	/**
	 * Pick one rule by weighted random. Zero-weight entries are skipped.
	 *
	 * @return the chosen rule, or {@code null} if no rule has positive
	 *         weight
	 */
	public Rule pick()
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
		return positiveRules[idx];
	}
}
