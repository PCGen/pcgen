/*
 * InfoKnownSpells.java
 * Copyright 2006 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on Jan 4, 2006
 *
 * $Id:  $
 *
 */
package pcgen.gui.tabs.spells;

import java.awt.BorderLayout;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableColumn;
import javax.swing.tree.TreePath;

import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.SpellBook;
import pcgen.core.character.SpellInfo;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui.GuiConstants;
import pcgen.gui.PCGen_Frame1;
import pcgen.gui.TableColumnManager;
import pcgen.gui.filter.FilterFactory;
import pcgen.gui.panes.FlippingSplitPane;
import pcgen.gui.utils.IconUtilitities;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.gui.utils.JTreeTableSorter;
import pcgen.gui.utils.PObjectNode;
import pcgen.gui.utils.ResizeColumnListener;
import pcgen.gui.utils.Utility;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

/**
 * <code>InfoSpellBooks</code> is responsible for the display of the list of
 * a character's spell books and the provision of an interface to let the 
 * user create and update spell books for their character.
 *
 * Last Editor: $Author:  $
 * Last Edited: $Date:  $
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: $
 */

public class InfoSpellBooks extends InfoSpellsSubTab
{
	private final JLabel avaLabel = new JLabel(PropertyFactory
		.getString("InfoSpellBooks.sort.avail.spells.by")); //$NON-NLS-1$
	private final JLabel selLabel = new JLabel(PropertyFactory
		.getString("InfoSpellBooks.sort.select.spells.by")); //$NON-NLS-1$
	private FlippingSplitPane asplit;
	private FlippingSplitPane bsplit;
	private FlippingSplitPane splitPane;

	private JButton addSpellButton;
	private JButton delSpellButton;

	private JTextField spellBookNameText = new JTextField();
	private JButton addSpellListButton;
	private JButton delSpellListButton;
	
	private JComboBoxEx primaryViewComboBox = new JComboBoxEx();
	private JComboBoxEx secondaryViewComboBox = new JComboBoxEx();
	private JComboBoxEx primaryViewSelectComboBox = new JComboBoxEx();
	private JComboBoxEx secondaryViewSelectComboBox = new JComboBoxEx();
	private JComboBoxEx selectFromComboBox = new JComboBoxEx();

	private JPanel botPane = new JPanel();
	private JPanel topPane = new JPanel();
	private boolean hasBeenSized = false;

	private int selectFromMode = 0;

