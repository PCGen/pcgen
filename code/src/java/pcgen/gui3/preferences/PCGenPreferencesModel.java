/*
 * Copyright 2019 (C) Eitan Adler <lists@eitanadler.com>
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

package pcgen.gui3.preferences;

import pcgen.cdom.base.Constants;
import pcgen.gui2.prefs.CharacterStatsPanel;
import pcgen.gui2.prefs.HouseRulesPanel;
import pcgen.gui2.prefs.LanguagePanel;
import pcgen.gui2.prefs.LocationPanel;
import pcgen.gui2.prefs.OutputPanel;
import pcgen.gui2.prefs.PCGenPrefsPanel;
import pcgen.gui3.JFXPanelFromResource;
import pcgen.system.LanguageBundle;

import javafx.embed.swing.JFXPanel;
import javafx.scene.control.TreeItem;

public final class PCGenPreferencesModel
{

    private PCGenPreferencesModel()
    {
    }

    /**
     * A wrapper for a JFXPanel to pretend to be a PCGenPrefsPanel
     * TODO: move this to a better place or figure out a way not to need this
     */
    private static final class EmptyPrefPanel extends PCGenPrefsPanel
    {
        private final String title;

        public EmptyPrefPanel(String title, JFXPanel innerPanel)
        {
            this.title = title;
            this.add(innerPanel);
        }

        @Override
        public String getTitle()
        {
            return title;
        }

        @Override
        public void applyOptionValuesToControls()
        {
            // do nothing
        }

        @Override
        public void setOptionsBasedOnControls()
        {
            // do nothing
        }
    }

    // Resource strings
    private static final String IN_APPERANCE = LanguageBundle.getString("in_Prefs_appearance"); //$NON-NLS-1$
    private static final String IN_CHARACTER = LanguageBundle.getString("in_Prefs_character"); //$NON-NLS-1$

    static TreeItem<PCGenPrefsPanel> buildEmptyPanel(String title, String messageText)
    {
        final var panel =
                new JFXPanelFromResource<>(
                        CenteredLabelPanelController.class,
                        "CenteredLabelPanel.fxml"
                );
        panel.getController().setText(messageText);
        EmptyPrefPanel emptyPrefPanel = new EmptyPrefPanel(title, panel);
        return new TreeItem<>(emptyPrefPanel);
    }

    public static TreeItem<PCGenPrefsPanel> buildRoot()
    {
        TreeItem<PCGenPrefsPanel> rootNode = buildEmptyPanel("root", "root");

        // Build the selection tree
        TreeItem<PCGenPrefsPanel> characterNode =
                buildEmptyPanel(IN_CHARACTER, LanguageBundle.getString("in_Prefs_charTip"));

        PCGenPrefsPanel characterStatsPanel = new CharacterStatsPanel();
        characterNode.getChildren().add(new TreeItem<>(characterStatsPanel));

        PCGenPrefsPanel hitPointsPanel = new ConvertedJavaFXPanel<>(
                HitPointsPreferencesController.class,
                "HitPointsPreferencesPanel.fxml",
                "in_Prefs_hp"
        );
        characterNode.getChildren().add(new TreeItem<>(hitPointsPanel));
        PCGenPrefsPanel houseRulesPanel = new HouseRulesPanel();
        characterNode.getChildren().add(new TreeItem<>(houseRulesPanel));
        PCGenPrefsPanel monsterPanel = new ConvertedJavaFXPanel<>(
                MonsterPreferencesPanelController.class,
                "MonsterPreferencesPanel.fxml",
                "in_Prefs_monsters");
        characterNode.getChildren().add(new TreeItem<>(monsterPanel));
        PCGenPrefsPanel defaultsPanel = new ConvertedJavaFXPanel<>(
                DefaultsPreferencesPanelController.class,
                "DefaultsPreferencesPanel.fxml",
                "in_Prefs_defaults");
        characterNode.getChildren().add(new TreeItem<>(defaultsPanel));
        rootNode.getChildren().add(characterNode);

        TreeItem<PCGenPrefsPanel> appearanceNode = buildEmptyPanel(
                IN_APPERANCE,
                LanguageBundle.getString("in_Prefs_appearanceTip")
        );

        PCGenPrefsPanel colorsPanel = new ConvertedJavaFXPanel<>(
                ColorsPreferencesPanelController.class,
                "ColorsPreferencesPanel.fxml",
                "in_Prefs_color"
        );

        appearanceNode.getChildren().add(new TreeItem<>(colorsPanel));
        PCGenPrefsPanel displayOptionsPanel = new ConvertedJavaFXPanel<>(
                DisplayOptionsPreferencesPanelController.class,
                "DisplayOptionsPreferencesPanel.fxml",
                "in_Prefs_displayOpts"
        );
        appearanceNode.getChildren().add(new TreeItem<>(displayOptionsPanel));
        PCGenPrefsPanel levelUpPanel = new ConvertedJavaFXPanel<>(
                LevelUpPreferencesPanelController.class,
                "LevelUpPreferencesPanel.fxml",
                "in_Prefs_levelUp"
        );
        appearanceNode.getChildren().add(new TreeItem<>(levelUpPanel));
        rootNode.getChildren().add(appearanceNode);

        TreeItem<PCGenPrefsPanel> pcGenNode = buildEmptyPanel(Constants.APPLICATION_NAME,
                LanguageBundle.getString("in_Prefs_pcgenTip"));

        // PCGen panels
        PCGenPrefsPanel equipmentPanel = new ConvertedJavaFXPanel<>(
                EquipmentPreferencesPanelController.class,
                "EquipmentPreferencesPanel.fxml",
                "in_Prefs_equipment"
        );
        pcGenNode.getChildren().add(new TreeItem<>(equipmentPanel));
        PCGenPrefsPanel languagePanel = new LanguagePanel();
        pcGenNode.getChildren().add(new TreeItem<>(languagePanel));
        PCGenPrefsPanel locationPanel = new LocationPanel();

        pcGenNode.getChildren().add(new TreeItem<>(locationPanel));
        PCGenPrefsPanel inputPanel = new ConvertedJavaFXPanel<>(
                InputPreferencesPanelController.class,
                "InputPreferencesPanel.fxml",
                "in_Prefs_input"
        );
        pcGenNode.getChildren().add(new TreeItem<>(inputPanel));
        PCGenPrefsPanel outputPanel = new OutputPanel();
        pcGenNode.getChildren().add(new TreeItem<>(outputPanel));
        ConvertedJavaFXPanel<SourcesPreferencesPanelController> sourcesPanel =
                new ConvertedJavaFXPanel<>(
                        SourcesPreferencesPanelController.class,
                        "SourcesPreferencesPanel.fxml",
                        "in_Prefs_sources"
                );
        pcGenNode.getChildren().add(new TreeItem<>(sourcesPanel));
        rootNode.getChildren().add(pcGenNode);

        String in_gamemode = LanguageBundle.getString("in_mnuSettingsCampaign");
        TreeItem<PCGenPrefsPanel> gameModeNode = buildEmptyPanel(
                in_gamemode,
                LanguageBundle.getString("in_mnuSettingsCampaignTip")
        );

        ConvertedJavaFXPanel<CopySettingsPanelController> convertedCopySettingsPanel = new ConvertedJavaFXPanel<>(
                CopySettingsPanelController.class,
                "CopySettingsPanel.fxml",
                "in_Prefs_copy"
        );

        gameModeNode.getChildren().add(new TreeItem<>(convertedCopySettingsPanel));
        rootNode.getChildren().add(gameModeNode);

        // Copy Settings
        CopySettingsPanelController copySettingsPanelController = convertedCopySettingsPanel.getController();
        copySettingsPanelController.registerAffectedPanel(characterStatsPanel);
        copySettingsPanelController.registerAffectedPanel(defaultsPanel);
        copySettingsPanelController.registerAffectedPanel(languagePanel);


        return rootNode;
    }

}
