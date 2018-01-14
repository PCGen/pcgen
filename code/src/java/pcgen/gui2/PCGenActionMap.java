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

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import pcgen.cdom.content.Sponsor;
import pcgen.core.Globals;
import pcgen.facade.core.AbilityFacade;
import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.ClassFacade;
import pcgen.facade.core.ItemFacade;
import pcgen.facade.core.KitFacade;
import pcgen.facade.core.RaceFacade;
import pcgen.facade.core.SkillFacade;
import pcgen.facade.core.SourceSelectionFacade;
import pcgen.facade.core.SpellFacade;
import pcgen.facade.core.StatFacade;
import pcgen.facade.core.TemplateFacade;
import pcgen.facade.util.ReferenceFacade;
import pcgen.facade.util.event.ReferenceEvent;
import pcgen.facade.util.event.ReferenceListener;
import pcgen.gui2.coreview.CoreViewFrame;
import pcgen.gui2.dialog.CalculatorDialog;
import pcgen.gui2.dialog.DataInstaller;
import pcgen.gui2.dialog.DebugDialog;
import pcgen.gui2.dialog.ExportDialog;
import pcgen.gui2.dialog.KitSelectionDialog;
import pcgen.gui2.dialog.PrintPreviewDialog;
import pcgen.gui2.solverview.SolverViewFrame;
import pcgen.gui2.tools.Icons;
import pcgen.gui2.tools.PCGenAction;
import pcgen.gui2.tools.Utility;
import pcgen.system.CharacterManager;
import pcgen.system.ConfigurationSettings;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

import gmgen.GMGenSystem;

/**
 * The PCGenActionMap is the action map for the PCGenFrame, and as such
 * hold all of the actions that the PCGenFrame uses. The purpose of this
 * class is to hold all of the regarding actions for the menubar, toolbar,
 * and accessory popup menus that may use them. Since all of the action
 * handlers are Action objects they can be disabled or enabled to cause
 * all buttons that use the actions to update themselves accordingly.
 */
public final class PCGenActionMap extends ActionMap
{

