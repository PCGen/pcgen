/*
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

import pcgen.core.PCClass;
import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.CharacterLevelFacade;
import pcgen.facade.core.CharacterLevelsFacade;
import pcgen.facade.util.DefaultListFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.event.ListEvent;
import pcgen.facade.util.event.ListListener;
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
import pcgen.gui2.util.table.TableUtils;
import pcgen.gui2.util.treeview.DataView;
import pcgen.gui2.util.treeview.DataViewColumn;
import pcgen.gui2.util.treeview.DefaultDataViewColumn;
import pcgen.gui2.util.treeview.TreeView;
import pcgen.gui2.util.treeview.TreeViewModel;
import pcgen.gui2.util.treeview.TreeViewPath;
import pcgen.system.LanguageBundle;
import pcgen.util.enumeration.Tab;

@SuppressWarnings("serial")
public class ClassInfoTab extends FlippingSplitPane implements CharacterInfoTab
{

	/**
	 * The table of available classes
	 */
	private final FilteredTreeViewTable<Object, PCClass> availableTable;
	/**
	 * The table of the character's classes
	 */
	private final JTable classTable;
	private final JButton addButton;
	private final JButton removeButton;
	private final TabTitle tabTitle;
	private final InfoPane infoPane;
	private final JSpinner spinner;
	private final FilterButton<Object, PCClass> qFilterButton;
	private final QualifiedTreeCellRenderer qualifiedRenderer;
	private final ClassTransferHandler classTransferHandler;
	private int spinnerValue;

	public ClassInfoTab()
	{
		super();
		this.availableTable = new FilteredTreeViewTable<>();
		this.classTable = TableUtils.createDefaultTable();
		this.addButton = new JButton();
		this.removeButton = new JButton();
		this.tabTitle = new TabTitle(Tab.CLASSES);
		this.infoPane = new InfoPane(LanguageBundle.getString("in_clInfo")); //$NON-NLS-1$
		this.spinner = new JSpinner(new SpinnerNumberModel(1, 1, 50, 1));
		this.qFilterButton = new FilterButton<>("ClassQualified");
		this.qualifiedRenderer = new QualifiedTreeCellRenderer();
		this.classTransferHandler = new ClassTransferHandler();
		initComponents();
	}

	private void initComponents()
	{
		FlippingSplitPane topPane = new FlippingSplitPane();
		setTopComponent(topPane);
		setOrientation(VERTICAL_SPLIT);

		JPanel availPanel = new JPanel(new BorderLayout());
		FilterBar<Object, PCClass> bar = new FilterBar<>();
		bar.addDisplayableFilter(new SearchFilterPanel());
		qFilterButton.setText(LanguageBundle.getString("in_igQualFilter")); //$NON-NLS-1$
		bar.addDisplayableFilter(qFilterButton);
		availPanel.add(bar, BorderLayout.NORTH);

		availableTable.setTreeCellRenderer(qualifiedRenderer);
		availableTable.setDisplayableFilter(bar);
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
			classTable.setDragEnabled(true);
			classTable.setTransferHandler(classTransferHandler);

			availableTable.setDragEnabled(true);
			availableTable.setTransferHandler(classTransferHandler);
		}
		initListeners();

		topPane.setRightComponent(selPanel);
		setBottomComponent(infoPane);
		setResizeWeight(0.75);
	}

	private void initListeners()
	{
		spinner.addChangeListener(e -> spinnerValue = (Integer) spinner.getValue());
		spinnerValue = (Integer) spinner.getValue();
		classTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		availableTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	@Override
	public ModelMap createModels(CharacterFacade character)
	{
		ModelMap models = new ModelMap();
		models.put(ClassTransferHandler.TransHandler.class, classTransferHandler.createHandler(character));
		models.put(ClassTreeViewModel.class, new ClassTreeViewModel(character));
		models.put(ClassTableModel.class, new ClassTableModel(character));
		models.put(AddClassAction.class, new AddClassAction(character));
		models.put(RemoveClassAction.class, new RemoveClassAction(character));
		models.put(InfoHandler.class, new InfoHandler(character));
		models.put(Handler.class, qualifiedRenderer.createHandler(character));
		models.put(QualifiedFilterHandler.class, new QualifiedFilterHandler(character));
		return models;
	}

	@Override
	public void storeModels(ModelMap models)
	{
		models.get(InfoHandler.class).uninstall();
		models.get(AddClassAction.class).uninstall();
		models.get(Handler.class).uninstall();
		models.get(ClassTransferHandler.TransHandler.class).uninstall();
	}

	@Override
	public void restoreModels(ModelMap models)
	{
		models.get(Handler.class).install();
		models.get(QualifiedFilterHandler.class).install();
		classTable.setModel(models.get(ClassTableModel.class));
		availableTable.setTreeViewModel(models.get(ClassTreeViewModel.class));
		models.get(AddClassAction.class).install();
		models.get(RemoveClassAction.class).install();
		models.get(InfoHandler.class).install();
		models.get(ClassTransferHandler.TransHandler.class).install();
	}

	@Override
	public TabTitle getTabTitle()
	{
		return tabTitle;
	}

	private PCClass getSelectedClass(Object eventSource)
	{
		Object data = null;
		if (eventSource == availableTable.getSelectionModel())
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
		if (data != null && data instanceof PCClass)
		{
			return (PCClass) data;
		}
		return null;
	}

	public void addCharacterLevels(CharacterFacade character, PCClass clazz)
	{
		if (clazz != null)
		{
			PCClass[] classes = new PCClass[spinnerValue];
			for (int x = 0; x < spinnerValue; x++)
			{
				classes[x] = clazz;
			}
			character.addCharacterLevels(classes);
		}
	}

	private class AddClassAction extends AbstractAction implements ListSelectionListener
	{

		private final CharacterFacade character;
		private PCClass selectedClass = null;

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
			addCharacterLevels(character, selectedClass);
		}

		public void install()
		{
			addButton.setAction(this);
			availableTable.addActionListener(this);
			availableTable.getSelectionModel().addListSelectionListener(this);
			classTable.getSelectionModel().addListSelectionListener(this);
		}

		public void uninstall()
		{
			availableTable.removeActionListener(this);
			availableTable.getSelectionModel().removeListSelectionListener(this);
			classTable.getSelectionModel().removeListSelectionListener(this);
		}

		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			if (!e.getValueIsAdjusting())
			{
				selectedClass = getSelectedClass(e.getSource());
				if (selectedClass == null)
				{
					setEnabled(false);
				}
				setEnabled(selectedClass != null);
			}
		}

	}

	private class RemoveClassAction extends AbstractAction implements ListListener<CharacterLevelFacade>
	{

		private final CharacterFacade character;

		public RemoveClassAction(CharacterFacade character)
		{
			super(LanguageBundle.getString("in_cl_removelevels"));
			this.character = character;
			putValue(SMALL_ICON, Icons.Back16.getImageIcon());
			setEnabled(false);
			character.getCharacterLevelsFacade().addListListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			character.removeCharacterLevels(1);
		}

		public void install()
		{
			removeButton.setAction(this);
		}

		private void checkEnabled()
		{
			setEnabled(!character.getCharacterLevelsFacade().isEmpty());
		}

		@Override
		public void elementAdded(ListEvent<CharacterLevelFacade> e)
		{
			checkEnabled();
		}

		@Override
		public void elementRemoved(ListEvent<CharacterLevelFacade> e)
		{
			checkEnabled();
		}

		@Override
		public void elementsChanged(ListEvent<CharacterLevelFacade> e)
		{
			checkEnabled();
		}

		@Override
		public void elementModified(ListEvent<CharacterLevelFacade> e)
		{
			//Do nothing
		}
	}

	private final class ClassTransferHandler extends TransferHandler
	{

		private final DataFlavor classFlavor =
				new DataFlavor(
					DataFlavor.javaJVMLocalObjectMimeType
					+ ";class=" + PCClass.class.getName(), null); //$NON-NLS-1$

		private CharacterFacade character = null;

		public void setCharacter(CharacterFacade character)
		{
			this.character = character;
		}

		public TransHandler createHandler(CharacterFacade character)
		{
			return new TransHandler(character);
		}

		private class TransHandler
		{

			private final CharacterFacade character;

			public TransHandler(CharacterFacade character)
			{
				this.character = character;
			}

			public void install()
			{
				setCharacter(character);
			}

			public void uninstall()
			{
				setCharacter(null);
			}
		}

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
			final PCClass selClass = getSelectedClass(availableTable);

			if (selClass == null)
			{
				return null;
			}
			return new Transferable()
			{

				@Override
				public DataFlavor[] getTransferDataFlavors()
				{
					return new DataFlavor[]{classFlavor};
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
					PCClass c = (PCClass) t.getTransferData(classFlavor);
					addCharacterLevels(character, c);
					return true;
				}
				catch (UnsupportedFlavorException | IOException ex)
				{
					Logger.getLogger(ClassTransferHandler.class.getName()).log(Level.SEVERE, null, ex);
				}
				return false;
			}
			return true;
		}

	}

	private class QualifiedFilterHandler implements Filter<Object, PCClass>
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
		public boolean accept(Object context, PCClass element)
		{
			return character.isQualifiedFor(element);
		}

	}

	private static class ClassTreeViewModel implements TreeViewModel<PCClass>, DataView<PCClass>
	{

		private static final List<DefaultDataViewColumn> COLUMNS =
				Arrays.asList(new DefaultDataViewColumn("in_clInfoHD", String.class), //$NON-NLS-1$
					new DefaultDataViewColumn("in_clInfoType", String.class, true), //$NON-NLS-1$
					new DefaultDataViewColumn("in_descrip", String.class, false), //$NON-NLS-1$
					new DefaultDataViewColumn("in_baseStat", String.class), //$NON-NLS-1$
					new DefaultDataViewColumn("in_spellType", String.class), //$NON-NLS-1$
					new DefaultDataViewColumn("in_source", String.class)); //$NON-NLS-1$
		private static final ListFacade<? extends TreeView<PCClass>> TREE_VIEWS =
				new DefaultListFacade<>(Arrays.asList(ClassTreeView.values()));
		private final CharacterFacade character;

		public ClassTreeViewModel(CharacterFacade character)
		{
			this.character = character;
		}

		@Override
		public String getPrefsKey()
		{
			return "ClassTree"; //$NON-NLS-1$
		}

		@Override
		public ListFacade<? extends TreeView<PCClass>> getTreeViews()
		{
			return TREE_VIEWS;
		}

		@Override
		public int getDefaultTreeViewIndex()
		{
			return 0;
		}

		@Override
		public DataView<PCClass> getDataView()
		{
			return this;
		}

		@Override
		public ListFacade<PCClass> getDataModel()
		{
			return character.getDataSet().getClasses();
		}

		@Override
		public Object getData(PCClass obj, int column)
		{
			switch (column)
			{
				case 0:
					return obj.getHD();
				case 1:
					return getTypes(obj);
				case 2:
					return character.getInfoFactory().getDescription(obj);
				case 3:
					return "None".equals(obj.getBaseStat()) ? "" : obj.getBaseStat(); //$NON-NLS-1$ //$NON-NLS-2$
				case 4:
					return obj.getSpellType();
				case 5:
					return obj.getSource();
				default:
					return null;
			}
		}

		@Override
		public void setData(Object value, PCClass element, int column)
		{
		}

		private String getTypes(PCClass obj)
		{
			String ret = ""; //$NON-NLS-1$
			String[] types = obj.getTypes();
			if (types != null && types.length > 0)
			{
				ret += types[0];
				for (int x = 1; x < types.length; x++)
				{
					ret += ", " + types[x]; //$NON-NLS-1$
				}
			}
			return ret;
		}

		@Override
		public List<? extends DataViewColumn> getDataColumns()
		{
			return COLUMNS;
		}

		private static enum ClassTreeView implements TreeView<PCClass>
		{

			NAME("in_nameLabel"), //$NON-NLS-1$
			TYPE_NAME("in_typeName"), //$NON-NLS-1$
			SOURCE_NAME("in_sourceName"); //$NON-NLS-1$
			private final String name;

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
			public List<TreeViewPath<PCClass>> getPaths(PCClass pobj)
			{
				switch (this)
				{
					case TYPE_NAME:
						String[] types = pobj.getTypes();
						if (types != null && types.length > 0)
						{
							List<TreeViewPath<PCClass>> paths = new ArrayList<>(types.length);
							for (String type : types)
							{
								paths.add(new TreeViewPath<>(pobj, type));
							}
							return paths;
						}
					case NAME:
						return Collections.singletonList(new TreeViewPath<>(pobj));
					case SOURCE_NAME:
						return Collections.singletonList(new TreeViewPath<>(pobj, pobj.getSourceForNodeDisplay()));
					default:
						throw new InternalError();
				}
			}
		}
	}

	private static class ClassTableModel extends AbstractTableModel implements ListListener<CharacterLevelFacade>
	{

		private static final String[] COLUMNS = {LanguageBundle.getString("in_level"), //$NON-NLS-1$
			LanguageBundle.getString("in_class"), //$NON-NLS-1$
			LanguageBundle.getString("in_source") //$NON-NLS-1$
		};
		private final CharacterLevelsFacade model;

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
			return COLUMNS[column];
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
				default:
					//Case not caught, should this cause an error?
					break;
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
			PCClass c = model.getClassTaken(model.getElementAt(rowIndex));
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

		@Override
		public void elementModified(ListEvent<CharacterLevelFacade> e)
		{
			fireTableRowsUpdated(e.getIndex(), e.getIndex());
		}

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
			classTable.getSelectionModel().addListSelectionListener(this);
			availableTable.getSelectionModel().addListSelectionListener(this);
			infoPane.setText(text);
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
				PCClass data = getSelectedClass(e.getSource());
				if (data != null)
				{
					text = character.getInfoFactory().getHTMLInfo(data, null);
					infoPane.setText(text);
				}
				else
				{
					text = ""; //$NON-NLS-1$
					infoPane.setText(""); //$NON-NLS-1$
				}
			}
		}

	}

}
