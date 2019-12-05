/*
 * Copyright 2009 (C) James Dempsey
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

import java.awt.BorderLayout;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import pcgen.core.GameMode;
import pcgen.core.RuleCheck;
import pcgen.core.SettingsHandler;
import pcgen.gui3.GuiUtility;
import pcgen.system.LanguageBundle;

import javafx.scene.control.ButtonBase;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

/**
 * The Class {@code HouseRulesPanel} is responsible for
 * displaying the house rules preferences and allowing the
 * preferences to be edited by the user.
 */
public final class HouseRulesPanel extends PCGenPrefsPanel
{
    private static final String IN_HOUSE_RULES = LanguageBundle.getString("in_Prefs_houseRules");

    private static final String HOUSE_RULE_STR = "{0} ({1})";

    private final Map<RuleCheck, ButtonBase> settings = new HashMap<>();

    /**
     * Instantiates a new house rules panel.
     */
    public HouseRulesPanel()
    {
        setLayout(new BorderLayout());
        VBox mainPanel = new VBox();

        GameMode gameMode = SettingsHandler.getGameAsProperty().get();
        Collection<RuleCheck> ruleCheckList =
                gameMode.getModeContext().getReferenceContext().getConstructedCDOMObjects(RuleCheck.class);

        VBox singleOptions = new VBox();
        mainPanel.getChildren().add(singleOptions);

        for (RuleCheck aRule : ruleCheckList)
        {
            String aKey = aRule.getKeyName();
            String aDesc = aRule.getDesc();
            boolean aBool = aRule.getDefault();

            if (SettingsHandler.hasRuleCheck(aKey))
            {
                aBool = SettingsHandler.getRuleCheck(aKey);
            }

            if (aRule.isExclude())
            {
                RuleCheck exclude = aRule.getExclude().get();
                ButtonBase buttonBase = settings.get(exclude);
                ToggleGroup toggleGroup;
                if (buttonBase == null)
                {
                    toggleGroup = new ToggleGroup();
                } else
                {
                    toggleGroup = ((Toggle) buttonBase).getToggleGroup();
                }

                RadioButton radioButton = new RadioButton(aKey);
                radioButton.setUserData(toggleGroup);
                radioButton.setSelected(aBool);
                radioButton.setToggleGroup(toggleGroup);
                singleOptions.getChildren().add(radioButton);
                settings.put(aRule, radioButton);
            } else
            {
                CheckBox checkBox = new CheckBox(MessageFormat.format(HOUSE_RULE_STR, aDesc, aKey));
                checkBox.setSelected(aBool);
                singleOptions.getChildren().add(checkBox);
                settings.put(aRule, checkBox);
            }

        }

        add(GuiUtility.wrapParentAsJFXPanel(mainPanel));

    }

    @Override
    public String getTitle()
    {
        return IN_HOUSE_RULES;
    }

    @Override
    public void setOptionsBasedOnControls()
    {
        final GameMode gameMode = SettingsHandler.getGameAsProperty().get();
        for (Map.Entry<RuleCheck, ButtonBase> settingsEntry : settings.entrySet())
        {
            // Save settings
            if (gameMode.getModeContext().getReferenceContext().containsConstructedCDOMObject(
                    RuleCheck.class,
                    settingsEntry.getKey().getKeyName()
            ))
            {
                ButtonBase buttonBase = settingsEntry.getValue();
                final boolean isSelected;
                // see https://github.com/javafxports/openjdk-jfx/issues/494
                if (buttonBase instanceof RadioButton)
                {
                    isSelected = ((Toggle) buttonBase).selectedProperty().get();
                } else if (buttonBase instanceof CheckBox)
                {
                    isSelected = ((CheckBox) buttonBase).selectedProperty().get();
                } else
                {
                    throw new IllegalStateException("button base that isn't of the right type " + buttonBase);
                }
                SettingsHandler.setRuleCheck(settingsEntry.getKey().getKeyName(), isSelected);
            }
        }

    }

    @Override
    public void applyOptionValuesToControls()
    {
        // Values get set on display
    }

}