	/**
	 *  Constructor for the InfoSpellBooks object
	 * 
	 * @param pc The character this tab is being created to display.
	 */
	public InfoSpellBooks(PlayerCharacter pc)
	{
		super(pc, Constants.TAB_SPELLBOOKS);

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				initComponents();
				initActionListeners();
			}
		});
	}

	/**
	 * @see pcgen.gui.CharacterInfoTab#getTabOrder()
	 */
	public int getTabOrder()
	{
		return SettingsHandler.getPCGenOption(".Panel.Spells.Books.Order", //$NON-NLS-1$
			Constants.TAB_KNOWN_SPELLS);
	}

	/**
	 * @see pcgen.gui.CharacterInfoTab#setTabOrder(int)
	 */
	public void setTabOrder(int order)
	{
		SettingsHandler.setPCGenOption(".Panel.Spells.Books.Order", order); //$NON-NLS-1$
	}

	/**
	 * @see pcgen.gui.CharacterInfoTab#getToDos()
	 */
	public List getToDos()
	{
		List toDoList = new ArrayList();
		return toDoList;
	}

	/**
	 * This recalculates the states of everything based
	 * upon the currently selected character.
	 */
	protected void updateCharacterInfo()
	{
		lastClass = ""; //$NON-NLS-1$

		if ((pc == null) || !needsUpdate)
		{
			return;
		}

		pc.getSpellList();
		updateBookList();

		updateAvailableModel();
		updateSelectedModel();

		classLabel.setText(""); //$NON-NLS-1$

		needsUpdate = false;
	}
	
	/**
	 * This is called when the tab is shown.
	 */
	protected void formComponentShown()
	{
		requestFocus();
		PCGen_Frame1.setMessageAreaTextWithoutSaving(""); //$NON-NLS-1$

		refresh();

		int divLocSplitPane = splitPane.getDividerLocation();
		int divLocVert = bsplit.getDividerLocation();
		int divLocHoriz = asplit.getDividerLocation();
		int width;

		if (!hasBeenSized)
		{
			hasBeenSized = true;
			divLocSplitPane = SettingsHandler.getPCGenOption("InfoSpellBooks.splitPane", //$NON-NLS-1$
				(int) ((this.getSize().getWidth() * 2) / 10));
			divLocVert = SettingsHandler.getPCGenOption("InfoSpellBooks.bsplit", //$NON-NLS-1$
				(int) (this.getSize().getHeight() - 101));
			divLocHoriz = SettingsHandler.getPCGenOption("InfoSpellBooks.asplit", //$NON-NLS-1$
				(int) (this.getSize().getWidth() - 300)); 

			// set the prefered width on selectedTable
			for (int i = 0; i < selectedTable.getColumnCount(); ++i)
			{
				TableColumn sCol = selectedTable.getColumnModel().getColumn(i);
				width = Globals.getCustColumnWidth("SpellSel", i); //$NON-NLS-1$

				if (width != 0)
				{
					sCol.setPreferredWidth(width);
				}

				sCol.addPropertyChangeListener(new ResizeColumnListener(
					selectedTable, "SpellSel", i)); //$NON-NLS-1$
			}

			// set the prefered width on availableTable
			for (int i = 0; i < availableTable.getColumnCount(); ++i)
			{
				TableColumn sCol = availableTable.getColumnModel().getColumn(i);
				width = Globals.getCustColumnWidth("SpellAva", i); //$NON-NLS-1$

				if (width != 0)
				{
					sCol.setPreferredWidth(width);
				}

				sCol.addPropertyChangeListener(new ResizeColumnListener(
					availableTable, "SpellAva", i)); //$NON-NLS-1$
			}
		}

		if (divLocSplitPane > 0)
		{
			splitPane.setDividerLocation(divLocSplitPane);
			SettingsHandler.setPCGenOption("InfoSpellBooks.splitPane", divLocSplitPane); //$NON-NLS-1$
		}

		if (divLocVert > 0)
		{
			bsplit.setDividerLocation(divLocVert);
			SettingsHandler.setPCGenOption("InfoSpellBooks.bsplit", divLocVert); //$NON-NLS-1$
		}

		if (divLocHoriz > 0)
		{
			asplit.setDividerLocation(divLocHoriz);
			SettingsHandler.setPCGenOption("InfoSpellBooks.asplit", divLocHoriz); //$NON-NLS-1$
		}
	}


	/**
	 * @see pcgen.gui.tabs.spells.InfoSpellsSubTab#initActionListeners()
	 */
	protected void initActionListeners()
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
					SettingsHandler.setPCGenOption("InfoSpellBooks.splitPane", s); //$NON-NLS-1$
				}

				s = asplit.getDividerLocation();

				if (s > 0)
				{
					SettingsHandler.setPCGenOption("InfoSpellBooks.asplit", s); //$NON-NLS-1$
				}

				s = bsplit.getDividerLocation();

				if (s > 0)
				{
					SettingsHandler.setPCGenOption("InfoSpellBooks.bsplit", s); //$NON-NLS-1$
				}
			}
		});
		addSpellButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addSpellButton();
			}
		});
		delSpellButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				delSpellButton();
			}
		});
		primaryViewComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				primaryViewComboBoxActionPerformed(primaryViewComboBox
					.getSelectedIndex());
			}
		});
		secondaryViewComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				secondaryViewComboBoxActionPerformed(secondaryViewComboBox
					.getSelectedIndex());
			}
		});
		primaryViewSelectComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				primaryViewSelectComboBoxActionPerformed(primaryViewSelectComboBox
					.getSelectedIndex());
			}
		});
		secondaryViewSelectComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				secondaryViewSelectComboBoxActionPerformed(secondaryViewSelectComboBox
					.getSelectedIndex());
			}
		});
		selectFromComboBox.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					selectFromComboBoxActionPerformed(selectFromComboBox
						.getSelectedIndex());
				}
			});
