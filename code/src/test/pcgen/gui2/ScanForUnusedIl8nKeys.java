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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Ignore;
import org.junit.Test;

/**
 * The Class {@code ScanForUnusedIl8nKeys} check for any unused keys in
 * the il8n properties. Currently it is a utility class masquerading as a unit 
 * test but after completion of localisation work it will be used as means of 
 * verifying the properties files.  
 */

public class ScanForUnusedIl8nKeys
{

	private static final String CODE_PATH = "code/src/java/";
	private static final String PROPERTIES_PATH = "pcgen/resources/lang/";
	private static final String PROPERTIES_FILE = "LanguageBundle.properties";
	private static final String NEW_PROPERTIES_FILE = "cleaned.properties";
	private static final String UNUSED_PROPERTIES_FILE = "unused.properties";
	private static final String[] PACKAGES = {"pcgen/gui2",
		"pcgen/core", "pcgen/system", "gmgen", "plugin", "pcgen/io",
		"pcgen/persistence", "pcgen/cdom", "pcgen/rules/context", "pcgen/util", };
	
	@Ignore
	@Test
	public void scanForUnusedKeys() throws Exception
	{
		//Read in bundle, grab all keys
		Properties p = new Properties();
		p.load(new FileInputStream(CODE_PATH + PROPERTIES_PATH + PROPERTIES_FILE));
		Set<String> keys =
				p.entrySet().stream()
				 .map(e -> (String) e.getKey())
				 .collect(Collectors.toCollection(TreeSet::new));

		// Grab a list of files to be scanned
		List<File> fileList = buildFileList();
		
		// Scan each file marking each found entry
		Set<String> missingKeys = new TreeSet<>(keys);
		actionWhitelistedKeys(missingKeys);
		for (File file : fileList)
		{
			scanJavaFileForKeys(file, missingKeys);
		}
		
		// Report all missing entries
		// missingKeys.stream().map(key -> "Found unused key '" + key + "'.").forEach(System.out::println);
		System.out.println("Total unused keys: " + missingKeys.size()
			+ " from a set of " + keys.size() + " defined keys. "
			+ ((missingKeys.size() * 100.0) / keys.size()) + "%");

		// Output a new set
		outputCleanedProperties(new File(CODE_PATH + PROPERTIES_PATH
			+ PROPERTIES_FILE), new File(CODE_PATH + PROPERTIES_PATH
			+ NEW_PROPERTIES_FILE), missingKeys);

		// Output the unused file
		outputUnusedProperties(new File(CODE_PATH + PROPERTIES_PATH
			+ PROPERTIES_FILE), new File(CODE_PATH + PROPERTIES_PATH
			+ UNUSED_PROPERTIES_FILE), missingKeys);
	}

	/**
	 * PCGenActionMap and PCGenAction dynamically construct keys. All keys 
	 * starting with the pattern used in those classes will be deemed present
	 * and removed from the missing keys set. 
	 * 
	 * @param missingKeys The list of missing keys
	 */
	private static void actionWhitelistedKeys(Collection<String> missingKeys)
	{
		missingKeys.removeIf(key ->
				key.startsWith("in_mnu")
				|| key.startsWith("in_mn_mnu")
				|| key.startsWith("in_EqBuilder_")
				|| key.startsWith("PrerequisiteOperator.display")
		);
	}

	/**
	 * @param file
	 * @param missingKeys
	 * @throws IOException 
	 */
	private static void scanJavaFileForKeys(File file, Collection<String> missingKeys) throws IOException
	{
		List<String> lines;
		try (Reader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8)))
		{
			lines = IOUtils.readLines(reader);
		}
		for (String line : lines)
		{
			missingKeys.removeIf(key -> line.contains("\"" + key + "\""));
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
		Reader reader = new BufferedReader(new FileReader(inputPropsFile, StandardCharsets.UTF_8));
		List<String> lines = IOUtils.readLines(reader);
		reader.close();
		Writer writer = new BufferedWriter(new PrintWriter(cleanPropsFile, StandardCharsets.UTF_8));
		writer.write("# " + PROPERTIES_FILE
			+ " with all unused keys removed as at "
			+ DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.format(new Date())
			+ "\n");
		boolean lastLineBlank = false;
		for (String line : lines)
		{
			boolean found;
			if (lastLineBlank && line.trim().isEmpty())
			{
				continue;
			}
			found = unusedKeys.stream().anyMatch(key -> line.startsWith(key + '='));
			if (!found)
			{
				lastLineBlank = line.trim().isEmpty();
				if (!StringUtils.isAsciiPrintable(line))
				{
					System.out.println("Found a non adcii line " + line);
				}

				writer.write(line + "\n");
			}
		}
		writer.close();
	}

	/**
	 * @param inputPropsFile
	 * @param unusedPropsFile
	 * @param unusedKeys
	 * @throws IOException 
	 */
	private void outputUnusedProperties(File inputPropsFile, File unusedPropsFile,
	                                    Collection<String> unusedKeys) throws IOException
	{
		Reader reader = new BufferedReader(new FileReader(inputPropsFile, StandardCharsets.UTF_8));
		List<String> lines = IOUtils.readLines(reader);
		reader.close();
		Writer writer = new BufferedWriter(new FileWriter(unusedPropsFile, StandardCharsets.UTF_8));
		writer.write("# " + PROPERTIES_FILE
			+ " with all used keys removed as at "
			+ DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(new Date())
			+ "\n");
		boolean lastLineBlank = false;
		for (String line : lines)
		{
			boolean found;
			if (lastLineBlank && line.trim().isEmpty())
			{
				continue;
			}
			else if (line.trim().startsWith("#") || line.trim().isEmpty())
			{
				found = true;
			}
			else
			{
				found = unusedKeys.stream().anyMatch(key -> line.startsWith(key + "="));
			}
			if (found)
			{
				lastLineBlank = line.trim().isEmpty();
				writer.write(line + "\n");
			}
		}
		writer.close();
	}

	/**
	 * @return A file list
	 * @throws IOException 
	 */
	private static List<File> buildFileList() throws IOException
	{
		List<File> allFiles = new ArrayList<>();
		JavaFileLister lister = new JavaFileLister();
		
		for (String pkg : PACKAGES)
		{
			File folder = new File(CODE_PATH + pkg);
			allFiles.addAll(lister.getJavaFileList(folder));
		}
		return allFiles;
	}

	private static class JavaFileLister extends DirectoryWalker<File>
	{

		private List<File> getJavaFileList(File startDirectory) throws IOException
		{
			List<File> results = new ArrayList<>();
			walk(startDirectory, results);
			return results;
		}

        @Override
		protected void handleFile(File file, int depth, Collection<File> results)
		{
			if (file.getName().endsWith(".java"))
			{
				results.add(file);
			}
		}
	}
	
}
