/*
 * InfoTempMod.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * @author  Jayme Cox <jaymecox@users.sourceforge.net>
 * Created on Feb 26, 2003, 2:15 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.gui.tabs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.tree.TreePath;

import pcgen.base.formula.Formula;
import pcgen.base.lang.UnreachableError;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.BonusManager;
import pcgen.core.Equipment;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.Skill;
import pcgen.core.BonusManager.TempBonusInfo;
import pcgen.core.analysis.DescriptionFormatting;
import pcgen.core.analysis.OutputNameFormatting;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.character.CharacterSpell;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.spell.Spell;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui.CharacterInfo;
import pcgen.gui.CharacterInfoTab;
import pcgen.gui.PCGen_Frame1;
import pcgen.gui.filter.FilterAdapterPanel;
import pcgen.gui.filter.FilterConstants;
import pcgen.gui.filter.FilterFactory;
import pcgen.gui.panes.FlippingSplitPane;
import pcgen.gui.utils.AbstractTreeTableModel;
import pcgen.gui.utils.ClickHandler;
import pcgen.gui.utils.InfoLabelTextBuilder;
import pcgen.gui.utils.JLabelPane;
import pcgen.gui.utils.JTreeTable;
import pcgen.gui.utils.JTreeTableMouseAdapter;
import pcgen.gui.utils.JTreeTableSorter;
import pcgen.gui.utils.LabelTreeCellRenderer;
import pcgen.gui.utils.PObjectNode;
import pcgen.gui.utils.ResizeColumnListener;
import pcgen.gui.utils.TreeTableModel;
import pcgen.gui.utils.Utility;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserRadio;
import pcgen.util.enumeration.Tab;

/**
 * <code>InfoTempMod</code> creates a new tabbed panel that is used to
 * allow application of temporary modifiers to PC's and Equipment
 *
 * @author  Jayme Cox <jaymecox@users.sourceforge.net>
 * @version $Revision$
 **/
public class InfoTempMod extends FilterAdapterPanel implements CharacterInfoTab
{
	private static final Tab tab = Tab.TEMPBONUS;

	private static boolean needsUpdate = true;

	// table model modes
	private static final int MODEL_BONUS = 0;
	private static final int MODEL_TARGET = 1;

	//column positions for tables
	// if you change these, you also need to change
	// the selNameList array in the BonusModel class
	private static final int COL_NAME = 0;
	private static final int COL_TYPE = 1;
	private static final int COL_SRC = 2;

	//column position for temporary bonus table
	// if you change these, you need to change
	// the colNameList array in the AppliedModel class
	private static final int BONUS_COL_NAME = 0;
	private static final int BONUS_COL_TYPE = 1;
	private static final int BONUS_COL_TO = 2;
	private static final int BONUS_COL_VAL = 3;
	private AppliedModel appliedBonusModel; // applied temp bonuses
	private BonusModel bonusModel = null;
	private BonusModel targetModel = null;
	private FlippingSplitPane botHorzSplit;
	private FlippingSplitPane centerHorzSplit;
	private FlippingSplitPane topVertSplit;
	private JButton applyBonusButton;
	private JButton removeBonusButton;
	private JLabel tempModsDisabledWarning;
	/* commented out until we fix temp mods, do not delete
	 private JCheckBox useTempMods;
	 */
	private JLabelPane infoLabel = new JLabelPane();
	private JPanel botPane = new JPanel();
	private JPanel topPane = new JPanel();
	private JTreeTable appliedTable; // target+bonuses
	private JTreeTable bonusTable; // bonus
	private JTreeTable targetTable; // targets for bonus
	private JTreeTableSorter bonusSort = null;
	private JTreeTableSorter targetSort = null;
	private List<TempWrap> tbwList;
	private CDOMObject lastAvaObject = null;
	private boolean hasBeenSized = false;

	private PlayerCharacter pc;
	private int serial = 0;
	private boolean readyForRefresh = false;

	/**
	 *  Constructor for the InfoEquips object
	 * @param pc
	 **/
	public InfoTempMod(PlayerCharacter pc)
	{
		this.pc = pc;
		// do not remove this as we will use it
		// to save component specific settings
		setName(tab.label());

		initComponents();

		initActionListeners();

		FilterFactory.restoreFilterSettings(this);
	}

	public void setPc(PlayerCharacter pc)
	{
		if (this.pc != pc || pc.getSerial() > serial)
		{
			this.pc = pc;
			serial = pc.getSerial();
			forceRefresh();
			// Commented out for [ 1461012 ] EquipSet temp bonuses setting
			// as the currently selected equipset controls if temp mods
			// are applied or not.
			//pc.setUseTempMods(true);
		}
	}

	public PlayerCharacter getPc()
	{
		return pc;
	}

	public int getTabOrder()
	{
		return SettingsHandler.getPCGenOption(".Panel.TempMod.Order", tab.ordinal()); //$NON-NLS-1$
	}

	public void setTabOrder(int order)
	{
		SettingsHandler.setPCGenOption(".Panel.TempMod.Order", order); //$NON-NLS-1$
	}

	public String getTabName()
	{
		GameMode game = SettingsHandler.getGame();
		return game.getTabName(tab);
	}

	public boolean isShown()
	{
		GameMode game = SettingsHandler.getGame();
		return game.getTabShown(tab);
	}

	/**
	 * Retrieve the list of tasks to be done on the tab.
	 * @return List of task descriptions as Strings.
	 */
	public List<String> getToDos()
	{
		return new ArrayList<String>();
	}

	public void refresh()
	{
		if (pc.getSerial() > serial)
		{
			serial = pc.getSerial();
			forceRefresh();
		}
	}

	public void forceRefresh()
	{
		if (readyForRefresh)
		{
			needsUpdate = true;
			updateCharacterInfo();
		}
		else
		{
			serial = 0;
		}
	}

	public JComponent getView()
	{
		return this;
	}

	/**
	 * specifies whether the "match any" option should be allowed
	 * @return true
	 **/
	public final boolean isMatchAnyEnabled()
	{
		return true;
	}

	/**
	 * Sets the update flag for this tab
	 * It's a lazy update and will only occur
	 * on other status change
	 * @param flag
	 **/
	public static void setNeedsUpdate(boolean flag)
	{
		needsUpdate = flag;
	}

	/**
	 * specifies whether the "negate/reverse" option should be allowed
	 * @return true
	 **/
	public final boolean isNegateEnabled()
	{
		return true;
	}

	/**
	 * specifies the filter selection mode
	 * @return FilterConstants.DISABLED_MODE = -2
	 **/
	public final int getSelectionMode()
	{
		return FilterConstants.DISABLED_MODE;
	}

	/**
	 * implementation of Filterable interface
	 **/
	public final void initializeFilters()
	{
		FilterFactory.registerAllSourceFilters(this);
	}

	/**
	 * implementation of Filterable interface
	 **/
	public final void refreshFiltering()
	{
		updateBonusModel();
		updateTargetModel();
	}

	/**
	 * This recalculates the states of everything based
	 * upon the currently target character
	 * But first test to see if we need to do anything
	 **/
	public final void updateCharacterInfo()
	{
		if ((pc == null) || !needsUpdate)
		{
			return;
		}

		updateBonusModel();
		updateTargetModel();
		updateAppliedModel();

		/* commented out until we fix temp mods, do not delete
		 useTempMods.setSelected(pc.getUseTempMods());
		 */

		updateTempModDisabledWarning();

		needsUpdate = false;
	}

	/**
	 * Update the warning message displayed if temporary
	 * mods are turned off in the equipset.
	 */
	private void updateTempModDisabledWarning()
	{
		if (!pc.getUseTempMods())
		{
			tempModsDisabledWarning.setText(PropertyFactory
				.getString("InfoTempMod.warn.mods.off")); //$NON-NLS-1$
		}
		else
		{
			tempModsDisabledWarning.setText(""); //$NON-NLS-1$
		}
		tempModsDisabledWarning.updateUI();
	}

	/**
	 * allows user to choose value of bonus
	 * @param newB 
	 * @param aChoice
	 * @param repeatValue
	 * @return bonus choice
	 **/
	private BonusInfo getBonusChoice(String oldValue, final CDOMObject source,
			String repeatValue)
	{
		String value = oldValue;

		// If repeatValue is set, this is a multi BONUS and they all
		// should get the same value as the first choice
		if (repeatValue.length() > 0)
		{
			// need to parse the aChoice string
			// and replace %CHOICE with choice
			if (value.indexOf("%CHOICE") >= 0) //$NON-NLS-1$
			{
				value = value.replaceAll(
						Pattern.quote("%CHOICE"), //$NON-NLS-1$ 
						repeatValue);
			}

			return new BonusInfo(value, repeatValue);
		}

		String aChoice = source.getSafe(StringKey.CHOICE_STRING);
		StringTokenizer aTok = new StringTokenizer(aChoice, "|");

		String testNumber = aChoice;
		
		Formula numchoices = source.get(FormulaKey.NUMCHOICES);
		if (numchoices != null)
		{
			Logging.errorPrint("NUMCHOICES is not implemented "
					+ "for CHOOSE in Temporary Mods");
			Logging.errorPrint("  CHOOSE was: " + aChoice
					+ ", NUMCHOICES was: " + numchoices);
		}
		if (testNumber.startsWith("NUMBER") && (aTok.countTokens() >= 3)) //$NON-NLS-1$
		{
			int min;
			int max;
			aTok.nextToken(); // throw away "NUMBER"

			String minString = aTok.nextToken();
			String maxString = aTok.nextToken();
			String titleString = PropertyFactory.getString("in_itmPickNumber"); //$NON-NLS-1$

			if (aTok.hasMoreTokens())
			{
				titleString = aTok.nextToken();

				if (titleString.startsWith("TITLE=")) //$NON-NLS-1$
				{
					// remove TITLE=
					titleString = titleString.substring(6);
				}
			}

			if (minString.startsWith("MIN=")) //$NON-NLS-1$
			{
				minString = minString.substring(4);
				min = pc.getVariableValue(minString, "").intValue();
			}
			else
			{
				min = pc.getVariableValue(minString, "").intValue();
			}

			if (maxString.startsWith("MAX=")) //$NON-NLS-1$
			{
				maxString = maxString.substring(4);
				max = pc.getVariableValue(maxString, "").intValue();
			}
			else
			{
				max = pc.getVariableValue(maxString, "").intValue();
			}

			if ((max > 0) || (min <= max))
			{
				List<String> numberList = new ArrayList<String>();

				for (int i = min; i <= max; i++)
				{
					numberList.add(Integer.toString(i));
				}

				// let them choose the number from a radio list
				ChooserRadio c = ChooserFactory.getRadioInstance();
				c.setAvailableList(numberList);
				c.setVisible(false);
				c.setTitle(PropertyFactory.getString("in_itmPickNumber")); //$NON-NLS-1$
				c.setMessageText(titleString);
				c.setVisible(true);

				ArrayList<String> selectedList = c.getSelectedList();
				if (selectedList.size() > 0)
				{
					final String aI = selectedList.get(0);

					// need to parse the bonus.getValue()
					// string and replace %CHOICE
					if (oldValue.indexOf("%CHOICE") >= 0) //$NON-NLS-1$
					{
						value =
							oldValue.replaceAll(Pattern.quote("%CHOICE"),  //$NON-NLS-1$
								                  aI);
					}

					return new BonusInfo(value, aI);
				}
				// they hit the cancel button
				return null;
			}
		}

		return null;
	}

