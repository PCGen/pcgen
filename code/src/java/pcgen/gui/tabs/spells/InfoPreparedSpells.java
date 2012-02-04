/*
 * InfoPrepSpells.java
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
 * $Id$
 *
 */
package pcgen.gui.tabs.spells;

import java.awt.BorderLayout;
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.AbilityUtilities;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.bonus.BonusObj;
import pcgen.core.bonus.BonusUtilities;
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
import pcgen.system.LanguageBundle;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;
import pcgen.util.enumeration.Tab;

/**
 * <code>InfoPreparedSpells</code> is responsible for the display of the
 * list of a character's prepared spell lists and the provision of an
 * interface to let the user create and update lists of prepared spells.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */

public class InfoPreparedSpells extends InfoSpellsSubTab
{
	private final JLabel avaLabel =
			new JLabel(LanguageBundle
				.getString("InfoPreparedSpells.sort.avail.spells.by")); //$NON-NLS-1$
	private final JLabel selLabel =
			new JLabel(LanguageBundle
				.getString("InfoPreparedSpells.sort.select.spells.by")); //$NON-NLS-1$
	private FlippingSplitPane asplit;
	private FlippingSplitPane bsplit;
	private FlippingSplitPane splitPane;

	private JButton addSpellButton;
	private JButton addSpellMMButton;
	private JButton delSpellButton;

	private JTextField spellBookNameText = new JTextField();
	private JButton addSpellListButton;
	private JButton delSpellListButton;

	private JComboBoxEx primaryViewComboBox = new JComboBoxEx();
	private JComboBoxEx secondaryViewComboBox = new JComboBoxEx();
	private JComboBoxEx primaryViewSelectComboBox = new JComboBoxEx();
	private JComboBoxEx secondaryViewSelectComboBox = new JComboBoxEx();

	private JCheckBox canUseHigherSlots =
			new JCheckBox(LanguageBundle
				.getString("InfoPreparedSpells.canUseHigherSlots")); //$NON-NLS-1$

	private List<String> characterMetaMagicFeats = new ArrayList<String>();

	private JPanel botPane = new JPanel();
	private JPanel topPane = new JPanel();
	private boolean hasBeenSized = false;

	/**
	 *  Constructor for the InfoPrepSpells object
	 *
	 * @param pc The character this tab is being created to display.
	 */
	public InfoPreparedSpells(PlayerCharacter pc)
	{
		super(pc, Tab.PREPARED_SPELLS);

		addSpellWithMetaMagicTitle =
				SettingsHandler.getGame().getAddWithMetamagicMessage();
		if (addSpellWithMetaMagicTitle.length() == 0)
		{
			addSpellWithMetaMagicTitle =
					LanguageBundle.getString("InfoSpells.add.with.metamagic"); //$NON-NLS-1$
		}

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
		return SettingsHandler.getPCGenOption(".Panel.Spells.Prepared.Order", //$NON-NLS-1$
			Tab.KNOWN_SPELLS.ordinal());
	}

	/**
	 * @see pcgen.gui.CharacterInfoTab#setTabOrder(int)
	 */
	public void setTabOrder(int order)
	{
		SettingsHandler.setPCGenOption(".Panel.Spells.Prepared.Order", order); //$NON-NLS-1$
	}

	/**
	 * @see pcgen.gui.CharacterInfoTab#getToDos()
	 */
	public List<String> getToDos()
	{
		List<String> toDoList = new ArrayList<String>();
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

		canUseHigherSlots.setSelected(pc.getUseHigherPreppedSlots());
		pc.getSpellList();
		updateBookList();

		updateAvailableModel();
		updateSelectedModel();

		createFeatList();

		classLabel.setText(""); //$NON-NLS-1$

		needsUpdate = false;
	}

	/**
	 *
	 * add all metamagic feats to arrayList
	 *
	 **/
	private void createFeatList()
	{
		// get the list of metamagic feats for the PC
		characterMetaMagicFeats.clear();
		List<Ability> feats =
				AbilityUtilities.getAggregateAbilitiesListForKey(
					AbilityCategory.FEAT.getKeyName(), pc);
		Globals.sortPObjectListByName(feats);

		for (Ability aFeat : feats)
		{
			if (aFeat.isType("Metamagic")) //$NON-NLS-1$
			{
				characterMetaMagicFeats.add(aFeat.getKeyName());
			}
		}

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
			divLocSplitPane =
					SettingsHandler.getPCGenOption("InfoPrepSpells.splitPane", //$NON-NLS-1$
						(int) ((this.getSize().getWidth() * 4) / 10));
			divLocVert = SettingsHandler.getPCGenOption("InfoPrepSpells.bsplit", //$NON-NLS-1$
				(int) (this.getSize().getHeight() - 101));
			divLocHoriz = SettingsHandler.getPCGenOption("InfoPrepSpells.asplit", //$NON-NLS-1$
				(int) (this.getSize().getWidth() - 408));

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
			SettingsHandler.setPCGenOption(
				"InfoPrepSpells.splitPane", divLocSplitPane); //$NON-NLS-1$
		}

