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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.util.WaitForAsyncUtils.asyncFx;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

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
			try (var pane = newPane(alwaysAdvanced))
			{
				return pane.getSelectedSource().orElseThrow().campaigns();
			}
		}).get(5, TimeUnit.SECONDS);
	}

	private SourceSelectionDialogPane newPane(boolean alwaysAdvanced)
	{
		var pane = new SourceSelectionDialogPane(
				FXCollections.observableArrayList(new SourceBundle("Basic", mode, List.of(basic), false, null)),
				FXCollections.observableArrayList(mode));
		pane.applyTabPreference(new SourceTabPreference(alwaysAdvanced));
		return pane;
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

	@Test
	void unloadRequest_should_preserveTheEditedSelection() throws Exception
	{
		asyncFx(() -> {
			try (var pane = newPane(true))
			{
				int[] requests = {0};
				pane.setOnUnloadAllRequested(() -> requests[0]++);
				var dialog = showDialog(pane);
				try
				{
					((Button) pane.lookup("#btnUnloadAll")).fire();
					assertEquals(1, requests[0]);
					assertEquals(List.of(advanced), pane.getSelectedSource().orElseThrow().campaigns());
				}
				finally
				{
					dialog.close();
				}
			}
		}).get(5, TimeUnit.SECONDS);
	}

	@Test
	void invalidLoad_should_leaveTheDialogOpen() throws Exception
	{
		asyncFx(() -> {
			try (var pane = newPane(true))
			{
				pane.setLoadValidator(bundle -> false);
				var dialog = showDialog(pane);
				try
				{
					loadButton(pane).fire();
					assertTrue(dialog.isShowing());
					assertEquals(List.of(advanced), pane.getSelectedSource().orElseThrow().campaigns());
				}
				finally
				{
					dialog.close();
				}
			}
		}).get(5, TimeUnit.SECONDS);
	}

	@Test
	void validLoad_should_closeWithTheLoadResult() throws Exception
	{
		asyncFx(() -> {
			try (var pane = newPane(true))
			{
				pane.setLoadValidator(bundle -> bundle.campaigns().equals(List.of(advanced)));
				var dialog = showDialog(pane);
				try
				{
					loadButton(pane).fire();
					assertFalse(dialog.isShowing());
					assertTrue(SourceDialogOutcome.loadsSelection(dialog.getResult()));
				}
				finally
				{
					dialog.close();
				}
			}
		}).get(5, TimeUnit.SECONDS);
	}

	@Test
	void basicDoubleClick_should_notBypassLoadValidation() throws Exception
	{
		asyncFx(() -> {
			try (var pane = newPane(false))
			{
				pane.setLoadValidator(bundle -> false);
				var dialog = showDialog(pane);
				try
				{
					pane.lookup("#sourceList").fireEvent(new MouseEvent(
							MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 2,
							false, false, false, false, false, false, false, false, false, false, null));
					assertTrue(dialog.isShowing());
				}
				finally
				{
					dialog.close();
				}
			}
		}).get(5, TimeUnit.SECONDS);
	}

	@Test
	void dialogControls_should_useTheSelectedLocale() throws Exception
	{
		Locale previous = Locale.getDefault();
		try
		{
			Locale.setDefault(Locale.FRENCH);
			LanguageBundle.reload();
			LanguageBundle.getString("");
			asyncFx(() -> {
				try (var pane = newPane(false))
				{
					var tabs = (TabPane) pane.getContent();
					assertEquals("Base", tabs.getTabs().getFirst().getText());
					assertEquals("Avanc\u00e9", tabs.getTabs().get(1).getText());
					assertEquals("Charger", loadButton(pane).getText());
					assertEquals("Installer des donn\u00e9es", pane.getInstallDataButton().getText());
					var introduction = (TextFlow) tabs.getTabs().getFirst().getContent().lookup("TextFlow");
					String text = ((Text) introduction.getChildren().getFirst()).getText();
					assertTrue(text.startsWith("S\u00e9lectionner un ensemble"), text);
				}
			}).get(5, TimeUnit.SECONDS);
		}
		finally
		{
			Locale.setDefault(previous);
			LanguageBundle.reload();
			LanguageBundle.getString("");
		}
	}

	private static Button loadButton(SourceSelectionDialogPane pane)
	{
		ButtonType load = pane.getButtonTypes().stream().filter(SourceDialogOutcome::loadsSelection)
				.findFirst().orElseThrow();
		return (Button) pane.lookupButton(load);
	}

	private static Dialog<ButtonType> showDialog(SourceSelectionDialogPane pane)
	{
		var dialog = new Dialog<ButtonType>();
		dialog.setDialogPane(pane);
		dialog.show();
		pane.applyCss();
		return dialog;
	}
}
