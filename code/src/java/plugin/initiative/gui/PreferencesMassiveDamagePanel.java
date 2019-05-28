/*
 *  Initiative - A role playing utility to track turns
 *  Copyright (C) 2002 Devon D Jones
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *  PreferencesMassiveDamagePanel.java
 */
package plugin.initiative.gui;

import java.awt.BorderLayout;

import pcgen.core.SettingsHandler;
import pcgen.gui3.GuiUtility;
import pcgen.system.LanguageBundle;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

public class PreferencesMassiveDamagePanel extends gmgen.gui.PreferencesPanel
{

	private static final String OPTION_NAME_TYPE = "Initiative.Damage.Massive.Type"; //$NON-NLS-1$
	private static final String OPTION_NAME_EFFECT = "Initiative.Damage.Massive.Effect"; //$NON-NLS-1$
	private static final String OPTION_NAME_USESIZE = "Initiative.Damage.Massive.SizeMod"; //$NON-NLS-1$

	private static final int MASSIVE_OFF = 1;
	static final int MASSIVE_DND = 2;
	static final int MASSIVE_D20_MODERN = 3;
	static final int MASSIVE_HOUSE_HALF = 4;
	static final int MASSIVE_EFFECT_KILL = 1;
	static final int MASSIVE_EFFECT_NEGATIVE = 2;
	static final int MASSIVE_EFFECT_HALF_TOTAL = 3;
	static final int MASSIVE_EFFECT_HALF_CURRENT = 4;

	private RadioButton massive1;
	private RadioButton massive2;
	private RadioButton massive3;
	private RadioButton massive4;

	private RadioButton effect1;
	private RadioButton effect2;
	private RadioButton effect3;
	private RadioButton effect4;

	private CheckBox sizeCheck;
	private Label sizeLabel1;
	private Label sizeLabel2;

	public PreferencesMassiveDamagePanel()
	{
		initComponents();
		initPreferences();
	}

	@Override
	public void applyPreferences()
	{
		SettingsHandler.setGMGenOption(OPTION_NAME_TYPE, getType());
		SettingsHandler.setGMGenOption(OPTION_NAME_EFFECT, getEffect());
		SettingsHandler.setGMGenOption(OPTION_NAME_USESIZE, sizeCheck.isSelected());
	}

	@Override
	public void initPreferences()
	{
		setType(SettingsHandler.getGMGenOption(OPTION_NAME_TYPE, MASSIVE_OFF));
		setEffect(SettingsHandler.getGMGenOption(OPTION_NAME_EFFECT, MASSIVE_EFFECT_KILL));
		setSizeMod(SettingsHandler.getGMGenOption(OPTION_NAME_USESIZE, true));
	}

	@Override
	public String toString()
	{
		return LanguageBundle.getString("in_plugin_init_massive_massive"); //$NON-NLS-1$
	}

	private void setEffect(int choice)
	{
		if (choice == MASSIVE_EFFECT_KILL)
		{
			effect1.setSelected(true);
		}
		else if (choice == MASSIVE_EFFECT_NEGATIVE)
		{
			effect2.setSelected(true);
		}
		else if (choice == MASSIVE_EFFECT_HALF_TOTAL)
		{
			effect3.setSelected(true);
		}
		else if (choice == MASSIVE_EFFECT_HALF_CURRENT)
		{
			effect4.setSelected(true);
		}
	}

	private int getEffect()
	{
		int returnVal = 0;

		if (effect1.isSelected())
		{
			returnVal = MASSIVE_EFFECT_KILL;
		}
		else if (effect2.isSelected())
		{
			returnVal = MASSIVE_EFFECT_NEGATIVE;
		}
		else if (effect3.isSelected())
		{
			returnVal = MASSIVE_EFFECT_HALF_TOTAL;
		}
		else if (effect4.isSelected())
		{
			returnVal = MASSIVE_EFFECT_HALF_CURRENT;
		}

		return returnVal;
	}

	private void setSizeMod(boolean selected)
	{
		sizeCheck.setSelected(selected);
	}

	private void setType(int choice)
	{
		if (choice == MASSIVE_OFF)
		{
			massive1.setSelected(true);
		}
		else if (choice == MASSIVE_DND)
		{
			massive2.setSelected(true);
		}
		else if (choice == MASSIVE_D20_MODERN)
		{
			massive3.setSelected(true);
		}
		else if (choice == MASSIVE_HOUSE_HALF)
		{
			massive4.setSelected(true);
		}
	}

	private int getType()
	{
		int returnVal = 0;

		if (massive1.isSelected())
		{
			returnVal = MASSIVE_OFF;
		}
		else if (massive2.isSelected())
		{
			returnVal = MASSIVE_DND;
		}
		else if (massive3.isSelected())
		{
			returnVal = MASSIVE_D20_MODERN;
		}
		else if (massive4.isSelected())
		{
			returnVal = MASSIVE_HOUSE_HALF;
		}

		return returnVal;
	}

