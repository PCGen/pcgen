/*
 * TemplateInfoTab.java
 * Copyright 2010 (C) Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Sep 13, 2010, 6:22:26 PM
 */
package pcgen.gui2.tabs;

import java.awt.BorderLayout;
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
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.InfoFactory;
import pcgen.core.facade.TemplateFacade;
import pcgen.core.facade.event.ListEvent;
import pcgen.core.facade.event.ListListener;
import pcgen.core.facade.util.DefaultListFacade;
import pcgen.core.facade.util.ListFacade;
import pcgen.gui2.filter.Filter;
import pcgen.gui2.filter.FilterBar;
import pcgen.gui2.filter.FilteredListFacade;
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
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class TemplateInfoTab extends FlippingSplitPane implements CharacterInfoTab
{

	private final TabTitle tabTitle = new TabTitle("in_Templates");
	private final FilteredTreeViewTable<CharacterFacade, TemplateFacade> availableTable;
	private final FilteredTreeViewTable<CharacterFacade, TemplateFacade> selectedTable;
	private final JButton addButton;
	private final JButton removeButton;
	private final InfoPane infoPane;

	public TemplateInfoTab()
	{
		this.availableTable = new FilteredTreeViewTable<CharacterFacade, TemplateFacade>();
		this.selectedTable = new FilteredTreeViewTable<CharacterFacade, TemplateFacade>();
		this.addButton = new JButton();
		this.removeButton = new JButton();
		this.infoPane = new InfoPane("Template Info");
		initComponents();
	}

	private void initComponents()
	{
		FlippingSplitPane topPane = new FlippingSplitPane();
		setTopComponent(topPane);
		setOrientation(VERTICAL_SPLIT);

		JPanel availPanel = new JPanel(new BorderLayout());
		FilterBar<CharacterFacade, TemplateFacade> bar = new FilterBar<CharacterFacade, TemplateFacade>();
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
		FilterBar<CharacterFacade, TemplateFacade> filterBar = new FilterBar<CharacterFacade, TemplateFacade>();
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
		TemplateRenderer
	}

	@Override
	public Hashtable<Object, Object> createModels(CharacterFacade character)
	{
		Hashtable<Object, Object> state = new Hashtable<Object, Object>();
		state.put(Models.AvailableModel, new TemplateTreeViewModel(character, true));
		state.put(Models.SelectedModel, new TemplateTreeViewModel(character, false));
		state.put(Models.InfoHandler, new InfoHandler(character));
		state.put(Models.AddAction, new AddAction(character));
		state.put(Models.RemoveAction, new RemoveAction(character));
		state.put(Models.TemplateRenderer, new QualifiedTreeCellRenderer(character));
		return state;
	}

	@Override
	public void restoreModels(Hashtable<?, ?> state)
	{
		availableTable.setTreeViewModel((TemplateTreeViewModel) state.get(Models.AvailableModel));
		selectedTable.setTreeViewModel((TemplateTreeViewModel) state.get(Models.SelectedModel));
		((InfoHandler) state.get(Models.InfoHandler)).install();
		((AddAction) state.get(Models.AddAction)).install();
		((RemoveAction) state.get(Models.RemoveAction)).install();

		addButton.setAction((AddAction) state.get(Models.AddAction));
		removeButton.setAction((RemoveAction) state.get(Models.RemoveAction));
		availableTable.setTreeCellRenderer((QualifiedTreeCellRenderer) state.get(Models.TemplateRenderer));
		selectedTable.setTreeCellRenderer((QualifiedTreeCellRenderer) state.get(Models.TemplateRenderer));
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
				if (obj instanceof TemplateFacade)
				{
					text = character.getInfoFactory().getHTMLInfo((TemplateFacade) obj);
					infoPane.setText(text);
				}
			}
		}

	}

	private class AddAction extends AbstractAction
	{

		private CharacterFacade character;

		public AddAction(CharacterFacade character)
		{
			super(LanguageBundle.getString("in_irAddTemplate"));
			this.character = character;
			putValue(SMALL_ICON, Icons.Forward16.getImageIcon());
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			List<Object> data = availableTable.getSelectedData();
			for (Object object : data)
			{
				if (object instanceof TemplateFacade)
				{
					character.addTemplate((TemplateFacade) object);
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

		private CharacterFacade character;

		public RemoveAction(CharacterFacade character)
		{
			super(LanguageBundle.getString("in_irRemoveTemplate"));
			this.character = character;
			putValue(SMALL_ICON, Icons.Back16.getImageIcon());
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			List<Object> data = selectedTable.getSelectedData();
			for (Object object : data)
			{
				if (object instanceof TemplateFacade)
				{
					character.removeTemplate((TemplateFacade) object);
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

	private static class TemplateTreeViewModel
			implements TreeViewModel<TemplateFacade>, DataView<TemplateFacade>,
			Filter<CharacterFacade, TemplateFacade>, ListListener<TemplateFacade>
	{

		private static final DefaultListFacade<? extends TreeView<TemplateFacade>> treeViews =
				new DefaultListFacade<TreeView<TemplateFacade>>(Arrays.asList(TemplateTreeView.values()));
		private final List<DefaultDataViewColumn> columns;
		private final CharacterFacade character;
		private final InfoFactory infoFactory;
		private final boolean isAvailModel;
		private FilteredListFacade<CharacterFacade, TemplateFacade> templates;

		public TemplateTreeViewModel(CharacterFacade character, boolean isAvailModel)
		{
			this.character = character;
			this.infoFactory = character.getInfoFactory();
			this.isAvailModel = isAvailModel;
			if (isAvailModel)
			{
				templates = new FilteredListFacade<CharacterFacade, TemplateFacade>();
				templates.setContext(character);
				templates.setFilter(this);
				templates.setDelegate(character.getDataSet().getTemplates());
				character.getTemplates().addListListener(this);
				columns = Arrays.asList(new DefaultDataViewColumn("Lvl Adj", String.class, true),
										new DefaultDataViewColumn("Modifier", String.class, true),
										new DefaultDataViewColumn("Pre-Reqs", String.class, true),
										new DefaultDataViewColumn("Source", String.class, false));
			}
			else
			{
				templates = null;
				columns = Arrays.asList(new DefaultDataViewColumn("Lvl Adj", String.class, false),
										new DefaultDataViewColumn("Modifier", String.class, false),
										new DefaultDataViewColumn("Pre-Reqs", String.class, false),
										new DefaultDataViewColumn("Source", String.class, false));
			}
		}

		@Override
		public ListFacade<? extends TreeView<TemplateFacade>> getTreeViews()
		{
			return treeViews;
		}

		@Override
		public int getDefaultTreeViewIndex()
		{
			return 0;
		}

		@Override
		public DataView<TemplateFacade> getDataView()
		{
			return this;
		}

		@Override
		public ListFacade<TemplateFacade> getDataModel()
		{
			if (isAvailModel)
			{
				return templates;
			}
			else
			{
				return character.getTemplates();
			}
		}

		@Override
		public List<?> getData(TemplateFacade obj)
		{
			return Arrays.asList(infoFactory.getLevelAdjustment(obj),
								 infoFactory.getModifier(obj),
								 infoFactory.getPreReqHTML(obj),
								 obj.getSource());
		}

		@Override
		public List<? extends DataViewColumn> getDataColumns()
		{
			return columns;
		}

		@Override
		public void elementAdded(ListEvent<TemplateFacade> e)
		{
			templates.refilter();
		}

		@Override
		public void elementRemoved(ListEvent<TemplateFacade> e)
		{
			templates.refilter();
		}

		@Override
		public void elementsChanged(ListEvent<TemplateFacade> e)
		{
			templates.refilter();
		}

		@Override
		public void elementModified(ListEvent<TemplateFacade> e)
		{
			templates.refilter();
		}

		@Override
		public boolean accept(CharacterFacade context, TemplateFacade element)
		{
			return !context.getTemplates().containsElement(element);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getPrefsKey()
		{
			return isAvailModel ? "TemplateTreeAvail" : "TemplateTreeSelected";  //$NON-NLS-1$//$NON-NLS-2$
		}

	}

	private enum TemplateTreeView implements TreeView<TemplateFacade>
	{

		NAME("Name"),
		TYPE_NAME("Type/Name"),
		SOURCE_NAME("Source/Name");
		private String name;

		private TemplateTreeView(String name)
		{
			this.name = name;
		}

		@Override
		public String getViewName()
		{
			return name;
		}

		@Override
		public List<TreeViewPath<TemplateFacade>> getPaths(TemplateFacade pobj)
		{
			switch (this)
			{
				case NAME:
					return Collections.singletonList(new TreeViewPath<TemplateFacade>(pobj));
				case TYPE_NAME:
					return Collections.singletonList(new TreeViewPath<TemplateFacade>(pobj, 
							pobj.getType()));
				case SOURCE_NAME:
					return Collections.singletonList(new TreeViewPath<TemplateFacade>(pobj, 
							pobj.getSourceForNodeDisplay()));
				default:
					throw new InternalError();
			}
		}

	}

}
