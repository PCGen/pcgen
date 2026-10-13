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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import pcgen.core.GameMode;

import org.junit.jupiter.api.Test;

/**
 * The rules deciding whether a proposed Save name may be used.
 *
 * <p>The legacy Swing dialog asked none of these questions. It matched the
 * typed name only against <em>custom</em> selections, so naming one after a
 * bundled selection silently created a second entry with the same label; it
 * accepted an empty name; and it replaced an existing selection without
 * asking.
 */
class SourceSaveDecisionTest
{
	private static final GameMode MODE = new GameMode("test");

	private static SourceBundle builtIn(String name)
	{
		return new SourceBundle(name, MODE, List.of(), false, null);
	}

	private static SourceBundle custom(String name)
	{
		return new SourceBundle(name, MODE, List.of(), true, null);
	}

	private static final List<SourceBundle> SOURCES = List.of(builtIn("Core"), custom("Mine"));

	private static SourceSaveDecision decide(String name)
	{
		return SourceSaveDecision.forName(name, SOURCES);
	}

	@Test
	void unusedName_isSavedWithoutFurtherAsking()
	{
		var decision = decide("Fresh");

		assertTrue(decision.canSave());
		assertFalse(decision.needsConfirmation());
	}

	@Test
	void nameOfAnExistingCustomSelection_isSavedOnlyAfterConfirmation()
	{
		var decision = decide("Mine");

		assertTrue(decision.canSave());
		assertTrue(decision.needsConfirmation(), "overwriting discards whatever was stored before");
	}

	/**
	 * A bundled selection cannot be overwritten, and allowing a custom one to
	 * take its name leaves two rows the user cannot tell apart.
	 */
	@Test
	void nameOfABundledSelection_isRefused()
	{
		var decision = decide("Core");

		assertFalse(decision.canSave());
	}

	@Test
	void refusedName_explainsItself()
	{
		var decision = decide("Core");

		assertTrue(decision.message().isPresent(), "refusing without saying why is a dead end");
		assertTrue(decision.message().orElseThrow().contains("Core"), decision.message().orElseThrow());
	}

	@Test
	void blankName_isRefused()
	{
		assertFalse(decide("   ").canSave());
		assertFalse(decide("").canSave());
	}

	@Test
	void absentName_isRefused()
	{
		assertFalse(decide(null).canSave());
	}

	/** A name typed with stray spaces is the same name. */
	@Test
	void surroundingWhitespace_isIgnored()
	{
		var decision = decide("  Mine  ");

		assertTrue(decision.needsConfirmation(), "it is the same selection as Mine");
		assertEquals("Mine", decision.name());
	}

	@Test
	void savedNameIsTheTrimmedOne()
	{
		assertEquals("Fresh", decide("  Fresh ").name());
	}

	/**
	 * Names differing only in case are different selections, matching how the
	 * bridge resolves a name back to its facade.
	 */
	@Test
	void differentlyCasedName_isADifferentSelection()
	{
		var decision = decide("mine");

		assertTrue(decision.canSave());
		assertFalse(decision.needsConfirmation());
	}

	@Test
	void confirmationMessage_namesWhatWillBeReplaced()
	{
		var decision = decide("Mine");

		assertTrue(decision.message().orElseThrow().contains("Mine"), decision.message().orElseThrow());
	}

	@Test
	void unusedName_hasNothingToSay()
	{
		assertTrue(decide("Fresh").message().isEmpty());
	}

	private static pcgen.core.Campaign campaign(String name)
	{
		var campaign = new pcgen.core.Campaign();
		campaign.setName(name);
		return campaign;
	}

	private static SourceBundle saved(String name, pcgen.core.Campaign... campaigns)
	{
		return new SourceBundle(name, MODE, List.of(campaigns), true, null);
	}

