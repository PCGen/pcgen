package pcgen.gui3.sources;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import pcgen.core.Campaign;
import pcgen.system.FacadeFactory;

public class BasicSourceSelectionController {
    @FXML
    private SplitPane splitPane;

    @FXML
    private ListView<Campaign> sourceList;

    @FXML
    private WebView infoPane;

    @FXML
    protected void initialize() {
        System.out.println("Initialize BasicSourceSelectionController");
        sourceList.setCellFactory(new CampaignCellFactory());
        sourceList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Selected: " + newValue.getKeyName());
            var infoText = FacadeFactory.getCampaignInfoFactory().getHTMLInfo(newValue);
            infoPane.getEngine().loadContent(infoText);
        });
    }

    public void setCampaignSource(ObservableList<Campaign> items) {
        sourceList.setItems(items);
        sourceList.getSelectionModel().selectFirst();
    }

    private static class CampaignCellFactory implements Callback<ListView<Campaign>, ListCell<Campaign>> {

        @Override
        public ListCell<Campaign> call(ListView<Campaign> param) {
            return new ListCell<>() {
                @Override
                public void updateItem(Campaign campaign, boolean empty) {
                    super.updateItem(campaign, empty);
                    if (empty || campaign == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(campaign.getKeyName());
                    }
                }
            };
        }
    }
}
