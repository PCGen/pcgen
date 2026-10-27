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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.util.WaitForAsyncUtils.waitForAsyncFx;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import pcgen.core.GameMode;
import pcgen.gui2.UIPropertyContext;
import pcgen.system.LanguageBundle;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

/**
 * Deleting a source is destructive and, since the trash icon sits on the row
 * itself, only one click away — so the controller always asks for confirmation
 * before forwarding the request. The legacy Swing dialog deleted outright.
 *
 * <p>An icon inside a list row is mouse-only, so the list also answers the
 * DELETE key for the selected bundle.
 */
@ExtendWith(ApplicationExtension.class)
class BasicSourceSelectionDeleteTest
{
	private static final GameMode MODE = new GameMode("test");
	private static final SourceBundle BUNDLED = new SourceBundle("Bundled", MODE, List.of(), false, null);
	private static final SourceBundle CUSTOM = new SourceBundle("Custom", MODE, List.of(), true, null);
	private static final UIPropertyContext CONTEXT = UIPropertyContext.createContext("SourceSelectionDialog");

	private BasicSourceSelectionController controller;
	private Tab tab;
	private Stage stage;
	private String previousSource;

	/** The bundle the controller asked to delete, or null if it never asked. */
	private SourceBundle deleted;

	@Start
	private void start(Stage primaryStage)
	{
		// Forces the bundle to load; the tab's FXML resolves %-keys against it.
		LanguageBundle.getString("");
		this.stage = primaryStage;
	}

	private void loadTab()
	{
		deleted = null;
		previousSource = CONTEXT.getProperty("selectedSource", null);
		waitForAsyncFx(5000, () -> {
			var loader = new FXMLLoader(
					BasicSourceSelectionController.class.getResource("BasicSourceSelection.fxml"),
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
			controller.setSources(FXCollections.observableArrayList(BUNDLED, CUSTOM));
			controller.setOnDeleteRequested(bundle -> deleted = bundle);
			// Showing the tab's content builds the control skins, without which
			// nested nodes (the list inside the SplitPane) are not yet lookup-able.
			stage.setScene(new Scene((Parent) tab.getContent()));
			stage.show();
		});
		waitForFxEvents();
	}

	@AfterEach
	void closeTab()
	{
		if (controller != null)
		{
			try
			{
				waitForAsyncFx(5000, controller::close);
			}
			finally
			{
				CONTEXT.setProperty("selectedSource", previousSource);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private ListView<SourceBundle> sourceList()
	{
		return (ListView<SourceBundle>) tab.getContent().lookup("#sourceList");
	}

	/** Clicks the trash icon on a freshly built row for {@code bundle}. */
	private void clickTrash(SourceBundle bundle)
	{
		waitForAsyncFx(5000, () -> {
			var list = sourceList();
			var cell = (SourceBundleListCell) list.getCellFactory().call(list);
			cell.updateItem(bundle, false);
			((Parent) cell.getGraphic()).getChildrenUnmodifiable().stream()
					.filter(Button.class::isInstance)
					.map(Button.class::cast)
					.findFirst()
					.orElseThrow()
					.fire();
		});
	}

	private void pressDelete(SourceBundle selection)
	{
		waitForAsyncFx(5000, () -> {
			var list = sourceList();
			list.getSelectionModel().select(selection);
			list.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.DELETE,
					false, false, false, false));
		});
	}

	@Test
	void delete_should_forwardTheBundle_when_confirmationIsAccepted()
	{
		loadTab();
		controller.setDeleteConfirmation(bundle -> true);

		clickTrash(CUSTOM);

		assertSame(CUSTOM, deleted);
	}

	@Test
	void delete_should_doNothing_when_confirmationIsDeclined()
	{
		loadTab();
		controller.setDeleteConfirmation(bundle -> false);

		clickTrash(CUSTOM);

		assertNull(deleted, "declining the confirmation must leave the source alone");
	}

	@Test
	void deleteKey_should_requestDelete_when_selectionIsModifiable()
	{
		loadTab();
		controller.setDeleteConfirmation(bundle -> true);

		pressDelete(CUSTOM);

		assertSame(CUSTOM, deleted, "the trash icon is mouse-only, so DELETE must work too");
	}

	@Test
	void deleteKey_should_doNothing_when_selectionIsNotModifiable()
	{
		loadTab();
		controller.setDeleteConfirmation(bundle -> true);

		pressDelete(BUNDLED);

		assertNull(deleted, "a built-in bundle cannot be deleted by any route");
	}

	@Test
	void confirmation_should_nameTheSourceAndOfferDeleteWithCancel()
	{
		waitForAsyncFx(5000, () -> {
			var alert = BasicSourceSelectionController.buildDeleteConfirmation(CUSTOM);
			var pane = alert.getDialogPane();
			var deleteType = alert.getButtonTypes().stream()
					.filter(button -> button != ButtonType.CANCEL).findFirst().orElseThrow();
			var delete = (Button) pane.lookupButton(deleteType);
			var cancel = (Button) pane.lookupButton(ButtonType.CANCEL);

			assertAll(
					() -> assertEquals("Delete \"Custom\"?", alert.getHeaderText()),
					() -> assertEquals(List.of("Delete", "Cancel"),
							alert.getButtonTypes().stream().map(ButtonType::getText).toList()),
					() -> assertEquals(ButtonBar.ButtonData.OK_DONE, deleteType.getButtonData()),
					() -> assertTrue(delete.isDefaultButton(), "Enter confirms deletion"),
					() -> assertFalse(cancel.isDefaultButton()),
					() -> assertTrue(cancel.isCancelButton(), "Escape cancels deletion"));
		});
	}

	@Test
	void basicTab_should_notCarryATabWideDeleteButton()
	{
		loadTab();
		assertNull(waitForAsyncFx(5000, () -> tab.getContent().lookup("#btnDelete")),
				"Delete belongs to the row that owns it, not to the tab");
	}
}