	/*
	 * set the bonus Info text to the currently selected bonus
	 */
	private void setInfoLabelText(Object anObj)
	{
		Equipment eqI = null;
		Spell aSpell = null;
		Ability aFeat = null;
		PCClass aClass = null;
		PCTemplate aTemp = null;
		Skill aSkill = null;

		if (anObj instanceof Equipment)
		{
			eqI = (Equipment) anObj;
		}
		else if (anObj instanceof Spell)
		{
			aSpell = (Spell) anObj;
		}
		else if (anObj instanceof Ability)
		{
			aFeat = (Ability) anObj;
		}
		else if (anObj instanceof PCClass)
		{
			aClass = (PCClass) anObj;
		}
		else if (anObj instanceof PCTemplate)
		{
			aTemp = (PCTemplate) anObj;
		}
		else if (anObj instanceof Skill)
		{
			aSkill = (Skill) anObj;
		}

		if (aClass != null)
		{
			
			InfoLabelTextBuilder b = new InfoLabelTextBuilder(aClass.getDisplayName());
			
			String bString = SourceFormat.getFormattedString(aClass,
			Globals.getSourceDisplay(), true);

			if (bString.length() > 0)
			{
				b.appendLineBreak().appendI18nElement("in_itmInfoLabelTextSource" , bString); //$NON-NLS-1$
			}

			bString = aClass.getSafe(StringKey.TEMP_DESCRIPTION);

			if (bString.length() > 0)
			{
				b.appendLineBreak().appendI18nElement("in_itmInfoLabelTextDesc", bString); //$NON-NLS-1$
			}

			infoLabel.setText(b.toString());
		}
		else if (aFeat != null)
		{
			InfoLabelTextBuilder b = new InfoLabelTextBuilder(OutputNameFormatting.piString(aFeat, false));
			
			b.appendLineBreak();
			b.appendI18nElement("in_itmInfoLabelTextType" , aFeat.getType()); //$NON-NLS-1$

			String bString = SourceFormat.getFormattedString(aFeat,
			Globals.getSourceDisplay(), true);

			if (bString.length() > 0)
			{
				b.appendLineBreak().appendI18nElement("in_itmInfoLabelTextSource" , bString); //$NON-NLS-1$
			}

			bString = aFeat.getSafe(StringKey.TEMP_DESCRIPTION);

			if (bString.length() > 0)
			{
				b.appendLineBreak().appendI18nElement("in_itmInfoLabelTextDesc", bString); //$NON-NLS-1$
			}

			infoLabel.setText(b.toString());
		}
		else if (eqI != null)
		{
			InfoLabelTextBuilder b = new InfoLabelTextBuilder(OutputNameFormatting.piString(eqI, false));
			
			if (!eqI.longName().equals(eqI.getName()))
			{
				b.append("(").append(eqI.longName()).append(")");
			}

			b.appendLineBreak();
			b.appendI18nElement("in_itmInfoLabelTextType" , eqI.getType()); //$NON-NLS-1$

			String bString =
					Globals.getGameModeUnitSet().displayWeightInUnitSet(
						eqI.getWeight(pc).doubleValue());

			if (bString.length() > 0)
			{
				b.appendSpacer();
				b.appendI18nElement("in_itmInfoLabelTextWT" , bString); //$NON-NLS-1$
			}

			Integer a = eqI.getACBonus(pc);

			if (a.intValue() > 0)
			{
				b.appendSpacer();
				b.appendI18nElement("in_itmInfoLabelTextAC" , a.toString()); //$NON-NLS-1$
			}

			if (eqI.isArmor() || eqI.isShield())
			{
				a = eqI.getMaxDex(pc);
				b.appendSpacer();
				b.appendI18nElement("in_itmInfoLabelTextMaxDex",a.toString()); //$NON-NLS-1$
				a = eqI.acCheck(pc);
				b.appendSpacer();
				b.appendI18nElement("in_itmInfoLabelTextAcCheck",a.toString()); //$NON-NLS-1$
			}

			if (Globals.getGameModeShowSpellTab())
			{
				a = eqI.spellFailure(pc);

				if (eqI.isArmor() || eqI.isShield() || (a.intValue() != 0))
				{
					b.appendSpacer();
					b.appendI18nElement("in_itmInfoLabelTextArcaneFailure",a.toString()); //$NON-NLS-1$
				}
			}

			bString = eqI.moveString();

			if (bString.length() > 0)
			{
				b.appendSpacer();
				b.appendI18nElement("in_itmInfoLabelTextMove",bString); //$NON-NLS-1$
			}

			bString = eqI.getSize();

			if (bString.length() > 0)
			{
				b.appendSpacer();
				b.appendI18nElement("in_itmInfoLabelTextSize",bString); //$NON-NLS-1$
			}

			if (eqI.isWeapon())
			{
				b.appendSpacer();
				b.appendI18nElement("in_itmInfoLabelTextDamage",eqI.getDamage(pc)); //$NON-NLS-1$
				b.appendSpacer();
				b.appendI18nElement("in_itmInfoLabelTextCritMult",eqI.getCritMult()); //$NON-NLS-1$
				b.appendSpacer();
				int critRange = pc.getCritRange(eqI, true);
				b.appendI18nElement("in_itmInfoLabelTextCritRange",critRange == 0 ? "" : Integer.toString(critRange)); //$NON-NLS-1$

				bString = eqI.getRange(pc).toString();
				if (bString.length() > 0)
				{
					b.appendSpacer();
					b.appendI18nElement("in_itmInfoLabelTextRange",bString); //$NON-NLS-1$
				}
			}

			final Integer charges = eqI.getRemainingCharges();

			if (charges >= 0)
			{
				b.appendSpacer();
				b.appendI18nElement("in_itmInfoLabelTextCharges",charges.toString()); //$NON-NLS-1$
			}

			b.appendI18nElement("in_itmInfoLabelTextCost",eqI.getCost(pc).toString()); //$NON-NLS-1$


			String IDS = eqI.getInterestingDisplayString(pc);
			if (IDS.length() > 0)
			{
				b.appendLineBreak();
				b.appendI18nElement("in_itmInfoLabelTextProperties" , eqI.getInterestingDisplayString(pc) ); //$NON-NLS-1$
			}

			bString = eqI.getSafe(StringKey.TEMP_DESCRIPTION);

			if (bString.length() > 0)
			{
				b.appendLineBreak().appendI18nElement("in_itmInfoLabelTextDesc",bString); //$NON-NLS-1$
			}
			
			b.appendLineBreak().appendI18nElement("in_itmInfoLabelTextSource",SourceFormat.getFormattedString(eqI,
			Globals.getSourceDisplay(), true)); //$NON-NLS-1$

			infoLabel.setText(b.toString());
		}
		else if (aSkill != null)
		{
			InfoLabelTextBuilder b = new InfoLabelTextBuilder(aSkill.getDisplayName());

			String bString = aSkill.getSafe(StringKey.TEMP_DESCRIPTION);
			if (bString.length() > 0)
			{
				b.appendLineBreak().appendI18nElement("in_itmInfoLabelTextDesc", bString); //$NON-NLS-1$
			}
			
			bString = SourceFormat.getFormattedString(aSkill,
			Globals.getSourceDisplay(), true);
			if (bString.length() > 0)
			{
				b.appendLineBreak();
				b.appendI18nElement("in_itmInfoLabelTextSource" , bString); //$NON-NLS-1$
			}

			infoLabel.setText(b.toString());
		}
		else if (aSpell != null)
		{
			InfoLabelTextBuilder b = new InfoLabelTextBuilder(OutputNameFormatting.piString(aSpell, false));
			
			b.appendLineBreak();
			b.appendI18nElement("in_itmInfoLabelTextDuration", aSpell.getListAsString(ListKey.DURATION)); //$NON-NLS-1
			b.appendSpacer();
			b.appendI18nElement("in_itmInfoLabelTextRange",aSpell.getListAsString(ListKey.RANGE)); //$NON-NLS-1$
			b.appendSpacer();
			b.appendI18nElement("in_itmInfoLabelTextTarget",aSpell.getSafe(StringKey.TARGET_AREA)); //$NON-NLS-1$
			b.appendLineBreak();
			b.appendI18nElement("in_itmInfoLabelTextSpellDescription",DescriptionFormatting.piDescSubString(pc, aSpell)); //$NON-NLS-1$

			String bString = aSpell.getSafe(StringKey.TEMP_DESCRIPTION);
			if (bString.length() > 0)
			{
				b.appendLineBreak().appendI18nElement("in_itmInfoLabelTextDesc", bString); //$NON-NLS-1$
			}

			String spellSource = SourceFormat.getFormattedString(aSpell,
			Globals.getSourceDisplay(), true);
			if (spellSource.length() > 0)
			{
				b.appendLineBreak();
				b.appendI18nElement("in_itmInfoLabelTextSource" , spellSource); //$NON-NLS-1$
			}

			infoLabel.setText(b.toString());
		}
		else if (aTemp != null)
		{
			InfoLabelTextBuilder b = new InfoLabelTextBuilder(aTemp.getDisplayName());

			String bString = aTemp.getSafe(StringKey.TEMP_DESCRIPTION);
			if (bString.length() > 0)
			{
				b.appendLineBreak().appendI18nElement("in_itmInfoLabelTextDesc", bString); //$NON-NLS-1$
			}
			
			bString = SourceFormat.getFormattedString(aTemp,
			Globals.getSourceDisplay(), true);
			if (bString.length() > 0)
			{
				b.appendLineBreak();
				b.appendI18nElement("in_itmInfoLabelTextSource" , bString); //$NON-NLS-1$
			}

			infoLabel.setText(b.toString());
		}
	}

