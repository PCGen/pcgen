/*
 * AdvancedSourceSelectionPanel.java
 * Copyright 2009 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Feb 21, 2009, 7:15:03 PM
 */
package pcgen.gui2.sources;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import pcgen.core.Globals;
import pcgen.core.facade.CampaignFacade;
import pcgen.core.facade.GameModeFacade;
import pcgen.core.facade.SourceSelectionFacade;
import pcgen.core.facade.event.ListEvent;
import pcgen.core.facade.event.ListListener;
import pcgen.core.facade.util.DefaultListFacade;
import pcgen.core.facade.util.ListFacade;
import pcgen.core.facade.util.ListFacades;
import pcgen.gui2.UIPropertyContext;
import pcgen.gui2.filter.FilterBar;
import pcgen.gui2.filter.FilteredTreeViewTable;
import pcgen.gui2.filter.SearchFilterPanel;
import pcgen.gui2.tools.FlippingSplitPane;
import pcgen.gui2.tools.Icons;
import pcgen.gui2.tools.InfoPane;
import pcgen.gui2.util.FacadeComboBoxModel;
import pcgen.gui2.util.treeview.DataView;
import pcgen.gui2.util.treeview.DataViewColumn;
import pcgen.gui2.util.treeview.DefaultDataViewColumn;
import pcgen.gui2.util.treeview.TreeView;
import pcgen.gui2.util.treeview.TreeViewModel;
import pcgen.gui2.util.treeview.TreeViewPath;
import pcgen.persistence.PersistenceManager;
import pcgen.system.FacadeFactory;
import pcgen.system.LanguageBundle;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
class AdvancedSourceSelectionPanel extends JPanel
		implements ListSelectionListener, ListListener<CampaignFacade>, ActionListener
{
	
	private final FilteredTreeViewTable availableTable;
	private final FilteredTreeViewTable selectedTable;
	private SourceTreeViewModel availTreeViewModel;
	private SourceTreeViewModel selTreeViewModel;
	private InfoPane infoPane;
	private GameModeFacade gameMode;
	private JComboBox gameModeList;
	private final JButton unloadAllButton;
	private final JButton addButton;
	private final JButton removeButton;
	private DefaultListFacade<CampaignFacade> selectedCampaigns;
	
	public AdvancedSourceSelectionPanel()
	{
		this.availableTable = new FilteredTreeViewTable();
		this.selectedTable = new FilteredTreeViewTable();
		this.selectedCampaigns = new DefaultListFacade<CampaignFacade>();
		this.availTreeViewModel = new SourceTreeViewModel();
		this.selTreeViewModel = new SourceTreeViewModel(selectedCampaigns);
		this.gameModeList = new JComboBox();
		this.unloadAllButton = new JButton();
		this.addButton = new JButton();
		this.removeButton = new JButton();
		this.infoPane = new InfoPane(LanguageBundle.getString("in_src_info")); //$NON-NLS-1$
		initComponents();
		initDefaults();
		selectedCampaigns.addListListener(this);
	}
	
	private void initComponents()
	{
		FlippingSplitPane mainPane = new FlippingSplitPane(FlippingSplitPane.VERTICAL_SPLIT);
		FlippingSplitPane topPane = new FlippingSplitPane();
		topPane.setResizeWeight(0.6);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JLabel(LanguageBundle.getString("in_src_gameLabel")), BorderLayout.WEST); //$NON-NLS-1$

		FacadeComboBoxModel<GameModeFacade> gameModes = new FacadeComboBoxModel<GameModeFacade>();
		gameModes.setListFacade(FacadeFactory.getGameModes());
		gameModeList.setModel(gameModes);
		gameModeList.addActionListener(this); 
		panel.add(gameModeList, BorderLayout.CENTER);
		
		FilterBar bar = new FilterBar();
		bar.add(panel, BorderLayout.WEST);
		bar.addDisplayableFilter(new SearchFilterPanel());
		panel = new JPanel(new BorderLayout());
		panel.add(bar, BorderLayout.NORTH);
		
		availableTable.setDisplayableFilter(bar);
		availableTable.setTreeViewModel(availTreeViewModel);
		availableTable.getSelectionModel().addListSelectionListener(this);
		availableTable.setTreeCellRenderer(new CampaignRenderer());
		
		JScrollPane pane = new JScrollPane(availableTable);
		pane.setPreferredSize(new Dimension(600, 310));
		panel.add(pane, BorderLayout.CENTER);

		Box box = Box.createHorizontalBox();
		unloadAllButton.setAction(new UnloadAllAction());
		box.add(unloadAllButton);
		box.add(Box.createHorizontalGlue());
		addButton.setHorizontalTextPosition(SwingConstants.LEADING);
		addButton.setAction(new AddAction());
		box.add(addButton);
		box.add(Box.createHorizontalStrut(5));
		box.setBorder(new EmptyBorder(0,  0, 5, 0));
		panel.add(box, BorderLayout.SOUTH);
		
		topPane.setLeftComponent(panel);
		
		JPanel selPanel = new JPanel(new BorderLayout());
		FilterBar filterBar = new FilterBar();
		filterBar.addDisplayableFilter(new SearchFilterPanel());
		selectedTable.setDisplayableFilter(filterBar);

		selectedTable.setTreeViewModel(selTreeViewModel);
		selectedTable.getSelectionModel().addListSelectionListener(this);
		selectedTable.setTreeCellRenderer(new CampaignRenderer());
		JScrollPane scrollPane = new JScrollPane(selectedTable);
		scrollPane.setPreferredSize(new Dimension(300,350));
		selPanel.add(scrollPane, BorderLayout.CENTER);
		box = Box.createHorizontalBox();
		box.add(Box.createHorizontalStrut(5));
		removeButton.setAction(new RemoveAction());
		box.add(removeButton);
		box.add(Box.createHorizontalGlue());
		box.setBorder(new EmptyBorder(0,  0, 5, 0));
		selPanel.add(box, BorderLayout.SOUTH);
		
		topPane.setRightComponent(selPanel);
		mainPane.setTopComponent(topPane);

		infoPane.setPreferredSize(new Dimension(800, 150));
		mainPane.setBottomComponent(infoPane);
		mainPane.setResizeWeight(0.7);
		add(mainPane);
	}
	
	private void initDefaults()
	{
		if (gameModeList.getModel().getSize() > 0)
		{
			gameModeList.setSelectedIndex(0);
		}
	}
	
	public GameModeFacade getSelectedGameMode()
	{
		return gameMode;
	}
	
	public List<CampaignFacade> getSelectedCampaigns()
	{
		return selectedCampaigns.getContents();
	}
	
	public void setSourceSelection(SourceSelectionFacade sources)
	{
		gameModeList.setSelectedItem(sources.getGameMode().getReference());
		selectedCampaigns.setContents(ListFacades.wrap(sources.getCampaigns()));
	}
	
	private void setSelectedGameMode(GameModeFacade elementAt)
	{
		this.gameMode = elementAt;
		selectedCampaigns.clearContents();
		availTreeViewModel.setGameModel(elementAt);
	}
	
	private void setSelectedCampaign(CampaignFacade source)
	{
		infoPane.setText(FacadeFactory.getCampaignInfoFactory().getHTMLInfo(
			source, selectedCampaigns.getContents()));
	}
	
	public void valueChanged(ListSelectionEvent e)
	{
		if (!e.getValueIsAdjusting())
		{
			if (e.getSource() == availableTable.getSelectionModel())
			{
				int selectedRow = availableTable.getSelectedRow();
				if (selectedRow != -1)
				{
					final Object data = availableTable.getModel().getValueAt(selectedRow, 0);
					if (data != null && data instanceof CampaignFacade)
					{
						setSelectedCampaign((CampaignFacade) data);
					}
				}
			}
			else if (e.getSource() == selectedTable.getSelectionModel())
			{
				int selectedRow = selectedTable.getSelectedRow();
				if (selectedRow != -1)
				{
					final Object data = selectedTable.getModel().getValueAt(selectedRow, 0);
					if (data != null && data instanceof CampaignFacade)
					{
						setSelectedCampaign((CampaignFacade) data);
					}
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		// Signal from the game mode conbo that the selection has changed.
		setSelectedGameMode((GameModeFacade) gameModeList.getSelectedItem());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void elementAdded(ListEvent<CampaignFacade> e)
	{
		// Refresh displayed rows now that the selection has changed
		availableTable.updateDisplay();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void elementRemoved(ListEvent<CampaignFacade> e)
	{
		// Refresh displayed rows now that the selection has changed
		availableTable.updateDisplay();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void elementsChanged(ListEvent<CampaignFacade> e)
	{
		// Refresh displayed rows now that the selection has changed
		availableTable.updateDisplay();
	}

	private class AddAction extends AbstractAction
	{

		public AddAction()
		{
			super(LanguageBundle.getString("in_addSelected")); //$NON-NLS-1$
			putValue(SMALL_ICON, Icons.Forward16.getImageIcon());
			availableTable.addActionListener(this);
		}

		public void actionPerformed(ActionEvent e)
		{
			List<CampaignFacade> list = availableTable.getSelectedData();
			if (list != null && !list.isEmpty())
			{
				CampaignFacade camp = list.get(0);
				selectedCampaigns.addElement(camp);
				if (!FacadeFactory.passesPrereqs(gameMode, selectedCampaigns.getContents()))
				{
					JOptionPane.showMessageDialog(AdvancedSourceSelectionPanel.this,
												  LanguageBundle.getString("in_src_badComboMsg"), //$NON-NLS-1$
												  LanguageBundle.getString("in_src_badComboTitle"), //$NON-NLS-1$
												  JOptionPane.INFORMATION_MESSAGE);
					selectedCampaigns.removeElement(camp);
				}
			}
		}
	}

	private class RemoveAction extends AbstractAction
	{

		public RemoveAction()
		{
			super(LanguageBundle.getString("in_removeSelected")); //$NON-NLS-1$
			putValue(SMALL_ICON, Icons.Back16.getImageIcon());
			selectedTable.addActionListener(this);
		}

		public void actionPerformed(ActionEvent e)
		{
			List<CampaignFacade> list = selectedTable.getSelectedData();
			if (list != null && !list.isEmpty())
			{
				CampaignFacade camp = list.get(0);
				selectedCampaigns.removeElement(camp);
			}
		}
	}

	private class UnloadAllAction extends AbstractAction
	{

		public UnloadAllAction()
		{
			super(LanguageBundle.getString("in_src_unloadAll")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e)
		{
			Globals.emptyLists();
			PersistenceManager pManager = PersistenceManager.getInstance();
			pManager.clear();
		}
	}

	private static class SourceTreeViewModel
			implements TreeViewModel<CampaignFacade>, DataView<CampaignFacade>, ListListener<CampaignFacade>
	{
		
		private static ListFacade<TreeView<CampaignFacade>> views =
				new DefaultListFacade<TreeView<CampaignFacade>>(Arrays.asList(SourceTreeView.values()));
		private DefaultListFacade<CampaignFacade> model;
		private ListFacade<CampaignFacade> baseModel = null;
		private final List<DefaultDataViewColumn> columns;
		private final boolean isAvailModel;
		
		public SourceTreeViewModel()
		{
			this.model = new DefaultListFacade<CampaignFacade>();
			this.isAvailModel = true;
			columns =
					Arrays.asList(new DefaultDataViewColumn("in_src_bookType", String.class, true), 
						new DefaultDataViewColumn("in_src_status", String.class, true));
		}
		
		public SourceTreeViewModel( DefaultListFacade<CampaignFacade> model)
		{
			this.model = model;
			this.isAvailModel = false;
			columns =
					Arrays.asList(new DefaultDataViewColumn("in_src_bookType", String.class, false), 
						new DefaultDataViewColumn("in_src_status", String.class, false));
		}
		
		public ListFacade<? extends TreeView<CampaignFacade>> getTreeViews()
		{
			return views;
		}
		
		public int getDefaultTreeViewIndex()
		{
			return isAvailModel ? 2 : 0;
		}
		
		public DataView<CampaignFacade> getDataView()
		{
			return this;
		}
		
		public ListFacade<CampaignFacade> getDataModel()
		{
			return model;
		}
		
		public List<?> getData(CampaignFacade obj)
		{
			return Arrays.asList(obj.getBookType(), obj.getStatus());
		}
		
		public List<? extends DataViewColumn> getDataColumns()
		{
			return columns;
		}
		
		public void setGameModel(GameModeFacade gameMode)
		{
			if (baseModel != null)
			{
				baseModel.removeListListener(this);
			}
			baseModel = FacadeFactory.getSupportedCampaigns(gameMode);
			model.setContents(ListFacades.wrap(baseModel));
			baseModel.addListListener(this);
		}
		
		public void elementAdded(ListEvent<CampaignFacade> e)
		{
			model.addElement(e.getIndex(), e.getElement());
		}
		
		public void elementRemoved(ListEvent<CampaignFacade> e)
		{
			model.removeElement(e.getIndex());
		}
		
		public void elementsChanged(ListEvent<CampaignFacade> e)
		{
			model.setContents(ListFacades.wrap(baseModel));
		}
		
		private static enum SourceTreeView implements
				TreeView<CampaignFacade>
		{
			
			NAME("in_nameLabel"), //$NON-NLS-1$
			PUBLISHER_NAME("in_src_pubName"), //$NON-NLS-1$
			PUBLISHER_SETTING_NAME("in_src_pubSetName"), //$NON-NLS-1$
			PUBLISHER_FORMAT_SETTING_NAME("in_src_pubFmtSetName"); //$NON-NLS-1$
			private String name;
			
			private SourceTreeView(String name)
			{
				this.name = LanguageBundle.getString(name);
			}
			
			public String getViewName()
			{
				return name;
			}
			
			public List<TreeViewPath<CampaignFacade>> getPaths(CampaignFacade pobj)
			{
				String publisher = pobj.getPublisher();
				if (publisher == null)
				{
					publisher = LanguageBundle.getString("in_other"); //$NON-NLS-1$
				}
				String setting = pobj.getSetting();
				String format = pobj.getFormat();
				switch (this)
				{
					case NAME:
						return Collections.singletonList(new TreeViewPath<CampaignFacade>(
								pobj));
					case PUBLISHER_FORMAT_SETTING_NAME:
						if (format != null && setting != null)
						{
							return Collections.singletonList(new TreeViewPath<CampaignFacade>(
									pobj, publisher, format, setting));
						}
						if (format != null)
						{
							return Collections.singletonList(new TreeViewPath<CampaignFacade>(
									pobj, publisher, format));
						}
					case PUBLISHER_SETTING_NAME:
						if (setting != null)
						{
							return Collections.singletonList(new TreeViewPath<CampaignFacade>(
									pobj, publisher, setting));
						}
					case PUBLISHER_NAME:
						return Collections.singletonList(new TreeViewPath<CampaignFacade>(
								pobj, publisher));
					default:
						throw new InternalError();
				}
			}
			
		}
		
	}

	/**
	 * The Class <code>CampaignRenderer</code> displays the tree cells of the
	 * source table.  
	 */
	private class CampaignRenderer extends DefaultTreeCellRenderer
	{

		/**
		 * Create a new renderer for the campaign names for a game mode. The 
		 * names will be coloured to show if they are qualified or not.
		 */
		public CampaignRenderer()
		{
			setTextNonSelectionColor(UIPropertyContext.getQualifiedColor());
			setClosedIcon(null);
			setLeafIcon(null);
			setOpenIcon(null);
		}

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value,
													  boolean sel, boolean expanded, boolean leaf, int row,
													  boolean hasFocus)
		{

			super.getTreeCellRendererComponent(tree, value, sel, expanded,
											   leaf, row, hasFocus);
			Object campaignObj = ((DefaultMutableTreeNode) value).getUserObject();
			if (campaignObj instanceof CampaignFacade)
			{
				CampaignFacade campaign = (CampaignFacade) campaignObj;
				List<CampaignFacade> testCampaigns = selectedCampaigns.getContents();
				testCampaigns.add(campaign);
				if (!FacadeFactory.passesPrereqs(gameMode, testCampaigns))
				{
					setForeground(UIPropertyContext.getNotQualifiedColor());
				}
			}
			return this;
		}

	}
	
}
