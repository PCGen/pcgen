/*
 * Copyright 2007 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
package plugin;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code PluginBuildTest} verifies that every plugin source file under
 * {@code code/src/java/plugin/<category>/} is packaged into the matching
 * {@code <category>plugins.jar} produced by {@code code/gradle/plugins.gradle}.
 * As a result this unit test is a bit different in structure to a normal test.
 */
class PluginBuildTest
{
	private static final String JAVA_EXT = ".java";
	private static final String CLASS_EXT = ".class";

	private static final Path PLUGIN_SOURCE_ROOT = Paths.get("code/src/java/plugin");

	@BeforeEach
	void setUp()
	{
	}

	/**
	 * Catches a new {@code code/src/java/plugin/&lt;category&gt;/} directory that was added
	 * without a matching jar entry being wired into {@code code/gradle/plugins.gradle}
	 * (and thus a matching test method here). The set below must stay in sync with the
	 * {@code createJarTask("...", "...plugins.jar", ..., "plugin/&lt;category&gt;/**\/*.class")}
	 * calls in {@code plugins.gradle}.
	 */
	@Test
	void everyPluginPackageHasACorrespondingJar() throws IOException
	{
		Set<String> packagedCategories = Set.of(
				"bonustokens", "converter", "exporttokens", "function",
				"grouping", "jepcommands", "lsttokens", "modifier",
				"pretokens", "primitive", "qualifier"
		);
		try (Stream<Path> stream = Files.list(PLUGIN_SOURCE_ROOT))
		{
			Set<String> uncovered = stream
					.filter(Files::isDirectory)
					.map(p -> p.getFileName().toString())
					.filter(n -> !packagedCategories.contains(n))
					.collect(Collectors.toCollection(TreeSet::new));
			assertTrue(uncovered.isEmpty(),
					"New plugin package(s) under " + PLUGIN_SOURCE_ROOT
							+ " without a jar entry in plugins.gradle: " + uncovered);
		}
	}

	/**
	 * Check for the presence of all Bonus token parsing plugins.
	 */
	@Test
	void testBonusPlugins() throws IOException
	{
		Path sourceFolder = Paths.get("code/src/java/plugin/bonustokens");
		Path jar = Paths.get("plugins/bonusplugins.jar");
		checkPluginJar(jar, sourceFolder);
	}

	/**
	 * Check for the presence of all Converter plugins.
	 */
	@Test
	void testConverterPlugins() throws IOException
	{
		Path sourceFolder = Paths.get("code/src/java/plugin/converter");
		Path jar = Paths.get("plugins/converterplugins.jar");
		checkPluginJar(jar, sourceFolder);
	}

	/**
	 * Check for the presence of all Export Token plugins.
	 */
	@Test
	void testExportTokensPlugins() throws IOException
	{
		Path sourceFolder = Paths.get("code/src/java/plugin/exporttokens");
		Path jar = Paths.get("plugins/exportplugins.jar");
		checkPluginJar(jar, sourceFolder);
	}

	/**
	 * Check for the presence of all Function plugins.
	 */
	@Test
	void testFunctionPlugins() throws IOException
	{
		Path sourceFolder = Paths.get("code/src/java/plugin/function");
		Path jar = Paths.get("plugins/functionplugins.jar");
		checkPluginJar(jar, sourceFolder);
	}

	/**
	 * Check for the presence of all Grouping plugins.
	 */
	@Test
	void testGroupingPlugins() throws IOException
	{
		Path sourceFolder = Paths.get("code/src/java/plugin/grouping");
		Path jar = Paths.get("plugins/groupingplugins.jar");
		checkPluginJar(jar, sourceFolder);
	}

	/**
	 * Check for the presence of all JEP Commands plugins.
	 */
	@Test
	void testJepCommandsPlugins() throws IOException
	{
		Path sourceFolder = Paths.get("code/src/java/plugin/jepcommands");
		Path jar = Paths.get("plugins/jepcommandsplugins.jar");
		checkPluginJar(jar, sourceFolder);
	}

	/**
	 * Check for the presence of all LST token parsing plugins.
	 */
	@Test
	void testLstPlugins() throws IOException
	{
		Path sourceFolder = Paths.get("code/src/java/plugin/lsttokens");
		Path jar = Paths.get("plugins/lstplugins.jar");
		checkPluginJar(jar, sourceFolder);
	}

	/**
	 * Check for the presence of all Modifier plugins.
	 */
	@Test
	void testModifierPlugins() throws IOException
	{
		Path sourceFolder = Paths.get("code/src/java/plugin/modifier");
		Path jar = Paths.get("plugins/modifierplugins.jar");
		checkPluginJar(jar, sourceFolder);
	}

	/**
	 * Check for the presence of all PRE tokens plugins.
	 */
	@Test
	void testPreTokensPlugins() throws IOException
	{
		Path sourceFolder = Paths.get("code/src/java/plugin/pretokens");
		Path jar = Paths.get("plugins/preplugins.jar");
		checkPluginJar(jar, sourceFolder);
	}

	/**
	 * Check for the presence of all Primitive plugins.
	 */
	@Test
	void testPrimitivePlugins() throws IOException
	{
		Path sourceFolder = Paths.get("code/src/java/plugin/primitive");
		Path jar = Paths.get("plugins/primitiveplugins.jar");
		checkPluginJar(jar, sourceFolder);
	}

	/**
	 * Check for the presence of all Qualifier plugins.
	 */
	@Test
	void testQualifierPlugins() throws IOException
	{
		Path sourceFolder = Paths.get("code/src/java/plugin/qualifier");
		Path jar = Paths.get("plugins/qualifierplugins.jar");
		checkPluginJar(jar, sourceFolder);
	}

	private void checkPluginJar(Path jar, Path sourceFolder) throws IOException
	{
		String expectedJarPrefix = PLUGIN_SOURCE_ROOT.getParent().relativize(sourceFolder)
				.toString().replace(File.separatorChar, '/') + '/';

		try (Stream<Path> stream = Files.walk(sourceFolder)) {
			var javaPlugins = stream
					.filter(Files::isRegularFile)
					.map(Path::getFileName)
					.map(Path::toString)
					.filter(s -> s.endsWith(JAVA_EXT))
					.map(s -> s.substring(0, s.length() - JAVA_EXT.length()))
					.collect(Collectors.toSet());

			try (JarFile jarFile = new JarFile(jar.toFile())) {
				Set<String> classEntries = jarFile.stream()
						.map(JarEntry::getRealName)
						.filter(s -> s.endsWith(CLASS_EXT))
						.collect(Collectors.toSet());

				Set<String> foreign = classEntries.stream()
						.filter(s -> s.startsWith("plugin/") && !s.startsWith(expectedJarPrefix))
						.collect(Collectors.toCollection(TreeSet::new));
				assertTrue(foreign.isEmpty(),
						"Jar " + jar + " contains classes outside the expected " + expectedJarPrefix
								+ " sub-package: " + foreign);

				var res = classEntries.stream()
						.map(s -> s.substring(s.lastIndexOf('/') + 1)) // trim everything before the last '/'
						.map(s -> s.substring(0, s.length() - CLASS_EXT.length()))   // trim the class extension
						.collect(Collectors.toSet());

				javaPlugins.removeAll(res);

				assertTrue(javaPlugins.isEmpty(), "All java plugins should be represented in jar " + jar + " but the following were not: " + javaPlugins);
			}
		}
	}
}
