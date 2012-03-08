/**
 * RaceInfoTab.java
 * Copyright James Dempsey, 2010
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
 * Created on 29/09/2010 7:16:42 PM
 *
 * $Id: RaceInfoTab.java 14578 2011-02-16 20:20:14Z cpmeister $
 */
package pcgen.gui2.tabs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.InfoFactory;
import pcgen.core.facade.RaceFacade;
import pcgen.core.facade.ReferenceFacade;
import pcgen.core.facade.event.ReferenceEvent;
import pcgen.core.facade.event.ReferenceListener;
import pcgen.core.facade.util.DefaultListFacade;
import pcgen.core.facade.util.ListFacade;
import pcgen.gui2.filter.FilterBar;
import pcgen.gui2.filter.FilteredTreeViewTable;
import pcgen.gui2.filter.SearchFilterPanel;
import pcgen.gui2.tabs.models.QualifiedTreeCellRenderer;
import pcgen.gui2.tools.FlippingSplitPane;
import pcgen.gui2.tools.Icons;
import pcgen.gui2.tools.InfoPane;
import pcgen.gui2.util.SortMode;
import pcgen.gui2.util.SortingPriority;
import pcgen.gui2.util.treeview.DataView;
import pcgen.gui2.util.treeview.DataViewColumn;
import pcgen.gui2.util.treeview.DefaultDataViewColumn;
import pcgen.gui2.util.treeview.TreeView;
import pcgen.gui2.util.treeview.TreeViewModel;
import pcgen.gui2.util.treeview.TreeViewPath;
import pcgen.system.LanguageBundle;

/**
 * The Class <code>RaceInfoTab</code> is a placeholder for the yet
 * to be implemented Race tab.
 * <br/>
 * Last Editor: $Author: cpmeister $
 * Last Edited: $Date: 2011-02-16 12:20:14 -0800 (Wed, 16 Feb 2011) $
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: 14578 $
 */
public class RaceInfoTab extends FlippingSplitPane implements CharacterInfoTab
{

	private static final TabTitle title = new TabTitle(LanguageBundle.getString("in_races"));
	private final FilteredTreeViewTable<Object, RaceFacade> raceTable;
	private final FilteredTreeViewTable<Object, RaceFacade> selectedTable;
	private final InfoPane infoPane;
	private final JButton selectRaceButton;
	private final JButton removeButton;

	public RaceInfoTab()
	{
		this.raceTable = new FilteredTreeViewTable<Object, RaceFacade>();
		this.selectedTable = new FilteredTreeViewTable<Object, RaceFacade>();
		this.infoPane = new InfoPane(LanguageBundle.getString("in_irRaceInfo"));
		this.selectRaceButton = new JButton();
		this.removeButton = new JButton();
		initComponents();
	}

	private void initComponents()
	{
		FlippingSplitPane topPane = new FlippingSplitPane();
		setTopComponent(topPane);
		setOrientation(VERTICAL_SPLIT);

		JPanel availPanel = new JPanel(new BorderLayout());
		FilterBar<Object, RaceFacade> bar = new FilterBar<Object, RaceFacade>();
		bar.addDisplayableFilter(new SearchFilterPanel());
		raceTable.setDisplayableFilter(bar);
		availPanel.add(bar, BorderLayout.NORTH);

		raceTable.setSortingPriority(Collections.singletonList(new SortingPriority(0, SortMode.ASCENDING)));
		raceTable.sortModel();
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
		FilterBar<Object, RaceFacade> filterBar = new FilterBar<Object, RaceFacade>();
		filterBar.addDisplayableFilter(new SearchFilterPanel());

		selectedTable.setDisplayableFilter(filterBar);
		selectedTable.setSortingPriority(Collections.singletonList(new SortingPriority(0, SortMode.ASCENDING)));
		selectedTable.sortModel();
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
		topPane.setResizeWeight(.75);

		setBottomComponent(infoPane);
		setResizeWeight(.75);
	}

	private enum Models
	{

		AvailableModel,
		SelectedModel
	}

	@Override
	public Hashtable<Object, Object> createModels(CharacterFacade character)
	{
		Hashtable<Object, Object> state = new Hashtable<Object, Object>();
		state.put(SelectRaceAction.class, new SelectRaceAction(character));
		state.put(RemoveRaceAction.class, new RemoveRaceAction(character));
		state.put(Models.AvailableModel, new RaceTreeViewModel(character, true));
		state.put(Models.SelectedModel, new RaceTreeViewModel(character, false));
		state.put(InfoHandler.class, new InfoHandler(character));
		state.put(QualifiedTreeCellRenderer.class, new QualifiedTreeCellRenderer(character));
		return state;
	}

	@Override
	public void restoreModels(Hashtable<?, ?> state)
	{
		raceTable.setTreeViewModel((RaceTreeViewModel) state.get(Models.AvailableModel));
		selectedTable.setTreeViewModel((RaceTreeViewModel) state.get(Models.SelectedModel));
		((InfoHandler) state.get(InfoHandler.class)).install();
		((SelectRaceAction) state.get(SelectRaceAction.class)).install();
		((RemoveRaceAction) state.get(RemoveRaceAction.class)).install();

		raceTable.setTreeCellRenderer((QualifiedTreeCellRenderer) state.get(QualifiedTreeCellRenderer.class));
		selectedTable.setTreeCellRenderer((QualifiedTreeCellRenderer) state.get(QualifiedTreeCellRenderer.class));
		selectRaceButton.setAction((SelectRaceAction) state.get(SelectRaceAction.class));
		removeButton.setAction((RemoveRaceAction) state.get(RemoveRaceAction.class));
	}