//		spellBookNameText.addActionListener(new ActionListener()
//			{
//				public void actionPerformed(ActionEvent evt)
//				{
//					addBookButton();
//				}
//			});
//		addSpellListButton.addActionListener(new ActionListener()
//			{
//				public void actionPerformed(ActionEvent evt)
//				{
//					addBookButton();
//				}
//			});
//		delSpellListButton.addActionListener(new ActionListener()
//			{
//				public void actionPerformed(ActionEvent evt)
//				{
//					delBookButton();
//				}
//			});

		FilterFactory.restoreFilterSettings(this);
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 **/
	protected void initComponents()
	{
		readyForRefresh = true;

		updateBookList();

		//
		// View List Sanity check
		//
		sanityCheckAvailableSpellMode(SettingsHandler
			.getSpellsTab_AvailableListMode());
		SettingsHandler.setSpellsTab_AvailableListMode(primaryViewMode);

		sanityCheckSelectedSpellMode(SettingsHandler
			.getSpellsTab_SelectedListMode());
		SettingsHandler.setSpellsTab_SelectedListMode(primaryViewSelectMode);

		// Configure the sort order combo boxes
		populateViewCombo(primaryViewComboBox, primaryViewMode, false);
		Utility.setDescription(primaryViewComboBox, PropertyFactory
			.getString("InfoSpells.change.how.spell.are.listed")); //$NON-NLS-1$
		populateViewCombo(secondaryViewComboBox, secondaryViewMode, true);

		populateViewCombo(primaryViewSelectComboBox, primaryViewSelectMode, false);
		Utility.setDescription(primaryViewSelectComboBox, PropertyFactory
			.getString("InfoSpells.change.how.spells.in.table.listed")); //$NON-NLS-1$
		populateViewCombo(secondaryViewSelectComboBox, secondaryViewSelectMode, true);
		Utility.setDescription(secondaryViewSelectComboBox, PropertyFactory
			.getString("InfoSpells.change.how.spells.in.table.listed")); //$NON-NLS-1$

		// Populate the select from combo box
		selectFromComboBox.addItem(PropertyFactory.getString("InfoSpellBooks.select.known")); //$NON-NLS-1$
		selectFromComboBox.addItem(PropertyFactory.getString("InfoSpellBooks.select.own.list")); //$NON-NLS-1$
		//TODO: The following option has been temporarily deactivated as adding spells from other classes doesn't work currently.
		// The spell storage code needs to be changed to have a character's spell book contents not be class dependant.
		//selectFromComboBox.addItem(PropertyFactory.getString("InfoSpellBooks.select.full.list")); //$NON-NLS-1$
		selectFromComboBox.setSelectedIndex(0);

		ImageIcon newImage;
		newImage = IconUtilitities.getImageIcon("Forward16.gif"); //$NON-NLS-1$
		addSpellButton = new JButton(newImage);
		newImage = IconUtilitities.getImageIcon("Back16.gif"); //$NON-NLS-1$
		delSpellButton = new JButton(newImage);

		// flesh out all the tree views
		createModels();

		// create tables associated with the above trees
		createTreeTables();
		
		List colNameList = new ArrayList();
		colNameList.add("School");
		colNameList.add("Descriptor");
		colNameList.add("Source File");
		List colActiveList = new ArrayList();
		colActiveList.add(new Boolean(true));
		colActiveList.add(new Boolean(true));
		colActiveList.add(new Boolean(true));
		
 		// Build the Top Panel
		buildTopPanel();

		// Build Bottom Panel
		initBottomPanel();

		// now split the top and bottom Panels
		bsplit = new FlippingSplitPane(JSplitPane.VERTICAL_SPLIT, topPane,
			botPane);
		bsplit.setOneTouchExpandable(true);
		bsplit.setDividerSize(10);

		// now add the entire mess (centered of course)
		this.setLayout(new BorderLayout());
		this.add(bsplit, BorderLayout.CENTER);

		// make sure we update when switching tabs
		this.addFocusListener(new FocusAdapter()
		{
			public void focusGained(FocusEvent evt)
			{
				refresh();
			}
		});

		// add the sorter tables so that clicking on the TableHeader
		// actually does something
		availableSort = new JTreeTableSorter(availableTable,
			(PObjectNode) availableModel.getRoot(), availableModel);
		selectedSort = new JTreeTableSorter(selectedTable,
			(PObjectNode) selectedModel.getRoot(), selectedModel);
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

		JPanel leftPane = new JPanel();
		JPanel rightPane = new JPanel();
		leftPane.setLayout(new BorderLayout());
		rightPane.setLayout(new BorderLayout());
		splitPane = new FlippingSplitPane(splitOrientation, leftPane, rightPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerSize(10);

		topPane.add(splitPane, BorderLayout.CENTER);

		JPanel controlsPanel = new JPanel();
		controlsPanel.setLayout(new BorderLayout());
		JPanel aPanel = new JPanel();
		aPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 1));
		aPanel.add(new JLabel(PropertyFactory
			.getString("InfoSpellBooks.select.from"))); //$NON-NLS-1$
		aPanel.add(selectFromComboBox);
		controlsPanel.add(aPanel, BorderLayout.NORTH);
		aPanel = new JPanel();
		aPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 1));
		aPanel.add(avaLabel);
		aPanel.add(primaryViewComboBox);
		aPanel.add(secondaryViewComboBox);
		controlsPanel.add(aPanel, BorderLayout.SOUTH);

		Utility.setDescription(controlsPanel, PropertyFactory.getString("InfoSpells.rightclick.add.to.spellbooks")); //$NON-NLS-1$
		leftPane.add(controlsPanel, BorderLayout.NORTH);

		JScrollPane scrollPane = new JScrollPane(availableTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		leftPane.add(scrollPane, BorderLayout.CENTER);

		JButton columnButton = new JButton();
		scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, columnButton);
		columnButton.setText("^");
		new TableColumnManager(availableTable, columnButton, availableModel);

		leftPane.add(buildModSpellPanel(addSpellButton, "InfoSpells.add.selected"), BorderLayout.SOUTH);
		
		JPanel sPanel = new JPanel();
		sPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 1));
		sPanel.add(selLabel);
		sPanel.add(primaryViewSelectComboBox);
		sPanel.add(secondaryViewSelectComboBox);
		rightPane.add(sPanel, BorderLayout.NORTH);

		scrollPane = new JScrollPane(selectedTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		selectedTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		selectedTable.setShowHorizontalLines(true);
		rightPane.add(scrollPane, BorderLayout.CENTER);

		JButton columnButton2 = new JButton();
		scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, columnButton2);
		columnButton2.setText("^");
		new TableColumnManager(selectedTable, columnButton2, selectedModel);

		rightPane.add(buildModSpellPanel(delSpellButton, "InfoSpells.remove.selected"), BorderLayout.SOUTH);
	}

	/**
	 * Build the panel with the controls to add a spell to a 
	 * prepared list.
	 *  
	 * @return The panel.
	 */
	private JPanel buildModSpellPanel(JButton button, String title)
	{
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 1));
		Utility.setDescription(button, PropertyFactory.getString(title)); //$NON-NLS-1$
		button.setEnabled(false);
		button.setMargin(new Insets(1, 14, 1, 14));
		panel.add(button);

		return panel;
	}

	/**
	 * Build the panel with the controls to select a 
	 * prepared list.
	 *  
	 * @return The panel.
	 */
	private JPanel buildSpellListPanel()
	{
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(2, 2, 2, 2);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTH;

		// 
		JPanel slPanel = new JPanel(new GridBagLayout());

		JLabel prepListLabel = new JLabel(PropertyFactory
			.getString("InfoPreparedSpells.preparedList")); //$NON-NLS-1$
		Utility.buildConstraints(c, 0, 0, 1, 1, 0.0, 0.0);
		slPanel.add(prepListLabel, c);
		spellBookNameText.setEditable(true);
		spellBookNameText.setPreferredSize(new Dimension(100, 20));
		Utility.buildConstraints(c, 1, 0, 1, 1, 1.0, 0.0);
		c.fill = GridBagConstraints.HORIZONTAL;
		slPanel.add(spellBookNameText, c);

		addSpellListButton = new JButton(PropertyFactory.getString("InfoSpells.add")); //$NON-NLS-1$
		Utility.setDescription(addSpellListButton, PropertyFactory
			.getString("InfoPreparedSpells.add.list")); //$NON-NLS-1$
		Utility.buildConstraints(c, 2, 0, 1, 1, 0.0, 0.0);
		c.fill = GridBagConstraints.NONE;
		slPanel.add(addSpellListButton, c);

		delSpellListButton = new JButton(PropertyFactory.getString("InfoSpells.delete")); //$NON-NLS-1$
		Utility.setDescription(delSpellListButton, PropertyFactory
			.getString("InfoPreparedSpells.del.list")); //$NON-NLS-1$
		Utility.buildConstraints(c, 3, 0, 1, 1, 0.0, 0.0);
		slPanel.add(delSpellListButton, c);

		return slPanel;
	}

	/**
	 * Build Bottom Panel. 
	 * botPane will contain a bLeftPane and a bRightPane
	 * bLeftPane will contain a scrollregion (spell info)
	 * bRightPane will contain a scrollregion (character Info)
  */
	private void initBottomPanel()
	{
		GridBagLayout gridbag;
		GridBagConstraints c;

		botPane.setLayout(new BorderLayout());

		gridbag = new GridBagLayout();
		c = new GridBagConstraints();

		JPanel bLeftPane = new JPanel();
		JPanel bRightPane = new JPanel();
		bLeftPane.setLayout(gridbag);
		bRightPane.setLayout(gridbag);

		asplit = new FlippingSplitPane(JSplitPane.HORIZONTAL_SPLIT, bLeftPane,
			bRightPane);
		asplit.setOneTouchExpandable(true);
		asplit.setDividerSize(10);

		botPane.add(asplit, BorderLayout.CENTER);

		// create a spell info scroll area
		Utility.buildConstraints(c, 0, 0, 1, 1, 1, 1, GridBagConstraints.BOTH,
			GridBagConstraints.CENTER);

		JScrollPane sScroll = new JScrollPane();
		gridbag.setConstraints(sScroll, c);

		TitledBorder sTitle = BorderFactory.createTitledBorder(PropertyFactory
			.getString("InfoSpells.spell.info")); //$NON-NLS-1$
		sTitle.setTitleJustification(TitledBorder.CENTER);
		sScroll.setBorder(sTitle);
		infoLabel.setBackground(topPane.getBackground());
		sScroll.setViewportView(infoLabel);
		bLeftPane.add(sScroll);

		// create a class info scroll area
		Utility.buildConstraints(c, 0, 0, 1, 1, 1, 1, GridBagConstraints.BOTH,
			GridBagConstraints.EAST);

		JScrollPane iScroll = new JScrollPane();

		TitledBorder iTitle = BorderFactory.createTitledBorder(PropertyFactory
			.getString("InfoSpells.class.info")); //$NON-NLS-1$
		iTitle.setTitleJustification(TitledBorder.CENTER);
		iScroll.setBorder(iTitle);
		classLabel.setBackground(topPane.getBackground());
		iScroll.setViewportView(classLabel);
		iScroll
			.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		gridbag.setConstraints(iScroll, c);
		bRightPane.add(iScroll);
	}

	/**
	 * @see pcgen.gui.tabs.spells.InfoSpellsSubTab#updateBookList()
	 */
	protected void updateBookList()
	{
		availableBookList.clear();
		availableBookList.add(Globals.getDefaultSpellBook());

		selectedBookList.clear();
		for (Iterator iBook = pc.getSpellBooks().iterator(); iBook.hasNext();)
		{
			// build spell book list
			String bookName = (String) iBook.next();
			SpellBook book = pc.getSpellBookByName(bookName);
			if (book.getType() == SpellBook.TYPE_SPELL_BOOK
				&& !selectedBookList.contains(bookName))
			{
				selectedBookList.add(bookName);
			}
		}
		
	}

	/**
	 * @see pcgen.gui.tabs.spells.InfoSpellsSubTab#createAvailableModel()
	 */
	protected void createAvailableModel()
	{
		if (availableModel == null)
		{
			availableModel = new SpellModel(primaryViewMode, secondaryViewMode,
				true, availableBookList, currSpellBook, selectFromMode, pc,
				this, ""); //$NON-NLS-1$
		}
		else
		{
			availableModel.resetModel(primaryViewMode, secondaryViewMode, true,
				availableBookList, currSpellBook, selectFromMode, this, ""); //$NON-NLS-1$
			if (currSpellBook.equals("")) //$NON-NLS-1$
			{
				currSpellBook = Globals.getDefaultSpellBook();
			}
		}

		if (availableSort != null)
		{
			availableSort.setRoot((PObjectNode) availableModel.getRoot());
			availableSort.sortNodeOnColumn();
		}
	}

	/**
	 * @see pcgen.gui.tabs.spells.InfoSpellsSubTab#createSelectedModel()
	 */
	protected void createSelectedModel()
	{
		if (selectedModel == null)
		{
			selectedModel = new SpellModel(primaryViewSelectMode,
				secondaryViewSelectMode, false, selectedBookList,
				currSpellBook, GuiConstants.INFOSPELLS_AVAIL_KNOWN, pc, this,
				PropertyFactory.getString("InfoSpellBooks.no.selected.help"));
		}
		else
		{
			selectedModel.resetModel(primaryViewSelectMode,
				secondaryViewSelectMode, false, selectedBookList,
				currSpellBook, GuiConstants.INFOSPELLS_AVAIL_KNOWN, this,
				PropertyFactory.getString("InfoSpellBooks.no.selected.help"));
			if (currSpellBook.equals("")) //$NON-NLS-1$
			{
				currSpellBook = Globals.getDefaultSpellBook();
			}
		}

		if (selectedSort != null)
		{
			selectedSort.setRoot((PObjectNode) selectedModel.getRoot());
			selectedSort.sortNodeOnColumn();
		}
	}

	/**
	 * @see pcgen.gui.tabs.spells.InfoSpellsSubTab#setSelectedSpell(pcgen.gui.utils.PObjectNode, boolean)
	 */
	protected void setSelectedSpell(PObjectNode fNode, boolean availSpell)
	{
		if (fNode == null)
		{
			lastSpell = null;
			infoLabel.setText();

			return;
		}

		if (fNode.getItem() instanceof SpellInfo)
		{
			CharacterSpell spellA = ((SpellInfo) fNode.getItem()).getOwner();

			if (spellA.getSpell() != null)
			{
				if (availSpell)
				{
					addSpellButton.setEnabled(true);
					addMenu.setEnabled(true);
				}
				else
				{
					delSpellButton.setEnabled(true);
					delSpellMenu.setEnabled(true);
				}
				setInfoLabelText((SpellInfo) fNode.getItem());
			}
		}
		else
		{
			if (availSpell)
			{
				addSpellButton.setEnabled(false);
				addMenu.setEnabled(false);
			}
			else
			{
				delSpellButton.setEnabled(false);
				delSpellMenu.setEnabled(false);
			}
			
			if (fNode.getItem() instanceof SpellBook)
			{
				SpellBook book = (SpellBook) fNode.getItem();
				if (!availSpell)
				{
					spellBookNameText.setText(book.getName());
					setInfoLabelText(book);
				}
			}
		}
	}

	/**
	 * @see pcgen.gui.tabs.spells.InfoSpellsSubTab#addSpellButton()
	 */
	protected void addSpellButton()
	{
		TreePath selCPath = selectedTable.getTree().getSelectionPath();
		String bookName;

		if (selCPath == null)
		{
			bookName = "";
		}
		else
		{
			bookName = getBookName((PObjectNode) selCPath.getPathComponent(1));
		}

		if (bookName.length() <= 0 || pc.getSpellBookByName(bookName) == null)
		{
			ShowMessageDelegate
				.showMessageDialog(
					PropertyFactory
						.getString("InfoSpells.first.select.spellbook"), Constants.s_APPNAME, MessageType.ERROR); //$NON-NLS-1$

			return; // need to select a spellbook
		}

		if (!(primaryViewMode == GuiConstants.INFOSPELLS_VIEW_CLASS || secondaryViewMode == GuiConstants.INFOSPELLS_VIEW_CLASS)
			|| !(primaryViewMode == GuiConstants.INFOSPELLS_VIEW_LEVEL || secondaryViewMode == GuiConstants.INFOSPELLS_VIEW_LEVEL))
		{
			ShowMessageDelegate
				.showMessageDialog(
					PropertyFactory
						.getString("InfoSpells.can.only.add.by.class.level"), Constants.s_APPNAME, MessageType.ERROR); //$NON-NLS-1$
			return; // need to select class/level or level/class as sorters
		}

		currSpellBook = bookName;

		TreePath[] avaCPaths = availableTable.getTree().getSelectionPaths();

		for (int index = avaCPaths.length - 1; index >= 0; --index)
		{
			Object aComp = avaCPaths[index].getLastPathComponent();
			PObjectNode fNode = (PObjectNode) aComp;

			addSpellToTarget(fNode, bookName);
		}

		pc.setDirty(true);

		// reset selected spellbook model
		updateSelectedModel();
	}
	
	/**
	 * Set the current book as the one to automatically add known 
	 * spells to.
	 *  
	 * @see pcgen.gui.tabs.spells.InfoSpellsSubTab#setAutoBookButton()
	 */
	protected void setAutoBookButton()
	{
		TreePath selCPath = selectedTable.getTree().getSelectionPath();
		String bookName;

		if (selCPath == null)
		{
			bookName = "";
		}
		else
		{
			bookName = getBookName((PObjectNode) selCPath.getPathComponent(1));
		}

		if (bookName.length() <= 0)
		{
			ShowMessageDelegate
				.showMessageDialog(
					PropertyFactory
						.getString("InfoSpells.first.select.spellbook"), Constants.s_APPNAME, MessageType.ERROR); //$NON-NLS-1$

			return; // need to select a spellbook
		}
		
		if (bookName.equals(pc.getSpellBookNameToAutoAddKnown()))
		{
			pc.setSpellBookNameToAutoAddKnown("");
		}
		else
		{
			pc.setSpellBookNameToAutoAddKnown(bookName);
		}
	}

	/**
	 * Gets the name of the spell book at the supplied node.
	 * @param node The node to get the name of
	 * @return The name of the spell book
	 */
	String getBookName(PObjectNode node)
	{
		String bookName;
		if (node.getItem() instanceof SpellBook)
		{
			SpellBook book = (SpellBook) node.getItem();
			bookName = book.getName();
		}
		else
		{
			bookName = String.valueOf(node.getItem());
		}
		return bookName;
	}

	/**
	 * @see pcgen.gui.tabs.spells.InfoSpellsSubTab#delSpellButton()
	 */
	protected void delSpellButton()
	{
		TreePath[] selCPaths = selectedTable.getTree().getSelectionPaths();
		if (selCPaths == null)
		{
			return;
		}

		for (int index = selCPaths.length - 1; index >= 0; --index)
		{
			TreePath selCPath = selCPaths[index];
	
			Object endComp = selCPath.getLastPathComponent();
			PObjectNode fNode = (PObjectNode) endComp;
			List aList = getInfoFromNode(fNode);
			CharacterSpell cs = null;
			String className = null;
			if (aList != null)
			{
				cs = (CharacterSpell) aList.get(0);
				className = (String) aList.get(1);
			}

			if (cs != null)
			{
		
				String bookName = getBookName((PObjectNode) selCPath
					.getPathComponent(1));
				SpellInfo si = (SpellInfo) fNode.getItem();
		
				PCClass aClass = pc.getClassNamed(className);
				if (aClass == null)
				{
					ShowMessageDelegate.showMessageDialog(PropertyFactory
						.getString("InfoSpells.can.only.add.by.class.level"), //$NON-NLS-1$
						Constants.s_APPNAME, MessageType.ERROR); 
				}
				else
				{
					bookName = currSpellBook = bookName;
			
					final String aString = pc.delSpell(si, aClass, bookName);
			
					if (aString.length() > 0)
					{
						ShowMessageDelegate.showMessageDialog(aString, Constants.s_APPNAME,
							MessageType.ERROR);
					}
				}
			}
		}
		pc.setDirty(true);
		updateSelectedModel();
	}

	/**
	 *
	 * This is used to add new spellbooks when the
	 * spellBookNameText JTextField is edited
	 *
	 */
	private void addBookButton()
	{
		final String aString = spellBookNameText.getText();

		if (aString == null || aString.equals(currSpellBook) || aString.trim().length() == 0)
		{
			return;
		}

		// added to prevent spellbooks being given the same name as a class
		for (Iterator i = Globals.getClassList().iterator(); i.hasNext();)
		{
			PCClass current = (PCClass) i.next();

			if ((aString.equals(current.getName())))
			{
				JOptionPane.showMessageDialog(null, PropertyFactory.getString("in_spellbook_name_error"), //$NON-NLS-1$
					Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);

				return;
			}
		}

		SpellBook book = new SpellBook(aString, SpellBook.TYPE_SPELL_BOOK);
		if (pc.addSpellBook(book))
		{
			pc.setDirty(true);
			spellBookNameText.setText(aString);
			currSpellBook = aString;
			if (!selectedBookList.contains(aString))
			{
				selectedBookList.add(aString);
			}
			updateAvailableModel();
			updateSelectedModel();
		}
		else
		{
			JOptionPane.showMessageDialog(null, PropertyFactory
				.getFormattedString(
					"InfoPreparedSpells.add.list.fail", new Object[]{aString}), //$NON-NLS-1$
				Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);

			return;
		}
	}


	private void delBookButton()
	{
		String aString = spellBookNameText.getText();

		if (aString.equalsIgnoreCase(Globals.getDefaultSpellBook()))
		{
			Logging.errorPrint(PropertyFactory.getString("InfoSpells.can.not.delete.default.spellbook")); //$NON-NLS-1$

			return;
		}

		if (pc.delSpellBook(aString))
		{
			pc.setDirty(true);
			updateBookList();
			currSpellBook = (String) (selectedBookList.size() > 0 ? selectedBookList
				.get(0)
				: Globals.getDefaultSpellBook());

			updateAvailableModel();
			updateSelectedModel();
		}
		else
		{
			Logging.errorPrint("delBookButton:failed "); //$NON-NLS-1$

			return;
		}
	}
	
	private void selectFromComboBoxActionPerformed(int selectedIndex)
	{
		if (selectedIndex >=0 && selectedIndex <= 2)
		{
			selectFromMode = selectedIndex;
			updateAvailableModel();
		}
	}
	
}
