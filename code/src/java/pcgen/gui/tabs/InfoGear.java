/*
 * InfoGear.java
 * Copyright 2001 (C) Mario Bonassin
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
 * @author  Mario Bonassin
 * Created on April 21, 2001, 2:15 PM
 * Modified June 5, 2001 by Bryan McRoberts (merton_monk@yahoo.com)
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.tree.TreePath;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.EquipmentLocation;
import pcgen.cdom.enumeration.MapKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Equipment;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.SizeAdjustment;
import pcgen.core.analysis.OutputNameFormatting;
import pcgen.core.analysis.SizeUtilities;
import pcgen.core.character.EquipSet;
import pcgen.core.character.WieldCategory;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.PrerequisiteUtilities;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui.CharacterInfoTab;
import pcgen.gui.EQFrame;
import pcgen.gui.GuiConstants;
import pcgen.gui.PCGen_Frame1;
import pcgen.gui.TableColumnManager;
import pcgen.gui.TableColumnManagerModel;
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
import pcgen.util.BigDecimalHelper;
import pcgen.util.InputFactory;
import pcgen.util.InputInterface;
import pcgen.util.Logging;
import pcgen.system.LanguageBundle;
import pcgen.util.enumeration.Tab;

/**
 *
 * This class is responsible for drawing the equipment related window
 * including indicating what items are available, which ones are selected
 * and handling the selection/de-selection of both.
 *
 * @author  Mario Bonassin
 * @version $Revision$
 **/
