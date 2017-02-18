/**
 * RaceInfoTab.java Copyright James Dempsey, 2010
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *
 */
package pcgen.gui2.tabs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.InfoFactory;
import pcgen.facade.core.RaceFacade;
import pcgen.facade.util.DefaultListFacade;
import pcgen.facade.util.ListFacade;
import pcgen.gui2.filter.Filter;
import pcgen.gui2.filter.FilterBar;
import pcgen.gui2.filter.FilterButton;
import pcgen.gui2.filter.FilteredTreeViewTable;
import pcgen.gui2.filter.SearchFilterPanel;
import pcgen.gui2.tabs.models.CharacterTreeCellRenderer.Handler;
import pcgen.gui2.tabs.models.QualifiedTreeCellRenderer;
import pcgen.gui2.tools.FlippingSplitPane;
import pcgen.gui2.tools.Icons;
import pcgen.gui2.tools.InfoPane;
import pcgen.gui2.util.treeview.CachedDataView;
import pcgen.gui2.util.treeview.DataView;
import pcgen.gui2.util.treeview.DataViewColumn;
import pcgen.gui2.util.treeview.DefaultDataViewColumn;
import pcgen.gui2.util.treeview.TreeView;
import pcgen.gui2.util.treeview.TreeViewModel;
import pcgen.gui2.util.treeview.TreeViewPath;
import pcgen.system.LanguageBundle;
import pcgen.util.enumeration.Tab;

/**
 * The Class {@code RaceInfoTab} is the component used in the Race tab.
 * <br>
 * -0800 (Wed, 16 Feb 2011) $
 *
 */
@SuppressWarnings("serial")
public class RaceInfoTab extends FlippingSplitPane implements CharacterInfoTab
{

	private static final TabTitle title = new TabTitle(Tab.RACE);
	private final FilteredTreeViewTable<Object, RaceFacade> raceTable;
	private final FilteredTreeViewTable<Object, RaceFacade> selectedTable;
	private final InfoPane infoPane;
	private final JButton selectRaceButton;
	private final JButton removeButton;
	private final FilterButton<Object, RaceFacade> qFilterButton;
	private final FilterButton<Object, RaceFacade> noRacialHdFilterButton;
	private final QualifiedTreeCellRenderer qualifiedRenderer;

	public RaceInfoTab()
	{
		super("Race");
		this.raceTable = new FilteredTreeViewTable<>();
		this.selectedTable = new FilteredTreeViewTable<>();
		this.infoPane = new InfoPane(LanguageBundle.getString("in_irRaceInfo")); //$NON-NLS-1$
		this.selectRaceButton = new JButton();
		this.removeButton = new JButton();
		this.qFilterButton = new FilterButton<>("RaceQualified");
		this.noRacialHdFilterButton = new FilterButton<>("RaceNoHD");
		this.qualifiedRenderer = new QualifiedTreeCellRenderer();
		initComponents();
	}

	private void initComponents()
	{
		FlippingSplitPane topPane = new FlippingSplitPane("RaceTop");
		setTopComponent(topPane);
		setOrientation(VERTICAL_SPLIT);

		JPanel availPanel = new JPanel(new BorderLayout());
		FilterBar<Object, RaceFacade> bar = new FilterBar<>();
		bar.addDisplayableFilter(new SearchFilterPanel());
		noRacialHdFilterButton.setText(LanguageBundle.getString("in_irNoRacialHd")); //$NON-NLS-1$
		noRacialHdFilterButton.setToolTipText(LanguageBundle.getString("in_irNoRacialHdTip")); //$NON-NLS-1$
		bar.addDisplayableFilter(noRacialHdFilterButton);
		qFilterButton.setText(LanguageBundle.getString("in_igQualFilter")); //$NON-NLS-1$
		bar.addDisplayableFilter(qFilterButton);
		raceTable.setDisplayableFilter(bar);
		availPanel.add(bar, BorderLayout.NORTH);

		raceTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		raceTable.setTreeCellRenderer(qualifiedRenderer);
		availPanel.add(new JScrollPane(raceTable), BorderLayout.CENTER);

		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalGlue());
		selectRaceButton.setHorizontalTextPosition(SwingConstants.LEADING);

		box.add(selectRaceButton);
		box.add(Box.createHorizontalStrut(5));
		box.setBorder(new EmptyBorder(0, 0, 5, 0));
		availPanel.add(box, BorderLayout.SOUTH);

		topPane.setLeftComponent(availPanel);

		JPanel selPanel = new JPanel(new BorderLayout());
		FilterBar<Object, RaceFacade> filterBar = new FilterBar<>();
		filterBar.addDisplayableFilter(new SearchFilterPanel());

