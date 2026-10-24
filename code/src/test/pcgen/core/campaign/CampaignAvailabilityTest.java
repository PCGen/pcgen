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

import pcgen.core.campaign.CampaignFakes.FakeContents;
import pcgen.core.campaign.CampaignFakes.FakePrereqs;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link CampaignAvailability}, which says how each offered
 * source stands against the current selection so the tree can show it before
 * the user clicks Add.
 *
 * <p>The legacy Swing panel coloured a row red whenever adding it would fail,
 * which conflates two quite different situations. A book inside a selected
 * collection also "fails", because the collection excludes the books it
 * repackages -- but it is loaded, and marking it as a problem sends the user
 * looking for one that does not exist.
 */
class CampaignAvailabilityTest
{
	@Test
	void campaignThatCanBeAdded_isAddable()
	{
		var book = campaign("Core Rulebook");

		var status = CampaignAvailability.classify(
				List.of(book), Collections.emptyList(), new FakeContents(), new FakePrereqs());

		assertEquals(CampaignAvailability.Status.ADDABLE, status.get(book));
	}

	@Test
	void campaignInsideASelectedCollection_isAlreadyIncluded()
	{
		var bundle = campaign("Pathfinder RPG for Game Masters");
		var book = campaign("Advanced Player's Guide");
		var contents = new FakeContents();
		contents.contains(bundle, book);
		var prereqs = new FakePrereqs();
		prereqs.exclude("Pathfinder RPG for Game Masters", "Advanced Player's Guide");

		var status = CampaignAvailability.classify(List.of(book), List.of(bundle), contents, prereqs);

		assertEquals(CampaignAvailability.Status.ALREADY_INCLUDED, status.get(book),
				"it is loaded through the collection, not broken");
	}

	@Test
	void campaignThatCannotBeAdded_isBlocked()
	{
		var bundle = campaign("Pathfinder RPG for Game Masters");
		var book = campaign("Pathfinder RPG for Players");
		var prereqs = new FakePrereqs();
		prereqs.exclude("Pathfinder RPG for Players", "Pathfinder RPG for Game Masters");

		var status = CampaignAvailability.classify(
				List.of(book), List.of(bundle), new FakeContents(), prereqs);

		assertEquals(CampaignAvailability.Status.BLOCKED, status.get(book));
	}

	/**
	 * A selected book is loaded by definition. Asking the engine whether it
	 * could be added would add it twice and could answer no.
	 */
	@Test
	void campaignThatIsItselfSelected_isAlreadyIncluded()
	{
		var book = campaign("Core Rulebook");
		var prereqs = new FakePrereqs();
		prereqs.exclude("Core Rulebook", "Core Rulebook");

		var status = CampaignAvailability.classify(
				List.of(book), List.of(book), new FakeContents(), prereqs);

		assertEquals(CampaignAvailability.Status.ALREADY_INCLUDED, status.get(book));
	}

	@Test
	void campaignWithAnAlreadySelectedKey_isAlreadyIncluded()
	{
		var selected = campaign("Core Rulebook");
		var offered = campaign("core rulebook");

		var status = CampaignAvailability.classify(
				List.of(offered), List.of(selected), new FakeContents(), new FakePrereqs());

		assertEquals(CampaignAvailability.Status.ALREADY_INCLUDED, status.get(offered));
	}

	/**
	 * A selection that is already unqualified must not turn the whole tree red:
	 * the candidate did not cause it and removing the candidate would not fix
	 * it.
	 */
	@Test
	void campaignOfferedAgainstABrokenSelection_isStillAddable()
	{
		var broken = campaign("Already Broken");
		var book = campaign("Core Rulebook");
		var prereqs = new FakePrereqs();
		prereqs.neverQualifies("Already Broken");

		var status = CampaignAvailability.classify(
				List.of(book), List.of(broken), new FakeContents(), prereqs);

		assertEquals(CampaignAvailability.Status.ADDABLE, status.get(book));
	}

	@Test
	void unqualifiedCandidate_isBlockedEvenWhenTheSelectionIsAlreadyBroken()
	{
		var broken = campaign("Already Broken");
		var book = campaign("Missing Requirements");
		var prereqs = new FakePrereqs();
		prereqs.neverQualifies("Already Broken");
		prereqs.neverQualifies("Missing Requirements");

		var status = CampaignAvailability.classify(
				List.of(book), List.of(broken), new FakeContents(), prereqs);

		assertEquals(CampaignAvailability.Status.BLOCKED, status.get(book));
	}

	@Test
	void candidateThatBreaksAnotherSelectedBook_isBlockedDespiteAnExistingFailure()
	{
		var broken = campaign("Already Broken");
		var selected = campaign("Selected");
		var book = campaign("Excluded");
		var prereqs = new FakePrereqs();
		prereqs.neverQualifies("Already Broken");
		prereqs.exclude("Selected", "Excluded");

		var status = CampaignAvailability.classify(
				List.of(book), List.of(broken, selected), new FakeContents(), prereqs);

		assertEquals(CampaignAvailability.Status.BLOCKED, status.get(book));
	}

	@Test
	void everyOfferedCampaign_isClassified()
	{
		var first = campaign("Core Rulebook");
		var second = campaign("Bonus Bestiary");

		var status = CampaignAvailability.classify(
				List.of(first, second), Collections.emptyList(), new FakeContents(), new FakePrereqs());

		assertEquals(2, status.size());
		assertTrue(status.containsKey(first));
		assertTrue(status.containsKey(second));
	}

	/**
	 * Campaigns are engine-wide singletons and {@code CDOMObject} equality is
	 * not identity, so a lookup must not confuse two campaigns that compare
	 * equal.
	 */
	@Test
	void lookupIsByIdentity_notEquality()
	{
		var book = campaign("Core Rulebook");
		var twin = campaign("Core Rulebook");

		var status = CampaignAvailability.classify(
				List.of(book), Collections.emptyList(), new FakeContents(), new FakePrereqs());

		assertSame(null, status.get(twin), "a different campaign object is a different campaign");
	}

	@Test
	void nothingOffered_asksTheEngineNothing()
	{
		var prereqs = new FakePrereqs();

		var status = CampaignAvailability.classify(
				Collections.emptyList(), List.of(campaign("Core Rulebook")), new FakeContents(), prereqs);

		assertTrue(status.isEmpty());
		assertTrue(prereqs.calls.isEmpty());
	}

	/**
	 * The tree is recomputed on every selection change, so the cost is one
	 * engine call per offered campaign and no more.
	 */
	@Test
	void eachOfferedCampaign_costsOneEngineCall()
	{
		var prereqs = new FakePrereqs();
		var books = List.of(campaign("A"), campaign("B"), campaign("C"));

		CampaignAvailability.classify(books, Collections.emptyList(), new FakeContents(), prereqs);

		assertEquals(3, prereqs.calls.size());
	}
}
