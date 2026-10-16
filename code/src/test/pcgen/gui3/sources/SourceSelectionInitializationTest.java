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
import static org.testfx.util.WaitForAsyncUtils.asyncFx;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javafx.collections.FXCollections;

import pcgen.core.Campaign;
import pcgen.core.GameMode;
import pcgen.core.SettingsHandler;
import pcgen.facade.util.DefaultListFacade;
import pcgen.gui2.UIPropertyContext;
import pcgen.system.FacadeFactory;
import pcgen.system.LanguageBundle;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

/** Exercises preferred-tab restoration through both real FXML controllers. */
@ExtendWith(ApplicationExtension.class)
class SourceSelectionInitializationTest
{
	private static final UIPropertyContext CONTEXT =
			UIPropertyContext.createContext("advancedSourceSelectionPanel");
	private static final String MODE_KEY = "selectedGame";
	private static final String SOURCES_KEY = "selectedSources.SourceSelectionInitializationTest";

	private final GameMode mode = new GameMode("SourceSelectionInitializationTest");
	private final Campaign basic = campaign("Basic");
	private final Campaign advanced = campaign("Advanced");
	private Field campaignListMap;
	private Object previousCampaigns;
	private GameMode previousMode;
	private String previousModeName;
	private String previousSources;

	@BeforeEach
	void setUp() throws Exception
	{
		LanguageBundle.getString("");
		campaignListMap = FacadeFactory.class.getDeclaredField("campaignListMap");
		campaignListMap.setAccessible(true);
		previousCampaigns = campaignListMap.get(null);
		campaignListMap.set(null, Map.of(mode, new DefaultListFacade<>(List.of(basic, advanced))));
		previousMode = SettingsHandler.getGameAsProperty().get();
		SettingsHandler.getGameAsProperty().set(mode);
		previousModeName = CONTEXT.getProperty(MODE_KEY, null);
		previousSources = CONTEXT.getProperty(SOURCES_KEY, null);
		CONTEXT.setProperty(MODE_KEY, mode.getName());
		CONTEXT.setProperty(SOURCES_KEY, advanced.toString());
	}

	@AfterEach
	void restoreState() throws Exception
	{
		waitForFxEvents();
		campaignListMap.set(null, previousCampaigns);
		SettingsHandler.getGameAsProperty().set(previousMode);
		CONTEXT.setProperty(MODE_KEY, previousModeName);
		CONTEXT.setProperty(SOURCES_KEY, previousSources);
	}

	private static Campaign campaign(String name)
	{
		var campaign = new Campaign();
		campaign.setName(name);
		campaign.setSourceURI(URI.create("file:/test/" + name + ".pcc"));
		return campaign;
	}

	private List<Campaign> openWith(boolean alwaysAdvanced) throws Exception
	{
		return asyncFx(() -> {
			try (var pane = new SourceSelectionDialogPane(
					FXCollections.observableArrayList(new SourceBundle("Basic", mode, List.of(basic), false, null)),
					FXCollections.observableArrayList(mode)))
			{
				pane.applyTabPreference(new SourceTabPreference(alwaysAdvanced));
				return pane.getSelectedSource().orElseThrow().campaigns();
			}
		}).get(5, TimeUnit.SECONDS);
	}

	@Test
	void openingAdvanced_should_restoreItsRememberedCampaigns() throws Exception
	{
		assertEquals(List.of(advanced), openWith(true));
	}

	@Test
	void openingBasic_should_useTheBasicBundle() throws Exception
	{
		assertEquals(List.of(basic), openWith(false));
	}
}
