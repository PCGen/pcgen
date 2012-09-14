/*
 * PreferencesDialog.java
 *
 * Copyright 2001 (C) B. K. Oxley (binkley) <binkley@alumni.rice.edu>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on July 8th, 2002.
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.gui;

import gmgen.gui.PreferencesPanel;
import gmgen.gui.PreferencesPluginsPanel;
import gmgen.pluginmgr.GMBComponent;
import gmgen.pluginmgr.GMBMessage;
import gmgen.pluginmgr.messages.PreferencesPanelAddMessage;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.lang.SystemUtils;

import pcgen.cdom.base.Constants;
import pcgen.core.Globals;
import pcgen.core.PaperInfo;
import pcgen.core.SettingsHandler;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui.panes.FlippingSplitPane;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.gui.utils.LinkableHtmlMessage;
import pcgen.gui.utils.SkinManager;
import pcgen.gui.utils.Utility;
import pcgen.gui.utils.WholeNumberField;
import pcgen.gui2.prefs.CharacterStatsPanel;
import pcgen.gui2.prefs.CopySettingsPanel;
import pcgen.gui2.prefs.DefaultsPanel;
import pcgen.gui2.prefs.HouseRulesPanel;
import pcgen.gui2.prefs.LanguagePanel;
import pcgen.gui2.prefs.MonsterPanel;
import pcgen.gui2.prefs.PCGenPrefsPanel;
import pcgen.gui2.prefs.SourcesPanel;
import pcgen.util.Logging;
import pcgen.system.LanguageBundle;
import pcgen.system.PCGenSettings;
import pcgen.util.SkinLFResourceChecker;

/**
 *  PCGen preferences dialog
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
final class PreferencesDialog extends JDialog
{
	static final long serialVersionUID = 388745661262349737L;

	// Used to create the entries for the max spell level combos
	private static final int SPELLLVLMIN = 0;
	private static final int SPELLLVLMAX = 9;
	private static String[] potionSpellLevel =
			new String[SPELLLVLMAX - SPELLLVLMIN + 1];
	private static String[] wandSpellLevel =
			new String[SPELLLVLMAX - SPELLLVLMIN + 1];

	// Resource strings
	private static String in_allowMetamagic =
			LanguageBundle.getString("in_Prefs_allowMetamagic");
	private static String in_alwaysOverwrite =
			LanguageBundle.getString("in_Prefs_alwaysOverwrite");
	private static String in_appearance =
			LanguageBundle.getString("in_Prefs_appearance");
	private static String in_anyAutoEquip =
			LanguageBundle.getString("in_Prefs_anyAutoEquip");
	private static String in_autoEquip =
			LanguageBundle.getString("in_Prefs_autoEquip");
	private static String in_autoEquipRace =
			LanguageBundle.getString("in_Prefs_autoEquipRace");
	private static String in_autoEquipMasterwork =
			LanguageBundle.getString("in_Prefs_autoEquipMasterwork");
	private static String in_autoEquipMagic =
			LanguageBundle.getString("in_Prefs_autoEquipMagic");
	private static String in_autoEquipExotic =
			LanguageBundle.getString("in_Prefs_autoEquipExotic");
	private static String in_browserPath =
			LanguageBundle.getString("in_Prefs_browserPath");
	private static String in_clearBrowserPath =
			LanguageBundle.getString("in_Prefs_clearBrowserPath");
	private static String in_color =
			LanguageBundle.getString("in_Prefs_color");
	private static String in_colorPrereqQualify =
			LanguageBundle.getString("in_Prefs_colorPrereqQualify");
	private static String in_colorPrereqFail =
			LanguageBundle.getString("in_Prefs_colorPrereqFail");
	private static String in_colorAutoFeat =
			LanguageBundle.getString("in_Prefs_colorAutoFeat");
	private static String in_colorVirtFeat =
			LanguageBundle.getString("in_Prefs_colorVirtFeat");

	private static String in_colorSourceRelease =
			LanguageBundle.getString("in_Prefs_colorStatusRelease");
	private static String in_colorSourceAlpha =
			LanguageBundle.getString("in_Prefs_colorStatusAlpha");
	private static String in_colorSourceBeta =
			LanguageBundle.getString("in_Prefs_colorStatusBeta");
	private static String in_colorSourceTest =
			LanguageBundle.getString("in_Prefs_colorStatusTest");

	private static String in_charTabPlacement =
			LanguageBundle.getString("in_Prefs_charTabPlacement");
	private static String in_charTabLabel =
			LanguageBundle.getString("in_Prefs_charTabLabel");
	private static String in_character =
			LanguageBundle.getString("in_Prefs_character");
	//LanguageBundle.getString("in_Prefs_chooseSkin");
	private static String in_cmNone =
			LanguageBundle.getString("in_Prefs_cmNone");
	private static String in_cmSelect =
			LanguageBundle.getString("in_Prefs_cmSelect");
	private static String in_cmSelectExit =
			LanguageBundle.getString("in_Prefs_cmSelectExit");
	private static String in_dialogTitle =
			LanguageBundle.getFormattedString("in_Prefs_title", Constants.APPLICATION_NAME); //$NON-NLS-1$
	private static String in_displayOpts =
			LanguageBundle.getString("in_Prefs_displayOpts");
	private static String in_expertGUI =
			LanguageBundle.getString("in_Prefs_expertGUI");
	private static String in_enforceSpending =
			LanguageBundle.getString("in_Prefs_enforceSpending");
	private static String in_equipment =
			LanguageBundle.getString("in_Prefs_equipment");
	//	private static String in_featWindow = LanguageBundle.getString("in_Prefs_featWindow");
	private static String in_hp = LanguageBundle.getString("in_Prefs_hp");
	private static String in_houseRules =
			LanguageBundle.getString("in_Prefs_houseRules");
	private static String in_hpWindow =
			LanguageBundle.getString("in_Prefs_hpWindow");
	private static String in_invalidToHitText =
			LanguageBundle.getString("in_Prefs_invalidToHitText");
	private static String in_invalidDmgText =
			LanguageBundle.getString("in_Prefs_invalidDmgText");
	private static String in_location =
			LanguageBundle.getString("in_Prefs_location");
	private static String in_lookAndFeel =
			LanguageBundle.getString("in_Prefs_lookAndFeel");
	private static String in_levelUp =
			LanguageBundle.getString("in_Prefs_levelUp");
	private static String in_monsters =
			LanguageBundle.getString("in_Prefs_monsters");
	private static String in_mainTabPlacement =
			LanguageBundle.getString("in_Prefs_mainTabPlacement");
	private static String in_noAutoEquip =
			LanguageBundle.getString("in_Prefs_noAutoEquip");
	private static String in_output =
			LanguageBundle.getString("in_Prefs_output");
	private static String in_input =
			LanguageBundle.getString("in_Prefs_input");
	private static String in_printDeprecation = LanguageBundle
			.getString("in_Prefs_printDeprecation");
	private static String in_printUnconstructed = LanguageBundle
			.getString("in_Prefs_printUnconstructed");
	private static String in_outputSheetEqSet =
			LanguageBundle.getString("in_Prefs_templateEqSet");
	private static String in_pcgen = Constants.APPLICATION_NAME;
	private static String in_potionMax =
			LanguageBundle.getString("in_Prefs_potionMax");
	private static String in_paperType =
			LanguageBundle.getString("in_Prefs_paperType");
	private static String in_postExportCommandStandard =
			LanguageBundle.getString("in_Prefs_postExportCommandStandard");
	private static String in_postExportCommandPDF =
			LanguageBundle.getString("in_Prefs_postExportCommandPDF");
	private static String in_removeTemp =
			LanguageBundle.getString("in_Prefs_removeTemp");
	private static String in_statWindow =
			LanguageBundle.getString("in_Prefs_statWindow");
	private static String in_showToolTips =
			LanguageBundle.getString("in_Prefs_showToolTips");
	private static String in_showToolBar =
			LanguageBundle.getString("in_Prefs_showToolBar");
	private static String in_showFeatDescription =
			LanguageBundle.getString("in_Prefs_showFeatDesciption");
	private static String in_singleChoiceOption =
			LanguageBundle.getString("in_Prefs_singleChoiceOption");
	private static String in_skinnedLAF =
			LanguageBundle.getString("in_Prefs_skinnedLAF");
	private static String in_saveOutputSheetWithPC =
			LanguageBundle.getString("in_Prefs_saveOutputSheetWithPC");
	private static String in_showMemory =
			LanguageBundle.getString("in_Prefs_showMemory");
	private static String in_showImagePreview =
			LanguageBundle.getString("in_Prefs_showImagePreview");
	private static String in_showSkillModifierBreakdown =
		LanguageBundle.getString("in_Prefs_showSkillModifierBreakdown");
	private static String in_showSkillRanksBreakdown =
		LanguageBundle.getString("in_Prefs_showSkillRanksBreakdown");
	private static String in_showSingleBoxPerBundle =
		LanguageBundle.getString("in_Prefs_showSingleBoxPerBundle");
	private static String in_tabs = LanguageBundle.getString("in_Prefs_tabs");
	private static String in_tabLabelPlain =
			LanguageBundle.getString("in_Prefs_tabLabelPlain");
	private static String in_tabLabelEpic =
			LanguageBundle.getString("in_Prefs_tabLabelEpic");
	private static String in_tabLabelRace =
			LanguageBundle.getString("in_Prefs_tabLabelRace");
	private static String in_tabLabelNetHack =
			LanguageBundle.getString("in_Prefs_tabLabelNetHack");
	private static String in_tabLabelFull =
			LanguageBundle.getString("in_Prefs_tabLabelFull");
	private static String in_tabPosTop =
			LanguageBundle.getString("in_Prefs_tabPosTop");
	private static String in_tabPosBottom =
			LanguageBundle.getString("in_Prefs_tabPosBottom");
	private static String in_tabPosLeft =
			LanguageBundle.getString("in_Prefs_tabPosLeft");
	private static String in_tabPosRight =
			LanguageBundle.getString("in_Prefs_tabPosRight");
	private static String in_tabAbilities =
			LanguageBundle.getString("in_Prefs_tabAbilities");
	private static String in_useAutoWaitCursor =
			LanguageBundle.getString("in_Prefs_useAutoWaitCursor");
	private static String in_useOutputNamesEquipment =
			LanguageBundle.getString("in_Prefs_useOutputNamesEquipment");
	private static String in_useOutputNamesSpells =
			LanguageBundle.getString("in_Prefs_useOutputNamesSpells");
	private static String in_wandMax =
			LanguageBundle.getString("in_Prefs_wandMax");
	private static String in_warnFirstLevelUp =
			LanguageBundle.getString("in_Prefs_warnFirstLevelUp");
	private static String in_weaponProfPrintout =
			LanguageBundle.getString("in_Prefs_weaponProfPrintout");
	private static String in_skillChoice =
			LanguageBundle.getString("in_Prefs_skillChoiceLabel");
	private static String in_skillChoiceNone =
			LanguageBundle.getString("in_Prefs_skillChoiceNone");
	private static String in_skillChoiceUntrained =
			LanguageBundle.getString("in_Prefs_skillChoiceUntrained");
	private static String in_skillChoiceAll =
			LanguageBundle.getString("in_Prefs_skillChoiceAll");
	private static String in_skillChoiceAsUI =
			LanguageBundle.getString("in_Prefs_skillChoiceAsUI");
	private static String[] singleChoiceMethods =
			{in_cmNone, in_cmSelect, in_cmSelectExit};
	private static String in_choose = "...";
	private ButtonGroup groupFilesDir;
	private DefaultTreeModel settingsModel;
	private FlippingSplitPane splitPane;
	private JButton browserPathButton;
	private JButton clearBrowserPathButton;
	private JButton featAutoColor;
	private JButton featVirtualColor;
	private JButton sourceStatusRelease;
	private JButton sourceStatusAlpha;
	private JButton sourceStatusBeta;
	private JButton sourceStatusTest;
	private JButton outputSheetEqSetButton;
	private JButton outputSheetHTMLDefaultButton;
	private JButton outputSheetPDFDefaultButton;
	private JButton outputSheetSpellsDefaultButton;
	private JButton pcgenCharacterDirButton;
	private JButton pcgenCustomDirButton;
	private JButton pcgenVendorDataDirButton;
	private JButton pcgenDataDirButton;
	private JButton pcgenDocsDirButton;
	private JButton pcgenFilesDirButton;
	private JButton pcgenOutputSheetDirButton;
	private JButton pcgenPreviewDirButton;
	private JButton pcgenPortraitsDirButton;
	private JButton pcgenSystemDirButton;
	private JButton pcgenBackupCharacterDirButton;
	private JButton prereqFailColor;

	// Colors
	private JButton prereqQualifyColor;
	private JButton themepack;

	// Equipment
	private JCheckBox allowMetamagicInEqBuilder = new JCheckBox();
	private JCheckBox autoMethod1 = new JCheckBox();
	private JCheckBox autoMethod2 = new JCheckBox();
	private JCheckBox autoMethod3 = new JCheckBox();
	private JCheckBox autoMethod4 = new JCheckBox();

	private JCheckBox displayAbilitiesAsTab = new JCheckBox();
	private JCheckBox expertGUICheckBox = new JCheckBox();
	private JCheckBox featDescriptionShown = new JCheckBox();
	//	private JCheckBox featDialogShownAtLevelUp = new JCheckBox();

	// Level Up
	private JCheckBox hpDialogShownAtLevelUp = new JCheckBox();
	private JCheckBox maxHpAtFirstLevel = new JCheckBox();
	private JCheckBox maxHpAtFirstClassLevel = new JCheckBox();
	private JCheckBox maxHpAtFirstPCClassLevelOnly = new JCheckBox();
	private JCheckBox printSpellsWithPC = new JCheckBox();
	private JCheckBox removeTempFiles;
	private JCheckBox saveOutputSheetWithPC = new JCheckBox();
	private JCheckBox showToolbar = new JCheckBox();
	private JCheckBox showWarningAtFirstLevelUp = new JCheckBox();
	private JCheckBox statDialogShownAtLevelUp = new JCheckBox();
	private JCheckBox showSkillModifier = new JCheckBox();
	private JCheckBox showSkillRanks = new JCheckBox();
	private JCheckBox enforceSpendingBeforeLevelUp = new JCheckBox();

	// Displayed
	private JCheckBox toolTipTextShown = new JCheckBox();
	private JCheckBox showMemory = new JCheckBox();
	private JCheckBox showImagePreview = new JCheckBox();

	private JCheckBox useOutputNamesEquipment = new JCheckBox();
	private JCheckBox useOutputNamesSpells = new JCheckBox();
	private JCheckBox waitCursor = new JCheckBox();
	private JCheckBox weaponProfPrintout;
	private JComboBoxEx charTabPlacementCombo;
	private JComboBoxEx cmbChoiceMethods = new JComboBoxEx(singleChoiceMethods);
	//	private JComboBoxEx crossClassSkillCostCombo = new JComboBoxEx(new String[]{ "0  ", "1  ", "2  " });

	// Tab Options
	private JComboBoxEx mainTabPlacementCombo;
	private JComboBoxEx paperType = new JComboBoxEx();
	private JComboBoxEx potionMaxLevel = new JComboBoxEx();
	private JComboBoxEx skillChoice = new JComboBoxEx();
	private JComboBoxEx tabLabelsCombo;
	private JComboBoxEx wandMaxLevel = new JComboBoxEx();
	private JPanel controlPanel;
	private JPanel settingsPanel;
	private JRadioButton autoEquipCreate;

	// "HP Roll Methods"
	private JRadioButton hpAutomax =
			new JRadioButton(LanguageBundle.getString("in_Prefs_hpAutoMax"));
	private JRadioButton hpAverage =
			new JRadioButton(LanguageBundle.getString("in_Prefs_hpAverage"));
	private JRadioButton hpPercentage =
			new JRadioButton(LanguageBundle.getString("in_Prefs_hpPercentage"));
	private JRadioButton hpStandard =
			new JRadioButton(LanguageBundle.getString("in_Prefs_hpStandard"));
	private JRadioButton hpUserRolled =
			new JRadioButton(LanguageBundle.getString("in_Prefs_hpUserRolled"));
	private JRadioButton hpAverageRoundedUp =
			new JRadioButton(LanguageBundle.getString("in_Prefs_hpAverageRoundedUp"));

	private JRadioButton noAutoEquipCreate;
	private JRadioButton pcgenFilesDirRadio;
	private JRadioButton selectFilesDirRadio;
	private JRadioButton skinnedLookFeel = new JRadioButton();
	private JRadioButton usersFilesDirRadio;
	private JScrollPane settingsScroll;

	// Input
	private JCheckBox printDeprecationMessages = new JCheckBox();
	private JCheckBox printUnconstructedDetail = new JCheckBox();

	// Location
	private JTextField browserPath;
	private JTextField outputSheetEqSet;
	private JTextField outputSheetHTMLDefault;
	private JTextField outputSheetPDFDefault;
	private JTextField outputSheetSpellsDefault;
	private JTextField pcgenCharacterDir;
	private JTextField pcgenCustomDir;
	private JTextField pcgenVendorDataDir;
	private JTextField pcgenDataDir;
	private JTextField pcgenDocsDir;
	private JTextField pcgenFilesDir;
	private JTextField pcgenOutputSheetDir;
	private JCheckBox pcgenCreateBackupCharacter = new JCheckBox();
	private JTextField pcgenBackupCharacterDir;
	private JTextField pcgenPreviewDir;

	// Output
	private JTextField pcgenPortraitsDir;
	private JTextField pcgenSystemDir;
	private JTextField postExportCommandStandard;
	private JTextField postExportCommandPDF;
	private JTextField themepackLabel;
	private JTree settingsTree;
	private JTextField invalidToHitText;
	private JTextField invalidDmgText;
	private JCheckBox alwaysOverwrite;
	private JCheckBox showSingleBoxPerBundle;
	
	// Listeners
	private PrefsButtonListener prefsButtonHandler = new PrefsButtonListener();
	private final TextFocusLostListener textFieldListener =
			new TextFocusLostListener();
	private WholeNumberField hpPct = new WholeNumberField(0, 6);
	//	private String[] allSameValue = new String[STATMAX - STATMIN + 1];

	// "House Rules"
	private PCGenPrefsPanel houseRulesPanel;

	// Look and Feel
	private JRadioButton[] laf;
	private String[] paperNames = null;

	// "Character Stats"
	private PCGenPrefsPanel characterStatsPanel;

	private LanguagePanel languagePanel;

	// "Monsters"
	private PCGenPrefsPanel monsterPanel;

	// "Defaults"
	private PCGenPrefsPanel defaultsPanel;

	private PCGenPrefsPanel sourcesPanel;

	// "Copy Settings"
	private CopySettingsPanel copySettingsPanel;

	//Plugins
	private static PreferencesComponent compInst;
	private PreferencesPluginsPanel pluginsPanel;

	private PreferencesDialog(JFrame parent, boolean modal)
	{
		super(parent, in_dialogTitle, modal);

		buildSettingsTreeAndPanel();
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(splitPane, BorderLayout.CENTER);
		this.getContentPane().add(controlPanel, BorderLayout.SOUTH);

		applyOptionValuesToControls();
		settingsTree.setSelectionRow(1);

		pack();
	}

	public static void show(JFrame frame)
	{
		PreferencesDialog prefsDialog;

		prefsDialog = new PreferencesDialog(frame, true);

		Utility.centerDialog(prefsDialog);

		prefsDialog.setVisible(true);
	}

	public static PreferencesComponent getPreferencesComponent()
	{
		if (compInst == null)
		{
			compInst = new PreferencesComponent();
		}
		return compInst;
	}

	private void addPluginPanes(DefaultMutableTreeNode rootNode,
		DefaultMutableTreeNode pluginNode)
	{
		List<String> nameList = compInst.getNameList();
		List<PreferencesPanel> panelList = compInst.getPanelList();
		HashMap<String, JTabbedPane> nodeMap =
				new HashMap<String, JTabbedPane>();

		for (int i = 0; i < nameList.size(); i++)
		{
			String name = nameList.get(i);
			PreferencesPanel panel = panelList.get(i);
			JTabbedPane tpane;
			if (nodeMap.get(name) == null)
			{
				tpane = new JTabbedPane();
			}
			else
			{
				tpane = nodeMap.get(name);
			}

			tpane.add(panel.toString(), panel);
			nodeMap.put(name, tpane);
		}

		Set<String> keySet = nodeMap.keySet();
		for (String name : keySet)
		{
			JTabbedPane tpane = nodeMap.get(name);

			pluginNode.add(new DefaultMutableTreeNode(name));
			settingsPanel.add(tpane, name);
		}

		if (pluginsPanel == null)
		{
			pluginsPanel = new PreferencesPluginsPanel();
		}
		JTabbedPane tpane = new JTabbedPane();
		tpane.add(pluginsPanel.toString(), pluginsPanel);
		settingsPanel.add(tpane, "Plugins");
		rootNode.add(pluginNode);
	}

	public void applyPluginPreferences()
	{
		List<PreferencesPanel> panelList = compInst.getPanelList();

		for (int i = 0; i < panelList.size(); i++)
		{
			PreferencesPanel panel = panelList.get(i);
			panel.applyPreferences();
		}
		pluginsPanel.applyPreferences();
	}

	private void setOptionsBasedOnControls()
	{
		// Abilities - character stats
		characterStatsPanel.setOptionsBasedOnControls();

		// Hit points
		if (hpStandard.isSelected())
		{
			SettingsHandler.setHPRollMethod(Constants.HP_STANDARD);
		}
		else if (hpAutomax.isSelected())
		{
			SettingsHandler.setHPRollMethod(Constants.HP_AUTO_MAX);
		}
		else if (hpAverage.isSelected())
		{
			SettingsHandler.setHPRollMethod(Constants.HP_AVERAGE);
		}
		else if (hpPercentage.isSelected())
		{
			SettingsHandler.setHPRollMethod(Constants.HP_PERCENTAGE);
		}
		else if (hpUserRolled.isSelected())
		{
			SettingsHandler.setHPRollMethod(Constants.HP_USER_ROLLED);
		}
		else if (hpAverageRoundedUp.isSelected())
		{
			SettingsHandler.setHPRollMethod(Constants.HP_AVERAGE_ROUNDED_UP);
		}

		SettingsHandler.setHPPercent(hpPct.getValue());
		SettingsHandler.setHPMaxAtFirstLevel(maxHpAtFirstLevel.isSelected());
		SettingsHandler.setHPMaxAtFirstClassLevel(maxHpAtFirstClassLevel.isSelected());
		SettingsHandler.setHPMaxAtFirstPCClassLevelOnly(maxHpAtFirstPCClassLevelOnly.isSelected());

		// House Rules
		houseRulesPanel.setOptionsBasedOnControls();

		//		SettingsHandler.setIntCrossClassSkillCost(crossClassSkillCostCombo.getSelectedIndex());

		// Monsters
		monsterPanel.setOptionsBasedOnControls();

		// Defaults
		defaultsPanel.setOptionsBasedOnControls();

		// Tab Options
		switch (mainTabPlacementCombo.getSelectedIndex())
		{
			case 0:
				SettingsHandler.setTabPlacement(SwingConstants.TOP);

				break;

			case 1:
				SettingsHandler.setTabPlacement(SwingConstants.BOTTOM);

				break;

			case 2:
				SettingsHandler.setTabPlacement(SwingConstants.LEFT);

				break;

			case 3:
				SettingsHandler.setTabPlacement(SwingConstants.RIGHT);

				break;

			default:
				Logging
					.errorPrint("In PreferencesDialog.setOptionsBasedOnControls (mainTabPlacementCombo) the index "
						+ mainTabPlacementCombo.getSelectedIndex()
						+ " is unsupported.");

				break;
		}

		switch (charTabPlacementCombo.getSelectedIndex())
		{
			case 0:
				SettingsHandler.setChaTabPlacement(SwingConstants.TOP);

				break;

			case 1:
				SettingsHandler.setChaTabPlacement(SwingConstants.BOTTOM);

				break;

			case 2:
				SettingsHandler.setChaTabPlacement(SwingConstants.LEFT);

				break;

			case 3:
				SettingsHandler.setChaTabPlacement(SwingConstants.RIGHT);

				break;

			default:
				Logging
					.errorPrint("In PreferencesDialog.setOptionsBasedOnControls (charTabPlacementCombo) the index "
						+ charTabPlacementCombo.getSelectedIndex()
						+ " is unsupported.");

				break;
		}

		switch (tabLabelsCombo.getSelectedIndex())
		{
			case 0:
				SettingsHandler
					.setNameDisplayStyle(Constants.DISPLAY_STYLE_NAME);

				break;

			case 1:
				SettingsHandler
					.setNameDisplayStyle(Constants.DISPLAY_STYLE_NAME_CLASS);

				break;

			case 2:
				SettingsHandler
					.setNameDisplayStyle(Constants.DISPLAY_STYLE_NAME_RACE);

				break;

			case 3:
				SettingsHandler
					.setNameDisplayStyle(Constants.DISPLAY_STYLE_NAME_RACE_CLASS);

				break;

			case 4:
				SettingsHandler
					.setNameDisplayStyle(Constants.DISPLAY_STYLE_NAME_FULL);

				break;

			default:
				Logging
					.errorPrint("In PreferencesDialog.setOptionsBasedOnControls (tabLabelsCombo) the index "
						+ tabLabelsCombo.getSelectedIndex()
						+ " is unsupported.");

				break;
		}

		SettingsHandler.setAbilitiesShownAsATab(displayAbilitiesAsTab
			.isSelected());
		SettingsHandler.setExpertGUI(expertGUICheckBox.isSelected());
		SettingsHandler.setIncludeSkills(skillChoice.getSelectedIndex());

		// Display Options
		SettingsHandler.setToolTipTextShown(toolTipTextShown.isSelected());
		SettingsHandler.setShowMemoryArea(showMemory.isSelected());
		SettingsHandler.setShowImagePreview(showImagePreview.isSelected());
		SettingsHandler.setToolBarShown(showToolbar.isSelected());
		SettingsHandler.setUseWaitCursor(waitCursor.isSelected());
		SettingsHandler.setGUIUsesOutputNameEquipment(useOutputNamesEquipment
			.isSelected());
		SettingsHandler.setGUIUsesOutputNameSpells(useOutputNamesSpells
			.isSelected());
		SettingsHandler.setSingleChoicePreference(cmbChoiceMethods
			.getSelectedIndex());
		SettingsHandler.setUseFeatBenefits(!featDescriptionShown.isSelected());
		SettingsHandler.setShowSkillModifier(showSkillModifier.isSelected());
		SettingsHandler.setShowSkillRanks(showSkillRanks.isSelected());

		// Look and Feel
		int sourceIndex = 500; // XXX - magic number?

		for (int i = 0; i < laf.length; ++i)
		{
			if (laf[i].isSelected())
			{
				sourceIndex = i;
			}
		}

		if (sourceIndex < laf.length)
		{
			if (SettingsHandler.getLookAndFeel() != sourceIndex)
			{
				SettingsHandler.setLookAndFeel(sourceIndex);
				UIFactory.setLookAndFeel(sourceIndex);
			}
		}
		else if (skinnedLookFeel.isSelected())
		{
			if ((SkinLFResourceChecker.getMissingResourceCount() == 0))
			{
				if (SettingsHandler.getSkinLFThemePack().length() == 0)
				{
					ShowMessageDelegate.showMessageDialog(LanguageBundle
						.getString("in_Prefs_noSkinError"), in_pcgen,
						MessageType.WARNING);
				}
				else
				{
					SettingsHandler.setLookAndFeel(laf.length);

					try
					{
						SkinManager.applySkin();
					}
					catch (Exception e) //This is what applySkin actually throws...
					{
						SettingsHandler.setLookAndFeel(0);
						UIFactory.setLookAndFeel(0);
						ShowMessageDelegate.showMessageDialog(LanguageBundle
							.getString("in_Prefs_skinSetError")
							+ e.toString(), in_pcgen, MessageType.ERROR);
					}
				}
			}
			else
			{
				Logging.errorPrint(SkinLFResourceChecker
					.getMissingResourceMessage());

				//final String missingLibMsg = LanguageBundle.getString("MissingLibMessage").replace('|', '\n');
				//GuiFacade.showMessageDialog(null, SkinLFResourceChecker.getMissingResourceMessage() + missingLibMsg, Constants.APPLICATION_NAME, GuiFacade.WARNING_MESSAGE);
				new LinkableHtmlMessage(this, SkinLFResourceChecker
					.getMissingResourceMessage(), Constants.APPLICATION_NAME)
					.setVisible(true);
			}
		}

		// Level up
		SettingsHandler.setShowHPDialogAtLevelUp(hpDialogShownAtLevelUp.isSelected());
		//SettingsHandler.setShowFeatDialogAtLevelUp(featDialogShownAtLevelUp.isSelected());
		SettingsHandler.setShowStatDialogAtLevelUp(statDialogShownAtLevelUp.isSelected());
		SettingsHandler.setShowWarningAtFirstLevelUp(showWarningAtFirstLevelUp.isSelected());
		SettingsHandler.setEnforceSpendingBeforeLevelUp(enforceSpendingBeforeLevelUp.isSelected());

		// Equipment
		SettingsHandler.setMetamagicAllowedInEqBuilder(allowMetamagicInEqBuilder.isSelected());
		SettingsHandler.setMaxPotionSpellLevel(potionMaxLevel.getSelectedIndex() + SPELLLVLMIN);
		SettingsHandler.setMaxWandSpellLevel(wandMaxLevel.getSelectedIndex() + SPELLLVLMIN);

		// Turn it off temporarily so we can set the values
		SettingsHandler.setWantToLoadMasterworkAndMagic(false);

		SettingsHandler.setAutogen(Constants.AUTOGEN_RACIAL,          autoMethod1.isSelected());
		SettingsHandler.setAutogen(Constants.AUTOGEN_MASTERWORK,      autoMethod2.isSelected());
		SettingsHandler.setAutogen(Constants.AUTOGEN_MAGIC,           autoMethod3.isSelected());
		SettingsHandler.setAutogen(Constants.AUTOGEN_EXOTIC_MATERIAL, autoMethod4.isSelected());

		 // Now set it properly
		SettingsHandler.setWantToLoadMasterworkAndMagic(noAutoEquipCreate.isSelected());

		// Language
		languagePanel.setOptionsBasedOnControls();
		
		// Input
		SettingsHandler.setOutputDeprecationMessages(printDeprecationMessages.isSelected());
		SettingsHandler.setInputUnconstructedMessages(printUnconstructedDetail.isSelected());
		
		// Location -- added 10 April 2000 by sage_sam
		SettingsHandler.setBrowserPath(browserPath.getText());
		SettingsHandler.setPcgPath(new File(pcgenCharacterDir.getText()));
		SettingsHandler.setPortraitsPath(new File(pcgenPortraitsDir.getText()));
		SettingsHandler.setPcgenCustomDir(new File(pcgenCustomDir.getText()));
		SettingsHandler.setPcgenVendorDataDir(new File(pcgenVendorDataDir
			.getText()));
		SettingsHandler.setPccFilesLocation(new File(pcgenDataDir.getText()));
		SettingsHandler.setPcgenDocsDir(new File(pcgenDocsDir.getText()));
		SettingsHandler.setPcgenSystemDir(new File(pcgenSystemDir.getText()));
		if (pcgenFilesDirRadio.isSelected())
		{
			SettingsHandler.setFilePaths("pcgen");
		}
		else if (usersFilesDirRadio.isSelected())
		{
			SettingsHandler.setFilePaths("user");
		}
		else
		{
			SettingsHandler.setFilePaths(pcgenFilesDir.getText());
		}
		SettingsHandler.setPcgenFilesDir(new File(pcgenFilesDir.getText()));
		SettingsHandler.setPcgenOutputSheetDir(new File(pcgenOutputSheetDir
			.getText()));
		SettingsHandler.setCreatePcgBackup(pcgenCreateBackupCharacter
			.isSelected());
		SettingsHandler.setBackupPcgPath(new File(pcgenBackupCharacterDir
			.getText()));

		SettingsHandler.setPcgenPreviewDir(new File(pcgenPreviewDir.getText()));
		
		// Output
		Globals.selectPaper((String) paperType.getSelectedItem());

		if (SettingsHandler.getCleanupTempFiles()
			|| removeTempFiles.isSelected())
		{
			SettingsHandler.setCleanupTempFiles(removeTempFiles.isSelected());
		}

		if (SettingsHandler.getWeaponProfPrintout() != weaponProfPrintout
			.isSelected())
		{
			SettingsHandler.setWeaponProfPrintout(weaponProfPrintout
				.isSelected());
		}

		if (SettingsHandler.getAlwaysOverwrite()
			|| alwaysOverwrite.isSelected())
		{
			SettingsHandler.setAlwaysOverwrite(alwaysOverwrite.isSelected());
		}

		if (SettingsHandler.getShowSingleBoxPerBundle()
			|| showSingleBoxPerBundle.isSelected())
		{
			SettingsHandler.setShowSingleBoxPerBundle(showSingleBoxPerBundle
				.isSelected());
		}

		// added 10 April 2000 by sage_sam
		SettingsHandler.setSelectedCharacterHTMLOutputSheet(
			outputSheetHTMLDefault.getText(), null);
		SettingsHandler.setSelectedCharacterPDFOutputSheet(
			outputSheetPDFDefault.getText(), null);
		SettingsHandler.setSelectedEqSetTemplate(outputSheetEqSet.getText());
		SettingsHandler.setSaveOutputSheetWithPC(saveOutputSheetWithPC
			.isSelected());
		SettingsHandler.setSelectedSpellSheet(outputSheetSpellsDefault
			.getText());
		SettingsHandler.setPrintSpellsWithPC(printSpellsWithPC.isSelected());
		SettingsHandler.setPostExportCommandStandard(postExportCommandStandard
			.getText());
		SettingsHandler.setPostExportCommandPDF(postExportCommandPDF.getText());
		SettingsHandler.setInvalidToHitText(invalidToHitText.getText());
		SettingsHandler.setInvalidDmgText(invalidDmgText.getText());

		// Sources
		sourcesPanel.setOptionsBasedOnControls();

		// Copy Settings
		copySettingsPanel.setOptionsBasedOnControls();
		
		// Now get any panels affected to refresh
		CharacterInfo ci = PCGen_Frame1.getCharacterPane();
		if (ci != null)
		{
			ci.refresh();
		}
	}

	private void applyOptionValuesToControls()
	{

		// Abilities - character stats
		characterStatsPanel.applyOptionValuesToControls();
		
		// Hit Points
		switch (SettingsHandler.getHPRollMethod())
		{
			case Constants.HP_AUTO_MAX:
				hpAutomax.setSelected(true);

				break;

			case Constants.HP_AVERAGE:
				hpAverage.setSelected(true);

				break;

			case Constants.HP_PERCENTAGE:
				hpPercentage.setSelected(true);

				break;

			case Constants.HP_USER_ROLLED:
				hpUserRolled.setSelected(true);

				break;

			case Constants.HP_AVERAGE_ROUNDED_UP:
				hpAverageRoundedUp.setSelected(true);

				break;

			case Constants.HP_STANDARD:
				//No break
			default:
				hpStandard.setSelected(true);

				break;
		}

		hpPct.setValue(SettingsHandler.getHPPercent());
		maxHpAtFirstLevel.setSelected(SettingsHandler.isHPMaxAtFirstLevel());
		maxHpAtFirstClassLevel.setSelected(SettingsHandler.isHPMaxAtFirstClassLevel());
		maxHpAtFirstPCClassLevelOnly.setSelected(SettingsHandler.isHPMaxAtFirstPCClassLevelOnly());

		// House Rules
		houseRulesPanel.applyOptionValuesToControls();
		
		//		crossClassSkillCostCombo.setSelectedIndex(SettingsHandler.getIntCrossClassSkillCost());

		// Monsters
		monsterPanel.applyOptionValuesToControls();
		
		// Experience
		defaultsPanel.applyOptionValuesToControls();
		
		// Colors
		prereqQualifyColor.setForeground(new Color(SettingsHandler
			.getPrereqQualifyColor()));
		prereqFailColor.setForeground(new Color(SettingsHandler
			.getPrereqFailColor()));
		featAutoColor.setForeground(new Color(SettingsHandler
			.getFeatAutoColor()));
		featVirtualColor.setForeground(new Color(SettingsHandler
			.getFeatVirtualColor()));
		sourceStatusRelease.setForeground(new Color(SettingsHandler
				.getSourceStatusReleaseColor()));
		sourceStatusAlpha.setForeground(new Color(SettingsHandler
				.getSourceStatusAlphaColor()));
		sourceStatusBeta.setForeground(new Color(SettingsHandler
				.getSourceStatusBetaColor()));
		sourceStatusTest.setForeground(new Color(SettingsHandler
				.getSourceStatusTestColor()));

		// Tab options
		switch (SettingsHandler.getTabPlacement())
		{
			case SwingConstants.TOP:
				mainTabPlacementCombo.setSelectedIndex(0);

				break;

			case SwingConstants.BOTTOM:
				mainTabPlacementCombo.setSelectedIndex(1);

				break;

			case SwingConstants.LEFT:
				mainTabPlacementCombo.setSelectedIndex(2);

				break;

			case SwingConstants.RIGHT:
				mainTabPlacementCombo.setSelectedIndex(3);

				break;

			default:
				Logging
					.errorPrint("In PreferencesDialog.applyOptionValuesToControls (tab placement) the tab option "
						+ SettingsHandler.getTabPlacement()
						+ " is unsupported.");

				break;
		}

		switch (SettingsHandler.getChaTabPlacement())
		{
			case SwingConstants.TOP:
				charTabPlacementCombo.setSelectedIndex(0);

				break;

			case SwingConstants.BOTTOM:
				charTabPlacementCombo.setSelectedIndex(1);

				break;

			case SwingConstants.LEFT:
				charTabPlacementCombo.setSelectedIndex(2);

				break;

			case SwingConstants.RIGHT:
				charTabPlacementCombo.setSelectedIndex(3);

				break;

			default:
				Logging
					.errorPrint("In PreferencesDialog.applyOptionValuesToControls (cha tab placement) the tab option "
						+ SettingsHandler.getChaTabPlacement()
						+ " is unsupported.");

				break;
		}

		switch (SettingsHandler.getNameDisplayStyle())
		{
			case Constants.DISPLAY_STYLE_NAME:
				tabLabelsCombo.setSelectedIndex(0);

				break;

			case Constants.DISPLAY_STYLE_NAME_CLASS:
				tabLabelsCombo.setSelectedIndex(1);

				break;

			case Constants.DISPLAY_STYLE_NAME_RACE:
				tabLabelsCombo.setSelectedIndex(2);

				break;

			case Constants.DISPLAY_STYLE_NAME_RACE_CLASS:
				tabLabelsCombo.setSelectedIndex(3);

				break;

			case Constants.DISPLAY_STYLE_NAME_FULL:
				tabLabelsCombo.setSelectedIndex(4);

				break;

			default:
				Logging
					.errorPrint("In PreferencesDialog.applyOptionValuesToControls (name display style) the tab option "
						+ SettingsHandler.getNameDisplayStyle()
						+ " is unsupported.");

				break;
		}

		displayAbilitiesAsTab.setSelected(SettingsHandler
			.isAbilitiesShownAsATab());
		expertGUICheckBox.setSelected(SettingsHandler.isExpertGUI());
		skillChoice.setSelectedIndex(SettingsHandler.getIncludeSkills());

		// Display options
		cmbChoiceMethods.setSelectedIndex(SettingsHandler
			.getSingleChoicePreference());
		featDescriptionShown.setSelected(!SettingsHandler.useFeatBenefits());
		showMemory.setSelected(SettingsHandler.isShowMemoryArea());
		showImagePreview.setSelected(SettingsHandler.isShowImagePreview());
		showSkillModifier.setSelected(SettingsHandler.getShowSkillModifier());
		showSkillRanks.setSelected(SettingsHandler.getShowSkillRanks());
		showToolbar.setSelected(SettingsHandler.isToolBarShown());
		toolTipTextShown.setSelected(SettingsHandler.isToolTipTextShown());
		useOutputNamesEquipment.setSelected(SettingsHandler
			.guiUsesOutputNameEquipment());
		useOutputNamesSpells.setSelected(SettingsHandler
			.guiUsesOutputNameSpells());
		waitCursor.setSelected(SettingsHandler.getUseWaitCursor());

		// Look and feel
		int crossIndex = UIFactory.indexOfCrossPlatformLookAndFeel();

		if (SettingsHandler.getLookAndFeel() < laf.length)
		{
			laf[SettingsHandler.getLookAndFeel()].setSelected(true);
		}
		else if (SettingsHandler.getLookAndFeel() == laf.length)
		{
			if ((SkinLFResourceChecker.getMissingResourceCount() == 0))
			{
				skinnedLookFeel.setSelected(true);
			}
			else
			{
				laf[crossIndex].setSelected(true);
			}
		}
		else
		{
			laf[crossIndex].setSelected(true);
		}

		// Level up
		hpDialogShownAtLevelUp.setSelected(SettingsHandler.getShowHPDialogAtLevelUp());
		//featDialogShownAtLevelUp.setSelected(SettingsHandler.getShowFeatDialogAtLevelUp());
		statDialogShownAtLevelUp.setSelected(SettingsHandler.getShowStatDialogAtLevelUp());
		showWarningAtFirstLevelUp.setSelected(SettingsHandler.isShowWarningAtFirstLevelUp());
		enforceSpendingBeforeLevelUp.setSelected(SettingsHandler.getEnforceSpendingBeforeLevelUp());

		// Equipment
		allowMetamagicInEqBuilder.setSelected(SettingsHandler.isMetamagicAllowedInEqBuilder());
		potionMaxLevel.setSelectedIndex(SettingsHandler.getMaxPotionSpellLevel() - SPELLLVLMIN);
		wandMaxLevel.setSelectedIndex(SettingsHandler.getMaxWandSpellLevel() - SPELLLVLMIN);

		if (SettingsHandler.wantToLoadMasterworkAndMagic())
		{
			noAutoEquipCreate.setSelected(true);
		}
		else
		{
			autoEquipCreate.setSelected(true);
		}

		// Turn off temporarily so we get current setting
		SettingsHandler.setWantToLoadMasterworkAndMagic(false);

		autoMethod1.setSelected(SettingsHandler.getAutogen(Constants.AUTOGEN_RACIAL));
		autoMethod2.setSelected(SettingsHandler.getAutogen(Constants.AUTOGEN_MASTERWORK));
		autoMethod3.setSelected(SettingsHandler.getAutogen(Constants.AUTOGEN_MAGIC));
		autoMethod4.setSelected(SettingsHandler.getAutogen(Constants.AUTOGEN_EXOTIC_MATERIAL));

		// Reset its state now we are done
		SettingsHandler.setWantToLoadMasterworkAndMagic(noAutoEquipCreate.isSelected());

		// Language
		languagePanel.applyOptionValuesToControls();
		
		// Locations
		pcgenCreateBackupCharacter.setSelected(PCGenSettings.getCreatePcgBackup());

		// Input
		printDeprecationMessages.setSelected(SettingsHandler.outputDeprecationMessages());
		printUnconstructedDetail.setSelected(SettingsHandler.inputUnconstructedMessages());
		
		// Output
		paperType.setSelectedIndex(Globals.getSelectedPaper());
		weaponProfPrintout.setSelected(SettingsHandler.getWeaponProfPrintout());
		saveOutputSheetWithPC.setSelected(SettingsHandler.getSaveOutputSheetWithPC());
		printSpellsWithPC.setSelected(SettingsHandler.getPrintSpellsWithPC());

		// Sources
		sourcesPanel.applyOptionValuesToControls();
		
		// Copy Settings
		copySettingsPanel.applyOptionValuesToControls();
		copySettingsPanel.registerAffectedPanel(characterStatsPanel);
		copySettingsPanel.registerAffectedPanel(defaultsPanel);
		copySettingsPanel.registerAffectedPanel(languagePanel);

	}

	private JPanel buildColorsPanel()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
		Border etched = null;
		TitledBorder title1 =
				BorderFactory.createTitledBorder(etched, in_color);
		JPanel colorsPanel = new JPanel();

		title1.setTitleJustification(TitledBorder.LEFT);
		colorsPanel.setBorder(title1);
		gridbag = new GridBagLayout();
		colorsPanel.setLayout(gridbag);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(2, 2, 2, 2);

		// NB - not alphabetized!
		addColorsOption(0, 0, c, gridbag, colorsPanel,
				prereqQualifyColor = new JButton(in_colorPrereqQualify));
		addColorsOption(0, 1, c, gridbag, colorsPanel, 
				prereqFailColor = new JButton(in_colorPrereqFail));
		addColorsOption(0, 2, c, gridbag, colorsPanel, 
				featAutoColor = new JButton(in_colorAutoFeat));
		addColorsOption(0, 3, c, gridbag, colorsPanel, 
				featVirtualColor = new JButton(in_colorVirtFeat));

		addColorsOption(1, 0, c, gridbag, colorsPanel, 
				sourceStatusRelease = new JButton(in_colorSourceRelease));
		addColorsOption(1, 1, c, gridbag, colorsPanel, 
				sourceStatusAlpha = new JButton(in_colorSourceAlpha));
		addColorsOption(1, 2, c, gridbag, colorsPanel, 
				sourceStatusBeta = new JButton(in_colorSourceBeta));
		addColorsOption(1, 3, c, gridbag, colorsPanel,
				sourceStatusTest = new JButton(in_colorSourceTest));
		
		Utility.buildConstraints(c, 5, 20, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		colorsPanel.add(label);

		return colorsPanel;
	}

	private void addColorsOption(int row, int col, final GridBagConstraints c,
		final GridBagLayout gridbag, final JPanel colorsPanel,
		final JButton button)
	{
		Utility.buildConstraints(c, row, col, 1, 1, 0, 0);
		gridbag.setConstraints(button, c);
		colorsPanel.add(button);
		button.addActionListener(prefsButtonHandler);
	}

	private JPanel buildDisplayOptionsPanel()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
		Border etched = null;
		TitledBorder title1 =
				BorderFactory.createTitledBorder(etched, in_displayOpts);
		JPanel displayOptsPanel = new JPanel();
		int line = 0;

		title1.setTitleJustification(TitledBorder.LEFT);
		displayOptsPanel.setBorder(title1);
		gridbag = new GridBagLayout();
		displayOptsPanel.setLayout(gridbag);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(2, 2, 2, 2);

		// Automatically sort the options alphabetically.
		final SortedMap<String, JComponent> options =
				new TreeMap<String, JComponent>();

		options.put(in_showFeatDescription, featDescriptionShown);
		options.put(in_showMemory, showMemory);
		options.put(in_showImagePreview, showImagePreview);
		options.put(in_showSkillModifierBreakdown, showSkillModifier);
		options.put(in_showSkillRanksBreakdown, showSkillRanks);
		options.put(in_showToolBar, showToolbar);
		options.put(in_showToolTips, toolTipTextShown);
		options.put(in_singleChoiceOption, cmbChoiceMethods);
		options.put(in_useAutoWaitCursor, waitCursor);
		options.put(in_useOutputNamesEquipment, useOutputNamesEquipment);
		options.put(in_useOutputNamesSpells, useOutputNamesSpells);

		for (Map.Entry<String, JComponent> entry : options.entrySet())
		{
			line =
					addDisplayOption(line, c, gridbag, displayOptsPanel, entry
						.getKey(), entry.getValue());
		}

		Utility.buildConstraints(c, 5, 20, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		displayOptsPanel.add(label);

		return displayOptsPanel;
	}

	private int addDisplayOption(final int line,
		final GridBagConstraints constraints, final GridBagLayout gridbag,
		final JPanel panel, final String labelText, final JComponent c)
	{
		final JLabel label = new JLabel(labelText + ": ");

		Utility.buildConstraints(constraints, 0, line, 2, 1, 0, 0);
		gridbag.setConstraints(label, constraints);
		panel.add(label);

		// Clicking on the label is just as good as clicking on the c.
		// This is closer to how selection boxes work elsewhere as well.
		if (c instanceof JCheckBox)
		{
			final JCheckBox checkbox = (JCheckBox) c;

			label.addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(final MouseEvent e)
				{
					checkbox.setSelected(!checkbox.isSelected());
				}
			});
		}

		Utility.buildConstraints(constraints, 2, line, 1, 1, 0, 0);
		gridbag.setConstraints(c, constraints);
		panel.add(c);

		return line + 1;
	}

	private JPanel buildEmptyPanel(String title, String messageText)
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
		JPanel panel = new JPanel();
		Border etched = null;
		TitledBorder title1 = BorderFactory.createTitledBorder(etched, title);

		title1.setTitleJustification(TitledBorder.LEFT);
		panel.setBorder(title1);
		gridbag = new GridBagLayout();
		panel.setLayout(gridbag);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(2, 2, 2, 2);

		Utility.buildConstraints(c, 5, 20, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(messageText, SwingConstants.CENTER);
		gridbag.setConstraints(label, c);
		panel.add(label);

		return panel;
	}

	private JPanel buildEquipmentPanel()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
		ButtonGroup exclusiveGroup;
		Border etched = null;
		TitledBorder title1 =
				BorderFactory.createTitledBorder(etched, in_equipment);
		JPanel equipmentPanel = new JPanel();

		title1.setTitleJustification(TitledBorder.LEFT);
		equipmentPanel.setBorder(title1);
		equipmentPanel.setLayout(gridbag);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(2, 2, 2, 2);
		exclusiveGroup = new ButtonGroup();

		Utility.buildConstraints(c, 0, 0, 3, 1, 0, 0);
		label = new JLabel(in_allowMetamagic + ": ");
		gridbag.setConstraints(label, c);
		equipmentPanel.add(label);
		Utility.buildConstraints(c, 3, 0, 1, 1, 0, 0);
		gridbag.setConstraints(allowMetamagicInEqBuilder, c);
		equipmentPanel.add(allowMetamagicInEqBuilder);

		Utility.buildConstraints(c, 0, 1, 3, 1, 0, 0);
		label = new JLabel(in_potionMax + ": ");
		gridbag.setConstraints(label, c);
		equipmentPanel.add(label);
		Utility.buildConstraints(c, 3, 1, 1, 1, 0, 0);
		Utility.buildConstraints(c, 3, 1, 1, 1, 0, 0);

		for (int i = SPELLLVLMIN; i <= SPELLLVLMAX; ++i)
		{
			potionSpellLevel[i - SPELLLVLMIN] = String.valueOf(i) + "  ";
		}

		potionMaxLevel = new JComboBoxEx(potionSpellLevel);
		gridbag.setConstraints(potionMaxLevel, c);
		equipmentPanel.add(potionMaxLevel);

		Utility.buildConstraints(c, 0, 2, 3, 1, 0, 0);
		label = new JLabel(in_wandMax + ": ");
		gridbag.setConstraints(label, c);
		equipmentPanel.add(label);
		Utility.buildConstraints(c, 3, 2, 1, 1, 0, 0);

		for (int i = SPELLLVLMIN; i <= SPELLLVLMAX; ++i)
		{
			wandSpellLevel[i - SPELLLVLMIN] = String.valueOf(i) + "	 ";
		}

		wandMaxLevel = new JComboBoxEx(wandSpellLevel);
		gridbag.setConstraints(wandMaxLevel, c);
		equipmentPanel.add(wandMaxLevel);

		Utility.buildConstraints(c, 0, 3, 3, 1, 0, 0);
		label = new JLabel(in_anyAutoEquip + ": ");
		gridbag.setConstraints(label, c);
		equipmentPanel.add(label);

		Utility.buildConstraints(c, 0, 4, 2, 1, 0, 0);
		noAutoEquipCreate = new JRadioButton(in_noAutoEquip);
		gridbag.setConstraints(noAutoEquipCreate, c);
		equipmentPanel.add(noAutoEquipCreate);
		exclusiveGroup.add(noAutoEquipCreate);

		Utility.buildConstraints(c, 0, 5, 2, 1, 0, 0);
		autoEquipCreate = new JRadioButton(in_autoEquip + ": ");
		gridbag.setConstraints(autoEquipCreate, c);
		equipmentPanel.add(autoEquipCreate);
		exclusiveGroup.add(autoEquipCreate);

		Utility.buildConstraints(c, 0, 6, 1, 1, 0, 0);
		label = new JLabel("	");
		gridbag.setConstraints(label, c);
		equipmentPanel.add(label);
		Utility.buildConstraints(c, 1, 6, 2, 1, 0, 0);
		label = new JLabel(in_autoEquipRace + ": ");
		gridbag.setConstraints(label, c);
		equipmentPanel.add(label);
		Utility.buildConstraints(c, 3, 6, 1, 1, 0, 0);
		gridbag.setConstraints(autoMethod1, c);
		equipmentPanel.add(autoMethod1);

		Utility.buildConstraints(c, 1, 7, 2, 1, 0, 0);
		label = new JLabel(in_autoEquipMasterwork + ": ");
		gridbag.setConstraints(label, c);
		equipmentPanel.add(label);
		Utility.buildConstraints(c, 3, 7, 1, 1, 0, 0);
		gridbag.setConstraints(autoMethod2, c);
		equipmentPanel.add(autoMethod2);

		Utility.buildConstraints(c, 1, 8, 2, 1, 0, 0);
		label = new JLabel(in_autoEquipMagic + ": ");
		gridbag.setConstraints(label, c);
		equipmentPanel.add(label);
		Utility.buildConstraints(c, 3, 8, 1, 1, 0, 0);
		gridbag.setConstraints(autoMethod3, c);
		equipmentPanel.add(autoMethod3);

		Utility.buildConstraints(c, 1, 9, 2, 1, 0, 0);
		label = new JLabel(in_autoEquipExotic + ": ");
		gridbag.setConstraints(label, c);
		equipmentPanel.add(label);
		Utility.buildConstraints(c, 3, 9, 1, 1, 0, 0);
		gridbag.setConstraints(autoMethod4, c);
		equipmentPanel.add(autoMethod4);

		Utility.buildConstraints(c, 5, 20, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		equipmentPanel.add(label);

		return equipmentPanel;
	}

	private JPanel buildHitPointsPanel()
	{
		int iRow = 0;

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
		ButtonGroup exclusiveGroup;
		Border etched = null;
		TitledBorder title1 = BorderFactory.createTitledBorder(etched, in_hp);
		JPanel hitPointsPanel = new JPanel();

		title1.setTitleJustification(TitledBorder.LEFT);
		hitPointsPanel.setBorder(title1);
		gridbag = new GridBagLayout();
		hitPointsPanel.setLayout(gridbag);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(2, 2, 2, 2);

		exclusiveGroup = new ButtonGroup();
		Utility.buildConstraints(c, 0, iRow, 3, 1, 0, 0);
		label =
				new JLabel(LanguageBundle.getString("in_Prefs_hpGenLabel")
					+ ": ");
		gridbag.setConstraints(label, c);
		hitPointsPanel.add(label);

		//
		// Insert a blank label to indent the HP rolling choices
		//
		Utility.buildConstraints(c, 0, iRow++, 1, 1, 0, 0);
		label = new JLabel("  ");
		gridbag.setConstraints(label, c);
		hitPointsPanel.add(label);

		Utility.buildConstraints(c, 1, iRow++, 2, 1, 0, 0);
		gridbag.setConstraints(hpUserRolled, c);
		hitPointsPanel.add(hpUserRolled);
		exclusiveGroup.add(hpUserRolled);

		Utility.buildConstraints(c, 1, iRow++, 2, 1, 0, 0);
		gridbag.setConstraints(hpStandard, c);
		hitPointsPanel.add(hpStandard);
		exclusiveGroup.add(hpStandard);

		Utility.buildConstraints(c, 1, iRow++, 2, 1, 0, 0);
		gridbag.setConstraints(hpAverage, c);
		hitPointsPanel.add(hpAverage);
		exclusiveGroup.add(hpAverage);

		Utility.buildConstraints(c, 1, iRow++, 2, 1, 0, 0);
		gridbag.setConstraints(hpAutomax, c);
		hitPointsPanel.add(hpAutomax);
		exclusiveGroup.add(hpAutomax);

		Utility.buildConstraints(c, 1, iRow, 1, 1, 0, 0);
		gridbag.setConstraints(hpPercentage, c);
		hitPointsPanel.add(hpPercentage);
		exclusiveGroup.add(hpPercentage);

		Utility.buildConstraints(c, 2, iRow++, 1, 1, 0, 0);
		gridbag.setConstraints(hpPct, c);
		hitPointsPanel.add(hpPct);

		Utility.buildConstraints(c, 1, iRow++, 2, 1, 0, 0);
		gridbag.setConstraints(hpAverageRoundedUp, c);
		hitPointsPanel.add(hpAverageRoundedUp);
		exclusiveGroup.add(hpAverageRoundedUp);

		Utility.buildConstraints(c, 0, iRow, 2, 1, 0, 0);
		label =
				new JLabel(LanguageBundle.getString("in_Prefs_hpMaxAtFirst")
					+ ": ");
		gridbag.setConstraints(label, c);
		hitPointsPanel.add(label);
		Utility.buildConstraints(c, 2, iRow++, 1, 1, 0, 0);
		gridbag.setConstraints(maxHpAtFirstLevel, c);
		hitPointsPanel.add(maxHpAtFirstLevel);

		Utility.buildConstraints(c, 0, iRow, 2, 1, 0, 0);
		label =
				new JLabel("      " 
					+ LanguageBundle.getString("in_Prefs_hpMaxAtFirstClassLevel") 
					+ ": ");
		gridbag.setConstraints(label, c);
		hitPointsPanel.add(label);
		Utility.buildConstraints(c, 2, iRow++, 1, 1, 0, 0);
		gridbag.setConstraints(maxHpAtFirstClassLevel, c);
		hitPointsPanel.add(maxHpAtFirstClassLevel);

		Utility.buildConstraints(c, 0, iRow, 2, 1, 0, 0);
		label =
				new JLabel("      " 
					+ LanguageBundle.getString("in_Prefs_hpMaxAtFirstPCClassLevelOnly") 
					+ ": ");
		gridbag.setConstraints(label, c);
		hitPointsPanel.add(label);
		Utility.buildConstraints(c, 2, iRow++, 1, 1, 0, 0);
		gridbag.setConstraints(maxHpAtFirstPCClassLevelOnly, c);
		hitPointsPanel.add(maxHpAtFirstPCClassLevelOnly);

		Utility.buildConstraints(c, 5, 20, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		hitPointsPanel.add(label);

		return hitPointsPanel;
	}

	private JPanel buildLevelUpPanel()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
		Border etched = null;
		TitledBorder title1 =
				BorderFactory.createTitledBorder(etched, in_levelUp);
		JPanel levelUpPanel = new JPanel();

		title1.setTitleJustification(TitledBorder.LEFT);
		levelUpPanel.setBorder(title1);
		gridbag = new GridBagLayout();
		levelUpPanel.setLayout(gridbag);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(2, 2, 2, 2);

		Utility.buildConstraints(c, 0, 0, 2, 1, 0, 0);
		label = new JLabel(in_hpWindow + ": ");
		gridbag.setConstraints(label, c);
		levelUpPanel.add(label);
		Utility.buildConstraints(c, 2, 0, 1, 1, 0, 0);
		gridbag.setConstraints(hpDialogShownAtLevelUp, c);
		levelUpPanel.add(hpDialogShownAtLevelUp);

		//		Utility.buildConstraints(c, 0, 1, 2, 1, 0, 0);
		//		label = new JLabel(in_featWindow + ": ");
		//		gridbag.setConstraints(label, c);
		//		levelUpPanel.add(label);
		//		Utility.buildConstraints(c, 2, 1, 1, 1, 0, 0);
		//		gridbag.setConstraints(featDialogShownAtLevelUp, c);
		//		levelUpPanel.add(featDialogShownAtLevelUp);

		Utility.buildConstraints(c, 0, 2, 2, 1, 0, 0);
		label = new JLabel(in_statWindow + ": ");
		gridbag.setConstraints(label, c);
		levelUpPanel.add(label);
		Utility.buildConstraints(c, 2, 2, 1, 1, 0, 0);
		gridbag.setConstraints(statDialogShownAtLevelUp, c);
		levelUpPanel.add(statDialogShownAtLevelUp);

		Utility.buildConstraints(c, 0, 3, 2, 1, 0, 0);
		label = new JLabel(in_warnFirstLevelUp + ": ");
		gridbag.setConstraints(label, c);
		levelUpPanel.add(label);
		Utility.buildConstraints(c, 2, 3, 1, 1, 0, 0);
		gridbag.setConstraints(showWarningAtFirstLevelUp, c);
		levelUpPanel.add(showWarningAtFirstLevelUp);

		Utility.buildConstraints(c, 0, 4, 2, 1, 0, 0);
		label = new JLabel(in_enforceSpending + ": ");
		gridbag.setConstraints(label, c);
		levelUpPanel.add(label);
		Utility.buildConstraints(c, 2, 4, 1, 1, 0, 0);
		gridbag.setConstraints(enforceSpendingBeforeLevelUp, c);
		levelUpPanel.add(enforceSpendingBeforeLevelUp);

		Utility.buildConstraints(c, 5, 20, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		levelUpPanel.add(label);

		return levelUpPanel;
	}

	private JPanel buildInputPanel()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		Border etched = null;
		TitledBorder title1 =
				BorderFactory.createTitledBorder(etched, in_location);
		JPanel inputPanel = new JPanel();

		title1.setTitleJustification(TitledBorder.LEFT);
		inputPanel.setBorder(title1);
		gridbag = new GridBagLayout();
		inputPanel.setLayout(gridbag);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(2, 2, 2, 2);

		Utility.buildConstraints(c, 0, 14, 3, 1, 0, 0);
		printDeprecationMessages =
				new JCheckBox(in_printDeprecation, SettingsHandler
					.outputDeprecationMessages());
		gridbag.setConstraints(printDeprecationMessages, c);
		inputPanel.add(printDeprecationMessages);
		
		Utility.buildConstraints(c, 0, 28, 3, 1, 0, 0);
		printUnconstructedDetail =
				new JCheckBox(in_printUnconstructed, SettingsHandler
					.inputUnconstructedMessages());
		gridbag.setConstraints(printUnconstructedDetail, c);
		inputPanel.add(printUnconstructedDetail);
		
		return inputPanel;
	}

	private JPanel buildLocationPanel()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
		Border etched = null;
		TitledBorder title1 =
				BorderFactory.createTitledBorder(etched, in_location);
		JPanel locationPanel = new JPanel();

		title1.setTitleJustification(TitledBorder.LEFT);
		locationPanel.setBorder(title1);
		gridbag = new GridBagLayout();
		locationPanel.setLayout(gridbag);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(2, 2, 2, 2);

		Utility.buildConstraints(c, 0, 0, 1, 1, 0, 0);
		label = new JLabel(in_browserPath + ": ");
		gridbag.setConstraints(label, c);
		locationPanel.add(label);
		Utility.buildConstraints(c, 1, 0, 1, 1, 1, 0);
		browserPath =
				new JTextField(String.valueOf(SettingsHandler.getBrowserPath()));

		// sage_sam 9 April 2003
		browserPath.addFocusListener(textFieldListener);
		gridbag.setConstraints(browserPath, c);
		locationPanel.add(browserPath);
		Utility.buildConstraints(c, 2, 0, 1, 1, 0, 0);
		browserPathButton = new JButton(in_choose);
		gridbag.setConstraints(browserPathButton, c);
		locationPanel.add(browserPathButton);
		browserPathButton.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(c, 1, 1, 1, 1, 0, 0);
		clearBrowserPathButton = new JButton(in_clearBrowserPath);
		gridbag.setConstraints(clearBrowserPathButton, c);
		locationPanel.add(clearBrowserPathButton);
		clearBrowserPathButton.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(c, 0, 2, 1, 1, 0, 0);
		label =
				new JLabel(LanguageBundle
					.getString("in_Prefs_pcgenCharacterDir")
					+ ": ");
		gridbag.setConstraints(label, c);
		locationPanel.add(label);
		Utility.buildConstraints(c, 1, 2, 1, 1, 0, 0);
		pcgenCharacterDir =
				new JTextField(String.valueOf(SettingsHandler.getPcgPath()));

		// sage_sam 9 April 2003
		pcgenCharacterDir.addFocusListener(textFieldListener);
		gridbag.setConstraints(pcgenCharacterDir, c);
		locationPanel.add(pcgenCharacterDir);
		Utility.buildConstraints(c, 2, 2, 1, 1, 0, 0);
		pcgenCharacterDirButton = new JButton(in_choose);
		gridbag.setConstraints(pcgenCharacterDirButton, c);
		locationPanel.add(pcgenCharacterDirButton);
		pcgenCharacterDirButton.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(c, 0, 3, 1, 1, 0, 0);

		//todo: i18n
		label = new JLabel("PCGen Portraits Directory" + ": ");
		gridbag.setConstraints(label, c);
		locationPanel.add(label);
		Utility.buildConstraints(c, 1, 3, 1, 1, 0, 0);
		pcgenPortraitsDir =
				new JTextField(String.valueOf(SettingsHandler
					.getPortraitsPath()));

		// sage_sam 9 April 2003
		pcgenPortraitsDir.addFocusListener(textFieldListener);
		gridbag.setConstraints(pcgenPortraitsDir, c);
		locationPanel.add(pcgenPortraitsDir);
		Utility.buildConstraints(c, 2, 3, 1, 1, 0, 0);
		pcgenPortraitsDirButton = new JButton(in_choose);
		gridbag.setConstraints(pcgenPortraitsDirButton, c);
		locationPanel.add(pcgenPortraitsDirButton);
		pcgenPortraitsDirButton.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(c, 0, 4, 1, 1, 0, 0);
		label =
				new JLabel(LanguageBundle.getString("in_Prefs_pcgenDataDir")
					+ ": ");
		gridbag.setConstraints(label, c);
		locationPanel.add(label);
		Utility.buildConstraints(c, 1, 4, 1, 1, 0, 0);
		pcgenDataDir =
				new JTextField(String.valueOf(SettingsHandler
					.getPccFilesLocation()));

		// sage_sam 9 April 2003
		pcgenDataDir.addFocusListener(textFieldListener);
		gridbag.setConstraints(pcgenDataDir, c);
		locationPanel.add(pcgenDataDir);
		Utility.buildConstraints(c, 2, 4, 1, 1, 0, 0);
		pcgenDataDirButton = new JButton(in_choose);
		gridbag.setConstraints(pcgenDataDirButton, c);
		locationPanel.add(pcgenDataDirButton);
		pcgenDataDirButton.addActionListener(prefsButtonHandler);

		//////////////////////
		Utility.buildConstraints(c, 0, 5, 1, 1, 0, 0);
		label =
				new JLabel(LanguageBundle.getString("in_Prefs_pcgenCustomDir")
					+ ": ");
		gridbag.setConstraints(label, c);
		locationPanel.add(label);
		Utility.buildConstraints(c, 1, 5, 1, 1, 0, 0);
		pcgenCustomDir =
				new JTextField(String.valueOf(SettingsHandler
					.getPcgenCustomDir()));

		// sage_sam 9 April 2003
		pcgenCustomDir.addFocusListener(textFieldListener);
		gridbag.setConstraints(pcgenCustomDir, c);
		locationPanel.add(pcgenCustomDir);
		Utility.buildConstraints(c, 2, 5, 1, 1, 0, 0);
		pcgenCustomDirButton = new JButton(in_choose);
		gridbag.setConstraints(pcgenCustomDirButton, c);
		locationPanel.add(pcgenCustomDirButton);
		pcgenCustomDirButton.addActionListener(prefsButtonHandler);

		////////////////////

		Utility.buildConstraints(c, 0, 6, 1, 1, 0, 0);
		label =
				new JLabel(LanguageBundle
					.getString("in_Prefs_pcgenVendorDataDir")
					+ ": ");
		gridbag.setConstraints(label, c);
		locationPanel.add(label);
		Utility.buildConstraints(c, 1, 6, 1, 1, 0, 0);
		pcgenVendorDataDir =
				new JTextField(String.valueOf(SettingsHandler
					.getPcgenVendorDataDir()));

		// sage_sam 9 April 2003
		pcgenVendorDataDir.addFocusListener(textFieldListener);
		gridbag.setConstraints(pcgenVendorDataDir, c);
		locationPanel.add(pcgenVendorDataDir);
		Utility.buildConstraints(c, 2, 6, 1, 1, 0, 0);
		pcgenVendorDataDirButton = new JButton(in_choose);
		gridbag.setConstraints(pcgenVendorDataDirButton, c);
		locationPanel.add(pcgenVendorDataDirButton);
		pcgenVendorDataDirButton.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(c, 0, 7, 1, 1, 0, 0);
		label =
				new JLabel(LanguageBundle.getString("in_Prefs_pcgenDocsDir")
					+ ": ");
		gridbag.setConstraints(label, c);
		locationPanel.add(label);
		Utility.buildConstraints(c, 1, 7, 1, 1, 0, 0);
		pcgenDocsDir =
				new JTextField(String
					.valueOf(SettingsHandler.getPcgenDocsDir()));

		// sage_sam 9 April 2003
		pcgenDocsDir.addFocusListener(textFieldListener);
		gridbag.setConstraints(pcgenDocsDir, c);
		locationPanel.add(pcgenDocsDir);
		Utility.buildConstraints(c, 2, 7, 1, 1, 0, 0);
		pcgenDocsDirButton = new JButton(in_choose);
		gridbag.setConstraints(pcgenDocsDirButton, c);
		locationPanel.add(pcgenDocsDirButton);
		pcgenDocsDirButton.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(c, 0, 8, 1, 1, 0, 0);
		label =
				new JLabel(LanguageBundle.getString("in_Prefs_pcgenSystemDir")
					+ ": ");
		gridbag.setConstraints(label, c);
		locationPanel.add(label);
		Utility.buildConstraints(c, 1, 8, 1, 1, 0, 0);
		pcgenSystemDir =
				new JTextField(String.valueOf(SettingsHandler
					.getPcgenSystemDir()));

		// sage_sam 9 April 2003
		pcgenSystemDir.addFocusListener(textFieldListener);
		gridbag.setConstraints(pcgenSystemDir, c);
		locationPanel.add(pcgenSystemDir);
		Utility.buildConstraints(c, 2, 8, 1, 1, 0, 0);
		pcgenSystemDirButton = new JButton(in_choose);
		gridbag.setConstraints(pcgenSystemDirButton, c);
		locationPanel.add(pcgenSystemDirButton);
		pcgenSystemDirButton.addActionListener(prefsButtonHandler);

		// Output Sheet directory
		Utility.buildConstraints(c, 0, 9, 1, 1, 0, 0);
		label =
				new JLabel(LanguageBundle
					.getString("in_Prefs_pcgenOutputSheetDir")
					+ ": ");
		gridbag.setConstraints(label, c);
		locationPanel.add(label);
		Utility.buildConstraints(c, 1, 9, 1, 1, 0, 0);
		pcgenOutputSheetDir =
				new JTextField(String.valueOf(SettingsHandler
					.getPcgenOutputSheetDir()));
		pcgenOutputSheetDir.addFocusListener(textFieldListener);
		gridbag.setConstraints(pcgenOutputSheetDir, c);
		locationPanel.add(pcgenOutputSheetDir);
		Utility.buildConstraints(c, 2, 9, 1, 1, 0, 0);
		pcgenOutputSheetDirButton = new JButton(in_choose);
		gridbag.setConstraints(pcgenOutputSheetDirButton, c);
		locationPanel.add(pcgenOutputSheetDirButton);
		pcgenOutputSheetDirButton.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(c, 0, 10, 1, 1, 0, 0);
		label = new JLabel(LanguageBundle.getString("in_Prefs_pcgenPreviewDir") + ": ");
		gridbag.setConstraints(label, c);
		locationPanel.add(label);
		Utility.buildConstraints(c, 1, 10, 1, 1, 0, 0);
		pcgenPreviewDir = new JTextField(String.valueOf(SettingsHandler.getPcgenPreviewDir()));
		pcgenPreviewDir.addFocusListener(textFieldListener);
		gridbag.setConstraints(pcgenPreviewDir, c);
		locationPanel.add(pcgenPreviewDir);
		Utility.buildConstraints(c, 2, 10, 1, 1, 0, 0);
		pcgenPreviewDirButton = new JButton(in_choose);
		gridbag.setConstraints(pcgenPreviewDirButton, c);
		locationPanel.add(pcgenPreviewDirButton);
		pcgenPreviewDirButton.addActionListener(prefsButtonHandler);
		
		// Character File Backup directory
		Utility.buildConstraints(c, 0, 11, 1, 1, 0, 0);
		label =
				new JLabel(LanguageBundle
					.getString("in_Prefs_pcgenCreateBackupCharacter")
					+ ": ");
		gridbag.setConstraints(label, c);
		locationPanel.add(label);
		Utility.buildConstraints(c, 1, 11, 1, 1, 0, 0);
		gridbag.setConstraints(pcgenCreateBackupCharacter, c);
		locationPanel.add(pcgenCreateBackupCharacter);

		Utility.buildConstraints(c, 0, 12, 1, 1, 0, 0);
		label =
				new JLabel(LanguageBundle
					.getString("in_Prefs_pcgenBackupCharacterDir")
					+ ": ");
		gridbag.setConstraints(label, c);
		locationPanel.add(label);
		Utility.buildConstraints(c, 1, 12, 1, 1, 0, 0);
		pcgenBackupCharacterDir =
				new JTextField(String.valueOf(SettingsHandler
					.getBackupPcgPath()));
		pcgenBackupCharacterDir.addFocusListener(textFieldListener);
		gridbag.setConstraints(pcgenBackupCharacterDir, c);
		locationPanel.add(pcgenBackupCharacterDir);
		Utility.buildConstraints(c, 2, 12, 1, 1, 0, 0);
		pcgenBackupCharacterDirButton = new JButton(in_choose);
		gridbag.setConstraints(pcgenBackupCharacterDirButton, c);
		locationPanel.add(pcgenBackupCharacterDirButton);
		pcgenBackupCharacterDirButton.addActionListener(prefsButtonHandler);

		// Where to store options.ini file
		Utility.buildConstraints(c, 0, 13, 1, 1, 0, 0);
		label =
				new JLabel(LanguageBundle.getString("in_Prefs_pcgenFilesDir")
					+ ": ");
		gridbag.setConstraints(label, c);
		locationPanel.add(label);

		pcgenFilesDirRadio = new JRadioButton("PCGen Dir");
		usersFilesDirRadio = new JRadioButton("Home Dir");
		selectFilesDirRadio = new JRadioButton("Select a directory");
		pcgenFilesDir =
				new JTextField(String.valueOf(SettingsHandler
					.getPcgenFilesDir()));
		pcgenFilesDir.addFocusListener(textFieldListener);

		String fType = SettingsHandler.getFilePaths();

		if ((fType == null) || (fType.length() < 1))
		{
			// make sure we have a default
			fType = "pcgen";
		}

		if (fType.equals("pcgen"))
		{
			pcgenFilesDirRadio.setSelected(true);
			pcgenFilesDir.setText(System.getProperty("user.dir"));
			SettingsHandler.setFilePaths("pcgen");
		}
		else if (fType.equals("user"))
		{
			usersFilesDirRadio.setSelected(true);
			pcgenFilesDir.setText(System.getProperty("user.home")
				+ File.separator + ".pcgen");
		}
		else
		{
			selectFilesDirRadio.setSelected(true);
		}

		Utility.buildConstraints(c, 0, 14, 1, 1, 0, 0);
		gridbag.setConstraints(pcgenFilesDirRadio, c);
		locationPanel.add(pcgenFilesDirRadio);
		Utility.buildConstraints(c, 1, 14, 1, 1, 0, 0);
		gridbag.setConstraints(usersFilesDirRadio, c);
		locationPanel.add(usersFilesDirRadio);

		groupFilesDir = new ButtonGroup();
		groupFilesDir.add(pcgenFilesDirRadio);
		groupFilesDir.add(usersFilesDirRadio);
		groupFilesDir.add(selectFilesDirRadio);

		pcgenFilesDirRadio.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				pcgenFilesDir.setText(System.getProperty("user.dir"));
				pcgenFilesDirButton.setEnabled(false);
			}
		});
		usersFilesDirRadio.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				pcgenFilesDir.setText(System.getProperty("user.home")
					+ File.separator + ".pcgen");
				pcgenFilesDirButton.setEnabled(false);
			}
		});
		selectFilesDirRadio.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				pcgenFilesDir.setText("");
				pcgenFilesDirButton.setEnabled(true);
			}
		});

		Utility.buildConstraints(c, 0, 15, 1, 1, 0, 0);
		gridbag.setConstraints(selectFilesDirRadio, c);
		locationPanel.add(selectFilesDirRadio);
		Utility.buildConstraints(c, 1, 15, 1, 1, 0, 0);
		gridbag.setConstraints(pcgenFilesDir, c);
		locationPanel.add(pcgenFilesDir);
		Utility.buildConstraints(c, 2, 15, 1, 1, 0, 0);
		pcgenFilesDirButton = new JButton(in_choose);
		pcgenFilesDirButton.setEnabled(selectFilesDirRadio.isSelected());
		gridbag.setConstraints(pcgenFilesDirButton, c);
		locationPanel.add(pcgenFilesDirButton);
		pcgenFilesDirButton.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(c, 0, 20, 3, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(" ");

		gridbag.setConstraints(label, c);
		locationPanel.add(label);

		return locationPanel;
	}

	private JPanel buildLookAndFeelPanel()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
		ButtonGroup exclusiveGroup;
		Border etched = null;
		TitledBorder title1 =
				BorderFactory.createTitledBorder(etched, in_lookAndFeel);
		JPanel lafPanel = new JPanel();

		title1.setTitleJustification(TitledBorder.LEFT);
		lafPanel.setBorder(title1);
		gridbag = new GridBagLayout();
		lafPanel.setLayout(gridbag);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(2, 2, 2, 2);

		exclusiveGroup = new ButtonGroup();
		laf = new JRadioButton[UIFactory.getLookAndFeelCount()];

		for (int i = 0; i < laf.length; ++i)
		{
			laf[i] = new JRadioButton();
			laf[i].setText(UIFactory.getLookAndFeelName(i));
			Utility.setDescription(laf[i], UIFactory.getLookAndFeelTooltip(i));

			if (laf[i].getText().charAt(0) != 'C')
			{
				laf[i].setMnemonic(laf[i].getText().charAt(0));
			}
			else
			{
				laf[i].setMnemonic(laf[i].getText().charAt(1));
			}

			Utility.buildConstraints(c, 0, i, 3, 1, 0, 0);
			gridbag.setConstraints(laf[i], c);
			lafPanel.add(laf[i]);
			exclusiveGroup.add(laf[i]);
		}

		skinnedLookFeel.setText(in_skinnedLAF + ": ");
		Utility.setDescription(skinnedLookFeel, LanguageBundle
			.getString("in_Prefs_skinnedLAFTooltip"));
		skinnedLookFeel.setMnemonic(LanguageBundle
			.getMnemonic("in_mn_Prefs_skinnedLAF"));
		Utility.buildConstraints(c, 0, laf.length, 3, 1, 0, 0);
		gridbag.setConstraints(skinnedLookFeel, c);
		lafPanel.add(skinnedLookFeel);
		exclusiveGroup.add(skinnedLookFeel);

		Utility.buildConstraints(c, 3, laf.length, 1, 1, 1, 0);
		themepackLabel = new JTextField(SettingsHandler.getSkinLFThemePack());
		themepackLabel.setEditable(false);
		gridbag.setConstraints(themepackLabel, c);
		lafPanel.add(themepackLabel);
		Utility.buildConstraints(c, 4, laf.length, 1, 1, 0, 0);
		themepack = new JButton(in_choose);
		Utility.setDescription(themepack, LanguageBundle
			.getString("in_Prefs_chooseSkinTooltip"));
		gridbag.setConstraints(themepack, c);
		lafPanel.add(themepack);
		themepack.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(c, 0, 20, 5, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		lafPanel.add(label);

		return lafPanel;
	}

	private JPanel buildOutputPanel()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
		Border etched = null;
		TitledBorder title1 =
				BorderFactory.createTitledBorder(etched, in_output);
		JPanel outputPanel = new JPanel();

		title1.setTitleJustification(TitledBorder.LEFT);
		outputPanel.setBorder(title1);
		outputPanel.setLayout(gridbag);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(2, 2, 2, 2);

		Utility.buildConstraints(c, 0, 0, 1, 1, 0, 0);
		label =
				new JLabel(LanguageBundle
					.getString("in_Prefs_outputSheetHTMLDefault")
					+ ": ");
		gridbag.setConstraints(label, c);
		outputPanel.add(label);
		Utility.buildConstraints(c, 1, 0, 1, 1, 1, 0);
		outputSheetHTMLDefault =
				new JTextField(String.valueOf(SettingsHandler
					.getSelectedCharacterHTMLOutputSheet(null)));

		// sage_sam 9 April 2003
		outputSheetHTMLDefault.addFocusListener(textFieldListener);
		gridbag.setConstraints(outputSheetHTMLDefault, c);
		outputPanel.add(outputSheetHTMLDefault);
		Utility.buildConstraints(c, 2, 0, 1, 1, 0, 0);
		outputSheetHTMLDefaultButton = new JButton(in_choose);
		gridbag.setConstraints(outputSheetHTMLDefaultButton, c);
		outputPanel.add(outputSheetHTMLDefaultButton);
		outputSheetHTMLDefaultButton.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(c, 0, 1, 1, 1, 0, 0);
		label =
				new JLabel(LanguageBundle
					.getString("in_Prefs_outputSheetPDFDefault")
					+ ": ");
		gridbag.setConstraints(label, c);
		outputPanel.add(label);
		Utility.buildConstraints(c, 1, 1, 1, 1, 1, 0);
		outputSheetPDFDefault =
				new JTextField(String.valueOf(SettingsHandler
					.getSelectedCharacterPDFOutputSheet(null)));

		// sage_sam 9 April 2003
		outputSheetPDFDefault.addFocusListener(textFieldListener);
		gridbag.setConstraints(outputSheetPDFDefault, c);
		outputPanel.add(outputSheetPDFDefault);
		Utility.buildConstraints(c, 2, 1, 1, 1, 0, 0);
		outputSheetPDFDefaultButton = new JButton(in_choose);
		gridbag.setConstraints(outputSheetPDFDefaultButton, c);
		outputPanel.add(outputSheetPDFDefaultButton);
		outputSheetPDFDefaultButton.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(c, 0, 2, 1, 1, 0, 0);
		label = new JLabel(in_outputSheetEqSet + ": ");
		gridbag.setConstraints(label, c);
		outputPanel.add(label);
		Utility.buildConstraints(c, 1, 2, 1, 1, 0, 0);
		outputSheetEqSet =
				new JTextField(String.valueOf(SettingsHandler
					.getSelectedEqSetTemplate()));

		// sage_sam 9 April 2003
		outputSheetEqSet.addFocusListener(textFieldListener);
		gridbag.setConstraints(outputSheetEqSet, c);
		outputPanel.add(outputSheetEqSet);
		Utility.buildConstraints(c, 2, 2, 1, 1, 0, 0);
		outputSheetEqSetButton = new JButton(in_choose);
		gridbag.setConstraints(outputSheetEqSetButton, c);
		outputPanel.add(outputSheetEqSetButton);
		outputSheetEqSetButton.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(c, 0, 3, 1, 1, 0, 0);
		label = new JLabel(in_saveOutputSheetWithPC + ": ");
		gridbag.setConstraints(label, c);
		outputPanel.add(label);
		Utility.buildConstraints(c, 1, 3, 1, 1, 0, 0);
		gridbag.setConstraints(saveOutputSheetWithPC, c);
		outputPanel.add(saveOutputSheetWithPC);

		Utility.buildConstraints(c, 0, 4, 1, 1, 0, 0);
		label =
				new JLabel(LanguageBundle
					.getString("in_Prefs_outputSpellSheetDefault")
					+ ": ");
		gridbag.setConstraints(label, c);
		outputPanel.add(label);
		Utility.buildConstraints(c, 1, 4, 1, 1, 0, 0);
		outputSheetSpellsDefault =
				new JTextField(String.valueOf(SettingsHandler
					.getSelectedSpellSheet()));
		outputSheetSpellsDefault.addFocusListener(textFieldListener);
		gridbag.setConstraints(outputSheetSpellsDefault, c);
		outputPanel.add(outputSheetSpellsDefault);
		Utility.buildConstraints(c, 2, 4, 1, 1, 0, 0);
		outputSheetSpellsDefaultButton = new JButton(in_choose);
		gridbag.setConstraints(outputSheetSpellsDefaultButton, c);
		outputPanel.add(outputSheetSpellsDefaultButton);
		outputSheetSpellsDefaultButton.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(c, 0, 5, 1, 1, 0, 0);
		label =
				new JLabel(LanguageBundle
					.getString("in_Prefs_printSpellsWithPC")
					+ ": ");
		gridbag.setConstraints(label, c);
		outputPanel.add(label);
		Utility.buildConstraints(c, 1, 5, 1, 1, 0, 0);
		gridbag.setConstraints(printSpellsWithPC, c);
		outputPanel.add(printSpellsWithPC);

		Utility.buildConstraints(c, 0, 6, 1, 1, 0, 0);
		label = new JLabel(in_paperType + ": ");
		gridbag.setConstraints(label, c);
		outputPanel.add(label);
		Utility.buildConstraints(c, 1, 6, 1, 1, 0, 0);

		final int paperCount = Globals.getPaperCount();
		paperNames = new String[paperCount];

		for (int i = 0; i < paperCount; ++i)
		{
			paperNames[i] = Globals.getPaperInfo(i, PaperInfo.NAME);
		}

		paperType = new JComboBoxEx(paperNames);
		gridbag.setConstraints(paperType, c);
		outputPanel.add(paperType);

		Utility.buildConstraints(c, 0, 7, 3, 1, 0, 0);
		removeTempFiles =
				new JCheckBox(in_removeTemp, SettingsHandler
					.getCleanupTempFiles());
		gridbag.setConstraints(removeTempFiles, c);
		outputPanel.add(removeTempFiles);

		Utility.buildConstraints(c, 0, 8, 3, 1, 0, 0);
		weaponProfPrintout =
				new JCheckBox(in_weaponProfPrintout, SettingsHandler
					.getWeaponProfPrintout());
		gridbag.setConstraints(weaponProfPrintout, c);
		outputPanel.add(weaponProfPrintout);

		Utility.buildConstraints(c, 0, 9, 1, 1, 0, 0);
		label = new JLabel(in_postExportCommandStandard + ": ");
		gridbag.setConstraints(label, c);
		outputPanel.add(label);
		Utility.buildConstraints(c, 1, 9, 1, 1, 0, 0);
		postExportCommandStandard =
				new JTextField(String.valueOf(SettingsHandler
					.getPostExportCommandStandard()));
		gridbag.setConstraints(postExportCommandStandard, c);
		outputPanel.add(postExportCommandStandard);

		Utility.buildConstraints(c, 0, 10, 1, 1, 0, 0);
		label = new JLabel(in_postExportCommandPDF + ": ");
		gridbag.setConstraints(label, c);
		outputPanel.add(label);
		Utility.buildConstraints(c, 1, 10, 1, 1, 0, 0);
		postExportCommandPDF =
				new JTextField(String.valueOf(SettingsHandler
					.getPostExportCommandPDF()));
		gridbag.setConstraints(postExportCommandPDF, c);
		outputPanel.add(postExportCommandPDF);

		Utility.buildConstraints(c, 0, 11, 1, 1, 0, 0);
		label = new JLabel(in_skillChoice + ": ");
		gridbag.setConstraints(label, c);
		outputPanel.add(label);
		Utility.buildConstraints(c, 1, 11, 1, 1, 0, 0);
		skillChoice.setModel(new DefaultComboBoxModel(new String[]{
			in_skillChoiceNone, in_skillChoiceUntrained, in_skillChoiceAll,
			in_skillChoiceAsUI}));
		skillChoice.setSelectedIndex(SettingsHandler.getIncludeSkills());
		gridbag.setConstraints(skillChoice, c);
		outputPanel.add(skillChoice);

		Utility.buildConstraints(c, 0, 12, 1, 1, 0, 0);
		label = new JLabel(in_invalidToHitText + ": ");
		gridbag.setConstraints(label, c);
		outputPanel.add(label);
		Utility.buildConstraints(c, 1, 12, 1, 1, 0, 0);
		invalidToHitText =
				new JTextField(String.valueOf(SettingsHandler
					.getInvalidToHitText()));
		gridbag.setConstraints(invalidToHitText, c);
		outputPanel.add(invalidToHitText);

		Utility.buildConstraints(c, 0, 13, 1, 1, 0, 0);
		label = new JLabel(in_invalidDmgText + ": ");
		gridbag.setConstraints(label, c);
		outputPanel.add(label);
		Utility.buildConstraints(c, 1, 13, 1, 1, 0, 0);
		invalidDmgText =
				new JTextField(String.valueOf(SettingsHandler
					.getInvalidDmgText()));
		gridbag.setConstraints(invalidDmgText, c);
		outputPanel.add(invalidDmgText);

		Utility.buildConstraints(c, 0, 14, 3, 1, 0, 0);
		alwaysOverwrite =
				new JCheckBox(in_alwaysOverwrite, SettingsHandler
					.getAlwaysOverwrite());
		gridbag.setConstraints(alwaysOverwrite, c);
		outputPanel.add(alwaysOverwrite);

		Utility.buildConstraints(c, 0, 15, 3, 1, 0, 0);
		showSingleBoxPerBundle =
				new JCheckBox(in_showSingleBoxPerBundle, SettingsHandler
					.getShowSingleBoxPerBundle());
		gridbag.setConstraints(showSingleBoxPerBundle, c);
		outputPanel.add(showSingleBoxPerBundle);

		Utility.buildConstraints(c, 0, 20, 3, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		outputPanel.add(label);

		return outputPanel;
	}

	private void buildSettingsTreeAndPanel()
	{
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Root");
		DefaultMutableTreeNode characterNode;
		DefaultMutableTreeNode pcGenNode;
		DefaultMutableTreeNode appearanceNode;
		DefaultMutableTreeNode gameModeNode;

		// Build the settings panel
		settingsPanel = new JPanel();
		settingsPanel.setLayout(new CardLayout());

		// Build the selection tree
		characterNode = new DefaultMutableTreeNode(in_character);
		settingsPanel.add(buildEmptyPanel("", LanguageBundle
			.getString("in_Prefs_charTip")), in_character);

		characterStatsPanel = new CharacterStatsPanel(this);
		characterNode.add(new DefaultMutableTreeNode(characterStatsPanel
			.getTitle()));
		settingsPanel.add(characterStatsPanel, characterStatsPanel.getTitle());
		characterNode.add(new DefaultMutableTreeNode(in_hp));
		settingsPanel.add(buildHitPointsPanel(), in_hp);
		houseRulesPanel = new HouseRulesPanel();
		characterNode.add(new DefaultMutableTreeNode(houseRulesPanel.getTitle()));
		settingsPanel.add(houseRulesPanel, houseRulesPanel.getTitle());
		characterNode.add(new DefaultMutableTreeNode(in_monsters));
		monsterPanel = new MonsterPanel();
		settingsPanel.add(monsterPanel, monsterPanel.getTitle());
		defaultsPanel = new DefaultsPanel();
		characterNode
			.add(new DefaultMutableTreeNode(defaultsPanel.getTitle()));
		settingsPanel.add(defaultsPanel, defaultsPanel.getTitle());
		rootNode.add(characterNode);

		appearanceNode = new DefaultMutableTreeNode(in_appearance);
		settingsPanel.add(buildEmptyPanel("", LanguageBundle
			.getString("in_Prefs_appearanceTip")), in_appearance);

		appearanceNode.add(new DefaultMutableTreeNode(in_color));
		settingsPanel.add(buildColorsPanel(), in_color);
		appearanceNode.add(new DefaultMutableTreeNode(in_displayOpts));
		settingsPanel.add(buildDisplayOptionsPanel(), in_displayOpts);
		appearanceNode.add(new DefaultMutableTreeNode(in_levelUp));
		settingsPanel.add(buildLevelUpPanel(), in_levelUp);
		appearanceNode.add(new DefaultMutableTreeNode(in_lookAndFeel));
		settingsPanel.add(buildLookAndFeelPanel(), in_lookAndFeel);
		appearanceNode.add(new DefaultMutableTreeNode(in_tabs));
		settingsPanel.add(buildTabsAppearancePanel(), in_tabs);
		rootNode.add(appearanceNode);

		pcGenNode = new DefaultMutableTreeNode(in_pcgen);
		settingsPanel.add(buildEmptyPanel("", LanguageBundle
			.getString("in_Prefs_pcgenTip")), in_pcgen);

		pcGenNode.add(new DefaultMutableTreeNode(in_equipment));
		settingsPanel.add(buildEquipmentPanel(), in_equipment);
		languagePanel = new LanguagePanel();
		pcGenNode.add(new DefaultMutableTreeNode(languagePanel.getTitle()));
		settingsPanel.add(languagePanel, languagePanel.getTitle());
		pcGenNode.add(new DefaultMutableTreeNode(in_location));
		settingsPanel.add(buildLocationPanel(), in_location);
		pcGenNode.add(new DefaultMutableTreeNode(in_input));
		settingsPanel.add(buildInputPanel(), in_input);
		pcGenNode.add(new DefaultMutableTreeNode(in_output));
		settingsPanel.add(buildOutputPanel(), in_output);
		sourcesPanel = new SourcesPanel();
		pcGenNode.add(new DefaultMutableTreeNode(sourcesPanel.getTitle()));
		settingsPanel.add(sourcesPanel, sourcesPanel.getTitle());
		rootNode.add(pcGenNode);

		String in_gamemode =  LanguageBundle.getString("in_mnuSettingsCampaign");
		gameModeNode = new DefaultMutableTreeNode(in_gamemode);
		settingsPanel.add(buildEmptyPanel("", LanguageBundle
			.getString("in_Prefs_gameModeTip")), in_gamemode);

		gameModeNode.add(new DefaultMutableTreeNode(LanguageBundle.getString("in_Prefs_copy")));
		copySettingsPanel = new CopySettingsPanel();
		settingsPanel.add(copySettingsPanel, copySettingsPanel.getTitle());
		rootNode.add(gameModeNode);
		
		DefaultMutableTreeNode pluginNode =
				new DefaultMutableTreeNode("Plugins");

		addPluginPanes(rootNode, pluginNode);

		settingsModel = new DefaultTreeModel(rootNode);
		settingsTree = new JTree(settingsModel);

		/*
		 * <!--
		 *    bug:     TreeView not displaying correctly with Kunststoff LaF
		 *    fix:     need to set a (wide enough) border
		 *    author:     Thomas Behr
		 *    date:     02/10/02
		 * -->
		 */
		if (UIFactory.getLookAndFeelName(SettingsHandler.getLookAndFeel())
			.equals("Kunststoff"))
		{
			settingsTree
				.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		}
		else
		{
			settingsTree.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));
		}

		settingsTree.setRootVisible(false);
		settingsTree.setShowsRootHandles(true);
		settingsTree.getSelectionModel().setSelectionMode(
			TreeSelectionModel.SINGLE_TREE_SELECTION);
		settingsScroll =
				new JScrollPane(settingsTree,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		// Turn off the icons
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setLeafIcon(null);
		renderer.setOpenIcon(null);
		renderer.setClosedIcon(null);
		settingsTree.setCellRenderer(renderer);

		// Expand all of the branch nodes
		settingsTree.expandPath(new TreePath(characterNode.getPath()));
		settingsTree.expandPath(new TreePath(pcGenNode.getPath()));
		settingsTree.expandPath(new TreePath(appearanceNode.getPath()));
		settingsTree.expandPath(new TreePath(gameModeNode.getPath()));
		settingsTree.expandPath(new TreePath(pluginNode.getPath()));

		// Add the listener which switches panels when a node of the tree is selected
		settingsTree.addTreeSelectionListener(new TreeSelectionListener()
		{
			public void valueChanged(TreeSelectionEvent e)
			{
				DefaultMutableTreeNode node =
						(DefaultMutableTreeNode) settingsTree
							.getLastSelectedPathComponent();

				if (node == null)
				{
					return;
				}

				CardLayout cl = (CardLayout) (settingsPanel.getLayout());
				cl.show(settingsPanel, String.valueOf(node));
			}
		});

		// Build a scroller for the settings panels
		JScrollPane jScrollPane1 = new JScrollPane();
		JPanel borderPanel = new JPanel();
		borderPanel.setLayout(new BorderLayout());
		jScrollPane1.setViewportView(settingsPanel);
		borderPanel.add(jScrollPane1, BorderLayout.CENTER);
		
		// Build the split pane
		splitPane =
				new FlippingSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					settingsScroll, borderPanel);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerSize(10);

		// Build the control panel (OK/Cancel buttons)
		controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		JButton okButton = new JButton(LanguageBundle.getString("in_ok"));
		okButton.setMnemonic(LanguageBundle.getMnemonic("in_mn_ok"));
		controlPanel.add(okButton);
		okButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				okButtonActionPerformed();
			}
		});

		JButton cancelButton =
				new JButton(LanguageBundle.getString("in_cancel"));
		cancelButton.setMnemonic(LanguageBundle.getMnemonic("in_mn_cancel"));
		controlPanel.add(cancelButton);
		cancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				cancelButtonActionPerformed();
			}
		});
	}

	private JPanel buildTabsAppearancePanel()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
		Border etched = null;
		TitledBorder title1 = BorderFactory.createTitledBorder(etched, in_tabs);
		JPanel tabsPanel = new JPanel();

		title1.setTitleJustification(TitledBorder.LEFT);
		tabsPanel.setBorder(title1);
		gridbag = new GridBagLayout();
		tabsPanel.setLayout(gridbag);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(2, 2, 2, 2);

		Utility.buildConstraints(c, 0, 0, 2, 1, 0, 0);
		label = new JLabel(in_mainTabPlacement + ": ");
		gridbag.setConstraints(label, c);
		tabsPanel.add(label);
		Utility.buildConstraints(c, 2, 0, 1, 1, 0, 0);
		mainTabPlacementCombo =
				new JComboBoxEx(new String[]{in_tabPosTop, in_tabPosBottom,
					in_tabPosLeft, in_tabPosRight});
		gridbag.setConstraints(mainTabPlacementCombo, c);
		tabsPanel.add(mainTabPlacementCombo);

		Utility.buildConstraints(c, 0, 1, 2, 1, 0, 0);
		label = new JLabel(in_charTabPlacement + ": ");
		gridbag.setConstraints(label, c);
		tabsPanel.add(label);
		Utility.buildConstraints(c, 2, 1, 1, 1, 0, 0);
		charTabPlacementCombo =
				new JComboBoxEx(new String[]{in_tabPosTop, in_tabPosBottom,
					in_tabPosLeft, in_tabPosRight});
		gridbag.setConstraints(charTabPlacementCombo, c);
		tabsPanel.add(charTabPlacementCombo);

		Utility.buildConstraints(c, 0, 2, 2, 1, 0, 0);
		label = new JLabel(in_charTabLabel + ": ");
		gridbag.setConstraints(label, c);
		tabsPanel.add(label);
		Utility.buildConstraints(c, 2, 2, 1, 1, 0, 0);
		tabLabelsCombo =
				new JComboBoxEx(new String[]{in_tabLabelPlain, in_tabLabelEpic,
					in_tabLabelRace, in_tabLabelNetHack, in_tabLabelFull});
		gridbag.setConstraints(tabLabelsCombo, c);
		tabsPanel.add(tabLabelsCombo);

		Utility.buildConstraints(c, 0, 3, 2, 1, 0, 0);
		label = new JLabel(in_tabAbilities + ": ");
		gridbag.setConstraints(label, c);
		tabsPanel.add(label);
		Utility.buildConstraints(c, 2, 3, 1, 1, 0, 0);
		gridbag.setConstraints(displayAbilitiesAsTab, c);
		tabsPanel.add(displayAbilitiesAsTab);

		Utility.buildConstraints(c, 0, 4, 2, 1, 0, 0);
		label = new JLabel(in_expertGUI + ": ");
		gridbag.setConstraints(label, c);
		tabsPanel.add(label);
		Utility.buildConstraints(c, 2, 4, 1, 1, 0, 0);
		gridbag.setConstraints(expertGUICheckBox, c);
		tabsPanel.add(expertGUICheckBox);

		expertGUICheckBox.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent evt)
			{
				SettingsHandler.setExpertGUI(expertGUICheckBox.isSelected());
			}
		});

		Utility.buildConstraints(c, 5, 20, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		tabsPanel.add(label);

		return tabsPanel;
	}

	private void cancelButtonActionPerformed()
	{
		setVisible(false);
		this.dispose();
	}

	private void okButtonActionPerformed()
	{
		setOptionsBasedOnControls();
		applyPluginPreferences();
		setVisible(false);

		SettingsHandler.writeOptionsProperties(null);

		// We need to update the menus/toolbar since
		// some of those depend on the options
		PCGen_Frame1.enableDisableMenuItems();
		this.dispose();
	}

	private void selectThemePack()
	{
		JFileChooser fc =
				new JFileChooser(SettingsHandler.getPcgenThemePackDir());
		fc.setDialogTitle(LanguageBundle
			.getString("in_Prefs_chooseSkinDialogTitle"));

		String theme = SettingsHandler.getSkinLFThemePack();

		if (theme.length() > 0)
		{
			fc.setCurrentDirectory(new File(SettingsHandler
				.getSkinLFThemePack()));
			fc.setSelectedFile(new File(SettingsHandler.getSkinLFThemePack()));
		}

		fc.addChoosableFileFilter(new ThemePackFilter());

		if (fc.showOpenDialog(getParent().getParent()) == JFileChooser.APPROVE_OPTION) //ugly, but it works
		{
			File newTheme = fc.getSelectedFile();

			if (newTheme.isDirectory()
				|| (!newTheme.getName().endsWith("themepack.zip")))
			{
				ShowMessageDelegate.showMessageDialog(LanguageBundle
					.getString("in_Prefs_notAThemeErrorItem"), in_pcgen,
					MessageType.ERROR);
			}
			else
			{
				SettingsHandler.setSkinLFThemePack(newTheme.getAbsolutePath());

				if (SettingsHandler.getLookAndFeel() == laf.length)
				{
					try
					{
						SkinManager.applySkin();
					}
					catch (Exception e) //This is what applySkin actually throws...
					{
						//I can't think of anything better to do.
						SettingsHandler.setLookAndFeel(0);
						UIFactory.setLookAndFeel(0);
						ShowMessageDelegate.showMessageDialog(LanguageBundle
							.getString("in_Prefs_skinSetError")
							+ e.toString(), in_pcgen, MessageType.ERROR);
					}
				}
			}
		}
	}

	static final class ThemePackFilter extends FileFilter
	{
		// The description of this filter
		public String getDescription()
		{
			return "Themepacks (*themepack.zip)";
		}

		// Accept all directories and themepack.zip files.
		public boolean accept(File f)
		{
			if (f.isDirectory())
			{
				return true;
			}

			if (f.getName().endsWith("themepack.zip"))
			{
				return true;
			}

			return false;
		}
	}

	private final class PrefsButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent actionEvent)
		{
			JButton source = (JButton) actionEvent.getSource();

			if (source == null)
			{
				// Do nothing
			}
			else if ((source == prereqQualifyColor) || (source == prereqFailColor)
				|| (source == featAutoColor) || (source == featVirtualColor)
				|| (source == sourceStatusRelease) || (source == sourceStatusAlpha)
				|| (source == sourceStatusBeta) || (source == sourceStatusTest))
			{
				final Color newColor =
						JColorChooser.showDialog(Globals.getRootFrame(),
							LanguageBundle.getString("in_Prefs_colorSelect")
								+ source.getText().toLowerCase(), source
								.getForeground());

				if (newColor != null)
				{
					source.setForeground(newColor);

					if (source == prereqQualifyColor)
					{
						SettingsHandler
							.setPrereqQualifyColor(newColor.getRGB());
					}
					else if (source == prereqFailColor)
					{
						SettingsHandler.setPrereqFailColor(newColor.getRGB());
					}
					else if (source == featAutoColor)
					{
						SettingsHandler.setFeatAutoColor(newColor.getRGB());
					}
					else if (source == featVirtualColor)
					{
						SettingsHandler.setFeatVirtualColor(newColor.getRGB());
					}
					else if (source == sourceStatusRelease)
					{
						SettingsHandler.setSourceStatusReleaseColor(newColor.getRGB());
					}
					else if (source == sourceStatusAlpha)
					{
						SettingsHandler.setSourceStatusAlphaColor(newColor.getRGB());
					}
					else if (source == sourceStatusBeta)
					{
						SettingsHandler.setSourceStatusBetaColor(newColor.getRGB());
					}
					else if (source == sourceStatusTest)
					{
						SettingsHandler.setSourceStatusTestColor(newColor.getRGB());
					}
				}
			}
			else if (source == themepack)
			{
				selectThemePack();
				themepackLabel.setText(String.valueOf(SettingsHandler
					.getSkinLFThemePack()));
			}
			else if (source == browserPathButton)
			{
				Utility.selectDefaultBrowser(getParent());
				browserPath.setText(String.valueOf(SettingsHandler
					.getBrowserPath()));
			}
			else if (source == clearBrowserPathButton)
			{
				// If none is set, there is nothing to clear
				if (SettingsHandler.getBrowserPath() == null)
				{
					return;
				}

				final int choice =
						JOptionPane.showConfirmDialog(null, LanguageBundle
							.getString("in_Prefs_clearBrowserWarn"),
							LanguageBundle
								.getString("in_Prefs_clearBrowserTitle"),
							JOptionPane.YES_NO_OPTION);

				if (choice == JOptionPane.YES_OPTION)
				{
					SettingsHandler.setBrowserPath(null);
				}

				browserPath.setText(String.valueOf(SettingsHandler
					.getBrowserPath()));
			}
			else if (source == pcgenCharacterDirButton)
			{
				final String dialogTitle =
						LanguageBundle
							.getString("in_Prefs_pcgenCharacterDirTitle");
				final File currentPath = SettingsHandler.getPcgPath();
				final JTextField textField = pcgenCharacterDir;
				askForPath(currentPath, dialogTitle, textField);
			}
			else if (source == pcgenBackupCharacterDirButton)
			{
				final String dialogTitle =
						LanguageBundle
							.getString("in_Prefs_pcgenBackupCharacterDirTitle");
				final File currentPath = SettingsHandler.getBackupPcgPath();
				final JTextField textField = pcgenBackupCharacterDir;
				askForPath(currentPath, dialogTitle, textField);
			}
			else if (source == pcgenPortraitsDirButton)
			{
				final String dialogTitle =
						LanguageBundle
							.getString("in_Prefs_pcgenPortraitDirTitle");
				final File currentPath = SettingsHandler.getPortraitsPath();
				final JTextField textField = pcgenPortraitsDir;
				askForPath(currentPath, dialogTitle, textField);
			}
			else if (source == pcgenCustomDirButton)
			{
				final String dialogTitle =
						LanguageBundle
							.getString("in_Prefs_pcgenCustomDirTitle");
				final File currentPath = SettingsHandler.getPcgenCustomDir();
				final JTextField textField = pcgenCustomDir;
				askForPath(currentPath, dialogTitle, textField);
			}
			else if (source == pcgenVendorDataDirButton)
			{
				final String dialogTitle =
						LanguageBundle
							.getString("in_Prefs_pcgenVendorDataDirTitle");
				final File currentPath =
						SettingsHandler.getPcgenVendorDataDir();
				final JTextField textField = pcgenVendorDataDir;
				askForPath(currentPath, dialogTitle, textField);
			}
			else if (source == pcgenDataDirButton)
			{
				final String dialogTitle =
						LanguageBundle.getString("in_Prefs_pcgenDataDirTitle");
				final File currentPath = SettingsHandler.getPccFilesLocation();
				final JTextField textField = pcgenDataDir;
				askForPath(currentPath, dialogTitle, textField);
			}
			else if (source == pcgenDocsDirButton)
			{
				final String dialogTitle =
						LanguageBundle.getString("in_Prefs_pcgenDocsDirTitle");
				final File currentPath = SettingsHandler.getPcgenDocsDir();
				final JTextField textField = pcgenDocsDir;
				askForPath(currentPath, dialogTitle, textField);
			}
			else if (source == pcgenSystemDirButton)
			{
				final String dialogTitle =
						LanguageBundle
							.getString("in_Prefs_pcgenSystemDirTitle");
				final File currentPath = SettingsHandler.getPcgenSystemDir();
				final JTextField textField = pcgenSystemDir;
				askForPath(currentPath, dialogTitle, textField);
			}
			else if (source == pcgenFilesDirButton)
			{
				final String dialogTitle =
						LanguageBundle
							.getString("in_Prefs_pcgenFilesDirTitle");
				final File currentPath = SettingsHandler.getPcgenFilesDir();
				askForPath(currentPath, dialogTitle, pcgenFilesDir);
			}
			else if (source == pcgenOutputSheetDirButton)
			{
				final String dialogTitle =
						LanguageBundle
							.getString("in_Prefs_pcgenOutputSheetDirTitle");
				final File currentPath =
						SettingsHandler.getPcgenOutputSheetDir();
				final JTextField textField = pcgenOutputSheetDir;
				askForPath(currentPath, dialogTitle, textField);
			}
			else if (source == pcgenPreviewDirButton)
			{
				final String dialogTitle = LanguageBundle.getString("in_Prefs_pcgenPreviewDirTitle");
				final File currentPath = SettingsHandler.getPcgenPreviewDir();
				final JTextField textField = pcgenPreviewDir;
				askForPath(currentPath, dialogTitle, textField);
			}
			else if (source == outputSheetHTMLDefaultButton)
			{
				JFileChooser fc = new JFileChooser();
				fc.setDialogTitle(LanguageBundle
					.getString("in_Prefs_outputSheetHTMLDefaultTitle"));
				fc.setCurrentDirectory(new File(SettingsHandler
					.getHTMLOutputSheetPath()));
				fc.setSelectedFile(new File(SettingsHandler
					.getSelectedCharacterHTMLOutputSheet(null)));

				if (fc.showOpenDialog(getParent()) == JFileChooser.APPROVE_OPTION)
				{
					File newTemplate = fc.getSelectedFile();

					if (newTemplate.isDirectory()
						|| (!newTemplate.getName().startsWith("csheet") && !newTemplate
							.getName().startsWith("psheet")))
					{
						ShowMessageDelegate.showMessageDialog(LanguageBundle
							.getString("in_Prefs_outputSheetDefaultError"),
							in_pcgen, MessageType.ERROR);
					}
					else
					{
						if (newTemplate.getName().startsWith("csheet"))
						{
							SettingsHandler
								.setSelectedCharacterHTMLOutputSheet(
									newTemplate.getAbsolutePath(), null);
						}
						else
						{
							//it must be a psheet
							SettingsHandler
								.setSelectedPartyHTMLOutputSheet(newTemplate
									.getAbsolutePath());
						}
					}
				}

				outputSheetHTMLDefault.setText(String.valueOf(SettingsHandler
					.getSelectedCharacterHTMLOutputSheet(null)));
			}
			else if (source == outputSheetPDFDefaultButton)
			{
				JFileChooser fc = new JFileChooser();
				fc.setDialogTitle(LanguageBundle
					.getString("in_Prefs_outputSheetPDFDefaultTitle"));
				fc.setCurrentDirectory(new File(SettingsHandler
					.getPDFOutputSheetPath()));
				fc.setSelectedFile(new File(SettingsHandler
					.getSelectedCharacterPDFOutputSheet(null)));

				if (fc.showOpenDialog(getParent()) == JFileChooser.APPROVE_OPTION)
				{
					File newTemplate = fc.getSelectedFile();

					if (newTemplate.isDirectory()
						|| (!newTemplate.getName().startsWith("csheet") && !newTemplate
							.getName().startsWith("psheet")))
					{
						ShowMessageDelegate.showMessageDialog(LanguageBundle
							.getString("in_Prefs_outputSheetDefaultError"),
							in_pcgen, MessageType.ERROR);
					}
					else
					{
						if (newTemplate.getName().startsWith("csheet"))
						{
							SettingsHandler.setSelectedCharacterPDFOutputSheet(
								newTemplate.getAbsolutePath(), null);
						}
						else
						{
							//it must be a psheet
							SettingsHandler
								.setSelectedPartyPDFOutputSheet(newTemplate
									.getAbsolutePath());
						}
					}
				}

				outputSheetPDFDefault.setText(String.valueOf(SettingsHandler
					.getSelectedCharacterPDFOutputSheet(null)));
			}
			else if (source == outputSheetEqSetButton)
			{
				JFileChooser fc = new JFileChooser();
				fc.setDialogTitle(LanguageBundle
					.getString("in_Prefs_templateEqSetTitle"));
				fc
					.setCurrentDirectory(SettingsHandler
						.getPcgenOutputSheetDir());
				fc.setSelectedFile(new File(SettingsHandler
					.getSelectedEqSetTemplate()));

				if (fc.showOpenDialog(getParent()) == JFileChooser.APPROVE_OPTION)
				{
					File newTemplate = fc.getSelectedFile();

					if (newTemplate.isDirectory()
						|| !newTemplate.getName().startsWith("eqsheet"))
					{
						ShowMessageDelegate.showMessageDialog(LanguageBundle
							.getString("in_Prefs_templateEqSetError"),
							in_pcgen, MessageType.ERROR);
					}
					else
					{
						//it must be a psheet
						SettingsHandler.setSelectedEqSetTemplate(newTemplate
							.getAbsolutePath());
					}
				}

				outputSheetEqSet.setText(String.valueOf(SettingsHandler
					.getSelectedEqSetTemplate()));
			}
			else if (source == outputSheetSpellsDefaultButton)
			{
				JFileChooser fc = new JFileChooser();
				fc.setDialogTitle(LanguageBundle
					.getString("in_Prefs_outputSpellSheetDefault"));
				fc
					.setCurrentDirectory(SettingsHandler
						.getPcgenOutputSheetDir());
				fc.setSelectedFile(new File(SettingsHandler
					.getSelectedSpellSheet()));

				if (fc.showOpenDialog(getParent()) == JFileChooser.APPROVE_OPTION)
				{
					File newTemplate = fc.getSelectedFile();

					if (newTemplate.isDirectory()
						|| !newTemplate.getName().startsWith("csheet"))
					{
						ShowMessageDelegate.showMessageDialog(LanguageBundle
							.getString("in_Prefs_outputSheetDefaultError"),
							in_pcgen, MessageType.ERROR);
					}
					else
					{
						//it must be a psheet
						SettingsHandler.setSelectedSpellSheet(newTemplate
							.getAbsolutePath());
					}
				}

				outputSheetSpellsDefault.setText(String.valueOf(SettingsHandler
					.getSelectedSpellSheet()));
			}
		}

		/**
		 * Ask for a path, and return it (possibly return the currentPath.)
		 * @param currentPath when entering the method
		 * @param dialogTitle to show
		 * @param textField to update with the path information
		 * @return A path to the directory.
		 */
		private File askForPath(final File currentPath,
			final String dialogTitle, final JTextField textField)
		{
			File returnFile = currentPath;
			JFileChooser fc = null;

			if (currentPath == null)
			{
				fc = new JFileChooser();
			}
			else
			{
				fc = new JFileChooser(currentPath);
			}

			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.setDialogTitle(dialogTitle);

			if (SystemUtils.IS_OS_MAC)
			{
				// On MacOS X, do not traverse file bundles
				fc.putClientProperty("JFileChooser.appBundleIsTraversable",
					"never");
			}

			final int returnVal = fc.showOpenDialog(getParent());

			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				returnFile = fc.getSelectedFile();
			}

			textField.setText(String.valueOf(returnFile));

			return returnFile;
		}
	}

	// This is the focus listener so that text field values may be manually entered.
	// sage_sam April 2003 for FREQ 707022
	private final class TextFocusLostListener implements FocusListener
	{
		private String initialValue = null;
		private boolean dialogOpened = false;

		/**
		 * @see java.awt.event.FocusListener#focusGained(FocusEvent)
		 */
		public void focusGained(FocusEvent e)
		{
			// reset variables
			dialogOpened = false;

			final Object source = e.getSource();

			if (source instanceof JTextField)
			{
				// get the field value
				initialValue = ((JTextField) source).getText();
			}
		}

		/**
		 * @see java.awt.event.FocusListener#focusLost(FocusEvent)
		 */
		public void focusLost(FocusEvent e)
		{
			// Check the source to see if it was a text field
			final Object source = e.getSource();

			if (source instanceof JTextField)
			{
				// get the field value and validate it exists
				final String fieldValue = ((JTextField) source).getText();
				final File fieldFile = new File(fieldValue);

				if ((!fieldFile.exists())
					&& (!fieldValue.equalsIgnoreCase("null"))
					&& (fieldValue.trim().length() > 0) && (!dialogOpened))
				{
					// display error dialog and restore previous value
					dialogOpened = true;
					ShowMessageDelegate.showMessageDialog(
						"File does not exist; preferences were not set.",
						"Invalid Path", MessageType.ERROR);
					((JTextField) source).setText(initialValue);
				}
			}
		}
	}

	public static class PreferencesComponent implements GMBComponent
	{
		private List<PreferencesPanel> panelList =
				new ArrayList<PreferencesPanel>();
		private List<String> nameList = new ArrayList<String>();

		public void handleMessage(GMBMessage message)
		{
			if (message instanceof PreferencesPanelAddMessage)
			{
				PreferencesPanelAddMessage pmessage =
						(PreferencesPanelAddMessage) message;
				panelList.add(pmessage.getPane());
				nameList.add(pmessage.getName());
			}
		}

		public List<String> getNameList()
		{
			return nameList;
		}

		public List<PreferencesPanel> getPanelList()
		{
			return panelList;
		}
	}
}
