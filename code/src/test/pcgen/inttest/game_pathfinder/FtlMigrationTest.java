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
package pcgen.inttest.game_pathfinder;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Ignore;
import org.junit.Test;

import pcgen.cdom.base.Constants;
import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.SourceSelectionFacade;
import pcgen.facade.core.UIDelegate;
import pcgen.io.PCGFile;
import pcgen.persistence.SourceFileLoader;
import pcgen.system.BatchExporter;
import pcgen.system.CharacterManager;
import pcgen.system.ConsoleUIDelegate;
import pcgen.util.Logging;
import pcgen.util.TestHelper;

/**
 * Tests loading and exporting a character in both the old sheet and the 
 * FreeMarker conversion of that sheet. Due to differences in white space and 
 * the time element in some sheets, it is not expected that this test will ever 
 * pass. Instead it provides a harness for generating output from both the old 
 * and new sheets that can then be compared to identify any significant 
 * differences.
 * <p>
 * To use, modify the two sheet constants to point to the sheet you are working on 
 * and then specify the character to be run in the testLegacyAndFtlGen method.
 * <p>
 * 
 */
@Ignore
@SuppressWarnings("nls")
public class FtlMigrationTest
{
	/** The name of the original, legacy sheet. */
	private static final String ORIG_SHEET = "outputsheets/d20/fantasy/htmlxml/csheet_fantasy_statblock_pathfinder.htm";
	/** The name of the FreeMarker conversion of the sheet. */
	private static final String FTL_SHEET = "outputsheets/d20/fantasy/htmlxml/csheet_fantasy_statblock_pathfinder.htm.ftl";
	/** The settings file we will generate and use. */
	private static final String TEST_CONFIG_FILE = "config.ini.junit";


	public FtlMigrationTest()
	{
	}

	/**
	 * Loads the character and outputs it using both the legacy and the 
	 * FreeMarker sheet.
	 * 
	 * @throws Exception If an error occurs.
	 */
	@Test
	public void testLegacyAndFtlGen() throws Exception
	{
		//runTest("PFRPGPaladin", "Pathfinder_RPG");
		//runTest("PFRPGRogue", "Pathfinder_RPG");
		//runTest("PFRPGCleric", "Pathfinder_RPG");
		runTest("pf_goldielocks", "Pathfinder_RPG");
		//runTest("msrd_Ilyana", "msrd");
		//runTest("Quasvin", "35e");
		//runTest("FigFae", "3e");
	}

	/**
	 * Run the test.
	 * @param character The PC
	 * @param mode The game mode
	 * @throws Exception
	 */
	public void runTest(String character, String mode) throws Exception
	{
		System.out.println("RUNTEST with the character: " + character
			+ " and the game mode: " + mode);
		// Delete the old generated output for this test 
		File outputFolder= new File("code/testsuite/output");
		outputFolder.mkdirs();

		File originalSheet = new File(ORIG_SHEET);
		File ftlSheet = new File(FTL_SHEET);
		assertTrue("Ouptut sheet " + originalSheet + " should exist", originalSheet.exists());
		assertTrue("Ouptut sheet " + ftlSheet + " should exist", ftlSheet.exists());

		String outputFileName = character + ".html";
		String outputFileNameFtl = character + "-new.html";
		File outputFileFileOrig = new File(outputFolder, outputFileName);
		File outputFileFileFtl = new File(outputFolder, outputFileNameFtl);
		outputFileFileOrig.delete();
		
		String configFolder = "testsuite";
		
		String pccLoc = TestHelper.findDataFolder();

		// The String holder for the XML of the expected result
		String expected;
		// The String holder for the XML of the actual result
		String actual;
		/* 
		 * Override the pcc location, game mode and several other properties in 
		 * the options.ini file
		 */
		try
		{
//			TestHelper.createDummySettingsFile(TEST_CONFIG_FILE, configFolder,
//				pccLoc);

			// Fire off PCGen, which will produce an XML file 
			String characterFile = "code/testsuite/PCGfiles/" + character
				+ Constants.EXTENSION_CHARACTER_FILE;

			// Initialise PCGen
			TestHelper.loadGameModes(TEST_CONFIG_FILE);
			CharacterFacade pc = loadCharacter(characterFile);

			Logging.log(Logging.INFO, "Output using template " + ftlSheet + " to " + outputFileFileFtl);
			assertTrue("FTL export failed.", BatchExporter.exportCharacterToNonPDF(pc,
				outputFileFileFtl, ftlSheet));

			Logging.log(Logging.INFO, "Output using template " + originalSheet + " to " + outputFileFileOrig);
			assertTrue("Legacy export failed", BatchExporter.exportCharacterToNonPDF(pc,
				outputFileFileOrig, originalSheet));

			// Read in the XML produced by the old sheet
			expected = readFile(outputFileFileOrig);
			// Read in the XML produced by the ftl sheet
			actual = readFile(outputFileFileFtl);
		}
		finally
		{
			new File(TEST_CONFIG_FILE).delete();
		}

		// Do the XML comparison
		XMLUnit.setIgnoreWhitespace(true);
		//assertXMLEqual(expected, actual);
	}

	

	/**
	 * @param characterFile
	 * @return
	 */
	private CharacterFacade loadCharacter(String characterFilename)
	{
		File file = new File(characterFilename);
		if (!PCGFile.isPCGenCharacterFile(file))
		{
			Logging.errorPrint("Invalid character file specified: "
				+ file.getAbsolutePath());
			return null;
		}

		// Load data
		UIDelegate uiDelegate = new ConsoleUIDelegate();
		SourceSelectionFacade sourcesForCharacter =
				CharacterManager.getRequiredSourcesForCharacter(file,
					uiDelegate);
		Logging.log(Logging.INFO, "Loading sources " + sourcesForCharacter.getCampaigns()
			+ " using game mode " + sourcesForCharacter.getGameMode());
		SourceFileLoader loader = new SourceFileLoader(sourcesForCharacter, uiDelegate);
		loader.execute();

		// Load character
		CharacterFacade character =
				CharacterManager.openCharacter(file, uiDelegate,
					loader.getDataSetFacade());
		return character;
	}

	/**
	 * Read the XML file and return it as a String.
	 * @param outputFile
	 * @return String
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private String readFile(File outputFile)
		throws UnsupportedEncodingException, FileNotFoundException, IOException
	{
		BufferedReader br =
				new BufferedReader(new InputStreamReader(new FileInputStream(
					outputFile), "UTF-8"));
		StringBuilder output = new StringBuilder();
		try {
			String line = br.readLine();
			while (line != null)
			{
				output.append(line).append("\n");
				line = br.readLine();
			}
		} catch (IOException e) {
			br.close();
			fail();
		}
		return output.toString();
	}
	
}
