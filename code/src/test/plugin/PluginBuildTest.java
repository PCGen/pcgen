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
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code PluginBuildTest} verifies that the pluginbuild.xml file has all
 * required data. As a result this unit test is a bit different in structure to
 * a normal test.
 */
class PluginBuildTest
{
	private static final String JAVA_EXT = ".java";
	private static final String CLASS_EXT = ".class";

	@BeforeEach
	void setUp()
	{
	}

	/**
	 * Check for the presence of all Bonus token parsing plugins.
	 */
	@Test
	public void testBonusPlugins() throws IOException
	{
		Path sourceFolder = Paths.get("code/src/java/plugin/bonustokens");
		Path jar = Paths.get("plugins/bonusplugins.jar");
		checkPluginJar(jar, sourceFolder);
	}

	/**
	 * Check for the presence of all Converter plugins.
	 */
	@Test
	public void testConverterPlugins() throws IOException
	{
		Path sourceFolder = Paths.get("code/src/java/plugin/converter");
		Path jar = Paths.get("plugins/converterplugins.jar");
		checkPluginJar(jar, sourceFolder);
	}

	/**
	 * Check for the presence of all Export Token plugins.
	 */
	@Test
	public void testExportTokensPlugins() throws IOException
	{
		Path sourceFolder = Paths.get("code/src/java/plugin/exporttokens");
		Path jar = Paths.get("plugins/exportplugins.jar");
		checkPluginJar(jar, sourceFolder);
	}

	/**
	 * Check for the presence of all Function plugins.
	 */
	@Test
	public void testFunctionPlugins() throws IOException
	{
		Path sourceFolder = Paths.get("code/src/java/plugin/function");
		Path jar = Paths.get("plugins/functionplugins.jar");
		checkPluginJar(jar, sourceFolder);
	}

	/**
	 * Check for the presence of all Grouping plugins.
	 */
	@Test
	public void testGroupingPlugins() throws IOException
	{
		Path sourceFolder = Paths.get("code/src/java/plugin/grouping");
		Path jar = Paths.get("plugins/groupingplugins.jar");
		checkPluginJar(jar, sourceFolder);
	}

	/**
	 * Check for the presence of all JEP Commands plugins.
	 */
	@Test
	public void testJepCommandsPlugins() throws IOException
	{
		Path sourceFolder = Paths.get("code/src/java/plugin/jepcommands");
		Path jar = Paths.get("plugins/jepcommandsplugins.jar");
		checkPluginJar(jar, sourceFolder);
	}

	/**
	 * Check for the presence of all LST token parsing plugins.
	 */
	@Test
	public void testLstPlugins() throws IOException
	{
		Path sourceFolder = Paths.get("code/src/java/plugin/lsttokens");
		Path jar = Paths.get("plugins/lstplugins.jar");
		checkPluginJar(jar, sourceFolder);
	}

	/**
	 * Check for the presence of all Modifier plugins.
	 */
	@Test
	public void testModifierPlugins() throws IOException
	{
		Path sourceFolder = Paths.get("code/src/java/plugin/modifier");
		Path jar = Paths.get("plugins/modifierplugins.jar");
		checkPluginJar(jar, sourceFolder);
	}

	/**
	 * Check for the presence of all PRE tokens plugins.
	 */
	@Test
	public void testPreTokensPlugins() throws IOException
	{
		Path sourceFolder = Paths.get("code/src/java/plugin/pretokens");
		Path jar = Paths.get("plugins/preplugins.jar");
		checkPluginJar(jar, sourceFolder);
	}

	/**
	 * Check for the presence of all Primitive plugins.
	 */
	@Test
	public void testPrimitivePlugins() throws IOException
	{
		Path sourceFolder = Paths.get("code/src/java/plugin/primitive");
		Path jar = Paths.get("plugins/primitiveplugins.jar");
		checkPluginJar(jar, sourceFolder);
	}

	/**
	 * Check for the presence of all Qualifier plugins.
	 */
	@Test
	public void testQualifierPlugins() throws IOException
	{
		Path sourceFolder = Paths.get("code/src/java/plugin/qualifier");
		Path jar = Paths.get("plugins/qualifierplugins.jar");
		checkPluginJar(jar, sourceFolder);
	}

	private void checkPluginJar(Path jar, Path sourceFolder) throws IOException
	{
		try (Stream<Path> stream = Files.walk(sourceFolder)) {
			var javaPlugins = stream
					.filter(Files::isRegularFile)
					.map(Path::getFileName)
					.map(Path::toString)
					.filter(s -> s.endsWith(JAVA_EXT))
					.map(s -> s.substring(0, s.length() - JAVA_EXT.length()))
					.collect(Collectors.toSet());

			try (JarFile jarFile = new JarFile(jar.toFile())) {
				var res = jarFile.stream()
						.map(JarEntry::getRealName)
						.filter(s -> s.endsWith(CLASS_EXT))
						.map(s -> s.substring(s.lastIndexOf('/') + 1)) // trim everything before the last '/'
						.map(s -> s.substring(0, s.length() - CLASS_EXT.length()))   // trim the class extension
						.collect(Collectors.toSet());

				javaPlugins.removeAll(res);

				assertTrue(javaPlugins.isEmpty(), "All java plugins should be represented in jar " + jar + " but the following were not: " + javaPlugins);
			}
		}
	}
}
