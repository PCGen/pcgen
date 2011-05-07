/*
 * InfoTemplate.java
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.JButton;
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
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.tree.TreePath;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.RaceType;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.cdom.enumeration.Type;
import pcgen.core.Globals;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.analysis.OutputNameFormatting;
import pcgen.core.analysis.TemplateModifier;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.PrerequisiteUtilities;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui.CharacterInfo;
import pcgen.gui.GuiConstants;
import pcgen.gui.PCGen_Frame1;
import pcgen.gui.TableColumnManager;
import pcgen.gui.TableColumnManagerModel;
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
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;
import pcgen.util.enumeration.Tab;
import pcgen.util.enumeration.Visibility;

/**
 *  <code>InfoRace</code> creates a new tabbed panel
 *  with all the race and template information on it
 *
 * @author  Bryan McRoberts (merton_monk@yahoo.com)
 * @version $Revision: 198 $
 **/
public class InfoTemplates extends BaseCharacterInfoTab
{
	//	static final long serialVersionUID = 2565545289875422981L;

	private static final Tab tab = Tab.TEMPLATES;

	//	private static boolean needsUpdate = true;

	//Available Table
	private JLabel sortLabel =
			new JLabel(PropertyFactory.getString("in_irSortTempl"));
	private JComboBoxEx viewComboBox = new JComboBoxEx();
	private int viewMode = 0;
	private final JLabel lblQFilter = new JLabel("Filter:");
	private JTextField textQFilter = new JTextField();
	private JButton clearQFilterButton = new JButton("Clear");
	private static Integer saveViewMode = null;
	private JButton addButton;

	private TemplatesTableModel availableModel;
	private JTreeTableSorter availableSort = null;
	private JScrollPane availablePane;
	private JTreeTable availableTable;
	private JTree availableTree = null;

	//Selected Table
	private JLabel selSortLabel =
			new JLabel(PropertyFactory.getString("in_irSortTemplSel"));
	private JComboBoxEx viewSelComboBox = new JComboBoxEx();
	private int viewSelMode = 0;
	private JButton removeButton;

	private TemplatesTableModel selectedModel;
	private JTreeTableSorter selectedSort = null;
	private JScrollPane selectedPane;
	private JTreeTable selectedTable;
	private JTree selectedTree = null;

	//Other UI Elements
	private FlippingSplitPane split;
	private FlippingSplitPane bsplit;
	private JLabelPane infoLabel = new JLabelPane();
	private JPanel botPane = new JPanel();
	private JPanel topPane = new JPanel();
	private TreePath selPath;
	private static PObjectNode typeRoot;
	private static PObjectNode sourceRoot;

	// other
	private boolean hasBeenSized = false;
	private PCTemplate lastTemplate = null; //keep track of which PCTemplate was last selected from either table

	//Character pane elements
	//	private PlayerCharacter pc;
	//	private int serial = 0;
	//	private boolean readyForRefresh = false;