	//the File menu commands
	public static final String FILE_COMMAND = "file";
	public static final String NEW_COMMAND = FILE_COMMAND + ".new";
	public static final String OPEN_COMMAND = FILE_COMMAND + ".open";
	public static final String OPEN_RECENT_COMMAND = FILE_COMMAND + ".openrecent";
	public static final String CLOSE_COMMAND = FILE_COMMAND + ".close";
	public static final String CLOSEALL_COMMAND = FILE_COMMAND + ".closeall";
	public static final String SAVE_COMMAND = FILE_COMMAND + ".save";
	public static final String SAVEAS_COMMAND = FILE_COMMAND + ".saveas";
	public static final String SAVEALL_COMMAND = FILE_COMMAND + ".saveall";
	public static final String REVERT_COMMAND = FILE_COMMAND + ".reverttosaved";
	public static final String PARTY_COMMAND = FILE_COMMAND + ".party";
	public static final String OPEN_PARTY_COMMAND = PARTY_COMMAND + ".open";
	public static final String OPEN_RECENT_PARTY_COMMAND = PARTY_COMMAND + ".openrecent";
	public static final String CLOSE_PARTY_COMMAND = PARTY_COMMAND + ".close";
	public static final String SAVE_PARTY_COMMAND = PARTY_COMMAND + ".save";
	public static final String SAVEAS_PARTY_COMMAND = PARTY_COMMAND + ".saveas";
	public static final String PRINT_COMMAND = FILE_COMMAND + ".print";
	public static final String EXPORT_COMMAND = FILE_COMMAND + ".export";
	public static final String EXIT_COMMAND = FILE_COMMAND + ".exit";
	//the Edit menu commands
	public static final String EDIT_COMMAND = "edit";
	public static final String UNDO_COMMAND = EDIT_COMMAND + ".undo";
	public static final String REDO_COMMAND = EDIT_COMMAND + ".redo";
	public static final String ADD_KIT_COMMAND = EDIT_COMMAND + ".addkit";
	public static final String GENERATE_COMMAND = EDIT_COMMAND + ".regenerate";
	public static final String TEMP_BONUS_COMMAND = EDIT_COMMAND + ".tempbonus";
	public static final String EQUIPMENTSET_COMMAND = EDIT_COMMAND + ".equipmentset";
	//the Source menu commands
	public static final String SOURCES_COMMAND = "sources";
	public static final String SOURCES_LOAD_COMMAND = SOURCES_COMMAND + ".load";
	public static final String SOURCES_LOAD_SELECT_COMMAND = SOURCES_COMMAND + ".select";
	public static final String SOURCES_RELOAD_COMMAND = SOURCES_COMMAND + ".reload";
	public static final String SOURCES_UNLOAD_COMMAND = SOURCES_COMMAND + ".unload";
	public static final String INSTALL_DATA_COMMAND = SOURCES_COMMAND + ".installData";
	//the tools menu commands
	public static final String TOOLS_COMMAND = "tools";
	public static final String FILTERS_COMMAND = TOOLS_COMMAND + ".filters";
	public static final String KIT_FILTERS_COMMAND = FILTERS_COMMAND + ".kit";
	public static final String RACE_FILTERS_COMMAND = FILTERS_COMMAND + ".race";
	public static final String TEMPLATE_FILTERS_COMMAND = FILTERS_COMMAND + ".template";
	public static final String CLASS_FILTERS_COMMAND = FILTERS_COMMAND + ".class";
	public static final String ABILITY_FILTERS_COMMAND = FILTERS_COMMAND + ".ability";
	public static final String SKILL_FILTERS_COMMAND = FILTERS_COMMAND + ".skill";
	public static final String EQUIPMENT_FILTERS_COMMAND = FILTERS_COMMAND + ".equipment";
	public static final String SPELL_FILTERS_COMMAND = FILTERS_COMMAND + ".spell";
	public static final String GENERATORS_COMMAND = TOOLS_COMMAND + ".generators";
	public static final String TREASURE_GENERATORS_COMMAND = GENERATORS_COMMAND + ".treasure";
	public static final String RACE_GENERATORS_COMMAND = GENERATORS_COMMAND + ".race";
	public static final String TEMPLATE_GENERATORS_COMMAND = GENERATORS_COMMAND + ".template";
	public static final String CLASS_GENERATORS_COMMAND = GENERATORS_COMMAND + ".class";
	public static final String STAT_GENERATORS_COMMAND = GENERATORS_COMMAND + ".stat";
	public static final String ABILITY_GENERATORS_COMMAND = GENERATORS_COMMAND + ".ability";
	public static final String SKILL_GENERATORS_COMMAND = GENERATORS_COMMAND + ".skill";
	public static final String EQUIPMENT_GENERATORS_COMMAND = GENERATORS_COMMAND + ".equipment";
	public static final String SPELL_GENERATORS_COMMAND = GENERATORS_COMMAND + ".spell";
	public static final String PREFERENCES_COMMAND = TOOLS_COMMAND + ".preferences";
	public static final String GMGEN_COMMAND = TOOLS_COMMAND + ".gmgen";
	public static final String LOG_COMMAND = TOOLS_COMMAND + ".log";
	public static final String LOGGING_LEVEL_COMMAND = TOOLS_COMMAND + ".loggingLevel";
	public static final String CALCULATOR_COMMAND = TOOLS_COMMAND + ".calculator";
	public static final String COREVIEW_COMMAND = TOOLS_COMMAND + ".coreview";
	public static final String SOLVERVIEW_COMMAND = TOOLS_COMMAND + ".solverview";
	//the help menu commands
	public static final String HELP_COMMAND = "help";
	public static final String HELP_CONTEXT_COMMAND = HELP_COMMAND + ".context";
	public static final String HELP_DOCS_COMMAND = HELP_COMMAND + ".docs";
	public static final String HELP_OGL_COMMAND = HELP_COMMAND + ".ogl";
	public static final String HELP_SPONSORS_COMMAND = HELP_COMMAND + ".sponsors";
	public static final String HELP_TIPOFTHEDAY_COMMAND = HELP_COMMAND + ".tod";
	public static final String HELP_ABOUT_COMMAND = HELP_COMMAND + ".about";
	private final PCGenFrame frame;
	
