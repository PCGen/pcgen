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
 * $Id$
 *
 */
package pcgen.gui.tabs.spells;

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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
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
import pcgen.util.FOPHandler;
import pcgen.util.Logging;
import pcgen.system.LanguageBundle;
import pcgen.util.enumeration.Tab;

/**
 * <code>InfoKnownSpells</code> is responsible for the display of the list of
 * a character's known spells and the provision of an interface to let the
 * user update their character's known spell list.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */

public class InfoKnownSpells extends InfoSpellsSubTab
{
	private final JLabel avaLabel =
			new JLabel(LanguageBundle
				.getString("InfoKnownSpells.sort.avail.spells.by")); //$NON-NLS-1$
	private final JLabel selLabel =
			new JLabel(LanguageBundle
				.getString("InfoKnownSpells.sort.select.spells.by")); //$NON-NLS-1$
	private FlippingSplitPane asplit;
	private FlippingSplitPane bsplit;
	private FlippingSplitPane splitPane;
	private JButton addSpellButton;
	private JButton delSpellButton;

	private JComboBoxEx primaryViewComboBox = new JComboBoxEx();
	private JComboBoxEx secondaryViewComboBox = new JComboBoxEx();
	private JComboBoxEx primaryViewSelectComboBox = new JComboBoxEx();
	private JComboBoxEx secondaryViewSelectComboBox = new JComboBoxEx();

	private JCheckBox shouldAutoSpells =
			new JCheckBox(LanguageBundle.getString("InfoSpells.autoload")); //$NON-NLS-1$
	private JCheckBox canUseHigherSlots =
			new JCheckBox(LanguageBundle
				.getString("InfoKnownSpells.canUseHigherSlots")); //$NON-NLS-1$

	private JButton printHtml;
	private JButton printPdf;
	private JButton selectSpellSheetButton =
			new JButton(LanguageBundle
				.getString("InfoSpells.select.spellsheet")); //$NON-NLS-1$
	private JTextField selectSpellSheetField = new JTextField();

	private JPanel botPane = new JPanel();
	private JPanel topPane = new JPanel();
	private boolean hasBeenSized = false;

	/**
	 *  Constructor for the InfoKnownSpells object
	 *
	 * @param pc The character this tab is being created to display.
	 */
	public InfoKnownSpells(PlayerCharacter pc)
	{
		super(pc, Tab.KNOWN_SPELLS);

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
		return SettingsHandler.getPCGenOption(".Panel.Spells.Known.Order", //$NON-NLS-1$
			Tab.KNOWN_SPELLS.ordinal());
	}

	/**
	 * @see pcgen.gui.CharacterInfoTab#setTabOrder(int)
	 */
	public void setTabOrder(int order)
	{
		SettingsHandler.setPCGenOption(".Panel.Spells.Known.Order", order); //$NON-NLS-1$
	}

	/**
	 * @see pcgen.gui.CharacterInfoTab#getToDos()
	 */
	public List<String> getToDos()
	{
		List<String> toDoList = new ArrayList<String>();

		boolean hasFree = false;
		for (PCClass aClass : pc.getClassSet())
		{
			if (pc.getSpellSupport(aClass).hasKnownList() || pc.getSpellSupport(aClass).hasKnownSpells(pc))
			{
				int highestSpellLevel = pc.getSpellSupport(aClass).getHighestLevelSpell(pc);
				for (int i = 0; i <= highestSpellLevel; ++i)
				{
					if (pc.availableSpells(i, aClass, Globals
						.getDefaultSpellBook(), true, true))
					{
						hasFree = true;
						break;
					}
				}
			}
		}

		if (hasFree)
		{
			toDoList.add(LanguageBundle.getString("InfoSpells.Todo.Remain")); //$NON-NLS-1$
		}
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
		shouldAutoSpells.setSelected(pc.getAutoSpells());
		canUseHigherSlots.setSelected(pc.getUseHigherKnownSlots());
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
			divLocSplitPane =
					SettingsHandler.getPCGenOption("InfoKnownSpells.splitPane", //$NON-NLS-1$
						(int) ((this.getSize().getWidth() * 3) / 10));
			divLocVert = SettingsHandler.getPCGenOption("InfoKnownSpells.bsplit", //$NON-NLS-1$
				(int) (this.getSize().getHeight() - 101));
			divLocHoriz = SettingsHandler.getPCGenOption("InfoKnownSpells.asplit", //$NON-NLS-1$
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
				"InfoKnownSpells.splitPane", divLocSplitPane); //$NON-NLS-1$
		}

