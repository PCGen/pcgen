/*
 * Copyright 2026 the PCGen Project.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */
package pcgen.gui2.prefs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.util.WaitForAsyncUtils.asyncFx;

import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import javafx.embed.swing.JFXPanel;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;

import pcgen.system.ConfigurationSettings;
import pcgen.system.LanguageBundle;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

/** Measures the real Swing/JavaFX Location form inside the preferences viewport. */
@ExtendWith(ApplicationExtension.class)
class LocationPanelTest
{
	private LocationPanel panel;
	private JFXPanel content;
	private JScrollPane scroll;
	private String previousSettingsPath;

	@BeforeEach
	void setUp() throws Exception
	{
		previousSettingsPath = ConfigurationSettings.getSystemProperty(ConfigurationSettings.SETTINGS_FILES_PATH);
		SwingUtilities.invokeAndWait(() -> {
			panel = new LocationPanel();
			content = (JFXPanel) panel.getComponent(0);
			scroll = new JScrollPane(panel);
		});
		asyncFx(() -> { }).get();
	}

	@AfterEach
	void tearDown() throws Exception
	{
		asyncFx(() -> content.setScene(null)).get();
		ConfigurationSettings.setSystemProperty(ConfigurationSettings.SETTINGS_FILES_PATH, previousSettingsPath);
	}

	@Test
	@DisabledIfSystemProperty(named = "java.awt.headless", matches = "true",
			disabledReason = "JFXPanel needs a real AWT display")
	void locationForm_should_fillTheViewportWhenResized() throws Exception
	{
		double narrow = assertFillsViewport(650);
		double wide = assertFillsViewport(1100);
		assertTrue(wide - narrow >= 350, "Directory fields must expand with the window");
		assertEquals(narrow, assertFillsViewport(650), 2, "The form must also shrink back without overflowing");
	}

	@Test
	@DisabledIfSystemProperty(named = "java.awt.headless", matches = "true",
			disabledReason = "JFXPanel needs a real AWT display")
	void settingsChoices_should_preserveDirectorySelectionBehavior() throws Exception
	{
		assertFillsViewport(1100);
		asyncFx(() -> {
			var root = content.getScene().getRoot();
			var settings = root.lookupAll(".label").stream()
					.filter(Label.class::isInstance).map(Label.class::cast)
					.filter(label -> label.getText().equals(LanguageBundle.getString("in_Prefs_pcgenFilesDir")))
					.map(label -> (TextField) label.getLabelFor()).findFirst().orElseThrow();
			var browse = browseBeside(root, settings);
			var choices = root.lookupAll(".radio-button").stream()
					.map(RadioButton.class::cast).toList();
			choices.stream().filter(choice -> choice.getText().equals(LanguageBundle.getString("in_Prefs_homeDir")))
					.findFirst().orElseThrow().fire();
			assertEquals(ConfigurationSettings.getUserSettingsDirFromFilePath(), settings.getText());
			assertTrue(browse.isDisabled());
			choices.stream().filter(choice -> choice.getText().equals(LanguageBundle.getString("in_Prefs_selectDir")))
					.findFirst().orElseThrow().fire();
			assertEquals("", settings.getText());
			assertFalse(browse.isDisabled());
		}).get();
	}

	private double assertFillsViewport(int width) throws Exception
	{
		int[] geometry = new int[2];
		SwingUtilities.invokeAndWait(() -> {
			scroll.setSize(width, 600);
			scroll.doLayout();
			scroll.getViewport().doLayout();
			panel.doLayout();
			geometry[0] = scroll.getViewport().getWidth();
			geometry[1] = content.getWidth();
		});
		SwingUtilities.invokeAndWait(() -> { });
		assertEquals(geometry[0], geometry[1], "The embedded form must fill the available viewport width");
		return asyncFx(() -> {
			var root = content.getScene().getRoot();
			root.applyCss();
			root.layout();
			var fields = root.lookupAll(".text-field").stream().map(TextField.class::cast).toList();
			assertEquals(12, fields.size(), "Every existing directory setting must remain available");
			double start = fields.getFirst().localToScene(fields.getFirst().getLayoutBounds()).getMinX();
			for (var field : fields)
			{
				var bounds = field.localToScene(field.getLayoutBounds());
				assertEquals(start, bounds.getMinX(), 1, "Directory fields must share an aligned column");
				var button = browseBeside(root, field);
				var buttonBounds = button.localToScene(button.getLayoutBounds());
				assertTrue(buttonBounds.getMinX() > bounds.getMaxX(), "Browse belongs beside its directory field");
				assertEquals(geometry[0] - ((Region) root).getInsets().getRight(), buttonBounds.getMaxX(), 2,
						"Directory rows must use the available width");
			}
			return fields.stream().mapToDouble(TextField::getWidth).min().orElseThrow();
		}).get();
	}

	private static Button browseBeside(Parent root, TextField field)
	{
		var bounds = field.localToScene(field.getLayoutBounds());
		var beside = root.lookupAll(".button").stream().filter(Button.class::isInstance).map(Button.class::cast)
				.filter(button -> Math.abs(button.localToScene(button.getLayoutBounds()).getCenterY()
						- bounds.getCenterY()) < 1).findFirst();
		assertTrue(beside.isPresent(), "Each directory field needs its browse button on the same row");
		return beside.orElseThrow();
	}
}
