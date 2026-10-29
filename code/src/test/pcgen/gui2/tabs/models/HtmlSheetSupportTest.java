/*
 * Copyright 2026 the PCGen Project.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */
package pcgen.gui2.tabs.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

import freemarker.template.Configuration;

import pcgen.system.ConfigurationSettings;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** Verifies info-sheet resolution when the installation differs from the working directory. */
class HtmlSheetSupportTest
{
	@TempDir
	Path directory;
	private Path application;
	private Field installRoot;
	private Object previousRoot;

	@BeforeEach
	void setUp() throws Exception
	{
		application = Files.createDirectories(directory.resolve("portable/app"));
		installRoot = ConfigurationSettings.class.getDeclaredField("installRoot");
		installRoot.setAccessible(true);
		previousRoot = installRoot.get(null);
		installRoot.set(null, application);
	}

	@AfterEach
	void restoreRoot() throws Exception
	{
		installRoot.set(null, previousRoot);
	}

	@ParameterizedTest
	@ValueSource(strings = {"preview/summary/35e_info.html.ftl", "preview/skills/35e_skills.html"})
	void relativeInfoSheet_should_loadFromTheApplicationDirectory(String relative) throws Exception
	{
		Path expected = application.resolve(relative);
		Files.createDirectories(expected.getParent());
		Files.writeString(expected, "portable preview");
		var template = HtmlSheetSupport.resolveTemplateFile(relative);
		assertEquals(expected.toFile(), template);

		var configuration = new Configuration(Configuration.VERSION_2_3_35);
		configuration.setDirectoryForTemplateLoading(template.getParentFile());
		assertEquals("portable preview", configuration.getTemplate(template.getName()).toString());
	}

	@Test
	void absoluteInfoSheet_should_preserveTheCustomLocation() throws Exception
	{
		Path custom = directory.resolve("custom-summary.ftl");
		Files.writeString(custom, "custom preview");
		assertEquals(custom.toFile(), HtmlSheetSupport.resolveTemplateFile(custom.toString()));
	}
}
