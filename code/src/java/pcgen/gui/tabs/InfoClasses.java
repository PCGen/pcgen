/*
 * InfoClasses.java
 * Copyright 2002 (C) Bryan McRoberts
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
 * Created on Feb 16, 2002 11:15 AM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.gui.tabs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
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
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.tree.TreePath;

import pcgen.cdom.base.Constants;
import pcgen.cdom.content.HitDie;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.PCCheck;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.RuleConstants;
import pcgen.core.SettingsHandler;
import pcgen.core.SubClass;
import pcgen.core.analysis.OutputNameFormatting;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.PrerequisiteUtilities;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui.CharacterInfo;
import pcgen.gui.CharacterInfoTab;
import pcgen.gui.GuiConstants;
import pcgen.gui.PCGen_Frame1;
import pcgen.gui.TableColumnManager;
import pcgen.gui.TableColumnManagerModel;
import pcgen.gui.pcGenGUI;
import pcgen.gui.filter.FilterAdapterPanel;
import pcgen.gui.filter.FilterConstants;
import pcgen.gui.filter.FilterFactory;
import pcgen.gui.panes.FlippingSplitPane;
import pcgen.gui.utils.AbstractTreeTableModel;
import pcgen.gui.utils.ClickHandler;
import pcgen.gui.utils.IconUtilitities;
import pcgen.gui.utils.InfoLabelTextBuilder;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.gui.utils.JLabelPane;
import pcgen.gui.utils.JTreeTable;
import pcgen.gui.utils.JTreeTableMouseAdapter;
import pcgen.gui.utils.JTreeTableSorter;
import pcgen.gui.utils.LabelTreeCellRenderer;
import pcgen.gui.utils.PObjectNode;
import pcgen.gui.utils.ResizeColumnListener;
import pcgen.gui.utils.TreeTableModel;
import pcgen.gui.utils.Utility;
import pcgen.gui.utils.WholeNumberField;
import pcgen.util.Delta;
import pcgen.util.Logging;
import pcgen.system.LanguageBundle;
import pcgen.util.enumeration.Tab;
import pcgen.util.enumeration.Visibility;

/**
 * ???
 * @author  Bryan McRoberts (merton_monk@yahoo.com)
 * @version $Revision$
 */
