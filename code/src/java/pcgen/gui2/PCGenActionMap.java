/*
 * PCGenActionMap.java
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
 * Created on Aug 14, 2008, 3:51:27 PM
 */
package pcgen.gui2;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.swing.ActionMap;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import gmgen.GMGenSystem;
import pcgen.cdom.content.Sponsor;
import pcgen.core.Globals;
import pcgen.facade.core.AbilityFacade;
import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.CharacterStubFacade;
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

/**
 * The PCGenActionMap is the action map for the PCGenFrame, and as such
 * hold all of the actions that the PCGenFrame uses. The purpose of this
 * class is to hold all of the regarding actions for the menubar, toolbar,
 * and accessory popup menus that may use them. Since all of the action
 * handlers are Action objects they can be disabled or enabled to cause
 * all buttons that use the actions to update themselves accordingly.
 * @author Connor Petty &lt;cpmeister@users.sourceforge.net&gt;
 */
public final class PCGenActionMap extends ActionMap
{

	//the File menu commands
	static final String FILE_COMMAND = "file";
	static final String NEW_COMMAND = FILE_COMMAND + ".new";
	static final String OPEN_COMMAND = FILE_COMMAND + ".open";
	private static final String OPEN_RECENT_COMMAND = FILE_COMMAND + ".openrecent";
	static final String CLOSE_COMMAND = FILE_COMMAND + ".close";
	static final String CLOSEALL_COMMAND = FILE_COMMAND + ".closeall";
	static final String SAVE_COMMAND = FILE_COMMAND + ".save";
	static final String SAVEAS_COMMAND = FILE_COMMAND + ".saveas";
	static final String SAVEALL_COMMAND = FILE_COMMAND + ".saveall";
	static final String REVERT_COMMAND = FILE_COMMAND + ".reverttosaved";
	static final String PARTY_COMMAND = FILE_COMMAND + ".party";
	static final String OPEN_PARTY_COMMAND = PARTY_COMMAND + ".open";
	private static final String OPEN_RECENT_PARTY_COMMAND = PARTY_COMMAND + ".openrecent";
	static final String CLOSE_PARTY_COMMAND = PARTY_COMMAND + ".close";
	static final String SAVE_PARTY_COMMAND = PARTY_COMMAND + ".save";
	static final String SAVEAS_PARTY_COMMAND = PARTY_COMMAND + ".saveas";
	static final String PRINT_COMMAND = FILE_COMMAND + ".print";
	static final String EXPORT_COMMAND = FILE_COMMAND + ".export";
	static final String EXIT_COMMAND = FILE_COMMAND + ".exit";
	//the Edit menu commands
	static final String EDIT_COMMAND = "edit";
	private static final String UNDO_COMMAND = EDIT_COMMAND + ".undo";
	private static final String REDO_COMMAND = EDIT_COMMAND + ".redo";
	static final String ADD_KIT_COMMAND = EDIT_COMMAND + ".addkit";
	private static final String GENERATE_COMMAND = EDIT_COMMAND + ".regenerate";
	static final String TEMP_BONUS_COMMAND = EDIT_COMMAND + ".tempbonus";
	static final String EQUIPMENTSET_COMMAND = EDIT_COMMAND + ".equipmentset";
	//the Source menu commands
	static final String SOURCES_COMMAND = "sources";
	static final String SOURCES_LOAD_COMMAND = SOURCES_COMMAND + ".load";
	static final String SOURCES_LOAD_SELECT_COMMAND = SOURCES_COMMAND + ".select";
	static final String SOURCES_RELOAD_COMMAND = SOURCES_COMMAND + ".reload";
	static final String SOURCES_UNLOAD_COMMAND = SOURCES_COMMAND + ".unload";
	static final String INSTALL_DATA_COMMAND = SOURCES_COMMAND + ".installData";
	//the tools menu commands
	static final String TOOLS_COMMAND = "tools";
	static final String FILTERS_COMMAND = TOOLS_COMMAND + ".filters";
	static final String KIT_FILTERS_COMMAND = FILTERS_COMMAND + ".kit";
	static final String RACE_FILTERS_COMMAND = FILTERS_COMMAND + ".race";
	static final String TEMPLATE_FILTERS_COMMAND = FILTERS_COMMAND + ".template";
	static final String CLASS_FILTERS_COMMAND = FILTERS_COMMAND + ".class";
	static final String ABILITY_FILTERS_COMMAND = FILTERS_COMMAND + ".ability";
	static final String SKILL_FILTERS_COMMAND = FILTERS_COMMAND + ".skill";
	static final String EQUIPMENT_FILTERS_COMMAND = FILTERS_COMMAND + ".equipment";
	static final String SPELL_FILTERS_COMMAND = FILTERS_COMMAND + ".spell";
	static final String GENERATORS_COMMAND = TOOLS_COMMAND + ".generators";
	static final String TREASURE_GENERATORS_COMMAND = GENERATORS_COMMAND + ".treasure";
	static final String RACE_GENERATORS_COMMAND = GENERATORS_COMMAND + ".race";
	static final String TEMPLATE_GENERATORS_COMMAND = GENERATORS_COMMAND + ".template";
	static final String CLASS_GENERATORS_COMMAND = GENERATORS_COMMAND + ".class";
	static final String STAT_GENERATORS_COMMAND = GENERATORS_COMMAND + ".stat";
	static final String ABILITY_GENERATORS_COMMAND = GENERATORS_COMMAND + ".ability";
	static final String SKILL_GENERATORS_COMMAND = GENERATORS_COMMAND + ".skill";
	static final String EQUIPMENT_GENERATORS_COMMAND = GENERATORS_COMMAND + ".equipment";
	static final String SPELL_GENERATORS_COMMAND = GENERATORS_COMMAND + ".spell";
	static final String PREFERENCES_COMMAND = TOOLS_COMMAND + ".preferences";
	static final String GMGEN_COMMAND = TOOLS_COMMAND + ".gmgen";
	static final String LOG_COMMAND = TOOLS_COMMAND + ".log";
	static final String LOGGING_LEVEL_COMMAND = TOOLS_COMMAND + ".loggingLevel";
	static final String CALCULATOR_COMMAND = TOOLS_COMMAND + ".calculator";
	static final String COREVIEW_COMMAND = TOOLS_COMMAND + ".coreview";
	static final String SOLVERVIEW_COMMAND = TOOLS_COMMAND + ".solverview";
	//the help menu commands
	static final String HELP_COMMAND = "help";
	private static final String HELP_CONTEXT_COMMAND = HELP_COMMAND + ".context";
	static final String HELP_DOCS_COMMAND = HELP_COMMAND + ".docs";
	static final String HELP_OGL_COMMAND = HELP_COMMAND + ".ogl";
	static final String HELP_SPONSORS_COMMAND = HELP_COMMAND + ".sponsors";
	static final String HELP_TIPOFTHEDAY_COMMAND = HELP_COMMAND + ".tod";
	static final String HELP_ABOUT_COMMAND = HELP_COMMAND + ".about";
	private static final long serialVersionUID = 4950563297815275199L;
	private final PCGenFrame frame;
	
