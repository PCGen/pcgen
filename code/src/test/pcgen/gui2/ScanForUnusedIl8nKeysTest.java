/*
 * Copyright James Dempsey, 2012
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
package pcgen.gui2;

import org.junit.jupiter.api.Test;
import pcgen.system.ConfigurationSettings;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The Class {@code ScanForUnusedIl8nKeysTest} checks for any unused keys in
 * the i18n properties. Currently, it is a utility class masquerading as a unit
 * test, but after completion of localization work, it will be used as a means of
 * verifying the properties files.
 */
class ScanForUnusedIl8nKeysTest
{
	private static final int EXPECTED_TOTAL_FILES_COUNT = 3000;
	private static final String CODE_PATH = "code/src/java/";
	private static final String RESOURCES_PATH = "code/src/resources/";
	private static final String TEST_RESOURCES_PATH = "code/src/testResources/";
	private static final String PROPERTIES_PATH = "pcgen/lang/";
	private static final String PROPERTIES_FILE = "LanguageBundle.properties";
	private static final String NEW_PROPERTIES_FILE = "cleaned.properties";
	private static final String UNUSED_PROPERTIES_FILE = "unused.properties";

	private static Logger log = Logger.getLogger("slowtest");

	@Test
	void scanForUnusedKeys() throws IOException
	{
		/*
		 * PCGenActionMap and PCGenAction dynamically construct keys. All keys starting with the pattern used in those
		 * classes will be deemed present and removed from the missing keys set.
		 */
		Predicate<String> whitelistedKey =
				key -> key.startsWith("in_mnu")
						|| key.startsWith("in_mn_mnu")
						|| key.startsWith("in_EqBuilder_")
						|| key.startsWith("PrerequisiteOperator.display");

		try (var reader = new FileInputStream(RESOURCES_PATH + PROPERTIES_PATH + PROPERTIES_FILE))
		{
			// Read in a bundle, grab all keys
			var props = new Properties();
			props.load(reader);

			Set<String> keys = props.keySet()
					.stream()
					.map(o -> (String) o)
					.filter(Predicate.not(whitelistedKey))
					.collect(Collectors.toCollection(HashSet::new));

			// Grab a list of files to be scanned
			List<File> fileList = buildFileList();

			// Scan each file marking each found entry
			Set<String> missingKeys = ConcurrentHashMap.newKeySet(keys.size());
			missingKeys.addAll(keys);

			fileList.parallelStream().forEach(file -> {
				var missingSet = scanFilesForMissingKeys(file, missingKeys);
				missingKeys.removeAll(missingSet);
			});

			// Report all missing entries
			log.info(() -> String.format(Locale.ENGLISH, "Total unused keys: %d from a set of %d defined keys: %.2f%%.",
					missingKeys.size(), keys.size(), missingKeys.size() * 100.0 / keys.size()));

			// Output a new set with all properties that are used within the project
			outputCleanedProperties(new File(RESOURCES_PATH + PROPERTIES_PATH + PROPERTIES_FILE),
					new File(TEST_RESOURCES_PATH + PROPERTIES_PATH + NEW_PROPERTIES_FILE), missingKeys);

			// Output a new set with properties (including comments), where keys are not used
			outputUnusedProperties(new File(RESOURCES_PATH + PROPERTIES_PATH + PROPERTIES_FILE),
					new File(TEST_RESOURCES_PATH + PROPERTIES_PATH + UNUSED_PROPERTIES_FILE), missingKeys);
		}
	}

	private static Set<String> scanFilesForMissingKeys(File file, Set<String> keys)
	{
		try (var reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8)))
		{
			var javaFile = reader.lines().collect(Collectors.joining("\n"));
			if (file.getName().endsWith(".java"))
			{
				return keys.stream()
						.filter(key -> javaFile.contains("\"%s\"".formatted(key)))
						.collect(Collectors.toSet());
			} else if (file.getName().endsWith(".fxml"))
			{
				return keys.stream()
						.filter(key -> javaFile.contains("%%%s".formatted(key)))
						.collect(Collectors.toSet());
			} else
			{
				log.warning(
						"Unknown file extension: %s. This file will be ignored, because only .java and .fxml are supported"
								.formatted(file.getAbsolutePath()));
				return Collections.emptySet();
			}


		} catch (IOException e)
		{
			log.log(Level.WARNING, "Couldn't process file: %s".formatted(file.getAbsolutePath()), e);
			return Collections.emptySet();
		}
	}

	/**
	 * @param inputPropsFile
	 * @param cleanPropsFile
	 * @param unusedKeys
	 * @throws IOException
	 */
	private static void outputCleanedProperties(File inputPropsFile, File cleanPropsFile,
												Collection<String> unusedKeys) throws IOException
	{
		try (var reader = new BufferedReader(new FileReader(inputPropsFile, StandardCharsets.UTF_8));
			 var writer = new BufferedWriter(new PrintWriter(cleanPropsFile, StandardCharsets.UTF_8)))
		{
			String result =
					reader.lines()
							.filter(line -> unusedKeys.stream().noneMatch(key -> line.startsWith(key + '=')))
							.collect(Collectors.joining("\n"));
			writer.write(result);
		}
	}

	/**
	 * @param inputPropsFile
	 * @param unusedPropsFile
	 * @param unusedKeys
	 * @throws IOException
	 */
	private static void outputUnusedProperties(File inputPropsFile, File unusedPropsFile,
											   Collection<String> unusedKeys) throws IOException
	{
		try (var reader = new BufferedReader(new FileReader(inputPropsFile, StandardCharsets.UTF_8));
			 var writer = new BufferedWriter(new PrintWriter(unusedPropsFile, StandardCharsets.UTF_8)))
		{
			String result = reader.lines()
					.filter(line -> {
						var trimmedLine = line.trim();
						return trimmedLine.startsWith("#")
								|| trimmedLine.isEmpty()
								|| unusedKeys.stream().anyMatch(key -> trimmedLine.startsWith(key + '='));
					}).collect(Collectors.joining("\n"))
					.replaceAll("(\n){3,}", "\n\n");
			writer.write(result);
		}
	}

	/**
	 * @return A list of files that will be scanned for i18n labels
	 * @throws IOException if an I/O error is thrown when accessing the starting file.
	 */
	private static List<File> buildFileList() throws IOException
	{
		List<File> allFiles = new ArrayList<>(EXPECTED_TOTAL_FILES_COUNT);
		log.info("Current working directory: " + ConfigurationSettings.getUserDir());

		try (Stream<Path> codeWalk = Files.walk(Paths.get(CODE_PATH));
			 Stream<Path> resourcesWalk = Files.walk(Paths.get(RESOURCES_PATH)))
		{
			List<File> javaFiles = codeWalk
					.filter(Files::isRegularFile)
					.filter(e -> e.toString().endsWith(".java"))
					.map(Path::toFile)
					.toList();
			allFiles.addAll(javaFiles);

			List<File> fxmlFiles = resourcesWalk
					.filter(Files::isRegularFile)
					.filter(e -> e.toString().endsWith(".fxml"))
					.map(Path::toFile)
					.toList();
			allFiles.addAll(fxmlFiles);
		}

		log.info("The size of found files is %d, that will be scanned.".formatted(allFiles.size()));
		log.fine(() -> {
			var firstTenFiles = allFiles.stream()
					.limit(10)
					.map(File::getPath)
					.collect(Collectors.joining("\n"));
			return "Top 10 files:\n" + firstTenFiles;
		});
		return allFiles;
	}
}