	public static final String MNU_TOOLS = "mnuTools"; //$NON-NLS-1$
	public static final String MNU_TOOLS_PREFERENCES = "mnuToolsPreferences"; //$NON-NLS-1$
	public static final String MNU_EDIT = "mnuEdit"; //$NON-NLS-1$
	public static final String MNU_FILE = "mnuFile"; //$NON-NLS-1$

	public PCGenActionMap(PCGenFrame frame)
	{
		this.frame = frame;
		initActions();
	}

	private void initActions()
	{
		put(FILE_COMMAND, new FileAction());
		put(NEW_COMMAND, new NewAction());
		put(OPEN_COMMAND, new OpenAction());
		put(OPEN_RECENT_COMMAND, new OpenRecentAction());
		put(CLOSE_COMMAND, new CloseAction());
		put(CLOSEALL_COMMAND, new CloseAllAction());
		put(SAVE_COMMAND, new SaveAction());
		put(SAVEAS_COMMAND, new SaveAsAction());
		put(SAVEALL_COMMAND, new SaveAllAction());
		put(REVERT_COMMAND, new RevertAction());

		put(PARTY_COMMAND, new PartyAction());
		put(OPEN_PARTY_COMMAND, new OpenPartyAction());
		put(OPEN_RECENT_PARTY_COMMAND, new OpenRecentAction());
		put(CLOSE_PARTY_COMMAND, new ClosePartyAction());
		put(SAVE_PARTY_COMMAND, new SavePartyAction());
		put(SAVEAS_PARTY_COMMAND, new SaveAsPartyAction());

		put(PRINT_COMMAND, new PrintAction());
		put(EXPORT_COMMAND, new ExportAction());
		put(EXIT_COMMAND, new ExitAction());

		put(EDIT_COMMAND, new EditAction());
		put(UNDO_COMMAND, new UndoAction());
		put(REDO_COMMAND, new RedoAction());
		put(ADD_KIT_COMMAND, new AddKitAction());
		put(GENERATE_COMMAND, new GenerateAction());
		put(EQUIPMENTSET_COMMAND, new EquipmentSetAction());
		put(TEMP_BONUS_COMMAND, new TempBonusAction());
		put(PREFERENCES_COMMAND, new PreferencesAction());
		put(GMGEN_COMMAND, new GMGenAction());
		put(LOG_COMMAND, new DebugAction());
		put(LOGGING_LEVEL_COMMAND, new LoggingLevelAction());
		put(CALCULATOR_COMMAND, new CalculatorAction());
		put(COREVIEW_COMMAND, new CoreViewAction());
		put(SOLVERVIEW_COMMAND, new SolverViewAction());
		put(INSTALL_DATA_COMMAND, new InstallDataAction());
		put(FILTERS_COMMAND, new FiltersAction());
		put(KIT_FILTERS_COMMAND,
			new DefaultFiltersAction("mnuToolsFiltersKit", KIT_FILTERS_COMMAND,
									 KitFacade.class));
		put(RACE_FILTERS_COMMAND,
			new DefaultFiltersAction("mnuToolsFiltersRace", RACE_FILTERS_COMMAND,
									 RaceFacade.class));
		put(TEMPLATE_FILTERS_COMMAND,
			new DefaultFiltersAction("mnuToolsFiltersTemplate",
									 TEMPLATE_FILTERS_COMMAND,
									 TemplateFacade.class));
		put(CLASS_FILTERS_COMMAND,
			new DefaultFiltersAction("mnuToolsFiltersClass",
									 CLASS_FILTERS_COMMAND,
									 ClassFacade.class));
		put(ABILITY_FILTERS_COMMAND,
			new DefaultFiltersAction("mnuToolsFiltersAbility",
									 ABILITY_FILTERS_COMMAND,
									 AbilityFacade.class));
		put(SKILL_FILTERS_COMMAND,
			new DefaultFiltersAction("mnuToolsFiltersSkill",
									 SKILL_FILTERS_COMMAND,
									 SkillFacade.class));
		put(EQUIPMENT_FILTERS_COMMAND,
			new DefaultFiltersAction("mnuToolsFiltersEquipment",
									 EQUIPMENT_FILTERS_COMMAND,
									 ItemFacade.class));
		put(SPELL_FILTERS_COMMAND,
			new DefaultFiltersAction("mnuToolsFiltersSpell",
									 SPELL_GENERATORS_COMMAND,
									 SpellFacade.class));
		put(SOURCES_COMMAND, new SourcesAction());
		put(SOURCES_LOAD_COMMAND, new LoadSourcesAction());
		put(SOURCES_LOAD_SELECT_COMMAND, new LoadSourcesSelectAction());
		put(SOURCES_RELOAD_COMMAND, new ReloadSourcesAction());
		put(SOURCES_UNLOAD_COMMAND, new UnloadSourcesAction());
		put(GENERATORS_COMMAND, new GeneratorsAction());
		put(TREASURE_GENERATORS_COMMAND, new TreasureGeneratorsAction());
		put(STAT_GENERATORS_COMMAND,
			new DefaultGeneratorsAction("mnuToolsGeneratorsStat",
										STAT_GENERATORS_COMMAND,
										StatFacade.class));
		put(RACE_GENERATORS_COMMAND,
			new DefaultGeneratorsAction("mnuToolsGeneratorsRace",
										RACE_GENERATORS_COMMAND,
										RaceFacade.class));
		put(TEMPLATE_GENERATORS_COMMAND,
			new DefaultGeneratorsAction("mnuToolsGeneratorsTemplate",
										TEMPLATE_GENERATORS_COMMAND,
										TemplateFacade.class));
		put(CLASS_GENERATORS_COMMAND,
			new DefaultGeneratorsAction("mnuToolsGeneratorsClass",
										CLASS_GENERATORS_COMMAND,
										ClassFacade.class));
		put(ABILITY_GENERATORS_COMMAND,
			new DefaultGeneratorsAction("mnuToolsGeneratorsAbility",
										ABILITY_GENERATORS_COMMAND,
										AbilityFacade.class));
		put(SKILL_GENERATORS_COMMAND,
			new DefaultGeneratorsAction("mnuToolsGeneratorsSkill",
										SKILL_GENERATORS_COMMAND,
										SkillFacade.class));
		put(EQUIPMENT_GENERATORS_COMMAND,
			new DefaultGeneratorsAction("mnuToolsGeneratorsEquipment",
										EQUIPMENT_GENERATORS_COMMAND,
										ItemFacade.class));
		put(SPELL_GENERATORS_COMMAND,
			new DefaultGeneratorsAction("mnuToolsGeneratorsSpell",
										SPELL_GENERATORS_COMMAND,
										SpellFacade.class));
		put(TOOLS_COMMAND, new ToolsAction());

		put(HELP_COMMAND, new HelpAction());
		put(HELP_CONTEXT_COMMAND, new ContextHelpAction());
		put(HELP_DOCS_COMMAND, new DocsHelpAction());
		put(HELP_OGL_COMMAND, new OGLHelpAction());
		put(HELP_SPONSORS_COMMAND, new SponsorsHelpAction());
		put(HELP_TIPOFTHEDAY_COMMAND, new TipOfTheDayHelpAction());
		put(HELP_ABOUT_COMMAND, new AboutHelpAction());
	}

