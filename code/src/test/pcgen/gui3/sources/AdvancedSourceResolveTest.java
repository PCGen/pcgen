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
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;

import pcgen.core.Campaign;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Advanced tab's name-to-campaign resolution
 * ({@link AdvancedSourceSelectionController#resolveCampaigns}): remembered
 * names resolve against the available campaigns (matching on
 * {@link Campaign#toString()} for Swing interop), preserving order, and a name
 * that no longer resolves is skipped rather than failing.
 */
class AdvancedSourceResolveTest
{
	private static Campaign campaign(String name)
	{
		var campaign = new Campaign();
		campaign.setName(name);
		return campaign;
	}

	@Test
	void resolveCampaigns_should_resolveKnownNames_inOrder()
	{
		var a = campaign("Alpha");
		var b = campaign("Beta");
		var resolved = AdvancedSourceSelectionController.resolveCampaigns(
				List.of("Beta", "Alpha"), List.of(a, b));
		assertEquals(2, resolved.size());
		assertSame(b, resolved.get(0));
		assertSame(a, resolved.get(1));
	}

	@Test
	void resolveCampaigns_should_skipMissingName_notFail()
	{
		var a = campaign("Alpha");
		var resolved = AdvancedSourceSelectionController.resolveCampaigns(
				List.of("Alpha", "Ghost"), List.of(a));
		assertEquals(List.of(a), resolved);
	}

	@Test
	void resolveCampaigns_should_returnEmpty_when_allNamesMissing()
	{
		var a = campaign("Alpha");
		var resolved = AdvancedSourceSelectionController.resolveCampaigns(
				List.of("Ghost1", "Ghost2"), List.of(a));
		assertEquals(0, resolved.size());
	}

	@Test
	void resolveCampaigns_should_returnEmpty_when_noNames()
	{
		var a = campaign("Alpha");
		var resolved = AdvancedSourceSelectionController.resolveCampaigns(List.of(), List.of(a));
		assertEquals(0, resolved.size());
	}
}
