/*
 * DomainInfoTab.java
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
 * Created on Aug 8, 2010, 4:29:55 PM
 */
package pcgen.gui2.tabs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
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
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;

import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.DeityFacade;
import pcgen.core.facade.DomainFacade;
import pcgen.core.facade.InfoFacade;
import pcgen.core.facade.InfoFactory;
import pcgen.core.facade.ReferenceFacade;
import pcgen.core.facade.event.ListEvent;
import pcgen.core.facade.event.ListListener;
import pcgen.core.facade.event.ReferenceEvent;
import pcgen.core.facade.event.ReferenceListener;
import pcgen.core.facade.util.DefaultListFacade;
import pcgen.core.facade.util.ListFacade;
import pcgen.core.facade.util.ListFacades;
import pcgen.gui2.UIPropertyContext;
import pcgen.gui2.filter.DisplayableFilter;
import pcgen.gui2.filter.FilterBar;
import pcgen.gui2.filter.FilterHandler;
import pcgen.gui2.filter.FilteredListFacadeTableModel;
import pcgen.gui2.filter.FilteredTreeViewTable;
import pcgen.gui2.filter.SearchFilterPanel;
import pcgen.gui2.tabs.models.QualifiedTreeCellRenderer;
import pcgen.gui2.tools.FlippingSplitPane;
import pcgen.gui2.tools.InfoPane;
import pcgen.gui2.util.JDynamicTable;
import pcgen.gui2.util.table.TableUtils;
import pcgen.gui2.util.treeview.DataView;
import pcgen.gui2.util.treeview.DataViewColumn;
import pcgen.gui2.util.treeview.DefaultDataViewColumn;
import pcgen.gui2.util.treeview.TreeView;
import pcgen.gui2.util.treeview.TreeViewModel;
import pcgen.gui2.util.treeview.TreeViewPath;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class DomainInfoTab extends FlippingSplitPane implements CharacterInfoTab, TodoHandler
{

	private final FilteredTreeViewTable deityTable;
	private final JDynamicTable domainTable;
	private final JTable domainRowHeaderTable;
	private final JLabel selectedDeity;
	private final JButton selectDeity;
	private final JLabel selectedDomain;
	private final InfoPane deityInfo;
	private final InfoPane domainInfo;
	private DisplayableFilter domainFilter;
	private static final Object COLUMN_ID = new Object();

	public DomainInfoTab()
	{
		this.deityTable = new FilteredTreeViewTable();
		this.domainTable = new JDynamicTable();
		this.domainRowHeaderTable = TableUtils.createDefaultTable();
		this.selectedDeity = new JLabel();
		this.selectDeity = new JButton();
		this.selectedDomain = new JLabel();
		this.deityInfo = new InfoPane("Deity Info");
		this.domainInfo = new InfoPane("Domain Info");
		initComponents();
	}

	private void initComponents()
	{
		setOrientation(VERTICAL_SPLIT);

		JPanel panel = new JPanel(new BorderLayout());
		FilterBar bar = new FilterBar();
		bar.addDisplayableFilter(new SearchFilterPanel());
		deityTable.setDisplayableFilter(bar);
		panel.add(bar, BorderLayout.NORTH);

		ListSelectionModel selectionModel = deityTable.getSelectionModel();
		selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		panel.add(new JScrollPane(deityTable), BorderLayout.CENTER);

		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalGlue());
		box.add(new JLabel("Deity:"));
		box.add(Box.createHorizontalStrut(5));
		box.add(selectedDeity);
		box.add(Box.createHorizontalStrut(5));
		box.add(selectDeity);
		box.add(Box.createHorizontalGlue());
		panel.add(box, BorderLayout.SOUTH);

		FlippingSplitPane splitPane = new FlippingSplitPane();
		splitPane.setLeftComponent(panel);

		panel = new JPanel(new BorderLayout());
		bar = new FilterBar();
		bar.addDisplayableFilter(new SearchFilterPanel());
		domainFilter = bar;
		panel.add(bar, BorderLayout.NORTH);
		selectionModel = domainTable.getSelectionModel();
		selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPane = TableUtils.createCheckBoxSelectionPane(domainTable, domainRowHeaderTable);
		panel.add(scrollPane, BorderLayout.CENTER);

		box = Box.createHorizontalBox();
		box.add(Box.createHorizontalGlue());
		box.add(new JLabel("Domains Remaining to be Selected:"));
		box.add(Box.createHorizontalStrut(5));
		box.add(selectedDomain);
		box.add(Box.createHorizontalGlue());

		panel.add(box, BorderLayout.SOUTH);

		splitPane.setRightComponent(panel);
		setTopComponent(splitPane);
		splitPane = new FlippingSplitPane();
		splitPane.setLeftComponent(deityInfo);
		splitPane.setRightComponent(domainInfo);
		setBottomComponent(splitPane);
		setResizeWeight(.65);
	}

	public Hashtable<Object, Object> createModels(CharacterFacade character)
	{
		Hashtable<Object, Object> state = new Hashtable<Object, Object>();
		state.put(DeityTreeViewModel.class, new DeityTreeViewModel(character));
		state.put(DomainTableHandler.class, new DomainTableHandler(character));
		state.put(SelectDeityAction.class, new SelectDeityAction(character));
		state.put(DeityLabelHandler.class, new DeityLabelHandler(character, selectedDeity));
		state.put(DomainLabelHandler.class, new DomainLabelHandler(character, selectedDomain));
		state.put(DeityInfoHandler.class, new DeityInfoHandler(character));
		state.put(DomainInfoHandler.class, new DomainInfoHandler(character));
		state.put(DomainRenderer.class, new DomainRenderer(character));
		state.put(QualifiedTreeCellRenderer.class, new QualifiedTreeCellRenderer(character));
		return state;
	}

	public void restoreModels(Hashtable<?, ?> state)
	{
		((DomainLabelHandler) state.get(DomainLabelHandler.class)).install();
		((DeityLabelHandler) state.get(DeityLabelHandler.class)).install();
		((DomainTableHandler) state.get(DomainTableHandler.class)).install();
		((DomainInfoHandler) state.get(DomainInfoHandler.class)).install();
		((DeityInfoHandler) state.get(DeityInfoHandler.class)).install();
		((DomainRenderer) state.get(DomainRenderer.class)).install();
		((SelectDeityAction) state.get(SelectDeityAction.class)).install();

		deityTable.setTreeViewModel((DeityTreeViewModel) state.get(DeityTreeViewModel.class));
		deityTable.setTreeCellRenderer((QualifiedTreeCellRenderer) state.get(QualifiedTreeCellRenderer.class));
		selectDeity.setAction((SelectDeityAction) state.get(SelectDeityAction.class));
	}

	public void storeModels(Hashtable<Object, Object> state)
	{
		((DomainLabelHandler) state.get(DomainLabelHandler.class)).uninstall();
		((DeityLabelHandler) state.get(DeityLabelHandler.class)).uninstall();
		((DomainTableHandler) state.get(DomainTableHandler.class)).uninstall();
		((DomainInfoHandler) state.get(DomainInfoHandler.class)).uninstall();
		((DeityInfoHandler) state.get(DeityInfoHandler.class)).uninstall();
		((SelectDeityAction) state.get(SelectDeityAction.class)).uninstall();
	}

	public TabTitle getTabTitle()
	{
		return new TabTitle("in_domains");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void adviseTodo(String fieldName)
	{
		if ("Domains".equals(fieldName))
		{
			if (domainTable.getRowCount() > 0)
			{
				domainTable.requestFocusInWindow();
				domainTable.getSelectionModel().setSelectionInterval(0, 0);
				deityTable.getSelectionModel().clearSelection();
			}
			else if (deityTable.getRowCount() > 0)
			{
				deityTable.requestFocusInWindow();
				deityTable.getSelectionModel().setSelectionInterval(0, 0);
			}
		}
	}

	private class DomainRenderer extends DefaultTableCellRenderer
	{

		private CharacterFacade character;

		public DomainRenderer(CharacterFacade character)
		{
			this.character = character;
		}

		public void install()
		{
			domainTable.setDefaultRenderer(Object.class, this);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (value instanceof DomainFacade && !character.isQualifiedFor((DomainFacade) value))
			{
				setForeground(UIPropertyContext.getNotQualifiedColor());
			}
			else
			{
				setForeground(UIPropertyContext.getQualifiedColor());
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

	private class DeityInfoHandler implements ListSelectionListener
	{

		private CharacterFacade character;

		public DeityInfoHandler(CharacterFacade character)
		{
			this.character = character;
		}

		public void install()
		{
			deityTable.getSelectionModel().addListSelectionListener(this);
		}

		public void uninstall()
		{
			deityTable.getSelectionModel().removeListSelectionListener(this);
		}

		public void valueChanged(ListSelectionEvent e)
		{
			if (!e.getValueIsAdjusting())
			{
				int selectedRow = deityTable.getSelectedRow();
				if (selectedRow != -1)
				{
					Object obj = deityTable.getModel().getValueAt(selectedRow, 0);
					if (obj instanceof DeityFacade)
					{
						deityInfo.setText(character.getInfoFactory().getHTMLInfo((DeityFacade) obj));
					}
				}
			}
		}

	}

	private class DomainInfoHandler implements ListSelectionListener
	{

		private CharacterFacade character;

		public DomainInfoHandler(CharacterFacade character)
		{
			this.character = character;
		}

		public void install()
		{
			domainTable.getSelectionModel().addListSelectionListener(this);
		}

		public void uninstall()
		{
			domainTable.getSelectionModel().removeListSelectionListener(this);
		}

		public void valueChanged(ListSelectionEvent e)
		{
			if (!e.getValueIsAdjusting())
			{
				int selectedRow = domainTable.getSelectedRow();
				DomainFacade domain = null;
				if (selectedRow != -1)
				{
					domain = (DomainFacade) domainTable.getModel().getValueAt(selectedRow, 0);
				}
				if (domain != null)
				{
					domainInfo.setText(character.getInfoFactory().getHTMLInfo(domain));
				}
			}
		}

	}

	private class SelectDeityAction extends AbstractAction
	{

		private CharacterFacade character;

		public SelectDeityAction(CharacterFacade character)
		{
			super("Select");
			this.character = character;
		}

		public void actionPerformed(ActionEvent e)
		{
			int selectedRow = deityTable.getSelectedRow();
			if (selectedRow != -1)
			{
				DeityFacade deity = (DeityFacade) deityTable.getModel().getValueAt(selectedRow, 0);
				character.setDeity(deity);
			}
		}
		
		public void install()
		{
			deityTable.addActionListener(this);
		}
		
		public void uninstall()
		{
			deityTable.removeActionListener(this);
		}

	}

	private class DomainTableHandler implements FilterHandler
	{

		private DomainTableModel tableModel;

		public DomainTableHandler(CharacterFacade character)
		{
			tableModel = new DomainTableModel(character);
		}

		public void install()
		{
			domainFilter.setFilterHandler(this);
			tableModel.setFilter(domainFilter);
			domainTable.setModel(tableModel);
			domainRowHeaderTable.setModel(tableModel);
		}

		public void uninstall()
		{
			tableModel.setFilter(null);
		}

		public void refilter()
		{
			tableModel.refilter();
		}

		public void setSearchEnabled(boolean enable)
		{
		}

	}

	private static class DomainLabelHandler implements ReferenceListener<Integer>
	{

		private JLabel label;
		private ReferenceFacade<Integer> ref;

		public DomainLabelHandler(CharacterFacade character, JLabel label)
		{
			ref = character.getRemainingDomainSelectionsRef();
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

		public void referenceChanged(ReferenceEvent<Integer> e)
		{
			label.setText(e.getNewReference().toString());
		}

	}

	private static class DeityLabelHandler implements ReferenceListener<DeityFacade>
	{

		private JLabel label;
		private ReferenceFacade<DeityFacade> ref;

		public DeityLabelHandler(CharacterFacade character, JLabel label)
		{
			ref = character.getDeityRef();
			this.label = label;
		}

		public void install()
		{
			label.setFont(label.getFont().deriveFont(Font.PLAIN));
			if (ref.getReference() != null)
			{
				label.setText(ref.getReference().toString());
				if (ref.getReference().isNamePI())
				{
					label.setFont(label.getFont().deriveFont(Font.BOLD + Font.ITALIC));
				}
			}
			else
			{
				label.setText("");
			}
			ref.addReferenceListener(this);
		}

		public void uninstall()
		{
			ref.removeReferenceListener(this);
		}

		public void referenceChanged(ReferenceEvent<DeityFacade> e)
		{
			label.setText(e.getNewReference().toString());
		}

	}

	private static class DomainTableModel extends FilteredListFacadeTableModel<DomainFacade>
	{

		private final ListListener<DomainFacade> listListener = new ListListener<DomainFacade>()
		{

			public void elementAdded(ListEvent<DomainFacade> e)
			{
				int index = ListFacades.wrap(sortedList).indexOf(e.getElement());
				DomainTableModel.this.fireTableCellUpdated(index, -1);
			}

			public void elementRemoved(ListEvent<DomainFacade> e)
			{
				int index = ListFacades.wrap(sortedList).indexOf(e.getElement());
				DomainTableModel.this.fireTableCellUpdated(index, -1);
			}

			public void elementsChanged(ListEvent<DomainFacade> e)
			{
				DomainTableModel.this.fireTableRowsUpdated(0, sortedList.getSize() - 1);
			}

		};

		public DomainTableModel(CharacterFacade character)
		{
			super(character);
			setDelegate(character.getAvailableDomains());
			character.getDomains().addListListener(listListener);
		}

		@Override
		public Class<?> getColumnClass(int columnIndex)
		{
			if (columnIndex == -1)
			{
				return Boolean.class;
			}
			return super.getColumnClass(columnIndex);
		}

		@Override
		protected Object getValueAt(DomainFacade element, int column)
		{
			switch (column)
			{
				case -1:
					return character.getDomains().containsElement(element);
				case 0:
					return element;
				case 1:
					return element.getSource();
				default:
					return null;
			}
		}

		public int getColumnCount()
		{
			return 2;
		}

		@Override
		public String getColumnName(int column)
		{
			switch (column)
			{
				case 0:
					return "Domains";
				case 1:
					return "Source";
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			if (columnIndex >= 0)
			{
				return false;
			}
			if (character.getRemainingDomainSelectionsRef().getReference() > 0)
			{
				return true;
			}
			DomainFacade domain = sortedList.getElementAt(rowIndex);
			return character.getDomains().containsElement(domain);
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex)
		{
			DomainFacade domain = sortedList.getElementAt(rowIndex);
			Boolean bool = (Boolean) aValue;
			if (bool)
			{
				character.addDomain(domain);
			}
			else
			{
				character.removeDomain(domain);
			}
		}

	}

	private static class DeityTreeViewModel implements TreeViewModel<DeityFacade>, DataView<DeityFacade>
	{

		private static final ListFacade<TreeView<DeityFacade>> views =
				new DefaultListFacade<TreeView<DeityFacade>>(Arrays.asList(DeityTreeView.values()));
		private final List<DefaultDataViewColumn> columns = Arrays.asList(new DefaultDataViewColumn("in_alignLabel", Object.class), //$NON-NLS-1$
																		  new DefaultDataViewColumn("in_domains", String.class), //$NON-NLS-1$
																		  new DefaultDataViewColumn("in_sourceLabel", String.class)); //$NON-NLS-1$
		private final CharacterFacade character;
		private InfoFactory infoFactory;

		public DeityTreeViewModel(CharacterFacade character)
		{
			this.character = character;
			this.infoFactory = character.getInfoFactory();
		}

		public ListFacade<? extends TreeView<DeityFacade>> getTreeViews()
		{
			return views;
		}

		public int getDefaultTreeViewIndex()
		{
			return 0;
		}

		public DataView<DeityFacade> getDataView()
		{
			return this;
		}

		public ListFacade<DeityFacade> getDataModel()
		{
			return character.getDataSet().getDeities();
		}

		public List<?> getData(DeityFacade obj)
		{
			return Arrays.asList(obj.getAlignment(),
				infoFactory.getDomains(obj), obj.getSource());
		}

		public List<? extends DataViewColumn> getDataColumns()
		{
			return columns;
		}

	}

	//TODO: pantheon view
	private enum DeityTreeView implements TreeView<DeityFacade>
	{

		NAME("Name"),
		ALIGNMENT_NAME("Alignment/Name"),
		DOMAIN_NAME("Domain/Name"),
		SOURCE_NAME("Source/Name");
		private String name;

		private DeityTreeView(String name)
		{
			this.name = name;
		}

		public String getViewName()
		{
			return name;
		}

		public List<TreeViewPath<DeityFacade>> getPaths(DeityFacade pobj)
		{
			switch (this)
			{
				case NAME:
					return Collections.singletonList(new TreeViewPath<DeityFacade>(pobj));
				case DOMAIN_NAME:
					List<TreeViewPath<DeityFacade>> paths = new ArrayList<TreeViewPath<DeityFacade>>();
					for (String domain : pobj.getDomainNames())
					{
						paths.add(new TreeViewPath(pobj, domain));
					}
					return paths;
				case ALIGNMENT_NAME:
					return Collections.singletonList(new TreeViewPath<DeityFacade>(pobj, pobj.getAlignment()));
				case SOURCE_NAME:
					return Collections.singletonList(new TreeViewPath<DeityFacade>(pobj, pobj.getSource()));
				default:
					throw new InternalError();
			}


		}

	}

}
