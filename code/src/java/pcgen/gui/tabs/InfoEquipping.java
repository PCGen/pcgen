/*
 * InfoEquipping.java
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
 * Created on April 29th, 2002, 2:15 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.gui.tabs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.core.BonusManager;
import pcgen.core.Equipment;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.SystemCollections;
import pcgen.core.BonusManager.TempBonusInfo;
import pcgen.core.analysis.OutputNameFormatting;
import pcgen.core.bonus.BonusObj;
import pcgen.core.character.EquipSet;
import pcgen.core.character.EquipSlot;
import pcgen.core.character.WieldCategory;
import pcgen.core.prereq.PrerequisiteUtilities;
import pcgen.core.system.LoadInfo;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui.CharacterInfo;
import pcgen.gui.CharacterInfoTab;
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
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.WeightToken;
import pcgen.util.BigDecimalHelper;
import pcgen.util.FOPHandler;
import pcgen.util.InputFactory;
import pcgen.util.InputInterface;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;
import pcgen.util.chooser.ChooserRadio;
import pcgen.util.enumeration.Load;
import pcgen.util.enumeration.Tab;

/**
 * <code>InfoEquipping</code> creates a new tabbed panel that is used to
 * allow different combinations of equipment for printing on csheets
 *
 * @author  Jayme Cox <jaymecox@users.sourceforge.net>
 * @version $Revision$
 **/
