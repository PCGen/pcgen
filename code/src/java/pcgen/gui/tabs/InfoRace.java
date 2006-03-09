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
 * Current Ver: $Revision: 1.64 $
 * Last Editor: $Author: karianna $
 * Last Edited: $Date: 2006/02/10 12:05:25 $
 *
 */
package pcgen.gui.tabs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
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
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.tree.TreePath;

import pcgen.core.Constants;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui.CharacterInfo;
import pcgen.gui.CharacterInfoTab;
import pcgen.gui.GuiConstants;
import pcgen.gui.PCGen_Frame1;
import pcgen.gui.filter.FilterAdapterPanel;
import pcgen.gui.filter.FilterConstants;
import pcgen.gui.filter.FilterFactory;
import pcgen.gui.panes.FlippingSplitPane;
import pcgen.gui.utils.AbstractTreeTableModel;
import pcgen.gui.utils.ClickHandler;
import pcgen.gui.utils.IconUtilitities;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.gui.utils.JLabelPane;
import pcgen.gui.utils.JTableEx;
import pcgen.gui.utils.JTreeTable;
import pcgen.gui.utils.JTreeTableMouseAdapter;
import pcgen.gui.utils.JTreeTableSorter;
import pcgen.gui.utils.LabelTreeCellRenderer;
import pcgen.gui.utils.PObjectNode;
import pcgen.gui.utils.ResizeColumnListener;
import pcgen.gui.utils.TableSorter;
import pcgen.gui.utils.TreeTableModel;
import pcgen.gui.utils.Utility;
import pcgen.gui.utils.WholeNumberField;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

/**
 *  <code>InfoRace</code> creates a new tabbed panel
 *  with all the race and template information on it
 *
 * @author  Bryan McRoberts (merton_monk@yahoo.com)
 * @version $Revision: 1.64 $
 **/
public class InfoRace extends FilterAdapterPanel implements CharacterInfoTab
{
	static final long serialVersionUID = 2565545289875422981L;
	private static int splitOrientation = JSplitPane.HORIZONTAL_SPLIT;
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
	private AllTemplatesTableModel allTemplatesDataModel = new AllTemplatesTableModel();
	private FlippingSplitPane asplit;
	private FlippingSplitPane bsplit;
	private FlippingSplitPane splitPane;
	private JButton btnAddHD = new JButton("+");
	private JButton btnRemoveHD = new JButton("-");
	private JButton leftButton;
	private JButton rightButton;
	private JButton selButton = new JButton(PropertyFactory.getString("in_select"));
	private JButton clearQFilterButton = new JButton("Clear");
	private JComboBoxEx viewComboBox = new JComboBoxEx();
	private final JLabel avaLabel = new JLabel(PropertyFactory.getString("in_irAvaTmpl"));
	private JLabel lblHDModify = new JLabel(PropertyFactory.getString("in_sumHDToAddRem"));
	private JLabel lblMonsterHD = new JLabel(PropertyFactory.getString("in_sumMonsterHitDice"));
	private final JLabel lblQFilter = new JLabel("QuickFilter:");
	private JLabel raceText = new JLabel();
	private JLabel raceTextLabel = new JLabel(PropertyFactory.getString("in_irSelectedRace"));
	private final JLabel selLabel = new JLabel(PropertyFactory.getString("in_irSelTmpl"));
	private JLabel sortLabel = new JLabel(PropertyFactory.getString("in_irSortRaces"));
	private JLabel txtMonsterHD = new JLabel("1");
	private JLabelPane infoLabel = new JLabelPane();
	private JPanel botPane = new JPanel();
	private JPanel pnlHD = new JPanel();
	private JPanel topPane = new JPanel();
	private JScrollPane allTemplatesPane;
	private JScrollPane currentTemplatesPane;
	private JTableEx allTemplatesTable;
	private JTableEx currentTemplatesTable;
	private JTextField textQFilter = new JTextField();
	private JTreeTable raceTable; // Races
	private JTreeTableSorter raceSort = null;

	// the list from which to pull the templates to use
	private List currentPCdisplayTemplates = new ArrayList(0);
	private PCTemplatesTableModel currentTemplatesDataModel = new PCTemplatesTableModel();
	private RaceModel raceModel = null; // Model for JTreeTable
	private TableSorter sortedAllTemplatesModel = new TableSorter();
	private TableSorter sortedCurrentTemplatesModel = new TableSorter();
	private TreePath selPath;

