/*
 * Copyright James Dempsey, 2013
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.gui2.dialog;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.net.URL;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import pcgen.facade.core.ChooserFacade;
import pcgen.facade.core.InfoFacade;
import pcgen.facade.util.ListFacade;
import pcgen.gui3.component.OKCloseButtonBar;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * The Class {@code RadioChooserDialog} provides a dialog with a variable
 * number of radio buttons to allow the user to make a single choice from a
 * list. A ChooserFacade instance must be supplied, this defines the choices
 * available, the text to be displayed on screen and the actions to be taken
 * when the user confirms their choices. The chooser is generally displayed
 * via a call to UIDelgate.showGeneralChooser.
 */
public class RadioChooserDialog extends JDialog
{

    private final ChooserFacade chooser;
    private boolean committed;
    private RadioButton[] avaRadioButton;
    private ToggleGroup toggleGroup;

    /**
     * Create a new instance of RadioChooserDialog for selecting from the data
     * supplied in the chooserFacade.
     *
     * @param frame   The window we are opening relative to.
     * @param chooser The definition of what should be displayed.
     */
    public RadioChooserDialog(Frame frame, ChooserFacade chooser)
    {
        super(frame, true);
        this.chooser = chooser;
        committed = false;

        initComponents();
    }

    private void initComponents()
    {
        Pane outerPane = new VBox();
        JFXPanel jfxPanel = new JFXPanel();
        jfxPanel.setLayout(new BorderLayout());

        setTitle(LanguageBundle.getString("in_chooserSelectOne")); //$NON-NLS-1$
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        Node titleLabel = new Text(chooser.getName());
        titleLabel.getStyleClass().add("chooserTitle");
        URL applicationCss = getClass().getResource("/pcgen/gui3/application.css");
        String asString = applicationCss.toExternalForm();
        outerPane.getStylesheets().add(asString);
        outerPane.getChildren().add(titleLabel);
        toggleGroup = new ToggleGroup();

        outerPane.getChildren().add(buildButtonPanel());

        this.getContentPane().setLayout(new GridLayout());
        this.getContentPane().add(jfxPanel, BorderLayout.CENTER);


        ButtonBar buttonBar = new OKCloseButtonBar(
                this::onOK,
                this::onCancel);
        outerPane.getChildren().add(buttonBar);
        Platform.runLater(() -> {
            Scene scene = new Scene(outerPane);
            jfxPanel.setScene(scene);
            SwingUtilities.invokeLater(this::pack);
        });
    }

    /**
     * Create the panel of radio buttons.
     *
     * @return pane with radio buttons
     */
    private Pane buildButtonPanel()
    {
        ListFacade<InfoFacade> availableList = chooser.getAvailableList();
        int count = 0;
        avaRadioButton = new RadioButton[availableList.getSize()];

        // Create the buttons
        for (InfoFacade infoFacade : availableList)
        {
            avaRadioButton[count] = new RadioButton(infoFacade.toString());
            avaRadioButton[count].setToggleGroup(toggleGroup);
            avaRadioButton[count].setUserData(count);
            ++count;
        }

        if (count > 0)
        {
            avaRadioButton[0].setSelected(true);
        }

        if (count > 10)
        {
            return buildTwoColLayout();
        }
        return buildNormalColLayout();
    }

    /**
     * Build up two columns of buttons arranged in column order e.g. 1-6 then 7-12
     *
     * @return pane with radio buttons
     */
    private Pane buildTwoColLayout()
    {
        GridPane boxPane = new GridPane();
        int numButtons = avaRadioButton.length;
        int numRows = numButtons - (numButtons / 2);
        for (int row = 0;row < numRows;++row)
        {
            boxPane.add(avaRadioButton[row], 0, row);
            if ((row + numRows) < numButtons)
            {
                boxPane.add(avaRadioButton[row + numRows], 1, row);
            }
        }
        return boxPane;
    }

    /**
     * Build up a single columns of buttons
     *
     * @return pane with radio buttons
     */
    private Pane buildNormalColLayout()
    {
        GridPane boxPane = new GridPane();
        int numButtons = avaRadioButton.length;
        for (int row = 0;row < numButtons;++row)
        {
            boxPane.add(avaRadioButton[row], 0, row);
        }
        return boxPane;
    }


    private void onOK(final ActionEvent ignored)
    {
        Toggle selectedToggle = toggleGroup.getSelectedToggle();
        Logging.debugPrint("selected toggle is " + selectedToggle);
        if (selectedToggle != null)
        {
            Integer whichItemId = (Integer) selectedToggle.getUserData();
            InfoFacade selectedItem = chooser.getAvailableList().getElementAt(whichItemId);
            chooser.addSelected(selectedItem);
        }
        if (chooser.isRequireCompleteSelection() && (chooser.getRemainingSelections().get() > 0))
        {
            Dialog<ButtonType> alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(chooser.getName());
            alert.setContentText(LanguageBundle.getFormattedString("in_chooserRequireComplete",
                    chooser.getRemainingSelections().get()));
            alert.showAndWait();
            return;
        }
        chooser.commit();
        committed = true;
        this.dispose();
    }

    private void onCancel(final ActionEvent ignored)
    {
        committed = false;
        chooser.rollback();
        this.dispose();
    }

    /**
     * Returns the means by which the dialog was closed.
     *
     * @return the committed status, false for cancelled, true for OKed.
     */
    public boolean isCommitted()
    {
        return committed;
    }

}
