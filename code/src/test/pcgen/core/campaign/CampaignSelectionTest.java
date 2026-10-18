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
package pcgen.core.campaign;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pcgen.core.Campaign;

import org.junit.jupiter.api.Test;

/** Verifies the failure baseline shared by campaign planning and explanations. */
class CampaignSelectionTest
{
	private static Campaign campaign(String name)
	{
		var campaign = new Campaign();
		campaign.setName(name);
		return campaign;
	}

	@Test
	void newFailuresWith_should_ignoreExistingFailures_and_preserveEngineOrder()
	{
		var broken = campaign("Already Broken");
		var healthy = campaign("Selected");
		var candidate = campaign("New Book");
		var calls = new ArrayList<List<Campaign>>();
		var selection = new CampaignSelection(List.of(broken, healthy), campaigns -> {
			calls.add(List.copyOf(campaigns));
			return campaigns.size() == 2 ? List.of(broken) : List.of(healthy, candidate, broken);
		});

		var failures = selection.newFailuresWith(List.of(candidate));

		assertEquals(List.of(healthy, candidate), failures);
		assertEquals(List.of(List.of(broken, healthy), List.of(broken, healthy, candidate)), calls);
	}

	@Test
	void newFailuresWith_should_tolerateAnExistingFailure_withTheSameKey()
	{
		var broken = campaign("Same Key");
		var candidate = campaign("same key");
		var selection = new CampaignSelection(List.of(broken),
				campaigns -> campaigns.size() == 1 ? List.of(broken) : List.of(broken, candidate));

		var failures = selection.newFailuresWith(List.of(candidate));

		assertTrue(failures.isEmpty(), "the same campaign key must count as the existing failure");
	}

	@Test
	void newFailuresWith_should_skipAnEmptyBaseline()
	{
		var candidate = campaign("New Book");
		var calls = new ArrayList<List<Campaign>>();
		var selection = new CampaignSelection(Collections.emptyList(), campaigns -> {
			calls.add(List.copyOf(campaigns));
			return List.of(candidate);
		});

		assertEquals(List.of(candidate), selection.newFailuresWith(List.of(candidate)));
		assertEquals(List.of(List.of(candidate)), calls);
	}
}
