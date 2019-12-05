/*
 * Copyright 2008 (C) James Dempsey
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
package pcgen.gui2.prefs;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.List;

import pcgen.cdom.base.Constants;
import pcgen.cdom.content.RollMethod;
import pcgen.core.GameMode;
import pcgen.core.PointBuyMethod;
import pcgen.core.SettingsHandler;
import pcgen.gui3.GuiUtility;
import pcgen.system.LanguageBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

/**
 * The Class {@code CharacterStatsPanel} is responsible for managing
 * the character stats preferences.
 */
public final class CharacterStatsPanel extends PCGenPrefsPanel
{

    private static final String IN_ABILITIES = LanguageBundle.getString("in_Prefs_abilities");
    private String[] pMode;
    private String[] pModeMethodName;


    private final RadioButton abilitiesAllSameButton;
    private final RadioButton abilitiesPurchasedButton;
    private RadioButton abilitiesRolledButton;
    private final RadioButton abilitiesUserRolledButton;
    private final ComboBox<String> abilityPurchaseModeCombo;
    private ComboBox<String> abilityRolledModeCombo;
    private final ComboBox<String> abilityScoreCombo;
    private PurchaseModeFrame pmsFrame;

    private EventHandler<ActionEvent> rolledModeListener;
    private EventHandler<ActionEvent> purchaseModeListener;
    private EventHandler<ActionEvent> scoreListener;

    /**
     * Instantiates a new character stats panel.
     */
    public CharacterStatsPanel()
    {
        VBox vBox = new VBox();
        Label label;

        final GameMode gameMode = SettingsHandler.getGameAsProperty().get();

        ToggleGroup exclusiveGroup1 = new ToggleGroup();
        label = new Label(LanguageBundle.getFormattedString(
                "in_Prefs_abilitiesGenLabel", gameMode.getDisplayName())); //$NON-NLS-1$

        vBox.getChildren().add(label);

        abilitiesUserRolledButton = new RadioButton(LanguageBundle.getString("in_Prefs_abilitiesUserRolled"));
        vBox.getChildren().add(abilitiesUserRolledButton);
        abilitiesUserRolledButton.setToggleGroup(exclusiveGroup1);

        abilitiesAllSameButton = new RadioButton(LanguageBundle.getString("in_Prefs_abilitiesAllSame") + ": ");
        vBox.getChildren().add(abilitiesAllSameButton);
        abilitiesAllSameButton.setToggleGroup(exclusiveGroup1);


        abilityScoreCombo = new ComboBox<>();

        for (int i = gameMode.getStatMin();i <= gameMode.getStatMax();++i)
        {
            abilityScoreCombo.getItems().add(String.valueOf(i));
        }

        vBox.getChildren().add(abilityScoreCombo);

        List<RollMethod> rollMethods =
                gameMode.getModeContext().getReferenceContext().getSortkeySortedCDOMObjects(RollMethod.class);
        if (!rollMethods.isEmpty())
        {
            abilitiesRolledButton = new RadioButton("Rolled:");
            vBox.getChildren().add(abilitiesRolledButton);
            abilitiesRolledButton.setToggleGroup(exclusiveGroup1);

            abilityRolledModeCombo = new ComboBox<>();

            for (RollMethod rm : rollMethods)
            {
                abilityRolledModeCombo.getItems().add(rm.getDisplayName());
            }

            vBox.getChildren().add(abilityRolledModeCombo);
        }

        Collection<PointBuyMethod> methods =
                SettingsHandler.getGameAsProperty().get().getModeContext().getReferenceContext()
                        .getConstructedCDOMObjects(PointBuyMethod.class);
        final int purchaseMethodCount = methods.size();
        abilitiesPurchasedButton = new RadioButton(LanguageBundle.getString("in_Prefs_abilitiesPurchased") + ": ");
        vBox.getChildren().add(abilitiesPurchasedButton);
        abilitiesPurchasedButton.setToggleGroup(exclusiveGroup1);

        pMode = new String[purchaseMethodCount];
        pModeMethodName = new String[purchaseMethodCount];

        int i = 0;
        for (PointBuyMethod pbm : methods)
        {
            pMode[i] = pbm.getDescription();
            pModeMethodName[i] = pbm.getDisplayName();
            i++;
        }

        abilityPurchaseModeCombo = new ComboBox<>(FXCollections.observableArrayList(pMode));

        vBox.getChildren().add(abilityPurchaseModeCombo);

        // Hide controls if there are no entries to select
        if (purchaseMethodCount == 0)
        {
            abilityPurchaseModeCombo.setVisible(false);
            abilitiesPurchasedButton.setVisible(false);
        }

        Button purchaseModeButton = new Button(LanguageBundle.getString("in_Prefs_purchaseModeConfig"));
        vBox.getChildren().add(purchaseModeButton);
        purchaseModeButton.setOnAction(this::PurchaseModeButtonPressed);
        this.add(GuiUtility.wrapParentAsJFXPanel(vBox));
    }