		selectedTable.setDisplayableFilter(filterBar);
		selectedTable.setTreeCellRenderer(qualifiedRenderer);
		JScrollPane scrollPane = new JScrollPane(selectedTable);
		selPanel.add(scrollPane, BorderLayout.CENTER);
		scrollPane.setPreferredSize(new Dimension(0, 0));

		box = Box.createHorizontalBox();
		box.add(Box.createHorizontalStrut(5));
		box.add(removeButton);
		box.add(Box.createHorizontalGlue());
		box.setBorder(new EmptyBorder(0, 0, 5, 0));
		selPanel.add(box, BorderLayout.SOUTH);

		topPane.setRightComponent(selPanel);
		topPane.setResizeWeight(0.75);

		setBottomComponent(infoPane);
		setResizeWeight(0.75);
	}

	@Override
	public ModelMap createModels(CharacterFacade character)
	{
		ModelMap models = new ModelMap();
		models.put(SelectRaceAction.class, new SelectRaceAction(character));
		models.put(RemoveRaceAction.class, new RemoveRaceAction(character));
		models.put(TreeViewModelHandler.class, new TreeViewModelHandler(character));
		models.put(InfoHandler.class, new InfoHandler(character));
		models.put(Handler.class, qualifiedRenderer.createHandler(character));
		models.put(QualifiedFilterHandler.class, new QualifiedFilterHandler(character));
		models.put(NoRacialHdFilterHandler.class, new NoRacialHdFilterHandler(character));
		return models;
	}

	@Override
	public void restoreModels(ModelMap models)
	{
		models.get(QualifiedFilterHandler.class).install();
		models.get(NoRacialHdFilterHandler.class).install();
		models.get(Handler.class).install();
		models.get(TreeViewModelHandler.class).install();
		models.get(InfoHandler.class).install();
		models.get(SelectRaceAction.class).install();
		models.get(RemoveRaceAction.class).install();
	}

	@Override
	public void storeModels(ModelMap models)
	{
		models.get(TreeViewModelHandler.class).uninstall();
		models.get(InfoHandler.class).uninstall();
		models.get(SelectRaceAction.class).uninstall();
		models.get(RemoveRaceAction.class).uninstall();
		models.get(Handler.class).uninstall();
	}

	@Override
	public TabTitle getTabTitle()
	{
		return title;
	}

	private class InfoHandler implements ListSelectionListener
	{

		private final CharacterFacade character;
		private String text;

		public InfoHandler(CharacterFacade character)
		{
			this.character = character;
			this.text = ""; //$NON-NLS-1$
		}

		public void install()
		{
			raceTable.getSelectionModel().addListSelectionListener(this);
			selectedTable.getSelectionModel().addListSelectionListener(this);
			infoPane.setText(text);
		}

		public void uninstall()
		{
			raceTable.getSelectionModel().removeListSelectionListener(this);
			selectedTable.getSelectionModel().removeListSelectionListener(this);
		}

		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			if (!e.getValueIsAdjusting())
			{
				Object obj = null;
				if (e.getSource() == raceTable.getSelectionModel())
				{
					int selectedRow = raceTable.getSelectedRow();
					if (selectedRow != -1)
					{
						obj = raceTable.getModel().getValueAt(selectedRow, 0);
					}
				}
				else
				{
					int selectedRow = selectedTable.getSelectedRow();
					if (selectedRow != -1)
					{
						obj = selectedTable.getModel().getValueAt(selectedRow, 0);
					}
				}
				if (obj instanceof RaceFacade)
				{
					text = character.getInfoFactory().getHTMLInfo((RaceFacade) obj);
					infoPane.setText(text);
				}
			}
		}

	}

	private class SelectRaceAction extends AbstractAction
	{

		private final CharacterFacade character;

		public SelectRaceAction(CharacterFacade character)
		{
			super(LanguageBundle.getString("in_irSelectRace")); //$NON-NLS-1$
			this.character = character;
			putValue(SMALL_ICON, Icons.Forward16.getImageIcon());
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			Object obj = raceTable.getSelectedObject();
			if (obj instanceof RaceFacade)
			{
				character.setRace((RaceFacade) obj);
			}
		}

		public void install()
		{
			raceTable.addActionListener(this);
			selectRaceButton.setAction(this);
		}

		public void uninstall()
		{
			raceTable.removeActionListener(this);
		}

	}

	private class RemoveRaceAction extends AbstractAction
	{

		private final CharacterFacade character;

		public RemoveRaceAction(CharacterFacade character)
		{
			super(LanguageBundle.getString("in_irUnselectRace")); //$NON-NLS-1$
			this.character = character;
			putValue(SMALL_ICON, Icons.Back16.getImageIcon());
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			character.setRace(null);
		}

		public void install()
		{
			selectedTable.addActionListener(this);
			removeButton.setAction(this);
		}

		public void uninstall()
		{
			selectedTable.removeActionListener(this);
		}

	}

	/**
	 * The Class {@code NoRacialHdFilterHandler} provides the filter
	 * backing the No Racial HD filter button.
	 */
	private class NoRacialHdFilterHandler implements Filter<Object, RaceFacade>
	{

		private final InfoFactory infoFactory;

		public NoRacialHdFilterHandler(CharacterFacade character)
		{
			this.infoFactory = character.getInfoFactory();
		}

		public void install()
		{
			noRacialHdFilterButton.setFilter(this);
		}

		@Override
		public boolean accept(Object context, RaceFacade element)
		{
			return infoFactory.getNumMonsterClassLevels(element) == 0;
		}

	}

	/**
	 * The Class {@code QualifiedFilterHandler} provides the filter backing
	 * the Qualified filter button.
	 */
	private class QualifiedFilterHandler implements Filter<Object, RaceFacade>
	{

		private final CharacterFacade character;

		public QualifiedFilterHandler(CharacterFacade character)
		{
			this.character = character;
		}

		public void install()
		{
			qFilterButton.setFilter(this);
		}

		@Override
		public boolean accept(Object context, RaceFacade element)
		{
			return character.isQualifiedFor(element);
		}

	}

	private class TreeViewModelHandler
	{

		private final RaceDataView availableView;
		private final RaceDataView selectedView;
		private final RaceTreeViewModel availableModel;
		private final RaceTreeViewModel selectedModel;

		public TreeViewModelHandler(CharacterFacade character)
		{
			availableView = new RaceDataView(character, true);
			selectedView = new RaceDataView(character, false);
			availableModel = new RaceTreeViewModel(character, true, availableView);
			selectedModel = new RaceTreeViewModel(character, false, selectedView);
		}

		public void install()
		{
			raceTable.setTreeViewModel(availableModel);
			selectedTable.setTreeViewModel(selectedModel);
		}

		public void uninstall()
		{
		}
	}

	private class RaceDataView extends CachedDataView<RaceFacade>
	{

		private final List<DefaultDataViewColumn> columns;
		private final InfoFactory infoFactory;
		private final boolean isAvailModel;

		public RaceDataView(CharacterFacade character, boolean isAvailModel)
		{
			this.infoFactory = character.getInfoFactory();
			this.isAvailModel = isAvailModel;
			if (isAvailModel)
			{
				columns = Arrays.asList(new DefaultDataViewColumn("in_irTableStat", String.class, true), //$NON-NLS-1$
						new DefaultDataViewColumn("in_preReqs", String.class), //$NON-NLS-1$
						new DefaultDataViewColumn("in_size", String.class, true), //$NON-NLS-1$
						new DefaultDataViewColumn("in_movement", String.class, true), //$NON-NLS-1$
						new DefaultDataViewColumn("in_vision", String.class), //$NON-NLS-1$
						new DefaultDataViewColumn("in_favoredClass", String.class, true), //$NON-NLS-1$
						new DefaultDataViewColumn("in_lvlAdj", String.class, true), //$NON-NLS-1$
						new DefaultDataViewColumn("in_descrip", String.class, false), //$NON-NLS-1$
						new DefaultDataViewColumn("in_source", String.class, false)); //$NON-NLS-1$
			}
			else
			{
				columns = Arrays.asList(new DefaultDataViewColumn("in_irTableStat", String.class, false), //$NON-NLS-1$
						new DefaultDataViewColumn("in_preReqs", String.class, false), //$NON-NLS-1$
						new DefaultDataViewColumn("in_size", String.class, false), //$NON-NLS-1$
						new DefaultDataViewColumn("in_movement", String.class, false), //$NON-NLS-1$
						new DefaultDataViewColumn("in_vision", String.class, false), //$NON-NLS-1$
						new DefaultDataViewColumn("in_favoredClass", String.class, false), //$NON-NLS-1$
						new DefaultDataViewColumn("in_lvlAdj", String.class, false), //$NON-NLS-1$
						new DefaultDataViewColumn("in_descrip", String.class, false), //$NON-NLS-1$
						new DefaultDataViewColumn("in_source", String.class, false)); //$NON-NLS-1$
			}
		}

		@Override
		public List<? extends DataViewColumn> getDataColumns()
		{
			return columns;
		}

		@Override
		public String getPrefsKey()
		{
			return isAvailModel ? "RaceTreeAvail" : "RaceTreeSelected";  //$NON-NLS-1$//$NON-NLS-2$
		}

		@Override
		public Object getDataInternal(RaceFacade obj, int column)
		{
			switch(column){
				case 0:
					return infoFactory.getStatAdjustments(obj);
				case 1:
					return infoFactory.getPreReqHTML(obj);
				case 2:
					return obj.getSize();
				case 3:
					return infoFactory.getMovement(obj);
				case 4:
					return infoFactory.getVision(obj);
				case 5:
					return infoFactory.getFavoredClass(obj);
				case 6:
					return infoFactory.getLevelAdjustment(obj);
				case 7:
					return infoFactory.getDescription(obj);
				case 8:
					return obj.getSource();
				default:
					return null;
			}
		}

		@Override
		public void setData(Object value, RaceFacade element, int column)
		{
		}

//		@Override
//		protected void refreshTableData()
//		{
//			if (isAvailModel)
//			{
//				raceTable.refreshModelData();
//			}
//			else
//			{
//				selectedTable.refreshModelData();
//			}
//		}

	}

	private static class RaceTreeViewModel implements TreeViewModel<RaceFacade>
	{

		private static final DefaultListFacade<? extends TreeView<RaceFacade>> treeViews
				= new DefaultListFacade<TreeView<RaceFacade>>(Arrays.asList(RaceTreeView.values()));
		private final CharacterFacade character;
		private final boolean isAvailModel;
		private final DataView<RaceFacade> dataView;

		public RaceTreeViewModel(CharacterFacade character, boolean isAvailModel, DataView<RaceFacade> dataView)
		{
			this.character = character;
			this.isAvailModel = isAvailModel;
			this.dataView = dataView;
		}

		@Override
		public ListFacade<? extends TreeView<RaceFacade>> getTreeViews()
		{
			return treeViews;
		}

		@Override
		public int getDefaultTreeViewIndex()
		{
			return 0;
		}

		@Override
		public DataView<RaceFacade> getDataView()
		{
			return dataView;
		}

		@Override
		public ListFacade<RaceFacade> getDataModel()
		{
			if (isAvailModel)
			{
				return character.getDataSet().getRaces();
			}
			else
			{
				return character.getRaceAsList();
			}
		}

	}

	private enum RaceTreeView implements TreeView<RaceFacade>
	{

		NAME(LanguageBundle.getString("in_nameLabel")), //$NON-NLS-1$
		TYPE_NAME(LanguageBundle.getString("in_typeName")), //$NON-NLS-1$
		RACETYPE_NAME(LanguageBundle.getString("in_racetypeName")), //$NON-NLS-1$
		RACETYPE_RACE_SUBTYPE_NAME(
				LanguageBundle.getString("in_racetypeSubtypeName")), //$NON-NLS-1$
		SOURCE_NAME(LanguageBundle.getString("in_sourceName")); //$NON-NLS-1$
		private final String name;

		private RaceTreeView(String name)
		{
			this.name = name;
		}

		@Override
		public String getViewName()
		{
			return name;
		}

		@Override
		public List<TreeViewPath<RaceFacade>> getPaths(RaceFacade pobj)
		{
			switch (this)
			{
				case NAME:
					return Collections.singletonList(new TreeViewPath<>(pobj));
				case TYPE_NAME:
					return Collections.singletonList(new TreeViewPath<>(pobj,
                            pobj.getType()));
				case RACETYPE_RACE_SUBTYPE_NAME:
					List<String> subtypes = pobj.getRaceSubTypes();
					if (!subtypes.isEmpty())
					{
						List<TreeViewPath<RaceFacade>> paths
								= new ArrayList<>();
						String raceType = pobj.getRaceType();
						for (String subtype : subtypes)
						{
							paths.add(new TreeViewPath<>(pobj,
                                    raceType, subtype));
						}
						return paths;
					}
					// No subtypes, fall through to treat it as a type tree.
					return Collections.singletonList(new TreeViewPath<>(pobj,
                            pobj.getRaceType()));
				case RACETYPE_NAME:
					return Collections.singletonList(new TreeViewPath<>(pobj,
                            pobj.getRaceType()));
				case SOURCE_NAME:
					return Collections.singletonList(new TreeViewPath<>(pobj,
                            pobj.getSourceForNodeDisplay()));
				default:
					throw new InternalError();
			}
		}

	}

}
