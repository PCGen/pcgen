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
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
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

import pcgen.core.Constants;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
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
import pcgen.gui.utils.IconUtilitities;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.gui.utils.JLabelPane;
import pcgen.gui.utils.JTableEx;
import pcgen.gui.utils.PObjectNode;
import pcgen.gui.utils.ResizeColumnListener;
import pcgen.gui.utils.TableSorter;
import pcgen.gui.utils.Utility;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

/**
 *  <code>InfoRace</code> creates a new tabbed panel
 *  with all the race and template information on it
 *
 * @author  Bryan McRoberts (merton_monk@yahoo.com)
 * @version $Revision: 198 $
 **/
public class InfoTemplates extends FilterAdapterPanel implements CharacterInfoTab
{
	static final long serialVersionUID = 2565545289875422981L;
	private static boolean needsUpdate = true;

	// if you change these, you also have to change
	// the case statement in the RaceModel declaration
	private AllTemplatesTableModel allTemplatesDataModel = new AllTemplatesTableModel();
	private FlippingSplitPane split;
	private FlippingSplitPane bsplit;
	private JButton leftButton;
	private JButton rightButton;

	private JLabel sortLabel = new JLabel(PropertyFactory.getString("in_irSortTempl"));
	private JComboBoxEx viewComboBox = new JComboBoxEx();
	private int viewMode = 0;
	private final JLabel lblQFilter = new JLabel("QuickFilter:");
	private JTextField textQFilter = new JTextField();
	private JButton clearQFilterButton = new JButton("Clear");
	private static Integer saveViewMode = null;

	private JLabel selSortLabel = new JLabel(PropertyFactory.getString("in_irSortTemplSel"));
	private JComboBoxEx viewSelComboBox = new JComboBoxEx();
	private int viewSelMode = 0;
	private final JLabel lblSelQFilter = new JLabel("QuickFilter:");
	private JTextField textSelQFilter = new JTextField();
	private JButton clearSelQFilterButton = new JButton("Clear");
	private static Integer saveSelViewMode = null;

	private JLabelPane infoLabel = new JLabelPane();
	private JPanel botPane = new JPanel();
	private JPanel topPane = new JPanel();
	private JScrollPane allTemplatesPane;
	private JScrollPane currentTemplatesPane;
	private JTableEx allTemplatesTable;
	private JTableEx currentTemplatesTable;

	// the list from which to pull the templates to use
	private List currentPCdisplayTemplates = new ArrayList(0);
	private PCTemplatesTableModel currentTemplatesDataModel = new PCTemplatesTableModel();
	private TableSorter sortedAllTemplatesModel = new TableSorter();
	private TableSorter sortedCurrentTemplatesModel = new TableSorter();

	private boolean hasBeenSized = false;

	private PlayerCharacter pc;
	private int serial = 0;
	private boolean readyForRefresh = false;

	/**
	 * Constructor
	 * @param pc
	 */
	public InfoTemplates(PlayerCharacter pc)
	{
		this.pc = pc;
		// do not change/remove this as we use the component's name
		// to save component specific settings
		setName(Constants.tabNames[Constants.TAB_TEMPLATES]);

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
		return SettingsHandler.getPCGenOption(".Panel.Race.Order", Constants.TAB_TEMPLATES);
	}

	public void setTabOrder(int order)
	{
		SettingsHandler.setPCGenOption(".Panel.Race.Order", order);
	}

	public String getTabName()
	{
		GameMode game = SettingsHandler.getGame();
		return game.getTabName(Constants.TAB_TEMPLATES);
	}

	public boolean isShown()
	{
		GameMode game = SettingsHandler.getGame();
		return game.getTabShown(Constants.TAB_TEMPLATES);
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
		allTemplatesDataModel.resetModel(viewMode);
	}