	private static final String MNU_TOOLS = "mnuTools"; //$NON-NLS-1$
	private static final String MNU_TOOLS_PREFERENCES = "mnuToolsPreferences"; //$NON-NLS-1$
	private static final String MNU_EDIT = "mnuEdit"; //$NON-NLS-1$
	private static final String MNU_FILE = "mnuFile"; //$NON-NLS-1$

	PCGenActionMap(PCGenFrame frame)
	{
		this.frame = frame;
		initActions();
	}

	private void initActions()
	{
		put(PCGenActionMap.FILE_COMMAND, new FileAction());
		put(PCGenActionMap.NEW_COMMAND, new NewAction());
		put(PCGenActionMap.OPEN_COMMAND, new OpenAction());
		put(PCGenActionMap.OPEN_RECENT_COMMAND, new OpenRecentAction());
		put(PCGenActionMap.CLOSE_COMMAND, new CloseAction());
		put(PCGenActionMap.CLOSEALL_COMMAND, new CloseAllAction());
		put(PCGenActionMap.SAVE_COMMAND, new SaveAction());
		put(PCGenActionMap.SAVEAS_COMMAND, new SaveAsAction());
		put(PCGenActionMap.SAVEALL_COMMAND, new SaveAllAction());
		put(PCGenActionMap.REVERT_COMMAND, new RevertAction());

		put(PCGenActionMap.PARTY_COMMAND, new PartyAction());
		put(PCGenActionMap.OPEN_PARTY_COMMAND, new OpenPartyAction());
		put(PCGenActionMap.OPEN_RECENT_PARTY_COMMAND, new OpenRecentAction());
		put(PCGenActionMap.CLOSE_PARTY_COMMAND, new ClosePartyAction());
		put(PCGenActionMap.SAVE_PARTY_COMMAND, new SavePartyAction());
		put(PCGenActionMap.SAVEAS_PARTY_COMMAND, new SaveAsPartyAction());

		put(PCGenActionMap.PRINT_COMMAND, new PrintAction());
		put(PCGenActionMap.EXPORT_COMMAND, new ExportAction());
		put(PCGenActionMap.EXIT_COMMAND, new ExitAction());

		put(PCGenActionMap.EDIT_COMMAND, new EditAction());
		put(PCGenActionMap.UNDO_COMMAND, new UndoAction());
		put(PCGenActionMap.REDO_COMMAND, new RedoAction());
		put(PCGenActionMap.ADD_KIT_COMMAND, new AddKitAction());
		put(PCGenActionMap.GENERATE_COMMAND, new GenerateAction());
		put(PCGenActionMap.EQUIPMENTSET_COMMAND, new EquipmentSetAction());
		put(PCGenActionMap.TEMP_BONUS_COMMAND, new TempBonusAction());
		put(PCGenActionMap.PREFERENCES_COMMAND, new PreferencesAction());
		put(PCGenActionMap.GMGEN_COMMAND, new GMGenAction());
		put(PCGenActionMap.LOG_COMMAND, new DebugAction());
		put(PCGenActionMap.LOGGING_LEVEL_COMMAND, new LoggingLevelAction());
		put(PCGenActionMap.CALCULATOR_COMMAND, new CalculatorAction());
		put(PCGenActionMap.COREVIEW_COMMAND, new CoreViewAction());
		put(PCGenActionMap.SOLVERVIEW_COMMAND, new SolverViewAction());
		put(PCGenActionMap.INSTALL_DATA_COMMAND, new InstallDataAction());
		put(PCGenActionMap.FILTERS_COMMAND, new FiltersAction());
		put(PCGenActionMap.KIT_FILTERS_COMMAND,
				new DefaultFiltersAction("mnuToolsFiltersKit", PCGenActionMap.KIT_FILTERS_COMMAND,
						KitFacade.class));
		put(PCGenActionMap.RACE_FILTERS_COMMAND,
				new DefaultFiltersAction("mnuToolsFiltersRace", PCGenActionMap.RACE_FILTERS_COMMAND,
						RaceFacade.class));
		put(PCGenActionMap.TEMPLATE_FILTERS_COMMAND,
				new DefaultFiltersAction("mnuToolsFiltersTemplate",
						PCGenActionMap.TEMPLATE_FILTERS_COMMAND,
						TemplateFacade.class));
		put(PCGenActionMap.CLASS_FILTERS_COMMAND,
				new DefaultFiltersAction("mnuToolsFiltersClass",
						PCGenActionMap.CLASS_FILTERS_COMMAND,
						ClassFacade.class));
		put(PCGenActionMap.ABILITY_FILTERS_COMMAND,
				new DefaultFiltersAction("mnuToolsFiltersAbility",
						PCGenActionMap.ABILITY_FILTERS_COMMAND,
						AbilityFacade.class));
		put(PCGenActionMap.SKILL_FILTERS_COMMAND,
				new DefaultFiltersAction("mnuToolsFiltersSkill",
						PCGenActionMap.SKILL_FILTERS_COMMAND,
						SkillFacade.class));
		put(PCGenActionMap.EQUIPMENT_FILTERS_COMMAND,
				new DefaultFiltersAction("mnuToolsFiltersEquipment",
						PCGenActionMap.EQUIPMENT_FILTERS_COMMAND,
						ItemFacade.class));
		put(PCGenActionMap.SPELL_FILTERS_COMMAND,
				new DefaultFiltersAction("mnuToolsFiltersSpell",
						PCGenActionMap.SPELL_GENERATORS_COMMAND,
						SpellFacade.class));
		put(PCGenActionMap.SOURCES_COMMAND, new SourcesAction());
		put(PCGenActionMap.SOURCES_LOAD_COMMAND, new LoadSourcesAction());
		put(PCGenActionMap.SOURCES_LOAD_SELECT_COMMAND, new LoadSourcesSelectAction());
		put(PCGenActionMap.SOURCES_RELOAD_COMMAND, new ReloadSourcesAction());
		put(PCGenActionMap.SOURCES_UNLOAD_COMMAND, new UnloadSourcesAction());
		put(PCGenActionMap.GENERATORS_COMMAND, new GeneratorsAction());
		put(PCGenActionMap.TREASURE_GENERATORS_COMMAND, new TreasureGeneratorsAction());
		put(PCGenActionMap.STAT_GENERATORS_COMMAND,
				new DefaultGeneratorsAction("mnuToolsGeneratorsStat",
						PCGenActionMap.STAT_GENERATORS_COMMAND,
						StatFacade.class));
		put(PCGenActionMap.RACE_GENERATORS_COMMAND,
				new DefaultGeneratorsAction("mnuToolsGeneratorsRace",
						PCGenActionMap.RACE_GENERATORS_COMMAND,
						RaceFacade.class));
		put(PCGenActionMap.TEMPLATE_GENERATORS_COMMAND,
				new DefaultGeneratorsAction("mnuToolsGeneratorsTemplate",
						PCGenActionMap.TEMPLATE_GENERATORS_COMMAND,
						TemplateFacade.class));
		put(PCGenActionMap.CLASS_GENERATORS_COMMAND,
				new DefaultGeneratorsAction("mnuToolsGeneratorsClass",
						PCGenActionMap.CLASS_GENERATORS_COMMAND,
						ClassFacade.class));
		put(PCGenActionMap.ABILITY_GENERATORS_COMMAND,
				new DefaultGeneratorsAction("mnuToolsGeneratorsAbility",
						PCGenActionMap.ABILITY_GENERATORS_COMMAND,
						AbilityFacade.class));
		put(PCGenActionMap.SKILL_GENERATORS_COMMAND,
				new DefaultGeneratorsAction("mnuToolsGeneratorsSkill",
						PCGenActionMap.SKILL_GENERATORS_COMMAND,
						SkillFacade.class));
		put(PCGenActionMap.EQUIPMENT_GENERATORS_COMMAND,
				new DefaultGeneratorsAction("mnuToolsGeneratorsEquipment",
						PCGenActionMap.EQUIPMENT_GENERATORS_COMMAND,
						ItemFacade.class));
		put(PCGenActionMap.SPELL_GENERATORS_COMMAND,
				new DefaultGeneratorsAction("mnuToolsGeneratorsSpell",
						PCGenActionMap.SPELL_GENERATORS_COMMAND,
						SpellFacade.class));
		put(PCGenActionMap.TOOLS_COMMAND, new ToolsAction());

		put(PCGenActionMap.HELP_COMMAND, new HelpAction());
		put(PCGenActionMap.HELP_CONTEXT_COMMAND, new ContextHelpAction());
		put(PCGenActionMap.HELP_DOCS_COMMAND, new DocsHelpAction());
		put(PCGenActionMap.HELP_OGL_COMMAND, new OGLHelpAction());
		put(PCGenActionMap.HELP_SPONSORS_COMMAND, new SponsorsHelpAction());
		put(PCGenActionMap.HELP_TIPOFTHEDAY_COMMAND, new TipOfTheDayHelpAction());
		put(PCGenActionMap.HELP_ABOUT_COMMAND, new AboutHelpAction());
	}

