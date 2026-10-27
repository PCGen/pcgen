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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import pcgen.core.Campaign;
import pcgen.core.GameMode;
import pcgen.facade.util.DefaultListFacade;
import pcgen.system.FacadeFactory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link SourceSelectionModel}: the plain JavaFX view-model has
 * no live stage or FX thread requirement, so its property and list behaviour
 * are exercised directly.
 */
class SourceSelectionModelTest
{
	private final GameMode pathfinder = new GameMode("Model Pathfinder");
	private final GameMode starfinder = new GameMode("Model Starfinder");
	private final Campaign alpha = campaign("Alpha Core");
	private final Campaign beta = campaign("Beta Bestiary");
	private final Campaign starfinderCore = campaign("Starfinder Core");
	private Field campaignListMap;
	private Object previousCampaigns;

	@BeforeEach
	void supplyCampaigns() throws Exception
	{
		campaignListMap = FacadeFactory.class.getDeclaredField("campaignListMap");
		campaignListMap.setAccessible(true);
		previousCampaigns = campaignListMap.get(null);
		campaignListMap.set(null, Map.of(
				pathfinder, new DefaultListFacade<>(List.of(alpha, beta)),
				starfinder, new DefaultListFacade<>(List.of(starfinderCore))));
	}

	@AfterEach
	void restoreCampaigns() throws Exception
	{
		campaignListMap.set(null, previousCampaigns);
	}

	private static Campaign campaign(String name)
	{
		var campaign = new Campaign();
		campaign.setName(name);
		return campaign;
	}

	@Test
	void gameMode_should_startNull_when_fresh()
	{
		assertNull(new SourceSelectionModel().getGameMode());
	}

	@Test
	void setGameMode_should_updateProperty_when_set()
	{
		var model = new SourceSelectionModel();
		var mode = new GameMode("Pathfinder");

		model.setGameMode(mode);

		assertSame(mode, model.getGameMode());
		assertSame(mode, model.gameModeProperty().get());
	}

	@Test
	void selectedCampaigns_should_startEmpty_when_fresh()
	{
		assertEquals(0, new SourceSelectionModel().getSelectedCampaigns().size());
	}

	@Test
	void availableCampaigns_should_startEmpty_when_fresh()
	{
		assertEquals(0, new SourceSelectionModel().getAvailableCampaigns().size());
	}

	@Test
	void availableCampaigns_should_recompute_when_gameModeChanges()
	{
		var model = new SourceSelectionModel();
		model.setGameMode(pathfinder);
		assertEquals(List.of(alpha, beta), model.getAvailableCampaigns());

		model.setGameMode(starfinder);
		assertEquals(List.of(starfinderCore), model.getAvailableCampaigns());
		assertSame(starfinderCore, model.getAvailableCampaigns().getFirst());

		model.setGameMode(pathfinder);
		assertEquals(List.of(alpha, beta), model.getAvailableCampaigns());
	}

	@Test
	void availableCampaigns_should_recompute_when_searchQueryChanges()
	{
		var model = new SourceSelectionModel();
		model.setGameMode(pathfinder);
		assertEquals(List.of(alpha, beta), model.getAvailableCampaigns());

		model.searchQueryProperty().set("alpha");
		assertEquals(List.of(alpha), model.getAvailableCampaigns());

		model.searchQueryProperty().set("bestiary");
		assertEquals(List.of(beta), model.getAvailableCampaigns());

		model.searchQueryProperty().set("no matching campaign");
		assertEquals(List.of(), model.getAvailableCampaigns());

		model.searchQueryProperty().set("");
		assertEquals(List.of(alpha, beta), model.getAvailableCampaigns());
	}
}
