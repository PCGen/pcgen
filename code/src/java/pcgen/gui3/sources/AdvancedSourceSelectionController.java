package pcgen.gui3.sources;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
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
	private TextField fldSearch;

	@FXML
	private ComboBox<GameMode> cmbGameMode;

	@FXML
	private TreeTableView<SourceTreeNode> treeAvailable;

	@FXML
	private TreeTableView<SourceTreeNode> treeSelected;

	private Runnable onLoadRequested = () -> { };

	@FXML
	protected void initialize()
	{
		LOG.fine("Initialize AdvancedSourceSelectionController");
		btnFilterClear.setGraphic(new ImageView(pcgen.gui2.tools.Icons.CloseX9.asJavaFX()));
		cmbGameMode.setCellFactory(new GameModeCellFactory());

		treeAvailable.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

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

		treeAvailable.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2
					&& selectedCampaignFromAvailable().isPresent())
			{
				onLoadRequested.run();
			}
		});
	}


	@FXML
	protected void onFilterClearAction(ActionEvent actionEvent)
	{
		fldSearch.setText("");
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
	 * Builds the {@link SourceBundle} the user has chosen on this tab, if any.
	 * Until multi-select is wired, this is the single campaign focused in the
	 * available tree under the current game mode.
	 */
	public Optional<SourceBundle> getSelectedSource()
	{
		var gameMode = cmbGameMode.getSelectionModel().getSelectedItem();
		if (gameMode == null)
		{
			return Optional.empty();
		}
		return selectedCampaignFromAvailable().map(c ->
				new SourceBundle(c.getDisplayName(), gameMode, List.of(c), false, null));
	}

	private Optional<Campaign> selectedCampaignFromAvailable()
	{
		var item = treeAvailable.getSelectionModel().getSelectedItem();
		return item == null ? Optional.empty() : campaignOf(item);
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
			treeAvailable.setRoot(buildAvailableTree(campaigns));
		});
	}

	/**
	 * Builds an expanded Publisher → Setting → Campaign tree from the supplied
	 * campaigns. Campaigns without a CAMPAIGN_SETTING attach as direct
	 * publisher children; those with a setting are grouped under a setting node.
	 */
	private static TreeItem<SourceTreeNode> buildAvailableTree(List<Campaign> campaigns)
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
