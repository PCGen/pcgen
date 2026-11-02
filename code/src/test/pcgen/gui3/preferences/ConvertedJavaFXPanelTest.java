/*
 * Copyright 2026 the PCGen Project.
 * Distributed under the GNU Lesser General Public License, version 2.1 or later.
 */
package pcgen.gui3.preferences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.util.WaitForAsyncUtils.asyncFx;

import javax.swing.SwingUtilities;

import pcgen.gui3.ResettableController;
import pcgen.system.LanguageBundle;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

/** Exercises preference callbacks through the real Swing/JavaFX wrapper. */
@ExtendWith(ApplicationExtension.class)
@DisabledIfSystemProperty(named = "java.awt.headless", matches = "true",
		disabledReason = "JFXPanel needs a real AWT display")
public class ConvertedJavaFXPanelTest
{
	private ConvertedJavaFXPanel<ThreadCheckingController> panel;
	private JFXPanel content;

	@BeforeEach
	void setUp() throws Exception
	{
		LanguageBundle.getString("in_Prefs_location");
		SwingUtilities.invokeAndWait(() -> {
			panel = new ConvertedJavaFXPanel<>(ThreadCheckingController.class,
				"ThreadCheckingPreferences.fxml", "in_Prefs_location");
			content = (JFXPanel) panel.getComponent(0);
		});
	}

	@AfterEach
	void tearDown() throws Exception
	{
		asyncFx(() -> content.setScene(null)).get();
	}

	/** Reset runs on FX, and Apply finishes on FX whether called by Swing or JavaFX. */
	@Test
	void callbacks_should_runOnFxWhenInvokedFromEitherUiThread() throws Exception
	{
		SwingUtilities.invokeAndWait(panel::applyOptionValuesToControls);
		SwingUtilities.invokeAndWait(panel::setOptionsBasedOnControls);
		asyncFx(() -> {
			var controller = panel.getController();
			assertEquals(1, controller.resets);
			assertEquals(1, controller.applies, "Swing Apply must finish before returning");
			panel.setOptionsBasedOnControls();
			assertEquals(2, controller.applies);
			panel.applyOptionValuesToControls();
		}).get();
		asyncFx(() -> assertEquals(2, panel.getController().resets)).get();
	}

	/** Rejects controller callbacks outside the FX thread and records completed operations. */
	public static final class ThreadCheckingController implements ResettableController
	{
		private int resets;
		private int applies;

		@Override
		public void reset()
		{
			assertTrue(Platform.isFxApplicationThread(), "Reset must run on the FX thread");
			resets++;
		}

		@Override
		public void apply()
		{
			assertTrue(Platform.isFxApplicationThread(), "Apply must run on the FX thread");
			applies++;
		}
	}
}