	private static final class EditAction extends PCGenAction
	{

		private static final long serialVersionUID = -9078596668679439146L;

		private EditAction()
		{
			super(PCGenActionMap.MNU_EDIT);
		}

	}

	private static final class UndoAction extends PCGenAction//extends CharacterAction
	{

		private static final long serialVersionUID = 8410667084630982140L;

		private UndoAction()
		{
			super("mnuEditUndo", PCGenActionMap.UNDO_COMMAND,  "shortcut Z");
			setEnabled(false);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}

	}

	private static final class RedoAction extends PCGenAction //extends CharacterAction
	{

		private static final long serialVersionUID = 4140191055107154447L;

		private RedoAction()
		{
			super("mnuEditRedo", PCGenActionMap.REDO_COMMAND,  "shortcut Y");
			setEnabled(false);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}

	}

	private final class AddKitAction extends CharacterAction
	{

		private static final long serialVersionUID = -5255540274499077671L;

		private AddKitAction()
		{
			super();
			setEnabled(false);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			JDialog kitDialog =
					new KitSelectionDialog(frame, frame
						.getSelectedCharacterRef().get());
			Utility.setDialogRelativeLocation(frame, kitDialog);
			kitDialog.setVisible(true);			
		}

	}

	private static final class GenerateAction extends PCGenAction //extends CharacterAction
	{

