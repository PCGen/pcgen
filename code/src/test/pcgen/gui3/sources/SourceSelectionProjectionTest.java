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

import java.util.List;

import pcgen.core.Campaign;
import pcgen.core.GameMode;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the one-way Basic→model projection
 * ({@link SourceSelectionDialogPane#project}): a Basic-tab {@link SourceBundle}
 * must overwrite the model's game mode and campaigns, and a null selection must
 * leave the model untouched.
 */
class SourceSelectionProjectionTest
{
	private static SourceBundle bundle(GameMode mode, Campaign... campaigns)
	{
		return new SourceBundle(null, mode, List.of(campaigns), false, null);
	}

	@Test
	void project_should_adoptGameModeAndCampaigns_when_bundlePresent()
	{
		var model = new SourceSelectionModel();
		var mode = new GameMode("Pathfinder");
		var campaign = new Campaign();

		SourceSelectionDialogPane.project(model, bundle(mode, campaign));

		assertSame(mode, model.getGameMode());
		assertEquals(List.of(campaign), model.getSelectedCampaigns());
	}

	@Test
	void project_should_refreshCampaigns_when_sameGameModeReselected()
	{
		var model = new SourceSelectionModel();
		var mode = new GameMode("Pathfinder");
		var first = new Campaign();
		var second = new Campaign();

		SourceSelectionDialogPane.project(model, bundle(mode, first));
		SourceSelectionDialogPane.project(model, bundle(mode, second));

		// Same mode must not short-circuit the campaign refresh.
		assertSame(mode, model.getGameMode());
		assertEquals(List.of(second), model.getSelectedCampaigns());
	}

	@Test
	void project_should_replaceNotAppend_when_calledRepeatedly()
	{
		var model = new SourceSelectionModel();
		var pathfinder = new GameMode("Pathfinder");
		var starfinder = new GameMode("Starfinder");
		var first = new Campaign();
		var second = new Campaign();

		SourceSelectionDialogPane.project(model, bundle(pathfinder, first));
		SourceSelectionDialogPane.project(model, bundle(starfinder, second));

		assertSame(starfinder, model.getGameMode());
		assertEquals(List.of(second), model.getSelectedCampaigns());
	}

	@Test
	void project_should_leaveModelUntouched_when_bundleNull()
	{
		var model = new SourceSelectionModel();
		var mode = new GameMode("Pathfinder");
		var campaign = new Campaign();
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
		SourceSelectionDialogPane.project(model, bundle(mode, new Campaign()));

		SourceSelectionDialogPane.project(model, bundle(mode));

		assertSame(mode, model.getGameMode());
		assertEquals(0, model.getSelectedCampaigns().size());
	}
}