	private class EditAction extends PCGenAction
	{

		public EditAction()
		{
			super(MNU_EDIT);
		}

	}

	private class UndoAction extends PCGenAction//extends CharacterAction
	{

		public UndoAction()
		{
			super("mnuEditUndo", UNDO_COMMAND,  "shortcut Z");
			setEnabled(false);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}

	}

	private class RedoAction extends PCGenAction//extends CharacterAction
	{

		public RedoAction()
		{
			super("mnuEditRedo", REDO_COMMAND,  "shortcut Y");
			setEnabled(false);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}

	}

	private class AddKitAction extends CharacterAction
	{

		public AddKitAction()
		{
			super("mnuEditAddKit");
			setEnabled(false);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			KitSelectionDialog kitDialog =
					new KitSelectionDialog(frame, frame
						.getSelectedCharacterRef().get());
			Utility.setComponentRelativeLocation(frame, kitDialog);
			kitDialog.setVisible(true);			
		}

	}

	private class GenerateAction extends PCGenAction //extends CharacterAction
	{

		public GenerateAction()
		{
			super("mnuEditGenerate");
			setEnabled(false);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}

	}

	private class EquipmentSetAction extends PCGenAction
	{

		public EquipmentSetAction()
		{
			super("mnuEditEquipmentSet");
		}

	}

