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

import java.util.List;

import pcgen.core.Campaign;
import pcgen.core.GameMode;
import pcgen.facade.core.LoadableFacade.LoadingState;
import pcgen.facade.core.SourceSelectionFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.ListFacades;
import pcgen.facade.util.ReferenceFacade;

/**
 * A {@link SourceSelectionFacade} stub named by {@link #toString()} that records
 * what the engine was asked to store, as the real facade does.
 */
final class FakeSourceSelectionFacade implements SourceSelectionFacade
{
	private final String name;
	List<Campaign> campaigns;
	GameMode gameMode;

	FakeSourceSelectionFacade(String name)
	{
		this.name = name;
	}

	static FakeSourceSelectionFacade named(String name)
	{
		return new FakeSourceSelectionFacade(name);
	}

	@Override
	public ListFacade<Campaign> getCampaigns()
	{
		return ListFacades.emptyList();
	}

	@Override
	public void setCampaigns(List<Campaign> campaigns)
	{
		this.campaigns = campaigns;
	}

	@Override
	public ReferenceFacade<GameMode> getGameMode()
	{
		return null;
	}

	@Override
	public void setGameMode(GameMode gameMode)
	{
		this.gameMode = gameMode;
	}

	@Override
	public boolean isModifiable()
	{
		return true;
	}

	@Override
	public LoadingState getLoadingState()
	{
		return null;
	}

	@Override
	public String getLoadingErrorMessage()
	{
		return null;
	}

	@Override
	public String toString()
	{
		return name;
	}
}
