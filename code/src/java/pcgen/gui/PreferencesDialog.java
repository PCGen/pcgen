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
import pcgen.core.*;
import pcgen.core.system.GameModeRollMethod;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui.panes.FlippingSplitPane;
import pcgen.gui.utils.*;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;
import pcgen.util.SkinLFResourceChecker;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;

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
	private static String in_abilities =
			PropertyFactory.getString("in_Prefs_abilities");
	//PropertyFactory.getString("in_Prefs_abilitiesUserRolled");
	//PropertyFactory.getString("in_Prefs_abilitiesAllSame");
	//PropertyFactory.getString("in_Prefs_abilitiesPurchased");
	private static String in_allowMetamagic =
			PropertyFactory.getString("in_Prefs_allowMetamagic");
	private static String in_allowOverride =
			PropertyFactory.getString("in_Prefs_allowOverride");
	private static String in_alwaysOverwrite =
			PropertyFactory.getString("in_Prefs_alwaysOverwrite");
	private static String in_appearance =
			PropertyFactory.getString("in_Prefs_appearance");
	private static String in_autoLoadAtStart =
			PropertyFactory.getString("in_Prefs_autoLoadAtStart");
	private static String in_autoLoadWithPC =
			PropertyFactory.getString("in_Prefs_autoLoadWithPC");
	private static String in_anyAutoEquip =
			PropertyFactory.getString("in_Prefs_anyAutoEquip");
	private static String in_autoEquip =
			PropertyFactory.getString("in_Prefs_autoEquip");
	private static String in_autoEquipRace =
			PropertyFactory.getString("in_Prefs_autoEquipRace");
	private static String in_autoEquipMasterwork =
			PropertyFactory.getString("in_Prefs_autoEquipMasterwork");
	private static String in_autoEquipMagic =
			PropertyFactory.getString("in_Prefs_autoEquipMagic");
	private static String in_autoEquipExotic =
			PropertyFactory.getString("in_Prefs_autoEquipExotic");
	private static String in_browserPath =
			PropertyFactory.getString("in_Prefs_browserPath");
	private static String in_clearBrowserPath =
			PropertyFactory.getString("in_Prefs_clearBrowserPath");
	private static String in_color =
			PropertyFactory.getString("in_Prefs_color");
	private static String in_colorPrereqQualify =
			PropertyFactory.getString("in_Prefs_colorPrereqQualify");
	private static String in_colorPrereqFail =
			PropertyFactory.getString("in_Prefs_colorPrereqFail");
	private static String in_colorAutoFeat =
			PropertyFactory.getString("in_Prefs_colorAutoFeat");
	private static String in_colorVirtFeat =
			PropertyFactory.getString("in_Prefs_colorVirtFeat");
	private static String in_charTabPlacement =
			PropertyFactory.getString("in_Prefs_charTabPlacement");
	private static String in_charTabLabel =
			PropertyFactory.getString("in_Prefs_charTabLabel");
	private static String in_character =
			PropertyFactory.getString("in_Prefs_character");
	//PropertyFactory.getString("in_Prefs_chooseSkin");
	private static String in_cmNone =
			PropertyFactory.getString("in_Prefs_cmNone");
	private static String in_cmSelect =
			PropertyFactory.getString("in_Prefs_cmSelect");
	private static String in_cmSelectExit =
			PropertyFactory.getString("in_Prefs_cmSelectExit");
	private static String in_displayOGL =
			PropertyFactory.getString("in_Prefs_displayOGL");
	private static String in_displayMature =
			PropertyFactory.getString("in_Prefs_displayMature");
	private static String in_displayd20 =
			PropertyFactory.getString("in_Prefs_displayd20");
	private static String in_displaySponsors =
			PropertyFactory.getString("in_Prefs_displaySponsors");
	private static String in_dialogTitle =
			PropertyFactory.getString("in_Prefs_title");
	private static String in_displayOpts =
			PropertyFactory.getString("in_Prefs_displayOpts");
	private static String in_expertGUI =
			PropertyFactory.getString("in_Prefs_expertGUI");
	private static String in_enforceSpending =
			PropertyFactory.getString("in_Prefs_enforceSpending");
	private static String in_equipment =
			PropertyFactory.getString("in_Prefs_equipment");
	//	private static String in_featWindow = PropertyFactory.getString("in_Prefs_featWindow");
	private static String in_hp = PropertyFactory.getString("in_Prefs_hp");
	private static String in_houseRules =
			PropertyFactory.getString("in_Prefs_houseRules");
	private static String in_hpWindow =
			PropertyFactory.getString("in_Prefs_hpWindow");
	private static String in_invalidToHitText =
			PropertyFactory.getString("in_Prefs_invalidToHitText");
	private static String in_invalidDmgText =
			PropertyFactory.getString("in_Prefs_invalidDmgText");
	private static String in_loadURLs =
			PropertyFactory.getString("in_Prefs_loadURLs");
	private static String in_language =
			PropertyFactory.getString("in_Prefs_language");
	private static String in_langEnglish =
			PropertyFactory.getString("in_Prefs_langEnglish");
	private static String in_langFrench =
			PropertyFactory.getString("in_Prefs_langFrench");
	private static String in_langGerman =
			PropertyFactory.getString("in_Prefs_langGerman");
	private static String in_langItalian =
			PropertyFactory.getString("in_Prefs_langItalian");
	private static String in_langSpanish =
			PropertyFactory.getString("in_Prefs_langSpanish");
	private static String in_langPortuguese =
			PropertyFactory.getString("in_Prefs_langPortuguese");
	private static String in_langSystem =
			PropertyFactory.getString("in_Prefs_langSystem");
	private static String in_location =
			PropertyFactory.getString("in_Prefs_location");
	private static String in_lookAndFeel =
			PropertyFactory.getString("in_Prefs_lookAndFeel");
	private static String in_aaText =
			PropertyFactory.getString("in_Prefs_aaText");
	private static String in_levelUp =
			PropertyFactory.getString("in_Prefs_levelUp");
	private static String in_monsters =
			PropertyFactory.getString("in_Prefs_monsters");
	private static String in_mainTabPlacement =
			PropertyFactory.getString("in_Prefs_mainTabPlacement");
	private static String in_noAutoEquip =
			PropertyFactory.getString("in_Prefs_noAutoEquip");
	private static String in_output =
			PropertyFactory.getString("in_Prefs_output");
	private static String in_outputSheetEqSet =
			PropertyFactory.getString("in_Prefs_templateEqSet");
	private static String in_pcgen =
			PropertyFactory.getString("in_Prefs_pcgen");
	//PropertyFactory.getString("in_Prefs_purchaseModeConfig");
	private static String in_potionMax =
			PropertyFactory.getString("in_Prefs_potionMax");
	private static String in_paperType =
			PropertyFactory.getString("in_Prefs_paperType");
	private static String in_postExportCommandStandard =
			PropertyFactory.getString("in_Prefs_postExportCommandStandard");
	private static String in_postExportCommandPDF =
			PropertyFactory.getString("in_Prefs_postExportCommandPDF");
	private static String in_removeTemp =
			PropertyFactory.getString("in_Prefs_removeTemp");
	private static String in_statWindow =
			PropertyFactory.getString("in_Prefs_statWindow");
	private static String in_showToolTips =
			PropertyFactory.getString("in_Prefs_showToolTips");
	private static String in_showToolBar =
			PropertyFactory.getString("in_Prefs_showToolBar");
	private static String in_showFeatDescription =
			PropertyFactory.getString("in_Prefs_showFeatDesciption");
	private static String in_singleChoiceOption =
			PropertyFactory.getString("in_Prefs_singleChoiceOption");
	private static String in_skinnedLAF =
			PropertyFactory.getString("in_Prefs_skinnedLAF");
	private static String in_sources =
			PropertyFactory.getString("in_Prefs_sources");
	private static String in_saveCustom =
			PropertyFactory.getString("in_Prefs_saveCustom");
	private static String in_saveOutputSheetWithPC =
			PropertyFactory.getString("in_Prefs_saveOutputSheetWithPC");
	private static String in_sdLong =
			PropertyFactory.getString("in_Prefs_sdLong");
	private static String in_sdPage =
			PropertyFactory.getString("in_Prefs_sdPage");
	private static String in_sdShort =
			PropertyFactory.getString("in_Prefs_sdShort");
	private static String in_sdMedium =
			PropertyFactory.getString("in_Prefs_sdMedium");
	private static String in_sdWeb =
			PropertyFactory.getString("in_Prefs_sdWeb");
	private static String in_showMemory =
			PropertyFactory.getString("in_Prefs_showMemory");
	private static String in_showImagePreview =
			PropertyFactory.getString("in_Prefs_showImagePreview");
	private static String in_showSkillModifierBreakdown =
			PropertyFactory.getString("in_Prefs_showSkillModifierBreakdown");
	private static String in_sourceDisplay =
			PropertyFactory.getString("in_Prefs_sourceDisplay");
	private static String in_tabs = PropertyFactory.getString("in_Prefs_tabs");
	private static String in_tabLabelPlain =
			PropertyFactory.getString("in_Prefs_tabLabelPlain");
	private static String in_tabLabelEpic =
			PropertyFactory.getString("in_Prefs_tabLabelEpic");
	private static String in_tabLabelRace =
			PropertyFactory.getString("in_Prefs_tabLabelRace");
	private static String in_tabLabelNetHack =
			PropertyFactory.getString("in_Prefs_tabLabelNetHack");
	private static String in_tabLabelFull =
			PropertyFactory.getString("in_Prefs_tabLabelFull");
	private static String in_tabPosTop =
			PropertyFactory.getString("in_Prefs_tabPosTop");
	private static String in_tabPosBottom =
			PropertyFactory.getString("in_Prefs_tabPosBottom");
	private static String in_tabPosLeft =
			PropertyFactory.getString("in_Prefs_tabPosLeft");
	private static String in_tabPosRight =
			PropertyFactory.getString("in_Prefs_tabPosRight");
	private static String in_tabAbilities =
			PropertyFactory.getString("in_Prefs_tabAbilities");
	private static String in_unitSetType =
			PropertyFactory.getString("in_Prefs_unitSetType");
	private static String in_useAutoWaitCursor =
			PropertyFactory.getString("in_Prefs_useAutoWaitCursor");
	private static String in_useOutputNamesEquipment =
			PropertyFactory.getString("in_Prefs_useOutputNamesEquipment");
	private static String in_useOutputNamesSpells =
			PropertyFactory.getString("in_Prefs_useOutputNamesSpells");
	private static String in_wandMax =
			PropertyFactory.getString("in_Prefs_wandMax");
	private static String in_warnFirstLevelUp =
			PropertyFactory.getString("in_Prefs_warnFirstLevelUp");
	private static String in_weaponProfPrintout =
			PropertyFactory.getString("in_Prefs_weaponProfPrintout");
	private static String in_skillChoice =
			PropertyFactory.getString("in_Prefs_skillChoiceLabel");
	private static String in_skillChoiceNone =
			PropertyFactory.getString("in_Prefs_skillChoiceNone");
	private static String in_skillChoiceUntrained =
			PropertyFactory.getString("in_Prefs_skillChoiceUntrained");
	private static String in_skillChoiceAll =
			PropertyFactory.getString("in_Prefs_skillChoiceAll");
	private static String in_skillChoiceAsUI =
			PropertyFactory.getString("in_Prefs_skillChoiceAsUI");
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
	private JButton pcgenPortraitsDirButton;
	private JButton pcgenSystemDirButton;
	private JButton pcgenBackupCharacterDirButton;
	private JButton prereqFailColor;

	// Colors
	private JButton prereqQualifyColor;
	private JButton purchaseModeButton;
	private JButton themepack;

	// Equipment
	private JCheckBox allowMetamagicInEqBuilder = new JCheckBox();
	private JCheckBox allowOptsInSource = new JCheckBox();
	private JCheckBox autoMethod1 = new JCheckBox();
	private JCheckBox autoMethod2 = new JCheckBox();
	private JCheckBox autoMethod3 = new JCheckBox();
	private JCheckBox autoMethod4 = new JCheckBox();

	// Sources
	private JCheckBox campLoad = new JCheckBox();
	private JCheckBox charCampLoad = new JCheckBox();
	private JCheckBox displayAbilitiesAsTab = new JCheckBox();
	private JCheckBox expertGUICheckBox = new JCheckBox();
	private JCheckBox featDescriptionShown = new JCheckBox();
	//	private JCheckBox featDialogShownAtLevelUp = new JCheckBox();
	private JCheckBox hideMonsterClasses = new JCheckBox();

	// Level Up
	private JCheckBox hpDialogShownAtLevelUp = new JCheckBox();
	private JCheckBox ignoreMonsterHDCap = new JCheckBox();
	private JCheckBox loadURL = new JCheckBox();
	private JCheckBox maxHpAtFirstLevel = new JCheckBox();
	private JCheckBox printSpellsWithPC = new JCheckBox();
	private JCheckBox removeTempFiles;
	private JCheckBox saveCustom = new JCheckBox();
	private JCheckBox saveOutputSheetWithPC = new JCheckBox();
	private JCheckBox showOGL = new JCheckBox();
	private JCheckBox showMature = new JCheckBox();
	private JCheckBox showToolbar = new JCheckBox();
	private JCheckBox showWarningAtFirstLevelUp = new JCheckBox();
	private JCheckBox showd20 = new JCheckBox();
	private JCheckBox showSponsors = new JCheckBox();
	private JCheckBox statDialogShownAtLevelUp = new JCheckBox();
	private JCheckBox showSkillModifier = new JCheckBox();
	private JCheckBox enforceSpendingBeforeLevelUp = new JCheckBox();
	private JCheckBox allowOverride = new JCheckBox();

	// Displayed
	private JCheckBox toolTipTextShown = new JCheckBox();
	private JCheckBox showMemory = new JCheckBox();
	private JCheckBox showImagePreview = new JCheckBox();

	// "Monsters"
	private JCheckBox useMonsterDefault = new JCheckBox();
	private JCheckBox useOutputNamesEquipment = new JCheckBox();
	private JCheckBox useOutputNamesSpells = new JCheckBox();
	private JCheckBox waitCursor = new JCheckBox();
	private JCheckBox weaponProfPrintout;
	private JComboBoxEx abilityPurchaseModeCombo;
	private JComboBoxEx abilityScoreCombo;
	private JComboBoxEx abilityRolledModeCombo = null;
	private JComboBoxEx charTabPlacementCombo;
	private JComboBoxEx cmbChoiceMethods = new JComboBoxEx(singleChoiceMethods);
	//	private JComboBoxEx crossClassSkillCostCombo = new JComboBoxEx(new String[]{ "0  ", "1  ", "2  " });

	// Tab Options
	private JComboBoxEx mainTabPlacementCombo;
	private JComboBoxEx paperType = new JComboBoxEx();
	private JComboBoxEx potionMaxLevel = new JComboBoxEx();
	private JComboBoxEx skillChoice = new JComboBoxEx();
	private JComboBoxEx sourceOptions = new JComboBoxEx();
	private JComboBoxEx tabLabelsCombo;
	private JComboBoxEx unitSetType = new JComboBoxEx();
	private JComboBoxEx wandMaxLevel = new JComboBoxEx();
	private JPanel controlPanel;
	private JPanel settingsPanel;
	private JRadioButton abilitiesAllSameButton;
	private JRadioButton abilitiesPurchasedButton;
	private JRadioButton abilitiesRolledButton;

	// Abilities
	private JRadioButton abilitiesUserRolledButton;
	private JRadioButton autoEquipCreate;

	// "HP Roll Methods"
	private JRadioButton hpAutomax =
			new JRadioButton(PropertyFactory.getString("in_Prefs_hpAutoMax"));
	private JRadioButton hpAverage =
			new JRadioButton(PropertyFactory.getString("in_Prefs_hpAverage"));
	private JRadioButton hpPercentage =
			new JRadioButton(PropertyFactory.getString("in_Prefs_hpPercentage"));
	private JRadioButton hpStandard =
			new JRadioButton(PropertyFactory.getString("in_Prefs_hpStandard"));
	private JRadioButton hpUserRolled =
			new JRadioButton(PropertyFactory.getString("in_Prefs_hpUserRolled"));

	// Language
	private JRadioButton langEng;
	private JRadioButton langFre;
	private JRadioButton langGer;
	private JRadioButton langIt;
	private JRadioButton langEs;
	private JRadioButton langPt;
	private JRadioButton langSystem;
	private JRadioButton noAutoEquipCreate;
	private JRadioButton pcgenFilesDirRadio;
	private JRadioButton selectFilesDirRadio;
	private JRadioButton skinnedLookFeel = new JRadioButton();
	private JCheckBox aaText = new JCheckBox();
	private JRadioButton usersFilesDirRadio;
	private JScrollPane settingsScroll;

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

	// Output
	private JTextField pcgenPortraitsDir;
	private JTextField pcgenSystemDir;
	private JTextField postExportCommandStandard;
	private JTextField postExportCommandPDF;
	private JTextField themepackLabel;
	private JTree settingsTree;
	private List<RuleCheck> ruleCheckList = new ArrayList<RuleCheck>();
	private JTextField invalidToHitText;
	private JTextField invalidDmgText;
	private JCheckBox alwaysOverwrite;

	// Listeners
	private PrefsButtonListener prefsButtonHandler = new PrefsButtonListener();
	private PurchaseModeFrame pmsFrame = null;
	private final TextFocusLostListener textFieldListener =
			new TextFocusLostListener();
	private WholeNumberField hpPct = new WholeNumberField(0, 6);
	//	private String[] allSameValue = new String[STATMAX - STATMIN + 1];

	// "House Rules"
	private JCheckBox[] hrBoxes = null;
	private ButtonGroup[] hrGroup = null;
	private JRadioButton[] hrRadio = null;

	// Look and Feel
	private JRadioButton[] laf;
	private String[] pMode;
	private String[] pModeMethodName;
	private String[] paperNames = null;
	private String[] unitSetNames = null;

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

		addAbilitiesPanelListeners();

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
		final GameMode gameMode = SettingsHandler.getGame();

		// Abilities
		gameMode.setAllStatsValue(abilityScoreCombo.getSelectedIndex()
			+ gameMode.getStatMin());

		if (abilitiesUserRolledButton.isSelected())
		{
			gameMode.setRollMethod(Constants.CHARACTERSTATMETHOD_USER);
		}
		else if (abilitiesAllSameButton.isSelected())
		{
			gameMode.setRollMethod(Constants.CHARACTERSTATMETHOD_ALLSAME);
		}
		else if (abilitiesPurchasedButton.isSelected())
		{
			if (abilityPurchaseModeCombo.isVisible()
				&& (abilityPurchaseModeCombo.getSelectedIndex() >= 0))
			{
				gameMode
					.setPurchaseMethodName(pModeMethodName[abilityPurchaseModeCombo
						.getSelectedIndex()]);
			}
			else
			{
				gameMode.setRollMethod(Constants.CHARACTERSTATMETHOD_USER);
			}
		}
		else if ((abilitiesRolledButton != null)
			&& (abilitiesRolledButton.isSelected()))
		{
			if (abilityRolledModeCombo.getSelectedIndex() >= 0)
			{
				gameMode.setRollMethodExpressionByName(abilityRolledModeCombo
					.getSelectedItem().toString());
			}
			else
			{
				gameMode.setRollMethod(Constants.CHARACTERSTATMETHOD_USER);
			}
		}

		//
		// Update summary tab in case we have changed from/to rolled stats method
		//
		final CharacterInfo characterPane = PCGen_Frame1.getCharacterPane();
		if (characterPane != null)
		{
			characterPane.setPaneForUpdate(characterPane.infoSummary());
			characterPane.setRefresh(true);
			characterPane.refresh();
		}

		// Hit points
		if (hpStandard.isSelected())
		{
			SettingsHandler.setHPRollMethod(Constants.HP_STANDARD);
		}
		else if (hpAutomax.isSelected())
		{
			SettingsHandler.setHPRollMethod(Constants.HP_AUTOMAX);
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
			SettingsHandler.setHPRollMethod(Constants.HP_USERROLLED);
		}

		SettingsHandler.setHPPct(hpPct.getValue());
		SettingsHandler.setHPMaxAtFirstLevel(maxHpAtFirstLevel.isSelected());

		// House Rules

		for (int i = 0; i < hrBoxes.length; i++)
		{
			if (hrBoxes[i] != null)
			{
				String aKey = hrBoxes[i].getText();
				boolean aBool = hrBoxes[i].isSelected();

				// Save settings
				if (gameMode.hasRuleCheck(aKey))
				{
					SettingsHandler.setRuleCheck(aKey, aBool);
				}
			}
		}

		for (int i = 0; i < hrRadio.length; i++)
		{
			if (hrRadio[i] != null)
			{
				String aKey = hrRadio[i].getText();
				boolean aBool = hrRadio[i].isSelected();

				// Save settings
				if (gameMode.hasRuleCheck(aKey))
				{
					SettingsHandler.setRuleCheck(aKey, aBool);
				}
			}
		}

		//		SettingsHandler.setIntCrossClassSkillCost(crossClassSkillCostCombo.getSelectedIndex());

		// Monsters
		SettingsHandler.setMonsterDefault(useMonsterDefault.isSelected());
		SettingsHandler.setHideMonsterClasses(hideMonsterClasses.isSelected());
		SettingsHandler.setIgnoreMonsterHDCap(ignoreMonsterHDCap.isSelected());

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

		// Also give the UI a chance to update is we change the setting.
		// UIFactory keeps track of the current setting for us.
		SettingsHandler.setAaText(aaText.isSelected());
		UIFactory.setAaText(aaText.isSelected());

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
					ShowMessageDelegate.showMessageDialog(PropertyFactory
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
						ShowMessageDelegate.showMessageDialog(PropertyFactory
							.getString("in_Prefs_skinSetError")
							+ e.toString(), in_pcgen, MessageType.ERROR);
					}
				}
			}
			else
			{
				Logging.errorPrint(SkinLFResourceChecker
					.getMissingResourceMessage());

				//final String missingLibMsg = PropertyFactory.getString("MissingLibMessage").replace('|', '\n');
				//GuiFacade.showMessageDialog(null, SkinLFResourceChecker.getMissingResourceMessage() + missingLibMsg, Constants.s_APPNAME, GuiFacade.WARNING_MESSAGE);
				new LinkableHtmlMessage(this, SkinLFResourceChecker
					.getMissingResourceMessage(), Constants.s_APPNAME)
					.setVisible(true);
			}
		}

		// Level up
		SettingsHandler.setShowHPDialogAtLevelUp(hpDialogShownAtLevelUp
			.isSelected());
		//SettingsHandler.setShowFeatDialogAtLevelUp(featDialogShownAtLevelUp.isSelected());
		SettingsHandler.setShowStatDialogAtLevelUp(statDialogShownAtLevelUp
			.isSelected());
		SettingsHandler.setShowWarningAtFirstLevelUp(showWarningAtFirstLevelUp
			.isSelected());
		SettingsHandler
			.setEnforceSpendingBeforeLevelUp(enforceSpendingBeforeLevelUp
				.isSelected());

		// Equipment
		SettingsHandler
			.setMetamagicAllowedInEqBuilder(allowMetamagicInEqBuilder
				.isSelected());
		SettingsHandler.setMaxPotionSpellLevel(potionMaxLevel
			.getSelectedIndex()
			+ SPELLLVLMIN);
		SettingsHandler.setMaxWandSpellLevel(wandMaxLevel.getSelectedIndex()
			+ SPELLLVLMIN);
		SettingsHandler.setWantToLoadMasterworkAndMagic(false); // Turn it off temporarily so we can set the values
		SettingsHandler.setAutogen(Constants.AUTOGEN_RACIAL, autoMethod1
			.isSelected());
		SettingsHandler.setAutogen(Constants.AUTOGEN_MASTERWORK, autoMethod2
			.isSelected());
		SettingsHandler.setAutogen(Constants.AUTOGEN_MAGIC, autoMethod3
			.isSelected());
		SettingsHandler.setAutogen(Constants.AUTOGEN_EXOTICMATERIAL,
			autoMethod4.isSelected());
		SettingsHandler.setWantToLoadMasterworkAndMagic(noAutoEquipCreate
			.isSelected()); // Now set it properly

		// Language
		if (langEng.isSelected())
		{
			Globals.setLanguage("en");
			Globals.setCountry("US");
		}
		else if (langFre.isSelected())
		{
			Globals.setLanguage("fr");
			Globals.setCountry("FR");
		}
		else if (langGer.isSelected())
		{
			Globals.setLanguage("de");
			Globals.setCountry("DE");
		}
		else if (langIt.isSelected())
		{
			Globals.setLanguage("it");
			Globals.setCountry("IT");
		}
		else if (langEs.isSelected())
		{
			Globals.setLanguage("es");
			Globals.setCountry("ES");
		}
		else if (langPt.isSelected())
		{
			Globals.setLanguage("pt");
			Globals.setCountry("PT");
		}
		else if (langSystem.isSelected())
		{
			Globals.setLanguage(null);
			Globals.setCountry(null);
		}

		SettingsHandler.getGame().selectUnitSet(
			(String) unitSetType.getSelectedItem());

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
		SettingsHandler.setLoadCampaignsAtStart(campLoad.isSelected());
		SettingsHandler.setLoadCampaignsWithPC(charCampLoad.isSelected());
		SettingsHandler.setOptionAllowedInSources(allowOptsInSource
			.isSelected());
		SettingsHandler.setSaveCustomEquipment(saveCustom.isSelected());
		SettingsHandler.setShowLicense(showOGL.isSelected());
		SettingsHandler.setShowMature(showMature.isSelected());
		SettingsHandler.setShowD20Info(showd20.isSelected());
		SettingsHandler.setShowSponsors(showSponsors.isSelected());
		SettingsHandler.setLoadURLs(loadURL.isSelected());
		SettingsHandler.setAllowOverride(allowOverride.isSelected());

		// TODO - Fix this. We should not do this with a switch.
		switch (sourceOptions.getSelectedIndex())
		{
			case 0:
				Globals.setSourceDisplay(SourceEntry.SourceFormat.LONG);

				break;

			case 1:
				Globals.setSourceDisplay(SourceEntry.SourceFormat.MEDIUM);

				break;

			case 2:
				Globals.setSourceDisplay(SourceEntry.SourceFormat.SHORT);

				break;

			case 3:
				Globals.setSourceDisplay(SourceEntry.SourceFormat.PAGE);

				break;

			case 4:
				Globals.setSourceDisplay(SourceEntry.SourceFormat.WEB);

				break;

			default:
				Logging
					.errorPrint("In PreferencesDialog.setOptionsBasedOnControls (sourceOptions) the index "
						+ sourceOptions.getSelectedIndex() + " is unsupported.");

				break;
		}

		// Now get any panels affected to refresh
		CharacterInfo ci = PCGen_Frame1.getCharacterPane();
		if (ci != null)
		{
			ci.refresh();
		}
	}

	private void applyOptionValuesToControls()
	{
		final GameMode gameMode = SettingsHandler.getGame();
		boolean bValid = true;

		// Abilities
		final int rollMethod = gameMode.getRollMethod();

		switch (rollMethod)
		{
			case Constants.CHARACTERSTATMETHOD_USER:
				abilitiesUserRolledButton.setSelected(true);

				break;

			case Constants.CHARACTERSTATMETHOD_ALLSAME:
				abilitiesAllSameButton.setSelected(true);

				break;

			case Constants.CHARACTERSTATMETHOD_PURCHASE:
				if (!abilitiesPurchasedButton.isVisible()
					|| (pMode.length == 0))
				{
					bValid = false;
				}
				else
				{
					abilitiesPurchasedButton.setSelected(true);
				}

				break;

			case Constants.CHARACTERSTATMETHOD_ROLLED:
				if (abilitiesRolledButton == null)
				{
					bValid = false;
				}
				else
				{
					abilitiesRolledButton.setSelected(true);
					abilityRolledModeCombo.setSelectedItem(gameMode
						.getRollMethodExpressionName());
				}

				break;

			default:
				bValid = false;

				break;
		}

		if (!bValid)
		{
			abilitiesUserRolledButton.setSelected(true);
			gameMode.setRollMethod(Constants.CHARACTERSTATMETHOD_USER);
		}

		final int allStatsValue =
				Math.min(gameMode.getStatMax(), gameMode.getAllStatsValue());
		gameMode.setAllStatsValue(allStatsValue);
		abilityScoreCombo.setSelectedIndex(allStatsValue
			- gameMode.getStatMin());

		if ((pMode != null) && (pModeMethodName != null))
		{
			final String methodName = gameMode.getPurchaseModeMethodName();

			for (int i = 0; i < pMode.length; ++i)
			{
				if (pModeMethodName[i].equals(methodName))
				{
					abilityPurchaseModeCombo.setSelectedIndex(i);
				}
			}
		}

		// Hit Points
		switch (SettingsHandler.getHPRollMethod())
		{
			case Constants.HP_AUTOMAX:
				hpAutomax.setSelected(true);

				break;

			case Constants.HP_AVERAGE:
				hpAverage.setSelected(true);

				break;

			case Constants.HP_PERCENTAGE:
				hpPercentage.setSelected(true);

				break;

			case Constants.HP_USERROLLED:
				hpUserRolled.setSelected(true);

				break;

			case Constants.HP_STANDARD:
				//No break
			default:
				hpStandard.setSelected(true);

				break;
		}

		hpPct.setValue(SettingsHandler.getHPPct());
		maxHpAtFirstLevel.setSelected(SettingsHandler.isHPMaxAtFirstLevel());

		// House Rules
		//		crossClassSkillCostCombo.setSelectedIndex(SettingsHandler.getIntCrossClassSkillCost());

		// Monsters
		useMonsterDefault.setSelected(SettingsHandler.isMonsterDefault());
		hideMonsterClasses.setSelected(SettingsHandler.hideMonsterClasses());
		ignoreMonsterHDCap.setSelected(SettingsHandler.isIgnoreMonsterHDCap());

		// Colors
		prereqQualifyColor.setForeground(new Color(SettingsHandler
			.getPrereqQualifyColor()));
		prereqFailColor.setForeground(new Color(SettingsHandler
			.getPrereqFailColor()));
		featAutoColor.setForeground(new Color(SettingsHandler
			.getFeatAutoColor()));
		featVirtualColor.setForeground(new Color(SettingsHandler
			.getFeatVirtualColor()));

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
		showToolbar.setSelected(SettingsHandler.isToolBarShown());
		toolTipTextShown.setSelected(SettingsHandler.isToolTipTextShown());
		useOutputNamesEquipment.setSelected(SettingsHandler
			.guiUsesOutputNameEquipment());
		useOutputNamesSpells.setSelected(SettingsHandler
			.guiUsesOutputNameSpells());
		waitCursor.setSelected(SettingsHandler.getUseWaitCursor());

		// Look and feel
		aaText.setSelected(SettingsHandler.isAaText());

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
		hpDialogShownAtLevelUp.setSelected(SettingsHandler
			.getShowHPDialogAtLevelUp());
		//featDialogShownAtLevelUp.setSelected(SettingsHandler.getShowFeatDialogAtLevelUp());
		statDialogShownAtLevelUp.setSelected(SettingsHandler
			.getShowStatDialogAtLevelUp());
		showWarningAtFirstLevelUp.setSelected(SettingsHandler
			.isShowWarningAtFirstLevelUp());
		enforceSpendingBeforeLevelUp.setSelected(SettingsHandler
			.getEnforceSpendingBeforeLevelUp());

		// Equipment
		allowMetamagicInEqBuilder.setSelected(SettingsHandler
			.isMetamagicAllowedInEqBuilder());
		potionMaxLevel.setSelectedIndex(SettingsHandler
			.getMaxPotionSpellLevel()
			- SPELLLVLMIN);
		wandMaxLevel.setSelectedIndex(SettingsHandler.getMaxWandSpellLevel()
			- SPELLLVLMIN);

		if (SettingsHandler.wantToLoadMasterworkAndMagic())
		{
			noAutoEquipCreate.setSelected(true);
		}
		else
		{
			autoEquipCreate.setSelected(true);
		}

		SettingsHandler.setWantToLoadMasterworkAndMagic(false); // Turn off temporarily so we get current setting
		autoMethod1.setSelected(SettingsHandler
			.getAutogen(Constants.AUTOGEN_RACIAL));
		autoMethod2.setSelected(SettingsHandler
			.getAutogen(Constants.AUTOGEN_MASTERWORK));
		autoMethod3.setSelected(SettingsHandler
			.getAutogen(Constants.AUTOGEN_MAGIC));
		autoMethod4.setSelected(SettingsHandler
			.getAutogen(Constants.AUTOGEN_EXOTICMATERIAL));
		SettingsHandler.setWantToLoadMasterworkAndMagic(noAutoEquipCreate
			.isSelected()); // Reset its state now we are done

		// Language
		langEng.setSelected(false);
		langFre.setSelected(false);
		langGer.setSelected(false);
		langIt.setSelected(false);
		langEs.setSelected(false);
		langPt.setSelected(false);
		langSystem.setSelected(false);

		String language = Globals.getLanguage();
		if (language == null || language.equals(""))
		{
			langSystem.setSelected(true);
		}
		else if (Globals.getLanguage().equals("en"))
		{
			langEng.setSelected(true);
		}
		else if (Globals.getLanguage().equals("fr"))
		{
			langFre.setSelected(true);
		}
		else if (Globals.getLanguage().equals("de"))
		{
			langGer.setSelected(true);
		}
		else if (Globals.getLanguage().equals("it"))
		{
			langIt.setSelected(true);
		}
		else if (Globals.getLanguage().equals("es"))
		{
			langEs.setSelected(true);
		}
		else if (Globals.getLanguage().equals("pt"))
		{
			langPt.setSelected(true);
		}
		else
		{
			// Default to English
			langSystem.setSelected(true);
		}

		unitSetType.setSelectedIndex(0);
		for (int i = 0; i < SystemCollections.getUnitSetList().size(); ++i)
		{
			if (unitSetNames[i].equals(SettingsHandler.getGameModeUnitSet()
				.getName()))
			{
				unitSetType.setSelectedIndex(i);
			}
		}

		// Locations
		pcgenCreateBackupCharacter.setSelected(SettingsHandler
			.getCreatePcgBackup());

		// Output
		paperType.setSelectedIndex(Globals.getSelectedPaper());
		weaponProfPrintout.setSelected(SettingsHandler.getWeaponProfPrintout());
		saveOutputSheetWithPC.setSelected(SettingsHandler
			.getSaveOutputSheetWithPC());
		printSpellsWithPC.setSelected(SettingsHandler.getPrintSpellsWithPC());

		// Sources
		campLoad.setSelected(SettingsHandler.isLoadCampaignsAtStart());
		charCampLoad.setSelected(SettingsHandler.isLoadCampaignsWithPC());
		allowOptsInSource.setSelected(SettingsHandler
			.isOptionAllowedInSources());
		saveCustom.setSelected(SettingsHandler.getSaveCustomEquipment());
		showOGL.setSelected(SettingsHandler.showLicense());
		showMature.setSelected(SettingsHandler.showMature());
		showd20.setSelected(SettingsHandler.showD20Info());
		showSponsors.setSelected(SettingsHandler.showSponsors());
		loadURL.setSelected(SettingsHandler.isLoadURLs());
		allowOverride.setSelected(SettingsHandler.isAllowOverride());

		switch (Globals.getSourceDisplay())
		{
			case LONG:
				sourceOptions.setSelectedIndex(0);

				break;

			case MEDIUM:
				sourceOptions.setSelectedIndex(1);

				break;

			case SHORT:
				sourceOptions.setSelectedIndex(2);

				break;

			case PAGE:
				sourceOptions.setSelectedIndex(3);

				break;

			case WEB:
				sourceOptions.setSelectedIndex(4);

				break;

			default:
				Logging
					.errorPrint("In PreferencesDialog.applyOptionValuesToControls (source display) the option "
						+ Globals.getSourceDisplay() + " is unsupported.");

				break;
		}
	}

	private JPanel buildAbilitiesPanel()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
		ButtonGroup exclusiveGroup;
		Border etched = null;
		TitledBorder title1 =
				BorderFactory.createTitledBorder(etched, in_abilities);
		JPanel abilityScoresPanel = new JPanel();

		title1.setTitleJustification(TitledBorder.LEFT);
		abilityScoresPanel.setBorder(title1);
		abilityScoresPanel.setLayout(gridbag);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(2, 2, 2, 2);

		final GameMode gameMode = SettingsHandler.getGame();

		int row = 0;

		exclusiveGroup = new ButtonGroup();
		Utility.buildConstraints(c, 0, row++, 3, 1, 0, 0);
		label =
				new JLabel(PropertyFactory
					.getString("in_Prefs_abilitiesGenLabel")
					+ ": (" + gameMode.getName() + ")");
		gridbag.setConstraints(label, c);
		abilityScoresPanel.add(label);
		Utility.buildConstraints(c, 0, row, 1, 1, 0, 0);
		label = new JLabel("  ");
		gridbag.setConstraints(label, c);
		abilityScoresPanel.add(label);

		Utility.buildConstraints(c, 1, row++, 2, 1, 0, 0);
		abilitiesUserRolledButton =
				new JRadioButton(PropertyFactory
					.getString("in_Prefs_abilitiesUserRolled"));
		gridbag.setConstraints(abilitiesUserRolledButton, c);
		abilityScoresPanel.add(abilitiesUserRolledButton);
		exclusiveGroup.add(abilitiesUserRolledButton);

		Utility.buildConstraints(c, 1, row++, 2, 1, 0, 0);
		abilitiesAllSameButton =
				new JRadioButton(PropertyFactory
					.getString("in_Prefs_abilitiesAllSame")
					+ ": ");
		gridbag.setConstraints(abilitiesAllSameButton, c);
		abilityScoresPanel.add(abilitiesAllSameButton);
		exclusiveGroup.add(abilitiesAllSameButton);
		Utility.buildConstraints(c, 1, row, 1, 1, 0, 0);
		label = new JLabel("  ");
		gridbag.setConstraints(label, c);
		abilityScoresPanel.add(label);
		Utility.buildConstraints(c, 2, row++, 2, 1, 0, 0);

		abilityScoreCombo = new JComboBoxEx();
		for (int i = gameMode.getStatMin(); i <= gameMode.getStatMax(); ++i)
		{
			abilityScoreCombo.addItem(String.valueOf(i));
		}

		gridbag.setConstraints(abilityScoreCombo, c);
		abilityScoresPanel.add(abilityScoreCombo);

		GameModeRollMethod rm = gameMode.getRollingMethod(0);
		if (rm != null)
		{
			Utility.buildConstraints(c, 1, row++, 2, 1, 0, 0);
			abilitiesRolledButton = new JRadioButton("Rolled:");
			gridbag.setConstraints(abilitiesRolledButton, c);
			abilityScoresPanel.add(abilitiesRolledButton);
			exclusiveGroup.add(abilitiesRolledButton);
			Utility.buildConstraints(c, 2, row++, 2, 1, 0, 0);

			abilityRolledModeCombo = new JComboBoxEx();

			int gmi = 0;
			while (rm != null)
			{
				abilityRolledModeCombo.addItem(rm.getMethodName());
				rm = gameMode.getRollingMethod(++gmi);
			}

			gridbag.setConstraints(abilityRolledModeCombo, c);
			abilityScoresPanel.add(abilityRolledModeCombo);
		}

		final int purchaseMethodCount = gameMode.getPurchaseMethodCount();
		Utility.buildConstraints(c, 1, row++, 2, 1, 0, 0);
		abilitiesPurchasedButton =
				new JRadioButton(PropertyFactory
					.getString("in_Prefs_abilitiesPurchased")
					+ ": ");
		gridbag.setConstraints(abilitiesPurchasedButton, c);
		abilityScoresPanel.add(abilitiesPurchasedButton);
		exclusiveGroup.add(abilitiesPurchasedButton);
		Utility.buildConstraints(c, 2, row++, 2, 1, 0, 0);

		pMode = new String[purchaseMethodCount];
		pModeMethodName = new String[purchaseMethodCount];

		for (int i = 0; i < purchaseMethodCount; ++i)
		{
			final PointBuyMethod pbm = gameMode.getPurchaseMethod(i);
			pMode[i] = pbm.getDescription();
			pModeMethodName[i] = pbm.getMethodName();
		}

		abilityPurchaseModeCombo = new JComboBoxEx(pMode);

		gridbag.setConstraints(abilityPurchaseModeCombo, c);
		abilityScoresPanel.add(abilityPurchaseModeCombo);

		//
		// Hide controls if there are no entries to select
		//
		if (purchaseMethodCount == 0)
		{
			abilityPurchaseModeCombo.setVisible(false);
			abilitiesPurchasedButton.setVisible(false);
		}

		Utility.buildConstraints(c, 1, row++, 1, 1, 0, 0);
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		abilityScoresPanel.add(label);
		Utility.buildConstraints(c, 1, row++, 3, 1, 0, 0);
		purchaseModeButton =
				new JButton(PropertyFactory
					.getString("in_Prefs_purchaseModeConfig"));
		gridbag.setConstraints(purchaseModeButton, c);
		abilityScoresPanel.add(purchaseModeButton);
		purchaseModeButton.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(c, 5, 20, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		abilityScoresPanel.add(label);

		return abilityScoresPanel;
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

		int col = 0;

		// NB - not alphabetized!
		col =
				addColorsOption(col, c, gridbag, colorsPanel,
					prereqQualifyColor = new JButton(in_colorPrereqQualify));
		col =
				addColorsOption(col, c, gridbag, colorsPanel, prereqFailColor =
						new JButton(in_colorPrereqFail));
		col =
				addColorsOption(col, c, gridbag, colorsPanel, featAutoColor =
						new JButton(in_colorAutoFeat));
		col =
				addColorsOption(col, c, gridbag, colorsPanel, featVirtualColor =
						new JButton(in_colorVirtFeat));

		Utility.buildConstraints(c, 5, 20, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		colorsPanel.add(label);

		return colorsPanel;
	}

	private int addColorsOption(int col, final GridBagConstraints c,
		final GridBagLayout gridbag, final JPanel colorsPanel,
		final JButton button)
	{
		Utility.buildConstraints(c, 0, col++, 1, 1, 0, 0);
		gridbag.setConstraints(button, c);
		colorsPanel.add(button);
		button.addActionListener(prefsButtonHandler);

		return col;
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
				new JLabel(PropertyFactory.getString("in_Prefs_hpGenLabel")
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

		Utility.buildConstraints(c, 0, iRow, 2, 1, 0, 0);
		label =
				new JLabel(PropertyFactory.getString("in_Prefs_hpMaxAtFirst")
					+ ": ");
		gridbag.setConstraints(label, c);
		hitPointsPanel.add(label);
		Utility.buildConstraints(c, 2, iRow, 1, 1, 0, 0);
		gridbag.setConstraints(maxHpAtFirstLevel, c);
		hitPointsPanel.add(maxHpAtFirstLevel);

		Utility.buildConstraints(c, 5, 20, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		hitPointsPanel.add(label);

		return hitPointsPanel;
	}

	private JPanel buildHouseRulesPanel()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
		Border etched = null;
		TitledBorder title1 =
				BorderFactory.createTitledBorder(etched, in_houseRules);
		JPanel houseRulesPanel = new JPanel();

		title1.setTitleJustification(TitledBorder.LEFT);
		houseRulesPanel.setBorder(title1);
		gridbag = new GridBagLayout();
		houseRulesPanel.setLayout(gridbag);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(2, 2, 2, 2);

		Utility.buildConstraints(c, 0, 0, 2, 1, 0, 0);
		label =
				new JLabel(PropertyFactory
					.getString("in_Prefs_hrCrossSkillCost")
					+ ": ");
		gridbag.setConstraints(label, c);
		houseRulesPanel.add(label);
		//		Utility.buildConstraints(c, 2, 0, 1, 1, 0, 0);
		//		gridbag.setConstraints(crossClassSkillCostCombo, c);
		//		houseRulesPanel.add(crossClassSkillCostCombo);

		// build a list of checkboxes from the current gameMode Rules
		int gridNum = 1;
		GameMode gameMode = SettingsHandler.getGame();
		ruleCheckList = gameMode.getRuleCheckList();

		// initialize all the checkboxes
		hrBoxes = new JCheckBox[ruleCheckList.size()];

		int excludeCount = 0;
		int boxNum = 0;

		for (RuleCheck aRule : ruleCheckList)
		{
			aRule.getName();
			String aKey = aRule.getKey();
			String aDesc = aRule.getDesc();
			boolean aBool = aRule.getDefault();

			if (aRule.isExclude())
			{
				++excludeCount;

				continue;
			}

			if (SettingsHandler.hasRuleCheck(aKey))
			{
				aBool = SettingsHandler.getRuleCheck(aKey);
			}

			hrBoxes[boxNum] = new JCheckBox(aKey, aBool);

			Utility.buildConstraints(c, 0, gridNum, 2, 1, 0, 0);
			label = new JLabel(aDesc);
			gridbag.setConstraints(label, c);
			houseRulesPanel.add(label);
			Utility.buildConstraints(c, 2, gridNum, 1, 1, 0, 0);
			gridbag.setConstraints(hrBoxes[boxNum], c);
			houseRulesPanel.add(hrBoxes[boxNum]);
			++boxNum;
			++gridNum;
		}

		hrRadio = new JRadioButton[excludeCount];

		int exNum = 0;

		for (RuleCheck aRule : ruleCheckList)
		{
			aRule.getName();
			String aKey = aRule.getKey();
			aRule.getDesc();
			boolean aBool = aRule.getDefault();

			if (!aRule.isExclude())
			{
				continue;
			}

			hrRadio[exNum] = new JRadioButton(aKey);

			if (SettingsHandler.hasRuleCheck(aKey))
			{
				aBool = SettingsHandler.getRuleCheck(aKey);
			}

			hrRadio[exNum].setSelected(aBool);
			++exNum;
		}

		hrGroup = new ButtonGroup[excludeCount];

		int groupNum = 0;

		List<String> doneList = new ArrayList<String>();

		for (int i = 0; i < hrRadio.length; i++)
		{
			if (hrRadio[i] == null)
			{
				continue;
			}

			String aKey = hrRadio[i].getText();
			RuleCheck aRule = gameMode.getRuleByKey(aKey);

			if (aRule == null)
			{
				continue;
			}

			String aDesc = aRule.getDesc();
			String altKey = aRule.getExcludeKey();

			if (doneList.contains(aKey) || doneList.contains(altKey))
			{
				continue;
			}

			hrGroup[groupNum] = new ButtonGroup();
			hrGroup[groupNum].add(hrRadio[i]);
			doneList.add(aKey);

			Utility.buildConstraints(c, 0, gridNum, 3, 1, 0, 0);

			JPanel subPanel = new JPanel();
			gridbag.setConstraints(subPanel, c);

			subPanel.setLayout(gridbag);

			GridBagConstraints cc = new GridBagConstraints();
			cc.fill = GridBagConstraints.HORIZONTAL;
			cc.insets = new Insets(0, 4, 0, 0);

			Border aBord = BorderFactory.createEtchedBorder();
			subPanel.setBorder(aBord);

			label = new JLabel(aDesc);
			cc.anchor = GridBagConstraints.WEST;
			Utility.buildConstraints(cc, 0, 0, 2, 1, 2, 0);
			gridbag.setConstraints(label, cc);
			subPanel.add(label);
			cc.anchor = GridBagConstraints.EAST;
			Utility.buildConstraints(cc, 2, 0, 1, 1, 1, 0);
			gridbag.setConstraints(hrRadio[i], cc);
			subPanel.add(hrRadio[i]);

			for (int ii = 0; ii < hrRadio.length; ii++)
			{
				if (hrRadio[i] == null)
				{
					continue;
				}

				String exKey = hrRadio[ii].getText();

				if (exKey.equals(altKey))
				{
					aRule = gameMode.getRuleByKey(exKey);
					aDesc = aRule.getDesc();
					hrGroup[groupNum].add(hrRadio[ii]);
					doneList.add(altKey);

					label = new JLabel(aDesc);
					cc.anchor = GridBagConstraints.WEST;
					Utility.buildConstraints(cc, 0, 1, 2, 1, 2, 0);
					gridbag.setConstraints(label, cc);
					subPanel.add(label);
					cc.anchor = GridBagConstraints.EAST;
					Utility.buildConstraints(cc, 2, 1, 1, 1, 1, 0);
					gridbag.setConstraints(hrRadio[ii], cc);
					subPanel.add(hrRadio[ii]);
				}
			}

			houseRulesPanel.add(subPanel);
			++gridNum;
			++groupNum;
		}

		Utility.buildConstraints(c, 5, 20, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		houseRulesPanel.add(label);

		return houseRulesPanel;
	}

	private JPanel buildLanguagePanel()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
		ButtonGroup exclusiveGroup;
		Border etched = null;
		TitledBorder title1 =
				BorderFactory.createTitledBorder(etched, in_language);
		JPanel langPanel = new JPanel();

		title1.setTitleJustification(TitledBorder.LEFT);
		langPanel.setBorder(title1);
		langPanel.setLayout(gridbag);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(2, 2, 2, 2);
		exclusiveGroup = new ButtonGroup();

		int line = 0;

		// Use OS system language
		line =
				addLanguageOption(line, c, gridbag, langPanel, langSystem =
						new JRadioButton(in_langSystem), exclusiveGroup);

		final SortedSet<JRadioButton> sorted =
				new TreeSet<JRadioButton>(new Comparator<JRadioButton>()
				{
					public int compare(final JRadioButton o1,
						final JRadioButton o2)
					{
						return o1.getText().compareToIgnoreCase(o2.getText());
					}
				});

		sorted.add(langEng = new JRadioButton(in_langEnglish));
		sorted.add(langFre = new JRadioButton(in_langFrench));
		sorted.add(langGer = new JRadioButton(in_langGerman));
		sorted.add(langIt = new JRadioButton(in_langItalian));
		sorted.add(langEs = new JRadioButton(in_langSpanish));
		sorted.add(langPt = new JRadioButton(in_langPortuguese));

		for (JRadioButton b : sorted)
		{
			line =
					addLanguageOption(line, c, gridbag, langPanel, b,
						exclusiveGroup);
		}

		Utility.buildConstraints(c, 0, line++, 1, 1, 0, 0);
		label = new JLabel(in_unitSetType + ": ");
		gridbag.setConstraints(label, c);
		langPanel.add(label);

		Utility.buildConstraints(c, 1, line++, 2, 1, 0, 0);
		Map<String, UnitSet> unitSetList = SystemCollections.getUnitSetList();
		unitSetNames = new String[unitSetList.size()];
		int i = 0;
		for (UnitSet unitSet : unitSetList.values())
		{
			if (unitSet != null)
			{
				unitSetNames[i++] = unitSet.getName();
			}
		}

		unitSetType = new JComboBoxEx(unitSetNames);
		gridbag.setConstraints(unitSetType, c);
		langPanel.add(unitSetType);

		Utility.buildConstraints(c, 5, line, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		langPanel.add(label);

		return langPanel;
	}

	private static int addLanguageOption(int line,
		final GridBagConstraints constraints, final GridBagLayout gridbag,
		final JPanel panel, final JRadioButton button, final ButtonGroup group)
	{
		Utility.buildConstraints(constraints, 0, line++, 2, 1, 0, 0);
		gridbag.setConstraints(button, constraints);
		panel.add(button);
		group.add(button);

		return line;
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
				new JLabel(PropertyFactory
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
				new JLabel(PropertyFactory.getString("in_Prefs_pcgenDataDir")
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
				new JLabel(PropertyFactory.getString("in_Prefs_pcgenCustomDir")
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
				new JLabel(PropertyFactory
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
				new JLabel(PropertyFactory.getString("in_Prefs_pcgenDocsDir")
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
				new JLabel(PropertyFactory.getString("in_Prefs_pcgenSystemDir")
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
				new JLabel(PropertyFactory
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

		// Character File Backup directory
		Utility.buildConstraints(c, 0, 10, 1, 1, 0, 0);
		label =
				new JLabel(PropertyFactory
					.getString("in_Prefs_pcgenCreateBackupCharacter")
					+ ": ");
		gridbag.setConstraints(label, c);
		locationPanel.add(label);
		Utility.buildConstraints(c, 1, 10, 1, 1, 0, 0);
		gridbag.setConstraints(pcgenCreateBackupCharacter, c);
		locationPanel.add(pcgenCreateBackupCharacter);

		Utility.buildConstraints(c, 0, 11, 1, 1, 0, 0);
		label =
				new JLabel(PropertyFactory
					.getString("in_Prefs_pcgenBackupCharacterDir")
					+ ": ");
		gridbag.setConstraints(label, c);
		locationPanel.add(label);
		Utility.buildConstraints(c, 1, 11, 1, 1, 0, 0);
		pcgenBackupCharacterDir =
				new JTextField(String.valueOf(SettingsHandler
					.getBackupPcgPath()));
		pcgenBackupCharacterDir.addFocusListener(textFieldListener);
		gridbag.setConstraints(pcgenBackupCharacterDir, c);
		locationPanel.add(pcgenBackupCharacterDir);
		Utility.buildConstraints(c, 2, 11, 1, 1, 0, 0);
		pcgenBackupCharacterDirButton = new JButton(in_choose);
		gridbag.setConstraints(pcgenBackupCharacterDirButton, c);
		locationPanel.add(pcgenBackupCharacterDirButton);
		pcgenBackupCharacterDirButton.addActionListener(prefsButtonHandler);

		// Where to store options.ini file
		Utility.buildConstraints(c, 0, 12, 1, 1, 0, 0);
		label =
				new JLabel(PropertyFactory.getString("in_Prefs_pcgenFilesDir")
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

		Utility.buildConstraints(c, 0, 13, 1, 1, 0, 0);
		gridbag.setConstraints(pcgenFilesDirRadio, c);
		locationPanel.add(pcgenFilesDirRadio);
		Utility.buildConstraints(c, 1, 13, 1, 1, 0, 0);
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

		Utility.buildConstraints(c, 0, 14, 1, 1, 0, 0);
		gridbag.setConstraints(selectFilesDirRadio, c);
		locationPanel.add(selectFilesDirRadio);
		Utility.buildConstraints(c, 1, 14, 1, 1, 0, 0);
		gridbag.setConstraints(pcgenFilesDir, c);
		locationPanel.add(pcgenFilesDir);
		Utility.buildConstraints(c, 2, 14, 1, 1, 0, 0);
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
		Utility.setDescription(skinnedLookFeel, PropertyFactory
			.getString("in_Prefs_skinnedLAFTooltip"));
		skinnedLookFeel.setMnemonic(PropertyFactory
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
		Utility.setDescription(themepack, PropertyFactory
			.getString("in_Prefs_chooseSkinTooltip"));
		gridbag.setConstraints(themepack, c);
		lafPanel.add(themepack);
		themepack.addActionListener(prefsButtonHandler);

		aaText.setText(in_aaText);
		//		aaText.setSelected(SettingsHandler.isAaText());
		Utility.setDescription(aaText, PropertyFactory
			.getString("in_Prefs_aaTextTooltip"));
		aaText.setMnemonic(PropertyFactory.getMnemonic("in_mn_Prefs_aaText"));
		lafPanel.add(aaText);

		Utility.buildConstraints(c, 0, 20, 5, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		lafPanel.add(label);

		return lafPanel;
	}

	private JPanel buildMonstersPanel()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
		Border etched = null;
		TitledBorder title1 =
				BorderFactory.createTitledBorder(etched, in_monsters);
		JPanel monstersPanel = new JPanel();

		title1.setTitleJustification(TitledBorder.LEFT);
		monstersPanel.setBorder(title1);
		gridbag = new GridBagLayout();
		monstersPanel.setLayout(gridbag);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(2, 2, 2, 2);

		Utility.buildConstraints(c, 0, 0, 2, 1, 0, 0);
		label =
				new JLabel(PropertyFactory
					.getString("in_Prefs_defaultMonsters")
					+ ": ");
		gridbag.setConstraints(label, c);
		monstersPanel.add(label);
		Utility.buildConstraints(c, 2, 0, 1, 1, 0, 0);
		gridbag.setConstraints(useMonsterDefault, c);
		monstersPanel.add(useMonsterDefault);

		Utility.buildConstraints(c, 0, 1, 2, 1, 0, 0);
		label =
				new JLabel(PropertyFactory
					.getString("in_Prefs_hideMonsterClasses")
					+ ": ");
		gridbag.setConstraints(label, c);
		monstersPanel.add(label);
		Utility.buildConstraints(c, 2, 1, 1, 1, 0, 0);
		gridbag.setConstraints(hideMonsterClasses, c);
		monstersPanel.add(hideMonsterClasses);

		Utility.buildConstraints(c, 0, 2, 2, 1, 0, 0);
		label =
				new JLabel(PropertyFactory
					.getString("in_Prefs_ignoreMonsterHDCap")
					+ ": ");
		gridbag.setConstraints(label, c);
		monstersPanel.add(label);
		Utility.buildConstraints(c, 2, 2, 1, 1, 0, 0);
		gridbag.setConstraints(ignoreMonsterHDCap, c);
		monstersPanel.add(ignoreMonsterHDCap);

		Utility.buildConstraints(c, 5, 20, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		monstersPanel.add(label);

		return monstersPanel;
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
				new JLabel(PropertyFactory
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
				new JLabel(PropertyFactory
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
				new JLabel(PropertyFactory
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
				new JLabel(PropertyFactory
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
			paperNames[i] = Globals.getPaperInfo(i, Constants.PAPERINFO_NAME);
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

		// Build the settings panel
		settingsPanel = new JPanel();
		settingsPanel.setLayout(new CardLayout());

		// Build the selection tree
		characterNode = new DefaultMutableTreeNode(in_character);
		settingsPanel.add(buildEmptyPanel("", PropertyFactory
			.getString("in_Prefs_charTip")), in_character);

		characterNode.add(new DefaultMutableTreeNode(in_abilities));
		settingsPanel.add(buildAbilitiesPanel(), in_abilities);
		characterNode.add(new DefaultMutableTreeNode(in_hp));
		settingsPanel.add(buildHitPointsPanel(), in_hp);
		characterNode.add(new DefaultMutableTreeNode(in_houseRules));
		settingsPanel.add(buildHouseRulesPanel(), in_houseRules);
		characterNode.add(new DefaultMutableTreeNode(in_monsters));
		settingsPanel.add(buildMonstersPanel(), in_monsters);
		rootNode.add(characterNode);

		appearanceNode = new DefaultMutableTreeNode(in_appearance);
		settingsPanel.add(buildEmptyPanel("", PropertyFactory
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
		settingsPanel.add(buildEmptyPanel("", PropertyFactory
			.getString("in_Prefs_pcgenTip")), in_pcgen);

		pcGenNode.add(new DefaultMutableTreeNode(in_equipment));
		settingsPanel.add(buildEquipmentPanel(), in_equipment);
		pcGenNode.add(new DefaultMutableTreeNode(in_language));
		settingsPanel.add(buildLanguagePanel(), in_language);
		pcGenNode.add(new DefaultMutableTreeNode(in_location));
		settingsPanel.add(buildLocationPanel(), in_location);
		pcGenNode.add(new DefaultMutableTreeNode(in_output));
		settingsPanel.add(buildOutputPanel(), in_output);
		pcGenNode.add(new DefaultMutableTreeNode(in_sources));
		settingsPanel.add(buildSourcesPanel(), in_sources);
		rootNode.add(pcGenNode);

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

		// Build the split pane
		splitPane =
				new FlippingSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					settingsScroll, settingsPanel);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerSize(10);

		// Build the control panel (OK/Cancel buttons)
		controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		JButton okButton = new JButton(PropertyFactory.getString("in_ok"));
		okButton.setMnemonic(PropertyFactory.getMnemonic("in_mn_ok"));
		controlPanel.add(okButton);
		okButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				okButtonActionPerformed();
			}
		});

		JButton cancelButton =
				new JButton(PropertyFactory.getString("in_cancel"));
		cancelButton.setMnemonic(PropertyFactory.getMnemonic("in_mn_cancel"));
		controlPanel.add(cancelButton);
		cancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				cancelButtonActionPerformed();
			}
		});
	}

	private JPanel buildSourcesPanel()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
		Border etched = null;
		TitledBorder title1 =
				BorderFactory.createTitledBorder(etched, in_sources);
		JPanel sourcesPanel = new JPanel();

		title1.setTitleJustification(TitledBorder.LEFT);
		sourcesPanel.setBorder(title1);
		sourcesPanel.setLayout(gridbag);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(2, 2, 2, 2);

		Utility.buildConstraints(c, 0, 0, 3, 1, 0, 0);
		label = new JLabel(in_autoLoadAtStart + ": ");
		gridbag.setConstraints(label, c);
		sourcesPanel.add(label);
		Utility.buildConstraints(c, 3, 0, 1, 1, 0, 0);
		gridbag.setConstraints(campLoad, c);
		sourcesPanel.add(campLoad);

		Utility.buildConstraints(c, 0, 1, 3, 1, 0, 0);
		label = new JLabel(in_autoLoadWithPC + ": ");
		gridbag.setConstraints(label, c);
		sourcesPanel.add(label);
		Utility.buildConstraints(c, 3, 1, 1, 1, 0, 0);
		gridbag.setConstraints(charCampLoad, c);
		sourcesPanel.add(charCampLoad);

		Utility.buildConstraints(c, 0, 2, 3, 1, 0, 0);
		label =
				new JLabel(PropertyFactory
					.getString("in_Prefs_allowOptionInSource")
					+ ": ");
		gridbag.setConstraints(label, c);
		sourcesPanel.add(label);
		Utility.buildConstraints(c, 3, 2, 1, 1, 0, 0);
		gridbag.setConstraints(allowOptsInSource, c);
		sourcesPanel.add(allowOptsInSource);

		Utility.buildConstraints(c, 0, 3, 3, 1, 0, 0);
		label = new JLabel(in_saveCustom + ": ");
		gridbag.setConstraints(label, c);
		sourcesPanel.add(label);
		Utility.buildConstraints(c, 3, 3, 1, 1, 0, 0);
		gridbag.setConstraints(saveCustom, c);
		sourcesPanel.add(saveCustom);

		Utility.buildConstraints(c, 0, 4, 3, 1, 0, 0);
		label = new JLabel(in_displayOGL + ": ");
		gridbag.setConstraints(label, c);
		sourcesPanel.add(label);
		Utility.buildConstraints(c, 3, 4, 1, 1, 0, 0);
		gridbag.setConstraints(showOGL, c);
		sourcesPanel.add(showOGL);

		Utility.buildConstraints(c, 0, 5, 3, 1, 0, 0);
		label = new JLabel(in_displayd20 + ": ");
		gridbag.setConstraints(label, c);
		sourcesPanel.add(label);
		Utility.buildConstraints(c, 3, 5, 1, 1, 0, 0);
		gridbag.setConstraints(showd20, c);
		sourcesPanel.add(showd20);

		Utility.buildConstraints(c, 0, 6, 3, 1, 0, 0);
		label = new JLabel(in_displaySponsors + ": ");
		gridbag.setConstraints(label, c);
		sourcesPanel.add(label);
		Utility.buildConstraints(c, 3, 6, 1, 1, 0, 0);
		gridbag.setConstraints(showSponsors, c);
		sourcesPanel.add(showSponsors);

		Utility.buildConstraints(c, 0, 7, 3, 1, 0, 0);
		label = new JLabel(in_displayMature + ": ");
		gridbag.setConstraints(label, c);
		sourcesPanel.add(label);
		Utility.buildConstraints(c, 3, 7, 1, 1, 0, 0);
		gridbag.setConstraints(showMature, c);
		sourcesPanel.add(showMature);

		Utility.buildConstraints(c, 0, 8, 3, 1, 0, 0);
		label = new JLabel(in_sourceDisplay + ": ");
		gridbag.setConstraints(label, c);
		sourcesPanel.add(label);
		Utility.buildConstraints(c, 3, 8, 1, 1, 0, 0);
		sourceOptions =
				new JComboBoxEx(new String[]{in_sdLong, in_sdMedium,
					in_sdShort, in_sdPage, in_sdWeb});
		gridbag.setConstraints(sourceOptions, c);
		sourcesPanel.add(sourceOptions);

		Utility.buildConstraints(c, 0, 9, 3, 1, 0, 0);
		label = new JLabel(in_loadURLs + ": ");
		gridbag.setConstraints(label, c);
		sourcesPanel.add(label);
		Utility.buildConstraints(c, 3, 9, 1, 1, 0, 0);
		gridbag.setConstraints(loadURL, c);
		sourcesPanel.add(loadURL);
		loadURL.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				if (((JCheckBox) evt.getSource()).isSelected())
				{
					ShowMessageDelegate.showMessageDialog(PropertyFactory
						.getString("in_Prefs_urlBlocked"), Constants.s_APPNAME,
						MessageType.WARNING);
				}
			}
		});

		Utility.buildConstraints(c, 0, 10, 3, 1, 0, 0);
		label = new JLabel(in_allowOverride + ": ");
		gridbag.setConstraints(label, c);
		sourcesPanel.add(label);
		Utility.buildConstraints(c, 3, 10, 1, 1, 0, 0);
		gridbag.setConstraints(allowOverride, c);
		sourcesPanel.add(allowOverride);

		Utility.buildConstraints(c, 5, 20, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		sourcesPanel.add(label);

		return sourcesPanel;
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
		fc.setDialogTitle(PropertyFactory
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
				ShowMessageDelegate.showMessageDialog(PropertyFactory
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
						ShowMessageDelegate.showMessageDialog(PropertyFactory
							.getString("in_Prefs_skinSetError")
							+ e.toString(), in_pcgen, MessageType.ERROR);
					}
				}
			}
		}
	}

	private void showPurchaseModeConfiguration()
	{
		//Create and display purchasemodestats popup frame.
		if (pmsFrame == null)
		{
			pmsFrame = new PurchaseModeFrame(this);

			// add a listener to know when the window has closed
			pmsFrame.addWindowListener(new WindowAdapter()
			{
				public void windowClosed(WindowEvent e)
				{
					final int purchaseMethodCount =
							SettingsHandler.getGame().getPurchaseMethodCount();
					pMode = new String[purchaseMethodCount];
					pModeMethodName = new String[purchaseMethodCount];

					final String methodName =
							SettingsHandler.getGame()
								.getPurchaseModeMethodName();
					abilityPurchaseModeCombo.removeAllItems();

					for (int i = 0; i < purchaseMethodCount; ++i)
					{
						final PointBuyMethod pbm =
								SettingsHandler.getGame().getPurchaseMethod(i);
						pMode[i] = pbm.getDescription();
						pModeMethodName[i] = pbm.getMethodName();
						abilityPurchaseModeCombo.addItem(pMode[i]);

						if (pModeMethodName[i].equals(methodName))
						{
							abilityPurchaseModeCombo.setSelectedIndex(i);
						}
					}

					// free resources
					pmsFrame = null;

					//
					// If user has added at least one method, then make the controls visible. Otherwise
					// it is not a valid choice and cannot be selected, so hide it.
					//
					abilityPurchaseModeCombo
						.setVisible(purchaseMethodCount != 0);
					abilitiesPurchasedButton
						.setVisible(purchaseMethodCount != 0);

					//
					// If no longer visible, but was selected, then use 'user rolled' instead
					//
					if (!abilitiesPurchasedButton.isVisible()
						&& abilitiesPurchasedButton.isSelected())
					{
						abilitiesUserRolledButton.setSelected(true);
					}

				}
			});
		}

		Utility.centerDialog(pmsFrame);

		// ensure the frame is visible (in case user selects menu item again).
		pmsFrame.setVisible(true);
	}

	private void addAbilitiesPanelListeners()
	{
		abilityScoreCombo.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				abilitiesAllSameButton.setSelected(true);
			}
		});

		abilityPurchaseModeCombo.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				abilitiesPurchasedButton.setSelected(true);
			}
		});

		if (abilityRolledModeCombo != null)
		{
			abilityRolledModeCombo.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					abilitiesRolledButton.setSelected(true);
				}
			});
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
			else if (source == purchaseModeButton)
			{
				showPurchaseModeConfiguration();
			}
			else if ((source == prereqQualifyColor)
				|| (source == prereqFailColor) || (source == featAutoColor)
				|| (source == featVirtualColor))
			{
				final Color newColor =
						JColorChooser.showDialog(Globals.getRootFrame(),
							PropertyFactory.getString("in_Prefs_colorSelect")
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
						JOptionPane.showConfirmDialog(null, PropertyFactory
							.getString("in_Prefs_clearBrowserWarn"),
							PropertyFactory
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
						PropertyFactory
							.getString("in_Prefs_pcgenCharacterDirTitle");
				final File currentPath = SettingsHandler.getPcgPath();
				final JTextField textField = pcgenCharacterDir;
				askForPath(currentPath, dialogTitle, textField);
			}
			else if (source == pcgenBackupCharacterDirButton)
			{
				final String dialogTitle =
						PropertyFactory
							.getString("in_Prefs_pcgenBackupCharacterDirTitle");
				final File currentPath = SettingsHandler.getBackupPcgPath();
				final JTextField textField = pcgenBackupCharacterDir;
				askForPath(currentPath, dialogTitle, textField);
			}
			else if (source == pcgenPortraitsDirButton)
			{
				final String dialogTitle =
						PropertyFactory
							.getString("in_Prefs_pcgenPortraitDirTitle");
				final File currentPath = SettingsHandler.getPortraitsPath();
				final JTextField textField = pcgenPortraitsDir;
				askForPath(currentPath, dialogTitle, textField);
			}
			else if (source == pcgenCustomDirButton)
			{
				final String dialogTitle =
						PropertyFactory
							.getString("in_Prefs_pcgenCustomDirTitle");
				final File currentPath = SettingsHandler.getPcgenCustomDir();
				final JTextField textField = pcgenCustomDir;
				askForPath(currentPath, dialogTitle, textField);
			}
			else if (source == pcgenVendorDataDirButton)
			{
				final String dialogTitle =
						PropertyFactory
							.getString("in_Prefs_pcgenVendorDataDirTitle");
				final File currentPath =
						SettingsHandler.getPcgenVendorDataDir();
				final JTextField textField = pcgenVendorDataDir;
				askForPath(currentPath, dialogTitle, textField);
			}
			else if (source == pcgenDataDirButton)
			{
				final String dialogTitle =
						PropertyFactory.getString("in_Prefs_pcgenDataDirTitle");
				final File currentPath = SettingsHandler.getPccFilesLocation();
				final JTextField textField = pcgenDataDir;
				askForPath(currentPath, dialogTitle, textField);
			}
			else if (source == pcgenDocsDirButton)
			{
				final String dialogTitle =
						PropertyFactory.getString("in_Prefs_pcgenDocsDirTitle");
				final File currentPath = SettingsHandler.getPcgenDocsDir();
				final JTextField textField = pcgenDocsDir;
				askForPath(currentPath, dialogTitle, textField);
			}
			else if (source == pcgenSystemDirButton)
			{
				final String dialogTitle =
						PropertyFactory
							.getString("in_Prefs_pcgenSystemDirTitle");
				final File currentPath = SettingsHandler.getPcgenSystemDir();
				final JTextField textField = pcgenSystemDir;
				askForPath(currentPath, dialogTitle, textField);
			}
			else if (source == pcgenFilesDirButton)
			{
				final String dialogTitle =
						PropertyFactory
							.getString("in_Prefs_pcgenFilesDirTitle");
				final File currentPath = SettingsHandler.getPcgenFilesDir();
				askForPath(currentPath, dialogTitle, pcgenFilesDir);
			}
			else if (source == pcgenOutputSheetDirButton)
			{
				final String dialogTitle =
						PropertyFactory
							.getString("in_Prefs_pcgenOutputSheetDirTitle");
				final File currentPath =
						SettingsHandler.getPcgenOutputSheetDir();
				final JTextField textField = pcgenOutputSheetDir;
				askForPath(currentPath, dialogTitle, textField);
			}
			else if (source == outputSheetHTMLDefaultButton)
			{
				JFileChooser fc = new JFileChooser();
				fc.setDialogTitle(PropertyFactory
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
						ShowMessageDelegate.showMessageDialog(PropertyFactory
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
				fc.setDialogTitle(PropertyFactory
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
						ShowMessageDelegate.showMessageDialog(PropertyFactory
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
				fc.setDialogTitle(PropertyFactory
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
						ShowMessageDelegate.showMessageDialog(PropertyFactory
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
				fc.setDialogTitle(PropertyFactory
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
						ShowMessageDelegate.showMessageDialog(PropertyFactory
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

			if (System.getProperty("os.name").startsWith("Mac OS"))
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
