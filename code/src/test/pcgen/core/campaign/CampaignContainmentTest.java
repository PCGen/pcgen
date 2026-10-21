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

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import pcgen.core.Campaign;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link CampaignContainment}, which answers whether a campaign
 * is already loaded through one the user has selected.
 *
 * <p>PCGen's data ships collections that load other books through their
 * {@code PCC:} files, and those relationships nest: "Pathfinder RPG for Game
 * Masters" loads the Bestiary, which loads its own reduced "Player Options
 * Only" edition. A book two levels down is just as loaded as one named
 * directly.
 */
class CampaignContainmentTest
{
	private static Campaign campaign(String name)
	{
		var campaign = new Campaign();
		campaign.setName(name);
		return campaign;
	}

	/** Models the {@code PCC:} lines of a collection, without touching {@code Globals}. */
	private static final class FakeContents implements Function<Campaign, List<Campaign>>
	{
		private final Map<Campaign, List<Campaign>> contents = new IdentityHashMap<>();

		void contains(Campaign container, Campaign... contained)
		{
			contents.put(container, List.of(contained));
		}

		@Override
		public List<Campaign> apply(Campaign campaign)
		{
			return contents.getOrDefault(campaign, Collections.emptyList());
		}
	}

	@Test
	void campaignLoadedDirectly_isFound()
	{
		var bundle = campaign("Pathfinder RPG for Game Masters");
		var book = campaign("Advanced Player's Guide");
		var contents = new FakeContents();
		contents.contains(bundle, book);

		assertEquals(Optional.of(bundle), CampaignContainment.containerOf(book, List.of(bundle), contents));
	}

	@Test
	void campaignWithTheSameKeyAsALoadedBook_isFound()
	{
		var bundle = campaign("Collection");
		var book = campaign("Core Rulebook");
		var sought = campaign("core rulebook");
		var contents = new FakeContents();
		contents.contains(bundle, book);

		assertEquals(Optional.of(bundle), CampaignContainment.containerOf(sought, List.of(bundle), contents));
	}

	@Test
	void campaignLoadedThroughAnotherCollection_namesTheSelectedOne()
	{
		var bundle = campaign("Pathfinder RPG for Game Masters");
		var bestiary = campaign("Bestiary");
		var reduced = campaign("Bestiary ~ Player Options Only");
		var contents = new FakeContents();
		contents.contains(bundle, bestiary);
		contents.contains(bestiary, reduced);

		assertEquals(Optional.of(bundle), CampaignContainment.containerOf(reduced, List.of(bundle), contents),
				"the collection the user selected is the one they can act on");
	}

	@Test
	void campaignNobodyLoads_hasNoContainer()
	{
		var bundle = campaign("Pathfinder RPG for Game Masters");
		var book = campaign("Bonus Bestiary");

		assertEquals(Optional.empty(), CampaignContainment.containerOf(book, List.of(bundle), new FakeContents()));
	}

	@Test
	void campaignThatIsItselfSelected_hasNoContainer()
	{
		var book = campaign("Core Rulebook");

		assertEquals(Optional.empty(), CampaignContainment.containerOf(book, List.of(book), new FakeContents()),
				"a selected book is not loaded through anything, it is simply selected");
	}

	/** Data is hand-authored, so a collection may name itself or a sibling that names it back. */
	@Test
	void collectionThatContainsItself_doesNotLoopForever()
	{
		var bundle = campaign("Self Referential");
		var book = campaign("Core Rulebook");
		var contents = new FakeContents();
		contents.contains(bundle, bundle);

		assertEquals(Optional.empty(), CampaignContainment.containerOf(book, List.of(bundle), contents));
	}

	@Test
	void collectionsThatContainEachOther_doNotLoopForever()
	{
		var first = campaign("First");
		var second = campaign("Second");
		var book = campaign("Core Rulebook");
		var contents = new FakeContents();
		contents.contains(first, second);
		contents.contains(second, first);

		assertEquals(Optional.empty(), CampaignContainment.containerOf(book, List.of(first), contents));
	}
}
