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

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseButton;
import javafx.scene.web.WebView;
import javafx.util.Callback;

import pcgen.system.FacadeFactory;

/**
 * Controller for the Basic tab of the source-selection dialog: a list of
 * pre-defined {@link SourceBundle}s plus an HTML preview of the focused entry.
 */
public class BasicSourceSelectionController
{
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
				infoPane.getEngine().loadContent("");
				return;
			}
			// Use the first campaign as the legacy info source. If the bundle
			// holds several, the HTML covers only the leader; renderer can be
			// expanded later to summarise the whole bundle.
			var infoText = FacadeFactory.getCampaignInfoFactory().getHTMLInfo(selected.campaigns().get(0));
			infoPane.getEngine().loadContent(infoText);
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
		sourceList.getSelectionModel().selectFirst();
	}

	public Optional<SourceBundle> getSelectedSource()
	{
		return Optional.ofNullable(sourceList.getSelectionModel().getSelectedItem());
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
