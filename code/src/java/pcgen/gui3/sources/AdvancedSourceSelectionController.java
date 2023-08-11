package pcgen.gui3.sources;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Campaign;
import pcgen.core.GameMode;
import pcgen.facade.util.ListFacade;
import pcgen.gui2.UIPropertyContext;
import pcgen.system.FacadeFactory;
import pcgen.system.LanguageBundle;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AdvancedSourceSelectionController {
    private static final UIPropertyContext CONTEXT =
            UIPropertyContext.createContext("advancedSourceSelectionPanel"); //$NON-NLS-1$
    private static final String PROP_SELECTED_GAME = "selectedGame"; //$NON-NLS-1$
    private static final String PROP_SELECTED_SOURCES = "selectedSources."; //$NON-NLS-1$

    @FXML
    private Button btnFilterClear;

    @FXML
    private TextField fldSearch;

    @FXML
    private ComboBox<GameMode> cmbGameMode;

    @FXML
    private TreeTableView<Campaign> treeAvailable;

    @FXML
    protected void initialize() {
        System.out.println("Initialize AdvancedSourceSelectionController");
        btnFilterClear.setGraphic(new ImageView(pcgen.gui2.tools.Icons.CloseX9.asJavaFX()));
        cmbGameMode.setCellFactory(new AdvancedSourceSelectionController.GameModeCellFactory());
        treeAvailable.getColumns().get(0).setCellValueFactory(v -> {
            var treeItem = (RecursiveTreeItem) v.getValue();
            var value = treeItem.getCampaign().map(CDOMObject::getDisplayName).orElse(treeItem.getSetting().orElse(treeItem.getPublisher()));
            return new ReadOnlyObjectWrapper(value);
        });
        treeAvailable.getColumns().get(1).setCellValueFactory(v -> {
            var treeItem = (RecursiveTreeItem) v.getValue();
            return new ReadOnlyObjectWrapper(treeItem.getCampaign().map(c -> c.getListAsString(ListKey.BOOK_TYPE)).orElse(""));
        });
        treeAvailable.getColumns().get(2).setCellValueFactory(v -> {
            var treeItem = (RecursiveTreeItem) v.getValue();
            return new ReadOnlyObjectWrapper(treeItem.getCampaign().map(c -> c.getSafe(ObjectKey.STATUS).toString()).orElse(""));
        });
        treeAvailable.getColumns().get(3).setCellValueFactory(v -> {
            var treeItem = (RecursiveTreeItem) v.getValue();
            return new ReadOnlyObjectWrapper(treeItem.getCampaign().map(c -> "Loaded").orElse("Not loaded"));
        });
    }


    @FXML
    protected void onFilterClearAction(ActionEvent actionEvent) {
        fldSearch.setText("");
    }

    public void setGameModeSource(ObservableList<GameMode> gameModes) {
        cmbGameMode.setItems(gameModes);

        Optional<String> defaultGame = Optional.ofNullable(CONTEXT.getProperty(PROP_SELECTED_GAME, null));

        var selectedGame = defaultGame.flatMap(sgn ->
                gameModes.stream()
                        .filter(g -> sgn.equals(g.getDisplayName()))
                        .findFirst());
        selectedGame.ifPresent(s -> System.out.println("Found the saved GameMode: " + s));

        selectedGame.ifPresentOrElse(g -> cmbGameMode.getSelectionModel().select(g),
                cmbGameMode.getSelectionModel()::selectFirst);

        cmbGameMode.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selectedGameMode) -> {
            System.out.println("Selected: " + selectedGameMode.getDisplayName());
            var campaigns = FacadeFactory.getSupportedCampaigns(selectedGameMode);
            System.out.println("Found: " + campaigns.getSize() + " campaigns.");

            var campaignsHierarchy = StreamSupport
                    .stream(campaigns.spliterator(), false)
                    .collect(Collectors.groupingBy(c -> Optional
                                    .ofNullable(c.get(StringKey.DATA_PRODUCER))
                                    .orElse(LanguageBundle.getString("in_other")),
                            Collectors.groupingBy(c -> Optional.ofNullable(c.get(StringKey.CAMPAIGN_SETTING)))));

            var treeItem = new RecursiveTreeItem(campaigns, null, null, null);
            treeItem.setExpanded(true);

            treeAvailable.setRoot(treeItem);
        });
    }

    private static class GameModeCellFactory implements Callback<ListView<GameMode>, ListCell<GameMode>> {
        /**
         * Creates a custom ListCell for displaying a GameMode object.
         * Overrides the updateItem method to handle displaying the game mode's display name.
         *
         * @param param The ListView object that this ListCell is being used in.
         * @return ListCell<GameMode> The custom ListCell object.
         */
        @Override
        public ListCell<GameMode> call(ListView<GameMode> param) {
            return new ListCell<>() {
                @Override
                public void updateItem(GameMode gameMode, boolean empty) {
                    super.updateItem(gameMode, empty);
                    if (empty || gameMode == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(gameMode.getDisplayName());
                    }
                }
            };
        }
    }

    class RecursiveTreeItem extends TreeItem<Campaign> {
        private ListFacade<Campaign> campaigns;

        public String getPublisher() {
            return publisher;
        }

        public Optional<String> getSetting() {
            return setting;
        }

        public Optional<Campaign> getCampaign() {
            return campaign;
        }

        private String publisher;
        private Optional<String> setting;
        private Optional<Campaign> campaign;


        public RecursiveTreeItem(ListFacade<Campaign> campaigns, String publisher, String setting, Campaign campaign) {
            super();

            this.campaigns = campaigns;
            this.publisher = publisher;
            this.setting = Optional.ofNullable(setting);
            this.campaign = Optional.ofNullable(campaign);

            if (publisher == null) {           // the first level is "publishers"
                StreamSupport
                        .stream(campaigns.spliterator(), false)
                        .map(c -> Optional
                                .ofNullable(c.get(StringKey.DATA_PRODUCER))
                                .orElse(LanguageBundle.getString("in_other")))
                        .distinct()
                        .sorted()
                        .forEach(c -> {
                            var item = new RecursiveTreeItem(this.campaigns, c, null, null);
                            item.setExpanded(true);
                            this.getChildren().add(item);
                        });
            } else if (this.setting.isEmpty() && this.campaign.isEmpty()) { // the second level
                StreamSupport
                        .stream(campaigns.spliterator(), false)
                        .filter(c -> publisher.equals(Optional
                                .ofNullable(c.get(StringKey.DATA_PRODUCER))
                                .orElse(LanguageBundle.getString("in_other"))))
                        .sorted((c1, c2) -> {
                            var cs1 = (c1.get(StringKey.CAMPAIGN_SETTING) == null ? c1.getDisplayName() : c1.get(StringKey.CAMPAIGN_SETTING));
                            var cs2 = (c2.get(StringKey.CAMPAIGN_SETTING) == null ? c2.getDisplayName() : c2.get(StringKey.CAMPAIGN_SETTING));
                            return c1.compareTo(c2);
                        }).forEach(c -> {
                            var s = Optional.ofNullable(c.get(StringKey.CAMPAIGN_SETTING));
                            if (s.isPresent() && this.getChildren().stream().noneMatch(st -> s.equals(((RecursiveTreeItem) st).getSetting()))) {
                                this.getChildren().add(new RecursiveTreeItem(this.campaigns, this.publisher, s.get(), null));
                            } else {
                                this.getChildren().add(new RecursiveTreeItem(this.campaigns, this.publisher, null, c));
                            }
                        });
            } else if (this.campaign.isEmpty()) { // the third one

                StreamSupport
                        .stream(campaigns.spliterator(), false)
                        .filter(c -> publisher.equals(Optional
                                .ofNullable(c.get(StringKey.DATA_PRODUCER))
                                .orElse(LanguageBundle.getString("in_other"))
                        ) && this.setting.equals(Optional.ofNullable(c.get(StringKey.CAMPAIGN_SETTING))))
                        .forEach(c -> {
                            this.getChildren().add(new RecursiveTreeItem(this.campaigns, this.publisher, c.get(StringKey.CAMPAIGN_SETTING), c));
                        });
            }
        }
    }
}
