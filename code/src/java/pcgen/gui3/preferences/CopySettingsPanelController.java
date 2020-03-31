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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import pcgen.cdom.base.Constants;
import pcgen.core.GameMode;
import pcgen.core.SettingsHandler;
import pcgen.core.SystemCollections;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui2.prefs.PCGenPrefsPanel;
import pcgen.gui3.GuiAssertions;
import pcgen.gui3.ResettableController;
import pcgen.system.LanguageBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

public final class CopySettingsPanelController implements ResettableController
{
	@FXML
	private ComboBox<GameMode> gameModeSelect;
	@FXML
	private Label copyButtonLabel;
	@FXML
	private Button copyButton;


	/**
	 * These items are part of the model.
	 */
	private ObservableList<GameMode> gameModeItems;
	private final List<PCGenPrefsPanel> affectedPanels;

	public CopySettingsPanelController()
	{
		affectedPanels = new ArrayList<>();
	}

	@FXML
	void initialize()
	{
		SettingsHandler.getGameAsProperty().addListener((observable, oldValue, newValue) -> {
			GuiAssertions.assertIsNotJavaFXThread();
			Platform.runLater(() -> copyButtonLabel.setText(LanguageBundle.getFormattedString(
                    "in_Prefs_copyTo",
                    newValue.getName()
            )));
		});

		GameMode[] unmodifiableGameModeList = SystemCollections.getUnmodifiableGameModeList().toArray(new GameMode[0]);
		gameModeItems = FXCollections.observableArrayList(unmodifiableGameModeList);
		gameModeSelect.setItems(gameModeItems);
		gameModeSelect.getSelectionModel().select(0);
	}

	@FXML
	private void onCopy(final ActionEvent actionEvent)
	{
		GameMode gmFrom = gameModeSelect.getSelectionModel().getSelectedItem();
		GameMode gmTo = SettingsHandler.getGameAsProperty().get();

		// Copy the settings from one mode to the other
		gmTo.setAllStatsValue(Objects.requireNonNull(gmFrom).getAllStatsValue());
		gmTo.setRollMethodExpressionByName(gmFrom.getRollMethodExpressionName());
		if (gmTo.getPurchaseMethodByName(gmFrom.getPurchaseModeMethodName()) != null)
		{
			gmTo.setPurchaseMethodName(Objects.requireNonNull(gmFrom.getPurchaseModeMethodName()));
		}
		gmTo.setRollMethod(gmFrom.getRollMethod());
		gmTo.selectUnitSet(gmFrom.getUnitSet().getKeyName());
		if (gmTo.getXPTableNames().contains(gmFrom.getDefaultXPTableName()))
		{
			gmTo.setDefaultXPTableName(gmFrom.getDefaultXPTableName());
		}
		String currentICS =
				SettingsHandler.getPCGenOption("InfoCharacterSheet." + gmTo.getName() + ".CurrentSheet", "");
		String fromGmICS = SettingsHandler
				.getPCGenOption("InfoCharacterSheet." + gmFrom.getName() + ".CurrentSheet", currentICS);
		SettingsHandler.setPCGenOption("InfoCharacterSheet." + gmTo.getName() + ".CurrentSheet", fromGmICS);

		GuiAssertions.assertIsNotJavaFXThread();
		affectedPanels.forEach(PCGenPrefsPanel::applyOptionValuesToControls);

		// Let the user know it is done
		ShowMessageDelegate.showMessageDialog(LanguageBundle.getString("in_Prefs_copyDone"),
				Constants.APPLICATION_NAME, MessageType.INFORMATION);
	}

	/**
	 * Register the other settings panels that can be affected by this
	 * class.
	 *
	 * @param panel The ExperiencePanel instance
	 */
	public void registerAffectedPanel(PCGenPrefsPanel panel)
	{
		affectedPanels.add(panel);
	}

	@Override
	public void reset()
	{
		// we don't need to do anything
	}

	@Override
	public void apply()
	{
		// we don't need to do anything
	}
}
