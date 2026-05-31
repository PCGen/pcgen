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

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Immutable bag of {@link Rule} alternatives keyed by display title.
 * The {@code usage} field carries the legacy {@code "final"} marker that
 * tells the facade which rulesets the user picks directly versus the
 * shared building-block rulesets ({@code "private"}).
 */
public record RuleSet(String id, String title, String usage, List<Rule> rules)
{
	public RuleSet
	{
		rules = List.copyOf(rules);
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
		int total = rules.stream().mapToInt(Rule::weight).filter(w -> w > 0).sum();
		if (total <= 0)
		{
			return null;
		}
		int roll = ThreadLocalRandom.current().nextInt(total) + 1;
		int running = 0;
		for (Rule r : rules)
		{
			int w = r.weight();
			if (w <= 0)
			{
				continue;
			}
			running += w;
			if (roll <= running)
			{
				return r;
			}
		}
		return rules.get(rules.size() - 1);
	}
}