		if (divLocVert > 0)
		{
			bsplit.setDividerLocation(divLocVert);
			SettingsHandler.setPCGenOption("InfoKnownSpells.bsplit", divLocVert); //$NON-NLS-1$
		}

		if (divLocHoriz > 0)
		{
			asplit.setDividerLocation(divLocHoriz);
			SettingsHandler.setPCGenOption("InfoKnownSpells.asplit", divLocHoriz); //$NON-NLS-1$
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
								"InfoKnownSpells.splitPane", s);
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
							SettingsHandler.setPCGenOption("InfoKnownSpells.bsplit",
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
							SettingsHandler.setPCGenOption("InfoKnownSpells.asplit",
								s);
						}
					}
				}
			});
		shouldAutoSpells.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				pc.setAutoSpells(shouldAutoSpells.isSelected());
			}
		});
		canUseHigherSlots.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				pc.setUseHigherKnownSlots(canUseHigherSlots.isSelected());
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
		selectSpellSheetButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				selectSpellSheetButton();
			}
		});
		printHtml.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				boolean aBool = SettingsHandler.getPrintSpellsWithPC();
				SettingsHandler.setPrintSpellsWithPC(true);
				Utility.previewInBrowser(SettingsHandler
					.getSelectedSpellSheet(), pc);
				SettingsHandler.setPrintSpellsWithPC(aBool);
			}
		});
		printPdf.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				boolean aBool = SettingsHandler.getPrintSpellsWithPC();
				PCGen_Frame1.getInst();
				SettingsHandler.setPrintSpellsWithPC(true);
				exportSpellsToFile();
				SettingsHandler.setPrintSpellsWithPC(aBool);
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

		// Auto add known option
		leftPane.add(buildAddSpellPanel(), BorderLayout.SOUTH);

		//
		// now build the right pane
		// for the selected (SpellBooks) table
		//
		// Buttons above spellbooks and known spells
		JPanel sPanel = new JPanel();
		sPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 1));
		sPanel.add(selLabel);
		sPanel.add(primaryViewSelectComboBox);
		sPanel.add(secondaryViewSelectComboBox);
		rightPane.add(sPanel, BorderLayout.NORTH);

		// List of known spells Panel
		scrollPane =
				new JScrollPane(selectedTable,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		selectedTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		selectedTable.setShowHorizontalLines(true);
		rightPane.add(scrollPane, BorderLayout.CENTER);

		JButton columnButton2 = new JButton();
		scrollPane.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER,
			columnButton2);
		columnButton2.setText("^"); //$NON-NLS-1$
		new TableColumnManager(selectedTable, columnButton2, selectedModel);

		rightPane.add(buildOutputSpellsPanel(), BorderLayout.SOUTH);
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
		shouldAutoSpells.setSelected(pc.getAutoSpells());
		asPanel.add(shouldAutoSpells);
		controlsPanel.add(asPanel, BorderLayout.NORTH);

		asPanel = new JPanel();
		canUseHigherSlots.setSelected(pc.getUseHigherKnownSlots());
		asPanel.add(canUseHigherSlots);

		Utility.setDescription(addSpellButton, LanguageBundle
			.getString("InfoSpells.add.selected")); //$NON-NLS-1$
		addSpellButton.setEnabled(false);
		addSpellButton.setMargin(new Insets(1, 14, 1, 14));
		asPanel.add(addSpellButton);
		controlsPanel.add(asPanel, BorderLayout.SOUTH);

		return controlsPanel;
	}

	/**
	 * Build the Spell sheet selection and output panel
	 *
	 * @return The panel.
	 */
	private JPanel buildOutputSpellsPanel()
	{
		GridBagConstraints c = new GridBagConstraints();
		JPanel ssPanel = new JPanel(new GridBagLayout());

		c = new GridBagConstraints();
		Utility.buildConstraints(c, 0, 0, 1, 1, 0.0, 0.0);
		c.insets = new Insets(1, 2, 1, 2);
		Utility.setDescription(delSpellButton, LanguageBundle
			.getString("InfoSpells.remove.selected")); //$NON-NLS-1$
		delSpellButton.setEnabled(false);
		delSpellButton.setMargin(new Insets(1, 14, 1, 14));
		ssPanel.add(delSpellButton, c);

		c = new GridBagConstraints();
		Utility.buildConstraints(c, 1, 0, 1, 1, 0.0, 0.0);
		c.insets = new Insets(1, 2, 1, 2);
		ssPanel.add(selectSpellSheetButton, c);

		selectSpellSheetField.setEditable(false);
		selectSpellSheetField.setBackground(Color.lightGray);
		selectSpellSheetField.setText(SettingsHandler
			.getSelectedSpellSheetName());
		selectSpellSheetField.setToolTipText(SettingsHandler
			.getSelectedSpellSheetName());
		c = new GridBagConstraints();
		Utility.buildConstraints(c, 2, 0, 1, 1, 1.0, 0.0);
		c.insets = new Insets(1, 2, 1, 2);
		c.fill = GridBagConstraints.HORIZONTAL;
		ssPanel.add(selectSpellSheetField, c);

		printHtml = new JButton();
		printHtml.setToolTipText(LanguageBundle
			.getString("InfoSpells.print.preview")); //$NON-NLS-1$
		IconUtilitities.maybeSetIcon(printHtml, "PrintPreview16.gif"); //$NON-NLS-1$
		printHtml.setEnabled(true);
		c = new GridBagConstraints();
		Utility.buildConstraints(c, 3, 0, 1, 1, 0.0, 0.0);
		c.insets = new Insets(1, 2, 1, 2);
		printHtml.setMargin(new Insets(1, 14, 1, 14));
		ssPanel.add(printHtml, c);

		printPdf = new JButton();
		printPdf.setToolTipText(LanguageBundle.getString("InfoSpells.print")); //$NON-NLS-1$
		IconUtilitities.maybeSetIcon(printPdf, "Print16.gif"); //$NON-NLS-1$
		printPdf.setEnabled(true);
		c = new GridBagConstraints();
		Utility.buildConstraints(c, 4, 0, 1, 1, 0.0, 0.0);
		c.insets = new Insets(1, 2, 1, 2);
		printPdf.setMargin(new Insets(1, 14, 1, 14));
		ssPanel.add(printPdf, c);
		return ssPanel;
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

		selectedBookList.clear();
		selectedBookList.add(Globals.INNATE_SPELL_BOOK_NAME);
		selectedBookList.add(Globals.getDefaultSpellBook());
		for (SpellBook book : pc.getSpellBooks())
		{
			// build spell book list
			if (book.getType() == SpellBook.TYPE_INNATE_SPELLS)
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
						GuiConstants.INFOSPELLS_AVAIL_SPELL_LIST, pc, this, ""); //$NON-NLS-1$
		}
		else
		{
			availableModel.resetModel(primaryViewMode, secondaryViewMode, true,
				availableBookList, currSpellBook,
				GuiConstants.INFOSPELLS_AVAIL_SPELL_LIST, this, ""); //$NON-NLS-1$
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
						this, ""); //$NON-NLS-1$
		}
		else
		{
			selectedModel.resetModel(primaryViewSelectMode,
				secondaryViewSelectMode, false, selectedBookList,
				currSpellBook, GuiConstants.INFOSPELLS_AVAIL_KNOWN, this, ""); //$NON-NLS-1$
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
				}
				setInfoLabelText((SpellInfo) fNode.getItem());
			}
		}
		else
		{
			addSpellButton.setEnabled(false);
			addMenu.setEnabled(false);
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
			bookName = Globals.getDefaultSpellBook();
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

		TreePath[] avaCPaths = availableTable.getTree().getSelectionPaths();

		for (int index = avaCPaths.length - 1; index >= 0; --index)
		{
			Object aComp = avaCPaths[index].getLastPathComponent();
			PObjectNode fNode = (PObjectNode) aComp;

			boolean result = addSpellToTarget(fNode, bookName);
			if (result && !"".equals(pc.getSpellBookNameToAutoAddKnown())) //$NON-NLS-1$
			{
				addSpellToTarget(fNode, pc.getSpellBookNameToAutoAddKnown());
			}
		}

		pc.setDirty(true);

		// reset selected spellbook model
		updateSelectedModel();
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
					currSpellBook = bookName;

					final String aString = pc.delSpell(si, aClass, bookName);

					if (aString.length() > 0)
					{
						ShowMessageDelegate.showMessageDialog(aString,
							Constants.APPLICATION_NAME, MessageType.ERROR);
					}

					// As we are deleting this from known spells we need to 
					// remove it from any prepared spell lists too.
					removeFromPreparedLists(cs, aClass);
				}
			}
		}
		pc.setDirty(true);
		updateSelectedModel();
	}

	/**
	 * Remove the spell from all prepared lists, generally used 
	 * when removing the spell from the known list
	 * 
	 * @param cs the spell to be removed.
	 * @param aClass The class to remove the spell from
	 */
	private void removeFromPreparedLists(CharacterSpell cs, PCClass aClass)
	{
		List<SpellInfo> il = new ArrayList<SpellInfo>();
		il.addAll(cs.getInfoList());
		for (SpellInfo si : il)
		{
			if (pc.getSpellBookByName(si.getBook()).getType() == SpellBook.TYPE_PREPARED_LIST)
			{
				cs.removeSpellInfo(si);
			}
		}
		pc.removeCharacterSpell(aClass, cs);
	}

	/**
	 *  Select a spell output sheet
	 */
	private void selectSpellSheetButton()
	{
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(LanguageBundle
			.getString("InfoSpells.select.output.sheet")); //$NON-NLS-1$
		fc.setCurrentDirectory(SettingsHandler.getPcgenOutputSheetDir());
		fc.setSelectedFile(new File(SettingsHandler.getSelectedSpellSheet()));

		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			SettingsHandler.setSelectedSpellSheet(fc.getSelectedFile()
				.getAbsolutePath());
			selectSpellSheetField.setText(SettingsHandler
				.getSelectedSpellSheetName());
			selectSpellSheetField.setToolTipText(SettingsHandler
				.getSelectedSpellSheetName());
		}
	}

	/**
	 * Exports Spell using the selected output sheet to a file
	 */
	private void exportSpellsToFile()
	{
		final String template = SettingsHandler.getSelectedSpellSheet();
		String ext = template.substring(template.lastIndexOf('.'));

		JFileChooser fcExport = new JFileChooser();
		fcExport.setCurrentDirectory(SettingsHandler.getPcgPath());

		fcExport.setDialogTitle(LanguageBundle
			.getString("InfoSpells.export.spells.for") + pc.getDisplayName()); //$NON-NLS-1$

		if (fcExport.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
		{
			return;
		}

		final String aFileName = fcExport.getSelectedFile().getAbsolutePath();

		if (aFileName.length() < 1)
		{
			ShowMessageDelegate
				.showMessageDialog(
					LanguageBundle.getString("InfoSpells.must.set.filename"), "PCGen", MessageType.ERROR); //$NON-NLS-1$ //$NON-NLS-2$

			return;
		}

		try
		{
			final File outFile = new File(aFileName);

			if (outFile.isDirectory())
			{
				ShowMessageDelegate
					.showMessageDialog(
						LanguageBundle
							.getString("InfoSpells.can.not.overwrite.directory"), "PCGen", MessageType.ERROR); //$NON-NLS-1$ //$NON-NLS-2$

				return;
			}

			if (outFile.exists())
			{
				int reallyClose =
						JOptionPane
							.showConfirmDialog(
								this,
								LanguageBundle
									.getFormattedString(
										"InfoSpells.confirm.overwrite", outFile.getName()), //$NON-NLS-1$
								LanguageBundle
									.getFormattedString(
										"InfoSpells.overwriting", outFile.getName()), JOptionPane.YES_NO_OPTION); //$NON-NLS-1$

				if (reallyClose != JOptionPane.YES_OPTION)
				{
					return;
				}
			}

			if (ext.equalsIgnoreCase(".htm") || ext.equalsIgnoreCase(".html")) //$NON-NLS-1$ //$NON-NLS-2$
			{
				BufferedWriter w =
						new BufferedWriter(new OutputStreamWriter(
							new FileOutputStream(outFile), "UTF-8")); //$NON-NLS-1$
				Utility.printToWriter(w, template, pc);
			}
			else if (ext.equalsIgnoreCase(".fo") || ext.equalsIgnoreCase(".pdf")) //$NON-NLS-1$ //$NON-NLS-2$
			{
				File tmpFile = File.createTempFile("tempSpells_", ".fo"); //$NON-NLS-1$ //$NON-NLS-2$
				BufferedWriter w =
						new BufferedWriter(new OutputStreamWriter(
							new FileOutputStream(tmpFile), "UTF-8")); //$NON-NLS-1$
				Utility.printToWriter(w, template, pc);

				pdfExport(outFile, tmpFile, null);
			}
			else if (ext.equalsIgnoreCase(".xslt") || ext.equalsIgnoreCase(".xsl")) //$NON-NLS-1$ //$NON-NLS-2$
			{
				Logging.debugPrint("Printing using XML/XSLT");
				File tmpFile = File.createTempFile("tempSpells_", ".xml"); //$NON-NLS-1$ //$NON-NLS-2$
				BufferedWriter w =
					new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(tmpFile), "UTF-8")); //$NON-NLS-1$
				File baseTemplate = new File(SettingsHandler.getPcgenSystemDir() + File.separator + "gameModes" + File.separator + SettingsHandler.getGame().getName() + File.separator + "base.xml");
				if(!baseTemplate.exists()) {
					baseTemplate = new File(SettingsHandler.getPcgenOutputSheetDir() + File.separator + "base.xml");
				}
				Utility.printToWriter(w, baseTemplate.getAbsolutePath(), pc);

				File xsltFile = new File(template);
				pdfExport(outFile, tmpFile, xsltFile);
			}
		}
		catch (Exception ex)
		{
			Logging.errorPrint(LanguageBundle.getFormattedString(
				"InfoSpells.export.failed", pc.getDisplayName()), ex); //$NON-NLS-1$
			ShowMessageDelegate.showMessageDialog(LanguageBundle
				.getFormattedString("InfoSpells.export.failed.retry", //$NON-NLS-1$
					pc.getDisplayName()), "PCGen", //$NON-NLS-1$
				MessageType.ERROR);
		}
	}

	/**
	 * Export to PDF using the FOP PDF generator. 
	 * 
	 * @param outFile The file to place the output in.
	 * @param tmpFile The file containing the definition of the character data. May be FO or XML. 
	 * @param xsltFile An optional XSLT file for use when the tmpFile is in XML.
	 */
	private void pdfExport(final File outFile, File tmpFile, File xsltFile)
	{
		FOPHandler fh = new FOPHandler();

		// setting up pdf renderer
		fh.setMode(FOPHandler.PDF_MODE);
		if (xsltFile != null)
		{
			fh.setInputFile(tmpFile, xsltFile);
		}
		else
		{
			fh.setInputFile(tmpFile);
		}
		fh.setOutputFile(outFile);

		// render to awt
		fh.run();

		tmpFile.deleteOnExit();

		String errMessage = fh.getErrorMessage();

		if (errMessage.length() > 0)
		{
			ShowMessageDelegate.showMessageDialog(errMessage,
				"PCGen", MessageType.ERROR); //$NON-NLS-1$
		}
	}

}
