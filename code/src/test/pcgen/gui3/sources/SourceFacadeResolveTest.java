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

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import pcgen.facade.core.SourceSelectionFacade;

import org.junit.jupiter.api.Test;

class SourceFacadeResolveTest
{
	private static SourceSelectionFacade named(String name)
	{
		return FakeSourceSelectionFacade.named(name);
	}

	@Test
	void resolve_should_findByName()
	{
		var a = named("Alpha");
		var b = named("Beta");
		assertSame(b, SourceSelectionBridge.resolveCustomFacade("Beta", List.of(a, b)).orElseThrow());
	}

	@Test
	void resolve_should_returnEmpty_when_missing()
	{
		var a = named("Alpha");
		assertTrue(SourceSelectionBridge.resolveCustomFacade("Ghost", List.of(a)).isEmpty());
	}
}
