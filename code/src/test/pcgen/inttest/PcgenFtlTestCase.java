/*
 * Copyright 2013 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
package pcgen.inttest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Locale;
import java.util.logging.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.Diff;
import pcgen.LocaleDependentTestCase;
import pcgen.system.Main;
import pcgen.util.TestHelper;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * PcgenFtlTestCase is a base class for tests which use the FreeMarker
 * base template to produce an XML output for a character and then compare that
 * output with the expected result.
 */
public abstract class PcgenFtlTestCase
{
	private static final Logger LOG = Logger.getLogger(PcgenFtlTestCase.class.getName());

	private static final String TEST_CONFIG_FILE = "config.ini.junit";

	@BeforeEach
	public void setUp()
	{
		LocaleDependentTestCase.before(Locale.US);
	}

	@AfterEach
	public void tearDown()
	{
		LocaleDependentTestCase.after();
	}

	/**
	 * Run the test.
	 *
	 * @param character The PC
	 * @param mode The game mode
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void runTest(String character, String mode) throws IOException
	{
		LOG.info("RUNTEST with the character: " + character + " and the game mode: " + mode);
		// Delete the old generated output for this test
		String characterFileName = character + ".xml";
		String characterPCFileName = character + ".pcg";
		var inputFolder = new File("code/testsuite/PCGfiles");
		var outputFolder = new File("code/testsuite/output");
		var csheetsFolder = new File("code/testsuite/csheets");
		outputFolder.mkdirs();

		var inputFile = new File(inputFolder, characterPCFileName);
		var outputFile = new File(outputFolder, characterFileName);
		var expectedFile = new File(csheetsFolder, characterFileName);

		String pccLoc = TestHelper.findDataFolder();

		/*
		 * Override the pcc location, game mode and several other properties in
		 * the options.ini file
		 */
		String configFolder = "testsuite";
		TestHelper.createDummySettingsFile(TEST_CONFIG_FILE, configFolder, pccLoc);

		// Fire off PCGen, which will produce an XML file
		//String characterFile = "code/testsuite/PCGfiles/" + character + Constants.EXTENSION_CHARACTER_FILE;

		//String outputFile = outputFile.getCanonicalPath();

		// The code below had to be commented out as in Java 21+ there's no more SecurityManager
		// At time of writing there is no replacement, see <a href="https://bugs.openjdk.org/browse/JDK-8199704">JDK-8199704</a>
		// The tests seem to pass regardless.
		/*
		Runnable revertSystemExitInterceptor = SystemExitInterceptor.startInterceptor();

		assertEquals(0,
				assertThrows(SystemExitInterceptor.SystemExitCalledException.class,
						() -> Main.main("--character", characterFile,
								"--exportsheet", "code/testsuite/base-xml.ftl",
								"--outputfile", outputFile,
								"--configfilename", TEST_CONFIG_FILE)
				).getStatusCode(),
				"Export of " + character + " failed.");

		revertSystemExitInterceptor.run();
		*/

		Main.main("--character", inputFile.getCanonicalPath(),
				"--exportsheet", "code/testsuite/base-xml.ftl",
				"--outputfile", outputFile.getCanonicalPath(),
				"--configfilename", TEST_CONFIG_FILE);

		// the XML of the expected result
		var expected = Files.readString(expectedFile.toPath());
		// the XML of the actual result
		var actual = Files.readString(outputFile.toPath());

		Diff myDiff = DiffBuilder.compare(Input.fromString(expected))
				.withTest(Input.fromString(actual))
				.build();

		assertFalse(myDiff.hasDifferences(), myDiff.toString());
	}
}
