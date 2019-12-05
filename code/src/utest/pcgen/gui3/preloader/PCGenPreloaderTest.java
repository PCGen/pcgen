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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package pcgen.gui3.preloader;

import java.io.IOException;

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

@ExtendWith(ApplicationExtension.class)
class PCGenPreloaderTest
{
    @Start
    private void Start(Stage stage) throws IOException
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(PCGenPreloader.class.getResource("PCGenPreloader.fxml"));
        loader.setResources(LanguageBundle.getBundle());
        Scene scene = loader.load();
        stage.setScene(scene);
        stage.show();
    }

    @Test
    void test_preloader_has_a_image_and_progress_bar(final FxRobotInterface robot)
    {
        FxAssert.verifyThat("#styleImage", NodeMatchers.isVisible());
        FxAssert.verifyThat("#pcGenStatusBar", NodeMatchers.isVisible());
    }

}
