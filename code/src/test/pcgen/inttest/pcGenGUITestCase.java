package pcgen.inttest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;

import pcgen.LocaleDependentTestCase;
import pcgen.cdom.base.Constants;
import pcgen.core.Globals;
import pcgen.persistence.PersistenceManager;
import pcgen.system.Main;
import pcgen.util.TestHelper;

/**
 * A pcGenGUITestCase is an XMLTestCase.  It is an abstract 
 * class that the pcGenGUI<x> series of tests extend.
 * 
 * The basic idea is to run a test by getting PCGen to use the 
 * PCG file in testsuite/PCGfiles to generate a XML representation 
 * and compare the expected XML in the testsuite/csheets folder to 
 * the generated XML in the testsuite/output folder.
 */
@SuppressWarnings("nls")
public abstract class pcGenGUITestCase extends XMLTestCase
{
	/** */
	private static final String TEST_CONFIG_FILE = "config.ini.junit";

	/**
	 * Constructor.
	 */
	public pcGenGUITestCase()
	{
		// Empty Constructor
	}

	/**
	 * Standard JUnit style constructor
	 * @param name
	 */
	public pcGenGUITestCase(String name)
	{
		super(name);
	}

	/**
	 * Set the JAXP factories to use the Xerces parser
	 * - declare to throw Exception as if this fails then 
	 * all the tests will fail, and JUnit copes with these.
	 * Exceptions for us.
	 * @throws Exception 
	 */
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		XMLUnit
			.setControlParser("org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");

		/* 
		 * This next line is strictly not required - if no test parser is
		 * explicitly specified then the same factory class will be used for
		 * both test and control
		 */
		XMLUnit
			.setTestParser("org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");

		XMLUnit
			.setSAXParserFactory("org.apache.xerces.jaxp.SAXParserFactoryImpl");
		XMLUnit
			.setTransformerFactory("org.apache.xalan.processor.TransformerFactoryImpl");
		LocaleDependentTestCase.before(Locale.US);
	}
	
	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
		LocaleDependentTestCase.after();
	}

	/**
	 * Run the test.
	 * @param character The PC
	 * @param mode The game mode
	 * @throws Exception
	 */
	public void runTest(String character, String mode) throws Exception
	{
		runTest(character, mode, false);
	}

	/**
	 * Run the test.
	 * @param character The PC
	 * @param mode The game mode
	 * @param runTwice Should we rerun the test after unloading sources
	 * @throws Exception If there is an error while exporting the data
	 */
	public void runTest(String character, String mode, boolean runTwice) throws Exception
	{
		System.out.println("RUNTEST with the character: " + character
			+ " and the game mode: " + mode);
		// Delete the old generated output for this test 
		File outputFolder= new File("code/testsuite/output");
		outputFolder.mkdirs();
		String outputFileName = character + ".xml";
		File outputFileFile = new File(outputFolder, outputFileName);
		outputFileFile.delete();
		String outputFile = outputFileFile.getCanonicalPath();
		
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
			TestHelper.createDummySettingsFile(TEST_CONFIG_FILE, configFolder,
				pccLoc);
					
//					new File(TEST_CONFIG_FILE);
//			BufferedWriter bw =
//					new BufferedWriter(new OutputStreamWriter(
//						new FileOutputStream(configFile), "UTF-8"));
//			bw.write("settingsPath=" + configFolder + "\r\n");
//			if (pccLoc != null)
//			{
//				System.out.println("Using PCC Location of '" + pccLoc + "'.");
//				bw.write("pccFilesPath=" + pccLoc + "\r\n");
//			}
//			bw.write("customPathr=testsuite\\\\customdata\r\n");
//			bw.close();

			// Fire off PCGen, which will produce an XML file 
			String characterFile = "code/testsuite/PCGfiles/" + character
				+ Constants.EXTENSION_CHARACTER_FILE;

			Main.loadCharacterAndExport(characterFile,
				getSheetName(), outputFile, TEST_CONFIG_FILE);

			// Optionally do a second run
			if (runTwice)
			{
				new File(outputFile).delete();
				Globals.emptyLists();
				PersistenceManager.getInstance().clear();
				Main.loadCharacterAndExport(characterFile,
					getSheetName(), outputFile, TEST_CONFIG_FILE);
			}
			
			// Read in the actual XML produced by PCGen
			actual = readFile(new File(outputFile));
			// Read in the expected XML
			expected =
					readFile(new File("code/testsuite/csheets/" + character
						+ ".xml"));
		}
		finally
		{
			new File(TEST_CONFIG_FILE).delete();
		}

		// Do the XML comparison
		assertXMLEqual(expected, actual);
	}

	/**
	 * @return
	 */
	protected String getSheetName()
	{
		return "code/testsuite/base.xml";
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