/*
 * CompanionInfoTab.java Copyright 2012 Connor Petty <cpmeister@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */
package pcgen.gui2.tabs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.CompanionFacade;
import pcgen.facade.core.CompanionStubFacade;
import pcgen.facade.core.CompanionSupportFacade;
import pcgen.facade.util.DefaultListFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.MapFacade;
import pcgen.facade.util.event.ListEvent;
import pcgen.facade.util.event.ListListener;
import pcgen.facade.util.event.MapEvent;
import pcgen.facade.util.event.MapListener;
import pcgen.facade.util.event.ReferenceEvent;
import pcgen.facade.util.event.ReferenceListener;
import pcgen.gui2.PCGenFrame;
import pcgen.gui2.filter.Filter;
import pcgen.gui2.filter.FilteredListFacade;
import pcgen.gui2.filter.FilteredTreeViewTable;
import pcgen.gui2.filter.SearchFilterPanel;
import pcgen.gui2.tabs.models.HtmlSheetSupport;
import pcgen.gui2.tools.FlippingSplitPane;
import pcgen.gui2.tools.Utility;
import pcgen.gui2.util.DisplayAwareTab;
import pcgen.gui2.util.JTableEx;
import pcgen.gui2.util.JTreeTable;
import pcgen.gui2.util.treetable.AbstractTreeTableModel;
import pcgen.gui2.util.treetable.DefaultTreeTableNode;
import pcgen.gui2.util.treetable.TreeTableModel;
import pcgen.gui2.util.treeview.DataView;
import pcgen.gui2.util.treeview.DataViewColumn;
import pcgen.gui2.util.treeview.TreeView;
import pcgen.gui2.util.treeview.TreeViewModel;
import pcgen.gui2.util.treeview.TreeViewPath;
import pcgen.gui3.JFXPanelFromResource;
import pcgen.gui3.SimpleHtmlPanelController;
import pcgen.system.CharacterManager;
import pcgen.system.ConfigurationSettings;
import pcgen.system.LanguageBundle;
import pcgen.util.Comparators;
import pcgen.util.Logging;
import pcgen.util.enumeration.Tab;

/**
 * This component allows a user to manage a character's companions (animal,
 * familiar, cohort, mount, etc).
 */
@SuppressWarnings("PMD.UseArrayListInsteadOfVector")
public class CompanionInfoTab extends FlippingSplitPane implements CharacterInfoTab, TodoHandler, DisplayAwareTab
{

	private final JTreeTable companionsTable;
	private final JFXPanelFromResource<SimpleHtmlPanelController> infoPane;
	private final JButton loadButton;
	private CompanionDialog companionDialog = null;
	private Object selectedElement;

	CompanionInfoTab()
	{
		this.companionsTable = new JTreeTable()
		{
			@Override
			protected void configureEnclosingScrollPane()
			{
				//We do nothing so the table is displayed without a header
			}

		};
		this.infoPane = new JFXPanelFromResource<>(
				SimpleHtmlPanelController.class,
				"SimpleHtmlPanel.fxml"
		);
		this.loadButton = new JButton();
		initComponents();
	}

	private void initDialog()
	{
		if (companionDialog == null)
		{
			companionDialog = new CompanionDialog();
		}
	}

	private void initComponents()
	{
		{
			DefaultTableColumnModel model = new DefaultTableColumnModel();
			TableColumn column = new TableColumn(0);
			column.setResizable(true);
			model.addColumn(column);

			column = new TableColumn(1, 120, new ButtonCellRenderer(), null);
			column.setMaxWidth(120);
			column.setResizable(false);
			model.addColumn(column);

			companionsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			companionsTable.getTableHeader().setResizingAllowed(false);
			companionsTable.setAutoCreateColumnsFromModel(false);
			companionsTable.setColumnModel(model);
		}
		companionsTable.setIntercellSpacing(new Dimension(0, 0));
		companionsTable.setFocusable(false);
		companionsTable.setRowHeight(23);
		companionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setLeftComponent(new JScrollPane(companionsTable));
		JPanel rightPane = new JPanel(new BorderLayout());
		rightPane.add(new JScrollPane(infoPane), BorderLayout.CENTER);
		JPanel buttonPane = new JPanel(new FlowLayout());
		buttonPane.add(loadButton);
		rightPane.add(buttonPane, BorderLayout.SOUTH);
		setRightComponent(rightPane);
	}

