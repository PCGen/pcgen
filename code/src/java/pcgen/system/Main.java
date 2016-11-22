/*
 * Main.java
 * Copyright 2009 Connor Petty <cpmeister@users.sourceforge.net>
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
 * 
 * Created on Sep 1, 2009, 6:17:59 PM
 */
package pcgen.system;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import pcgen.cdom.base.Constants;
import pcgen.cdom.formula.PluginFunctionLibrary;
import pcgen.core.CustomData;
import pcgen.core.prereq.PrerequisiteTestFactory;
import pcgen.facade.core.UIDelegate;
import pcgen.gui2.PCGenUIManager;
import pcgen.gui2.SplashScreen;
import pcgen.gui2.UIPropertyContext;
import pcgen.gui2.converter.TokenConverter;
import pcgen.gui2.dialog.OptionsPathDialog;
import pcgen.gui2.plaf.LookAndFeelManager;
import pcgen.gui2.tools.Utility;
import pcgen.io.ExportHandler;
import pcgen.persistence.CampaignFileLoader;
import pcgen.persistence.GameModeFileLoader;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.TokenStore;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriterFactory;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.pluginmgr.PluginManager;
import pcgen.rules.persistence.TokenLibrary;
import pcgen.util.Logging;
import pcgen.util.PJEP;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.MutuallyExclusiveGroup;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * Main entry point for pcgen.
 */
public final class Main
{

