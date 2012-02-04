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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import pcgen.core.facade.CampaignFacade;
import pcgen.core.facade.GameModeFacade;
import pcgen.core.facade.SourceSelectionFacade;
import pcgen.core.facade.event.ListEvent;
import pcgen.core.facade.event.ListListener;
import pcgen.core.facade.util.DefaultListFacade;
import pcgen.core.facade.util.ListFacade;
import pcgen.core.facade.util.ListFacades;
import pcgen.gui2.filter.FilterBar;
import pcgen.gui2.filter.FilterUtilities;
import pcgen.gui2.filter.FilteredTreeViewTable;
import pcgen.gui2.tools.InfoPane;
import pcgen.gui2.util.SortedListModel;
import pcgen.gui2.util.table.TableUtils;
import pcgen.gui2.util.treeview.DataView;
import pcgen.gui2.util.treeview.DataViewColumn;
import pcgen.gui2.util.treeview.TreeView;
import pcgen.gui2.util.treeview.TreeViewModel;
import pcgen.gui2.util.treeview.TreeViewPath;
import pcgen.gui2.util.treeview.TreeViewTableModel;
import pcgen.system.FacadeFactory;
import pcgen.util.Comparators;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
class AdvancedSourceSelectionPanel extends JPanel
		implements ListSelectionListener
{
	
	private FilteredTreeViewTable selectionTable;
	private SourceTreeViewModel treeViewModel;
	private InfoPane infoPane;
	private GameModeFacade gameMode;
	private JList gameModeList;
	private List<CampaignFacade> selectedCampaigns;
	
	public AdvancedSourceSelectionPanel()
	{
		this.selectionTable = new FilteredTreeViewTable()
		{
			
			@Override
			protected TreeViewTableModel createDefaultTreeViewTableModel(DataView dataView)
			{
				return new TreeViewTableModel(dataView)
				{
					
					@Override
					public Class getColumnClass(int column)
					{
						if (column == -1)
						{
							return Boolean.class;
						}
						return super.getColumnClass(column);
					}
					
					@Override
					public Object getValueAt(Object node, int column)
					{
						if (column != -1)
						{
							return super.getValueAt(node, column);
						}
						Object userObject = ((DefaultMutableTreeNode) node).getUserObject();
						if (userObject instanceof CampaignFacade)
						{
							CampaignFacade camp = (CampaignFacade) userObject;
							return selectedCampaigns.contains(camp);
						}
						return null;
					}
					
					@Override
					public boolean isCellEditable(Object node, int column)
					{
						if (column == -1)
						{
							return ((DefaultMutableTreeNode) node).getUserObject() instanceof CampaignFacade;
						}
						return super.isCellEditable(node, column);
					}
					
					@Override
					public void setValueAt(Object aValue, Object node, int column)
					{
						CampaignFacade camp = (CampaignFacade) ((DefaultMutableTreeNode) node).getUserObject();
						Boolean value = (Boolean) aValue;
						if (value)
						{
							selectedCampaigns.add(camp);
							if (!FacadeFactory.passesPrereqs(gameMode, selectedCampaigns))
							{
								JOptionPane.showMessageDialog(AdvancedSourceSelectionPanel.this,
															  "Prereqs for this campaign have not fulfilled",
															  "Cannot Select Campaign",
															  JOptionPane.INFORMATION_MESSAGE);
								selectedCampaigns.remove(camp);
							}
						}
						else
						{
							selectedCampaigns.remove(camp);
						}
					}
					
				};
			}
			
		};
		this.treeViewModel = new SourceTreeViewModel();
		this.gameModeList = new JList();
		this.infoPane = new InfoPane("Campaign Info");
		this.selectedCampaigns = new ArrayList<CampaignFacade>();
		initComponents();
		initDefaults();
	}
	
	private void initComponents()
	{
		setLayout(new BorderLayout());
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBorder(BorderFactory.createTitledBorder(null, "GameModes",
														 TitledBorder.CENTER,
														 TitledBorder.DEFAULT_POSITION));
		//panel.add(new JLabel("GameModes"), BorderLayout.NORTH);

		ListModel gameModes = new SortedListModel<GameModeFacade>(FacadeFactory.getGameModes(),
																  Comparators.toStringIgnoreCaseCollator());
		gameModeList.setModel(gameModes);
		gameModeList.setCellRenderer(new DefaultListCellRenderer()
		{
			
			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
			{
				value = ((GameModeFacade) value).getDisplayName();
				return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			}
			
		});
		gameModeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		gameModeList.addListSelectionListener(this);
		panel.add(new JScrollPane(gameModeList), BorderLayout.CENTER);
		
		add(panel, BorderLayout.WEST);
		
		FilterBar bar = FilterUtilities.createDefaultFilterBar();
		panel = new JPanel(new BorderLayout());
		panel.add(bar, BorderLayout.NORTH);
		
		selectionTable.setDisplayableFilter(bar);
		selectionTable.setTreeViewModel(treeViewModel);
		selectionTable.toggleSort(0);
		selectionTable.getSelectionModel().addListSelectionListener(this);
		JScrollPane pane = TableUtils.createCheckBoxSelectionPane(selectionTable, TableUtils.createDefaultTable());
		pane.setPreferredSize(new Dimension(400, 400));
		panel.add(pane, BorderLayout.CENTER);
		
		add(panel, BorderLayout.CENTER);

		//infoPane.setMinimumSize(new Dimension(250, 100));
		infoPane.setPreferredSize(new Dimension(300, 200));
		add(infoPane, BorderLayout.EAST);
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
		return selectedCampaigns;
	}
	
	public void setSourceSelection(SourceSelectionFacade sources)
	{
		gameModeList.setSelectedValue(sources.getGameMode().getReference(), true);
		selectedCampaigns.clear();
		selectedCampaigns.addAll(ListFacades.wrap(sources.getCampaigns()));
		//selectionPanel.setSelectedObjects(ListFacades.wrap(sources.getCampaigns()));
	}
	
	private void setSelectedGameMode(GameModeFacade elementAt)
	{
		this.gameMode = elementAt;
		selectedCampaigns.clear();
		treeViewModel.setGameModel(elementAt);
	}
	
	private void setSelectedCampaign(CampaignFacade source)
	{
		infoPane.setText(FacadeFactory.getCampaignInfoFactory().getHTMLInfo(source));
	}
	
	public void valueChanged(ListSelectionEvent e)
	{
		if (!e.getValueIsAdjusting())
		{
			if (e.getSource() == selectionTable.getSelectionModel())
			{
				final Object data = selectionTable.getSelectedObject();
				if (data != null && data instanceof CampaignFacade)
				{
					setSelectedCampaign((CampaignFacade) data);
				}
			}
			else
			{
				setSelectedGameMode((GameModeFacade) gameModeList.getSelectedValue());
			}
		}
	}
	
	private static class SourceTreeViewModel
			implements TreeViewModel<CampaignFacade>, DataView<CampaignFacade>, ListListener<CampaignFacade>
	{
		
		private static ListFacade<TreeView<CampaignFacade>> views =
				new DefaultListFacade<TreeView<CampaignFacade>>(Arrays.asList(SourceTreeView.values()));
		private DefaultListFacade<CampaignFacade> model;
		private ListFacade<CampaignFacade> baseModel = null;
		
		public SourceTreeViewModel()
		{
			this.model = new DefaultListFacade<CampaignFacade>();
		}
		
		public ListFacade<? extends TreeView<CampaignFacade>> getTreeViews()
		{
			return views;
		}
		
		public int getDefaultTreeViewIndex()
		{
			return 2;
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
			return Collections.emptyList();
		}
		
		public List<? extends DataViewColumn> getDataColumns()
		{
			return Collections.emptyList();
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
			
			NAME("Name"),
			PUBLISHER_NAME("Publisher/Name"),
			PUBLISHER_SETTING_NAME("Publisher/Setting/Name"),
			PUBLISHER_FORMAT_SETTING_NAME("Publisher/Format/Setting/Name");
			private String name;
			
			private SourceTreeView(String name)
			{
				this.name = name;
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
					publisher = "Other";
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
	
}
