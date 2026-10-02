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

import pcgen.core.Campaign;
import pcgen.core.GameMode;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link SourceSelectionModel}: the plain JavaFX view-model has
 * no live stage or FX thread requirement, so its property and list behaviour
 * are exercised directly.
 */
class SourceSelectionModelTest
{
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
	void selectedCampaigns_should_replaceContents_when_setAll()
	{
		var model = new SourceSelectionModel();
		var first = new Campaign();
		var second = new Campaign();

		model.getSelectedCampaigns().setAll(first);
		model.getSelectedCampaigns().setAll(second);

		assertEquals(1, model.getSelectedCampaigns().size());
		assertSame(second, model.getSelectedCampaigns().getFirst());
	}
}
