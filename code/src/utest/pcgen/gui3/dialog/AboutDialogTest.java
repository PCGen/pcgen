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

package pcgen.gui3.dialog;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.junit.jupiter.api.Disabled;
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

// @Disabled // Receiver class com.sun.glass.ui.monocle.MonocleWindow does not define or inherit an implementation of the resolved method 'abstract void _updateViewSize(long)' of abstract class com.sun.glass.ui.Window.
@ExtendWith(ApplicationExtension.class)
class AboutDialogTest
{
	@Start
	private void Start(Stage stage) throws IOException
	{
		FXMLLoader loader = new FXMLLoader();
		URL resource = AboutDialogController.class.getResource("AboutDialog.fxml");
		assert resource != null;
		loader.setLocation(resource);
		// initialize the language bundle
		LanguageBundle.getString("");
		ResourceBundle bundle = LanguageBundle.getBundle();
		assert bundle != null;
		loader.setResources(bundle);
		Scene scene = loader.load();
		stage.setScene(scene);
		stage.show();
	}

	@Test
	void test_about_dialog_loads(final FxRobotInterface robot)
	{
		FxAssert.verifyThat("#abt_credits", NodeMatchers.isVisible());
	}
}
