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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pcgen.core.campaign.CampaignFakes.campaign;

import java.util.Collections;
import java.util.List;

import pcgen.core.Campaign;
import pcgen.core.campaign.CampaignFakes.FakePrereqs;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link CampaignAdditionPlanner}, which decides which of the
 * campaigns a user asked to add can actually be added.
 *
 * <p>The behaviour under test is the fix for a long-standing defect shared with
 * the legacy Swing panel: candidates used to be validated one at a time against
 * a growing set, so a campaign whose prerequisite sorted later in the tree was
 * rejected even though the user had selected both. Planning against the
 * complete resulting set removes that order dependency.
 */
class CampaignAdditionPlannerTest
{
	private static List<String> names(List<Campaign> campaigns)
	{
		return campaigns.stream().map(Campaign::getDisplayName).toList();
	}

	@Test
	void plan_should_acceptEveryCandidate_whenTheWholeSetQualifies()
	{
		var plan = CampaignAdditionPlanner.plan(
				Collections.emptyList(), List.of(campaign("Alpha"), campaign("Beta")), new FakePrereqs());

		assertEquals(List.of("Alpha", "Beta"), names(plan.accepted()));
		assertTrue(plan.rejected().isEmpty());
	}

	/**
	 * The reported bug. "Book of Arcane Magic" requires "DSP - Psionics
	 * Unleashed", which sorts later in the tree. Selecting both must add both.
	 */
	@Test
	void plan_should_acceptCandidate_whenItsPrerequisiteIsAnotherCandidateAddedLater()
	{
		var prereqs = new FakePrereqs();
		prereqs.require("Book of Arcane Magic", "DSP - Psionics Unleashed");

		var plan = CampaignAdditionPlanner.plan(
				Collections.emptyList(),
				List.of(campaign("Book of Arcane Magic"), campaign("DSP - Psionics Unleashed")),
				prereqs);

		assertEquals(List.of("Book of Arcane Magic", "DSP - Psionics Unleashed"), names(plan.accepted()));
		assertTrue(plan.rejected().isEmpty());
	}

	@Test
	void plan_should_rejectCandidate_whenItStaysUnqualifiedInTheWholeSet()
	{
		var prereqs = new FakePrereqs();
		prereqs.require("Book of Arcane Magic", "Never Selected");

		var plan = CampaignAdditionPlanner.plan(
				Collections.emptyList(), List.of(campaign("Book of Arcane Magic"), campaign("Beta")), prereqs);

		assertEquals(List.of("Beta"), names(plan.accepted()));
		assertEquals(List.of("Book of Arcane Magic"), names(plan.rejected()));
	}

	@Test
	void plan_should_countAlreadySelectedCampaigns_asSatisfyingPrerequisites()
	{
		var prereqs = new FakePrereqs();
		prereqs.require("Book of Arcane Magic", "DSP - Psionics Unleashed");

		var plan = CampaignAdditionPlanner.plan(
				List.of(campaign("DSP - Psionics Unleashed")),
				List.of(campaign("Book of Arcane Magic")),
				prereqs);

		assertEquals(List.of("Book of Arcane Magic"), names(plan.accepted()));
		assertTrue(plan.rejected().isEmpty());
	}

	@Test
	void plan_should_ignoreCandidate_thatIsAlreadySelected()
	{
		var alpha = campaign("Alpha");

		var plan = CampaignAdditionPlanner.plan(
				List.of(alpha), List.of(alpha, campaign("Beta")), new FakePrereqs());

		assertEquals(List.of("Beta"), names(plan.accepted()));
		assertTrue(plan.rejected().isEmpty());
	}

	@Test
	void plan_should_ignoreCandidate_withAnAlreadySelectedKey()
	{
		var selected = campaign("Same Key");
		var candidate = campaign("same key");
		var other = campaign("Other");

		var plan = CampaignAdditionPlanner.plan(List.of(selected),
				List.of(candidate, other), _ -> Collections.emptyList());

		assertEquals(List.of(other), plan.accepted());
		assertTrue(plan.rejected().isEmpty());
	}

	@Test
	void plan_should_keepTheFirstCandidate_when_campaignKeysAreEqual()
	{
		var first = campaign("Same Key");
		var second = campaign("same key");
		var other = campaign("Other");

		var plan = CampaignAdditionPlanner.plan(Collections.emptyList(),
				List.of(first, second, first, other, second), _ -> Collections.emptyList());

		assertEquals(2, plan.accepted().size());
		assertSame(first, plan.accepted().get(0));
		assertSame(other, plan.accepted().get(1));
		assertTrue(plan.rejected().isEmpty());
	}

	/**
	 * An already-selected campaign that no longer qualifies is left alone. The
	 * user did not ask to add it, so silently dropping it — or blaming the user
	 * for it in the warning — would be wrong.
	 */
	@Test
	void plan_should_notReportAlreadySelectedCampaign_asRejected()
	{
		var prereqs = new FakePrereqs();
		prereqs.require("Stale", "Never Selected");

		var plan = CampaignAdditionPlanner.plan(
				List.of(campaign("Stale")), List.of(campaign("Beta")), prereqs);

		assertEquals(List.of("Beta"), names(plan.accepted()));
		assertTrue(plan.rejected().isEmpty());
	}

