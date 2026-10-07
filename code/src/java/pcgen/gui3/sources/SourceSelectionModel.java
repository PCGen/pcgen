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

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.apache.commons.lang3.StringUtils;

import org.apache.commons.lang3.Strings;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Campaign;
import pcgen.core.GameMode;
import pcgen.system.FacadeFactory;

/**
 * The shared, editable state behind the source-selection dialog's Advanced tab:
 * a target {@link GameMode}, a search query, and the campaigns chosen under the
 * mode. {@link #availableCampaigns} is derived from the mode and query, so the
 * controller only reacts to it rather than re-filtering by hand. The Advanced
 * tab binds to this model; the Basic tab projects its selection onto it (see
 * {@link SourceSelectionDialogPane}). The projection is one-way — nothing here
 * writes back to the Basic tab.
 */
public final class SourceSelectionModel
{
	private final ObjectProperty<GameMode> gameMode = new SimpleObjectProperty<>(this, "gameMode");
	private final StringProperty searchQuery = new SimpleStringProperty(this, "searchQuery", "");
	private final ObservableList<Campaign> selectedCampaigns = FXCollections.observableArrayList();

	/**
	 * The campaigns available for the current game mode that match the current
	 * search query. The model maintains this list itself: it is recomputed and
	 * replaced whenever {@link #gameMode} or {@link #searchQuery} changes, so
	 * observers only need a plain list-change listener (no lazy-binding or
	 * listener-kind subtlety), mirroring {@link #selectedCampaigns}.
	 */
	private final ObservableList<Campaign> availableCampaigns = FXCollections.observableArrayList();

	public SourceSelectionModel()
	{
		gameMode.addListener((_, _, _) -> refreshAvailable());
		searchQuery.addListener((_, _, _) -> refreshAvailable());
	}

	private void refreshAvailable()
	{
		availableCampaigns.setAll(filter(getGameMode(), getSearchQuery()));
	}

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

	public StringProperty searchQueryProperty()
	{
		return searchQuery;
	}

	public String getSearchQuery()
	{
		return searchQuery.get();
	}

	public ObservableList<Campaign> getSelectedCampaigns()
	{
		return selectedCampaigns;
	}

	public ObservableList<Campaign> getAvailableCampaigns()
	{
		return availableCampaigns;
	}

	/**
	 * The campaigns supported by {@code mode} (empty for a null/unknown mode),
	 * kept to those matching {@code query}. A blank query is no filter — every
	 * supported campaign is returned.
	 */
	static List<Campaign> filter(GameMode mode, String query)
	{
		Stream<Campaign> supportedCampaigns = StreamSupport
				.stream(FacadeFactory.getSupportedCampaigns(mode).spliterator(), false);

		Stream<Campaign> matched = StringUtils.isBlank(query)
				? supportedCampaigns
				: supportedCampaigns.filter(campaign -> matches(campaign, query));
		return matched.toList();
	}

	/**
	 * Case-insensitive test of whether {@code query} appears in a campaign's
	 * display name, book type, or short source abbreviation (Swing
	 * SearchFilterPanel parity). Blank-query handling lives in {@link #filter}.
	 */
	static boolean matches(Campaign campaign, String query)
	{
		return Strings.CI.contains(campaign.getDisplayName(), query)
				|| Strings.CI.contains(campaign.getListAsString(ListKey.BOOK_TYPE), query)
				|| Strings.CI.contains(campaign.get(StringKey.SOURCE_SHORT), query);
	}
}
