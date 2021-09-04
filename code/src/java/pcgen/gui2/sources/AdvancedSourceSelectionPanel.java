/*
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
 */
package pcgen.gui2.sources;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Campaign;
import pcgen.core.GameMode;
import pcgen.facade.core.GameModeDisplayFacade;
import pcgen.facade.core.SourceSelectionFacade;
import pcgen.facade.util.DefaultListFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.ListFacades;
import pcgen.facade.util.event.ListEvent;
import pcgen.facade.util.event.ListListener;
import pcgen.gui2.PCGenFrame;
import pcgen.gui2.UIContext;
import pcgen.gui2.UIPropertyContext;
import pcgen.gui2.filter.FilterBar;
import pcgen.gui2.filter.FilteredTreeViewTable;
import pcgen.gui2.filter.SearchFilterPanel;
import pcgen.gui2.tools.FlippingSplitPane;
import pcgen.gui2.tools.Icons;
import pcgen.gui2.tools.InfoPane;
import pcgen.gui2.tools.InfoPaneLinkAction;
import pcgen.gui2.util.FacadeComboBoxModel;
import pcgen.gui2.util.TreeColumnCellRenderer;
import pcgen.gui2.util.table.DynamicTableColumnModel;
import pcgen.gui2.util.table.TableCellUtilities;
import pcgen.gui2.util.treeview.DataView;
import pcgen.gui2.util.treeview.DataViewColumn;
import pcgen.gui2.util.treeview.DefaultDataViewColumn;
import pcgen.gui2.util.treeview.TreeView;
import pcgen.gui2.util.treeview.TreeViewModel;
import pcgen.gui2.util.treeview.TreeViewPath;
import pcgen.gui3.utilty.ColorUtilty;
import pcgen.system.FacadeFactory;
import pcgen.system.LanguageBundle;
import pcgen.util.Comparators;
import pcgen.util.Logging;

import org.apache.commons.lang3.StringUtils;

