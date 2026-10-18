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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import pcgen.core.Campaign;

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
	private static Campaign campaign(String name)
	{
		var campaign = new Campaign();
		campaign.setName(name);
		return campaign;
	}

	/**
	 * A stand-in for the engine's prerequisite check. A campaign is unqualified
	 * when any campaign it requires is absent from the set under test, which is
	 * the same whole-set semantics {@code FacadeFactory.passesPrereqs} uses.
	 */
	private static final class FakePrereqs implements UnaryOperator<List<Campaign>>
	{
		private final Map<String, Set<String>> requirements = new HashMap<>();
		private final Map<String, Set<String>> exclusions = new HashMap<>();
		private final List<List<Campaign>> calls = new ArrayList<>();

		void require(String campaign, String... required)
		{
			requirements.put(campaign, Set.of(required));
		}

		/** Models {@code !PRECAMPAIGN}: {@code campaign} is refused if any of these is present. */
		void exclude(String campaign, String... excluded)
		{
			exclusions.put(campaign, Set.of(excluded));
		}

		@Override
		public List<Campaign> apply(List<Campaign> candidates)
		{
			calls.add(List.copyOf(candidates));
			var present = candidates.stream().map(Campaign::getDisplayName).collect(Collectors.toSet());
			return candidates.stream()
					.filter(c -> {
						var name = c.getDisplayName();
						var missingRequirement = !present.containsAll(
								requirements.getOrDefault(name, Set.of()));
						var blocked = exclusions.getOrDefault(name, Set.of()).stream()
								.anyMatch(present::contains);
						return missingRequirement || blocked;
					})
					.toList();
		}
	}

	private static List<String> names(List<Campaign> campaigns)
	{
		return campaigns.stream().map(Campaign::getDisplayName).toList();
	}

	@Test
	void plan_should_acceptEveryCandidate_whenTheWholeSetQualifies()
	{
		var plan = CampaignAdditionPlanner.plan(
				List.of(), List.of(campaign("Alpha"), campaign("Beta")), new FakePrereqs());

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
				List.of(),
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
				List.of(), List.of(campaign("Book of Arcane Magic"), campaign("Beta")), prereqs);

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

		var plan = CampaignAdditionPlanner.plan(List.of(campaign("Alpha")), List.of(), prereqs);

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
				List.of(),
				List.of(campaign("Alpha"), campaign("Beta"), campaign("Gamma"), campaign("Delta")),
				prereqs);

		assertEquals(List.of("Alpha", "Gamma"), names(plan.accepted()));
		assertEquals(List.of("Beta", "Delta"), names(plan.rejected()));
	}

	/**
	 * PCGen's data is full of deliberate mutual exclusions: {@code Bestiary}
	 * and {@code Bestiary ~ Player Options Only} each carry a
	 * {@code !PRECAMPAIGN} naming the other, because the second is a cut-down
	 * edition of the first. Validating the set as a whole sees both as
	 * unqualified and would refuse both, leaving the user with neither. One of
	 * them can plainly be added, so one of them must be.
	 */
	@Test
	void plan_should_keepOneOfAMutuallyExclusivePair()
	{
		var prereqs = new FakePrereqs();
		prereqs.exclude("Bestiary", "Bestiary ~ Player Options Only");
		prereqs.exclude("Bestiary ~ Player Options Only", "Bestiary");

		var plan = CampaignAdditionPlanner.plan(List.of(),
				List.of(campaign("Bestiary"), campaign("Bestiary ~ Player Options Only")), prereqs);

		assertEquals(List.of("Bestiary"), names(plan.accepted()),
				"one of an exclusive pair must still be added");
		assertEquals(List.of("Bestiary ~ Player Options Only"), names(plan.rejected()));
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

		var plan = CampaignAdditionPlanner.plan(List.of(),
				List.of(campaign("APG"), campaign("Bestiary"), campaign("Core Supplements"),
						campaign("Ultimate Magic")),
				prereqs);

		assertEquals(List.of("APG", "Bestiary", "Ultimate Magic"), names(plan.accepted()),
				"the books the bundle merely repackages must still be added");
		assertEquals(List.of("Core Supplements"), names(plan.rejected()));
	}

	/**
	 * Resolving an exclusion means choosing a winner, and the choice has to be
	 * predictable: the same selection must always produce the same result. The
	 * candidate the user sees first wins.
	 */
	@Test
	void plan_should_resolveAConflictInFavourOfTheEarlierCandidate()
	{
		var prereqs = new FakePrereqs();
		prereqs.exclude("Zulu", "Alpha");
		prereqs.exclude("Alpha", "Zulu");

		var plan = CampaignAdditionPlanner.plan(List.of(),
				List.of(campaign("Zulu"), campaign("Alpha")), prereqs);

		assertEquals(List.of("Zulu"), names(plan.accepted()),
				"the first candidate offered must win the conflict");
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

		var plan = CampaignAdditionPlanner.plan(List.of(),
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
