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
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import pcgen.core.Campaign;
import pcgen.core.GameMode;
import pcgen.facade.core.SourceSelectionFacade;

import org.junit.jupiter.api.Test;

/**
 * Covers the engine-boundary step that writes a {@link SourceBundle} back as a
 * custom {@link SourceSelectionFacade}.
 *
 * <p>Saving either reuses the facade already stored under the chosen name or
 * asks the engine for a new one. Getting that wrong is not a cosmetic matter:
 * {@code FacadeFactory.createCustomSourceSelection} appends to the list of
 * custom selections unconditionally, so creating one whose name is already
 * taken leaves two entries the user cannot tell apart.
 */
class SourceSaveFacadeTest
{
	private static final GameMode MODE = new GameMode("test");

	private final List<String> created = new ArrayList<>();

	private SourceSelectionFacade save(SourceBundle bundle, SourceSelectionFacade... existing)
	{
		return SourceSelectionBridge.saveSourceSelection(bundle, List.of(existing), name -> {
			created.add(name);
			return new FakeSourceSelectionFacade(name);
		});
	}

	private static SourceBundle bundle(String name, Campaign... campaigns)
	{
		return new SourceBundle(name, MODE, List.of(campaigns), true, null);
	}

	private static Campaign campaign(String name)
	{
		var campaign = new Campaign();
		campaign.setName(name);
		return campaign;
	}

	@Test
	void save_should_createAFacade_when_theNameIsUnused()
	{
		var saved = save(bundle("Mine"), new FakeSourceSelectionFacade("Other"));

		assertEquals(List.of("Mine"), created);
		assertNotNull(saved);
	}

	@Test
	void save_should_reuseTheExistingFacade_when_theNameIsTaken()
	{
		var mine = new FakeSourceSelectionFacade("Mine");

		var saved = save(bundle("Mine"), new FakeSourceSelectionFacade("Other"), mine);

		assertSame(mine, saved, "overwriting must not leave two selections of one name");
		assertEquals(List.of(), created, "the engine must not be asked to create a duplicate");
	}

	@Test
	void save_should_storeTheCampaigns()
	{
		var core = campaign("Core Rulebook");

		var saved = save(bundle("Mine", core));

		assertEquals(List.of(core), ((FakeSourceSelectionFacade) saved).campaigns);
	}

	@Test
	void save_should_storeTheGameMode()
	{
		var saved = save(bundle("Mine"));

		assertSame(MODE, ((FakeSourceSelectionFacade) saved).gameMode);
	}

	@Test
	void save_should_overwriteTheCampaignsOfAnExistingSelection()
	{
		var mine = new FakeSourceSelectionFacade("Mine");
		mine.setCampaigns(List.of(campaign("Old")));
		var core = campaign("Core Rulebook");

		save(bundle("Mine", core), mine);

		assertEquals(List.of(core), mine.campaigns, "saving replaces the stored set, it does not add to it");
	}

	/**
	 * Names are matched exactly, as {@link SourceSelectionBridge#classifyName}
	 * does, so the dialog's decision to overwrite and the facade it then
	 * overwrites cannot disagree.
	 */
	@Test
	void save_should_matchNamesExactly()
	{
		save(bundle("mine"), new FakeSourceSelectionFacade("Mine"));

		assertEquals(List.of("mine"), created, "a differently cased name is a different selection");
	}
}