	private void initComponents()
	{
		ToggleGroup massiveDamageGroup = new ToggleGroup();
		massive1 = new RadioButton();
		massive1.setToggleGroup(massiveDamageGroup);
		massive2 = new RadioButton();
		massive2.setToggleGroup(massiveDamageGroup);
		massive3 = new RadioButton();
		massive3.setToggleGroup(massiveDamageGroup);
		massive4 = new RadioButton();
		massive3.setToggleGroup(massiveDamageGroup);

		ToggleGroup effectGroup = new ToggleGroup();
		effect1 = new RadioButton();
		effect1.setToggleGroup(effectGroup);
		effect2 = new RadioButton();
		effect2.setToggleGroup(effectGroup);
		effect3 = new RadioButton();
		effect3.setToggleGroup(effectGroup);
		effect4 = new RadioButton();
		effect4.setToggleGroup(effectGroup);


		VBox sizePanel = new VBox();
		sizeCheck = new CheckBox();
		sizeLabel1 = new Label();
		sizeLabel2 = new Label();

		setLayout(new BorderLayout());

		VBox massivePanel = new VBox();

		massive1.setSelected(true);
		massive1.setText(LanguageBundle.getString("in_plugin_init_massive_noTrack")); //$NON-NLS-1$
		massive1.setOnAction(this::massiveActionPerformed);

		massivePanel.getChildren().add(massive1);

		massive2.setText(LanguageBundle.getString("in_plugin_init_massive_50damage")); //$NON-NLS-1$
		massive2.setOnAction(this::massiveActionPerformed);

		massivePanel.getChildren().add(massive2);

		massive3.setText(LanguageBundle.getString("in_plugin_init_massive_ConDamage")); //$NON-NLS-1$
		massive3.setOnAction(this::massiveActionPerformed);

		massivePanel.getChildren().add(massive3);

		massive4.setText(LanguageBundle.getString("in_plugin_init_massive_Half")); //$NON-NLS-1$
		massive4.setOnAction(this::massiveActionPerformed);

		massivePanel.getChildren().add(massive4);


		Node massivePanelWithTitle =
				new TitledPane(LanguageBundle.getString("in_plugin_init_massive_failure"), massivePanel);


		VBox effectPanel = new VBox();

		effect1.setSelected(true);
		effect1.setText(LanguageBundle.getString("in_plugin_init_massive_kill")); //$NON-NLS-1$
		effect1.setDisable(true);
		effectPanel.getChildren().add(effect1);

		effect2.setText(LanguageBundle.getString("in_plugin_init_massive_minusOne")); //$NON-NLS-1$
		effect2.setDisable(true);
		effectPanel.getChildren().add(effect2);

		effect3.setText(LanguageBundle.getString("in_plugin_init_massive_halfTotal")); //$NON-NLS-1$
		effect3.setDisable(true);
		effectPanel.getChildren().add(effect3);

		effect4.setText(LanguageBundle.getString("in_plugin_init_massive_halfCurrent")); //$NON-NLS-1$
		effect4.setDisable(true);
		effectPanel.getChildren().add(effect4);

		Node effectPanelWithTitle =
				new TitledPane(LanguageBundle.getString("in_plugin_init_massive_failure"), effectPanel);

		sizeCheck.setSelected(true);
		sizeCheck.setText(LanguageBundle.getString("in_plugin_init_massive_size")); //$NON-NLS-1$
		sizeCheck.setDisable(true);
		sizePanel.getChildren().add(sizeCheck);

		sizeLabel1.setText(LanguageBundle.getString("in_plugin_init_massive_sizeL")); //$NON-NLS-1$
		sizePanel.getChildren().add(sizeLabel1);

		sizeLabel2.setText(LanguageBundle.getString("in_plugin_init_massive_sizeS")); //$NON-NLS-1$
		sizePanel.getChildren().add(sizeLabel2);

		Node sizePanelWithTitle =
				new TitledPane(LanguageBundle.getString("in_plugin_init_size"), sizePanel);

		VBox mainPanel = new VBox();
		mainPanel.getChildren().add(massivePanelWithTitle);
		mainPanel.getChildren().add(effectPanelWithTitle);
		mainPanel.getChildren().add(sizePanelWithTitle);

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(mainPanel);

		add(GuiUtility.wrapParentAsJFXPanel(scrollPane));
	}

	private void massiveActionPerformed(ActionEvent evt)
	{
		if (evt.getSource() == massive1)
		{
			effect1.setDisable(true);
			effect2.setDisable(true);
			effect3.setDisable(true);
			effect4.setDisable(true);
			sizeCheck.setDisable(true);
			sizeLabel1.setDisable(true);
			sizeLabel2.setDisable(true);
		}
		else if (evt.getSource() == massive2)
		{
			effect1.setDisable(false);
			effect2.setDisable(false);
			effect3.setDisable(false);
			effect4.setDisable(false);
			sizeCheck.setDisable(false);
			sizeLabel1.setDisable(false);
			sizeLabel2.setDisable(false);
		}
		else if ((evt.getSource() == massive3) || (evt.getSource() == massive4))
		{
			effect1.setDisable(false);
			effect2.setDisable(false);
			effect3.setDisable(false);
			effect4.setDisable(false);
			sizeCheck.setDisable(true);
			sizeLabel1.setDisable(true);
			sizeLabel2.setDisable(true);
		}
	}
}