	@Test
	void plan_should_notConsultTheEngine_whenThereIsNothingToAdd()
	{
		var prereqs = new FakePrereqs();

		var plan = CampaignAdditionPlanner.plan(List.of(campaign("Alpha")), Collections.emptyList(), prereqs);

		assertTrue(plan.accepted().isEmpty());
		assertTrue(plan.rejected().isEmpty());
		assertTrue(prereqs.calls.isEmpty(), "planning nothing must not hit the engine");
	}

	@Test
	void plan_should_preserveCandidateOrder_inBothOutcomes()
	{
		var prereqs = new FakePrereqs();
		prereqs.require("Beta", "Never Selected");
		prereqs.require("Delta", "Never Selected");

		var plan = CampaignAdditionPlanner.plan(
				Collections.emptyList(),
				List.of(campaign("Alpha"), campaign("Beta"), campaign("Gamma"), campaign("Delta")),
				prereqs);

		assertEquals(List.of("Alpha", "Gamma"), names(plan.accepted()));
		assertEquals(List.of("Beta", "Delta"), names(plan.rejected()));
	}

	/**
	 * A MultiSet such as {@code Pathfinder RPG Core Supplements} repackages the
	 * individual books, so it and each of them exclude one another. Refusing
	 * the whole set because the bundle is in it throws away every book the
	 * bundle covers -- which is what made "select all" report nineteen
	 * rejections. Dropping the single bundle costs one book instead of many.
	 */
	@Test
	void plan_should_dropTheBundle_ratherThanEveryBookItExcludes()
	{
		var prereqs = new FakePrereqs();
		prereqs.exclude("Core Supplements", "APG", "Bestiary", "Ultimate Magic");
		prereqs.exclude("APG", "Core Supplements");
		prereqs.exclude("Bestiary", "Core Supplements");
		prereqs.exclude("Ultimate Magic", "Core Supplements");

		var plan = CampaignAdditionPlanner.plan(Collections.emptyList(),
				List.of(campaign("APG"), campaign("Bestiary"), campaign("Core Supplements"),
						campaign("Ultimate Magic")),
				prereqs);

		assertEquals(List.of("APG", "Bestiary", "Ultimate Magic"), names(plan.accepted()),
				"the books the bundle merely repackages must still be added");
		assertEquals(List.of("Core Supplements"), names(plan.rejected()));
	}

	/** A mutually exclusive pair keeps its first candidate, even when it sorts last alphabetically. */
	@Test
	void plan_should_resolveAConflictInFavourOfTheEarlierCandidate()
	{
		var prereqs = new FakePrereqs();
		prereqs.exclude("Zulu", "Alpha");
		prereqs.exclude("Alpha", "Zulu");

		var plan = CampaignAdditionPlanner.plan(Collections.emptyList(),
				List.of(campaign("Zulu"), campaign("Alpha")), prereqs);

		assertEquals(List.of("Zulu"), names(plan.accepted()),
				"the first candidate offered must win the conflict");
		assertEquals(List.of("Alpha"), names(plan.rejected()));
	}

	/**
	 * Dropping a conflicting candidate can satisfy another candidate's positive
	 * requirement, and that candidate may sort earlier than the one it needs.
	 * Recovery therefore cannot stop after a single pass: "Expansion" only
	 * becomes possible once "Standalone" has been taken back, which happens
	 * after it has already been tried and refused.
	 */
	@Test
	void plan_should_acceptAPair_thatOnlyQualifiesOnceAConflictIsDropped()
	{
		var prereqs = new FakePrereqs();
		prereqs.exclude("Bundle", "Standalone");
		prereqs.exclude("Standalone", "Bundle");
		prereqs.require("Expansion", "Standalone");

		var plan = CampaignAdditionPlanner.plan(Collections.emptyList(),
				List.of(campaign("Expansion"), campaign("Standalone"), campaign("Bundle")), prereqs);

		assertEquals(List.of("Expansion", "Standalone"), names(plan.accepted()),
				"recovery must keep trying until nothing more can be taken back");
		assertEquals(List.of("Bundle"), names(plan.rejected()));
	}

	/**
	 * The user did not ask to add what they already have, so a candidate must
	 * never be accepted at the cost of breaking the existing selection.
	 */
	@Test
	void plan_should_notBreakTheExistingSelection()
	{
		var prereqs = new FakePrereqs();
		prereqs.exclude("Existing Book", "New Book");

		var plan = CampaignAdditionPlanner.plan(List.of(campaign("Existing Book")),
				List.of(campaign("New Book")), prereqs);

		assertTrue(plan.accepted().isEmpty(),
				"a candidate that invalidates an existing choice must not be added");
		assertEquals(List.of("New Book"), names(plan.rejected()));
	}
}