	private class AvailableClickHandler implements ClickHandler
	{
		public void singleClickEvent()
		{
			// Do Nothing
		}

		public void doubleClickEvent()
		{
			applyBonusButton();
		}

		public boolean isSelectable(Object obj)
		{
			return !(obj instanceof String);
		}
	}

	private class SelectedClickHandler implements ClickHandler
	{
		public void singleClickEvent()
		{
			// Do Nothing
		}

		public void doubleClickEvent()
		{
			removeBonusButton();
		}

		public boolean isSelectable(Object obj)
		{
			return !(obj instanceof String);
		}
	}

	private final void createTreeTables()
	{
		bonusTable = new JTreeTable(bonusModel);

		final JTree atree = bonusTable.getTree();
		atree.setRootVisible(false);
		atree.setShowsRootHandles(true);
		atree.setCellRenderer(new LabelTreeCellRenderer());
		bonusTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		bonusTable.getSelectionModel().addListSelectionListener(
			new ListSelectionListener()
			{
				public void valueChanged(ListSelectionEvent e)
				{
					if (!e.getValueIsAdjusting())
					{
						TreePath avaPath = atree.getSelectionPath();

						if (avaPath == null)
						{
							return;
						}

						Object temp = avaPath.getLastPathComponent();

						if (temp == null)
						{
							infoLabel.setText();
							lastAvaObject = null;
							applyBonusButton.setEnabled(false);

							return;
						}

						PObjectNode fNode = (PObjectNode) temp;

						if ((fNode.getItem() != null)
							&& !(fNode.getItem() instanceof String))
						{
							lastAvaObject = (CDOMObject) fNode.getItem();

							setInfoLabelText(lastAvaObject);
							updateTargetModel();

							// Default choice is first item
							TreePath initTargPath =
									targetTable.getTree().getPathForRow(0);

							if (initTargPath != null)
							{
								applyBonusButton.setEnabled(true);
								targetTable.getTree().setSelectionPath(
									initTargPath);
							}
							else
							{
								applyBonusButton.setEnabled(false);
							}
						}
						else
						{
							applyBonusButton.setEnabled(false);
						}
					}
				}
			});

		// now do the targetTable and targetTree
		targetTable = new JTreeTable(targetModel);

		final JTree stree = targetTable.getTree();
		stree.setRootVisible(false);
		stree.setShowsRootHandles(true);
		stree.setCellRenderer(new LabelTreeCellRenderer());
		targetTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		targetTable.getSelectionModel().addListSelectionListener(
			new ListSelectionListener()
			{
				public void valueChanged(ListSelectionEvent e)
				{
					if (!e.getValueIsAdjusting())
					{
						TreePath selPath = stree.getSelectionPath();

						if (selPath == null)
						{
							return;
						}
					}
				}
			});

		//
		// now do the temporary bonus table
		//
		appliedTable = new JTreeTable(appliedBonusModel);

		final JTree btree = appliedTable.getTree();
		btree.setRootVisible(false);
		btree.setShowsRootHandles(true);
		btree.setCellRenderer(new LabelTreeCellRenderer());
		appliedTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		appliedTable.setShowHorizontalLines(true);

		appliedTable.getSelectionModel().addListSelectionListener(
			new ListSelectionListener()
			{
				public void valueChanged(ListSelectionEvent e)
				{
					if (!e.getValueIsAdjusting())
					{
						TreePath selPath = btree.getSelectionPath();

						if (selPath == null)
						{
							return;
						}

						//final Object temp = selPath.getPathComponent(1);
						final Object temp = selPath.getLastPathComponent();
						MyPONode fNode = (MyPONode) temp;

						//if ((fNode.getItem() != null) && !(fNode.getItem() instanceof String))
						if (fNode.getItem() != null)
						{
							removeBonusButton.setEnabled(true);
						}
						else
						{
							removeBonusButton.setEnabled(false);
						}
					}
				}
			});

		bonusTable.addMouseListener(new JTreeTableMouseAdapter(bonusTable,
			new AvailableClickHandler(), true));
		appliedTable.addMouseListener(new JTreeTableMouseAdapter(appliedTable,
			new SelectedClickHandler(), true));

		// create the rightclick popup menus
		hookupPopupMenu(bonusTable);
		hookupPopupMenu(targetTable);
		hookupPopupMenu(appliedTable);
	}

	/**
	 * Applies a temporary bonus to an Object
	 * The target can be either this PlayerCharacter
	 * or an Equipment object
	 **/
	private void applyBonusButton()
	{
		if (bonusTable.getTree().isSelectionEmpty())
		{
			ShowMessageDelegate.showMessageDialog(
				PropertyFactory.getString("in_itmAppBonButSelectBonusType"), Constants.APPLICATION_NAME, //$NON-NLS-1$
				MessageType.ERROR); 

			return;
		}

		if (targetTable.getTree().isSelectionEmpty())
		{
			ShowMessageDelegate.showMessageDialog(
				PropertyFactory.getString("in_itmAppBonButSelectBonusTarget"), //$NON-NLS-1$
				Constants.APPLICATION_NAME, MessageType.ERROR);

			return;
		}

		TreePath bonusPath = bonusTable.getTree().getSelectionPath();
		TreePath targetPath = targetTable.getTree().getSelectionPath();
		Object anObj = null;
		Object aTarget = null;
		CDOMObject aMod = null;
		int bonusLevel = 999;

		Object endComp = targetPath.getLastPathComponent();
		PObjectNode fNode = (PObjectNode) endComp;

		if (fNode.getItem() != null)
		{
			aTarget = fNode.getItem();
		}

		if ((aTarget == null) || (fNode == null))
		{
			ShowMessageDelegate.showMessageDialog(
				PropertyFactory.getString("in_itmAppBonButSelectBonusTarget"), //$NON-NLS-1$
				Constants.APPLICATION_NAME, MessageType.ERROR);

			return;
		}

		endComp = bonusPath.getLastPathComponent();
		fNode = (PObjectNode) endComp;

		if (fNode.getItem() != null)
		{
			anObj = fNode.getItem();
		}

		if ((anObj == null) || (fNode == null))
		{
			ShowMessageDelegate.showMessageDialog(
				PropertyFactory.getString("in_itmAppBonButSelectBonusType"), Constants.APPLICATION_NAME, //$NON-NLS-1$
				MessageType.ERROR);

			return;
		}

		if (anObj instanceof CDOMObject)
		{
			aMod = (CDOMObject) anObj;
		}

		Equipment aEq = null;

		if (aTarget instanceof Equipment)
		{
			// Create new Item
			aEq = ((Equipment) aTarget).clone();
			aEq.makeVirtual();
			String currAppName = aEq.getAppliedName();
			if (currAppName != null && currAppName.length() > 2)
			{
				if (pc.hasTempApplied(aMod))
				{
					ShowMessageDelegate.showMessageDialog(
						PropertyFactory.getString("in_itmAppBonButAlreadyApplied"), //$NON-NLS-1$
						Constants.APPLICATION_NAME, MessageType.ERROR);
					return;
				}
				// We need to remove the [] from the old name
				aEq.setAppliedName(currAppName.substring(2, currAppName
					.length() - 1)
					+ ", " + aMod.getKeyName());
			}
			else
			{
				aEq.setAppliedName(aMod.getKeyName());
			}
		}

		String repeatValue = "";

		// get the bonus string
		for (BonusObj aBonus : aMod.getBonusList(aEq == null ? pc : aEq))
		{
			if (aBonus.isTempBonus())
			{
				String oldValue = aBonus.toString();
				String newValue = oldValue;
				if (aMod.getSafe(StringKey.CHOICE_STRING).length() > 0)
				{
					BonusInfo bi = getBonusChoice(oldValue, aMod, repeatValue);
					if (bi != null)
					{
						newValue = bi.getBonusValue();
						repeatValue = bi.getRepeatValue();
					}
				}
				BonusObj newB = Bonus.newBonus(Globals.getContext(), newValue);
				if (newB != null)
				{
					// We clear the prereqs and add the non-PREAPPLY prereqs from the old bonus
					// Why are we doing this? (for qualifies)
					newB.clearPrerequisiteList();
					for (Prerequisite prereq : aBonus.getPrerequisiteList())
					{
						if (prereq.getKind() == null
							|| !prereq.getKind().equalsIgnoreCase(
								Prerequisite.APPLY_KIND))
						{
							try
							{
								newB.addPrerequisite(prereq.clone());
							}
							catch (CloneNotSupportedException e)
							{
								throw new UnreachableError(
										"Prerequisites should be cloneable by PCGen design");
							}
						}
					}

					// if Target was this PC, then add
					// bonus to TempBonusMap
					if (aTarget instanceof PlayerCharacter)
					{
						pc.setApplied(newB, newB.qualifies(pc, aEq));
						pc.addTempBonus(newB, aMod, aTarget);
					}
					else if (aEq != null)
					{
						pc.setApplied(newB, PrereqHandler.passesAll(newB.getPrerequisiteList(), aEq, pc));
						aEq.addTempBonus(newB);
						pc.addTempBonus(newB, aMod, aEq);
						// TODO - Why does this case make us mark the PC as 
						// dirty when the other case doesn't?
						pc.setDirty(true);
					}
				}
			}
		}

		// if the Target is an Equipment item
		// then add it to the tempBonusItemList
		if (aEq != null)
		{
			pc.addTempBonusItemList(aEq);
		}

		updateAppliedModel();
		updateTargetModel();

		pc.setDirty(true);

		// Make sure bonuses are recalculated
		pc.calcActiveBonuses();

		// now Update all the other tabs
		CharacterInfo pane = PCGen_Frame1.getCharacterPane();
		pane.setPaneForUpdate(pane.infoSpecialAbilities());
		pane.setPaneForUpdate(pane.infoClasses());
		pane.setPaneForUpdate(pane.infoAbilities());
		pane.setPaneForUpdate(pane.infoSkills());
		pane.setPaneForUpdate(pane.infoSpells());
		pane.setPaneForUpdate(pane.infoSummary());
		pane.refresh();
	}

