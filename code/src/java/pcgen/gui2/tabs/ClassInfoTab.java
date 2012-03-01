/*
 * ClassInfoTab.java
 * Copyright 2008 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Jun 27, 2008, 1:36:26 PM
 */
package pcgen.gui2.tabs;

import java.awt.BorderLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.CharacterLevelFacade;
import pcgen.core.facade.CharacterLevelsFacade;
import pcgen.core.facade.ClassFacade;
import pcgen.core.facade.event.ListEvent;
import pcgen.core.facade.event.ListListener;
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
import pcgen.gui2.util.table.TableUtils;
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
public class ClassInfoTab extends FlippingSplitPane implements CharacterInfoTab
{

	/** The table of available classes */
	private final FilteredTreeViewTable<Object, ClassFacade> availableTable;
	/** The table of the character's classes */
	private final JTable classTable;
	private final JButton addButton;
	private final JButton removeButton;
	private final TabTitle tabTitle;
	private final InfoPane infoPane;
	private AddClassAction addClassAction;
	private RemoveClassAction removeClassAction;
	private ClassFacade selectedClass;
	private int spinnerValue;
	private final JSpinner spinner;

	public ClassInfoTab()
	{
		this.availableTable = new FilteredTreeViewTable<Object, ClassFacade>();
		this.classTable = TableUtils.createDefaultTable();
		this.addButton = new JButton();
		this.removeButton = new JButton();
		this.tabTitle = new TabTitle("in_clClass");
		this.infoPane = new InfoPane(LanguageBundle.getString("in_clInfo"));
		this.spinner = new JSpinner(new SpinnerNumberModel(1, 1, 50, 1));
		initComponents();
	}

	private void initComponents()
	{
		FlippingSplitPane topPane = new FlippingSplitPane();
		setTopComponent(topPane);
		setOrientation(VERTICAL_SPLIT);

		JPanel availPanel = new JPanel(new BorderLayout());
		FilterBar<Object, ClassFacade> bar = new FilterBar<Object, ClassFacade>();
		bar.addDisplayableFilter(new SearchFilterPanel());
		availPanel.add(bar, BorderLayout.NORTH);

		availableTable.setDisplayableFilter(bar);
		availableTable.setSortingPriority(Collections.singletonList(new SortingPriority(0, SortMode.ASCENDING)));
		availableTable.sortModel();
		availPanel.add(new JScrollPane(availableTable), BorderLayout.CENTER);
		{
			Box box = Box.createHorizontalBox();
			box.add(Box.createHorizontalGlue());
			spinner.setMaximumSize(spinner.getPreferredSize());
			box.add(spinner);
			box.add(Box.createHorizontalStrut(5));
			addButton.setHorizontalTextPosition(SwingConstants.LEADING);
			box.add(addButton);
			box.add(Box.createHorizontalStrut(5));
			box.setBorder(new EmptyBorder(0, 0, 5, 0));
			availPanel.add(box, BorderLayout.SOUTH);
		}

		topPane.setLeftComponent(availPanel);

		JPanel selPanel = new JPanel(new BorderLayout());

		JScrollPane tablePane = new JScrollPane(classTable);
		selPanel.add(tablePane, BorderLayout.CENTER);
		{
			Box box = Box.createHorizontalBox();
			box.add(Box.createHorizontalStrut(5));
			box.add(removeButton);
			box.add(Box.createHorizontalGlue());
			box.setBorder(new EmptyBorder(0, 0, 5, 0));
			selPanel.add(box, BorderLayout.SOUTH);
		}
		{
			TransferHandler handler = new ClassTransferHandler();
			classTable.setDragEnabled(true);
			classTable.setTransferHandler(handler);

			availableTable.setDragEnabled(true);
			availableTable.setTransferHandler(handler);
		}
		initListeners();

		topPane.setRightComponent(selPanel);
		setBottomComponent(infoPane);
		setResizeWeight(.75);
	}

