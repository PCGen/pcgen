/*
 * Copyright 2026 Vest <Vest@users.noreply.github.com>
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
 */
package pcgen.gui3.namegen;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import pcgen.core.SettingsHandler;
import pcgen.system.LanguageBundle;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobotInterface;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.NodeMatchers;

/**
 * Smoke test for the random-name FXML panel — mirrors {@code AboutDialogTest}.
 * Loads the panel into a stage and asserts that the key controls render.
 * Deeper logic is exercised in {@link GenderSelectionTest} and the engine
 * tests under {@code pcgen.core.namegen}.
 */
@ExtendWith(ApplicationExtension.class)
class RandomNamePanelTest
{
	@Start
	private void start(Stage stage) throws IOException
	{
		// Controller's initialize() resolves the data dir through SettingsHandler.
		SettingsHandler.setGmgenPluginDir(new File(System.getProperty("user.dir"), "plugins"));

		FXMLLoader loader = new FXMLLoader();
		URL resource = RandomNamePanelController.class.getResource("RandomNamePanel.fxml");
		assert resource != null;
		loader.setLocation(resource);
		LanguageBundle.getString("");
		loader.setResources(LanguageBundle.getBundle());
		Scene scene = loader.load();
		stage.setScene(scene);
		stage.show();
	}

	@Test
	void test_panel_renders_expected_controls(final FxRobotInterface robot)
	{
		FxAssert.verifyThat("#categoryCombo", NodeMatchers.isVisible());
		FxAssert.verifyThat("#titleCombo", NodeMatchers.isVisible());
		FxAssert.verifyThat("#genderFemale", NodeMatchers.isVisible());
		FxAssert.verifyThat("#genderMale", NodeMatchers.isVisible());
		FxAssert.verifyThat("#genderOther", NodeMatchers.isVisible());
		FxAssert.verifyThat("#advancedPane", NodeMatchers.isVisible());
		FxAssert.verifyThat("#randomStructureCheck", NodeMatchers.isVisible());
		FxAssert.verifyThat("#generateButton", NodeMatchers.isVisible());
		FxAssert.verifyThat("#okButton", NodeMatchers.isVisible());
		FxAssert.verifyThat("#cancelButton", NodeMatchers.isVisible());
	}
}