	private class TempBonusAction extends PCGenAction
	{

		public TempBonusAction()
		{
			super("mnuEditTempBonus");
		}

	}

	private class PreferencesAction extends PCGenAction
	{


		public PreferencesAction()
		{
			super(MNU_TOOLS_PREFERENCES, Icons.Preferences16);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			PCGenUIManager.displayPreferencesDialog();
		}

	}

	private static class GMGenAction extends PCGenAction
	{

		public GMGenAction()
		{
			super("mnuToolsGMGen", GMGEN_COMMAND, null, Icons.gmgen_icon, GMGenSystem.APPLICATION_NAME);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			PCGenUIManager.displayGmGen();
		}

	}

	private class DebugAction extends PCGenAction
	{

		private DebugDialog dialog = null;

		public DebugAction()
		{
			super("mnuToolsLog", LOG_COMMAND, "F10");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (dialog == null)
			{
				dialog = new DebugDialog(frame);
			}
			Utility.setComponentRelativeLocation(frame, dialog);
			dialog.setVisible(true);
		}

	}

	private class CalculatorAction extends PCGenAction
	{

		private CalculatorDialog dialog = null;

		public CalculatorAction()
		{
			super("mnuToolsCalculator", CALCULATOR_COMMAND, "F11");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (dialog == null)
			{
				dialog = new CalculatorDialog(frame);
			}
			Utility.setComponentRelativeLocation(frame, dialog);
			dialog.setVisible(true);
		}

	}

	private class CoreViewAction extends CharacterAction
	{


		public CoreViewAction()
		{
			super("mnuToolsCoreView", COREVIEW_COMMAND, "Shift-F11");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			CharacterFacade cf = frame.getSelectedCharacterRef().get();
			CoreViewFrame cvf = new CoreViewFrame(frame, cf);
			cvf.setVisible(true);
		}

	}

	private class SolverViewAction extends CharacterAction
	{


		public SolverViewAction()
		{
			super("mnuToolsSolverView", SOLVERVIEW_COMMAND, "Ctrl-F11");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			SolverViewFrame svf = new SolverViewFrame();
			svf.setVisible(true);
		}

	}

	private class LoggingLevelAction extends PCGenAction
	{

		public LoggingLevelAction()
		{
			super("mnuLoggingLevel");
		}

	}

	/**
	 * The tools menu action to open the install data dialog.
	 */
	private class InstallDataAction extends PCGenAction
	{

		public InstallDataAction()
		{
			super("mnuSourcesInstallData");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			DataInstaller di = new DataInstaller();
			di.setVisible(true);			
		}

	}
	
	private class FileAction extends PCGenAction
	{

		public FileAction()
		{
			super(MNU_FILE);
		}

	}

	private class NewAction extends PCGenAction
	{

		private final ReferenceFacade<?> ref;

		public NewAction()
		{
			super("mnuFileNew", NEW_COMMAND, "shortcut N", Icons.New16);
			ref = frame.getLoadedDataSetRef();
			ref.addReferenceListener(new SourceListener());
			setEnabled(ref.get() != null);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.createNewCharacter();
		}

		private class SourceListener implements ReferenceListener<Object>
		{

			@Override
			public void referenceChanged(ReferenceEvent<Object> e)
			{
				setEnabled(e.getNewReference() != null);
			}

		}

	}

