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

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.web.WebView;
import javafx.util.Callback;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Campaign;
import pcgen.core.GameMode;
import pcgen.gui2.UIPropertyContext;
import pcgen.system.FacadeFactory;
import pcgen.system.LanguageBundle;

public class AdvancedSourceSelectionController
{
	private static final UIPropertyContext CONTEXT =
			UIPropertyContext.createContext("advancedSourceSelectionPanel"); //$NON-NLS-1$
	private static final String PROP_SELECTED_GAME = "selectedGame"; //$NON-NLS-1$
	private static final String PROP_SELECTED_SOURCES = "selectedSources."; //$NON-NLS-1$

	private static final Logger LOG = Logger.getLogger(AdvancedSourceSelectionController.class.getName());

	@FXML
	private Button btnFilterClear;

	@FXML
	private Button btnAddSelected;

	@FXML
	private Button btnRemoveSelected;

	@FXML
	private Button btnUnloadAll;

	@FXML
	private TextField fldSearch;

	@FXML
	private ComboBox<GameMode> cmbGameMode;

	@FXML
	private TreeTableView<SourceTreeNode> treeAvailable;

	@FXML
	private TreeTableView<SourceTreeNode> treeSelected;

	@FXML
	private WebView infoPane;

	private final ObservableList<Campaign> selectedCampaigns = FXCollections.observableArrayList();

	private Runnable onLoadRequested = () -> { };
	private Runnable onUnloadAllRequested = () -> { };

