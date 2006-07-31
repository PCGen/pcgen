/*
 * InfoRace.java
 * Copyright 2002 (C) Bryan McRoberts
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE  See the GNU
 * Lesser General Public License for more details
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * @author  Bryan McRoberts (merton_monk@yahoo.com)
 * Created on May 1, 2001, 5:57 PM
 * ReCreated on Feb 22, 2002 7:45 AM
 *
 * Current Ver: $Revision: 198 $
 * Last Editor: $Author: nuance $
 * Last Edited: $Date: 2006-03-14 16:04:50 -0700 (Tue, 14 Mar 2006) $
 *
 */
package pcgen.gui.tabs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
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

import pcgen.core.Constants;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
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
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;
import pcgen.util.enumeration.Tab;

/**
 *  <code>InfoRace</code> creates a new tabbed panel
 *  with all the race and template information on it
 *
 * @author  Bryan McRoberts (merton_monk@yahoo.com)
 * @version $Revision: 198 $
 **/
public class InfoRaces extends FilterAdapterPanel implements CharacterInfoTab
{
	static final long serialVersionUID = 2565545289875422981L;
	
	private static final Tab tab = Tab.RACES;
	
	private static boolean needsUpdate = true;

	// if you change these, you also have to change
	// the case statement in the RaceModel declaration
	private static final int COL_NAME = 0;
	private static final int COL_STAT = 1;
	private static final int COL_PRE = 2;
	private static final int COL_SIZE = 3;
	private static final int COL_MOVE = 4;
	private static final int COL_VISION = 5;
	private static final int COL_CLASS = 6;
	private static final int COL_LEVEL = 7;
	private FlippingSplitPane bsplit;
	private JButton selButton = new JButton(PropertyFactory.getString("in_select"));
	private JButton clearQFilterButton = new JButton("Clear");
	private JComboBoxEx viewComboBox = new JComboBoxEx();
	private final JLabel lblQFilter = new JLabel("Filter:");
	private JLabel raceText = new JLabel();
	private JLabel raceTextLabel = new JLabel(PropertyFactory.getString("in_irSelectedRace"));
	private JLabel sortLabel = new JLabel(PropertyFactory.getString("in_irSortRaces"));
	private JLabelPane infoLabel = new JLabelPane();
	private JPanel botPane = new JPanel();

	//Monster Hit Die Panel
	private JPanel monHdPanel = new JPanel();
	private JLabel lblHDModify = new JLabel(PropertyFactory.getString("in_sumHDToAddRem"));
	private WholeNumberField txtHD = new WholeNumberField(1, 3);
	private JButton btnAddHD = new JButton("+");
	private JButton btnRemoveHD = new JButton("-");
	private JLabel txtMonsterHD = new JLabel("1");
	private JLabel lblMonsterHD = new JLabel(PropertyFactory.getString("in_sumMonsterHitDice"));

	private JPanel topPane = new JPanel();
	private JTextField textQFilter = new JTextField();
	private JTreeTable raceTable; // Races
	private JTreeTableSorter raceSort = null;

	// the list from which to pull the templates to use
	private RaceModel raceModel = null; // Model for JTreeTable
	private TreePath selPath;

	// Monster HD Panel
	private boolean hasBeenSized = false;
	private int viewMode = 0;
	private static Integer saveViewMode = null;

	private PlayerCharacter pc;
	private int serial = 0;
	private boolean readyForRefresh = false;

	/**
	 * Constructor
	 * @param pc
	 */
	public InfoRaces(PlayerCharacter pc)
	{
		this.pc = pc;
		// do not change/remove this as we use the component's name
		// to save component specific settings
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
		if(this.pc != pc || pc.getSerial() > serial)
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
		return SettingsHandler.getPCGenOption(".Panel.Race.Order", tab.ordinal());
	}

	public void setTabOrder(int order)
	{
		SettingsHandler.setPCGenOption(".Panel.Race.Order", order);
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
		if (pc.getRace() == null
			|| Constants.s_NONESELECTED.equals(pc.getRace().getKeyName()))
		{
			toDoList.add(PropertyFactory.getString("in_irTodoRace")); //$NON-NLS-1$
		}
		return toDoList;
	}

	public void refresh()
	{
		if(pc.getSerial() > serial)
		{
			serial = pc.getSerial();
			forceRefresh();
		}
	}