	private class OpenAction extends PCGenAction
	{

		public OpenAction()
		{
			super("mnuFileOpen", OPEN_COMMAND, "shortcut O", Icons.Open16);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.showOpenCharacterChooser();
		}

	}

	private class OpenRecentAction extends PCGenAction
	{

		public OpenRecentAction()
		{
			super("mnuOpenRecent");
		}

	}

	private class CloseAction extends CharacterAction
	{

		public CloseAction()
		{
			super("mnuFileClose", CLOSE_COMMAND, "shortcut W", Icons.Close16);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.closeCharacter(frame.getSelectedCharacterRef().get());
		}

	}

	private class CloseAllAction extends CharacterAction
	{

		public CloseAllAction()
		{
			super("mnuFileCloseAll", CLOSEALL_COMMAND, Icons.CloseAll16);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.closeAllCharacters();
		}

	}

	private class SaveAction extends PCGenAction implements ReferenceListener<CharacterFacade>
	{

		private final FileRefListener fileListener = new FileRefListener();

		public SaveAction()
		{
			super("mnuFileSave", SAVE_COMMAND, "shortcut S", Icons.Save16);
			ReferenceFacade<CharacterFacade> ref = frame.getSelectedCharacterRef();
			ref.addReferenceListener(this);
			checkEnabled(ref.get());
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			final CharacterFacade pc = frame.getSelectedCharacterRef().get();
			if (pc == null)
			{
				return;
			}
			frame.saveCharacter(pc);
		}

		@Override
		public void referenceChanged(ReferenceEvent<CharacterFacade> e)
		{
			CharacterFacade oldRef = e.getOldReference();
			if (oldRef != null)
			{
				oldRef.getFileRef().removeReferenceListener(fileListener);
			}
			checkEnabled(e.getNewReference());
		}

		private void checkEnabled(CharacterFacade character)
		{
			if (character != null)
			{
				ReferenceFacade<File> file = character.getFileRef();
				file.addReferenceListener(fileListener);
				setEnabled(file.get() != null);
			}
			else
			{
				setEnabled(false);
			}
		}

		private class FileRefListener implements ReferenceListener<File>
		{

			@Override
			public void referenceChanged(ReferenceEvent<File> e)
			{
				setEnabled(e.getNewReference() != null);
			}

		}

	}

	private class SaveAsAction extends CharacterAction
	{

		public SaveAsAction()
		{
			super("mnuFileSaveAs", SAVEAS_COMMAND, "shift-shortcut S",
				  Icons.SaveAs16);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.showSaveCharacterChooser(frame.getSelectedCharacterRef().get());
		}

	}

	private class SaveAllAction extends CharacterAction
	{

		public SaveAllAction()
		{
			super("mnuFileSaveAll", SAVEALL_COMMAND, Icons.SaveAll16);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.saveAllCharacters();
		}

	}

	private class RevertAction extends CharacterAction
	{

		public RevertAction()
		{
			super("mnuFileRevertToSaved", REVERT_COMMAND, "shortcut R");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.revertCharacter(frame.getSelectedCharacterRef().get());
		}

	}

	private class PartyAction extends PCGenAction
	{

		public PartyAction()
		{
			super("mnuFileParty");
		}

	}

	private class OpenPartyAction extends PCGenAction
	{

		public OpenPartyAction()
		{
			super("mnuFilePartyOpen", OPEN_PARTY_COMMAND, Icons.Open16);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.showOpenPartyChooser();
		}

	}

	private class ClosePartyAction extends PCGenAction
	{

		public ClosePartyAction()
		{
			super("mnuFilePartyClose", CLOSE_PARTY_COMMAND, Icons.Close16);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.closeAllCharacters();
		}

	}

	private class SavePartyAction extends CharacterAction
	{

		public SavePartyAction()
		{
			super("mnuFilePartySave", SAVE_PARTY_COMMAND, Icons.Save16);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (frame.saveAllCharacters() && !CharacterManager.saveCurrentParty())
			{
				frame.showSavePartyChooser();
			}
		}

	}

	private class SaveAsPartyAction extends CharacterAction
	{

