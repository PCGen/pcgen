/*
 * SourceSelectionDialog.java
 * Copyright 2010 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Apr 15, 2010, 7:15:13 PM
 */
package pcgen.gui2.sources;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pcgen.core.facade.CampaignFacade;
import pcgen.core.facade.GameModeFacade;
import pcgen.core.facade.LoadableFacade.LoadingState;
import pcgen.core.facade.SourceSelectionFacade;
import pcgen.core.facade.util.ListFacade;
import pcgen.core.facade.util.ListFacades;
import pcgen.core.facade.util.SortedListFacade;
import pcgen.gui2.PCGenFrame;
import pcgen.gui2.UIPropertyContext;
import pcgen.gui2.dialog.DataInstaller;
import pcgen.gui2.filter.FilteredListFacadeTableModel;
import pcgen.gui2.tools.Utility;
import pcgen.gui2.util.FacadeListModel;
import pcgen.gui2.util.JTableEx;
import pcgen.gui2.util.table.TableUtils;
import pcgen.system.FacadeFactory;
import pcgen.system.LanguageBundle;
import pcgen.util.Comparators;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class SourceSelectionDialog extends JDialog
		implements ActionListener, ChangeListener, ListSelectionListener
{

	private static final UIPropertyContext context = UIPropertyContext.createContext("SourceSelectionDialog"); //$NON-NLS-1$
	private static final String PROP_SELECTED_SOURCE = "selectedSource"; //$NON-NLS-1$
	private static final String LOAD_COMMAND = "Load"; //$NON-NLS-1$
	private static final String CANCEL_COMMAND = "Cancel"; //$NON-NLS-1$
	private static final String SAVE_COMMAND = "Save"; //$NON-NLS-1$
	private static final String DELETE_COMMAND = "Delete"; //$NON-NLS-1$
	private static final String HIDEUNHIDE_COMMAND = "Hide"; //$NON-NLS-1$
	private static final String INSTALLDATA_COMMAND = "Install"; //$NON-NLS-1$
	private final PCGenFrame frame;
	private QuickSourceSelectionPanel basicPanel;
	private AdvancedSourceSelectionPanel advancedPanel;
	private JTabbedPane tabs;
	private JPanel buttonPanel;
	private JButton loadButton;
	private JButton cancelButton;
	private JButton hideunhideButton;
	private JButton deleteButton;
	private JButton installDataButton;
	private JButton saveButton;
	private JCheckBox alwaysAdvancedCheck;

	public SourceSelectionDialog(PCGenFrame frame)
	{
		super(frame, true);
		this.frame = frame;
		setTitle(LanguageBundle.getString("in_mnuSourcesLoadSelect")); //$NON-NLS-1$
		this.tabs = new JTabbedPane();
		this.basicPanel = new QuickSourceSelectionPanel();
		this.advancedPanel = new AdvancedSourceSelectionPanel(frame);
		this.buttonPanel = new JPanel();
		this.loadButton = new JButton(LanguageBundle.getString("in_load")); //$NON-NLS-1$
		this.cancelButton = new JButton(LanguageBundle.getString("in_cancel")); //$NON-NLS-1$
		this.hideunhideButton = new JButton(LanguageBundle.getString("in_hideunhide")); //$NON-NLS-1$
		this.deleteButton = new JButton(LanguageBundle.getString("in_delete")); //$NON-NLS-1$
		this.installDataButton = new JButton(LanguageBundle.getString("in_mnuSourcesInstallData")); //$NON-NLS-1$
		this.saveButton = new JButton(LanguageBundle.getString("in_saveSelection")); //$NON-NLS-1$
		this.alwaysAdvancedCheck =
				new JCheckBox(
					LanguageBundle.getString("in_sourceAlwaysAdvanced"), //$NON-NLS-1$
					!UIPropertyContext.getInstance().initBoolean(
						UIPropertyContext.SOURCE_USE_BASIC_KEY, true));
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		initComponents();
		initDefaults();
		pack();
	}

	private void initComponents()
	{
		Container pane = getContentPane();
		pane.setLayout(new BorderLayout());

		tabs.add(LanguageBundle.getString("in_basic"), basicPanel); //$NON-NLS-1$
		tabs.add(LanguageBundle.getString("in_advanced"), advancedPanel); //$NON-NLS-1$
		tabs.addChangeListener(this);
		pane.add(tabs, BorderLayout.CENTER);

		loadButton.setDefaultCapable(true);
		getRootPane().setDefaultButton(loadButton);

		loadButton.setActionCommand(LOAD_COMMAND);
		cancelButton.setActionCommand(CANCEL_COMMAND);
		deleteButton.setActionCommand(DELETE_COMMAND);
		saveButton.setActionCommand(SAVE_COMMAND);
		hideunhideButton.setActionCommand(HIDEUNHIDE_COMMAND);
		installDataButton.setActionCommand(INSTALLDATA_COMMAND);

		loadButton.addActionListener(this);
		cancelButton.addActionListener(this);
		saveButton.addActionListener(this);
		deleteButton.addActionListener(this);
		hideunhideButton.addActionListener(this);
		installDataButton.addActionListener(this);

		Box buttons = Box.createHorizontalBox();
		buttons.add(buttonPanel);
		buttons.add(Box.createHorizontalGlue());
		buttons.add(installDataButton);
		buttons.add(Box.createHorizontalGlue());
		buttons.add(loadButton);
		buttons.add(Box.createHorizontalStrut(5));
		buttons.add(cancelButton);
		buttons.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		pane.add(buttons, BorderLayout.SOUTH);

		Utility.installEscapeCloseOperation(this);
	}

	private void initDefaults()
	{
		boolean useBasic = context.initBoolean("useBasic", true); //$NON-NLS-1$
		SourceSelectionFacade selection = basicPanel.getSourceSelection();
		if (selection != null && useBasic)
		{
			deleteButton.setEnabled(selection.isModifiable());
			advancedPanel.setSourceSelection(selection);
		}
		else
		{
			deleteButton.setEnabled(false);
		}
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		if (useBasic)
		{
			setBasicButtons();
			advancedPanel.setSourceSelection(basicPanel.getSourceSelection());
		}
		else
		{
			setAdvancedButtons();
			tabs.setSelectedIndex(1);
		}
	}

	void setDeleteEnabled(boolean enable)
	{
		deleteButton.setEnabled(enable);
	}

	void setLoadEnabled(boolean enable)
	{
		loadButton.setEnabled(enable);
	}

	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		if (!e.getValueIsAdjusting())
		{
			SourceSelectionFacade selection = basicPanel.getSourceSelection();
			deleteButton.setEnabled(selection.isModifiable());
			advancedPanel.setSourceSelection(selection);
		}
	}

	private void setBasicButtons()
	{
		buttonPanel.removeAll();
		buttonPanel.add(hideunhideButton);
		buttonPanel.add(Box.createHorizontalStrut(5));
		buttonPanel.add(deleteButton);
		buttonPanel.revalidate();
	}

	private void setAdvancedButtons()
	{
		buttonPanel.removeAll();
		buttonPanel.add(saveButton);
		buttonPanel.add(alwaysAdvancedCheck);
		buttonPanel.revalidate();
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		if (tabs.getSelectedComponent() == basicPanel)
		{
			setBasicButtons();
		}
		else
		{
			setAdvancedButtons();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();
		if (command.equals(SAVE_COMMAND))
		{
			final JList sourcesList = new JList();
			final JTextField nameField = new JTextField();
			ListFacade<SourceSelectionFacade> sources = 
					new SortedListFacade<SourceSelectionFacade>(
							Comparators.toStringIgnoreCaseCollator(),
							FacadeFactory.getCustomSourceSelections());
			sourcesList.setModel(new FacadeListModel(sources));
			sourcesList.addListSelectionListener(new ListSelectionListener()
			{

				@Override
				public void valueChanged(ListSelectionEvent lse)
				{
					nameField.setText(sourcesList.getSelectedValue().toString());
				}

			});
			JPanel panel = new JPanel(new BorderLayout());
			panel.add(new JScrollPane(sourcesList), BorderLayout.CENTER);
			panel.add(nameField, BorderLayout.SOUTH);
			int ret = JOptionPane.showOptionDialog(
					this,
					panel, "Save the source selection as...",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
			if (ret == JOptionPane.OK_OPTION)
			{
				String name = nameField.getText();
				SourceSelectionFacade selection = null;
				for (SourceSelectionFacade sourceSelectionFacade : sources)
				{
					if (sourceSelectionFacade.toString().equals(name))
					{
						selection = sourceSelectionFacade;
						break;

					}
				}
				if (selection == null)
				{
					selection = FacadeFactory.createCustomSourceSelection(name);
				}
				selection.setCampaigns(advancedPanel.getSelectedCampaigns());
				selection.setGameMode(advancedPanel.getSelectedGameMode());
			}
		}
		else if (command.equals(DELETE_COMMAND))
		{
			FacadeFactory.deleteCustomSourceSelection(basicPanel.getSourceSelection());
		}
		else if (command.equals(LOAD_COMMAND))
		{
			fireSourceLoad();
		}
		else if (command.equals(INSTALLDATA_COMMAND))
		{
			// Swap to the install data dialog.
			setVisible(false);
			DataInstaller di = new DataInstaller(frame);
			di.setVisible(true);
		}
		else if (command.equals(HIDEUNHIDE_COMMAND))
		{
			SourcesTableModel model = new SourcesTableModel();
			JTableEx table = new JTableEx(model);
			JTable rowTable = TableUtils.createDefaultTable();
			JScrollPane pane = TableUtils.createCheckBoxSelectionPane(table, rowTable);
			table.setShowGrid(false);
			table.setFocusable(false);
			table.setRowSelectionAllowed(false);
			table.toggleSort(0);
			rowTable.setRowSelectionAllowed(false);

			pane.setPreferredSize(new Dimension(300, 200));

			int ret = JOptionPane.showOptionDialog(
					this,
					pane, "Select Sources to be visible",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
			if (ret == JOptionPane.OK_OPTION)
			{
				FacadeFactory.setDisplayedSources(model.getDisplayedSources());
			}
			model.dispose();
		}
		else
		{//must be the cancel command
			setVisible(false);
		}
	}

	private void fireSourceLoad()
	{
		SourceSelectionFacade selection;
		if (tabs.getSelectedComponent() == basicPanel)
		{
			selection = basicPanel.getSourceSelection();
		}
		else
		{
			selection = FacadeFactory.createSourceSelection(advancedPanel.getSelectedGameMode(), advancedPanel.getSelectedCampaigns());
		}
		if (selection == null)
		{
			return;
		}
		GameModeFacade gameMode = selection.getGameMode().getReference();
		List<CampaignFacade> campaigns = ListFacades.wrap(selection.getCampaigns());
		if (FacadeFactory.passesPrereqs(gameMode, campaigns))
		{
			setVisible(false);
			frame.loadSourceSelection(selection);
		}
		else
		{
			JOptionPane.showMessageDialog(this,
										  "Some sources have unfulfilled prereqs",
										  "Cannot Load Selected Sources",
										  JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setVisible(boolean visible)
	{
		if (visible)
		{
			advancedPanel.refreshDisplay();
		}
		else
		{
			UIPropertyContext.getInstance().setBoolean(
				UIPropertyContext.SOURCE_USE_BASIC_KEY,
				!alwaysAdvancedCheck.isSelected());
		}
		super.setVisible(visible);
	}

	private static class SourcesTableModel extends FilteredListFacadeTableModel<SourceSelectionFacade>
	{

		private List<SourceSelectionFacade> displayedSources;

		public SourcesTableModel()
		{
			setDelegate(FacadeFactory.getSourceSelections());
			displayedSources = new ArrayList<SourceSelectionFacade>();
			displayedSources.addAll(ListFacades.wrap(FacadeFactory.getDisplayedSourceSelections()));
		}

		public void dispose()
		{
			//this detaches the listeners
			setDelegate(null);
		}

		public SourceSelectionFacade[] getDisplayedSources()
		{
			return displayedSources.toArray(new SourceSelectionFacade[0]);
		}

		@Override
		protected Object getValueAt(SourceSelectionFacade element, int column)
		{
			if (column == -1)
			{
				return displayedSources.contains(element);
			}
			else
			{
				return element;
			}
		}

		@Override
		public int getColumnCount()
		{
			return 1;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex)
		{
			if (columnIndex == -1)
			{
				return Boolean.class;
			}
			else
			{
				return Object.class;
			}
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			return columnIndex == -1;
		}

		@Override
		public String getColumnName(int column)
		{
			return "Sources";
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex)
		{
			Boolean bool = (Boolean) aValue;
			SourceSelectionFacade source = sortedList.getElementAt(rowIndex);
			if (bool)
			{
				displayedSources.add(source);
			}
			else
			{
				displayedSources.remove(source);
			}
		}

	}

	private class QuickSourceSelectionPanel extends JPanel implements ListSelectionListener
	{

		private static final String DEFAULT_SOURCE = "Pathfinder RPG for Players"; //$NON-NLS-1$
		private JList sourceList;

		public QuickSourceSelectionPanel()
		{
			sourceList = new JList();
			initComponents();
			initDefaults();
		}

		private void initComponents()
		{
			setLayout(new BorderLayout());
			JLabel label = new JLabel(LanguageBundle.getString("in_qsrc_intro"));
			label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			add(label, BorderLayout.NORTH);
			ListFacade<SourceSelectionFacade> sources = 
					new SortedListFacade<SourceSelectionFacade>(
							Comparators.toStringIgnoreCaseCollator(),
							FacadeFactory.getDisplayedSourceSelections());
			sourceList.setModel(new FacadeListModel(sources));
			sourceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			sourceList.setCellRenderer(new SourceListCellRenderer());
			sourceList.addMouseListener(new MouseAdapter()
			{

				@Override
				public void mouseClicked(MouseEvent e)
				{
					if (e.getClickCount() > 1)
					{
						Point p = e.getPoint();
						int index = sourceList.locationToIndex(p);
						if (sourceList.getCellBounds(index, index).contains(p))
						{
							fireSourceLoad();
						}
					}

				}

			});
			//sourceList.setLayoutOrientation(JList.VERTICAL_WRAP);
			//sourceList.setVisibleRowCount(2);
			add(new JScrollPane(sourceList), BorderLayout.CENTER);
		}

		private void initDefaults()
		{
			final ListModel sortedModel = sourceList.getModel();
			String defaultSelectedSource =
					context.initProperty(PROP_SELECTED_SOURCE, DEFAULT_SOURCE);
			int index = Collections.binarySearch(new AbstractList<Object>()
			{

				@Override
				public Object get(int idx)
				{
					return sortedModel.getElementAt(idx);
				}

				@Override
				public int size()
				{
					return sortedModel.getSize();
				}

			}, defaultSelectedSource, Comparators.toStringIgnoreCaseCollator());
			if (index >= 0)
			{
				sourceList.setSelectedIndex(index);
			}
			else if (sortedModel.getSize() > 0)
			{
				sourceList.setSelectedIndex(0);
			}
			sourceList.addListSelectionListener(this);
		}

		public SourceSelectionFacade getSourceSelection()
		{
			return (SourceSelectionFacade) sourceList.getSelectedValue();
		}

		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			SourceSelectionFacade selection = getSourceSelection();
			if (selection != null)
			{
				context.setProperty(PROP_SELECTED_SOURCE, selection.toString());
				advancedPanel.setSourceSelection(selection);
				deleteButton.setEnabled(selection.isModifiable());
			}
			else
			{
				deleteButton.setEnabled(false);
			}
		}

		private class SourceListCellRenderer extends DefaultListCellRenderer
		{

			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
			{
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				setToolTipText("");
				if (value instanceof SourceSelectionFacade)
				{
					SourceSelectionFacade selection = (SourceSelectionFacade) value;
					if (selection.isModifiable())
					{
						setForeground(UIPropertyContext.getCustomItemColor());
					}
					if (selection.getLoadingState() == LoadingState.LOADED_WITH_ERRORS)
					{
						setForeground(Color.LIGHT_GRAY);
						setToolTipText(selection.getLoadingErrorMessage());
					}
				}
				return this;
			}

		}

	}

}