    @Override
    public void applyOptionValuesToControls()
    {
        stopListeners();

        final GameMode gameMode = SettingsHandler.getGameAsProperty().get();
        boolean bValid = true;
        final int rollMethod = gameMode.getRollMethod();

        switch (rollMethod)
        {
            case Constants.CHARACTER_STAT_METHOD_USER:
                abilitiesUserRolledButton.setSelected(true);

                break;

            case Constants.CHARACTER_STAT_METHOD_ALL_THE_SAME:
                abilitiesAllSameButton.setSelected(true);

                break;

            case Constants.CHARACTER_STAT_METHOD_PURCHASE:
                if (!abilitiesPurchasedButton.isVisible() || (pMode.length == 0))
                {
                    bValid = false;
                } else
                {
                    abilitiesPurchasedButton.setSelected(true);
                }

                break;

            case Constants.CHARACTER_STAT_METHOD_ROLLED:
                if (abilitiesRolledButton == null)
                {
                    bValid = false;
                } else
                {
                    abilitiesRolledButton.setSelected(true);
                    GuiUtility.runOnJavaFXThreadNow(() ->
                    {
                        abilityRolledModeCombo.getSelectionModel().select(gameMode.getRollMethodExpressionName());
                        return true;
                    });

                }

                break;

            default:
                bValid = false;

                break;
        }

        if (!bValid)
        {
            abilitiesUserRolledButton.setSelected(true);
            gameMode.setRollMethod(Constants.CHARACTER_STAT_METHOD_USER);
        }

        int allStatsValue = Math.min(gameMode.getStatMax(), gameMode.getAllStatsValue());
        allStatsValue = Math.max(gameMode.getStatMin(), allStatsValue);
        gameMode.setAllStatsValue(allStatsValue);
        final int finalAllStatsValue = allStatsValue;
        Platform.runLater(() -> abilityScoreCombo.getSelectionModel().select(finalAllStatsValue - gameMode.getStatMin()));

        if ((pMode != null) && (pModeMethodName != null))
        {
            final String methodName = gameMode.getPurchaseModeMethodName();

            for (int i = 0;i < pMode.length;++i)
            {
                if (pModeMethodName[i].equals(methodName))
                {
                    final int iFinal = i;
                    GuiUtility.runOnJavaFXThreadNow(() ->
                    {
                        abilityPurchaseModeCombo.getSelectionModel().select(iFinal);
                        return true;
                    });
                }
            }
        }

        startListeners();
    }