		public SaveAsPartyAction()
		{
			super("mnuFilePartySaveAs", SAVEAS_PARTY_COMMAND, Icons.SaveAs16);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.showSavePartyChooser();
		}

	}

	private class PrintAction extends CharacterAction
	{

		public PrintAction()
		{
			super("mnuFilePrint", PRINT_COMMAND, "shortcut P", Icons.Print16);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			PrintPreviewDialog.showPrintPreviewDialog(frame);
		}

	}

	private class ExportAction extends CharacterAction
	{

		public ExportAction()
		{
			super("mnuFileExport", EXPORT_COMMAND, "shift-shortcut P",
				Icons.Export16);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			ExportDialog.showExportDialog(frame);
		}

	}

	private class ExitAction extends PCGenAction
	{

		public ExitAction()
		{
			super("mnuFileExit", EXIT_COMMAND, "shortcut Q");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			PCGenUIManager.closePCGen();
		}

	}

	private class SourcesAction extends PCGenAction
	{

		public SourcesAction()
		{
			super("mnuSources");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}

	}

	private class LoadSourcesAction extends PCGenAction
	{

		public LoadSourcesAction()
		{
			super("mnuSourcesLoad");
		}

	}

	private class LoadSourcesSelectAction extends PCGenAction
	{

		public LoadSourcesSelectAction()
		{
			super("mnuSourcesLoadSelect", SOURCES_LOAD_COMMAND, "shortcut L");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.showSourceSelectionDialog();
		}

	}

	private class ReloadSourcesAction extends PCGenAction implements ReferenceListener<SourceSelectionFacade>
	{

		public ReloadSourcesAction()
		{
			super("mnuSourcesReload", SOURCES_RELOAD_COMMAND, "shift-shortcut R");
			ReferenceFacade<SourceSelectionFacade> currentSourceSelectionRef =
					frame.getCurrentSourceSelectionRef();
			currentSourceSelectionRef.addReferenceListener(this);
			checkEnabled(currentSourceSelectionRef.get());
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			SourceSelectionFacade sources =
					frame.getCurrentSourceSelectionRef().get();
			if (sources != null)
			{
				frame.unloadSources();
				frame.loadSourceSelection(sources);
			}
		}

		@Override
		public void referenceChanged(ReferenceEvent<SourceSelectionFacade> e)
		{
			checkEnabled(e.getNewReference());
		}

		private void checkEnabled(SourceSelectionFacade sources)
		{
			setEnabled(sources != null && !sources.getCampaigns().isEmpty());
		}

	}

	private class UnloadSourcesAction extends PCGenAction implements ReferenceListener<SourceSelectionFacade>
	{

		public UnloadSourcesAction()
		{
			super("mnuSourcesUnload", SOURCES_UNLOAD_COMMAND, "shortcut U");
			ReferenceFacade<SourceSelectionFacade> currentSourceSelectionRef =
					frame.getCurrentSourceSelectionRef();
			currentSourceSelectionRef.addReferenceListener(this);
			checkEnabled(currentSourceSelectionRef.get());
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.unloadSources();
		}

		@Override
		public void referenceChanged(ReferenceEvent<SourceSelectionFacade> e)
		{
			checkEnabled(e.getNewReference());
		}

		private void checkEnabled(SourceSelectionFacade sources)
		{
			setEnabled(sources != null && !sources.getCampaigns().isEmpty());
		}

	}

	private class GeneratorsAction extends PCGenAction
	{

		public GeneratorsAction()
		{
			super("mnuToolsGenerators");
			setEnabled(false);
		}

	}

	private class FiltersAction extends PCGenAction
	{

		public FiltersAction()
		{
			super("mnuToolsFilters");
			setEnabled(false);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}

	}

	private class TreasureGeneratorsAction extends PCGenAction
	{

		public TreasureGeneratorsAction()
		{
			super("mnuToolsGeneratorsTreasure", TREASURE_GENERATORS_COMMAND,
				  "shortcut T");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}

	}

	private class ToolsAction extends PCGenAction
	{