	@Override
	public void storeModels(Hashtable<Object, Object> state)
	{
		((InfoHandler) state.get(InfoHandler.class)).uninstall();
		((SelectRaceAction) state.get(SelectRaceAction.class)).uninstall();
		((RemoveRaceAction) state.get(RemoveRaceAction.class)).uninstall();
	}

	@Override
	public TabTitle getTabTitle()
	{
		return title;
	}

	private class InfoHandler implements ListSelectionListener
	{

		private final CharacterFacade character;

		public InfoHandler(CharacterFacade character)
		{
			this.character = character;
		}

		public void install()
		{
			raceTable.getSelectionModel().addListSelectionListener(this);
			selectedTable.getSelectionModel().addListSelectionListener(this);
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
					infoPane.setText(character.getInfoFactory().getHTMLInfo((RaceFacade) obj));
				}
			}
		}

	}

	private static class RaceLabelHandler implements ReferenceListener<RaceFacade>
	{

		private JLabel label;
		private ReferenceFacade<RaceFacade> ref;

		public RaceLabelHandler(CharacterFacade character, JLabel label)
		{
			ref = character.getRaceRef();
			this.label = label;
		}

		public void install()
		{
			if (ref.getReference() != null)
			{
				label.setText(ref.getReference().toString());
			}
			ref.addReferenceListener(this);
		}

		public void uninstall()
		{
			ref.removeReferenceListener(this);
		}

		@Override
		public void referenceChanged(ReferenceEvent<RaceFacade> e)
		{
			label.setText(e.getNewReference().toString());
		}

	}

	private class SelectRaceAction extends AbstractAction
	{

		private final CharacterFacade character;

		public SelectRaceAction(CharacterFacade character)
		{
			super(LanguageBundle.getString("in_irSelectRace"));
			this.character = character;
			putValue(SMALL_ICON, Icons.Forward16.getImageIcon());
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			Object obj = raceTable.getSelectedObject();
			if(obj instanceof RaceFacade)
			{
				character.setRace((RaceFacade) obj);
			}
		}

		public void install()
		{
			raceTable.addActionListener(this);
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
			super(LanguageBundle.getString("in_irUnselectRace"));
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
		}

		public void uninstall()
		{
			selectedTable.removeActionListener(this);
		}

	}

	private static class RaceTreeViewModel implements TreeViewModel<RaceFacade>, DataView<RaceFacade>
	{

		private static final DefaultListFacade<? extends TreeView<RaceFacade>> treeViews =
				new DefaultListFacade<TreeView<RaceFacade>>(Arrays.asList(RaceTreeView.values()));
		private final List<DefaultDataViewColumn> columns;
		private final CharacterFacade character;
		private final InfoFactory infoFactory;
		private final boolean isAvailModel;

		public RaceTreeViewModel(CharacterFacade character, boolean isAvailModel)
		{
			this.character = character;
			this.infoFactory = character.getInfoFactory();
			this.isAvailModel = isAvailModel;
			if (isAvailModel)
			{
				columns =
						Arrays.asList(new DefaultDataViewColumn("in_irTableStat", String.class, true),
									  new DefaultDataViewColumn("in_preReqs", String.class),
									  new DefaultDataViewColumn("in_size", String.class, true),
									  new DefaultDataViewColumn("in_movement", String.class, true),
									  new DefaultDataViewColumn("in_vision", String.class),
									  new DefaultDataViewColumn("in_favoredClass", String.class, true),
									  new DefaultDataViewColumn("in_lvlAdj", String.class, true));
			}
			else
			{
				columns = Arrays.asList(new DefaultDataViewColumn("in_irTableStat", String.class, false),
										new DefaultDataViewColumn("in_preReqs", String.class, false),
										new DefaultDataViewColumn("in_size", String.class, false),
										new DefaultDataViewColumn("in_movement", String.class, false),
										new DefaultDataViewColumn("in_vision", String.class, false),
										new DefaultDataViewColumn("in_favoredClass", String.class, false),
										new DefaultDataViewColumn("in_lvlAdj", String.class, false));
			}
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
			return this;
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

		@Override
		public List<?> getData(RaceFacade obj)
		{
			return Arrays.asList(infoFactory.getStatAdjustments(obj),
								 infoFactory.getPreReqHTML(obj),
								 obj.getSize(),
								 obj.getMovement(),
								 infoFactory.getVision(obj),
								 infoFactory.getFavoredClass(obj),
								 infoFactory.getLevelAdjustment(obj));
		}

		@Override
		public List<? extends DataViewColumn> getDataColumns()
		{
			return columns;
		}

	}

	private enum RaceTreeView implements TreeView<RaceFacade>
	{

		NAME(LanguageBundle.getString("in_nameLabel")),
		TYPE_NAME(LanguageBundle.getString("in_typeName")),
		RACETYPE_NAME(LanguageBundle.getString("in_racetypeName")),
		SOURCE_NAME(LanguageBundle.getString("in_sourceName"));
		private String name;

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
					return Collections.singletonList(new TreeViewPath<RaceFacade>(pobj));
				case TYPE_NAME:
					return Collections.singletonList(new TreeViewPath<RaceFacade>(pobj, 
							pobj.getType()));
				case RACETYPE_NAME:
					return Collections.singletonList(new TreeViewPath<RaceFacade>(pobj, 
							pobj.getRaceType()));
				case SOURCE_NAME:
					return Collections.singletonList(new TreeViewPath<RaceFacade>(pobj,
							pobj.getSourceForNodeDisplay()));
				default:
					throw new InternalError();
			}
		}

	}

}
