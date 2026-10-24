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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.util.WaitForAsyncUtils.waitForAsyncFx;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tab;
import javafx.stage.Stage;

import pcgen.core.Campaign;
import pcgen.core.GameMode;
import pcgen.facade.util.DefaultListFacade;
import pcgen.system.FacadeFactory;
import pcgen.system.LanguageBundle;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

/**
 * The Advanced tab owns "Save Source Selection" and the "Always use advanced
 * sources" check box, because both act on this tab's campaign set. Keeping them
 * here (rather than in the dialog's shared button bar) means the bar never
 * changes shape on a tab switch, and Save cannot dismiss the dialog the way a
 * {@code ButtonType} would.
 */
@ExtendWith(ApplicationExtension.class)
class AdvancedSourceSelectionSaveTest
{
	private final GameMode mode = new GameMode("AdvancedSourceSelectionSaveTest");
	private final Campaign core = campaign();
	private final SourceSelectionModel model = new SourceSelectionModel();
	private AdvancedSourceSelectionController controller;
	private Tab tab;
	private Stage stage;
	private Field campaignListMap;
	private Object previousCampaigns;

	private static Campaign campaign()
	{
		var campaign = new Campaign();
		campaign.setName("Core Rulebook");
		campaign.setSourceURI(URI.create("file:/test/core.pcc"));
		return campaign;
	}

	@Start
	private void start(Stage primaryStage)
	{
		// Forces the bundle to load; the tab's FXML resolves %-keys against it.
		LanguageBundle.getString("");
		this.stage = primaryStage;
	}

	@BeforeEach
	void loadTab() throws Exception
	{
		campaignListMap = FacadeFactory.class.getDeclaredField("campaignListMap");
		campaignListMap.setAccessible(true);
		previousCampaigns = campaignListMap.get(null);
		campaignListMap.set(null, Map.of(mode, new DefaultListFacade<>(List.of(core))));
		model.setGameMode(mode);
		waitForAsyncFx(5000, () -> {
			var loader = new FXMLLoader(
					AdvancedSourceSelectionController.class.getResource("AdvancedSourceSelection.fxml"),
					LanguageBundle.getBundle());
			try
			{
				tab = loader.load();
			}
			catch (Exception exception)
			{
				throw new IllegalStateException(exception);
			}
			controller = loader.getController();
			controller.setExistingSources(List::of);
			controller.setModel(model);
			// Showing the tab's content builds the control skins, without which
			// nodes nested in SplitPanes are not yet lookup-able.
			stage.setScene(new Scene((Parent) tab.getContent()));
			stage.show();
		});
		waitForFxEvents();
	}

	@AfterEach
	void closeTabAndRestoreCampaigns() throws Exception
	{
		try
		{
			waitForAsyncFx(5000, controller::close);
		}
		finally
		{
			campaignListMap.set(null, previousCampaigns);
		}
	}

	private Node lookup(String selector)
	{
		return waitForAsyncFx(5000, () -> tab.getContent().lookup(selector));
	}

	/** The ids sharing a parent row with {@code selector}, for placement assertions. */
	private List<String> rowSiblingsOf(String selector)
	{
		return waitForAsyncFx(5000, () -> {
			Node node = tab.getContent().lookup(selector);
			return node.getParent().getChildrenUnmodifiable().stream()
					.map(Node::getId)
					.filter(Objects::nonNull)
					.toList();
		});
	}

	@Test
	void alwaysAdvanced_should_sitBetweenUnloadAllAndAddSelected()
	{
		assertEquals(List.of("btnUnloadAll", "chkAlwaysAdvanced", "btnAddSelected"),
				rowSiblingsOf("#chkAlwaysAdvanced"),
				"the check box belongs in the available-sources action row, in that order");
	}

	@Test
	void save_should_sitToTheRightOfRemoveSelected()
	{
		assertEquals(List.of("btnRemoveSelected", "btnSave"),
				rowSiblingsOf("#btnSave"),
				"Save belongs in the selected-sources action row, after Remove");
	}

	@Test
	void advancedTab_should_hostAlwaysAdvancedCheckBox()
	{
		assertInstanceOf(CheckBox.class, lookup("#chkAlwaysAdvanced"));
	}

	@Test
	void save_should_beDisabled_when_noCampaignsAreSelected()
	{
		assertTrue(waitForAsyncFx(5000,
				() -> saveButton().isDisabled()));
	}

	@Test
	void namePrompt_should_disableSavingUntilTheNameIsNonblank()
	{
		waitForAsyncFx(5000, () -> {
			var dialog = controller.buildNamePrompt("");
			var save = dialog.getDialogPane().lookupButton(ButtonType.OK);
			assertTrue(save.isDisabled());

			dialog.getEditor().setText("   ");
			assertTrue(save.isDisabled());

			dialog.getEditor().setText(" My Sources ");
			assertFalse(save.isDisabled());
			assertEquals(stage, dialog.getOwner());
		});
	}

	/**
	 * Save asks for a name first, so a user who changes their mind at the
	 * prompt must not have anything stored.
	 */
	@Test
	void save_should_askForANameButStoreNothing_when_thePromptIsCancelled()
	{
		selectCampaign();
		var asked = new AtomicInteger();
		var fired = new AtomicInteger();
		waitForAsyncFx(5000, () -> {
			controller.setOnSaveRequested(_ -> fired.incrementAndGet());
			controller.setNamePrompt(_ -> {
				asked.incrementAndGet();
				return Optional.empty();
			});
			saveButton().fire();
		});

		assertEquals(1, asked.get(), "the button must reach the save flow");
		assertEquals(0, fired.get());
	}

	@Test
	void save_should_forwardTheCampaignsAndMode_when_thePromptIsAccepted()
	{
		selectCampaign();
		var saved = new AtomicReference<SourceBundle>();
		waitForAsyncFx(5000, () -> {
			controller.setOnSaveRequested(saved::set);
			controller.setNamePrompt(_ -> Optional.of("My Sources"));
			saveButton().fire();
		});

		assertEquals("My Sources", saved.get().name());
		assertEquals(List.of(core), saved.get().campaigns());
		assertSame(core, saved.get().campaigns().getFirst());
		assertSame(mode, saved.get().gameMode());
	}

	private Button saveButton()
	{
		return (Button) tab.getContent().lookup("#btnSave");
	}

	private void selectCampaign()
	{
		waitForAsyncFx(5000, () -> {
			model.getSelectedCampaigns().setAll(core);
			assertFalse(saveButton().isDisabled());
		});
	}

	@Test
	void alwaysAdvanced_should_exposeItsSelectedState()
	{
		waitForAsyncFx(5000,
				() -> ((CheckBox) tab.getContent().lookup("#chkAlwaysAdvanced")).setSelected(true));

		assertTrue(waitForAsyncFx(5000, () -> controller.isAlwaysAdvanced()));
	}
}