		private static final long serialVersionUID = -3846348145829681369L;

		private GenerateAction()
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

	private static final class EquipmentSetAction extends PCGenAction
	{

		private static final long serialVersionUID = 3020657204590227563L;

		private EquipmentSetAction()
		{
			super("mnuEditEquipmentSet");
		}

	}

	private static final class TempBonusAction extends PCGenAction
	{

		private static final long serialVersionUID = -1339054139211308334L;

		private TempBonusAction()
		{
			super("mnuEditTempBonus");
		}

	}

	private static final class PreferencesAction extends PCGenAction
	{


		private static final long serialVersionUID = -625166998373728421L;

		private PreferencesAction()
		{
			super(PCGenActionMap.MNU_TOOLS_PREFERENCES, Icons.Preferences16);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			PCGenUIManager.displayPreferencesDialog();
		}

	}

	private static final class GMGenAction extends PCGenAction
	{

		private static final long serialVersionUID = 9154912699482061065L;

		private GMGenAction()
		{
			super("mnuToolsGMGen", PCGenActionMap.GMGEN_COMMAND, null, Icons.gmgen_icon, GMGenSystem.APPLICATION_NAME);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			PCGenUIManager.displayGmGen();
		}

	}

	private final class DebugAction extends PCGenAction
	{

