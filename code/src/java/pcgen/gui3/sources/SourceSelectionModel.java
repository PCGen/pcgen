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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 */
package pcgen.gui3.sources;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import pcgen.core.Campaign;
import pcgen.core.GameMode;

/**
 * The shared, editable state behind the source-selection dialog's Advanced tab:
 * a target {@link GameMode} and the campaigns chosen under it. The Advanced tab
 * binds to it; the Basic tab projects its selection onto it (see
 * {@link SourceSelectionDialogPane}). The projection is one-way — nothing here
 * writes back to the Basic tab.
 */
public final class SourceSelectionModel
{
	private final ObjectProperty<GameMode> gameMode = new SimpleObjectProperty<>(this, "gameMode");
	private final ObservableList<Campaign> selectedCampaigns = FXCollections.observableArrayList();

	public ObjectProperty<GameMode> gameModeProperty()
	{
		return gameMode;
	}

	public GameMode getGameMode()
	{
		return gameMode.get();
	}

	public void setGameMode(GameMode mode)
	{
		gameMode.set(mode);
	}

	public ObservableList<Campaign> getSelectedCampaigns()
	{
		return selectedCampaigns;
	}
}