	public void forceRefresh()
	{
		if(readyForRefresh)
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
	 * Sets the nedsUpdate flag for Races tab
	 * @param b
	 */
	public static void setNeedsUpdate(boolean b)
	{
		needsUpdate = b;
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
	 * Push an update of the tabs in the GUI
	 */
	public void pushUpdate()
	{
		final PCGen_Frame1 rootFrame = PCGen_Frame1.getInst();
		rootFrame.featList_Changed();
		rootFrame.hpTotal_Changed();
		PCGen_Frame1.forceUpdate_PlayerTabs();
		CharacterInfo pane = PCGen_Frame1.getCharacterPane();
		pane.setPaneForUpdate(pane.infoSkills());
		pane.setPaneForUpdate(pane.infoSpells());
		pane.setPaneForUpdate(pane.infoDomain());
		pane.setPaneForUpdate(pane.infoInventory());
		pane.setPaneForUpdate(pane.infoSummary());
		pane.refresh();
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
	 * @return FilterConstants.MULTI_MULTI_MODE = 2
	 **/
	public final int getSelectionMode()
	{
		return FilterConstants.MULTI_MULTI_MODE;
	}

	/**
	 * implementation of Filterable interface
	 **/
	public final void initializeFilters()
	{
		FilterFactory.registerAllSourceFilters(this);
		FilterFactory.registerAllSizeFilters(this);
		FilterFactory.registerAllRaceFilters(this);
		FilterFactory.registerAllPrereqAlignmentFilters(this);
	}

	/**
	 * implementation of Filterable interface
	 **/
	public final void refreshFiltering()
	{
		createModel();
		raceTable.updateUI();
	}

	private class AvailableClickHandler implements ClickHandler
	{
		public void singleClickEvent() {
			// Do Nothing
		}

		public void doubleClickEvent()
		{
			selButton();
		}
		public boolean isSelectable(Object obj)
		{
			return !(obj instanceof String);
		}
	}

	private final void createTreeTables()
	{
		raceTable = new JTreeTable(raceModel);
		raceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		final JTree atree = raceTable.getTree();
		atree.setRootVisible(false);
		atree.setShowsRootHandles(true);
		atree.setCellRenderer(new LabelTreeCellRenderer());

		raceTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
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

						final Object temp = atree.getPathForRow(idx).getLastPathComponent();

						if (temp == null)
						{
							return;
						}

						PObjectNode fNode = (PObjectNode) temp;

						if (fNode.getItem() instanceof Race)
						{
							Race aRace = (Race) fNode.getItem();
							setInfoLabelText(aRace);
						}
						else
						{
							setInfoLabelText(null);
						}
					}
				}
			});

		raceTable.addMouseListener(new JTreeTableMouseAdapter(raceTable, new AvailableClickHandler(), false));

