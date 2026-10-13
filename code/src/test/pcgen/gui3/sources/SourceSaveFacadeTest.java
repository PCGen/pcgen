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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import pcgen.core.Campaign;
import pcgen.core.GameMode;
import pcgen.facade.core.LoadableFacade.LoadingState;
import pcgen.facade.core.SourceSelectionFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.ListFacades;
import pcgen.facade.util.ReferenceFacade;

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

	/** Records what the engine was asked to store, as the real facade does. */
	private static final class FakeFacade implements SourceSelectionFacade
	{
		private final String name;
		private List<Campaign> campaigns;
		private GameMode gameMode;

		FakeFacade(String name)
		{
			this.name = name;
		}

		@Override public ListFacade<Campaign> getCampaigns() { return ListFacades.emptyList(); }

		@Override public void setCampaigns(List<Campaign> campaigns) { this.campaigns = campaigns; }

		@Override public ReferenceFacade<GameMode> getGameMode() { return null; }

		@Override public void setGameMode(GameMode gameMode) { this.gameMode = gameMode; }

		@Override public boolean isModifiable() { return true; }

		@Override public LoadingState getLoadingState() { return null; }

		@Override public String getLoadingErrorMessage() { return null; }

		@Override public String toString() { return name; }
	}

	private Optional<SourceSelectionFacade> save(SourceBundle bundle, SourceSelectionFacade... existing)
	{
		return SourceSelectionBridge.saveSourceSelection(bundle, List.of(existing), name -> {
			created.add(name);
			return new FakeFacade(name);
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
		var saved = save(bundle("Mine"), new FakeFacade("Other"));

		assertEquals(List.of("Mine"), created);
		assertTrue(saved.isPresent());
	}

	@Test
	void save_should_reuseTheExistingFacade_when_theNameIsTaken()
	{
		var mine = new FakeFacade("Mine");

		var saved = save(bundle("Mine"), new FakeFacade("Other"), mine);

		assertSame(mine, saved.orElseThrow(), "overwriting must not leave two selections of one name");
		assertEquals(List.of(), created, "the engine must not be asked to create a duplicate");
	}

	@Test
	void save_should_storeTheCampaigns()
	{
		var core = campaign("Core Rulebook");

		var saved = save(bundle("Mine", core));

		assertEquals(List.of(core), ((FakeFacade) saved.orElseThrow()).campaigns);
	}

	@Test
	void save_should_storeTheGameMode()
	{
		var saved = save(bundle("Mine"));

		assertSame(MODE, ((FakeFacade) saved.orElseThrow()).gameMode);
	}

	@Test
	void save_should_overwriteTheCampaignsOfAnExistingSelection()
	{
		var mine = new FakeFacade("Mine");
		mine.setCampaigns(List.of(campaign("Old")));
		var core = campaign("Core Rulebook");

		save(bundle("Mine", core), mine);

		assertEquals(List.of(core), mine.campaigns, "saving replaces the stored set, it does not add to it");
	}

	/**
	 * The engine would happily store a selection called "", which the user then
	 * cannot tell from the unnamed fallback rows.
	 */
	@Test
	void save_should_refuseABlankName()
	{
		assertTrue(save(bundle("   ")).isEmpty());
		assertEquals(List.of(), created);
	}

	@Test
	void save_should_refuseAnUnnamedBundle()
	{
		assertTrue(save(bundle(null)).isEmpty());
		assertEquals(List.of(), created);
	}

	@Test
	void save_should_tolerateANullBundle()
	{
		assertTrue(save(null).isEmpty());
		assertEquals(List.of(), created);
	}

	/**
	 * Names are matched exactly, as {@link SourceSelectionBridge#classifyName}
	 * does, so the dialog's decision to overwrite and the facade it then
	 * overwrites cannot disagree.
	 */
	@Test
	void save_should_matchNamesExactly()
	{
		save(bundle("mine"), new FakeFacade("Mine"));

		assertEquals(List.of("mine"), created, "a differently cased name is a different selection");
	}
}
