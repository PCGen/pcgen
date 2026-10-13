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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tab;
import javafx.stage.Stage;

import pcgen.system.LanguageBundle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

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
	private AdvancedSourceSelectionController controller;
	private Tab tab;
	private Stage stage;

	@Start
	private void start(Stage primaryStage)
	{
		// Forces the bundle to load; the tab's FXML resolves %-keys against it.
		LanguageBundle.getString("");
		this.stage = primaryStage;
	}

	@BeforeEach
	void loadTab()
	{
		WaitForAsyncUtils.waitForAsyncFx(5000, () -> {
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
			// Showing the tab's content builds the control skins, without which
			// nodes nested in SplitPanes are not yet lookup-able.
			stage.setScene(new Scene((Parent) tab.getContent()));
			stage.show();
		});
		WaitForAsyncUtils.waitForFxEvents();
	}

	private Node lookup(String selector)
	{
		return WaitForAsyncUtils.waitForAsyncFx(5000, () -> tab.getContent().lookup(selector));
	}

	/** The ids sharing a parent row with {@code selector}, for placement assertions. */
	private List<String> rowSiblingsOf(String selector)
	{
		return WaitForAsyncUtils.waitForAsyncFx(5000, () -> {
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
	void advancedTab_should_hostSaveButton()
	{
		assertNotNull(lookup("#btnSave"));
	}

	@Test
	void advancedTab_should_hostAlwaysAdvancedCheckBox()
	{
		assertInstanceOf(CheckBox.class, lookup("#chkAlwaysAdvanced"));
	}

	/**
	 * Save asks for a name first, so a user who changes their mind at the
	 * prompt must not have anything stored.
	 */
	@Test
	void save_should_storeNothing_when_theNamePromptIsCancelled()
	{
		var fired = new AtomicInteger();
		controller.setOnSaveRequested(_ -> fired.incrementAndGet());
		controller.setNamePrompt(_ -> Optional.empty());

		WaitForAsyncUtils.waitForAsyncFx(5000, () -> ((Button) lookup("#btnSave")).fire());

		assertEquals(0, fired.get());
	}

	@Test
	void save_should_askForANameWhenFired()
	{
		var asked = new AtomicInteger();
		controller.setNamePrompt(_ -> {
			asked.incrementAndGet();
			return Optional.empty();
		});

		WaitForAsyncUtils.waitForAsyncFx(5000, () -> ((Button) lookup("#btnSave")).fire());

		assertEquals(1, asked.get(), "the button must reach the save flow");
	}

	@Test
	void alwaysAdvanced_should_exposeItsSelectedState()
	{
		WaitForAsyncUtils.waitForAsyncFx(5000,
				() -> ((CheckBox) lookup("#chkAlwaysAdvanced")).setSelected(true));

		assertTrue(WaitForAsyncUtils.waitForAsyncFx(5000, () -> controller.isAlwaysAdvanced()));
	}
}