		// create the rightclick popup menus
		hookupPopupMenu(raceTable);
	}

	private void setInfoLabelText(Race aRace)
	{
		StringBuffer b = new StringBuffer();
		b.append("<html>");

		if ((aRace != null) && !aRace.getKeyName().startsWith("<none"))
		{
			b.append("<b>").append(aRace.piSubString()).append("</b>");
			b.append("<br><b>RACE TYPE</b>: ").append(aRace.getRaceType());
			List<String> subTypes = aRace.getRacialSubTypes();
			if (subTypes.size() > 0)
			{
				b.append(" &nbsp;<b>SUBTYPES</b>: ");
				boolean first = true;
				for (String s : subTypes)
				{
					if (!first)
					{
						b.append(", ");
					}
					b.append(s);
					first = false;
				}
			}
			if (aRace.getType().length() > 0)
			{
				b.append(" &nbsp;<b>TYPE</b>:").append(aRace.getType());
			}
			String bString = aRace.getSource();

			if (bString.length() > 0)
			{
				b.append(" &nbsp;<b>SOURCE</b>:").append(bString);
			}
		}

		b.append("</html>");
		infoLabel.setText(b.toString());
	}

	private static int getSelectedIndex(ListSelectionEvent e)
	{
		final DefaultListSelectionModel model = (DefaultListSelectionModel) e.getSource();

		if (model == null)
		{
			return -1;
		}

		return model.getMinSelectionIndex();
	}

	/**
	 * creates the RaceModel that will be used
	 **/
	private final void createModel()
	{
		if (raceModel == null)
		{
			raceModel = new RaceModel(viewMode);
		}
		else
		{
			raceModel.resetModel(viewMode);
		}

		if (raceSort != null)
		{
			raceSort.setRoot((PObjectNode) raceModel.getRoot());
			raceSort.sortNodeOnColumn();
		}
	}

	/**
	 * This is called when the tab is shown
	 **/
	private void formComponentShown()
	{
		requestFocus();
		PCGen_Frame1.setMessageAreaTextWithoutSaving(PropertyFactory.getString("in_irSelectRace"));
		refresh();

		int width;
		int t = bsplit.getDividerLocation();

		if (!hasBeenSized)
		{
			t = SettingsHandler.getPCGenOption("InfoRace.bsplit", (int) (InfoRaces.this.getSize().getHeight() - 120));

			// set the prefered width on raceTable
			for (int i = 0; i < raceTable.getColumnCount(); i++)
			{
				TableColumn sCol = raceTable.getColumnModel().getColumn(i);
				width = Globals.getCustColumnWidth("Race", i);

				if (width != 0)
				{
					sCol.setPreferredWidth(width);
				}

				sCol.addPropertyChangeListener(new ResizeColumnListener(raceTable, "Race", i));
			}
		}

		if (t > 0)
		{
			bsplit.setDividerLocation(t);
			SettingsHandler.setPCGenOption("InfoRace.bsplit", t);
		}
	}

	private void hookupPopupMenu(JTreeTable treeTable)
	{
		treeTable.addMouseListener(new RacePopupListener(treeTable, new RacePopupMenu()));
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
		addComponentListener(new ComponentAdapter()
			{
				public void componentResized(ComponentEvent e)
				{
					int s = bsplit.getDividerLocation();

					if (s > 0)
					{
						SettingsHandler.setPCGenOption("InfoRace.bsplit", s);
					}
				}
			});
		selButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					selButton();
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

		// Monster HD Panel Listeners
		btnAddHD.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					int num = 1;
					try {
						num = Integer.parseInt(txtHD.getText());
					}
					catch(Exception exe) {
						// TODO Deal with this
					}
					PCGen_Frame1.addMonsterHD(num);
					updateHD();
				}
			});
		btnRemoveHD.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					int num = 1;
					try {
						num = Integer.parseInt(txtHD.getText());
					}
					catch(Exception exe) {
						// TODO Deal with this
					}
					PCGen_Frame1.addMonsterHD(num * -1);
					updateHD();
				}
			});

		FilterFactory.restoreFilterSettings(this);
	}

	/**
	 * This method is called from within the
	 * constructor to initialize the form
	 **/
	private void initComponents()
	{
		readyForRefresh = true;
		//
		// View List Sanity check
		//
		final int iView = SettingsHandler.getRaceTab_ListMode();

		if ((iView >= GuiConstants.INFORACE_VIEW_NAME) && (iView <= GuiConstants.INFORACE_VIEW_SOURCE))
		{
			viewMode = iView;
		}

		SettingsHandler.setRaceTab_ListMode(viewMode);

		viewComboBox.addItem(PropertyFactory.getString("in_nameLabel") + "   ");
		viewComboBox.addItem(PropertyFactory.getString("in_racetypeName") + "   ");
		viewComboBox.addItem(PropertyFactory.getString("in_racetypeSubtypeName") + "   ");
		viewComboBox.addItem(PropertyFactory.getString("in_typeName") + "   ");
		viewComboBox.addItem(PropertyFactory.getString("in_allTypes") + "   ");
		viewComboBox.addItem(PropertyFactory.getString("in_sourceName") + " ");
		viewComboBox.setSelectedIndex(viewMode);

		// initialize the raceModel
		raceModel = new RaceModel(viewMode);

		// create the tree's from the raceModel
		createTreeTables();

		//-----------------------------------------------------------------------
		// build the topPane which will contain leftPane and rightPane
		// leftPane will have a panel and a scrollregion
		// rightPane will have a single panel
		//-----------------------------------------------------------------------
		buildTopPanel();

		//-----------------------------------------------------------------------
		//  Bottom Panel
		//  - this has all the Template stuff in it
		//-----------------------------------------------------------------------
		buildBottomPanel();

		//----------------------------------------------------------------------
		// now split the top and bottom panels
		bsplit = new FlippingSplitPane(JSplitPane.VERTICAL_SPLIT, topPane, botPane);
		bsplit.setOneTouchExpandable(true);
		bsplit.setDividerSize(10);

		// now add all the panes (centered of course)
		this.setLayout(new BorderLayout());
		this.add(bsplit, BorderLayout.CENTER);

		addFocusListener(new FocusAdapter()
			{
				public void focusGained(FocusEvent evt)
				{
					refresh();
				}
			});
	}

	/**
	 * Build the top panel.
	 * topPane which will contain leftPane and rightPane
	 * leftPane will have two panels and a scrollregion
	 * rightPane will have one panel and a scrollregion
	 */
	private void buildTopPanel()
	{
		topPane.setLayout(new BorderLayout());

		JPanel mainPane = new JPanel();
		mainPane.setLayout(new BorderLayout());
		topPane.add(mainPane, BorderLayout.CENTER);

		mainPane.add(InfoTabUtils.createFilterPane(sortLabel, viewComboBox, lblQFilter, textQFilter, clearQFilterButton), BorderLayout.NORTH);

		JScrollPane scrollPane = new JScrollPane(raceTable);
		raceTable.setColAlign(3, SwingConstants.CENTER);
		raceTable.setColAlign(7, SwingConstants.CENTER);
		// add the sorter so that clicking on the TableHeader
		// actualy does something
		raceSort = new JTreeTableSorter(raceTable, (PObjectNode) raceModel.getRoot(), raceModel);
		JButton columnButton = new JButton();
		scrollPane.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, columnButton);
		columnButton.setText("^");
		new TableColumnManager(raceTable, columnButton, raceModel);
		mainPane.add(scrollPane, BorderLayout.CENTER);

		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 1));
		raceText.setPreferredSize(new Dimension(120, 25));
		raceText.setBorder(BorderFactory.createEtchedBorder());
		raceText.setHorizontalAlignment(SwingConstants.CENTER);
		bottomPanel.add(raceTextLabel);
		bottomPanel.add(raceText);
		bottomPanel.add(selButton);

		// Monster HD Panel (Part 1)
		monHdPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
		monHdPanel.add(lblHDModify);
		monHdPanel.add(txtHD);
		monHdPanel.add(btnAddHD);
		monHdPanel.add(btnRemoveHD);
		monHdPanel.add(lblMonsterHD);
		monHdPanel.add(txtMonsterHD);
		bottomPanel.add(monHdPanel);

		mainPane.add(bottomPanel, BorderLayout.SOUTH);
	}

	/**
	 * Build the top panel.
	 * botPane which will contain the info section
	 */
	private void buildBottomPanel()
	{
		botPane.setLayout(new BorderLayout());
		JScrollPane scroll = new JScrollPane();

		TitledBorder title1 = BorderFactory.createTitledBorder(PropertyFactory.getString("in_irRaceInfo"));
		title1.setTitleJustification(TitledBorder.CENTER);
		scroll.setBorder(title1);
		infoLabel.setBackground(topPane.getBackground());
		scroll.setViewportView(infoLabel);
		botPane.add(scroll);
	}

	/**
	 * selectes the race
	 **/
	private void selButton()
	{
		TreePath aPath = raceTable.getTree().getSelectionPath();

		if (aPath == null)
		{
			return;
		}

		Object endComp = aPath.getLastPathComponent();
		PObjectNode fNode = (PObjectNode) endComp;

		if (!(fNode.getItem() instanceof Race))
		{
			return;
		}

		Race aRace = (Race) fNode.getItem();

		if (aRace == null)
		{
			return;
		}

		Race oldRace = pc.getRace();

		if (!aRace.equals(oldRace))
		{
			pc.setRace(aRace);
			PCGen_Frame1.forceUpdate_PlayerTabs();
			CharacterInfo pane = PCGen_Frame1.getCharacterPane();
			pane.setPaneForUpdate(pane.infoClasses());
			pane.setPaneForUpdate(pane.infoFeats());
			pane.setPaneForUpdate(pane.infoSkills());
			pane.setPaneForUpdate(pane.infoSpells());
			pane.setPaneForUpdate(pane.infoSummary());
			pane.refresh();

			// If the either race was monstrous, natural weapons in the gear need
			// updated.  sage_sam 20 March 2003
			if (pc.getRace().hitDice(pc) != 0)
			{
				pc.getRace().rollHP(pc);
			}

			raceText.setText(pc.getRace().piString());
			raceText.setMinimumSize(new Dimension(120, 25));
			setInfoLabelText(pc.getRace());

			if (monHdPanel.isVisible())
			{
				updateHD();
			}
		}
	}

	/**
	 * This recalculates the states of everything based
	 * upon the currently selected character
	 **/
	private final void updateCharacterInfo()
	{
		monHdPanel.setVisible(SettingsHandler.hideMonsterClasses());

		if (!needsUpdate)
		{
			return;
		}

		try
		{
			raceText.setText(pc.getRace().piString());
			raceText.setMinimumSize(new Dimension(120, 25));
			setInfoLabelText(pc.getRace());
		}
		catch (Exception exc)
		{
			raceText.setText("");
			setInfoLabelText(null);
		}

		createModel();
		raceTable.updateUI();

		if (monHdPanel.isVisible())
		{
			updateHD();
			txtHD.setValue(1);
		}

		needsUpdate = false;
	}

	private void updateHD()
	{
		int monsterHD = -1;
		int minLevel = 0;

		if (pc != null)
		{
			final String monsterClass = pc.getRace().getMonsterClass(pc, false);

			if (monsterClass != null)
			{
				monsterHD = pc.getRace().hitDice(pc);
				minLevel = pc.getRace().hitDice(pc) + pc.getRace().getMonsterClassLevels(pc);

				final PCClass aClass = pc.getClassKeyed(monsterClass);

				if (aClass != null)
				{
					monsterHD += aClass.getLevel();
				}
			}
		}

		btnAddHD.setEnabled(pc.getRace().hasAdvancement() && (monsterHD >= 0));
		btnRemoveHD.setEnabled(monsterHD > minLevel);

		if (monsterHD < 0)
		{
			monsterHD = 0;
		}

		txtMonsterHD.setText(Integer.toString(monsterHD));
		txtHD.setEnabled(btnAddHD.isEnabled() | btnRemoveHD.isEnabled());
	}

	private void viewComboBoxActionPerformed()
	{
		final int index = viewComboBox.getSelectedIndex();

		if (index != viewMode)
		{
			viewMode = index;
			SettingsHandler.setRaceTab_ListMode(viewMode);
			createModel();
			raceTable.updateUI();
		}
	}

	private void clearQFilter()
	{
		raceModel.clearQFilter();
		if (saveViewMode != null)
		{
			viewMode = saveViewMode.intValue();
			saveViewMode = null;
		}
		textQFilter.setText("");
		raceModel.resetModel(viewMode);
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
		raceModel.setQFilter(aString);

		if (saveViewMode == null)
		{
			saveViewMode = new Integer(viewMode);
		}
		viewMode = GuiConstants.INFORACE_VIEW_NAME;
		raceModel.resetModel(viewMode);
		clearQFilterButton.setEnabled(true);
		viewComboBox.setEnabled(false);
		forceRefresh();
	}

	/**
	 * The RaceModel has a single root node
	 * This root node has a null parent
	 * all other nodes have a parent which points to a non-null node
	 * Parent nodes must have a list of all children
	 * Children must point to their parent
	 * nodes which have 0 children are leafs (the end of the linked list)
	 * most leafs contain an Object (in this case, it's a race object)
	 **/
	private final class RaceModel extends AbstractTreeTableModel implements TableColumnManagerModel {
		// this is the root node
		private PObjectNode raceRoot;
		private List<Boolean> displayList = null;

		// list of column names
		private final String[] raceNameList = {
			PropertyFactory.getString("in_nameLabel"),
			PropertyFactory.getString("in_irTableStat"),
			PropertyFactory.getString("in_preReqs"),
			PropertyFactory.getString("in_size"),
			PropertyFactory.getString("in_speed"),
			PropertyFactory.getString("in_vision"),
			PropertyFactory.getString("in_favoredClass"),
			PropertyFactory.getString("in_lvlAdj")
		};

		private final int[] colDefaultWidth = {
				300, 100, 100, 20,	100, 100, 40, 20
		};

		private RaceModel(int mode) {
			super(null);
			resetModel(mode);
			displayList = new ArrayList<Boolean>();
			displayList.add(new Boolean(true));
			displayList.add(new Boolean(getColumnViewOption(raceNameList[1], true)));
			displayList.add(new Boolean(getColumnViewOption(raceNameList[2], true)));
			displayList.add(new Boolean(getColumnViewOption(raceNameList[3], true)));
			displayList.add(new Boolean(getColumnViewOption(raceNameList[4], false)));
			displayList.add(new Boolean(getColumnViewOption(raceNameList[5], false)));
			displayList.add(new Boolean(getColumnViewOption(raceNameList[6], false)));
			displayList.add(new Boolean(getColumnViewOption(raceNameList[7], true)));
		}

		/**
		 * return the Class for a column
		 * @param column
		 * @return Class
		 **/
		public Class<?> getColumnClass(int column) {
			if (column == COL_NAME) {
				return TreeTableModel.class;
			}
			return String.class;
		}

		/**
		 * the number of columns
		 * @return column count
		 **/
		public int getColumnCount() {
			return raceNameList.length;
		}

		/**
		 * the name of each column (for the headers)
		 * @param column
		 * @return column name
		 **/
		public String getColumnName(int column) {
			return raceNameList[column];
		}

		/**
		 * return the root node
		 * @return root
		 **/
		public Object getRoot() {
			return (PObjectNode) super.getRoot();
		}

		/**
		 * return the value of a column
		 * @param node
		 * @param column
		 * @return value
		 **/
		public Object getValueAt(Object node, int column) {
			final PObjectNode fn = (PObjectNode) node;

			if (fn == null) {
				Logging.errorPrint("No active node when doing getValueAt in InfoRace");
				return null;
			}

			switch (column) {
				case COL_NAME:
					return getColumnName(fn);

				case COL_STAT:
					return getColumnStat(fn);

				case COL_PRE:
					return getColumnPre(fn);

				case COL_SIZE:
					return getColumnSize(fn);

				case COL_MOVE:
					return getColumnMove(fn);

				case COL_VISION:
					return getColumnVision(fn);

				case COL_CLASS:
					return getColumnClass(fn);

				case COL_LEVEL:
					return getColumnLevel(fn);

				default:
					Logging.errorPrint("In InfoRace.RaceModel.getValueAt the column " + column + " is not supported.");
					break;
			}
			return null;
		}

		private Object getColumnName(PObjectNode fn) {
			return fn.toString();
		}

		private Object getColumnStat(PObjectNode fn) {
			if (fn.getItem() instanceof Race) {
				Race race = (Race) fn.getItem();
				final StringBuffer retString = new StringBuffer();

				for (int i = 0; i < SettingsHandler.getGame().s_ATTRIBSHORT.length; i++) {
					if (race.isNonAbility(i)) {
						if (retString.length() > 0) {
							retString.append(' ');
						}

						retString.append(SettingsHandler.getGame().s_ATTRIBSHORT[i] + ":Nonability");
					}
					else {
						if (race.getStatMod(i, pc) != 0) {
							if (retString.length() > 0) {
								retString.append(' ');
							}

							retString.append(SettingsHandler.getGame().s_ATTRIBSHORT[i] + ":" + race.getStatMod(i, pc));
						}
					}
				}

				return retString.toString();
			}
			return null;
		}

		private Object getColumnPre(PObjectNode fn) {
			if (fn.getItem() instanceof Race) {
				Race race = (Race) fn.getItem();
				return race.preReqHTMLStrings(pc);
			}
			return null;
		}

		private Object getColumnSize(PObjectNode fn) {
			if (fn.getItem() instanceof Race) {
				Race race = (Race) fn.getItem();
				return race.getSize();
			}
			return null;
		}

		private Object getColumnMove(PObjectNode fn) {
			if (fn.getItem() instanceof Race) {
				Race race = (Race) fn.getItem();
				if (race.getMovement() != null) {
					return race.getMovement().toString();
				}
			}
			return null;
		}

		private Object getColumnVision(PObjectNode fn) {
			if (fn.getItem() instanceof Race) {
				Race race = (Race) fn.getItem();
				return race.getDisplayVision(pc);
			}
			return null;
		}

		private Object getColumnClass(PObjectNode fn) {
			if (fn.getItem() instanceof Race) {
				Race race = (Race) fn.getItem();
				return (!".".equals(race.getFavoredClass())) ? race.getFavoredClass() : PropertyFactory.getString("in_various");
			}
			return null;
		}

		private Object getColumnLevel(PObjectNode fn) {
			if (fn.getItem() instanceof Race) {
				Race race = (Race) fn.getItem();
				return new Integer(race.getLevelAdjustment(pc));
			}
			return null;
		}

		/**
		 * There must be a root node, but we keep it hidden
		 * @param aNode
		 **/
		private void setRoot(PObjectNode aNode) {
			super.setRoot(aNode);
		}

		private void resetModel(int mode) {
			// set the root node
			raceRoot = new PObjectNode();
			setRoot(raceRoot);

			switch(mode) {
				// races by name
				case GuiConstants.INFORACE_VIEW_NAME:
					buildNameView();
					break; // end VIEW_NAME

				case GuiConstants.INFORACE_VIEW_TYPE:
					buildTypeView();
					break; // end VIEW_TYPE

				case GuiConstants.INFORACE_VIEW_SOURCE:
					buildSourceView();
					break; // end VIEW_SOURCE

				case GuiConstants.INFORACE_VIEW_RACETYPE_NAME:
					buildRaceTypeView();
					break;

				case GuiConstants.INFORACE_VIEW_RACETYPE_SUBTYPE_NAME:
					buildRaceTypeSubTypeView();
					break;

				case GuiConstants.INFORACE_VIEW_ALL_TYPES:
					buildAllTypesView();
					break;

				default:
					Logging.errorPrint("In InfoRace.RaceModel.resetModel the mode " + mode + " is not supported.");
					break;
			} // end of switch(mode)

			PObjectNode rootAsPObjectNode = (PObjectNode) super.getRoot();

			if(rootAsPObjectNode.getChildCount() > 0) {
				fireTreeNodesChanged(super.getRoot(), new TreePath(super.getRoot()));
			}
		}

		private void buildNameView() 
		{
			List<Race> raceList = new ArrayList<Race>();
			String qFilter = this.getQFilter();

			// now loop through all the races and
			// see which ones are not filtered out
			for(Race aRace : Globals.getRaceMap().values()) 
			{
				if(accept(pc, aRace)) {
					if (qFilter == null ||
							( aRace.getDisplayName().toLowerCase().indexOf(qFilter) >= 0 ||
							  aRace.getType().toLowerCase().indexOf(qFilter) >= 0 ))
					raceList.add(aRace);
				}
			}

			PObjectNode[] rn = new PObjectNode[raceList.size()];

			// iterate through the race names
			// and fill out the tree
			for (int iName = 0; iName < raceList.size(); iName++) {
				final Race aRace = (Race) raceList.get(iName);

				if (aRace != null) {
					rn[iName] = new PObjectNode();
					rn[iName].setItem(aRace);
					rn[iName].setParent(raceRoot);
				}
			}

			// now add to the root node
			raceRoot.setChildren(rn);
		}

		private void buildTypeView() 
		{
			List<String> typeList = new ArrayList<String>();

			// now loop through all the races and
			// see which ones are not filtered out
			for (Race aRace :  Globals.getRaceMap().values()) 
			{
				if(accept(pc, aRace)) {
					if(!typeList.contains(aRace.getType())) {
						typeList.add(aRace.getType());
					}
				}
			}

			//build the TYPE root nodes
			PObjectNode[] rt = new PObjectNode[typeList.size()];

			// iterate through the race types
			// and fill out the tree
			for (int iType = 0; iType < typeList.size(); iType++) 
			{
				final String aType = (String) typeList.get(iType);
				rt[iType] = new PObjectNode();
				rt[iType].setItem(aType);

				for (Race aRace : Globals.getRaceMap().values()) 
				{
					if (aRace == null) {
						continue;
					}

					if (!aRace.getType().equals(aType)) {
						continue;
					}

					PObjectNode aFN = new PObjectNode();
					aFN.setItem(aRace);
					aFN.setParent(rt[iType]);
					rt[iType].addChild(aFN);
				}

				// if it's not empty, add it
				if (!rt[iType].isLeaf()) {
					rt[iType].setParent(raceRoot);
				}
			}

			// now add to the root node
			raceRoot.setChildren(rt);
		}

		private void buildSourceView() {
			List<String> sourceList = new ArrayList<String>();

			// now loop through all the races and
			// see which ones are not filtered out
			for(Race aRace : Globals.getRaceMap().values()) 
			{
				if(accept(pc, aRace)) {
					String aString = aRace.getSourceWithKey("LONG");
					if(aString != null && !sourceList.contains(aString) && aString.length() > 0)
					{
						sourceList.add(aString);
					}
				}
			}

			//build the SOURCE root nodes
			PObjectNode[] rs = new PObjectNode[sourceList.size()];

			// iterate through the race sources
			// and fill out the tree
			for (int iSource = 0; iSource < sourceList.size(); iSource++) {
				final String aSource = (String) sourceList.get(iSource);
				rs[iSource] = new PObjectNode();
				rs[iSource].setItem(aSource);

				for (Race aRace : Globals.getRaceMap().values()) 
				{
					if (aRace == null) {
						continue;
					}

					String aString = aRace.getSourceWithKey("LONG");
					if (aString != null && !aString.equals(aSource)) {
						continue;
					}

					PObjectNode aFN = new PObjectNode();
					aFN.setItem(aRace);
					aFN.setParent(rs[iSource]);
					rs[iSource].addChild(aFN);
				}

				// if it's not empty, add it
				if (!rs[iSource].isLeaf()) {
					rs[iSource].setParent(raceRoot);
				}
			}

			// now add to the root node
			raceRoot.setChildren(rs);
		}

		private void buildRaceTypeView()
		{
			List<String> typeList = new ArrayList<String>();

			// now loop through all the races and
			// see which ones are not filtered out
			for (Race aRace : Globals.getRaceMap().values())
			{
				if (accept(pc, aRace))
				{
					final String raceType = aRace.getRaceType();
					if (!typeList.contains(raceType))
					{
						typeList.add(raceType);
					}
				}
			}

			//build the TYPE root nodes
			PObjectNode[] rt = new PObjectNode[typeList.size()];

			// iterate through the race types
			// and fill out the tree
			for (int iType = 0; iType < typeList.size(); iType++)
			{
				final String aType = (String) typeList.get(iType);
				rt[iType] = new PObjectNode();
				rt[iType].setItem(aType);

				for (Race aRace : Globals.getRaceMap().values() )
				{
					if (aRace == null)
					{
						continue;
					}

					if (!aRace.getRaceType().equals(aType))
					{
						continue;
					}

					PObjectNode aFN = new PObjectNode();
					aFN.setItem(aRace);
					aFN.setParent(rt[iType]);
					rt[iType].addChild(aFN);
				}

				// if it's not empty, add it
				if (!rt[iType].isLeaf())
				{
					rt[iType].setParent(raceRoot);
				}
			}

			// now add to the root node
			raceRoot.setChildren(rt);
		}

		private void buildRaceTypeSubTypeView()
		{
			List<String> typeList = new ArrayList<String>();

			// now loop through all the races and
			// see which ones are not filtered out
			for (Race aRace : Globals.getRaceMap().values())
			{
				if (accept(pc, aRace))
				{
					final String raceType = aRace.getRaceType();
					if (!typeList.contains(raceType))
					{
						typeList.add(raceType);
					}
				}
			}

			//build the TYPE root nodes
			PObjectNode[] rt = new PObjectNode[typeList.size()];

			// iterate through the race types
			// and fill out the tree
			for (int iType = 0; iType < typeList.size(); iType++)
			{
				final String aType = (String) typeList.get(iType);
				rt[iType] = new PObjectNode();
				rt[iType].setItem(aType);

				HashMap<String, PObjectNode> subTypes = new HashMap<String, PObjectNode>();
				for (Race aRace : Globals.getRaceMap().values())
				{
					if (aRace == null)
					{
						continue;
					}

					if (!aRace.getRaceType().equals(aType))
					{
						continue;
					}

					List<String> raceSubTypes = aRace.getRacialSubTypes();
					if (raceSubTypes.size() > 0)
					{
						for (String subTypeName : raceSubTypes)
						{
							PObjectNode subTypeNode = (PObjectNode)subTypes.get(subTypeName);
							if (subTypeNode == null)
							{
								// We don't have this subtype at this level yet
								// so create a node for it.
								subTypeNode = new PObjectNode();
								subTypeNode.setItem(subTypeName);
								subTypeNode.setParent(rt[iType]);
								rt[iType].addChild(subTypeNode);

								// Dump it in the hashtable so we can find it
								// later.
								subTypes.put(subTypeName, subTypeNode);
							}
							PObjectNode raceNode = new PObjectNode();
							raceNode.setItem(aRace);
							raceNode.setParent(subTypeNode);
							subTypeNode.addChild(raceNode);
						}
					}
					else
					{
						PObjectNode aFN = new PObjectNode();
						aFN.setItem(aRace);
						aFN.setParent(rt[iType]);
						rt[iType].addChild(aFN);
					}
				}

				// if it's not empty, add it
				if (!rt[iType].isLeaf())
				{
					rt[iType].setParent(raceRoot);
				}
			}

			// now add to the root node
			raceRoot.setChildren(rt);
		}

		private void buildAllTypesView()
		{
			List<String> typeList = new ArrayList<String>();

			// now loop through all the races and
			// see which ones are not filtered out
			for (Race aRace : Globals.getRaceMap().values())
			{
				if (accept(pc, aRace))
				{
					final String raceType = aRace.getRaceType();
					if (!typeList.contains(raceType))
					{
						typeList.add(raceType);
					}
					final String type = aRace.getTypeUsingFlag(true);
					if (type.length() > 0)
					{
						StringTokenizer tok = new StringTokenizer(type, ".");
						while (tok.hasMoreTokens())
						{
							final String aType = tok.nextToken();
							if (!typeList.contains(aType))
							{
								typeList.add(aType);
							}
						}
					}
				}
			}

			//build the TYPE root nodes
			PObjectNode[] rt = new PObjectNode[typeList.size()];

			// iterate through the race types
			// and fill out the tree
			for (int iType = 0; iType < typeList.size(); iType++)
			{
				final String aType = (String) typeList.get(iType);
				rt[iType] = new PObjectNode();
				rt[iType].setItem(aType);

				for (Race aRace : Globals.getRaceMap().values())
				{
					if (aRace == null)
					{
						continue;
					}

					boolean typeMatch = false;
					if (aRace.getRaceType().equals(aType))
					{
						typeMatch = true;
					}
					if (aRace.getTypeUsingFlag(true).indexOf(aType) != -1)
					{
						StringTokenizer tok = new StringTokenizer(aRace.getTypeUsingFlag(true), ".");
						while (tok.hasMoreTokens())
						{
							final String type = tok.nextToken();
							if (aType.equals(type))
							{
								typeMatch = true;
							}
						}

					}
					if (typeMatch == false)
					{
						continue;
					}

					PObjectNode aFN = new PObjectNode();
					aFN.setItem(aRace);
					aFN.setParent(rt[iType]);
					rt[iType].addChild(aFN);
				}

				// if it's not empty, add it
				if (!rt[iType].isLeaf())
				{
					rt[iType].setParent(raceRoot);
				}
			}

			// now add to the root node
			raceRoot.setChildren(rt);
		}

		public List<String> getMColumnList() 
		{
			List<String> retList = new ArrayList<String>();
			for(int i = 1; i < raceNameList.length; i++) 
			{
				retList.add(raceNameList[i]);
			}
			return retList;
		}

		public boolean isMColumnDisplayed(int col) {
			return ((Boolean)displayList.get(col)).booleanValue();
		}

		public void setMColumnDisplayed(int col, boolean disp) {
			setColumnViewOption(raceNameList[col], disp);
			displayList.set(col, new Boolean(disp));
		}

		public int getMColumnOffset() {
			return 1;
		}

		public int getMColumnDefaultWidth(int col) {
			return SettingsHandler.getPCGenOption("InfoRaces.sizecol." + raceNameList[col], colDefaultWidth[col]);
		}

		public void setMColumnDefaultWidth(int col, int width) {
			SettingsHandler.setPCGenOption("InfoRaces.sizecol." + raceNameList[col], width);
		}

		private boolean getColumnViewOption(String colName, boolean defaultVal) {
			return SettingsHandler.getPCGenOption("InfoRaces.viewcol." + colName, defaultVal);
		}

		private void setColumnViewOption(String colName, boolean val) {
			SettingsHandler.setPCGenOption("InfoRaces.viewcol." + colName, val);
		}

		public void resetMColumn(int col, TableColumn column) {
			// TODO Auto-generated method stub

		}
	}

	private class RacePopupListener extends MouseAdapter
	{
		private JTree tree;
		private RacePopupMenu menu;

		RacePopupListener(JTreeTable treeTable, RacePopupMenu aMenu)
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
							final KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(e);

							for (int i = 0; i < menu.getComponentCount(); i++)
							{
								final Component menuComponent = menu.getComponent(i);

								if (menuComponent instanceof JMenuItem)
								{
									KeyStroke ks = ((JMenuItem) menuComponent).getAccelerator();

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
				selPath = tree.getClosestPathForLocation(evt.getX(), evt.getY());

				if (selPath == null)
				{
					return;
				}

				tree.setSelectionPath(selPath);
				menu.show(evt.getComponent(), evt.getX(), evt.getY());
			}
		}
	}

	private class RacePopupMenu extends JPopupMenu
	{
		static final long serialVersionUID = 2565545289875422981L;
		private String lastSearch = "";

		RacePopupMenu()
		{
			RacePopupMenu.this.add(createAddMenuItem(PropertyFactory.getString("in_select"), "shortcut EQUALS"));
			this.addSeparator();
			RacePopupMenu.this.add(Utility.createMenuItem("Find item",
					new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						lastSearch = raceTable.searchTree(lastSearch);
					}
				}, "searchItem", (char) 0, "shortcut F", "Find item", null, true));
		}

		private JMenuItem createAddMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new AddRaceActionListener(), PropertyFactory.getString("in_select"),
				'\0', accelerator, PropertyFactory.getString("in_irSelRaceTip"), "Add16.gif", true);
		}

		private class AddRaceActionListener extends RaceActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				selButton();
			}
		}

		private class RaceActionListener implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				// TODO This method currently does nothing?
			}
		}
	}
}