	private void initListeners()
	{
		spinner.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e)
			{
				spinnerValue = (Integer) spinner.getValue();
			}

		});
		spinnerValue = (Integer) spinner.getValue();

		ListSelectionModel selectionModel = classTable.getSelectionModel();
		selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		selectionModel.addListSelectionListener(new ListSelectionListener()
		{

			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				ListSelectionModel selModel =
						(ListSelectionModel) e.getSource();
				int index = selModel.getMinSelectionIndex();
				if (index != -1)
				{
					if (!e.getValueIsAdjusting())
					{
						TableModel model = classTable.getModel();
						setSelectedClass((ClassFacade) model.getValueAt(index,
																		1));
					}
					availableTable.getSelectionModel().clearSelection();
				}
			}

		});

		selectionModel = availableTable.getSelectionModel();
		selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		selectionModel.addListSelectionListener(new ListSelectionListener()
		{

			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				ListSelectionModel selModel =
						(ListSelectionModel) e.getSource();
				if (!selModel.isSelectionEmpty())
				{
					if (!e.getValueIsAdjusting())
					{
						List<Object> data = availableTable.getSelectedData();
						ClassFacade clazz = null;
						if (!data.isEmpty()
								&& data.get(0) instanceof ClassFacade)
						{
							clazz = (ClassFacade) data.get(0);
						}
						setSelectedClass(clazz);
					}
					classTable.getSelectionModel().clearSelection();
				}
			}

		});
	}

	private void setSelectedClass(ClassFacade selectedClass)
	{
		this.selectedClass = selectedClass;
		if (selectedClass != null)
		{
			addClassAction.setEnabled(true);
			removeClassAction.setEnabled(true);
		}
	}

	@Override
	public Hashtable<Object, Object> createModels(CharacterFacade character)
	{
		Hashtable<Object, Object> state = new Hashtable<Object, Object>();
		state.put(ClassTreeViewModel.class, new ClassTreeViewModel(character));
		state.put(ClassTableModel.class, new ClassTableModel(character));
		state.put(AddClassAction.class, new AddClassAction(character));
		state.put(RemoveClassAction.class, new RemoveClassAction(character));
		state.put(InfoHandler.class, new InfoHandler(character));
		state.put(QualifiedTreeCellRenderer.class, new QualifiedTreeCellRenderer(character));
		CharacterLevelsFacade levels = character.getCharacterLevelsFacade();
		if (levels.getSize() > 0)
		{
			state.put("SelectedClass", levels.getClassTaken(levels.getElementAt(0)));
		}
		return state;
	}

	@Override
	public void storeModels(Hashtable<Object, Object> state)
	{
		if (selectedClass != null)
		{
			state.put("SelectedClass", selectedClass);
		}
		((InfoHandler) state.get(InfoHandler.class)).uninstall();
		((AddClassAction) state.get(AddClassAction.class)).uninstall();
	}

	@Override
	public void restoreModels(Hashtable<?, ?> state)
	{
		addClassAction = (AddClassAction) state.get(AddClassAction.class);
		removeClassAction = (RemoveClassAction) state.get(RemoveClassAction.class);

		classTable.setModel((ClassTableModel) state.get(ClassTableModel.class));
		addButton.setAction(addClassAction);
		removeButton.setAction(removeClassAction);

		availableTable.setTreeViewModel((ClassTreeViewModel) state.get(ClassTreeViewModel.class));
		availableTable.setTreeCellRenderer((QualifiedTreeCellRenderer) state.get(QualifiedTreeCellRenderer.class));

		((InfoHandler) state.get(InfoHandler.class)).install();
		((AddClassAction) state.get(AddClassAction.class)).install();
		setSelectedClass((ClassFacade) state.get("SelectedClass"));
	}

	@Override
	public TabTitle getTabTitle()
	{
		return tabTitle;
	}

	private class AddClassAction extends AbstractAction
	{

		private CharacterFacade character;

		public AddClassAction(CharacterFacade character)
		{
			super(LanguageBundle.getString("in_cl_addlevels"));
			this.character = character;
			putValue(SMALL_ICON, Icons.Forward16.getImageIcon());
			setEnabled(false);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			addCharacterLevels(selectedClass);
		}

		public void addCharacterLevels(ClassFacade clazz)
		{
			if (clazz != null)
			{
				ClassFacade[] classes = new ClassFacade[spinnerValue];
				for (int x = 0; x < spinnerValue; x++)
				{
					classes[x] = clazz;
				}
				character.addCharacterLevels(classes);
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

	private class RemoveClassAction extends AbstractAction
	{

		private CharacterFacade character;

		public RemoveClassAction(CharacterFacade character)
		{
			super(LanguageBundle.getString("in_cl_removelevels"));
			this.character = character;
			putValue(SMALL_ICON, Icons.Back16.getImageIcon());
			setEnabled(false);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			character.removeCharacterLevels(1);
		}

	}

	private final class ClassTransferHandler extends TransferHandler
	{

		private final DataFlavor classFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType
				+ ";class=" + ClassFacade.class.getName(), null);

		@Override
		public int getSourceActions(JComponent c)
		{
			if (c == classTable)
			{
				return NONE;
			}
			else
			{
				return COPY;
			}
		}

		@Override
		protected Transferable createTransferable(JComponent c)
		{
			List<Object> data = availableTable.getSelectedData();
			if (data.isEmpty())
			{
				return null;
			}
			Object obj = data.get(0);
			if (!(obj instanceof ClassFacade))
			{
				return null;
			}

			final ClassFacade selClass = (ClassFacade) obj;

			return new Transferable()
			{

				@Override
				public DataFlavor[] getTransferDataFlavors()
				{
					return new DataFlavor[]
							{
								classFlavor
							};
				}

				@Override
				public boolean isDataFlavorSupported(DataFlavor flavor)
				{
					return classFlavor == flavor;
				}

				@Override
				public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
				{
					if (!isDataFlavorSupported(flavor))
					{
						throw new UnsupportedFlavorException(flavor);
					}
					return selClass;
				}

			};
		}

		@Override
		public boolean canImport(JComponent comp, DataFlavor[] transferFlavors)
		{
			return transferFlavors[0] == classFlavor;
		}

		@Override
		public boolean importData(JComponent comp, Transferable t)
		{
			if (comp == classTable)
			{
				try
				{
					ClassFacade c = (ClassFacade) t.getTransferData(classFlavor);
					addClassAction.addCharacterLevels(c);
					return true;
				}
				catch (UnsupportedFlavorException ex)
				{
					Logger.getLogger(ClassInfoTab.class.getName()).log(Level.SEVERE,
																	   null,
																	   ex);
				}
				catch (IOException ex)
				{
					Logger.getLogger(ClassInfoTab.class.getName()).log(Level.SEVERE,
																	   null,
																	   ex);
				}
				return false;
			}
			return true;
		}

	}

	private static class ClassTreeViewModel implements TreeViewModel<ClassFacade>,
			DataView<ClassFacade>
	{

		private static final List<DefaultDataViewColumn> columns =
				Arrays.asList(new DefaultDataViewColumn("in_clInfoHD", String.class),
							  new DefaultDataViewColumn("in_clInfoType", String.class, true),
							  new DefaultDataViewColumn("in_baseStat", String.class),
							  new DefaultDataViewColumn("in_spellType", String.class),
							  new DefaultDataViewColumn("in_source", String.class));
		private static final ListFacade<? extends TreeView<ClassFacade>> treeviews =
				new DefaultListFacade<TreeView<ClassFacade>>(Arrays.asList(ClassTreeView.values()));
		private CharacterFacade character;

		public ClassTreeViewModel(CharacterFacade character)
		{
			this.character = character;
		}

		@Override
		public ListFacade<? extends TreeView<ClassFacade>> getTreeViews()
		{
			return treeviews;
		}

		@Override
		public int getDefaultTreeViewIndex()
		{
			return 0;
		}

		@Override
		public DataView<ClassFacade> getDataView()
		{
			return this;
		}

		@Override
		public ListFacade<ClassFacade> getDataModel()
		{
			return character.getDataSet().getClasses();
		}

		@Override
		public List<?> getData(ClassFacade obj)
		{
			return Arrays.asList(obj.getHD(), getTypes(obj),
								 "None".equals(obj.getBaseStat()) ? "" : obj.getBaseStat(),
								 obj.getSpellType(), obj.getSource());
		}

		private String getTypes(ClassFacade obj)
		{
			String ret = "";
			String[] types = obj.getTypes();
			if (types != null && types.length > 0)
			{
				ret += types[0];
				for (int x = 1; x < types.length; x++)
				{
					ret += ", " + types[x];
				}
			}
			return ret;
		}

		@Override
		public List<? extends DataViewColumn> getDataColumns()
		{
			return columns;
		}

		private static enum ClassTreeView implements TreeView<ClassFacade>
		{

			NAME("in_nameLabel"),
			TYPE_NAME("in_typeName"),
			SOURCE_NAME("in_sourceName");
			private String name;

			private ClassTreeView(String nameKey)
			{
				this.name = LanguageBundle.getString(nameKey);
			}

			@Override
			public String getViewName()
			{
				return name;
			}

			@Override
			public List<TreeViewPath<ClassFacade>> getPaths(ClassFacade pobj)
			{
				switch (this)
				{
					case TYPE_NAME:
						String[] types = pobj.getTypes();
						if (types != null && types.length > 0)
						{
							List<TreeViewPath<ClassFacade>> paths = new ArrayList<TreeViewPath<ClassFacade>>(
									types.length);
							for (String type : types)
							{
								paths.add(new TreeViewPath<ClassFacade>(pobj,
																		type));
							}
							return paths;
						}
					case NAME:
						return Collections.singletonList(new TreeViewPath<ClassFacade>(pobj));
					case SOURCE_NAME:
						return Collections.singletonList(
								new TreeViewPath<ClassFacade>(pobj,
															  pobj.getSource()));
					default:
						throw new InternalError();
				}

			}

		}

	}

	private static class ClassTableModel extends AbstractTableModel implements ListListener<CharacterLevelFacade>
	{

		private static final String[] columns =
		{
			LanguageBundle.getString("in_level"),
			LanguageBundle.getString("in_class"),
			LanguageBundle.getString("in_source")
		};
		private CharacterLevelsFacade model;

		public ClassTableModel(CharacterFacade character)
		{
			this.model = character.getCharacterLevelsFacade();
			model.addListListener(this);
		}

		@Override
		public int getRowCount()
		{
			return model.getSize();
		}

		@Override
		public int getColumnCount()
		{
			return 3;
		}

		@Override
		public String getColumnName(int column)
		{
			return columns[column];
		}

		@Override
		public Class<?> getColumnClass(int columnIndex)
		{
			switch (columnIndex)
			{
				case 0:
					return Integer.class;
				case 1:
					return Object.class;
				case 2:
					return String.class;
			}
			return null;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			if (columnIndex == 0)
			{
				return rowIndex + 1;
			}
			ClassFacade c = model.getClassTaken(model.getElementAt(rowIndex));
			switch (columnIndex)
			{
				case 1:
					return c;
				case 2:
					return c.getSource();
				default:
					return null;
			}
		}

		@Override
		public void elementAdded(ListEvent<CharacterLevelFacade> e)
		{
			fireTableRowsInserted(e.getIndex(), e.getIndex());
		}

		@Override
		public void elementRemoved(ListEvent<CharacterLevelFacade> e)
		{
			fireTableRowsDeleted(e.getIndex(), e.getIndex());
		}

		@Override
		public void elementsChanged(ListEvent<CharacterLevelFacade> e)
		{
			fireTableRowsUpdated(0, getRowCount() - 1);
		}

	}

	private class InfoHandler implements ListSelectionListener
	{

		private CharacterFacade character;

		public InfoHandler(CharacterFacade character)
		{
			this.character = character;
		}

		public void install()
		{
			classTable.getSelectionModel().addListSelectionListener(this);
			availableTable.getSelectionModel().addListSelectionListener(this);
		}

		public void uninstall()
		{
			classTable.getSelectionModel().removeListSelectionListener(this);
			availableTable.getSelectionModel().removeListSelectionListener(this);
		}

		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			if (!e.getValueIsAdjusting())
			{
				Object data = null;
				if (e.getSource() == availableTable.getSelectionModel())
				{
					data = availableTable.getSelectedObject();
				}
				else
				{
					int selectedRow = classTable.getSelectedRow();
					if (selectedRow != -1)
					{
						data = classTable.getModel().getValueAt(selectedRow, 1);
					}
				}
				if (data != null && data instanceof ClassFacade)
				{
					//TODO: Need to identify if this is a subclass and if so supply the parent class too.
					infoPane.setText(character.getInfoFactory().getHTMLInfo(
							(ClassFacade) data, null));
				}
				else
				{
					infoPane.setText("");
				}
			}
		}

	}

}
