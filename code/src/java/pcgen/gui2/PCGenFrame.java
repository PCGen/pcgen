/*
 * Copyright 2008 Connor Petty <cpmeister@users.sourceforge.net>
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
 */
package pcgen.gui2;

import static javax.swing.JOptionPane.CLOSED_OPTION;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Observer;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.logging.LogRecord;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

import pcgen.cdom.base.Constants;
import pcgen.core.Campaign;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.CharacterStubFacade;
import pcgen.facade.core.ChooserFacade;
import pcgen.facade.core.CompanionFacade;
import pcgen.facade.core.DataSetFacade;
import pcgen.facade.core.EquipmentBuilderFacade;
import pcgen.facade.core.PartyFacade;
import pcgen.facade.core.SourceSelectionFacade;
import pcgen.facade.core.SpellBuilderFacade;
import pcgen.facade.core.UIDelegate;
import pcgen.facade.util.DefaultReferenceFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.ReferenceFacade;
import pcgen.facade.util.event.ReferenceEvent;
import pcgen.facade.util.event.ReferenceListener;
import pcgen.gui2.dialog.ChooserDialog;
import pcgen.gui2.dialog.EquipCustomizerDialog;
import pcgen.gui2.dialog.PostLevelUpDialog;
import pcgen.gui2.dialog.RadioChooserDialog;
import pcgen.gui2.dialog.SpellChoiceDialog;
import pcgen.gui2.sources.SourceSelectionDialog;
import pcgen.gui2.tabs.InfoTabbedPane;
import pcgen.gui2.tools.CharacterSelectionListener;
import pcgen.gui2.tools.Icons;
import pcgen.gui2.tools.TipOfTheDayHandler;
import pcgen.gui2.util.ShowMessageGuiObserver;
import pcgen.gui3.GuiAssertions;
import pcgen.gui3.GuiUtility;
import pcgen.gui3.JFXPanelFromResource;
import pcgen.gui3.component.PCGenToolBar;
import pcgen.gui3.dialog.AboutDialog;
import pcgen.gui3.dialog.RememberingChoiceDialog;
import pcgen.gui3.dialog.TipOfTheDayController;
import pcgen.io.PCGFile;
import pcgen.persistence.SourceFileLoader;
import pcgen.system.CharacterManager;
import pcgen.system.ConfigurationSettings;
import pcgen.system.FacadeFactory;
import pcgen.system.LanguageBundle;
import pcgen.system.Main;
import pcgen.system.PCGenPropBundle;
import pcgen.system.PCGenSettings;
import pcgen.system.PropertyContext;
import pcgen.util.Logging;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.RandomChooser;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToolBar;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.commons.lang3.StringUtils;

/**
 * The main window for PCGen. In addition this class is responsible for providing 
 * global UI functions such as message dialogs. 
 */
public final class PCGenFrame extends JFrame implements UIDelegate, CharacterSelectionListener
{

	private final PCGenActionMap actionMap;
	private final CharacterTabs characterTabs;
	private final PCGenStatusBar statusBar;
	private final PCGenMenuBar pcGenMenuBar;

	/**
	 * The context indicating what items are currently loaded/being processed in the UI
	 */
	private final UIContext uiContext;

	private final DefaultReferenceFacade<SourceSelectionFacade> currentSourceSelection;
	private final DefaultReferenceFacade<CharacterFacade> currentCharacterRef;
	private final DefaultReferenceFacade<DataSetFacade> currentDataSetRef;
	private final FilenameListener filenameListener;
	private JDialog sourceSelectionDialog;
	private SourceLoadWorker sourceLoader;
	private String section15;
	private String lastCharacterPath;
	/**
	 * This is a bit of a hack until we're full on JavaFX for showing dialogs
	 */
	private Window javaFXStage;

	public PCGenFrame(UIContext uiContext)
	{
		this.uiContext = Objects.requireNonNull(uiContext);
		Globals.setRootFrame(this);
		this.currentSourceSelection = uiContext.getCurrentSourceSelectionRef();
		this.currentCharacterRef = new DefaultReferenceFacade<>();
		this.currentDataSetRef = new DefaultReferenceFacade<>();
		this.actionMap = new PCGenActionMap(this, uiContext);
		this.characterTabs = new CharacterTabs(this);
		this.statusBar = new PCGenStatusBar(this);
		this.filenameListener = new FilenameListener();
		Observer messageObserver = new ShowMessageGuiObserver(this);
		ShowMessageDelegate.getInstance().addObserver(messageObserver);
		ChooserFactory.setDelegate(this);
		this.pcGenMenuBar = new PCGenMenuBar(this, uiContext);
		initComponents();
		pack();
		initSettings();
		Platform.runLater(() ->
			javaFXStage = new Stage()
		);
	}

	private void initComponents()
	{
		setLayout(new BorderLayout());

		JComponent root = getRootPane();
		root.setActionMap(actionMap);
		root.setInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, createInputMap(actionMap));

		characterTabs.add(new InfoGuidePane(this, uiContext));

		setJMenuBar(pcGenMenuBar);
		PCGenToolBar pcGenToolBar = new PCGenToolBar(this);
		ToolBar toolBar = pcGenToolBar.buildMenu();
		JFXPanel wrappedToolBar = GuiUtility.wrapParentAsJFXPanel(toolBar);

