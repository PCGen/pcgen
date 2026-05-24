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
 * One element of a {@link Rule}: either a reference to a {@link NameList}
 * or another {@link RuleSet}, or a literal separator. Resolved once at
 * load time so name generation needs at most one map lookup per part.
 */
public sealed interface RulePart
{
	/** Materialise the part into one or more {@link DataValue}s. */
	List<DataValue> generate();

	/** Human-readable label for {@code Rule.toString()} (e.g. "[Given] "). */
	String label();

	/** A reference to a {@link NameList}: pick one weighted value. */
	record ListRef(NameList list) implements RulePart
	{
		@Override
		public List<DataValue> generate()
		{
			WeightedDataValue picked = list.pick();
			return picked == null ? List.of() : List.of(picked);
		}

		@Override
		public String label()
		{
			return "[" + list.title() + "] ";
		}
	}

	/**
	 * A reference to a {@link RuleSet}, resolved through a shared map.
	 * Rulesets reference each other, so we can't hold the target record
	 * directly at construction time — the map is populated and frozen
	 * after the loader has built every {@code RuleSet}, then handed in.
	 */
	record RuleSetRef(String targetId, String targetTitle,
			Map<String, RuleSet> rulesets) implements RulePart
	{
		@Override
		public List<DataValue> generate()
		{
			RuleSet rs = rulesets.get(targetId);
			if (rs == null)
			{
				return List.of();
			}
			Rule picked = rs.pick();
			return picked == null ? List.of() : picked.generate();
		}

		@Override
		public String label()
		{
			return "[" + targetTitle + "] ";
		}
	}

	/** Literal separator emitted as-is into the generated name. */
	enum Literal implements RulePart
	{
		SPACE(" "), HYPHEN("-"), CR("\n");

		private final DataValue value;

		Literal(String text)
		{
			this.value = new DataValue(text);
		}

		@Override
		public List<DataValue> generate()
		{
			return List.of(value);
		}

		@Override
		public String label()
		{
			return "";
		}
	}
}
