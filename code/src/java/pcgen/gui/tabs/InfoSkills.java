/*
 * InfoSkills.java
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
 * Created on May 1, 2001, 5:57 PM
 * ReCreated on Feb 22, 2002 7:45 AM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.gui.tabs;

import static pcgen.gui.HTMLUtils.BOLD;
import static pcgen.gui.HTMLUtils.BR;
import static pcgen.gui.HTMLUtils.END_BOLD;
import static pcgen.gui.HTMLUtils.END_FONT;
import static pcgen.gui.HTMLUtils.END_HTML;
import static pcgen.gui.HTMLUtils.FONT_PLUS_1;
import static pcgen.gui.HTMLUtils.HTML;
import static pcgen.gui.HTMLUtils.THREE_SPACES;

import java.awt.BorderLayout;
import java.awt.Color;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.InputVerifier;
import javax.swing.JButton;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.TreePath;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.content.TabInfo;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.cdom.enumeration.Type;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.RuleConstants;
import pcgen.core.SettingsHandler;
import pcgen.core.Skill;
import pcgen.core.SkillComparator;
import pcgen.core.SkillUtilities;
import pcgen.core.analysis.ChooseActivation;
import pcgen.core.analysis.OutputNameFormatting;
import pcgen.core.analysis.SkillInfoUtilities;
import pcgen.core.analysis.SkillModifier;
import pcgen.core.analysis.SkillRankControl;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.PrerequisiteUtilities;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui.CharacterInfoTab;
import pcgen.gui.GuiConstants;
import pcgen.gui.InfoSkillsSorter;
import pcgen.gui.InfoSkillsSorters;
import pcgen.gui.PCGen_Frame1;
import pcgen.gui.TableColumnManager;
import pcgen.gui.TableColumnManagerModel;
import pcgen.gui.filter.AbstractPObjectFilter;
import pcgen.gui.filter.FilterAdapterPanel;
import pcgen.gui.filter.FilterConstants;
import pcgen.gui.filter.FilterFactory;
import pcgen.gui.filter.PObjectFilter;
import pcgen.gui.panes.FlippingSplitPane;
import pcgen.gui.utils.AbstractTreeTableModel;
import pcgen.gui.utils.ClickHandler;
import pcgen.gui.utils.IconUtilitities;
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
import pcgen.util.PropertyFactory;
import pcgen.util.ResetableListIterator;
import pcgen.util.StringIgnoreCaseComparator;
import pcgen.util.enumeration.Tab;
import pcgen.util.enumeration.Visibility;

/**
 * @author Bryan McRoberts (merton_monk@yahoo.com)
 * @author Jason Buchanan (lonejedi70@hotmail.com)
 * @author Jayme Cox (jaymecox@users.sourceforge.net)
 * @version $Revision$
 */
public class InfoSkills extends FilterAdapterPanel implements CharacterInfoTab
{
	static final long serialVersionUID = -5369872214039221832L;

	private static final Tab tab = Tab.SKILLS;

	private static boolean resetSelectedModel = true;
	private static PCClass previouslySelectedClass = null;
	private static boolean needsUpdate = true;
	private static int splitOrientation = JSplitPane.HORIZONTAL_SPLIT;

	//column positions for tables
	// keep track of view mode for Available. defaults to "Cost/Name"
	private static int viewMode = GuiConstants.INFOSKILLS_VIEW_COST_NAME;
	private static Integer saveAvailableViewMode = null;

	// keep track of view mode for Selected. defaults to "Name"
	private static int viewSelectMode = GuiConstants.INFOSKILLS_VIEW_NAME;
	private static Integer saveSelectedViewMode = null;

	// keep track of skills output order. defaults to manual, but will
	// be overriden by the settings from the new or laoded character.
	private static int selectedOutputOrder =
			GuiConstants.INFOSKILLS_OUTPUT_BY_MANUAL;

	//table model modes
	private static final int MODEL_AVAIL = 0;
	private static final int MODEL_SELECT = 1;
	// +/- button column indexes
	private static final int COL_INC = 8;
	private static final int COL_DEC = 9;
	/** The Number of costs to display - CSkill, CCSkill and x */
	public static final int nCosts = 3;
	private final JLabel avaLabel =
			new JLabel(PropertyFactory.getString("in_iskDisplay_By")); //$NON-NLS-1$
	private final JLabel selLabel =
			new JLabel(PropertyFactory.getString("in_iskDisplay_By")); //$NON-NLS-1$
	private FlippingSplitPane asplit;
	private FlippingSplitPane bsplit;
	private FlippingSplitPane splitPane;
	private JButton addButton;
	private JButton removeButton;
	private JButton clearAvailableQFilterButton = new JButton(PropertyFactory.getString("in_clear"));
	private JButton clearSelectedQFilterButton = new JButton(PropertyFactory.getString("in_clear"));
	private JComboBoxEx currCharacterClass = null; // now contains Strings of Class/lvl

	/** The output order selection drop-down */
	private JComboBoxEx outputOrderComboBox = new JComboBoxEx();
	private JComboBoxEx skillChoice = new JComboBoxEx();
	private JComboBoxEx viewComboBox = new JComboBoxEx();
	private JComboBoxEx viewSelectComboBox = new JComboBoxEx();
	private JLabel exclusiveLabel = new JLabel();
	private JLabel includeLabel = new JLabel();
	private final JLabel lblAvailableQFilter = new JLabel(PropertyFactory.getString("in_filter") + ":");
	private final JLabel lblSelectedQFilter = new JLabel(PropertyFactory.getString("in_filter") + ":");
	private JLabel jLbClassSkillPoints = null;
	private JLabel jLbMaxCrossSkill = new JLabel();
	private JLabel jLbMaxSkill = new JLabel();
	private JLabel jLbTotalSkillPointsLeft = new JLabel();
	private JLabel maxCrossSkillRank = new JLabel();
	private JLabel maxSkillRank = new JLabel();
	private JLabelPane infoLabel = new JLabelPane();
	private JPanel center = new JPanel();
	private JPanel jPanel1 = new JPanel();
	private JScrollPane cScroll = new JScrollPane();
	//	private JTextField exclusiveSkillCost = new JTextField();
	private JTextField textAvailableQFilter = new JTextField();
	private JTextField textSelectedQFilter = new JTextField();
	private JTreeTable availableTable;
	private JTreeTable selectedTable;
	private JTreeTableSorter availableSort;
	private JTreeTableSorter selectedSort;
	private RendererEditor plusMinusRenderer = new RendererEditor();

	//keep track of which skill was selected last from either table
	private Skill lastSkill;
	private SkillModel availableModel;
	private SkillModel selectedModel;

	// Right-click table item
	private TreePath selPath;
	private WholeNumberField currCharClassSkillPnts = null;
	private WholeNumberField totalSkillPointsLeft = new WholeNumberField(0, 4);
	private boolean hasBeenSized = false;

	private PlayerCharacter pc;
	private int serial = 0;
	private boolean readyForRefresh = false;

	/**
	 * Constructor
	 * @param pc
	 */
	public InfoSkills(PlayerCharacter pc)
	{
		this.pc = pc;
		// do not remove this
		// we will use the component's name to save component specific settings
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
		return SettingsHandler.getPCGenOption(".Panel.Skills.Order", tab
			.ordinal());
	}

	public void setTabOrder(int order)
	{
		SettingsHandler.setPCGenOption(".Panel.Skills.Order", order);
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
		if (pc.getSkillPoints() < 0)
		{
			toDoList.add(PropertyFactory.getString("in_iskTodoTooMany")); //$NON-NLS-1$
		}
		else if (pc.getSkillPoints() > 0)
		{
			toDoList.add(PropertyFactory.getString("in_iskTodoRemain")); //$NON-NLS-1$
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
	public final boolean isMatchAnyEnabled()
	{
		return true;
	}

	/**
	 * specifies whether the "negate/reverse" option should be available
	 * @return true
	 */
	public final boolean isNegateEnabled()
	{
		return true;
	}

	/**
	 * specifies the filter selection mode
	 * @return FilterConstants.MULTI_MULTI_MODE = 2
	 */
	public final int getSelectionMode()
	{
		return FilterConstants.MULTI_MULTI_MODE;
	}

	/**
	 * Create a Skill Wrapper
	 * @param available
	 * @param skill
	 * @param pc
	 * @return a Skill Wrapper
	 */
	public static SkillWrapper createSkillWrapper(boolean available,
		Skill skill, PlayerCharacter pc)
	{
		Integer outputIndex = pc.getAssoc(skill, AssociationKey.OUTPUT_INDEX);
		if (outputIndex == null)
		{
			outputIndex = Integer.valueOf(0);
		}
		return available ? 
			new SkillWrapper(skill, Integer.valueOf(0), new Float(0), Integer.valueOf(0),
					skill.qualifies(pc, skill)) : 
			new SkillWrapper(skill, SkillModifier.modifier(skill, pc), SkillRankControl.getTotalRank(pc, skill), 
					outputIndex, skill.qualifies(pc, skill));
	}

	/**
	 * implementation of Filterable interface
	 */
	public final void initializeFilters()
	{
		registerFilter(createClassSkillFilter());
		registerFilter(createCrossClassSkillFilter());
		registerFilter(createExclusiveSkillFilter());
		registerFilter(createQualifyFilter());

		FilterFactory.registerAllSourceFilters(this);
		FilterFactory.registerAllSkillFilters(this);
	}

	/**
	 * implementation of Filterable interface
	 */
	public final void refreshFiltering()
	{
		if (availableTable != null) {
			updateAvailableModel();
		}
	}

	private PCLevelInfo getSelectedLevelInfo(PlayerCharacter aPC)
	{
		//
		// No class levels, so can be no information
		//
		if (aPC.getLevelInfo() == null)
		{
			return null;
		}

		int i = -1;
		if (currCharacterClass != null)
		{
			i = Math.max(0, currCharacterClass.getSelectedIndex());
		}
		else
		{
			//
			// Find the first entry with skill points remaining
			//
			i = 0;
			for (PCLevelInfo pcl : aPC.getLevelInfo())
			{
				if (pcl.getSkillPointsRemaining() != 0)
				{
					break;
				}
				++i;
			}
			if (i == aPC.getLevelInfo().size())
			{
				--i;
			}
		}

		if ((i < 0) || (i >= aPC.getLevelInfo().size()))
		{
			return null;
		}
		return aPC.getLevelInfo().get(i);
	}

	/**
	 * Get the currently selected Character Class.
	 *
	 * @return PCClass
	 *         author    Brian Forester  (ysgarran@yahoo.com)
	 */
	public PCClass getSelectedPCClass()
	{
		if (Globals.getGameModeHasPointPool())
		{
			return (PCClass) pc.getSpellClassAtIndex(0);
		}
		PCLevelInfo pcl = getSelectedLevelInfo(pc);
		if (pcl != null)
		{
			return pc.getClassKeyed(pcl.getClassKeyName());
		}
		return null;
	}

	/**
	 * Here we want to select the first class to have remaining skill
	 * points or if none then the last class added
	 * Note: Currently if you add a new level of an older class, there is no
	 * way to tell if the skill points come from the new level or the old level
	 * eg: lvl 1 class a, lvl2 class b, lvl 3 class a
	 * If all points for level 1 have been spent, but points for levels 2
	 * and 3 remain to be spent, class a will erroneously be selected
	 * This can be corrected once skill points are tracked by PCLevelInfo
	 */
	private void setCurrentClassCombo()
	{
		boolean oldFlag = resetSelectedModel;
		PCClass aClass = null;

		// Search for a class with remaining points.
		// Search is done in the order levels are assigned
		// to hopefully get the earliest class with remaining points
		int idx = 0;

		for (; idx < (pc.getLevelInfoSize() - 1); ++idx)
		{
			PCLevelInfo pcl = pc.getLevelInfo().get(idx);

			if (pcl.getSkillPointsRemaining() > 0)
			{
				aClass = pc.getClassKeyed(pcl.getClassKeyName());

				break;
			}
		}

		if (idx < pc.getLevelInfoSize())
		{
			resetSelectedModel = !(previouslySelectedClass == aClass);
			if (currCharacterClass.getSelectedIndex() != idx)
			{
				currCharacterClass.setSelectedIndex(idx);
				updateAvailableModel();
			}
			previouslySelectedClass = aClass;
			resetSelectedModel = oldFlag;
		}
	}

	/**
	 * Retrieve the highest output index used in any of the
	 * character's skills.
	 * @return highest output index
	 */
	private int getHighestOutputIndex()
	{
		int maxOutputIndex = 0;
		for (Skill bSkill : pc.getSkillSet())
		{
			Integer outputIndex = pc.getAssoc(bSkill, AssociationKey.OUTPUT_INDEX);
			if (outputIndex != null && outputIndex > maxOutputIndex)
			{
				maxOutputIndex = outputIndex;
			}
		}

		return maxOutputIndex;
	}

	private static int getSelectedIndex(ListSelectionEvent e)
	{
		final DefaultListSelectionModel model =
				(DefaultListSelectionModel) e.getSource();

		if (model == null)
		{
			return -1;
		}

		return model.getMinSelectionIndex();
	}

	private final void createAvailableModel()
	{
		if (availableModel == null)
		{
			availableModel = new SkillModel(viewMode, true);
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

		return panel;
	}

	/*
	 * ##################################################################
	 * factory methods
	 * these are needed for reflection method calls in FilterFactory!
	 * ##################################################################
	 */
	private final PObjectFilter createClassSkillFilter()
	{
		return new ClassSkillFilter();
	}

	private final PObjectFilter createCrossClassSkillFilter()
	{
		return new CrossClassSkillFilter();
	}

	private final PObjectFilter createExclusiveSkillFilter()
	{
		return new ExclusiveSkillFilter();
	}

	/**
	 * Creates the ClassModel that will be used.
	 */
	private final void createModels()
	{
		createAvailableModel();
		createSelectedModel();
	}

	private final PObjectFilter createQualifyFilter()
	{
		return new QualifyFilter();
	}

	private final void createSelectedModel()
	{
		if (selectedModel == null)
		{
			selectedModel = new SkillModel(viewSelectMode, false);
		}
		else
		{
			if (resetSelectedModel)
			{
				selectedModel.resetModel(viewSelectMode, false);
			}
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
			addSkill(1);
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
					final Skill theSkill = getSelectedSkill();

					if (theSkill != null)
					{
						int points;
						if (ChooseActivation.hasChooseToken(theSkill))
						{
							points = pc.getSkillCostForClass(theSkill,
									getSelectedPCClass()).getCost();
							final int classSkillCost = SkillCost.CLASS
									.getCost();
							if (classSkillCost > 1)
							{
								points /= classSkillCost;
							}
						}
						else
						{
							points = 1;
						}
						addSkill(-points);
					}
				}
			});
		}

		public boolean isSelectable(Object obj)
		{
			return !(obj instanceof String);
		}
	}

