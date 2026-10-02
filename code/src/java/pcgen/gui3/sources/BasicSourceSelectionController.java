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
import java.util.logging.Logger;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseButton;
import javafx.scene.web.WebView;
import javafx.util.Callback;

import pcgen.gui2.UIPropertyContext;

/**
 * Controller for the Basic tab of the source-selection dialog: a list of
 * pre-defined {@link SourceBundle}s plus an HTML preview of the focused entry.
 */
public class BasicSourceSelectionController
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

	private Runnable onLoadRequested = () -> { };

	@FXML
	protected void initialize()
	{
		sourceList.setCellFactory(new SourceCellFactory());
		sourceList.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
			if (selected == null || selected.campaigns().isEmpty())
			{
				SourceInfoRenderer.clear(infoPane.getEngine());
				return;
			}
			CONTEXT.setProperty(PROP_SELECTED_SOURCE, selected.displayName());
			// Use the first campaign as the info source. If the bundle
			// holds several, the info covers only the leader; renderer can be
			// expanded later to summarise the whole bundle.
			SourceInfoRenderer.renderCampaign(infoPane.getEngine(), selected.campaigns().getFirst());
		});
		sourceList.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2
					&& sourceList.getSelectionModel().getSelectedItem() != null)
			{
				onLoadRequested.run();
			}
		});
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
		this.onLoadRequested = handler == null ? () -> { } : handler;
	}

	private static final class SourceCellFactory
			implements Callback<ListView<SourceBundle>, ListCell<SourceBundle>>
	{
		@Override
		public ListCell<SourceBundle> call(ListView<SourceBundle> param)
		{
			return new ListCell<>()
			{
				@Override
				public void updateItem(SourceBundle bundle, boolean empty)
				{
					super.updateItem(bundle, empty);
					if (empty || bundle == null)
					{
						setText(null);
						setGraphic(null);
					}
					else
					{
						setText(bundle.displayName());
					}
				}
			};
		}
	}
}
