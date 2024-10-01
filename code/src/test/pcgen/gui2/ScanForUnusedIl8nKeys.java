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
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import pcgen.system.ConfigurationSettings;

/**
 * The Class {@code ScanForUnusedIl8nKeys} checks for any unused keys in
 * the i18n properties. Currently, it is a utility class masquerading as a unit
 * test, but after completion of localization work, it will be used as a means of
 * verifying the properties files.
 */
class ScanForUnusedIl8nKeys
{
	private static final String CODE_PATH = "code/src/java/";
	private static final String RESOURCES_PATH = "code/src/resources/";
	private static final String TEST_RESOURCES_PATH = "code/src/testResources/";
	private static final String PROPERTIES_PATH = "pcgen/lang/";
	private static final String PROPERTIES_FILE = "LanguageBundle.properties";
	private static final String NEW_PROPERTIES_FILE = "cleaned.properties";
	private static final String UNUSED_PROPERTIES_FILE = "unused.properties";

	private static Logger log = Logger.getLogger("slowtest");

	@Test
	void scanForUnusedKeys() throws Exception
	{
	/*
	 * PCGenActionMap and PCGenAction dynamically construct keys. All keys starting with the pattern used in those
	 * classes will be deemed present and removed from the missing keys set.
	 */
		Predicate<String> whitelistedKey = key -> key.startsWith("in_mnu")
				|| key.startsWith("in_mn_mnu")
				|| key.startsWith("in_EqBuilder_")
				|| key.startsWith("PrerequisiteOperator.display");

		// Read in a bundle, grab all keys
		Properties p = new Properties();
		p.load(new FileInputStream(RESOURCES_PATH + PROPERTIES_PATH + PROPERTIES_FILE));
		Set<String> keys =
				p.keySet().stream()
					.map(o -> (String) o)
					.filter(Predicate.not(whitelistedKey))
					.map(key -> '"' + key + '"')
					.collect(Collectors.toCollection(HashSet::new));

		// Grab a list of files to be scanned
		List<File> fileList = buildFileList();

		// Scan each file marking each found entry
		Set<String> missingKeys = ConcurrentHashMap.newKeySet(keys.size());
		missingKeys.addAll(keys);

		fileList.parallelStream()
			.forEach(file -> {
				var missingSet = scanJavaFileForMissingKeys(file, missingKeys);
				missingKeys.removeAll(missingSet);
			});

		// Report all missing entries
		log.info(() -> String.format("Total unused keys: %d from a set of %d defined keys: %.2f%%.", missingKeys.size(),
				keys.size(), missingKeys.size() * 100.0 / keys.size()));

		// Output a new set
		outputCleanedProperties(new File(RESOURCES_PATH + PROPERTIES_PATH
			+ PROPERTIES_FILE), new File(TEST_RESOURCES_PATH + PROPERTIES_PATH
			+ NEW_PROPERTIES_FILE), missingKeys);

		// Output the unused file
		outputUnusedProperties(new File(RESOURCES_PATH + PROPERTIES_PATH
			+ PROPERTIES_FILE), new File(TEST_RESOURCES_PATH + PROPERTIES_PATH
			+ UNUSED_PROPERTIES_FILE), missingKeys);
	}

	/**
	 * @param file
	 * @param missingKeys
	 * @throws IOException
	 */
	private static void scanJavaFileForKeys(File file, Collection<String> missingKeys) throws IOException
	{
		try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8), 10240))
		{
			reader
				.lines()
				.forEach(line -> missingKeys.removeIf(line::contains));
		}
	}

	private static Set<String> scanJavaFileForMissingKeys(File file, Set<String> keys)
	{
		try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8)))
		{
			var java = reader
					.lines().collect(Collectors.joining("\n"));
			return keys.stream()
					.filter(java::contains)
					.collect(Collectors.toSet());
		} catch (IOException e) {
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
			String result = reader
				.lines()
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
			String result = reader
					.lines()
					.filter(line -> {
						var trimmedLine = line.trim();
						return trimmedLine.startsWith("#")
								|| trimmedLine.isEmpty()
								|| unusedKeys.stream().noneMatch(key -> line.startsWith(key + '='));
					})
					.collect(Collectors.joining("\n"))
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
		List<File> allFiles = new ArrayList<>();
		log.info("Current working directory: " + ConfigurationSettings.getUserDir());

		try (Stream<Path> codeWalk = Files.walk(Paths.get(CODE_PATH));
			 Stream<Path> resourcesWalk = Files.walk(Paths.get(RESOURCES_PATH)))
		{
			List<File> collect = codeWalk
					.filter(Files::isRegularFile)
					.filter(e -> e.toString().endsWith(".java"))
					.map(Path::toFile)
					.toList();
			allFiles.addAll(collect);

			List<File> collect2 = resourcesWalk
					.filter(Files::isRegularFile)
					.filter(e -> e.toString().endsWith(".fxml"))
					.map(Path::toFile)
					.toList();
			allFiles.addAll(collect2);
		}

		log.info("The size of found files is %d".formatted(allFiles.size()));
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