		add(wrappedToolBar, BorderLayout.NORTH);
		add(characterTabs, BorderLayout.CENTER);
		add(statusBar, BorderLayout.SOUTH);
		updateTitle();
		setIconImage(Icons.PCGenApp.getImageIcon().getImage());
	}

	private void initSettings()
	{
		setSize(1060, 725); //this is the default frame dimensions

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{

			@Override
			public void windowClosing(WindowEvent e)
			{
				PCGenUIManager.closePCGen();
			}

		});
	}

	/**
	 * This is called after initialization and starts up the PCGenFrame
	 * by setting it visible and then performing other startup actions
	 * as the user's preferences dictate.
	 */
	void startPCGenFrame()
	{
		GuiAssertions.assertIsSwingThread();
		setVisible(true);
		new StartupWorker().start();
	}

	/**
	 * This thread does the work of starting up the UI.
	 * If command arguments are present it handles them, otherwise it starts up normally.
	 */
	private class StartupWorker extends Thread
	{

		@Override
		public void run()
		{
			try
			{
				boolean alternateStartup = false;
				alternateStartup |= maybeLoadCampaign();
				alternateStartup |= maybeLoadOrCreateCharacter();
				alternateStartup |= maybeAutoLoadSources();

				if (!alternateStartup)
				{
					//Do a default startup
					SwingUtilities.invokeLater(() -> {
						if (TipOfTheDayHandler.shouldShowTipOfTheDay())
						{
							showTipsOfTheDay();
						}

						if (!SourceSelectionDialog.skipSourceSelection())
						{
							showSourceSelectionDialog();
						}
					});
				}
			}
			catch (InterruptedException | InvocationTargetException ex)
			{
				Logging.errorPrint("Unexepected exception", ex);
			}
		}

		/**
		 * If the preference to auto load sources at start is set, find the 
		 * sources that were last loaded and load them now.
		 * @return true if the sources have been loaded, false if not.
		 * @throws InterruptedException If the load was interrupted.
		 */
		private boolean maybeAutoLoadSources() throws InterruptedException
		{
			boolean autoLoadSources =
					PCGenSettings.OPTIONS_CONTEXT.initBoolean(PCGenSettings.OPTION_AUTOLOAD_SOURCES_AT_START, false);
			if (autoLoadSources)
			{
				String gameModeName = PCGenSettings.getInstance().getProperty(PCGenSettings.LAST_LOADED_GAME);
				String sourcesNameString = PCGenSettings.getInstance().getProperty(PCGenSettings.LAST_LOADED_SOURCES);
				if (StringUtils.isEmpty(gameModeName) || StringUtils.isEmpty(sourcesNameString))
				{
					return false;
				}
				GameMode gameMode = null;
				for (GameMode facade : FacadeFactory.getGameModes())
				{
					if (gameModeName.equals(facade.toString()))
					{
						gameMode = facade;
						break;
					}

				}
				if (gameMode == null)
				{
					return false;
				}

				List<Campaign> campaigns = new ArrayList<>();
				String[] sourceNames = sourcesNameString.split("\\|"); //$NON-NLS-1$
				for (Campaign camp : FacadeFactory.getCampaigns())
				{
					for (String name : sourceNames)
					{
						if (name.equals(camp.toString()))
						{
							campaigns.add(camp);
							break;
						}
					}
				}

				SourceSelectionFacade selection = FacadeFactory.createSourceSelection(gameMode, campaigns);
				loadSourceSelection(selection);
				sourceLoader.join();
				return true;
			}

			return false;
		}

		private boolean maybeLoadCampaign() throws InterruptedException
		{
			String camp = Main.getStartupCampaign();
			if (camp != null)
			{
				SourceSelectionFacade selection = null;
				ListFacade<SourceSelectionFacade> sources = FacadeFactory.getSourceSelections();
				for (SourceSelectionFacade sourceSelectionFacade : sources)
				{
					if (sourceSelectionFacade.toString().equals(camp))
					{
						selection = sourceSelectionFacade;
						break;
					}
				}
				if (selection != null)
				{
					loadSourceSelection(selection);
					sourceLoader.join();
					return true;
				}
				else
				{
					//did not find source
					Logging.errorPrint("Ignoring invalid campaign requested in -m flag: '" + camp + "'.");
					return false;
				}
			}
			return false;
		}

		/**
		 * loads or creates a character based upon the command line arguments.
		 * This also handles the start in character sheet command option
		 * @return boolean
		 * @throws InterruptedException
		 * @throws InvocationTargetException
		 */
		private boolean maybeLoadOrCreateCharacter() throws InterruptedException, InvocationTargetException
		{
			if (Main.getStartupCharacterFile() == null)
			{
				return false;
			}
			final File file = new File(Main.getStartupCharacterFile());
			final DataSetFacade dataset = currentDataSetRef.get();
			if (!file.exists() && dataset == null)
			{
				//TODO: complain about it
				return false;
			}
			if (Main.shouldStartInCharacterSheet())
			{
				String key = UIPropertyContext.C_PROP_INITIAL_TAB;
				key = UIPropertyContext.createFilePropertyKey(file, key);
				UIPropertyContext.getInstance().setInt(key, InfoTabbedPane.CHARACTER_SHEET_TAB);
			}
			GuiAssertions.assertIsNotSwingThread();
			SwingUtilities.invokeAndWait(() -> {
				if (!file.exists())
				{
					createNewCharacter(file);
				}
				else if (dataset == null)
				{
					loadCharacterFromFile(file);
				}
				else
				{
					openCharacter(file, dataset);
				}
			});
			return true;
		}

	}

	private static InputMap createInputMap(ActionMap actionMap)
	{
		InputMap inputMap = new InputMap();
		for (Object obj : actionMap.keys())
		{
			KeyStroke key = (KeyStroke) actionMap.get(obj).getValue(Action.ACCELERATOR_KEY);
			if (key != null)
			{
				inputMap.put(key, obj);
			}
		}
		return inputMap;
	}

	public PCGenActionMap getActionMap()
	{
		return actionMap;
	}

	@Override
	public void setCharacter(CharacterFacade character)
	{
		if (currentCharacterRef.get() != null)
		{
			currentCharacterRef.get().getFileRef().removeReferenceListener(filenameListener);
		}
		currentCharacterRef.set(character);
		updateTitle();
		if (character != null && character.getFileRef() != null)
		{
			character.getFileRef().addReferenceListener(filenameListener);
		}
		if (character != null)
		{
			pcGenMenuBar.setCharacter(character);
		}
	}

	/**
	 *
	 * @return a reference to the selected character
	 */
	public ReferenceFacade<CharacterFacade> getSelectedCharacterRef()
	{
		return currentCharacterRef;
	}

	/**
	 *
	 * @return a reference to the currently loaded data set
	 */
	public ReferenceFacade<DataSetFacade> getLoadedDataSetRef()
	{
		return currentDataSetRef;
	}

	/**
	 * @return the status bar for the main PCGen frame
	 */
	public PCGenStatusBar getStatusBar()
	{
		return statusBar;
	}

	/**
	 * Unload any currently loaded sources. 
	 */
	public void unloadSources()
	{
		//make sure all characters are closed before unloading sources.
		if (closeAllCharacters())
		{
			currentSourceSelection.set(null);
			currentDataSetRef.set(null);
			Globals.emptyLists();
			updateTitle();
		}
	}

	/**
	 * Loads a selection of sources into PCGen asynchronously and
	 * tracks the load progress on the status bar. While sources
	 * are being loaded any calls to this method are ignored until
	 * sources are finished loading.
	 * @param sources a SourceSelectionFacade specifying the sources to load
	 * @return true if the sources are loaded or are loading
	 */
	public boolean loadSourceSelection(SourceSelectionFacade sources)
	{
		if (sources == null)
		{
			return false;
		}
		if (sourceLoader != null && sourceLoader.isAlive())
		{
			return checkSourceEquality(sources, sourceLoader.sources);
		}
		if (checkSourceEquality(sources, currentSourceSelection.get()))
		{
			return true;
		}
		//make sure all characters are closed before loading new sources.
		if (closeAllCharacters())
		{
			sourceLoader = new SourceLoadWorker(sources, this);
			sourceLoader.start();
			return true;
		}
		return false;
	}

	private boolean checkSourceEquality(SourceSelectionFacade source1, SourceSelectionFacade source2)
	{
		if (source1 == source2)
		{
			return true;
		}
		if (source1 == null ^ source2 == null)
		{
			return false;
		}
		//we use reference equality since GameModes come from a fixed database
		if (source1.getGameMode().get() != source2.getGameMode().get())
		{
			return false;
		}
		ListFacade<Campaign> campaigns1 = source1.getCampaigns();
		ListFacade<Campaign> campaigns2 = source2.getCampaigns();
		if (campaigns1.getSize() != campaigns2.getSize())
		{
			return false;
		}
		for (Campaign campaign : campaigns1)
		{
			if (!campaigns2.containsElement(campaign))
			{
				return false;
			}
		}
		return true;
	}

	private static boolean checkGameModeEquality(SourceSelectionFacade source1, SourceSelectionFacade source2)
	{
		if (source1 == source2)
		{
			return true;
		}
		if (source1 == null ^ source2 == null)
		{
			return false;
		}
		//we use reference equality since GameModes come from a fixed database
        return source1.getGameMode().get() == source2.getGameMode().get();
    }

	public boolean saveCharacter(CharacterFacade character)
	{
		if (!CharacterManager.characterFilenameValid(character))
		{
			return showSaveCharacterChooser(character);
		}
		// We must have a file name before we prepare.
		prepareForSave(character, false);
		if (!reallySaveCharacter(character))
		{
			return showSaveCharacterChooser(character);
		}
		return true;
	}

	/**
	 * Wraps the CharacterManager with GUI progress updates
	 * @param character
	 * @return value from CharacterManager.saveCharacter()
	 */
	public boolean reallySaveCharacter(CharacterFacade character)
	{
		boolean result = false;

		// KAW TODO externalize and NLS the msg
		final String msg = "Saving character...";
		statusBar.startShowingProgress(msg, true);
		try
		{
			result = CharacterManager.saveCharacter(character);
		}
		catch (Exception e)
		{
			Logging.errorPrint(e.getLocalizedMessage(), e);
		}
		finally
		{
			statusBar.endShowingProgress();
		}
		return result;
	}

	/**
	 * Prepare the character for a save. This is primarily concerned with 
	 * ensuring all companions (or masters) have file names before the save is 
	 * done.
	 * @param character The character being saved.
	 */
	private void prepareForSave(CharacterFacade character, boolean savingAll)
	{
		List<CompanionFacade> tobeSaved = new ArrayList<>();
		for (CompanionFacade comp : character.getCompanionSupport().getCompanions())
		{
			if (StringUtils.isEmpty(comp.getFileRef().get().getName())
				&& CharacterManager.getCharacterMatching(comp) != null)
			{
				tobeSaved.add(comp);
			}
		}
		if (!tobeSaved.isEmpty())
		{
			if (savingAll
				|| showMessageConfirm(Constants.APPLICATION_NAME,
					LanguageBundle.getString("in_unsavedCompanions"))) //$NON-NLS-1$
			{
				for (CompanionFacade companionFacade : tobeSaved)
				{
					CharacterFacade compChar = CharacterManager.getCharacterMatching(companionFacade);
					showSaveCharacterChooser(compChar);
				}
			}
		}
		CharacterStubFacade master = character.getMaster();
		if (master != null
			&& (master.getFileRef().get() == null || StringUtils.isEmpty(master.getFileRef().get().getName())))
		{
			if (savingAll
				|| showMessageConfirm(Constants.APPLICATION_NAME,
					LanguageBundle.getString("in_unsavedMaster"))) //$NON-NLS-1$
			{
				CharacterFacade masterChar = CharacterManager.getCharacterMatching(master);
				showSaveCharacterChooser(masterChar);
			}
		}
	}

	public void closeCharacter(CharacterFacade character)
	{
		if (character.isDirty())
		{
			int ret = JOptionPane.showConfirmDialog(this,
				LanguageBundle.getFormattedString("in_savePcChoice", character //$NON-NLS-1$
				.getNameRef().get()), Constants.APPLICATION_NAME, JOptionPane.YES_NO_CANCEL_OPTION);
			if (ret == JOptionPane.CANCEL_OPTION)
			{
				return;
			}
			if (ret == JOptionPane.YES_OPTION)
			{
				saveCharacter(character);
			}
		}
		CharacterManager.removeCharacter(character);
	}

	public boolean closeAllCharacters()
	{
		final int CLOSE_OPT_CHOOSE = 2;
		ListFacade<CharacterFacade> characters = CharacterManager.getCharacters();
		if (characters.isEmpty())
		{
			return true;
		}
		int saveAllChoice = CLOSE_OPT_CHOOSE;

		List<CharacterFacade> characterList = new ArrayList<>();
		List<CharacterFacade> unsavedPCs = new ArrayList<>();
		for (CharacterFacade characterFacade : characters)
		{
			if (characterFacade.isDirty())
			{
				unsavedPCs.add(characterFacade);
			}
			else
			{
				characterList.add(characterFacade);
			}
		}
		if (unsavedPCs.size() > 1)
		{
			Object[] options = {LanguageBundle.getString("in_closeOptSaveAll"), //$NON-NLS-1$
				LanguageBundle.getString("in_closeOptSaveNone"), //$NON-NLS-1$
				LanguageBundle.getString("in_closeOptChoose"), //$NON-NLS-1$
				LanguageBundle.getString("in_cancel") //$NON-NLS-1$
			};
			saveAllChoice = JOptionPane.showOptionDialog(this,
				LanguageBundle.getString("in_closeOptSaveTitle"), //$NON-NLS-1$
				Constants.APPLICATION_NAME,
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					options[0]);
		}
		if (saveAllChoice == 3 || saveAllChoice == CLOSED_OPTION)
		{
			// Cancel
			return false;
		}
		if (saveAllChoice == 1)
		{
			// Save none
			CharacterManager.removeAllCharacters();
			return true;
		}

		for (CharacterFacade character : unsavedPCs)
		{
			int saveSingleChoice = JOptionPane.YES_OPTION;

			if (saveAllChoice == CLOSE_OPT_CHOOSE)
			{
				saveSingleChoice = JOptionPane.showConfirmDialog(this,
					LanguageBundle.getFormattedString("in_savePcChoice", character //$NON-NLS-1$
						.getNameRef().get()),
					Constants.APPLICATION_NAME, JOptionPane.YES_NO_CANCEL_OPTION);
			}

			switch (saveSingleChoice)
			{
				case JOptionPane.YES_OPTION: //If you get here then the user either selected "Yes to All" or "Yes"
					if (saveCharacter(character))
					{
						characterList.add(character);
					}
					break;
				case JOptionPane.NO_OPTION:
					characterList.add(character);
					break;
				case JOptionPane.CANCEL_OPTION:
					return false;
			}
		}

		for (CharacterFacade character : characterList)
		{
			CharacterManager.removeCharacter(character);
		}
		return characters.isEmpty();
	}

	boolean showSavePartyChooser()
	{
		PartyFacade party = CharacterManager.getCharacters();
		PCGenSettings context = PCGenSettings.getInstance();
		String parentPath = context.getProperty(PCGenSettings.PCP_SAVE_PATH);
		File oldFile = party.getFileRef().get();


		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save PCGen Party");
		fileChooser.setInitialDirectory(new File(parentPath));

		FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(
				"party files only", "*.pcp"
		);
		fileChooser.getExtensionFilters().add(extensionFilter);
		fileChooser.setSelectedExtensionFilter(extensionFilter);
		if (oldFile != null)
		{
			fileChooser.setInitialFileName(oldFile.getName());
		}

		File file = GuiUtility.runOnJavaFXThreadNow(() ->
				fileChooser.showSaveDialog(null));

		if (file == null)
		{
			return false;
		}
		if (file.exists())
		{
			boolean overwrite =
					showWarningConfirm(
						LanguageBundle.getFormattedString("in_savePcConfirmOverTitle", file.getName()), //$NON-NLS-1$
						LanguageBundle.getFormattedString("in_savePcConfirmOverMsg", file.getName())); //$NON-NLS-1$
			if (!overwrite)
			{
				return showSavePartyChooser();
			}
		}
		party.setFile(file);
		context.setProperty(PCGenSettings.PCP_SAVE_PATH, file.getParent());
		if (!saveAllCharacters())
		{
			showErrorMessage(LanguageBundle.getString("in_savePartyFailTitle"), //$NON-NLS-1$
				LanguageBundle.getString("in_savePartyFailMsg")); //$NON-NLS-1$
			return false;
		}
		if (!CharacterManager.saveCurrentParty())
		{
			return showSavePartyChooser();
		}
		return true;
	}

	boolean saveAllCharacters()
	{
		boolean ok = true;
		for (CharacterFacade character : CharacterManager.getCharacters())
		{
			File file = character.getFileRef().get();
			if (file == null || StringUtils.isEmpty(file.getName()))
			{
				ok &= showSaveCharacterChooser(character);
			}
			else
			{
				prepareForSave(character, true);
				ok &= reallySaveCharacter(character);
			}
		}
		return ok;
	}

	/**
	 * This brings up a file chooser allows the user to select
	 * the location that a character should be saved to.
	 * @param character the character to be saved
	 */
	boolean showSaveCharacterChooser(CharacterFacade character)
	{
		PCGenSettings context = PCGenSettings.getInstance();
		String parentPath = lastCharacterPath;
		if (parentPath == null)
		{
			parentPath = context.getProperty(PCGenSettings.PCG_SAVE_PATH);
		}

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save PCGen Character File");
		FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(
				"character files only", '*' + Constants.EXTENSION_CHARACTER_FILE
		);
		fileChooser.getExtensionFilters().add(extensionFilter);
		fileChooser.setSelectedExtensionFilter(extensionFilter);

		File prevFile = character.getFileRef().get();
		if (prevFile == null || StringUtils.isEmpty(prevFile.getName()))
		{
			fileChooser.setInitialDirectory(new File(parentPath));
			fileChooser.setInitialFileName(character.getNameRef().get() + Constants.EXTENSION_CHARACTER_FILE);
		}

		File file = GuiUtility.runOnJavaFXThreadNow(() ->
				fileChooser.showSaveDialog(null));

		if (file != null)
		{
			UIDelegate delegate = character.getUIDelegate();
			if (file.isDirectory())
			{
				delegate.showErrorMessage(Constants.APPLICATION_NAME,
					LanguageBundle.getString("in_savePcDirOverwrite")); //$NON-NLS-1$
				return showSaveCharacterChooser(character);
			}

			if (file.exists() && (prevFile == null || !file.getName().equals(prevFile.getName())))
			{
				boolean overwrite = delegate.showWarningConfirm(
					LanguageBundle.getFormattedString("in_savePcConfirmOverTitle", //$NON-NLS-1$
						file.getName()),
					LanguageBundle.getFormattedString("in_savePcConfirmOverMsg", //$NON-NLS-1$
						file.getName()));

				if (!overwrite)
				{
					return showSaveCharacterChooser(character);
				}
			}

			try
			{
				character.setFile(file);
				prepareForSave(character, false);
				if (!reallySaveCharacter(character))
				{
					return showSaveCharacterChooser(character);
				}

				lastCharacterPath = file.getParent();
				return true;
			}
			catch (Exception e)
			{
				Logging.errorPrint("Error saving character to new file " + file, e); //$NON-NLS-1$
				delegate.showErrorMessage(Constants.APPLICATION_NAME,
					LanguageBundle.getFormattedString("in_saveFailMsg", file.getName())); //$NON-NLS-1$
			}
		}
		return false;
	}

	/**
	 * Revert the character to the previous save. If no previous save, open a
	 * new character tab.
	 * @param character The character being saved.
	 */
	public void revertCharacter(CharacterFacade character)
	{
		if (character.isDirty())
		{
			int ret =
					JOptionPane.showConfirmDialog(this,
						LanguageBundle.getFormattedString("in_revertPcChoice", character //$NON-NLS-1$
						.getNameRef().get()), Constants.APPLICATION_NAME, JOptionPane.YES_NO_OPTION);
			if (ret == JOptionPane.YES_OPTION)
			{
				CharacterManager.removeCharacter(character);

				if (character.getFileRef().get() != null && character.getFileRef().get().exists())
				{
					openCharacter(character.getFileRef().get(), currentDataSetRef.get());
				}
				else
				{
					createNewCharacter(null);
				}
			}
		}

	}

	public void showOpenCharacterChooser()
	{
		GuiAssertions.assertIsNotJavaFXThread();
		PropertyContext context = PCGenSettings.getInstance();
		String path = lastCharacterPath;
		if (path == null)
		{
			path = context.getProperty(PCGenSettings.PCG_SAVE_PATH);
		}

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open PCGen Character");
		fileChooser.setInitialDirectory(new File(path));

		FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(
				"character files only", '*' + Constants.EXTENSION_CHARACTER_FILE
		);
		fileChooser.getExtensionFilters().add(extensionFilter);
		fileChooser.setSelectedExtensionFilter(extensionFilter);

		File file = GuiUtility.runOnJavaFXThreadNow(() ->
				fileChooser.showOpenDialog(null));
		if (file != null)
		{
			lastCharacterPath = file.getAbsoluteFile().getParent();
			loadCharacterFromFile(file);
		}
	}

	void showOpenPartyChooser()
	{
		PropertyContext context = PCGenSettings.getInstance();
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open PCGen Party File");
		fileChooser.setInitialDirectory(new File(context.getProperty(PCGenSettings.PCP_SAVE_PATH)));

		FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(
				"party files only", "*.pcp"
		);
		fileChooser.getExtensionFilters().add(extensionFilter);
		fileChooser.setSelectedExtensionFilter(extensionFilter);

		File file = GuiUtility.runOnJavaFXThreadNow(() ->
				fileChooser.showOpenDialog(null));
		if (file != null)
		{
			loadPartyFromFile(file);
		}
	}

	/**
	 * creates a new character and sets its file if possible
	 * then sets the character as the currently selected character
	 * @param file the File for this character
	 */
	public void createNewCharacter(File file)
	{
		GuiAssertions.assertIsSwingThread();
		DataSetFacade data = getLoadedDataSetRef().get();
		CharacterFacade character = CharacterManager.createNewCharacter(this, data);
		//This is called before the we set it as the selected character so
		//the InfoTabbedPane can catch any character specific properties when
		//it is first displayed
		if (file != null)
		{
			character.setFile(file);
		}
		//Because CharacterManager adds the new character to the character
		//list before it returns, it is not necessary to update the character
		//tabs since they will catch that event before the call to
		//setCharacter is called
		setCharacter(character);
	}

	/**
	 * Loads a character from a file. Any sources that are required for
	 * this character are loaded first, then the character is loaded
	 * from the file and a tab is opened for it.
	 * @param pcgFile a file specifying the character to be loaded
	 */
	public void loadCharacterFromFile(final File pcgFile)
	{
		if (!PCGFile.isPCGenCharacterFile(pcgFile))
		{
			this.showErrorMessage(LanguageBundle.getFormattedString("in_loadPcInvalid", pcgFile),
					LanguageBundle.getFormattedString("in_loadPcInvalid", pcgFile)	);
			return;
		}
		if (!pcgFile.canRead())
		{
			this.showErrorMessage(LanguageBundle.getFormattedString("in_loadPcFailTtile"),
					LanguageBundle.getFormattedString("in_loadPcNoRead", pcgFile)	);
			return;
		}

		SourceSelectionFacade sources = CharacterManager.getRequiredSourcesForCharacter(pcgFile, this);
		if (sources == null)
		{
			this.showErrorMessage(LanguageBundle.getFormattedString("in_loadPcNoSources", pcgFile),
					LanguageBundle.getString("in_loadPcFailTtile"));
		}
		else if (!sources.getCampaigns().isEmpty())
		{
			// Check if the user has asked that sources not be loaded with the character
			boolean dontLoadSources = currentSourceSelection.get() != null
				&& !PCGenSettings.OPTIONS_CONTEXT.initBoolean(PCGenSettings.OPTION_AUTOLOAD_SOURCES_WITH_PC, true);
			boolean sourcesSame = checkSourceEquality(sources, currentSourceSelection.get());
			boolean gameModesSame = checkGameModeEquality(sources, currentSourceSelection.get());
			if (!dontLoadSources && !sourcesSame && gameModesSame)
			{
				Object[] btnNames = {LanguageBundle.getString("in_loadPcDiffSourcesLoaded"),
					LanguageBundle.getString("in_loadPcDiffSourcesCharacter"), LanguageBundle.getString("in_cancel")};
				int choice = JOptionPane.showOptionDialog(this,
					LanguageBundle.getFormattedString("in_loadPcDiffSources",
						getFormattedCampaigns(currentSourceSelection.get()), getFormattedCampaigns(sources)),
					LanguageBundle.getString("in_loadPcSourcesLoadTitle"), JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, btnNames, null);
				if (choice == JOptionPane.CANCEL_OPTION)
				{
					return;
				}
				if (choice == JOptionPane.YES_OPTION)
				{
					openCharacter(pcgFile, currentDataSetRef.get());
					return;
				}
			}

			if (dontLoadSources)
			{
				if (!checkSourceEquality(sources, currentSourceSelection.get()))
				{
					Logging.log(Logging.WARNING, "Loading character with different sources. Character: " + sources
						+ " current: " + currentSourceSelection.get());
				}
				openCharacter(pcgFile, currentDataSetRef.get());
			}
			else if (loadSourceSelection(sources))
			{
				if (sourceSelectionDialog == null)
				{
					sourceSelectionDialog = new SourceSelectionDialog(this, uiContext);
				}
				((SourceSelectionDialog) sourceSelectionDialog).setAdvancedSources(sources);
				currentSourceSelection.set(sources);
				loadSourcesThenCharacter(pcgFile);
			}
			else
			{
				JOptionPane.showMessageDialog(this, LanguageBundle.getString("in_loadPcIncompatSource"), //$NON-NLS-1$
					LanguageBundle.getString("in_loadPcFailTtile"), //$NON-NLS-1$
					JOptionPane.ERROR_MESSAGE);
			}
		}
		else if (currentDataSetRef.get() != null)
		{
			if (showWarningConfirm(Constants.APPLICATION_NAME,
				LanguageBundle.getFormattedString("in_loadPcSourcesLoadQuery", //$NON-NLS-1$
					pcgFile)))
			{
				openCharacter(pcgFile, currentDataSetRef.get());
			}
		}
		else
		{
			// No character sources and no sources loaded.
			this.showErrorMessage(LanguageBundle.getFormattedString("in_loadPcNoSources", pcgFile),
					LanguageBundle.getString("in_loadPcFailTtile"));
		}
	}

	/**
	 * @param pcgFile the character's pcgFile
	 * @param reference data set reference
	 */
	private void openCharacter(final File pcgFile, final DataSetFacade reference)
	{
		final String msg = LanguageBundle.getFormattedString("in_loadPcLoadingFile", pcgFile.getName());
		statusBar.startShowingProgress(msg, false);
		statusBar.getProgressBar().getModel().setRangeProperties(0, 1, 0, 2, false);
		statusBar.getProgressBar().setString(LanguageBundle.getString("in_loadPcOpening"));
		SwingUtilities.invokeLater(() -> {

			try
			{
				CharacterManager.openCharacter(pcgFile, PCGenFrame.this, reference);
				statusBar.getProgressBar().getModel().setRangeProperties(1, 1, 0, 2, false);
			}
			catch (Exception e)
			{
				Logging.errorPrint("Error loading character: " + pcgFile.getName(), e);
			}
			finally
			{
				statusBar.endShowingProgress();
			}
		});
	}

	private static String getFormattedCampaigns(SourceSelectionFacade sources)
	{
		StringBuilder campList = new StringBuilder(100);
		campList.append("<UL>");
		int count = 1;
		final int maxListLen = 6;
		for (Campaign facade : sources.getCampaigns())
		{
			campList.append("<li>");
			if (count >= maxListLen && sources.getCampaigns().getSize() > maxListLen)
			{
				int numExtra = sources.getCampaigns().getSize() - maxListLen + 1;
				campList.append(
					LanguageBundle.getFormattedString("in_loadPcDiffSourcesExcessSources", String.valueOf(numExtra)));
				break;
			}
			campList.append(facade);
			campList.append("</li>");
			count++;
		}
		campList.append("</UL>");
		return campList.toString();
	}

	/**
	 * Asynchronously load the sources required for a character and then load the character.
	 * @param pcgFile The character to be loaded.
	 */
	private void loadSourcesThenCharacter(final File pcgFile)
	{
		new Thread(() -> {
			try
			{
				sourceLoader.join();
				SwingUtilities.invokeAndWait(() -> {
					final String msg =
							LanguageBundle.getFormattedString("in_loadPcLoadingFile", pcgFile.getName());
					statusBar.startShowingProgress(msg, false);
					statusBar.getProgressBar().getModel().setRangeProperties(0, 1, 0, 2, false);
					statusBar.getProgressBar().setString(LanguageBundle.getString("in_loadPcOpening"));
				});
				SwingUtilities.invokeLater(() -> {
					try
					{
						CharacterManager.openCharacter(pcgFile, PCGenFrame.this, currentDataSetRef.get());
						statusBar.getProgressBar().getModel().setRangeProperties(1, 1, 0, 2, false);
					}
					catch (Exception e)
					{
						Logging.errorPrint("Error loading character: " + pcgFile.getName(), e);
					}
					finally
					{
						statusBar.endShowingProgress();
					}
				});
			}
			catch (InterruptedException ex)
			{
				//Do nothing
			}
			catch (InvocationTargetException e1)
			{
				Logging.errorPrint("Error showing progress bar.", e1);
			}
		}).start();
	}

	public void loadPartyFromFile(final File pcpFile)
	{
		if (!PCGFile.isPCGenPartyFile(pcpFile))
		{
			this.showErrorMessage(LanguageBundle.getString("in_loadPartyFailTtile"),
					LanguageBundle.getFormattedString("in_loadPartyInvalid", pcpFile)	);
			return;
		}
		if (!pcpFile.canRead())
		{
			this.showErrorMessage(LanguageBundle.getString("in_loadPartyFailTtile"),
					LanguageBundle.getFormattedString("in_loadPartyNoRead", pcpFile)	);
			return;
		}
		SourceSelectionFacade sources = CharacterManager.getRequiredSourcesForParty(pcpFile, this);
		if (sources == null)
		{
			this.showErrorMessage(LanguageBundle.getString("in_loadPartyFailTtile"),
					LanguageBundle.getFormattedString("in_loadPartyNoSources", pcpFile)	);
		}
		else if (!sources.getCampaigns().isEmpty())
		{
			// Check if the user has asked that sources not be loaded with the character
			boolean dontLoadSources = currentSourceSelection.get() != null
				&& !PCGenSettings.OPTIONS_CONTEXT.initBoolean(PCGenSettings.OPTION_AUTOLOAD_SOURCES_WITH_PC, true);
			if (dontLoadSources)
			{
				if (!checkSourceEquality(sources, currentSourceSelection.get()))
				{
					Logging.log(Logging.WARNING, "Loading party with different sources. Party: " + sources
						+ " current: " + currentSourceSelection.get());
				}
				CharacterManager.openParty(pcpFile, PCGenFrame.this, currentDataSetRef.get());
			}
			else if (loadSourceSelection(sources))
			{
				new Thread(() -> {
					try
					{
						sourceLoader.join();
						SwingUtilities.invokeLater(() -> CharacterManager.openParty(pcpFile, PCGenFrame.this, currentDataSetRef.get()));
					}
					catch (InterruptedException ex)
					{
						//Do nothing
					}
				}).start();
			}
			else
			{
				this.showErrorMessage(LanguageBundle.getString("in_loadPartyFailTtile"),
						LanguageBundle.getString("in_loadPcIncompatSource")	);
			}
		}
	}

	/**
	 * Set the frame's title based on the current character and source/game mode.
	 */
	private void updateTitle()
	{
		StringBuilder title = new StringBuilder(100);
		File characterFile;
		String characterFileName = null;
		String sourceName = null;
		if (currentCharacterRef != null && currentCharacterRef.get() != null)
		{
			// characterFileName The file name (without path) of the active character 
			// sourceName The name of the source selection.

			characterFile = currentCharacterRef.get().getFileRef().get();
			if (characterFile == null || StringUtils.isEmpty(characterFile.getName()))
			{
				characterFileName = LanguageBundle.getString("in_unsaved_char"); //$NON-NLS-1$
			}
			else
			{
				characterFileName = characterFile.getName();
			}
		}
		if (currentSourceSelection.get() != null)
		{
			sourceName = currentSourceSelection.get().toString();
		}

		if (characterFileName != null && !characterFileName.isEmpty())
		{
			title.append(characterFileName);
			title.append(" - ");
		}
		if (sourceName != null && !sourceName.isEmpty())
		{
			title.append(sourceName);
			title.append(" - ");
		}
		title.append("PCGen v");
		title.append(PCGenPropBundle.getVersionNumber());
		setTitle(title.toString());
	}

	/**
	 * display the tips of the day dialog to the user
	 */
	static void showTipsOfTheDay()
	{
		var totd = new JFXPanelFromResource<>(
				TipOfTheDayController.class,
				"TipOfTheDay.fxml"
		);
		totd.showAsStage(LanguageBundle.getString("in_tod_title"));
	}

	/**
	 * display the source selection dialog to the user
	 */
	public void showSourceSelectionDialog()
	{
		if (sourceSelectionDialog == null)
		{
			sourceSelectionDialog = new SourceSelectionDialog(this, uiContext);
		}
		sourceSelectionDialog.setLocationRelativeTo(this);
		sourceSelectionDialog.setVisible(true);
	}

	//TODO: This should be in a utility class.
	/**
	 * Builds a JPanel containing the supplied message, split at each new
	 * line and an optional checkbox, suitable for use in a showMessageDialog
	 * call. This is generally useful for showing messges which can turned
	 * off either in preferences or when they are displayed.
	 *
	 * @param message The message to be displayed.
	 * @param checkbox The optional checkbox to be added - may be null.
	 * @return JPanel A panel containing the message and the checkbox.
	 */
	public static JPanel buildMessageLabelPanel(String message, JCheckBox checkbox)
	{
		JPanel panel = new JPanel();
		JLabel label;
		String part;

		panel.setLayout(new GridBagLayout());

		GridBagConstraints cons = new GridBagConstraints();
		cons.gridx = cons.gridy = 0;
		cons.gridwidth = GridBagConstraints.REMAINDER;
		cons.gridheight = 1;
		cons.anchor = GridBagConstraints.WEST;
		cons.insets = new Insets(0, 0, 3, 0);
		cons.weightx = 1;
		cons.weighty = 0;
		cons.fill = GridBagConstraints.NONE;

		int start = 0;
		int sepPos;

		do
		{
			sepPos = message.indexOf("\n", start); //$NON-NLS-1$

			if (sepPos >= 0)
			{
				part = message.substring(start, sepPos);
				start = sepPos + 1;
			}
			else
			{
				part = message.substring(start);
				start = -1;
			}

			label = new JLabel(part, SwingConstants.LEADING);

			panel.add(label, cons);
			cons.gridy++;
		}
		while (start >= 0);

		if (checkbox != null)
		{
			label = new JLabel("", SwingConstants.LEADING); //$NON-NLS-1$
			panel.add(label, cons);
			cons.gridy++;
			panel.add(checkbox, cons);
			cons.gridy++;
		}

		return panel;
	}

	@Override
	public Boolean maybeShowWarningConfirm(String title, String message, String checkBoxText,
		final PropertyContext context, final String contextProp)
	{
		if (!context.getBoolean(contextProp, true))
		{
			return null;
		}
		final JCheckBox checkBox = new JCheckBox(checkBoxText, true);
		checkBox.addItemListener(e -> context.setBoolean(contextProp, checkBox.isSelected()));
		JPanel panel = buildMessageLabelPanel(message, checkBox);
		int ret = JOptionPane.showConfirmDialog(this, panel, title, JOptionPane.YES_NO_OPTION,
			JOptionPane.WARNING_MESSAGE);
		return ret == JOptionPane.YES_OPTION;
	}

	@Override
	public boolean showWarningConfirm(String title, String message)
	{
		Alert alert = GuiUtility.runOnJavaFXThreadNow(() -> new Alert(Alert.AlertType.CONFIRMATION));
		alert.setTitle(title);
		alert.setContentText(message);
		Optional<ButtonType> buttonType = GuiUtility.runOnJavaFXThreadNow(alert::showAndWait);
		return buttonType.orElse(ButtonType.NO).equals(ButtonType.OK);
	}

	private boolean showMessageConfirm(String title, String message)
	{
		JComponent msgComp = new JLabel(message);
		int ret = JOptionPane.showConfirmDialog(this, msgComp, title, JOptionPane.YES_NO_OPTION);
		return ret == JOptionPane.YES_OPTION;
	}

	@Override
	public void showErrorMessage(String title, String message)
	{
		GuiAssertions.assertIsNotJavaFXThread();
		Alert alert = GuiUtility.runOnJavaFXThreadNow(() -> new Alert(Alert.AlertType.ERROR));
		alert.setTitle(title);
		alert.setContentText(message);
		GuiUtility.runOnJavaFXThreadNow(alert::showAndWait);
	}

	@Override
	public void showInfoMessage(String title, String message)
	{
		GuiAssertions.assertIsNotJavaFXThread();
		Alert alert = GuiUtility.runOnJavaFXThreadNow(() -> new Alert(Alert.AlertType.INFORMATION));
		alert.setTitle(title);
		alert.setContentText(message);
		GuiUtility.runOnJavaFXThreadNow(alert::showAndWait);
	}

	@Override
	public void showWarningMessage(String title, String message)
	{
		GuiAssertions.assertIsNotJavaFXThread();
		Alert alert = GuiUtility.runOnJavaFXThreadNow(() -> new Alert(Alert.AlertType.WARNING));
		alert.setTitle(title);
		alert.setContentText(message);
		GuiUtility.runOnJavaFXThreadNow(alert::showAndWait);
	}

	@Override
	public Optional<String> showInputDialog(String title, String message, String initialValue)
	{
		GuiAssertions.assertIsNotJavaFXThread();
		TextInputDialog dialog = GuiUtility.runOnJavaFXThreadNow(() -> new TextInputDialog(initialValue));
		dialog.setTitle(title);
		dialog.setContentText(message);
		return GuiUtility.runOnJavaFXThreadNow(dialog::showAndWait);
	}

	@Override
	public void showLevelUpInfo(CharacterFacade character, int oldLevel)
	{
		PostLevelUpDialog.showPostLevelUpDialog(this, character, oldLevel);
	}

	@Override
	public boolean showGeneralChooser(ChooserFacade chooserFacade)
	{
		// Check for an override of the chooser to be used 
		Optional<RandomChooser> choiceHandler = ChooserFactory.getChoiceHandler();
		if (choiceHandler.isPresent())
		{
			return choiceHandler.get().makeChoice(chooserFacade);
		}

		if (chooserFacade.isPreferRadioSelection() && chooserFacade.getRemainingSelections().get() == 1)
		{
			RadioChooserDialog dialog = new RadioChooserDialog(this, chooserFacade);
			dialog.setLocationRelativeTo(this);
			dialog.setVisible(true);
			return dialog.isCommitted();
		}
		else
		{
			ChooserDialog dialog = new ChooserDialog(this, chooserFacade);
			dialog.setLocationRelativeTo(this);
			dialog.setVisible(true);
			return dialog.isCommitted();
		}
	}

	/*
	 * This thread does all work of loading sources that the user
	 * has selected. After the sources are loaded the thread then
	 * displays the licenses for the data.
	 */
	private class SourceLoadWorker extends Thread
	{

		private final SourceSelectionFacade sources;
		private final SourceFileLoader loader;
		private final SwingWorker<List<LogRecord>, List<LogRecord>> worker;

		public SourceLoadWorker(SourceSelectionFacade sources, UIDelegate delegate)
		{
			this.sources = sources;
			loader = new SourceFileLoader(delegate, sources.getCampaigns(), sources.getGameMode().get().getName());
			worker = statusBar.createWorker(LanguageBundle.getString("in_taskLoadSources"), loader); //$NON-NLS-1$
		}

		@Override
		public void run()
		{
			worker.execute();
			//wait until the worker finish and post any errors that occurred
			try
			{
				statusBar.setSourceLoadErrors(worker.get());
			} catch (InterruptedException | ExecutionException e)
			{
				Logging.errorPrint("error when loading sources", e);
			}
			//now that the SourceFileLoader has finished
			//handle licenses and whatnot
			section15 = " "
					+ readTextFromFile(
					ConfigurationSettings.getSystemsDir() + File.separator + "opengaminglicense.10a.txt")
					+ loader.getOGL();
			try
			{
				showLicenses();
			}
			catch (Throwable e)
			{
				Logging.errorPrint("Failed to show licences.", e);
			}

			DataSetFacade data = loader.getDataSetFacade();
			if (data != null)
			{
				currentSourceSelection.set(sources);

				StringBuilder sourceString = new StringBuilder(100);
				ListFacade<Campaign> campaigns = sources.getCampaigns();
				for (int i = 0; i < campaigns.getSize(); i++)
				{
					if (i > 0)
					{
						sourceString.append('|');
					}
					sourceString.append(campaigns.getElementAt(i));
				}
				PCGenSettings.getInstance().setProperty(PCGenSettings.LAST_LOADED_GAME,
					sources.getGameMode().toString());
				PCGenSettings.getInstance().setProperty(PCGenSettings.LAST_LOADED_SOURCES, sourceString.toString());
			}
			else
			{
				currentSourceSelection.set(null);
			}
			currentDataSetRef.set(data);
			updateTitle();
		}

		private void showLicenses()
		{
			PropertyContext context = PCGenSettings.OPTIONS_CONTEXT;
			if (context.initBoolean(PCGenSettings.OPTION_SHOW_LICENSE, true))
			{
				if (loader.hasOGLCampaign())
				{
					showOGLDialog();
				}
				if (loader.hasLicensedCampaign())
				{
					String licenses = loader.getLicenses();
					if (!licenses.trim().isEmpty())
					{
						showLicenseDialog(LanguageBundle.getString("in_specialLicenses"), licenses); //$NON-NLS-1$
					}
					for (String license : loader.getOtherLicenses())
					{
						showLicenseDialog(LanguageBundle.getString("in_specialLicenses"), license); //$NON-NLS-1$
					}

				}
			}
			if (loader.hasMatureCampaign() && context.initBoolean(PCGenSettings.OPTION_SHOW_MATURE_ON_LOAD, true))
			{
				showMatureDialog(loader.getMatureInfo());
			}
		}

	}

	void showOGLDialog()
	{
		showLicenseDialog(LanguageBundle.getString("in_oglTitle"), section15); //$NON-NLS-1$
	}

	private static void showLicenseDialog(String title, String htmlString)
	{
		GuiAssertions.assertIsNotJavaFXThread();
		if (htmlString == null)
		{
			htmlString = LanguageBundle.getString("in_licNoInfo"); //$NON-NLS-1$
		}
		Alert alert = RememberingChoiceDialog.create(
				title,
				"",
				htmlString,
				"in_licShowOnLoad",
				PCGenSettings.OPTIONS_CONTEXT,
				PCGenSettings.OPTION_SHOW_LICENSE
				);
		GuiUtility.runOnJavaFXThreadNow(alert::showAndWait);
	}

	private static void showMatureDialog(final String text)
	{
		Logging.log(Logging.WARNING, "The following datasets contains mature themes. User discretion is advised.");
		Logging.log(Logging.WARNING, text);
		// todo: combine into a single l18n string
		String matureWarning = LanguageBundle.getString("in_matureWarningLine1")
				+ '\n'
				+ LanguageBundle.getString("in_matureWarningLine2");
		Alert alert = RememberingChoiceDialog.create(
				LanguageBundle.getString("in_matureTitle"),
				matureWarning,
				text,
				"in_Prefs_displayMature",
				PCGenSettings.OPTIONS_CONTEXT,
				PCGenSettings.OPTION_SHOW_MATURE_ON_LOAD
		);
		GuiUtility.runOnJavaFXThreadNow(alert::showAndWait);
	}

	static void showAboutDialog()
	{
		new AboutDialog();
	}

	@Override
	public CustomEquipResult showCustomEquipDialog(CharacterFacade character, EquipmentBuilderFacade equipBuilder)
	{
		EquipCustomizerDialog eqDialog = new EquipCustomizerDialog(this, character, equipBuilder);
		eqDialog.setLocationRelativeTo(this);
		eqDialog.setVisible(true);
		return eqDialog.isCancelled() ? CustomEquipResult.CANCELLED
			: eqDialog.isPurchase() ? CustomEquipResult.PURCHASE : CustomEquipResult.OK;
	}

	@Override
	public boolean showCustomSpellDialog(SpellBuilderFacade spellBuilderFI)
	{
		SpellChoiceDialog spellDialog = new SpellChoiceDialog(this, spellBuilderFI);
		spellDialog.setLocationRelativeTo(this);
		spellDialog.setVisible(true);
		return !spellDialog.isCancelled();
	}

	private static String readTextFromFile(String fileName)
	{
		try
		{
			return Files.readString(Paths.get(fileName));
		} catch (IOException e)
		{
			Logging.errorPrint("Could not read license at " + fileName, e);
			return LanguageBundle.getString("in_licNoInfo"); //$NON-NLS-1$
		}
	}

	/**
	 * The Class {@code FilenameListener} is used to update the frame title each time the
	 * current character's file name is changed.
	 */
	class FilenameListener implements ReferenceListener<File>
	{
		@Override
		public void referenceChanged(ReferenceEvent<File> e)
		{
			PCGenFrame.this.updateTitle();
		}

	}

}