public final class InfoClasses extends FilterAdapterPanel implements
		CharacterInfoTab
{
	static final long serialVersionUID = 9141488354194857537L;
	private static boolean needsUpdate = true;
	private static int viewMode = GuiConstants.INFOCLASS_VIEW_NAME; // keep track of what view mode we're in for Available
	private static Integer saveViewMode = null;
	private static int viewSelectMode = GuiConstants.INFOCLASS_VIEW_NAME; // keep track of what view mode we're in for Selected. defaults to LanguageBundle.getString("in_nameLabel")
	private static int splitOrientation = JSplitPane.HORIZONTAL_SPLIT;
	private static PObjectNode typeRoot;
	private static PObjectNode sourceRoot;
	private final JLabel avaLabel =
			new JLabel(LanguageBundle.getString("in_available"));
	private final JLabel selLabel =
			new JLabel(LanguageBundle.getString("in_selected"));
	private ClassModel availableModel = null; // Model for the JTreeTable.
	private ClassModel selectedModel = null; // Model for the JTreeTable.
	private FlippingSplitPane asplit;
	private FlippingSplitPane bsplit;
	private FlippingSplitPane splitPane;
	private JButton addButton;
	private JButton adjXP = new JButton(LanguageBundle.getString("in_adjXP"));
	private JButton hpButton = null;
	private JButton removeButton;
	private JButton clearQFilterButton = new JButton(LanguageBundle.getString("in_clear"));
	private JComboBoxEx viewComboBox = new JComboBoxEx();
	private JComboBoxEx viewSelectComboBox = new JComboBoxEx();
	private JTree availableTree = null;
	private JTree selectedTree = null;
	private JLabel featCount = new JLabel();
	private JLabel lAHP = new JLabel();
	private JLabel lBAB = new JLabel();
	private JLabel lDefense = new JLabel();
	private JLabel lVariableDisplay = new JLabel();
	private JLabel lVariableDisplay2 = new JLabel();
	private JLabel lVariableDisplay3 = new JLabel();
	private JLabel lblAltHP = new JLabel();
	private JLabel lblBAB = new JLabel();
	private JLabel lblDefense = new JLabel();
	private JLabel lblExperience = new JLabel();
	private JLabel lblFeats = null;
	private JLabel lblHP = new JLabel();
	private JLabel lblHPName = null;
	private JLabel lblNextLevel = new JLabel();
	private final JLabel lblQFilter = new JLabel(LanguageBundle.getString("in_filter") + ":");
	private JLabel lblSkills = new JLabel();
	private JLabel lblVariableDisplay = new JLabel();
	private JLabel lblVariableDisplay2 = new JLabel();
	private JLabel lblVariableDisplay3 = new JLabel();
	private JLabel skillCount = new JLabel();
	private JLabelPane infoLabel = new JLabelPane();
	private JPanel center = new JPanel();
	private JPanel pnlAltHP = new JPanel();
	private JPanel pnlBAB = new JPanel();
	private JPanel pnlDefense = new JPanel();
	private JPanel pnlEast = new JPanel();
	private JPanel pnlFeats = null;
	private JPanel pnlFillerEast = new JPanel();
	private JPanel pnlFillerSouth = new JPanel();
	private JPanel pnlFillerWest = new JPanel();
	private JPanel pnlHP = new JPanel();
	private JPanel pnlSkills = new JPanel();
	private JPanel pnlVariableDisplay = new JPanel();
	private JPanel pnlVariableDisplay2 = new JPanel();
	private JPanel pnlVariableDisplay3 = new JPanel();
	private JPanel pnlWest = new JPanel();
	private JPanel pnlXP = new JPanel();
	private JTextField textQFilter = new JTextField();
	private JTreeTable availableTable; // the available Class
	private JTreeTable selectedTable; // the selected Class
	private JTreeTableSorter availableSort = null;
	private PCClass lastClass = null; //keep track of which PCClass was last selected from either table

	// Right-click inventory item
	private TreePath selPath;
	private WholeNumberField experience = new WholeNumberField();
	private WholeNumberField txtNextLevel = new WholeNumberField();
	private JLabel[] lCheck;
	private JLabel[] lblCheck;
	private JPanel[] pnlCheck;
	private boolean hasBeenSized = false;

	private PlayerCharacter pc;
	private int serial = 0;
	private boolean readyForRefresh = false;

	/**
	 * Constructor
	 * @param pc
	 */
	public InfoClasses(PlayerCharacter pc)
	{
		this.pc = pc;

		// do not remove this
		// we will use the component's name to save component specific settings
		setName(Tab.CLASSES.toString());

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				initComponents();
				initActionListeners();
			}
		});
	}

	public void setPc(PlayerCharacter pc)
	{
		if (this.pc != pc || pc.getSerial() > serial)
		{
			this.pc = pc;
			serial = pc.getSerial();
			forceRefresh();
		}
	}

	public PlayerCharacter getPc()
	{
		return pc;
	}

	public int getTabOrder()
	{
		return SettingsHandler.getPCGenOption(".Panel.Classes.Order",
			Tab.CLASSES.ordinal());
	}

	public void setTabOrder(int order)
	{
		SettingsHandler.setPCGenOption(".Panel.Classes.Order", order);
	}

	public String getTabName()
	{
		GameMode game = SettingsHandler.getGame();
		return game.getTabName(Tab.CLASSES);
	}

	public boolean isShown()
	{
		GameMode game = SettingsHandler.getGame();
		return game.getTabShown(Tab.CLASSES);
	}

	/**
	 * Retrieve the list of tasks to be done on the tab.
	 * @return List of task descriptions as Strings.
	 */
	public List<String> getToDos()
	{
		List<String> toDoList = new ArrayList<String>();
		if (pc.getXP() >= pc.minXPForNextECL())
		{
			toDoList.add(SettingsHandler.getGame().getLevelUpMessage());
		}
		return toDoList;
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
	 * specifies whether the "match any" option should be available
	 * @return true
	 */
	public boolean isMatchAnyEnabled()
	{
		return true;
	}

	/**
	 * specifies whether the "negate/reverse" option should be available
	 * @return true
	 */
	public boolean isNegateEnabled()
	{
		return true;
	}

	/**
	 * specifies the filter selection mode
	 * @return FilterConstants.MULTI_MULTI_MODE = 2
	 */
	public int getSelectionMode()
	{
		return FilterConstants.MULTI_MULTI_MODE;
	}

	/**
	 * implementation of Filterable interface
	 */
	public void initializeFilters()
	{
		FilterFactory.registerAllSourceFilters(this);
		FilterFactory.registerAllClassFilters(this);
		FilterFactory.registerAllPrereqAlignmentFilters(this);
	}

	/**
	 * implementation of Filterable interface
	 */
	public void refreshFiltering()
	{
		forceRefresh();
	}

	/**
	 * Update the HP of the PC
	 */
	public void updateHP()
	{
		if (pc == null)
		{
			return;
		}

		lblHP.setText(String.valueOf(pc.hitPoints()));
	}

	private void setInfoLabelText(PCClass aClass, PObjectNode pn)
	{
		String aString;
		boolean isSubClass = false;
		lastClass = aClass; //even if that's null

		if (lastClass instanceof SubClass)
		{
			lastClass = (PCClass) pn.getParent().getItem();
			isSubClass = true;
		}

		if (aClass != null)
		{
			final InfoLabelTextBuilder b = new InfoLabelTextBuilder(OutputNameFormatting.piString(aClass, false));
			b.appendLineBreak();

			
			//
			// Type
			//
			aString = aClass.getType();
			if (isSubClass && (aString.length() == 0))
			{
				aString = lastClass.getType();
			}
			b.appendI18nElement("in_clInfoType",aString); //$NON-NLS-1$

			//
			// Hit Die
			//
			HitDie hitDie = aClass.getSafe(ObjectKey.LEVEL_HITDIE);
			if (isSubClass && HitDie.ZERO.equals(hitDie))
			{
				hitDie = lastClass.getSafe(ObjectKey.LEVEL_HITDIE);
			}
			if (!HitDie.ZERO.equals(hitDie))
			{
				b.appendSpacer();
				b.appendI18nElement("in_clInfoHD", "d" + hitDie.getDie()); //$NON-NLS-1$  //$NON-NLS-2$
			}

			if (Globals.getGameModeShowSpellTab())
			{
				aString = aClass.get(StringKey.SPELLTYPE);

				if (isSubClass && aString == null)
				{
					aString = lastClass.getSpellType();
				}

				b.appendSpacer();
				b.appendI18nElement("in_clInfoSpellType", aString); //$NON-NLS-1$

				aString = aClass.getSpellBaseStat();

				/*
				 * CONSIDER This test here is the ONLY place where the "magical"
				 * value of null is tested for in getSpellBaseStat(). This is
				 * currently set by SubClass and SubstititionClass, so it IS
				 * used, but the question is: Is there a better method for
				 * identifying this special deferral to the "lastClass" other
				 * than null SpellBaseStat? - thpr 11/9/06
				 */
				if (isSubClass
					&& ((aString == null) || (aString.length() == 0)))
				{
					aString = lastClass.getSpellBaseStat();
				}

				b.appendSpacer();
				b.appendI18nElement("in_clInfoBaseStat", aString); //$NON-NLS-1$
			}

			//
			// Prereqs
			//
			aString = PrerequisiteUtilities.preReqHTMLStringsForList(pc, null,
			aClass.getPrerequisiteList(), false);

			if (isSubClass && (aString.length() == 0))
			{
				aString = PrerequisiteUtilities.preReqHTMLStringsForList(pc, null,
				lastClass.getPrerequisiteList(), false);
			}

			if (aString.length() > 0)
			{
				b.appendLineBreak();
				b.appendI18nElement("in_requirements", aString); //$NON-NLS-1$
			}

			//
			// Source
			//
			aString = SourceFormat.getFormattedString(aClass,
			Globals.getSourceDisplay(), true);

			if (isSubClass && (aString.length() == 0))
			{
				aString = SourceFormat.getFormattedString(lastClass,
				Globals.getSourceDisplay(), true);
			}

			if (aString.length() > 0)
			{
				b.appendLineBreak();
				b.appendI18nElement("in_source", aString); //$NON-NLS-1$
			}

			infoLabel.setText(b.toString());
		}
	}

	private PCClass getSelectedClass()
	{
		if (lastClass == null)
		{
			ShowMessageDelegate.showMessageDialog(LanguageBundle
				.getString("in_clNoClass"), Constants.APPLICATION_NAME,
				MessageType.ERROR);
		}

		return lastClass;
	}

	private int getSelectedIndex(ListSelectionEvent e)
	{
		final DefaultListSelectionModel model =
				(DefaultListSelectionModel) e.getSource();

		if (model == null)
		{
			return -1;
		}

		return model.getMinSelectionIndex();
	}

	private void addClass(int levels)
	{
		if (Globals.getGameModeAlignmentText().length() != 0)
		{
			if ((levels > 0)
				&& (pc.getPCAlignment().getAbb().equals(Constants.NONE)))
			{
				ShowMessageDelegate.showMessageDialog(LanguageBundle
					.getString("in_clSelAlign"), Constants.APPLICATION_NAME,
					MessageType.ERROR);

				return;
			}

			if ((levels > 0) && !pc.canLevelUp())
			{
				ShowMessageDelegate.showMessageDialog(LanguageBundle
					.getString("in_Enforce_rejectLevelUp"),
					Constants.APPLICATION_NAME, MessageType.ERROR);
				return;
			}

		}

		PCClass theClass = getSelectedClass();

		if ((theClass == null) || !theClass.qualifies(pc, theClass))
		{
			return;
		}

		pc.setDirty(true);

		final PCClass aClass = pc.getClassKeyed(theClass.getKeyName());

		//
		// TODO:
		// If attempting to add a different subclass
		// (eg. Evoker to an Illusionist) warn the user
		// However, adding a level of the base class
		// (i.e. Wizard to Illusionist) should still be okay
		//
		

		// Check if the subclass (if any) is qualified for
		String subClassKey = pc.getSubClassName(aClass);
		if (levels > 0 && aClass != null && subClassKey != null)
		{
			final PCClass subClass =
					aClass.getSubClassKeyed(subClassKey);
			if (subClass != null && !subClass.qualifies(pc, aClass))
			{
				ShowMessageDelegate.showMessageDialog(LanguageBundle
					.getString("in_clYouAreNotQualifiedToTakeTheClass")
					+ aClass.getDisplayName()
					+ "/" //$NON-NLS-1$
					+ subClass.getDisplayName()
					+ ".", Constants.APPLICATION_NAME, MessageType.ERROR);  //$NON-NLS-1$

				return;
			}
		}
		
		if ((levels < 0)
			|| (aClass == null)
			|| Globals.checkRule(RuleConstants.LEVELCAP)
			|| (!Globals.checkRule(RuleConstants.LEVELCAP) && (!aClass
				.hasMaxLevel() || pc.getLevel(aClass) < aClass.getSafe(IntegerKey.LEVEL_LIMIT))))
		{
			pc.incrementClassLevel(levels, theClass);
		}
		else
		{
			ShowMessageDelegate.showMessageDialog(LanguageBundle
				.getString("in_clMaxLvl"), Constants.APPLICATION_NAME,
				MessageType.INFORMATION);

			return;
		}

		CharacterInfo pane = PCGen_Frame1.getCharacterPane();
		pane.setPaneForUpdate(pane.infoAbilities());
		pane.setPaneForUpdate(pane.infoDomain());
		pane.setPaneForUpdate(pane.infoSkills());
		pane.setPaneForUpdate(pane.infoSpells());
		pane.setPaneForUpdate(pane.infoSpecialAbilities());
		pane.setPaneForUpdate(pane.infoSummary());
		pane.refresh();

		//
		// If we've just added the first non-monster level,
		// ask to choose free item of clothing if haven't already
		//
		if (levels > 0)
		{
			TabUtils.selectClothes(pc);
		}

		pc.setDirty(true);

		forceRefresh();
	}

	/**
	 * Populate the lower right-hand panel's right panel (the one with the checks in it).
	 */
	private void buildEastPanel()
	{
		GridBagConstraints gbc;
		String aString;

		pnlEast.setLayout(new GridBagLayout());

		final List<PCCheck> checkList = Globals.getContext().ref
				.getOrderSortedCDOMObjects(PCCheck.class);
		final int countChecks = checkList.size();

		if (countChecks != 0)
		{
			pnlCheck = new JPanel[countChecks];
			lCheck = new JLabel[countChecks];
			lblCheck = new JLabel[countChecks];

			for (int i = 0; i < countChecks; ++i)
			{
				pnlCheck[i] = new JPanel();
				pnlCheck[i].setLayout(new BorderLayout(5, 5));

				lblCheck[i] = new JLabel();
				lCheck[i] = new JLabel();
				pnlCheck[i].add(lblCheck[i], BorderLayout.WEST);
				pnlCheck[i].add(lCheck[i], BorderLayout.EAST);

				gbc = new GridBagConstraints();
				gbc.gridx = 0;

				if (i == 0)
				{
					gbc.gridy = 0;
				}

				gbc.fill = GridBagConstraints.BOTH;
				gbc.anchor = GridBagConstraints.WEST;
				gbc.weightx = 1.0;
				pnlEast.add(pnlCheck[i], gbc);
			}
		}

		pnlVariableDisplay.setLayout(new BorderLayout(5, 5));
		aString = Globals.getGameModeVariableDisplayText();
		lblVariableDisplay.setText(aString);
		pnlVariableDisplay.add(lblVariableDisplay, BorderLayout.WEST);
		pnlVariableDisplay.setVisible(aString.length() != 0);
		pnlVariableDisplay.add(lVariableDisplay, BorderLayout.EAST);
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		pnlEast.add(pnlVariableDisplay, gbc);

		pnlVariableDisplay2.setLayout(new BorderLayout(5, 5));
		aString = Globals.getGameModeVariableDisplay2Text();
		lblVariableDisplay2.setText(aString);
		pnlVariableDisplay2.add(lblVariableDisplay2, BorderLayout.WEST);
		pnlVariableDisplay2.setVisible(aString.length() != 0);
		pnlVariableDisplay2.add(lVariableDisplay2, BorderLayout.EAST);
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		pnlEast.add(pnlVariableDisplay2, gbc);

		pnlVariableDisplay3.setLayout(new BorderLayout(5, 5));
		aString = Globals.getGameModeVariableDisplay3Text();
		lblVariableDisplay3.setText(aString);
		pnlVariableDisplay3.add(lblVariableDisplay3, BorderLayout.WEST);
		pnlVariableDisplay3.setVisible(aString.length() != 0);
		pnlVariableDisplay3.add(lVariableDisplay3, BorderLayout.EAST);
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		pnlEast.add(pnlVariableDisplay3, gbc);

		pnlAltHP.setLayout(new BorderLayout(5, 5));
		aString = Globals.getGameModeAltHPText();
		lblAltHP.setText(aString);
		pnlAltHP.add(lblAltHP, BorderLayout.WEST);
		pnlAltHP.setVisible(aString.length() != 0);
		pnlAltHP.add(lAHP, BorderLayout.EAST);
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		pnlEast.add(pnlAltHP, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		pnlEast.add(pnlFillerEast, gbc);
		pnlEast.setVisible(true);
		pnlEast.updateUI();
	}

	private void createAvailableModel()
	{
		if (availableModel == null)
		{
			availableModel = new ClassModel(viewMode, true);
		}
		else
		{
			availableModel.resetModel(viewMode, true);
		}

		if (availableSort != null)
		{
			availableSort.setRoot((PObjectNode) availableModel.getRoot());
			availableSort.sortNodeOnColumn();
		}
	}

	/**
	 * Creates the ClassModel that will be used.
	 */
	private void createModels()
	{
		createSelectedModel();
		createAvailableModel();
	}

	private void createSelectedModel()
	{
		if (selectedModel == null)
		{
			selectedModel = new ClassModel(viewSelectMode, false);
		}
		else
		{
			selectedModel.resetModel(viewSelectMode, false);
		}
	}

	private void createTreeTables()
	{
		availableTable = new JTreeTable(availableModel);
		availableTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		availableTree = availableTable.getTree();

		availableTree.setRootVisible(false);
		availableTree.setShowsRootHandles(true);
		availableTree.setCellRenderer(new LabelTreeCellRenderer());

		selectedTable = new JTreeTable(selectedModel);
		selectedTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		selectedTree = selectedTable.getTree();
		selectedTree.setRootVisible(false);
		selectedTree.setShowsRootHandles(true);
		selectedTree.setCellRenderer(new LabelTreeCellRenderer());

		hookupPopupMenu(availableTable);
		hookupPopupMenu(selectedTable);
	}

	// This is called when the tab is shown.
	private void formComponentShown()
	{
		requestFocus();
		PCGen_Frame1.setMessageAreaTextWithoutSaving(LanguageBundle
			.getString("in_clNotQualify"));
		refresh();

		int s = splitPane.getDividerLocation();
		int t = bsplit.getDividerLocation();
		int u = asplit.getDividerLocation();
		int width;

		if (!hasBeenSized)
		{
			hasBeenSized = true;
			s =
					SettingsHandler.getPCGenOption("InfoClasses.splitPane",
						(int) ((this.getSize().getWidth() * 7) / 10));
			t =
					SettingsHandler.getPCGenOption("InfoClasses.bsplit",
						(int) (this.getSize().getHeight() - 140));
			u =
					SettingsHandler.getPCGenOption("InfoClasses.asplit",
						(int) (this.getSize().getWidth() - 334));

			// set the prefered width on selectedTable
			for (int i = 0; i < selectedTable.getColumnCount(); i++)
			{
				TableColumn sCol = selectedTable.getColumnModel().getColumn(i);
				width = Globals.getCustColumnWidth("ClassSel", i);

				if (width != 0)
				{
					sCol.setPreferredWidth(width);
				}

				sCol.addPropertyChangeListener(new ResizeColumnListener(
					selectedTable, "ClassSel", i));
			}

			// set the prefered width on availableTable
			for (int i = 0; i < availableTable.getColumnCount(); i++)
			{
				TableColumn sCol = availableTable.getColumnModel().getColumn(i);
				width = Globals.getCustColumnWidth("ClassAva", i);

				if (width != 0)
				{
					sCol.setPreferredWidth(width);
				}

				sCol.addPropertyChangeListener(new ResizeColumnListener(
					availableTable, "ClassAva", i));
			}
		}

		if (s > 0)
		{
			splitPane.setDividerLocation(s);
			SettingsHandler.setPCGenOption("InfoClasses.splitPane", s);
		}

		if (t > 0)
		{
			bsplit.setDividerLocation(t);
			SettingsHandler.setPCGenOption("InfoClasses.bsplit", t);
		}

		if (u > 0)
		{
			asplit.setDividerLocation(u);
			SettingsHandler.setPCGenOption("InfoClasses.asplit", u);
		}
	}

	private void hookupPopupMenu(JTreeTable treeTable)
	{
		treeTable.addMouseListener(new ClassPopupListener(treeTable,
			new ClassPopupMenu(treeTable)));
	}

	private void initActionListeners()
	{
		addFocusListener(new PaneFocusAdapter());
		addComponentListener(new PaneComponentAdapter());
		removeButton.addActionListener(new RemoveClassButtonActionListener());
		adjXP.addActionListener(new AdjustXPButtonActionListener());
		addButton.addActionListener(new AddClassButtonActionListener());
		viewComboBox.addActionListener(new ViewComboBoxActionListener());
		viewSelectComboBox
			.addActionListener(new ViewSelectComboBoxActionListener());
		if (hpButton != null)
		{
			hpButton.addActionListener(new HpButtonActionListener());
		}
		experience.setInputVerifier(new ExperienceBoxInputVerifier());
		availableTable.getSelectionModel().addListSelectionListener(
			new AvailableListSelectionListener());
		selectedTable.getSelectionModel().addListSelectionListener(
			new SelectedListSelectionListener());
		availableTable.addMouseListener(new JTreeTableMouseAdapter(
			availableTable, new AvailableClickHandler(), false));
		selectedTable.addMouseListener(new JTreeTableMouseAdapter(
			selectedTable, new SelectedClickHandler(), false));

		asplit.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
			new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent evt)
				{
					saveDividerLocations();
				}
			});
		bsplit.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
			new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent evt)
				{
					saveDividerLocations();
				}
			});
		splitPane.addPropertyChangeListener(
			JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent evt)
				{
					saveDividerLocations();
				}
			});
		
		textQFilter.getDocument().addDocumentListener(new DocumentListener()
		{
			public void changedUpdate(DocumentEvent evt)
			{
				setQFilter();
			}

			public void insertUpdate(DocumentEvent evt)
			{
				setQFilter();
			}

			public void removeUpdate(DocumentEvent evt)
			{
				setQFilter();
			}
		});
		clearQFilterButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				clearQFilter();
			}
		});
		FilterFactory.restoreFilterSettings(this);
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		readyForRefresh = true;

		typeRoot = new PObjectNode();
		sourceRoot = new PObjectNode();

		List<String> typeList = new ArrayList<String>();
		List<String> sourceList = new ArrayList<String>();

		for (PCClass aClass : Globals.getContext().ref.getConstructedCDOMObjects(PCClass.class))
		{
			for (Type type : aClass.getTrueTypeList(false))
			{
				String aType = type.toString();
				if (!typeList.contains(aType))
				{
					typeList.add(aType);
				}
			}
			final String aString = SourceFormat.getFormattedString(aClass,
					SourceFormat.LONG, false);
			if (aString.length() == 0)
			{
				Logging.errorPrintLocalised("in_icPCClassHasNoSourceLongEntry",
						aClass.getDisplayName());
			}
			else if (!sourceList.contains(aString))
			{
				sourceList.add(aString);
			}
		}

		Collections.sort(typeList);
		if (!typeList.contains(LanguageBundle.getString("in_other")))
		{
			typeList.add(LanguageBundle.getString("in_other"));
		}
		PObjectNode[] pTypes = new PObjectNode[typeList.size()];
		for (int i = 0; i < pTypes.length; i++)
		{
			pTypes[i] = new PObjectNode();
			pTypes[i].setItem(typeList.get(i));
			pTypes[i].setParent(typeRoot);
		}
		typeRoot.setChildren(pTypes);

		Collections.sort(sourceList);
		PObjectNode[] pSources = new PObjectNode[sourceList.size()];
		for (int i = 0; i < pSources.length; i++)
		{
			final String aString = sourceList.get(i).toString();
			if (aString != null)
			{
				pSources[i] = new PObjectNode();
				pSources[i].setItem(aString);
				pSources[i].setParent(sourceRoot);
			}
		}
		sourceRoot.setChildren(pSources);

		int iView = SettingsHandler.getClassTab_AvailableListMode();

		if ((iView >= GuiConstants.INFOCLASS_VIEW_NAME)
			&& (iView <= GuiConstants.INFOCLASS_VIEW_SOURCE_NAME))
		{
			viewMode = iView;
		}

		SettingsHandler.setClassTab_AvailableListMode(viewMode);
		viewComboBox.addItem(LanguageBundle.getString("in_nameLabel"));
		viewComboBox.addItem(LanguageBundle.getString("in_typeName"));
		viewComboBox.addItem(LanguageBundle.getString("in_sourceName"));
		Utility.setDescription(viewComboBox, LanguageBundle
			.getString("in_clChangCl"));
		viewComboBox.setSelectedIndex(viewMode); // must be done before createModels call

		iView = SettingsHandler.getClassTab_SelectedListMode();

		if ((iView >= GuiConstants.INFOCLASS_VIEW_NAME)
			&& (iView <= GuiConstants.INFOCLASS_VIEW_SOURCE_NAME))
		{
			viewSelectMode = iView;
		}

		SettingsHandler.setClassTab_SelectedListMode(viewSelectMode);
		viewSelectComboBox.addItem(LanguageBundle.getString("in_nameLabel"));
		viewSelectComboBox.addItem(LanguageBundle.getString("in_typeName"));
		viewSelectComboBox.addItem(LanguageBundle.getString("in_sourceName"));
		Utility.setDescription(viewSelectComboBox, LanguageBundle
			.getString("in_clChangCl"));
		viewSelectComboBox.setSelectedIndex(viewSelectMode); // must be done before createModels call

		createModels();
		createTreeTables();

		//  Base Panel, Contains left and right panels
		center.setLayout(new BorderLayout());

		buildTop();

		JPanel bLeftPane = new JPanel(new BorderLayout());
		JPanel bRightPane = new JPanel();

		//  Bottom Left Pane - Class Info
		TitledBorder title1 =
				BorderFactory.createTitledBorder(LanguageBundle
					.getString("in_clInfo"));
		title1.setTitleJustification(TitledBorder.CENTER);
		JScrollPane infoScroll = new JScrollPane(infoLabel);
		infoScroll.setBorder(title1);
		infoLabel.setBackground(bLeftPane.getBackground());
		bLeftPane.add(infoScroll, BorderLayout.CENTER);
		Utility.setDescription(bLeftPane, LanguageBundle
			.getString("in_infoScrollTip"));

		//  Bottom Right Pane - Character Info
		initSEPanel(bRightPane);

		//  Split the Pane
		asplit =
				new FlippingSplitPane(JSplitPane.HORIZONTAL_SPLIT, bLeftPane,
					bRightPane);
		asplit.setOneTouchExpandable(true);
		asplit.setDividerSize(10);

		//  Add the Bottom Pane
		JPanel botPane = new JPanel();
		botPane.setLayout(new BorderLayout());
		botPane.add(asplit, BorderLayout.CENTER);
		bsplit =
				new FlippingSplitPane(JSplitPane.VERTICAL_SPLIT, center,
					botPane);
		bsplit.setOneTouchExpandable(true);
		bsplit.setDividerSize(10);

		this.setLayout(new BorderLayout());
		this.add(bsplit, BorderLayout.CENTER);

		// add the sorter so that clicking on the TableHeader actually does something
		availableSort =
				new JTreeTableSorter(availableTable,
					(PObjectNode) availableModel.getRoot(), availableModel);
	}

	private void buildTop()
	{
		//GridBagLayout gridbag = new GridBagLayout();
		//GridBagConstraints c = new GridBagConstraints();
		JPanel leftPane = new JPanel(new BorderLayout());
		JPanel rightPane = new JPanel(new BorderLayout());
		splitPane =
				new FlippingSplitPane(splitOrientation, leftPane, rightPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerSize(10);
		center.add(splitPane, BorderLayout.CENTER);

		//  Top Left Pane - Available Classes
		leftPane.add(InfoTabUtils.createFilterPane(avaLabel, viewComboBox,
			lblQFilter, textQFilter, clearQFilterButton), BorderLayout.NORTH);

		JScrollPane scrollPane =
				new JScrollPane(availableTable,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JButton columnButton = new JButton();
		scrollPane.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER,
			columnButton);
		columnButton.setText("^");
		new TableColumnManager(availableTable, columnButton, availableModel);
		leftPane.add(scrollPane, BorderLayout.CENTER);

		JPanel leftBottomPanel =
				new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 1));
		addButton = new JButton(IconUtilitities.getImageIcon("Forward16.gif"));
		Utility.setDescription(addButton, LanguageBundle
			.getString("in_clAddTip"));
		addButton.setEnabled(false);
		leftBottomPanel.add(addButton);
		leftPane.add(leftBottomPanel, BorderLayout.SOUTH);

		//  Top Right Pane - Selected Classes
		JPanel rightTopPanel =
				new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 1));
		rightTopPanel.add(selLabel);
		rightTopPanel.add(viewSelectComboBox);
		rightPane.add(rightTopPanel, BorderLayout.NORTH);

		selectedTable.getColumnModel().getColumn(1).setPreferredWidth(15);
		JScrollPane scrollPane2 =
				new JScrollPane(selectedTable,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		rightPane.add(scrollPane2, BorderLayout.CENTER);
		JButton columnButton2 = new JButton();
		scrollPane2.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER,
			columnButton2);
		columnButton2.setText("^");
		new TableColumnManager(selectedTable, columnButton2, selectedModel);

		// Centre the level column
		int index = selectedTable.convertColumnIndexToView(1);
		if (index > -1)
		{
			selectedTable.setColAlign(index, SwingConstants.CENTER);
		}

		JPanel rightBottomPanel =
				new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 1));
		removeButton = new JButton(IconUtilitities.getImageIcon("Back16.gif"));
		Utility.setDescription(removeButton, LanguageBundle
			.getString("in_clRemoveTip"));
		removeButton.setEnabled(false);
		rightBottomPanel.add(removeButton);
		rightPane.add(rightBottomPanel, BorderLayout.SOUTH);
	}

	private void saveDividerLocations()
	{
		if (!hasBeenSized)
		{
			return;
		}

		int s = splitPane.getDividerLocation();

		if (s > 0)
		{
			SettingsHandler.setPCGenOption("InfoClasses.splitPane", s); //$NON-NLS-1$
		}

		s = asplit.getDividerLocation();

		if (s > 0)
		{
			SettingsHandler.setPCGenOption("InfoClasses.asplit", s); //$NON-NLS-1$
		}

		s = bsplit.getDividerLocation();

		if (s > 0)
		{
			SettingsHandler.setPCGenOption("InfoClasses.bsplit", s); //$NON-NLS-1$
		}
	}

	private boolean maybeSetExperience(int xp)
	{
		// Skip this processing if we have already been through
		if (xp == pc.getXP())
		{
			return true;
		}

		pc.setXP(xp);

		if (xp >= pc.minXPForNextECL())
		{
			ShowMessageDelegate.showMessageDialog(SettingsHandler.getGame()
				.getLevelUpMessage(), Constants.APPLICATION_NAME,
				MessageType.INFORMATION);
		}

		return true;
	}

	private void experienceFocusLost()
	{
		maybeSetExperience(experience.getValue());
	}

	/**
	 * Populate the lower right-hand panel
	 * @param sep
	 */
	private void initSEPanel(JPanel sep)
	{
		GridBagConstraints gbc;
		String aString;

		sep.setLayout(new GridBagLayout());
		pnlWest.setLayout(new GridBagLayout());

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		if (Globals.getGameModeHPFormula().length() == 0)
		{
			gbc.gridy = 0;
			gbc.gridwidth = 2;
			gbc.insets = new Insets(0, 5, 0, 5);

			hpButton = new JButton();
			hpButton.setText(Globals.getGameModeHPAbbrev());
			hpButton.setAlignmentY(0.0F);
			hpButton.setHorizontalAlignment(SwingConstants.LEFT);
			pnlHP.add(hpButton);

			lblHP.setHorizontalAlignment(SwingConstants.TRAILING);
			pnlHP.add(lblHP);

			sep.add(pnlHP, gbc);
		}
		else
		{
			pnlHP.setLayout(new BorderLayout(5, 5));

			lblHPName = new JLabel(Globals.getGameModeHitPointText());
			pnlHP.add(lblHPName, BorderLayout.WEST);
			pnlHP.add(lblHP, BorderLayout.EAST);

			pnlWest.add(pnlHP, gbc);
		}

		if (!Globals.getGameModeHasPointPool())
		{
			pnlFeats = new JPanel();
			pnlFeats.setLayout(new BorderLayout(5, 5));

			lblFeats = new JLabel();
			lblFeats.setText(LanguageBundle.getString("in_feats"));
			pnlFeats.add(lblFeats, BorderLayout.WEST);

			featCount.setText("0");
			pnlFeats.add(featCount, BorderLayout.EAST);

			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.anchor = GridBagConstraints.NORTHWEST;
			gbc.weightx = 1.0;
			pnlWest.add(pnlFeats, gbc);
		}
		pnlSkills.setLayout(new BorderLayout(5, 5));

		if (Globals.getGameModeHasPointPool())
		{
			lblSkills.setText(Globals.getGameModePointPoolName());
		}
		else
		{
			lblSkills.setText(LanguageBundle.getString("in_skills"));
		}
		pnlSkills.add(lblSkills, BorderLayout.WEST);

		skillCount.setText("0");
		pnlSkills.add(skillCount, BorderLayout.EAST);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		pnlWest.add(pnlSkills, gbc);

		pnlBAB.setLayout(new BorderLayout(5, 5));

		aString = SettingsHandler.getGame().getBabAbbrev();
		if (aString == null)
		{
			aString = "BAB";
		}
		lblBAB.setText(aString);
		pnlBAB.add(lblBAB, BorderLayout.WEST);

		lBAB.setText("0");
		pnlBAB.add(lBAB, BorderLayout.EAST);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		pnlWest.add(pnlBAB, gbc);

		pnlDefense.setLayout(new BorderLayout(5, 5));
		aString = Globals.getGameModeACText();
		lblDefense.setText(aString);
		pnlDefense.add(lblDefense, BorderLayout.WEST);
		pnlDefense.setVisible(aString.length() != 0);
		lDefense.setText("0");
		pnlDefense.add(lDefense, BorderLayout.EAST);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		pnlWest.add(pnlDefense, gbc);

		pnlAltHP.setLayout(new BorderLayout(5, 5));
		aString = Globals.getGameModeAltHPText();
		lblAltHP.setText(aString);
		pnlAltHP.add(lblAltHP, BorderLayout.WEST);
		pnlAltHP.setVisible(aString.length() != 0);
		pnlAltHP.add(lAHP, BorderLayout.EAST);
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		pnlWest.add(pnlAltHP, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		pnlWest.add(pnlFillerWest, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridheight = 2;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 6, 0, 6);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		sep.add(pnlWest, gbc);

		buildEastPanel();

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridheight = 2;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 5, 0, 5);
		gbc.anchor = GridBagConstraints.EAST;
		gbc.weightx = 1.0;
		sep.add(pnlEast, gbc);

		//pnlXP.setLayout(new BorderLayout(5, 5));
		pnlXP.setLayout(new GridBagLayout());

		lblExperience.setText(LanguageBundle.getString("in_experience"));

		//lblExperience.setPreferredSize(new Dimension(64, 16));
		//pnlXP.add(lblExperience, BorderLayout.WEST);
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		pnlXP.add(lblExperience, gbc);

		experience.setHorizontalAlignment(SwingConstants.TRAILING);
		experience.setText("0");

		//experience.setPreferredSize(new Dimension(11, 20));
		//pnlXP.add(experience, BorderLayout.CENTER);
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 3, 0, 3);
		gbc.weightx = 1.0;
		pnlXP.add(experience, gbc);

		pnlXP.add(adjXP, new GridBagConstraints());

		lblNextLevel.setText(LanguageBundle.getString("in_icNextLevel"));
		gbc = new GridBagConstraints();
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		pnlXP.add(lblNextLevel, gbc);

		txtNextLevel.setHorizontalAlignment(SwingConstants.TRAILING);
		txtNextLevel.setBorder(BorderFactory.createEtchedBorder(
			Color.lightGray, Color.lightGray));
		txtNextLevel.setBackground(Color.lightGray);
		txtNextLevel.setEditable(false);
		txtNextLevel.setValue(0);
		gbc = new GridBagConstraints();
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 3, 0, 3);
		gbc.weightx = 1.0;
		pnlXP.add(txtNextLevel, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 5, 0, 5);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		sep.add(pnlXP, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		sep.add(pnlFillerSouth, gbc);
	}

	/**
	 * Updates the Available table
	 **/
	private void updateAvailableModel()
	{
		List<String> pathList = availableTable.getExpandedPaths();
		createAvailableModel();
		availableTable.updateUI();
		availableTable.expandPathList(pathList);
	}

	// This recalculates the states of everything based upon the
	// currently selected character.
	private void updateCharacterInfo()
	{
		if ((pc == null) || !needsUpdate)
		{
			return;
		}

		if (hpButton != null)
		{
			hpButton.setText(Globals.getGameModeHPAbbrev());
		}
		else if (lblHPName != null)
		{
			lblHPName.setText(Globals.getGameModeHitPointText());
		}

		String aString = Globals.getGameModeACText();

		if (Globals.getGameModeShowClassDefense())
		{
			lblDefense.setText(aString + " (" + LanguageBundle.getString("in_class") + ")");
			pnlDefense.setVisible(true);
		}
		else
		{
			pnlDefense.setVisible(false);
		}

		aString = Globals.getGameModeAltHPText();

		if (aString.length() != 0)
		{
			lblAltHP.setText(aString);
			pnlAltHP.setVisible(true);
		}
		else
		{
			pnlAltHP.setVisible(false);
		}

		aString = Globals.getGameModeVariableDisplayText();

		if (aString.length() != 0)
		{
			lblVariableDisplay.setText(aString);
			pnlVariableDisplay.setVisible(true);
		}
		else
		{
			pnlVariableDisplay.setVisible(false);
		}

		aString = Globals.getGameModeVariableDisplay2Text();

		if (aString.length() != 0)
		{
			lblVariableDisplay2.setText(aString);
			pnlVariableDisplay2.setVisible(true);
		}
		else
		{
			pnlVariableDisplay2.setVisible(false);
		}

		aString = Globals.getGameModeVariableDisplay3Text();

		if (aString.length() != 0)
		{
			lblVariableDisplay3.setText(aString);
			pnlVariableDisplay3.setVisible(true);
		}
		else
		{
			pnlVariableDisplay3.setVisible(false);
		}

		updateAvailableModel();
		updateSelectedModel();

		//Calculate the aggregate feat list
		pc.aggregateFeatList();
		updateHP();
		featCount.setText(Double.toString(pc.getRemainingFeatPoolPoints()));
		skillCount.setText(Integer.toString(pc.getSkillPoints()));
		lBAB.setText(Integer.toString(pc.baseAttackBonus()));
		lDefense.setText(Integer.toString(pc.classAC()));

		updateChecks();

		lAHP.setText(Integer.toString(pc.altHP()));
		updateXP(pc); // race changes effective XP

		int mytempvar =
				(int) pc.getTotalBonusTo("VAR", Globals
					.getGameModeVariableDisplayName());
		lVariableDisplay.setText(Integer.toString(mytempvar));

		int mytempvar2 =
				(int) pc.getTotalBonusTo("VAR", Globals
					.getGameModeVariableDisplay2Name());
		lVariableDisplay2.setText(Integer.toString(mytempvar2));

		int mytempvar3 =
				(int) pc.getTotalBonusTo("VAR", Globals
					.getGameModeVariableDisplay3Name());
		lVariableDisplay3.setText(Integer.toString(mytempvar3));

		needsUpdate = false;
	}

	private void updateChecks()
	{
		final List<PCCheck> checkList = Globals.getContext().ref
				.getOrderSortedCDOMObjects(PCCheck.class);

		if ((lCheck == null) || (checkList.size() != lCheck.length))
		{
			buildEastPanel();
		}

		int i = 0;
		for (PCCheck check : checkList)
		{
			lblCheck[i].setText(check.getDisplayName());
			lCheck[i].setText(Delta.toString(pc.getTotalCheck(check)));
			i++;
		}
	}

	/**
	 * Updates the Selected table
	 **/
	private void updateSelectedModel()
	{
		List<String> pathList = selectedTable.getExpandedPaths();
		createSelectedModel();
		selectedTable.updateUI();
		selectedTable.expandPathList(pathList);
	}

	private void updateXP(PlayerCharacter currentPC)
	{
		if (currentPC == null)
		{
			return;
		}

		experience.setValue(currentPC.getXP());

		txtNextLevel.setValue(currentPC.minXPForNextECL());
	}

	private void viewComboBoxActionPerformed()
	{
		final int index = viewComboBox.getSelectedIndex();

		if (index != viewMode)
		{
			viewMode = index;
			SettingsHandler.setClassTab_AvailableListMode(viewMode);
			updateAvailableModel();
		}
	}

	private void viewSelectComboBoxActionPerformed()
	{
		final int index = viewSelectComboBox.getSelectedIndex();

		if (index != viewSelectMode)
		{
			viewSelectMode = index;
			SettingsHandler.setClassTab_SelectedListMode(viewSelectMode);
			updateSelectedModel();
		}
	}

	private void clearQFilter()
	{
		availableModel.clearQFilter();
		if (saveViewMode != null)
		{
			viewMode = saveViewMode.intValue();
			saveViewMode = null;
		}
		textQFilter.setText("");
		availableModel.resetModel(viewMode, true);
		clearQFilterButton.setEnabled(false);
		viewComboBox.setEnabled(true);
		forceRefresh();
	}

	private void setQFilter()
	{
		String aString = textQFilter.getText();

		if (aString.length() == 0)
		{
			clearQFilter();
			return;
		}
		availableModel.setQFilter(aString);

		if (saveViewMode == null)
		{
			saveViewMode = Integer.valueOf(viewMode);
		}
		viewMode = GuiConstants.INFOCLASS_VIEW_NAME;
		availableModel.resetModel(viewMode, true);
		clearQFilterButton.setEnabled(true);
		viewComboBox.setEnabled(false);
		forceRefresh();
	}

	private static String getBabTitle()
	{
		String bab = SettingsHandler.getGame().getBabAbbrev();
		if (bab == null)
		{
			bab = "BAB";
		}
		return bab;
	}

	/**
	 * The basic idea of the TreeTableModel is that there is a single
	 * <code>root</code> object.  This root object has a null
	 * <code>parent</code>.  All other objects have a parent which
	 * points to a non-null object.  parent objects contain a list of
	 * <code>children</code>, which are all the objects that point
	 * to it as their parent.
	 * objects (or <code>nodes</code>) which have 0 children
	 * are leafs (the end of that linked list).
	 * nodes which have at least 1 child are not leafs.
	 * Leafs are like files and non-leafs are like directories.
	 **/
	private final class ClassModel extends AbstractTreeTableModel implements
			TableColumnManagerModel
	{
		// Types of the columns.
		private int modelType = 0; // availableModel=0,selectedModel=1
		private List<Boolean> displayList = null;
		private static final int COL_NAME = 0;
		private static final int COL_REQ = 1;
		private static final int COL_LEVEL = 2;
		private static final int COL_TYPE = 3;
		private static final int COL_BAB = 4;
		private static final int COL_HD = 5;
		private static final int COL_SPELLTYPE = 6;
		private static final int COL_SPELLSTAT = 7;
		private static final int COL_SRC = 8;

		private final String[] colNameList =
				{LanguageBundle.getString("in_nameLabel"),
					LanguageBundle.getString("in_preReqs"),
					LanguageBundle.getString("in_level"),
					LanguageBundle.getString("in_type"), getBabTitle(),
					LanguageBundle.getString("in_hdLabel"),
					LanguageBundle.getString("in_spellType"),
					LanguageBundle.getString("in_baseStat"),
					LanguageBundle.getString("in_sourceLabel")};

		private final int[] colDefaultWidth =
				{200, 100, 35, 70, 35, 40, 60, 60, 100};

		/**
		 * Creates a ClassModel
		 * @param mode
		 * @param available
		 **/
		private ClassModel(int mode, boolean available)
		{
			super(null);

			if (!available)
			{
				modelType = 1;
			}

			resetModel(mode, available);
			displayList = new ArrayList<Boolean>();
			displayList.add(Boolean.TRUE);
			if (modelType == 0)
			{
				displayList.add(Boolean.valueOf(getColumnViewOption(modelType
					+ "." + colNameList[1], true)));
				displayList.add(Boolean.valueOf(getColumnViewOption(modelType
					+ "." + colNameList[2], false)));
			}
			else
			{
				displayList.add(Boolean.valueOf(getColumnViewOption(modelType
					+ "." + colNameList[1], false)));
				displayList.add(Boolean.valueOf(getColumnViewOption(modelType
					+ "." + colNameList[2], true)));
			}
			displayList.add(Boolean.valueOf(getColumnViewOption(modelType + "."
				+ colNameList[3], false)));
			displayList.add(Boolean.valueOf(getColumnViewOption(modelType + "."
				+ colNameList[4], false)));
			displayList.add(Boolean.valueOf(getColumnViewOption(modelType + "."
				+ colNameList[5], false)));
			displayList.add(Boolean.valueOf(getColumnViewOption(modelType + "."
				+ colNameList[6], false)));
			displayList.add(Boolean.valueOf(getColumnViewOption(modelType + "."
				+ colNameList[7], false)));
			displayList.add(Boolean.valueOf(getColumnViewOption(modelType + "."
				+ colNameList[8], true)));
		}

		public boolean isCellEditable(Object node, int column)
		{
			return (column == COL_NAME);
		}

		/**
		 * Returns Class for the column.
		 * @param column
		 * @return Class
		 */
		public Class<?> getColumnClass(int column)
		{
			switch (column)
			{
				case COL_NAME:
					return TreeTableModel.class;

				case COL_REQ:
					return String.class;

				case COL_LEVEL:
					return Integer.class;

				case COL_TYPE:
				case COL_BAB:
				case COL_HD:
				case COL_SPELLTYPE:
				case COL_SPELLSTAT:
				case COL_SRC:
					return String.class;

				default:
					Logging.errorPrint(LanguageBundle.getString("in_clICEr4")
						+ " " + column + " "
						+ LanguageBundle.getString("in_clICEr2"));

					break;
			}

			return String.class;
		}

		/* The JTreeTableNode interface. */

		/**
		 * Returns int number of columns.
		 * @return colNameList.length
		 */
		public int getColumnCount()
		{
			return colNameList.length;
		}

		/**
		 * Returns String name of a column.
		 * @param column
		 * @return column name
		 */
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
		 * Returns Object value of the column.
		 * @param node
		 * @param column
		 * @return value
		 */
		public Object getValueAt(Object node, int column)
		{
			final PObjectNode fn = (PObjectNode) node;
			PCClass pcclass = null;

			if ((fn != null) && (fn.getItem() instanceof PCClass))
			{
				pcclass = (PCClass) fn.getItem();
			}

			final Integer c = Integer.valueOf(0);
			String retString = "";

			switch (column)
			{
				case COL_NAME: // Name

					if (fn != null)
					{
						return fn.toString();
					}
					Logging.errorPrint(LanguageBundle.getString("in_clICEr5"));
					return "";

				case COL_REQ:
					if (modelType == 0)
					{
						if (pcclass != null)
						{
							retString = PrerequisiteUtilities.preReqHTMLStringsForList(pc, null,
							pcclass.getPrerequisiteList(), true);
						}
					}
					return retString;

				case COL_LEVEL:
					if (pcclass != null)
					{
						return Integer.valueOf(pc.getLevel(pcclass));
					}

					return c;

				case COL_TYPE:
					if (pcclass != null)
					{
						retString = pcclass.getType();
					}
					return retString;

				case COL_BAB:
					if (pcclass != null)
					{
						//						retString = pcclass.getAttackBonusType();
					}
					return retString;

				case COL_HD:
					if (pcclass != null)
					{
						int hitDie = pcclass.getSafe(ObjectKey.LEVEL_HITDIE).getDie();
						retString = "1d" + hitDie;
					}
					return retString;

				case COL_SPELLTYPE:
					if (pcclass != null)
					{
						retString = pcclass.getSpellType();
					}
					return retString;

				case COL_SPELLSTAT:
					if (pcclass != null)
					{
						retString = pcclass.getSpellBaseStat();
					}
					return retString;

				case COL_SRC: // Source or Qty

					if (fn != null)
					{
						retString = fn.getSource();
					}
					return retString;

				case -1:

					if (fn != null)
					{
						return fn.getItem();
					}
					Logging.errorPrint(LanguageBundle.getString("in_clICEr5"));
					return null;

				default:
					Logging.errorPrint(LanguageBundle.getString("in_clICEr6")
						+ " " + column + " "
						+ LanguageBundle.getString("in_ICEr2"));

					break;
			}

			return null;
		}

		// There must be a root object, though it can be hidden
		private void setRoot(PObjectNode aNode)
		{
			super.setRoot(aNode);
		}

		private void addSubClassesTo(PObjectNode aFN, PCClass aClass)
		{
			List<SubClass> subClassList = aClass.getListFor(ListKey.SUB_CLASS);
			if (subClassList != null)
			{
				for (SubClass sClass : subClassList)
				{
					PObjectNode aSN = new PObjectNode();
					aSN.setParent(aFN);
					aSN.setItem(sClass);
					PrereqHandler.passesAll(sClass.getPrerequisiteList(), pc, sClass);
					aFN.addChild(aSN);
				}
			}
		}

		/**
		 * This assumes the ClassModel exists
		 * but needs to be repopulated
		 * @param mode
		 * @param available
		 **/
		private void resetModel(int mode, boolean available)
		{
			Collection<PCClass> classList;

			if (available)
			{
				classList = Globals.getContext().ref.getConstructedCDOMObjects(PCClass.class);
			}
			else
			{
				classList = pc.getClassSet();
			}

			switch (mode)
			{
				case GuiConstants.INFOCLASS_VIEW_NAME: // Name
					setRoot(new PObjectNode()); // just need a blank one
					String qFilter = this.getQFilter();

					for (PCClass aClass : classList)
					{
						// in the availableTable, if filtering out unqualified items
						// ignore any class the PC doesn't qualify for
						if (!shouldDisplayThis(aClass))
						{
							continue;
						}

						if (qFilter == null
							|| (aClass.getDisplayName().toLowerCase().indexOf(
								qFilter) >= 0 || aClass.getType().toLowerCase()
								.indexOf(qFilter) >= 0))
						{
							PObjectNode aFN = new PObjectNode();
							aFN.setParent((PObjectNode) super.getRoot());
							aFN.setItem(aClass);
							PrereqHandler.passesAll(aClass.getPrerequisiteList(), pc,
								aClass);
							((PObjectNode) super.getRoot()).addChild(aFN);

							if (available)
							{
								addSubClassesTo(aFN, aClass);
							}
						}
					}

					break;

				case GuiConstants.INFOCLASS_VIEW_TYPE_NAME: // type/name
					setRoot(typeRoot.clone());

					for (PCClass aClass : classList)
					{
						// in the availableTable, if filtering out unqualified items
						// ignore any class the PC doesn't qualify for
						if (!shouldDisplayThis(aClass))
						{
							continue;
						}

						PObjectNode rootAsPObjectNode =
								(PObjectNode) super.getRoot();
						boolean added = false;

						for (int i = 0; i < rootAsPObjectNode.getChildCount(); i++)
						{
							if ((!added && (i == (rootAsPObjectNode
								.getChildCount() - 1)))
								|| aClass
									.isType((rootAsPObjectNode.getChildren()
										.get(i)).getItem().toString()))
							{
								PObjectNode aFN = new PObjectNode();
								aFN.setParent(rootAsPObjectNode.getChild(i));
								aFN.setItem(aClass);
								PrereqHandler.passesAll(aClass.getPrerequisiteList(),
									pc, aClass);
								rootAsPObjectNode.getChild(i).addChild(aFN);
								added = true;

								if (available)
								{
									addSubClassesTo(aFN, aClass);
								}
							}
						}
					}

					break;

				case GuiConstants.INFOCLASS_VIEW_SOURCE_NAME: // source/name
					setRoot(sourceRoot.clone());

					for (PCClass aClass : classList)
					{
						// in the availableTable, if filtering out unqualified items
						// ignore any class the PC doesn't qualify for
						if (!shouldDisplayThis(aClass))
						{
							continue;
						}

						PObjectNode rootAsPObjectNode =
								(PObjectNode) super.getRoot();
						boolean added = false;

						for (int i = 0; i < rootAsPObjectNode.getChildCount(); i++)
						{
							final String sourceString = SourceFormat
								.getFormattedString(aClass, SourceFormat.LONG,
										false);
							if (sourceString.length() == 0)
							{
								Logging.errorPrintLocalised("in_icPCClassHasNoSourceLongEntry",
                                                                        aClass.getDisplayName());
							}
							else if ((!added && (i == (rootAsPObjectNode
								.getChildCount() - 1)))
								|| sourceString
									.equals((rootAsPObjectNode.getChildren()
										.get(i)).getItem().toString()))
							{
								PObjectNode aFN = new PObjectNode();
								aFN.setParent(rootAsPObjectNode.getChild(i));
								aFN.setItem(aClass);
								PrereqHandler.passesAll(aClass.getPrerequisiteList(),
									pc, aClass);
								rootAsPObjectNode.getChild(i).addChild(aFN);
								added = true;

								if (available)
								{
									addSubClassesTo(aFN, aClass);
								}
							}
						}
					}

					break;

				default:
					Logging.errorPrint(LanguageBundle.getString("in_clICEr1")
						+ " " + mode + " "
						+ LanguageBundle.getString("in_clICEr2"));

					break;
			}

			PObjectNode rootAsPObjectNode = (PObjectNode) super.getRoot();

			if (rootAsPObjectNode.getChildCount() > 0)
			{
				fireTreeNodesChanged(super.getRoot(), new TreePath(super
					.getRoot()));
			}
		}

		/**
		 * return a boolean to indicate if the item should be included in the list.
		 * Only Weapon, Armor and Shield type items should be checked for proficiency.
		 * @param aClass
		 * @return true if it should be displayed
		 */
		private boolean shouldDisplayThis(final PCClass aClass)
		{
			if (SettingsHandler.hideMonsterClasses() && aClass.isMonster())
			{
				return false;
			}

			return (modelType == 1)
				|| (aClass.getSafe(ObjectKey.VISIBILITY).equals(Visibility.DEFAULT) && accept(
					pc, aClass));
		}

		public List<String> getMColumnList()
		{
			List<String> retList = new ArrayList<String>();
			for (int i = 1; i < colNameList.length; i++)
			{
				retList.add(colNameList[i]);
			}
			return retList;
		}

		public boolean isMColumnDisplayed(int col)
		{
			return (displayList.get(col)).booleanValue();
		}

		public void setMColumnDisplayed(int col, boolean disp)
		{
			setColumnViewOption(modelType + "." + colNameList[col], disp);
			displayList.set(col, Boolean.valueOf(disp));
		}

		public int getMColumnOffset()
		{
			return 1;
		}

		public int getMColumnDefaultWidth(int col)
		{
			return SettingsHandler.getPCGenOption("InfoClasses.sizecol."
				+ colNameList[col], colDefaultWidth[col]);
		}

		public void setMColumnDefaultWidth(int col, int width)
		{
			SettingsHandler.setPCGenOption("InfoClasses.sizecol."
				+ colNameList[col], width);
		}

		private boolean getColumnViewOption(String colName, boolean defaultVal)
		{
			return SettingsHandler.getPCGenOption("InfoClasses.viewcol."
				+ colName, defaultVal);
		}

		private void setColumnViewOption(String colName, boolean val)
		{
			SettingsHandler.setPCGenOption("InfoClasses.viewcol." + colName,
				val);
		}

		public void resetMColumn(int col, TableColumn column)
		{
			// TODO Auto-generated method stub

		}
	}

	private class ClassPopupListener extends MouseAdapter
	{
		private ClassPopupMenu menu;
		private JTree tree;

		ClassPopupListener(JTreeTable treeTable, ClassPopupMenu aMenu)
		{
			tree = treeTable.getTree();
			menu = aMenu;

			KeyListener myKeyListener = new KeyListener()
			{
				public void keyTyped(KeyEvent e)
				{
					dispatchEvent(e);
				}

				public void keyPressed(KeyEvent e)
				{
					final int keyCode = e.getKeyCode();

					if (keyCode != KeyEvent.VK_UNDEFINED)
					{
						final KeyStroke keyStroke =
								KeyStroke.getKeyStrokeForEvent(e);

						for (int i = 0; i < menu.getComponentCount(); i++)
						{
							final Object obj = menu.getComponent(i);
							if (obj instanceof JMenuItem)
							{
								KeyStroke ks =
										((JMenuItem) obj).getAccelerator();

								if ((ks != null) && keyStroke.equals(ks))
								{
									selPath = tree.getSelectionPath();
									((JMenuItem) obj).doClick(2);

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
				selPath =
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

	private class ClassPopupMenu extends JPopupMenu
	{
		static final long serialVersionUID = 9141488354194857537L;
		private String lastSearch = "";

		ClassPopupMenu(JTreeTable treeTable)
		{
			if (treeTable == availableTable)
			{
				/*
				 * jikes says:
				 *   "Ambiguous reference to member 'add' inherited from
				 *    type 'javax/swing/JPopupMenu' but also declared or
				 *    inherited in the enclosing type 'pcgen/gui/InfoInventory'.
				 *    Explicit qualification is required."
				 * Well, let's do what jikes wants us to do ;-)
				 *
				 * author: Thomas Behr 08-02-02
				 *
				 * changed accelerator from "control PLUS" to "control EQUALS" as cannot
				 * get "control PLUS" to function on standard US keyboard with Windows 98
				 *
				 */
				ClassPopupMenu.this.add(createAddMenuItem(LanguageBundle
					.getString("in_add1"), "shortcut EQUALS"));
				this.addSeparator();
				ClassPopupMenu.this.add(Utility.createMenuItem(LanguageBundle.getString("in_icFindItem"),
					new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							lastSearch = availableTable.searchTree(lastSearch);
						}
					}, "searchItem", (char) 0, "shortcut F", LanguageBundle.getString("in_icFindItem"), null,
					true));
			}

			else
			// selectedTable
			{
				/*
				 * jikes says:
				 *   "Ambiguous reference to member 'add' inherited from
				 *    type 'javax/swing/JPopupMenu' but also declared or
				 *    inherited in the enclosing type 'pcgen/gui/InfoInventory'.
				 *    Explicit qualification is required."
				 * Well, let's do what jikes wants us to do ;-)
				 *
				 * author: Thomas Behr 08-02-02
				 *
				 * changed accelerator from "control PLUS" to "control EQUALS" as cannot
				 * get "control PLUS" to function on standard US keyboard with Windows 98
				 */
				ClassPopupMenu.this.add(createAddMenuItem(LanguageBundle
					.getString("in_add1"), "shortcut EQUALS"));
				ClassPopupMenu.this.add(createRemoveMenuItem(LanguageBundle
					.getString("in_remove1"), "shortcut MINUS"));
				this.addSeparator();
				ClassPopupMenu.this.add(Utility.createMenuItem(LanguageBundle.getString("in_icFindItem"),
					new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							lastSearch = selectedTable.searchTree(lastSearch);
						}
					}, "searchItem", (char) 0, "shortcut F", LanguageBundle.getString("in_icFindItem"), null,
					true));
			}
		}

		private JMenuItem createAddMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new AddClassActionListener(),
				LanguageBundle.getString("in_add1"), (char) 0, accelerator,
				LanguageBundle.getString("in_add1lvl"), "Add16.gif", true);
		}

		private JMenuItem createRemoveMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label,
				new RemoveClassActionListener(), LanguageBundle
					.getString("in_remove1"), (char) 0, accelerator,
				LanguageBundle.getString("in_remove1lvl"), "Remove16.gif",
				true);
		}

		private class AddClassActionListener extends ClassActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				addClass(1);
			}
		}

		private class ClassActionListener implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				// TODO This method currently does nothing?
			}
		}

		private class RemoveClassActionListener extends ClassActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				addClass(-1);
			}
		}
	}

	private class PaneFocusAdapter extends FocusAdapter
	{
		public void focusGained(FocusEvent evt)
		{
			refresh();
		}
	}

	private class PaneComponentAdapter extends ComponentAdapter
	{
		public void componentShown(ComponentEvent evt)
		{
			try
			{
				formComponentShown();
			}
			catch (Throwable e)
			{
				Logging
					.errorPrintLocalised(
						"in_icFailureWhileShowingClassTab",
						e);
			}
		}

		public void componentResized(ComponentEvent e)
		{
			bsplit.setDividerLocation((int) (InfoClasses.this.getSize()
				.getHeight() - 140));
			asplit.setDividerLocation((int) (InfoClasses.this.getSize()
				.getWidth() - 334));
		}
	}

	private class ViewComboBoxActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			viewComboBoxActionPerformed();
		}
	}

	private class ViewSelectComboBoxActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			viewSelectComboBoxActionPerformed();
		}
	}

	private class ExperienceBoxInputVerifier extends InputVerifier
	{
		public boolean shouldYieldFocus(JComponent input)
		{
			boolean valueOk = verify(input);
			experienceFocusLost();
			return valueOk;
		}

		public boolean verify(JComponent input)
		{
			return true;
		}
	}

	private class HpButtonActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			pcGenGUI.showHpFrame(pc);
		}
	}

	private class AddClassButtonActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			addClass(1);
		}
	}

	private class RemoveClassButtonActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			addClass(-1);
		}
	}

	private class AdjustXPButtonActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			String selectedValue =
					JOptionPane.showInputDialog(null, LanguageBundle
						.getString("in_clEnterXP"), Constants.APPLICATION_NAME,
						JOptionPane.QUESTION_MESSAGE);

			if (selectedValue != null)
			{
				try
				{
					int x = Integer.parseInt(selectedValue) + pc.getXP();

					if (maybeSetExperience(x))
					{
						experience.setValue(x);
					}

					//experienceFocusLost(null); // force xp messages as neccessary
				}
				catch (NumberFormatException e)
				{
					ShowMessageDelegate.showMessageDialog(LanguageBundle
						.getString("in_clInvalidNum"), Constants.APPLICATION_NAME,
						MessageType.ERROR);

					return;
				}
			}
		}
	}

	private class AvailableListSelectionListener implements
			ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent e)
		{
			if (!e.getValueIsAdjusting())
			{
				/////////////////////////
				// Byngl Feb 20/2002
				// fix bug with displaying incorrect class when use cursor keys to navigate the tree
				//
				//final Object temp = availableTable.getTree().getLastSelectedPathComponent();
				final int idx = getSelectedIndex(e);

				if (idx < 0)
				{
					return;
				}

				Object temp =
						availableTable.getTree().getPathForRow(idx)
							.getLastPathComponent();

				/////////////////////////
				if (temp == null)
				{
					lastClass = null;
					ShowMessageDelegate.showMessageDialog(LanguageBundle
						.getString("in_clNoClass"), Constants.APPLICATION_NAME,
						MessageType.ERROR);

					return;
				}

				PCClass aClass = null;
				PObjectNode pn = null;

				if (temp instanceof PObjectNode)
				{
					pn = (PObjectNode) temp;
					temp = ((PObjectNode) temp).getItem();

					if (temp instanceof PCClass)
					{
						aClass = (PCClass) temp;
					}
				}

				addButton.setEnabled(aClass != null);
				setInfoLabelText(aClass, pn);
			}
		}
	}

	private class SelectedListSelectionListener implements
			ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent e)
		{
			if (!e.getValueIsAdjusting())
			{
				/////////////////////////
				// Byngl Feb 20/2002
				// fix bug with displaying incorrect class when use cursor keys to navigate the tree
				//
				//final Object temp = selectedTable.getTree().getLastSelectedPathComponent();
				final int idx = getSelectedIndex(e);

				if (idx < 0)
				{
					return;
				}

				Object temp =
						selectedTable.getTree().getPathForRow(idx)
							.getLastPathComponent();

				/////////////////////////
				if (temp == null)
				{
					lastClass = null;
					infoLabel.setText();

					return;
				}

				PCClass aClass = null;
				PObjectNode pn = null;

				if (temp instanceof PObjectNode)
				{
					pn = (PObjectNode) temp;

					Object t = pn.getItem();

					if (t instanceof PCClass)
					{
						aClass = (PCClass) t;
					}
				}

				removeButton.setEnabled(aClass != null);
				setInfoLabelText(aClass, pn);
			}
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
			// We run this after the event has been processed so that
			// we don't confuse the table when we change its contents
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					addClass(1);
				}
			});
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
			// Do nothing
		}

		public void doubleClickEvent()
		{
			// We run this after the event has been processed so that
			// we don't confuse the table when we change its contents
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					addClass(-1);
				}
			});
		}

		public boolean isSelectable(Object obj)
		{
			return !(obj instanceof String);
		}
	}
}