	/**
	 * Constructor
	 * @param pc
	 */
	public InfoTemplates(final PlayerCharacter pc)
	{
		super(pc);

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				initComponents();
				initActionListeners();
			}
		});
	}

	private void initActionListeners()
	{
		addFocusListener(new FocusAdapter()
		{
			public void focusGained(FocusEvent evt)
			{
				refresh();
			}
		});
		split.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
			new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent evt)
				{
					if (hasBeenSized)
					{
						int s = split.getDividerLocation();

						if (s > 0)
						{
							SettingsHandler.setPCGenOption(
								"InfoTemplates.asplit", s);
						}

						s = bsplit.getDividerLocation();

						if (s > 0)
						{
							SettingsHandler.setPCGenOption(
								"InfoTemplates.bsplit", s);
						}
					}
				}
			});
		removeButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				removeTemplate();
			}
		});
		addButton.addActionListener(new ActionListener()
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
		viewSelComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				viewSelComboBoxActionPerformed();
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
		availableTable.getSelectionModel().addListSelectionListener(
			new AvailableListSelectionListener());
		selectedTable.getSelectionModel().addListSelectionListener(
			new SelectedListSelectionListener());
		availableTable.addMouseListener(new JTreeTableMouseAdapter(
			availableTable, new AvailableClickHandler(), false));
		selectedTable.addMouseListener(new JTreeTableMouseAdapter(
			selectedTable, new SelectedClickHandler(), false));
		FilterFactory.restoreFilterSettings(this);
	}

	/**
	 * This method is called from within the
	 * constructor to initialize the form
	 **/
	private void initComponents()
	{
		typeRoot = new PObjectNode();
		sourceRoot = new PObjectNode();

		List<String> typeList = new ArrayList<String>();
		List<String> sourceList = new ArrayList<String>();

		for (PCTemplate template : Globals.getContext().ref.getConstructedCDOMObjects(PCTemplate.class))
		{
			for (Type type : template.getTrueTypeList(false))
			{
				String aType = type.toString();
				if (!typeList.contains(aType))
				{
					typeList.add(aType);
				}
			}
			final String sourceString = SourceFormat.getFormattedString(
					template, SourceFormat.LONG, false);
			if (sourceString.length() == 0)
			{
				Logging.errorPrint("PC template " + template.getDisplayName()
					+ " has no source long entry.");
			}
			else if (!sourceList.contains(sourceString))
			{
				sourceList.add(sourceString);
			}
		}

		Collections.sort(typeList);
		if (!typeList.contains(PropertyFactory.getString("in_other")))
		{
			typeList.add(PropertyFactory.getString("in_other"));
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

		//
		// View List Sanity check
		//
		int iView = SettingsHandler.getTemplateTab_ListMode();
		if (iView >= GuiConstants.INFOTEMPLATE_VIEW_NAME
			&& iView <= GuiConstants.INFOTEMPLATE_VIEW_SOURCE_NAME)
		{
			viewMode = iView;
		}
		SettingsHandler.setTemplateTab_ListMode(viewMode);
		viewComboBox.addItem(PropertyFactory.getString("in_nameLabel"));
		viewComboBox.addItem(PropertyFactory.getString("in_typeName"));
		viewComboBox.addItem(PropertyFactory.getString("in_sourceName"));
		viewComboBox.setSelectedIndex(viewMode);

		iView = SettingsHandler.getTemplateSelTab_ListMode();
		if (iView >= GuiConstants.INFOTEMPLATE_VIEW_NAME
				&& iView <= GuiConstants.INFOTEMPLATE_VIEW_SOURCE_NAME)
		{
			viewSelMode = iView;
		}
		SettingsHandler.setTemplateSelTab_ListMode(viewSelMode);
		viewSelComboBox.addItem(PropertyFactory.getString("in_nameLabel"));
		viewSelComboBox.addItem(PropertyFactory.getString("in_typeName"));
		viewSelComboBox.addItem(PropertyFactory.getString("in_sourceName"));
		viewSelComboBox.setSelectedIndex(viewSelMode);

		createModels();
		createTreeTables();

		buildTopPanel();

		buildBottomPanel();

		//----------------------------------------------------------------------
		// now split the top and bottom panels
		bsplit =
				new FlippingSplitPane(JSplitPane.VERTICAL_SPLIT, topPane,
					botPane);
		bsplit.setOneTouchExpandable(true);
		bsplit.setDividerSize(10);

		// now add all the panes (centered of course)
		this.setLayout(new BorderLayout());
		this.add(bsplit, BorderLayout.CENTER);

		// add the sorter so that clicking on the TableHeader actually does something
		availableSort =
				new JTreeTableSorter(availableTable,
					(PObjectNode) availableModel.getRoot(), availableModel);
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

	/**
	 * Build the top panel.
	 * topPane which will contain leftPane and rightPane
	 * leftPane will have two panels and a scrollregion
	 * rightPane will have one panel and a scrollregion
	 */
	private void buildTopPanel()
	{
		//-----------------------------------------------------------------------
		// build the topPane which will contain leftPane and rightPane
		// leftPane will have a panel and a scrollregion
		// rightPane will have a single panel
		//-----------------------------------------------------------------------

		//-----------------------------------------------------------------------
		//  Top Panel
		//  - this has all the Template stuff in it
		//-----------------------------------------------------------------------

		topPane.setLayout(new BorderLayout());

		JPanel leftPane = new JPanel();
		JPanel rightPane = new JPanel();
		leftPane.setLayout(new BorderLayout());
		rightPane.setLayout(new BorderLayout());

		split =
				new FlippingSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane,
					rightPane);
		split.setOneTouchExpandable(true);
		split.setDividerSize(10);

		topPane.add(split, BorderLayout.CENTER);

		//-------------------------------------------------------------
		//  Top Left Pane
		//  - available templates

		// Header
		leftPane.add(InfoTabUtils.createFilterPane(sortLabel, viewComboBox,
			lblQFilter, textQFilter, clearQFilterButton), BorderLayout.NORTH);

		// Data - All Available Templates Table
		availablePane =
				new JScrollPane(availableTable,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JButton columnButton = new JButton();
		availablePane.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER,
			columnButton);
		columnButton.setText("^");
		new TableColumnManager(availableTable, columnButton, availableModel);

		leftPane.add(availablePane, BorderLayout.CENTER);

		JPanel bottomLeftPanel = new JPanel();
		addButton = new JButton(IconUtilitities.getImageIcon("Forward16.gif"));
		Utility.setDescription(addButton, PropertyFactory
			.getString("in_irTemplAddTip"));
		addButton.setEnabled(true);
		bottomLeftPanel.add(addButton);
		leftPane.add(bottomLeftPanel, BorderLayout.SOUTH);

		//-------------------------------------------------------------
		//  Top Right Pane
		//  - selected templates

		// Header
		rightPane.add(InfoTabUtils.createFilterPane(selSortLabel,
			viewSelComboBox, null, null, null), BorderLayout.NORTH);

		// Data - Selected Templates table
		selectedPane =
				new JScrollPane(selectedTable,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JButton columnButton2 = new JButton();
		selectedPane.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER,
			columnButton2);
		columnButton2.setText("^");
		new TableColumnManager(selectedTable, columnButton2, selectedModel);
		rightPane.add(selectedPane, BorderLayout.CENTER);

		JPanel rightBottomPanel = new JPanel();
		removeButton = new JButton(IconUtilitities.getImageIcon("Back16.gif"));
		Utility.setDescription(removeButton, PropertyFactory
			.getString("in_irTemplRemoveTip"));
		removeButton.setEnabled(true);
		rightBottomPanel.add(removeButton);
		rightPane.add(rightBottomPanel, BorderLayout.SOUTH);
	}

	private void buildBottomPanel()
	{
		JPanel mainPane = new JPanel();
		mainPane.setLayout(new BorderLayout());

		botPane.setLayout(new BorderLayout());
		botPane.add(mainPane, BorderLayout.CENTER);

		//-------------------------------------------------------------
		//  Bottom Pane
		//  - Template Info

		JScrollPane scroll = new JScrollPane();

		TitledBorder title1 =
				BorderFactory.createTitledBorder(PropertyFactory
					.getString("in_irTemplateInfo"));
		title1.setTitleJustification(TitledBorder.CENTER);
		scroll.setBorder(title1);
		infoLabel.setBackground(topPane.getBackground());
		scroll.setViewportView(infoLabel);
		mainPane.add(scroll);
	}

	/**
	 * Returns the <tt>Tab</tt> enum associated with this Tab.
	 * 
	 * @return Tab
	 * 
	 * @see pcgen.gui.tabs.BaseCharacterInfoTab#getTab()
	 */
	@Override
	public Tab getTab()
	{
		return tab;
	}

	/**
	 * @see pcgen.gui.tabs.BaseCharacterInfoTab#getTabOrder()
	 */
	@Override
	public int getTabOrder()
	{
		return SettingsHandler.getPCGenOption(
			".Panel.Race.Order", tab.ordinal()); //$NON-NLS-1$
	}

	/**
	 * @see pcgen.gui.tabs.BaseCharacterInfoTab#setTabOrder(int)
	 */
	@Override
	public void setTabOrder(int order)
	{
		SettingsHandler.setPCGenOption(".Panel.Race.Order", order);
	}

	/**
	 * Retrieve the list of tasks to be done on the tab.
	 * @return List of task descriptions as Strings.
	 */
	@Override
	public List<String> getToDos()
	{
		return Collections.emptyList();
	}

	//	/**
	//	 * Set needs update flag for templates tab
	//	 * @param b
	//	 */
	//	public static void setNeedsUpdate(boolean b)
	//	{
	//		needsUpdate = b;
	//	}

	/**
	 * Specifies whether the "match any" option should be available.
	 * 
	 * @return true
	 * 
	 * @see pcgen.gui.filter.FilterAdapterPanel#isMatchAnyEnabled()
	 */
	@Override
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
	 * Specifies whether the "negate/reverse" option should be available.
	 * 
	 * @return true
	 * 
	 * @see pcgen.gui.filter.FilterAdapterPanel#isNegateEnabled()
	 */
	@Override
	public final boolean isNegateEnabled()
	{
		return true;
	}

	/**
	 * Specifies the filter selection mode.
	 * 
	 * @return FilterConstants.MULTI_MULTI_MODE = 2
	 * 
	 * @see pcgen.gui.filter.FilterAdapterPanel#getSelectionMode()
	 */
	@Override
	public final int getSelectionMode()
	{
		return FilterConstants.MULTI_MULTI_MODE;
	}

	/**
	 * Registers the appropriate filters for use with this tab.
	 * 
	 * <p>Registers Source, Size, Race, and Alignment Prereq filters.
	 * 
	 * @see pcgen.gui.filter.FilterAdapterPanel#initializeFilters()
	 */
	@Override
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
	@Override
	public final void refreshFiltering()
	{
		availableModel.resetModel(viewMode, true);
		selectedModel.resetModel(viewSelMode, false);
	}

	private void setInfoLabelText(PCTemplate temp, PObjectNode pn)
	{
		lastTemplate = temp; //even if that's null

		final InfoLabelTextBuilder b = new InfoLabelTextBuilder();

		if ((temp != null))
		{
			b.appendTitleElement(OutputNameFormatting.piString(temp, false));
			b.appendLineBreak();

			RaceType rt = temp.get(ObjectKey.RACETYPE);
			if (rt != null)
			{
				b.appendI18nElement("in_irInfoRaceType",rt.toString()); //$NON-NLS-1$
			}

			if (temp.getType().length() > 0)
			{
				b.appendSpacer();
				b.appendI18nElement("in_irInfoType", temp.getType()); //$NON-NLS-1$
			}

			String bString = PrerequisiteUtilities.preReqHTMLStringsForList(getPc(), null,
			temp.getPrerequisiteList(), false);
			if (bString.length() > 0)
			{
				b.appendLineBreak();
				b.appendI18nElement("in_requirements", bString); //$NON-NLS-1$
			}

			bString = SourceFormat.getFormattedString(temp,
			Globals.getSourceDisplay(), true);
			if (bString.length() > 0)
			{
				b.appendLineBreak();
				b.appendI18nElement("in_sourceLabel", bString); //$NON-NLS-1$
			}
		}

		infoLabel.setText(b.toString());
	}

	/**
	 * This is called when the tab is shown
	 **/
	private void formComponentShown()
	{
		requestFocus();
		PCGen_Frame1.setMessageAreaTextWithoutSaving(PropertyFactory
			.getString("in_irSelectTemplate"));
		refresh();

		int width;
		int t = bsplit.getDividerLocation();
		int u = split.getDividerLocation();

		if (!hasBeenSized)
		{
			t =
					SettingsHandler.getPCGenOption("InfoTemplate.bsplit",
						(int) (InfoTemplates.this.getSize().getHeight() - 120));
			u =
					SettingsHandler
						.getPCGenOption(
							"InfoTemplate.asplit",
							(int) ((InfoTemplates.this.getSize().getWidth() * 75.0) / 100.0));

			// set the prefered width on allTemplatesTable
			for (int i = 0; i < availableTable.getColumnCount(); i++)
			{
				TableColumn sCol = availableTable.getColumnModel().getColumn(i);
				width = Globals.getCustColumnWidth("Template", i);

				if (width != 0)
				{
					sCol.setPreferredWidth(width);
				}

				sCol.addPropertyChangeListener(new ResizeColumnListener(
					availableTable, "Template", i));
			}
			hasBeenSized = true;
		}

		if (t > 0)
		{
			bsplit.setDividerLocation(t);
			SettingsHandler.setPCGenOption("InfoRace.bsplit", t);
		}

		if (u > 0)
		{
			split.setDividerLocation(u);
			SettingsHandler.setPCGenOption("InfoRace.asplit", u);
		}
	}

	private void hookupPopupMenu(JTreeTable treeTable)
	{
		treeTable.addMouseListener(new TemplatePopupListener(treeTable,
			new TemplatePopupMenu(treeTable)));
	}

	private void viewComboBoxActionPerformed()
	{
		final int index = viewComboBox.getSelectedIndex();

		if (index != viewMode)
		{
			viewMode = index;
			SettingsHandler.setTemplateTab_ListMode(viewMode);
			updateAvailableModel();
		}
	}

	private void viewSelComboBoxActionPerformed()
	{
		final int index = viewSelComboBox.getSelectedIndex();

		if (index != viewSelMode)
		{
			viewSelMode = index;
			SettingsHandler.setTemplateSelTab_ListMode(viewSelMode);
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
		viewMode = GuiConstants.INFOTEMPLATE_VIEW_NAME;
		availableModel.resetModel(viewMode, true);
		clearQFilterButton.setEnabled(true);
		viewComboBox.setEnabled(false);
		forceRefresh();
	}

	/**
	 * <p>Handles the action from <code>rightButton</code>.  Adds the currently selected template
	 * to the character if the character is qualified.</p>
	 * <p>Forces update of all tabs by calling <code>forceUpdate()</code>, and updates
	 * <code>allTemplatesDataModel</code> to refresh template or other dependancies.</p>
	 */
	private void addTemplate()
	{
		PCTemplate template = getSelectedTemplate();

		if ((template == null) || !template.qualifies(getPc(), template))
		{
			return;
		}

		getPc().setDirty(true);

		if (!getPc().hasTemplate(template))
		{
			getPc().addTemplate(template);
			pushUpdate();
			availableModel.resetModel(viewMode, true);
		}
		else
		{
			JOptionPane.showMessageDialog(null, PropertyFactory
				.getString("in_irHaveTemplate"));
		}

		forceRefresh();
	}

	/**
	 * <p>Handles the action from <code>leftButton</code>.  Removes the currently selected template
	 * from the character if the template is removeable.</p>
	 * <p>Forces update of all tabs by calling <code>forceUpdate()</code>, and updates
	 * <code>allTemplatesDataModel</code> to refresh template or other dependancies.</p>
	 */
	private void removeTemplate()
	{
		PCTemplate template = getSelectedTemplate();

		//Change for FIX 1577347
		//if ((template == null) || !template.isQualified(getPc()))
		if (template == null)
		{
			return;
		}

		getPc().setDirty(true);

		if (getPc().hasTemplate(template))
		{
			getPc().removeTemplate(template);
			pushUpdate();
			availableModel.resetModel(viewMode, true);
			selectedModel.resetModel(viewSelMode, false);
		}
		else
		{
			JOptionPane.showMessageDialog(null, PropertyFactory
				.getString("in_irNotRemovable"));
		}

		forceRefresh();
	}

	private PCTemplate getSelectedTemplate()
	{
		if (lastTemplate == null)
		{
			ShowMessageDelegate.showMessageDialog(PropertyFactory
				.getString("in_irNoTemplate"), Constants.APPLICATION_NAME,
				MessageType.ERROR);
		}

		return lastTemplate;
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

	/**
	 * Creates the ClassModel that will be used.
	 */
	private void createModels()
	{
		createSelectedModel();
		createAvailableModel();
	}

	private void createAvailableModel()
	{
		if (availableModel == null)
		{
			availableModel = new TemplatesTableModel(viewMode, true);
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

	private void createSelectedModel()
	{
		if (selectedModel == null)
		{
			selectedModel = new TemplatesTableModel(viewSelMode, false);
		}
		else
		{
			selectedModel.resetModel(viewSelMode, false);
		}

		if (selectedSort != null)
		{
			selectedSort.setRoot((PObjectNode) selectedModel.getRoot());
			selectedSort.sortNodeOnColumn();
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
		List<String> pathList = selectedTable.getExpandedPaths();
		createSelectedModel();
		selectedTable.updateUI();
		selectedTable.expandPathList(pathList);
	}

	/**
	 * This recalculates the states of everything based
	 * upon the currently selected character
	 */
	@Override
	public final void updateCharacterInfo()
	{
		if (!needsUpdate())
		{
			return;
		}

		updateAvailableModel();
		updateSelectedModel();

		setNeedsUpdate(false);
	}

	/**
	 *
	 * A TableModel to handle the full list of templates.
	 * It pulls its data straight from Globals.getTemplateList()
	 *
	 **/
	private final class TemplatesTableModel extends AbstractTreeTableModel
			implements TableColumnManagerModel
	{
		static final long serialVersionUID = 2565545289875422981L;
		private static final int COL_NAME = 0;
		private static final int COL_LEVEL = 1;
		private static final int COL_MODIFIER = 2;
		private static final int COL_REQS = 3;
		private static final int COL_SRC = 4;

		private final String[] COL_NAMES =
				new String[]{PropertyFactory.getString("in_nameLabel"),
					PropertyFactory.getString("in_lvlAdj"),
					PropertyFactory.getString("in_modifier"),
					PropertyFactory.getString("in_preReqs"),
					PropertyFactory.getString("in_source")};

		private final int[] COL_DEFAULT_WIDTH = {200, 35, 35, 100, 100};
		private int modelType = 0; // availableModel=0,selectedModel=1
		private List<Boolean> displayList = null;

		private TemplatesTableModel(int mode, boolean available)
		{
			super(null);
			if (!available)
			{
				modelType = 1;
			}
			resetModel(viewMode, available);
			displayList = new ArrayList<Boolean>();
			displayList.add(Boolean.TRUE);
			if (available)
			{
				displayList.add(Boolean.valueOf(getColumnViewOption(modelType
					+ "." + COL_NAMES[1], true)));
				displayList.add(Boolean.valueOf(getColumnViewOption(modelType
					+ "." + COL_NAMES[2], true)));
				displayList.add(Boolean.valueOf(getColumnViewOption(modelType
					+ "." + COL_NAMES[3], true)));
			}
			else
			{
				displayList.add(Boolean.valueOf(getColumnViewOption(modelType
					+ "." + COL_NAMES[1], false)));
				displayList.add(Boolean.valueOf(getColumnViewOption(modelType
					+ "." + COL_NAMES[2], false)));
				displayList.add(Boolean.valueOf(getColumnViewOption(modelType
					+ "." + COL_NAMES[3], false)));
			}
			displayList.add(Boolean.valueOf(getColumnViewOption(modelType + "."
				+ COL_NAMES[4], false)));
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

				case COL_LEVEL:
					return Integer.class;

				case COL_MODIFIER:
				case COL_REQS:
				case COL_SRC:
					return String.class;

				default:
					Logging.errorPrintLocalised("in_irIREr4",column);

					break;
			}

			return String.class;
		}

		/**
		 * @return the number of columns
		 */
		public int getColumnCount()
		{
			return COL_NAMES.length;
		}

		/**
		 * @param columnIndex the index of the column name to retrieve
		 * @return the name.. of the specified column
		 */
		public String getColumnName(int columnIndex)
		{
			return ((columnIndex >= 0) && (columnIndex < COL_NAMES.length))
				? COL_NAMES[columnIndex] : "Out Of Bounds";
		}

		/**
		 * Returns the root node of the tree
		 * @return the root node
		 */
		public Object getRoot()
		{
			return super.getRoot();
		}

		/**
		 * There must be a root object, though it can be hidden
		 * @param aNode - the root node
		 */
		private void setRoot(PObjectNode aNode)
		{
			super.setRoot(aNode);
		}

		/**
		 * @param node
		 * @param columnIndex the column of the cell to retrieve
		 * @return the value of the cell
		 */
		public Object getValueAt(Object node, int columnIndex)
		{
			final PObjectNode fn = (PObjectNode) node;
			PCTemplate template = null;

			if ((fn != null) && (fn.getItem() instanceof PCTemplate))
			{
				template = (PCTemplate) fn.getItem();
			}

			if (template != null)
			{
				switch (columnIndex)
				{
					case COL_NAME:
						return template.toString();

					case COL_LEVEL:
						return template.getSafe(FormulaKey.LEVEL_ADJUSTMENT)
							.resolve(getPc(), "");

					case COL_MODIFIER:
						return TemplateModifier.modifierString(template, getPc());

					case COL_REQS:
						return PrereqHandler.toHtmlString(template.getPrerequisiteList());

					case COL_SRC:
						return SourceFormat.getFormattedString(template,
						Globals.getSourceDisplay(), true);

					default:
						Logging
							.errorPrint("In InfoTemplates.AllTemplatesTableModel.getValueAt the column "
								+ columnIndex + " is not supported.");
						break;
				}
			}

			return null;
		}

		/**
		 * This assumes the TemplateModel exists
		 * but needs to be repopulated
		 * @param mode
		 * @param available
		 **/
		private void resetModel(int mode, boolean available)
		{
			Collection<PCTemplate> templList;

			if (available)
			{
				templList = Globals.getContext().ref.getConstructedCDOMObjects(PCTemplate.class);
			}
			else
			{
				templList = getPc().getTemplateSet();
			}

			switch (mode)
			{
				case GuiConstants.INFOTEMPLATE_VIEW_NAME: // Name
					createNameViewModel(templList);
					break;

				case GuiConstants.INFOTEMPLATE_VIEW_TYPE_NAME: // type/name
					createTypeViewModel(templList);
					break;

				case GuiConstants.INFOTEMPLATE_VIEW_SOURCE_NAME: // source/name
					createSourceViewModel(templList);
					break;

				default:
					Logging.errorPrintLocalised("in_irIREr1",mode);

					break;
			}

			PObjectNode rootAsPObjectNode = (PObjectNode) super.getRoot();

			if (rootAsPObjectNode.getChildCount() > 0)
			{
				fireTreeNodesChanged(super.getRoot(), new TreePath(super
					.getRoot()));
			}
		}

		private void createNameViewModel(Collection<PCTemplate> templList)
		{
			setRoot(new PObjectNode()); // just need a blank one
			String qFilter = this.getQFilter();

			for (PCTemplate template : templList)
			{
				// in the availableTable, if filtering out unqualified items
				// ignore any class the PC doesn't qualify for
				if (!shouldDisplayThis(template))
				{
					continue;
				}

				if (qFilter == null
					|| (template.getDisplayName().toLowerCase()
						.indexOf(qFilter) >= 0 || template.getType()
						.toLowerCase().indexOf(qFilter) >= 0))
				{
					PObjectNode aFN = new PObjectNode();
					aFN.setParent((PObjectNode) super.getRoot());
					aFN.setItem(template);
					PrereqHandler.passesAll(template.getPrerequisiteList(), getPc(),
						template);
					((PObjectNode) super.getRoot()).addChild(aFN);
				}
			}

		}

		private void createTypeViewModel(Collection<PCTemplate> templList)
		{
			setRoot(typeRoot.clone());

			for (PCTemplate template : templList)
			{
				// in the availableTable, if filtering out unqualified items
				// ignore any class the PC doesn't qualify for
				if (!shouldDisplayThis(template))
				{
					continue;
				}

				PObjectNode rootAsPObjectNode = (PObjectNode) super.getRoot();
				boolean added = false;

				for (int i = 0; i < rootAsPObjectNode.getChildCount(); i++)
				{
					if ((!added && (i == (rootAsPObjectNode.getChildCount() - 1)))
						|| template.isType((rootAsPObjectNode.getChildren()
							.get(i)).getItem().toString()))
					{
						PObjectNode aFN = new PObjectNode();
						aFN.setParent(rootAsPObjectNode.getChild(i));
						aFN.setItem(template);
						PrereqHandler.passesAll(template.getPrerequisiteList(),
							getPc(), template);
						rootAsPObjectNode.getChild(i).addChild(aFN);
						added = true;
					}
				}
			}
		}

		private void createSourceViewModel(Collection<PCTemplate> templList)
		{
			setRoot(sourceRoot.clone());

			for (PCTemplate template : templList)
			{
				// in the availableTable, if filtering out unqualified items
				// ignore any class the PC doesn't qualify for
				if (!shouldDisplayThis(template))
				{
					continue;
				}

				PObjectNode rootAsPObjectNode = (PObjectNode) super.getRoot();
				boolean added = false;

				for (int i = 0; i < rootAsPObjectNode.getChildCount(); i++)
				{
					final String sourceString = SourceFormat.getFormattedString(
							template, SourceFormat.LONG, false);
					if (sourceString.length() != 0)
					{
						if ((!added && (i == (rootAsPObjectNode.getChildCount() - 1)))
							|| sourceString.equals((rootAsPObjectNode
								.getChildren().get(i)).getItem().toString()))
						{
							PObjectNode aFN = new PObjectNode();
							aFN.setParent(rootAsPObjectNode.getChild(i));
							aFN.setItem(template);
							PrereqHandler.passesAll(template.getPrerequisiteList(),
								getPc(), template);
							rootAsPObjectNode.getChild(i).addChild(aFN);
							added = true;
						}
					}
					else
					{
						Logging.errorPrint("PC template "
							+ template.getDisplayName()
							+ " has no source long entry.");
					}
				}
			}
		}

		/**
		 * return a boolean to indicate if the item should be included in the list.
		 * Only Weapon, Armor and Shield type items should be checked for proficiency.
		 * @param template
		 * @return true if it should be displayed
		 */
		private boolean shouldDisplayThis(final PCTemplate template)
		{
			return ((template.getSafe(ObjectKey.VISIBILITY) == Visibility.DEFAULT || template.getSafe(ObjectKey.VISIBILITY) == Visibility.DISPLAY_ONLY) && accept(getPc(),
				template));
		}

		public List<String> getMColumnList()
		{
			List<String> retList = new ArrayList<String>();
			for (int i = 1; i < COL_NAMES.length; i++)
			{
				retList.add(COL_NAMES[i]);
			}
			return retList;
		}

		public boolean isMColumnDisplayed(int col)
		{
			return (displayList.get(col)).booleanValue();
		}

		public void setMColumnDisplayed(int col, boolean disp)
		{
			setColumnViewOption(modelType + "." + COL_NAMES[col], disp);
			displayList.set(col, Boolean.valueOf(disp));
		}

		public int getMColumnOffset()
		{
			return 1;
		}

		public int getMColumnDefaultWidth(int col)
		{
			return SettingsHandler.getPCGenOption("InfoTemplates.sizecol."
				+ COL_NAMES[col], COL_DEFAULT_WIDTH[col]);
		}

		public void setMColumnDefaultWidth(int col, int width)
		{
			SettingsHandler.setPCGenOption("InfoTemplates.sizecol."
				+ COL_NAMES[col], width);
		}

		private boolean getColumnViewOption(String colName, boolean defaultVal)
		{
			return SettingsHandler.getPCGenOption("InfoTemplates.viewcol."
				+ colName, defaultVal);
		}

		private void setColumnViewOption(String colName, boolean val)
		{
			SettingsHandler.setPCGenOption("InfoTemplates.viewcol." + colName,
				val);
		}

		public void resetMColumn(int col, TableColumn column)
		{
			// TODO Auto-generated method stub

		}
	}

	private class TemplatePopupListener extends MouseAdapter
	{
		private JTree tree;
		private TemplatePopupMenu menu;

		TemplatePopupListener(JTreeTable treeTable, TemplatePopupMenu aMenu)
		{
			tree = treeTable.getTree();
			this.menu = aMenu;

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

				tree.setSelectionPath(selPath);
				menu.show(evt.getComponent(), evt.getX(), evt.getY());
			}
		}
	}

	private class TemplatePopupMenu extends JPopupMenu
	{
		static final long serialVersionUID = 2565545289875422981L;

		TemplatePopupMenu(JTreeTable treeTable)
		{
			if (treeTable == availableTable)
			{
				TemplatePopupMenu.this.add(createAddMenuItem(PropertyFactory
					.getString("in_irAddTemplate"), "shortcut EQUALS"));
			}
			else
			{
				TemplatePopupMenu.this.add(createRemoveMenuItem(PropertyFactory
					.getString("in_irRemoveTemplate"), "shortcut MINUS"));
			}
		}

		private JMenuItem createAddMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label,
				new AddTemplateActionListener(), PropertyFactory
					.getString("in_select"), '\0', accelerator, PropertyFactory
					.getString("in_irAddTemplateTip"), "Add16.gif", true);
		}

		private JMenuItem createRemoveMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label,
				new RemoveTemplateActionListener(), PropertyFactory
					.getString("in_select"), '\0', accelerator, PropertyFactory
					.getString("in_irRemoveTemplateTip"), "Remove16.gif", true);
		}

		private class AddTemplateActionListener extends TemplateActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				addTemplate();
			}
		}

		private class RemoveTemplateActionListener extends
				TemplateActionListener
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
					lastTemplate = null;
					ShowMessageDelegate.showMessageDialog(PropertyFactory
						.getString("in_irNoTemplate"), Constants.APPLICATION_NAME,
						MessageType.ERROR);

					return;
				}

				PCTemplate template = null;
				PObjectNode pn = null;

				if (temp instanceof PObjectNode)
				{
					pn = (PObjectNode) temp;
					temp = ((PObjectNode) temp).getItem();

					if (temp instanceof PCTemplate)
					{
						template = (PCTemplate) temp;
					}
				}

				addButton.setEnabled(template != null);
				setInfoLabelText(template, pn);
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
					lastTemplate = null;
					infoLabel.setText();

					return;
				}

				PCTemplate template = null;
				PObjectNode pn = null;

				if (temp instanceof PObjectNode)
				{
					pn = (PObjectNode) temp;

					Object t = pn.getItem();

					if (t instanceof PCTemplate)
					{
						template = (PCTemplate) t;
					}
				}

				removeButton.setEnabled(template != null);
				setInfoLabelText(template, pn);
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
					addTemplate();
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
					removeTemplate();
				}
			});
		}

		public boolean isSelectable(Object obj)
		{
			return !(obj instanceof String);
		}
	}

}