		private static final long serialVersionUID = 4052592315501771741L;
		private DebugDialog dialog = null;

		private DebugAction()
		{
			super("mnuToolsLog", PCGenActionMap.LOG_COMMAND, "F10");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (dialog == null)
			{
				dialog = new DebugDialog(frame);
			}
			Utility.setDialogRelativeLocation(frame, dialog);
			dialog.setVisible(true);
		}

	}

	private final class CalculatorAction extends PCGenAction
	{

		private static final long serialVersionUID = -2512446087277042940L;
		private CalculatorDialog dialog = null;

		private CalculatorAction()
		{
			super("mnuToolsCalculator", PCGenActionMap.CALCULATOR_COMMAND, "F11");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (dialog == null)
			{
				dialog = new CalculatorDialog(frame);
			}
			Utility.setDialogRelativeLocation(frame, dialog);
			dialog.setVisible(true);
		}

	}

	private final class CoreViewAction extends CharacterAction
	{


		private static final long serialVersionUID = -6767019180906791251L;

		private CoreViewAction()
		{
			super("mnuToolsCoreView", PCGenActionMap.COREVIEW_COMMAND, "Shift-F11");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			CharacterFacade cf = frame.getSelectedCharacterRef().get();
			Component cvf = new CoreViewFrame(frame, cf);
			cvf.setVisible(true);
		}

	}

