package pcgen.gui3.sources;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import pcgen.core.GameMode;
import pcgen.gui2.UIPropertyContext;
import pcgen.system.FacadeFactory;

import java.util.Optional;

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
    protected void initialize() {
        System.out.println("Initialize AdvancedSourceSelectionController");
        btnFilterClear.setGraphic(new ImageView(pcgen.gui2.tools.Icons.CloseX9.asJavaFX()));
        cmbGameMode.setCellFactory(new AdvancedSourceSelectionController.GameModeCellFactory());

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
                () -> cmbGameMode.getSelectionModel().selectFirst());

        cmbGameMode.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selectedGameMode) -> {
            System.out.println("Selected: " + selectedGameMode.getDisplayName());
            var campaigns = FacadeFactory.getSupportedCampaigns(selectedGameMode);
        });
    }

    private static class GameModeCellFactory implements Callback<ListView<GameMode>, ListCell<GameMode>> {
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
}