	private final void createBonusModel()
	{
		if (bonusModel == null)
		{
			bonusModel = new BonusModel(MODEL_BONUS);
		}
		else
		{
			bonusModel.resetModel(MODEL_BONUS);
		}

		if (bonusSort != null)
		{
			bonusSort.setRoot((PObjectNode) bonusModel.getRoot());

			bonusSort.sortNodeOnColumn();
		}
	}

	private final void createModels()
	{
		createBonusModel();
		createTargetModel();
		createAppliedModel();
	}

	private final void createTargetModel()
	{
		if (targetModel == null)
		{
			targetModel = new BonusModel(MODEL_TARGET);
		}
		else
		{
			targetModel.resetModel(MODEL_TARGET);
		}

		if (targetSort != null)
		{
			targetSort.setRoot((PObjectNode) targetModel.getRoot());

			targetSort.sortNodeOnColumn();
		}
	}

	/**
	 * Creates the Temp AppliedModel
	 **/
	private void createAppliedModel()
	{
		if (appliedBonusModel == null)
		{
			appliedBonusModel = new AppliedModel();
		}
		else
		{
			appliedBonusModel.resetModel();
		}
	}

	/**
	 * This is called when the tab is shown
	 **/
	private void formComponentShown()
	{
		requestFocus();
		PCGen_Frame1.setMessageAreaTextWithoutSaving("");
		refresh();

		int s = topVertSplit.getDividerLocation();
		int t = centerHorzSplit.getDividerLocation();
		int u = botHorzSplit.getDividerLocation();
		int width;

		if (!hasBeenSized)
		{
			hasBeenSized = true;

			Component c = getParent();
			s =
					SettingsHandler.getPCGenOption("InfoTempMod.topVertSplit", //$NON-NLS-1$
						((c.getWidth() * 1) / 2));
			t =
					SettingsHandler.getPCGenOption(
						"InfoTempMod.centerHorzSplit", //$NON-NLS-1$
						((c.getHeight() * 1) / 2));
			u =
					SettingsHandler.getPCGenOption("InfoTempMod.botHorzSplit", //$NON-NLS-1$
						((botPane.getHeight() * 1) / 2));

			// set the prefered width on targetTable
			for (int i = 0; i < targetTable.getColumnCount(); ++i)
			{
				TableColumn sCol = targetTable.getColumnModel().getColumn(i);
				width = Globals.getCustColumnWidth("TempModSel", i); //$NON-NLS-1$

				if (width != 0)
				{
					sCol.setPreferredWidth(width);
				}

				sCol.addPropertyChangeListener(new ResizeColumnListener(
					targetTable, "TempModSel", i)); //$NON-NLS-1$
			}

			// set the prefered width on bonusTable
			for (int i = 0; i < bonusTable.getColumnCount(); ++i)
			{
				TableColumn sCol = bonusTable.getColumnModel().getColumn(i);
				width = Globals.getCustColumnWidth("TempModAva", i); //$NON-NLS-1$

				if (width != 0)
				{
					sCol.setPreferredWidth(width);
				}

				sCol.addPropertyChangeListener(new ResizeColumnListener(
					bonusTable, "TempModAva", i)); //$NON-NLS-1$
			}
		}

		if (s > 0)
		{
			topVertSplit.setDividerLocation(s);
			SettingsHandler.setPCGenOption("InfoTempMod.topVertSplit", s); //$NON-NLS-1$
		}

		if (t > 0)
		{
			centerHorzSplit.setDividerLocation(t);
			SettingsHandler.setPCGenOption("InfoTempMod.centerHorzSplit", t); //$NON-NLS-1$
		}

		if (u > 0)
		{
			botHorzSplit.setDividerLocation(u);
			SettingsHandler.setPCGenOption("InfoTempMod.botHorzSplit", u); //$NON-NLS-1$
		}
	}

	private void hookupPopupMenu(JTreeTable treeTable)
	{
		treeTable.addMouseListener(new BonusPopupListener(treeTable,
			new BonusPopupMenu(treeTable)));
	}

