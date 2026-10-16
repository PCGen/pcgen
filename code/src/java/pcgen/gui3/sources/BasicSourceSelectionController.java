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

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.web.WebView;

import pcgen.gui2.UIPropertyContext;
import pcgen.system.LanguageBundle;

/**
 * Controller for the Basic tab of the source-selection dialog: a list of
 * pre-defined {@link SourceBundle}s plus an HTML preview of the focused entry.
 */
public class BasicSourceSelectionController implements AutoCloseable
{
	private static final Logger LOG = Logger.getLogger(BasicSourceSelectionController.class.getName());

	// Shares the legacy Swing dialog's context/key so a previously remembered
	// source carries over and both dialogs stay in sync.
	private static final UIPropertyContext CONTEXT =
			UIPropertyContext.createContext("SourceSelectionDialog"); //$NON-NLS-1$
	private static final String PROP_SELECTED_SOURCE = "selectedSource"; //$NON-NLS-1$
	private static final String DEFAULT_SOURCE = "Pathfinder RPG for Players"; //$NON-NLS-1$

	@FXML
	private SplitPane splitPane;

	@FXML
	private ListView<SourceBundle> sourceList;

	@FXML
	private WebView infoPane;

	private SourceInfoRenderer infoRenderer;

	private Runnable onLoadRequested;

	private Consumer<SourceBundle> onDeleteRequested;

	private Predicate<SourceBundle> deleteConfirmation = this::confirmDelete;

	@FXML
	protected void initialize()
	{
		infoRenderer = new SourceInfoRenderer(infoPane.getEngine());
		sourceList.setCellFactory(list -> new SourceBundleListCell(this::requestDelete));
		sourceList.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
			if (selected == null || selected.campaigns().isEmpty())
			{
				infoRenderer.clear();
				return;
			}
			CONTEXT.setProperty(PROP_SELECTED_SOURCE, selected.displayName());
			// Use the first campaign as the info source. If the bundle
			// holds several, the info covers only the leader; renderer can be
			// expanded later to summarise the whole bundle.
			infoRenderer.renderCampaign(selected.campaigns().getFirst());
		});
		// The trash icon is mouse-only, so give the list a keyboard equivalent.
		sourceList.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.DELETE)
			{
				requestDelete(sourceList.getSelectionModel().getSelectedItem());
				event.consume();
			}
		});
		sourceList.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2
					&& sourceList.getSelectionModel().getSelectedItem() != null)
			{
				onLoadRequested.run();
			}
		});
	}

	/** Releases the preview's document channel when the source dialog closes. */
	@Override
	public void close()
	{
		infoRenderer.close();
	}

	public void setSources(ObservableList<SourceBundle> items)
	{
		sourceList.setItems(items);
		// Restore the previously selected source (shared with the legacy dialog),
		// falling back to a sensible default and then the first entry.
		String remembered = CONTEXT.initProperty(PROP_SELECTED_SOURCE, DEFAULT_SOURCE);
		items.stream()
				.filter(bundle -> remembered.equals(bundle.displayName()))
				.findFirst()
				.ifPresentOrElse(bundle -> {
					LOG.fine(() -> "Restored saved source: " + remembered);
					sourceList.getSelectionModel().select(bundle);
				}, sourceList.getSelectionModel()::selectFirst);
	}

	/**
	 * Selects the row holding {@code bundle}, so a selection just saved from the
	 * Advanced tab is visible here when the user looks.
	 *
	 * <p>Matched by name rather than identity: the row carries a different
	 * {@link SourceBundle} instance describing the same stored selection.
	 *
	 * @param bundle the selection to highlight
	 */
	public void selectSource(SourceBundle bundle)
	{
		sourceList.getItems().stream()
				.filter(item -> bundle.name().equals(item.name()))
				.findFirst()
				.ifPresent(item -> {
					sourceList.getSelectionModel().select(item);
					sourceList.scrollTo(item);
				});
	}

	public Optional<SourceBundle> getSelectedSource()
	{
		return Optional.ofNullable(sourceList.getSelectionModel().getSelectedItem());
	}

	/**
	 * The list's selected-source property, so the dialog can observe Basic
	 * selections and project them onto the shared {@link SourceSelectionModel}.
	 */
	public ReadOnlyObjectProperty<SourceBundle> selectedSourceProperty()
	{
		return sourceList.getSelectionModel().selectedItemProperty();
	}

	/**
	 * Registers the action to invoke when the user double-clicks an entry —
	 * typically the same action as the dialog's Load button.
	 */
	public void setOnLoadRequested(Runnable handler)
	{
		this.onLoadRequested = handler;
	}

	/**
	 * Registers the action to invoke for Delete — deleting the custom source
	 * selection currently picked in the list.
	 */
	public void setOnDeleteRequested(Consumer<SourceBundle> handler)
	{
		this.onDeleteRequested = handler;
	}

	/** Overrides the confirmation prompt shown before a delete is forwarded. */
	void setDeleteConfirmation(Predicate<SourceBundle> confirmation)
	{
		this.deleteConfirmation = confirmation;
	}

	/**
	 * Forwards a delete request for {@code bundle}, after re-checking that it is
	 * actually deletable and that the user confirmed. The modifiable check is
	 * repeated here because both entry points (the row's trash icon and the
	 * DELETE key) funnel through this method.
	 */
	private void requestDelete(SourceBundle bundle)
	{
		if (isModifiable(bundle) && deleteConfirmation.test(bundle))
		{
			onDeleteRequested.accept(bundle);
		}
	}

	/**
	 * Default confirmation: deleting a saved selection is irreversible, and the
	 * trash icon sits on the row itself, so it is easy to hit by accident.
	 */
	private boolean confirmDelete(SourceBundle bundle)
	{
		var alert = buildDeleteConfirmation(bundle);
		alert.initOwner(sourceList.getScene().getWindow());
		return alert.showAndWait()
				.filter(button -> button.getButtonData() == ButtonData.OK_DONE)
				.isPresent();
	}

	/**
	 * Builds the confirmation prompt. Split from {@link #confirmDelete} so the
	 * wording and button semantics can be asserted without showing a modal
	 * dialog.
	 */
	static Alert buildDeleteConfirmation(SourceBundle bundle)
	{
		var alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle(LanguageBundle.getString("in_src_deleteTitle"));
		// The name goes in the header, which renders larger and heavier than the
		// body — emphasis without resorting to a rich-text content node. The
		// quote marks live in the bundle so translators can use their locale's.
		alert.setHeaderText(LanguageBundle.getFormattedString(
				"in_src_deleteConfirm", bundle.displayName()));
		alert.setContentText(LanguageBundle.getString("in_src_deleteWarning"));

		var delete = new ButtonType(LanguageBundle.getString("in_delete"), ButtonData.OK_DONE);
		alert.getButtonTypes().setAll(delete, ButtonType.CANCEL);
		// OK_DONE is what JavaFX keys both placement and default status off:
		// ButtonBar sorts it into the trailing group (after CANCEL_CLOSE) and
		// DialogPane.createButton calls setDefaultButton(ButtonData#isDefaultButton).
		// Nothing further to set -- doing it by hand is how the odd leading
		// Delete button crept in.
		return alert;
	}

	private static boolean isModifiable(SourceBundle bundle)
	{
		return bundle != null && bundle.modifiable();
	}
}