	private void availTableMouseClicked(MouseEvent evt)
	{
		final int selectedSkill = availableTable.getSelectedRow();

		if (selectedSkill < 0)
		{
			return;
		}

		Object temp =
				availableTable.getTree().getPathForRow(selectedSkill)
					.getLastPathComponent();

		if (temp == null)
		{
			lastSkill = null;
			ShowMessageDelegate.showMessageDialog(PropertyFactory
				.getString("in_iskErr_message_02"), Constants.APPLICATION_NAME,
				MessageType.ERROR); //$NON-NLS-1$

			return;
		}

		Skill aSkill = null;

		if (temp instanceof PObjectNode)
		{
			temp = ((PObjectNode) temp).getItem();

			if (temp instanceof SkillWrapper)
			{
				aSkill = ((SkillWrapper) temp).getSkWrapSkill();
			}
		}

		addButton.setEnabled(aSkill != null);
		setInfoLabelText(aSkill);

		int column = availableTable.columnAtPoint(evt.getPoint());
		column = availableTable.convertColumnIndexToModel(column);
		skillTableMouseClicked(evt, column);
	}

	private void selectedTableMouseClicked(MouseEvent evt)
	{
		final int selectedSkill = selectedTable.getSelectedRow();
		
		if (selectedSkill < 0)
		{
			return;
		}

		TreePath path = selectedTable.getTree().getPathForRow(selectedSkill);
		Object temp = (path == null ? null : path.getLastPathComponent());

		if (temp == null)
		{
			lastSkill = null;
			ShowMessageDelegate.showMessageDialog(PropertyFactory
				.getString("in_iskErr_message_02"), Constants.APPLICATION_NAME,
				MessageType.ERROR); //$NON-NLS-1$

			return;
		}

		Skill aSkill = null;

		if (temp instanceof PObjectNode)
		{
			temp = ((PObjectNode) temp).getItem();

			if (temp instanceof SkillWrapper)
			{
				aSkill = ((SkillWrapper) temp).getSkWrapSkill();
			}
		}

		removeButton.setEnabled(aSkill != null);
		setInfoLabelText(aSkill);
		int column = selectedTable.columnAtPoint(evt.getPoint());
		column = selectedTable.convertColumnIndexToModel(column);
		skillTableMouseClicked(evt, column);
	}
	
	/**
	 * This method is invoked when the mouse is clicked on the stat table
	 * If the requested change is valid based on the rules mode selected,
	 * it performs the update on the character stat and forces the rest of
	 * the connected items to update.
	 * @param evt The MouseEvent we are processing
	 **/
	private void skillTableMouseClicked(MouseEvent evt, int column)
	{
		switch (column)
		{
			case COL_INC:
				addSkill(1);
				break;

			case COL_DEC:
				addSkill(-1);
				break;

			default:
				break;
		}
	}

	private final void createTreeTables()
	{
		availableTable = new JTreeTable(availableModel);
		availableTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		final JTree tree = availableTable.getTree();
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.setCellRenderer(new LabelTreeCellRenderer());

		availableTable.getSelectionModel().addListSelectionListener(
			new ListSelectionListener()
			{
				public void valueChanged(ListSelectionEvent e)
				{
					if (!e.getValueIsAdjusting())
					{
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
							lastSkill = null;
							ShowMessageDelegate.showMessageDialog(
								PropertyFactory
									.getString("in_iskErr_message_02"),
								Constants.APPLICATION_NAME, MessageType.ERROR); //$NON-NLS-1$

							return;
						}

						Skill aSkill = null;

						if (temp instanceof PObjectNode)
						{
							temp = ((PObjectNode) temp).getItem();

							if (temp instanceof SkillWrapper)
							{
								aSkill = ((SkillWrapper) temp).getSkWrapSkill();
							}
						}

						addButton.setEnabled(aSkill != null);
						setInfoLabelText(aSkill);
					}
				}
			});

		availableTable.addMouseListener(new JTreeTableMouseAdapter(
			availableTable, new AvailableClickHandler(), false));