	private final class SolverViewAction extends CharacterAction
	{


		private static final long serialVersionUID = -6583197277730752995L;

		private SolverViewAction()
		{
			super("mnuToolsSolverView", PCGenActionMap.SOLVERVIEW_COMMAND, "Ctrl-F11");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			Component svf = new SolverViewFrame();
			svf.setVisible(true);
		}

	}

	private static final class LoggingLevelAction extends PCGenAction
	{

		private static final long serialVersionUID = -819189451817913013L;

		private LoggingLevelAction()
		{
			super("mnuLoggingLevel");
		}

	}

	/**
	 * The tools menu action to open the install data dialog.
	 */
	private static final class InstallDataAction extends PCGenAction
	{

		private static final long serialVersionUID = 5228500058028889697L;

		private InstallDataAction()
		{
			super("mnuSourcesInstallData");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			Component di = new DataInstaller();
			di.setVisible(true);			
		}

	}
	
	private static final class FileAction extends PCGenAction
	{

		private static final long serialVersionUID = -7046746896841698403L;

		private FileAction()
		{
			super(PCGenActionMap.MNU_FILE);
		}

	}

	private final class NewAction extends PCGenAction
	{

		private static final long serialVersionUID = 2392273805000178884L;
		private final ReferenceFacade<?> ref;