	private void initActionListeners()
	{
		addComponentListener(new ComponentAdapter()
		{
			public void componentShown(ComponentEvent evt)
			{
				formComponentShown();
			}
		});
		topVertSplit.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
			new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent evt)
				{
					saveDividerLocations();
				}
			});
		botHorzSplit.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
			new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent evt)
				{
					saveDividerLocations();
				}
			});
		centerHorzSplit.addPropertyChangeListener(
			JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent evt)
				{
					saveDividerLocations();
				}
			});
		applyBonusButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				applyBonusButton();
			}
		});
		removeBonusButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				removeBonusButton();
			}
		});
		/* commented out until we fix temp mods, do not delete
		 useTempMods.addActionListener(new ActionListener()
		 {
		 public void actionPerformed(ActionEvent evt)
		 {
		 pc.setUseTempMods(useTempMods.isSelected());
		 pc.setDirty(true);
		 }
		 });
		 */
	}

	private void saveDividerLocations()
	{
		if (!hasBeenSized)
		{
			return;
		}

		int s = topVertSplit.getDividerLocation();
		if (s > 0)
		{
			SettingsHandler.setPCGenOption("InfoTempMod.topVertSpli", s); //$NON-NLS-1$
		}

		s = botHorzSplit.getDividerLocation();
		if (s > 0)
		{
			SettingsHandler.setPCGenOption("InfoTempMod.botHorzSplit", s); //$NON-NLS-1$
		}

		s = centerHorzSplit.getDividerLocation();
		if (s > 0)
		{
			SettingsHandler.setPCGenOption("InfoTempMod.centerHorzSplit", s); //$NON-NLS-1$
		}
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		readyForRefresh = true;
		// flesh out all the tree views
		createModels();

		// create tables associated with the above trees
		createTreeTables();

		// build topPane which will contain leftPane and rightPane
		// leftPane will have a scrollregion
		// rightPane will have a scrollregion
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		topPane.setLayout(gridbag);

		JPanel leftPane = new JPanel();
		JPanel rightPane = new JPanel();
		leftPane.setLayout(gridbag);
		rightPane.setLayout(gridbag);

		applyBonusButton = new JButton(PropertyFactory.getString(
				"in_itmInitCompAppBonTitle")); //$NON-NLS-1$
		Utility.setDescription(applyBonusButton,
				PropertyFactory.getString("in_itmInitCompAppBonDesc")); //$NON-NLS-1$
		applyBonusButton.setEnabled(false);
		applyBonusButton.setPreferredSize(new Dimension(60, 20));
		applyBonusButton.setSize(new Dimension(60, 20));

		Utility.buildConstraints(c, 0, 0, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTH;
		c.insets = new Insets(2, 0, 2, 0);
		gridbag.setConstraints(applyBonusButton, c);
		topPane.add(applyBonusButton);

		JPanel aPanel = new JPanel();
		aPanel.setLayout(new BorderLayout());

		// Create the split between the two panels
		topVertSplit =
				new FlippingSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane,
					rightPane);
		topVertSplit.setOneTouchExpandable(true);
		topVertSplit.setDividerSize(10);
		aPanel.add(topVertSplit, BorderLayout.CENTER);

		Utility.buildConstraints(c, 0, 2, 1, 1, 10, 10);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTH;
		c.ipadx = 1;
		gridbag.setConstraints(aPanel, c);
		topPane.add(aPanel);

		// build the left pane for the available bonus table
		Utility.buildConstraints(c, 0, 0, 1, 1, 10, 10);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTH;
		c.ipadx = 1;

		JScrollPane scrollPane = new JScrollPane(bonusTable);
		gridbag.setConstraints(scrollPane, c);
		leftPane.add(scrollPane);

		// now build the right pane for the target table
		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		rightPane.setLayout(gridbag);

		Utility.buildConstraints(c, 0, 0, 1, 1, 10, 10);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTH;
		c.ipadx = 1;
		scrollPane = new JScrollPane(targetTable);
		gridbag.setConstraints(scrollPane, c);
		targetTable.setShowHorizontalLines(true);
		rightPane.add(scrollPane);

		//bonusTable.setColAlign(COL_TYPE, SwingConstants.RIGHT);
		//bonusTable.setColAlign(COL_QTY, SwingConstants.CENTER);
		//targetTable.setColAlign(COL_TYPE, SwingConstants.RIGHT);
		//targetTable.setColAlign(COL_QTY, SwingConstants.CENTER);
		// ---------- build Bottom Panel ----------------
		// botPane will contain a bHeadPane and a bTailPane
		// bHeadPane will contain a scrollregion (Source Bonus info)
		// bTailPane will contain a scrollregion (applied Bonuses)
		botPane.setLayout(new BorderLayout());

		gridbag = new GridBagLayout();
		c = new GridBagConstraints();

		JPanel bHeadPane = new JPanel();
		JPanel bTailPane = new JPanel();
		bHeadPane.setLayout(gridbag);
		bTailPane.setLayout(gridbag);

		botHorzSplit =
				new FlippingSplitPane(JSplitPane.VERTICAL_SPLIT, bHeadPane,
					bTailPane);
		botHorzSplit.setOneTouchExpandable(true);
		botHorzSplit.setDividerSize(10);

		botPane.add(botHorzSplit, BorderLayout.CENTER);

		// Bottom Head (top) panel
		// create an info scroll area
		Utility.buildConstraints(c, 0, 0, 1, 1, 2, 2);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;

		JScrollPane sScroll = new JScrollPane();
		gridbag.setConstraints(sScroll, c);

		TitledBorder sTitle = BorderFactory.createTitledBorder(PropertyFactory.getString("in_itmInitCompBorderInfo")); //$NON-NLS-1$
		sTitle.setTitleJustification(TitledBorder.CENTER);
		sScroll.setBorder(sTitle);
		infoLabel.setBackground(topPane.getBackground());
		sScroll.setViewportView(infoLabel);

		bHeadPane.add(sScroll);

		// Bottom Tail (bottom) panel
		// create a temproary bonus select and view panel
		Utility.buildConstraints(c, 0, 0, 1, 1, 1, 1);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;

		JPanel rPanel = new JPanel();
		gridbag.setConstraints(rPanel, c);

		JPanel iPanel = new JPanel();
		iPanel.setLayout(new BorderLayout(8, 0));

		/* commented out until we fix temp mods, do not delete
		 useTempMods = new JCheckBox("Use Temporary Bonuses");
		 */
		removeBonusButton = new JButton(PropertyFactory.getString("in_itmInitCompRemoveButTitle")); //$NON-NLS-1$
		removeBonusButton.setEnabled(false);

		tempModsDisabledWarning = new JLabel("");
		/* commented out until we fix temp mods, do not delete
		 iPanel.add(useTempMods, BorderLayout.WEST);
		 */
		iPanel.add(removeBonusButton, BorderLayout.CENTER);
		iPanel.add(tempModsDisabledWarning, BorderLayout.EAST);

		rPanel.add(iPanel);

		Utility.buildConstraints(c, 0, 1, 1, 1, 10, 10);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;

		JScrollPane tbPane = new JScrollPane(appliedTable);
		gridbag.setConstraints(tbPane, c);

		bTailPane.add(rPanel);
		bTailPane.add(tbPane);

		// now split the top and bottom Panels
		centerHorzSplit =
				new FlippingSplitPane(JSplitPane.VERTICAL_SPLIT, topPane,
					botPane);
		centerHorzSplit.setOneTouchExpandable(true);
		centerHorzSplit.setDividerSize(10);

		// now add the entire mess (centered of course)
		this.setLayout(new BorderLayout());
		this.add(centerHorzSplit, BorderLayout.CENTER);

		// make sure we update when switching tabs
		this.addFocusListener(new FocusAdapter()
		{
			public void focusGained(FocusEvent evt)
			{
				refresh();
			}
		});

		// add the sorter tables to that clicking on the TableHeader
		// actualy does something (gawd damn it's slow!)
		bonusSort =
				new JTreeTableSorter(bonusTable, (PObjectNode) bonusModel
					.getRoot(), bonusModel);
		targetSort =
				new JTreeTableSorter(targetTable, (PObjectNode) targetModel
					.getRoot(), targetModel);
	}

	/**
	 * Redraw/recalc everything
	 **/
	private void refreshButton()
	{
		forceRefresh();
	}

	/**
	 * removes a bonus, target pair from the appliedTable
	 **/
	private void removeBonusButton()
	{
		if (appliedTable.getTree().isSelectionEmpty())
		{
			ShowMessageDelegate.showMessageDialog(
				PropertyFactory.getString("in_itmRemBonButSelect"), Constants.APPLICATION_NAME,
				MessageType.ERROR); //$NON-NLS-1$

			return;
		}

		TreePath bonusPath = appliedTable.getTree().getSelectionPath();

		TempWrap tbWrap = null;
		Object aCreator = null;
		Object aTarget = null;

		Object endComp = bonusPath.getLastPathComponent();
		MyPONode fNode = (MyPONode) endComp;

		if ((fNode == null) || (fNode.getItem() == null))
		{
			Logging.errorPrintLocalised("in_itmRemBoneButFnodeNull");

			return;
		}

		if (fNode.getItem() instanceof TempWrap)
		{
			tbWrap = (TempWrap) fNode.getItem();
		}
		else if (fNode.getItem() instanceof String)
		{
			for (TempWrap tw : tbwList)
			{
				if (tw.getName().equals(fNode.getItem()))
				{
					tbWrap = tw;
				}
			}
		}
		else
		{
			Logging.errorPrintLocalised("in_itmRemBonButUnkownBonusType");

			return;
		}

		if (tbWrap != null)
		{
			aCreator = tbWrap.getCreator();
			aTarget = tbWrap.getTarget();
		}

		if ((aTarget == null) || (aCreator == null))
		{
			Logging.errorPrintLocalised("in_itmRemBonButTargetNull");
			return;
		}

		Equipment aEq = null;
		PlayerCharacter bPC = null;

		if (aTarget instanceof Equipment)
		{
			aEq = (Equipment) aTarget;
		}
		else if (aTarget instanceof PlayerCharacter)
		{
			bPC = (PlayerCharacter) aTarget;
		}

		for (Map.Entry<BonusObj, BonusManager.TempBonusInfo> me : pc
				.getTempBonusMap().entrySet())
		{
			BonusObj aBonus = me.getKey();
			TempBonusInfo tbi = me.getValue();
			Object aC = tbi.source;
			Object aT = tbi.target;

			if ((aT instanceof Equipment) && (aEq != null))
			{
				if (aEq.equals(aT) && (aCreator == aC))
				{
					pc.removeTempBonus(aBonus);
					pc.removeTempBonusItemList((Equipment) aT);
					((Equipment) aT).removeTempBonus(aBonus);
					((Equipment) aT).setAppliedName("");
				}
			}
			else if ((aT instanceof PlayerCharacter) && (bPC != null))
			{
				if (((PlayerCharacter) aT == bPC) && (aCreator == aC))
				{
					pc.removeTempBonus(aBonus);
				}
			}
		}

		updateAppliedModel();
		updateTargetModel();
		pc.setDirty(true);

		// Make sure bonuses are recalculated
		pc.calcActiveBonuses();

		// now Update all the other tabs
		CharacterInfo pane = PCGen_Frame1.getCharacterPane();
		pane.setPaneForUpdate(pane.infoSpecialAbilities());
		pane.setPaneForUpdate(pane.infoClasses());
		pane.setPaneForUpdate(pane.infoAbilities());
		pane.setPaneForUpdate(pane.infoSkills());
		pane.setPaneForUpdate(pane.infoSpells());
		pane.setPaneForUpdate(pane.infoSummary());
		pane.refresh();
	}

	/**
	 * Updates the Temp Bonus table
	 **/
	private void updateAppliedModel()
	{
		List<String> pathList = appliedTable.getExpandedPaths();
		createAppliedModel();
		appliedTable.updateUI();
		appliedTable.expandPathList(pathList);
	}

	/**
	 * Updates the Bonus table
	 **/
	private void updateBonusModel()
	{
		List<String> pathList = bonusTable.getExpandedPaths();
		createBonusModel();
		bonusTable.updateUI();
		bonusTable.expandPathList(pathList);
	}

	/**
	 * Updates the Target table
	 **/
	private void updateTargetModel()
	{
		List<String> pathList = targetTable.getExpandedPaths();
		createTargetModel();
		targetTable.updateUI();
		targetTable.expandPathList(pathList);
	}

	/**
	 * Class Bonus wrapper
	 * each ClassWrap contains a Class and the level
	 **/
	public static final class ClassWrap
	{
		private PCClass _class = null;
		private int _level = 0;

		/**
		 * Constructor
		 * @param aClass
		 * @param aLevel
		 */
		public ClassWrap(PCClass aClass, int aLevel)
		{
			_class = aClass;
			_level = aLevel;
		}

		/**
		 * Get the level
		 * @return level
		 */
		public int getLevel()
		{
			return _level;
		}

		/**
		 * Get the class
		 * @return class
		 */
		public PCClass getMyClass()
		{
			return _class;
		}

		@Override
		public String toString()
		{
			StringBuffer b = new StringBuffer();
			b.append(_class.getDisplayName());
			b.append(" (").append(_level).append(")");

			return b.toString();
		}
	}

	/**
	 * Temp Bonus wrapper for the appliedTable
	 * each TempWrap contains the creator and target of a bonus
	 **/
	public static final class TempWrap
	{
		private BonusObj _bonus = null;
		private Object _creator = null;
		private Object _target = null;

		/**
		 * Constructor
		 * @param aMod
		 * @param aTarget
		 * @param aBonus
		 */
		public TempWrap(Object aMod, Object aTarget, BonusObj aBonus)
		{
			_creator = aMod;
			_target = aTarget;
			_bonus = aBonus;
		}

		/**
		 * Get the BONUS object
		 * @return BONUS object
		 */
		public BonusObj getBonus()
		{
			return _bonus;
		}

		/**
		 * Get the creator of the bonus
		 * @return the creator of the bonus
		 */
		public Object getCreator()
		{
			return _creator;
		}

		/**
		 * Get the name of this bonus
		 * @return the name of this bonus
		 */
		public String getName()
		{
			StringBuffer b = new StringBuffer();

			if (_creator instanceof PlayerCharacter)
			{
				b.append(((PlayerCharacter) _creator).getName());
			}
			else if (_creator instanceof CDOMObject)
			{
				b.append(_creator.toString());
			}

			b.append(" [");

			if (_target instanceof PlayerCharacter)
			{
				b.append(PropertyFactory.getString("in_itmTmpWrapGetNamePC"));
			}
			else if (_target instanceof Equipment)
			{
				b.append(((Equipment) _target).getName());
			}

			b.append("]");

			return b.toString();
		}

		/**
		 * Get the target of the bonus
		 * @return the target of the bonus
		 */
		public Object getTarget()
		{
			return _target;
		}
	}

	private final class AppliedModel extends AbstractTreeTableModel
	{
		// list of columns names
		private String[] colNameList =
				new String[]{PropertyFactory.getString("in_itmAppModelNameTarget"), //$NON-NLS-1$
					PropertyFactory.getString("in_itmAppModelBonusType"), PropertyFactory.getString("in_itmAppModelBonusTo"), //$NON-NLS-1$ //$NON-NLS-2$
					PropertyFactory.getString("in_itmAppModelBonusValue")}; //$NON-NLS-1$
		private MyPONode bonusRoot;

		/**
		 * Creates a AppliedModel
		 **/
		private AppliedModel()
		{
			super(null);
			resetModel();
		}

		/**
		 * Returns boolean if can edit a cell
		 * @param node
		 * @param column
		 * @return true if cell editable
		 **/
		public boolean isCellEditable(Object node, int column)
		{
			return column == BONUS_COL_NAME;
		}

		/**
		 * Returns Class for the column
		 * @param column
		 * @return Class
		 **/
		public Class<?> getColumnClass(int column)
		{
			if (column == BONUS_COL_NAME)
			{
				return TreeTableModel.class;
			}

			return String.class;
		}

		/* The JTreeTableNode interface. */

		/**
		 * Returns int number of columns
		 * @return column count
		 **/
		public int getColumnCount()
		{
			return colNameList.length;
		}

		/**
		 * Returns String name of a column
		 * @param column
		 * @return column name
		 **/
		public String getColumnName(int column)
		{
			return colNameList[column];
		}

		// return the root node
		public Object getRoot()
		{
			return super.getRoot();
		}

		/**
		 * Returns Object value of the column
		 * @param node
		 * @param column
		 * @return value
		 **/
		public Object getValueAt(Object node, int column)
		{
			final MyPONode fn = (MyPONode) node;
			TempWrap tbWrap = null;
			String nameString = "";
			String aType = "";
			String aTo = "";
			String aVal = "";

			if (fn == null)
			{
				Logging
					.errorPrintLocalised("in_itmAppModelNoActiveNode"); //$NON-NLS-1$

				return null;
			}

			if (fn.getItem() instanceof TempWrap)
			{
				tbWrap = (TempWrap) fn.getItem();

				BonusObj aBonus = tbWrap.getBonus();
				tbWrap.getTarget();
				aType = aBonus.getTypeOfBonus();
				aTo = aBonus.getBonusInfo();
				aVal = aBonus.resolve(pc, "").toString();
			}
			else if (fn.getItem() instanceof String)
			{
				nameString = fn.toString();
			}

			switch (column)
			{
				case BONUS_COL_NAME:

					if (tbWrap != null)
					{
						return tbWrap;
					}
					else if (nameString.length() > 0)
					{
						return nameString;
					}

					return null;

				case BONUS_COL_TYPE:
					return aType;

				case BONUS_COL_TO:
					return aTo;

				case BONUS_COL_VAL:
					return aVal;

				default:

					if (fn != null)
					{
						return fn.toString();
					}
					Logging
						.errorPrintLocalised("in_itmAppModelNoActiveNode"); //$NON-NLS-1$
					return null;
			}
		}

		// There must be a root node, but we keep it hidden
		private void setRoot(MyPONode aNode)
		{
			super.setRoot(aNode);
		}

		/**
		 * This assumes the AppliedModel exists but
		 * needs branches and nodes to be repopulated
		 **/
		private void resetModel()
		{
			// this is the root node
			bonusRoot = new MyPONode();

			// an array of TempWrap'ers
			List<String> sList = new ArrayList<String>();
			tbwList = new ArrayList<TempWrap>();

			// iterate thru all PC's bonuses
			// and build an Array of TempWrap'ers
			for (Map.Entry<BonusObj, BonusManager.TempBonusInfo> me : pc
					.getTempBonusMap().entrySet())
			{
				BonusObj aBonus = me.getKey();
				TempBonusInfo tbi = me.getValue();
				Object aC = tbi.source;
				Object aT = tbi.target;
				TempWrap tw = new TempWrap(aC, aT, aBonus);

				tbwList.add(tw);

				String sString = tw.getName();

				if (!sList.contains(sString))
				{
					sList.add(sString);
				}
			}

			// build the tree structure
			MyPONode[] cc = new MyPONode[sList.size()];

			for (int i = 0; i < sList.size(); i++)
			{
				String hString = sList.get(i);
				cc[i] = new MyPONode();
				cc[i].setItem(hString);

				for (int j = 0; j < tbwList.size(); j++)
				{
					TempWrap tw = tbwList.get(j);
					String aString = tw.getName();

					if (hString.equals(aString))
					{
						MyPONode aFN = new MyPONode(tw);
						aFN.setParent(cc[i]);
						cc[i].addChild(aFN);
					}
				}

				if (!cc[i].isLeaf())
				{
					cc[i].setParent(bonusRoot);
				}
			}

			bonusRoot.setChildren(cc);

			setRoot(bonusRoot);

			MyPONode rootAsPObjectNode = (MyPONode) super.getRoot();

			if ((rootAsPObjectNode != null)
				&& (rootAsPObjectNode.getChildCount() > 0))
			{
				fireTreeNodesChanged(super.getRoot(), new TreePath(super
					.getRoot()));
			}
		}
	}

	/**
	 * The TreeTableModel has a single <code>root</code> node
	 * This root node has a null <code>parent</code>
	 * All other nodes have a parent which points to a non-null node
	 * Parent nodes contain a list of  <code>children</code>, which
	 * are all the nodes that point to it as their parent
	 * <code>nodes</code> which have 0 children are leafs (the end of
	 * that linked list)  Nodes which have at least 1 child are not leafs
	 * Leafs are like files and non-leafs are like directories
	 * The leafs contain an Object that we want to know about (Equipment)
	 **/
	private final class BonusModel extends AbstractTreeTableModel
	{
		// there are two roots. One for bonus equipment
		// and one for target equipment profiles
		private PObjectNode avaRoot;
		private PObjectNode selRoot;

		// list of columns names
		private String[] avaNameList = {""};
		private String[] selNameList = {""};

		// Types of the columns.
		private int modelType = MODEL_BONUS;

		/**
		 * Creates a BonusModel
		 * @param iModel
		 **/
		private BonusModel(int iModel)
		{
			super(null);

			//
			// if you change/add/remove entries to nameList
			// you also need to change the static COL_XXX defines
			// at the begining of this file
			//
			avaNameList = new String[]{PropertyFactory.getString("in_itmBonModelAvaNameName"), //$NON-NLS-1$
					PropertyFactory.getString("in_itmBonModelAvaNameSource"), PropertyFactory.getString("in_itmBonModelAvaNameFile")}; //$NON-NLS-1$ //$NON-NLS-2$
			selNameList = new String[]{PropertyFactory.getString("in_itmBonModelSelNameName"), //$NON-NLS-1$
					PropertyFactory.getString("in_itmBonModelSelNameTarget"), PropertyFactory.getString("in_itmBonModelSelNameFile")}; //$NON-NLS-1$ //$NON-NLS-2$

			modelType = iModel;
			resetModel(iModel);
		}

		/**
		 * Returns boolean if can edit a cell. (BonusModel)
		 * @param node
		 * @param column
		 * @return true if cell editable
		 **/
		public boolean isCellEditable(Object node, int column)
		{
			return column == COL_NAME;
		}

		/**
		 * Returns Class for the column. (BonusModel)
		 * @param column
		 * @return Class
		 **/
		public Class<?> getColumnClass(int column)
		{
			switch (column)
			{
				case COL_NAME:
					return TreeTableModel.class;

				case COL_TYPE:
				case COL_SRC:
					break;

				default:
					Logging
						.errorPrintLocalised("in_itmBonModelGetColumnClassNotSupported",column);

					break;
			}

			return String.class;
		}

		/* The JTreeTableNode interface. */

		/**
		 * Returns int number of columns. (BonusModel)
		 * @return column count
		 **/
		public int getColumnCount()
		{
			return (modelType == MODEL_BONUS) ? avaNameList.length
				: selNameList.length;
		}

		/**
		 * Returns String name of a column. (BonusModel)
		 * @param column
		 * @return column name
		 **/
		public String getColumnName(int column)
		{
			return (modelType == MODEL_BONUS) ? avaNameList[column]
				: selNameList[column];
		}

		// return the root node
		public Object getRoot()
		{
			return super.getRoot();
		}

		/**
		 * Returns Object value of the column. (BonusModel)
		 * @param node
		 * @param column
		 * @return value
		 **/
		public Object getValueAt(Object node, int column)
		{
			PObjectNode fn = (PObjectNode) node;
			Ability aFeat = null;
			Ability aAbility = null;
			Spell aSpell = null;
			Equipment eqI = null;
			PCClass aClass = null;
			PCClassLevel aClassLevel = null;
			PCTemplate aTemp = null;
			Skill aSkill = null;
			PlayerCharacter bPC = null;

			if (fn == null)
			{
				Logging
					.errorPrintLocalised("in_itmBonModelNoActiveNode");

				return null;
			}

			String name = null;

			if (fn.getItem() instanceof Equipment)
			{
				eqI = (Equipment) fn.getItem();
				name =
						new StringBuffer(eqI.longName()).append(
							eqI.getAppliedName()).toString();
			}
			else if (fn.getItem() instanceof PCClass)
			{
				aClass = (PCClass) fn.getItem();
			}
			else if (fn.getItem() instanceof PCClassLevel)
			{
				aClassLevel = (PCClassLevel) fn.getItem();
			}
			else if (fn.getItem() instanceof Ability)
			{
				aFeat = (Ability) fn.getItem();
				if (!aFeat.getCategory().equals("FEAT"))
				{
					aAbility = (Ability) fn.getItem();
				}
			}
			else if (fn.getItem() instanceof Spell)
			{
				aSpell = (Spell) fn.getItem();
			}
			else if (fn.getItem() instanceof PCTemplate)
			{
				aTemp = (PCTemplate) fn.getItem();
			}
			else if (fn.getItem() instanceof Skill)
			{
				aSkill = (Skill) fn.getItem();
			}
			else if (fn.getItem() instanceof PlayerCharacter)
			{
				bPC = (PlayerCharacter) fn.getItem();
			}

			switch (column)
			{
				case COL_NAME:

					if (bPC != null)
					{
						return bPC.getName();
					}
					else if (name != null)
					{
						return name;
					}
					else
					{
						return fn.toString();
					}

				case COL_TYPE:

					if (eqI != null)
					{
						return eqI.getType();
					}
					else if (aSpell != null)
					{
						return PropertyFactory.getString("in_itmBonModelTargetTypeSpell"); //$NON-NLS-1$ 
					}
					else if (aAbility != null)
					{
						return PropertyFactory.getString("in_itmBonModelTargetTypeClass"); //$NON-NLS-1$
					}
					else if (aFeat != null)
					{
						return PropertyFactory.getString("in_itmBonModelTargetTypeFeat"); //$NON-NLS-1$
					}
					else if (aClass != null || aClassLevel != null)
					{
						return PropertyFactory.getString("in_itmBonModelTargetTypeClass"); //$NON-NLS-1$
					}
					else if (aTemp != null)
					{
						return PropertyFactory.getString("in_itmBonModelTargetTypeTemplate"); //$NON-NLS-1$
					}
					else if (aSkill != null)
					{
						return PropertyFactory.getString("in_itmBonModelTargetTypeSkill"); //$NON-NLS-1$
					}
					else if (bPC != null)
					{
						return PropertyFactory.getString("in_itmBonModelTargetTypeCharacter"); //$NON-NLS-1$
					}
					else
					{
						return null;
					}

				case COL_SRC:

					if (eqI != null)
					{
						return SourceFormat.getFormattedString(eqI,
						Globals.getSourceDisplay(), true);
					}
					else if (aSpell != null)
					{
						return SourceFormat.getFormattedString(aSpell,
						Globals.getSourceDisplay(), true);
					}
					else if (aAbility != null)
					{
						return SourceFormat.getFormattedString(aAbility,
						Globals.getSourceDisplay(), true);
					}
					else if (aFeat != null)
					{
						return SourceFormat.getFormattedString(aFeat,
						Globals.getSourceDisplay(), true);
					}
					else if (aClass != null)
					{
						return SourceFormat.getFormattedString(aClass,
						Globals.getSourceDisplay(), true);
					}
					else if (aClassLevel != null)
					{
						return SourceFormat.getFormattedString(aClassLevel,
						Globals.getSourceDisplay(), true);
					}
					else if (aTemp != null)
					{
						return SourceFormat.getFormattedString(aTemp,
						Globals.getSourceDisplay(), true);
					}
					else if (aSkill != null)
					{
						return SourceFormat.getFormattedString(aSkill,
						Globals.getSourceDisplay(), true);
					}
					else
					{
						return null;
					}

				default:

					return fn.toString();
			}
		}

		// There must be a root node, but we keep it hidden
		private void setRoot(PObjectNode aNode)
		{
			super.setRoot(aNode);
		}

		/**
		 * This assumes the BonusModel exists but
		 * needs branches and nodes to be repopulated
		 * @param argModelType
		 **/
		private void resetModel(int argModelType)
		{
			// This is the array of all equipment types
			List<String> eqTypeList = new ArrayList<String>();
			List<String> typeList = new ArrayList<String>();

			// build the list of all equipment types
			eqTypeList.add(Constants.s_CUSTOM);

			for (Equipment bEq : pc.getEquipmentSet())
			{
				final StringTokenizer aTok =
						new StringTokenizer(bEq.getType(), ".", false);
				String aString;

				while (aTok.hasMoreTokens())
				{
					aString = aTok.nextToken();

					if (!eqTypeList.contains(aString))
					{
						eqTypeList.add(aString);
					}
				}
			}

			Collections.sort(eqTypeList);

			typeList.add("Feats"); //$NON-NLS-1$
			typeList.add("Items"); //$NON-NLS-1$
			typeList.add("Spells"); //$NON-NLS-1$
			typeList.add("Classes"); //$NON-NLS-1$
			typeList.add("Templates"); //$NON-NLS-1$
			typeList.add("Skills"); //$NON-NLS-1$

			//
			// build bonusTable (list of all equipment)
			//
			if (argModelType == MODEL_BONUS)
			{
				// this is the root node
				avaRoot = new PObjectNode();

				setRoot(avaRoot);

				// build the Type root nodes
				PObjectNode[] pNode = new PObjectNode[6];
				pNode[0] = new PObjectNode("Feat"); //$NON-NLS-1$
				pNode[1] = new PObjectNode("Spell"); //$NON-NLS-1$
				pNode[2] = new PObjectNode("Item"); //$NON-NLS-1$
				pNode[3] = new PObjectNode("Class"); //$NON-NLS-1$
				pNode[4] = new PObjectNode("Templates"); //$NON-NLS-1$
				pNode[5] = new PObjectNode("Skills"); //$NON-NLS-1$

				//
				// first do PC's feats and other abilities
				for (Ability aFeat : pc.getFullAbilitySet())
				{
					for (BonusObj aBonus : aFeat.getRawBonusList(pc))
					{
						if (aBonus.isTempBonus())
						{
							PObjectNode aFN = new PObjectNode(aFeat);
							aFN.setParent(pNode[0]);
							pNode[0].addChild(aFN, true);
							pNode[0].setParent(avaRoot);
						}
					}
				}

				//
				// next do all Feats to get PREAPPLY:ANYPC
				for (Ability aFeat : Globals.getContext().ref.getManufacturer(
						Ability.class, AbilityCategory.FEAT).getAllObjects())
				{
					for (BonusObj aBonus : aFeat.getRawBonusList(pc))
					{
						if (aBonus.isTempBonus()
							&& aBonus
								.isTempBonusTarget(BonusObj.TempBonusTarget.ANYPC))
						{
							PObjectNode aFN = new PObjectNode(aFeat);
							aFN.setParent(pNode[0]);
							pNode[0].addChild(aFN, true);
							pNode[0].setParent(avaRoot);
						}
					}
				}

				//
				// Do all the PC's spells
				for (Spell aSpell : pc.aggregateSpellList("", "", "", 0, 9))
				{
					if (aSpell == null)
					{
						continue;
					}

					for (BonusObj aBonus : aSpell.getRawBonusList(pc))
					{
						if (aBonus.isTempBonus())
						{
							PObjectNode aFN = new PObjectNode(aSpell);
							aFN.setParent(pNode[1]);
							pNode[1].addChild(aFN, true);
							pNode[1].setParent(avaRoot);
						}
					}
				}
				
				// Do all the pc's innate spells.
				Collection<CharacterSpell> innateSpells= pc.getCharacterSpells(pc.getRace(), Globals.INNATE_SPELL_BOOK_NAME);
				for (CharacterSpell aCharacterSpell : innateSpells) {
					if (aCharacterSpell == null)
					{
						continue;
					}
					for (BonusObj aBonus : aCharacterSpell.getSpell().getRawBonusList(pc))
					{
						if (aBonus.isTempBonus())
						{
							PObjectNode aFN = new PObjectNode(aCharacterSpell.getSpell());
							aFN.setParent(pNode[1]);
							pNode[1].addChild(aFN, true);
							pNode[1].setParent(avaRoot);
						}
					}
				}
				
				//
				// Next do all spells to get PREAPPLY:ANYPC
				for (Iterator<?> fI = Globals.getSpellMap().values().iterator(); fI
					.hasNext();)
				{
					final Object obj = fI.next();
					Spell aSpell = null;

					if (obj instanceof Spell)
					{
						aSpell = (Spell) obj;
					}
					else if (obj instanceof ArrayList)
					{
						continue;
					}

					if (aSpell == null)
					{
						continue;
					}

					for (BonusObj aBonus : aSpell.getRawBonusList(pc))
					{
						//aBonus.getPrereqString();

						if (aBonus.isTempBonus()
							&& !aBonus
								.isTempBonusTarget(BonusObj.TempBonusTarget.PC))
						{
							PObjectNode aFN = new PObjectNode(aSpell);
							aFN.setParent(pNode[1]);
							pNode[1].addChild(aFN, true);
							pNode[1].setParent(avaRoot);
						}
					}
				}

				if (!pNode[1].isLeaf())
				{
					pNode[1].setParent(avaRoot);
				}

				//
				// iterate thru all PC's equipment objects
				for (Equipment aEq : pc.getEquipmentSet())
				{
					for (BonusObj aBonus : aEq.getRawBonusList(pc))
					{
						if (aBonus.isTempBonus())
						{
							PObjectNode aFN = new PObjectNode(aEq);
							aFN.setParent(pNode[2]);
							pNode[2].addChild(aFN, true);
							pNode[2].setParent(avaRoot);
						}
					}
				}

				if (!pNode[2].isLeaf())
				{
					pNode[2].setParent(avaRoot);
				}

				//
				// Do we also need to Iterate Globals.getAbilityKeyIterator(Constants.ALL_CATEGORIES); ?
				// or will they be covered by getClassList()?
				//
				// iterate thru all PC's Classes
				for (PCClass aClass : pc.getClassSet())
				{
					int currentLevel = pc.getLevel(aClass);
					for (BonusObj aBonus : aClass.getRawBonusList(pc))
					{
						if (aBonus.isTempBonus())
						{
							PObjectNode aFN = new PObjectNode(aClass);
							aFN.setParent(pNode[3]);
							pNode[3].addChild(aFN, true);
							pNode[3].setParent(avaRoot);
						}
					}
					for (int i = 1; i < currentLevel; i++)
					{
						PCClassLevel pcl = pc.getActiveClassLevel(aClass, i);
						for (BonusObj aBonus : pcl.getRawBonusList(pc))
						{
							if (aBonus.isTempBonus())
							{
								PObjectNode aFN = new PObjectNode(pcl);
								aFN.setParent(pNode[3]);
								pNode[3].addChild(aFN, true);
								pNode[3].setParent(avaRoot);
							}
						}
					}
				}

				if (!pNode[3].isLeaf())
				{
					pNode[3].setParent(avaRoot);
				}

				//
				// Iterate through all the PC's Templates
				for (PCTemplate aTemp : pc.getTemplateSet())
				{
					for (BonusObj aBonus : aTemp.getRawBonusList(pc))
					{
						if (aBonus.isTempBonus())
						{
							PObjectNode aFN = new PObjectNode(aTemp);
							aFN.setParent(pNode[4]);
							pNode[4].addChild(aFN, true);
							pNode[4].setParent(avaRoot);
						}
					}
				}

				// do all Templates to get PREAPPLY:ANYPC
				for (PCTemplate aTemp : Globals.getContext().ref.getConstructedCDOMObjects(PCTemplate.class))
				{
					for (BonusObj aBonus : aTemp.getRawBonusList(pc))
					{
						if (aBonus.isTempBonus()
							&& aBonus
								.isTempBonusTarget(BonusObj.TempBonusTarget.ANYPC))
						{
							PObjectNode aFN = new PObjectNode(aTemp);
							aFN.setParent(pNode[4]);
							pNode[4].addChild(aFN, true);
							pNode[4].setParent(avaRoot);
						}
					}
				}

				if (!pNode[4].isLeaf())
				{
					pNode[4].setParent(avaRoot);
				}

				//
				// Iterate through all the PC's Skills
				for (Skill aSkill : pc.getSkillSet())
				{
					for (BonusObj aBonus : aSkill.getRawBonusList(pc))
					{
						if (aBonus.isTempBonus())
						{
							PObjectNode aFN = new PObjectNode(aSkill);
							aFN.setParent(pNode[5]);
							pNode[5].addChild(aFN, true);
							pNode[5].setParent(avaRoot);
						}
					}
				}

				if (!pNode[5].isLeaf())
				{
					pNode[5].setParent(avaRoot);
				}

				// now add to the root node
				avaRoot.setChildren(pNode);
			}
			// end of bonusTable builder

			else
			{ // targetTable builder

				// this is the root node
				selRoot = new PObjectNode();
				setRoot(selRoot);

				if (lastAvaObject == null)
				{
					return;
				}

				boolean found = false;

				for (BonusObj aBonus : lastAvaObject.getBonusList(pc))
				{
					if (aBonus == null)
					{
						continue;
					}

					if (aBonus.isTempBonus())
					{
						if ((aBonus
							.isTempBonusTarget(BonusObj.TempBonusTarget.ANYPC) || aBonus
							.isTempBonusTarget(BonusObj.TempBonusTarget.PC))
							&& !found)
						{
							PObjectNode aFN = new PObjectNode(pc);
							aFN.setParent(selRoot);
							selRoot.addChild(aFN, true);
							found = true;
						}
					}
				}

				pc.setCalcEquipmentList(pc.getUseTempMods());
				for (Equipment aEq : pc.getEquipmentSet())
				{
					found = false;

					for (BonusObj aBonus : lastAvaObject.getBonusList(aEq))
					{
						if (aBonus == null)
						{
							continue;
						}
						if (aBonus.isTempBonus())
						{
							boolean passesApply = true;
							for (Iterator<Prerequisite> iter =
									aBonus.getPrerequisiteList().iterator(); iter
								.hasNext()
								&& passesApply;)
							{
								Prerequisite element = iter.next();
								if (element.getKind() != null
									&& element.getKind().equalsIgnoreCase(
										Prerequisite.APPLY_KIND))
								{
									if (!PrereqHandler.passes(element, aEq, pc))
										passesApply = false;
								}
							}
							if (passesApply && !found)
							{
								PObjectNode aFN = new PObjectNode(aEq);
								aFN.setParent(selRoot);
								aFN.setDisplayName(new StringBuffer(aEq
									.longName()).append(aEq.getAppliedName())
									.toString());
								selRoot.addChild(aFN);
								found = true;
							}
						}
					}
				}

				setRoot(selRoot);
			}
			// end if else

			PObjectNode rootAsPObjectNode = (PObjectNode) super.getRoot();

			if ((rootAsPObjectNode != null)
				&& (rootAsPObjectNode.getChildCount() > 0))
			{
				fireTreeNodesChanged(super.getRoot(), new TreePath(super
					.getRoot()));
			}
		}
	}

	private class BonusPopupListener extends MouseAdapter
	{
		private BonusPopupMenu menu;
		private JTree tree;

		BonusPopupListener(JTreeTable treeTable, BonusPopupMenu aMenu)
		{
			tree = treeTable.getTree();
			menu = aMenu;

			KeyListener myKeyListener = new KeyListener()
			{
				public void keyTyped(KeyEvent e)
				{
					dispatchEvent(e);
				}

				//
				// Walk through the list of accelerators to see
				// if the user has pressed a sequence used by
				// the popup. This would not otherwise happen
				// unless the popup was showing
				//
				public void keyPressed(KeyEvent e)
				{
					final int keyCode = e.getKeyCode();

					if (keyCode != KeyEvent.VK_UNDEFINED)
					{
						final KeyStroke keyStroke =
								KeyStroke.getKeyStrokeForEvent(e);

						for (int i = 0; i < menu.getComponentCount(); ++i)
						{
							final Component menuComponent =
									menu.getComponent(i);

							if (menuComponent instanceof JMenuItem)
							{
								KeyStroke ks =
										((JMenuItem) menuComponent)
											.getAccelerator();

								if ((ks != null) && keyStroke.equals(ks))
								{
									((JMenuItem) menuComponent).doClick(2);

									return;
								}
							}
						}
					}

					dispatchEvent(e);
				}

				public void keyReleased(KeyEvent e)
				{
					dispatchEvent(e);
				}
			};

			treeTable.addKeyListener(myKeyListener);
		}

		public void mousePressed(MouseEvent evt)
		{
			maybeShowPopup(evt);
		}

		public void mouseReleased(MouseEvent evt)
		{
			maybeShowPopup(evt);
		}

		private void maybeShowPopup(MouseEvent evt)
		{
			if (evt.isPopupTrigger())
			{
				TreePath selPath =
						tree.getClosestPathForLocation(evt.getX(), evt.getY());

				if (selPath == null)
				{
					return;
				}

				tree.setSelectionPath(selPath);
				menu.show(evt.getComponent(), evt.getX(), evt.getY());
			}
		}
	}

	/**
	 * create right click menus and listeners
	 **/
	private class BonusPopupMenu extends JPopupMenu
	{
		BonusPopupMenu(JTreeTable treeTable)
		{
			if (treeTable == bonusTable)
			{
				BonusPopupMenu.this.add(createAddMenuItem(PropertyFactory.getString("in_itmBonPopUpAppBon"), //$NON-NLS-1$
					"shortcut EQUALS"));
				BonusPopupMenu.this.addSeparator();
				BonusPopupMenu.this
					.add(createRefreshMenuItem(PropertyFactory.getString("in_itmBonusPopUpRedraw"))); //$NON-NLS-1$
			}
			else if (treeTable == targetTable)
			{
				BonusPopupMenu.this
					.add(createRefreshMenuItem(PropertyFactory.getString("in_itmBonusPopUpRedraw"))); //$NON-NLS-1$
			}
			else if (treeTable == appliedTable)
			{
				BonusPopupMenu.this.add(createRemoveMenuItem(PropertyFactory.getString("in_itmBonPopUpRemove"),
					"shortcut MINUS"));
			}
		}

		private JMenuItem createAddMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new AddBonusActionListener(),
				PropertyFactory.getString("in_itmBonPopUpAppBon"), (char) 0, accelerator, 
				PropertyFactory.getString("in_itmBonPopUpAppBonDesc"), "Add16.gif", true); //$NON-NLS-1$ //$NON-NLS-2$
		}

		private JMenuItem createRefreshMenuItem(String label)
		{
			return Utility.createMenuItem(label, new RefreshActionListener(),
				PropertyFactory.getString("in_itmBonusPopUpRedraw"), (char) 0, null,
				PropertyFactory.getString("in_itmBonPopUpRedrawDesc"), "", true); //$NON-NLS-1$ //$NON-NLS-2$
		}

		private JMenuItem createRemoveMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new RemoveActionListener(),
				PropertyFactory.getString("in_itmBonPopUpRemove"), (char) 0, 
				accelerator,PropertyFactory.getString("in_itmBonPopUpRemove"), "",
				true); //$NON-NLS-1$ //$NON-NLS-2$
		}

		private class AddBonusActionListener extends BonusActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				applyBonusButton();
			}
		}

		private class BonusActionListener implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				// TODO This method currently does nothing?
			}
		}

		private class RefreshActionListener extends BonusActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				refreshButton();
			}
		}

		private class RemoveActionListener extends BonusActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				removeBonusButton();
			}
		}
	}

	/**
	 * This is an extend of PObjectNode so I can overload toString()
	 **/
	private static final class MyPONode extends PObjectNode
	{
		private MyPONode()
		{
			// Empty Constructor
		}

		private MyPONode(Object anItem)
		{
			super(anItem);
		}

		@Override
		public String toString()
		{
			Object item = super.getItem();

			if (item == null)
			{
				return ""; //$NON-NLS-1$
			}

			if (item instanceof String)
			{
				return (String) item;
			}
			else if (item instanceof TempWrap)
			{
				return "--"; //$NON-NLS-1$
			}
			else
			{
				return super.toString();
			}
		}
	}
	
	private class BonusInfo
	{

		private final String bonusValue;
		private final String repeatValue;
		
		public BonusInfo(String value, String repeat)
		{
			bonusValue = value;
			repeatValue = repeat;
		}

		public String getBonusValue()
		{
			return bonusValue;
		}

		public String getRepeatValue()
		{
			return repeatValue;
		}
		
	}
}