		availableTable.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				availTableMouseClicked(evt);
			}
		});

		//
		// now do the selectedTable
		//
		selectedTable = new JTreeTable(selectedModel);
		selectedTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		final JTree btree = selectedTable.getTree();
		btree.setRootVisible(false);
		btree.setShowsRootHandles(true);
		btree.setCellRenderer(new LabelTreeCellRenderer());

		selectedTable.getSelectionModel().addListSelectionListener(
			new ListSelectionListener()
			{
				public void valueChanged(ListSelectionEvent e)
				{
					if (!e.getValueIsAdjusting())
					{
						final int idx = getSelectedIndex(e);

						if (idx < 0)
						{
							return;
						}

						Object temp =
								selectedTable.getTree().getPathForRow(idx)
									.getLastPathComponent();

						if (temp == null)
						{
							lastSkill = null;
							infoLabel.setText();

							return;
						}

						Skill aSkill = null;

						if (temp instanceof PObjectNode)
						{
							temp = ((PObjectNode) temp).getItem();

							if (temp instanceof SkillWrapper)
							{
								aSkill = ((SkillWrapper) temp).getSkWrapSkill();
							}
						}

						removeButton.setEnabled(aSkill != null);
						setInfoLabelText(aSkill);
					}
				}
			});
		selectedTable.addMouseListener(new JTreeTableMouseAdapter(
			selectedTable, new SelectedClickHandler(), false));

		selectedTable.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent evt)
			{
				selectedTableMouseClicked(evt);
			}
		});

		hookupPopupMenu(availableTable);
		hookupPopupMenu(selectedTable);
	}

	private Skill getSelectedSkill()
	{
		return lastSkill;
	}

	private void addSkill(int points)
	{
		final Skill theSkill = getSelectedSkill();

		if (theSkill == null)
		{
			return;
		}

		if (theSkill.getSafe(ObjectKey.READ_ONLY))
		{
			ShowMessageDelegate.showMessageDialog("You cannot "
				+ (points < 0 ? "remove" : "add") + " ranks for this skill: "
				+ theSkill.getDisplayName(), Constants.APPLICATION_NAME,
				MessageType.ERROR);
			return;
		}

		final int classSkillCost = SkillCost.CLASS.getCost();
		if (classSkillCost > 1)
		{
			points *= classSkillCost;
		}

		//
		// Get list of skills with fulfilled prereqs
		//
		ArrayList<Skill> prereqSkills = getSatisfiedPrereqSkills(theSkill);

		//
		// If the skill has prerequisites, then make sure we don't invalidate them by adding too many ranks
		//
		boolean dirty = false;
		int pointsLeft = points;
		if ((pointsLeft > 0) && theSkill.hasPrerequisites())
		{
			while (pointsLeft > classSkillCost)
			{
				if (!modRank(theSkill, classSkillCost, false))
				{
					break;
				}
				pointsLeft -= classSkillCost;
				dirty = true;
			}
		}

		// modRank returns true on success and false on failure
		// if we failed to add skill points, don't do anything
		if (!dirty && !modRank(theSkill, pointsLeft, true))
		{
			updateSelectedModel();
			return;
		}

		//
		// Make sure all previously fulfilled prerequisites are still fulfilled
		//
		if (prereqSkillsInvalid(prereqSkills))
		{
			//
			// Back out the modifications
			//
			modRank(theSkill, -points, false);
			updateSelectedModel();
			//
			// Notify the user
			//
			ShowMessageDelegate
				.showMessageDialog(
					"Modifying this skill invalidates the prerequisites for the following skill(s):\n"
						+ prereqSkills.toString(), Constants.APPLICATION_NAME,
					MessageType.ERROR);
			return;
		}

		PCLevelInfo pcl = getSelectedLevelInfo(pc);
		pc.setDirty(true);

		if (pcl != null)
		{
			if (currCharClassSkillPnts != null)
			{
				currCharClassSkillPnts.setValue(pcl.getSkillPointsRemaining());
			}
			totalSkillPointsLeft.setValue(pc.getSkillPoints());
		}

		updateSelectedModel();

		if (pc.isDisplayUpdate())
		{
			pc.setDisplayUpdate(false);
			pc.calcActiveBonuses();
		}

		// ensure that the target skill gets displayed
		// in the selectedTable if you've just added skill points
		if (points > 0)
		{
			selectedTable.expandByPObjectName(theSkill.getKeyName());
		}

		if (Globals.getGameModeHasPointPool())
		{
			totalSkillPointsLeft.setValue(pc.getSkillPoints());
		}
	}

	/**
	 * Action a user requested change in the number of skill points for 
	 * the current class level. 
	 */
	private void currCharClassSkillPntsChanged()
	{
		final PlayerCharacter currentPC = pc;
		currentPC.setDirty(true);

		PCClass aClass = this.getSelectedPCClass();
		PCLevelInfo pcl = getSelectedLevelInfo(currentPC);
		if (pcl == null)
		{
			return;
		}
		int skillPool = pcl.getSkillPointsRemaining();

		if (currCharClassSkillPnts.getText().length() > 0)
		{
			final int anInt =
					Delta.decode(currCharClassSkillPnts.getText()).intValue();

			if ((aClass == null) || (anInt == skillPool))
			{
				return;
			}

			final int i = skillPool - anInt;
			pcl.setSkillPointsRemaining(anInt);
			pc.setAssoc(aClass, AssociationKey.SKILL_POOL, Math.max(0, aClass.getSkillPool(pc) - i));
			currentPC.setDirty(true);
		}

		currCharClassSkillPnts.setValue(pcl.getSkillPointsRemaining());
		totalSkillPointsLeft.setValue(currentPC.getSkillPoints());
	}

	private void currCharacterClassActionPerformed()
	{
		PCLevelInfo pcl = this.getSelectedLevelInfo(pc);
		boolean oldFlag = resetSelectedModel;
		PCClass aClass = null;

		if (pcl != null)
		{
			currCharClassSkillPnts.setValue(pcl.getSkillPointsRemaining());
			totalSkillPointsLeft.setValue(pc.getSkillPoints());
			aClass = pc.getClassKeyed(pcl.getClassKeyName());
			resetSelectedModel = !(aClass == previouslySelectedClass);
		}
		else
		{
			currCharClassSkillPnts.setValue(0);
			totalSkillPointsLeft.setValue(0);
		}

		//we need to do this in order for class/xclass views to re-sort
		updateSelectedModel();
		updateAvailableModel();
		previouslySelectedClass = aClass;
		resetSelectedModel = oldFlag;
	}

	// This is called when the tab is shown.
	private void formComponentShown()
	{
		requestFocus();
		PCGen_Frame1.setMessageAreaTextWithoutSaving(""); //$NON-NLS-1$
		refresh();

		int s = splitPane.getDividerLocation();
		int t = bsplit.getDividerLocation();
		int u = asplit.getDividerLocation();
		int width;

		if (!hasBeenSized)
		{
			hasBeenSized = true;
			s =
					SettingsHandler
						.getPCGenOption(
							"InfoSkills.splitPane", (int) ((this.getSize().getWidth() * 4) / 10)); //$NON-NLS-1$
			t =
					SettingsHandler
						.getPCGenOption(
							"InfoSkills.bsplit", (int) (this.getSize().getHeight() - 120)); //$NON-NLS-1$
			u =
					SettingsHandler
						.getPCGenOption(
							"InfoSkills.asplit", (int) (this.getSize().getWidth() - 500)); //$NON-NLS-1$

			// set the prefered width on selectedTable
			final TableColumnModel selectedTableColumnModel =
					selectedTable.getColumnModel();

			for (int i = 0; i < selectedTable.getColumnCount(); ++i)
			{
				TableColumn sCol = selectedTableColumnModel.getColumn(i);
				int colIndex = sCol.getModelIndex();
				width = Globals.getCustColumnWidth("InfoSel", colIndex); //$NON-NLS-1$

				if (width != 0)
				{
					sCol.setPreferredWidth(width);
				}

				sCol.addPropertyChangeListener(new ResizeColumnListener(
					selectedTable, "InfoSel", i)); //$NON-NLS-1$

				if (colIndex == 6)
				{
					sCol.setCellEditor(new OutputOrderEditor(new String[]{
						PropertyFactory.getString("in_iskFirst"),
						PropertyFactory.getString("in_iskLast"),
						PropertyFactory.getString("in_iskHidden")})); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
				else if (colIndex == COL_INC || colIndex == COL_DEC)
				{
					sCol.setCellRenderer(plusMinusRenderer);
				}
			}

			// set the prefered width on availableTable
			for (int i = 0; i < availableTable.getColumnCount(); ++i)
			{
				final TableColumnModel availableTableColumnModel =
						availableTable.getColumnModel();
				TableColumn sCol = availableTableColumnModel.getColumn(i);
				int colIndex = sCol.getModelIndex();
				width = Globals.getCustColumnWidth("InfoAva", colIndex); //$NON-NLS-1$

				if (width != 0)
				{
					sCol.setPreferredWidth(width);
				}

				sCol.addPropertyChangeListener(new ResizeColumnListener(
					availableTable, "InfoAva", i)); //$NON-NLS-1$

				if (colIndex == 6)
				{
					sCol.setCellEditor(new OutputOrderEditor(new String[]{
						PropertyFactory.getString("in_iskFirst"),
						PropertyFactory.getString("in_iskLast"),
						PropertyFactory.getString("in_iskHidden")})); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
				else if (colIndex == COL_INC || colIndex == COL_DEC)
				{
					sCol.setCellRenderer(plusMinusRenderer);
				}
			}
		}

		if (s > 0)
		{
			splitPane.setDividerLocation(s);
			SettingsHandler.setPCGenOption("InfoSkills.splitPane", s); //$NON-NLS-1$
		}

		if (t > 0)
		{
			bsplit.setDividerLocation(t);
			SettingsHandler.setPCGenOption("InfoSkills.bsplit", t); //$NON-NLS-1$
		}

		if (u > 0)
		{
			asplit.setDividerLocation(u);
			SettingsHandler.setPCGenOption("InfoSkills.asplit", u); //$NON-NLS-1$
		}
	}

	private void hookupPopupMenu(JTreeTable treeTable)
	{
		treeTable.addMouseListener(new SkillPopupListener(treeTable,
			new SkillPopupMenu(treeTable)));
	}

	private void initActionListeners()
	{
		addComponentListener(new ComponentAdapter()
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
						.errorPrint(
							"Failure while showing skills tab. Skills tab may not be properly displayed.",
							e);
				}
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
		removeButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addSkill(-1);
			}
		});
		addButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addSkill(1);
			}
		});
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
		outputOrderComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				outputOrderComboBoxActionPerformed();
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

		FilterFactory.restoreFilterSettings(this);
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		readyForRefresh = true;
		//
		// Sanity check
		//
		int iView = SettingsHandler.getSkillsTab_AvailableListMode();

		if ((iView >= GuiConstants.INFOSKILLS_VIEW_STAT_TYPE_NAME)
			&& (iView <= GuiConstants.INFOSKILLS_VIEW_NAME))
		{
			viewMode = iView;
		}

		SettingsHandler.setSkillsTab_AvailableListMode(viewMode);
		iView = SettingsHandler.getSkillsTab_SelectedListMode();

		if ((iView >= GuiConstants.INFOSKILLS_VIEW_STAT_TYPE_NAME)
			&& (iView <= GuiConstants.INFOSKILLS_VIEW_NAME))
		{
			viewSelectMode = iView;
		}

		SettingsHandler.setSkillsTab_SelectedListMode(viewSelectMode);

		viewComboBox.addItem(PropertyFactory
			.getString("in_iskKeyStat_SubType_Name")); //$NON-NLS-1$
		viewComboBox.addItem(PropertyFactory.getString("in_iskKeyStat_Name")); //$NON-NLS-1$
		viewComboBox.addItem(PropertyFactory.getString("in_iskSubType_Name")); //$NON-NLS-1$
		viewComboBox.addItem(PropertyFactory
			.getString("in_iskCost_SubType_Name")); //$NON-NLS-1$
		viewComboBox.addItem(PropertyFactory.getString("in_iskCost_Name")); //$NON-NLS-1$
		viewComboBox.addItem(PropertyFactory.getString("in_iskName")); //$NON-NLS-1$
		Utility.setDescription(viewComboBox, PropertyFactory
			.getString("in_iskSkill_display_order_tooltip")); //$NON-NLS-1$
		viewComboBox.setSelectedIndex(viewMode); // must be done before createModels call

		viewSelectComboBox.addItem(PropertyFactory
			.getString("in_iskKeyStat_SubType_Name")); //$NON-NLS-1$
		viewSelectComboBox.addItem(PropertyFactory
			.getString("in_iskKeyStat_Name")); //$NON-NLS-1$
		viewSelectComboBox.addItem(PropertyFactory
			.getString("in_iskSubType_Name")); //$NON-NLS-1$
		viewSelectComboBox.addItem(PropertyFactory
			.getString("in_iskCost_SubType_Name")); //$NON-NLS-1$
		viewSelectComboBox
			.addItem(PropertyFactory.getString("in_iskCost_Name")); //$NON-NLS-1$
		viewSelectComboBox.addItem(PropertyFactory.getString("in_iskName")); //$NON-NLS-1$
		Utility.setDescription(viewSelectComboBox, PropertyFactory
			.getString("in_iskSkill_display_order_tooltip")); //$NON-NLS-1$
		viewSelectComboBox.setSelectedIndex(viewSelectMode); // must be done before createModels call

		// Build the Output Order Combo-box
		outputOrderComboBox.addItem(PropertyFactory
			.getString("in_iskBy_name_ascending")); //$NON-NLS-1$
		outputOrderComboBox.addItem(PropertyFactory
			.getString("in_iskBy_name_descending")); //$NON-NLS-1$
		outputOrderComboBox.addItem(PropertyFactory
			.getString("in_iskBy_trained_then_untrained")); //$NON-NLS-1$
		outputOrderComboBox.addItem(PropertyFactory
			.getString("in_iskBy_untrained_then_trained")); //$NON-NLS-1$
		outputOrderComboBox.addItem(PropertyFactory.getString("in_iskManual")); //$NON-NLS-1$
		Utility.setDescription(outputOrderComboBox, PropertyFactory
			.getString("in_iskSkill_output_order_tooltip")); //$NON-NLS-1$
		outputOrderComboBox.setSelectedIndex(selectedOutputOrder);

		createModels();

		// create available table of skills
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
		leftPane.add(InfoTabUtils.createFilterPane(avaLabel, viewComboBox,
			lblAvailableQFilter, textAvailableQFilter,
			clearAvailableQFilterButton), BorderLayout.NORTH);

		JScrollPane scrollPane =
				new JScrollPane(availableTable,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		leftPane.add(scrollPane, BorderLayout.CENTER);

		addButton = new JButton(IconUtilitities.getImageIcon("Forward16.gif"));
		leftPane.add(buildModPanel(addButton, PropertyFactory
			.getString("in_iskAdd_skill_tooltip")), BorderLayout.SOUTH);

		JButton columnButton = new JButton();
		scrollPane.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER,
			columnButton);
		columnButton.setText("^");
		new TableColumnManager(availableTable, columnButton, availableModel);

		// Right Pane - Selected
		rightPane.setLayout(new BorderLayout());

		rightPane.add(InfoTabUtils.createFilterPane(selLabel,
			viewSelectComboBox, lblSelectedQFilter, textSelectedQFilter,
			clearSelectedQFilterButton), BorderLayout.NORTH);

		scrollPane =
				new JScrollPane(selectedTable,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		rightPane.add(scrollPane, BorderLayout.CENTER);

		removeButton = new JButton(IconUtilitities.getImageIcon("Back16.gif"));
		rightPane.add(buildDelPanel(removeButton, PropertyFactory
			.getString("in_iskRemove_skill_tooltip")), BorderLayout.SOUTH);

		JButton columnButton2 = new JButton();
		scrollPane.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER,
			columnButton2);
		columnButton2.setText("^");
		new TableColumnManager(selectedTable, columnButton2, selectedModel);

		// set the alignment on these columns to center
		// might as well set the prefered width while we're at it
		//		availableTable.setColAlign(COL_MOD, SwingConstants.CENTER);
		//		availableTable.getColumnModel().getColumn(COL_MOD).setPreferredWidth(15);
		//		selectedTable.getColumnModel().getColumn(COL_NAME).setPreferredWidth(60);
		//		selectedTable.setColAlign(COL_MOD, SwingConstants.CENTER);
		//		selectedTable.getColumnModel().getColumn(COL_MOD).setPreferredWidth(15);
		//		selectedTable.setColAlign(COL_RANK, SwingConstants.CENTER);
		//		selectedTable.getColumnModel().getColumn(COL_RANK).setPreferredWidth(15);
		//		selectedTable.setColAlign(COL_TOTAL, SwingConstants.CENTER);
		//		selectedTable.getColumnModel().getColumn(COL_TOTAL).setPreferredWidth(15);
		//		selectedTable.setColAlign(COL_COST, SwingConstants.CENTER);
		//		selectedTable.getColumnModel().getColumn(COL_COST).setPreferredWidth(15);
		//		selectedTable.getColumnModel().getColumn(COL_SRC).setCellRenderer(new OutputOrderRenderer());
		//		selectedTable.getColumnModel().getColumn(COL_SRC).setPreferredWidth(15);

		TitledBorder title1 =
				BorderFactory.createTitledBorder(PropertyFactory
					.getString("in_iskSkill_Info")); //$NON-NLS-1$
		title1.setTitleJustification(TitledBorder.CENTER);
		cScroll.setBorder(title1);
		infoLabel.setBackground(rightPane.getBackground());
		cScroll.setViewportView(infoLabel);

		//CoreUtility.setDescription(cScroll, "Any requirements you don't meet are in italics.");  //no pre-reqs to show, wo not sure that to do in this tooltip
		jPanel1.setLayout(new GridBagLayout());

		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();

		jLbMaxSkill.setText(PropertyFactory
			.getString("in_iskMax_Class_Skill_Rank")); //$NON-NLS-1$
		Utility.setDescription(jLbMaxSkill, PropertyFactory
			.getString("in_iskMax_Class_Skill_Rank_tooltip")); //$NON-NLS-1$
		jLbMaxSkill.setForeground(Color.black);
		Utility.buildConstraints(gridBagConstraints2, 0, 0, 1, 1, 5, 5);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(jLbMaxSkill, gridBagConstraints2);

		//maxSkillRank.setText(String.valueOf(PlayerCharacter.maxClassSkillForLevel(pc.getTotalLevels() + pc.totalHitDice(), pc)));
		maxSkillRank.setForeground(Color.black);
		Utility.buildConstraints(gridBagConstraints2, 1, 0, 1, 1, 5, 5);
		gridBagConstraints2.anchor = GridBagConstraints.WEST;
		jPanel1.add(maxSkillRank, gridBagConstraints2);

		jLbMaxCrossSkill.setText(PropertyFactory
			.getString("in_iskMax_Cross-Class_Skill_Rank")); //$NON-NLS-1$
		Utility.setDescription(jLbMaxCrossSkill, PropertyFactory
			.getString("in_iskMax_Cross-Class_Skill_Rank_tooltip")); //$NON-NLS-1$
		jLbMaxCrossSkill.setForeground(Color.black);
		Utility.buildConstraints(gridBagConstraints2, 0, 1, 1, 1, 5, 5);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(jLbMaxCrossSkill, gridBagConstraints2);

		//maxCrossSkillRank.setText(PlayerCharacter.maxCrossClassSkillForLevel(pc.getTotalLevels() + pc.totalHitDice(),pc).toString());
		maxCrossSkillRank.setForeground(Color.black);
		Utility.buildConstraints(gridBagConstraints2, 1, 1, 1, 1, 5, 5);
		gridBagConstraints2.anchor = GridBagConstraints.WEST;
		jPanel1.add(maxCrossSkillRank, gridBagConstraints2);

		Utility.buildConstraints(gridBagConstraints2, 2, 1, 1, 1, 5, 5);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(new JLabel(PropertyFactory
			.getString("in_iskSkill_output_order")), gridBagConstraints2); //$NON-NLS-1$

		Utility.buildConstraints(gridBagConstraints2, 3, 1, 1, 1, 5, 5);
		gridBagConstraints2.anchor = GridBagConstraints.WEST;
		jPanel1.add(outputOrderComboBox, gridBagConstraints2);

		if (!Globals.getGameModeHasPointPool())
		{
			jLbClassSkillPoints = new JLabel();
			currCharacterClass = new JComboBoxEx();
			currCharClassSkillPnts = new WholeNumberField(0, 4);

			jLbClassSkillPoints.setText(PropertyFactory
				.getString("in_iskSkill_Points_Left_for_Class")); //$NON-NLS-1$
			jLbClassSkillPoints.setForeground(Color.black);
			Utility.buildConstraints(gridBagConstraints2, 0, 3, 2, 1, 5, 5);
			gridBagConstraints2.anchor = GridBagConstraints.EAST;
			jPanel1.add(jLbClassSkillPoints, gridBagConstraints2);

			//updateClassSelection();

			currCharacterClass.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					currCharacterClassActionPerformed();
				}
			});

			Utility.buildConstraints(gridBagConstraints2, 2, 3, 1, 1, 5, 5);
			gridBagConstraints2.anchor = GridBagConstraints.CENTER;

			final int oldFill = gridBagConstraints2.fill;
			gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
			jPanel1.add(currCharacterClass, gridBagConstraints2);
			gridBagConstraints2.fill = oldFill;
		}

		if (Globals.getGameModeHasPointPool())
		{
			jLbTotalSkillPointsLeft.setText(Globals.getGameModePointPoolName()
				+ ": ");
		}
		else
		{
			jLbTotalSkillPointsLeft.setText(PropertyFactory
				.getString("in_iskTotal_Skill_Points_Left")); //$NON-NLS-1$
		}
		jLbTotalSkillPointsLeft.setForeground(Color.black);
		Utility.buildConstraints(gridBagConstraints2, 2, 2, 1, 1, 5, 5);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(jLbTotalSkillPointsLeft, gridBagConstraints2);

		if (Globals.getGameModeHasPointPool())
		{
			totalSkillPointsLeft.setEditable(false);
			// SwingConstants.RIGHT is equivalent to JTextField.RIGHT but more
			// 'correct' in a Java coding context (it is a static reference)
			totalSkillPointsLeft.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		else
		{
			totalSkillPointsLeft.setInputVerifier(new InputVerifier()
			{
				public boolean shouldYieldFocus(JComponent input)
				{
					boolean valueOk = verify(input);
					totalSkillPointsLeftChanged();
					return valueOk;
				}

				public boolean verify(JComponent input)
				{
					return true;
				}
			});
		}
		Utility.buildConstraints(gridBagConstraints2, 3, 2, 1, 1, 5, 5);
		gridBagConstraints2.anchor = GridBagConstraints.WEST;
		totalSkillPointsLeft.setMinimumSize(new Dimension(40, 17));
		totalSkillPointsLeft.setPreferredSize(new Dimension(40, 17));
		jPanel1.add(totalSkillPointsLeft, gridBagConstraints2);

		if (currCharClassSkillPnts != null)
		{
			currCharClassSkillPnts.setInputVerifier(new InputVerifier()
			{
				public boolean shouldYieldFocus(JComponent input)
				{
					boolean valueOk = verify(input);
					currCharClassSkillPntsChanged();
					return valueOk;
				}

				public boolean verify(JComponent input)
				{
					return true;
				}
			});

			Utility.buildConstraints(gridBagConstraints2, 3, 3, 1, 1, 5, 5);
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
			currCharClassSkillPnts.setPreferredSize(new Dimension(40, 20));
			currCharClassSkillPnts.setMinimumSize(new Dimension(40, 20));
			jPanel1.add(currCharClassSkillPnts, gridBagConstraints2);
		}

		includeLabel =
				new JLabel(PropertyFactory.getString("in_iskInclude_Skills")); //$NON-NLS-1$
		Utility.buildConstraints(gridBagConstraints2, 2, 0, 1, 1, 5, 5);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(includeLabel, gridBagConstraints2);

		skillChoice.setModel(new DefaultComboBoxModel(new String[]{
			PropertyFactory.getString("in_iskNone"),
			PropertyFactory.getString("in_iskUntrained"),
			PropertyFactory.getString("in_iskAll")})); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		skillChoice.setMaximumRowCount(3);
		Utility.setDescription(skillChoice, PropertyFactory
			.getString("in_iskDisplayed_skills_tooltip")); //$NON-NLS-1$
		skillChoice.setMinimumSize(new Dimension(98, 22));
		skillChoice.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				skillChoiceActionPerformed();
			}
		});

		boolean oldFlag = resetSelectedModel;
		resetSelectedModel = true;
		skillChoice.setSelectedIndex(SettingsHandler
			.getSkillsTab_IncludeSkills());
		resetSelectedModel = oldFlag;
		Utility.buildConstraints(gridBagConstraints2, 3, 0, 1, 1, 5, 5);
		gridBagConstraints2.anchor = GridBagConstraints.WEST;
		jPanel1.add(skillChoice, gridBagConstraints2);

		//		exclusiveLabel = new JLabel(PropertyFactory.getString("in_iskExclusive_skill_cost")); //$NON-NLS-1$
		exclusiveLabel =
				new JLabel("Class:"
					+ Integer.toString(SkillCost.CLASS.getCost())
					+ " Cross:"
					+ Integer.toString(SkillCost.CROSS_CLASS.getCost())
					+ " Exclusive:"
					+ Integer.toString(SkillCost.EXCLUSIVE.getCost()));
		PropertyFactory.getString("in_iskExclusive_skill_cost"); //$NON-NLS-1$
		Utility.setDescription(exclusiveLabel, PropertyFactory
			.getString("in_iskExclusive_skill_cost_tooltip")); //$NON-NLS-1$
		Utility.buildConstraints(gridBagConstraints2, 0, 2, 1, 1, 5, 5);
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		jPanel1.add(exclusiveLabel, gridBagConstraints2);

		//		exclusiveSkillCost.setColumns(3);
		//		exclusiveSkillCost.setText("0"); //$NON-NLS-1$
		//		exclusiveSkillCost.setMinimumSize(new Dimension(40, 17));
		//		exclusiveSkillCost.setText(Integer.toString(SettingsHandler.getExcSkillCost()));
		//		exclusiveSkillCost.setText(Integer.toString(Globals.getGameModeSkillCost_Exclusive()));
		//		exclusiveSkillCost.addActionListener(new ActionListener()
		//			{
		//				public void actionPerformed(ActionEvent evt)
		//				{
		//					updateSkillCost();
		//				}
		//			});
		//		exclusiveSkillCost.addFocusListener(new FocusAdapter()
		//			{
		//				public void focusLost(FocusEvent evt)
		//				{
		//					excCostFocusEvent();
		//				}
		//			});
		//		Utility.buildConstraints(gridBagConstraints2, 1, 2, 1, 1, 5, 5);
		//		gridBagConstraints2.anchor = GridBagConstraints.WEST;
		//		jPanel1.add(exclusiveSkillCost, gridBagConstraints2);

		asplit =
				new FlippingSplitPane(JSplitPane.HORIZONTAL_SPLIT, cScroll,
					jPanel1);
		asplit.setOneTouchExpandable(true);
		asplit.setDividerSize(10);

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
	}

	private boolean modRank(Skill aSkill, double points,
		final boolean showSkillMsg)
	{
		if (CoreUtility.doublesEqual(points, 0.0))
		{
			return false;
		}

		PCLevelInfo pcl = getSelectedLevelInfo(pc);
		if (pcl == null)
		{
			return false;
		}

		int skillPool;
		if (Globals.getGameModeHasPointPool())
		{
			skillPool = pc.getSkillPoints();
		}
		else
		{
			int ix = -1;
			if (currCharacterClass != null)
			{
				ix = currCharacterClass.getSelectedIndex();
			}

			if (points > 0)
			{
				updateClassSelection();
			}

			if (currCharacterClass != null)
			{
				if (ix != currCharacterClass.getSelectedIndex())
				{
					ShowMessageDelegate.showMessageDialog(PropertyFactory
						.getFormattedString("in_iskErr_message_03a",
						pcl.getClassKeyName(),
						String.valueOf(pcl.getClassLevel())),
						Constants.APPLICATION_NAME, MessageType.INFORMATION); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

					return false;
				}
			}
			skillPool = pcl.getSkillPointsRemaining();

			if ((points < 0.0)
				&& ((skillPool - points) > pcl.getSkillPointsGained(pc)))
			{
				ShowMessageDelegate.showMessageDialog(PropertyFactory.getFormattedString("in_iskErr_message_05a",
					pcl.getClassKeyName(),
					String.valueOf(pcl.getClassLevel()),
					String.valueOf(pcl.getSkillPointsGained(pc))),
					Constants.APPLICATION_NAME, MessageType.INFORMATION); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

				return false;
			}
		}

		if ((points > 0.0) && (points > skillPool))
		{
			ShowMessageDelegate.showMessageDialog(PropertyFactory
				.getFormattedString("in_iskErr_message_04a",
				String.valueOf(skillPool)),
				Constants.APPLICATION_NAME, MessageType.INFORMATION); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

			return false;
		}

		// the old Skills tab used cost as a double,
		// so I'll duplicate that behavior
		PCClass aClass = getSelectedPCClass();
		SkillCost sc = pc.getSkillCostForClass(aSkill, aClass);

		final double cost = sc.getCost();
		if (cost <= 0.001)
		{
			ShowMessageDelegate
				.showMessageDialog(
					PropertyFactory.getString("in_iskErr_message_06"), Constants.APPLICATION_NAME, MessageType.INFORMATION); //$NON-NLS-1$ //$NON-NLS-2$

			return false;
		}

		double rank = points / cost;

		if (aSkill != null)
		{
			//bSkill.activateBonuses();
			pc.addSkill(aSkill);

			// in order to get the selected table to sort properly
			// we need to sort the PC's skill list now that the
			// new skill has been added, this won't get called
			// when adding a rank to an existing skill
			// NB: This does get called on a rank change, should it be fixed?
//			Collections.sort(pc.getSkillList(),
//				new StringIgnoreCaseComparator());

			// Now re calc the output order
			if (selectedOutputOrder != GuiConstants.INFOSKILLS_OUTPUT_BY_MANUAL)
			{
				resortSelected(selectedOutputOrder);
			}
			else
			{
				Integer outputIndex = pc.getAssoc(aSkill, AssociationKey.OUTPUT_INDEX);
				if (outputIndex == null || outputIndex == 0)
				{
					pc.setAssoc(aSkill, AssociationKey.OUTPUT_INDEX, getHighestOutputIndex() + 1);
				}
			}
		}

		String aString = ""; //$NON-NLS-1$

		if (aSkill != null)
		{
			aString = SkillRankControl.modRanks(rank, aClass, false, pc, aSkill);

			if ("".equals(aString)) //$NON-NLS-1$
			{
				if (currCharacterClass == null)
				{
					updatePcl((int) points);
				}
				else
				{
					pcl.setSkillPointsRemaining(skillPool - (int) points);
				}

				//bSkill.activateBonuses();
			}

			updateClassSelection();

			//
			// Remove the skill from the skill list if we've
			// just set the rank to zero and it is not untrained
			//
			if (CoreUtility.doublesEqual(SkillRankControl.getRank(pc, aSkill).doubleValue(), 0.0)
				&& !aSkill.getSafe(ObjectKey.USE_UNTRAINED))
			{
				pc.removeSkill(aSkill);
			}
		}

		if (aString.length() > 0)
		{
			if (showSkillMsg)
			{
				ShowMessageDelegate.showMessageDialog(aString,
					Constants.APPLICATION_NAME, MessageType.INFORMATION); //$NON-NLS-1$
			}

			return false;
		}

		return true;
	}

	/**
	 * Process a change in the selected output order. Will sort the skills in
	 * the requested order and save the setting in the user's preferences.
	 */
	private void outputOrderComboBoxActionPerformed()
	{
		final int index = outputOrderComboBox.getSelectedIndex();

		if (index != selectedOutputOrder)
		{
			selectedOutputOrder = index;
			resortSelected(selectedOutputOrder);

			if (pc != null)
			{
				pc.setDirty(true);
				pc.setSkillsOutputOrder(selectedOutputOrder);
			}
		}
	}

	private void resortSelected(int sortSelection)
	{
		int sort = -1;
		boolean sortOrder = false;

		switch (sortSelection)
		{
			case GuiConstants.INFOSKILLS_OUTPUT_BY_NAME_ASC:
				sort = SkillComparator.RESORT_NAME;
				sortOrder = SkillComparator.RESORT_ASCENDING;

				break;

			case GuiConstants.INFOSKILLS_OUTPUT_BY_NAME_DSC:
				sort = SkillComparator.RESORT_NAME;
				sortOrder = SkillComparator.RESORT_DESCENDING;

				break;

			case GuiConstants.INFOSKILLS_OUTPUT_BY_TRAINED_ASC:
				sort = SkillComparator.RESORT_TRAINED;
				sortOrder = SkillComparator.RESORT_ASCENDING;

				break;

			case GuiConstants.INFOSKILLS_OUTPUT_BY_TRAINED_DSC:
				sort = SkillComparator.RESORT_TRAINED;
				sortOrder = SkillComparator.RESORT_DESCENDING;

				break;

			default:

				// Manual sort, or unrecognised, so do no sorting.
				updateSelectedModel();

				return;
		}

		resortSelected(sort, sortOrder);
	}

	private void resortSelected(int sort, boolean sortOrder)
	{
		if (pc == null)
		{
			return;
		}
		SkillComparator comparator = new SkillComparator(pc, sort, sortOrder);
		int nextOutputIndex = 1;
		List<Skill> skillList = new ArrayList<Skill>(pc.getSkillSet());
		Collections.sort(skillList, comparator);

		for (Skill aSkill : skillList)
		{
			Integer outputIndex = pc.getAssoc(aSkill, AssociationKey.OUTPUT_INDEX);
			if (outputIndex == null || outputIndex >= 0)
			{
				pc.setAssoc(aSkill, AssociationKey.OUTPUT_INDEX, nextOutputIndex++);
			}
		}

		if (selectedTable != null)
		{
			updateSelectedModel();
		}
		//pc.setDirty(true);
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
			SettingsHandler.setPCGenOption("InfoSkills.splitPane", s); //$NON-NLS-1$
		}

		s = asplit.getDividerLocation();

		if (s > 0)
		{
			SettingsHandler.setPCGenOption("InfoSkills.asplit", s); //$NON-NLS-1$
		}

		s = bsplit.getDividerLocation();

		if (s > 0)
		{
			SettingsHandler.setPCGenOption("InfoSkills.bsplit", s); //$NON-NLS-1$
		}
	}

	private void skillChoiceActionPerformed()
	{
		final int selection = skillChoice.getSelectedIndex();
		final int oldSelection = SettingsHandler.getSkillsTab_IncludeSkills();

		if ((selection >= 0) && (selection <= 2)
			&& ((selection != oldSelection) || resetSelectedModel))
		{
			SettingsHandler.setSkillsTab_IncludeSkills(selection);
			pc.populateSkills(selection);
			updateSelectedModel();
		}
	}

	/**
	 * Action a user request to change the total number of skill points 
	 * remaining. 
	 */
	private void totalSkillPointsLeftChanged()
	{
		final PlayerCharacter currentPC = pc;
		currentPC.setDirty(true);

		if (totalSkillPointsLeft.getText().length() > 0)
		{
			final int anInt =
					Delta.decode(totalSkillPointsLeft.getText()).intValue();

			if (anInt == currentPC.getSkillPoints())
			{
				return;
			}

			currentPC.setDirty(true);

			final int x = currentPC.getClassCount();
			if (x == 0)
			{
				return;
			}
			final int y = anInt / x;

			for (PCClass aClass : currentPC.getClassSet())
			{
				currentPC.setAssoc(aClass, AssociationKey.SKILL_POOL, Math.max(0, y));
			}

			PCLevelInfo pcl = getSelectedLevelInfo(currentPC);
			int skillPool = pcl.getSkillPointsRemaining();
			PCClass aClass = getSelectedPCClass();

			if (aClass != null)
			{
				if (currCharClassSkillPnts != null)
				{
					currCharClassSkillPnts.setValue(skillPool);
				}
			}
		}

		totalSkillPointsLeft.setValue(currentPC.getSkillPoints());
	}

	//
	// This recalculates everything for the currently selected character
	//
	private final void updateCharacterInfo()
	{
		final PlayerCharacter bPC = pc;

		if ((bPC != null) && needsUpdate)
		{
			selectedOutputOrder = bPC.getSkillsOutputOrder();
			outputOrderComboBox.setSelectedIndex(selectedOutputOrder);
			bPC.refreshSkillList(); // forces refresh of skills

			SettingsHandler.getSkillsTab_IncludeSkills();

			resetSelectedModel = true;
			previouslySelectedClass = null;
			skillChoiceActionPerformed();
			resortSelected(selectedOutputOrder);
		}

		pc = bPC;

		if ((pc == null) || !needsUpdate)
		{
			return;
		}

		updateClassSelection();

		if (Globals.getGameModeHasPointPool())
		{
			////			totalSkillPointsLeft.setValue((int)pc.getTotalBonusTo("SKILLPOOL", "NUMBER"));
			//			totalSkillPointsLeft.setValue(pc.getSkillPoints());
		}
		else
		{
			PCLevelInfo pcl = getSelectedLevelInfo(pc);

			if (pcl != null)
			{
				if (currCharClassSkillPnts != null)
				{
					currCharClassSkillPnts.setValue(pcl
						.getSkillPointsRemaining());
				}
				//				totalSkillPointsLeft.setValue(pc.getSkillPoints());
			}
			else
			{
				if (currCharClassSkillPnts != null)
				{
					currCharClassSkillPnts.setValue(0);
				}
				//				totalSkillPointsLeft.setValue(0);
			}
		}
		totalSkillPointsLeft.setValue(pc.getSkillPoints());

		maxSkillRank.setText(String.valueOf(SkillUtilities
			.maxClassSkillForLevel(pc.getTotalLevels(), pc)));
		maxCrossSkillRank.setText(String.valueOf(SkillUtilities
			.maxCrossClassSkillForLevel(pc.getTotalLevels(), pc)));

		resetSelectedModel = true;
		updateAvailableModel();
		updateSelectedModel();

		needsUpdate = false;
	}

	private final void updateClassSelection()
	{
		if (Globals.getGameModeHasPointPool())
		{
			// TODO Do Nothing?
		}
		else
		{
			resetSelectedModel = false;

			String[] comboStrings = new String[pc.getLevelInfoSize()];

			int i = 0;
			for (PCLevelInfo pcl : pc.getLevelInfo())
			{
				StringBuffer sb = new StringBuffer();
				sb.append(pcl.getClassKeyName()).append('/').append(
					pcl.getClassLevel()).append(' ').append('[').append(
					pcl.getSkillPointsRemaining()).append('/').append(
					pcl.getSkillPointsGained(pc)).append(']');
				comboStrings[i] = sb.toString();
				i++;
			}
			if (currCharacterClass != null)
			{
				ActionListener[] listeners =
						currCharacterClass.getActionListeners();
				for (int j = 0; j < listeners.length; ++j)
				{
					currCharacterClass.removeActionListener(listeners[j]);
				}
				currCharacterClass.removeAllItems();
				currCharacterClass.setAllItems(comboStrings);

				resetSelectedModel = true;

				if (currCharacterClass.getItemCount() > 0)
				{
					setCurrentClassCombo();
				}
				for (int j = 0; j < listeners.length; ++j)
				{
					currCharacterClass.addActionListener(listeners[j]);
				}
			}
		}
	}

	//	private final void updateSkillCost()
	//	{
	//		try
	//		{
	//			SettingsHandler.setExcSkillCost(Integer.parseInt(exclusiveSkillCost.getText()));
	//		}
	//		catch (NumberFormatException nfe)
	//		{
	//			exclusiveSkillCost.setText(Integer.toString(SettingsHandler.getExcSkillCost()));
	//		}
	//	}

	private void setInfoLabelText(Skill aSkill)
	{
		lastSkill = aSkill; //even if that's null

		if (aSkill != null)
		{
			StringBuffer b = new StringBuffer();
			b.append(HTML).append(FONT_PLUS_1).append(BOLD)
				.append(OutputNameFormatting.piString(aSkill, false))
				.append(END_BOLD).append(END_FONT).append(BR);
			if (!Globals.checkRule(RuleConstants.SKILLMAX))
			{
				b.append(PropertyFactory.getString("in_iskHtml_MAXRANK"))
					.append(pc.getMaxRank(aSkill, getSelectedPCClass()).doubleValue()); //$NON-NLS-1$
				b.append(THREE_SPACES); 
			}
			b.append(PropertyFactory.getString("in_iskHtml_TYPE"))
				.append(StringUtil.join(aSkill.getTrueTypeList(true), ". ")); //$NON-NLS-1$

			String aString = SkillInfoUtilities.getKeyStatFromStats(pc, aSkill);
			if (aString.length() != 0)
			{
				b.append(PropertyFactory.getString("in_iskHtml_KEY_STAT")).append(aString); //$NON-NLS-1$
			}
			b.append(PropertyFactory.getString("in_iskHtml_UNTRAINED")) //$NON-NLS-1$
				.append(aSkill.getSafe(ObjectKey.USE_UNTRAINED) ? PropertyFactory.getString("in_yes") : PropertyFactory.getString("in_no")); 
			b.append(PropertyFactory.getString("in_iskHtml_EXCLUSIVE")) //$NON-NLS-1$
				.append(aSkill.getSafe(ObjectKey.EXCLUSIVE) ? PropertyFactory.getString("in_yes") : PropertyFactory.getString("in_no")); 

			String bString = PrerequisiteUtilities.preReqHTMLStringsForList(pc, null,
			aSkill.getPrerequisiteList(), false);

			if (bString.length() > 0)
			{
				b.append(PropertyFactory.getFormattedString(
					"in_InfoRequirements", //$NON-NLS-1$
					bString));
			}

			bString = SourceFormat.getFormattedString(aSkill,
			Globals.getSourceDisplay(), true);
			if (bString.length() > 0)
			{
				b.append(PropertyFactory.getString("in_iskHtml_SOURCE")).append(bString); //$NON-NLS-1$
			}

			if (SettingsHandler.getShowSkillModifier())
			{
				bString = SkillModifier.getModifierExplanation(aSkill, pc, false);
				if (bString.length() != 0)
				{
					b.append(PropertyFactory.getFormattedString(
						"in_iskHtml_PcMod", //$NON-NLS-1$
						bString));
				}
			}

			if (SettingsHandler.getShowSkillRanks())
			{
				bString = SkillRankControl.getRanksExplanation(pc, aSkill);
				if (bString.length() != 0)
				{
					b.append(PropertyFactory.getFormattedString(
						"in_iskHtml_Ranks", //$NON-NLS-1$
						bString));
				}
			}

			b.append(END_HTML);
			infoLabel.setText(b.toString());
		}
	}

	/**
	 * Updates the Available table
	 */
	private void updateAvailableModel()
	{
		List<String> pathList = availableTable.getExpandedPaths();
		createAvailableModel();
		availableTable.updateUI();
		availableTable.expandPathList(pathList);
	}

	/**
	 * Updates the Selected table
	 */
	private void updateSelectedModel()
	{
		List<String> pathList = selectedTable.getExpandedPaths();
		createSelectedModel();
		try
		{
			selectedTable.updateUI();
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		selectedTable.expandPathList(pathList);
	}

	private void viewComboBoxActionPerformed()
	{
		final int index = viewComboBox.getSelectedIndex();

		if (index != viewMode)
		{
			viewMode = index;
			SettingsHandler.setSkillsTab_AvailableListMode(viewMode);
			updateAvailableModel();
		}
	}

	private void viewSelectComboBoxActionPerformed()
	{
		final int index = viewSelectComboBox.getSelectedIndex();

		if (index != viewSelectMode)
		{
			viewSelectMode = index;
			SettingsHandler.setSkillsTab_SelectedListMode(viewSelectMode);
			updateSelectedModel();
		}
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
		viewMode = GuiConstants.INFOSKILLS_VIEW_NAME;
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
		viewSelectMode = GuiConstants.INFOSKILLS_VIEW_NAME;
		selectedModel.resetModel(viewMode, false);
		clearSelectedQFilterButton.setEnabled(true);
		viewSelectComboBox.setEnabled(false);
		forceRefresh();
	}

	/**
	 * a wrapper for Skill, mods and ranks
	 */
	public static final class SkillWrapper
	{
		private Float _ranks;
		private Integer _mod;
		private Integer _outputIndex;
		private Skill _aSkill = null;
		private boolean _bPassesPreReqs;

		private SkillWrapper(Skill aSkill, Integer mod, Float ranks,
			Integer outputIndex, boolean bPassesPreReqs)
		{
			_aSkill = aSkill;
			_mod = mod;
			_ranks = ranks;
			_outputIndex = outputIndex;
			_bPassesPreReqs = bPassesPreReqs;
		}

		@Override
		public String toString()
		{
			if (_aSkill == null)
			{
				return ""; //$NON-NLS-1$
			}

			if (!_bPassesPreReqs)
			{
				// indicates to LabelTreeCellRenderer to change text color
				// to a user-preference (default is red)
				Color aColor = Color.red;

				if (SettingsHandler.getPrereqFailColor() != 0)
				{
					aColor =
							new Color(SettingsHandler.getPrereqFailColor());
				}

				return "|" + aColor.getRGB() + "|" + OutputNameFormatting.piString(_aSkill, true);
			}

			return OutputNameFormatting.piString(_aSkill, true);
		}

		protected Integer getSkWrapMod()
		{
			return _mod;
		}

		protected Integer getSkWrapOutputIndex()
		{
			return _outputIndex;
		}

		protected Float getSkWrapRank()
		{
			return _ranks;
		}

		protected Skill getSkWrapSkill()
		{
			return _aSkill;
		}
	}

	/**
	 * OutputOrderEditor is a JCombobox based table cell editor. It allows the user
	 * to either enter their own output order index, or to select from hidden, first
	 * or last. If first or last are selected, then special values are returned to
	 * the setValueAt method, which are actioned by that method.
	 */
	private static final class OutputOrderEditor extends JComboBoxEx implements
			TableCellEditor
	{
		private final transient List<CellEditorListener> d_listeners =
				new ArrayList<CellEditorListener>();
		private transient int d_originalValue;

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
					setSelectedItem(PropertyFactory.getString("in_iskHidden")); //$NON-NLS-1$
				}
				else
				{
					setSelectedItem(String.valueOf(i));
				}
			}
			else
			{
				setSelectedItem(PropertyFactory.getString("in_iskHidden")); //$NON-NLS-1$
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

	/**
	 * The basic idea of the TreeTableModel is that there is a
	 * single <code>root</code> object.  This root object has a
	 * null <code>parent</code>.  All other objects have a parent
	 * which points to a non-null object.  parent objects contain
	 * a list of <code>children</code>, which are all the objects
	 * that point to it as their parent.  objects (or
	 * <code>nodes</code>) which have 0 children are leafs (the
	 * end of that linked list).  nodes which have at least 1
	 * child are not leafs. Leafs are like files and non-leafs
	 * are like directories.
	 * <p/>
	 * TODO: This class implements the java.util.Iterator interface.? However, its next() method is not capable of throwing java.util.NoSuchElementException.? The next() method should be changed so it throws NoSuchElementException if is called when there are no more elements to return.
	 */
	private final class SkillModel extends AbstractTreeTableModel implements
			TableColumnManagerModel
	{

		private static final int COL_NAME = 0;
		private static final int COL_TOTAL = 1;
		private static final int COL_MOD = 2;
		private static final int COL_RANK = 3;
		private static final int COL_COST = 4;
		private static final int COL_CLASS = 5;
		private static final int COL_SRC = 6;
		private static final int COL_INDEX = 7;

		private String[] names =
				{"Skill", "Total", "Modifier", "Ranks", "Cost", "Class",
				    "Source", "Order", "+", "-"};
		private int[] widths = {100, 100, 100, 100, 100, 100, 100, 100, 30, 30};

		private List<Boolean> displayList;

		// Types of the columns.
		private int modelType = MODEL_AVAIL;

		/**
		 * Creates a SkillModel
		 * @param mode
		 * @param available
		 */
		private SkillModel(int mode, boolean available)
		{
			super(null);

			if (!available)
			{
				modelType = MODEL_SELECT;
			}

			resetModel(mode, available);
			int i = 1;
			displayList = new ArrayList<Boolean>();
			displayList.add(Boolean.TRUE); // Skill
			if (available)
			{
				displayList.add(Boolean.valueOf(getColumnViewOption(modelType
					+ "." + names[i++], false))); // Total
				displayList.add(Boolean.valueOf(getColumnViewOption(modelType
					+ "." + names[i++], false))); // Modifier
				displayList.add(Boolean.valueOf(getColumnViewOption(modelType
					+ "." + names[i++], false))); // Rank
				displayList.add(Boolean.valueOf(getColumnViewOption(modelType
					+ "." + names[i++], true))); // Cost
				displayList.add(Boolean.valueOf(getColumnViewOption(modelType
					+ "." + names[i++], true))); // Class
				displayList.add(Boolean.valueOf(getColumnViewOption(modelType
					+ "." + names[i++], true))); // Source
				displayList.add(Boolean.valueOf(getColumnViewOption(modelType
					+ "." + names[i++], false))); // Order
				displayList.add(Boolean.valueOf(getColumnViewOption(modelType
					+ "." + names[i++], true))); // Inc
				displayList.add(Boolean.valueOf(getColumnViewOption(modelType
					+ "." + names[i++], true))); // Dec
			}
			else
			{
				displayList.add(Boolean.valueOf(getColumnViewOption(modelType
					+ "." + names[i++], true))); // Total
				displayList.add(Boolean.valueOf(getColumnViewOption(modelType
					+ "." + names[i++], true))); // Modifier
				displayList.add(Boolean.valueOf(getColumnViewOption(modelType
					+ "." + names[i++], true))); // Rank
				displayList.add(Boolean.valueOf(getColumnViewOption(modelType
					+ "." + names[i++], true))); // Cost
				displayList.add(Boolean.valueOf(getColumnViewOption(modelType
					+ "." + names[i++], true))); // Class
				displayList.add(Boolean.valueOf(getColumnViewOption(modelType
					+ "." + names[i++], false))); // Source
				displayList.add(Boolean.valueOf(getColumnViewOption(modelType
					+ "." + names[i++], true))); // Order
				displayList.add(Boolean.valueOf(getColumnViewOption(modelType
					+ "." + names[i++], true))); // Inc
				displayList.add(Boolean.valueOf(getColumnViewOption(modelType
					+ "." + names[i++], true))); // Dec
			}
		}

		/**
		 * Evaluate if the cell is editable.
		 * true for first column so that it highlights
		 * true for the output index column of the selected table if this is
		 * a skill row
		 * @see pcgen.gui.utils.TreeTableModel#isCellEditable(Object, int)
		 */
		public boolean isCellEditable(Object node, int column)
		{
			return ((column == 0) || ((modelType == MODEL_SELECT)
				&& (column == 5) && (((PObjectNode) node).getItem() instanceof SkillWrapper)));
		}

		/**
		 * Returns Skill for the column.
		 * @param column
		 * @return Class
		 */
		public Class<?> getColumnClass(int column)
		{
			GameMode gm;
			switch (column)
			{
				case COL_NAME: //skill name
					return TreeTableModel.class;

				case COL_MOD: //skill modifier
					return Integer.class;

				case COL_RANK: //skill ranks
					gm = SettingsHandler.getGame();
					if (gm.hasSkillRankDisplayText())
					{
						return String.class;
					}
					return Float.class;

				case COL_TOTAL: //total skill
					gm = SettingsHandler.getGame();
					if (gm.hasSkillRankDisplayText())
					{
						return String.class;
					}
					return Integer.class;

				case COL_COST: //skill rank cost
					return Integer.class;

				case COL_CLASS: //class skill
					return String.class;

				case COL_INDEX: //display index
					return Integer.class;

				case COL_INC: //increment
				case COL_DEC: //decrement
					return String.class;

				case COL_SRC:
					break;

				default:
					Logging.errorPrintLocalised("in_iskErr_message_08",
						"InfoSkills.SkillModel.getColumnClass",
						String.valueOf(column)); //$NON-NLS-1$ //$NON-NLS-2$

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
		 * Sets the new table cell value. Currently this only deals with the
		 * output index column. Here is deals with the possible special values
		 * that could be entered. This method takes 1000 as last, and sets the
		 * value to 1 more than the maximum index currently in use. It also
		 * takes 0 as first, setting the value to 1, and shuffling up all the
		 * other indexes to be after this new one.
		 * @param aValue
		 * @param node
		 * @param column
		 */
		public void setValueAt(Object aValue, Object node, int column)
		{
			boolean needRefresh = false;

			if (modelType != MODEL_SELECT)
			{
				return; // can only set values for selectedTableModel
			}

			if (!(((PObjectNode) node).getItem() instanceof SkillWrapper))
			{
				return; // can only use rows with Skills in them
			}

			final PObjectNode fn = (PObjectNode) node;
			SkillWrapper skillA = (SkillWrapper) fn.getItem();
			Skill aSkill = skillA.getSkWrapSkill();

			if (aSkill != null)
			{
				Integer outi = pc.getAssoc(aSkill, AssociationKey.OUTPUT_INDEX);
				if (outi == null)
				{
					outi = Integer.valueOf(0);
				}
				switch (column)
				{
					case 5:

						int outputIndex = ((Integer) aValue).intValue();

						if (outputIndex == 1000) // Last
						{
							// Set it to one higher that the highest output index so far
							outputIndex = getHighestOutputIndex() + 1;
						}
						else if (outputIndex == 0) // First
						{
							// Set it to 1 and shuffle everyone up in order
							needRefresh = true;
							outputIndex = 2;

							for (Skill bSkill : pc.getSkillListInOutputOrder())
							{
								Integer oi = pc.getAssoc(bSkill, AssociationKey.OUTPUT_INDEX);
								if ((oi == null || oi > -1) && (bSkill != aSkill))
								{
									pc.setAssoc(bSkill, AssociationKey.OUTPUT_INDEX, outputIndex++);
								}
							}

							outputIndex = 1;
						}
						else if (outputIndex != -1) // A specific value
						{
							int workingIndex = 1;

							// Reorder everything so that we have a proper sequence - its the only way to be sure
							needRefresh = true;

							for (Skill bSkill : pc.getSkillListInOutputOrder())
							{
								if (workingIndex == outputIndex)
								{
									workingIndex++;
								}

								Integer oi = pc.getAssoc(bSkill, AssociationKey.OUTPUT_INDEX);
								if ((oi == null || oi > -1) && (bSkill != aSkill))
								{
									pc.setAssoc(bSkill, AssociationKey.OUTPUT_INDEX, workingIndex++);
								}
							}
						}

						pc.setAssoc(aSkill, AssociationKey.OUTPUT_INDEX, outputIndex);
						skillA =
								new SkillWrapper(aSkill, SkillModifier.modifier(aSkill, pc),
									SkillRankControl.getTotalRank(pc, aSkill), 
									outi,
									aSkill.qualifies(pc, aSkill));
						fn.setItem(skillA);

						if (needRefresh)
						{
							updateSelectedModel();
						}

						break;

					default:
						Logging.errorPrintLocalised("in_iskErr_message_08",
							"InfoSkills.SkillModel.setValueAt",
							String.valueOf(column)); //$NON-NLS-1$ //$NON-NLS-2$

						break;
				}
			}
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
			Skill aSkill;
			Integer mods;
			Float ranks;
			Integer outputIndex;

			if (fn == null)
			{
				Logging.errorPrint(PropertyFactory
					.getString("in_iskErr_message_09")); //$NON-NLS-1$

				return null;
			}

			if (fn.getItem() instanceof SkillWrapper)
			{
				SkillWrapper skillA = (SkillWrapper) fn.getItem();
				aSkill = skillA.getSkWrapSkill();
				mods = skillA.getSkWrapMod();
				ranks = skillA.getSkWrapRank();
				outputIndex = skillA.getSkWrapOutputIndex();
			}
			else
			{
				// optimize this for non-skill rows
				// roll up rows in the tree shouldn't have numbers in the table
				return (column == COL_NAME) ? fn.toString() : null;
			}

			GameMode gm;

			switch (column)
			{
				case COL_NAME: // Name
					return fn.toString();

				case COL_MOD: // Bonus mods
					return mods;

				case COL_RANK: // number of ranks
					gm = SettingsHandler.getGame();
					if (gm.hasSkillRankDisplayText())
					{
						return gm.getSkillRankDisplayText(ranks.intValue());
					}
					return ranks;

				case COL_TOTAL: // Total skill level
					gm = SettingsHandler.getGame();
					if (gm.hasSkillRankDisplayText())
					{
						return gm.getSkillRankDisplayText(mods.intValue()
							+ ranks.intValue());
					}
					return Integer.valueOf(mods.intValue() + ranks.intValue());

				case COL_COST: // Cost to buy skill points

					if (aSkill != null)
					{
						return Integer.valueOf(pc.getSkillCostForClass(aSkill, getSelectedPCClass()).getCost());
					}

					return "0";

				case COL_CLASS: // class skill

					if (aSkill != null)
					{
						if (pc.isClassSkill(aSkill, getSelectedPCClass()))
						{
							return "yes";
						}
					}

					return "no";

				case COL_SRC: // Source Info

					if (aSkill != null)
					{
						return SourceFormat.getFormattedString(aSkill,
						Globals.getSourceDisplay(), true);
					}
					return fn.getSource();

				case COL_INDEX: // Output index
					return outputIndex;

				case COL_INC:
					return "+"; //$NON-NLS-1$

				case COL_DEC:
					return "-"; //$NON-NLS-1$

				case -1:
					return fn.getItem();

				default:
					Logging.errorPrintLocalised("in_iskErr_message_08",
						"InfoSkills.SkillModel.getValueAt",
						String.valueOf(column)); //$NON-NLS-1$ //$NON-NLS-2$

					break;
			}

			return null;
		}

		// "There can be only one!" There must be a root
		// object, though it can be hidden to make it's
		// existence basically a convenient way to keep track
		// of the objects
		private void setRoot(PObjectNode aNode)
		{
			super.setRoot(aNode);
		}

		/**
		 * The real work.  Since all the node sorting for
		 * skills is now in data structures, the driver is
		 * trivial.
		 *
		 * @param node      the current node to populate
		 * @param skillsIt  a resetable skills iterator
		 * @param sorter    the real work
		 * @param available available or selected tree model?
		 * @see InfoSkillsSorter
		 */
		private void createRootNode(PObjectNode node,
			ResetableListIterator skillsIt, InfoSkillsSorter sorter,
			boolean available)
		{
			populateNode(node, skillsIt, sorter, available);

			if (sorter.nodeHaveNext())
			{
				while (node.hasNext())
				{
					createRootNode((PObjectNode) node.next(), skillsIt, sorter
						.nextSorter(), available);
				}
			}
		}

		private void initRoot(boolean available, InfoSkillsSorter sorter)
		{
			PObjectNode root = new PObjectNode();
			createRootNode(root, new DisplayableSkillsIterator(available),
				sorter, available);
			setRoot(sorter.finalPass(root));
		}

		/**
		 * Conditionally add selected parts of a sequence of
		 * Skills as the children of a PObjectNode.
		 * <p/>
		 * THE BIG IDEA: generalize the populating of sort
		 * order for skills so that new UI and custom sort
		 * orders fall out naturally.  You may then make
		 * subsorts or custom sorts to your heart's content.
		 * Are you happy now?
		 * <p/>
		 * No assumptions about the skills are made, so if you
		 * want to filter them (say with
		 * <code>shouldDisplayThis(skill)</code>) you need to
		 * do so in <code>does</code>.
		 *
		 * @param node      add children here
		 * @param skillsIt  an iterator over the skills
		 * @param sorter    does the new node go here? what part(s)?
		 * @param available availabl or selected tree model?
		 */
		private void populateNode(PObjectNode node,
			ResetableListIterator skillsIt, InfoSkillsSorter sorter,
			boolean available)
		{
			final SortedSet<PObjectNode> set =
					new TreeSet<PObjectNode>(new StringIgnoreCaseComparator());

			String qFilter = this.getQFilter();

			set.clear();
			skillsIt.reset();

			while (skillsIt.hasNext())
			{
				Skill skill = (Skill) skillsIt.next();

				if (qFilter == null
					|| (skill.getDisplayName().toLowerCase().indexOf(qFilter) >= 0 || skill
						.getType().toLowerCase().indexOf(qFilter) >= 0))
				{
					if (!sorter.nodeGoHere(node, skill))
					{
						continue;
					}

					Object part = sorter.whatPart(available, skill, pc);

					if (part instanceof Iterator)
					{
						for (Iterator<?> partIt = (Iterator) part; partIt
							.hasNext();)
						{
							Object anObj = partIt.next();
							if (anObj instanceof Type)
							{
								if (Globals.isSkillTypeHidden(anObj.toString()))
								{
									PObjectNode nameNode = new PObjectNode(createSkillWrapper(available, skill, pc));
									PrereqHandler.passesAll(skill.getPrerequisiteList(), pc,
										skill);
									set.add(nameNode);
									continue;
								}
							}
							set.add(new PObjectNode(anObj));
						}
					}

					else
					{
						if (available
							&& (skill.getSafe(ObjectKey.VISIBILITY) == Visibility.OUTPUT_ONLY))
						{
							continue;
						}

						PObjectNode nameNode = new PObjectNode(part);
						PrereqHandler.passesAll(skill.getPrerequisiteList(), pc,
							skill);
						set.add(nameNode);
					}
				}
			}

			for (PObjectNode n : set)
			{
				node.addChild(n);
			}
		}

		/**
		 * This assumes the SkillModel exists but needs to be repopulated.
		 * @param mode
		 * @param available
		 */
		protected void resetModel(int mode, boolean available)
		{

			switch (mode)
			{
				case GuiConstants.INFOSKILLS_VIEW_STAT_TYPE_NAME: // KeyStat/SubType/Name
					initRoot(available,
						new InfoSkillsSorters.KeystatSubtypeName_Primary(
							InfoSkills.this));

					break;

				case GuiConstants.INFOSKILLS_VIEW_STAT_NAME: // KeyStat/Name
					initRoot(available,
						new InfoSkillsSorters.KeystatName_Primary(
							InfoSkills.this));

					break;

				case GuiConstants.INFOSKILLS_VIEW_TYPE_NAME: // SubType/Name
					initRoot(available,
						new InfoSkillsSorters.SubtypeName_Primary(
							InfoSkills.this));

					break;

				case GuiConstants.INFOSKILLS_VIEW_COST_TYPE_NAME: // Cost/SubType/Name
					initRoot(available,
						new InfoSkillsSorters.CostSubtypeName_Primary(
							InfoSkills.this));

					break;

				case GuiConstants.INFOSKILLS_VIEW_COST_NAME: // Cost/Name
					initRoot(available, new InfoSkillsSorters.CostName_Primary(
						InfoSkills.this));

					break;

				case GuiConstants.INFOSKILLS_VIEW_NAME: // Name
					initRoot(available, new InfoSkillsSorters.Name_Primary(
						InfoSkills.this));

					break;

				default:
					Logging.errorPrintLocalised("in_iskErr_message_07",
						String.valueOf(mode)); //$NON-NLS-1$ //$NON-NLS-2$

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
		 * Return a boolean to indicate if the item should be
		 * included in the list
		 *
		 * @param aSkill
		 * @return true if it should be displayed
		 */
		private boolean shouldDisplayThis(final Skill aSkill)
		{
			return ((modelType == MODEL_SELECT) || accept(pc, aSkill));
		}

		/**
		 * In the availableTable, if filtering out unqualified
		 * items ignore any skill the PC doesn't qualify for
		 *
		 * TODO: This class implements the java.util.Iterator interface
		 * However, its next() method is not capable of throwing
		 * java.util.NoSuchElementException
		 * The next() method should be changed so it throws NoSuchElementException
		 * if is called when there are no more elements to return.
		 */
		private class DisplayableSkillsIterator implements
				ResetableListIterator
		{
			private boolean available;
			private int index;
			private int listSize;

			List<Skill> skillList;

			/**
			 * Constructor
			 * @param argAvailable
			 */
			public DisplayableSkillsIterator(boolean argAvailable)
			{
				available = argAvailable;
				reset();
			}

			public void add(Object obj)
			{
				throw new UnsupportedOperationException();
			}

			public boolean hasNext()
			{
				return (nextIndex() < listSize);
			}

			public boolean hasPrevious()
			{
				return (previousIndex() >= 0);
			}

			public Object next()
			{
				for (;;)
				{
					final Skill peek = skillList.get(index++);
					if (shouldDisplayThis(peek))
					{
						return peek;
					}
				}
			}

			public int nextIndex()
			{
				int idx = index;
				while (idx < listSize)
				{
					final Skill peek = skillList.get(idx);

					if (shouldDisplayThis(peek))
					{
						break;
					}
					++idx;
				}
				return idx;
			}

			public Object previous()
			{
				for (;;)
				{
					final Skill peek = skillList.get(--index);
					if (shouldDisplayThis(peek))
					{
						return peek;
					}
				}
			}

			public int previousIndex()
			{
				int idx = index - 1;
				while (idx >= 0)
				{
					final Skill peek = skillList.get(idx);

					if (shouldDisplayThis(peek))
					{
						break;
					}
					--idx;
				}
				return idx;
			}

			public void remove()
			{
				throw new UnsupportedOperationException();
			}

			public void reset()
			{
				skillList =
						available ? Globals.getObjectsOfVisibility(Globals
								.getContext().ref.getConstructedCDOMObjects(Skill.class),
								Visibility.DISPLAY_ONLY) : pc
							.getPartialSkillList(Visibility.DISPLAY_ONLY);
				listSize = skillList.size();

				index = 0;
				index = nextIndex();
			}

			public void set(Object obj)
			{
				throw new UnsupportedOperationException();
			}
		}

		public int getMColumnDefaultWidth(int col)
		{
			return SettingsHandler.getPCGenOption("InfoSkills.sizecol."
				+ names[col], widths[col]);
		}

		public void setMColumnDisplayed(int col, boolean disp)
		{
			setColumnViewOption(modelType + "." + names[col], disp);
			displayList.set(col, Boolean.valueOf(disp));
		}

		private void setColumnViewOption(String colName, boolean val)
		{
			SettingsHandler
				.setPCGenOption("InfoSkills.viewcol." + colName, val);
		}

		public int getMColumnOffset()
		{
			return 1;
		}

		public boolean isMColumnDisplayed(int col)
		{
			TabInfo ti = Globals.getContext().ref
					.silentlyGetConstructedCDOMObject(TabInfo.class, tab
							.toString());
			return ti.isColumnVisible(col)
					&& (displayList.get(col)).booleanValue();
		}

		public void setMColumnDefaultWidth(int col, int width)
		{
			SettingsHandler.setPCGenOption("InfoSkills.sizecol." + names[col],
				width);
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

		private boolean getColumnViewOption(String colName, boolean defaultVal)
		{
			return SettingsHandler.getPCGenOption("InfoSkills.viewcol."
				+ colName, defaultVal);
		}

		public void resetMColumn(int col, TableColumn column)
		{
			int colNum = column.getModelIndex();
			switch (colNum)
			{
				case COL_MOD:
				case COL_RANK:
				case COL_TOTAL:
				case COL_COST:
				case COL_CLASS:
					//				case COL_INDEX:
					column
						.setCellRenderer(new pcgen.gui.utils.JTableEx.AlignCellRenderer(
							SwingConstants.CENTER));
					break;

				case COL_INDEX:
					column.setCellEditor(new OutputOrderEditor(new String[]{
						PropertyFactory.getString("in_iskFirst"), //$NON-NLS-1$
						PropertyFactory.getString("in_iskLast"), //$NON-NLS-1$
						PropertyFactory.getString("in_iskHidden")})); //$NON-NLS-1$
					break;

				case COL_INC:
				case COL_DEC:
					column.setCellRenderer(plusMinusRenderer);
					column.setMaxWidth(30);
					column.setMinWidth(30);
					break;

				default:
					break;
			}
		}

	}

	private class ClassSkillFilter extends AbstractPObjectFilter
	{
		private ClassSkillFilter()
		{
			super("Skill", "Class");

			if (SettingsHandler.isToolTipTextShown())
			{
				setDescription(PropertyFactory
					.getString("in_iskFilter_class_tooltip")); //$NON-NLS-1$
			}
		}

		public final String getName(PlayerCharacter aPC)
		{
			PCClass pcClass = getSelectedPCClass();

			if (pcClass != null)
			{
				return super.getName(aPC)
					+ " (" + pcClass.getDisplayName() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
			}

			return super.getName(aPC);
		}

		public boolean accept(PlayerCharacter aPC, PObject pObject)
		{
			if (pObject == null)
			{
				return false;
			}

			if (pObject instanceof Skill)
			{
				PCClass pcClass = getSelectedPCClass();

				return (pcClass != null)
					&& aPC.isClassSkill(((Skill) pObject), pcClass);
			}

			return true;
		}
	}

	private class CrossClassSkillFilter extends AbstractPObjectFilter
	{
		private CrossClassSkillFilter()
		{
			super("Skill", "Cross-Class");

			if (SettingsHandler.isToolTipTextShown())
			{
				setDescription(PropertyFactory
					.getString("in_iskFilter_crossclass_tooltip")); //$NON-NLS-1$
			}
		}

		public String getName(PlayerCharacter aPC)
		{
			PCClass pcClass = getSelectedPCClass();

			if (pcClass != null)
			{
				return super.getName(aPC)
					+ " (" + pcClass.getDisplayName() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
			}

			return super.getName(aPC);
		}

		public boolean accept(PlayerCharacter aPC, PObject pObject)
		{
			if (pObject == null)
			{
				return false;
			}

			if (pObject instanceof Skill)
			{
				PCClass pcClass = getSelectedPCClass();
				Skill aSkill = (Skill) pObject;

				return (pcClass != null) && !aPC.isClassSkill(aSkill, pcClass)
					&& !aSkill.getSafe(ObjectKey.EXCLUSIVE);
			}

			return true;
		}
	}

	private class ExclusiveSkillFilter extends AbstractPObjectFilter
	{
		private ExclusiveSkillFilter()
		{
			super("Skill", "Exclusive");

			if (SettingsHandler.isToolTipTextShown())
			{
				setDescription(PropertyFactory
					.getString("in_iskFilter_exclusive_tooltip")); //$NON-NLS-1$
			}
		}

		public String getName(PlayerCharacter aPC)
		{
			PCClass pcClass = getSelectedPCClass();

			if (pcClass != null)
			{
				return super.getName(aPC)
					+ " (" + pcClass.getDisplayName() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
			}

			return super.getName(aPC);
		}

		public boolean accept(PlayerCharacter aPC, PObject pObject)
		{
			if (pObject == null)
			{
				return false;
			}

			if (pObject instanceof Skill)
			{
				PCClass pcClass = getSelectedPCClass();
				Skill aSkill = (Skill) pObject;

				return (pcClass != null) && !aPC.isClassSkill(aSkill, pcClass)
					&& aSkill.getSafe(ObjectKey.EXCLUSIVE);
			}

			return true;
		}
	}

	/*
	 * define ClassSkillFilter and CrossClassSkillFilter locally,
	 * these two depend on the currently selected class,
	 * so they cannot be used outside of InfoSkills
	 *
	 * I don't really like it, but I can't think of a better/cleaner
	 * solution right now
	 */
	private class QualifyFilter extends AbstractPObjectFilter
	{
		private QualifyFilter()
		{
			super("Miscellaneous", "Qualify");

			if (SettingsHandler.isToolTipTextShown())
			{
				setDescription(PropertyFactory
					.getString("in_iskFilter_qual_tooltip")); //$NON-NLS-1$
			}
		}

		public boolean accept(PlayerCharacter aPC, PObject pObject)
		{
			if (pObject == null)
			{
				return false;
			}

			if (pObject instanceof Skill)
			{
				PCClass pcClass = getSelectedPCClass();
				Skill aSkill = (Skill) pObject;

				return (pcClass != null)
					&& !(aSkill.getSafe(ObjectKey.EXCLUSIVE) && !aPC.isClassSkill(aSkill, pcClass));
			}

			return true;
		}
	}

	private class SkillPopupListener extends MouseAdapter
	{
		private JTree tree;
		private SkillPopupMenu menu;

		private SkillPopupListener(JTreeTable treeTable, SkillPopupMenu aMenu)
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
							Component aComponent = menu.getComponent(i);

							if (aComponent instanceof JMenuItem)
							{
								final JMenuItem menuItem =
										(JMenuItem) aComponent;
								KeyStroke ks = menuItem.getAccelerator();

								if ((ks != null) && keyStroke.equals(ks))
								{
									selPath = tree.getSelectionPath();
									menuItem.doClick(2);

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

				JMenu resortMenu = menu.getResortMenu();
				if (resortMenu != null)
				{
					resortMenu
						.setEnabled(selectedOutputOrder == GuiConstants.INFOSKILLS_OUTPUT_BY_MANUAL);
				}

				menu.show(evt.getComponent(), evt.getX(), evt.getY());
			}
		}
	}

	private class SkillPopupMenu extends JPopupMenu
	{
		static final long serialVersionUID = -5369872214039221832L;
		private JMenu resortMenu = null;
		private String lastSearch = "";

		/**
		 * Create a new SkillPopupMenu for a skills tree, either the available
		 * or selected table.
		 *
		 * @param treeTable The skills tree.
		 */
		SkillPopupMenu(JTreeTable treeTable)
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
				 */
				SkillPopupMenu.this.add(createAddMenuItem(PropertyFactory
					.getString("in_iskAdd_1"), 1, "shortcut EQUALS")); //$NON-NLS-1$ //$NON-NLS-2$
				SkillPopupMenu.this.add(createAddMenuItem(PropertyFactory
					.getString("in_iskAdd_2"), 2, null)); //$NON-NLS-1$
				SkillPopupMenu.this.add(createAddMenuItem(PropertyFactory
					.getString("in_iskAdd_5"), 5, null)); //$NON-NLS-1$
				SkillPopupMenu.this.add(createAddMenuItem(PropertyFactory
					.getString("in_iskAdd_10"), 10, null)); //$NON-NLS-1$
				SkillPopupMenu.this.add(createAddMenuItem(PropertyFactory
					.getString("in_iskAdd_n"), -1, "alt A")); //$NON-NLS-1$ //$NON-NLS-2$
				SkillPopupMenu.this.add(createMaxMenuItem(PropertyFactory
					.getString("in_iskMax_Ranks"), "alt M")); //$NON-NLS-1$ //$NON-NLS-2$
				this.addSeparator();
				SkillPopupMenu.this.add(Utility.createMenuItem("Find item",
					new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							lastSearch = availableTable.searchTree(lastSearch);
						}
					}, "searchItem", (char) 0, "shortcut F", "Find item", null,
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
				SkillPopupMenu.this.add(createAddMenuItem(PropertyFactory
					.getString("in_iskAdd_1"), 1, "shortcut EQUALS")); //$NON-NLS-1$ //$NON-NLS-2$
				SkillPopupMenu.this.add(createAddMenuItem(PropertyFactory
					.getString("in_iskAdd_2"), 2, null)); //$NON-NLS-1$
				SkillPopupMenu.this.add(createAddMenuItem(PropertyFactory
					.getString("in_iskAdd_5"), 5, null)); //$NON-NLS-1$
				SkillPopupMenu.this.add(createAddMenuItem(PropertyFactory
					.getString("in_iskAdd_10"), 10, null)); //$NON-NLS-1$
				SkillPopupMenu.this.add(createAddMenuItem(PropertyFactory
					.getString("in_iskAdd_n"), -1, "alt A")); //$NON-NLS-1$ //$NON-NLS-2$
				SkillPopupMenu.this.add(createRemoveMenuItem(PropertyFactory
					.getString("in_iskRemove_1"), 1, "shortcut MINUS")); //$NON-NLS-1$ //$NON-NLS-2$
				SkillPopupMenu.this.add(createRemoveMenuItem(PropertyFactory
					.getString("in_iskRemove_2"), 2, null)); //$NON-NLS-1$
				SkillPopupMenu.this.add(createRemoveMenuItem(PropertyFactory
					.getString("in_iskRemove_5"), 5, null)); //$NON-NLS-1$
				SkillPopupMenu.this.add(createRemoveMenuItem(PropertyFactory
					.getString("in_iskRemove_10"), 10, null)); //$NON-NLS-1$
				SkillPopupMenu.this.add(createRemoveMenuItem(PropertyFactory
					.getString("in_iskRemove_n"), -1, "alt R")); //$NON-NLS-1$ //$NON-NLS-2$
				SkillPopupMenu.this.add(createMaxMenuItem(PropertyFactory
					.getString("in_iskMax_Ranks"), "alt M")); //$NON-NLS-1$ //$NON-NLS-2$
				SkillPopupMenu.this.add(createResetMenuItem(PropertyFactory
					.getString("in_iskZero_Ranks"), "alt Z")); //$NON-NLS-1$ //$NON-NLS-2$
				this.addSeparator();
				SkillPopupMenu.this.add(Utility.createMenuItem("Find item",
					new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							lastSearch = selectedTable.searchTree(lastSearch);
						}
					}, "searchItem", (char) 0, "shortcut F", "Find item", null,
					true));

				this.addSeparator();

				resortMenu =
						Utility
							.createMenu(
								"Output Order",
								(char) 0,
								PropertyFactory.getString("in_iskOutput_Order"), null, true); //$NON-NLS-1$ //$NON-NLS-2$

				SkillPopupMenu.this.add(resortMenu);

				resortMenu.add(Utility.createMenuItem(
					PropertyFactory.getString("in_iskBy_name_ascending"), //$NON-NLS-1$
					new ResortActionListener(SkillComparator.RESORT_NAME,
						SkillComparator.RESORT_ASCENDING), "sortOutput",
					(char) 0, null, PropertyFactory
						.getString("in_iskBy_name_ascending_tooltip"), null,
					true)); //$NON-NLS-1$ //$NON-NLS-2$
				resortMenu.add(Utility.createMenuItem(
					PropertyFactory.getString("in_iskBy_name_descending"), //$NON-NLS-1$
					new ResortActionListener(SkillComparator.RESORT_NAME,
						SkillComparator.RESORT_DESCENDING), "sortOutput",
					(char) 0, null, PropertyFactory
						.getString("in_iskBy_name_descending_tooltip"), null,
					true)); //$NON-NLS-1$ //$NON-NLS-2$
				resortMenu
					.add(Utility
						.createMenuItem(
							PropertyFactory
								.getString("in_iskBy_trained_then_untrained"), //$NON-NLS-1$
							new ResortActionListener(
								SkillComparator.RESORT_TRAINED,
								SkillComparator.RESORT_ASCENDING),
							"sortOutput",
							(char) 0,
							null,
							PropertyFactory
								.getString("in_iskBy_trained_then_untrained_tooltip"), null, true)); //$NON-NLS-1$ //$NON-NLS-2$
				resortMenu
					.add(Utility
						.createMenuItem(
							PropertyFactory
								.getString("in_iskBy_untrained_then_trained"), //$NON-NLS-1$
							new ResortActionListener(
								SkillComparator.RESORT_TRAINED,
								SkillComparator.RESORT_DESCENDING),
							"sortOutput",
							(char) 0,
							null,
							PropertyFactory
								.getString("in_iskBy_untrained_then_trained_tooltip"), null, true)); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}

		private JMenuItem createAddMenuItem(String label, int qty,
			String accelerator)
		{
			return Utility
				.createMenuItem(
					label,
					new AddSkillActionListener(qty),
					"add " + qty,
					(char) 0,
					accelerator,
					PropertyFactory.getString("in_iskAdd")
						+ ((qty < 0) ? PropertyFactory.getString("in_iskn")
							: Integer.toString(qty))
						+ PropertyFactory.getString("in_isk_skill_point")
						+ ((qty == 1)
							? "" : PropertyFactory.getString("in_isks")), "Add16.gif", true); //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
		}

		private JMenuItem createMaxMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new MaxSkillActionListener(0),
				"max ranks", (char) 0, accelerator, PropertyFactory
					.getString("in_iskSet_to_max_ranks"), "Add16.gif", true); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		private JMenuItem createRemoveMenuItem(String label, int qty,
			String accelerator)
		{
			return Utility
				.createMenuItem(
					label,
					new RemoveSkillActionListener(qty),
					"remove " + qty,
					(char) 0,
					accelerator,
					PropertyFactory.getString("in_iskRemove")
						+ ((qty < 0) ? PropertyFactory.getString("in_iskn")
							: Integer.toString(qty))
						+ PropertyFactory.getString("in_isk_skill_point")
						+ ((qty == 1)
							? "" : PropertyFactory.getString("in_isks")), "Remove16.gif", true); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
		}

		private JMenuItem createResetMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label,
				new ResetSkillActionListener(0), "reset ranks", (char) 0,
				accelerator, PropertyFactory
					.getString("in_iskReset_to_zero_ranks"), "Add16.gif", true); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		/**
		 * Return the re-sorting menu
		 * @return the re-sorting menu
		 */
		public JMenu getResortMenu()
		{
			return resortMenu;
		}

		private class AddSkillActionListener extends SkillActionListener
		{
			private AddSkillActionListener(int qty)
			{
				super(qty);
			}

			public void actionPerformed(ActionEvent evt)
			{
				int newQty = qty;

				// Get a number from the user via a popup
				if (qty < 0)
				{
					String selectedValue =
							JOptionPane.showInputDialog(null, PropertyFactory
								.getString("in_iskAdd_quantity_tooltip"),
								Constants.APPLICATION_NAME,
								JOptionPane.QUESTION_MESSAGE); //$NON-NLS-1$

					if (selectedValue != null)
					{
						try
						{
							//abs just in case someone types in a negative value
							newQty =
									Math.abs(Integer.parseInt(selectedValue
										.trim()));
						}
						catch (NumberFormatException e)
						{
							ShowMessageDelegate.showMessageDialog(
								PropertyFactory
									.getString("in_iskInvalid_number"),
								Constants.APPLICATION_NAME, MessageType.ERROR); //$NON-NLS-1$

							return;
						}
					}
					else
					{
						return;
					}
				}

				addSkill(newQty);
			}
		}

		private class MaxSkillActionListener extends SkillActionListener
		{
			//qty should remain unused by this derived class
			private MaxSkillActionListener(int qty)
			{
				super(qty);
			}

			public void actionPerformed(ActionEvent evt)
			{
				Skill aSkill = getSelectedSkill();

				if (aSkill == null)
				{
					return;
				}

				final PlayerCharacter currentPC = pc;
				currentPC.setDirty(true);

				PCClass aClass = getSelectedPCClass();
				PCLevelInfo pcl = getSelectedLevelInfo(currentPC);
				double maxRank = 0.0;
				double skillPool = 0.0;

				if (aClass != null)
				{
					maxRank =
							currentPC.getMaxRank(aSkill, aClass)
								.doubleValue();
					if (Globals.getGameModeHasPointPool())
					{
						skillPool = pc.getSkillPoints();
					}
					else if (pcl != null)
					{
						skillPool = pcl.getSkillPointsRemaining();
					}
				}

				if ((maxRank > SkillRankControl.getRank(pc, aSkill).doubleValue())
					|| Globals.checkRule(RuleConstants.SKILLMAX)) //$NON-NLS-1$
				{
					final int cost =
							pc.getSkillCostForClass(aSkill, aClass).getCost();
					final double pointsNeeded =
							Math.floor((maxRank - SkillRankControl.getTotalRank(pc, aSkill)
								.doubleValue())
								* cost);
					double points = Math.min(pointsNeeded, skillPool);

					final int classSkillCost =
							SkillCost.CLASS.getCost();
					if (classSkillCost > 1)
					{
						points = Math.floor(points / classSkillCost);
					}
					addSkill((int) points);
				}
				else
				{
					ShowMessageDelegate.showMessageDialog(PropertyFactory
						.getString("in_iskErr_message_01"),
						Constants.APPLICATION_NAME, MessageType.INFORMATION); //$NON-NLS-1$
				}
			}
		}

		private class RemoveSkillActionListener extends SkillActionListener
		{
			private RemoveSkillActionListener(int qty)
			{
				super(qty);
			}

			public void actionPerformed(ActionEvent evt)
			{
				int newQty = qty;

				// Get a number from the user via a popup
				if (qty < 0)
				{
					String selectedValue =
							JOptionPane.showInputDialog(null, PropertyFactory
								.getString("in_iskRemove_quantity_tooltip"),
								Constants.APPLICATION_NAME,
								JOptionPane.QUESTION_MESSAGE); //$NON-NLS-1$

					if (selectedValue != null)
					{
						try
						{
							//abs just in case someone types in a negative value
							newQty =
									Math.abs(Integer.parseInt(selectedValue
										.trim()));
						}
						catch (NumberFormatException e)
						{
							ShowMessageDelegate.showMessageDialog(
								PropertyFactory
									.getString("in_iskInvalid_number"),
								Constants.APPLICATION_NAME, MessageType.ERROR); //$NON-NLS-1$

							return;
						}
					}
					else
					{
						return;
					}
				}

				addSkill(-newQty);
			}
		}

		private class ResetSkillActionListener extends SkillActionListener
		{
			//qty should remain unused by this derived class
			private ResetSkillActionListener(int qty)
			{
				super(qty);
			}

			public void actionPerformed(ActionEvent evt)
			{
				Skill aSkill = getSelectedSkill();

				if (aSkill != null && pc.hasSkill(aSkill))
				{
					//remove all ranks from this skill for all PCClasses
					for (Iterator<PCClass> iter = pc.getClassList().iterator(); iter
						.hasNext();)
					{
						//TODO: This value is thrown away, should it really be?
						iter.next();
						final int cost =
								pc.getSkillCostForClass(aSkill, getSelectedPCClass()).getCost();
						double points =
								-SkillRankControl.getTotalRank(pc, aSkill).doubleValue() * cost;
						if (SkillCost.CLASS.getCost() > 1)
						{
							points /= SkillCost.CLASS.getCost();
						}

						addSkill((int) points);

						//						aSkill.setZeroRanks(aClass);
					}

					//
					// Remove the skill from the skill list if we've just set the rank to zero
					// and it is not an untrained skill
					//
					if ((CoreUtility.doublesEqual(SkillRankControl.getRank(pc, aSkill)
						.doubleValue(), 0.0))
						&& !aSkill.getSafe(ObjectKey.USE_UNTRAINED))
					{
						pc.removeSkill(aSkill);
					}

					// don't need to update availableTable
					updateSelectedModel();

					// available skill points need update
					if (!Globals.getGameModeHasPointPool())
					{
						currCharacterClassActionPerformed();
					}
				}
			}
		}

		private class SkillActionListener implements ActionListener
		{
			int qty = 0;

			private SkillActionListener(int aQty)
			{
				qty = aQty;
			}

			public void actionPerformed(ActionEvent evt)
			{
				// TODO This method currently does nothing?
			}
		}
	}

	private void updatePcl(int points)
	{
		PCLevelInfo pcl;
		if (points > 0)
		{
			int ptsRemaining;
			pcl = getSelectedLevelInfo(pc);
			while ((pcl != null) && (points > 0))
			{
				ptsRemaining = pcl.getSkillPointsRemaining();
				if (ptsRemaining >= points)
				{
					ptsRemaining -= points;
					pcl.setSkillPointsRemaining(ptsRemaining);
					points = 0;
				}
				else
				{
					points -= ptsRemaining;
					pcl.setSkillPointsRemaining(0);
					pcl = getSelectedLevelInfo(pc);
				}
			}
		}
		else
		{
			points = -points;
			for (int i = pc.getLevelInfo().size() - 1; i >= 0; --i)
			{
				pcl = pc.getLevelInfo().get(i);
				final int ptsGained = pcl.getSkillPointsGained(pc);
				int ptsUsed = ptsGained - pcl.getSkillPointsRemaining();
				if (ptsUsed >= points)
				{
					ptsUsed -= points;
					pcl.setSkillPointsRemaining(ptsGained - ptsUsed);
					break;
				}
				pcl.setSkillPointsRemaining(ptsGained);
				points -= ptsUsed;
			}
		}
	}

	//
	// Compile a list of all skills the character currently has that have prerequisites that
	// are currently fulfilled
	//
	private ArrayList<Skill> getSatisfiedPrereqSkills(final Skill theSkill)
	{
		ArrayList<Skill> prereqSkills = new ArrayList<Skill>();
		for (Skill aSkill : pc.getSkillSet())
		{
			if (theSkill.compareTo(aSkill) != 0)
			{
				if (aSkill.hasPrerequisites() && aSkill.qualifies(pc, aSkill))
				{
					prereqSkills.add(aSkill);
				}
			}
		}
		return prereqSkills;
	}

	/*
	 * Given a list of skills, determine if they all meet their prerequisites
	 * @param prereqSkills
	 * @return true if at least 1 one skill fails its prerequisites
	 * @return false if all pass prerequisites
	 */
	private boolean prereqSkillsInvalid(ArrayList<Skill> prereqSkills)
	{
		for (Iterator<Skill> iter = prereqSkills.iterator(); iter.hasNext();)
		{
			final Skill aSkill = iter.next();

			if (aSkill.qualifies(pc, aSkill))
			{
				iter.remove();
			}
		}
		//
		// Any skills that no longer meet the prerequisites are left in the list
		//
		if (prereqSkills.size() != 0)
		{
			return true;
		}
		return false;
	}

	private final class RendererEditor implements TableCellRenderer
	{
		private DefaultTableCellRenderer def = new DefaultTableCellRenderer();
		private JButton plusButton = new JButton("+"); //$NON-NLS-1$

		private RendererEditor()
		{
			def.setBackground(InfoSkills.this.getBackground());
			def.setAlignmentX(Component.CENTER_ALIGNMENT);
			def.setHorizontalAlignment(SwingConstants.CENTER);
			plusButton.setPreferredSize(new Dimension(30, 24));
			plusButton.setMinimumSize(new Dimension(30, 24));
			plusButton.setMaximumSize(new Dimension(30, 24));
		}

		public Component getTableCellRendererComponent(JTable table,
			Object value, boolean isSelected, boolean hasFocus, int row,
			int column)
		{
			int colNum = table.convertColumnIndexToModel(column);
			if (colNum == COL_INC)
			{
				def.setText("+"); //$NON-NLS-1$
				def.setBorder(BorderFactory.createEtchedBorder());

				return def;
			}
			else if (colNum == COL_DEC)
			{
				def.setText("-"); //$NON-NLS-1$
				def.setBorder(BorderFactory.createEtchedBorder());

				return def;
			}

			return null;
		}
	}

	/*
	 * Debugging
	 *

	 private void dumpLevelInfo()
	 {
	 for (Iterator iter = pc.getLevelInfo().iterator(); iter.hasNext();)
	 {
	 final PCLevelInfo pcl = (PCLevelInfo) iter.next();
	 System.err.println(Integer.toString(pcl.getLevel()) + ":" + Integer.toString(pcl.getSkillPointsRemaining()));
	 }
	 }
	 */
}