    /**
     * Create and display purchase mode stats popup frame.
     */
    private void showPurchaseModeConfiguration()
    {
        if (pmsFrame == null)
        {
            pmsFrame = new PurchaseModeFrame();
            final GameMode gameMode = SettingsHandler.getGame();

            pmsFrame.setStatMin(gameMode.getStatMin());
            pmsFrame.setStatMax(gameMode.getStatMax());

            // add a listener to know when the window has closed
            pmsFrame.addWindowListener(new WindowAdapter()
            {
                @Override
                public void windowClosed(WindowEvent e)
                {
                    Collection<PointBuyMethod> methods = SettingsHandler.getGameAsProperty().get().getModeContext()
                            .getReferenceContext().getConstructedCDOMObjects(PointBuyMethod.class);
                    final int purchaseMethodCount = methods.size();
                    pMode = new String[purchaseMethodCount];
                    pModeMethodName = new String[purchaseMethodCount];

                    final String methodName = SettingsHandler.getGameAsProperty().get().getPurchaseModeMethodName();
                    abilityPurchaseModeCombo.getItems().clear();

                    int i = 0;
                    for (PointBuyMethod pbm : methods)
                    {
                        pMode[i] = pbm.getDescription();
                        pModeMethodName[i] = pbm.getDisplayName();
                        abilityPurchaseModeCombo.getItems().add(pMode[i]);

                        if (pModeMethodName[i].equals(methodName))
                        {
                            abilityPurchaseModeCombo.getSelectionModel().select(i);
                        }
                        i++;
                    }

                    // free resources
                    pmsFrame = null;

                    //
                    // If user has added at least one method, then make the controls visible. Otherwise
                    // it is not a valid choice and cannot be selected, so hide it.
                    //
                    abilityPurchaseModeCombo.setVisible(purchaseMethodCount != 0);
                    abilitiesPurchasedButton.setVisible(purchaseMethodCount != 0);

                    //
                    // If no longer visible, but was selected, then use 'user rolled' instead
                    //
                    if (!abilitiesPurchasedButton.isVisible() && abilitiesPurchasedButton.isSelected())
                    {
                        abilitiesUserRolledButton.setSelected(true);
                    }

                }
            });
        }

        pmsFrame.pack();
        pmsFrame.setLocationRelativeTo(null);
        pmsFrame.setVisible(true);
        scoreListener = evt -> abilitiesAllSameButton.setSelected(true);
        purchaseModeListener = evt -> abilitiesPurchasedButton.setSelected(true);
        rolledModeListener = evt -> abilitiesRolledButton.setSelected(true);
        startListeners();

    }

    /**
     * Start the listeners that track changing data. These have to
     * be stopped when updating data programatically to avoid
     * spurious setting of dirty flags etc.
     */
    private void startListeners()
    {
        abilityScoreCombo.setOnAction(scoreListener);
        abilityPurchaseModeCombo.setOnAction(purchaseModeListener);
        if (abilityRolledModeCombo != null)
        {
            abilityRolledModeCombo.setOnAction(rolledModeListener);
        }
    }

    /**
     * Stop the listeners that track changing data. These have to
     * be stopped when updating data programatically to avoid
     * spurious setting of dirty flags etc.
     */
    private void stopListeners()
    {
        abilityScoreCombo.setOnAction(null);
        abilityPurchaseModeCombo.setOnAction(null);
        if (abilityRolledModeCombo != null)
        {
            abilityRolledModeCombo.setOnAction(null);
        }
    }

    @Override
    public String getTitle()
    {
        return IN_ABILITIES;
    }

    @Override
    public void setOptionsBasedOnControls()
    {
        final GameMode gameMode = SettingsHandler.getGameAsProperty().get();
        gameMode.setAllStatsValue(abilityScoreCombo.getSelectionModel().getSelectedIndex() + gameMode.getStatMin());

        if (abilitiesUserRolledButton.isSelected())
        {
            gameMode.setRollMethod(Constants.CHARACTER_STAT_METHOD_USER);
        } else if (abilitiesAllSameButton.isSelected())
        {
            gameMode.setRollMethod(Constants.CHARACTER_STAT_METHOD_ALL_THE_SAME);
        } else if (abilitiesPurchasedButton.isSelected())
        {
            if (abilityPurchaseModeCombo.isVisible() && (abilityPurchaseModeCombo.getSelectionModel().getSelectedIndex() >= 0))
            {
                gameMode.setPurchaseMethodName(pModeMethodName[abilityPurchaseModeCombo.getSelectionModel().getSelectedIndex()]);
            } else
            {
                gameMode.setRollMethod(Constants.CHARACTER_STAT_METHOD_USER);
            }
        } else if ((abilitiesRolledButton != null) && (abilitiesRolledButton.isSelected()))
        {
            if (abilityRolledModeCombo.getSelectionModel().getSelectedIndex() >= 0)
            {
                gameMode.setRollMethodExpressionByName(abilityRolledModeCombo.getSelectionModel().getSelectedItem());
            } else
            {
                gameMode.setRollMethod(Constants.CHARACTER_STAT_METHOD_USER);
            }
        }
    }

    /**
     * Handler for the Purchase Mode Config button.
     */
    private void PurchaseModeButtonPressed(ActionEvent actionEvent)
    {
        showPurchaseModeConfiguration();
    }
}
