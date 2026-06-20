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
import java.util.Objects;

import pcgen.core.Campaign;
import pcgen.core.GameMode;
import pcgen.facade.core.LoadableFacade.LoadingState;

/**
 * gui3-side aggregate of "a selection of sources for a game mode" — what the
 * user picks in the source-selection dialog. Replaces the legacy
 * {@code SourceSelectionFacade} for UI purposes; convert at the engine
 * boundary via {@link pcgen.system.FacadeFactory#createSourceSelection}.
 *
 * @param name display name (may be null for ad-hoc selections — falls back to
 *             the game-mode name when shown).
 * @param gameMode the game mode the bundle targets; never null.
 * @param campaigns the campaigns to load; may be empty for an "empty" bundle.
 * @param modifiable whether the user can rename or delete this bundle.
 * @param loadingState last-known loading state — null until the bundle has
 *                    actually been attempted.
 */
public record SourceBundle(
		String name,
		GameMode gameMode,
		List<Campaign> campaigns,
		boolean modifiable,
		LoadingState loadingState)
{
	public SourceBundle
	{
		Objects.requireNonNull(gameMode, "gameMode");
		campaigns = List.copyOf(campaigns);
	}

	/**
	 * Display label matching the legacy {@code BasicSourceSelectionFacade.toString()}
	 * fallback so existing screenshots/tests stay valid.
	 */
	public String displayName()
	{
		return name != null ? name : gameMode.getDisplayName();
	}
}
