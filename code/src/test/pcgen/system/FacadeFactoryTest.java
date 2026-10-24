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
package pcgen.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pcgen.core.Campaign;
import pcgen.core.GameMode;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.persistence.PersistenceManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests that qualification trials restore the actual persistence manager's chosen sources. */
class FacadeFactoryTest
{
	private final GameMode mode = new GameMode("FacadeFactoryTest");
	private final PersistenceManager manager = PersistenceManager.getInstance();
	private final List<URI> chosenSources = List.of(
			URI.create("file:/campaigns/chosen-a.pcc"), URI.create("file:/campaigns/chosen-b.pcc"));
	private GameMode previousMode;
	private List<URI> previousSources;
	private String previousSourcesOption;

	@BeforeEach
	void setUp()
	{
		previousMode = SettingsHandler.getGameAsProperty().get();
		SettingsHandler.getGameAsProperty().set(mode);
		previousSources = new ArrayList<>(manager.getChosenCampaignSourcefiles());
		previousSourcesOption = SettingsHandler.getOptions().getProperty(sourcesOptionKey());
		manager.setChosenCampaignSourcefiles(chosenSources);
	}

	@AfterEach
	void tearDown()
	{
		try
		{
			manager.setChosenCampaignSourcefiles(previousSources);
			if (previousSourcesOption == null)
			{
				SettingsHandler.getOptions().remove(sourcesOptionKey());
			}
			else
			{
				SettingsHandler.getOptions().setProperty(sourcesOptionKey(), previousSourcesOption);
			}
		}
		finally
		{
			SettingsHandler.getGameAsProperty().set(previousMode);
		}
	}

	@Test
	void emptyQualificationTrial_restoresChosenSources()
	{
		assertEquals(Collections.emptyList(), FacadeFactory.findUnqualified(Collections.emptyList()));

		assertEquals(chosenSources, manager.getChosenCampaignSourcefiles());
	}

	@Test
	void nonemptyQualificationTrial_restoresChosenSources()
	{
		var campaign = new Campaign();
		campaign.setName("Trial");
		campaign.setSourceURI(URI.create("file:/campaigns/trial.pcc"));

		assertEquals(Collections.emptyList(), FacadeFactory.findUnqualified(List.of(campaign)));

		assertEquals(chosenSources, manager.getChosenCampaignSourcefiles());
	}

	@Test
	void failedQualificationTrial_restoresChosenSources()
	{
		var failure = new IllegalStateException("Qualification failed");
		var campaign = new Campaign()
		{
			@Override
			public boolean qualifies(PlayerCharacter character, Object owner)
			{
				throw failure;
			}
		};
		campaign.setName("Failing trial");
		campaign.setSourceURI(URI.create("file:/campaigns/failing-trial.pcc"));

		assertSame(failure, assertThrows(IllegalStateException.class,
				() -> FacadeFactory.findUnqualified(List.of(campaign))));
		assertEquals(chosenSources, manager.getChosenCampaignSourcefiles());
	}

	private String sourcesOptionKey()
	{
		return "pcgen.files.chosenCampaignSourcefiles." + mode.getName();
	}
}
