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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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
	private TreeTableColumn<SourceTreeNode, String> colAvailableName;

	@FXML
	private TreeTableColumn<SourceTreeNode, String> colAvailableBookType;

	@FXML
	private TreeTableColumn<SourceTreeNode, String> colAvailableStatus;

	@FXML
	private TreeTableColumn<SourceTreeNode, String> colAvailableLoaded;

	@FXML
	private TreeTableView<SourceTreeNode> treeSelected;

	@FXML
	private TreeTableColumn<SourceTreeNode, String> colSelectedName;

	@FXML
	private WebView infoPane;

	/**
	 * The campaigns chosen on this tab. Backed by {@link #model} once
	 * {@link #setModel} runs; an empty standalone list until then, so the
	 * controller is usable in isolation (e.g. FXML preview).
	 */
	private ObservableList<Campaign> selectedCampaigns = FXCollections.observableArrayList();

	private SourceSelectionModel model;

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

		treeAvailable.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2
					&& selectedLeavesIn(treeAvailable).findAny().isPresent())
			{
				onLoadRequested.run();
			}
		});

		// Enter in the search field is swallowed so it does not bubble to the
		// dialog's default Load button and close the dialog mid-search; filtering
		// is live via the searchQuery binding (see setModel).
		fldSearch.setOnAction(ActionEvent::consume);
	}

	private void bindAvailableColumns()
	{
		colAvailableName.setCellValueFactory(v ->
				new ReadOnlyObjectWrapper<>(v.getValue().getValue().displayLabel()));
		colAvailableBookType.setCellValueFactory(v ->
				new ReadOnlyObjectWrapper<>(campaignOf(v.getValue())
						.map(c -> c.getListAsString(ListKey.BOOK_TYPE)).orElse("")));
		colAvailableStatus.setCellValueFactory(v ->
				new ReadOnlyObjectWrapper<>(campaignOf(v.getValue())
						.map(c -> c.getSafe(ObjectKey.STATUS).toString()).orElse("")));
		colAvailableLoaded.setCellValueFactory(v ->
				new ReadOnlyObjectWrapper<>(campaignOf(v.getValue()).isPresent() ? "Loaded" : "Not loaded"));
	}

	private void bindSelectedColumns()
	{
		colSelectedName.setCellValueFactory(v ->
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
		// Validate against a working copy so the observable list — and the tree
		// bound to it — is mutated once at the end, not per candidate.
		var working = new ArrayList<>(selectedCampaigns);
		var accepted = new ArrayList<Campaign>();
		selectedLeavesIn(treeAvailable).forEach(c -> {
			if (working.contains(c))
			{
				return;
			}
			working.add(c);
			if (FacadeFactory.passesPrereqs(working))
			{
				accepted.add(c);
			}
			else
			{
				working.remove(c);
				warnBadCombo(c, working);
			}
		});
		selectedCampaigns.addAll(accepted);
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

	private void warnBadCombo(Campaign campaign, List<Campaign> context)
	{
		var prereqDesc = FacadeFactory.getCampaignInfoFactory()
				.getRequirementsHTMLString(campaign, context);
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
			SourceInfoRenderer.clear(infoPane.getEngine());
			return;
		}
		campaignOf(item).ifPresentOrElse(
				c -> SourceInfoRenderer.renderCampaign(infoPane.getEngine(), c),
				() -> SourceInfoRenderer.clear(infoPane.getEngine()));
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
		var gameMode = cmbGameMode.getValue();
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

	/**
	 * Persists the committed choice as the remembered defaults: the game mode and,
	 * keyed by that mode, the campaign names actually loaded. Call only when the
	 * user commits the dialog with Load/OK on the Advanced tab, passing the bundle
	 * being loaded (not {@code null}) — browsing then cancelling, or loading from
	 * the Basic tab, must not overwrite the Advanced memory.
	 *
	 * <p>Keys/values use {@link GameMode#getName()} and {@link Campaign#toString()}
	 * for interoperability with the legacy Swing dialog.
	 */
	public void commitSelection(SourceBundle loaded)
	{
		GameMode mode = loaded.gameMode();
		if (mode == null)
		{
			return;
		}
		CONTEXT.setProperty(PROP_SELECTED_GAME, mode.getName());
		String names = loaded.campaigns().stream()
				.map(Campaign::toString)
				.collect(Collectors.joining("|")); //$NON-NLS-1$
		CONTEXT.setProperty(PROP_SELECTED_SOURCES + mode.getName(), names);
	}

	private static Stream<Campaign> selectedLeavesIn(TreeTableView<SourceTreeNode> tree)
	{
		return tree.getSelectionModel().getSelectedItems().stream()
				.filter(Objects::nonNull)
				.flatMap(item -> campaignOf(item).stream());
	}

	private static Optional<Campaign> campaignOf(TreeItem<SourceTreeNode> item)
	{
		return item.getValue() instanceof SourceTreeNode.Leaf(Campaign campaign) ? Optional.of(campaign) : Optional.empty();
	}

	/**
	 * Binds this tab to its shared {@link SourceSelectionModel}: the Selected
	 * tree now reflects the model's campaign list, and the available tree
	 * follows the model's game mode. The Basic tab projects onto the same model,
	 * so a Basic selection lands here through this binding.
	 */
	public void setModel(SourceSelectionModel sourceModel)
	{
		this.model = Objects.requireNonNull(sourceModel);
		this.selectedCampaigns = sourceModel.getSelectedCampaigns();

		// Selected list changes drive the right-side tree rebuild.
		selectedCampaigns.addListener((ListChangeListener<Campaign>) _ ->
				treeSelected.setRoot(buildTree(selectedCampaigns)));
		treeSelected.setRoot(buildTree(selectedCampaigns));

		// The available tree mirrors the model's maintained availableCampaigns
		// list (recomputed by the model on game-mode or search-query change).
		var available = model.getAvailableCampaigns();
		available.addListener((ListChangeListener<Campaign>) _ ->
				treeAvailable.setRoot(buildTree(available)));

		// Seed the model's game mode (unless already set, e.g. by a Basic
		// projection) before binding, so there is a single value to react to.
		if (model.getGameMode() == null)
		{
			model.setGameMode(resolveDefaultGameMode());
		}

		// The combo's value and the model's game mode are one store; likewise the
		// search field and the model's query. Write only through the model.
		cmbGameMode.valueProperty().bindBidirectional(model.gameModeProperty());
		fldSearch.textProperty().bindBidirectional(model.searchQueryProperty());

		// Swap the selected campaigns to the new mode's remembered set when the
		// user switches mode (persistence of the choice happens only on Load/OK).
		model.gameModeProperty().addListener((_, _, mode) -> onGameModeChanged(mode));

		// Reflect the initial mode into the tree once.
		treeAvailable.setRoot(buildTree(model.getAvailableCampaigns()));
	}

	/**
	 * Reacts to a user mode switch: replace the selected campaigns (which belong
	 * to the old mode) with the new mode's remembered set. The Basic projection
	 * sets the mode then replaces campaigns, so its {@code setAll} runs after this
	 * and wins (see {@link SourceSelectionDialogPane#project}).
	 */
	private void onGameModeChanged(GameMode mode)
	{
		selectedCampaigns.setAll(rememberedCampaignsFor(mode));
	}

	/**
	 * The campaigns to pre-select for {@code mode}: the set remembered under
	 * {@link #PROP_SELECTED_SOURCES} for this mode, or the mode's default data set
	 * when nothing is remembered. Names that no longer resolve to a supported
	 * campaign are skipped with a warning — a stale config must never be fatal.
	 */
	private List<Campaign> rememberedCampaignsFor(GameMode mode)
	{
		if (mode == null)
		{
			return List.of();
		}
		String remembered = CONTEXT.getProperty(PROP_SELECTED_SOURCES + mode.getName(), null);
		List<String> names = (remembered == null || remembered.isBlank())
				? mode.getDefaultDataSetList()
				: List.of(remembered.split("\\|")); //$NON-NLS-1$
		List<Campaign> available = StreamSupport
				.stream(FacadeFactory.getSupportedCampaigns(mode).spliterator(), false)
				.toList();
		return resolveCampaigns(names, available);
	}

	/**
	 * Resolves campaign {@code names} against the {@code available} campaigns,
	 * matching on {@link Campaign#toString()} (the key the legacy Swing dialog
	 * reads and writes). A name that no longer resolves is skipped with a warning
	 * rather than failing — a stale config must never be fatal. Package-private
	 * for unit testing.
	 */
	static List<Campaign> resolveCampaigns(List<String> names, List<Campaign> available)
	{
		Map<String, Campaign> byName = new HashMap<>();
		available.forEach(c -> byName.put(c.toString(), c));

		List<Campaign> resolved = new ArrayList<>(names.size());
		for (String name : names)
		{
			Campaign campaign = byName.get(name);
			if (campaign == null)
			{
				LOG.warning(() -> "Remembered source '" + name
						+ "' is no longer available; skipping.");
			}
			else
			{
				resolved.add(campaign);
			}
		}
		return resolved;
	}

	/**
	 * Supplies the combo's game-mode choices. The combo's value is bound to the
	 * model in {@link #setModel}; selection changes flow through that binding, so
	 * there is no listener to install here.
	 */
	public void setGameModeSource(ObservableList<GameMode> gameModes)
	{
		cmbGameMode.setItems(gameModes);
	}

	/**
	 * Resolves the last-used game mode saved under {@link #PROP_SELECTED_GAME}
	 * against the combo's current items, falling back to the first item. Requires
	 * {@link #setGameModeSource} to have populated the items first.
	 */
	private GameMode resolveDefaultGameMode()
	{
		Optional<String> savedName = Optional.ofNullable(CONTEXT.getProperty(PROP_SELECTED_GAME, null));
		return savedName
				.flatMap(name -> cmbGameMode.getItems().stream()
						.filter(g -> name.equals(g.getName()))
						.findFirst())
				.map(saved -> {
					LOG.fine(() -> "Restored saved GameMode: " + saved.getDisplayName());
					return saved;
				})
				.orElseGet(() -> cmbGameMode.getItems().isEmpty() ? null : cmbGameMode.getItems().getFirst());
	}

	/**
	 * Builds an expanded Publisher → Setting → Campaign tree from the supplied
	 * campaigns. Campaigns without a CAMPAIGN_SETTING attach as direct
	 * publisher children; those with a setting are grouped under a setting node.
	 * Package-private for unit testing.
	 */
	static TreeItem<SourceTreeNode> buildTree(List<Campaign> campaigns)
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
