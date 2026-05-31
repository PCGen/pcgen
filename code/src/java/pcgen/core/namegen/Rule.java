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

import java.util.ArrayList;
import java.util.List;

/**
 * One alternative within a {@link RuleSet}: a sequence of {@link RulePart}s
 * that together produce a name. References inside the parts are linked at
 * load time, so generation is a flat traversal — no map lookups, no
 * runtime casts.
 */
public record Rule(int weight, String displayLabel, List<RulePart> parts)
{
	public Rule
	{
		parts = List.copyOf(parts);
	}

	/** Expand the rule into the value sequence consumed by the assembler. */
	public List<DataValue> generate()
	{
		List<DataValue> out = new ArrayList<>();
		for (RulePart part : parts)
		{
			out.addAll(part.generate());
		}
		return out;
	}

	/**
	 * Used by the Advanced "Structure" combo box, which renders rule
	 * alternatives like {@code "[Given] [Surname] "}.
	 */
	@Override
	public String toString()
	{
		return displayLabel;
	}
}