	private void setInfoLabelText(PCTemplate temp, PObjectNode pn)
	{
		StringBuffer b = new StringBuffer();
		b.append("<html>");

		if ((temp != null))
		{
			b.append("<b>").append(temp.piSubString()).append("</b>");
			b.append("<br><b>RACE TYPE</b>: ").append(temp.getRaceType());
			if (temp.getType().length() > 0)
			{
				b.append(" &nbsp;<b>TYPE</b>:").append(temp.getType());
			}
			String bString = temp.getSource();

			if (bString.length() > 0)
			{
				b.append(" &nbsp;<b>SOURCE</b>:").append(bString);
			}
		}

		b.append("</html>");
		infoLabel.setText(b.toString());
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
				allTemplatesDataModel.resetModel(viewMode);
			}
			else
			{
				JOptionPane.showMessageDialog(null, PropertyFactory.getString("in_irHaveTemplate"));
			}
		}

		currentTemplatesDataModel.setFilter(currentTemplatesDataModel.curFilter);
		currentTemplatesDataModel.resetModel(viewSelMode);
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
		int u = split.getDividerLocation();

		if (!hasBeenSized)
		{
			t = SettingsHandler.getPCGenOption("InfoRace.bsplit", (int) (InfoTemplates.this.getSize().getHeight() - 120));
			u = SettingsHandler.getPCGenOption("InfoRace.asplit",
					(int) ((InfoTemplates.this.getSize().getWidth() * 75.0) / 100.0));

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
					int s = split.getDividerLocation();

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
		textSelQFilter.getDocument().addDocumentListener(new DocumentListener()
			{
				public void changedUpdate(DocumentEvent evt)
				{
					setSelQFilter();
				}
				public void insertUpdate(DocumentEvent evt)
				{
					setSelQFilter();
				}
				public void removeUpdate(DocumentEvent evt)
				{
					setSelQFilter();
				}
			});
		clearQFilterButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					clearQFilter();
				}
			});
		clearSelQFilterButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					clearSelQFilter();
				}
			});
		allTemplatesTable.getSelectionModel().addListSelectionListener(new AllListSelectionListener());
		currentTemplatesTable.getSelectionModel().addListSelectionListener(new CurrentListSelectionListener());

		FilterFactory.restoreFilterSettings(this);
		currentTemplatesDataModel.setFilter(currentTemplatesDataModel.curFilter);
		currentTemplatesDataModel.resetModel(viewSelMode);
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
		int iView = SettingsHandler.getTemplateTab_ListMode();
		if (iView >= GuiConstants.INFOTEMPLATE_VIEW_NAME)
		{
			viewMode = iView;
		}
		SettingsHandler.setTemplateTab_ListMode(viewMode);
		viewComboBox.addItem(PropertyFactory.getString("in_nameLabel") + "   ");
		viewComboBox.setSelectedIndex(viewMode);

		iView = SettingsHandler.getTemplateSelTab_ListMode();
		if (iView >= GuiConstants.INFOTEMPLATE_VIEW_NAME)
		{
			viewMode = iView;
		}
		SettingsHandler.setTemplateSelTab_ListMode(viewMode);
		viewSelComboBox.addItem(PropertyFactory.getString("in_nameLabel") + "   ");
		viewSelComboBox.setSelectedIndex(viewMode);

		buildTopPanel();

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

		split = new FlippingSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane, rightPane);
		split.setOneTouchExpandable(true);
		split.setDividerSize(10);

		topPane.add(split, BorderLayout.CENTER);


		//-------------------------------------------------------------
		//  Top Left Pane
		//  - available templates

		// Header
		leftPane.add(createFilterPane(sortLabel, viewComboBox, lblQFilter, textQFilter, clearQFilterButton), BorderLayout.NORTH);

		// Data - All Available Templates Table
		allTemplatesTable = new JTableEx();
		allTemplatesPane = new JScrollPane(allTemplatesTable);

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
		allTemplatesTable.setColAlign(0, SwingConstants.CENTER);
		allTemplatesTable.setColAlign(2, SwingConstants.CENTER);

		leftPane.add(allTemplatesPane, BorderLayout.CENTER);

		JPanel bottomLeftPanel = new JPanel();
		rightButton = new JButton(IconUtilitities.getImageIcon("Forward16.gif"));
		Utility.setDescription(rightButton, PropertyFactory.getString("in_irTemplAddTip"));
		rightButton.setEnabled(true);
		bottomLeftPanel.add(rightButton);
		leftPane.add(bottomLeftPanel, BorderLayout.SOUTH);

		//-------------------------------------------------------------
		//  Top Right Pane
		//  - selected templates

		// Header
		rightPane.add(createFilterPane(selSortLabel, viewSelComboBox, lblSelQFilter, textSelQFilter, clearSelQFilterButton), BorderLayout.NORTH);

		// Data - Selected Templates table
		currentTemplatesPane = new JScrollPane();

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
		rightPane.add(currentTemplatesPane, BorderLayout.CENTER);

		JPanel rightBottomPanel = new JPanel();
		leftButton = new JButton(IconUtilitities.getImageIcon("Back16.gif"));
		Utility.setDescription(leftButton, PropertyFactory.getString("in_irTemplRemoveTip"));
		leftButton.setEnabled(true);
		rightBottomPanel.add(leftButton);
		rightPane.add(rightBottomPanel, BorderLayout.SOUTH);
	}

	private void buildBottomPanel() {
		JPanel mainPane = new JPanel();
		mainPane.setLayout(new BorderLayout());

		botPane.setLayout(new BorderLayout());		
		botPane.add(mainPane, BorderLayout.CENTER);

		//-------------------------------------------------------------
		//  Bottom Pane
		//  - Template Info

		JScrollPane scroll = new JScrollPane();

		TitledBorder title1 = BorderFactory.createTitledBorder(PropertyFactory.getString("in_irTemplateInfo"));
		title1.setTitleJustification(TitledBorder.CENTER);
		scroll.setBorder(title1);
		infoLabel.setBackground(topPane.getBackground());
		scroll.setViewportView(infoLabel);
		mainPane.add(scroll);
	}
	
	private JPanel createFilterPane(JLabel treeLabel, JComboBox treeCb, JLabel filterLabel, JTextField filterText, JButton clearButton)
	{
		GridBagConstraints c = new GridBagConstraints();
		JPanel filterPanel = new JPanel(new GridBagLayout());
		int i = 0;

		if(treeLabel != null)
		{
			Utility.buildConstraints(c, i++, 0, 1, 1, 0, 0);
			c.insets = new Insets(1, 2, 1, 2);
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.LINE_START;
			filterPanel.add(treeLabel, c);
		}
		
		if(treeCb != null)
		{
			Utility.buildConstraints(c, i++, 0, 1, 1, 0, 0);
			c.insets = new Insets(1, 2, 1, 2);
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.LINE_START;
			filterPanel.add(treeCb, c);
		}

		if(filterLabel != null)
		{
			Utility.buildConstraints(c, i++, 0, 1, 1, 0, 0);
			c.insets = new Insets(1, 2, 1, 2);
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.LINE_START;
			filterPanel.add(filterLabel, c);
		}
		
		if(filterText != null)
		{
			Utility.buildConstraints(c, i++, 0, 1, 1, 95, 0);
			c.insets = new Insets(1, 2, 1, 2);
			c.fill = GridBagConstraints.HORIZONTAL;
			c.anchor = GridBagConstraints.LINE_START;
			filterPanel.add(filterText, c);
		}
		
		if(clearButton != null)
		{
			Utility.buildConstraints(c, i++, 0, 1, 1, 0, 0);
			c.insets = new Insets(0, 2, 0, 2);
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.LINE_START;
			clearButton.setEnabled(false);
			filterPanel.add(clearButton, c);
		}
		return filterPanel;
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
		allTemplatesDataModel.resetModel(viewMode);
		currentTemplatesDataModel.setFilter(currentTemplatesDataModel.curFilter);
		currentTemplatesDataModel.resetModel(viewSelMode);
	}

	/**
	 * This recalculates the states of everything based
	 * upon the currently selected character
	 **/
	private final void updateCharacterInfo()
	{
		if (!needsUpdate)
		{
			return;
		}

		allTemplatesDataModel.resetModel(viewMode);
		currentTemplatesDataModel.setFilter(currentTemplatesDataModel.curFilter);
		currentTemplatesDataModel.resetModel(viewSelMode);
		needsUpdate = false;
	}

	private void viewComboBoxActionPerformed()
	{
		final int index = viewComboBox.getSelectedIndex();

		if (index != viewMode)
		{
			viewMode = index;
			SettingsHandler.setTemplateTab_ListMode(viewMode);
		}
	}

	private void viewSelComboBoxActionPerformed()
	{
		final int index = viewSelComboBox.getSelectedIndex();

		if (index != viewSelMode)
		{
			viewSelMode = index;
			SettingsHandler.setTemplateSelTab_ListMode(viewSelMode);
		}
	}
	
	private void clearQFilter()
	{
		allTemplatesDataModel.clearQFilter();
		if (saveViewMode != null)
		{
			viewMode = saveViewMode.intValue();
			saveViewMode = null;
		}
		allTemplatesDataModel.resetModel(viewMode);
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
		allTemplatesDataModel.setQFilter(aString);

		if (saveViewMode == null)
		{
			saveViewMode = new Integer(viewMode);
		}
		viewMode = GuiConstants.INFORACE_VIEW_NAME;
		allTemplatesDataModel.resetModel(viewMode);
		clearQFilterButton.setEnabled(true);
		viewComboBox.setEnabled(false);
		forceRefresh();
	}

	private void clearSelQFilter()
	{
		currentTemplatesDataModel.clearQFilter();
		if (saveSelViewMode != null)
		{
			viewSelMode = saveSelViewMode.intValue();
			saveSelViewMode = null;
		}
		currentTemplatesDataModel.resetModel(viewSelMode);
		clearSelQFilterButton.setEnabled(false);
		viewSelComboBox.setEnabled(true);
		forceRefresh();
	}

	private void setSelQFilter()
	{
		String aString = textSelQFilter.getText();

		if (aString.length() == 0)
		{
			clearSelQFilter();
			return;
		}
		currentTemplatesDataModel.setQFilter(aString);

		if (saveSelViewMode == null)
		{
			saveSelViewMode = new Integer(viewSelMode);
		}
		viewSelMode = GuiConstants.INFORACE_VIEW_NAME;
		currentTemplatesDataModel.resetModel(viewSelMode);
		clearSelQFilterButton.setEnabled(true);
		viewSelComboBox.setEnabled(false);
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
		private String qFilter = null;

		private AllTemplatesTableModel()
		{
			resetModel(viewMode);
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

		private void resetModel(int view)
		{
			displayTemplates.clear();

			for (Iterator it = Globals.getTemplateList().iterator(); it.hasNext();)
			{
				final PCTemplate aPCTemplate = (PCTemplate) it.next();

				if (((aPCTemplate.isVisible() % 2) == 1) && accept(pc, aPCTemplate))
				{
					if (qFilter == null || 
							( aPCTemplate.getName().toLowerCase().indexOf(qFilter) >= 0 ||
							aPCTemplate.getType().toLowerCase().indexOf(qFilter) >= 0 ))
					{
						displayTemplates.add(aPCTemplate);
					}
				}
			}

			fireTableDataChanged();
		}

		/**
		 * Get the QuickFilter
		 * @return QuickFilter
		 */
		public String getQFilter()
		{
			return qFilter;
		}

		/**
		 * Set theQuickFilter
		 * @param quickFilter
		 */
		public void setQFilter(String quickFilter) 
		{
			if(quickFilter != null) {
				this.qFilter = quickFilter.toLowerCase();
			}
			else {
				this.qFilter = null;
			}
		}

		/**
		 * Clear the QuickFilter
		 */
		public void clearQFilter() 
		{
			this.qFilter = null;
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
		private String qFilter = null;

		public PCTemplatesTableModel() {
			resetModel(0);
		}
		
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
		
		private void resetModel(int view)
		{
			currentPCdisplayTemplates.clear();

			if(pc != null) {
				for (Iterator it = pc.getTemplateList().iterator(); it.hasNext();)
				{
					final PCTemplate aPCTemplate = (PCTemplate) it.next();
	
					if (((aPCTemplate.isVisible() % 2) == 1) && accept(pc, aPCTemplate))
					{
						if (qFilter == null || 
								( aPCTemplate.getName().toLowerCase().indexOf(qFilter) >= 0 ||
								aPCTemplate.getType().toLowerCase().indexOf(qFilter) >= 0 ))
						{
							currentPCdisplayTemplates.add(aPCTemplate);
						}
					}
				}
			}

			fireTableDataChanged();
		}
		
		/**
		 * Get the QuickFilter
		 * @return QuickFilter
		 */
		public String getQFilter()
		{
			return qFilter;
		}

		/**
		 * Set theQuickFilter
		 * @param quickFilter
		 */
		public void setQFilter(String quickFilter) 
		{
			if(quickFilter != null) {
				this.qFilter = quickFilter.toLowerCase();
			}
			else {
				this.qFilter = null;
			}
		}

		/**
		 * Clear the QuickFilter
		 */
		public void clearQFilter() 
		{
			this.qFilter = null;
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

	private class AllListSelectionListener implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent e)
		{
			if (!e.getValueIsAdjusting())
			{
				PCTemplate template = allTemplatesDataModel.get(sortedAllTemplatesModel.getRowTranslated(allTemplatesTable.getSelectedRow()));

				PObjectNode pn = null;
				//rightButton.setEnabled(template != null);
				setInfoLabelText(template, pn);
			}
		}
	}

	private class CurrentListSelectionListener implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent e)
		{
			if (!e.getValueIsAdjusting())
			{
				PCTemplate template = (PCTemplate) currentPCdisplayTemplates.get(sortedCurrentTemplatesModel.getRowTranslated(
						currentTemplatesTable.getSelectedRow()));

				PObjectNode pn = null;
				//rightButton.setEnabled(template != null);
				setInfoLabelText(template, pn);
			}
		}
	}
}
