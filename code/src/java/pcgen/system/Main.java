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

import static pcgen.system.ConfigurationSettings.SETTINGS_FILES_PATH;
import static pcgen.system.ConfigurationSettings.getPluginsDir;
import static pcgen.system.ConfigurationSettings.getSystemProperty;
import static pcgen.system.ConfigurationSettings.initSystemProperty;
import static pcgen.system.ConfigurationSettings.setSystemProperty;
import gmgen.pluginmgr.PluginManager;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;

import pcgen.cdom.base.Constants;
import pcgen.core.CustomData;
import pcgen.core.facade.UIDelegate;
import pcgen.core.prereq.PrerequisiteTestFactory;
import pcgen.gui.converter.TokenConverter;
import pcgen.gui.utils.DialogInputInterface;
import pcgen.gui.utils.SwingChooser;
import pcgen.gui.utils.SwingChooserRadio;
import pcgen.gui.utils.SwingChooserUserInput;
import pcgen.gui2.PCGenUIManager;
import pcgen.gui2.SplashScreen;
import pcgen.gui2.UIPropertyContext;
import pcgen.gui2.dialog.OptionsPathDialog;
import pcgen.gui2.plaf.LookAndFeelManager;
import pcgen.gui2.tools.Utility;
import pcgen.io.ExportHandler;
import pcgen.io.PCGFile;
import pcgen.persistence.CampaignFileLoader;
import pcgen.persistence.GameModeFileLoader;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.TokenStore;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriterFactory;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.rules.persistence.TokenLibrary;
import pcgen.util.InputFactory;
import pcgen.util.Logging;
import pcgen.util.PJEP;
import pcgen.util.chooser.ChooserFactory;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public final class Main
{

	private static PropertyContextFactory configFactory;
	private static boolean startGMGen = false;
	private static boolean startNPCGen = false;
	private static boolean startInSheet = false;
	private static boolean doExport = false;
	private static boolean ignoreJavaVer = false;
	private static String settingsDir = null;
	private static String campaignMode = null;
	private static String characterSheet = null;
	private static String exportSheet = null;
	private static String partyFile = null;
	private static String characterFile = null;
	private static String outputFile = null;

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
		return startInSheet;
	}

	public static String getStartupCampaign()
	{
		return campaignMode;
	}

	public static String getStartupCharacterSheet()
	{
		return characterSheet;
	}

	public static String getStartupPartyFile()
	{
		return partyFile;
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
		for (String key : keys)
		{
			pwriter.println(key + "=" + props.getProperty(key)); //$NON-NLS-1$
		}
		Logging.log(Level.CONFIG, writer.toString());
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args)
	{
		Logging.log(Level.INFO, "Starting PCGen v" + PCGenPropBundle.getVersionNumber()); //$NON-NLS-1$
		logSystemProps();
		configFactory = new PropertyContextFactory(SystemUtils.USER_DIR);
		configFactory.registerAndLoadPropertyContext(ConfigurationSettings.getInstance());

		parseCommands(args);
		validateCommands();
		
		if (!doExport)
		{
			startupWithGUI();
		}
		else
		{
			startupWithoutGUI();
			
			shutdown();
		}
	}

	public static void loadCharacterAndExport(String characterFile, String exportSheet, String outputFile, String configFile)
	{
		Main.characterFile = characterFile;
		Main.exportSheet = exportSheet;
		Main.outputFile = outputFile;
		//Main.settingsDir = settingsDir;

		configFactory = new PropertyContextFactory(SystemUtils.USER_DIR);
		configFactory.registerAndLoadPropertyContext(ConfigurationSettings.getInstance(configFile));
		
		startupWithoutGUI();
	}
	
	private static void parseCommands(String[] args)
	{
		int index = 0;
		while (index < args.length)
		{
			String arg = args[index];
			if (arg.equals("-V"))
			{
				//Print Version and exit
				Logging.log(Level.CONFIG, "PCGen v" + PCGenPropBundle.getVersionNumber());
				System.exit(0);
			}
			else if (arg.equals("-G"))
			{
				// Start in GMGen
				startGMGen = true;
			}
			else if (arg.equals("-N"))
			{
				// Start in NPC Generation mode
				startNPCGen = true;
			}
			else if (arg.equals("-v"))
			{
				// Verbose output
				Logging.setCurrentLoggingLevel(Logging.DEBUG);
			}
			else if (arg.equals("-s"))
			{
				// Specify the setting directory
				index++;
				if (index == args.length)
				{
					Logging.errorPrint("-s is missing argument");
					System.exit(1);
				}
				settingsDir = args[index];
			}
			else if (arg.equals("-m"))
			{
				// Specify the campaign mode
				index++;
				if (index == args.length)
				{
					Logging.errorPrint("-m is missing argument");
					System.exit(1);
				}
				campaignMode = args[index];
			}
			else if (arg.equals("-D"))
			{
				// Start showing the character sheet tab, optionally specifying the sheet to be used
				startInSheet = true;
				if (index + 1 < args.length && !args[index + 1].startsWith("-"))
				{
					characterSheet = args[index + 1];
					index++;
				}
			}
			else if (arg.equals("-E"))
			{
				// Specify the export sheet to be used
				doExport = true;
				if (index + 1 < args.length && !args[index + 1].startsWith("-"))
				{
					exportSheet = args[index + 1];
					index++;
				}
			}
			else if (arg.equals("-p"))
			{
				// Specify the party to be loaded
				index++;
				if (index == args.length)
				{
					Logging.errorPrint("-p is missing argument");
					System.exit(1);
				}
				partyFile = args[index];
			}
			else if (arg.equals("-c"))
			{
				// Specify the character to be loaded
				index++;
				if (index == args.length)
				{
					Logging.errorPrint("-c is missing argument");
					System.exit(1);
				}
				characterFile = args[index];
			}
			else if (arg.equals("-o"))
			{
				// Specify the output file
				index++;
				if (index == args.length)
				{
					Logging.errorPrint("-o is missing argument");
					System.exit(1);
				}
				outputFile  = args[index];
			}
			else if (arg.equals("-J"))
			{
				// Ignore Java version checks
				ignoreJavaVer = true;
			}
			else
			{
				//Unrecognized command argument
				Logging.errorPrint("Unrecognized argument: \"" + arg + "\"");
				System.exit(1);
			}
			index++;
		}
	}

	private static void validateCommands()
	{
		if (!(startGMGen ^ startNPCGen ^ startInSheet)
				^ ((startGMGen & startNPCGen & startInSheet)
				|| !(startGMGen | startNPCGen | startInSheet)))
		{
			Logging.errorPrint("Multiple startup arguments");
			System.exit(1);
		}
		if (settingsDir != null)
		{
			File file = new File(settingsDir);
			if (!file.isDirectory())
			{
				Logging.errorPrint("Invalid settings directory specified: " + file.getAbsolutePath());
				System.exit(1);
			}
		}
//		if (characterSheet != null && !new File(characterSheet).isFile())
//		{
//			Logging.errorPrint("Invalid characterSheet specified");
//			System.exit(1);
//		}
//		if (exportSheet != null && !new File(exportSheet).isFile())
//		{
//			System.exit(1);
//		}
		if (partyFile != null)
		{
			File file = new File(partyFile);
			if (!PCGFile.isPCGenPartyFile(file))
			{
				Logging.errorPrint("Invalid party file specified: " + file.getAbsolutePath());
				System.exit(1);
			}
		}
		if (characterFile != null)
		{
			File file = new File(characterFile);
			if (!PCGFile.isPCGenCharacterFile(file))
			{
				Logging.errorPrint("Invalid character file specified: " + file.getAbsolutePath());
				System.exit(1);
			}
		}
	}

	private static void startupWithGUI()
	{
		// configure the UI before any type of user prompting may take place
		configureUI();
		validateEnvironment(true);
		loadProperties(true);

		boolean showSplash = Boolean.parseBoolean(initSystemProperty("showSplash", "true"));
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
		if (StringUtils.isNotEmpty(language))
		{
			Locale.setDefault(new Locale(language, country));
		}
		LanguageBundle.init();
		LookAndFeelManager.initLookAndFeel();
		Utility.setApplicationTitle(Constants.APPLICATION_NAME);

		//TODO: Remove these registrations once the old choosers are fully replaced.
		ChooserFactory.setInterfaceClassname(SwingChooser.class.getName());
		ChooserFactory.setRadioInterfaceClassname(SwingChooserRadio.class.getName());
		ChooserFactory.setUserInputInterfaceClassname(SwingChooserUserInput.class.getName());
		InputFactory.setInterfaceClassname(DialogInputInterface.class
			.getName());

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
			if (majorVar < 1 || (majorVar == 1 && minorVar < 6))
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
			if (majorVar > 1 || (majorVar == 1 && minorVar > 7))
			{
				String message =
						"Java version "
							+ javaVerString
							+ " is newer than PCGen supports. The program may not\nwork correctly. Java versions up to 1.7 are supported.";
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
	}

	public static void loadProperties(boolean useGui)
	{
		if (settingsDir == null && getSystemProperty(SETTINGS_FILES_PATH) == null)
		{
			if (!useGui)
			{
				Logging.errorPrint("No settingsDir specified via -s in batch mode and no default exists.");
				System.exit(1);
			}
			String filePath = OptionsPathDialog.promptSettingsPath();
			setSystemProperty(SETTINGS_FILES_PATH, filePath);
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
	 * @return The task to load plugins.
	 */
	public static PCGenTask createLoadPluginTask()
	{
		String pluginsDir = getPluginsDir();
		PluginClassLoader loader = new PluginClassLoader(new File(pluginsDir));
		loader.addPluginLoader(TokenLibrary.getInstance());
		loader.addPluginLoader(TokenStore.inst());
		try
		{
			loader.addPluginLoader(PreParserFactory.getInstance());
		}
		catch (PersistenceLayerException ex)
		{
			Logging.errorPrint("createLoadPluginTask failed", ex);
		}
		loader.addPluginLoader(PrerequisiteTestFactory.getInstance());
		loader.addPluginLoader(PrerequisiteWriterFactory.getInstance());
		loader.addPluginLoader(PJEP.getJepPluginLoader());
		loader.addPluginLoader(ExportHandler.getPluginLoader());
		loader.addPluginLoader(TokenConverter.getPluginLoader());
		loader.addPluginLoader(PluginManager.getInstance());
		return loader;
	}


	private static void startupWithoutGUI()
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
		
		if (partyFile != null)
		{
			exporter.exportParty(partyFile, outputFile);
		}

		if (characterFile != null)
		{
			exporter.exportCharacter(characterFile, outputFile);
		}
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

}