	private static PropertyContextFactory configFactory;
	private static boolean startGMGen;
	private static boolean startNPCGen;
	private static boolean ignoreJavaVer;
	private static String settingsDir;
	private static String campaignMode;
	private static String characterSheet;
	private static String exportSheet;
	private static String partyFile;
	private static String characterFile;
	private static String outputFile;
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);


	private Main()
	{
	}

	public static boolean shouldStartInGMGen()
	{
		return startGMGen;
	}

	public static boolean shouldStartInNPCGen()
	{
		return startNPCGen;
	}

	public static boolean shouldStartInCharacterSheet()
	{
		return characterSheet != null;
	}

	public static String getStartupCampaign()
	{
		return campaignMode;
	}

	public static String getStartupCharacterFile()
	{
		return characterFile;
	}

	private static void logSystemProps()
	{
		Properties props = System.getProperties();
		StringWriter writer = new StringWriter();
		PrintWriter pwriter = new PrintWriter(writer);
		pwriter.println();
		pwriter.println("-- listing properties --"); //$NON-NLS-1$
		// Manually output the property values to avoid them being cut off at 40 characters
		Set<String> keys = props.stringPropertyNames();
		//$NON-NLS-1$
		keys.forEach(key ->
		{
			pwriter.println(key + "=" + props.getProperty(key)); //$NON-NLS-1$
		});
		Logging.log(Level.CONFIG, writer.toString());
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args)
	{
		Marker versionMarker = MarkerFactory.getMarker("VERSION");
		LOGGER.info(versionMarker, "Starting PCGen v {} {}",
				PCGenPropBundle.getVersionNumber(),
				PCGenPropBundle.getAutobuildString());

		Thread.setDefaultUncaughtExceptionHandler(new PCGenUncaughtExceptionHandler());
		logSystemProps();
		configFactory = new PropertyContextFactory(getConfigPath());
		configFactory.registerAndLoadPropertyContext(ConfigurationSettings.getInstance());

		parseCommands(args);

		if (exportSheet == null)
		{
			startupWithGUI();
		}
		else
		{
			startupWithoutGUI();
			shutdown();
		}
	}

	private static String getConfigPath()
	{
		//TODO: convert to a proper command line argument instead of a -D java property
		// First see if it was specified on the command line
		String aPath = System.getProperty("pcgen.config"); //$NON-NLS-1$
		if (aPath != null)
		{
			File testPath = new File(aPath);
			// Then make sure it's an existing folder
			if (testPath.exists() && testPath.isDirectory())
			{
				return aPath;
			}
		}
		// Otherwise return user dir
		return SystemUtils.USER_DIR;
	}

	public static boolean loadCharacterAndExport(String characterFile, String exportSheet, String outputFile, String configFile)
	{
		Main.characterFile = characterFile;
		Main.exportSheet = exportSheet;
		Main.outputFile = outputFile;

		configFactory = new PropertyContextFactory(SystemUtils.USER_DIR);
		configFactory.registerAndLoadPropertyContext(ConfigurationSettings.getInstance(configFile));
		return startupWithoutGUI();
	}

	/**
	 * Initialize Main - must be called before any other getter can be used.
	 *
	 * @param argv the command line arguments to be parsed
	 */
	private static void parseCommands(String[] argv)
	{
		Namespace args = getParser().parseArgsOrFail(argv);

		if (args.getInt("verbose") > 0)
		{

			Logging.setCurrentLoggingLevel(Logging.DEBUG);
		}

		startGMGen = args.getBoolean("gmgen");
		startNPCGen = args.getBoolean("npc");
		ignoreJavaVer = args.getBoolean("J");
		settingsDir = args.getString("settingsdir");
		campaignMode = args.getString("campaignmode");
		characterSheet = args.get("D");
		exportSheet = args.get("E");
		partyFile = args.get("p");
		characterFile = args.get("c");
		outputFile = args.get("o");
	}

	private static void startupWithGUI()
	{
		// configure the UI before any type of user prompting may take place
		configureUI();
		validateEnvironment(true);
		loadProperties(true);
		initPrintPreviewFonts();

		boolean showSplash = Boolean.parseBoolean(ConfigurationSettings.initSystemProperty("showSplash", "true"));
		//TODO: allow commandline override of spash property
		SplashScreen splash = null;
		if (showSplash)
		{
			splash = new SplashScreen();
			splash.setVisible(true);
		}
		PCGenTaskExecutor executor = new PCGenTaskExecutor();
		executor.addPCGenTask(createLoadPluginTask());
		executor.addPCGenTask(new GameModeFileLoader());
		executor.addPCGenTask(new CampaignFileLoader());
		if (splash != null)
		{
			executor.addPCGenTaskListener(splash);
		}
		executor.execute();
		if (splash != null)
		{
			splash.setMessage(LanguageBundle.getString("in_taskInitUi")); //$NON-NLS-1$
		}
		FacadeFactory.initialize();
		PCGenUIManager.initializeGUI();
		if (splash != null)
		{
			splash.dispose();
		}
		PCGenUIManager.startGUI();
	}

	private static void configureUI()
	{
		String language = ConfigurationSettings.getLanguage();
		String country = ConfigurationSettings.getCountry();
		if (StringUtils.isNotEmpty(language) && StringUtils.isNotEmpty(country))
		{
			Locale.setDefault(new Locale(language, country));
		}
		LanguageBundle.init();
		LookAndFeelManager.initLookAndFeel();
		Utility.setApplicationTitle(Constants.APPLICATION_NAME);
	}

	/**
	 * Check that the runtime environment is suitable for PCGen to run.
	 * e.g. correct Java version
	 */
	private static void validateEnvironment(boolean useGui)
	{
		String javaVerString = System.getProperty("java.version");
		String[] javaVer = javaVerString.split("\\.");
		int majorVar = Integer.parseInt(javaVer[0]);
		int minorVar = Integer.parseInt(javaVer[1]);
		if (!ignoreJavaVer)
		{
			if ((majorVar < 1) || ((majorVar == 1) && (minorVar < 6)))
			{
				String message =
						"Java version "
								+ javaVerString
								+ " is too old. PCGen requires at least Java 1.6 to run.";
				Logging.errorPrint(message);
				if (useGui)
				{
					JOptionPane.showMessageDialog(null, message,
							Constants.APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);
				}
				System.exit(1);
			}
			if ((majorVar > 1) || ((majorVar == 1) && (minorVar > 8)))
			{
				String message =
						"Java version "
								+ javaVerString
								+ " is newer than PCGen supports. The program may not\n"
								+ "work correctly. Java versions up to 1.8 are supported.";
				Logging.errorPrint(message);
				if (useGui)
				{
					int result =
							JOptionPane.showConfirmDialog(null, message
											+ "\n\nDo you wish to continue?",
									Constants.APPLICATION_NAME,
									JOptionPane.OK_CANCEL_OPTION);
					if (result != JOptionPane.OK_OPTION)
					{
						System.exit(1);
					}
				}
			}
		}

		// Check our main folders are present
		String[] neededDirs =
				{ConfigurationSettings.getSystemsDir(),
						ConfigurationSettings.getPccFilesDir(),
						ConfigurationSettings.getPluginsDir(),
						ConfigurationSettings.getPreviewDir(),
						ConfigurationSettings.getOutputSheetsDir()};
		StringBuilder missingDirs = new StringBuilder();
		for (final String dirPath : neededDirs)
		{
			File dir = new File(dirPath);
			if (!dir.exists())
			{
				String path = dirPath;
				try
				{
					path = dir.getCanonicalPath();
				}
				catch (IOException e)
				{
					Logging.errorPrint("Unable to find canonical path for "
							+ dir);
				}
				missingDirs.append("  ").append(path).append("\n");
			}
		}
		if (missingDirs.length() > 0)
		{
			String message;
			message =
					"This installation of PCGen is missing the following required folders:\n"
							+ missingDirs;
			Logging.errorPrint(message);
			if (useGui)
			{
				JOptionPane.showMessageDialog(null, message
								+ "\nPlease reinstall PCGen.", Constants.APPLICATION_NAME,
						JOptionPane.ERROR_MESSAGE);
			}
			System.exit(1);
		}
	}

	public static void loadProperties(boolean useGui)
	{
		if ((settingsDir == null) && (
				ConfigurationSettings.getSystemProperty(ConfigurationSettings.SETTINGS_FILES_PATH) == null
		))
		{
			if (!useGui)
			{
				Logging.errorPrint("No settingsDir specified via -s in batch mode and no default exists.");
				System.exit(1);
			}
			String filePath = OptionsPathDialog.promptSettingsPath();
			ConfigurationSettings.setSystemProperty(ConfigurationSettings.SETTINGS_FILES_PATH, filePath);
		}
		PropertyContextFactory.setDefaultFactory(settingsDir);

		//Existing PropertyContexts are registered here
		PropertyContextFactory defaultFactory = PropertyContextFactory.getDefaultFactory();
		defaultFactory.registerPropertyContext(PCGenSettings.getInstance());
		defaultFactory.registerPropertyContext(UIPropertyContext.getInstance());
		defaultFactory.registerPropertyContext(LegacySettings.getInstance());
		defaultFactory.loadPropertyContexts();
	}

	/**
	 * Create a task to load all system plugins.
	 *
	 * @return The task to load plugins.
	 */
	public static PCGenTask createLoadPluginTask()
	{
		String pluginsDir = ConfigurationSettings.getPluginsDir();
		PluginClassLoader loader = new PluginClassLoader(new File(pluginsDir));
		loader.addPluginLoader(TokenLibrary.getInstance());
		loader.addPluginLoader(TokenStore.inst());
		try
		{
			loader.addPluginLoader(PreParserFactory.getInstance());
		} catch (PersistenceLayerException ex)
		{
			Logging.errorPrint("createLoadPluginTask failed", ex);
		}
		loader.addPluginLoader(PrerequisiteTestFactory.getInstance());
		loader.addPluginLoader(PrerequisiteWriterFactory.getInstance());
		loader.addPluginLoader(PJEP.getJepPluginLoader());
		loader.addPluginLoader(ExportHandler.getPluginLoader());
		loader.addPluginLoader(TokenConverter.getPluginLoader());
		loader.addPluginLoader(PluginManager.getInstance());
		loader.addPluginLoader(PluginFunctionLibrary.getInstance());
		return loader;
	}


	private static boolean startupWithoutGUI()
	{
		loadProperties(false);
		validateEnvironment(false);

		PCGenTaskExecutor executor = new PCGenTaskExecutor();
		executor.addPCGenTask(createLoadPluginTask());
		executor.addPCGenTask(new GameModeFileLoader());
		executor.addPCGenTask(new CampaignFileLoader());
		executor.execute();

		UIDelegate uiDelegate = new ConsoleUIDelegate();

		BatchExporter exporter = new BatchExporter(exportSheet, uiDelegate);

		boolean result = true;
		if (partyFile != null)
		{
			result = exporter.exportParty(partyFile, outputFile);
		}

		if (characterFile != null)
		{
			result = exporter.exportCharacter(characterFile, outputFile);
		}

		return result;
	}

	public static void shutdown()
	{
		configFactory.savePropertyContexts();
		BatchExporter.removeTemporaryFiles();
		PropertyContextFactory.getDefaultFactory().savePropertyContexts();

		// Need to (possibly) write customEquipment.lst
		if (PCGenSettings.OPTIONS_CONTEXT
				.getBoolean(PCGenSettings.OPTION_SAVE_CUSTOM_EQUIPMENT))
		{
			CustomData.writeCustomItems();
		}

		System.exit(0);
	}

	private static void initPrintPreviewFonts()
	{
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		String fontDir = ConfigurationSettings.getOutputSheetsDir() + File.separator
				+ "fonts" + File.separator + "NotoSans" + File.separator;
		try
		{
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File(fontDir + "NotoSans-Regular.ttf")));
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File(fontDir + "NotoSans-Bold.ttf")));
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File(fontDir + "NotoSans-Italic.ttf")));
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File(fontDir + "NotoSans-BoldItalic.ttf")));
		}
		catch (IOException | FontFormatException ex)
		{
			Logging.errorPrint("Unexpected exception loading fonts fo print p", ex);
		}
	}

	/**
	 * @return an ArgumentParser used to peform argument parsing
	 */
	private static ArgumentParser getParser()
	{
		ArgumentParser parser = ArgumentParsers
				.newArgumentParser(Constants.APPLICATION_NAME)
				.defaultHelp(false)
				.description("RPG Character Generator")
				.version(PCGenPropBundle.getVersionNumber());

		parser.addArgument("-v", "--verbose")
				.help("verbose logging")
				.type(Boolean.class)
				.action(Arguments.count());

		parser.addArgument("-V", "--version")
				.action(Arguments.version());

		parser.addArgument("-J")
				.help("ignore java version checks")
				.action(Arguments.storeTrue());

		MutuallyExclusiveGroup startupMode = parser
				.addMutuallyExclusiveGroup()
				.description("start up on a specific mode");

		startupMode.addArgument("-G", "--gmgen")
				.help("GMGen mode")
				.type(Boolean.class)
				.action(Arguments.storeTrue());

		startupMode.addArgument("-N", "--npc")
				.help("NPC generation mode")
				.type(Boolean.class)
				.action(Arguments.storeTrue());

		startupMode.addArgument("-D", "--tab").nargs(1);

		parser.addArgument("-s", "--settingsdir")
				.nargs(1)
				.type(
						Arguments.fileType()
								.verifyIsDirectory()
								.verifyCanRead()
								.verifyExists()
				);
		parser.addArgument("-m", "--campaignmode")
				.nargs(1)
				.type(String.class)
		;
		parser.addArgument("-E", "--exportsheet")
				.nargs(1)
				.type(
						Arguments.fileType()
								.verifyCanRead()
								.verifyExists()
								.verifyIsFile()
				);

		parser.addArgument("-o", "--outputfile")
				.nargs(1)
				.type(
						Arguments.fileType()
								.verifyCanCreate()
								.verifyCanWrite()
								.verifyNotExists()
				);

		parser.addArgument("-c", "--character")
				.nargs(1)
				.type(
						Arguments.fileType()
								.verifyCanRead()
								.verifyExists()
								.verifyIsFile()
				);

		parser.addArgument("-p", "--party")
				.nargs(1)
				.type(
						Arguments.fileType()
								.verifyCanRead()
								.verifyExists()
								.verifyIsFile()
				);

		return parser;
	}

	/**
	 * The Class {@code PCGenUncaughtExceptionHandler} reports any
	 * exceptions that are not otherwise handled by the program.
	 */
	private static class PCGenUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler
	{
		@Override
		public void uncaughtException(Thread t, Throwable e)
		{
			Logging.errorPrint("Uncaught error - ignoring", e);
		}
	}
}
