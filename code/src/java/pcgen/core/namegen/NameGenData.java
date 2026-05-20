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
 * XML files. {@link #allVars} holds every {@link DataElement} keyed by id
 * (lists, rules, rulesets, separators); {@link #categories} is the
 * category-name -> ruleset list index used to drive UI pickers.
 */
public record NameGenData(VariableHashMap allVars,
		Map<String, List<RuleSet>> categories)
{
}
