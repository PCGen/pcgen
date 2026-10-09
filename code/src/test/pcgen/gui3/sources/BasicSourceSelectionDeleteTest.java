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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import pcgen.core.GameMode;
import pcgen.system.LanguageBundle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

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

	private BasicSourceSelectionController controller;
	private Tab tab;
	private Stage stage;

	/** The bundle the controller asked to delete, or null if it never asked. */
	private SourceBundle deleted;

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
		deleted = null;
		WaitForAsyncUtils.waitForAsyncFx(5000, () -> {
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
		WaitForAsyncUtils.waitForFxEvents();
	}

	@SuppressWarnings("unchecked")
	private ListView<SourceBundle> sourceList()
	{
		return WaitForAsyncUtils.waitForAsyncFx(5000,
				() -> (ListView<SourceBundle>) tab.getContent().lookup("#sourceList"));
	}

	/** Clicks the trash icon on a freshly built row for {@code bundle}. */
	private void clickTrash(SourceBundle bundle)
	{
		WaitForAsyncUtils.waitForAsyncFx(5000, () -> {
			var list = sourceList();
			var cell = (SourceBundleListCell) list.getCellFactory().call(list);
			cell.updateItem(bundle, false);
			((javafx.scene.Parent) cell.getGraphic()).getChildrenUnmodifiable().stream()
					.filter(javafx.scene.control.Button.class::isInstance)
					.map(javafx.scene.control.Button.class::cast)
					.findFirst()
					.orElseThrow()
					.fire();
		});
	}

	private void pressDelete(SourceBundle selection)
	{
		WaitForAsyncUtils.waitForAsyncFx(5000, () -> {
			var list = sourceList();
			list.getSelectionModel().select(selection);
			list.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.DELETE,
					false, false, false, false));
		});
	}

	@Test
	void delete_should_forwardTheBundle_when_confirmationIsAccepted()
	{
		controller.setDeleteConfirmation(bundle -> true);

		clickTrash(CUSTOM);

		assertSame(CUSTOM, deleted);
	}

	@Test
	void delete_should_doNothing_when_confirmationIsDeclined()
	{
		controller.setDeleteConfirmation(bundle -> false);

		clickTrash(CUSTOM);

		assertNull(deleted, "declining the confirmation must leave the source alone");
	}

	@Test
	void deleteKey_should_requestDelete_when_selectionIsModifiable()
	{
		controller.setDeleteConfirmation(bundle -> true);

		pressDelete(CUSTOM);

		assertSame(CUSTOM, deleted, "the trash icon is mouse-only, so DELETE must work too");
	}

	@Test
	void deleteKey_should_doNothing_when_selectionIsNotModifiable()
	{
		controller.setDeleteConfirmation(bundle -> true);

		pressDelete(BUNDLED);

		assertNull(deleted, "a built-in bundle cannot be deleted by any route");
	}

	private Alert confirmationFor(SourceBundle bundle)
	{
		return WaitForAsyncUtils.waitForAsyncFx(5000,
				() -> BasicSourceSelectionController.buildDeleteConfirmation(bundle));
	}

	@Test
	void confirmation_should_quoteTheSourceNameInItsHeader()
	{
		// The header is rendered larger and heavier than the body, so putting the
		// name there emphasises it without needing rich text.
		assertEquals("Delete \"Custom\"?", confirmationFor(CUSTOM).getHeaderText());
	}

	@Test
	void confirmation_should_offerDeleteAndCancel_ratherThanYesAndNo()
	{
		var labels = confirmationFor(CUSTOM).getButtonTypes().stream().map(ButtonType::getText).toList();

		assertEquals(List.of("Delete", "Cancel"), labels,
				"a verb names the consequence; Yes forces the user to re-read the question");
	}

	@Test
	void confirmation_should_renderDeleteToTheRightOfCancel()
	{
		// ButtonBar reorders by ButtonData, so the rendered order is the only
		// honest assertion here -- getButtonTypes() keeps insertion order.
		assertEquals(List.of("Cancel", "Delete"), renderedButtonLabels(confirmationFor(CUSTOM)),
				"the action belongs in the trailing position; a leading Delete reads as a label");
	}

	@Test
	void confirmation_should_makeDeleteTheDefaultButton()
	{
		var alert = confirmationFor(CUSTOM);
		var cancel = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);
		var delete = (Button) alert.getDialogPane().lookupButton(deleteButtonType(alert));

		assertTrue(delete.isDefaultButton(), "Enter must confirm the action the dialog is named after");
		assertFalse(cancel.isDefaultButton(), "two default buttons would fight over Enter");
	}

	@Test
	void confirmation_should_keepCancelOnEscape()
	{
		var cancel = (Button) confirmationFor(CUSTOM).getDialogPane().lookupButton(ButtonType.CANCEL);

		assertTrue(cancel.isCancelButton(), "Escape must always back out of a destructive prompt");
	}

	@Test
	void confirmationResult_should_beDrivenByButtonData_notListPosition()
	{
		// confirmDelete() has to recognise the confirming button. Keying off
		// OK_DONE keeps it correct if the ButtonTypes are ever reordered.
		assertEquals(ButtonBar.ButtonData.OK_DONE, deleteButtonType(confirmationFor(CUSTOM)).getButtonData());
	}

	private static ButtonType deleteButtonType(Alert alert)
	{
		return alert.getButtonTypes().stream()
				.filter(b -> b.getButtonData() != ButtonBar.ButtonData.CANCEL_CLOSE)
				.findFirst()
				.orElseThrow();
	}

	/** Lays the alert out off-screen and reads its buttons left to right. */
	private List<String> renderedButtonLabels(Alert alert)
	{
		return WaitForAsyncUtils.waitForAsyncFx(5000, () -> {
			var pane = alert.getDialogPane();
			pane.applyCss();
			pane.layout();
			var bar = (ButtonBar) pane.lookup(".button-bar");
			var labels = new ArrayList<String>();
			collectButtonText(bar, labels);
			return labels;
		});
	}

	private static void collectButtonText(Parent parent, List<String> into)
	{
		for (var child : parent.getChildrenUnmodifiable())
		{
			if (child instanceof Button button)
			{
				into.add(button.getText());
			}
			else if (child instanceof Parent nested)
			{
				collectButtonText(nested, into);
			}
		}
	}

	@Test
	void basicTab_should_notCarryATabWideDeleteButton()
	{
		assertNull(WaitForAsyncUtils.waitForAsyncFx(5000, () -> tab.getContent().lookup("#btnDelete")),
				"Delete belongs to the row that owns it, not to the tab");
	}
}
