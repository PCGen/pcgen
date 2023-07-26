package pcgen.gui3.sources;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import pcgen.core.Campaign;
import pcgen.facade.core.GameModeDisplayFacade;
import pcgen.facade.core.SourceSelectionFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.SortedListFacade;
import pcgen.system.FacadeFactory;
import pcgen.system.LanguageBundle;
import pcgen.util.Comparators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SourceSelectionDialogPane extends DialogPane {

    private final ListFacade<SourceSelectionFacade> sources;
    private final ListFacade<GameModeDisplayFacade> gameModes;

    private final FXMLLoader loader;

    @FXML
    private TabPane tabSources;

    @FXML
    private Tab basicTab;

    @FXML
    private BasicSourceSelectionController basicTabController;

    @FXML
    private AdvancedSourceSelectionController advancedTabController;

    @FXML
    private Tab advancedTab;

    @FXML
    private ButtonType btnSave;

    @FXML
    private ButtonType btnAlways;

    @FXML
    private ButtonType btnDelete;

    public SourceSelectionDialogPane(ListFacade<SourceSelectionFacade> sources, ListFacade<GameModeDisplayFacade> gameModes) {
        this.sources = sources;
        this.gameModes = gameModes;

        loader = new FXMLLoader(SourceSelectionDialogPane.class.getResource("SourceSelectionDialogPane.fxml"), LanguageBundle.getBundle());
        loader.setRoot(this);
        loader.setController(this);

        activeTabProperty().addListener((observable, oldValue, newValue) ->
        {
            System.out.println("New value is: " + newValue);
            if (newValue == ActiveTabEnum.BASIC) {
                this.getButtonTypes().remove(btnSave);
                this.getButtonTypes().remove(btnAlways);
                this.getButtonTypes().add(btnDelete);
            } else {
                this.getButtonTypes().add(btnSave);
                this.getButtonTypes().add(btnAlways);
                this.getButtonTypes().remove(btnDelete);
            }
        });

        try {
            loader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    protected void initialize() {
        System.out.println("Initialize Source Selection");
        tabSources.selectionModelProperty().addListener((observable, oldValue, newValue) ->
        {
            System.out.println("Selection property: " + newValue);

            /*
            if (newValue.getSelectedItem() == basicTab) {
                activeTab.set(ActiveTabEnum.BASIC);
            } else {
                activeTab.set(ActiveTabEnum.ADVANCED);
            }
            */
        });

       var campaigns = StreamSupport
               .stream(this.sources.spliterator(), false)
               .map(s -> s.getCampaigns().getElementAt(0))
               .toList();

       var gameModes = StreamSupport
               .stream(this.gameModes.spliterator(), false)
               .map(GameModeDisplayFacade::getGameMode)
               .toList();

       basicTabController.setCampaignSource(FXCollections.observableList(campaigns));
       advancedTabController.setGameModeSource(FXCollections.observableList(gameModes));
    }

    @FXML
    protected void onSelectionTabChanged(Event event) {
        var target = (Tab) event.getTarget();
        if (target.isSelected()) {
            if (target == basicTab) {
                activeTabProperty().set(ActiveTabEnum.BASIC);
            } else {
                activeTabProperty().set(ActiveTabEnum.ADVANCED);
            }
        }
    }

    @Override
    protected Node createButtonBar() {
        ButtonBar buttonBar = (ButtonBar) super.createButtonBar();

        return buttonBar;
    }

    private void updateButtonBar(ButtonBar buttonBar) {

    }

    @Override
    protected Node createButton(ButtonType buttonType) {
        Node button;

        if (buttonType.getButtonData() == ButtonBar.ButtonData.SMALL_GAP) {
            var checkBox = new CheckBox();
            checkBox.setText(buttonType.getText());
            button = checkBox;
        } else {
            button = super.createButton(buttonType);
        }
        ButtonBar.setButtonUniformSize(button, false);

        return button;
    }

    private ObjectProperty<ActiveTabEnum> activeTab;

    public final ActiveTabEnum getActiveTab() {
        return activeTab == null ? ActiveTabEnum.BASIC : activeTab.get();
    }

    public final ObjectProperty<ActiveTabEnum> activeTabProperty() {
        if (activeTab == null) {
            activeTab = new SimpleObjectProperty<ActiveTabEnum>(this, "activeTab", ActiveTabEnum.INITIAL) {
                @Override
                public void set(ActiveTabEnum newValue) {
                    var model = tabSources.getSelectionModel();
                    var tab = switch (newValue) {
                        case INITIAL, BASIC -> basicTab;
                        case ADVANCED -> advancedTab;
                    };

                    super.set(newValue);
                    model.select(tab);
                }
            };
        }
        return activeTab;
    }

    enum ActiveTabEnum {
        INITIAL, BASIC, ADVANCED
    }
}