		public ToolsAction()
		{
			super(MNU_TOOLS);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}

	}

	private class HelpAction extends PCGenAction
	{

		public HelpAction()
		{
			super("mnuHelp", HELP_COMMAND);
		}

	}

	private class ContextHelpAction extends PCGenAction
	{

		public ContextHelpAction()
		{
			super("mnuHelpContext", HELP_CONTEXT_COMMAND, Icons.ContextualHelp16);
			setEnabled(false);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}

	}

	private class DocsHelpAction extends PCGenAction
	{

		public DocsHelpAction()
		{
			super("mnuHelpDocumentation", HELP_DOCS_COMMAND, "F1", Icons.Help16);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				Utility.viewInBrowser(new File(ConfigurationSettings
					.getDocsDir(), "index.html"));
			}
			catch (IOException ex)
			{
				Logging.errorPrint("Could not open docs in external browser", ex);
				JOptionPane.showMessageDialog(frame,
					LanguageBundle.getString("in_menuDocsNotOpenMsg"),
					LanguageBundle.getString("in_menuDocsNotOpenTitle"),
					JOptionPane.ERROR_MESSAGE);
			}
		}

	}

	private class OGLHelpAction extends PCGenAction
	{

		public OGLHelpAction()
		{
			super("mnuHelpOGL", HELP_OGL_COMMAND);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.showOGLDialog();
		}

	}

	private class SponsorsHelpAction extends PCGenAction
	{

		public SponsorsHelpAction()
		{
			super("mnuHelpSponsors", HELP_SPONSORS_COMMAND);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			Collection<Sponsor> sponsors = Globals.getGlobalContext().getReferenceContext().getConstructedCDOMObjects(Sponsor.class);
			if (sponsors.size() > 1)
			{
				frame.showSponsorsDialog();
				return;
			}
			JOptionPane.showMessageDialog(frame,
										  "There are no sponsors",
										  "Missing Sponsors",
										  JOptionPane.INFORMATION_MESSAGE);
		}

	}

	private class TipOfTheDayHelpAction extends PCGenAction
	{

		public TipOfTheDayHelpAction()
		{
			super("mnuHelpTipOfTheDay", HELP_TIPOFTHEDAY_COMMAND,
				  Icons.TipOfTheDay16);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.showTipsOfTheDay();
		}

	}

	private class AboutHelpAction extends PCGenAction
	{

		public AboutHelpAction()
		{
			super("mnuHelpAbout", HELP_ABOUT_COMMAND, Icons.About16);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.showAboutDialog();
		}

	}

	private class DefaultGeneratorsAction extends PCGenAction
	{

		private final Class<?> generatorClass;

		public DefaultGeneratorsAction(String prop, String command,
									   Class<?> generatorClass)
		{
			super(prop, command);
			this.generatorClass = generatorClass;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}

	}

	private class DefaultFiltersAction extends PCGenAction
	{

		private final Class<?> filterClass;

		public DefaultFiltersAction(String prop, String command,
									Class<?> filterClass)
		{
			super(prop, command);
			this.filterClass = filterClass;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}

	}

	private abstract class CharacterAction extends PCGenAction
	{

		private final ReferenceFacade<?> ref;

		public CharacterAction(String prop)
		{
			this(prop, null, null, null);
		}

		public CharacterAction(String prop, Icons icon)
		{
			this(prop, null, null, icon);
		}

		public CharacterAction(String prop, String command, String accelerator)
		{
			this(prop, command, accelerator, null);
		}

		public CharacterAction(String prop, String command, Icons icon)
		{
			this(prop, command, null, icon);
		}

		public CharacterAction(String prop, String command, String accelerator, Icons icon)
		{
			super(prop, command, accelerator, icon);
			ref = frame.getSelectedCharacterRef();
			ref.addReferenceListener(new CharacterListener());
			setEnabled(ref.get() != null);
		}

		private class CharacterListener implements ReferenceListener<Object>
		{

			@Override
			public void referenceChanged(ReferenceEvent<Object> e)
			{
				setEnabled(e.getNewReference() != null);
			}

		}

	}

}