		if (divLocVert > 0)
		{
			bsplit.setDividerLocation(divLocVert);
			SettingsHandler.setPCGenOption("InfoPrepSpells.bsplit", divLocVert); //$NON-NLS-1$
		}

		if (divLocHoriz > 0)
		{
			asplit.setDividerLocation(divLocHoriz);
			SettingsHandler.setPCGenOption("InfoPrepSpells.asplit", divLocHoriz); //$NON-NLS-1$
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
		splitPane.addPropertyChangeListener(
			JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent evt)
				{
					if (hasBeenSized)
					{
						int s = splitPane.getDividerLocation();
						if (s > 0)
						{
							SettingsHandler.setPCGenOption(
								"InfoPrepSpells.splitPane", s);
						}
					}
				}
			});
		bsplit.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
			new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent evt)
				{
					if (hasBeenSized)
					{
						int s = bsplit.getDividerLocation();
						if (s > 0)
						{
							SettingsHandler.setPCGenOption("InfoPrepSpells.bsplit",
								s);
						}
					}
				}
			});
		asplit.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
			new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent evt)
				{
					if (hasBeenSized)
					{
						int s = asplit.getDividerLocation();
						if (s > 0)
						{
							SettingsHandler.setPCGenOption("InfoPrepSpells.asplit",
								s);
						}
					}
				}
			});
		canUseHigherSlots.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				pc.setUseHigherPreppedSlots(canUseHigherSlots.isSelected());
			}
		});
		addSpellButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addSpellButton();
			}
		});
		addSpellMMButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addSpellMMButton();
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
		spellBookNameText.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addBookButton();
			}
		});
		addSpellListButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addBookButton();
			}
		});
		delSpellListButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				delBookButton();
			}
		});

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
		Utility.setDescription(primaryViewComboBox, LanguageBundle
			.getString("InfoSpells.change.how.spell.are.listed")); //$NON-NLS-1$
		populateViewCombo(secondaryViewComboBox, secondaryViewMode, true);

		populateViewCombo(primaryViewSelectComboBox, primaryViewSelectMode,
			false);
		Utility.setDescription(primaryViewSelectComboBox, LanguageBundle
			.getString("InfoSpells.change.how.spells.in.table.listed")); //$NON-NLS-1$
		populateViewCombo(secondaryViewSelectComboBox, secondaryViewSelectMode,
			true);
		Utility.setDescription(secondaryViewSelectComboBox, LanguageBundle
			.getString("InfoSpells.change.how.spells.in.table.listed")); //$NON-NLS-1$

		ImageIcon newImage;
		newImage = IconUtilitities.getImageIcon("Forward16.gif"); //$NON-NLS-1$
		addSpellButton = new JButton(newImage);
		newImage = IconUtilitities.getImageIcon("Back16.gif"); //$NON-NLS-1$
		delSpellButton = new JButton(newImage);

		// flesh out all the tree views
		createModels();

		// create tables associated with the above trees
		createTreeTables();

		// Build the Top Panel
		buildTopPanel();

		// Build Bottom Panel
		initBottomPanel();

		// now split the top and bottom Panels
		bsplit =
				new FlippingSplitPane(JSplitPane.VERTICAL_SPLIT, topPane,
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
		availableSort =
				new JTreeTableSorter(availableTable,
					(PObjectNode) availableModel.getRoot(), availableModel);
		selectedSort =
				new JTreeTableSorter(selectedTable, (PObjectNode) selectedModel
					.getRoot(), selectedModel);
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
		splitPane =
				new FlippingSplitPane(splitOrientation, leftPane, rightPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerSize(10);

		topPane.add(splitPane, BorderLayout.CENTER);

		//
		// first build the left pane
		// for the availabe spells table and info
		//

		JPanel aPanel = new JPanel();
		aPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 1));
		aPanel.add(avaLabel);
		aPanel.add(primaryViewComboBox);
		aPanel.add(secondaryViewComboBox);

		Utility.setDescription(aPanel, LanguageBundle
			.getString("InfoSpells.rightclick.add.to.spellbooks")); //$NON-NLS-1$
		leftPane.add(aPanel, BorderLayout.NORTH);

		// the available spells panel
		JScrollPane scrollPane =
				new JScrollPane(availableTable,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		leftPane.add(scrollPane, BorderLayout.CENTER);

		JButton columnButton = new JButton();
		scrollPane.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER,
			columnButton);
		columnButton.setText("^"); //$NON-NLS-1$
		new TableColumnManager(availableTable, columnButton, availableModel);

		leftPane.add(buildAddSpellPanel(), BorderLayout.SOUTH);

		//
		// now build the right pane
		// for the selected (SpellBooks) table
		//

		JPanel sPanel = new JPanel();
		sPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 1));
		sPanel.add(selLabel);
		sPanel.add(primaryViewSelectComboBox);
		sPanel.add(secondaryViewSelectComboBox);
		rightPane.add(sPanel, BorderLayout.NORTH);

		// Prepared spell lists panel
		JPanel slPanel = buildSpellListPanel();
		rightPane.add(slPanel);

		// List of known spells Panel
		scrollPane =
				new JScrollPane(selectedTable,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane
			.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		selectedTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		selectedTable.setShowHorizontalLines(true);
		rightPane.add(scrollPane, BorderLayout.CENTER);

		JButton columnButton2 = new JButton();
		scrollPane.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER,
			columnButton2);
		columnButton2.setText("^"); //$NON-NLS-1$
		new TableColumnManager(selectedTable, columnButton2, selectedModel);

		// Buttons above spellbooks and known spells
		rightPane.add(buildSpellListPanel(), BorderLayout.SOUTH);
	}

	/**
	 * Build the panel with the controls to add a spell to a
	 * prepared list.
	 *
	 * @return The panel.
	 */
	private JPanel buildAddSpellPanel()
	{
		JPanel controlsPanel = new JPanel();
		controlsPanel.setLayout(new BorderLayout());
		JPanel asPanel = new JPanel();
		asPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 1));
		canUseHigherSlots.setSelected(pc.getUseHigherPreppedSlots());
		asPanel.add(canUseHigherSlots);
		controlsPanel.add(asPanel, BorderLayout.NORTH);

		asPanel = new JPanel();
		asPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 1));

		addSpellMMButton = new JButton(addSpellWithMetaMagicTitle);
		createFeatList();
		asPanel.add(addSpellMMButton);

		Utility.setDescription(addSpellButton, LanguageBundle
			.getString("InfoSpells.add.selected")); //$NON-NLS-1$
		addSpellButton.setEnabled(false);
		addSpellButton.setMargin(new Insets(1, 14, 1, 14));
		asPanel.add(addSpellButton);
		controlsPanel.add(asPanel, BorderLayout.SOUTH);

		return controlsPanel;
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
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;

		JPanel slPanel = new JPanel(new GridBagLayout());

		Utility.buildConstraints(c, 0, 0, 1, 1, 0.0, 0.0);
		c.insets = new Insets(1, 2, 1, 2);
		Utility.setDescription(delSpellButton, LanguageBundle
			.getString("InfoSpells.add.selected")); //$NON-NLS-1$
		delSpellButton.setEnabled(false);
		delSpellButton.setMargin(new Insets(1, 14, 1, 14));
		slPanel.add(delSpellButton, c);

		JLabel prepListLabel =
				new JLabel(LanguageBundle
					.getString("InfoPreparedSpells.preparedList")); //$NON-NLS-1$
		Utility.buildConstraints(c, 1, 0, 1, 1, 0.0, 0.0);
		c.insets = new Insets(1, 2, 1, 2);
		slPanel.add(prepListLabel, c);
		spellBookNameText.setEditable(true);
		Utility.buildConstraints(c, 2, 0, 1, 1, 1.0, 0.0);
		c.fill = GridBagConstraints.HORIZONTAL;
		slPanel.add(spellBookNameText, c);

		addSpellListButton =
				new JButton(LanguageBundle.getString("InfoSpells.add")); //$NON-NLS-1$
		Utility.setDescription(addSpellListButton, LanguageBundle
			.getString("InfoPreparedSpells.add.list")); //$NON-NLS-1$
		Utility.buildConstraints(c, 3, 0, 1, 1, 0.0, 0.0);
		c.insets = new Insets(1, 2, 1, 2);
		c.fill = GridBagConstraints.NONE;
		slPanel.add(addSpellListButton, c);

		delSpellListButton =
				new JButton(LanguageBundle.getString("InfoSpells.delete")); //$NON-NLS-1$
		Utility.setDescription(delSpellListButton, LanguageBundle
			.getString("InfoPreparedSpells.del.list")); //$NON-NLS-1$
		Utility.buildConstraints(c, 4, 0, 1, 1, 0.0, 0.0);
		c.insets = new Insets(1, 2, 1, 2);
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

		asplit =
				new FlippingSplitPane(JSplitPane.HORIZONTAL_SPLIT, bLeftPane,
					bRightPane);
		asplit.setOneTouchExpandable(true);
		asplit.setDividerSize(10);

		botPane.add(asplit, BorderLayout.CENTER);

		// create a spell info scroll area
		Utility.buildConstraints(c, 0, 0, 1, 1, 1, 1, GridBagConstraints.BOTH,
			GridBagConstraints.CENTER);

		JScrollPane sScroll = new JScrollPane();
		gridbag.setConstraints(sScroll, c);

		TitledBorder sTitle =
				BorderFactory.createTitledBorder(LanguageBundle
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

		TitledBorder iTitle =
				BorderFactory.createTitledBorder(LanguageBundle
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
		for (SpellBook book : pc.getSpellBooks())
		{
			// build spell book list
			if (book.getType() == SpellBook.TYPE_PREPARED_LIST)
			{
				String bookName = book.getName();
				if (!selectedBookList.contains(bookName))
				{
					selectedBookList.add(bookName);
				}
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
			availableModel =
					new SpellModel(primaryViewMode, secondaryViewMode, true,
						availableBookList, currSpellBook,
						GuiConstants.INFOSPELLS_AVAIL_KNOWN, pc, this, ""); //$NON-NLS-1$
		}
		else
		{
			availableModel.resetModel(primaryViewMode, secondaryViewMode, true,
				availableBookList, currSpellBook,
				GuiConstants.INFOSPELLS_AVAIL_KNOWN, this, ""); //$NON-NLS-1$
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
			selectedModel =
					new SpellModel(primaryViewSelectMode,
						secondaryViewSelectMode, false, selectedBookList,
						currSpellBook, GuiConstants.INFOSPELLS_AVAIL_KNOWN, pc,
						this, "Prepared Spells");//$NON-NLS-1$
		}
		else
		{
			selectedModel.resetModel(primaryViewSelectMode,
				secondaryViewSelectMode, false, selectedBookList,
				currSpellBook, GuiConstants.INFOSPELLS_AVAIL_KNOWN, this,
				"Prepared Spells");//$NON-NLS-1$
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
					addSpellMMButton.setEnabled(true);
					addMenu.setEnabled(true);
					addMetaMagicMenu.setEnabled(true);
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
				addSpellMMButton.setEnabled(false);
				addMenu.setEnabled(false);
				addMetaMagicMenu.setEnabled(false);
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
			bookName = ""; //$NON-NLS-1$
		}
		else
		{
			bookName = selCPath.getPathComponent(1).toString();
		}

		if (bookName.length() <= 0)
		{
			ShowMessageDelegate
				.showMessageDialog(
					LanguageBundle
						.getString("InfoSpells.first.select.spelllist"), Constants.APPLICATION_NAME, MessageType.ERROR); //$NON-NLS-1$

			return; // need to select a spellbook
		}

		if (!(primaryViewMode == GuiConstants.INFOSPELLS_VIEW_CLASS || secondaryViewMode == GuiConstants.INFOSPELLS_VIEW_CLASS)
			|| !(primaryViewMode == GuiConstants.INFOSPELLS_VIEW_LEVEL || secondaryViewMode == GuiConstants.INFOSPELLS_VIEW_LEVEL))
		{
			ShowMessageDelegate
				.showMessageDialog(
					LanguageBundle
						.getString("InfoSpells.can.only.add.by.class.level"), Constants.APPLICATION_NAME, MessageType.ERROR); //$NON-NLS-1$
			return; // need to select class/level or level/class as sorters
		}

		currSpellBook = bookName;
		if (!ensureDefaultBookPresent())
		{
			return;
		}

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
	 * Check if the default spell book is present and if not then
	 * add it to the character. Used so that the spellbook is only added if
	 * it is used.
	 * @return false if an error occurs and the book can't be added.
	 */
	private boolean ensureDefaultBookPresent()
	{
		if (!pc.hasSpellBook(currSpellBook))
		{
			if (pc.addSpellBook(currSpellBook))
			{
				pc.setDirty(true);
				spellBookNameText.setText(currSpellBook);
				if (!selectedBookList.contains(currSpellBook))
				{
					selectedBookList.add(currSpellBook);
				}
				updateAvailableModel();
				updateSelectedModel();
			}
			else
			{
				JOptionPane
					.showMessageDialog(
						null,
						LanguageBundle
							.getFormattedString(
								"InfoPreparedSpells.add.list.fail", new Object[]{currSpellBook}), //$NON-NLS-1$
						Constants.APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);

				return false;
			}
		}
		return true;
	}

	/**
	 * memorize a spell with metamagic feats applied.
	 */
	protected void addSpellMMButton()
	{
		TreePath avaCPath = availableTable.getTree().getSelectionPath();
		TreePath selCPath = selectedTable.getTree().getSelectionPath();

		String bookName;
		if (selCPath == null)
		{
			bookName = spellBookNameText.getText();
		}
		else
		{
			bookName = selCPath.getPathComponent(1).toString();
		}

		if (bookName.length() <= 0)
		{
			ShowMessageDelegate
				.showMessageDialog(
					LanguageBundle
						.getString("InfoSpells.first.select.spellbook"), Constants.APPLICATION_NAME, MessageType.ERROR); //$NON-NLS-1$
			return; // need to selected a spellbook
		}

		currSpellBook = bookName;
		if (!ensureDefaultBookPresent())
		{
			return;
		}

		String className = ""; //$NON-NLS-1$

		Object endComp = avaCPath.getLastPathComponent();
		PObjectNode fNode = (PObjectNode) endComp;

		if (!(fNode.getItem() instanceof SpellInfo))
		{
			ShowMessageDelegate
				.showMessageDialog(
					LanguageBundle.getString("InfoSpells.can.not.metamagic"), Constants.APPLICATION_NAME, MessageType.ERROR); //$NON-NLS-1$
			return;
		}

		SpellInfo si = (SpellInfo) fNode.getItem();
		CharacterSpell spellA = si.getOwner();
		if (!(spellA.getOwner() instanceof PCClass))
		{
			ShowMessageDelegate
				.showMessageDialog(
					LanguageBundle.getString("InfoSpells.unable.to.metamagic") + spellA.getOwner().getDisplayName(), Constants.APPLICATION_NAME, MessageType.ERROR); //$NON-NLS-1$
			return;
		}

		PCClass aClass = (PCClass) spellA.getOwner();
		if (aClass == null)
		{
			ShowMessageDelegate
				.showMessageDialog(
					LanguageBundle
						.getString("InfoSpells.con.only.metamagic.class.level"), Constants.APPLICATION_NAME, MessageType.ERROR); //$NON-NLS-1$
			return; // need to select class/level or level/class as sorters
		}

		if (bookName.equals(Globals.getDefaultSpellBook()))
		{
			spellA = new CharacterSpell(spellA.getOwner(), spellA.getSpell());
		}
		className = aClass.getKeyName();

		// make sure all the feats are set
		createFeatList();

		ChooserInterface c = ChooserFactory.getChooserInstance();
		if (Globals.hasSpellPPCost())
		{
			//
			// Does feat apply a BONUS:PPCOST to this spell, all spells, or all spells of
			// one of this spell's types? If it does, then we can possibly apply it to
			// this spell.
			//

			final String aKey = spellA.getSpell().getKeyName();
			List<Ability> metamagicFeats = new ArrayList<Ability>();
			for (String s : characterMetaMagicFeats)
			{
				final Ability anAbility = Globals.getContext().ref
						.silentlyGetConstructedCDOMObject(Ability.class,
								AbilityCategory.FEAT, s);
				if (anAbility == null)
				{
					continue;
				}

				boolean canAdd = false;
				List<BonusObj> bonusList =
						BonusUtilities.getBonusFromList(anAbility
							.getRawBonusList(pc), "PPCOST"); //$NON-NLS-1$
				if (bonusList.size() == 0)
				{
					canAdd = true; // if doesn't modify PP COST, then allow it
				}
				else
				{
					for (BonusObj aBonus : bonusList)
					{
						final java.util.StringTokenizer aTok =
								new java.util.StringTokenizer(aBonus
									.getBonusInfo(), ","); //$NON-NLS-1$
						while (aTok.hasMoreTokens())
						{
							final String aBI = aTok.nextToken();

							if (aBI.equalsIgnoreCase(aKey)
								|| aBI.equalsIgnoreCase("ALL")) //$NON-NLS-1$
							{
								canAdd = true;
								break;
							}
							else if (aBI.startsWith("TYPE=") || aBI.startsWith("TYPE.")) //$NON-NLS-1$ //$NON-NLS-2$
							{
								if (spellA.getSpell().isType(aBI.substring(5)))
								{
									canAdd = true;
									break;
								}
							}
						}
					}
				}
				if (!canAdd)
				{
					continue;
				}
				metamagicFeats.add(anAbility);
			}
			c.setAvailableList(metamagicFeats);
		}
		else
		{
			c.setAvailableList(characterMetaMagicFeats);
		}
		c.setVisible(false);
		c.setPoolFlag(false);
		c.setAllowsDups(true);
		c.setTitle(addSpellWithMetaMagicTitle); //$NON-NLS-1$
		c.setMessageText(LanguageBundle
			.getString("InfoSpells.select.metamagic")); //$NON-NLS-1$
		c.setTotalChoicesAvail(99);
		c.setVisible(true);

		final List<String> fList = c.getSelectedList();
		List<Ability> selFeatList = new ArrayList<Ability>();
		int spLevel = si.getActualLevel();
		int realLevel = spLevel;

		for (int i = 0; i < fList.size(); ++i)
		{
			Ability aFeat = pc.getFeatNamed(fList.get(i));
			realLevel += aFeat.getSafe(IntegerKey.ADD_SPELL_LEVEL);
			selFeatList.add(aFeat);
		}

		final String aString =
				pc.addSpell(spellA, selFeatList, className, bookName,
					realLevel, spLevel);

		if (aString.length() > 0)
		{
			ShowMessageDelegate.showMessageDialog(aString, Constants.APPLICATION_NAME,
				MessageType.ERROR);
			return;
		}

		pc.setDirty(true);

		updateSelectedModel();

		spellBookNameText.setText(bookName);
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
			List<Object> aList = getInfoFromNode(fNode);
			CharacterSpell cs = null;
			String className = null;
			if (aList != null)
			{
				cs = (CharacterSpell) aList.get(0);
				className = (String) aList.get(1);
			}

			if (cs != null)
			{

				String bookName = selCPath.getPathComponent(1).toString();
				SpellInfo si = (SpellInfo) fNode.getItem();

				// TODO Check this
				PCClass aClass = pc.getClassKeyed(className);
				if (aClass == null)
				{
					ShowMessageDelegate.showMessageDialog(LanguageBundle
						.getString("InfoSpells.can.only.add.by.class.level"), //$NON-NLS-1$
						Constants.APPLICATION_NAME, MessageType.ERROR);
				}
				else
				{
					bookName = currSpellBook = bookName;

					final String aString = pc.delSpell(si, aClass, bookName);

					if (aString.length() > 0)
					{
						ShowMessageDelegate.showMessageDialog(aString,
							Constants.APPLICATION_NAME, MessageType.ERROR);
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

		if (aString == null || aString.equals(currSpellBook)
			|| aString.trim().length() == 0)
		{
			return;
		}

		// added to prevent spellbooks being given the same name as a class
		for (PCClass current : Globals.getContext().ref.getConstructedCDOMObjects(PCClass.class))
		{
			if ((aString.equals(current.getKeyName())))
			{
				JOptionPane.showMessageDialog(null, LanguageBundle
					.getString("in_spellbook_name_error"), //$NON-NLS-1$
					Constants.APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);

				return;
			}
		}

		if (pc.addSpellBook(aString))
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
			JOptionPane.showMessageDialog(null, LanguageBundle
				.getFormattedString(
					"InfoPreparedSpells.add.list.fail", new Object[]{aString}), //$NON-NLS-1$
				Constants.APPLICATION_NAME, JOptionPane.ERROR_MESSAGE);

			return;
		}
	}

	private void delBookButton()
	{
		String aString = spellBookNameText.getText();

		if (aString.equalsIgnoreCase(Globals.getDefaultSpellBook()))
		{
			Logging.errorPrint(LanguageBundle
				.getString("InfoSpells.can.not.delete.default.spellbook")); //$NON-NLS-1$

			return;
		}

		if (pc.delSpellBook(aString))
		{
			pc.setDirty(true);
			updateBookList();
			currSpellBook =
					(selectedBookList.size() > 0 ? selectedBookList.get(0)
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

}