		private NewAction()
		{
			super("mnuFileNew", PCGenActionMap.NEW_COMMAND, "shortcut N", Icons.New16);
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

	private final class OpenAction extends PCGenAction
	{

		private static final long serialVersionUID = -4492151389120578827L;

		private OpenAction()
		{
			super("mnuFileOpen", PCGenActionMap.OPEN_COMMAND, "shortcut O", Icons.Open16);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.showOpenCharacterChooser();
		}

	}

	private static final class OpenRecentAction extends PCGenAction
	{

		private static final long serialVersionUID = 4079790340889732763L;

		private OpenRecentAction()
		{
			super("mnuOpenRecent");
		}

	}

	private final class CloseAction extends CharacterAction
	{

		private static final long serialVersionUID = -2337052408501691893L;

		private CloseAction()
		{
			super("mnuFileClose", PCGenActionMap.CLOSE_COMMAND, "shortcut W", Icons.Close16);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.closeCharacter(frame.getSelectedCharacterRef().get());
		}

	}

	private final class CloseAllAction extends CharacterAction
	{

		private static final long serialVersionUID = -6061973680151950030L;

		private CloseAllAction()
		{
			super("mnuFileCloseAll", PCGenActionMap.CLOSEALL_COMMAND, Icons.CloseAll16);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.closeAllCharacters();
		}

	}

	private final class SaveAction extends PCGenAction implements ReferenceListener<CharacterFacade>
	{

		private static final long serialVersionUID = 1621039673357108556L;
		private final ReferenceListener fileListener = new FileRefListener();

		private SaveAction()
		{
			super("mnuFileSave", PCGenActionMap.SAVE_COMMAND, "shortcut S", Icons.Save16);
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

		private void checkEnabled(CharacterStubFacade character)
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

	private final class SaveAsAction extends CharacterAction
	{

		private static final long serialVersionUID = -3696739426779760145L;

		private SaveAsAction()
		{
			super("mnuFileSaveAs", PCGenActionMap.SAVEAS_COMMAND, "shift-shortcut S",
				  Icons.SaveAs16);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.showSaveCharacterChooser(frame.getSelectedCharacterRef().get());
		}

	}

	private final class SaveAllAction extends CharacterAction
	{

		private static final long serialVersionUID = -3479723030520940999L;

		private SaveAllAction()
		{
			super("mnuFileSaveAll", PCGenActionMap.SAVEALL_COMMAND, Icons.SaveAll16);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.saveAllCharacters();
		}

	}

	private final class RevertAction extends CharacterAction
	{

		private static final long serialVersionUID = -3202010575937778347L;

		private RevertAction()
		{
			super("mnuFileRevertToSaved", PCGenActionMap.REVERT_COMMAND, "shortcut R");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.revertCharacter(frame.getSelectedCharacterRef().get());
		}

	}

	private static final class PartyAction extends PCGenAction
	{

		private static final long serialVersionUID = 2974296486316248646L;

		private PartyAction()
		{
			super("mnuFileParty");
		}

	}

	private final class OpenPartyAction extends PCGenAction
	{

		private static final long serialVersionUID = 3129313978634709648L;

		private OpenPartyAction()
		{
			super("mnuFilePartyOpen", PCGenActionMap.OPEN_PARTY_COMMAND, Icons.Open16);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.showOpenPartyChooser();
		}

	}

	private final class ClosePartyAction extends PCGenAction
	{

		private static final long serialVersionUID = 4351918389206745175L;

		private ClosePartyAction()
		{
			super("mnuFilePartyClose", PCGenActionMap.CLOSE_PARTY_COMMAND, Icons.Close16);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.closeAllCharacters();
		}

	}

	private final class SavePartyAction extends CharacterAction
	{

		private static final long serialVersionUID = 5254781662690235461L;

		private SavePartyAction()
		{
			super("mnuFilePartySave", PCGenActionMap.SAVE_PARTY_COMMAND, Icons.Save16);
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

	private final class SaveAsPartyAction extends CharacterAction
	{

		private static final long serialVersionUID = 5095552401695568286L;

		private SaveAsPartyAction()
		{
			super("mnuFilePartySaveAs", PCGenActionMap.SAVEAS_PARTY_COMMAND, Icons.SaveAs16);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.showSavePartyChooser();
		}

	}

	private final class PrintAction extends CharacterAction
	{

		private static final long serialVersionUID = -5627174908452228949L;

		private PrintAction()
		{
			super("mnuFilePrint", PCGenActionMap.PRINT_COMMAND, "shortcut P", Icons.Print16);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			PrintPreviewDialog.showPrintPreviewDialog(frame);
		}

	}

	private final class ExportAction extends CharacterAction
	{

		private static final long serialVersionUID = 2157556612732788341L;

		private ExportAction()
		{
			super("mnuFileExport", PCGenActionMap.EXPORT_COMMAND, "shift-shortcut P",
				Icons.Export16);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			ExportDialog.showExportDialog(frame);
		}

	}

	private static final class ExitAction extends PCGenAction
	{

		private static final long serialVersionUID = 7110804175509770920L;

		private ExitAction()
		{
			super("mnuFileExit", PCGenActionMap.EXIT_COMMAND, "shortcut Q");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			PCGenUIManager.closePCGen();
		}

	}

	private static final class SourcesAction extends PCGenAction
	{

		private static final long serialVersionUID = -2953033271014145741L;

		private SourcesAction()
		{
			super("mnuSources");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}

	}

	private static final class LoadSourcesAction extends PCGenAction
	{

		private static final long serialVersionUID = -501258627845044253L;

		private LoadSourcesAction()
		{
			super("mnuSourcesLoad");
		}

	}

	private final class LoadSourcesSelectAction extends PCGenAction
	{

		private static final long serialVersionUID = -537376679086813455L;

		private LoadSourcesSelectAction()
		{
			super("mnuSourcesLoadSelect", PCGenActionMap.SOURCES_LOAD_COMMAND, "shortcut L");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.showSourceSelectionDialog();
		}

	}

	private final class ReloadSourcesAction extends PCGenAction implements ReferenceListener<SourceSelectionFacade>
	{

		private static final long serialVersionUID = -8879680233032855529L;

		private ReloadSourcesAction()
		{
			super("mnuSourcesReload", PCGenActionMap.SOURCES_RELOAD_COMMAND, "shift-shortcut R");
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

	private final class UnloadSourcesAction extends PCGenAction implements ReferenceListener<SourceSelectionFacade>
	{

		private static final long serialVersionUID = 8180779313278682059L;

		private UnloadSourcesAction()
		{
			super("mnuSourcesUnload", PCGenActionMap.SOURCES_UNLOAD_COMMAND, "shortcut U");
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

	private static final class GeneratorsAction extends PCGenAction
	{

		private static final long serialVersionUID = -7320615166740682151L;

		private GeneratorsAction()
		{
			super("mnuToolsGenerators");
			setEnabled(false);
		}

	}

	private static final class FiltersAction extends PCGenAction
	{

		private static final long serialVersionUID = -2352116298977947650L;

		private FiltersAction()
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

	private static final class TreasureGeneratorsAction extends PCGenAction
	{

		private static final long serialVersionUID = 5296374334751722605L;

		private TreasureGeneratorsAction()
		{
			super("mnuToolsGeneratorsTreasure", PCGenActionMap.TREASURE_GENERATORS_COMMAND,
				  "shortcut T");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}

	}

	private static final class ToolsAction extends PCGenAction
	{

		private static final long serialVersionUID = -6435173437585361853L;

		private ToolsAction()
		{
			super(PCGenActionMap.MNU_TOOLS);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}

	}

	private static final class HelpAction extends PCGenAction
	{

		private static final long serialVersionUID = -2006348316040218060L;

		private HelpAction()
		{
			super("mnuHelp", PCGenActionMap.HELP_COMMAND);
		}

	}

	private static final class ContextHelpAction extends PCGenAction
	{

		private static final long serialVersionUID = 7220714628475146471L;

		private ContextHelpAction()
		{
			super("mnuHelpContext", PCGenActionMap.HELP_CONTEXT_COMMAND, Icons.ContextualHelp16);
			setEnabled(false);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}

	}

	private final class DocsHelpAction extends PCGenAction
	{

		private static final long serialVersionUID = -683754922828155651L;

		private DocsHelpAction()
		{
			super("mnuHelpDocumentation", PCGenActionMap.HELP_DOCS_COMMAND, "F1", Icons.Help16);
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

	private final class OGLHelpAction extends PCGenAction
	{

		private static final long serialVersionUID = -6941674273963002267L;

		private OGLHelpAction()
		{
			super("mnuHelpOGL", PCGenActionMap.HELP_OGL_COMMAND);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.showOGLDialog();
		}

	}

	private final class SponsorsHelpAction extends PCGenAction
	{

		private static final long serialVersionUID = 4942686578487566535L;

		private SponsorsHelpAction()
		{
			super("mnuHelpSponsors", PCGenActionMap.HELP_SPONSORS_COMMAND);
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

	private final class TipOfTheDayHelpAction extends PCGenAction
	{

		private static final long serialVersionUID = -6250509312451031282L;

		private TipOfTheDayHelpAction()
		{
			super("mnuHelpTipOfTheDay", PCGenActionMap.HELP_TIPOFTHEDAY_COMMAND,
				  Icons.TipOfTheDay16);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.showTipsOfTheDay();
		}

	}

	private final class AboutHelpAction extends PCGenAction
	{

		private static final long serialVersionUID = -1898454253772351751L;

		private AboutHelpAction()
		{
			super("mnuHelpAbout", PCGenActionMap.HELP_ABOUT_COMMAND, Icons.About16);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.showAboutDialog();
		}

	}

	private static final class DefaultGeneratorsAction extends PCGenAction
	{

		private static final long serialVersionUID = 5832212361068408244L;
		private final Class<?> generatorClass;

		private DefaultGeneratorsAction(String prop, String command,
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

	private static final class DefaultFiltersAction extends PCGenAction
	{

		private static final long serialVersionUID = -7265618866064067142L;

		private DefaultFiltersAction(String prop, String command,
		                             Class<?> filterClass)
		{
			super(prop, command);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}

	}

	private abstract class CharacterAction extends PCGenAction
	{

		private static final long serialVersionUID = -3071795868018935599L;
		private final ReferenceFacade<?> ref;

		private CharacterAction()
		{
			this("mnuEditAddKit", null, null, null);
		}

		private CharacterAction(String prop, String command, String accelerator)
		{
			this(prop, command, accelerator, null);
		}

		private CharacterAction(String prop, String command, Icons icon)
		{
			this(prop, command, null, icon);
		}

		private CharacterAction(String prop, String command, String accelerator, Icons icon)
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