	@Override
	public ModelMap createModels(CharacterFacade character)
	{
		ModelMap models = new ModelMap();
		models.put(CompanionsModel.class, new CompanionsModel(character));
		models.put(ButtonCellEditor.class, new ButtonCellEditor(character));
		models.put(LoadButtonAndSheetHandler.class, new LoadButtonAndSheetHandler());
		models.put(TreeExpansionHandler.class, new TreeExpansionHandler());
		return models;
	}

	@Override
	public void restoreModels(ModelMap models)
	{
		companionsTable.setTreeTableModel(models.get(CompanionsModel.class));
		companionsTable.setDefaultEditor(Object.class, models.get(ButtonCellEditor.class));
		models.get(TreeExpansionHandler.class).install();
		models.get(LoadButtonAndSheetHandler.class).install();
	}

	@Override
	public void storeModels(ModelMap models)
	{
		models.get(TreeExpansionHandler.class).uninstall();
		models.get(LoadButtonAndSheetHandler.class).uninstall();
	}

	@Override
	public TabTitle getTabTitle()
	{
		return new TabTitle(Tab.COMPANIONS);
	}

	@Override
	public void tabSelected()
	{
		// Refresh the character sheet as we have been displayed.
		LoadButtonAndSheetHandler action = (LoadButtonAndSheetHandler) loadButton.getAction();
		if (action != null)
		{
			action.showCompanion(false);
		}
	}

	private void selectCompanion(CompanionFacade compFacade)
	{
		TreeTableModel treeTableModel = companionsTable.getTreeTableModel();
		treeTableModel.getRoot();
		TreePath path = null;

		JTree tree = companionsTable.getTree();
		String companionType = compFacade.getCompanionType();
		for (int i = 0; i < tree.getRowCount(); i++)
		{
			TreePath pathForRow = tree.getPathForRow(i);
			Object lastPathComponent = pathForRow.getLastPathComponent();
			if (lastPathComponent.toString().startsWith(companionType))
			{
				tree.expandRow(i);
			}
			else if (lastPathComponent instanceof pcgen.gui2.tabs.CompanionInfoTab.CompanionsModel.CompanionNode)
			{
				CompanionFacade rowComp =
					(CompanionFacade)
						((pcgen.gui2.tabs.CompanionInfoTab.CompanionsModel.CompanionNode) lastPathComponent)
							.getValueAt(0);

				if (rowComp != null && rowComp.getFileRef().get() == compFacade.getFileRef().get()
					&& rowComp.getNameRef().get() == compFacade.getNameRef().get()
					&& rowComp.getRaceRef().get() == compFacade.getRaceRef().get())
				{
					path = pathForRow;
				}
			}
		}
		if (path != null)
		{
			companionsTable.getTree().setSelectionPath(path);
			companionsTable.getTree().scrollPathToVisible(path);
		}
	}

	private class TreeExpansionHandler implements TreeExpansionListener
	{

		private final JTree tree;
		private List<TreePath> expandedPaths = Collections.emptyList();

		public TreeExpansionHandler()
		{
			this.tree = companionsTable.getTree();
		}

		public void install()
		{
			for (TreePath path : expandedPaths)
			{
				tree.expandPath(path);
			}
			tree.addTreeExpansionListener(this);
		}

		public void uninstall()
		{
			tree.removeTreeExpansionListener(this);
		}

		@Override
		public void treeExpanded(TreeExpansionEvent event)
		{
			saveExpansionState();
		}

		@Override
		public void treeCollapsed(TreeExpansionEvent event)
		{
			saveExpansionState();
		}

		private void saveExpansionState()
		{
			Object root = companionsTable.getTreeTableModel().getRoot();
			Enumeration<TreePath> paths = tree.getExpandedDescendants(new TreePath(root));
			expandedPaths = Collections.list(paths);
		}

	}

	private class LoadButtonAndSheetHandler extends AbstractAction implements ListSelectionListener
	{