	// Monster HD Panel
	private WholeNumberField txtHD = new WholeNumberField(1, 3);
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
	public InfoRace(PlayerCharacter pc)
	{
		this.pc = pc;
		// do not change/remove this as we use the component's name
		// to save component specific settings
		setName(Constants.tabNames[Constants.TAB_RACES]);

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
		return SettingsHandler.getPCGenOption(".Panel.Race.Order", Constants.TAB_RACES);
	}

	public void setTabOrder(int order)
	{
		SettingsHandler.setPCGenOption(".Panel.Race.Order", order);
	}

	public String getTabName()
	{
		GameMode game = SettingsHandler.getGame();
		return game.getTabName(Constants.TAB_RACES);
	}

	public boolean isShown()
	{
		GameMode game = SettingsHandler.getGame();
		return game.getTabShown(Constants.TAB_RACES);
	}

	/**
	 * Retrieve the list of tasks to be done on the tab.
	 * @return List of task descriptions as Strings.
	 */
	public List getToDos()
	{
		List toDoList = new ArrayList();
		if (Globals.s_EMPTYRACE.equals(pc.getRace()) || pc.getRace() == null)
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
		allTemplatesDataModel.updateModel();
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

		if ((aRace != null) && !aRace.getName().startsWith("<none"))
		{
			b.append("<b>").append(aRace.piSubString()).append("</b>");
			b.append("<br><b>RACE TYPE</b>: ").append(aRace.getRaceType());
			List subTypes = aRace.getRacialSubTypes();
			if (subTypes.size() > 0)
			{
				b.append(" &nbsp;<b>SUBTYPES</b>: ");
				boolean first = true;
				for (Iterator i = subTypes.iterator(); i.hasNext(); )
				{
					if (!first)
					{
						b.append(", ");
					}
					b.append((String)i.next());
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
	 * <p>Handles the action from <code>rightButton</code>.  Adds the currently selected template
	 * to the character if the character is qualified.</p>
	 * <p>Forces update of all tabs by calling <code>forceUpdate()</code>, and updates
	 * <code>allTemplatesDataModel</code> to refresh template or other dependancies.</p>
	 */
	private void addTemplate()
	{
		if (allTemplatesTable.getSelectedRowCount() <= 0)
		{
			return;
		}

		pc.setDirty(true);

		PCTemplate theTmpl = allTemplatesDataModel.get(sortedAllTemplatesModel.getRowTranslated(
					allTemplatesTable.getSelectedRow()));

		if ((theTmpl != null) && theTmpl.isQualified(pc))
		{
			PCTemplate aTmpl = pc.getTemplateNamed(theTmpl.getName());

			if (aTmpl == null)
			{
				pc.addTemplate(theTmpl);
				pushUpdate();
				allTemplatesDataModel.updateModel();
			}
			else
			{
				JOptionPane.showMessageDialog(null, PropertyFactory.getString("in_irHaveTemplate"));
			}
		}

		currentTemplatesDataModel.setFilter(currentTemplatesDataModel.curFilter);
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
		int s = splitPane.getDividerLocation();
		int t = bsplit.getDividerLocation();
		int u = asplit.getDividerLocation();

		if (!hasBeenSized)
		{
			s = SettingsHandler.getPCGenOption("InfoRace.splitPane",
					(int) ((InfoRace.this.getSize().getWidth() * 75.0) / 100.0));
			t = SettingsHandler.getPCGenOption("InfoRace.bsplit", (int) (InfoRace.this.getSize().getHeight() - 120));
			u = SettingsHandler.getPCGenOption("InfoRace.asplit",
					(int) ((InfoRace.this.getSize().getWidth() * 75.0) / 100.0));

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

			// set the prefered width on allTemplatesTable
			for (int i = 0; i < allTemplatesTable.getColumnCount(); i++)
			{
				TableColumn sCol = allTemplatesTable.getColumnModel().getColumn(i);
				width = Globals.getCustColumnWidth("Tamplate", i);

				if (width != 0)
				{
					sCol.setPreferredWidth(width);
				}

				sCol.addPropertyChangeListener(new ResizeColumnListener(allTemplatesTable, "Tamplate", i));
			}

			if (s > 0)
			{
				hasBeenSized = true;
			}
		}

		if (s > 0)
		{
			splitPane.setDividerLocation(s);
			SettingsHandler.setPCGenOption("InfoRace.splitPane", s);
		}

		if (t > 0)
		{
			bsplit.setDividerLocation(t);
			SettingsHandler.setPCGenOption("InfoRace.bsplit", t);
		}

		if (u > 0)
		{
			asplit.setDividerLocation(u);
			SettingsHandler.setPCGenOption("InfoRace.asplit", u);
		}
	}

	private void hookupPopupMenu(JTreeTable treeTable)
	{
		treeTable.addMouseListener(new RacePopupListener(treeTable, new RacePopupMenu()));
	}

	private void hookupTemplatePopupMenu(JTableEx table)
	{
		table.addMouseListener(new TemplatePopupListener(table, new TemplatePopupMenu(table)));
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
					int s = splitPane.getDividerLocation();

					if (s > 0)
					{
						SettingsHandler.setPCGenOption("InfoRace.splitPane", s);
					}

					s = asplit.getDividerLocation();

					if (s > 0)
					{
						SettingsHandler.setPCGenOption("InfoRace.asplit", s);
					}

					s = bsplit.getDividerLocation();

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
		leftButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					removeTemplate();
				}
			});
		rightButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					addTemplate();
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

		FilterFactory.restoreFilterSettings(this);
		currentTemplatesDataModel.setFilter(currentTemplatesDataModel.curFilter);
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
		topPane.setLayout(new BorderLayout());

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JPanel leftPane = new JPanel();
		JPanel rightPane = new JPanel();
		leftPane.setLayout(gridbag);
		rightPane.setLayout(gridbag);
		splitPane = new FlippingSplitPane(splitOrientation, leftPane, rightPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerSize(10);

		topPane.add(splitPane, BorderLayout.CENTER);

		//-------------------------------------------------------------
		//  Top Left Pane
		//  - it will have all the races
		// Left Pane - Header
		// A weight of 4 and 96 instead of 5/95 like others to allow for the
		// gridBagLayout used for the Monster HD - to still allow the base header
		// without MonsterHD to match Class and Skills and Feat Tabs (headers)
		Utility.buildConstraints(c, 0, 0, 3, 1, 100, 5); //gx,gy,gw,gh,wx,wy
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;

		JPanel aPanel = new JPanel();
		gridbag.setConstraints(aPanel, c);

		// A new panel to hold base Race Selection and Sorting
		// A grid will be used to handle the "Monster HD Sub-panel"
		JPanel tPanel = new JPanel();
		tPanel.add(sortLabel);
		tPanel.add(viewComboBox);

		raceText.setPreferredSize(new Dimension(120, 25));
		raceText.setBorder(BorderFactory.createEtchedBorder());
		raceText.setHorizontalAlignment(SwingConstants.CENTER);

		//raceText.setBackground(Color.black);
		tPanel.add(raceTextLabel);
		tPanel.add(raceText);
		tPanel.add(selButton);

		aPanel.setLayout(new GridBagLayout());

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		aPanel.add(tPanel, c);

		// Monster HD Panel (Part 1)
		pnlHD.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnlHD.add(lblHDModify);
		pnlHD.add(txtHD);
		pnlHD.add(btnAddHD);
		pnlHD.add(btnRemoveHD);

		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 1;
		c.anchor = GridBagConstraints.EAST;
		c.weightx = 0.3;
		aPanel.add(pnlHD, c);

		// Monster HD Panel (Part 2)
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		aPanel.add(txtMonsterHD, c);

		// Monster HD Panel (Part 3)
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.EAST;
		aPanel.add(lblMonsterHD, c);

		// Monster HD Panel (Part 4 - Listeners)
		btnAddHD.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					PCGen_Frame1.addMonsterHD(1);
					updateHD();
				}
			});
		btnRemoveHD.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					PCGen_Frame1.addMonsterHD(-1);
					updateHD();
				}
			});

		leftPane.add(aPanel);

		Utility.buildConstraints(c, 0, 1, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.LINE_START;
		gridbag.setConstraints(lblQFilter, c);
		leftPane.add(lblQFilter);
		
		Utility.buildConstraints(c, 1, 1, 1, 1, 95, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LINE_START;
		gridbag.setConstraints(textQFilter, c);
		leftPane.add(textQFilter);
		
		Utility.buildConstraints(c, 2, 1, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.LINE_START;
		gridbag.setConstraints(clearQFilterButton, c);
		clearQFilterButton.setEnabled(false);
		leftPane.add(clearQFilterButton);

		// Left Pane - Data (Race Table)
		// build the available races panel
		Utility.buildConstraints(c, 0, 2, 3, 1, 100, 95);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;

		JScrollPane scrollPane = new JScrollPane(raceTable);
		gridbag.setConstraints(scrollPane, c);

		raceTable.setColAlign(3, SwingConstants.CENTER);
		raceTable.setColAlign(7, SwingConstants.CENTER);

		leftPane.add(scrollPane);

		//-------------------------------------------------------------
		//  Top Right Pane
		//  - Race Info
		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		rightPane.setLayout(gridbag);

		Utility.buildConstraints(c, 0, 0, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;

		JScrollPane rScroll = new JScrollPane();
		gridbag.setConstraints(rScroll, c);

		TitledBorder title1 = BorderFactory.createTitledBorder(PropertyFactory.getString("in_irRaceInfo"));
		title1.setTitleJustification(TitledBorder.CENTER);
		rScroll.setBorder(title1);
		infoLabel.setBackground(topPane.getBackground());
		rScroll.setViewportView(infoLabel);
		rightPane.add(rScroll);

		//-----------------------------------------------------------------------
		//  Bottom Panel
		//  - this has all the Template stuff in it
		//-----------------------------------------------------------------------
		botPane.setLayout(new BorderLayout());

		gridbag = new GridBagLayout();
		c = new GridBagConstraints();

		JPanel bLeftPane = new JPanel();
		JPanel bRightPane = new JPanel();
		bLeftPane.setLayout(gridbag);
		bRightPane.setLayout(gridbag);

		asplit = new FlippingSplitPane(JSplitPane.HORIZONTAL_SPLIT, bLeftPane, bRightPane);
		asplit.setOneTouchExpandable(true);
		asplit.setDividerSize(10);

		botPane.add(asplit, BorderLayout.CENTER);

		//-------------------------------------------------------------
		//  Bottom Left Pane
		//  - available templates
		// Header
		Utility.buildConstraints(c, 0, 0, 1, 1, 1, 1);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		aPanel = new JPanel();
		gridbag.setConstraints(aPanel, c);

		//avaLabel.setPreferredSize(new Dimension(150, 20));
		aPanel.add(avaLabel);

		ImageIcon newImage;
		newImage = IconUtilitities.getImageIcon("Forward16.gif");
		rightButton = new JButton(newImage);
		Utility.setDescription(rightButton, PropertyFactory.getString("in_irTemplAddTip"));
		rightButton.setEnabled(true);
		aPanel.add(rightButton);

		bLeftPane.add(aPanel);

		// Data - All Available Templates Table
		Utility.buildConstraints(c, 0, 1, 1, 1, 10, 10);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTH;
		allTemplatesTable = new JTableEx();
		allTemplatesPane = new JScrollPane(allTemplatesTable);
		gridbag.setConstraints(allTemplatesPane, c);

		MouseListener aml = new MouseAdapter()
			{
				public void mousePressed(MouseEvent e)
				{
					if (e.getClickCount() == 2)
					{
						addTemplate();
					}
				}
			};
		allTemplatesTable.addMouseListener(aml);

		sortedAllTemplatesModel.setModel(allTemplatesDataModel);
		allTemplatesTable.setModel(sortedAllTemplatesModel);
		allTemplatesPane.setViewportView(allTemplatesTable);
		allTemplatesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		hookupTemplatePopupMenu(allTemplatesTable);
		bLeftPane.add(allTemplatesPane);

		allTemplatesTable.setColAlign(0, SwingConstants.CENTER);
		allTemplatesTable.setColAlign(2, SwingConstants.CENTER);

		//-------------------------------------------------------------
		//  Bottom Right Pane
		//  - selected templates
		//  Header
		Utility.buildConstraints(c, 0, 0, 1, 1, 1, 1);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;

		JPanel bPanel = new JPanel();
		gridbag.setConstraints(bPanel, c);

		//selLabel.setPreferredSize(new Dimension(150, 20));
		bPanel.add(selLabel);
		newImage = IconUtilitities.getImageIcon("Back16.gif");
		leftButton = new JButton(newImage);
		Utility.setDescription(leftButton, PropertyFactory.getString("in_irTemplRemoveTip"));
		leftButton.setEnabled(true);
		bPanel.add(leftButton);
		bRightPane.add(bPanel);

		// Data - Selected Templates table
		Utility.buildConstraints(c, 0, 1, 1, 1, 10, 10);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTH;
		currentTemplatesPane = new JScrollPane();
		gridbag.setConstraints(currentTemplatesPane, c);

		currentTemplatesTable = new JTableEx();
		sortedCurrentTemplatesModel.setModel(currentTemplatesDataModel);
		sortedCurrentTemplatesModel.addMouseListenerToHeaderInTable(currentTemplatesTable);

		aml = new MouseAdapter()
			{
				public void mousePressed(MouseEvent e)
				{
					if (e.getClickCount() == 2)
					{
						removeTemplate();
					}
				}
			};
		currentTemplatesTable.addMouseListener(aml);

		currentTemplatesTable.setModel(sortedCurrentTemplatesModel);
		currentTemplatesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		currentTemplatesTable.setDoubleBuffered(false);
		currentTemplatesPane.setViewportView(currentTemplatesTable);
		hookupTemplatePopupMenu(currentTemplatesTable);
		bRightPane.add(currentTemplatesPane);

		//----------------------------------------------------------------------
		// now split the top and bottom panels
		bsplit = new FlippingSplitPane(JSplitPane.VERTICAL_SPLIT, topPane, botPane);
		bsplit.setOneTouchExpandable(true);
		bsplit.setDividerSize(10);

		// now add all the panes (centered of course)
		this.setLayout(new BorderLayout());
		this.add(bsplit, BorderLayout.CENTER);

		// add the sorter so that clicking on the TableHeader
		// actualy does something
		raceSort = new JTreeTableSorter(raceTable, (PObjectNode) raceModel.getRoot(), raceModel);

		addFocusListener(new FocusAdapter()
			{
				public void focusGained(FocusEvent evt)
				{
					refresh();
				}
			});
	}

	/**
	 * <p>Handles the action from <code>leftButton</code>.  Removes the currently selected template
	 * from the character if the template is removeable.</p>
	 * <p>Forces update of all tabs by calling <code>forceUpdate()</code>, and updates
	 * <code>allTemplatesDataModel</code> to refresh template or other dependancies.</p>
	 */
	private void removeTemplate()
	{
		if (currentTemplatesTable.getSelectedRowCount() <= 0)
		{
			return;
		}

		pc.setDirty(true);

		PCTemplate theTmpl = (PCTemplate) currentPCdisplayTemplates.get(sortedCurrentTemplatesModel.getRowTranslated(
					currentTemplatesTable.getSelectedRow()));

		if (!theTmpl.isRemovable())
		{
			ShowMessageDelegate.showMessageDialog(PropertyFactory.getString("in_irNotRemovable"), Constants.s_APPNAME, MessageType.ERROR);

			return;
		}

		pc.removeTemplate(theTmpl);
		pushUpdate();
		allTemplatesDataModel.updateModel();
		currentTemplatesDataModel.setFilter(currentTemplatesDataModel.curFilter);
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

			allTemplatesDataModel.updateModel();
			currentTemplatesDataModel.setFilter(currentTemplatesDataModel.curFilter);
			raceText.setText(pc.getRace().piString());
			raceText.setMinimumSize(new Dimension(120, 25));
			setInfoLabelText(pc.getRace());

			if (pnlHD.isVisible())
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
		lblMonsterHD.setVisible(SettingsHandler.hideMonsterClasses());
		txtMonsterHD.setVisible(SettingsHandler.hideMonsterClasses());
		lblHDModify.setVisible(SettingsHandler.hideMonsterClasses());
		pnlHD.setVisible(SettingsHandler.hideMonsterClasses());

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

		allTemplatesDataModel.updateModel();
		currentTemplatesDataModel.setFilter(currentTemplatesDataModel.curFilter);
		createModel();
		raceTable.updateUI();

		if (pnlHD.isVisible())
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

				final PCClass aClass = pc.getClassNamed(monsterClass);

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
	 *
	 * A TableModel to handle the full list of templates.
	 * It pulls its data straight from Globals.getTemplateList()
	 *
	 **/
	private final class AllTemplatesTableModel extends AbstractTableModel
	{
		static final long serialVersionUID = 2565545289875422981L;
		private List displayTemplates = new ArrayList();
		private final String[] ALL_TEMPLATES_COLUMN_NAMES = new String[]
			{
				PropertyFactory.getString("in_Q"), PropertyFactory.getString("in_nameLabel"),
				PropertyFactory.getString("in_lvlAdj"), PropertyFactory.getString("in_modifier"),
				PropertyFactory.getString("in_preReqs"), PropertyFactory.getString("in_source")
			};
		private int curFilter;
		private int prevGlobalTemplateCount;

		private AllTemplatesTableModel()
		{
			updateModel();
		}

		/**
		 * @param columnIndex the index of the column to retrieve
		 * @return the type of the specified column
		 */
		public Class getColumnClass(int columnIndex)
		{
			return String.class;
		}

		/**
		 * @return the number of columns
		 */
		public int getColumnCount()
		{
			return ALL_TEMPLATES_COLUMN_NAMES.length;
		}

		/**
		 * @param columnIndex the index of the column name to retrieve
		 * @return the name.. of the specified column
		 */
		public String getColumnName(int columnIndex)
		{
			return ((columnIndex >= 0) && (columnIndex < ALL_TEMPLATES_COLUMN_NAMES.length))
			? ALL_TEMPLATES_COLUMN_NAMES[columnIndex] : "Out Of Bounds";
		}

		/**
		 * @return the number of rows in the model
		 */
		public int getRowCount()
		{
			if (prevGlobalTemplateCount != Globals.getTemplateList().size())
			{
				updateFilter();
			}

			return (displayTemplates != null) ? displayTemplates.size() : 0;
		}

		/**
		 * @param rowIndex the row of the cell to retrieve
		 * @param columnIndex the column of the cell to retrieve
		 * @return the value of the cell
		 */
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			if (displayTemplates != null)
			{
				PCTemplate selectedTemplate = (PCTemplate) displayTemplates.get(rowIndex);
				final PCTemplate pcTemplate = pc.getTemplateNamed(selectedTemplate.toString());

				if (pcTemplate != null)
				{
					selectedTemplate = pcTemplate;
				}

				switch (columnIndex)
				{
					case 0:
						return selectedTemplate.isQualified(pc) ? "Y" : "N";

					case 1:
						return selectedTemplate.toString();

					case 2:
						return "" + selectedTemplate.getLevelAdjustment(pc);

					case 3:
						return selectedTemplate.modifierString(pc);

					case 4:
						return selectedTemplate.preReqStrings();

					case 5:
						return selectedTemplate.getSource();

					default:
						Logging.errorPrint("In InfoRace.AllTemplatesTableModel.getValueAt the column " + columnIndex
							+ " is not supported.");

						break;
				}
			}

			return null;
		}

		/**
		 * Uses the ID from the jcbFilter, so any change to the list of filters
		 * will require a modification of this method.
		 * at the moment:
		 * 0: All
		 * 1: Qualified
		 * @param filterID the filter type
		 */
		private void setFilter(int filterID)
		{
			prevGlobalTemplateCount = Globals.getTemplateList().size();
			displayTemplates = new ArrayList();

			switch (filterID)
			{
				case 0: // All

					for (Iterator it = Globals.getTemplateList().iterator(); it.hasNext();)
					{
						PCTemplate pcTmpl = (PCTemplate) it.next();

						if ((pcTmpl.isVisible() == 1) || (pcTmpl.isVisible() == 3))
						{
							displayTemplates.add(pcTmpl);
						}
					}

					break;

				case 1: // Qualified

					for (Iterator it = Globals.getTemplateList().iterator(); it.hasNext();)
					{
						PCTemplate pcTmpl = (PCTemplate) it.next();

						if (((pcTmpl.isVisible() == 1) || (pcTmpl.isVisible() == 3)) && pcTmpl.isQualified(pc))
						{
							displayTemplates.add(pcTmpl);
						}
					}

					break;

				default:
					Logging.errorPrint("In InfoRace.AllTemplatesTableModel.setFilter the filter ID " + filterID
						+ " is not supported.");

					break;
			}

			fireTableDataChanged();
			curFilter = filterID;
		}

		private PCTemplate get(int index)
		{
			return (PCTemplate) displayTemplates.get(index);
		}

		/**
		 * Re-fetches and re-filters the data from the global template list.
		 */
		private void updateFilter()
		{
			setFilter(curFilter);
		}

		private void updateModel()
		{
			displayTemplates.clear();

			for (Iterator it = Globals.getTemplateList().iterator(); it.hasNext();)
			{
				final PCTemplate aPCTemplate = (PCTemplate) it.next();

				if (((aPCTemplate.isVisible() % 2) == 1) && accept(pc, aPCTemplate))
				{
					displayTemplates.add(aPCTemplate);
				}
			}

			fireTableDataChanged();
		}
	}

	/**
	 * This is the model for currently selected templates
	 **/
	private final class PCTemplatesTableModel extends AbstractTableModel
	{
		static final long serialVersionUID = 2565545289875422981L;
		private int curFilter;
		private int prevGlobalTemplateCount;

		public Class getColumnClass(int columnIndex)
		{
			return String.class;
		}

		public int getColumnCount()
		{
			return 2;
		}

		public String getColumnName(int columnIndex)
		{
			switch (columnIndex)
			{
				case 0:
					return "Template";

				case 1:
					return "Removable";

				default:
					Logging.errorPrint("In InfoRace.PCTemplatesTableModel.getColumnName the column " + columnIndex
						+ " is not supported.");

					break;
			}

			return "Out Of Bounds";
		}

		public int getRowCount()
		{
			return currentPCdisplayTemplates.size();
		}

		public Object getValueAt(int rowIndex, int columnIndex)
		{
			if ((pc != null) && (pc.getTemplateList() != null))
			{
				PCTemplate t = (PCTemplate) currentPCdisplayTemplates.get(rowIndex);

				switch (columnIndex)
				{
					case 0:
						return t.toString();

					case 1:
						return (t.isRemovable() ? "Yes" : "No");

					default:
						Logging.errorPrint("In InfoRace.PCTemplatesTableModel.getValueAt the column " + columnIndex
							+ " is not supported.");

						break;
				}
			}

			return null;
		}

		/**
		 * Uses the ID from the jcbFilter, so any change to the list
		 * of filters will require a modification of this method.
		 * at the moment:
		 * 0: Visible
		 * 1: Invisible
		 * 2: All
		 * @param filterID the filter type
		 */
		private void setFilter(int filterID)
		{
			if (pc == null)
			{
				currentPCdisplayTemplates = new ArrayList(0);
			}
			else
			{
				prevGlobalTemplateCount = pc.getTemplateList().size();
				currentPCdisplayTemplates = new ArrayList(prevGlobalTemplateCount);

				switch (filterID)
				{
					case 0:

						for (Iterator it = pc.getTemplateList().iterator(); it.hasNext();)
						{
							final PCTemplate pcTmpl = (PCTemplate) it.next();

							if ((pcTmpl.isVisible() == 1) || (pcTmpl.isVisible() == 3))
							{
								currentPCdisplayTemplates.add(pcTmpl);
							}
						}

						break;

					case 1:

						for (Iterator it = pc.getTemplateList().iterator(); it.hasNext();)
						{
							final PCTemplate pcTmpl = (PCTemplate) it.next();

							if ((pcTmpl.isVisible() == 0) || (pcTmpl.isVisible() == 2))
							{
								currentPCdisplayTemplates.add(pcTmpl);
							}
						}

						break;

					case 2:
						currentPCdisplayTemplates.addAll(pc.getTemplateList());

						break;

					default:
						Logging.errorPrint("In InfoRace.PCTemplatesTableModel.setFilter the filter ID " + filterID
							+ " is not supported.");

						break;
				}
			}

			fireTableDataChanged();
			curFilter = filterID;
		}
	}

	private class TemplatePopupListener extends MouseAdapter
	{
		private JTableEx table;
		private TemplatePopupMenu menu;

		TemplatePopupListener(JTableEx aTable, TemplatePopupMenu aMenu)
		{
			table = aTable;
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

			table.addKeyListener(myKeyListener);
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
				java.awt.Point p = evt.getPoint();
				int rowIndex = table.rowAtPoint(p);
				table.setRowSelectionInterval(rowIndex, rowIndex);

				menu.show(evt.getComponent(), evt.getX(), evt.getY());
			}
		}
	}

	private class TemplatePopupMenu extends JPopupMenu
	{
		static final long serialVersionUID = 2565545289875422981L;

		TemplatePopupMenu(JTableEx table)
		{
			if (table == allTemplatesTable)
			{
				TemplatePopupMenu.this.add(createAddMenuItem(PropertyFactory.getString("in_irAddTemplate"), "shortcut EQUALS"));
			}
			else
			{
				TemplatePopupMenu.this.add(createRemoveMenuItem(PropertyFactory.getString("in_irRemoveTemplate"), "shortcut MINUS"));
			}
		}

		private JMenuItem createAddMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new AddTemplateActionListener(), PropertyFactory.getString("in_select"),
					'\0', accelerator, PropertyFactory.getString("in_irAddTemplateTip"), "Add16.gif", true);
		}

		private JMenuItem createRemoveMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new RemoveTemplateActionListener(), PropertyFactory.getString("in_select"),
					'\0', accelerator, PropertyFactory.getString("in_irRemoveTemplateTip"), "Remove16.gif", true);
		}

		private class AddTemplateActionListener extends TemplateActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				addTemplate();
			}
		}

		private class RemoveTemplateActionListener extends TemplateActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				removeTemplate();
			}
		}

		private class TemplateActionListener implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				// TODO This method currently does nothing?
			}
		}
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
	private final class RaceModel extends AbstractTreeTableModel {
		// this is the root node
		private PObjectNode raceRoot;

		// list of column names
		private final String[] raceNameList = {
			PropertyFactory.getString("in_nameLabel"), PropertyFactory.getString("in_irTableStat"),
			PropertyFactory.getString("in_preReqs"), PropertyFactory.getString("in_size"),
			PropertyFactory.getString("in_speed"), PropertyFactory.getString("in_vision"),
			PropertyFactory.getString("in_favoredClass"), PropertyFactory.getString("in_lvlAdj")
		};

		private RaceModel(int mode) {
			super(null);
			resetModel(mode);
		}

		/**
		 * return the Class for a column
		 * @param column
		 * @return Class
		 **/
		public Class getColumnClass(int column) {
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

		private void buildNameView() {
			List raceList = new ArrayList();
			String qFilter = this.getQFilter();

			// now loop through all the races and
			// see which ones are not filtered out
			for(Iterator it = Globals.getRaceMap().values().iterator(); it.hasNext(); ) {
				final Race aRace = (Race) it.next();

				if(accept(pc, aRace)) {
					if (qFilter == null || 
							( aRace.getName().toLowerCase().indexOf(qFilter) >= 0 ||
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

		private void buildTypeView() {
			List typeList = new ArrayList();

			// now loop through all the races and
			// see which ones are not filtered out
			for(Iterator it = Globals.getRaceMap().values().iterator(); it.hasNext(); ) {
				final Race aRace = (Race) it.next();

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
			for (int iType = 0; iType < typeList.size(); iType++) {
				final String aType = (String) typeList.get(iType);
				rt[iType] = new PObjectNode();
				rt[iType].setItem(aType);

				for (Iterator fI = Globals.getRaceMap().values().iterator(); fI.hasNext(); ) {
					final Race aRace = (Race) fI.next();

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
			List sourceList = new ArrayList();

			// now loop through all the races and
			// see which ones are not filtered out
			for(Iterator it = Globals.getRaceMap().values().iterator(); it.hasNext(); ) {
				final Race aRace = (Race) it.next();

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

				for (Iterator fI = Globals.getRaceMap().values().iterator(); fI.hasNext(); ) {
					final Race aRace = (Race) fI.next();

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
			List typeList = new ArrayList();

			// now loop through all the races and
			// see which ones are not filtered out
			for (Iterator it = Globals.getRaceMap().values().iterator(); it.hasNext(); )
			{
				final Race aRace = (Race) it.next();

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

				for (Iterator fI = Globals.getRaceMap().values().iterator(); fI.hasNext(); )
				{
					final Race aRace = (Race) fI.next();

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
			List typeList = new ArrayList();

			// now loop through all the races and
			// see which ones are not filtered out
			for (Iterator it = Globals.getRaceMap().values().iterator(); it.hasNext(); )
			{
				final Race aRace = (Race) it.next();

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

				HashMap subTypes = new HashMap();
				for (Iterator fI = Globals.getRaceMap().values().iterator(); fI.hasNext(); )
				{
					final Race aRace = (Race) fI.next();

					if (aRace == null)
					{
						continue;
					}

					if (!aRace.getRaceType().equals(aType))
					{
						continue;
					}

					List raceSubTypes = aRace.getRacialSubTypes();
					if (raceSubTypes.size() > 0)
					{
						for (Iterator i = raceSubTypes.iterator(); i.hasNext();)
						{
							String subTypeName = (String)i.next();
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
			List typeList = new ArrayList();

			// now loop through all the races and
			// see which ones are not filtered out
			for (Iterator it = Globals.getRaceMap().values().iterator(); it.hasNext(); )
			{
				final Race aRace = (Race) it.next();

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

				for (Iterator fI = Globals.getRaceMap().values().iterator(); fI.hasNext(); )
				{
					final Race aRace = (Race) fI.next();

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
