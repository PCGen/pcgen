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
import java.util.Map;

/**
 * Immutable container for the data loaded from a directory of random-name
 * XML files. References between rules, rulesets, and lists are resolved
 * at load time, so generation reads only from these maps.
 *
 * <p>{@link #unresolvedReferences()} lists every {@code GETLIST}/{@code GETRULE}
 * whose target id was not present after the load completed. The loader
 * skips such parts (matching the legacy engine's silent behaviour); the
 * list lets callers and tests detect data-file bugs.
 */
public record NameGenData(
		Map<String, NameList> lists,
		Map<String, RuleSet> rulesets,
		Map<String, List<RuleSet>> categories,
		List<UnresolvedReference> unresolvedReferences)
{
	public NameGenData
	{
		lists = Map.copyOf(lists);
		rulesets = Map.copyOf(rulesets);
		categories = Map.copyOf(categories);
		unresolvedReferences = List.copyOf(unresolvedReferences);
	}

	/** A {@code GETLIST}/{@code GETRULE} whose target id wasn't found. */
	public record UnresolvedReference(Kind kind, String targetId)
	{
		public enum Kind { GETLIST, GETRULE }
	}
}
