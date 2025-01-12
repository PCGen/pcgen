/*
 * Copyright 2009 Connor Petty <cpmeister@users.sourceforge.net>
 * Copyright 2019 Timothy Reaves <treaves@silverfieldstech.com>
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
package pcgen.system;

import java.awt.Component;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import pcgen.cdom.base.Constants;
import pcgen.cdom.formula.PluginFunctionLibrary;
import pcgen.core.CustomData;
import pcgen.core.prereq.PrerequisiteTestFactory;
import pcgen.facade.core.UIDelegate;
import pcgen.gui2.PCGenUIManager;
import pcgen.gui2.UIPropertyContext;
import pcgen.gui2.converter.TokenConverter;
import pcgen.gui2.dialog.RandomNameDialog;
import pcgen.gui3.JFXPanelFromResource;
import pcgen.gui3.dialog.OptionsPathDialogController;
import pcgen.gui3.preloader.PCGenPreloader;
import pcgen.io.ExportHandler;
import pcgen.persistence.CampaignFileLoader;
import pcgen.persistence.GameModeFileLoader;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.TokenStore;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriterFactory;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.pluginmgr.PluginManager;
import pcgen.rules.persistence.TokenLibrary;
import pcgen.system.application.DeadlockDetectorTask;
import pcgen.system.application.LoggingUncaughtExceptionHandler;
import pcgen.system.application.PCGenLoggingDeadlockHandler;
import pcgen.util.GracefulExit;
import pcgen.util.Logging;
import pcgen.util.PJEP;

import javafx.embed.swing.JFXPanel;

/**
 * Main entry point for pcgen.
 */
public final class Main
{

	private static PropertyContextFactory configFactory;
	private static CommandLineArguments commandLineArguments = new CommandLineArguments(new String[0]);

	private Main()
	{
	}

	public static boolean shouldStartInCharacterSheet()
	{
		return commandLineArguments.getCharacterFile().isPresent();
	}

	public static Optional<String> getStartupCampaign()
	{
		return commandLineArguments.getCampaignMode();
	}

	public static Optional<File> getStartupCharacterFile()
	{
		return commandLineArguments.getCharacterFile();
	}

	private static void logSystemProps()
	{
		StringBuilder builder = new StringBuilder(System.lineSeparator() + "-- listing properties --");
		System.getProperties().forEach((key, value) -> builder.append(System.lineSeparator()).append(key).append("=").append(value));
		Logging.log(Level.CONFIG, builder.toString());
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String... args)
	{
		Logging.log(Level.INFO, "Starting PCGen v" + PCGenPropBundle.getVersionNumber() //$NON-NLS-1$
			+ PCGenPropBundle.getAutobuildString());

		Thread.setDefaultUncaughtExceptionHandler(new LoggingUncaughtExceptionHandler());
		DeadlockDetectorTask deadlockDetectorTask = new DeadlockDetectorTask(new PCGenLoggingDeadlockHandler());
		deadlockDetectorTask.initialize();

		logSystemProps();

		commandLineArguments = parseCommands(args);

		configFactory = new PropertyContextFactory(getConfigPath());
		if (commandLineArguments.getConfigFileName().isPresent())
		{
			configFactory.registerAndLoadPropertyContext(
					ConfigurationSettings.getInstance(commandLineArguments.getConfigFileName().get()));
		}
		else
		{
			configFactory.registerAndLoadPropertyContext(ConfigurationSettings.getInstance());
		}

		if (commandLineArguments.isStartNameGenerator())
		{
			Component dialog = new RandomNameDialog(null, null);
			dialog.setVisible(true);
			GracefulExit.exit(0);
		}

		if (commandLineArguments.getExportSheet().isEmpty())
		{
			startupWithGUI();
		}
		else
		{
			shutdown(startupWithoutGUI());
		}
	}

	private static String getConfigPath()
	{
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
		// Otherwise return command line argument for the settings directory
		// Otherwise return user dir
		return commandLineArguments.getSettingsDir()
				.map(File::getPath)
				.orElse(SystemUtils.USER_DIR);
	}

	/**
	 * Initialize Main - must be called before any other getter can be used.
	 *
	 * @param argv the command line arguments to be parsed
	 */
	private static CommandLineArguments parseCommands(String[] argv)
	{
		CommandLineArguments result = new CommandLineArguments(argv);

		if (result.isVerbose())
		{
			Logging.setCurrentLoggingLevel(Logging.DEBUG);
		}

		return result;
	}

	private static void startupWithGUI()
	{
		// configure the UI before any type of user prompting may take place
		configureUI();
		validateEnvironment(true);
		loadProperties(true);
		initPrintPreviewFonts();

		new JFXPanel();

		PCGenPreloader splash = new PCGenPreloader();
		PCGenTaskExecutor executor = new PCGenTaskExecutor();
		executor.addPCGenTask(createLoadPluginTask());
		executor.addPCGenTask(new GameModeFileLoader());
		executor.addPCGenTask(new CampaignFileLoader());
		executor.addPCGenTaskListener(splash);
		executor.run();
		splash.getController().setProgress(LanguageBundle.getString("in_taskInitUi"), 1.0d);
		FacadeFactory.initialize();
		PCGenUIManager.initializeGUI();
		splash.done();
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
	}

