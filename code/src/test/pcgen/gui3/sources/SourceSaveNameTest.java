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
 */
package pcgen.gui3.sources;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import pcgen.core.GameMode;

import org.junit.jupiter.api.Test;

class SourceSaveNameTest
{
	private static SourceBundle builtIn(String name)
	{
		return new SourceBundle(name, new GameMode("m"), List.of(), false, null);
	}

	private static SourceBundle custom(String name)
	{
		return new SourceBundle(name, new GameMode("m"), List.of(), true, null);
	}

	@Test
	void classify_should_returnNew_when_nameUnused()
	{
		var sources = List.of(builtIn("Core"), custom("Mine"));
		assertEquals(SourceSelectionBridge.NameClass.NEW,
				SourceSelectionBridge.classifyName("Fresh", sources));
	}

	@Test
	void classify_should_returnOverwriteCustom_when_nameMatchesModifiable()
	{
		var sources = List.of(builtIn("Core"), custom("Mine"));
		assertEquals(SourceSelectionBridge.NameClass.OVERWRITE_CUSTOM,
				SourceSelectionBridge.classifyName("Mine", sources));
	}

	@Test
	void classify_should_returnBlockBuiltin_when_nameMatchesBundled()
	{
		var sources = List.of(builtIn("Core"), custom("Mine"));
		assertEquals(SourceSelectionBridge.NameClass.BLOCK_BUILTIN,
				SourceSelectionBridge.classifyName("Core", sources));
	}

	@Test
	void classify_should_beCaseSensitiveExactMatch_treatingDifferentAsNew()
	{
		var sources = List.of(custom("Mine"));
		assertEquals(SourceSelectionBridge.NameClass.NEW,
				SourceSelectionBridge.classifyName("mine", sources));
	}
}