		private final HtmlSheetSupport sheetSupport;
		private int selectedRow;
		private final ListSelectionModel selectionModel;
		private final PCGenFrame frame;

		public LoadButtonAndSheetHandler()
		{
			File sheet = Path.of(
						ConfigurationSettings.getPreviewDir(),
						"companions", "compact_companion.htm").toFile(); //$NON-NLS-1$ //$NON-NLS-2$
			this.sheetSupport = new HtmlSheetSupport(infoPane, sheet.getAbsolutePath());
			this.selectedRow = -1;
			this.selectionModel = companionsTable.getSelectionModel();
			this.frame = (PCGenFrame) JOptionPane.getFrameForComponent(CompanionInfoTab.this);
		}

		public void install()
		{
			configureButton();
			loadButton.setAction(this);
			if (selectedRow == -1)
			{
				selectionModel.clearSelection();
			}
			else
			{
				selectionModel.setSelectionInterval(selectedRow, selectedRow);
			}
			selectionModel.addListSelectionListener(this);

			showCompanion(false);
			sheetSupport.install();
		}

		public void uninstall()
		{
			selectionModel.removeListSelectionListener(this);
			sheetSupport.uninstall();
		}

		private void configureButton()
		{
			CompanionFacade companion = getSelectedCompanion();
			setEnabled(companion != null);
			if (companion != null && isCompanionOpen(companion))
			{
				//configure action for show
				this.putValue(Action.NAME, LanguageBundle.getString("in_companionSwitchTo")); //$NON-NLS-1$
			}
			else
			{
				//configure action for load
				this.putValue(Action.NAME, LanguageBundle.getString("in_companionLoadComp")); //$NON-NLS-1$
			}
		}

		private void showCompanion(boolean switchTabs)
		{
			CompanionFacade companion = getSelectedCompanion();
			if (companion == null)
			{
				if (!switchTabs)
				{
					infoPane.getController().setHtml(""); //$NON-NLS-1$
				}
				return;
			}
			if (isCompanionOpen(companion))
			{
				CharacterFacade character = CharacterManager.getCharacterMatching(companion);
				if (character != null)
				{
					if (switchTabs)
					{
						frame.setCharacter(character);
						return;
					}
					else
					{
						sheetSupport.setCharacter(character);
						sheetSupport.refresh();
					}
				}
				//the companion was not found
				//TODO: show error, complain?
			}
			else if (switchTabs)
			{
				frame.loadCharacterFromFile(companion.getFileRef().get());
			}
			else
			{
				// Display a message telling the user to open the companion.
				infoPane.getController().setHtml(LanguageBundle.getString("in_companionLoadCompanionMessage"));
			}
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			showCompanion(true);
		}

		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			if (e.getValueIsAdjusting())
			{
				return;
			}
			selectedRow = selectionModel.getMinSelectionIndex();
			configureButton();
			showCompanion(false);
		}

		private CompanionFacade getSelectedCompanion()
		{
			if (selectedRow == -1)
			{
				return null;
			}
			Object value = companionsTable.getValueAt(selectedRow, 0);
			if (value instanceof CompanionFacade)
			{
				return (CompanionFacade) value;
			}
			return null;
		}