class AdvancedSourceSelectionPanel extends JPanel
		implements ListSelectionListener, ListListener<Campaign>, ActionListener
{

	private static final UIPropertyContext CONTEXT = 
			UIPropertyContext.createContext("advancedSourceSelectionPanel"); //$NON-NLS-1$
	private static final String PROP_SELECTED_GAME = "selectedGame"; //$NON-NLS-1$
	private static final String PROP_SELECTED_SOURCES = "selectedSources."; //$NON-NLS-1$

	private final FilteredTreeViewTable<Object, Campaign> availableTable;
	private final FilteredTreeViewTable<Object, Campaign> selectedTable;
	private final SourceTreeViewModel availTreeViewModel;
	private final SourceTreeViewModel selTreeViewModel;
	private final InfoPane infoPane;
	private GameMode gameMode;
	private final JComboBox gameModeList;
	private final InfoPaneLinkAction linkAction;
	private final JButton unloadAllButton;
	private final JButton addButton;
	private final JButton removeButton;
	private final DefaultListFacade<Campaign> selectedCampaigns;
	private final PCGenFrame frame;

	/**
	 * The context indicating what items are currently loaded/being processed in the UI
	 */
	private final UIContext uiContext;
	
	public AdvancedSourceSelectionPanel(PCGenFrame frame, UIContext uiContext)
	{
		this.uiContext = Objects.requireNonNull(uiContext);
		this.frame = frame;
		this.availableTable = new FilteredTreeViewTable<>();
		this.selectedTable = new FilteredTreeViewTable<>();
		this.selectedCampaigns = new DefaultListFacade<>();
		this.availTreeViewModel = new SourceTreeViewModel();
		this.selTreeViewModel = new SourceTreeViewModel(selectedCampaigns);
		this.gameModeList = new JComboBox<>();
		this.unloadAllButton = new JButton();
		this.addButton = new JButton();
		this.removeButton = new JButton();
		this.infoPane = new InfoPane(LanguageBundle.getString("in_src_info")); //$NON-NLS-1$
		this.linkAction = new InfoPaneLinkAction(infoPane);

		initComponents();
		initDefaults();
		selectedCampaigns.addListListener(this);
	}

	private void initComponents()
	{
		FlippingSplitPane mainPane = new FlippingSplitPane(JSplitPane.VERTICAL_SPLIT);
		FlippingSplitPane topPane = new FlippingSplitPane();
		topPane.setResizeWeight(0.6);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JLabel(LanguageBundle.getString("in_src_gameLabel")), BorderLayout.WEST); //$NON-NLS-1$

		FacadeComboBoxModel<GameModeDisplayFacade> gameModes = new FacadeComboBoxModel<>();
		gameModes.setListFacade(FacadeFactory.getGameModeDisplays());
		gameModeList.setModel(gameModes);
		gameModeList.addActionListener(this);
		panel.add(gameModeList, BorderLayout.CENTER);

		FilterBar<Object, Campaign> bar = new FilterBar<>(false);
		bar.add(panel, BorderLayout.WEST);
		bar.addDisplayableFilter(new SearchFilterPanel());
		panel = new JPanel(new BorderLayout());
		panel.add(bar, BorderLayout.NORTH);

		availableTable.setDisplayableFilter(bar);
		availableTable.setTreeViewModel(availTreeViewModel);
		availableTable.getSelectionModel().addListSelectionListener(this);
		availableTable.setTreeCellRenderer(new CampaignRenderer());
		((DynamicTableColumnModel) availableTable.getColumnModel()).getAvailableColumns().get(2)
			.setCellRenderer(new TableCellUtilities.AlignRenderer(SwingConstants.CENTER));

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
		box.setBorder(new EmptyBorder(0, 0, 5, 0));
		panel.add(box, BorderLayout.SOUTH);

		topPane.setLeftComponent(panel);

		JPanel selPanel = new JPanel(new BorderLayout());
		FilterBar<Object, Campaign> filterBar = new FilterBar<>();
		filterBar.addDisplayableFilter(new SearchFilterPanel());
		selectedTable.setDisplayableFilter(filterBar);

		selectedTable.setTreeViewModel(selTreeViewModel);
		selectedTable.getSelectionModel().addListSelectionListener(this);
		selectedTable.setTreeCellRenderer(new CampaignRenderer());
		((DynamicTableColumnModel) selectedTable.getColumnModel()).getAvailableColumns().get(2)
			.setCellRenderer(new TableCellUtilities.AlignRenderer(SwingConstants.CENTER));
		JScrollPane scrollPane = new JScrollPane(selectedTable);
		scrollPane.setPreferredSize(new Dimension(300, 350));
		selPanel.add(scrollPane, BorderLayout.CENTER);
		box = Box.createHorizontalBox();
		box.add(Box.createHorizontalStrut(5));
		removeButton.setAction(new RemoveAction());
		box.add(removeButton);
		box.add(Box.createHorizontalGlue());
		box.setBorder(new EmptyBorder(0, 0, 5, 0));
		selPanel.add(box, BorderLayout.SOUTH);

		topPane.setRightComponent(selPanel);
		mainPane.setTopComponent(topPane);

		linkAction.install();
		infoPane.setPreferredSize(new Dimension(800, 150));
		mainPane.setBottomComponent(infoPane);
		mainPane.setResizeWeight(0.7);
		setLayout(new BorderLayout());
		add(mainPane, BorderLayout.CENTER);
	}

	private void initDefaults()
	{
		String defaultGame = CONTEXT.initProperty(PROP_SELECTED_GAME, "");
		GameModeDisplayFacade modeDisplay = null;
		if (StringUtils.isNotEmpty(defaultGame))
		{
			for (int i = 0; i < gameModeList.getModel().getSize(); i++)
			{
				GameModeDisplayFacade game = (GameModeDisplayFacade) gameModeList.getModel().getElementAt(i);
				if (defaultGame.equals(game.getGameMode().toString()))
				{
					gameModeList.setSelectedIndex(i);
					modeDisplay = game;
				}
			}
		}
		if (modeDisplay == null && gameModeList.getModel().getSize() > 0)
		{
			gameModeList.setSelectedIndex(0);
			modeDisplay = (GameModeDisplayFacade) gameModeList.getSelectedItem();
		}
	}

	/**
	 * Add the user's previously selected sources for this gamemode 
	 * to the selected list.  
	 * @param mode The game mode being selected
	 */
	private void selectDefaultSources(GameMode mode)
	{
		if (mode != null)
		{
			List<String> sourceNames;
			String defaultSelectedSources = CONTEXT.initProperty(
				PROP_SELECTED_SOURCES + mode.toString(), ""); //$NON-NLS-1$
			if (defaultSelectedSources == null || "".equals(defaultSelectedSources))
			{
				sourceNames = mode.getDefaultDataSetList();
			}
			else
			{
				sourceNames = Arrays.asList(defaultSelectedSources.split("\\|")); //$NON-NLS-1$
			}

			for (String name : sourceNames)
			{
				for (Campaign camp : FacadeFactory.getSupportedCampaigns(mode))
				{
					if (name.equals(camp.toString()))
					{
						selectedCampaigns.addElement(camp);
						break;
					}
				}
			}
		}
	}

	public GameMode getSelectedGameMode()
	{
		return gameMode;
	}

	public List<Campaign> getSelectedCampaigns()
	{
		return selectedCampaigns.getContents();
	}

	public void setSourceSelection(SourceSelectionFacade sources)
	{
		if (sources == null || sources.getGameMode() == null)
		{
			Logging.errorPrint("Invalid source selection " + sources + "- ignoring.");
			return;
		}
		GameMode selectedGame = sources.getGameMode().get();
		for (int i = 0; i < gameModeList.getModel().getSize(); i++)
		{
			GameModeDisplayFacade gmdf = (GameModeDisplayFacade) gameModeList.getModel().getElementAt(i);
			if (gmdf.getGameMode() == selectedGame)
			{
				gameModeList.setSelectedItem(gmdf);
			}
		}
		List<Campaign> wrap = new ArrayList<>(ListFacades.wrap(sources.getCampaigns()));
		wrap.sort(Comparators.toStringIgnoreCaseCollator());
		selectedCampaigns.setContents(wrap);
	}

	private void setSelectedGameMode(GameModeDisplayFacade elementAt)
	{
		this.gameMode = elementAt.getGameMode();
		CONTEXT.setProperty(PROP_SELECTED_GAME, gameMode.toString());
		selectedCampaigns.clearContents();
		availTreeViewModel.setGameModel(elementAt.getGameMode());
		selectDefaultSources(gameMode);
	}

	private void setSelectedCampaign(Campaign source)
	{
		infoPane.setText(FacadeFactory.getCampaignInfoFactory().getHTMLInfo(source, selectedCampaigns.getContents()));
	}

	@Override
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
					if (data != null && data instanceof Campaign)
					{
						setSelectedCampaign((Campaign) data);
					}
				}
			}
			else if (e.getSource() == selectedTable.getSelectionModel())
			{
				int selectedRow = selectedTable.getSelectedRow();
				if (selectedRow != -1)
				{
					final Object data = selectedTable.getModel().getValueAt(selectedRow, 0);
					if (data != null && data instanceof Campaign)
					{
						setSelectedCampaign((Campaign) data);
					}
				}
			}
		}
	}

	void refreshDisplay()
	{
		availableTable.refreshModelData();
		selectedTable.refreshModelData();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		// Signal from the game mode conbo that the selection has changed.
		setSelectedGameMode((GameModeDisplayFacade) gameModeList.getSelectedItem());
	}

	@Override
	public void elementAdded(ListEvent<Campaign> e)
	{
		// Refresh displayed rows now that the selection has changed
		availableTable.updateDisplay();
	}

	@Override
	public void elementRemoved(ListEvent<Campaign> e)
	{
		// Refresh displayed rows now that the selection has changed
		availableTable.updateDisplay();
	}

	@Override
	public void elementsChanged(ListEvent<Campaign> e)
	{
		// Refresh displayed rows now that the selection has changed
		availableTable.updateDisplay();
	}

	@Override
	public void elementModified(ListEvent<Campaign> e)
	{
		availableTable.updateDisplay();
	}

	/**
	 * Save the selected sources for the current game mode so they can be 
	 * restored next time we start.
	 */
	void rememberSelectedSources()
	{
		String sources = StringUtils.join(getSelectedCampaigns(), "|"); //$NON-NLS-1$
		CONTEXT.setProperty(PROP_SELECTED_SOURCES + gameMode.toString(), sources);
	}

	private class AddAction extends AbstractAction
	{

		public AddAction()
		{
			super(LanguageBundle.getString("in_addSelected")); //$NON-NLS-1$
			putValue(SMALL_ICON, Icons.Forward16.getImageIcon());
			availableTable.addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			List<Object> list = availableTable.getSelectedData();
			if (list != null && !list.isEmpty())
			{
				for (Object obj : list)
				{
					if (obj instanceof Campaign camp)
					{
						if (selectedCampaigns.containsElement(camp))
						{
							// Already in the list - ignore
							continue;
						}
						selectedCampaigns.addElement(camp);
						if (!FacadeFactory.passesPrereqs(selectedCampaigns.getContents()))
						{
							String prereqDesc = FacadeFactory.getCampaignInfoFactory().getRequirementsHTMLString(camp,
								selectedCampaigns.getContents());
							JOptionPane.showMessageDialog(AdvancedSourceSelectionPanel.this,
								LanguageBundle.getFormattedString("in_src_badComboMsg", //$NON-NLS-1$
									prereqDesc),
								LanguageBundle.getString("in_src_badComboTitle"), //$NON-NLS-1$
								JOptionPane.INFORMATION_MESSAGE);
							selectedCampaigns.removeElement(camp);
						}
					}
				}
				rememberSelectedSources();
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

		@Override
		public void actionPerformed(ActionEvent e)
		{
			List<Object> list = selectedTable.getSelectedData();
			if (list != null && !list.isEmpty())
			{
				for (Object obj : list)
				{
					if (obj instanceof Campaign)
					{
						selectedCampaigns.removeElement((Campaign) obj);
					}
				}
				rememberSelectedSources();
			}
		}
	}

	private class UnloadAllAction extends AbstractAction
	{

		public UnloadAllAction()
		{
			super(LanguageBundle.getString("in_src_unloadAll")); //$NON-NLS-1$
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.unloadSources();
			availableTable.refreshModelData();
			selectedTable.refreshModelData();
		}
	}

	private class SourceTreeViewModel
			implements TreeViewModel<Campaign>, DataView<Campaign>, ListListener<Campaign>
	{

		private final ListFacade<TreeView<Campaign>> views =
				new DefaultListFacade<>(Arrays.asList(SourceTreeView.values()));
		private final DefaultListFacade<Campaign> model;
		private ListFacade<Campaign> baseModel = null;
		private final List<DefaultDataViewColumn> columns;
		private final boolean isAvailModel;

		public SourceTreeViewModel()
		{
			this.model = new DefaultListFacade<>();
			this.isAvailModel = true;
			columns = Arrays.asList(new DefaultDataViewColumn("in_src_bookType", String.class, true),
				new DefaultDataViewColumn("in_src_status", String.class, true),
				new DefaultDataViewColumn("in_src_loaded", String.class, false));
		}

		public SourceTreeViewModel(DefaultListFacade<Campaign> model)
		{
			this.model = model;
			this.isAvailModel = false;
			columns = Arrays.asList(new DefaultDataViewColumn("in_src_bookType", String.class, false),
				new DefaultDataViewColumn("in_src_status", String.class, false),
				new DefaultDataViewColumn("in_src_loaded", String.class, false));
		}

		@Override
		public ListFacade<? extends TreeView<Campaign>> getTreeViews()
		{
			return views;
		}

		@Override
		public int getDefaultTreeViewIndex()
		{
			return isAvailModel ? 2 : 0;
		}

		@Override
		public DataView<Campaign> getDataView()
		{
			return this;
		}

		@Override
		public ListFacade<Campaign> getDataModel()
		{
			return model;
		}

		@Override
		public Object getData(Campaign obj, int column)
		{
			SourceSelectionFacade sourceFacade = uiContext.getCurrentSourceSelectionRef().get();
			boolean isLoaded = (sourceFacade != null) && sourceFacade.getCampaigns().containsElement(obj);
			switch (column)
			{
				case 0:
					return obj.getListAsString(ListKey.BOOK_TYPE);
				case 1:
					return obj.getSafe(ObjectKey.STATUS).toString();
				case 2:
					return isLoaded ? LanguageBundle.getString("in_yes") : LanguageBundle.getString("in_no");
				default:
					return null;
			}
		}

		@Override
		public void setData(Object value, Campaign element, int column)
		{
		}

		@Override
		public List<? extends DataViewColumn> getDataColumns()
		{
			return columns;
		}

		public void setGameModel(GameMode gameMode)
		{
			if (baseModel != null)
			{
				baseModel.removeListListener(this);
			}
			baseModel = FacadeFactory.getSupportedCampaigns(gameMode);
			model.setContents(ListFacades.wrap(baseModel));
			baseModel.addListListener(this);
		}

		@Override
		public void elementAdded(ListEvent<Campaign> e)
		{
			model.addElement(e.getIndex(), e.getElement());
		}

		@Override
		public void elementRemoved(ListEvent<Campaign> e)
		{
			model.removeElement(e.getIndex());
		}

		@Override
		public void elementsChanged(ListEvent<Campaign> e)
		{
			model.setContents(ListFacades.wrap(baseModel));
		}

		@Override
		public void elementModified(ListEvent<Campaign> e)
		{
		}

		@Override
		public String getPrefsKey()
		{
			return isAvailModel ? "SourceAvail" : "SourceSelected"; //$NON-NLS-1$//$NON-NLS-2$
		}

	}

	private static enum SourceTreeView implements TreeView<Campaign>
	{

		NAME("in_nameLabel"), //$NON-NLS-1$
		PUBLISHER_NAME("in_src_pubName"), //$NON-NLS-1$
		PUBLISHER_SETTING_NAME("in_src_pubSetName"), //$NON-NLS-1$
		PUBLISHER_FORMAT_SETTING_NAME("in_src_pubFmtSetName"); //$NON-NLS-1$
		private final String name;

		private SourceTreeView(String name)
		{
			this.name = LanguageBundle.getString(name);
		}

		@Override
		public String getViewName()
		{
			return name;
		}

		@Override
		public List<TreeViewPath<Campaign>> getPaths(Campaign pobj)
		{
			String publisher = pobj.get(StringKey.DATA_PRODUCER);
			if (publisher == null)
			{
				publisher = LanguageBundle.getString("in_other"); //$NON-NLS-1$
			}
			String setting = pobj.get(StringKey.CAMPAIGN_SETTING);
			String format = pobj.get(StringKey.DATA_FORMAT);
			switch (this)
			{
				case NAME:
					return Collections.singletonList(new TreeViewPath<>(pobj));
				case PUBLISHER_FORMAT_SETTING_NAME:
					if (format != null && setting != null)
					{
						return Collections
							.singletonList(new TreeViewPath<>(pobj, publisher, format, setting));
					}
					if (format != null)
					{
						return Collections.singletonList(new TreeViewPath<>(pobj, publisher, format));
					}
				case PUBLISHER_SETTING_NAME:
					if (setting != null)
					{
						return Collections.singletonList(new TreeViewPath<>(pobj, publisher, setting));
					}
				case PUBLISHER_NAME:
					return Collections.singletonList(new TreeViewPath<>(pobj, publisher));
				default:
					throw new InternalError();
			}
		}

	}

	/**
	 * The Class {@code CampaignRenderer} displays the tree cells of the
	 * source table.  
	 */
	private class CampaignRenderer extends TreeColumnCellRenderer
	{

		/**
		 * Create a new renderer for the campaign names for a game mode. The 
		 * names will be coloured to show if they are qualified or not.
		 */
		public CampaignRenderer()
		{
			setTextNonSelectionColor(ColorUtilty.colorToAWTColor(UIPropertyContext.getQualifiedColor()));
		}

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
			boolean leaf, int row, boolean focus)
		{

			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, focus);
			Object campaignObj = ((DefaultMutableTreeNode) value).getUserObject();
			if (campaignObj instanceof Campaign campaign)
			{
				List<Campaign> testCampaigns = selectedCampaigns.getContents();
				testCampaigns.add(campaign);
				if (!FacadeFactory.passesPrereqs(testCampaigns))
				{
					setForeground(ColorUtilty.colorToAWTColor(UIPropertyContext.getNotQualifiedColor()));
				}
			}
			return this;
		}

	}

}