	/**
	 * Re-saving a selection the user opened should offer its own name, so the
	 * obvious action overwrites it rather than quietly creating a second copy
	 * under a name they have to retype.
	 */
	@Test
	void suggestedName_isThatOfTheSavedSelectionWithTheSameSources()
	{
		var core = campaign("Core Rulebook");

		var suggestion = AdvancedSourceSelectionController.suggestedSaveName(
				List.of(core), List.of(builtIn("Core"), saved("Mine", core)));

		assertEquals("Mine", suggestion);
	}

	@Test
	void suggestedName_isEmpty_whenNoSavedSelectionMatches()
	{
		var suggestion = AdvancedSourceSelectionController.suggestedSaveName(
				List.of(campaign("Core Rulebook")), List.of(saved("Mine", campaign("Other"))));

		assertEquals("", suggestion);
	}

	/**
	 * A bundled selection cannot be saved over, so offering its name would walk
	 * the user straight into a refusal.
	 */
	@Test
	void suggestedName_ignoresBundledSelections()
	{
		var core = campaign("Core Rulebook");

		var suggestion = AdvancedSourceSelectionController.suggestedSaveName(
				List.of(core), List.of(new SourceBundle("Core", MODE, List.of(core), false, null)));

		assertEquals("", suggestion);
	}

	@Test
	void suggestedName_ignoresUnnamedSelections()
	{
		var core = campaign("Core Rulebook");

		var suggestion = AdvancedSourceSelectionController.suggestedSaveName(
				List.of(core), List.of(new SourceBundle(null, MODE, List.of(core), true, null)));

		assertEquals("", suggestion);
	}

	@Test
	void suggestedName_isEmpty_whenNothingIsSelected()
	{
		var suggestion = AdvancedSourceSelectionController.suggestedSaveName(
				List.of(), List.of(saved("Mine", campaign("Core Rulebook"))));

		assertEquals("", suggestion, "an empty selection must not match a saved one by accident");
	}

	private final List<String> reported = new ArrayList<>();

	private Optional<SourceBundle> plan(String typed, boolean confirmed, List<SourceBundle> existing)
	{
		return AdvancedSourceSelectionController.plannedSave(typed, existing, MODE,
				List.of(campaign("Core Rulebook")), _ -> confirmed, reported::add);
	}

	@Test
	void unusedName_isStoredWithoutAsking()
	{
		var planned = plan("Fresh", false, SOURCES);

		assertTrue(planned.isPresent(), "a free name needs no confirmation, so refusing one must not block it");
		assertEquals("Fresh", planned.orElseThrow().name());
	}

	@Test
	void storedSelection_carriesTheCampaignsAndMode()
	{
		var planned = plan("Fresh", false, SOURCES).orElseThrow();

		assertEquals(List.of(campaign("Core Rulebook")).size(), planned.campaigns().size());
		assertSame(MODE, planned.gameMode());
	}

	/** Anything saved from here is the user's own, and must be deletable. */
	@Test
	void storedSelection_isModifiable()
	{
		assertTrue(plan("Fresh", false, SOURCES).orElseThrow().modifiable());
	}

	@Test
	void existingName_isStored_whenTheUserConfirms()
	{
		assertTrue(plan("Mine", true, SOURCES).isPresent());
	}

	@Test
	void existingName_isNotStored_whenTheUserDeclines()
	{
		assertTrue(plan("Mine", false, SOURCES).isEmpty());
		assertEquals(List.of(), reported, "declining is not an error and needs no message");
	}

	@Test
	void bundledName_isNotStored_andIsExplained()
	{
		assertTrue(plan("Core", true, SOURCES).isEmpty(),
				"confirming must not override a name that cannot be used at all");
		assertEquals(1, reported.size());
		assertTrue(reported.getFirst().contains("Core"), reported.getFirst());
	}

	@Test
	void blankName_isNotStored_andSaysNothing()
	{
		assertTrue(plan("   ", true, SOURCES).isEmpty());
		assertEquals(List.of(), reported, "the user simply has not finished typing");
	}

	@Test
	void storedNameIsTrimmed()
	{
		assertEquals("Fresh", plan("  Fresh  ", false, SOURCES).orElseThrow().name());
	}
}
