/*
 * TempBonusInfoTab.java
 * Copyright James Dempsey, 2012
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
 * Created on 08/06/2012 7:42:35 PM
 *
 * $Id$
 */
package pcgen.gui2.tabs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.InfoFacade;
import pcgen.core.facade.InfoFactory;
import pcgen.core.facade.TempBonusFacade;
import pcgen.core.facade.event.ListEvent;
import pcgen.core.facade.event.ListListener;
import pcgen.core.facade.util.DefaultListFacade;
import pcgen.core.facade.util.ListFacade;
import pcgen.gui2.UIPropertyContext;
import pcgen.gui2.filter.Filter;
import pcgen.gui2.filter.FilterBar;
import pcgen.gui2.filter.FilteredListFacade;
import pcgen.gui2.filter.FilteredTreeViewTable;
import pcgen.gui2.filter.SearchFilterPanel;
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
 * The Class <code>TempBonusInfoTab</code> allows the user to select which 
 * temporary bonus should be applied to their character.
 *
 * <br/>
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class TempBonusInfoTab extends FlippingSplitPane implements CharacterInfoTab
{

	/** Version for serialisation.  */
	private static final long serialVersionUID = 4521237435574462482L;
	
	private final TabTitle tabTitle = new TabTitle("in_InfoTempMod"); //$NON-NLS-1$
	private final FilteredTreeViewTable<CharacterFacade, TempBonusFacade> availableTable;
	private final FilteredTreeViewTable<CharacterFacade, TempBonusFacade> selectedTable;
	private final JButton addButton;
	private final JButton removeButton;
	private final InfoPane infoPane;

	/**
	 * Create a new instance of TemporaryBonusInfoTab.
	 */
	public TempBonusInfoTab()
	{
		this.availableTable = new FilteredTreeViewTable<CharacterFacade, TempBonusFacade>();
		this.selectedTable = new FilteredTreeViewTable<CharacterFacade, TempBonusFacade>();
		this.addButton = new JButton();
		this.removeButton = new JButton();
		this.infoPane = new InfoPane(LanguageBundle.getString("in_InfoTempMod")); //$NON-NLS-1$
		initComponents();
	}

	private void initComponents()
	{
		FlippingSplitPane topPane = new FlippingSplitPane();
		setTopComponent(topPane);
		setOrientation(VERTICAL_SPLIT);

		JPanel availPanel = new JPanel(new BorderLayout());
		FilterBar<CharacterFacade, TempBonusFacade> bar = new FilterBar<CharacterFacade, TempBonusFacade>();
		bar.addDisplayableFilter(new SearchFilterPanel());
		availPanel.add(bar, BorderLayout.NORTH);

		availableTable.setDisplayableFilter(bar);
		availableTable.setSortingPriority(Collections.singletonList(new SortingPriority(0, SortMode.ASCENDING)));
		availableTable.sortModel();
		availPanel.add(new JScrollPane(availableTable), BorderLayout.CENTER);

		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalGlue());
		addButton.setHorizontalTextPosition(SwingConstants.LEADING);
		box.add(addButton);
		box.add(Box.createHorizontalStrut(5));
		box.setBorder(new EmptyBorder(0,  0, 5, 0));
		availPanel.add(box, BorderLayout.SOUTH);

		topPane.setLeftComponent(availPanel);

		JPanel selPanel = new JPanel(new BorderLayout());
		FilterBar<CharacterFacade, TempBonusFacade> filterBar = new FilterBar<CharacterFacade, TempBonusFacade>();
		filterBar.addDisplayableFilter(new SearchFilterPanel());

		selectedTable.setDisplayableFilter(filterBar);
		selectedTable.setSortingPriority(Collections.singletonList(new SortingPriority(0, SortMode.ASCENDING)));
		selectedTable.sortModel();
		selPanel.add(new JScrollPane(selectedTable), BorderLayout.CENTER);

		box = Box.createHorizontalBox();
		box.add(Box.createHorizontalStrut(5));
		box.add(removeButton);
		box.add(Box.createHorizontalGlue());
		box.setBorder(new EmptyBorder(0,  0, 5, 0));
		selPanel.add(box, BorderLayout.SOUTH);

		topPane.setRightComponent(selPanel);
		setBottomComponent(infoPane);
		setResizeWeight(.75);
	}

	private enum Models
	{

		AvailableModel,
		SelectedModel,
		InfoHandler,
		AddAction,
		RemoveAction,
		TempBonusRenderer
	}

	@Override
	public Hashtable<Object, Object> createModels(CharacterFacade character)
	{
		Hashtable<Object, Object> state = new Hashtable<Object, Object>();
		state.put(Models.AvailableModel, new TempBonusTreeViewModel(character, true));
		state.put(Models.SelectedModel, new TempBonusTreeViewModel(character, false));
		state.put(Models.InfoHandler, new InfoHandler(character));
		state.put(Models.AddAction, new AddAction(character));
		state.put(Models.RemoveAction, new RemoveAction(character));
		state.put(Models.TempBonusRenderer, new TempBonusRenderer(character));
		return state;
	}

	@Override
	public void restoreModels(Hashtable<?, ?> state)
	{
		((TempBonusTreeViewModel) state.get(Models.AvailableModel)).install();
		availableTable.setTreeViewModel((TempBonusTreeViewModel) state.get(Models.AvailableModel));
		((TempBonusTreeViewModel) state.get(Models.SelectedModel)).install();
		selectedTable.setTreeViewModel((TempBonusTreeViewModel) state.get(Models.SelectedModel));
		((InfoHandler) state.get(Models.InfoHandler)).install();
		((AddAction) state.get(Models.AddAction)).install();
		((RemoveAction) state.get(Models.RemoveAction)).install();

		addButton.setAction((AddAction) state.get(Models.AddAction));
		removeButton.setAction((RemoveAction) state.get(Models.RemoveAction));
		availableTable.setTreeCellRenderer((TempBonusRenderer) state.get(Models.TempBonusRenderer));
		selectedTable.setTreeCellRenderer((TempBonusRenderer) state.get(Models.TempBonusRenderer));
	}

	@Override
	public void storeModels(Hashtable<Object, Object> state)
	{
		((InfoHandler) state.get(Models.InfoHandler)).uninstall();
		((AddAction) state.get(Models.AddAction)).uninstall();
		((RemoveAction) state.get(Models.RemoveAction)).uninstall();
	}

	@Override
	public TabTitle getTabTitle()
	{
		return tabTitle;
	}

	private class TempBonusRenderer extends DefaultTreeCellRenderer
	{
		/** Version for serialisation.  */
		private static final long serialVersionUID = -9006249573217208478L;

		private CharacterFacade character;

		public TempBonusRenderer(CharacterFacade character)
		{
			this.character = character;
			setTextNonSelectionColor(UIPropertyContext.getQualifiedColor());
			setClosedIcon(null);
			setLeafIcon(null);
			setOpenIcon(null);
		}

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row, boolean focus)
		{
			Object obj = ((DefaultMutableTreeNode) value).getUserObject();
			if ("".equals(obj)) //$NON-NLS-1$
			{
				obj = LanguageBundle.getString("in_none"); //$NON-NLS-1$
			}
			super.getTreeCellRendererComponent(tree, obj, sel, expanded, leaf, row, focus);
			if (value instanceof TempBonusFacade && !character.isQualifiedFor((TempBonusFacade) value))
			{
				setForeground(UIPropertyContext.getNotQualifiedColor());
			}
			if (value instanceof InfoFacade && ((InfoFacade) value).isNamePI())
			{
				setFont(getFont().deriveFont(Font.BOLD + Font.ITALIC));
			}
			else
			{
				setFont(getFont().deriveFont(Font.PLAIN));
			}
			return this;
		}

	}

	private class InfoHandler implements ListSelectionListener
	{

		private CharacterFacade character;
		private String text;

		public InfoHandler(CharacterFacade character)
		{
			this.character = character;
			this.text = ""; //$NON-NLS-1$
		}

		public void install()
		{
			availableTable.getSelectionModel().addListSelectionListener(this);
			selectedTable.getSelectionModel().addListSelectionListener(this);
			infoPane.setText(text);
		}

		public void uninstall()
		{
			availableTable.getSelectionModel().removeListSelectionListener(this);
			selectedTable.getSelectionModel().removeListSelectionListener(this);
		}

		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			if (!e.getValueIsAdjusting())
			{
				Object obj = null;
				if (e.getSource() == availableTable.getSelectionModel())
				{
					int selectedRow = availableTable.getSelectedRow();
					if (selectedRow != -1)
					{
						obj = availableTable.getModel().getValueAt(selectedRow, 0);
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
				if (obj instanceof TempBonusFacade)
				{
					text = character.getInfoFactory().getHTMLInfo((TempBonusFacade) obj);
					infoPane.setText(text);
				}
			}
		}

	}

	private class AddAction extends AbstractAction
	{
		/** Version for serialisation.  */
		private static final long serialVersionUID = -6640460398947215666L;

		private CharacterFacade character;

		public AddAction(CharacterFacade character)
		{
			super(LanguageBundle.getString("in_itmInitCompAppBonTitle")); //$NON-NLS-1$
			this.character = character;
			putValue(SMALL_ICON, Icons.Forward16.getImageIcon());
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			List<Object> data = availableTable.getSelectedData();
			for (Object object : data)
			{
				if (object instanceof TempBonusFacade)
				{
					character.addTempBonus((TempBonusFacade) object);
					return;
				}
			}
		}
		
		public void install()
		{
			availableTable.addActionListener(this);
		}
		
		public void uninstall()
		{
			availableTable.removeActionListener(this);
		}

	}

	private class RemoveAction extends AbstractAction
	{
		/** Version for serialisation.  */
		private static final long serialVersionUID = 2922387838116495051L;

		private CharacterFacade character;

		public RemoveAction(CharacterFacade character)
		{
			super(LanguageBundle.getString("in_itmInitCompRemoveButTitle")); //$NON-NLS-1$
			this.character = character;
			putValue(SMALL_ICON, Icons.Back16.getImageIcon());
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			List<Object> data = selectedTable.getSelectedData();
			for (Object object : data)
			{
				if (object instanceof TempBonusFacade)
				{
					character.removeTempBonus((TempBonusFacade) object);
					return;
				}
			}
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

	private static class TempBonusTreeViewModel
			implements TreeViewModel<TempBonusFacade>, DataView<TempBonusFacade>,
			Filter<CharacterFacade, TempBonusFacade>, ListListener<TempBonusFacade>
	{

		private static final DefaultListFacade<? extends TreeView<TempBonusFacade>> treeViews =
				new DefaultListFacade<TreeView<TempBonusFacade>>(Arrays.asList(TempBonusTreeView.values()));
		private final List<DefaultDataViewColumn> columns;
		private final CharacterFacade character;
		private final InfoFactory infoFactory;
		private final boolean isAvailModel;
		private FilteredListFacade<CharacterFacade, TempBonusFacade> tempBonuses;

		public TempBonusTreeViewModel(CharacterFacade character, boolean isAvailModel)
		{
			this.character = character;
			this.infoFactory = character.getInfoFactory();
			this.isAvailModel = isAvailModel;
			if (isAvailModel)
			{
				tempBonuses = new FilteredListFacade<CharacterFacade, TempBonusFacade>();
				tempBonuses.setContext(character);
				tempBonuses.setFilter(this);
				tempBonuses.setDelegate(character.getAvailableTempBonuses());
				character.getAvailableTempBonuses().addListListener(this);
				columns = Arrays.asList(new DefaultDataViewColumn("in_itmFrom", String.class, true), //$NON-NLS-1$
										new DefaultDataViewColumn("in_itmTarget", String.class, true), //$NON-NLS-1$
										new DefaultDataViewColumn("in_source", String.class, true)); //$NON-NLS-1$
			}
			else
			{
				tempBonuses = null;
				columns = Arrays.asList(new DefaultDataViewColumn("in_itmFrom", String.class, false), //$NON-NLS-1$
										new DefaultDataViewColumn("in_itmTarget", String.class, true), //$NON-NLS-1$
										new DefaultDataViewColumn("in_source", String.class, false)); //$NON-NLS-1$
			}
		}

		public void install()
		{
			for (TempBonusTreeView tbTreeView : TempBonusTreeView.values())
			{
				tbTreeView.setInfoFactory(infoFactory);
			}
		}
		
		@Override
		public ListFacade<? extends TreeView<TempBonusFacade>> getTreeViews()
		{
			return treeViews;
		}

		@Override
		public int getDefaultTreeViewIndex()
		{
			return isAvailModel ? 1 : 0;
		}

		@Override
		public DataView<TempBonusFacade> getDataView()
		{
			return this;
		}

		@Override
		public ListFacade<TempBonusFacade> getDataModel()
		{
			if (isAvailModel)
			{
				return tempBonuses;
			}
			else
			{
				return character.getTempBonuses();
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public List<?> getData(TempBonusFacade obj)
		{
			return Arrays.asList(obj.getOriginType(),
								 infoFactory.getTempBonusTarget(obj),
								 obj.getSource());
		}

		@Override
		public List<? extends DataViewColumn> getDataColumns()
		{
			return columns;
		}

		@Override
		public void elementAdded(ListEvent<TempBonusFacade> e)
		{
			tempBonuses.refilter();
		}

		@Override
		public void elementRemoved(ListEvent<TempBonusFacade> e)
		{
			tempBonuses.refilter();
		}

		@Override
		public void elementsChanged(ListEvent<TempBonusFacade> e)
		{
			tempBonuses.refilter();
		}

		@Override
		public void elementModified(ListEvent<TempBonusFacade> e)
		{
			tempBonuses.refilter();
		}

		@Override
		public boolean accept(CharacterFacade context, TempBonusFacade element)
		{
			return !context.getTempBonuses().containsElement(element);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getPrefsKey()
		{
			return isAvailModel ? "TempModsTreeAvail" : "TempModsTreeSelected";  //$NON-NLS-1$//$NON-NLS-2$
		}

	}

	private enum TempBonusTreeView implements TreeView<TempBonusFacade>
	{

		NAME("in_nameLabel"), //$NON-NLS-1$
		ORIGIN_NAME("in_itmOriginName"), //$NON-NLS-1$
		SOURCE_NAME("in_sourceName"), //$NON-NLS-1$
		TARGET_NAME("in_itmTargetName"); //$NON-NLS-1$
		
		private final String name;
		private InfoFactory infoFactory;

		private TempBonusTreeView(String name)
		{
			this.name = LanguageBundle.getString(name);
		}

		/**
		 * @param factory The InfoFactory for the character ebing displayed. 
		 */
		public void setInfoFactory(InfoFactory factory)
		{
			this.infoFactory = factory;
		}

		@Override
		public String getViewName()
		{
			return name;
		}

		@Override
		public List<TreeViewPath<TempBonusFacade>> getPaths(TempBonusFacade bonus)
		{
			switch (this)
			{
				case TARGET_NAME:
					if (infoFactory != null)
					{
						return Collections.singletonList(new TreeViewPath<TempBonusFacade>(bonus, 
								infoFactory.getTempBonusTarget(bonus)));
					}
					// No info factory? Treat as a name 
				case NAME:
					return Collections.singletonList(new TreeViewPath<TempBonusFacade>(bonus));
				case ORIGIN_NAME:
					return Collections.singletonList(new TreeViewPath<TempBonusFacade>(bonus, 
							bonus.getOriginType()));
				case SOURCE_NAME:
					return Collections.singletonList(new TreeViewPath<TempBonusFacade>(bonus, 
							bonus.getSourceForNodeDisplay()));
				default:
					throw new InternalError();
			}
		}

	}

}