	/**
	 * Check that the runtime environment is suitable for PCGen to run.
	 */
	private static void validateEnvironment(boolean useGui)
	{
		// Check our main folders are present
		String[] neededDirs = {
				ConfigurationSettings.getSystemsDir(), ConfigurationSettings.getPccFilesDir(),
				ConfigurationSettings.getPluginsDir(), ConfigurationSettings.getPreviewDir(),
				ConfigurationSettings.getOutputSheetsDir()
		};
		String missingDirs = Arrays.stream(neededDirs)
				.map(File::new)
				.filter(Predicate.not(File::exists))
				.map(dir -> {
					try
					{
						return dir.getCanonicalPath();
					}
					catch (IOException e)
					{
						Logging.errorPrint("Unable to find canonical path for " + dir);
						return dir.getPath();
					}
				})
				.map(path -> "  " + path)
				.collect(Collectors.joining("\n"));

		if (!missingDirs.isEmpty())
		{
			String message = "This installation of PCGen is missing the following required folders:\n" + missingDirs;
			Logging.errorPrint(message);
			if (useGui)
			{
				JOptionPane.showMessageDialog(null, message + "\nPlease reinstall PCGen.", Constants.APPLICATION_NAME,
					JOptionPane.ERROR_MESSAGE);
			}
			System.exit(1);
		}
	}

	public static void loadProperties(boolean useGui)
	{
		if (commandLineArguments.getSettingsDir().isEmpty()
				&& (ConfigurationSettings.getSystemProperty(ConfigurationSettings.SETTINGS_FILES_PATH) == null))
		{
			if (!useGui)
			{
				Logging.errorPrint("No settingsDir specified via -s in batch mode and no default exists.");
				System.exit(1);
			}
			var panel = new JFXPanelFromResource<>(
					OptionsPathDialogController.class,
					"OptionsPathDialog.fxml"
			);
			panel.showAndBlock("Directory for options.ini location");
		}
		PropertyContextFactory.setDefaultFactory(commandLineArguments.getSettingsDir().map(File::getPath).orElse(null));

		//Existing PropertyContexts are registered here
		PropertyContextFactory defaultFactory = PropertyContextFactory.getDefaultFactory();
		PropertyContext settingscontext = PCGenSettings.getInstance();
		defaultFactory.registerPropertyContext(settingscontext);
		defaultFactory.registerPropertyContext(UIPropertyContext.getInstance());
		defaultFactory.registerPropertyContext(LegacySettings.getInstance());
		defaultFactory.loadPropertyContexts();
		//Make savepath directory if it doesn't exist
		String savepath = settingscontext.getProperty(PCGenSettings.PCG_SAVE_PATH);
		File savepath_dir = new File(savepath);
		if (!savepath_dir.exists() && !savepath_dir.isDirectory())
		{
			try
			{
				Logging.log(Level.INFO, "Making directory " + savepath_dir);
				savepath_dir.mkdir();
			}
			catch (Exception e)
			{
				Logging.log(Level.SEVERE, "Unable to create PCG_SAVE_PATH " + savepath_dir + ": " + e );
			}
		}
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
		executor.run();

		UIDelegate uiDelegate = new ConsoleUIDelegate();

		BatchExporter exporter = new BatchExporter(commandLineArguments.getExportSheet().map(File::getPath).orElse(null), uiDelegate);

		boolean result = true;
		if (commandLineArguments.getPartyFile().isPresent())
		{
			result = exporter.exportParty(commandLineArguments.getPartyFile().map(File::getPath).orElseThrow(),
					commandLineArguments.getOutputFile().map(File::getPath).orElse(null));
		}

		if (commandLineArguments.getCharacterFile().isPresent())
		{
			result = exporter.exportCharacter(commandLineArguments.getCharacterFile().map(File::getPath).orElseThrow(),
					commandLineArguments.getOutputFile().map(File::getPath).orElse(null));
		}

		return result;
	}

	public static void shutdown(boolean success)
	{
		configFactory.savePropertyContexts();
		BatchExporter.removeTemporaryFiles();
		PropertyContextFactory.getDefaultFactory().savePropertyContexts();

		// Need to (possibly) write customEquipment.lst
		if (PCGenSettings.OPTIONS_CONTEXT.getBoolean(PCGenSettings.OPTION_SAVE_CUSTOM_EQUIPMENT))
		{
			CustomData.writeCustomItems();
		}

		GracefulExit.exit(success ? 0 : 1);
	}

	private static void initPrintPreviewFonts()
	{
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		String fontDir = ConfigurationSettings.getOutputSheetsDir() + File.separator + "fonts" + File.separator
			+ "NotoSans" + File.separator;
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
}
