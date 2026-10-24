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

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import pcgen.core.Campaign;
import pcgen.core.GameMode;
import pcgen.system.FacadeFactory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the one-way Basic→model projection
 * ({@link SourceSelectionDialogPane#project}): a Basic-tab {@link SourceBundle}
 * must overwrite the model's game mode and campaigns, and a null selection must
 * leave the model untouched.
 */
class SourceSelectionProjectionTest
{
	private Field campaignListMap;
	private Object previousCampaigns;

	@BeforeEach
	void initializeCampaigns() throws Exception
	{
		campaignListMap = FacadeFactory.class.getDeclaredField("campaignListMap");
		campaignListMap.setAccessible(true);
		previousCampaigns = campaignListMap.get(null);
		campaignListMap.set(null, Map.of());
	}

	@AfterEach
	void restoreCampaigns() throws Exception
	{
		campaignListMap.set(null, previousCampaigns);
	}

	private static SourceBundle bundle(GameMode mode, Campaign... campaigns)
	{
		return new SourceBundle(null, mode, List.of(campaigns), false, null);
	}

	private static Campaign campaign(String name)
	{
		var campaign = new Campaign();
		campaign.setName(name);
		return campaign;
	}

	@Test
	void project_should_adoptGameModeAndCampaigns_when_bundlePresent()
	{
		var model = new SourceSelectionModel();
		var mode = new GameMode("Pathfinder");
		var campaign = campaign("Core");

		SourceSelectionDialogPane.project(model, bundle(mode, campaign));

		assertSame(mode, model.getGameMode());
		assertEquals(List.of(campaign), model.getSelectedCampaigns());
		assertSame(campaign, model.getSelectedCampaigns().getFirst());
	}

	@Test
	void project_should_refreshCampaigns_when_sameGameModeReselected()
	{
		var model = new SourceSelectionModel();
		var mode = new GameMode("Pathfinder");
		var first = campaign("First");
		var second = campaign("Second");

		SourceSelectionDialogPane.project(model, bundle(mode, first));
		SourceSelectionDialogPane.project(model, bundle(mode, second));

		// Same mode must not short-circuit the campaign refresh.
		assertSame(mode, model.getGameMode());
		assertEquals(List.of(second), model.getSelectedCampaigns());
		assertSame(second, model.getSelectedCampaigns().getFirst());
	}

	@Test
	void project_should_replaceNotAppend_when_calledRepeatedly()
	{
		var model = new SourceSelectionModel();
		var pathfinder = new GameMode("Pathfinder");
		var starfinder = new GameMode("Starfinder");
		var first = campaign("Pathfinder Core");
		var second = campaign("Starfinder Core");

		SourceSelectionDialogPane.project(model, bundle(pathfinder, first));
		SourceSelectionDialogPane.project(model, bundle(starfinder, second));

		assertSame(starfinder, model.getGameMode());
		assertEquals(List.of(second), model.getSelectedCampaigns());
		assertSame(second, model.getSelectedCampaigns().getFirst());
	}

	@Test
	void project_should_leaveModelUntouched_when_bundleNull()
	{
		var model = new SourceSelectionModel();
		var mode = new GameMode("Pathfinder");
		var campaign = campaign("Core");
		SourceSelectionDialogPane.project(model, bundle(mode, campaign));

		SourceSelectionDialogPane.project(model, null);

		assertSame(mode, model.getGameMode());
		assertEquals(List.of(campaign), model.getSelectedCampaigns());
	}

	@Test
	void project_should_clearCampaigns_when_bundleHasNone()
	{
		var model = new SourceSelectionModel();
		var mode = new GameMode("Pathfinder");
		SourceSelectionDialogPane.project(model, bundle(mode, campaign("Core")));

		SourceSelectionDialogPane.project(model, bundle(mode));

		assertSame(mode, model.getGameMode());
		assertEquals(0, model.getSelectedCampaigns().size());
	}
}