public class InfoEquipping extends FilterAdapterPanel implements
		CharacterInfoTab
{
	static final long serialVersionUID = 6988134124127535195L;

	private static final Tab tab = Tab.EQUIPPING;

	private static List<EquipSet> equipSetList = new ArrayList<EquipSet>();
	private static List<EquipSet> tempSetList = new ArrayList<EquipSet>();
	//	private static final String[] loadTypes = { "LIGHT", "MEDIUM", "HEAVY", "OVERLOADED" };
	private static final String defaultEquipSet =
			PropertyFactory.getString("in_ieDefault");
	private static final String nameAdded =
			PropertyFactory.getString("in_ieAddEqSet");
	private static final String nameNotAdded =
			PropertyFactory.getString("in_ieNotAdd");
	private static int splitOrientation = JSplitPane.HORIZONTAL_SPLIT;
	private static boolean needsUpdate = true;

	// table model modes
	private static final int MODEL_AVAIL = 0;
	private static final int MODEL_SELECTED = 1;

	//column positions for tables
	// if you change these, you also need to change
	// the selNameList array in the EquipModel class
	private static final int COL_NAME = 0;
	private static final int COL_TYPE = 1;
	private static final int COL_QTY = 2;
	private static final int COL_LOCATION = 3;
	private static final int COL_COST = 4;
	private static final int COL_WEIGHT = 5;
	private static final int COL_BONUS = 6;
	private EquipModel availableModel = null; // Model for JTreeTable
	private EquipModel selectedModel = null; // Model for JTreeTable
	private final JLabel avaLabel =
			new JLabel(PropertyFactory.getString("in_ieSort"));
	private final JLabel calcLabel =
			new JLabel(PropertyFactory.getString("in_ieCalc"));
	private final JLabel loadLabel =
			new JLabel(PropertyFactory.getString("in_load") + ": ");
	private final JLabel weightLabel =
			new JLabel(PropertyFactory.getString("in_weight") + ": ");
	private final JLabel loadLimitsLabel =
			new JLabel(PropertyFactory.getString("in_loadlimits") + ": ");
	private FlippingSplitPane asplit;
	private FlippingSplitPane bsplit;
	private FlippingSplitPane splitPane;
	private JButton addEquipButton;
	private JButton addEquipSetButton;
	private JButton delEquipButton;
	private JButton delEquipSetButton;
	private JButton exportEqSetButton;
	private JButton selectTemplateButton;
	private JButton setNoteButton;
	private JButton setQtyButton;
	private JButton viewEqSetButton;
	private JComboBoxEx calcComboBox = new JComboBoxEx();
	private JComboBoxEx viewComboBox = new JComboBoxEx();
	private JLabelPane infoLabel = new JLabelPane();
	private final JTextField loadWeight = new JTextField();
	private final JTextField loadLimits = new JTextField();
	private final JTextField totalWeight = new JTextField();
	private JMenuItem AddAllMenu;
	private JMenuItem AddMenu;
	private JMenuItem AddNumMenu;
	private JMenuItem BuyMenu;
	private JMenuItem BuyNumMenu;
	private JMenuItem CopyEquipSetMenu;
	private JMenuItem DelMenu;
	private JMenuItem RenameEquipSetMenu;
	private JMenuItem SellMenu;
	private JMenuItem SellNumMenu;
	private JMenuItem SetLocationMenu;
	private JMenuItem SetNoteMenu;
	private JMenuItem SetQtyMenu;
	private JPanel botPane = new JPanel();
	private JPanel topPane = new JPanel();
	private JTextField equipSetTextField = new JTextField();
	private JTextField templateTextField = new JTextField();
	private JTreeTable availableTable; // available Equipment
	private JTreeTable selectedTable; // Equipment Sets
	private JTreeTableSorter availableSort = null;
	private JTreeTableSorter selectedSort = null;
	private String selectedEquipSet = "";
	private TreePath selPath;
	private boolean hasBeenSized = false;
	private int viewMode = 0;
	private int viewSelectMode = 0;
	private Map<String, Float> equipAddMap = new HashMap<String, Float>();
	private Map<String, Float> equipNotMap = new HashMap<String, Float>();

	private final JLabel lblQFilter = new JLabel(PropertyFactory.getString("InfoTabs.FilterLabel"));
	private JTextField textQFilter = new JTextField();
	private JButton clearQFilterButton = new JButton(PropertyFactory.getString("in_clear"));
	private static Integer saveViewMode = null;

	private PlayerCharacter pc;
	private int serial = 0;
	private boolean readyForRefresh = false;
	private ActionListener calcComboBoxListener;

	/**
	 *  Constructor for the InfoEquips object
	 * @param pc
	 */
	public InfoEquipping(PlayerCharacter pc)
	{
		this.pc = pc;
		// do not remove this as we will use the component's name
		// to save component specific settings
		setName(tab.toString());

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
		}
	}

	public PlayerCharacter getPc()
	{
		return pc;
	}

	public int getTabOrder()
	{
		return SettingsHandler.getPCGenOption(".Panel.Equipping.Order", tab
			.ordinal());
	}

	public void setTabOrder(int order)
	{
		SettingsHandler.setPCGenOption(".Panel.Equipping.Order", order);
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

		boolean hasEquip = false;
		for (EquipSet es : pc.getEquipSet())
		{
			if (es.getItem() != null)
			{
				hasEquip = true;
				break;
			}
		}
		if (!hasEquip && !pc.getEquipmentMasterList().isEmpty())
		{
			toDoList.add(PropertyFactory.getString("in_ieTodoEquip")); //$NON-NLS-1$
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
	 * specifies whether the "negate/reverse" option should be available
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
		updateAvailableModel();
		updateSelectedModel();
	}

	/**
	 * This recalculates the states of everything based
	 * upon the currently selected character.
	 * But first test to see if we need to do anything
	 **/
	private final void updateCharacterInfo()
	{
		if ((pc == null) || !needsUpdate)
		{
			return;
		}

		EquipSet cES = pc.getEquipSetByIdPath(pc.getCalcEquipSetId());

		if (cES != null)
		{
			selectedEquipSet = cES.getName();
			equipSetTextField.setText(selectedEquipSet);
		}

		calcComboBoxFill();
		updateTotalWeight();

		updateAvailableModel();
		updateSelectedModel();

		needsUpdate = false;
	}

	/**
	 * returns the Parent Node EquipSet
	 * @return current EquipSet
	 **/
	private EquipSet getCurrentEquipSet()
	{
		TreePath ceSelPath = selectedTable.getTree().getSelectionPath();
		String equipSetName;
		EquipSet eSet = null;

		if (ceSelPath == null)
		{
			equipSetName = equipSetTextField.getText();
			eSet = pc.getEquipSetByName(equipSetName);
		}
		else
		{
			Object endComp = ceSelPath.getPathComponent(1);
			MyPONode fNode = (MyPONode) endComp;

			if ((fNode.getItem() instanceof EquipSet))
			{
				eSet = (EquipSet) fNode.getItem();
			}
		}

		return eSet;
	}

	/**
	 * returns the primary location Name an an equipment item
	 * @param eqI
	 * @return equipment type name
	 **/
	private static String getEqTypeName(Equipment eqI)
	{
		String locTypeName = "";

		if (eqI.isWeapon())
		{
			locTypeName = PropertyFactory.getString("in_ieLocNameWeapon");
		}
		else if (eqI.isArmor())
		{
			locTypeName = PropertyFactory.getString("in_ieLocNameArmor");
		}
		else if (eqI.isShield())
		{
			locTypeName = PropertyFactory.getString("in_ieLocNameShield");
		}
		else if (eqI.isAmmunition())
		{
			locTypeName = PropertyFactory.getString("in_ieLocNameAmmo");
		}
		else if (eqI.isSuit())
		{
			locTypeName = PropertyFactory.getString("in_ieLocNameSuit");
		}
		else if (eqI.isMonk())
		{
			locTypeName = PropertyFactory.getString("in_ieLocNameMonk");
		}
		else if (eqI.isUnarmed())
		{
			locTypeName = PropertyFactory.getString("in_ieLocNameUnarmed");
		}
		else if (eqI.isContainer())
		{
			locTypeName = PropertyFactory.getString("in_ieLocNameContainer");
		}
		else if (eqI.isType("ROBE"))
		{
			locTypeName = PropertyFactory.getString("in_ieLocNameRobe");
		}
		else if (eqI.isType("HEADGEAR"))
		{
			locTypeName = PropertyFactory.getString("in_ieLocNameHeadgear");
		}
		else if (eqI.isType("EYEGEAR"))
		{
			locTypeName = PropertyFactory.getString("in_ieLocNameEyegear");
		}
		else if (eqI.isType("MASK"))
		{
			locTypeName = PropertyFactory.getString("in_ieLocNameMask");
		}
		else if (eqI.isType("AMULET") || eqI.isType("NECKLACE"))
		{
			locTypeName = PropertyFactory.getString("in_ieLocNameAmulet");
		}
		else if (eqI.isType("CAPE") || eqI.isType("CLOAK"))
		{
			locTypeName = PropertyFactory.getString("in_ieLocNameCape");
		}
		else if (eqI.isType("CLOTHING"))
		{
			locTypeName = PropertyFactory.getString("in_ieLocNameClothing");
		}
		else if (eqI.isType("SHIRT") || eqI.isType("VEST"))
		{
			locTypeName = PropertyFactory.getString("in_ieLocNameShirt");
		}
		else if (eqI.isType("BRACER") || eqI.isType("ARMWEAR"))
		{
			locTypeName = PropertyFactory.getString("in_ieLocNameBracers");
		}
		else if (eqI.isType("GLOVE"))
		{
			locTypeName = PropertyFactory.getString("in_ieLocNameGlove");
		}
		else if (eqI.isType("RING"))
		{
			locTypeName = PropertyFactory.getString("in_ieLocNameRing");
		}
		else if (eqI.isType("BELT"))
		{
			locTypeName = PropertyFactory.getString("in_ieLocNameBelt");
		}
		else if (eqI.isType("BOOT"))
		{
			locTypeName = PropertyFactory.getString("in_ieLocNameBoot");
		}
		else if (eqI.isType("POTION"))
		{
			locTypeName = PropertyFactory.getString("in_ieLocNamePotion");
		}
		else if (eqI.isType("ROD"))
		{
			locTypeName = PropertyFactory.getString("in_ieLocNameRod");
		}
		else if (eqI.isType("STAFF"))
		{
			locTypeName = PropertyFactory.getString("in_ieLocNameStaff");
		}
		else if (eqI.isType("WAND"))
		{
			locTypeName = PropertyFactory.getString("in_ieLocNameWand");
		}
		else if (eqI.isType("INSTRUMENT"))
		{
			locTypeName = PropertyFactory.getString("in_ieLocNameInstrument");
		}
		else if (eqI.isType("BOOK"))
		{
			locTypeName = PropertyFactory.getString("in_ieLocNameBook");
		}

		return locTypeName;
	}

	/**
	 * set the equipment Info text in the Equipment Info
	 * panel to the currently selected equipment
	 * @param eqI
	 **/
	private void setInfoLabelText(Equipment eqI)
	{

		if (eqI != null)
		{
			StringBuffer title = new StringBuffer(40);
			title.append(OutputNameFormatting.piString(eqI, false));
			if (!eqI.longName().equals(eqI.getName()))
			{
				title.append("(").append(eqI.longName()).append(")");
			}
			final InfoLabelTextBuilder b = new InfoLabelTextBuilder(title.toString());

			b.appendLineBreak();
			b.appendI18nElement("in_ieInfoLabelTextType",eqI.getType()); //$NON-NLS-1$

			//
			// Should only be meaningful for weapons, but if included on some other piece of
			// equipment, show it anyway
			//
			if (eqI.isWeapon() || eqI.get(ObjectKey.WIELD) != null)
			{
				WieldCategory wCat = eqI.getEffectiveWieldCategory(pc);
				b.appendSpacer();
				b.appendI18nElement("in_ieInfoLabelTextWield",wCat.getKeyName()); //$NON-NLS-1$
			}

			//
			// Only meaningful for weapons, armor and shields
			//
			if (eqI.isWeapon() || eqI.isArmor() || eqI.isShield())
			{
				b.appendSpacer();
				b.appendI18nElement(
						"in_ieInfoLabelTextProficient",
						((pc.isProficientWith(eqI) && eqI.meetsPreReqs(pc))
							? PropertyFactory
								.getString("in_ieInfoLabelTextYes")
							: (SettingsHandler.getPrereqFailColorAsHtmlStart()
								+ PropertyFactory
									.getString("in_ieInfoLabelTextNo") + SettingsHandler
								.getPrereqFailColorAsHtmlEnd())));
			}

			String bString =
					Globals.getGameModeUnitSet().displayWeightInUnitSet(
						eqI.getWeight(pc).doubleValue());
			if (bString.length() > 0)
			{
				b.appendSpacer();
				b.appendI18nElement("in_ieInfoLabelTextWeight",bString); //$NON-NLS-1$
				b.append(Globals.getGameModeUnitSet().getWeightUnit());
			}

			Integer a = eqI.getACBonus(pc);
			if (a.intValue() > 0)
			{
				b.appendSpacer();
				b.appendI18nElement("in_ieInfoLabelTextAC",a.toString()); //$NON-NLS-1$
			}

			if (eqI.isArmor() || eqI.isShield())
			{
				a = eqI.getMaxDex(pc);
				b.appendSpacer();
				b.appendI18nElement("in_ieInfoLabelTextMaxDex",a.toString()); //$NON-NLS-1$

				a = eqI.acCheck(pc);
				b.appendSpacer();
				b.appendI18nElement("in_ieInfoLabelTextAcCheck",a.toString()); //$NON-NLS-1$
			}

			if (Globals.getGameModeShowSpellTab())
			{
				a = eqI.spellFailure(pc);

				if (eqI.isArmor() || eqI.isShield() || (a.intValue() != 0))
				{
					b.appendSpacer();
					b.appendI18nElement("in_ieInfoLabelTextArcaneFailure",a.toString()); //$NON-NLS-1$
				}
			}

			bString = Globals.getGameModeDamageResistanceText();

			if (bString.length() != 0)
			{
				a = eqI.eDR(pc);

				if (eqI.isArmor() || eqI.isShield() || (a.intValue() != 0))
				{
					b.appendSpacer();
					b.appendElement(bString, a.toString());
				}
			}

			bString = eqI.moveString();

			if (bString.length() > 0)
			{
				b.appendSpacer();
				b.appendI18nElement("in_ieInfoLabelTextMove",bString); //$NON-NLS-1$
			}

			bString = eqI.getSize();

			if (bString.length() > 0)
			{
				b.appendSpacer();
				b.appendI18nElement("in_ieInfoLabelTextSize",bString); //$NON-NLS-1$
			}

			bString = eqI.getDamage(pc);

			if (bString.length() > 0)
			{
				b.appendSpacer();
				b.appendI18nElement("in_ieInfoLabelTextDamage",bString); //$NON-NLS-1$

				if (eqI.isDouble())
				{
					b.append('/').append(eqI.getAltDamage(pc));
				}
			}

			int critrange = pc.getCritRange(eqI, true);
			int altcritrange = pc.getCritRange(eqI, false);
			bString = critrange == 0 ? "" : Integer.toString(critrange);
			if (eqI.isDouble() && critrange != altcritrange)
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

			bString = eqI.getCritMult();
			if (eqI.isDouble()
					&& !(eqI.getCritMultiplier() == eqI.getAltCritMultiplier()))
			{
				bString += "/" + eqI.getAltCritMult();
			}

			if (bString.length() > 0)
			{
				b.appendSpacer();
				b.appendI18nElement("in_ieInfoLabelTextCritMult",bString); //$NON-NLS-1$
			}

			if (eqI.isWeapon())
			{
				bString =
						Globals.getGameModeUnitSet().displayDistanceInUnitSet(
							eqI.getRange(pc).intValue());

				if (bString.length() > 0)
				{
					b.appendSpacer();
					b.appendI18nElement("in_ieInfoLabelTextRange",bString); //$NON-NLS-1$
				}
			}

			final int charges = eqI.getRemainingCharges();

			if (charges >= 0)
			{
				b.appendSpacer();
				b.appendI18nElement("in_ieInfoLabelTextCharges",String.valueOf(charges)); //$NON-NLS-1$
			}

			b.appendSpacer();
			b.appendI18nElement("in_ieInfoLabelTextCost",eqI.getCost(pc).toString()); //$NON-NLS-1$
			b.append(Globals.getCurrencyDisplay());

			String IDS = eqI.getInterestingDisplayString(pc);
			if (IDS.length() > 0)
			{
				b.appendLineBreak();
				b.appendI18nElement("in_ieInfoLabelTextProperties",IDS); //$NON-NLS-1$
			}

			bString = eqI.getContainerCapacityString();
			if (bString.length() > 0)
			{
				b.appendLineBreak();
				b.appendI18nElement("in_ieInfoLabelTextContainer",bString); //$NON-NLS-1$
			}

			bString = eqI.getContainerContentsString();
			if (bString.length() > 0)
			{
				b.appendSpacer();
				b.appendI18nElement("in_ieInfoLabelTextCurrentlyContains",bString); //$NON-NLS-1$

				BigDecimal d =
						new BigDecimal(String
							.valueOf(eqI.getContainedValue(pc)));
				String aVal =
						BigDecimalHelper.formatBigDecimal(d, 2).toString();
				b.appendSpacer();
				b.appendI18nElement("in_ieInfoLabelTextContainedValue",aVal); //$NON-NLS-1$
				b.append(Globals.getCurrencyDisplay());
			}

			final String cString = PrerequisiteUtilities.preReqHTMLStringsForList(pc, null,
			eqI.getPrerequisiteList(), false);
			if (cString.length() > 0)
			{
				b.appendLineBreak();
				b.appendI18nElement("in_ieInfoLabelTextRequirements",cString); //$NON-NLS-1$
			}

			bString = SourceFormat.getFormattedString(eqI,
			Globals.getSourceDisplay(), true);
			if (bString.length() > 0)
			{
				b.appendLineBreak();
				b.appendI18nElement("in_ieInfoLabelTextCostSource",bString); //$NON-NLS-1$
			}

			infoLabel.setText(b.toString());
		}
	}

	/**
	 * Change Location of equipment in an EquipSet
	 **/
	private void setLocationButton()
	{
		TreePath slSelPath = selectedTable.getTree().getSelectionPath();
		EquipSet eSet = null;
		EquipSet rootSet = null;
		String locName = "";
		Equipment eqI = null;

		if (slSelPath == null)
		{
			ShowMessageDelegate.showMessageDialog(
				PropertyFactory.getString("in_ieSetLocNoItemSelectedMsg"), Constants.s_APPNAME,
				MessageType.ERROR);

			return;
		}
		Object endComp = slSelPath.getLastPathComponent();
		MyPONode fNode = (MyPONode) endComp;

		if ((fNode.getItem() instanceof EquipSet))
		{
			eSet = (EquipSet) fNode.getItem();
			rootSet = pc.getEquipSetByIdPath(eSet.getRootIdPath());
			eqI = eSet.getItem();
		}

		if (eqI == null)
		{
			ShowMessageDelegate.showMessageDialog(
				PropertyFactory.getString("in_ieSetLocNoItemSelectedMsg"), Constants.s_APPNAME,
				MessageType.ERROR);

			return;
		}

		// if the eSet.getIdPath() is longer than 3
		// it's inside a container, so bail out
		StringTokenizer aTok =
				new StringTokenizer(eSet.getIdPath(), ".", false);

		if (aTok.countTokens() > 3)
		{
			ShowMessageDelegate.showMessageDialog(
				PropertyFactory.getString("in_ieSetLocInsideContMsg"),
				Constants.s_APPNAME, MessageType.ERROR);

			return;
		}

		List<EqSetWrapper> containers = new ArrayList<EqSetWrapper>();

		// get the possible locations for this item
		List<String> aList = locationChoices(eqI, containers);

		// let them choose where to put the item
		ChooserRadio c = ChooserFactory.getRadioInstance();

		if (containers.size() != 0)
		{
			c.setComboData(PropertyFactory.getString("in_ieContChooserTitle"), containers);
		}

		c.setAvailableList(aList);
		c.setVisible(false);
		c.setTitle(eqI.getName());
		c.setMessageText(PropertyFactory.getString("in_ieSelectLocationMsg"));
		c.setVisible(true);

		Equipment eqTarget = null;
		EquipSet eTargetSet = null;

		aList = c.getSelectedList();

		if (aList.size() > 0)
		{
			final Object loc = aList.get(0);

			if (loc instanceof String)
			{
				locName = (String) loc;
			}
			else
			{
				/*
				 * CONSIDER The generic type of aList above, this code is
				 * unreachable. - thpr 10/21/06
				 */
				eTargetSet = ((EqSetWrapper) loc).getEqSet();
				eqTarget = eTargetSet.getItem();
				rootSet = pc.getEquipSetByIdPath(eTargetSet.getIdPath());

				if (eqTarget.canContain(pc, eqI) == 1)
				{
					locName = eqTarget.getName();
				}
				else
				{
					ShowMessageDelegate.showMessageDialog(
						PropertyFactory.getFormattedString("in_ieContainerFullMsg", 
						eqTarget.getName() ), Constants.s_APPNAME,
						MessageType.ERROR);

					return;
				}
			}
		}

		if ("".equals(locName) || (locName.length() == 0))
		{
			return;
		}

		if ((eTargetSet != null)
			&& eSet.getIdPath().equals(eTargetSet.getIdPath()))
		{
			ShowMessageDelegate.showMessageDialog(
				PropertyFactory.getString("in_ieSetLocInsideSelfMsg"), Constants.s_APPNAME,
				MessageType.ERROR);

			return;
		}

		// make sure we can add item to that slot in this EquipSet
		if (!canAddEquip(rootSet, locName, eqI, eqTarget))
		{
			ShowMessageDelegate.showMessageDialog(
				PropertyFactory.getFormattedString("in_ieCanNotEquipToSlotMsg",
				eqI.getName() , locName) , Constants.s_APPNAME,
				MessageType.ERROR);

			return;
		}

		if ((eqTarget != null) && eqTarget.isContainer())
		{
			eqTarget.insertChild(pc, eqI);
			eqI.setParent(eqTarget);
		}

		if (eTargetSet != null)
		{
			final String oldPath = eSet.getIdPath();

			// if we are moving this item into a container
			// we need to construct a new IdPath
			eSet.setIdPath(getNewIdPath(eTargetSet));

			// if the item we are moving is a container
			// need to also move all the items it may contain
			if (eqI.isContainer())
			{
				for (EquipSet es : pc.getEquipSet())
				{
					String pIdPath = es.getParentIdPath() + ".";
					String oldIdPath = oldPath + ".";

					if (pIdPath.startsWith(oldIdPath))
					{
						es.setIdPath(getNewIdPath(eSet));
					}
				}
			}
		}

		// change the location of the equipment
		eSet.setName(locName);

		// reset EquipSet model to get the new equipment
		// added into the selectedTable tree
		pc.setDirty(true);
		updateSelectedModel();

		// Make sure equipment based bonuses get recalculated
		pc.calcActiveBonuses();
	}

	/**
	 * returns new id_Path with the last id one higher than the current
	 * highest id for EquipSets with the same ParentIdPath
	 * @param eSet
	 * @return new id path
	 **/
	private String getNewIdPath(EquipSet eSet)
	{
		String pid = "0";
		int newID = 0;

		if (eSet != null)
		{
			pid = eSet.getIdPath();
		}

		for (EquipSet es : pc.getEquipSet())
		{
			if (es.getParentIdPath().equals(pid) && (es.getId() > newID))
			{
				newID = es.getId();
			}
		}

		++newID;

		return pid + '.' + newID;
	}

	/**
	 * add additional information to an equipset
	 **/
	private void setNoteButton()
	{
		TreePath noteSelPath = selectedTable.getTree().getSelectionPath();

		if (noteSelPath == null)
		{
			ShowMessageDelegate.showMessageDialog(PropertyFactory.getString("in_ieNoEqSelectedMsg"),
				Constants.s_APPNAME, MessageType.ERROR);

			return;
		}

		String pid;

		Object endComp = noteSelPath.getLastPathComponent();
		MyPONode fNode = (MyPONode) endComp;

		if (!(fNode.getItem() instanceof EquipSet))
		{
			return;
		}

		EquipSet eSet = (EquipSet) fNode.getItem();

		// now make sure we have this PC's EquipSet
		pid = eSet.getIdPath();
		eSet = pc.getEquipSetByIdPath(pid);

		Equipment eqI = eSet.getItem();

		if (eqI == null)
		{
			ShowMessageDelegate.showMessageDialog(PropertyFactory.getString("in_ieNoEqSelectedMsg"),
				Constants.s_APPNAME, MessageType.ERROR);

			return;
		}

		String newNote;

		InputInterface ii = InputFactory.getInputInstance();
		Object selectedValue =
				ii.showInputDialog(null, PropertyFactory.getString("in_ieNewNote"), Constants.s_APPNAME,
					MessageType.QUESTION, null, eSet.getNote());

		if (selectedValue != null)
		{
			newNote = ((String) selectedValue).trim();
		}
		else
		{
			// canceled, so just return
			return;
		}

		if (newNote != null)
		{
			eSet.setNote(newNote);
		}

		pc.setDirty(true);
		updateSelectedModel();
	}

	/**
	 * sets the quantity carried
	 * @param aQty
	 **/
	private void setQtyButton(Float aQty)
	{
		TreePath qtySelPath = selectedTable.getTree().getSelectionPath();

		if (qtySelPath == null)
		{
			ShowMessageDelegate.showMessageDialog(PropertyFactory.getString("in_ieNoEqSelectedMsg"),
				Constants.s_APPNAME, MessageType.ERROR);

			return;
		}

		String pid;

		Object endComp = qtySelPath.getLastPathComponent();
		MyPONode fNode = (MyPONode) endComp;

		if (!(fNode.getItem() instanceof EquipSet))
		{
			return;
		}

		EquipSet eSet = (EquipSet) fNode.getItem();

		// now make sure we have this PC's EquipSet
		pid = eSet.getIdPath();
		eSet = pc.getEquipSetByIdPath(pid);

		Equipment eqI = eSet.getItem();
		StringTokenizer aTok =
				new StringTokenizer(eSet.getIdPath(), ".", false);

		if (eqI == null)
		{
			ShowMessageDelegate.showMessageDialog(PropertyFactory.getString("in_ieNoEqSelectedMsg"),
				Constants.s_APPNAME, MessageType.ERROR);

			return;
		}

		// only allow this button to change the quantity
		// of carried items or items inside a container
		if (eqI.isContainer())
		{
			ShowMessageDelegate.showMessageDialog(
				PropertyFactory.getString("in_ieNoChangeQuantityCont"), Constants.s_APPNAME,
				MessageType.ERROR);

			return;
		}

		if ((aTok.countTokens() <= 3) && eqI.isEquipped())
		{
			ShowMessageDelegate.showMessageDialog(
				PropertyFactory.getString("in_ieNoChangeQuantityEquip"),
				Constants.s_APPNAME, MessageType.ERROR);

			return;
		}

		Float currentNum = eSet.getQty();
		Float numCarried = eqI.getCarried();
		float newNum;

		if (aQty.floatValue() <= 0.0f)
		{
			Object selectedValue =
					JOptionPane.showInputDialog(null, 
						PropertyFactory.getString("in_ieNewQuantity"),
						Constants.s_APPNAME, JOptionPane.QUESTION_MESSAGE);

			if (selectedValue != null)
			{
				try
				{
					newNum = Float.parseFloat(((String) selectedValue).trim());
				}
				catch (Exception e)
				{
					ShowMessageDelegate.showMessageDialog(
						PropertyFactory.getString("in_ieInvalidNumber"),
						Constants.s_APPNAME, MessageType.ERROR);

					return;
				}
			}
			else
			{
				// canceled, so just return
				return;
			}
		}
		else
		{
			newNum = aQty.floatValue();
		}

		// if the new number is the same as the old number,
		// just return as there is nothing to do
		if (CoreUtility.doublesEqual(newNum, currentNum.floatValue()))
		{
			return;
		}

		float addNum = newNum;

		// if there are existing items, then subtract that from
		// the desired new total to get the right amount to add
		if (currentNum.floatValue() > 0)
		{
			addNum = newNum - currentNum.floatValue();
		}

		// Check to make sure this EquipSet does not exceed
		// the PC's equipmentList number for this item
		if (addNum > diffItemCount(eSet, eqI).floatValue())
		{
			ShowMessageDelegate.showMessageDialog(PropertyFactory.getFormattedString(
				"in_ieEquipQuantityToBig" , newNum, eqI.getName() ), Constants.s_APPNAME, MessageType.ERROR);

			return;
		}

		// Equipment is inside a container, so we have to check to
		// make sure the container can hold that many
		if (aTok.countTokens() > 3)
		{
			Equipment eqP = eqI.getParent();

			// set these to new values for testing
			eqI.setQty(new Float(addNum));
			eqI.setNumberCarried(new Float(addNum));

			// Make sure the container accepts items
			// of this type and is not full
			if (eqP.canContain(pc, eqI) != 1)
			{
				// set back to old values
				eqI.setQty(currentNum);
				eqI.setNumberCarried(numCarried);

				// Send error message
				ShowMessageDelegate.showMessageDialog(PropertyFactory.getFormattedString(
					"in_ieContainerFull" , eqP.getName()) , Constants.s_APPNAME,
					MessageType.ERROR);

				return;
			}
		}

		// set the new quantity
		eSet.setQty(new Float(newNum));
		eqI.setNumberCarried(new Float(newNum));

		pc.setDirty(true);
		updateTotalWeight();
		updateSelectedModel();
	}

	/**
	 * If an item can only go in one location, return the name of that
	 * location to add to an EquipSet
	 * @param eqI
	 * @return single location
	 **/
	private String getSingleLocation(Equipment eqI)
	{
		// Handle natural weapons
		if (eqI.isNatural())
		{
			if (eqI.getSlots(pc) == 0)
			{
				if (eqI.modifiedName().endsWith("Primary"))
				{
					return Constants.EQUIP_LOCATION_NATURAL_PRIMARY;
				}
				return Constants.EQUIP_LOCATION_NATURAL_SECONDARY;
			}
		}

		// Always force weapons to go through the chooser dialog
		// unless they are also armor (ie: with Armor Spikes)
		if ((eqI.isWeapon()) && !(eqI.isArmor()))
		{
			return "";
		}

		List<EquipSlot> eqSlotList =
				SystemCollections.getUnmodifiableEquipSlotList();

		if ((eqSlotList == null) || eqSlotList.isEmpty())
		{
			return "";
		}

		for (EquipSlot es : eqSlotList)
		{
			// see if this EquipSlot can contain this item TYPE
			if (es.canContainType(eqI.getType()))
			{
				return es.getSlotName();
			}
		}

		return "";
	}

	private void setTableSelectedIndex(JTreeTable aTable, int idx)
	{
		aTable.setRowSelectionInterval(idx, idx);
	}

	private static int getTableSelectedIndex(ListSelectionEvent e)
	{
		final DefaultListSelectionModel model =
				(DefaultListSelectionModel) e.getSource();

		if (model == null)
		{
			return -1;
		}

		return model.getMinSelectionIndex();
	}

	private class AvailableClickHandler implements ClickHandler
	{
		public void singleClickEvent()
		{
			// Do Nothing
		}

		public void doubleClickEvent()
		{
			addEquipButton(new Float(1));
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
			// We run this after the event has been processed so that
			// we don't confuse the table when we change its contents
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					delEquipButton();
				}
			});
		}

		public boolean isSelectable(Object obj)
		{
			if (obj instanceof EquipSet)
			{
				EquipSet eqset = (EquipSet) obj;
				String pathId = eqset.getIdPath();
				if (pathId.equals(eqset.getRootIdPath()))
				{
					return false;
				}
				return true;
			}
			return false;
		}
	}

	private final void createTreeTables()
	{
		availableTable = new JTreeTable(availableModel);

		final JTree atree = availableTable.getTree();
		atree.setRootVisible(false);
		atree.setShowsRootHandles(true);
		atree.setCellRenderer(new LabelTreeCellRenderer());
		atree.getSelectionModel().setSelectionMode(
			TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		availableTable
			.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		availableTable.getSelectionModel().addListSelectionListener(
			new ListSelectionListener()
			{
				public void valueChanged(ListSelectionEvent e)
				{
					if (!e.getValueIsAdjusting())
					{
						int iRow = getTableSelectedIndex(e);
						TreePath avaPath = atree.getPathForRow(iRow);

						if (iRow < 0)
						{
							avaPath = atree.getSelectionPath();
						}

						if (avaPath == null)
						{
							return;
						}

						Object temp = avaPath.getLastPathComponent();

						if (temp == null)
						{
							infoLabel.setText();

							return;
						}

						MyPONode fNode = (MyPONode) temp;

						if (fNode.getItem() instanceof Equipment)
						{
							Equipment eqI = (Equipment) fNode.getItem();

							if (eqI != null)
							{
								AddMenu.setEnabled(true);
								AddNumMenu.setEnabled(true);
								AddAllMenu.setEnabled(true);
								addEquipButton.setEnabled(true);
								setInfoLabelText(eqI);
							}
						}
						else
						{
							AddMenu.setEnabled(false);
							AddNumMenu.setEnabled(false);
							AddAllMenu.setEnabled(false);
							addEquipButton.setEnabled(false);
						}
					}
				}
			});

		availableTable.addMouseListener(new JTreeTableMouseAdapter(
			availableTable, new AvailableClickHandler(), true));

		// now do the selectedTable and selectedTree
		selectedTable = new JTreeTable(selectedModel);

		final JTree stree = selectedTable.getTree();
		stree.setRootVisible(false);
		stree.setShowsRootHandles(true);
		stree.setCellRenderer(new LabelTreeCellRenderer());
		selectedTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		selectedTable.getSelectionModel().addListSelectionListener(
			new ListSelectionListener()
			{
				public void valueChanged(ListSelectionEvent e)
				{
					if (!e.getValueIsAdjusting())
					{
						int iRow = getTableSelectedIndex(e);
						TreePath vcSelPath = stree.getPathForRow(iRow);

						if (iRow < 0)
						{
							vcSelPath = stree.getSelectionPath();
						}

						if (vcSelPath == null)
						{
							return;
						}

						if (!stree.isSelectionEmpty())
						{
							final MyPONode fn =
									(MyPONode) vcSelPath.getPathComponent(1);
							final EquipSet eSet = (EquipSet) fn.getItem();

							if ((eSet != null)
								&& (!eSet.getName().equals(
									equipSetTextField.getText())))
							{
								equipSetTextField.setText(eSet.getName());
								selectedEquipSet = eSet.getName();

								final int index =
										viewComboBox.getSelectedIndex();

								if (index == GuiConstants.INFOEQUIPPING_VIEW_EQUIPPED)
								{
									updateAvailableModel();
								}
							}
						}

						final Object temp = vcSelPath.getLastPathComponent();

						if (temp == null)
						{
							infoLabel.setText();

							return;
						}

						MyPONode fNode = (MyPONode) temp;

						if (fNode.getItem() instanceof EquipSet)
						{
							EquipSet eSet = (EquipSet) fNode.getItem();
							Equipment eqI = eSet.getItem();

							if (eqI == null)
							{
								CopyEquipSetMenu.setEnabled(true);
								RenameEquipSetMenu.setEnabled(true);
								DelMenu.setEnabled(false);
								SetQtyMenu.setEnabled(false);
								SetLocationMenu.setEnabled(false);
								SetNoteMenu.setEnabled(false);
								delEquipButton.setEnabled(false);
								setQtyButton.setEnabled(false);
								setNoteButton.setEnabled(false);
							}

							if (eqI != null)
							{
								CopyEquipSetMenu.setEnabled(false);
								RenameEquipSetMenu.setEnabled(false);
								DelMenu.setEnabled(true);
								SetQtyMenu.setEnabled(true);
								SetLocationMenu.setEnabled(true);
								SetNoteMenu.setEnabled(true);
								delEquipButton.setEnabled(true);
								setQtyButton.setEnabled(true);
								setNoteButton.setEnabled(true);
								setInfoLabelText(eqI);
							}
						}
					}
				}
			});

		selectedTable.addMouseListener(new JTreeTableMouseAdapter(
			selectedTable, new SelectedClickHandler(), true));

		// create the rightclick popup menus
		hookupPopupMenu(availableTable);
		hookupPopupMenu(selectedTable);
	}

	/**
	 * This method gets a list of locations for a weapon
	 * @param hands
	 * @param multiHand
	 * @return weapon location choices
	 **/
	private static List<String> getWeaponLocationChoices(final int hands,
		final String multiHand)
	{
		final List<String> result = new ArrayList<String>(hands + 2);

		if (hands > 0)
		{
			result.add(Constants.EQUIP_LOCATION_PRIMARY);

			for (int i = 1; i < hands; ++i)
			{
				if (i > 1)
				{
					result.add(Constants.EQUIP_LOCATION_SECONDARY + " " + i);
				}
				else
				{
					result.add(Constants.EQUIP_LOCATION_SECONDARY);
				}
			}

			if (multiHand.length() > 0)
			{
				result.add(multiHand);
			}
		}

		return result;
	}

	/**
	 * Handle a user request to add all of the selected item
	 **/
	private void addAllEquipButton()
	{
		addEquipButton(new Float(-1));
	}

	/**
	 * Adds equipment to an EquipSet
	 * @param newQty
	 **/
	private void addEquipButton(Float newQty)
	{
		TreePath aeSelPath = selectedTable.getTree().getSelectionPath();

		if (selectedTable.getTree().isSelectionEmpty()
			|| !pathExists(selectedTable.getTree(), aeSelPath))
		{
			ShowMessageDelegate.showMessageDialog(PropertyFactory
				.getString("in_ieSelectSet"), Constants.s_APPNAME,
				MessageType.ERROR);

			return;
		}

		String equipSetName = equipSetTextField.getText();
		String locName = "";
		MyPONode parentNode;
		Equipment eqTarget = null;
		Equipment eqI;
		Equipment eq;
		EquipSet eSet = null;
		EquipSet newSet;

		Object endComp = aeSelPath.getLastPathComponent();
		parentNode = (MyPONode) endComp;

		if ((parentNode.getItem() instanceof EquipSet))
		{
			eSet = (EquipSet) parentNode.getItem();

			if (!"".equals(eSet.getValue()))
			{
				eqTarget = eSet.getItem();

				if (eqTarget == null)
				{
					eqTarget = Globals.getContext().ref
							.silentlyGetConstructedCDOMObject(Equipment.class,
									eSet.getValue());
				}

				if (!eqTarget.isContainer())
				{
					eSet = getCurrentEquipSet();
					parentNode = (MyPONode) aeSelPath.getPathComponent(1);
				}
			}
		}

		if ((eSet == null) || (parentNode == null))
		{
			ShowMessageDelegate.showMessageDialog(
				PropertyFactory.getString("in_ieSelectSet"),
				Constants.s_APPNAME, MessageType.ERROR);

			return;
		}

		TreePath[] avaCPaths = availableTable.getTree().getSelectionPaths();

		for (int index = 0; index < avaCPaths.length; ++index)
		{
			Object aComp = avaCPaths[index].getLastPathComponent();
			MyPONode fNode = (MyPONode) aComp;

			if (!(fNode.getItem() instanceof Equipment))
			{
				return;
			}

			// get the equipment Item from the available Table
			eq = (Equipment) fNode.getItem();

			int outIndex = eq.getOutputIndex();
			eqI = eq.clone();
			eqI.setOutputIndex(outIndex);

			// Add the item of equipment
			newSet = addEquipToTarget(eSet, eqTarget, locName, eqI, newQty);

			// add EquipSet into the selectedTable tree
			if (newSet != null)
			{
				MyPONode fN = new MyPONode();
				fN.setItem(newSet);
				fN.setParent(parentNode);
				parentNode.addChild(fN);
			}
			else
			{
				Logging.errorPrintLocalised("Warnings.PCGenParser.EquipmentNotFound", eqI);

				return;
			}
		}

		selectedEquipSet = equipSetName;
		updateTotalWeight();
		updateSelectedModel();

		final int index = viewComboBox.getSelectedIndex();

		if (index == GuiConstants.INFOEQUIPPING_VIEW_EQUIPPED)
		{
			updateAvailableModel();
		}

		// Make sure equipment based bonuses get recalculated
		pc.calcActiveBonuses();
	}

	/**
	 * Verify that the path exists in the tree.
	 * 
	 * @param tree The tree to be scanned.
	 * @param path The path to be verified.
	 * @return true if the path exists in the tree.
	 */
	private boolean pathExists(JTree tree, TreePath path)
	{
		PObjectNode root = (PObjectNode) tree.getModel().getRoot();
		PObjectNode currTreeNode = null;
		for (int i = 0; i < path.getPathCount(); i++)
		{
			PObjectNode pathNode = (PObjectNode) path.getPathComponent(i);
			if (currTreeNode == null)
			{
				if (root != pathNode)
				{
					return false;
				}
				currTreeNode = root;
			}
			else
			{
				List<PObjectNode> children = currTreeNode.getChildren();
				boolean found = false;
				for (PObjectNode node : children)
				{
					if (node == pathNode)
					{
						found = true;
						currTreeNode = node;
						break;
					}
				}
				if (!found)
				{
					return false;
				}
			}

		}

		return true;
	}

	/*
	 *****  **  **  **   **  **  ** **    **
	 **     **  **  ***  **  ** **   **  **
	 ***This is used to add new equipment Sets when
	 ***the equipSetTextField JTextField is edited
	 **     **  **  **  ***  ** **     **
	 **     ******  **   **  **  **    **
	 */
	private void addEquipSetButton()
	{
		String equipSetFieldText = equipSetTextField.getText();

		if (equipSetFieldText.equals(selectedEquipSet))
		{
			return;
		}

		EquipSet pcSet = pc.getEquipSetByName(equipSetFieldText);

		if (pcSet != null)
		{
			return;
		}

		// get an new unique id that is one higher than any
		// other EquipSet attached to the root node
		String id = getNewIdPath(null);

		// Create a new EquipSet and assign to root node
		EquipSet eSet = new EquipSet(id, equipSetFieldText);

		pc.setDirty(true);
		selectedEquipSet = equipSetFieldText;
		pc.addEquipSet(eSet);
		calcComboBoxFill();
		updateSelectedModel();
	}

	/**
	 * Add the specified item of equipment to the provided equipset
	 * The location the item is added is either eqTarget and locName
	 * If eqTarget is set, it will override the locName setting
	 * @param eSet
	 * @param eqTarget
	 * @param locName
	 * @param eqI
	 * @param newQty
	 * @return Equip Set
	 **/
	private EquipSet addEquipToTarget(EquipSet eSet, Equipment eqTarget,
		String locName, Equipment eqI, Float newQty)
	{
		Float tempQty = newQty;
		boolean addAll = false;
		boolean mergeItem = false;

		// if newQty is less than zero, we want to
		// add all of this item to the EquipSet
		// or all remaining items that havn't already
		// been added to the EquipSet
		if (newQty.floatValue() < 0.0f)
		{
			tempQty = diffItemCount(eSet, eqI);
			newQty =
					new Float(tempQty.floatValue()
						+ existingQty(eSet, eqI).floatValue());
			addAll = true;
		}

		// Check to make sure this EquipSet does not exceed
		// the PC's equipmentList number for this item
		if (tempQty.floatValue() > diffItemCount(eSet, eqI).floatValue())
		{
			ShowMessageDelegate.showMessageDialog(
				PropertyFactory.getFormattedString("in_ieAlreadyEquipedAll", eqI.getName()),
				Constants.s_APPNAME, MessageType.ERROR);

			return null;
		}

		// check to see if the target item is a container
		if ((eqTarget != null) && eqTarget.isContainer())
		{
			// set these to newQty just for testing
			eqI.setQty(newQty);
			eqI.setNumberCarried(newQty);

			// Make sure the container accepts items
			// of this type and is not full
			if (eqTarget.canContain(pc, eqI) == 1)
			{
				locName = eqTarget.getName();
				addAll = true;
				mergeItem = true;
			}
			else
			{
				ShowMessageDelegate.showMessageDialog(PropertyFactory.getFormattedString(
					"in_ieContainerFull", eqTarget.getName()), Constants.s_APPNAME,
					MessageType.ERROR);

				return null;
			}
		}

		if ("".equals(locName) || (locName.length() == 0))
		{
			List<EqSetWrapper> containers = new ArrayList<EqSetWrapper>();

			// get the possible locations for this item
			List<String> aList = locationChoices(eqI, containers);
			locName = getSingleLocation(eqI);

			if ((locName.length() != 0)
				&& canAddEquip(eSet, locName, eqI, eqTarget))
			{
				// seems to be the right choice
			}
			else
			{
				// let them choose where to put the item
				ChooserRadio c = ChooserFactory.getRadioInstance();

				if (containers.size() != 0)
				{
					c.setComboData(PropertyFactory.getString("in_ieContChooserTitle"), containers);
				}

				c.setAvailableList(aList);
				c.setVisible(false);
				c.setTitle(eqI.getName());
				c.setMessageText(PropertyFactory.getString("in_ieSelectLocationMsg"));
				c.setVisible(true);
				aList = c.getSelectedList();

				if (aList.size() > 0)
				{
					Object loc = aList.get(0);

					if (loc instanceof String)
					{
						locName = (String) loc;
						mergeItem = true;
					}
					else
					{
						/*
						 * CONSIDER Considering the Generic type of aList, this
						 * else statement is unreachable - thpr 10/21/06
						 */
						eSet = ((EqSetWrapper) loc).getEqSet();
						eqTarget = eSet.getItem();

						if (eqTarget.canContain(pc, eqI) == 1)
						{
							locName = eqTarget.getName();
							addAll = true;
							mergeItem = true;
						}
						else
						{
							ShowMessageDelegate.showMessageDialog(
								PropertyFactory.getFormattedString("in_ieContainerFullMsg",
								eqTarget.getName()),
								Constants.s_APPNAME, MessageType.ERROR);

							return null;
						}
					}
				}
			}
		}

		if ("".equals(locName) || (locName.length() == 0))
		{
			return null;
		}

		// make sure we can add item to that slot in this EquipSet
		if (!canAddEquip(eSet, locName, eqI, eqTarget))
		{
			ShowMessageDelegate.showMessageDialog(PropertyFactory.getFormattedString(
				"in_ieCanNotEquipToSlotMsg", eqI.getName(), locName)
				, Constants.s_APPNAME, MessageType.ERROR);

			return null;
		}

		if (eqI.isContainer())
		{
			// don't merge containers
			mergeItem = false;
		}

		EquipSet existingSet = existingItem(eSet, eqI);

		if (addAll && mergeItem && (existingSet != null))
		{
			newQty =
					new Float(tempQty.floatValue()
						+ existingQty(eSet, eqI).floatValue());
			existingSet.setQty(newQty);
			eqI.setQty(newQty);
			eqI.setNumberCarried(newQty);
			pc.setDirty(true);

			if ((eqTarget != null) && eqTarget.isContainer())
			{
				eqTarget.updateContainerContentsString(pc);
			}

			return existingSet;
		}
		if ((eqTarget != null) && eqTarget.isContainer())
		{
			eqTarget.insertChild(pc, eqI);
			eqI.setParent(eqTarget);
		}

		// construct the new IdPath
		// new id is one larger than any
		// other id at this path level
		String id = getNewIdPath(eSet);

		// now create a new EquipSet to add
		// this Equipment item to
		EquipSet newSet = new EquipSet(id, locName, eqI.getName(), eqI);

		// set the Quantity of equipment
		eqI.setQty(newQty);
		newSet.setQty(newQty);

		pc.addEquipSet(newSet);
		pc.setDirty(true);

		return newSet;
	}

	/**
	 * Adds iQty of equipment to an EquipSet
	 **/
	private void addNumEquipButton()
	{
		Float newQty;
		Object selectedValue =
				JOptionPane.showInputDialog(null, PropertyFactory.getString("in_ieAddEquipQuantityMsg"),
					Constants.s_APPNAME, JOptionPane.QUESTION_MESSAGE);

		if (selectedValue != null)
		{
			try
			{
				newQty = new Float(((String) selectedValue).trim());
			}
			catch (Exception e)
			{
				ShowMessageDelegate.showMessageDialog(
					PropertyFactory.getString("in_ieInvalidNumber"),
					Constants.s_APPNAME, MessageType.ERROR);

				return;
			}

			addEquipButton(newQty);
		}
	}

	/**
	 * Buy additional equipment
	 * @param newQty
	 **/
	private void buyEquipButton(Float newQty)
	{
		if (newQty.floatValue() <= 0)
		{
			Object selectedValue =
					JOptionPane.showInputDialog(null, PropertyFactory.getString("in_ieBuyEquipQuantityMsg"),
						Constants.s_APPNAME, JOptionPane.QUESTION_MESSAGE);

			if (selectedValue != null)
			{
				try
				{
					newQty = new Float(((String) selectedValue).trim());
				}
				catch (Exception e)
				{
					ShowMessageDelegate.showMessageDialog(
						PropertyFactory.getString("in_ieInvalidNumber"),
						Constants.s_APPNAME, MessageType.ERROR);

					return;
				}
			}
		}

		TreePath[] avaCPaths = availableTable.getTree().getSelectionPaths();

		for (int index = 0; index < avaCPaths.length; ++index)
		{
			Object aComp = avaCPaths[index].getLastPathComponent();
			MyPONode fNode = (MyPONode) aComp;

			if (!(fNode.getItem() instanceof Equipment))
			{
				return;
			}

			// get the equipment Item from the available Table
			Equipment eq = (Equipment) fNode.getItem();
			PCGen_Frame1.getCharacterPane().infoInventory().getInfoGear()
				.buySpecifiedEquipment(eq, newQty.doubleValue());
			forceRefresh();
		}
	}

	/**
	 * Changes the EquipSet used to calculate/output to Output sheets
	 **/
	private void calcComboBoxActionPerformed()
	{
		final EquipSet eSet = (EquipSet) calcComboBox.getSelectedItem();

		if (eSet != null)
		{
			final String eqSetId = eSet.getIdPath();

			if (!eqSetId.equals(pc.getCalcEquipSetId()))
			{
				pc.setCalcEquipSetId(eqSetId);
				pc.setCalcEquipmentList();
				pc.setUseTempMods(eSet.getUseTempMods());
				pc.calcActiveBonuses();
				pc.setDirty(true);
				updateTotalWeight();

				// now Update all the other tabs
				updateOtherTabs();
			}
		}
	}

	/**
	 * Update all the other tabs affected by equip set changes.
	 */
	private void updateOtherTabs()
	{
		CharacterInfo pane = PCGen_Frame1.getCharacterPane();
		pane.setPaneForUpdate(pane.infoSpecialAbilities());
		pane.setPaneForUpdate(pane.infoClasses());
		pane.setPaneForUpdate(pane.infoAbilities());
		pane.setPaneForUpdate(pane.infoSkills());
		pane.setPaneForUpdate(pane.infoSpells());
		pane.setPaneForUpdate(pane.infoSummary());
		pane.setPaneForUpdate(pane.infoInventory().getTempModPane());
		pane.refresh();
	}

	/**
	 * Load the EquipSet Calc dropdown with all EquipSets
	 **/
	private void calcComboBoxFill()
	{
		if (calcComboBoxListener != null)
		{
			calcComboBox.removeActionListener(calcComboBoxListener);
		}
		List<String> calcList = new ArrayList<String>(1);
		calcComboBox.removeAllItems();
		equipSetList = new ArrayList<EquipSet>(pc.getEquipSet());

		// loop through all root EquipSet's and add
		// to calcComboBox list
		for (EquipSet es : equipSetList)
		{
			if (es.getParentIdPath().equals("0")
				&& !calcList.contains(es.getIdPath()))
			{
				calcList.add(es.getIdPath());
				calcComboBox.addItem(es);
			}
		}

		EquipSet cES = pc.getEquipSetByIdPath(pc.getCalcEquipSetId());

		if (cES == null)
		{
			if (calcComboBoxListener != null)
			{
				calcComboBox.addActionListener(calcComboBoxListener);
			}
			return;
		}

		for (int i = 0; i <= calcComboBox.getItemCount(); i++)
		{
			EquipSet es = (EquipSet) calcComboBox.getItemAt(i);

			if ((es != null) && es.getIdPath().equals(cES.getIdPath()))
			{
				calcComboBox.setSelectedIndex(i);
			}
		}
		if (calcComboBoxListener != null)
		{
			calcComboBox.addActionListener(calcComboBoxListener);
		}
	}

	/**
	 * returns true if you can put Equipment into a location in EquipSet
	 * @param eSet
	 * @param locName
	 * @param eqToBeAdded
	 * @param eqTarget
	 * @return true if equipment can be added
	 **/
	private boolean canAddEquip(EquipSet eSet, String locName, Equipment eqToBeAdded,
		Equipment eqTarget)
	{
		final String idPath = eSet.getIdPath();

		// If target is a container, allow it
		if ((eqTarget != null) && eqTarget.isContainer())
		{
			return true;
		}

		// If Carried/Equipped/Not Carried slot
		// allow as many as they would like
		if (locName.startsWith(Constants.EQUIP_LOCATION_CARRIED)
			|| locName.startsWith(Constants.EQUIP_LOCATION_EQUIPPED)
			|| locName.startsWith(Constants.EQUIP_LOCATION_NOTCARRIED))
		{
			return true;
		}

		// allow as many unarmed items as you'd like
		if (eqToBeAdded.isUnarmed())
		{
			return true;
		}

		// allow many Secondary Natural weapons
		if (locName.equals(Constants.EQUIP_LOCATION_NATURAL_SECONDARY))
		{
			return true;
		}

		// Don't allow weapons that are too large for PC
		if (eqToBeAdded.isWeapon() && eqToBeAdded.isWeaponOutsizedForPC(pc) && !eqToBeAdded.isNatural())
		{
			return false;
		}

		// make a HashMap to keep track of the number of each
		// item that is already equipped to a slot
		Map<String, Integer> slotMap = new HashMap<String, Integer>();

		for (EquipSet es : pc.getEquipSet())
		{
			String esID = es.getParentIdPath() + ".";
			String abID = idPath + ".";

			if (!esID.startsWith(abID))
			{
				continue;
			}

			// check to see if we already have
			// an item in that particular location
			if (es.getName().equals(locName))
			{
				final Equipment eItem = es.getItem();
				final Integer numSlots = slotMap.get(locName);
				int existNum = 0;

				if (numSlots != null)
				{
					existNum = numSlots;
				}

				if (eItem != null)
				{
					existNum += eItem.getSlots(pc);
				}

				slotMap.put(locName, existNum);
			}
		}

		for (EquipSet es : pc.getEquipSet())
		{
			String esID = es.getParentIdPath() + ".";
			String abID = idPath + ".";

			if (!esID.startsWith(abID))
			{
				continue;
			}

			// if it's a weapon we have to do some
			// checks for hands already in use
			if (eqToBeAdded.isWeapon())
			{
				// weapons can never occupy the same slot
				if (es.getName().equals(locName))
				{
					return false;
				}

				// if Double Weapon or Both Hands, then no
				// other weapon slots can be occupied
				if ((locName.equals(Constants.EQUIP_LOCATION_BOTH) || locName
					.equals(Constants.EQUIP_LOCATION_DOUBLE))
					&& (es.getName().equals(Constants.EQUIP_LOCATION_PRIMARY)
						|| es.getName().equals(Constants.EQUIP_LOCATION_SECONDARY)
						|| es.getName().equals(Constants.EQUIP_LOCATION_BOTH)
						|| es.getName().equals(Constants.EQUIP_LOCATION_DOUBLE)))
				{
					return false;
				}

				// inverse of above case
				if ((locName.equals(Constants.EQUIP_LOCATION_PRIMARY) || locName
					.equals(Constants.EQUIP_LOCATION_SECONDARY))
					&& (es.getName().equals(Constants.EQUIP_LOCATION_BOTH)
						|| es.getName().equals(Constants.EQUIP_LOCATION_DOUBLE)))
				{
					return false;
				}
			}
		}

		// Check to see how many items are allowed in the slot
		final Integer numSlots = slotMap.get(locName);
		int existNum = numSlots == null ? 0 : numSlots;

		existNum += eqToBeAdded.getSlots(pc);

		EquipSlot eSlot = Globals.getEquipSlotByName(locName);

		if (eSlot == null)
		{
			return true;
		}

		for (String slotType : eSlot.getContainType())
		{
			if (eqToBeAdded.isType(slotType))
			{
				// if the item takes more slots, return false
				if (existNum > (eSlot.getSlotCount() + (int) pc.getTotalBonusTo(
					"SLOTS", slotType)))
				{
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * This is a recursive call to populate the PObjectNode
	 * tree structure from the EquipSet ArrayList
	 * @param aNode
	 * @param aSet
	 **/
	private void addEquipTreeNodes(MyPONode aNode, EquipSet aSet)
	{
		// create a temporary list of EquipSets to pass to this
		// function when we recursivly call it for child nodes
		List<EquipSet> aList = new ArrayList<EquipSet>();

		String idPath = "0";

		if (aSet != null)
		{
			idPath = aSet.getIdPath();
		}

		// process all EquipSet Items
		for (int iSet = 0; iSet < tempSetList.size(); ++iSet)
		{
			EquipSet es = tempSetList.get(iSet);

			if (es.getParentIdPath().equals(idPath))
			{
				MyPONode fN = new MyPONode();
				fN.setItem(es);
				fN.setParent(aNode);
				aNode.addChild(fN);

				// add to list for recursive calls
				aList.add(es);

				// and remove from tempSetList so
				// it won't get processed again
				tempSetList.remove(es);
				--iSet;
			}
		}

		// recursivly call addEquipTreeNodes to get all
		// the child EquipSet items added to each root node
		for (int i = 0; i < aList.size(); ++i)
		{
			addEquipTreeNodes((MyPONode) aNode.getChild(i), aList.get(i));
		}
	}

	/**
	 * Select which temp bonuses to use on this EquipSet
	 * @param eSet
	 **/
	private void chooseTempBonuses(EquipSet eSet)
	{
		List<String> checkList = new ArrayList<String>();
		List<TempWrap> tbList = new ArrayList<TempWrap>();
		List<String> sList = new ArrayList<String>();

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

			tbList.add(tw);

			String sString = tw.getName();

			if (!sList.contains(sString))
			{
				sList.add(sString);
			}
		}

		// Let them choose which bonuses to use
		ChooserInterface lc = ChooserFactory.getChooserInstance();
		lc.setVisible(false);
		lc.setTitle(PropertyFactory.getString("in_ieBonChooserTitle"));
		lc.setMessageText(PropertyFactory.getString("in_ieBonChooserMsg"));
		lc.setAvailableList(sList);
		lc.setTotalChoicesAvail(sList.size());
		lc.setPoolFlag(false);
		lc.setVisible(true);

		Map<BonusObj, BonusManager.TempBonusInfo> aList = new IdentityHashMap<BonusObj, BonusManager.TempBonusInfo>();
		for (String aString : (List<String>) lc.getSelectedList())
		{
			for (int j = 0; j < tbList.size(); j++)
			{
				TempWrap tw = tbList.get(j);
				String tbString = tw.getName();

				if ((aString.equals(tbString)) && !checkList.contains(tbString))
				{
					String sourceStr = tw.getCreatorName();
					String targetStr = tw.getTargetName();
					aList.putAll(pc.getTempBonusMap(sourceStr, targetStr));
					checkList.add(tbString);
				}
			}
		}

		eSet.setTempBonusList(aList);
		pc.setDirty(true);
	}

	/**
	 * Process a request to duplicate the current equipment set.
	 **/
	private void copyEquipSetButton()
	{
		EquipSet eSet;
		EquipSet equipItem;
		String newName;
		String pid;
		List<EquipSet> newEquipSet = new ArrayList<EquipSet>();

		eSet = getCurrentEquipSet();

		if (eSet == null)
		{
			ShowMessageDelegate.showMessageDialog(
				PropertyFactory.getString("in_ieCpEqSetNotSelected"), Constants.s_APPNAME,
				MessageType.ERROR);

			return;
		}

		pid = eSet.getIdPath();

		// Get a new name
		newName =
				JOptionPane.showInputDialog(null,
					PropertyFactory.getString("in_ieNameNewEqSet"), Constants.s_APPNAME,
					JOptionPane.QUESTION_MESSAGE);

		if ((newName == null) || (newName.length() <= 0))
		{
			return;
		}

		// First check to make sure there are no other EquipSet's
		// with the same name (as it causes wierd problems)
		EquipSet pcSet = pc.getEquipSetByName(newName);

		if (pcSet != null)
		{
			ShowMessageDelegate.showMessageDialog(
				PropertyFactory.getString("in_ieEqSetNameExists"),
				Constants.s_APPNAME, MessageType.ERROR);

			return;
		}

		// Everything looks good, let's get a new ID and copy EQ
		// get an new unique id that is one higher than any
		// other EquipSet attached to the root node
		String id = getNewIdPath(null);

		eSet = (EquipSet) eSet.clone();
		eSet.setIdPath(id);
		eSet.setName(newName);

		selectedEquipSet = newName;
		pc.addEquipSet(eSet);

		for (EquipSet es : equipSetList)
		{
			String esIdPath = es.getParentIdPath() + ".";
			String pIdPath = pid + ".";

			if (!esIdPath.startsWith(pIdPath))
			{
				continue;
			}

			equipItem = (EquipSet) es.clone();
			equipItem.setIdPath(id + es.getIdPath().substring(pid.length()));

			newEquipSet.add(equipItem);
		}

		for (EquipSet es : newEquipSet)
		{
			pc.addEquipSet(es);
		}

		pc.setDirty(true);
		calcComboBoxFill();
		updateSelectedModel();
	}

	private final void createAvailableModel()
	{
		if (availableModel == null)
		{
			availableModel = new EquipModel(viewMode, MODEL_AVAIL);
		}
		else
		{
			availableModel.resetModel(viewMode, MODEL_AVAIL);
		}

		if (availableSort != null)
		{
			availableSort.setRoot((MyPONode) availableModel.getRoot());
			availableSort.sortNodeOnColumn();
		}
	}

	private final void createModels()
	{
		createAvailableModel();
		createSelectedModel();
	}

	private final void createSelectedModel()
	{
		if (selectedModel == null)
		{
			selectedModel = new EquipModel(viewSelectMode, MODEL_SELECTED);
		}
		else
		{
			selectedModel.resetModel(viewSelectMode, MODEL_SELECTED);
		}

		if (selectedSort != null)
		{
			selectedSort.setRoot((MyPONode) selectedModel.getRoot());
			selectedSort.sortNodeOnColumn();
		}
	}

	/**
	 * removes an item from the selected EquipSet
	 **/
	private void delEquipButton()
	{
		TreePath delSelPath = selectedTable.getTree().getSelectionPath();

		if (delSelPath == null)
		{
			ShowMessageDelegate.showMessageDialog(
				PropertyFactory.getString("in_ieDelEqNotSelected"),
				Constants.s_APPNAME, MessageType.ERROR);

			return;
		}

		TreePath[] selCPaths = selectedTable.getTree().getSelectionPaths();

		for (int index = 0; index < selCPaths.length; ++index)
		{
			Object endComp = selCPaths[index].getLastPathComponent();
			MyPONode fNode = (MyPONode) endComp;

			if (!(fNode.getItem() instanceof EquipSet))
			{
				return;
			}

			final EquipSet eSet = (EquipSet) fNode.getItem();

			// only allow this button to delete equipment
			// not the root EquipSet node
			if (eSet.getItem() == null)
			{
				ShowMessageDelegate
					.showMessageDialog(
						PropertyFactory.getString("in_ieDelEqNotEqSet"),
						Constants.s_APPNAME, MessageType.ERROR);

				return;
			}

			Equipment eqI = eSet.getItem();
			StringTokenizer aTok =
					new StringTokenizer(eSet.getIdPath(), ".", false);

			// remove Equipment (via EquipSet) from the PC
			pc.delEquipSet(eSet);

			// if it was inside a container, make sure to update
			// the container Equipment Object
			if (aTok.countTokens() > 3)
			{
				Equipment eqP = eqI.getParent();

				if (eqP != null)
				{
					eqP.removeChild(pc, eqI);
				}
			}
		}

		pc.setDirty(true);
		updateTotalWeight();
		updateSelectedModel();

		// Make sure equipment based bonuses get recalculated
		pc.calcActiveBonuses();

		final int index = viewComboBox.getSelectedIndex();

		if (index == GuiConstants.INFOEQUIPPING_VIEW_EQUIPPED)
		{
			updateAvailableModel();
		}
	}

	/*
	 *****  **  **  **   **  **   ** **    **
	 **  ** **  **  ***  **  ***  **  **  **
	 **This deletes the EquipSet and all "children" of the set
	 **the children all have the same parent Id as this EquipSet
	 **  ** **  **  **  ***  **  ***    **
	 *****  ******  **   **  **   **    **
	 */
	private void delEquipSetButton()
	{
		String equipSetFieldText = equipSetTextField.getText();
		EquipSet eSet = pc.getEquipSetByName(equipSetFieldText);

		if (eSet == null)
		{
			Logging.errorPrintLocalised("in_ieDelEqSetNotNamedError", equipSetFieldText);

			return;
		}

		int iConfirm =
				JOptionPane.showConfirmDialog(null,
					PropertyFactory.getString("in_ieDelEqSetConfirmMsg"), PropertyFactory.getString("in_ieDelEqSetConfirmTitle"),
					JOptionPane.YES_NO_OPTION);

		if (iConfirm != JOptionPane.YES_OPTION)
		{
			return;
		}

		if (pc.delEquipSet(eSet))
		{
			pc.setDirty(true);
			calcComboBoxFill();
			selectedEquipSet = "";
			updateSelectedModel();
		}
		else
		{
			Logging.errorPrintLocalised("in_ieDelEqSetFailedError");

			return;
		}
	}

	/**
	 * Returns the difference between the item count in the master
	 * equipment list and the item count in the EquipSet eSet
	 * @param eSet
	 * @param eqI
	 * @return Float
	 **/
	private Float diffItemCount(EquipSet eSet, Equipment eqI)
	{
		Float aVal = new Float(Integer.MAX_VALUE);
		final String rPath = eSet.getRootIdPath();
		float cQty = 0.0f;

		Equipment masterEq = pc.getEquipmentNamed(eqI.getName());

		if (masterEq == null)
		{
			return aVal;
		}

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
				cQty += es.getQty().floatValue();
			}
		}

		if (cQty <= masterEq.getQty().floatValue())
		{
			aVal = new Float(masterEq.getQty().floatValue() - cQty);
		}

		return aVal;
	}

	/**
	 * Prints the characters EquipSet's to the writer specified.
	 *
	 * @param bw  The writer to print the data to.
	 * @param template
	 * @throws IOException  If any problems occur in writing the data
	 */
	private void eqSetPrintToWriter(BufferedWriter bw, String template)
		throws IOException
	{
		File esTemplate = new File(template);

		int[] tests = new int[]{0, 0};
		int length = 0;

		BufferedReader br =
				new BufferedReader(new InputStreamReader(new FileInputStream(
					esTemplate), "UTF-8"));

		while (br.readLine() != null)
		{
			++length;
		}

		br.close();
		
		//Suffix is required because the Patterns to be used for export filtering
		//are based on file suffix - thpr  May 6, 07
		String suffix = template;
		final int idx = suffix.lastIndexOf('.');

		if (idx >= 0)
		{
			suffix = template.substring(idx + 1);
		}

		File reParse = File.createTempFile("eqTTemp_", ".tmp." + suffix);
		String reparseName = reParse.getPath();
		BufferedWriter rpW =
				new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
					reparseName, true), "UTF-8"));

		// The eqsheet file can have multiple instances of the
		// |EQSET.START| |EQSET.END| tags
		// This means we have to parse the file 'til we find one,
		// output everthing to reParse file, read the stuff between
		// the start/end tags, pass it to ExportHandler.write(), take
		// the output and append to reParse file, then continue 'til
		// we find another start/end tag and repeat 'til EOF
		//
		// Once we have done this, we need to pass the entire reParse
		// to ExportHandler.write() one last time to parse the rest of
		// the (non-equipset) tags.

		File temp = File.createTempFile("eqTemp_", ".tmp." + suffix);

		while (tests[0] < length)
		{
			// Pass the eqsheet template file and a temporary file
			// to the splitter. The temp file will contain part of
			// the esTemplate we need to parse/write for this loop
			int[] ret = eqsheetSplit(esTemplate, temp, tests);
			tests[0] = ret[0];

			// We found an EQSET tag, so parse it
			if (ret[1] == 1)
			{
				// parse/write EquipSet's to the reParse file
				equipItch(temp, rpW);
				rpW.flush();
			}
			else
			{
				// no EQSET tag, so just write to reParse file
				String aLine;
				BufferedReader tempReader =
						new BufferedReader(new InputStreamReader(
							new FileInputStream(temp), "UTF-8"));

				while ((aLine = tempReader.readLine()) != null)
				{
					rpW.write(aLine);
					rpW.newLine();
				}

				rpW.flush();
				tempReader.close();
			}
		}

		// we are all done writing to the reParse, so close it
		rpW.close();

		// delete temporary file
		temp.delete();

		// make sure the buffer is ready for writing
		bw.flush();

		// Now pass reParse file to ExportHandler
		// to get reparsed and output to final destination
		(new ExportHandler(reParse)).write(pc, bw);

		// delete temp file when done
		reParse.deleteOnExit();
	}

	/**
	 *
	 * Takes a template file and looks for delimiter
	 *     |EQSET.START|
	 * and
	 *     |EQSET.END|
	 *
	 * returns an array of int
	 * the first element is the line of the file last parsed
	 * the second element is if we need to process an EQSET loop
	 * @param template
	 * @param tmpFile
	 * @param tests
	 * @return int[]
	 *
	 **/
	private static int[] eqsheetSplit(File template, File tmpFile, int[] tests)
	{
		boolean done = false;
		boolean eqset = false;
		int[] ret = new int[2];
		int lineNum = tests[0];
		String aLine;

		//InputStream in = null;
		BufferedWriter output = null;

		try
		{
			//FileWriter w = new FileWriter(tmpFile);
			//output = new BufferedWriter(w);
			output =
					new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(tmpFile), "UTF-8"));

			// read in the eqsheet template file
			//BufferedReader br = new BufferedReader(new FileReader(template));
			BufferedReader br =
					new BufferedReader(new InputStreamReader(
						new FileInputStream(template), "UTF-8"));
			List<String> lines = new ArrayList<String>();

			while ((aLine = br.readLine()) != null)
			{
				lines.add(aLine);
			}

			br.close();

			String line;

			// parse each line and look for EQSET delimiter
			while (!done && (lineNum < lines.size()))
			{
				line = lines.get(lineNum);

				if (line.indexOf("|EQSET.START|") > -1)
				{
					++lineNum;
					done = true;
				}
				else if (line.indexOf("|EQSET.END|") > -1)
				{
					++lineNum;
					done = true;
					eqset = true;
				}
				else
				{
					output.write(line);
					output.newLine();
					++lineNum;
				}
			}
		}
		// end of try
		catch (IOException ioe)
		{
			ShowMessageDelegate.showMessageDialog(
				PropertyFactory.getString("in_ieCldNotCreateTmpEqFileError"), Constants.s_APPNAME ,
				MessageType.ERROR);
			Logging
				.errorPrintLocalised("in_ieCldNotCreateTmpEqFileError");
		}
		finally
		{
			if (output != null)
			{
				try
				{
					output.flush();
					output.close();
				}
				catch (IOException e)
				{
					//TODO: Should we really ignore this?
				}
				catch (NullPointerException e)
				{
					//TODO: Should we really ignore this?
				}
			}
		}

		// return the last line we've parsed through
		ret[0] = lineNum;

		// if we should process an EQSET loop
		if (eqset)
		{
			ret[1] = 1;
		}
		else
		{
			ret[1] = 0;
		}

		return ret;
	}

	/**
	 * takes a template file as input
	 * File template is the original template file
	 * that has been parsed and seperated by
	 * |EQSET.START| and
	 * |EQSET.END| tags
	 *
	 * Loops through all EquipSet's, sets equipped, carried, etc
	 * status on the equipment in each EquipSet and the sends
	 * template file to ExportHandler to get parsed
	 * The output from ExportHandler gets appended to: out
	 * @param template
	 * @param out
	 *
	 **/
	private void equipItch(File template, BufferedWriter out)
	{
		// Array containing the id's of root EquipSet's
		List<EquipSet> eqRootList = new ArrayList<EquipSet>();

		// we count all EquipSet with parent of 0
		for (EquipSet es : equipSetList)
		{
			if (es.getParentIdPath().equals("0"))
			{
				eqRootList.add(es);
			}
		}

		// First, we have to save off the current Calc EquipSet Id
		String oldEqId = pc.getCalcEquipSetId();

		// make sure EquipSet's are in sorted order
		// (important for Containers contents)
		Collections.sort(equipSetList);

		// save off the old TempBonusList
		Map<BonusObj, BonusManager.TempBonusInfo> aList = pc.getTempBonusMap();

		// Current EquipSet count
		int eqCount = 0;

		// Next we loop through all the root EquipSet's, populate
		// the new eqList and print out an iteration of the eqsheet
		for (EquipSet esRL : eqRootList)
		{
			String pid = esRL.getIdPath();

			// be sure to set the currently exporting EquipSet
			// so that token EQUIPSET.NAME can be parsed
			pc.setCurrentEquipSetName(esRL.getName());
			pc.setCalcEquipSetId(pid);
			pc.setEquipSetNumber(eqCount);
			++eqCount;

			// get the current setting for Temp Bonuses
			boolean currentTempBonus = pc.getUseTempMods();

			// now set the value depending on the EquipSet
			pc.setUseTempMods(esRL.getUseTempMods());

			// set TempBonus List
			if (esRL.useTempBonusList())
			{
				pc.setTempBonusMap(esRL.getTempBonusMap());
			}

			// using the split eqsheet template
			// print out the current EquipSet to file
			(new ExportHandler(template)).write(pc, out);

			// reset the Temp Bonuses check back to original value
			pc.setUseTempMods(currentTempBonus);

			// reset TempBonus List back to original value
			pc.setTempBonusMap(aList);
		}

		// make sure everything has been written
		try
		{
			out.flush();
		}
		catch (IOException ioe)
		{
			//ignored
		}

		// Last, set the "working" EquipSet Id back to the old one
		// and recalculate everything
		pc.setCalcEquipSetId(oldEqId);
		pc.setCalcEquipmentList();
		pc.setEquipSetNumber(0);
	}

	/**
	 * Checks to see if Equipment exists in selected EquipSet
	 * and if so, then return the EquipSet containing eqI
	 * @param eSet
	 * @param eqI
	 * @return EquipSet
	 **/
	private EquipSet existingItem(EquipSet eSet, Equipment eqI)
	{
		final String rPath = eSet.getIdPath();

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

	/**
	 * Returns the current number of eqI items in eSet
	 * @param eSet
	 * @param eqI
	 * @return existing quantity
	 **/
	private Float existingQty(EquipSet eSet, Equipment eqI)
	{
		final String rPath = eSet.getIdPath();

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
				return es.getQty();
			}
		}

		return new Float(0);
	}

	/**
	 * This is called when the tab is shown
	 **/
	private void formComponentShown()
	{

		PCGen_Frame1
			.setMessageAreaTextWithoutSaving(PropertyFactory.getString("in_ieSelContToAdd"));

		refresh();

		int s = splitPane.getDividerLocation();
		int t = bsplit.getDividerLocation();
		int u = asplit.getDividerLocation();
		int width;

		if (!hasBeenSized)
		{
			hasBeenSized = true;

			Component c = getParent();
			s =
					SettingsHandler.getPCGenOption("InfoEquipping.splitPane",
						((c.getWidth() * 6) / 10));
			t =
					SettingsHandler.getPCGenOption("InfoEquipping.bsplit", (c
						.getHeight() - 160));
			u =
					SettingsHandler.getPCGenOption("InfoEquipping.asplit", (c
						.getWidth() - 408));

			// set the prefered width on selectedTable
			for (int i = 0; i < selectedTable.getColumnCount(); ++i)
			{
				TableColumn sCol = selectedTable.getColumnModel().getColumn(i);
				width = Globals.getCustColumnWidth("EquipSel", i);

				if (width != 0)
				{
					sCol.setPreferredWidth(width);
				}

				sCol.addPropertyChangeListener(new ResizeColumnListener(
					selectedTable, "EquipSel", i));
			}

			// set the prefered width on availableTable
			for (int i = 0; i < availableTable.getColumnCount(); ++i)
			{
				TableColumn sCol = availableTable.getColumnModel().getColumn(i);
				width = Globals.getCustColumnWidth("EquipAva", i);

				if (width != 0)
				{
					sCol.setPreferredWidth(width);
				}

				sCol.addPropertyChangeListener(new ResizeColumnListener(
					availableTable, "EquipAva", i));
			}

			// have to add this here otherwise it fires before
			// we have everything setup correctly
			calcComboBoxListener = new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					calcComboBoxActionPerformed();
				}
			};
			calcComboBox.addActionListener(calcComboBoxListener);
		}

		if (s > 0)
		{
			splitPane.setDividerLocation(s);
			SettingsHandler.setPCGenOption("InfoEquipping.splitPane", s);
		}

		if (t > 0)
		{
			bsplit.setDividerLocation(t);
			SettingsHandler.setPCGenOption("InfoEquipping.bsplit", t);
		}

		if (u > 0)
		{
			asplit.setDividerLocation(u);
			SettingsHandler.setPCGenOption("InfoEquipping.asplit", u);
		}
	}

	private void hookupPopupMenu(JTreeTable treeTable)
	{
		treeTable.addMouseListener(new EquipPopupListener(treeTable,
			new EquipPopupMenu(treeTable)));
	}

	private void initActionListeners()
	{
		// make sure we update when switching tabs
		this.addFocusListener(new FocusAdapter()
		{
			public void focusGained(FocusEvent evt)
			{
				refresh();
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
		viewEqSetButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				viewEqSetButton();
			}
		});
		exportEqSetButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				exportEqSetButton();
			}
		});
		selectTemplateButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				selectTemplateButton();
			}
		});
		addEquipButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addEquipButton(new Float(1));
			}
		});
		delEquipButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				delEquipButton();
			}
		});
		setQtyButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				setQtyButton(new Float(0));
			}
		});
		setNoteButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				setNoteButton();
			}
		});
		addEquipSetButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addEquipSetButton();
			}
		});
		delEquipSetButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				delEquipSetButton();
			}
		});
		viewComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				viewComboBoxActionPerformed();
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
			SettingsHandler.setPCGenOption("InfoEquipping.splitPane", s); //$NON-NLS-1$
		}

		s = asplit.getDividerLocation();

		if (s > 0)
		{
			SettingsHandler.setPCGenOption("InfoEquipping.asplit", s); //$NON-NLS-1$
		}

		s = bsplit.getDividerLocation();

		if (s > 0)
		{
			SettingsHandler.setPCGenOption("InfoEquipping.bsplit", s); //$NON-NLS-1$
		}
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		readyForRefresh = true;
		//
		// View List Sanity check
		//
		int iView = SettingsHandler.getEquipTab_AvailableListMode();

		if ((iView >= GuiConstants.INFOEQUIPPING_VIEW_NAME)
			&& (iView <= GuiConstants.INFOEQUIPPING_VIEW_TYPE))
		{
			viewMode = iView;
		}
		else
		{
			viewMode = GuiConstants.INFOEQUIPPING_VIEW_EQUIPPED;
		}

		SettingsHandler.setEquipTab_AvailableListMode(viewMode);
		iView = SettingsHandler.getEquipTab_SelectedListMode();

		if ((iView >= GuiConstants.INFOEQUIPPING_VIEW_NAME)
			&& (iView <= GuiConstants.INFOEQUIPPING_VIEW_LOCATION))
		{
			viewSelectMode = iView;
		}
		else
		{
			viewSelectMode = GuiConstants.INFOEQUIPPING_VIEW_NAME;
		}

		SettingsHandler.setEquipTab_SelectedListMode(viewSelectMode);

		viewComboBox.addItem(PropertyFactory.getString("in_nameLabel")
			+ "     ");
		viewComboBox.addItem(PropertyFactory.getString("in_ieLoc") + " ");
		viewComboBox.addItem(PropertyFactory.getString("in_ieEquipped") + " ");
		viewComboBox.addItem(PropertyFactory.getString("in_type") + "     ");
		Utility.setDescription(viewComboBox, "Blah Blah");
		viewComboBox.setSelectedIndex(viewMode);

		ImageIcon newImage;
		newImage = IconUtilitities.getImageIcon("Forward16.gif");
		addEquipButton = new JButton(newImage);
		newImage = IconUtilitities.getImageIcon("Back16.gif");
		delEquipButton = new JButton(newImage);
		setQtyButton = new JButton(PropertyFactory.getString("in_ieSetQt"));
		setNoteButton = new JButton(PropertyFactory.getString("in_ieSetNote"));

		// flesh out all the tree views
		createModels();

		// create tables associated with the above trees
		createTreeTables();

		// fill the ComboBox
		calcComboBoxFill();

		final EquipSet es = (EquipSet) calcComboBox.getSelectedItem();

		if (es != null)
		{
			equipSetTextField.setText(es.getName());
			selectedEquipSet = equipSetTextField.getText();

			TreePath initSelPath = selectedTable.getTree().getPathForRow(0);

			if (initSelPath != null)
			{
				selectedTable.getTree().setSelectionPath(initSelPath);
			}
		}

		buildTopPanel();

		buildBottomPanel();
	}

	private void buildTopPanel()
	{
		// build topPane which will contain leftPane and rightPane
		// leftPane will have two panels and a scrollregion
		// rightPane will have one panel and a scrollregion
		topPane.setLayout(new BorderLayout());

		JPanel leftPane = new JPanel(new BorderLayout());
		JPanel rightPane = new JPanel(new BorderLayout());
		splitPane =
				new FlippingSplitPane(splitOrientation, leftPane, rightPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerSize(10);

		topPane.add(splitPane, BorderLayout.CENTER);

		leftPane.add(InfoTabUtils.createFilterPane(avaLabel, viewComboBox,
			lblQFilter, textQFilter, clearQFilterButton), BorderLayout.NORTH);

		// the available equipment sets panel
		JScrollPane scrollPane =
				new JScrollPane(availableTable,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JButton columnButton = new JButton();
		scrollPane.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER,
			columnButton);
		columnButton.setText(PropertyFactory.getString("in_caretSymbol"));
		new TableColumnManager(availableTable, columnButton, availableModel);
		leftPane.add(scrollPane, BorderLayout.CENTER);

		JPanel bottomLeftPane =
				new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 1));
		Utility.setDescription(addEquipButton, PropertyFactory
			.getString("in_ieAddEqBut"));
		addEquipButton.setEnabled(false);
		bottomLeftPane.add(addEquipButton);
		Utility.setDescription(bottomLeftPane, PropertyFactory
			.getString("in_ieAddEqRight"));
		leftPane.add(bottomLeftPane, BorderLayout.SOUTH);

		// now build the right pane
		// for the selected (equipment) table
		JPanel topRightPane =
				new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 1));
		JLabel selProfileLabel =
				new JLabel(PropertyFactory.getString("in_ieEquipSet") + ":");
		topRightPane.add(selProfileLabel);
		equipSetTextField.setPreferredSize(new Dimension(100, 20));
		topRightPane.add(equipSetTextField);
		addEquipSetButton = new JButton(PropertyFactory.getString("in_add"));
		topRightPane.add(addEquipSetButton);
		delEquipSetButton = new JButton(PropertyFactory.getString("in_ieDel"));
		topRightPane.add(delEquipSetButton);

		rightPane.add(topRightPane, BorderLayout.NORTH);

		JScrollPane scrollPane2 =
				new JScrollPane(selectedTable,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JButton columnButton2 = new JButton();
		scrollPane2.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER,
			columnButton2);
		columnButton2.setText(PropertyFactory.getString("in_caretSymbol"));
		new TableColumnManager(selectedTable, columnButton2, selectedModel);
		rightPane.add(scrollPane2, BorderLayout.CENTER);

		JPanel bottomRightPane =
				new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 1));
		Utility.setDescription(setNoteButton,
			PropertyFactory.getString("in_ieNoteButDesc"));
		setNoteButton.setEnabled(false);
		bottomRightPane.add(setNoteButton);

		Utility.setDescription(setQtyButton, 
			PropertyFactory.getString("in_ieQtyButDesc"));
		setQtyButton.setEnabled(false);
		bottomRightPane.add(setQtyButton);

		Utility.setDescription(delEquipButton,
			PropertyFactory.getString("in_ieDelEqButDesc"));
		delEquipButton.setEnabled(false);
		bottomRightPane.add(delEquipButton);
		rightPane.add(bottomRightPane, BorderLayout.SOUTH);

		// add the sorter tables to that clicking on the TableHeader
		// actualy does something (gawd damn it's slow!)
		availableSort =
				new JTreeTableSorter(availableTable, (MyPONode) availableModel
					.getRoot(), availableModel);
		selectedSort =
				new JTreeTableSorter(selectedTable, (MyPONode) selectedModel
					.getRoot(), selectedModel);
	}

	private void buildBottomPanel()
	{
		// ---------- build Bottom Panel ----------------
		// botPane will contain a bLeftPane and a bRightPane
		// bLeftPane will contain a scrollregion (equipment info)
		// bRightPane will contain a scrollregion (character Info)
		botPane.setLayout(new BorderLayout());

		JPanel bLeftPane = new JPanel(new BorderLayout());
		JPanel bRightPane = new JPanel();

		asplit =
				new FlippingSplitPane(JSplitPane.HORIZONTAL_SPLIT, bLeftPane,
					bRightPane);
		asplit.setOneTouchExpandable(true);
		asplit.setDividerSize(10);

		botPane.add(asplit, BorderLayout.CENTER);

		// Bottom left panel
		// Create a Weight Panel

		JPanel wPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 1));
		totalWeight.setEditable(false);
		totalWeight.setOpaque(false);
		totalWeight.setBorder(null);
		totalWeight.setBackground(Color.lightGray);
		loadWeight.setEditable(false);
		loadWeight.setOpaque(false);
		loadWeight.setBorder(null);
		loadWeight.setBackground(Color.lightGray);
		loadLimits.setEditable(false);
		loadLimits.setOpaque(false);
		loadLimits.setBorder(null);
		loadLimits.setBackground(Color.lightGray);
		wPanel.add(weightLabel);
		wPanel.add(totalWeight);
		wPanel.add(loadLabel);
		wPanel.add(loadWeight);
		wPanel.add(loadLimitsLabel);
		wPanel.add(loadLimits);

		// create an equipment info scroll area
		JScrollPane sScroll = new JScrollPane();
		TitledBorder sTitle =
				BorderFactory.createTitledBorder(PropertyFactory.getString("in_ieEqInfo"));
		sTitle.setTitleJustification(TitledBorder.CENTER);
		sScroll.setBorder(sTitle);
		infoLabel.setBackground(topPane.getBackground());
		sScroll.setViewportView(infoLabel);

		bLeftPane.add(wPanel, BorderLayout.NORTH);
		bLeftPane.add(sScroll, BorderLayout.CENTER);

		// Bottom right panel
		// create a template select and view panel
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		bRightPane.setLayout(gridbag);

		Utility.buildConstraints(c, 0, 0, 1, 1, 1, 1);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;

		JPanel ePanel = new JPanel();
		gridbag.setConstraints(ePanel, c);
		ePanel.add(calcLabel);
		ePanel.add(calcComboBox);

		Utility.buildConstraints(c, 0, 1, 1, 1, 1, 1);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;

		JPanel iPanel = new JPanel();
		gridbag.setConstraints(iPanel, c);

		viewEqSetButton = new JButton(PropertyFactory.getString("in_ieViewBrowserButTitle"));
		Utility.setDescription(viewEqSetButton,
			PropertyFactory.getString("in_ieViewBrowserButDesc"));
		viewEqSetButton.setEnabled(true);
		iPanel.add(viewEqSetButton);

		exportEqSetButton = new JButton(PropertyFactory.getString("in_ieExportFileButTitle"));
		Utility.setDescription(exportEqSetButton,
			PropertyFactory.getString("in_ieExportFileButDesc"));
		exportEqSetButton.setEnabled(true);
		iPanel.add(exportEqSetButton);

		Utility.buildConstraints(c, 0, 2, 1, 1, 1, 1);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;

		JPanel iiPanel = new JPanel();
		gridbag.setConstraints(iiPanel, c);

		templateTextField.setEditable(false);
		Utility.setDescription(templateTextField, PropertyFactory.getString("in_ieTplTxtFld"));
		templateTextField.setBackground(Color.lightGray);
		templateTextField.setText(SettingsHandler
			.getSelectedEqSetTemplateName());
		selectTemplateButton = new JButton(PropertyFactory.getString("in_ieBtmPanelSelTplButTitle"));
		Utility.setDescription(selectTemplateButton,
				PropertyFactory.getString("in_ieBtmPanelSelTplButDesc"));
		iiPanel.add(selectTemplateButton);
		iiPanel.add(templateTextField);

		bRightPane.add(ePanel);
		bRightPane.add(iPanel);
		bRightPane.add(iiPanel);

		// now split the top and bottom Panels
		bsplit =
				new FlippingSplitPane(JSplitPane.VERTICAL_SPLIT, topPane,
					botPane);
		bsplit.setOneTouchExpandable(true);
		bsplit.setDividerSize(10);

		// now add the entire mess (centered of course)
		this.setLayout(new BorderLayout());
		this.add(bsplit, BorderLayout.CENTER);
	}

	/**
	 * @deprecated Consolidate this functionality to one place.
	 * @param eqI Equipment
	 * @param containers List
	 * @return List
	 */
	@Deprecated
	private final List<String> locationChoices(Equipment eqI,
		List<EqSetWrapper> containers)
	{
		// Some Equipment locations are based on the number of hands
		int hands = 0;

		if (pc != null)
		{
			hands = pc.getHands();
		}

		List<String> aList = new ArrayList<String>();

		if (eqI.isWeapon())
		{
			if (eqI.isUnarmed())
			{
				aList.add(Constants.EQUIP_LOCATION_UNARMED);
			}
			else if (eqI.isShield())
			{
				aList.add(Constants.EQUIP_LOCATION_SHIELD);
			}
			else if (eqI.isMelee() && eqI.isWeaponOutsizedForPC(pc))
			{
				// do nothing for melee ousized;
			}
			else if (!(eqI.isMelee()) && eqI.isWeaponOutsizedForPC(pc))
			{
				// do nothing for ranged ousized;
			}
			else
			{
				if (eqI.isWeaponOneHanded(pc))
				{
					aList = getWeaponLocationChoices(hands, Constants.EQUIP_LOCATION_BOTH);

					if (eqI.isMelee())
					{
						if (eqI.isDouble())
						{
							aList.add(Constants.EQUIP_LOCATION_DOUBLE);
						}
					}
				}
				else
				{
					aList.add(Constants.EQUIP_LOCATION_BOTH);

					if (eqI.isMelee() && eqI.isDouble())
					{
						aList.add(Constants.EQUIP_LOCATION_DOUBLE);
					}
				}
			}
		}
		else
		{
			String locName = getSingleLocation(eqI);

			if (locName.length() != 0)
			{
				aList.add(locName);
			}
			else
			{
				aList.add(Constants.EQUIP_LOCATION_EQUIPPED);
			}
		}

		if (!eqI.isUnarmed())
		{
			aList.add(Constants.EQUIP_LOCATION_CARRIED);
			aList.add(Constants.EQUIP_LOCATION_NOTCARRIED);
		}

		//
		// Generate a list of containers
		//
		if (containers != null)
		{
			EquipSet eqSet = getCurrentEquipSet();

			if (eqSet != null)
			{
				final String idPath = eqSet.getIdPath();

				// process all EquipSet Items
				for (EquipSet es : pc.getEquipSet())
				{
					String esID = es.getParentIdPath() + ".";
					String abID = idPath + ".";

					if (esID.startsWith(abID))
					{
						if ((es.getItem() != null)
							&& es.getItem().isContainer())
						{
							containers.add(new EqSetWrapper(es));
						}
					}
				}
			}
		}

		return aList;
	}

	/**
	 * Exports the EquipSets through selected template to a file
	 **/
	private void exportEqSetButton()
	{
		final String template = SettingsHandler.getSelectedEqSetTemplate();
		if (template == null || template.length() == 0)
		{
			ShowMessageDelegate
				.showMessageDialog(
					PropertyFactory.getString("in_ieExportEqSetNoEqSetTpl"),
					Constants.s_APPNAME, MessageType.ERROR);
			return;
		}
		String ext = template.substring(template.lastIndexOf('.'));

		JFileChooser fcExport = new JFileChooser();
		fcExport.setCurrentDirectory(SettingsHandler.getPcgPath());

		fcExport.setDialogTitle(PropertyFactory.getFormattedString("in_ieExportEqSetFileChTitle" , pc.getDisplayName()));

		if (fcExport.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
		{
			return;
		}

		final String aFileName = fcExport.getSelectedFile().getAbsolutePath();

		if (aFileName.length() < 1)
		{
			ShowMessageDelegate.showMessageDialog(PropertyFactory.getString("in_ieExportEqSetNoFilename"),
				Constants.s_APPNAME , MessageType.ERROR);

			return;
		}

		try
		{
			final File outFile = new File(aFileName);

			if (outFile.isDirectory())
			{
				ShowMessageDelegate.showMessageDialog(
					PropertyFactory.getString("in_ieExportEqSetNoOverwriteDirWithFile"), Constants.s_APPNAME ,
					MessageType.ERROR);

				return;
			}

			if (outFile.exists())
			{
				int reallyClose =
						JOptionPane
							.showConfirmDialog(
								this,
								PropertyFactory.getFormattedString("in_ieExportEqSetOverwriteFileConfirmDesc",outFile.getName()),
								PropertyFactory.getFormattedString("in_ieExportEqSetOverwriteFileConfirmTitle", outFile.getName()),
								JOptionPane.YES_NO_OPTION);

				if (reallyClose != JOptionPane.YES_OPTION)
				{
					return;
				}
			}

			if (ext.equalsIgnoreCase(".htm") || ext.equalsIgnoreCase(".html"))
			{
				BufferedWriter w =
						new BufferedWriter(new OutputStreamWriter(
							new FileOutputStream(outFile), "UTF-8"));
				eqSetPrintToWriter(w, template);
				w.flush();
				w.close();
			}
			else if (ext.equalsIgnoreCase(".fo")
				|| ext.equalsIgnoreCase(".pdf"))
			{
				File tmpFile = File.createTempFile("equipSet_", ".fo");
				BufferedWriter w =
						new BufferedWriter(new OutputStreamWriter(
							new FileOutputStream(tmpFile), "UTF-8"));
				eqSetPrintToWriter(w, template);
				w.flush();
				w.close();

				FOPHandler fh = new FOPHandler();

				// setting up pdf renderer
				fh.setMode(FOPHandler.PDF_MODE);
				fh.setInputFile(tmpFile);
				fh.setOutputFile(outFile);

				// render to awt
				fh.run();

				tmpFile.deleteOnExit();

				String errMessage = fh.getErrorMessage();

				if (errMessage.length() > 0)
				{
					ShowMessageDelegate.showMessageDialog(errMessage, Constants.s_APPNAME,
						MessageType.ERROR);
				}
			}
		}
		catch (IOException ex)
		{
			ShowMessageDelegate.showMessageDialog(PropertyFactory.getFormattedString(
				"in_ieExportEqSetUnableToExport", pc.getDisplayName()), Constants.s_APPNAME,
				MessageType.ERROR);
			Logging.errorPrint("Could not export " + pc.getDisplayName(), ex);
		}
	}

	/**
	 * Redraw/recalc everything
	 **/
	private void refreshButton()
	{
		forceRefresh();
	}

	/**
	 * Process a request to rename the current equipment set
	 **/
	private void renameEquipSetButton()
	{
		EquipSet eSet;
		String newName;
		String oldName;

		eSet = getCurrentEquipSet();

		if (eSet == null)
		{
			ShowMessageDelegate.showMessageDialog(
				PropertyFactory.getString("in_ieRenameEqSetNoEqSet"), Constants.s_APPNAME,
				MessageType.ERROR);

			return;
		}

		oldName = eSet.getName();

		// Get a new name
		newName =
				JOptionPane.showInputDialog(null,
					PropertyFactory.getString("in_ieRenameEqSetNewName"), Constants.s_APPNAME,
					JOptionPane.QUESTION_MESSAGE);

		// If they are the same, just return
		if (newName.equals(oldName))
		{
			return;
		}

		// First check to make sure there are no other EquipSet's
		// with the same name (as it causes wierd problems)
		if ((newName != null) && (newName.length() > 0))
		{
			EquipSet pcSet = pc.getEquipSetByName(newName);

			if (pcSet != null)
			{
				ShowMessageDelegate.showMessageDialog(
					PropertyFactory.getString("in_ieRenameEqSetNameAlreadyExists"),
					Constants.s_APPNAME, MessageType.ERROR);

				return;
			}

			// everything looks good, so do it
			eSet.setName(newName);
			selectedEquipSet = newName;
			pc.setDirty(true);
			calcComboBoxFill();
			updateSelectedModel();
		}
	}

	/**
	 *
	 * preview/print EquipSet templates
	 *
	 **/

	/*
	 *
	 *  Select a template for parseing the EquipSet's through
	 *
	 */
	private void selectTemplateButton()
	{
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("in_ieSelTplButFileChooserTitle");
		fc.setCurrentDirectory(SettingsHandler.getPcgenOutputSheetDir());
		fc
			.setSelectedFile(new File(SettingsHandler
				.getSelectedEqSetTemplate()));

		if (fc.showOpenDialog(InfoEquipping.this) == JFileChooser.APPROVE_OPTION)
		{
			SettingsHandler.setSelectedEqSetTemplate(fc.getSelectedFile()
				.getAbsolutePath());
			templateTextField.setText(SettingsHandler
				.getSelectedEqSetTemplateName());
		}
	}

	/**
	 * sell equipment
	 * @param newQty
	 **/
	private void sellEquipButton(Float newQty)
	{
		if (newQty.floatValue() <= 0)
		{
			Object selectedValue =
					JOptionPane.showInputDialog(null, 
						PropertyFactory.getString("in_ieSellEquipGetQuantity"),
						Constants.s_APPNAME, JOptionPane.QUESTION_MESSAGE);

			if (selectedValue != null)
			{
				try
				{
					newQty = new Float(((String) selectedValue).trim());
				}
				catch (Exception e)
				{
					ShowMessageDelegate.showMessageDialog(
						PropertyFactory.getString("in_ieInvalidNumber"),
						Constants.s_APPNAME, MessageType.ERROR);

					return;
				}
			}
		}

		TreePath[] avaCPaths = availableTable.getTree().getSelectionPaths();

		for (int index = 0; index < avaCPaths.length; ++index)
		{
			Object aComp = avaCPaths[index].getLastPathComponent();
			MyPONode fNode = (MyPONode) aComp;

			if (!(fNode.getItem() instanceof Equipment))
			{
				return;
			}

			// get the equipment Item from the available Table
			Equipment eq = (Equipment) fNode.getItem();
			PCGen_Frame1.getCharacterPane().infoInventory().getInfoGear()
				.sellSpecifiedEquipment(eq, newQty.doubleValue());
			forceRefresh();
		}
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

	/**
	 * Updates the Selected table
	 **/
	private void updateSelectedModel()
	{
		TreePath modelSelPath;
		List<String> pathList = selectedTable.getExpandedPaths();

		modelSelPath = selectedTable.getTree().getSelectionPath();

		int idx = selectedTable.getTree().getRowForPath(modelSelPath);

		createSelectedModel();
		selectedTable.updateUI();
		selectedTable.expandPathList(pathList);

		selectedTable.getTree().setSelectionPath(modelSelPath);
		selectedTable.getTree().expandPath(modelSelPath);

		int count = selectedTable.getTree().getRowCount();

		if ((idx >= 0) && (idx < count))
		{
			// set the selected Table row to match the Tree path
			setTableSelectedIndex(selectedTable, idx);
		}
	}

	/**
	 * updates "Load" and "Weight Limits" labels
	 **/
	private void updateTotalWeightLoadLimits()
	{
		List<String> tmp = new ArrayList<String>();
		LoadInfo loadInfo = SettingsHandler.getGame().getLoadInfo();
		for(Load load : Load.values()) {
			String loadstring = load.toString();
			if (loadInfo.getLoadMultiplier(loadstring) != null) {
				Double limit = WeightToken.getLoadToken(loadstring,pc);
				tmp.add(loadstring + "<=" + limit);
			}
		}
		loadLimits.setText(tmp.toString());
	}

	private void updateTotalWeight()
	{
		if (pc == null)
		{
			return;
		}

		pc.setCalcEquipmentList();

		final Float weight = pc.totalWeight();
		final Float roundedValue =
				new Float((new Float(Math.round(weight.doubleValue() * 10000)))
					.floatValue() / 10000);
		totalWeight.setText(Globals.getGameModeUnitSet()
			.displayWeightInUnitSet(roundedValue.doubleValue())
			+ Globals.getGameModeUnitSet().getWeightUnit());
		loadWeight.setText(pc.getLoadType()
			.toString());

		updateTotalWeightLoadLimits();
	}

	/**
	 * Changed the view Sort for available Equipment
	 **/
	private void viewComboBoxActionPerformed()
	{
		final int index = viewComboBox.getSelectedIndex();

		if (index != viewMode)
		{
			viewMode = index;
			SettingsHandler.setEquipTab_AvailableListMode(viewMode);
			updateAvailableModel();
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
		availableModel.resetModel(viewMode, MODEL_AVAIL);
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
		viewMode = GuiConstants.INFOEQUIPPING_VIEW_NAME;
		availableModel.resetModel(viewMode, MODEL_AVAIL);
		clearQFilterButton.setEnabled(true);
		viewComboBox.setEnabled(false);
		forceRefresh();
	}

	/**
	 * Previews the EquipSets through selected template in the Browser
	 **/
	private void viewEqSetButton()
	{
		final String template = SettingsHandler.getSelectedEqSetTemplate();

		if (template == null || template.length() == 0)
		{
			ShowMessageDelegate
				.showMessageDialog(
					PropertyFactory.getString("in_ieViewEqSetNoTpl"),
					Constants.s_APPNAME, MessageType.ERROR);
			return;
		}
		// Karianna - Fix for bug 966281
		File outFile = Utility.getTempPreviewFile(pc, template);

		if (outFile == null)
		{
			return;
		}

		try
		{
			BufferedWriter w =
					new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(outFile), "UTF-8"));
			eqSetPrintToWriter(w, template);
			w.flush();
			w.close();

			URL url = outFile.toURI().toURL();
			Utility.viewInBrowser(url.toString());
		}
		catch (Exception ex)
		{
			Logging.errorPrint(PropertyFactory.getString("in_ieViewEqSetNoViewBrowser"),
				ex);
		}
	}

	/**
	 * Temp Bonus wrapper for the Chooser
	 * each TempWrap contains the creator and target of a bonus
	 **/
	public static final class TempWrap
	{
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
		}

		/**
		 * Get name
		 * @return name
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
				b.append("Player");
			}
			else if (_target instanceof Equipment)
			{
				b.append(((Equipment) _target).getName());
			}

			b.append("]");

			return b.toString();
		}

		/**
		 * Get the creator name (either PC or object that created this item)
		 * @return creator name
		 */
		public String getCreatorName()
		{
			if (_creator instanceof PlayerCharacter)
			{
				return (((PlayerCharacter) _creator).getName());
			}
			else if (_creator instanceof PObject)
			{
				return ((PObject) _creator).getKeyName();
			}
			return "";
		}

		/**
		 * Get the target name
		 * @return target name
		 */
		public String getTargetName()
		{
			if (_target instanceof PlayerCharacter)
			{
				return (((PlayerCharacter) _target).getName());
			}
			else if (_target instanceof PObject)
			{
				return ((PObject) _target).getKeyName();
			}
			return "";
		}

	}

	private static class EqSetWrapper implements Serializable
	{
		private EquipSet eqSet;

		/**
		 * Constructor
		 * @param argEqSet
		 */
		public EqSetWrapper(EquipSet argEqSet)
		{
			eqSet = argEqSet;
		}

		/**
		 * Get the equipment set
		 * @return the equipment set
		 */
		public EquipSet getEqSet()
		{
			return eqSet;
		}

		@Override
		public String toString()
		{
			return eqSet.getItem().getName();
		}
	}

	/*
	 * Allows temporary bonuses on a per EquipSet basis
	 */
	private static final class BonusEditor extends JComboBoxEx implements
			TableCellEditor
	{
		private final transient List<CellEditorListener> d_listeners =
				new ArrayList<CellEditorListener>();
		private transient int d_originalValue = 0;

		private BonusEditor()
		{
			super(new String[]{	PropertyFactory.getString("in_ieBonusEditorNo"), 
								PropertyFactory.getString("in_ieBonusEditorSelected"),
								PropertyFactory.getString("in_ieBonusEditorYes")});
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
				case 0: // Don't use Temp Bonuses
					return Integer.valueOf(0);

				case 1: // use selected Temp Bonuses
					return Integer.valueOf(1);

				case 2: // use Temp Bonuses
					return Integer.valueOf(2);

				default:
					return Integer.valueOf(0);
			}
		}

		public Component getTableCellEditorComponent(JTable jTable, Object obj,
			boolean isSelected, int row, int column)
		{
			if (obj == null)
			{
				return this;
			}

			d_originalValue = this.getSelectedIndex();

			if (obj instanceof Integer)
			{
				int i = ((Integer) obj).intValue();

				if (i == 0)
				{
					setSelectedItem(PropertyFactory.getString("in_ieBonusEditorNo"));
				}
				else if (i == 1)
				{
					setSelectedItem(PropertyFactory.getString("in_ieBonusEditorSelected"));
				}
				else
				{
					setSelectedItem(PropertyFactory.getString("in_ieBonusEditorYes"));
				}
			}
			else if (obj instanceof String)
			{
				setSelectedItem(obj);
			}
			else
			{
				setSelectedItem(PropertyFactory.getString("in_ieBonusEditorNo"));
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

	private static final class ColorRenderer extends DefaultTableCellRenderer
	{
		public Component getTableCellRendererComponent(JTable jTable,
			Object value, boolean isSelected, boolean hasFocus, int row,
			int column)
		{
			JLabel comp =
					(JLabel) super.getTableCellRendererComponent(jTable, value,
						isSelected, hasFocus, row, column);

			if (value instanceof String)
			{
				Font aFont = comp.getFont();
				String fontName = aFont.getName();
				int iSize = aFont.getSize();
				String aString = comp.getText();

				if (aString.indexOf("B|") == 0)
				{
					aString = aString.substring(2, aString.length());

					Font newFont = new Font(fontName, Font.BOLD, iSize);
					comp.setFont(newFont);
				}
				else if (aString.indexOf("I|") == 0)
				{
					aString = aString.substring(2, aString.length());

					Font newFont = new Font(fontName, Font.ITALIC, iSize);
					comp.setFont(newFont);
				}

				comp.setText(aString);
			}

			return comp;
		}
	}

	/**
	 * The TreeTableModel has a single <code>root</code> node
	 * This root node has a null <code>parent</code>
	 * All other nodes have a parent which points to a non-null node
	 * Parent nodes contain a list of  <code>children</code>, which
	 * are all the nodes that point to it as their parent
	 * <code>nodes</code> which have 0 children are leafs (the end of
	 * that linked list)  nodes which have at least 1 child are not leafs
	 * Leafs are like files and non-leafs are like directories
	 * The leafs contain an Object that we want to know about (Equipment)
	 **/
	private final class EquipModel extends AbstractTreeTableModel implements
			TableColumnManagerModel
	{
		// if you change/add/remove entries to nameList
		// you also need to change the static COL_XXX defines
		// at the begining of this file
		private String[] avaNameList =
				new String[]{"Name", "Type", "Qty", "Weight", "Cost"};
		private String[] selNameList =
				new String[]{"Item", "Type", "Qty", "Location", "Temp Bonus"};
		private final int[] avaDefaultWidth = {200, 100, 100, 100, 100, 100};
		private final int[] selDefaultWidth = {200, 100, 100, 100, 100, 100};

		private List<Boolean> displayList = null;

		// Types of the columns.
		private int modelType = MODEL_AVAIL;

		// there are two roots. One for available equipment
		// and one for selected equipment profiles
		private MyPONode avaRoot;
		private MyPONode selRoot;

		/**
		 * Creates a EquipModel
		 * @param mode
		 * @param model
		 **/
		private EquipModel(int mode, int model)
		{
			super(null);

			modelType = model;
			resetModel(mode, model);
			String[] colNameList = getNameList();
			displayList = new ArrayList<Boolean>();
			displayList.add(Boolean.TRUE);
			displayList.add(Boolean.valueOf(getColumnViewOption(modelType + "."
				+ colNameList[1], true)));
			displayList.add(Boolean.valueOf(getColumnViewOption(modelType + "."
				+ colNameList[2], true)));
			displayList.add(Boolean.valueOf(getColumnViewOption(modelType + "."
				+ colNameList[3], true)));
			if (modelType == 0)
			{
				displayList.add(Boolean.valueOf(getColumnViewOption(modelType
					+ "." + colNameList[4], true)));
			}
			else
			{
				displayList.add(Boolean.valueOf(getColumnViewOption(modelType
					+ "." + colNameList[4], false)));
			}
		}

		/**
		 * Returns boolean if can edit a cell. (EquipModel)
		 * @param node
		 * @param column
		 * @return true if cell is editable
		 **/
		public boolean isCellEditable(Object node, int column)
		{
			column = adjustAvailColumnConst(column);

			if (column == COL_NAME)
			{
				return true;
			}

			if ((modelType == MODEL_SELECTED) && (column == COL_COST))
			{
				return true;
			}
			else if (column == COL_QTY)
			{
				final MyPONode fn = (MyPONode) node;

				if ((fn != null) && (fn.getItem() instanceof EquipSet))
				{
					EquipSet eSet = (EquipSet) fn.getItem();

					if (eSet.getItem() != null)
					{
						return true;
					}
				}
			}
			else if (column == COL_BONUS)
			{
				final MyPONode fn = (MyPONode) node;

				if ((fn != null) && (fn.getItem() instanceof EquipSet))
				{
					EquipSet eSet = (EquipSet) fn.getItem();

					if (eSet.getItem() == null)
					{
						return true;
					}
				}
			}

			return false;
		}

		/**
		 * Returns Class for the column. (EquipModel)
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
				case COL_QTY:
				case COL_LOCATION:
				case COL_WEIGHT:
				case COL_COST:
				case COL_BONUS:
					break;

				default:
					Logging
						.errorPrintLocalised("in_ieEquipModelGetColumnClassNotSupported",
						column);

					break;
			}

			return String.class;
		}

		/* The JTreeTableNode interface. */

		/**
		 * Returns int number of columns. (EquipModel)
		 * @return column count
		 **/
		public int getColumnCount()
		{
			return (modelType == MODEL_AVAIL) ? avaNameList.length
				: selNameList.length;
		}

		/**
		 * Returns String name of a column. (EquipModel)
		 * @param column
		 * @return column name
		 **/
		public String getColumnName(int column)
		{
			return (modelType == MODEL_AVAIL) ? avaNameList[column]
				: selNameList[column];
		}

		// return the root node
		public Object getRoot()
		{
			return super.getRoot();
		}

		/**
		 * Used by BonusEditor to set the value of EquipSet.tempBonus
		 * @param value
		 * @param node
		 * @param column
		 **/
		public void setValueAt(Object value, Object node, int column)
		{
			if ((pc == null) || (modelType != MODEL_SELECTED))
			{
				return;
			}

			final MyPONode fn = (MyPONode) node;

			if (!(fn.getItem() instanceof EquipSet))
			{
				return;
			}

			EquipSet eSet = (EquipSet) fn.getItem();

			if (eSet == null)
			{
				return;
			}

			column = adjustAvailColumnConst(column);

			switch (column)
			{
				case COL_BONUS:

					int i = ((Integer) value).intValue();

					if (i == 0)
					{
						eSet.setUseTempMods(false);
						eSet.clearTempBonusList();
					}
					else if (i == 1)
					{
						eSet.setUseTempMods(true);
						eSet.clearTempBonusList();
						chooseTempBonuses(eSet);
					}
					else
					{
						eSet.setUseTempMods(true);
						eSet.clearTempBonusList();
					}

					if (eSet.getIdPath().equals(pc.getCalcEquipSetId()))
					{
						pc.setUseTempMods(eSet.getUseTempMods());
						pc.setDirty(true);
						updateOtherTabs();
					}
					break;

				default:

					// don't do anything
					break;
			}
		}

		/**
		 * Returns Object value of the column. (EquipModel)
		 * @param node
		 * @param column
		 * @return value
		 **/
		public Object getValueAt(Object node, int column)
		{
			final MyPONode fn = (MyPONode) node;
			EquipSet eSet = null;
			Equipment eqI = null;

			if (fn == null)
			{
				Logging
					.errorPrint(PropertyFactory.getString("in_ieEquipModelGetValueAt"));

				return null;
			}

			column = adjustAvailColumnConst(column);

			if (fn.getItem() instanceof Equipment)
			{
				eqI = (Equipment) fn.getItem();
			}

			if (fn.getItem() instanceof EquipSet)
			{
				eSet = (EquipSet) fn.getItem();
				eqI = eSet.getItem();
			}

			switch (column)
			{
				case COL_NAME:

					// This column is a PObjectNode object
					// output is from PObjectNode.tostring()
					return (fn != null) ? fn.toString() : null;

				case COL_TYPE:

					if (eqI != null)
					{
						String type = getEqTypeName(eqI);

						if ("".equals(type))
						{
							StringTokenizer aTok =
									new StringTokenizer(eqI.getType(), ".",
										false);

							if (aTok.hasMoreTokens())
							{
								type = aTok.nextToken();
							}
						}

						if (eqI.isContainer() && (modelType == MODEL_SELECTED))
						{
							StringBuffer b = new StringBuffer();
							b.append("B|");
							b.append(type);

							return b.toString();
						}

						return type;
					}
					return null;

				case COL_QTY:

					if ((eSet != null) && (eSet.getValue().length() > 0))
					{
						return BigDecimalHelper.trimZeros(eSet.getQty()
							.toString());
					}
					else if (eqI != null)
					{
						if ((viewMode == GuiConstants.INFOEQUIPPING_VIEW_EQUIPPED)
							&& (fn.getParent().getNodeName() != null))
						{
							String nodeName = fn.getParent().getNodeName();
							if (nodeName.equals(nameAdded))
							{
								return equipAddMap.get(eqI.getName());
							}
							return equipNotMap.get(eqI.getName());
						}
						return BigDecimalHelper.trimZeros(eqI.getQty()
							.toString());
					}
					else
					{
						return null;
					}

				case COL_LOCATION:

					StringBuffer b = new StringBuffer();

					if ((eSet != null) && (eqI != null))
					{
						StringTokenizer aTok =
								new StringTokenizer(eSet.getIdPath(), ".",
									false);

						if (aTok.countTokens() > 3)
						{
							b.append("I|");
							b.append(eSet.toString());
						}
						else
						{
							b.append(eSet.toString());
						}

						if (eSet.getNote().length() > 0)
						{
							b.append(" [");
							b.append(eSet.getNote());
							b.append("]");
						}

						return b.toString();
					}

					if (eSet != null)
					{
						b.append(" (");
						b.append(Globals.getGameModeUnitSet()
							.displayWeightInUnitSet(
								pc.getEquipSetWeightDouble(eSet.getIdPath())));
						b.append(Globals.getGameModeUnitSet().getWeightUnit());
						b.append(")");

						return b.toString();
					}

					return (fn != null) ? fn.toString() : null;

				case COL_WEIGHT:
					return (eqI != null)
						? Globals.getGameModeUnitSet().displayWeightInUnitSet(
							eqI.getWeight(pc).doubleValue()) : null;

				case COL_COST:
					return (eqI != null) ? BigDecimalHelper.trimZeros(eqI
						.getCost(pc).toString()) : null;

				case COL_BONUS:

					if ((eqI == null) && (eSet != null))
					{
						// Allow bonuses?
						if (eSet.getUseTempMods())
						{
							if (eSet.useTempBonusList())
							{
								return "Selected";
							}

							return "Yes";
						}
						return "No";
					}

					return null;

				default:

					if (fn != null)
					{
						return fn.toString();
					}
					Logging
						.errorPrintLocalised("in_ieEquipModelGetValueAtBis");
					return null;
			}
		}

		// There must be a root node, but we keep it hidden
		private void setRoot(MyPONode aNode)
		{
			super.setRoot(aNode);
		}

		/**
		 * changes the column order sequence and/or number of
		 * columns based on modelType (0=available, 1=selected)
		 * @param column
		 * @return int
		 **/
		private int adjustAvailColumnConst(int column)
		{
			// available table
			if ((modelType == MODEL_AVAIL) && (column == COL_LOCATION))
			{
				return COL_WEIGHT;
			}

			if ((modelType == MODEL_SELECTED) && (column == COL_COST))
			{
				return COL_BONUS;
			}

			return column;
		}

		/**
		 * This assumes the EquipModel exists but
		 * needs branches and nodes to be repopulated
		 * @param mode
		 * @param model
		 **/
		private void resetModel(int mode, int model)
		{
			// This is the array of all equipment types
			List<String> typeList = new ArrayList<String>();
			List<String> locList = new ArrayList<String>();

			// build the list of all equipment types
			typeList.add(Constants.s_CUSTOM);

			for (Equipment bEq : pc.getEquipmentMasterList())
			{
				final StringTokenizer aTok =
						new StringTokenizer(bEq.getType(), ".", false);
				String aString;

				while (aTok.hasMoreTokens())
				{
					aString = aTok.nextToken();

					if (!typeList.contains(aString))
					{
						typeList.add(aString);
					}
				}
			}

			for (EquipSlot eSlot : SystemCollections
				.getUnmodifiableEquipSlotList())
			{
				final String aString = eSlot.getSlotName();

				if (!locList.contains(aString) && (aString.length() > 0))
				{
					locList.add(aString);
				}
			}

			locList.add("Other");
			Collections.sort(typeList);
			Collections.sort(locList);

			// Setup the default EquipSet if not already present
			if (!pc.hasEquipSet())
			{
				String id = getNewIdPath(null);
				EquipSet eSet = new EquipSet(id, defaultEquipSet);
				pc.addEquipSet(eSet);

				selectedEquipSet = defaultEquipSet;
			}

			if ("".equals(selectedEquipSet))
			{
				EquipSet cES = pc.getEquipSetByIdPath(pc.getCalcEquipSetId());

				if (cES != null)
				{
					selectedEquipSet = cES.getName();
					equipSetTextField.setText(selectedEquipSet);
				}
			}

			//
			// build availableTable (list of all equipment)
			//
			if (model == MODEL_AVAIL)
			{
				// this is the root node
				avaRoot = new MyPONode();

				switch (mode)
				{
					// Equipment Type Tree
					case GuiConstants.INFOEQUIPPING_VIEW_TYPE:
						setRoot(avaRoot);

						// build the Type root nodes
						MyPONode[] eq = new MyPONode[typeList.size()];

						// iterate thru the equipment
						// type and fill out the tree
						for (int iType = 0; iType < typeList.size(); ++iType)
						{
							String aType = typeList.get(iType);
							eq[iType] = new MyPONode(aType);

							for (Equipment aEq : pc.getEquipmentMasterList())
							{
								if (!aEq.isType(aType))
								{
									continue;
								}

								MyPONode aFN = new MyPONode(aEq);
								aFN.setParent(eq[iType]);
								eq[iType].addChild(aFN);
							}

							if (!eq[iType].isLeaf())
							{
								eq[iType].setParent(avaRoot);
							}
						}
						// end type loop

						// now add to the root node
						avaRoot.setChildren(eq);

						break; // end VIEW_TYPE

					// Equipment Location Tree
					case GuiConstants.INFOEQUIPPING_VIEW_LOCATION:
						setRoot(avaRoot);

						// build the Location root nodes
						MyPONode[] loc = new MyPONode[locList.size()];

						// iterate thru the equipment
						// type and fill out the tree
						for (int iLoc = 0; iLoc < locList.size(); ++iLoc)
						{
							String aLoc = locList.get(iLoc);
							loc[iLoc] = new MyPONode(aLoc);

							for (Equipment aEq : pc.getEquipmentMasterList())
							{
								String aString = getSingleLocation(aEq);

								if (aEq.isShield())
								{
									aString = "Shield";
								}
								else if (aEq.isWeapon())
								{
									aString = "Weapon";
								}
								else if (aEq.isType("RING"))
								{
									aString = "Fingers";
								}

								if (aString.length() == 0)
								{
									aString = "Other";
								}

								if (!aLoc.equals(aString))
								{
									continue;
								}

								MyPONode aFN = new MyPONode(aEq);
								aFN.setParent(loc[iLoc]);
								loc[iLoc].addChild(aFN);
							}

							if (!loc[iLoc].isLeaf())
							{
								loc[iLoc].setParent(avaRoot);
							}
						}
						// end location loop

						// now add to the root node
						avaRoot.setChildren(loc);

						break; // end VIEW_LOCATION

					// just by equipment name
					case GuiConstants.INFOEQUIPPING_VIEW_NAME:
						setRoot(avaRoot);
						String qFilter = this.getQFilter();

						// iterate thru all PC's equip
						// and fill out the tree
						for (Equipment aEq : pc.getEquipmentMasterList())
						{
							if (qFilter == null
								|| (aEq.getName().toLowerCase()
									.indexOf(qFilter) >= 0 || aEq.getType()
									.toLowerCase().indexOf(qFilter) >= 0))
							{
								MyPONode aFN = new MyPONode(aEq);
								aFN.setParent(avaRoot);
								avaRoot.addChild(aFN, true);
							}

						}

						break; // end VIEW_NAME

					// Equipment Added/not added Tree
					case GuiConstants.INFOEQUIPPING_VIEW_EQUIPPED:
						setRoot(avaRoot);

						// get current EquipSet
						String esN = equipSetTextField.getText();
						EquipSet pSet = pc.getEquipSetByName(esN);

						if (pSet == null)
						{
							break;
						}

						String pId = pSet.getIdPath();
						MyPONode[] add = new MyPONode[2];
						add[0] = new MyPONode(nameAdded);
						add[1] = new MyPONode(nameNotAdded);

						equipAddMap.clear();
						equipNotMap.clear();

						// iterate thru all PC's equip
						// and build the AddMap and
						// NotMap for current EquipSet
						for (Equipment aEq : pc.getEquipmentMasterList())
						{
							String eqName = aEq.getName();
							Float countAdd = pc.getEquipSetCount(pId, eqName);
							Float countNot =
									new Float(aEq.getQty().floatValue()
										- countAdd.floatValue());

							if (countAdd.floatValue() > 0.0f)
							{
								// add to already added Node
								MyPONode aFN = new MyPONode(aEq);
								aFN.setParent(add[0]);
								add[0].addChild(aFN);
								add[0].setParent(avaRoot);
								equipAddMap.put(eqName, countAdd);
							}
							if (countNot.floatValue() > 0.0f)
							{
								// add to Not added Node
								MyPONode aFN = new MyPONode(aEq);
								aFN.setParent(add[1]);
								add[1].addChild(aFN);
								add[1].setParent(avaRoot);
								equipNotMap.put(eqName, countNot);
							}
						}

						avaRoot.setChildren(add);

						break; // end VIEW_EQUIPPED

					default:
						Logging
							.errorPrintLocalised("in_ieEquipModelResetModelIllegalMode",mode);

						break;
				} // end of switch(mode)
			}
			// end of availableTable builder

			else
			{ // selectedTable builder (it's a list of Equip sets)

				// this is the root node
				selRoot = new MyPONode();

				// get the current EquiSet's
				equipSetList = new ArrayList<EquipSet>(pc.getEquipSet());

				// Make sure it's sorted by pathId
				Collections.sort(equipSetList);

				// create a clone to manipulate
				tempSetList = new ArrayList<EquipSet>(equipSetList);

				// EquipSet tree
				addEquipTreeNodes(selRoot, null);
				setRoot(selRoot);
			}
			// end if else

			MyPONode rootAsmyPONode = (MyPONode) super.getRoot();

			if (rootAsmyPONode.getChildCount() > 0)
			{
				fireTreeNodesChanged(super.getRoot(), new TreePath(super
					.getRoot()));
			}
		}

		private String[] getNameList()
		{
			String[] colNameList = null;
			if (modelType == MODEL_AVAIL)
			{
				colNameList = avaNameList;
			}
			else
			{
				colNameList = selNameList;
			}
			return colNameList;
		}

		private int[] getWidthList()
		{
			int[] colWidthList = null;
			if (modelType == MODEL_AVAIL)
			{
				colWidthList = avaDefaultWidth;
			}
			else
			{
				colWidthList = selDefaultWidth;
			}
			return colWidthList;
		}

		public List<String> getMColumnList()
		{
			String[] colNameList = getNameList();
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
			String[] colNameList = getNameList();
			setColumnViewOption(modelType + "." + colNameList[col], disp);
			displayList.set(col, Boolean.valueOf(disp));
		}

		public int getMColumnOffset()
		{
			return 1;
		}

		public int getMColumnDefaultWidth(int col)
		{
			String[] colNameList = getNameList();
			int[] colDefaultWidth = getWidthList();
			return SettingsHandler.getPCGenOption("InfoEquipping.sizecol."
				+ modelType + "." + colNameList[col], colDefaultWidth[col]);
		}

		public void setMColumnDefaultWidth(int col, int width)
		{
			String[] colNameList = getNameList();
			SettingsHandler.setPCGenOption("InfoEquipping.sizecol." + modelType
				+ "." + colNameList[col], width);
		}

		public void resetMColumn(int num, TableColumn column)
		{
			if (modelType == MODEL_AVAIL)
			{
				switch (adjustAvailColumnConst(num))
				{
					case COL_QTY:
					case COL_COST:
					case COL_WEIGHT:
						column
							.setCellRenderer(new pcgen.gui.utils.JTableEx.AlignCellRenderer(
								SwingConstants.CENTER));
						break;

					default:
						break;
				}
			}
			else
			{
				switch (num)
				{
					case COL_TYPE:
						column.setCellRenderer(new ColorRenderer());
						break;
					case COL_LOCATION:
						column.setCellRenderer(new ColorRenderer());
						break;
					case COL_QTY:
						column.setCellEditor(new QuantityEditor());
						break;
					case COL_COST:
						column.setCellEditor(new BonusEditor());
						break;
				}
			}
		}

		private boolean getColumnViewOption(String colName, boolean defaultVal)
		{
			return SettingsHandler.getPCGenOption("InfoEquipping.viewcol."
				+ modelType + "." + colName, defaultVal);
		}

		private void setColumnViewOption(String colName, boolean val)
		{
			SettingsHandler.setPCGenOption("InfoEquipping.viewcol." + modelType
				+ "." + colName, val);
		}
	}

	private class EquipPopupListener extends MouseAdapter
	{
		private EquipPopupMenu menu;

		//private JTreeTable aTreeTable;
		private JTree tree;

		EquipPopupListener(JTreeTable treeTable, EquipPopupMenu aMenu)
		{
			//aTreeTable = treeTable;
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
					menu.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			}
		}
	}

	/**
	 * create right click menus and listeners
	 **/
	private class EquipPopupMenu extends JPopupMenu
	{
		static final long serialVersionUID = 6988134124127535195L;
		private String lastSearch = "";

		EquipPopupMenu(JTreeTable treeTable)
		{
			if (treeTable == availableTable)
			{
				EquipPopupMenu.this.add(createAddMenuItem(PropertyFactory
					.getString("in_ieAddItem2"), "shortcut EQUALS"));
				EquipPopupMenu.this.add(createAddNumMenuItem(PropertyFactory
					.getString("in_ieAddItem")));
				EquipPopupMenu.this.add(createAddAllMenuItem(PropertyFactory
					.getString("in_ieAddAllItem")));
				EquipPopupMenu.this.addSeparator();
				EquipPopupMenu.this.add(createBuyMenuItem("Buy 1"));
				EquipPopupMenu.this.add(createBuyNumMenuItem("Buy #"));
				EquipPopupMenu.this.add(createSellMenuItem("Sell 1"));
				EquipPopupMenu.this.add(createSellNumMenuItem("Sell #"));
				EquipPopupMenu.this.addSeparator();
				EquipPopupMenu.this
					.add(createRefreshMenuItem(PropertyFactory.getString(
							"in_ieEquipPopupRefreshLabel")));
				this.addSeparator();
				EquipPopupMenu.this.add(Utility.createMenuItem(
					PropertyFactory.getString("in_ieEquipPopupFindItem"),
					new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							lastSearch = availableTable.searchTree(lastSearch);
						}
					}, "searchItem", (char) 0, "shortcut F", 
					PropertyFactory.getString("in_ieEquipPopupFindItem"), null,
					true));
			}
			else
			// selectedTable
			{
				EquipPopupMenu.this.add(createDelMenuItem(PropertyFactory
					.getString("in_ieRemEq"), "shortcut MINUS"));
				EquipPopupMenu.this.add(createSetQtyMenuItem(PropertyFactory
					.getString("in_ieSetQt")));
				EquipPopupMenu.this
					.add(createSetLocationMenuItem(PropertyFactory
						.getString("in_ieChangeLoc")));
				EquipPopupMenu.this.add(createSetNoteMenuItem(PropertyFactory
					.getString("in_ieSetNote")));
				EquipPopupMenu.this.addSeparator();
				EquipPopupMenu.this
					.add(createCopyEquipSetMenuItem(PropertyFactory
						.getString("in_ieCopyEq")));
				EquipPopupMenu.this
					.add(createRenameEquipSetMenuItem(PropertyFactory
						.getString("in_ieRenameEq")));
				EquipPopupMenu.this.addSeparator();
				EquipPopupMenu.this
					.add(createRefreshMenuItem(PropertyFactory
						.getString("in_ieEquipPopupRefreshLabel")));
				this.addSeparator();
				EquipPopupMenu.this.add(Utility.createMenuItem(
					PropertyFactory.getString("in_ieEquipPopupFindItem"),
					new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							lastSearch = selectedTable.searchTree(lastSearch);
						}
					}, "searchItem", (char) 0, "shortcut F", 
					PropertyFactory.getString("in_ieEquipPopupFindItem"), null,
					true));
			}
		}

		private JMenuItem createAddAllMenuItem(String label)
		{
			AddAllMenu =
					Utility.createMenuItem(label,
						new AddAllEquipActionListener(), PropertyFactory
							.getString("in_ieAddAll"), (char) 0, null,
						PropertyFactory.getString("in_ieAddAllItem"), "", true);

			return AddAllMenu;
		}

		private JMenuItem createAddMenuItem(String label, String accelerator)
		{
			AddMenu =
					Utility.createMenuItem(label, new AddEquipActionListener(),
						PropertyFactory.getString("in_add") + " 1", (char) 0,
						accelerator, PropertyFactory.getString("in_ieAddEq"),
						"", true);

			return AddMenu;
		}

		private JMenuItem createAddNumMenuItem(String label)
		{
			AddNumMenu =
					Utility.createMenuItem(label,
						new AddNumEquipActionListener(), PropertyFactory
							.getString("in_add")
							+ " #", (char) 0, null, PropertyFactory
							.getString("in_ieAddItem"), "", true);

			return AddNumMenu;
		}

		private JMenuItem createBuyMenuItem(String label)
		{
			BuyMenu =
					Utility.createMenuItem(label, new BuyEquipActionListener(),
						"Buy 1", (char) 0, null, "", "", true);

			return BuyMenu;
		}

		private JMenuItem createBuyNumMenuItem(String label)
		{
			BuyNumMenu =
					Utility.createMenuItem(label,
						new BuyNumEquipActionListener(), "Buy #", (char) 0,
						null, "", "", true);

			return BuyNumMenu;
		}

		private JMenuItem createCopyEquipSetMenuItem(String label)
		{
			CopyEquipSetMenu =
					Utility.createMenuItem(label,
						new CopyEquipSetActionListener(), PropertyFactory
							.getString("in_ieCopyEq"), (char) 0, null,
						PropertyFactory.getString("in_ieDupEq"), "", true);

			return CopyEquipSetMenu;
		}

		private JMenuItem createDelMenuItem(String label, String accelerator)
		{
			DelMenu =
					Utility.createMenuItem(label, new DelEquipActionListener(),
						PropertyFactory.getString("in_remove") + " 1",
						(char) 0, accelerator, PropertyFactory
							.getString("in_ieRemEq"), "", true);

			return DelMenu;
		}

		private JMenuItem createRefreshMenuItem(String label)
		{
			return Utility.createMenuItem(label, new RefreshActionListener(),
				PropertyFactory.getString("in_ieEquipPopupRefreshLabel"), (char) 0, null,
				PropertyFactory.getString("in_ieEquipPopupRefreshDescription"), "", true);
		}

		private JMenuItem createRenameEquipSetMenuItem(String label)
		{
			RenameEquipSetMenu =
					Utility.createMenuItem(label,
						new RenameEquipSetActionListener(), PropertyFactory
							.getString("in_ieRenameEq"), (char) 0, null,
						PropertyFactory.getString("in_ieRenameEqThis"), "",
						true);

			return RenameEquipSetMenu;
		}

		private JMenuItem createSellMenuItem(String label)
		{
			SellMenu =
					Utility.createMenuItem(label,
						new SellEquipActionListener(), "Sell 1", (char) 0,
						null, "", "", true);

			return SellMenu;
		}

		private JMenuItem createSellNumMenuItem(String label)
		{
			SellNumMenu =
					Utility.createMenuItem(label,
						new SellNumEquipActionListener(), "Sell #", (char) 0,
						null, "", "", true);

			return SellNumMenu;
		}

		private JMenuItem createSetLocationMenuItem(String label)
		{
			SetLocationMenu =
					Utility.createMenuItem(label,
						new SetLocationActionListener(), PropertyFactory
							.getString("in_ieChangeLoc"), (char) 0, null,
						PropertyFactory.getString("in_ieChangeLoc"), "", true);

			return SetLocationMenu;
		}

		private JMenuItem createSetNoteMenuItem(String label)
		{
			SetNoteMenu =
					Utility.createMenuItem(label, new SetNoteActionListener(),
						PropertyFactory.getString("in_ieSetNote"), (char) 0,
						null, PropertyFactory.getString("in_ieSetNotefull"),
						"", true);

			return SetNoteMenu;
		}

		private JMenuItem createSetQtyMenuItem(String label)
		{
			SetQtyMenu =
					Utility.createMenuItem(label, new SetQtyActionListener(),
						PropertyFactory.getString("in_ieSetQt"), (char) 0,
						null, PropertyFactory.getString("in_ieSetQtfull"), "",
						true);

			return SetQtyMenu;
		}

		private class AddAllEquipActionListener extends EquipActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				addAllEquipButton();
			}
		}

		private class AddEquipActionListener extends EquipActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				addEquipButton(new Float(1));
			}
		}

		private class AddNumEquipActionListener extends EquipActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				addNumEquipButton();
			}
		}

		private class BuyEquipActionListener extends EquipActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				buyEquipButton(new Float(1));
			}
		}

		private class BuyNumEquipActionListener extends EquipActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				buyEquipButton(new Float(-1));
			}
		}

		private class CopyEquipSetActionListener extends EquipActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				copyEquipSetButton();
			}
		}

		private class DelEquipActionListener extends EquipActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				delEquipButton();
			}
		}

		private class EquipActionListener implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				// TODO This method currently does nothing?
			}
		}

		private class RefreshActionListener extends EquipActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				refreshButton();
			}
		}

		private class RenameEquipSetActionListener extends EquipActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				renameEquipSetButton();
			}
		}

		private class SellEquipActionListener extends EquipActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				sellEquipButton(new Float(1));
			}
		}

		private class SellNumEquipActionListener extends EquipActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				sellEquipButton(new Float(-1));
			}
		}

		private class SetLocationActionListener extends EquipActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				setLocationButton();
			}
		}

		private class SetNoteActionListener extends EquipActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				setNoteButton();
			}
		}

		private class SetQtyActionListener extends EquipActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				setQtyButton(new Float(0));
			}
		}
	}

	/**
	 * Allows in-cell editing of a value
	 **/
	private final class QuantityEditor extends JTextField implements
			TableCellEditor
	{
		private final transient List<CellEditorListener> d_listeners =
				new ArrayList<CellEditorListener>();
		private transient String d_originalValue = "";

		private QuantityEditor()
		{
			super();
			setEditable(true);
			this.setHorizontalAlignment(SwingConstants.RIGHT);
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
					setText("0");
				}
			}

			d_originalValue = getText();
			jTable.setRowSelectionInterval(row, row);
			jTable.setColumnSelectionInterval(column, column);
			this.setHorizontalAlignment(SwingConstants.RIGHT);
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
			setQtyButton((Float) getCellEditorValue());

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
			final Object item = super.getItem();

			if (item == null)
			{
				return "";
			}

			if (item instanceof EquipSet)
			{
				final EquipSet eSet = (EquipSet) item;
				final Equipment eqI = eSet.getItem();

				if (eqI == null)
				{
					StringBuffer b = new StringBuffer();
					b.append("B|");
					b.append(eSet.toString());

					return b.toString();
				}

				return OutputNameFormatting.piString(eqI, true);
			}
			else if (item instanceof Equipment)
			{
				return OutputNameFormatting.piString(((Equipment) item), true);
			}
			else
			{
				return super.toString();
			}
		}
	}
}