	@FXML
	protected void initialize()
	{
		LOG.fine("Initialize AdvancedSourceSelectionController");
		btnFilterClear.setGraphic(new ImageView(pcgen.gui2.tools.Icons.CloseX9.asJavaFX()));
		cmbGameMode.setCellFactory(new GameModeCellFactory());

		treeAvailable.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
		treeSelected.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
		treeAvailable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		treeSelected.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		bindAvailableColumns();
		bindSelectedColumns();

		// Either tree drives the bottom info pane: whichever was clicked last
		// shows its leaf's HTML. The legacy mirrors this — the panel listens to
		// both selection models with the same handler.
		treeAvailable.getSelectionModel().selectedItemProperty()
				.addListener((obs, old, item) -> showInfoFor(item));
		treeSelected.getSelectionModel().selectedItemProperty()
				.addListener((obs, old, item) -> showInfoFor(item));

		// Selected list changes drive the right-side tree rebuild. Done as a
		// listener (rather than a binding) so we keep the same TreeItem
		// expansion state and column widths across rebuilds.
		selectedCampaigns.addListener((javafx.collections.ListChangeListener<Campaign>) c ->
				treeSelected.setRoot(buildTree(selectedCampaigns)));

		treeAvailable.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2
					&& selectedLeavesIn(treeAvailable).findAny().isPresent())
			{
				onLoadRequested.run();
			}
		});
	}

	private void bindAvailableColumns()
	{
		// FXML's <TreeTableColumn> declarations are untyped, so the columns
		// list comes back as TreeTableColumn<SourceTreeNode, ?>. Cast to
		// String columns at wire-up time so the cell-value factories can
		// return ReadOnlyObjectWrapper<String> without unchecked warnings.
		@SuppressWarnings("unchecked")
		var nameColumn = (TreeTableColumn<SourceTreeNode, String>) treeAvailable.getColumns().get(0);
		@SuppressWarnings("unchecked")
		var bookTypeColumn = (TreeTableColumn<SourceTreeNode, String>) treeAvailable.getColumns().get(1);
		@SuppressWarnings("unchecked")
		var statusColumn = (TreeTableColumn<SourceTreeNode, String>) treeAvailable.getColumns().get(2);
		@SuppressWarnings("unchecked")
		var loadedColumn = (TreeTableColumn<SourceTreeNode, String>) treeAvailable.getColumns().get(3);

		nameColumn.setCellValueFactory(v ->
				new ReadOnlyObjectWrapper<>(v.getValue().getValue().displayLabel()));
		bookTypeColumn.setCellValueFactory(v ->
				new ReadOnlyObjectWrapper<>(campaignOf(v.getValue())
						.map(c -> c.getListAsString(ListKey.BOOK_TYPE)).orElse("")));
		statusColumn.setCellValueFactory(v ->
				new ReadOnlyObjectWrapper<>(campaignOf(v.getValue())
						.map(c -> c.getSafe(ObjectKey.STATUS).toString()).orElse("")));
		loadedColumn.setCellValueFactory(v ->
				new ReadOnlyObjectWrapper<>(campaignOf(v.getValue()).isPresent() ? "Loaded" : "Not loaded"));
	}

	private void bindSelectedColumns()
	{
		@SuppressWarnings("unchecked")
		var selectedNameColumn = (TreeTableColumn<SourceTreeNode, String>) treeSelected.getColumns().get(0);
		selectedNameColumn.setCellValueFactory(v ->
				new ReadOnlyObjectWrapper<>(v.getValue().getValue().displayLabel()));
	}

	@FXML
	protected void onFilterClearAction(ActionEvent actionEvent)
	{
		fldSearch.setText("");
	}

	/**
	 * Copies every focused leaf in the available tree into the selected list,
	 * skipping campaigns that are already selected. If adding a campaign would
	 * break a prereq chain, we roll the add back and warn — same contract as
	 * the legacy AdvancedSourceSelectionPanel.AddAction.
	 */
	@FXML
	protected void onAddSelectedAction(ActionEvent event)
	{
		selectedLeavesIn(treeAvailable).forEach(c -> {
			if (selectedCampaigns.contains(c))
			{
				return;
			}
			selectedCampaigns.add(c);
			if (!FacadeFactory.passesPrereqs(selectedCampaigns))
			{
				selectedCampaigns.remove(c);
				warnBadCombo(c);
			}
		});
	}

	@FXML
	protected void onRemoveSelectedAction(ActionEvent event)
	{
		selectedLeavesIn(treeSelected).toList().forEach(selectedCampaigns::remove);
	}

	@FXML
	protected void onUnloadAllAction(ActionEvent event)
	{
		onUnloadAllRequested.run();
		selectedCampaigns.clear();
	}

	private void warnBadCombo(Campaign campaign)
	{
		var prereqDesc = FacadeFactory.getCampaignInfoFactory()
				.getRequirementsHTMLString(campaign, selectedCampaigns);
		var alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(LanguageBundle.getString("in_src_badComboTitle"));
		alert.setHeaderText(null);
		alert.setContentText(LanguageBundle.getFormattedString("in_src_badComboMsg", prereqDesc));
		alert.showAndWait();
	}

	private void showInfoFor(TreeItem<SourceTreeNode> item)
	{
		if (item == null)
		{
			infoPane.getEngine().loadContent("");
			return;
		}
		campaignOf(item).ifPresentOrElse(
				c -> infoPane.getEngine().loadContent(FacadeFactory.getCampaignInfoFactory().getHTMLInfo(c)),
				() -> infoPane.getEngine().loadContent(""));
	}

	/**
	 * Registers the action to invoke when the user double-clicks an available
	 * campaign — typically the same action as the dialog's Load button.
	 */
	public void setOnLoadRequested(Runnable handler)
	{
		this.onLoadRequested = handler == null ? () -> { } : handler;
	}

	/**
	 * Registers the action to invoke for Unload All — typically
	 * {@code PCGenFrame.unloadSources()}. The selected list is cleared
	 * unconditionally afterwards on this side.
	 */
	public void setOnUnloadAllRequested(Runnable handler)
	{
		this.onUnloadAllRequested = handler == null ? () -> { } : handler;
	}

	/**
	 * Builds the {@link SourceBundle} the user has chosen on this tab.
	 * Prefers the explicit Selected list when non-empty; falls back to the
	 * single focused leaf in the available tree so single-click + Load still
	 * works without an Add round-trip.
	 */
	public Optional<SourceBundle> getSelectedSource()
	{
		var gameMode = cmbGameMode.getSelectionModel().getSelectedItem();
		if (gameMode == null)
		{
			return Optional.empty();
		}
		if (!selectedCampaigns.isEmpty())
		{
			var snapshot = List.copyOf(selectedCampaigns);
			return Optional.of(new SourceBundle(null, gameMode, snapshot, false, null));
		}
		return selectedLeavesIn(treeAvailable).findFirst().map(c ->
				new SourceBundle(c.getDisplayName(), gameMode, List.of(c), false, null));
	}

	private static Stream<Campaign> selectedLeavesIn(TreeTableView<SourceTreeNode> tree)
	{
		return tree.getSelectionModel().getSelectedItems().stream()
				.filter(java.util.Objects::nonNull)
				.flatMap(item -> campaignOf(item).stream());
	}

	private static Optional<Campaign> campaignOf(TreeItem<SourceTreeNode> item)
	{
		return item.getValue() instanceof SourceTreeNode.Leaf l ? Optional.of(l.campaign()) : Optional.empty();
	}

	public void setGameModeSource(ObservableList<GameMode> gameModes)
	{
		cmbGameMode.setItems(gameModes);

		Optional<String> defaultGame = Optional.ofNullable(CONTEXT.getProperty(PROP_SELECTED_GAME, null));

		var selectedGame = defaultGame.flatMap(sgn ->
				gameModes.stream()
						.filter(g -> sgn.equals(g.getDisplayName()))
						.findFirst());
		selectedGame.ifPresent(s -> LOG.fine(() -> "Restored saved GameMode: " + s.getDisplayName()));

		selectedGame.ifPresentOrElse(g -> cmbGameMode.getSelectionModel().select(g),
				cmbGameMode.getSelectionModel()::selectFirst);

		cmbGameMode.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selectedGameMode) -> {
			LOG.fine(() -> "Selected GameMode: " + selectedGameMode.getDisplayName());
			var campaigns = StreamSupport
					.stream(FacadeFactory.getSupportedCampaigns(selectedGameMode).spliterator(), false)
					.toList();
			LOG.fine(() -> "Found " + campaigns.size() + " campaigns.");
			treeAvailable.setRoot(buildTree(campaigns));
			// Switching game mode invalidates the selected list — campaigns from
			// a different mode aren't loadable here.
			selectedCampaigns.clear();
		});
	}

	/**
	 * Builds an expanded Publisher → Setting → Campaign tree from the supplied
	 * campaigns. Campaigns without a CAMPAIGN_SETTING attach as direct
	 * publisher children; those with a setting are grouped under a setting node.
	 */
	private static TreeItem<SourceTreeNode> buildTree(List<Campaign> campaigns)
	{
		var fallbackPublisher = LanguageBundle.getString("in_other");

		Map<String, List<Campaign>> byPublisher = campaigns.stream()
				.collect(Collectors.groupingBy(c -> Optional.ofNullable(c.get(StringKey.DATA_PRODUCER))
						.orElse(fallbackPublisher), Collectors.toList()));

		var root = new TreeItem<SourceTreeNode>();
		byPublisher.entrySet().stream()
				.sorted(Map.Entry.comparingByKey(String.CASE_INSENSITIVE_ORDER))
				.forEach(pubEntry -> root.getChildren().add(buildPublisherNode(pubEntry.getKey(), pubEntry.getValue())));
		return root;
	}

	private static TreeItem<SourceTreeNode> buildPublisherNode(String publisher, List<Campaign> children)
	{
		var node = new TreeItem<SourceTreeNode>(new SourceTreeNode.Publisher(publisher));
		node.setExpanded(true);

		// Campaigns within a publisher group either have a setting (folder) or
		// not (direct child). Settings are alphabetised; direct campaigns sort
		// after their setting siblings, both groups by display name.
		Map<Optional<String>, List<Campaign>> bySetting = children.stream()
				.collect(Collectors.groupingBy(c -> Optional.ofNullable(c.get(StringKey.CAMPAIGN_SETTING)),
						Collectors.toList()));

		bySetting.entrySet().stream()
				.filter(e -> e.getKey().isPresent())
				.sorted(Comparator.comparing(e -> e.getKey().get(), String.CASE_INSENSITIVE_ORDER))
				.forEach(e -> node.getChildren().add(buildSettingNode(publisher, e.getKey().get(), e.getValue())));

		bySetting.getOrDefault(Optional.empty(), List.of()).stream()
				.sorted(Comparator.comparing(Campaign::getDisplayName, String.CASE_INSENSITIVE_ORDER))
				.forEach(c -> node.getChildren().add(new TreeItem<>(new SourceTreeNode.Leaf(c))));

		return node;
	}

	private static TreeItem<SourceTreeNode> buildSettingNode(String publisher, String setting, List<Campaign> children)
	{
		var node = new TreeItem<SourceTreeNode>(new SourceTreeNode.Setting(publisher, setting));
		node.setExpanded(true);
		children.stream()
				.sorted(Comparator.comparing(Campaign::getDisplayName, String.CASE_INSENSITIVE_ORDER))
				.forEach(c -> node.getChildren().add(new TreeItem<>(new SourceTreeNode.Leaf(c))));
		return node;
	}

	private static class GameModeCellFactory implements Callback<ListView<GameMode>, ListCell<GameMode>>
	{
		/**
		 * Creates a custom ListCell for displaying a GameMode object.
		 * Overrides the updateItem method to handle displaying the game mode's display name.
		 *
		 * @param param The ListView object that this ListCell is being used in.
		 * @return ListCell<GameMode> The custom ListCell object.
		 */
		@Override
		public ListCell<GameMode> call(ListView<GameMode> param)
		{
			return new ListCell<>()
			{
				@Override
				public void updateItem(GameMode gameMode, boolean empty)
				{
					super.updateItem(gameMode, empty);
					if (empty || gameMode == null)
					{
						setText(null);
						setGraphic(null);
					}
					else
					{
						setText(gameMode.getDisplayName());
					}
				}
			};
		}
	}
}
