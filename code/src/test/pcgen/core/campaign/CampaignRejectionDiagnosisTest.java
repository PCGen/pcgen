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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pcgen.core.campaign.CampaignFakes.campaign;

import java.util.Collections;
import java.util.List;

import pcgen.core.campaign.CampaignFakes.FakeContents;
import pcgen.core.campaign.CampaignFakes.FakePrereqs;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link CampaignRejectionDiagnosis}, which explains why a
 * campaign the user asked for could not be added.
 *
 * <p>The behaviour under test answers a complaint about the real Pathfinder
 * data: selecting every book while the "Pathfinder RPG for Game Masters"
 * MultiSet was already selected refused fourteen books, and the dialog could
 * only quote each book's own prerequisite expression. Six of those books are
 * listed inside that MultiSet and so were already loaded, and the rest were
 * refused by something the user had selected rather than by anything missing.
 * Neither fact was discoverable from the message.
 */
class CampaignRejectionDiagnosisTest
{
	@Test
	void directlySelectedCampaign_isReportedAsAlreadySelected()
	{
		var book = campaign("Core Rulebook");

		var diagnoses = CampaignRejectionDiagnosis.diagnose(
				List.of(book), List.of(book), new FakeContents(), new FakePrereqs());

		assertInstanceOf(CampaignRejectionDiagnosis.AlreadySelected.class, diagnoses.getFirst().reason());
	}

	@Test
	void directlySelectedCampaignWithDifferentKeyCase_isReportedAsAlreadySelected()
	{
		var selected = campaign("Core Rulebook");
		var book = campaign("CORE RULEBOOK");

		var diagnoses = CampaignRejectionDiagnosis.diagnose(
				List.of(book), List.of(selected), new FakeContents(), new FakePrereqs());

		assertInstanceOf(CampaignRejectionDiagnosis.AlreadySelected.class, diagnoses.getFirst().reason());
	}

	@Test
	void directlySelectedCampaignInsideASelectedBundle_isReportedAsAlreadySelected()
	{
		var bundle = campaign("Pathfinder RPG for Game Masters");
		var book = campaign("Core Rulebook");
		var contents = new FakeContents();
		contents.contains(bundle, book);

		var diagnoses = CampaignRejectionDiagnosis.diagnose(
				List.of(book), List.of(bundle, book), contents, new FakePrereqs());

		assertInstanceOf(CampaignRejectionDiagnosis.AlreadySelected.class, diagnoses.getFirst().reason());
	}

	@Test
	void campaignListedInsideASelectedBundle_isReportedAsAlreadyIncluded()
	{
		var bundle = campaign("Pathfinder RPG for Game Masters");
		var book = campaign("Advanced Player's Guide");
		var contents = new FakeContents();
		contents.contains(bundle, book);
		var prereqs = new FakePrereqs();
		prereqs.exclude("Pathfinder RPG for Game Masters", "Advanced Player's Guide");

		var diagnoses = CampaignRejectionDiagnosis.diagnose(
				List.of(book), List.of(bundle), contents, prereqs);

		var reason = assertInstanceOf(CampaignRejectionDiagnosis.AlreadyIncluded.class,
				diagnoses.getFirst().reason());
		assertEquals(bundle, reason.container());
	}

	@Test
	void campaignBuriedDeeperInsideASelectedBundle_isStillReportedAsAlreadyIncluded()
	{
		var bundle = campaign("Pathfinder RPG for Game Masters");
		var inner = campaign("Pathfinder RPG Core Supplements");
		var book = campaign("Advanced Player's Guide");
		var contents = new FakeContents();
		contents.contains(bundle, inner);
		contents.contains(inner, book);

		var diagnoses = CampaignRejectionDiagnosis.diagnose(
				List.of(book), List.of(bundle), contents, new FakePrereqs());

		var reason = assertInstanceOf(CampaignRejectionDiagnosis.AlreadyIncluded.class,
				diagnoses.getFirst().reason());
		assertEquals(bundle, reason.container(),
				"the bundle the user actually selected is the one worth naming");
	}

