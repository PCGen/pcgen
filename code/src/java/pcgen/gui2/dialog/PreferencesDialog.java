/*
 *
 * Copyright 2001 (C) B. K. Oxley (binkley) <binkley@alumni.rice.edu>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.gui2.dialog;

import java.awt.Dimension;
import java.util.Objects;
import java.util.function.Consumer;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import pcgen.cdom.base.Constants;
import pcgen.core.SettingsHandler;
import pcgen.gui2.prefs.PCGenPrefsPanel;
import pcgen.gui3.GuiAssertions;
import pcgen.gui3.GuiUtility;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * PCGen preferences dialog
 */
public final class PreferencesDialog extends AbstractDialog
{
    private static final String LB_TITLE = "in_Prefs_title"; //$NON-NLS-1$

    private TreeView<PCGenPrefsPanel> settingsTree;
    private final TreeItem<PCGenPrefsPanel> root;
    private JSplitPane splitPane;

    public PreferencesDialog(JFrame parent, TreeItem<PCGenPrefsPanel> model, String applicationName)
    {
        super(parent, LanguageBundle.getFormattedString(LB_TITLE, applicationName), true);
        this.root = Objects.requireNonNull(model);
        initCenter();

        applyOptionValuesToControls();
        pack();
        this.setLocationRelativeTo(getParent());
    }

    private void setOptionsBasedOnControls()
    {
        forEachLeaf(root, PCGenPrefsPanel::setOptionsBasedOnControls);

        if (SettingsHandler.settingsNeedRestartProperty().get())
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(Constants.APPLICATION_NAME);
            alert.setContentText(LanguageBundle.getString("in_Prefs_restartRequired"));
            alert.showAndWait();
        }
    }

    private void applyOptionValuesToControls()
    {
        forEachLeaf(root, PCGenPrefsPanel::applyOptionValuesToControls);
    }

    private void initCenter()
    {
        GuiAssertions.assertIsNotJavaFXThread();

        Platform.runLater(() -> {

            settingsTree.setRoot(root);

            settingsTree.showRootProperty().set(false);
            settingsTree.selectionModelProperty().get().setSelectionMode(SelectionMode.SINGLE);

            settingsTree.getRoot().getChildren().forEach(child -> child.setExpanded(true));

            settingsTree.getRoot().setExpanded(true);
            // Add the listener which switches panels when a node of the tree is selected
            settingsTree.selectionModelProperty().get().selectedItemProperty().addListener((observable, oldValue,
                    newValue) -> {
                // this actually gets called by both swing and JavaFX threads.
                // It appears to be fine to 'invokelater' regardless of the thread
                // but this is certainly weird
                // assert we're on a GUI thread mostly to be clear about what we're expecting
                GuiAssertions.assertIsOnGUIThread();
                if (newValue == null)
                {
                    return;
                }

                SwingUtilities.invokeLater(() -> {
                    Logging.debugPrint("new preference tree value is " + newValue);
                    PCGenPrefsPanel value = newValue.getValue();
                    JScrollPane scrollableSettings = new JScrollPane(value);
                    splitPane.setRightComponent(scrollableSettings);
                });
            });
            settingsTree.getSelectionModel().select(1);

        });
    }

    @Override
    protected JComponent getCenter()
    {
        // Build the settings panel
        JPanel emptyPanel = new JPanel();
        emptyPanel.setPreferredSize(new Dimension(780, 420));

        settingsTree = new TreeView<>();
        settingsTree.setRoot(new TreeItem<>(null));

        // Build the split pane
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, GuiUtility.wrapParentAsJFXPanel(settingsTree),
                emptyPanel
        );
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerSize(10);

        return splitPane;
    }


    private static <T> void forEachLeaf(TreeItem<? extends T> parent, Consumer<T> func)
    {
        if (parent.isLeaf())
        {
            func.accept(parent.getValue());
        } else
        {
            parent.getChildren().forEach(child -> forEachLeaf(child, func));
        }
    }

    @Override
    public void applyButtonActionPerformed()
    {
        setOptionsBasedOnControls();
    }

    @Override
    protected boolean includeApplyButton()
    {
        return true;
    }

}