		private boolean isCompanionOpen(CompanionFacade companion)
		{
			File compFile = companion.getFileRef().get();
			if (compFile == null)
			{
				return true;
			}
			for (CharacterFacade character : CharacterManager.getCharacters())
			{
				File charFile = character.getFileRef().get();
				if (compFile.equals(charFile))
				{
					return true;
				}
			}
			return false;
		}

	}

	private static class ButtonCellRenderer extends JPanel implements TableCellRenderer
	{

		private final JButton button = new JButton();
		private final DefaultTableCellRenderer background = new DefaultTableCellRenderer();

		public ButtonCellRenderer()
		{
			button.setMargin(new Insets(0, 0, 0, 0));

			setOpaque(true);
			setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.weightx = 1;
			gbc.weighty = 1;
			gbc.anchor = GridBagConstraints.EAST;
			gbc.fill = GridBagConstraints.VERTICAL;
			add(button, gbc);
		}

		@Override
		public boolean isOpaque()
		{
			Color back = getBackground();
			Component p = getParent();
			if (p != null)
			{
				p = p.getParent();
			}

			// p should now be the JTable. 
			boolean colorMatch = (back != null) && (p != null) && back.equals(p.getBackground()) && p.isOpaque();
			return !colorMatch && super.isOpaque();
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column)
		{
			background.getTableCellRendererComponent(table, null, isSelected, hasFocus, row, column);
			setBackground(background.getBackground());
			value = table.getValueAt(row, 0);
			if (value instanceof CompanionFacade)
			{
				button.setText(LanguageBundle.getString("in_companionRemove")); //$NON-NLS-1$
			}
			else
			{
				button.setText(LanguageBundle.getString("in_companionCreateNew")); //$NON-NLS-1$
			}
			value = table.getValueAt(row, 1);
			if (value instanceof Boolean)
			{
				button.setEnabled((Boolean) value);
			}
			else
			{
				button.setEnabled(true);
			}
			return this;
		}

	}

	private class ButtonCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener
	{

		private static final String CREATE_COMMAND = "New"; //$NON-NLS-1$
		private static final String REMOVE_COMMAND = "Remove"; //$NON-NLS-1$
		private final JButton button = new JButton();
		private final JPanel container = new JPanel();
		private final DefaultTableCellRenderer background = new DefaultTableCellRenderer();
		private final CharacterFacade character;

		public ButtonCellEditor(CharacterFacade character)
		{
			this.character = character;

			button.addActionListener(this);
			button.setMargin(new Insets(0, 0, 0, 0));

			container.setOpaque(true);
			container.setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.weightx = 1;
			gbc.weighty = 1;
			gbc.anchor = GridBagConstraints.EAST;
			gbc.fill = GridBagConstraints.VERTICAL;
			container.add(button, gbc);
		}

		@Override
		public Object getCellEditorValue()
		{
			return null;
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
			int column)
		{
			background.getTableCellRendererComponent(table, null, true, false, row, column);
			container.setBackground(background.getBackground());
			selectedElement = table.getValueAt(row, 0);
			if (selectedElement instanceof CompanionFacade)
			{
				button.setText(LanguageBundle.getString("in_companionRemove")); //$NON-NLS-1$
				button.setActionCommand(REMOVE_COMMAND);
			}
			else
			{
				button.setText(LanguageBundle.getString("in_companionCreateNew")); //$NON-NLS-1$
				button.setActionCommand(CREATE_COMMAND);
			}
			return container;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			CompanionSupportFacade support = character.getCompanionSupport();
			if (REMOVE_COMMAND.equals(e.getActionCommand()))
			{
				CompanionFacade companion = (CompanionFacade) selectedElement;
				int ret = JOptionPane.showConfirmDialog(button,
					LanguageBundle.getFormattedString("in_companionConfirmRemovalMsg", companion //$NON-NLS-1$
						.getNameRef().get()),
					LanguageBundle.getString("in_companionConfirmRemoval"), //$NON-NLS-1$
					JOptionPane.YES_NO_OPTION);
				if (ret == JOptionPane.YES_OPTION)
				{
					support.removeCompanion(companion);
				}
			}
			if (CREATE_COMMAND.equals(e.getActionCommand()))
			{
				initDialog();
				String type = (String) selectedElement;
				companionDialog.setCharacter(character);
				companionDialog.setCompanionType(type);
				companionDialog.setLocationRelativeTo(CompanionInfoTab.this);
				companionDialog.setVisible(true);
				CharacterFacade comp = companionDialog.getNewCompanion();
				if (comp != null)
				{
					selectCompanion(comp);
				}
			}
			cancelCellEditing();
		}

	}

	private static class FilteredCompanionList extends FilteredListFacade<String, CompanionStubFacade>
			implements Filter<String, CompanionStubFacade>
	{

		public FilteredCompanionList()
		{
			setFilter(this);
		}

		public void setCompanionType(String type)
		{
			setContext(type);
		}

		@Override
		public boolean accept(String context, CompanionStubFacade element)
		{
			if (context == null)
			{
				return true;
			}
			return context.equals(element.getCompanionType());
		}

	}

	private class CompanionDialog extends JDialog
			implements TreeViewModel<CompanionStubFacade>, DataView<CompanionStubFacade>, ActionListener
	{

		private final FilteredCompanionList model;
		private final JButton selectButton;
		private final FilteredTreeViewTable raceTable;
		private CharacterFacade character;
		private String companionType;
		private CharacterFacade newCompanion;
		private final DefaultListFacade<CompanionTreeView> treeViews =
				new DefaultListFacade<>(Arrays.asList(CompanionTreeView.values()));

		public CompanionDialog()
		{
			super(JOptionPane.getFrameForComponent(CompanionInfoTab.this), true);
			this.model = new FilteredCompanionList();
			this.selectButton = new JButton();
			this.raceTable = new FilteredTreeViewTable<>();
			initComponents();
			pack();
		}

		private void initComponents()
		{
			setTitle(LanguageBundle.getString("in_companionSelectRace")); //$NON-NLS-1$
			setLayout(new BorderLayout());
			Container container = getContentPane();
			{
				final ListSelectionModel selectionModel = raceTable.getSelectionModel();
				selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				selectionModel.addListSelectionListener(e -> {
                    if (!e.getValueIsAdjusting())
                    {
                        selectButton.setEnabled(!selectionModel.isSelectionEmpty());
                    }
                });
			}
			SearchFilterPanel searchBar = new SearchFilterPanel();
			container.add(searchBar, BorderLayout.NORTH);
			raceTable.setDisplayableFilter(searchBar);
			raceTable.addActionListener(this);
			raceTable.setTreeViewModel(this);
			container.add(new JScrollPane(raceTable), BorderLayout.CENTER);
			JPanel buttonPane = new JPanel(new FlowLayout());
			selectButton.addActionListener(this);
			selectButton.setEnabled(false);
			selectButton.setActionCommand("SELECT");
			buttonPane.add(selectButton);

			JButton cancelButton = new JButton(LanguageBundle.getString("in_cancel"));
			cancelButton.addActionListener(this);
			cancelButton.setActionCommand("CANCEL");
			buttonPane.add(cancelButton);
			container.add(buttonPane, BorderLayout.SOUTH);

			Utility.installEscapeCloseOperation(this);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (!"null".equals(e.getActionCommand()))
			{
				if ("SELECT".equals(e.getActionCommand()) || (e.getID() == JTableEx.ACTION_DOUBLECLICK))
				{
					newCompanion =
							CharacterManager.createNewCharacter(character.getUIDelegate(), character.getDataSet());
					CompanionStubFacade selected = (CompanionStubFacade) raceTable.getSelectedObject();
					newCompanion.setRace(selected.getRaceRef().get());
					character.getCompanionSupport().addCompanion(newCompanion, companionType);
					setVisible(false);
				}
				else
				{
					newCompanion = null;
					setVisible(false);
				}
			}
		}

		public void setCharacter(CharacterFacade character)
		{
			this.character = character;
			model.setDelegate(character.getCompanionSupport().getAvailableCompanions());
		}

		public void setCompanionType(String type)
		{
			companionType = type;
			model.setCompanionType(type);
			selectButton.setText(LanguageBundle.getFormattedString("in_companionCreateType", type)); //$NON-NLS-1$
			newCompanion = null;
		}

		@Override
		public ListFacade<? extends TreeView<CompanionStubFacade>> getTreeViews()
		{
			return treeViews;
		}

		@Override
		public int getDefaultTreeViewIndex()
		{
			return 0;
		}

		@Override
		public DataView<CompanionStubFacade> getDataView()
		{
			return this;
		}

		@Override
		public ListFacade<CompanionStubFacade> getDataModel()
		{
			return model;
		}

		@Override
		public Object getData(CompanionStubFacade element, int column)
		{
			return null;
		}

		@Override
		public void setData(Object value, CompanionStubFacade element, int column)
		{
		}

		@Override
		public List<? extends DataViewColumn> getDataColumns()
		{
			return Collections.emptyList();
		}

		@Override
		public String getPrefsKey()
		{
			return "CompanionAvail"; //$NON-NLS-1$
		}

		/**
		 * @return the newCompanion
		 */
		public CharacterFacade getNewCompanion()
		{
			return newCompanion;
		}

	}

	private enum CompanionTreeView implements TreeView<CompanionStubFacade>
	{

		NAME("in_race"); //$NON-NLS-1$
		private final String name;

		private CompanionTreeView(String name)
		{
			this.name = LanguageBundle.getString(name);
		}

		@Override
		public String getViewName()
		{
			return name;
		}

		@Override
		public List<TreeViewPath<CompanionStubFacade>> getPaths(CompanionStubFacade pobj)
		{
            if (this == CompanionTreeView.NAME) {
                return Collections.singletonList(new TreeViewPath<>(pobj));
            }
            throw new InternalError();
        }

	}

	private static class CompanionsModel extends AbstractTreeTableModel implements TreeTableModel
	{

		private final CompanionSupportFacade support;
		private final MapFacade<String, Integer> maxMap;

		public CompanionsModel(CharacterFacade character)
		{
			this.support = character.getCompanionSupport();
			this.maxMap = support.getMaxCompanionsMap();
			this.setRoot(new RootNode());
		}

		@Override
		public boolean isCellEditable(Object node, int column)
		{
			if (column > 0)
			{
				Object value = getValueAt(node, column);
				if (value instanceof Boolean)
				{
					return (Boolean) value;
				}
				return true;
			}
			else
			{
				return super.isCellEditable(node, column);
			}
		}

		@Override
		public int getColumnCount()
		{
			return 2;
		}

		private static class CompanionNode extends DefaultTreeTableNode
		{

			private final CompanionFacade companion;

			public CompanionNode(CompanionFacade companion)
			{
				this.companion = companion;
			}

			@Override
			public Object getValueAt(int column)
			{
				if (column == 0)
				{
					return companion;
				}
				return null;
			}

			@Override
			public String toString()
			{
				return companion.getNameRef().get();
			}

		}

		private class CompanionTypeNode extends DefaultTreeTableNode implements ReferenceListener<String>
		{

			private final String type;

			public CompanionTypeNode(String type)
			{
				super(Arrays.asList(type, null));
				this.type = type;
			}

			@Override
			public Object getValueAt(int column)
			{
				if (column > 0)
				{
					Integer max = maxMap.getValue(type);
					if (max < 0)
					{
						return true;
					}
					return getChildCount() < max;
				}
				else
				{
					return super.getValueAt(column);
				}
			}

			@SuppressWarnings("nls")
			@Override
			public String toString()
			{
				Integer max = maxMap.getValue(type);
				String maxString = max == -1 ? "*" : max.toString();
				return type + " (" + getChildCount() + "/" + maxString + ")";
			}

			private void addCompanion(CompanionFacade companion, boolean silently)
			{
				companion.getNameRef().addReferenceListener(this);
				CompanionNode child = new CompanionNode(companion);
				if (children == null)
				{
					children = new Vector<>();
				}
				@SuppressWarnings("unchecked")
				int insertIndex = Collections.binarySearch(children, child, Comparators.toStringIgnoreCaseCollator());
				if (insertIndex < 0)
				{
					if (silently)
					{
						insert(child, -(insertIndex + 1));
					}
					else
					{
						insertNodeInto(child, this, -(insertIndex + 1));
					}
				}
				else
				{
					if (silently)
					{
						insert(child, insertIndex);
					}
					else
					{
						insertNodeInto(child, this, insertIndex);
					}
				}
				if (!silently)
				{
					nodeChanged(this);
				}
			}

			private void removeCompanion(CompanionFacade companion)
			{
				companion.getNameRef().removeReferenceListener(this);
				//we create a dummy child for comparison
				CompanionNode child = new CompanionNode(companion);
				@SuppressWarnings("unchecked")
				int index = Collections.binarySearch(children, child, Comparators.toStringIgnoreCaseCollator());
				removeNodeFromParent((CompanionNode) getChildAt(index));
				nodeChanged(this);
			}

			@Override
			@SuppressWarnings("unchecked")
			public void referenceChanged(ReferenceEvent<String> e)
			{
				children.sort(Comparators.toStringIgnoreCaseCollator());
				int[] indexes = new int[getChildCount()];
				Arrays.setAll(indexes, i -> i);
				nodesChanged(this, indexes);
			}

			@Override
			public void setParent(MutableTreeNode newParent)
			{
				super.setParent(newParent);
				if (newParent == null && children != null)
				{
					for (int i = 0; i < getChildCount(); i++)
					{
						CompanionNode child = (CompanionNode) getChildAt(i);
						child.companion.getNameRef().removeReferenceListener(this);
					}
				}
			}

		}

		class RootNode extends DefaultTreeTableNode
				implements MapListener<String, Integer>, ListListener<CompanionFacade>
		{

			private final List<String> types;
			private final ListFacade<? extends CompanionFacade> companions;

			public RootNode()
			{
				this.types = new ArrayList<>();
				this.companions = support.getCompanions();
				maxMap.addMapListener(this);
				companions.addListListener(this);
				initChildren();
			}

			private void initChildren()
			{
				types.clear();
				types.addAll(maxMap.getKeys());
				types.sort(Comparators.toStringIgnoreCaseCollator());
				removeAllChildren();
				for (String key : types)
				{
					CompanionTypeNode child = new CompanionTypeNode(key);
					add(child);
				}
				for (CompanionFacade companion : companions)
				{
					addCompanion(companion, true);
				}
			}

			private void addCompanion(CompanionFacade companion, boolean silently)
			{
				String type = companion.getCompanionType();
				int index = Collections.binarySearch(types, type, Comparators.toStringIgnoreCaseCollator());
				if (index < 0)
				{
					Logging.errorPrint(
						"Unable to add companion " + companion + " as the type " + type + " could not be found.");
					return;
				}
				CompanionTypeNode child = (CompanionTypeNode) getChildAt(index);
				child.addCompanion(companion, silently);
			}

			@Override
			public void keyAdded(MapEvent<String, Integer> e)
			{
				int insertIndex = Collections.binarySearch(types, e.getKey(), Comparators.toStringIgnoreCaseCollator());
				if (insertIndex < 0)
				{
					insertIndex = -(insertIndex + 1);
				}
				types.add(insertIndex, e.getKey());
				CompanionTypeNode child = new CompanionTypeNode(e.getKey());
				insertNodeInto(child, this, insertIndex);
			}

			@Override
			public void keyRemoved(MapEvent<String, Integer> e)
			{
				int index = types.indexOf(e.getKey());
				types.remove(index);
				removeNodeFromParent((MutableTreeNode) getChildAt(index));
			}

			@Override
			public void keyModified(MapEvent<String, Integer> e)
			{
				//ignore this
			}

			@Override
			public void valueChanged(MapEvent<String, Integer> e)
			{
				int index = types.indexOf(e.getKey());
				nodeChanged(getChildAt(index));
			}

			@Override
			public void valueModified(MapEvent<String, Integer> e)
			{
				//ignore this
			}

			@Override
			public void keysChanged(MapEvent<String, Integer> e)
			{
				initChildren();
				nodeStructureChanged(this);
			}

			@Override
			public void elementAdded(ListEvent<CompanionFacade> e)
			{
				addCompanion(e.getElement(), false);
			}

			@Override
			public void elementRemoved(ListEvent<CompanionFacade> e)
			{
				String type = e.getElement().getCompanionType();
				int index = Collections.binarySearch(types, type, Comparators.toStringIgnoreCaseCollator());
				CompanionTypeNode child = (CompanionTypeNode) getChildAt(index);
				child.removeCompanion(e.getElement());
			}

			@Override
			public void elementsChanged(ListEvent<CompanionFacade> e)
			{
				initChildren();
				nodeStructureChanged(this);
			}

			@Override
			public void elementModified(ListEvent<CompanionFacade> e)
			{
				//this is handled by the CompanionTypeNode
			}

		}

	}

	@Override
	public void adviseTodo(String fieldName)
	{
		CompanionsModel model = (CompanionsModel) companionsTable.getTreeTableModel();
		CompanionsModel.RootNode root = (CompanionsModel.RootNode) model.getRoot();
		for (int i = 0; i < root.getChildCount(); i++)
		{
			TreeNode node = root.getChildAt(i);
			if (node.toString().startsWith(fieldName))
			{
				companionsTable.getSelectionModel().setSelectionInterval(i, i);
				return;
			}
		}
	}

}