public final class InfoGear extends FilterAdapterPanel implements
		CharacterInfoTab
{
	static final long serialVersionUID = -2320970658737297916L;

	private static final Tab tab = Tab.GEAR;

	private static boolean needsUpdate = true;
	private static int splitOrientation = JSplitPane.HORIZONTAL_SPLIT;
	private static int viewMode = GuiConstants.INFOINVENTORY_VIEW_TYPE_NAME; // keep track of what view mode we're in for Available
	private static int viewSelectMode = GuiConstants.INFOINVENTORY_VIEW_NAME; // keep track of what view mode we're in for Selected. defaults to "Name"
	private static Integer saveAvailableViewMode = null;
	private static Integer saveSelectedViewMode = null;
	private static final int COL_NAME = 0;
	private static final int COL_COST = 1;
	private static final int COL_WEIGHT = 2;
	private static final int COL_QTY = 3;
	private static final int COL_INDEX = 4;
	private static final int COL_SRC = 5;

	/**
	 * typeSubtypeRoot is the base structure used by both the available
	 * and selected tables; no need to generate this same list twice.
	 **/
	private static Object typeSubtypeRoot;
	private static Object typeRoot;
	private static Object allTypeRoot;
	private static Object sourceRoot;

	// Action listeners for standard buy/sell actions involving a single item
	private BuyGearActionListener buyOneListener = new BuyGearActionListener(1);
	private EQFrame eqFrame = null;
	private EquipmentModel availableModel = null; // Model for the JTreeTable.
	private EquipmentModel selectedModel = null; // Model for the JTreeTable.
	private FlippingSplitPane asplit;
	private FlippingSplitPane bsplit;
	private FlippingSplitPane splitPane;
	private JComboBoxEx cmbBuyPercent = new JComboBoxEx();
	private JComboBoxEx cmbSellPercent = new JComboBoxEx();
	private final JLabel lblAvailableQFilter = new JLabel(LanguageBundle.getString("InfoTabs.FilterLabel")); //$NON-NLS-1$
	private final JLabel lblSelectedQFilter = new JLabel(LanguageBundle.getString("InfoTabs.FilterLabel")); //$NON-NLS-1$
	private final JLabel goldLabel =
			new JLabel(Globals.getLongCurrencyDisplay() + ": "); //$NON-NLS-1$
	private final JLabel lblBuyRate = new JLabel(LanguageBundle.getString("in_igBuyRateLabel")); //$NON-NLS-1$
	private final JLabel lblSellRate = new JLabel(LanguageBundle.getString("in_igSellRateLabel")); //$NON-NLS-1$
	private final JLabel valueLabel = new JLabel(LanguageBundle.getString("in_igValueLabel")); //$NON-NLS-1$
	private JButton addButton;
	private JButton removeButton;
	private JButton clearAvailableQFilterButton = new JButton(LanguageBundle.getString("in_clear")); //$NON-NLS-1$
	private JButton clearSelectedQFilterButton = new JButton(LanguageBundle.getString("in_clear")); //$NON-NLS-1$
	private JCheckBox allowDebtBox = new JCheckBox(LanguageBundle.getString("in_igAllowDebt")); //$NON-NLS-1$
	private JCheckBox autoResize = new JCheckBox(LanguageBundle.getString("in_igAutoResize")); //$NON-NLS-1$
	private JCheckBox autoSort = new JCheckBox(LanguageBundle.getString("in_igAutoSort"), true); //$NON-NLS-1$
	private JCheckBox chkViewAll = new JCheckBox();
	private JCheckBox costBox = new JCheckBox(LanguageBundle.getString("in_igIgnoreCost")); //$NON-NLS-1$
	private JComboBoxEx viewComboBox = new JComboBoxEx();
	private JComboBoxEx viewSelectComboBox = new JComboBoxEx();
	private JLabelPane infoLabel = new JLabelPane();
	private JMenu pcCopyMenu =
			Utility.createMenu(LanguageBundle.getString("in_igCopyItemMenuTitle"), (char) 0, //$NON-NLS-1$
					LanguageBundle.getString("in_igCopyItemMenuDesc"), null, true); //$NON-NLS-1$
	private JMenu pcMoveMenu =
			Utility.createMenu(LanguageBundle.getString("in_igMoveItemMenuTitle"), (char) 0, //$NON-NLS-1$
					LanguageBundle.getString("in_igMoveItemMenuDesc"), null, true); //$NON-NLS-1$
	private JPanel center = new JPanel();
	private JPanel pnlBuy = new JPanel();
	private JPanel pnlSell = new JPanel();
	private JPanel south = new JPanel();
	private JScrollPane eqScroll = new JScrollPane();
	//rivate JScrollPane scrollPane;
	private JTextField textAvailableQFilter = new JTextField();
	private JTextField textSelectedQFilter = new JTextField();
	private JTextField gold = new JTextField();
	private JTextField totalValue = new JTextField("Temp"); //$NON-NLS-1$
	private JTreeTable availableTable; // the available Equipment
	private JTreeTable selectedTable; // the selected Equipment
	private JTreeTableSorter availableSort = null;
	private JTreeTableSorter selectedSort = null;
	private SellGearActionListener sellOneListener =
			new SellGearActionListener(1);
	private Runnable sellOneRunnable = new Runnable()
	{
		public void run()
		{
			sellOneListener.actionPerformed(null);
		}
	};

	private TreePath selPath;
	private boolean hasBeenSized = false;
	private boolean readyForRefresh = false;

	//	private static final int COL_CARRIED = 4;
	//	private static final int EQUIPMENT_NOTCARRIED = 0;
	//	private static final int EQUIPMENT_CARRIED = 1;
	//	private static final int EQUIPMENT_EQUIPPED = 2;
	//	private static final int EQUIPMENT_CONTAINED = 3;
	// Right-click inventory item

	private PlayerCharacter pc;
	private int serial = 0;

	/**
	 * Constructor
	 * @param pc
	 */
	public InfoGear(PlayerCharacter pc)
	{
		this.pc = pc;
		// we use the component's name to save
		// component specific settings
		setName(tab.toString());

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
		return SettingsHandler.getPCGenOption(".Panel.Gear.Order", tab //$NON-NLS-1$
			.ordinal());
	}

	public void setTabOrder(int order)
	{
		SettingsHandler.setPCGenOption(".Panel.Gear.Order", order); //$NON-NLS-1$
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
		List<String> toDoList = new ArrayList<String>();
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
	 * Set the needs update flag
	 * @param flag
	 */
	public static void setNeedsUpdate(boolean flag)
	{
		needsUpdate = flag;
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
	 * This method causes the character to buy a number of the
	 * given equipment item at the rate specified by the user
	 *
	 * @param selectedEquipment Equipment to sell
	 * @param newQty double number of the item to buy
	 **/
	public void buySpecifiedEquipment(Equipment selectedEquipment, double newQty)
	{
		buySpecifiedEquipmentRate(selectedEquipment, newQty, SettingsHandler
			.getGearTab_BuyRate());
	}

	/**
	 * implementation of Filterable interface
	 */
	public void initializeFilters()
	{
		FilterFactory.registerAllSourceFilters(this);
		FilterFactory.registerAllSizeFilters(this);
		FilterFactory.registerAllEquipmentFilters(this);
	}

	/**
	 * Refresh the available gear list
	 * @param newEq
	 * @param purchase
	 * @param isCurrent
	 */
	public void refreshAvailableList(Equipment newEq, boolean purchase,
		boolean isCurrent)
	{
		// Add new item to available list
		availableModel.addItemToModel(newEq, true);
		availableModel.updateTree();
		availableTable.updateUI();

		// select the item just added
		if (isCurrent)
		{
			if (availableTable.search(newEq.getName(), true) != null)
			{
				availableTable.requestFocus();
			}
		}

		// Attempt to purchase if that was requested
		if (purchase)
		{
			buySpecifiedEquipment(newEq, 1);
		}

		// TODO: need to resync the selected list to add any types that have just been added
	}

	/**
	 * implementation of Filterable interface
	 */
	public void refreshFiltering()
	{
		forceRefresh();
	}

	/**
	 * This method causes the character to sell a number of the
	 * given equipment item from inventory at the rate specified
	 * in the rate combo box.
	 * @param selectedEquipment Equipment to sell
	 * @param qty double number of the item to sell
	 **/
	public void sellSpecifiedEquipment(Equipment selectedEquipment, double qty)
	{
		sellSpecifiedEquipmentRate(selectedEquipment, qty, SettingsHandler
			.getGearTab_SellRate());
	}

	// This recalculates the states of everything based upon the currently selected
	// character.
	private void updateCharacterInfo()
	{
		int ix = 0;
		pcMoveMenu.removeAll();
		pcCopyMenu.removeAll();

		for (PlayerCharacter testPc : Globals.getPCList())
		{
			if (testPc != pc)
			{
				pcMoveMenu.add(Utility.createMenuItem(testPc.getName(),
					new MoveItemListener(ix, 0), "MoveItemTo", (char) 0, null,
					"Move Item To " + testPc.getName(), null, true));
				pcCopyMenu.add(Utility.createMenuItem(testPc.getName(),
					new MoveItemListener(ix, 1), "CopyItemTo", (char) 0, null,
					"Copy Item To " + testPc.getName(), null, true));
			}

			++ix;
		}

		// Only make the menu usable if there is something there!
		pcMoveMenu.setEnabled(pcMoveMenu.getItemCount() > 0);
		pcCopyMenu.setEnabled(pcCopyMenu.getItemCount() > 0);

		if (!needsUpdate)
		{
			return;
		}

		autoSort.setSelected(pc.isAutoSortGear());
		costBox.setSelected(pc.isIgnoreCost());
		allowDebtBox.setSelected(pc.isAllowDebt());
		autoResize.setSelected(pc.isAutoResize());
		pc.aggregateFeatList();
		updateAvailableModel();
		updateSelectedModel();
		gold.setText(pc.getGold().toString());
		updateTotalValue();
		needsUpdate = false;
	}

	private static int getEventSelectedIndex(ListSelectionEvent e)
	{
		final DefaultListSelectionModel model =
				(DefaultListSelectionModel) e.getSource();

		if (model == null)
		{
			return -1;
		}

		return model.getMinSelectionIndex();
	}

	/**
	 * This method gets the Object currently selected in the Available gear
	 * treetable.
	 * @return Object selected in the table or null if no item can be located
	 */
	private Object getCurrentAvailableTableItem()
	{
		Object item = null;
		final int row =
				availableTable.getSelectionModel().getAnchorSelectionIndex();

		if (row >= 0)
		{
			final TreePath treePath =
					availableTable.getTree().getPathForRow(row);
			final Object eo = treePath.getLastPathComponent();
			final PObjectNode e = (PObjectNode) eo;
			item = e.getItem();
		}

		return item;
	}

	/**
	 * This method gets the Object currently selected in the Available gear
	 * treetable.
	 * @return Object selected in the table or null if no item can be located
	 */
	private Object getCurrentSelectedTableItem()
	{
		Object item = null;
		final int row =
				selectedTable.getSelectionModel().getAnchorSelectionIndex();

		if (row >= 0)
		{
			final TreePath treePath =
					selectedTable.getTree().getPathForRow(row);
			final Object eo = treePath.getLastPathComponent();
			final PObjectNode e = (PObjectNode) eo;
			item = e.getItem();
		}

		return item;
	}

	/**
	 * Retrieve the highest output index used in any of the
	 * character's equipment.
	 * @return highest output index
	 */
	private int getHighestOutputIndex()
	{
		int maxOutputIndex = 0;

		if (pc == null)
		{
			return 0;
		}

		for (Equipment item : pc.getEquipmentMasterList())
		{
			if (item.getOutputIndex() > maxOutputIndex)
			{
				maxOutputIndex = item.getOutputIndex();
			}
		}

		return maxOutputIndex;
	}

	private void setInfoLabelText(Equipment aEq)
	{
		if (aEq != null)
		{
			final StringBuilder title = new StringBuilder(50);
			title.append(OutputNameFormatting.piString(aEq, false));

			if (!aEq.longName().equals(aEq.getName()))
			{
				title.append("(").append(aEq.longName()).append(")");
			}
			
			final InfoLabelTextBuilder b = new InfoLabelTextBuilder(title.toString());
			b.appendLineBreak();
			
			b.appendI18nElement("in_igInfoLabelTextType", //$NON-NLS-1$
					StringUtil.join(aEq.getTrueTypeList(true), ". "));
//			CoreUtility.join(aEq.getTypeList(true), '.'));

			//
			// Should only be meaningful for weapons, but if included on some other piece of
			// equipment, show it anyway
			//
			if (aEq.isWeapon() || aEq.get(ObjectKey.WIELD) != null)
			{
				b.appendSpacer();
				final WieldCategory wCat = aEq.getEffectiveWieldCategory(pc);
				b.appendI18nElement("in_igInfoLabelTextWield", //$NON-NLS-1$
					wCat.getKeyName());
			}

			//
			// Only meaningful for weapons, armor and shields
			//
			if (aEq.isWeapon() || aEq.isArmor() || aEq.isShield())
			{
				b.appendSpacer();
				final String value = (pc.isProficientWith(aEq) && aEq.meetsPreReqs(pc))
						? LanguageBundle.getString("in_igInfoLabelTextYes") //$NON-NLS-1$
						: (SettingsHandler.getPrereqFailColorAsHtmlStart()
							+ LanguageBundle.getString("in_igInfoLabelTextNo") + //$NON-NLS-1$
							SettingsHandler.getPrereqFailColorAsHtmlEnd());
				b.appendI18nElement("in_igInfoLabelTextProficient",value); //$NON-NLS-1$
			}

			final String cString = PrerequisiteUtilities.preReqHTMLStringsForList(pc, null,
			aEq.getPrerequisiteList(), false);

			if (cString.length() > 0)
			{
				b.appendSpacer();
				b.appendI18nElement("in_igInfoLabelTextReq",cString); //$NON-NLS-1$
			}

			String IDS = aEq.getInterestingDisplayString(pc);

			if (IDS.length() > 0)
			{
				b.appendSpacer();
				b.appendI18nElement("in_igInfoLabelTextProp",IDS); //$NON-NLS-1$
			}

			String bString =
					Globals.getGameModeUnitSet().displayWeightInUnitSet(
						aEq.getWeight(pc).doubleValue());

			if (bString.length() > 0)
			{
				b.appendSpacer();
				bString += Globals.getGameModeUnitSet().getWeightUnit();
				b.appendI18nElement("in_igInfoLabelTextWeight",bString); //$NON-NLS-1$
					
			}

			Integer a = aEq.getMaxDex(pc);

			if (a.intValue() != 100)
			{
				b.appendSpacer();
				b.appendI18nElement("in_igInfoLabelTextMaxDex",a.toString()); //$NON-NLS-1$
			}

			a = aEq.acCheck(pc);

			if (aEq.isArmor() || aEq.isShield() || (a.intValue() != 0))
			{
				b.appendSpacer();
				b.appendI18nElement("in_igInfoLabelTextAcCheck",a.toString()); //$NON-NLS-1$
			}

			if (Globals.getGameModeACText().length() != 0)
			{
				a = aEq.getACBonus(pc);

				if (aEq.isArmor() || aEq.isShield() || (a.intValue() != 0))
				{
					b.appendSpacer();
					b.appendElement(LanguageBundle.getFormattedString(
						"in_igInfoLabelTextAcBonus", //$NON-NLS-1$
						Globals.getGameModeACText()), a.toString()); 
				}
			}

			if (Globals.getGameModeShowSpellTab())
			{
				a = aEq.spellFailure(pc);

				if (aEq.isArmor() || aEq.isShield() || (a.intValue() != 0))
				{
					b.appendSpacer();
					b.appendI18nElement("in_igInfoLabelTextArcaneFailure",a.toString()); //$NON-NLS-1$
				}
			}

			bString = Globals.getGameModeDamageResistanceText();

			if (bString.length() != 0)
			{
				a = aEq.eDR(pc);

				if (aEq.isArmor() || aEq.isShield() || (a.intValue() != 0))
				{
					b.appendSpacer();
					b.appendElement(bString , a.toString());
				}
			}

			bString = aEq.moveString();

			if (bString.length() > 0)
			{
				b.appendSpacer();
				b.appendI18nElement("in_igInfoLabelTextMove" , bString); //$NON-NLS-1$
			}

			bString = aEq.getSize();

			if (bString.length() > 0)
			{
				b.appendSpacer();
				b.appendI18nElement("in_igInfoLabelTextSize" , bString); //$NON-NLS-1$
			}

			bString = aEq.getDamage(pc);

			if (bString.length() > 0)
			{
				
				if (aEq.isDouble())
				{
					bString += "/" + aEq.getAltDamage(pc); //$NON-NLS-1$
				}
				
				b.appendSpacer();
				b.appendI18nElement("in_igInfoLabelTextDamage",bString); //$NON-NLS-1$
			}

			int critrange = pc.getCritRange(aEq, true);
			int altcritrange = pc.getCritRange(aEq, false);
			bString = critrange == 0 ? "" : Integer.toString(critrange);
			if (aEq.isDouble() && critrange != altcritrange)
			{
				bString += "/"
						+ (altcritrange == 0 ? "" : Integer
								.toString(altcritrange));
			}

			if (bString.length() > 0)
			{
				b.appendSpacer();
				b.appendI18nElement("in_ieInfoLabelTextCritRange",bString); //$NON-NLS-1$
			}

			bString = aEq.getCritMult();
			if (aEq.isDouble()
					&& !(aEq.getCritMultiplier() == aEq.getAltCritMultiplier()))
			{
				bString += "/" + aEq.getAltCritMult(); //$NON-NLS-1$
			}

			if (bString.length() > 0)
			{
				b.appendSpacer();
				b.appendI18nElement("in_igInfoLabelTextCritMult" , bString ); //$NON-NLS-1$
			}

			if (aEq.isWeapon())
			{
				bString =
						Globals.getGameModeUnitSet().displayDistanceInUnitSet(
							aEq.getRange(pc).intValue());

				if (bString.length() > 0)
				{
					b.appendSpacer();
					b.appendI18nElement("in_igInfoLabelTextRange" , bString + //$NON-NLS-1$
						Globals.getGameModeUnitSet().getDistanceUnit());
				}
			}

			bString = aEq.getContainerCapacityString();

			if (bString.length() > 0)
			{
				b.appendSpacer();
				b.appendI18nElement("in_igInfoLabelTextContainer" , bString); //$NON-NLS-1$
			}

			bString = aEq.getContainerContentsString();

			if (bString.length() > 0)
			{
				b.appendSpacer();
				b.appendI18nElement("in_igInfoLabelTextCurrentlyContains" , bString); //$NON-NLS-1$
			}

			final int charges = aEq.getRemainingCharges();

			if (charges >= 0)
			{
				b.appendSpacer();
				b.appendI18nElement("in_igInfoLabelTextCharges" , Integer.valueOf(charges).toString() ); //$NON-NLS-1$
			}

			Map<String, String> qualityMap = aEq.getMapFor(MapKey.QUALITY);
			if (qualityMap != null)
			{
				b.appendSpacer();
				Set<String> qualities = new TreeSet<String>();
				for (Map.Entry<String, String> me : qualityMap.entrySet())
				{
					qualities.add(new StringBuilder().append(me.getKey())
							.append(": ").append(me.getValue()).toString());
				}
				
				b.appendI18nElement("in_igInfoLabelTextQualities", StringUtil.join(qualities, ", ")); //$NON-NLS-1$
			}

			bString = SourceFormat.getFormattedString(aEq,
			Globals.getSourceDisplay(), true);
			if (bString.length() > 0)
			{
				b.appendLineBreak();
				b.appendI18nElement("in_igInfoLabelTextSource", bString); //$NON-NLS-1$
			}

			infoLabel.setText(b.toString());
		}
		else
		{
			infoLabel.setText();
		}
	}

	/**
	 * This method performs the actual adjustment of the character's
	 * equipment
	 *
	 * Do NOT modify the equipment set directly or from any method other than this one
	 *
	 * Method overhauled March, 2003 by sage_sam as part of FREQ 606205
	 *
	 * @param equipItemToAdjust Equipment item selected to update
	 * @param adjustment number of items to add (positive) or
	 * remove (negative) from the character.
	 * @return double containing the actual adjustment made to a character,
	 * i.e. if 10 were requested removed but the charater only had 5,
	 * this will return 5.
	 */
	private double adjustBelongings(final Equipment equipItemToAdjust,
		final double adjustment)
	{
		int nextOutputIndex = 1;
		double actualAdjustment = adjustment;

		if (pc != null)
		{
			Equipment updatedItem =
					pc.getEquipmentNamed(equipItemToAdjust.getName());

			// see if item is already in inventory; update it
			if (updatedItem != null)
			{
				final double prevQty =
						(updatedItem.qty() < 0) ? 0 : updatedItem.qty();
				final double newQty = prevQty + adjustment;

				final double numberOfItemInUse =
						getNumberOfItemInUse(pc, updatedItem);
				if (newQty <= 0)
				{
					// completely remove item
					if (numberOfItemInUse > 0.0)
					{
						ShowMessageDelegate
							.showMessageDialog(
								LanguageBundle.getFormattedString("in_igAdjBelongStillEquiped", //$NON-NLS-1$
									updatedItem.getName()),
									Constants.APPLICATION_NAME, MessageType.ERROR);
						return 0.0;
					}

					actualAdjustment = -prevQty;
					updatedItem.setNumberCarried(new Float(0));
					updatedItem.setLocation(EquipmentLocation.NOT_CARRIED);

					final Equipment eqParent = updatedItem.getParent();

					if (eqParent != null)
					{
						eqParent.removeChild(pc, updatedItem);
					}

					pc.removeEquipment(updatedItem);
					pc.delEquipSetItem(updatedItem);
					selectedModel.removeItemFromNodes(null, updatedItem);
					pc.setCalcEquipmentList();
					pc.totalWeight();
				}
				else
				{
					// update item count
					if (numberOfItemInUse > newQty)
					{
						ShowMessageDelegate
							.showMessageDialog(
								LanguageBundle.getFormattedString("in_igAdjBelongNumberStillEquiped", //$NON-NLS-1$
								updatedItem.getName() ,
								newQty ,
								numberOfItemInUse), 
								Constants.APPLICATION_NAME, MessageType.ERROR);
						return 0.0;
					}
					pc.updateEquipmentQty(updatedItem, prevQty, newQty);
					Float qty = new Float(newQty);
					updatedItem.setQty(qty);
					updatedItem.setNumberCarried(qty);
					selectedModel.setValueForItemInNodes(null, updatedItem,
						newQty, COL_QTY);
				}
			}
			else
			// item is not in inventory; add it
			{
				if (adjustment > 0)
				{
					updatedItem = equipItemToAdjust.clone();

					if (updatedItem != null)
					{
						// Calc the item's output order
						if (autoSort.isSelected())
						{
							updatedItem.setOutputIndex(nextOutputIndex);
						}
						else
						{
							if (updatedItem.getOutputIndex() == 0)
							{
								updatedItem
									.setOutputIndex(getHighestOutputIndex() + 1);
								pc.cacheOutputIndex(updatedItem);
							}
						}

						// Set the number carried and add it to the character
						Float qty = new Float(adjustment);
						updatedItem.setQty(qty);
						updatedItem.setNumberCarried(qty);
						pc.addEquipment(updatedItem);
						if (autoSort.isSelected())
						{
							resortSelected(ResortComparator.RESORT_EQUIPPED,
								ResortComparator.RESORT_ASCENDING);
						}

						// Update the selected table
						selectedModel.addItemToModel(updatedItem, true);
					}
				}
			}

			// Update the PC and equipment
			pc.setCalcEquipmentList();
			updateEqInfo(updatedItem);
			pc.setDirty(true);

			// Update the selected table
			selectedModel.updateTree();
			selectedTable.updateUI();

			// Return the actual adjustment amount
			return actualAdjustment;
		}

		return 0;
	}

	/**
	 * Returns the number of these objects that have been allocated to an
	 * equipment set.
	 *
	 * i.e. If the character has 10 throwing axes and has assigned 4 to Primary
	 * Hand in EqSet 1, and 6 to Backpack and 1 to Primary Hand in EqSet 2 then
	 * this method will return 7
	 *
	 * @param pc2
	 *            The Character to examine
	 * @param equipment
	 *            The piece of equipment to look for in all of the equipmentSets
	 * @return The number of times this piece of equipment has been allocated in
	 *         a single EqSet
	 */
	private double getNumberOfItemInUse(PlayerCharacter pc2, Equipment equipment)
	{
		Map<String, Float> foundCounts = new HashMap<String, Float>();
		for (EquipSet element : pc2.getEquipSet())
		{
			if (element.getValue().equalsIgnoreCase(equipment.getName()))
			{
				String path = element.getRootIdPath();
				if (!foundCounts.containsKey(path))
				{
					foundCounts.put(path, new Float(0.0));
				}
				Float count = foundCounts.get(path);
				count =
						new Float(count.floatValue()
							+ element.getQty().floatValue());
				foundCounts.put(path, count);
			}
		}
		if (foundCounts.isEmpty())
		{
			return 0.0;
		}
		Float max = Collections.max(foundCounts.values());
		return max.doubleValue();
	}

	/**
	 * This method adjusts the character's gold in the event an item is bought or sold.
	 * @param base Equipment item being bought/sold, used to determine the base price
	 * @param diffQty double number of the item bought/sold
	 * @param buyRate int rate (typically 0-100) at which to buy an item
	 * @param sellRate int rate (typically 0-100) at which to sell an item
	 *
	 * This method was overhauled March, 2003 by sage_sam as part of FREQ 606205
	 */
	private void adjustGold(Equipment base, double diffQty, int buyRate,
		int sellRate)
	{
		if (!costBox.isSelected() && (pc != null))
		{
			double itemCost = diffQty * base.getCost(pc).floatValue() * -0.01;

			if (diffQty < 0)
			{
				itemCost *= sellRate;
			}
			else if (diffQty > 0)
			{
				itemCost *= buyRate;
			}

			pc.setDirty(true);
			pc.adjustGold(itemCost);
			gold.setText(pc.getGold().toString());
		}
	}

	/**
	 * This method causes the character to buy a number of the
	 * given equipment item at the rate specified in rateCombo box
	 *
	 * @param selectedEquipment Equipment to buy
	 * @param qty double number of the item to buy
	 **/
	private void buySpecifiedEquipmentRate(Equipment selectedEquipment,
		double qty)
	{
		Object defaultValue = cmbSellPercent.getSelectedItem().toString();
		InputInterface ii = InputFactory.getInputInstance();
		Object input =
				ii.showInputDialog(this, LanguageBundle.getString("in_igBuyPricePercMsg"), //$NON-NLS-1$
					LanguageBundle.getString("in_igBuyPricePercTitle"), MessageType.QUESTION, null, defaultValue); //$NON-NLS-1$

		if (input != null)
		{
			String iString = input.toString().trim();

			try
			{
				int buyRate = Integer.parseInt(iString);
				buySpecifiedEquipmentRate(selectedEquipment, qty, buyRate);
			}
			catch (NumberFormatException nfe)
			{
				ShowMessageDelegate.showMessageDialog(
					LanguageBundle.getString("in_igBuyPricePercNoInteger"), Constants.APPLICATION_NAME, //$NON-NLS-1$
					MessageType.ERROR);
			}
		}
	}

	/**
	 * This method causes the character to buy a number of the
	 * given equipment item at the rate specified.
	 * @param selectedEquipment Equipment to sell
	 * @param qtyToBuy double number of the item to buy
	 * @param buyRate int rate percentage (typically 0 to 100) at which to buy the item
	 *
	 * sage_sam 27 Feb 2003 for FREQ 606205
	 */
	private void buySpecifiedEquipmentRate(Equipment selectedEquipment,
		double qtyToBuy, int buyRate)
	{
		try
		{
			if (selectedEquipment.getSafe(ObjectKey.MOD_CONTROL).getModifiersRequired())
			{
				if ((selectedEquipment.getEqModifierList(true).size() == 0)
					&& (selectedEquipment.getEqModifierList(false).size() == 0))
				{
					ShowMessageDelegate
						.showMessageDialog(
							LanguageBundle.getString("in_igBuyMustCustomizeItemFirst"), //$NON-NLS-1$
							Constants.APPLICATION_NAME, MessageType.ERROR);

					return;
				}
			}

			//
			// Get a number from the user via a popup
			//
			double buyQty = qtyToBuy;

			if (buyQty < 0)
			{
				Object selectedValue =
						JOptionPane.showInputDialog(null, LanguageBundle.getString("in_igBuyEnterQuantity"), //$NON-NLS-1$
							Constants.APPLICATION_NAME, JOptionPane.QUESTION_MESSAGE);

				if (selectedValue != null)
				{
					try
					{
						buyQty =
								Float.parseFloat(((String) selectedValue)
									.trim());
					}
					catch (Exception e)
					{
						ShowMessageDelegate.showMessageDialog(
							LanguageBundle.getString("in_igInvalidNumber"), Constants.APPLICATION_NAME, //$NON-NLS-1$
							MessageType.ERROR);

						return;
					}
				}
				else
				{
					return;
				}
			}

			if (selectedEquipment.acceptsChildren()
				&& !CoreUtility.doublesEqual((buyQty % 1), 0))
			{
				ShowMessageDelegate.showMessageDialog(
					LanguageBundle.getString("in_igBuyNonIntegralNumContainers"), //$NON-NLS-1$
					Constants.APPLICATION_NAME, MessageType.ERROR);

				return;
			}

			if (autoResize.isEnabled() && autoResize.isSelected()
				&& Globals.canResizeHaveEffect(pc, selectedEquipment, null)
				&& pc.sizeInt() != SizeUtilities.sizeInt(selectedEquipment.getSize()))
			{
				final SizeAdjustment newSize = pc.getSizeAdjustment();
				final String existingKey = selectedEquipment.getKeyName();
				final String newKey =
						selectedEquipment.createKeyForAutoResize(newSize);

				Equipment potential = Globals.getContext().ref.silentlyGetConstructedCDOMObject(
						Equipment.class, newKey);

				if (newKey.equals(existingKey))
				{
					// nothing to do
				}

				// If we've already resized this piece of equipment to this size
				// on a previous occasion, just substitute that piece of equipment
				// in place of the selected equipment.
				else if (potential != null)
				{
					selectedEquipment = potential;
				}
				else
				{
					final String newName =
							selectedEquipment.createNameForAutoResize(newSize);
					potential = Globals.getContext().ref
							.silentlyGetConstructedCDOMObject(Equipment.class,
									newName);

					if (potential != null)
					{
						selectedEquipment = potential;
					}
					else
					{
						final Equipment newEq =
								selectedEquipment.clone();

						if (!newEq.containsKey(ObjectKey.BASE_ITEM))
						{
							newEq.put(ObjectKey.BASE_ITEM, CDOMDirectSingleRef
								.getRef(selectedEquipment));
						}

						newEq.setName(newName);
						newEq.put(StringKey.OUTPUT_NAME, newName);
						newEq.put(StringKey.KEY_NAME, newKey);
						newEq.resizeItem(pc, newSize);
						newEq.removeType(Type.AUTO_GEN);
						newEq.removeType(Type.STANDARD);
						if (!newEq.isType(Constants.TYPE_CUSTOM))
						{
							newEq.addType(Type.CUSTOM);
						}

						Globals.getContext().ref.importObject(newEq);
						refreshAvailableList(newEq, false, true);

						selectedEquipment = newEq;
					}
				}
			}

			if (canAfford(selectedEquipment, buyQty, buyRate))
			{
				// Adjust the character's belongings
				adjustBelongings(selectedEquipment, buyQty);

				// Adjust the characters money
				adjustGold(selectedEquipment, buyQty, buyRate, 0);
			}
			else
			{
				ShowMessageDelegate.showMessageDialog(
					LanguageBundle.getFormattedString("in_igBuyInsufficientFunds", qtyToBuy, //$NON-NLS-1$
					selectedEquipment.getName()), Constants.APPLICATION_NAME,
					MessageType.INFORMATION);
			}
		}
		catch (Exception exc)
		{
			ShowMessageDelegate.showMessageDialog(
				"buySpecifiedEquipment: Exception:" + exc.getMessage(),
				Constants.APPLICATION_NAME, MessageType.ERROR);
		}
	}

	/**
	 * This method is called to determine whether the character can afford to buy
	 * the requested quantity of an item at the rate selected.
	 * @param selected Equipment item being bought, used to determine the base price
	 * @param purchaseQty double number of the item bought
	 * @param buyRate int rate (typically 0-100) at which to buy an item
	 *
	 * This method was overhauled March, 2003 by sage_sam as part of FREQ 606205
	 * @return true if it can be afforded
	 */
	private boolean canAfford(Equipment selected, double purchaseQty,
		int buyRate)
	{
		final float currentFunds =
				((pc != null) ? pc.getGold().floatValue() : 0);

		final double itemCost =
				(purchaseQty * buyRate) * (float) 0.01
					* selected.getCost(pc).floatValue();

		return (costBox.isSelected() || allowDebtBox.isSelected() || (itemCost <= currentFunds));
	}

	private void createAvailableModel()
	{
		if (availableModel == null)
		{
			availableModel = new EquipmentModel(viewMode, true);
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
	 * Build the panel with the controls to add an item to the
	 * selected list.
	 * @param button
	 * @param title
	 *
	 * @return The panel.
	 */
	private JPanel buildModPanel(JButton button, String title)
	{
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 1));
		Utility.setDescription(button, title); //$NON-NLS-1$
		button.setEnabled(false);
		button.setMargin(new Insets(1, 14, 1, 14));
		panel.add(button);

		return panel;
	}

	/**
	 * Build the panel with the controls to add an item to the
	 * selected list.
	 * @param button
	 * @param title
	 *
	 * @return The panel.
	 */
	private JPanel buildDelPanel(JButton button, String title)
	{
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 1));
		Utility.setDescription(button, title); //$NON-NLS-1$
		button.setEnabled(false);
		button.setMargin(new Insets(1, 14, 1, 14));
		panel.add(button);
		if (SettingsHandler.allowFeatDebugging())
		{
			panel.add(chkViewAll);
		}

		return panel;
	}

	private JPanel buildRemoveItemPanel()
	{
		JPanel riPanel = new JPanel();
		riPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 1));
		riPanel.add(removeButton);

		autoSort.setSelected(pc.isAutoSortGear());
		riPanel.add(autoSort);

		Utility.setDescription(autoSort, LanguageBundle
			.getString("in_igAutoSortTip")); //$NON-NLS-1$
		autoSort.setEnabled(true);
		autoSort.setMargin(new Insets(1, 14, 1, 14));

		return riPanel;
	}

	/**
	 * Creates the EquipmentModel that will be used.
	 **/
	private void createModels()
	{
		createAvailableModel();
		createSelectedModel();
	}

	private void createSelectedModel()
	{
		if (selectedModel == null)
		{
			selectedModel = new EquipmentModel(viewSelectMode, false);
		}
		else
		{
			selectedModel.resetModel(viewSelectMode, false);
		}

		if (selectedSort != null)
		{
			selectedSort.setRoot((PObjectNode) selectedModel.getRoot());
			selectedSort.sortNodeOnColumn();
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
			buyOneListener.actionPerformed(null);
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
			SwingUtilities.invokeLater(sellOneRunnable);
		}

		public boolean isSelectable(Object obj)
		{
			return !(obj instanceof String);
		}
	}

	private void createTreeTables()
	{
		availableTable = new JTreeTable(availableModel);

		final JTree avaTree = availableTable.getTree();
		avaTree.setRootVisible(false);
		avaTree.setShowsRootHandles(true);
		avaTree.setCellRenderer(new LabelTreeCellRenderer());
		availableTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		availableTable.getSelectionModel().addListSelectionListener(
			new ListSelectionListener()
			{
				public void valueChanged(ListSelectionEvent e)
				{
					if (!e.getValueIsAdjusting())
					{
						//final Object temp = availableTable.getTree().getLastSelectedPathComponent();
						final int idx = getEventSelectedIndex(e);

						if (idx < 0)
						{
							return;
						}

						final Object temp =
								availableTable.getTree().getPathForRow(idx)
									.getLastPathComponent();
						Equipment aEq;

						if (temp != null)
						{
							//
							// Only display information about equipment, not equipment with
							// same name as type
							//
							final PObjectNode pobjn = (PObjectNode) temp;

							if (!(pobjn.getItem() instanceof Equipment))
							{
								setInfoLabelText(null);

								return;
							}

							aEq = (Equipment) pobjn.getItem();
						}
						else
						{
							// This will popup if displaying by name only and we add an item to the available list
							// after customizing, so I've removed it---Byngl
							//
							//GuiFacade.showMessageDialog(null,
							//	"No equipment selected! Try again.", Constants.APPLICATION_NAME, GuiFacade.ERROR_MESSAGE);
							return;
						}

						addButton.setEnabled(aEq != null);
						setInfoLabelText(aEq);
					}
				}
			});

		availableTable.addMouseListener(new JTreeTableMouseAdapter(
			availableTable, new AvailableClickHandler(), false));

		selectedTable = new JTreeTable(selectedModel);

		selectedTable.getColumnModel().getColumn(COL_QTY).setCellEditor(
			new QuantityEditor());
		selectedTable.getColumnModel().getColumn(COL_INDEX).setCellEditor(
			new OutputOrderEditor(new String[]{"First", "Last", "Hidden"}));

		final JTree selTree = selectedTable.getTree();
		selTree.setRootVisible(false);
		selTree.setShowsRootHandles(true);
		selTree.setCellRenderer(new LabelTreeCellRenderer());
		selectedTable.getSelectionModel().addListSelectionListener(
			new ListSelectionListener()
			{
				public void valueChanged(ListSelectionEvent e)
				{
					if (!e.getValueIsAdjusting())
					{
						Equipment aEq = null;
						Object temp = getCurrentSelectedTableItem();

						if (temp != null)
						{
							//
							// Only display information about equipment, not equipment with
							// same name as type
							if (!(temp instanceof Equipment))
							{
								setInfoLabelText(null);

								return;
							}

							aEq = (Equipment) temp;
						}
						else
						{
							infoLabel.setText();
						}

						removeButton.setEnabled(aEq != null);
						setInfoLabelText(aEq);
					}
				}
			});
		selectedTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		selectedTable.addMouseListener(new JTreeTableMouseAdapter(
			selectedTable, new SelectedClickHandler(), false));

		hookupPopupMenu(availableTable);
		hookupPopupMenu(selectedTable);
	}

	private void customizeButtonClick()
	{
		if (!addButton.isEnabled())
		{
			return;
		}

		final int currentRow = availableTable.getSelectedRow();

		if (currentRow >= 0)
		{
			int row =
					availableTable.getSelectionModel()
						.getAnchorSelectionIndex();
			TreePath treePath = availableTable.getTree().getPathForRow(row);
			Object eo = treePath.getLastPathComponent();
			PObjectNode e = (PObjectNode) eo;

			if (!(e.getItem() instanceof Equipment))
			{
				ShowMessageDelegate.showMessageDialog(
					LanguageBundle.getString("in_igCanNotCustomizeTypes"), //$NON-NLS-1$
					Constants.APPLICATION_NAME, MessageType.ERROR);

				return;
			}

			final Equipment aEq = (Equipment) e.getItem();
			openCustomizer(aEq);
		}
	}

	private void deleteCustomButtonClick()
	{
		final int currentRow = availableTable.getSelectedRow();

		if (currentRow >= 0)
		{
			int row =
					availableTable.getSelectionModel()
						.getAnchorSelectionIndex();
			TreePath treePath = availableTable.getTree().getPathForRow(row);
			Object eo = treePath.getLastPathComponent();
			PObjectNode e = (PObjectNode) eo;

			if (!(e.getItem() instanceof Equipment))
			{
				ShowMessageDelegate.showMessageDialog(LanguageBundle.getString("in_igCanNotDeleteTypes"), //$NON-NLS-1$
					Constants.APPLICATION_NAME, MessageType.ERROR);

				return;
			}

			Equipment aEq = (Equipment) e.getItem();

			if (!aEq.isType(Constants.TYPE_CUSTOM))
			{
				ShowMessageDelegate.showMessageDialog(
					LanguageBundle.getString("in_igCanOnlyDeleteCustom"), Constants.APPLICATION_NAME, //$NON-NLS-1$
					MessageType.ERROR);

				return;
			}

			List<String> whoHasIt = new ArrayList<String>();

			for (PlayerCharacter playerCharacter : Globals.getPCList())
			{
				if (playerCharacter.getEquipmentNamed(aEq.getName()) != null)
				{
					whoHasIt.add(playerCharacter.getName());
				}
			}

			if (whoHasIt.size() != 0)
			{
				String whose = whoHasIt.toString();
				whose = whose.substring(1, whose.length() - 1);
				ShowMessageDelegate
					.showMessageDialog(
						LanguageBundle.getFormattedString("in_igCanOnlyDeleteUncarriedItems",whose), //$NON-NLS-1$
						Constants.APPLICATION_NAME, MessageType.ERROR);

				return;
			}

			aEq = Globals.getContext().ref.silentlyGetConstructedCDOMObject(
					Equipment.class, aEq.getKeyName());

			if (aEq != null)
			{
				//
				// Give user a chance to bail
				//
				if (JOptionPane.showConfirmDialog(null, 
					LanguageBundle.getFormattedString("in_igConfirmDelete",aEq.getName()), //$NON-NLS-1$
					Constants.APPLICATION_NAME, JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
				{
					return;
				}

				Globals.getContext().ref.forget(aEq);

				//
				// This will unexpand all expanded nodes
				// TODO: be a little less draconian and remember what's expanded
				// TODO: sneak onto all other character's Gear tabs and refresh the available list
				updateAvailableModel();
			}
		}
	}

	private void editChargesButtonClicked()
	{
		Object item = getCurrentSelectedTableItem();

		if (!(item instanceof Equipment))
		{
			return;
		}

		final Equipment aEq = (Equipment) item;
		final int minCharges = aEq.getMinCharges();
		final int maxCharges = aEq.getMaxCharges();

		if (minCharges < 0)
		{
			ShowMessageDelegate.showMessageDialog(
				"This item cannot hold charges.", Constants.APPLICATION_NAME,
				MessageType.ERROR);

			return;
		}

		InputInterface ii = InputFactory.getInputInstance();
		Object selectedValue =
				ii.showInputDialog(null, "Enter Number of Charges ("
					+ Integer.toString(minCharges) + "-"
					+ Integer.toString(maxCharges) + ")", Constants.APPLICATION_NAME,
					MessageType.INFORMATION, null, Integer.toString(aEq
						.getRemainingCharges()));

		if (selectedValue == null)
		{
			return;
		}

		try
		{
			final String aString = ((String) selectedValue).trim();
			int charges = Integer.parseInt(aString);

			if ((charges < minCharges) || (charges > maxCharges))
			{
				ShowMessageDelegate.showMessageDialog("Value out of range",
					Constants.APPLICATION_NAME, MessageType.ERROR);

				return;
			}

			if (aEq.getRemainingCharges() != charges)
			{
				Equipment newEq = aEq.clone();
				newEq.setRemainingCharges(charges);
				newEq.setQty(1.0);
				aEq.setQty(aEq.getQty() - 1.0);
				pc.updateEquipSetItem(aEq, newEq);

				if (aEq.getQty().floatValue() <= 0.0f)
				{
					pc.removeEquipment(aEq);
				}

				Equipment overlap = pc.getEquipmentNamed(newEq.getName());
				if (overlap == null)
				{
					pc.addEquipment(newEq);
					updateEqInfo(newEq);
				}
				else
				{
					overlap.setQty(overlap.getQty() + 1.0);
					updateEqInfo(overlap);
				}

				updateSelectedModel();
				pc.setDirty(true);

				return;
			}
		}
		catch (Exception exc)
		{
			// TODO This exception needs to be handled
		}
	}

	// This is called when the tab is shown.
	private void formComponentShown()
	{
		requestFocus();
		// TODO: I18N
		PCGen_Frame1
			.setMessageAreaTextWithoutSaving("Equipment character is not proficient with are in Red.");
		refresh();

		setDividerLocs();
	}

	/**
	 * Set the pane divider locations.
	 */
	private void setDividerLocs()
	{
		int width;
		int s = splitPane.getDividerLocation();
		int t = bsplit.getDividerLocation();
		int u = asplit.getDividerLocation();

		//		TableColumn[] acol = new TableColumn[NUM_COL_AVAILABLE];
		//		TableColumn[] scol = new TableColumn[NUM_COL_SELECTED];
		//		int[] awidth = new int[NUM_COL_AVAILABLE];
		//		int[] swidth = new int[NUM_COL_SELECTED];
		//
		//		for (int i = 0; i < NUM_COL_AVAILABLE; i++)
		//		{
		//			acol[i] = availableTable.getColumnModel().getColumn(i);
		//			awidth[i] = acol[i].getWidth();
		//		}
		//
		//		for (int i = 0; i < NUM_COL_SELECTED; i++)
		//		{
		//			scol[i] = selectedTable.getColumnModel().getColumn(i);
		//			swidth[i] = scol[i].getWidth();
		//		}
		//
		if (!hasBeenSized)
		{
			hasBeenSized = true;
			s =
					SettingsHandler.getPCGenOption("InfoGear.splitPane",
						(int) ((this.getSize().getWidth() * 6) / 10));
			t =
					SettingsHandler.getPCGenOption("InfoGear.bsplit",
						(int) (this.getSize().getHeight() - 180));
			u =
					SettingsHandler.getPCGenOption("InfoGear.asplit",
						(int) (this.getSize().getWidth() - 295));

			// set the prefered width on selectedTable
			for (int i = 0; i < selectedTable.getColumnCount(); i++)
			{
				TableColumn sCol = selectedTable.getColumnModel().getColumn(i);
				width = Globals.getCustColumnWidth("InvSel", i);

				if (width != 0)
				{
					sCol.setPreferredWidth(width);
				}

				sCol.addPropertyChangeListener(new ResizeColumnListener(
					selectedTable, "InvSel", i));
			}

			// set the prefered width on availableTable
			for (int i = 0; i < availableTable.getColumnCount(); i++)
			{
				TableColumn sCol = availableTable.getColumnModel().getColumn(i);
				width = Globals.getCustColumnWidth("InvAva", i);

				if (width != 0)
				{
					sCol.setPreferredWidth(width);
				}

				sCol.addPropertyChangeListener(new ResizeColumnListener(
					availableTable, "InvAva", i));
			}
		}

		if (s > 0)
		{
			splitPane.setDividerLocation(s);
			SettingsHandler.setPCGenOption("InfoGear.splitPane", s);
		}

		if (t > 0)
		{
			bsplit.setDividerLocation(t);
			SettingsHandler.setPCGenOption("InfoGear.bsplit", t);
		}

		if (u > 0)
		{
			asplit.setDividerLocation(u);
			SettingsHandler.setPCGenOption("InfoGear.asplit", u);
		}
	}

	private void hookupPopupMenu(JTreeTable treeTable)
	{
		treeTable.addMouseListener(new GearPopupListener(treeTable,
			new GearPopupMenu(treeTable)));
	}

	private void initActionListeners()
	{
		gold.setInputVerifier(new InputVerifier()
		{
			public boolean shouldYieldFocus(JComponent input)
			{
				boolean valueOk = verify(input);
				if (gold.getText().length() > 0)
				{
					if (pc != null)
					{
						pc.setDirty(true);
						pc.setGold(gold.getText());
					}
					return valueOk;
				}
				else if (pc != null)
				{
					gold.setText(pc.getGold().toString());
				}
				return valueOk;
			}

			public boolean verify(JComponent input)
			{
				return true;
			}
		});
		addComponentListener(new ComponentAdapter()
		{
			public void componentShown(ComponentEvent evt)
			{
				formComponentShown();
			}
		});
		asplit.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
			new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent evt)
				{
					saveDividerLocs();
				}
			});
		bsplit.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
			new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent evt)
				{
					saveDividerLocs();
				}
			});
		splitPane.addPropertyChangeListener(
			JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent evt)
				{
					saveDividerLocs();
				}
			});
		
		removeButton.addActionListener(sellOneListener);
		addButton.addActionListener(buyOneListener);
		viewComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				viewComboBoxActionPerformed();
			}
		});
		viewSelectComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				viewSelectComboBoxActionPerformed();
			}
		});
		textAvailableQFilter.getDocument().addDocumentListener(
			new DocumentListener()
			{
				public void changedUpdate(DocumentEvent evt)
				{
					setAvailableQFilter();
				}

				public void insertUpdate(DocumentEvent evt)
				{
					setAvailableQFilter();
				}

				public void removeUpdate(DocumentEvent evt)
				{
					setAvailableQFilter();
				}
			});
		clearAvailableQFilterButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				clearAvailableQFilter();
			}
		});
		textSelectedQFilter.getDocument().addDocumentListener(
			new DocumentListener()
			{
				public void changedUpdate(DocumentEvent evt)
				{
					setSelectedQFilter();
				}

				public void insertUpdate(DocumentEvent evt)
				{
					setSelectedQFilter();
				}

				public void removeUpdate(DocumentEvent evt)
				{
					setSelectedQFilter();
				}
			});
		clearSelectedQFilterButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				clearSelectedQFilter();
			}
		});
		costBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				if (pc != null)
				{
					pc.setDirty(true);
					pc.setIgnoreCost(costBox.isSelected());
				}
			}
		});
		allowDebtBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				if (pc != null)
				{
					pc.setDirty(true);
					pc.setAllowDebt(allowDebtBox.isSelected());
				}
			}
		});
		autoResize.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				if (pc != null)
				{
					pc.setDirty(true);
					pc.setAutoResize(autoResize.isSelected());
				}
			}
		});
		autoSort.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				if (pc != null)
				{
					pc.setDirty(true);
					pc.setAutoSortGear(autoSort.isSelected());
				}
			}
		});

		FilterFactory.restoreFilterSettings(this);
	}

	/**
	 * Save the locations of the pane dividers to pcgen options. 
	 */
	public void saveDividerLocs()
	{
		if (!hasBeenSized)
		{
			return;
		}

		int s = splitPane.getDividerLocation();
		if (s > 0)
		{
			SettingsHandler
				.setPCGenOption("InfoGear.splitPane", s);
		}

		s = asplit.getDividerLocation();
		if (s > 0)
		{
			SettingsHandler.setPCGenOption("InfoGear.asplit", s);
		}

		s = bsplit.getDividerLocation();
		if (s > 0)
		{
			SettingsHandler.setPCGenOption("InfoGear.bsplit", s);
		}
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 **/
	private void initComponents()
	{
		readyForRefresh = true;
		//
		// Sanity check
		//
		int iView = SettingsHandler.getGearTab_AvailableListMode();

		if ((iView >= GuiConstants.INFOINVENTORY_VIEW_TYPE_SUBTYPE_NAME)
			&& (iView <= GuiConstants.INFOINVENTORY_VIEW_SOURCE_NAME))
		{
			viewMode = iView;
		}

		SettingsHandler.setGearTab_AvailableListMode(viewMode);
		iView = SettingsHandler.getGearTab_SelectedListMode();

		if ((iView >= GuiConstants.INFOINVENTORY_VIEW_TYPE_SUBTYPE_NAME)
			&& (iView <= GuiConstants.INFOINVENTORY_VIEW_SOURCE_NAME))
		{
			viewSelectMode = iView;
		}

		SettingsHandler.setGearTab_SelectedListMode(viewSelectMode);

		viewComboBox.addItem(LanguageBundle.getString("in_typeSubtypeName"));
		viewComboBox.addItem(LanguageBundle.getString("in_typeName"));
		viewComboBox.addItem(LanguageBundle.getString("in_nameLabel"));
		viewComboBox.addItem(LanguageBundle.getString("in_allTypes"));
		viewComboBox.addItem(LanguageBundle.getString("in_sourceName"));
		Utility.setDescription(viewComboBox,
			"You can change how the Equipment in the Tables are listed.");
		viewComboBox.setSelectedIndex(viewMode); // must be done before createModels call

		viewSelectComboBox.addItem(LanguageBundle
			.getString("in_typeSubtypeName"));
		viewSelectComboBox.addItem(LanguageBundle.getString("in_typeName"));
		viewSelectComboBox.addItem(LanguageBundle.getString("in_nameLabel"));
		viewSelectComboBox.addItem(LanguageBundle.getString("in_allTypes"));
		viewSelectComboBox.addItem(LanguageBundle.getString("in_sourceName"));
		Utility.setDescription(viewSelectComboBox,
			"You can change how the Equipment in the Tables are listed.");
		viewSelectComboBox.setSelectedIndex(viewSelectMode); // must be done before createModels call

		boolean customExists =
				Globals.getEquipmentTypes().contains(Constants.TYPE_CUSTOM);

		typeSubtypeRoot = new PObjectNode();
		typeRoot = new PObjectNode();
		allTypeRoot = new PObjectNode();
		sourceRoot = new PObjectNode();

		List<String> aList = new ArrayList<String>();
		List<String> bList = new ArrayList<String>();
		List<String> sourceList = new ArrayList<String>();

		if (customExists)
		{
			aList.add(Constants.TYPE_CUSTOM);
			bList.add(Constants.TYPE_CUSTOM);
			sourceList.add(Constants.SOURCE_CUSTOM);
		}

		for (Equipment bEq : Globals.getContext().ref.getConstructedCDOMObjects(Equipment.class))
		{
			List<Type> typeList = bEq.getTrueTypeList(true);

			if (typeList.isEmpty())
			{
				continue;
			}

			// we only want the first TYPE to be in the top-level
			String aString = typeList.get(0).toString();

			if (!aList.contains(aString))
			{
				aList.add(aString);
			}

			if (!bList.contains(aString))
			{
				bList.add(aString);
			}

			//ty=1 is intentional - 0 does not go in aList
			for (int ty = 1; ty < typeList.size(); ty++)
			{
				aString = typeList.get(ty).toString();

				if (!bList.contains(aString))
				{
					bList.add(aString);
				}
			}

			final String sourceString = SourceFormat.getFormattedString(
					bEq, SourceFormat.LONG, false);
			if ((sourceString.length() != 0) && (!sourceList.contains(sourceString)))
			{
				sourceList.add(sourceString);
			}
		}

		Collections.sort(aList);
		Collections.sort(bList);

		PObjectNode[] cc = new PObjectNode[aList.size()];
		PObjectNode[] dc = new PObjectNode[aList.size()];

		for (int i = 0; i < aList.size(); i++)
		{
			cc[i] = new PObjectNode();
			cc[i].setItem(aList.get(i));
			cc[i].setParent((PObjectNode) typeSubtypeRoot);
			dc[i] = new PObjectNode();
			dc[i].setItem(aList.get(i));
			dc[i].setParent((PObjectNode) typeRoot);
		}

		((PObjectNode) typeSubtypeRoot).setChildren(cc);
		((PObjectNode) typeRoot).setChildren(dc);

		Collections.sort(sourceList);
		PObjectNode[] pSources = new PObjectNode[sourceList.size()];
		for (int i = 0; i < sourceList.size(); i++)
		{
			pSources[i] = new PObjectNode();
			pSources[i].setItem(sourceList.get(i).toString());
			pSources[i].setParent((PObjectNode) sourceRoot);
		}
		((PObjectNode) sourceRoot).setChildren(pSources);

		for (int i = 0; i < cc.length; i++)
		{
			aList.clear();

			for (Equipment bEq : Globals.getContext().ref.getConstructedCDOMObjects(Equipment.class))
			{
				final String topType = cc[i].toString();

				if (!bEq.isType(topType))
				{
					continue;
				}

				for (Type type : bEq.getTrueTypeList(true))
				{
					String aString = type.toString();
					if (!aString.equals(topType) && !aList.contains(aString))
					{
						aList.add(aString);
					}
				}
			}

			Collections.sort(aList);

			for (String aString : aList)
			{
				PObjectNode d = new PObjectNode();
				d.setParent(cc[i]);
				cc[i].addChild(d);
				d.setItem(aString);
			}
		}

		PObjectNode[] ec = new PObjectNode[bList.size()];

		for (int i = 0; i < bList.size(); i++)
		{
			ec[i] = new PObjectNode();
			ec[i].setItem(bList.get(i));
			ec[i].setParent((PObjectNode) allTypeRoot);
		}

		((PObjectNode) allTypeRoot).setChildren(ec);

		/*
		 * Setup GUI
		 */
		createModels();

		// create Treetables of equipment
		createTreeTables();

		center.setLayout(new BorderLayout());

		JPanel leftPane = new JPanel();
		JPanel rightPane = new JPanel();

		splitPane =
				new FlippingSplitPane(splitOrientation, leftPane, rightPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerSize(10);

		center.add(splitPane, BorderLayout.CENTER);

		// Top Left - Available
		leftPane.setLayout(new BorderLayout());

		JLabel avaLabel = new JLabel("Available: ");
		leftPane.add(InfoTabUtils.createFilterPane(avaLabel, viewComboBox,
			lblAvailableQFilter, textAvailableQFilter,
			clearAvailableQFilterButton), BorderLayout.NORTH);

		JScrollPane scrollPane =
				new JScrollPane(availableTable,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		leftPane.add(scrollPane, BorderLayout.CENTER);

		addButton = new JButton(IconUtilitities.getImageIcon("Forward16.gif"));
		leftPane.add(buildModPanel(addButton,
			"Click to add the selected item from the Available list of items"),
			BorderLayout.SOUTH);

		JButton columnButton = new JButton();
		scrollPane.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER,
			columnButton);
		columnButton.setText("^");

		//		availableTable.getColumnModel().getColumn(COL_COST).setPreferredWidth(15);
		//		availableTable.setColAlign(COL_SRC, SwingConstants.LEFT);
		new TableColumnManager(availableTable, columnButton, availableModel);

		// Right align the cost column
		int index = availableTable.convertColumnIndexToView(COL_COST);
		if (index > -1)
		{
			availableTable.setColAlign(index, SwingConstants.RIGHT);
		}

		// Right Pane - Selected
		rightPane.setLayout(new BorderLayout());

		JLabel selLabel = new JLabel("Selected: ");
		rightPane.add(InfoTabUtils.createFilterPane(selLabel,
			viewSelectComboBox, lblSelectedQFilter, textSelectedQFilter,
			clearSelectedQFilterButton), BorderLayout.NORTH);

		scrollPane =
				new JScrollPane(selectedTable,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		rightPane.add(scrollPane, BorderLayout.CENTER);

		removeButton = new JButton(IconUtilitities.getImageIcon("Back16.gif"));
		rightPane
			.add(
				buildDelPanel(removeButton,
					LanguageBundle.getString("in_igRemoveHelpMsg")), //$NON-NLS-1$
				BorderLayout.SOUTH);

		JButton columnButton2 = new JButton();
		scrollPane.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER,
			columnButton2);
		columnButton2.setText("^");
		//		selectedTable.getColumnModel().getColumn(COL_NAME).setPreferredWidth(60);
		//		selectedTable.setColAlign(COL_COST, SwingConstants.RIGHT);
		//		selectedTable.getColumnModel().getColumn(COL_COST).setPreferredWidth(15);
		//		selectedTable.setColAlign(COL_QTY, SwingConstants.CENTER);
		//		selectedTable.getColumnModel().getColumn(COL_QTY).setPreferredWidth(10);
		//		selectedTable.getColumnModel().getColumn(COL_INDEX).setCellRenderer(new OutputOrderRenderer());
		//		selectedTable.getColumnModel().getColumn(COL_INDEX).setPreferredWidth(20);
		new TableColumnManager(selectedTable, columnButton2, selectedModel);

		rightPane.add(buildRemoveItemPanel(), BorderLayout.SOUTH);

		TitledBorder title1 =
				BorderFactory.createTitledBorder(LanguageBundle.getString("in_igEqInfo")); //$NON-NLS-1$
		title1.setTitleJustification(TitledBorder.CENTER);
		eqScroll.setBorder(title1);
		infoLabel.setBackground(rightPane.getBackground());
		eqScroll.setViewportView(infoLabel);
		Utility.setDescription(eqScroll,
			LanguageBundle.getString("in_igReqNotMet")); //$NON-NLS-1$

		GridBagLayout gridbag2 = new GridBagLayout();
		GridBagConstraints c2 = new GridBagConstraints();
		south.setLayout(gridbag2);
		south.setMinimumSize(new Dimension(280, 66));

		totalValue.setEditable(false);
		totalValue.setColumns(8);
		totalValue.setBorder(null);
		totalValue.setOpaque(false);

		gold.setColumns(9);

		JPanel fPanel = new JPanel();
		fPanel.setLayout(new BorderLayout());

		JPanel f2Panel = new JPanel();
		f2Panel.setLayout(new BorderLayout());
		f2Panel.add(goldLabel, BorderLayout.WEST);
		f2Panel.add(gold, BorderLayout.EAST);
		fPanel.add(f2Panel, BorderLayout.EAST);
		Utility.buildConstraints(c2, 0, 0, 1, 1, 0, 0);
		c2.fill = GridBagConstraints.BOTH;
		c2.anchor = GridBagConstraints.WEST;
		gridbag2.setConstraints(fPanel, c2);
		south.add(fPanel);

		JPanel gPanel = new JPanel();
		gPanel.setLayout(new BorderLayout());

		JPanel g2Panel = new JPanel();
		g2Panel.setLayout(new BorderLayout());
		g2Panel.add(valueLabel, BorderLayout.WEST);
		g2Panel.add(totalValue, BorderLayout.EAST);
		gPanel.add(g2Panel, BorderLayout.EAST);
		Utility.buildConstraints(c2, 0, 1, 1, 1, 0, 0);
		c2.fill = GridBagConstraints.BOTH;
		c2.anchor = GridBagConstraints.WEST;
		gridbag2.setConstraints(gPanel, c2);
		south.add(gPanel);

		// Note this one section has been changed to use the new
		// preferred constant names of this layoutManager

		JPanel hPanel = new JPanel();
		hPanel.setLayout(new BorderLayout());
		hPanel.add(allowDebtBox, BorderLayout.LINE_START);
		hPanel.add(costBox, BorderLayout.LINE_END);
		hPanel.add(autoResize, BorderLayout.PAGE_END);

		Utility.buildConstraints(c2, 0, 2, 1, 1, 0, 0);
		c2.fill = GridBagConstraints.BOTH;
		c2.anchor = GridBagConstraints.WEST;
		gridbag2.setConstraints(hPanel, c2);
		south.add(hPanel);
		costBox.setSelected(SettingsHandler.getGearTab_IgnoreCost());
		autoResize.setSelected(SettingsHandler.getGearTab_AutoResize());
		autoResize.setEnabled(SettingsHandler.getGame().getAllowAutoResize());
		allowDebtBox.setSelected(SettingsHandler.getGearTab_AllowDebt());

		Integer[] predefinedPercent = new Integer[5];

		for (int i = 0; i < 5; ++i)
		{
			predefinedPercent[i] = Integer.valueOf(i * 50);
		}

		cmbBuyPercent.setEditable(true);
		cmbSellPercent.setEditable(true);
		cmbBuyPercent.setModel(new DefaultComboBoxModel(predefinedPercent));
		cmbSellPercent.setModel(new DefaultComboBoxModel(predefinedPercent));

		cmbBuyPercent.setSelectedItem(Integer.valueOf(SettingsHandler
			.getGearTab_BuyRate()));
		cmbSellPercent.setSelectedItem(Integer.valueOf(SettingsHandler
			.getGearTab_SellRate()));

		cmbBuyPercent.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				if (evt.getActionCommand().equals("comboBoxChanged"))
				{
					final Object enteredRate = cmbBuyPercent.getSelectedItem();
					int rate;

					if (enteredRate instanceof Integer)
					{
						rate = ((Integer) enteredRate).intValue();
					}
					else
					{
						try
						{
							rate = Integer.parseInt((String) enteredRate);
						}
						catch (Exception exc)
						{
							rate = -1;
						}
					}

					if (rate < 0)
					{
						rate = SettingsHandler.getGearTab_BuyRate();
						cmbBuyPercent.setSelectedItem(Integer.valueOf(rate));
					}

					SettingsHandler.setGearTab_BuyRate(rate);
				}
			}
		});
		cmbSellPercent.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				if (evt.getActionCommand().equals("comboBoxChanged"))
				{
					final Object enteredRate = cmbSellPercent.getSelectedItem();
					int rate;

					if (enteredRate instanceof Integer)
					{
						rate = ((Integer) enteredRate).intValue();
					}
					else
					{
						try
						{
							rate = Integer.parseInt((String) enteredRate);
						}
						catch (Exception exc)
						{
							rate = -1;
						}
					}

					if (rate < 0)
					{
						rate = SettingsHandler.getGearTab_SellRate();
						cmbSellPercent.setSelectedItem(Integer.valueOf(rate));
					}

					SettingsHandler.setGearTab_SellRate(rate);
				}
			}
		});

		pnlBuy.setLayout(new BorderLayout());
		pnlSell.setLayout(new BorderLayout());

		pnlBuy.add(lblBuyRate, BorderLayout.WEST);
		pnlBuy.add(cmbBuyPercent, BorderLayout.EAST);
		pnlSell.add(lblSellRate, BorderLayout.WEST);
		pnlSell.add(cmbSellPercent, BorderLayout.EAST);
		Utility.buildConstraints(c2, 0, 3, 1, 1, 0, 0);
		c2.fill = GridBagConstraints.BOTH;
		c2.anchor = GridBagConstraints.WEST;
		gridbag2.setConstraints(pnlBuy, c2);
		south.add(pnlBuy, c2);

		Utility.buildConstraints(c2, 0, 4, 1, 1, 0, 0);
		c2.fill = GridBagConstraints.BOTH;
		c2.anchor = GridBagConstraints.WEST;
		gridbag2.setConstraints(pnlSell, c2);
		south.add(pnlSell);

		asplit =
				new FlippingSplitPane(JSplitPane.HORIZONTAL_SPLIT, eqScroll,
					south);
		asplit.setOneTouchExpandable(true);
		asplit.setDividerSize(10);

		JPanel botPane = new JPanel();
		botPane.setLayout(new BorderLayout());
		botPane.setMinimumSize(new Dimension(200, 120));
		botPane.add(asplit, BorderLayout.CENTER);
		bsplit =
				new FlippingSplitPane(JSplitPane.VERTICAL_SPLIT, center,
					botPane);
		bsplit.setOneTouchExpandable(true);
		bsplit.setDividerSize(10);

		this.setLayout(new BorderLayout());
		this.add(bsplit, BorderLayout.CENTER);
		availableSort =
				new JTreeTableSorter(availableTable,
					(PObjectNode) availableModel.getRoot(), availableModel);
		selectedSort =
				new JTreeTableSorter(selectedTable, (PObjectNode) selectedModel
					.getRoot(), selectedModel);
		addFocusListener(new FocusAdapter()
		{
			public void focusGained(FocusEvent evt)
			{
				refresh();
			}
		});
		
		setDividerLocs();
	}

	private void openCustomizer(Equipment aEq)
	{
		if (aEq != null)
		{
			if (eqFrame == null)
			{
				eqFrame = new EQFrame(null, pc);
			}

			if (eqFrame.setEquipment(aEq))
			{
				eqFrame.setVisible(true);
				eqFrame.toFront();
			}
		}
	}

	/**
	 * This method removes a selected number of equipment items from inventory.
	 * If the number of requested items to remove is greater than the number in
	 * inventory, all items of the type given are removed.  If the number of requested
	 * items to remove is negative, the user is prompted for a number of items to remove.
	 * @param selectedEquipment Equipment item to remove
	 * @param qty double number of items to remove
	 * @return double number of items actually removed from the character's inventory
	 */
	private double removeSpecifiedEquipment(Equipment selectedEquipment,
		double qty)
	{
		// Get a number from the user via a popup
		double sellQty = qty;

		if (sellQty < 0.0f)
		{
			Object selectedValue =
					JOptionPane.showInputDialog(null, LanguageBundle.getString("in_igRemoveEnterQuantity"), //$NON-NLS-1$
						Constants.APPLICATION_NAME, JOptionPane.QUESTION_MESSAGE);

			if (selectedValue != null)
			{
				try
				{
					sellQty = Float.parseFloat(((String) selectedValue).trim());
				}
				catch (Exception e)
				{
					ShowMessageDelegate.showMessageDialog(LanguageBundle.getString("in_igInvalidNumber"), //$NON-NLS-1$
						Constants.APPLICATION_NAME, MessageType.ERROR);

					return 0;
				}
			}
			else
			{
				return 0;
			}
		}

		if (selectedEquipment.getChildCount() == 0)
		{
			if (!selectedEquipment.acceptsChildren()
				|| (CoreUtility.doublesEqual((sellQty % 1), 0)))
			{
				return adjustBelongings(selectedEquipment, -sellQty);
			}
			ShowMessageDelegate
				.showMessageDialog(
					LanguageBundle.getString("in_igRemoveNoIntegerMsg"), //$NON-NLS-1$
					Constants.APPLICATION_NAME, MessageType.ERROR);

			return 0;
		}
		ShowMessageDelegate.showMessageDialog(
			LanguageBundle.getString("in_igRemoveNoRemoveFilledContainer"), Constants.APPLICATION_NAME, //$NON-NLS-1$
			MessageType.ERROR);
		return 0;
	}

	private void resortSelected(int sort, boolean sortOrder)
	{
		ResortComparator comparator = new ResortComparator(sort, sortOrder, pc);
		int nextOutputIndex = 1;
		List<Equipment> eqList = pc.getEquipmentMasterList();
		Collections.sort(eqList, comparator);

		for (Equipment item : eqList)
		{
			if (item.getOutputIndex() >= 0)
			{
				item.setOutputIndex(nextOutputIndex++);
				if (item.isAutomatic())
				{
					pc.cacheOutputIndex(item);
				}
			}
		}

		selectedModel.updateTree();
		selectedTable.updateUI();
		pc.setDirty(true);
	}

	private void clearAvailableQFilter()
	{
		availableModel.clearQFilter();
		if (saveAvailableViewMode != null)
		{
			viewMode = saveAvailableViewMode.intValue();
			saveAvailableViewMode = null;
		}
		textAvailableQFilter.setText("");
		availableModel.resetModel(viewMode, true);
		clearAvailableQFilterButton.setEnabled(false);
		viewComboBox.setEnabled(true);
		forceRefresh();
	}

	private void clearSelectedQFilter()
	{
		selectedModel.clearQFilter();
		if (saveSelectedViewMode != null)
		{
			viewSelectMode = saveSelectedViewMode.intValue();
			saveSelectedViewMode = null;
		}
		textSelectedQFilter.setText("");
		selectedModel.resetModel(viewSelectMode, false);
		clearSelectedQFilterButton.setEnabled(false);
		viewSelectComboBox.setEnabled(true);
		forceRefresh();
	}

	private void setAvailableQFilter()
	{
		String aString = textAvailableQFilter.getText();

		if (aString.length() == 0)
		{
			clearAvailableQFilter();
			return;
		}
		availableModel.setQFilter(aString);

		if (saveAvailableViewMode == null)
		{
			saveAvailableViewMode = Integer.valueOf(viewMode);
		}
		viewMode = GuiConstants.INFOINVENTORY_VIEW_NAME;
		availableModel.resetModel(viewMode, true);
		clearAvailableQFilterButton.setEnabled(true);
		viewComboBox.setEnabled(false);
		forceRefresh();

	}

	private void setSelectedQFilter()
	{
		String aString = textSelectedQFilter.getText();

		if (aString.length() == 0)
		{
			clearSelectedQFilter();
			return;
		}
		selectedModel.setQFilter(aString);

		if (saveSelectedViewMode == null)
		{
			saveSelectedViewMode = Integer.valueOf(viewSelectMode);
		}
		viewSelectMode = GuiConstants.INFOINVENTORY_VIEW_NAME;
		selectedModel.resetModel(viewMode, false);
		clearSelectedQFilterButton.setEnabled(true);
		viewSelectComboBox.setEnabled(false);
		forceRefresh();
	}

	/**
	 * This method causes the character to sell all of a
	 * given equipment item from inventory at the
	 * rate specified in the rate combo box.
	 * @param selectedEquipment Equipment to sell
	 **/
	private void sellSpecifiedEquipmentRate(Equipment selectedEquipment)
	{
		sellSpecifiedEquipmentRate(selectedEquipment, selectedEquipment.qty());
	}

	/**
	 * This method causes the character to sell a number of the
	 * given equipment item from inventory at the rate specified
	 *
	 * @param selectedEquipment Equipment to sell
	 * @param qty double number of the item to sell
	 **/
	private void sellSpecifiedEquipmentRate(Equipment selectedEquipment,
		double qty)
	{
		Object defaultValue = cmbSellPercent.getSelectedItem().toString();
		InputInterface ii = InputFactory.getInputInstance();
		Object input =
				ii
					.showInputDialog(this, LanguageBundle.getString("in_igSellPricePercMsg"), //$NON-NLS-1$
						LanguageBundle.getString("Sell at Percent"), MessageType.QUESTION, null, //$NON-NLS-1$
						defaultValue);

		if (input != null)
		{
			String iString = input.toString().trim();

			try
			{
				int sellRate = Integer.parseInt(iString);
				sellSpecifiedEquipmentRate(selectedEquipment, qty, sellRate);
			}
			catch (NumberFormatException nfe)
			{
				ShowMessageDelegate.showMessageDialog(
					LanguageBundle.getString("in_igSellPricePercNoIntegerMsg"), //$NON-NLS-1$
					LanguageBundle.getString("in_igSellPricePercNoIntegerTitle"), //$NON-NLS-1$
					MessageType.ERROR);
			}
		}
	}

	/**
	 * This method causes the character to sell a number of the
	 * given equipment item from inventory at the rate specified.
	 *
	 * @param selectedEquipment Equipment to sell
	 * @param qty double number of the item to sell
	 * @param sellRate int rate percentage (typically 0 to 100) at which to sell the item
	 **/
	private void sellSpecifiedEquipmentRate(Equipment selectedEquipment,
		double qty, int sellRate)
	{
		adjustGold(selectedEquipment, removeSpecifiedEquipment(
			selectedEquipment, qty), 0, sellRate);
	}

	/**
	 * Updates the Available table
	 **/
	private void updateAvailableModel()
	{
		try
		{
			List<String> pathList = availableTable.getExpandedPaths();
			createAvailableModel();
			availableTable.updateUI();
			availableTable.expandPathList(pathList);
		}
		catch (RuntimeException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void updateEqInfo(Equipment selectedEquipment)
	{
		updateTotalValue();
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

	private void updateTotalValue()
	{
		totalValue.setText(BigDecimalHelper.trimZeros(pc.totalValue()) + " "
			+ Globals.getCurrencyDisplay());
	}

	private void viewComboBoxActionPerformed()
	{
		final int index = viewComboBox.getSelectedIndex();

		if (index != viewMode)
		{
			viewMode = index;
			SettingsHandler.setGearTab_AvailableListMode(viewMode);
			updateAvailableModel();
		}
	}

	private void viewSelectComboBoxActionPerformed()
	{
		final int index = viewSelectComboBox.getSelectedIndex();

		if (index != viewSelectMode)
		{
			viewSelectMode = index;
			SettingsHandler.setGearTab_SelectedListMode(viewSelectMode);
			updateSelectedModel();
		}
	}

	/**
	 * This class is an ActionListener used to perform actions such as
	 * add/remove of equipment selected in the equipment tree tables.
	 * This class is an abstract base class for concrete implementations
	 * and provides handling of item quantities.
	 *
	 * sage_sam 27 Feb 2003 for FREQ 606205
	 */
	private static abstract class GearActionListener implements ActionListener
	{
		int qty = 0;

		private GearActionListener(int aQty)
		{
			qty = aQty;
		}

		protected abstract void updateEquipment(Equipment eq, int argQty);
	}

	/**
	 * This class is an ActionListener used to perform actions such as
	 * add/remove of equipment selected in the available equipment table.
	 * This class is an abstract base class for concrete implementations
	 * and provides determination of which item is currently selected.
	 *
	 * sage_sam 27 Feb 2003 for FREQ 606205
	 */
	private abstract class AvailableGearActionListener extends
			GearActionListener
	{
		private AvailableGearActionListener(int aQty)
		{
			super(aQty);
		}

		public void actionPerformed(ActionEvent evt)
		{
			Object pe = getCurrentAvailableTableItem();

			if ((pe != null) && (pe instanceof Equipment))
			{
				updateEquipment((Equipment) pe, qty);
			}
		}
	}

	/**
	 * This class is an ActionListener used to perform actions such as
	 * add/remove of equipment selected in the selected equipment table.
	 * This class is an abstract base class for concrete implementations
	 * and provides determination of which item is currently selected.
	 *
	 * sage_sam 27 Feb 2003 for FREQ 606205
	 */
	private abstract class SelectedGearActionListener extends
			GearActionListener
	{
		private SelectedGearActionListener(int aQty)
		{
			super(aQty);
		}

		public void actionPerformed(ActionEvent evt)
		{
			Object pe = getCurrentSelectedTableItem();

			if ((pe != null) && (pe instanceof Equipment))
			{
				updateEquipment((Equipment) pe, qty);
			}
		}
	}

	/**
	 * This class is an ActionListener used to perform a purchase of the
	 * equipment item currently selected in the available table at the
	 * rate selected in the buy rate combo box.
	 *
	 * sage_sam 27 Feb 2003 for FREQ 606205
	 */
	private class BuyGearActionListener extends AvailableGearActionListener
	{
		private BuyGearActionListener(int qty)
		{
			super(qty);
		}

		protected void updateEquipment(Equipment eq, int argQty)
		{
			buySpecifiedEquipment(eq, argQty);
		}
	}

	/**
	 * This class is an ActionListener used to perform a purchase of the
	 * equipment item currently selected in the available table at the
	 * rate selected at the time the action is performed.
	 *
	 * sage_sam 27 Feb 2003 for FREQ 606205
	 */
	private class BuyRateGearActionListener extends AvailableGearActionListener
	{
		private BuyRateGearActionListener(int qty)
		{
			super(qty);
		}

		protected void updateEquipment(Equipment eq, int argQty)
		{
			buySpecifiedEquipmentRate(eq, argQty);
		}
	}

	/**
	 * These are the right click popup menus
	 **/
	private class GearPopupMenu extends JPopupMenu
	{
		static final long serialVersionUID = -2320970658737297916L;

		private GearPopupMenu(JTreeTable treeTable)
		{
			if (treeTable == availableTable)
			{
				GearPopupMenu.this.add(createBuyMenuItem(LanguageBundle.
					getString("in_igBuy1"), 1, "shortcut EQUALS")); //$NON-NLS-1$

				JMenu buyMenu =
						Utility.createMenu(LanguageBundle.getString("in_igBuyQuantity"), //$NON-NLS-1$
								(char) 0, LanguageBundle.getString("in_igBuyQuantity"), //$NON-NLS-1$
							null, true);

				buyMenu.add(createBuyMenuItem(LanguageBundle.
					getString("in_igBuy2"), 2, null));  //$NON-NLS-1$
				buyMenu.add(createBuyMenuItem(LanguageBundle.
					getString("in_igBuy5"), 5, null)); //$NON-NLS-1$
				buyMenu.add(createBuyMenuItem(LanguageBundle.
					getString("in_igBuy10"), 10, null)); //$NON-NLS-1$
				buyMenu.add(createBuyMenuItem(LanguageBundle.
					getString("in_igBuy15"), 15, null)); //$NON-NLS-1$
				buyMenu.add(createBuyMenuItem(LanguageBundle.
					getString("in_igBuy20"), 20, null)); //$NON-NLS-1$
				buyMenu.add(createBuyMenuItem(LanguageBundle.
					getString("in_igBuy50"), 50, null)); //$NON-NLS-1$
				GearPopupMenu.this.add(buyMenu);
				GearPopupMenu.this.add(createBuyMenuItem(LanguageBundle.
					getString("in_igBuyN"), -1, "alt N")); //$NON-NLS-1$
				this.addSeparator();
				GearPopupMenu.this.add(createBuyRateMenuItem(LanguageBundle.
					getString("in_igBuy1At"), 1, null)); //$NON-NLS-1$

				JMenu buyAtMenu =
						Utility.createMenu(LanguageBundle.getString("in_igBuyQuantityAt"), //$NON-NLS-1$
							(char) 0,LanguageBundle.getString("in_igBuyQuantityAt"), null, true); //$NON-NLS-1$
				buyAtMenu.add(createBuyRateMenuItem(LanguageBundle
					.getString("in_igBuy2At"), 2, null)); //$NON-NLS-1$
				buyAtMenu.add(createBuyRateMenuItem(LanguageBundle
						.getString("in_igBuy5At"), 5, null)); //$NON-NLS-1$
				buyAtMenu.add(createBuyRateMenuItem(LanguageBundle
						.getString("in_igBuy10At"), 10, null)); //$NON-NLS-1$
				buyAtMenu.add(createBuyRateMenuItem(LanguageBundle
						.getString("in_igBuy15At"), 15, null)); //$NON-NLS-1$
				buyAtMenu.add(createBuyRateMenuItem(LanguageBundle
						.getString("in_igBuy20At"), 20, null)); //$NON-NLS-1$
				buyAtMenu.add(createBuyRateMenuItem(LanguageBundle
						.getString("in_igBuy50At"), 50, null)); //$NON-NLS-1$
				GearPopupMenu.this.add(buyAtMenu);
				GearPopupMenu.this.add(createBuyRateMenuItem(LanguageBundle.getString("in_igBuyNAt"), //$NON-NLS-1$
					-1, "alt N"));
				this.addSeparator();

				GearPopupMenu.this.add(Utility.createMenuItem(
					LanguageBundle.getString("in_igCreateCustomItemLabel"), new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							customizeButtonClick();
						}
					}, "newCustomItem", (char) 0, "alt C",
					LanguageBundle.getString("in_igCreateCustomItemDesc"), null, true));
				GearPopupMenu.this.add(Utility.createMenuItem(
						LanguageBundle.getString("in_igDeleteCustomItemLabel"), new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							deleteCustomButtonClick();
						}
					}, "deleteItem", (char) 0, "DELETE", 
					LanguageBundle.getString("in_igDeleteCustomItemDesc"), null, true));

				/*                GearPopupMenu.this.add(CoreUtility.createMenuItem("Create custom item from scratch",
				 new ActionListener()
				 {
				 public void actionPerformed(ActionEvent e)
				 {
				 createGenericItemButtonClick();
				 }
				 }
				 , "createGenericItem", (char)0, "GENERIC", "Create custom item from scratch", null, true));
				 */
			}

			else
			// selectedTable
			{
				GearPopupMenu.this.add(createRemoveMenuItem(LanguageBundle.
					getString("in_igRemove1"), 1, "shortcut MINUS")); //$NON-NLS-1$

				JMenu remMenu =
						Utility.createMenu(LanguageBundle.getString("in_igRemoveQuantity"), (char) 0, //$NON-NLS-1$
						LanguageBundle.getString("in_igRemoveQuantity"), null, true); //$NON-NLS-1$
				remMenu.add(createRemoveMenuItem(LanguageBundle.
						getString("in_igRemove2"), 2, null)); //$NON-NLS-1$
				remMenu.add(createRemoveMenuItem(LanguageBundle.
						getString("in_igRemove5"), 5, null)); //$NON-NLS-1$
				remMenu.add(createRemoveMenuItem(LanguageBundle.
						getString("in_igRemove10"), 10, null)); //$NON-NLS-1$
				remMenu.add(createRemoveMenuItem(LanguageBundle.
						getString("in_igRemove15"), 15, null)); //$NON-NLS-1$
				remMenu.add(createRemoveMenuItem(LanguageBundle.
						getString("in_igRemove20"), 20, null)); //$NON-NLS-1$
				remMenu.add(createRemoveMenuItem(LanguageBundle.
						getString("in_igRemove50"), 50, null)); //$NON-NLS-1$
				remMenu.add(createRemoveMenuItem(LanguageBundle.
						getString("in_igRemoveN"), -1, null)); //$NON-NLS-1$
				GearPopupMenu.this.add(remMenu);
				GearPopupMenu.this.add(createRemoveMenuItem(LanguageBundle.
						getString("in_igRemoveAll"), -5, null)); //$NON-NLS-1$
				this.addSeparator();

				GearPopupMenu.this.add(createSellMenuItem(LanguageBundle.
						getString("in_igSell1"), 1, null)); //$NON-NLS-1$

				JMenu sellMenu =
						Utility.createMenu(LanguageBundle.
							getString("in_igSellQuantity"), (char) 0, //$NON-NLS-1$
							LanguageBundle.getString("in_igSellQuantity"), null, true); //$NON-NLS-1$
				sellMenu.add(createSellMenuItem(LanguageBundle.
						getString("in_igSell2"), 2, null)); //$NON-NLS-1$
				sellMenu.add(createSellMenuItem(LanguageBundle.
						getString("in_igSell5"), 5, null)); //$NON-NLS-1$
				sellMenu.add(createSellMenuItem(LanguageBundle.
						getString("in_igSell10"), 10, null)); //$NON-NLS-1$
				sellMenu.add(createSellMenuItem(LanguageBundle.
						getString("in_igSell15"), 15, null)); //$NON-NLS-1$
				sellMenu.add(createSellMenuItem(LanguageBundle.
						getString("in_igSell20"), 20, null)); //$NON-NLS-1$
				sellMenu.add(createSellMenuItem(LanguageBundle.
						getString("in_igSell50"), 50, null)); //$NON-NLS-1$
				sellMenu.add(createSellMenuItem(LanguageBundle.
						getString("in_igSellN"), -1, null)); //$NON-NLS-1$
				GearPopupMenu.this.add(sellMenu);
				GearPopupMenu.this
					.add(createSellMenuItem(LanguageBundle.getString("in_igSellAll"), //$NON-NLS-1$
						-5, null));
				this.addSeparator();

				GearPopupMenu.this.add(createSellRateMenuItem(LanguageBundle.
					getString("in_igSell1At"),	1, null)); //$NON-NLS-1$
				GearPopupMenu.this.add(createSellRateMenuItem(LanguageBundle.
					getString("in_igSellNAtt"), -1, null)); //$NON-NLS-1$
				GearPopupMenu.this.add(createSellRateMenuItem(LanguageBundle.
					getString("in_igSellAllAt"), -5, null)); //$NON-NLS-1$
				this.addSeparator();

				GearPopupMenu.this.add(Utility.createMenuItem(LanguageBundle.
					getString("in_igModChargesMenuLabel"), //$NON-NLS-1$
					new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							editChargesButtonClicked();
						}
					}, LanguageBundle.getString("in_igModChargesMenuCommand"), //$NON-NLS-1$
					(char) 0, LanguageBundle.getString("in_igModChargesMenuAccelerator"), //$NON-NLS-1$
					LanguageBundle.getString("in_igModChargesMenuDesc"), null, true)); //$NON-NLS-1$
				this.addSeparator();

				GearPopupMenu.this.add(pcMoveMenu);
				GearPopupMenu.this.add(pcCopyMenu);
				this.addSeparator();

				JMenu resortMenu =
						Utility.createMenu(LanguageBundle.getString("in_igSortMenuLabel"), //$NON-NLS-1$
						(char) 0,LanguageBundle.getString("in_igSortMenuDesc"), //$NON-NLS-1$
						null, true);

				GearPopupMenu.this.add(resortMenu);

				resortMenu
					.add(Utility
						.createMenuItem(
							LanguageBundle.getString("in_igSortNameAscLabel"), //$NON-NLS-1$
							new ResortActionListener(
								ResortComparator.RESORT_NAME,
								ResortComparator.RESORT_ASCENDING),
							LanguageBundle.getString("in_igSortCommand"), //$NON-NLS-1$
							(char) 0,
							null,
							LanguageBundle.getString("in_igSortNameAscDesc"), //$NON-NLS-1$
							null, true));
				resortMenu
					.add(Utility
						.createMenuItem(
							LanguageBundle.getString("in_igSortNameDscLabel"), //$NON-NLS-1$
							new ResortActionListener(
								ResortComparator.RESORT_NAME,
								ResortComparator.RESORT_DESCENDING),
							LanguageBundle.getString("in_igSortCommand"), //$NON-NLS-1$
							(char) 0,
							null,
							LanguageBundle.getString("in_igSortNameDscDesc"), //$NON-NLS-1$
							null, true));
				resortMenu.add(Utility.createMenuItem(LanguageBundle.
					getString("in_igSortWeightAscLabel"), //$NON-NLS-1$
					new ResortActionListener(ResortComparator.RESORT_WEIGHT,
						ResortComparator.RESORT_ASCENDING), LanguageBundle.
					getString("in_igSortCommand"), (char) 0, null, //$NON-NLS-1$
					LanguageBundle.getString("in_igSortWeightAscDesc"), null, //$NON-NLS-1$
					true));
				resortMenu.add(Utility.createMenuItem(LanguageBundle.
					getString("in_igSortWeightDscLabel"), //$NON-NLS-1$
					new ResortActionListener(ResortComparator.RESORT_WEIGHT,
						ResortComparator.RESORT_DESCENDING), LanguageBundle.
					getString("in_igSortCommand"), (char) 0, null, //$NON-NLS-1$
					LanguageBundle.getString("in_igSortWeightDscDesc"), null, //$NON-NLS-1$
					true));
				resortMenu.add(Utility.createMenuItem(LanguageBundle.
					getString("in_igSortEquippedAscLabel"), //$NON-NLS-1$
					new ResortActionListener(ResortComparator.RESORT_EQUIPPED,
						ResortComparator.RESORT_ASCENDING), LanguageBundle.
					getString("in_igSortCommand"), (char) 0, null, //$NON-NLS-1$
					LanguageBundle.getString("in_igSortEquippedAscDesc"), null, //$NON-NLS-1$
					true));
				resortMenu.add(Utility.createMenuItem(LanguageBundle.
					getString("in_igSortEquippedDscLabel"), //$NON-NLS-1$
					new ResortActionListener(ResortComparator.RESORT_EQUIPPED,
						ResortComparator.RESORT_DESCENDING), LanguageBundle.
					getString("in_igSortCommand"), (char) 0, null, //$NON-NLS-1$
					LanguageBundle.getString("in_igSortEquippedDscDesc"), null, //$NON-NLS-1$
					true));
			}
		}

		private JMenuItem createBuyMenuItem(String label, int qty,
			String accelerator)
		{
			return Utility.createMenuItem(label, new BuyGearActionListener(qty), 
				LanguageBundle.getFormattedString("in_igBuyMenuCommand" , qty), (char) 0, //$NON-NLS-1$
				accelerator, LanguageBundle.getFormattedString("in_igBuyRateMenuDesc" , //$NON-NLS-1$
				(qty < 0) ? "n" : Integer.toString(qty)),"Add16.gif", true); //$NON-NLS-1$ //$NON-NLS-2$
		}

		private JMenuItem createBuyRateMenuItem(String label, int qty,
			String accelerator)
		{
			return Utility.createMenuItem(label, new BuyRateGearActionListener(	qty), 
				LanguageBundle.getFormattedString("in_igBuyRateMenuCommand", qty), (char) 0, accelerator, //$NON-NLS-1$
				LanguageBundle.getFormattedString("in_igBuyRateMenuDesc", //$NON-NLS-1$
				(qty < 0) ? "n" : Integer.toString(qty) ), "Add16.gif", true); //$NON-NLS-1$ //$NON-NLS-2$
		}

		private JMenuItem createRemoveMenuItem(String label, int qty,
			String accelerator)
		{
			return Utility.createMenuItem(label, new RemoveGearActionListener(qty), LanguageBundle.
				getFormattedString("in_igRemoveMenuCommand" , qty), (char) 0, accelerator, //$NON-NLS-1$  
				LanguageBundle.getFormattedString("in_igRemoveMenuDesc", //$NON-NLS-1$
				(qty < 0) ? "n" : Integer.toString(qty) ), "Remove16.gif", true); //$NON-NLS-1$ //$NON-NLS-2$
		}

		private JMenuItem createSellMenuItem(String label, int qty,
			String accelerator)
		{
			return Utility.createMenuItem(label,
				new SellGearActionListener(qty), LanguageBundle.
				getFormattedString("in_igSellMenuCommand",qty), (char) 0, //$NON-NLS-1$
				accelerator, LanguageBundle.getFormattedString("in_igSellMenuDesc", //$NON-NLS-1$
				(qty < 0) ? "n" : Integer.toString(qty)), null, true); //$NON-NLS-1$
		}

		private JMenuItem createSellRateMenuItem(String label, int qty,
			String accelerator)
		{
			return Utility.createMenuItem(label,
				new SellRateGearActionListener(qty), LanguageBundle.
				getFormattedString("in_igSellRateMenuCommand" , qty), (char) 0, //$NON-NLS-1$
				accelerator, LanguageBundle.getFormattedString("in_igSellRateMenuDesc", //$NON-NLS-1$
				(qty < 0) ? "n" : Integer.toString(qty)), null, true); //$NON-NLS-1$
		}
	}

	private class MoveItemListener implements ActionListener
	{
		int moveType; // 0=move, 1=copy
		int pcIndex;

		/**
		 * Constructor
		 * @param index
		 * @param typeOfMove
		 */
		public MoveItemListener(int index, int typeOfMove)
		{
			pcIndex = index;
			moveType = typeOfMove;
		}

		public void actionPerformed(ActionEvent e)
		{
			PlayerCharacter playerCharacter = Globals.getPCList().get(pcIndex);
			Equipment eq = (Equipment) getCurrentSelectedTableItem();

			if (eq == null)
			{
				return;
			}

			if (moveType == 0) // move
			{
				playerCharacter.addEquipment(eq.clone());
				playerCharacter.setDirty(true);
				pc.removeEquipment(eq);
				pc.setDirty(true);
			}
			else
			{
				playerCharacter.addEquipment(eq.clone());
			}

			playerCharacter.setDirty(true);
			updateSelectedModel();
		}
	}

	/**
	 * This class is an ActionListener used to perform a removal of the
	 * equipment item currently selected in the selected table.  Note this
	 * would be equivalent to such an action as drinking a potion, where no
	 * currency is received for the item.
	 *
	 * sage_sam 27 Feb 2003 for FREQ 606205
	 */
	private class RemoveGearActionListener extends SelectedGearActionListener
	{
		RemoveGearActionListener(int qty)
		{
			super(qty);
		}

		protected void updateEquipment(Equipment eq, int argQty)
		{
			if (argQty < -1)
			{
				removeSpecifiedEquipment(eq, eq.qty());
			}
			else
			{
				removeSpecifiedEquipment(eq, argQty);
			}
		}
	}

	private class ResortActionListener implements ActionListener
	{
		boolean sortOrder;
		int sort;

		/**
		 * Constructor
		 * @param i
		 * @param aBool
		 */
		public ResortActionListener(int i, boolean aBool)
		{
			sort = i;
			sortOrder = aBool;
		}

		public void actionPerformed(ActionEvent e)
		{
			resortSelected(sort, sortOrder);
		}
	}

	private static class ResortComparator implements Comparator<Object>
	{
		/** The equipment is to be sorted by name */
		public static final int RESORT_NAME = 0;
		/** The equipment is to be sorted by weight */
		public static final int RESORT_WEIGHT = 1;
		/** The equipment is to be sorted by whether it is equipped */
		public static final int RESORT_EQUIPPED = 2;
		/** The re-sort is ascending by default */
		public static final boolean RESORT_ASCENDING = true;
		/** The re-sort is not descending by default */
		public static final boolean RESORT_DESCENDING = false;
		/** The sort order is set to RESORT_ASCENDING by default */
		private boolean sortOrder = RESORT_ASCENDING;
		private int sort = RESORT_EQUIPPED;
		private PlayerCharacter pc;

		/**
		 * Constructor
		 * @param sort
		 * @param sortOrder
		 * @param pc
		 */
		public ResortComparator(int sort, boolean sortOrder, PlayerCharacter pc)
		{
			this.sort = sort;
			this.sortOrder = sortOrder;
			this.pc = pc;
		}

		// Comparator will be specific to Equipment objects
		public int compare(Object obj1, Object obj2)
		{
			Equipment e1;
			Equipment e2;

			if (sortOrder == RESORT_ASCENDING)
			{
				e1 = (Equipment) obj1;
				e2 = (Equipment) obj2;
			}
			else
			{
				e1 = (Equipment) obj2;
				e2 = (Equipment) obj1;
			}

			switch (sort)
			{
				case RESORT_EQUIPPED:
					EquipSet es1 = getCurrentEqSetItem(e1);
					if (es1 != null)
					{
						e1 = es1.getItem();
					}
					EquipSet es2 = getCurrentEqSetItem(e2);
					if (es2 != null)
					{
						e2 = es2.getItem();
					}
					//Logging.log(Logging.DEBUG, "Sort: 1." + e1.getName() + "/"+ e1.isEquipped() + "/" + (es1 != null && Constants.EQUIP_LOCATION_CARRIED.equals(es1.getName())));
					//Logging.log(Logging.DEBUG, "Sort: 2." + e2.getName() + "/"+ e2.isEquipped() + "/" + (es2 != null && Constants.EQUIP_LOCATION_CARRIED.equals(es2.getName())));
				
					if (e1.isEquipped() && !e2.isEquipped())
					{
						return -1;
					}
					if (!e1.isEquipped() && e2.isEquipped())
					{
						return 1;
					}
					if (!e1.isEquipped() && !e2.isEquipped())
					{
						boolean e1Carried = es1 != null
							&& Constants.EQUIP_LOCATION_CARRIED.equals(es1.getName());

						boolean e2Carried = es2 != null
							&& Constants.EQUIP_LOCATION_CARRIED.equals(es2.getName());

						if (e1Carried != e2Carried)
						{
							return e1Carried ? -1 : 1;
						}
					}
					return e1.getName().compareToIgnoreCase(e2.getName());
					
				case RESORT_WEIGHT:
					return e1.getWeight(pc).compareTo(e2.getWeight(pc));

				case RESORT_NAME:
				default:
					return e1.getName().compareToIgnoreCase(e2.getName());
			}
		}
		

		/**
		 * Retrieve the EquipSet holding the item of equipment for the currently 
		 * active EquipSet
		 * 
		 * @param eqI The equipment item to be found
		 * @return EquipSet The set containing the item
		 **/
		private EquipSet getCurrentEqSetItem(Equipment eqI)
		{
			final String rPath = pc.getCalcEquipSetId();

			for (EquipSet es : pc.getEquipSet())
			{
				String esIdPath = es.getIdPath() + ".";
				String rIdPath = rPath + ".";

				if (!esIdPath.startsWith(rIdPath))
				{
					continue;
				}

				if (eqI.getName().equals(es.getValue()))
				{
					return es;
				}
			}

			return null;
		}
	}

	private class GearPopupListener extends MouseAdapter
	{
		private GearPopupMenu menu;
		private JTree tree;

		private GearPopupListener(JTreeTable treeTable, GearPopupMenu aMenu)
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
				// Walk through the list of accelerators to see if the user has
				// pressed a sequence used by the popup.
				// This would not otherwise happen unless the popup was showing
				//
				public void keyPressed(KeyEvent e)
				{
					final int keyCode = e.getKeyCode();

					if (keyCode != KeyEvent.VK_UNDEFINED)
					{
						final KeyStroke keyStroke =
								KeyStroke.getKeyStrokeForEvent(e);

						for (int i = 0; i < menu.getComponentCount(); i++)
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
									selPath = tree.getSelectionPath();
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
				selPath =
						tree.getClosestPathForLocation(evt.getX(), evt.getY());

				if (selPath == null)
				{
					return;
				}

				if (tree.isSelectionEmpty())
				{
					tree.setSelectionPath(selPath);
					// Workaround for swing bug - initial row wasn't being selected.
					tree.getSelectionModel().resetRowSelection();
					menu.show(evt.getComponent(), evt.getX(), evt.getY());
				}
				else if (!tree.isPathSelected(selPath))
				{
					tree.setSelectionPath(selPath);
					menu.show(evt.getComponent(), evt.getX(), evt.getY());
				}
				else
				{
					tree.addSelectionPath(selPath);
					// Workaround for swing bug - initial row wasn't being selected.
					tree.getSelectionModel().resetRowSelection();
					menu.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			}
		}
	}

	/**
	 * OutputOrderEditor is a JCombobox based table cell editor.
	 * It allows the user to either enter their own output order index
	 * or to select from hidden, first or last. If first or last are
	 * selected, then special values are returned to the setValueAt
	 * method, which are actioned by that method.
	 **/
	private static final class OutputOrderEditor extends JComboBoxEx implements
			TableCellEditor
	{
		private final transient List<CellEditorListener> d_listeners =
				new ArrayList<CellEditorListener>();
		private transient int d_originalValue = 0;

		private OutputOrderEditor(String[] choices)
		{
			super(choices);

			setEditable(true);

			addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent ae)
				{
					stopCellEditing();
				}
			});
		}

		public boolean isCellEditable(EventObject eventObject)
		{
			return true;
		}

		public Object getCellEditorValue()
		{
			switch (this.getSelectedIndex())
			{
				case 0: // First
					return Integer.valueOf(0);

				case 1: // Last
					return Integer.valueOf(1000);

				case 2: // Hidden
					return Integer.valueOf(-1);

				default: // A number

					return Integer.valueOf((String) getSelectedItem());
			}
		}

		public Component getTableCellEditorComponent(JTable jTable,
			Object value, boolean isSelected, int row, int column)
		{
			if (value == null)
			{
				return this;
			}

			d_originalValue = this.getSelectedIndex();

			if (value instanceof Integer)
			{
				int i = ((Integer) value).intValue();

				if (i == -1)
				{
					setSelectedItem("Hidden");
				}
				else
				{
					setSelectedItem(String.valueOf(i));
				}
			}
			else
			{
				setSelectedItem("Hidden");
			}

			jTable.setRowSelectionInterval(row, row);
			jTable.setColumnSelectionInterval(column, column);

			return this;
		}

		public void addCellEditorListener(CellEditorListener cellEditorListener)
		{
			d_listeners.add(cellEditorListener);
		}

		public void cancelCellEditing()
		{
			fireEditingCanceled();
		}

		public void removeCellEditorListener(
			CellEditorListener cellEditorListener)
		{
			d_listeners.remove(cellEditorListener);
		}

		public boolean shouldSelectCell(EventObject eventObject)
		{
			return true;
		}

		public boolean stopCellEditing()
		{
			fireEditingStopped();

			return true;
		}

		private void fireEditingCanceled()
		{
			setSelectedIndex(d_originalValue);

			ChangeEvent ce = new ChangeEvent(this);

			for (int i = d_listeners.size() - 1; i >= 0; --i)
			{
				(d_listeners.get(i)).editingCanceled(ce);
			}
		}

		private void fireEditingStopped()
		{
			ChangeEvent ce = new ChangeEvent(this);

			for (int i = d_listeners.size() - 1; i >= 0; --i)
			{
				(d_listeners.get(i)).editingStopped(ce);
			}
		}
	}

	//End OutputOrderEditor classes

	/**
	 * In the TreeTableModel there is a single <code>root</code>
	 * object.  This root object has a null <code>parent</code>.
	 * All other objects have a parent which points to a non-null object.
	 * parent objects contain a list of <code>children</code>, which are
	 * all the objects that point to it as their parent.
	 * objects (or <code>nodes</code>) which have 0 children
	 * are leafs (the end of that linked list).
	 * nodes which have at least 1 child are not leafs. Leafs are like files
	 * and non-leafs are like directories.
	 **/
	private final class EquipmentModel extends AbstractTreeTableModel implements
			TableColumnManagerModel
	{
		private int currentMode =
				GuiConstants.INFOINVENTORY_VIEW_TYPE_SUBTYPE_NAME;

		private static final int MODEL_TYPE_AVAIL = 0;
		private static final int MODEL_TYPE_SELECTED = 1;

		// Names of the columns.
		private String[] names =
				{LanguageBundle.getString("in_igEqModelColItem"), //$NON-NLS-1$
				LanguageBundle.getString("in_igEqModelColCost"), //$NON-NLS-1$
				LanguageBundle.getString("in_igEqModelColWeight"), //$NON-NLS-1$
				LanguageBundle.getString("in_igEqModelColQty"), //$NON-NLS-1$
				LanguageBundle.getString("in_igEqModelColOrder"), //$NON-NLS-1$
				LanguageBundle.getString("in_igEqModelColSource")}; //$NON-NLS-1$
		private int[] widths = {100, 20, 20, 20, 20, 100};

		// Types of the columns.
		private int modelType = MODEL_TYPE_AVAIL; // availableModel
		private List<Boolean> displayList;

		/**
		 * Creates a EquipmentModel
		 * @param mode
		 * @param available
		 **/
		private EquipmentModel(int mode, boolean available)
		{
			super(null);

			if (!available)
			{
				modelType = MODEL_TYPE_SELECTED;
			}

			resetModel(mode, available);
			int i = 1;
			displayList = new ArrayList<Boolean>();
			displayList.add(Boolean.TRUE);
			displayList.add(Boolean.valueOf(getColumnViewOption(modelType + "." //$NON-NLS-1$
				+ names[i++], true)));
			if (available)
			{
				displayList.add(Boolean.valueOf(getColumnViewOption(modelType
					+ "." + names[i++], false))); //$NON-NLS-1$
				displayList.add(Boolean.valueOf(getColumnViewOption(modelType
					+ "." + names[i++], false))); //$NON-NLS-1$
				displayList.add(Boolean.valueOf(getColumnViewOption(modelType
					+ "." + names[i++], false))); //$NON-NLS-1$
				displayList.add(Boolean.valueOf(getColumnViewOption(modelType
					+ "." + names[i++], true))); //$NON-NLS-1$
			}
			else
			{
				displayList.add(Boolean.valueOf(getColumnViewOption(modelType
					+ "." + names[i++], false))); //$NON-NLS-1$
				displayList.add(Boolean.valueOf(getColumnViewOption(modelType
					+ "." + names[i++], true))); //$NON-NLS-1$
				displayList.add(Boolean.valueOf(getColumnViewOption(modelType
					+ "." + names[i++], true))); //$NON-NLS-1$
				displayList.add(Boolean.valueOf(getColumnViewOption(modelType
					+ "." + names[i++], false))); //$NON-NLS-1$
			}
		}

		public boolean isCellEditable(Object node, int column)
		{
			return ((column == COL_NAME) || ((modelType == MODEL_TYPE_SELECTED)
				&& (((PObjectNode) node).getItem() instanceof Equipment) && ((column == COL_QTY) || (column == COL_INDEX))));
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

				case COL_COST:
					return BigDecimal.class;

				case COL_WEIGHT:
					return Float.class;

				case COL_QTY:
					return Float.class;

				case COL_SRC:
					return String.class;

				case COL_INDEX:
					return Integer.class;

				default:
					Logging
						.errorPrint("In InfoGear.EquipmentModel.getColumnClass the column "
							+ column + " is not supported.");

					break;
			}

			return String.class;
		}

		/* The JTreeTableNode interface. */

		/**
		 * Returns int number of columns.
		 * @return column count
		 */
		public int getColumnCount()
		{
			return names.length;
		}

		/**
		 * Returns String name of a column.
		 * @param column
		 * @return column name
		 */
		public String getColumnName(int column)
		{
			return names[column];
		}

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
			Object retVal = null;

			Equipment eq = null;
			Object temp = fn.getItem();
			if (temp instanceof Equipment)
			{
				eq = (Equipment) temp;
			}

			switch (column)
			{
				case COL_NAME:
					retVal = fn.toString();
					break;

				case COL_COST:
					if (eq != null)
					{
						retVal = eq.getCost(pc);
					}
					break;

				case COL_WEIGHT:
					if (eq != null)
					{
						retVal = eq.getWeight(pc);
					}
					break;

				case COL_QTY:
					if (eq != null)
					{
						retVal = new Float(eq.qty());
					}
					break;

				case COL_INDEX: // Output index
					if (eq != null)
					{
						retVal = Integer.valueOf(eq.getOutputIndex());
						if (eq.isAutomatic())
						{
							// Automatic equip is onstantly recreated, so grab the cached index
							retVal = pc.getCachedOutputIndex(eq.getKeyName());
						}
					}
					break;

				case COL_SRC: // Source
					retVal = fn.getSource().toString();
					break;

				case -1:
					retVal = fn.getItem();
					break;

				default:
					Logging
						.errorPrint("In InfoGear.EquipmentModel.getValueAt the column "
							+ column + " is not supported.");
					break;
			}
			return retVal;
		}

		/**
		 * "There can be only one" There must be a root object, though it can be hidden
		 * to make it's existence basically a convenient way to keep track of the objects
		 *
		 * @param aNode
		 */
		private void setRoot(PObjectNode aNode)
		{
			super.setRoot(aNode);
		}

		public void setValueAt(Object value, Object node, int column)
		{
			if (pc == null)
			{
				return;
			}

			if (modelType != MODEL_TYPE_SELECTED)
			{
				return; // can only set values for selectedTableModel
			}

			Object obj = ((PObjectNode) node).getItem();

			if (!(obj instanceof Equipment))
			{
				return; // can only use rows with Equipment in them
			}

			Equipment selectedEquipment = (Equipment) obj;

			if (getBaseEquipment(selectedEquipment) == null)
			{
				return;
			}

			switch (column)
			{
				case COL_QTY:
					setColumnQty(selectedEquipment, value);
					break;

				case COL_INDEX:
					setColumnIndex(selectedEquipment, value);
					break;

				default:
					Logging
						.errorPrint("In InfoGear.EquipmentModel.setValueAt the column "
							+ column + " is not supported.");
					break;
			}
		}

		private void setColumnQty(Equipment eq, Object value)
		{
			double qtyToAdd = ((Float) value).floatValue() - eq.qty();

			if (qtyToAdd > 0.0)
			{
				buySpecifiedEquipment(eq, qtyToAdd);
			}
			else if (qtyToAdd < 0.0)
			{
				sellSpecifiedEquipment(eq, -qtyToAdd);
			}
		}

		private void setColumnIndex(Equipment eq, Object value)
		{
			int outputIndex = ((Integer) value).intValue();
			int workingIndex = 1;

			if (outputIndex == 1000)
			{ // Last
				// Set it to one higher that the highest output index so far
				outputIndex = getHighestOutputIndex() + 1;
			}
			else if (outputIndex == 0)
			{ // First
				// Set it to 1 and shuffle everyone up in order
				outputIndex = 1;
				workingIndex = 2;
			}

			eq.setOutputIndex(outputIndex);

			for (Equipment item : pc.getEquipmentMasterListInOutputOrder())
			{
				if (workingIndex == outputIndex)
				{
					workingIndex++;
				}

				if ((item.getOutputIndex() > -1) && (item != eq))
				{
					item.setOutputIndex(workingIndex++);
					if (item.isAutomatic())
					{
						pc.cacheOutputIndex(item);
					}
				}
			}

			selectedModel.updateTree();
			selectedTable.updateUI();
			if (eq.isAutomatic())
			{
				pc.cacheOutputIndex(eq);
			}
			pc.setDirty(true);
		}

		private Equipment getBaseEquipment(Equipment selectedEquipment)
		{
			String keyName = selectedEquipment.getKeyName();
			Equipment baseEquipment = Globals.getContext().ref
					.silentlyGetConstructedCDOMObject(Equipment.class, keyName);

			if (baseEquipment == null)
			{
				baseEquipment = pc.getEquipmentNamed(keyName);
			}

			return baseEquipment;
		}

		private void setValueForItemInNodes(PObjectNode p, Equipment e,
			double f, int column)
		{
			if (p == null)
			{
				p = (PObjectNode) super.getRoot();
			}

			Object obj = p.getItem();

			// if no children, remove it and update parent
			if ((p.getChildCount() == 0) && (obj != null)
				&& (obj instanceof Equipment) && obj.equals(e))
			{
				final Equipment pe = (Equipment) obj;

				switch (column)
				{
					case COL_QTY:
						pe.setQty(new Float(f));

						if (pe.getCarried().floatValue() > f)
						{
							pe.setNumberCarried(new Float(f));
						}

						break;

					default:
						Logging
							.errorPrint("In InfoGear.EquipmentModel.setValueForItemInNodes the column "
								+ column + " is not supported.");

						break;
				}
			}
			else
			{
				for (int i = 0; i < p.getChildCount(); i++)
				{
					setValueForItemInNodes(p.getChild(i), e, f, column);
				}
			}
		}

		private void addItemToModel(Equipment eq, boolean fireEvent)
		{
			PObjectNode rootAsPObjectNode = (PObjectNode) super.getRoot();

			if (eq == null || !shouldDisplayThis(eq))
			{
				return;
			}

			switch (currentMode)
			{
				case GuiConstants.INFOINVENTORY_VIEW_TYPE_SUBTYPE_NAME: // Type/SubType/Name
					addChildTypeSubtypeName(eq, rootAsPObjectNode, fireEvent);
					break;

				case GuiConstants.INFOINVENTORY_VIEW_TYPE_NAME: // Type/Name
					addChildTypeName(eq, rootAsPObjectNode, fireEvent);
					break;

				case GuiConstants.INFOINVENTORY_VIEW_NAME: // Name
					addChildName(eq, rootAsPObjectNode, fireEvent);
					break;

				case GuiConstants.INFOINVENTORY_VIEW_ALL_TYPES: // All Types... every unique TYPE is listed
					addChildAllTypes(eq, rootAsPObjectNode, fireEvent);
					break;

				case GuiConstants.INFOINVENTORY_VIEW_SOURCE_NAME: // Source/Name
					addChildSourceName(eq, rootAsPObjectNode, fireEvent);
					break;

				default:
					Logging
						.errorPrint("In InfoGear.EquipmentModel.addItemToModel (second switch) the mode "
							+ currentMode + " is not supported.");
					break;
			}
		}

		private void addChildTypeSubtypeName(Equipment eq,
			PObjectNode rootAsPObjectNode, boolean fireEvent)
		{
			if (fireEvent)
			{
				//Add custom node if it does not exist
				if (eq.isType(Constants.TYPE_CUSTOM))
				{
					addChild(Constants.TYPE_CUSTOM, typeSubtypeRoot, true);
				}

				//Add item's type to the root
				String type = eq.typeIndex(0);
				addChild(type, typeSubtypeRoot, true);

				// Now add any missing subtypes to type/subtype/name tree
				PObjectNode typeSubtypeRootAsPObjectNode =
						(PObjectNode) typeSubtypeRoot;
				for (String s : eq.typeList())
				{
					for (int i = 0; i < typeSubtypeRootAsPObjectNode
						.getChildCount(); i++)
					{
						final String treeType =
								typeSubtypeRootAsPObjectNode.getChild(i)
									.toString();
						if ((typeSubtypeRootAsPObjectNode.getChild(i).getItem() instanceof PObject)
							|| !eq.isType(treeType) || s.equals(treeType))
						{
							continue;
						}
						addChild(s, typeSubtypeRootAsPObjectNode.getChild(i),
							true);
					}
				}
			}

			//Add Equipment
			for (int i = 0; i < rootAsPObjectNode.getChildCount(); i++)
			{
				if (eq.isType(rootAsPObjectNode.getChild(i).toString()))
				{
					// Items with only 1 type will not show up unless we do this
					List<PObjectNode> d;

					if (eq.typeList().size() == 1)
					{
						d = new ArrayList<PObjectNode>(1);
						d.add(rootAsPObjectNode.getChild(i));
					}
					else
					{
						d = rootAsPObjectNode.getChild(i).getChildren();
					}

					for (int k = 0; (d != null) && (k < d.size()); k++)
					{
						// Don't add children to items (those with only 1 type)
						if (!((d.get(k)).getItem() instanceof PObject))
						{
							if (eq.isType((d.get(k)).toString()))
							{
								addChild(eq, d.get(k), fireEvent);
							}
						}
					}
				}
			}
		}

		private void addChildTypeName(Equipment eq,
			PObjectNode rootAsPObjectNode, boolean fireEvent)
		{
			if (fireEvent)
			{
				//Add custom node if it does not exist
				if (eq.isType(Constants.TYPE_CUSTOM))
				{
					addChild(Constants.TYPE_CUSTOM, typeRoot, true);
				}

				//Add Type
				String type = eq.typeIndex(0);
				addChild(type, typeRoot, true);
			}

			//Add Equipment
			int length = rootAsPObjectNode.getChildCount(); // seperated out for performance reasons
			for (int i = 0; i < length; i++)
			{
				if (eq.isType(rootAsPObjectNode.getChild(i).toString()))
				{
					addChild(eq, rootAsPObjectNode.getChild(i), fireEvent);
				}
			}
		}

		private void addChildName(Equipment eq, PObjectNode rootAsPObjectNode,
			boolean fireEvent)
		{
			//Add Equipment
			addChild(eq, rootAsPObjectNode, fireEvent);
		}

		private void addChildAllTypes(Equipment eq,
			PObjectNode rootAsPObjectNode, boolean fireEvent)
		{
			if (fireEvent)
			{
				// Add Types
				for (String type : eq.typeList())
				{
					addChild(type, allTypeRoot, true);
				}
			}

			//Add Equipment
			for (int i = 0; i < rootAsPObjectNode.getChildCount(); i++)
			{
				if (eq.isType(rootAsPObjectNode.getChild(i).toString()))
				{
					addChild(eq, rootAsPObjectNode.getChild(i), fireEvent);
				}
			}
		}

		private void addChildSourceName(Equipment eq,
			PObjectNode rootAsPObjectNode, boolean fireEvent)
		{
			final String sourceString = SourceFormat.getFormattedString(
					eq, SourceFormat.LONG, false);
			if (fireEvent)
			{
				//Add custom node if it does not exist
				if (eq.isType(Constants.TYPE_CUSTOM))
				{
					addChild(Constants.SOURCE_CUSTOM, sourceRoot, true);
				}

				//Add Type
				addChild(sourceString, sourceRoot, true);
			}

			//Add Equipment
			int length = rootAsPObjectNode.getChildCount(); // seperated out for performance reasons
			for (int i = 0; i < length; i++)
			{
				if (sourceString.length() == 0)
				{
					Logging.errorPrintLocalised("in_igLogGearHasNoLongSource", 
							eq.getName() ); //$NON-NLS-1$
				}
				else if (sourceString.equals(rootAsPObjectNode.getChild(i)
					.toString()))
				{
					addChild(eq, rootAsPObjectNode.getChild(i), fireEvent);
				}
			}
		}

		private boolean addChild(Object aChild, Object aParent, boolean sort)
		{
			PObjectNode aFN = new PObjectNode();
			aFN.setItem(aChild);
			aFN.setParent((PObjectNode) aParent);

			if (aChild instanceof Equipment)
			{
				Equipment eq = (Equipment) aChild;
				PrereqHandler.passesAll(eq.getPrerequisiteList(), pc, eq);
			}

			return ((PObjectNode) aParent).addChild(aFN, sort);
		}

		private void removeItemFromNodes(PObjectNode p, Object e)
		{
			if (p == null)
			{
				p = (PObjectNode) super.getRoot();
			}
			p.removeItemFromNodes(e);
		}

		/**
		 * This assumes the EquipmentModel exists but needs to be repopulated
		 * @param mode
		 * @param available
		 */
		private void resetModel(int mode, boolean available)
		{
			Collection<Equipment> eqList;

			//TODO (DJ) Equipment fix, make this more efficient
			if (available)
			{
				eqList = Globals.getContext().ref.getConstructedCDOMObjects(Equipment.class);
			}
			else
			{
				eqList = pc.getEquipmentMasterList();
			}

			currentMode = mode;
			String qFilter = this.getQFilter();

			switch (mode)
			{
				case GuiConstants.INFOINVENTORY_VIEW_TYPE_SUBTYPE_NAME: // Type/SubType/Name
					setRoot(((PObjectNode) typeSubtypeRoot).clone());
					break;

				case GuiConstants.INFOINVENTORY_VIEW_TYPE_NAME: // Type/Name
					setRoot(((PObjectNode) typeRoot).clone());
					break;

				case GuiConstants.INFOINVENTORY_VIEW_NAME: // Name
					setRoot(new PObjectNode()); // just need a blank one
					break;

				case GuiConstants.INFOINVENTORY_VIEW_ALL_TYPES: // All Types... every unique TYPE is listed
					setRoot(((PObjectNode) allTypeRoot).clone());
					break;

				case GuiConstants.INFOINVENTORY_VIEW_SOURCE_NAME: // Type/Name
					setRoot(((PObjectNode) sourceRoot).clone());
					break;

				default:
					Logging
						.errorPrint("In InfoGear.EquipmentModel.resetModel the mode "
							+ mode + " is not supported.");
					break;
			}

			for (Equipment aEq : eqList)
			{
				if (qFilter == null
					|| (aEq.getName().toLowerCase().indexOf(qFilter) >= 0 || aEq
						.getType().toLowerCase().indexOf(qFilter) >= 0))
				{
					addItemToModel(aEq, false);
				}
			}

			PObjectNode rootAsmyPONode = (PObjectNode) super.getRoot();

			if (rootAsmyPONode.getChildCount() > 0)
			{
				fireTreeNodesChanged(super.getRoot(), new TreePath(super
					.getRoot()));
			}
		}

		/**
		 * return a boolean to indicate if the item should be included in the list.
		 * Only Weapon, Armor and Shield type items should be checked for proficiency.
		 *
		 * update for new filtering
		 * author: Thomas Behr 09-02-02
		 * @param equip
		 * @return true if should be displayed
		 */
		private boolean shouldDisplayThis(Equipment equip)
		{
			if (modelType == MODEL_TYPE_AVAIL)
			{
				return accept(pc, equip);
			}
			return true;
		}

		public List<String> getMColumnList()
		{
			List<String> retList = new ArrayList<String>();
			for (int i = 1; i < names.length; i++)
			{
				retList.add(names[i]);
			}
			return retList;
		}

		/**
		 * Get the column align list
		 * @return the column align list
		 */
		public List<String> getMColumnAlignList()
		{
			List<String> retAlignList = new ArrayList<String>();
			for (int i = 1; i < names.length; i++)
			{
				retAlignList.add(names[i]);
			}
			return retAlignList;
		}

		public boolean isMColumnDisplayed(int col)
		{
			return (displayList.get(col)).booleanValue();
		}

		public void setMColumnDisplayed(int col, boolean disp)
		{
			setColumnViewOption(modelType + "." + names[col], disp); //$NON-NLS-1$
			displayList.set(col, Boolean.valueOf(disp));
		}

		public int getMColumnOffset()
		{
			return 1;
		}

		public int getMColumnDefaultWidth(int col)
		{
			return SettingsHandler.getPCGenOption("InfoGear.sizecol." //$NON-NLS-1$
				+ names[col], widths[col]);
		}

		public void setMColumnDefaultWidth(int col, int width)
		{
			SettingsHandler.setPCGenOption("InfoGear.sizecol." + names[col], //$NON-NLS-1$
				width);
		}

		private boolean getColumnViewOption(String colName, boolean defaultVal)
		{
			return SettingsHandler.getPCGenOption(
				"InfoGear.viewcol." + colName, defaultVal); //$NON-NLS-1$
		}

		private void setColumnViewOption(String colName, boolean val)
		{
			SettingsHandler.setPCGenOption("InfoGear.viewcol." + colName, val); //$NON-NLS-1$
		}

		public void resetMColumn(int col, TableColumn column)
		{
			switch (col)
			{
				case COL_COST:
				case COL_WEIGHT:
				case COL_QTY:
					column
						.setCellRenderer(new pcgen.gui.utils.JTableEx.AlignCellRenderer(
							SwingConstants.CENTER));
					break;

				default:
					break;
			}
		}
	}

	private static final class QuantityEditor extends JTextField implements
			TableCellEditor
	{
		private final transient List<CellEditorListener> d_listeners =
				new ArrayList<CellEditorListener>();
		private transient String d_originalValue = ""; //$NON-NLS-1$

		private QuantityEditor()
		{
			super();
			this.setAlignmentX(Component.RIGHT_ALIGNMENT);
		}

		public boolean isCellEditable(EventObject eventObject)
		{
			return true;
		}

		public Object getCellEditorValue()
		{
			try
			{
				return new Float(getText());
			}
			catch (NumberFormatException nfe)
			{
				return new Float(d_originalValue);
			}
		}

		public Component getTableCellEditorComponent(JTable jTable, Object obj,
			boolean isSelected, int row, int column)
		{
			if (obj instanceof Number
				&& (((Number) obj).intValue() == ((Number) obj).floatValue()))
			{
				setText(Integer.toString(((Number) obj).intValue()));
			}
			else
			{
				if (obj != null)
				{
					setText(obj.toString());
				}
				else
				{
					setText("0"); //$NON-NLS-1$
				}
			}

			d_originalValue = getText();
			jTable.setRowSelectionInterval(row, row);
			jTable.setColumnSelectionInterval(column, column);
			this.setAlignmentX(RIGHT_ALIGNMENT);
			selectAll();

			return this;
		}

		public void addCellEditorListener(CellEditorListener cellEditorListener)
		{
			d_listeners.add(cellEditorListener);
		}

		public void cancelCellEditing()
		{
			fireEditingCanceled();
		}

		public void removeCellEditorListener(
			CellEditorListener cellEditorListener)
		{
			d_listeners.remove(cellEditorListener);
		}

		public boolean shouldSelectCell(EventObject eventObject)
		{
			return true;
		}

		public boolean stopCellEditing()
		{
			fireEditingStopped();

			return true;
		}

		private void fireEditingCanceled()
		{
			setText(d_originalValue);

			ChangeEvent ce = new ChangeEvent(this);

			for (int i = d_listeners.size() - 1; i >= 0; --i)
			{
				(d_listeners.get(i)).editingCanceled(ce);
			}
		}

		private void fireEditingStopped()
		{
			ChangeEvent ce = new ChangeEvent(this);

			for (int i = d_listeners.size() - 1; i >= 0; --i)
			{
				(d_listeners.get(i)).editingStopped(ce);
			}
		}
	}

	/**
	 * This class is an ActionListener used to perform a sale of the
	 * equipment item currently selected in the selected table at the
	 * rate selected in the buy rate combo box.
	 *
	 * sage_sam 27 Feb 2003 for FREQ 606205
	 */
	private class SellGearActionListener extends SelectedGearActionListener
	{
		SellGearActionListener(int qty)
		{
			super(qty);
		}

		protected void updateEquipment(Equipment eq, int argQty)
		{
			if (argQty < -1)
			{
				sellSpecifiedEquipment(eq, eq.qty());
			}
			else
			{
				sellSpecifiedEquipment(eq, argQty);
			}
		}
	}

	/**
	 * This class is an ActionListener used to perform a sale of the
	 * equipment item currently selected in the selected table at the
	 * rate selected at the time the action is performed.
	 *
	 * sage_sam 27 Feb 2003 for FREQ 606205
	 */
	private class SellRateGearActionListener extends SelectedGearActionListener
	{
		SellRateGearActionListener(int qty)
		{
			super(qty);
		}

		protected void updateEquipment(Equipment eq, int argQty)
		{
			if (argQty < -1)
			{
				sellSpecifiedEquipmentRate(eq);
			}
			else
			{
				sellSpecifiedEquipmentRate(eq, argQty);
			}
		}
	}
}