	@Test
	void campaignThatBreaksASelectedCampaign_namesThatCampaignAsTheBlocker()
	{
		var bundle = campaign("Pathfinder RPG for Game Masters");
		var book = campaign("Core Rulebook");
		var prereqs = new FakePrereqs();
		prereqs.exclude("Pathfinder RPG for Game Masters", "Core Rulebook");

		var diagnoses = CampaignRejectionDiagnosis.diagnose(
				List.of(book), List.of(bundle), new FakeContents(), prereqs);

		var reason = assertInstanceOf(CampaignRejectionDiagnosis.ConflictsWith.class,
				diagnoses.getFirst().reason());
		assertEquals(List.of(bundle), reason.blockers());
	}

	@Test
	void campaignRefusedByNothingSelected_isReportedAsAnUnmetRequirement()
	{
		var book = campaign("Bonus Bestiary");
		var other = campaign("Core Rulebook");
		var prereqs = new FakePrereqs();
		prereqs.neverQualifies("Bonus Bestiary");

		var diagnoses = CampaignRejectionDiagnosis.diagnose(
				List.of(book), List.of(other), new FakeContents(), prereqs);

		assertInstanceOf(CampaignRejectionDiagnosis.Unmet.class, diagnoses.getFirst().reason());
	}

	@Test
	void selectedCampaignThatWasAlreadyUnqualified_isNotBlamedOnTheCandidate()
	{
		var broken = campaign("Already Broken");
		var book = campaign("Bonus Bestiary");
		var prereqs = new FakePrereqs();
		prereqs.neverQualifies("Already Broken");
		prereqs.neverQualifies("Bonus Bestiary");

		var diagnoses = CampaignRejectionDiagnosis.diagnose(
				List.of(book), List.of(broken), new FakeContents(), prereqs);

		assertInstanceOf(CampaignRejectionDiagnosis.Unmet.class, diagnoses.getFirst().reason(),
				"the candidate did not break it, so naming it would send the user after the wrong book");
	}

	@Test
	void everyRejectionIsDiagnosed_inTheOrderGiven()
	{
		var bundle = campaign("Pathfinder RPG for Game Masters");
		var included = campaign("Advanced Player's Guide");
		var conflicting = campaign("Core Rulebook");
		var contents = new FakeContents();
		contents.contains(bundle, included);
		var prereqs = new FakePrereqs();
		prereqs.exclude("Pathfinder RPG for Game Masters", "Core Rulebook");

		var diagnoses = CampaignRejectionDiagnosis.diagnose(
				List.of(included, conflicting), List.of(bundle), contents, prereqs);

		assertEquals(List.of(included, conflicting),
				diagnoses.stream().map(CampaignRejectionDiagnosis.Diagnosis::campaign).toList());
	}

	@Test
	void nothingRejected_asksTheEngineNothing()
	{
		var prereqs = new FakePrereqs();

		var diagnoses = CampaignRejectionDiagnosis.diagnose(
				Collections.emptyList(), List.of(campaign("Core Rulebook")), new FakeContents(), prereqs);

		assertTrue(diagnoses.isEmpty());
		assertTrue(prereqs.calls.isEmpty(), "diagnosis is an error path and must cost nothing otherwise");
	}

	@Test
	void bundleThatContainsItself_doesNotLoopForever()
	{
		var bundle = campaign("Self Referential");
		var book = campaign("Core Rulebook");
		var contents = new FakeContents();
		contents.contains(bundle, bundle);

		var diagnoses = CampaignRejectionDiagnosis.diagnose(
				List.of(book), List.of(bundle), contents, new FakePrereqs());

		assertInstanceOf(CampaignRejectionDiagnosis.Unmet.class, diagnoses.getFirst().reason());
	}
}
