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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import pcgen.core.Campaign;
import pcgen.core.GameMode;
import pcgen.facade.core.LoadableFacade.LoadingState;
import pcgen.facade.core.SourceSelectionFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.ListFacades;
import pcgen.facade.util.ReferenceFacade;

import org.junit.jupiter.api.Test;

/**
 * Covers the engine-boundary step that turns a {@link SourceBundle} back into
 * the {@link SourceSelectionFacade} that {@code FacadeFactory} can delete.
 */
class SourceDeleteFacadeTest
{
	private static final GameMode MODE = new GameMode("test");

	private final List<SourceSelectionFacade> deleted = new ArrayList<>();

	private static SourceSelectionFacade named(String name)
	{
		return new SourceSelectionFacade()
		{
			@Override public ListFacade<Campaign> getCampaigns() { return ListFacades.emptyList(); }
			@Override public void setCampaigns(List<Campaign> c) { }
			@Override public ReferenceFacade<GameMode> getGameMode() { return null; }
			@Override public void setGameMode(GameMode g) { }
			@Override public boolean isModifiable() { return true; }
			@Override public LoadingState getLoadingState() { return null; }
			@Override public String getLoadingErrorMessage() { return null; }
			@Override public String toString() { return name; }
		};
	}

	private boolean delete(SourceBundle bundle, SourceSelectionFacade... candidates)
	{
		return SourceSelectionBridge.deleteSourceSelection(bundle, List.of(candidates), deleted::add);
	}

	@Test
	void delete_should_passTheMatchingFacadeToTheEngine()
	{
		var mine = named("Mine");
		var bundle = new SourceBundle("Mine", MODE, List.of(), true, null);

		assertTrue(delete(bundle, named("Other"), mine));
		assertEquals(1, deleted.size());
		assertSame(mine, deleted.getFirst(), "deleting must target the facade the row was built from");
	}

	@Test
	void delete_should_reportFailure_when_noFacadeMatches()
	{
		// The row is only removed on a true return, so a stale row must not vanish.
		var bundle = new SourceBundle("Ghost", MODE, List.of(), true, null);

		assertFalse(delete(bundle, named("Mine")));
		assertTrue(deleted.isEmpty());
	}

}
