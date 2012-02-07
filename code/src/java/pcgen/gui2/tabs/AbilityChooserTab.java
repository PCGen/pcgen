/*
 * AbilityChooserTab.java
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
 * Created on Jun 29, 2008, 10:30:57 PM
 */
package pcgen.gui2.tabs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.undo.StateEditable;

import pcgen.cdom.enumeration.Nature;
import pcgen.core.facade.AbilityCategoryFacade;
import pcgen.core.facade.AbilityFacade;
import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.util.DefaultListFacade;
import pcgen.core.facade.util.DelegatingListFacade;
import pcgen.core.facade.util.ListFacade;
import pcgen.gui2.UIPropertyContext;
import pcgen.gui2.filter.Filter;
import pcgen.gui2.filter.FilterBar;
import pcgen.gui2.filter.FilterButton;
import pcgen.gui2.filter.FilterHandler;
import pcgen.gui2.filter.FilterUtilities;
import pcgen.gui2.filter.FilteredTreeViewTable;
import pcgen.gui2.tabs.ability.AbilityTreeTableModel;
import pcgen.gui2.tabs.ability.AbilityTreeViews;
import pcgen.gui2.tabs.ability.CategoryTableModel;
import pcgen.gui2.tabs.models.QualifiedTreeCellRenderer;
import pcgen.gui2.tools.FlippingSplitPane;
import pcgen.gui2.tools.Icons;
import pcgen.gui2.tools.InfoPane;
import pcgen.gui2.util.JTreeTable;
import pcgen.gui2.util.treeview.DataView;
import pcgen.gui2.util.treeview.DataViewColumn;
import pcgen.gui2.util.treeview.DefaultDataViewColumn;
import pcgen.gui2.util.treeview.TreeView;
import pcgen.gui2.util.treeview.TreeViewModel;
import pcgen.system.LanguageBundle;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class AbilityChooserTab extends FlippingSplitPane implements StateEditable, TodoHandler
{

	private static final DataView<AbilityFacade> abilityDataView = new DataView<AbilityFacade>()
	{

		private final List<? extends DataViewColumn> dataColumns =
				Arrays.asList(new DefaultDataViewColumn("Type",
														String.class),
							  new DefaultDataViewColumn("Mult",
														Boolean.class),
							  new DefaultDataViewColumn("Stack",
														Boolean.class),
							  new DefaultDataViewColumn("Description",
														String.class),
							  new DefaultDataViewColumn("Source",
														String.class));

		public List<? extends DataViewColumn> getDataColumns()
		{
			return dataColumns;
		}

		public List<?> getData(AbilityFacade obj)
		{
			return Arrays.asList(getTypes(obj.getTypes()),
								 obj.isMult(),
								 obj.isStackable(),
								 obj.getDescription(),
								 obj.getSource());
		}

		private String getTypes(List<String> types)
		{
			if (types.isEmpty())
			{
				return "";
			}
			String ret = types.get(0);
			for (int x = 1; x < types.size(); x++)
			{
				ret += ", " + types.get(x);
			}
			return ret;
		}

	};
	private final FilteredTreeViewTable availableTreeViewPanel;
	private final JTreeTable selectedTreeViewPanel;
	private final JTable categoryTable;
	private final InfoPane infoPane;
	private final JButton addButton;
	private final JButton removeButton;
	private final FilterBar<CharacterFacade, AbilityCategoryFacade> categoryBar;

	public AbilityChooserTab()
	{
		this.availableTreeViewPanel = new FilteredTreeViewTable();
		this.selectedTreeViewPanel = new JTreeTable();
		this.categoryTable = new JTable();
		this.infoPane = new InfoPane();
		this.addButton = new JButton();
		this.removeButton = new JButton();
		this.categoryBar = new FilterBar();
		initComponents();
	}

	private void initComponents()
	{
		setOrientation(VERTICAL_SPLIT);
		availableTreeViewPanel.setDefaultRenderer(Boolean.class, new BooleanRenderer());
		JPanel availPanel = FilterUtilities.configureFilteredTreeViewPane(availableTreeViewPanel,
																		  FilterUtilities.createDefaultFilterBar());
		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalGlue());
		addButton.setHorizontalTextPosition(SwingConstants.LEADING);
		box.add(addButton);
		box.add(Box.createHorizontalStrut(5));
		box.setBorder(new EmptyBorder(0,  0, 5, 0));
		availPanel.add(box, BorderLayout.SOUTH);
		JPanel selPanel = new JPanel(new BorderLayout());
		selPanel.add(new JScrollPane(selectedTreeViewPanel), BorderLayout.CENTER);
		AbilityTreeTableModel.initializeTreeTable(selectedTreeViewPanel);

		box = Box.createHorizontalBox();
		box.add(Box.createHorizontalStrut(5));
		box.add(removeButton);
		box.add(Box.createHorizontalGlue());
		box.setBorder(new EmptyBorder(0,  0, 5, 0));
		selPanel.add(box, BorderLayout.SOUTH);
		FlippingSplitPane topPane = new FlippingSplitPane(JSplitPane.HORIZONTAL_SPLIT,
														  true,
														  availPanel,
														  selPanel);

		setTopComponent(topPane);

		FilterButton gainedFilterButton = new FilterButton();
		gainedFilterButton.setText("Gained");
		gainedFilterButton.setEnabled(true);
		gainedFilterButton.setSelected(true);
		gainedFilterButton.setFilter(new Filter<CharacterFacade, AbilityCategoryFacade>()
		{

			public boolean accept(CharacterFacade context, AbilityCategoryFacade element)
			{
				return context.getActiveAbilityCategories().containsElement(element);
			}

		});
		categoryBar.addDisplayableFilter(gainedFilterButton);

		JPanel filterPanel = new JPanel(new BorderLayout());
		filterPanel.add(categoryBar, BorderLayout.NORTH);
		filterPanel.add(new JScrollPane(categoryTable), BorderLayout.CENTER);

		FlippingSplitPane bottomPane = new FlippingSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		bottomPane.setLeftComponent(filterPanel);
		bottomPane.setRightComponent(infoPane);
		setBottomComponent(bottomPane);
	}

	private static final class BooleanRenderer extends DefaultTableCellRenderer
	{

		public BooleanRenderer()
		{
			setHorizontalAlignment(CENTER);
		}

		@Override
		protected void setValue(Object value)
		{
			if (value == Boolean.TRUE)
			{
				setText("Yes");
			}
			else if (value == Boolean.FALSE)
			{
				setText("No");
			}
			else
			{
				setText("");
			}
		}

	}

	private class AvailableAbilityTreeViewModel extends DelegatingListFacade<AbilityFacade>
			implements TreeViewModel<AbilityFacade>, ListSelectionListener
	{

		private final ListFacade<? extends TreeView<AbilityFacade>> treeviews;
		private final CharacterFacade character;
		private AbilityCategoryFacade category;
		private final ListFacade<AbilityCategoryFacade> categories;
		private final ListSelectionModel selectionModel;

		public AvailableAbilityTreeViewModel(CharacterFacade character,
											 ListFacade<AbilityCategoryFacade> categories,
											 ListSelectionModel selectionModel)
		{
			this.character = character;
			this.treeviews = new DefaultListFacade<TreeView<AbilityFacade>>(AbilityTreeViews.createTreeViewList(character));
			this.categories = categories;
			this.selectionModel = selectionModel;
			selectionModel.addListSelectionListener(this);
			this.setDelegate(new DefaultListFacade<AbilityFacade>());
		}

		public ListFacade<? extends TreeView<AbilityFacade>> getTreeViews()
		{
			return treeviews;
		}

		public int getDefaultTreeViewIndex()
		{
			return 0;
		}

		public DataView<AbilityFacade> getDataView()
		{
			return abilityDataView;
		}

		public ListFacade<AbilityFacade> getDataModel()
		{
			return this;
		}

		public void install()
		{
			availableTreeViewPanel.setTreeViewModel(this);
			selectedTreeViewPanel.getSelectionModel().addListSelectionListener(this);
		}

		public void uninstall()
		{
			selectedTreeViewPanel.getSelectionModel().removeListSelectionListener(this);
		}

		public void valueChanged(ListSelectionEvent e)
		{
			if (e.getValueIsAdjusting())
			{
				return;
			}
			if (e.getSource() == selectionModel)
			{
				int index = selectionModel.getMinSelectionIndex();
				if (index != -1)
				{
					setDelegate(character.getDataSet().getAbilities((AbilityCategoryFacade) categoryTable.getValueAt(index, 0)));
				}
			}
			else
			{
				int index = selectedTreeViewPanel.getSelectedRow();
				if (index != -1 && index < selectedTreeViewPanel.getRowCount())
				{
					Object data = selectedTreeViewPanel.getValueAt(index, 0);
					if (data instanceof AbilityCategoryFacade)
					{
						setDelegate(character.getDataSet().getAbilities((AbilityCategoryFacade) data));
						// Select the appropriate row in the category table
						for (int i = 0; i < categoryTable.getRowCount(); i++)
						{
							Object catData = categoryTable.getValueAt(i, 0);
							if (catData == data)
							{
								categoryTable.setRowSelectionInterval(i, i);
							}
						}
						
					}
				}
			}
		}

	}

	private class InfoHandler implements ListSelectionListener
	{

		private CharacterFacade character;
		private final ListFacade<AbilityCategoryFacade> categories;

		public InfoHandler(CharacterFacade character, ListFacade<AbilityCategoryFacade> categories)
		{
			this.character = character;
			this.categories = categories;
		}

		public void install()
		{
			availableTreeViewPanel.getSelectionModel().addListSelectionListener(this);
			selectedTreeViewPanel.getSelectionModel().addListSelectionListener(this);
			categoryTable.getSelectionModel().addListSelectionListener(this);
		}

		public void uninstall()
		{
			availableTreeViewPanel.getSelectionModel().removeListSelectionListener(this);
			selectedTreeViewPanel.getSelectionModel().removeListSelectionListener(this);
			categoryTable.getSelectionModel().removeListSelectionListener(this);
		}

		public void valueChanged(ListSelectionEvent e)
		{
			if (!e.getValueIsAdjusting())
			{
				Object data = null;
				if (e.getSource() == availableTreeViewPanel.getSelectionModel())
				{
					data = availableTreeViewPanel.getSelectedObject();
				}
				else if (e.getSource() == selectedTreeViewPanel.getSelectionModel())
				{
					int index = selectedTreeViewPanel.getSelectedRow();
					if (index != -1)
					{
						data = selectedTreeViewPanel.getModel().getValueAt(index, 0);
					}
				}
				else
				{
					int index = categoryTable.getSelectionModel().getMinSelectionIndex();
					if (index != -1)
					{
						data = categoryTable.getValueAt(index, 0);
					}
				}
				if (data != null)
				{
					if (data instanceof AbilityFacade)
					{
						infoPane.setText(character.getInfoFactory().getHTMLInfo((AbilityFacade) data));
					}
					if (data instanceof AbilityCategoryFacade)
					{
						infoPane.setTitle(((AbilityCategoryFacade) data).getName() + " Info");

					}
				}
				else
				{
					infoPane.setText("");
				}
			}
		}

	}
//
//	private final class AbilityTransferHandler extends TransferHandler
//	{
//
//		private CharacterFacade character;
//
//		public AbilityTransferHandler(CharacterFacade character)
//		{
//			this.character = character;
//		}
//
//		private final DataFlavor abilityFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType +
//				";class=" +
//				AbilityFacade.class.getName(),
//																null);
//
//		@Override
//		public int getSourceActions(JComponent c)
//		{
//			if (selectedAbility != null)
//			{
//				if (selectedTreeViewPanel.isAncestorOf(c))
//				{
//					return MOVE;
//				}
//				if (selectedAbility.isMult())
//				{
//					return COPY;
//				}
//				if (!character.hasAbility(selectedCatagory, selectedAbility))
//				{
//					return MOVE;
//				}
//			}
//			return NONE;
//		}
//
//		@Override
//		protected Transferable createTransferable(JComponent c)
//		{
//			final AbilityFacade transferAbility = selectedAbility;
//			return new Transferable()
//			{
//
//				public DataFlavor[] getTransferDataFlavors()
//				{
//					return new DataFlavor[]
//							{
//								abilityFlavor
//							};
//				}
//
//				public boolean isDataFlavorSupported(DataFlavor flavor)
//				{
//					return abilityFlavor == flavor;
//				}
//
//				public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
//				{
//					if (!isDataFlavorSupported(flavor))
//					{
//						throw new UnsupportedFlavorException(flavor);
//					}
//					return transferAbility;
//				}
//
//			};
//		}
//
//		@Override
//		public boolean canImport(JComponent comp, DataFlavor[] transferFlavors)
//		{
//			return transferFlavors[0] == abilityFlavor;
//		}
//
//		@Override
//		public boolean importData(JComponent comp, Transferable t)
//		{
//			if (selectedTreeViewPanel.isAncestorOf(comp))
//			{
//				try
//				{
//					AbilityFacade ability = (AbilityFacade) t.getTransferData(abilityFlavor);
//					// TODO: add some extra logic
//					character.addAbility(selectedCatagory, ability);
//					return true;
//				}
//				catch (UnsupportedFlavorException ex)
//				{
//					Logger.getLogger(AbilityChooserTab.class.getName()).log(Level.SEVERE,
//																			null,
//																			ex);
//				}
//				catch (IOException ex)
//				{
//					Logger.getLogger(AbilityChooserTab.class.getName()).log(Level.SEVERE,
//																			null,
//																			ex);
//				}
//				return false;
//			}
//			return true;
//		}
//
//		@Override
//		protected void exportDone(JComponent source, Transferable data,
//								  int action)
//		{
//			if (action == COPY)
//			{
//				return;
//			}
//		}
//
//	}

	private static final String SELECTED_TREEVIEW_PANEL_STATE = "SelectedTreeViewPanelState";
	private static final String AVAILABLE_TREEVIEW_PANEL_STATE = "AvailableTreeViewPanelState";

	public Hashtable<Object, Object> createState(CharacterFacade character,
												 ListFacade<AbilityCategoryFacade> categories,
												 ListFacade<AbilityCategoryFacade> fullCategoryList)
	{
		Hashtable<Object, Object> state = new Hashtable<Object, Object>();
		CategoryTableModel categoryTableModel = new CategoryTableModel(character, fullCategoryList, categoryBar);
		state.put(CategoryTableModel.class, categoryTableModel);

		ListSelectionModel listModel = new DefaultListSelectionModel();
		listModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		state.put(ListSelectionModel.class, listModel);
		state.put(AbilityTreeTableModel.class, new AbilityTreeTableModel(character, categories));
		state.put(AvailableAbilityTreeViewModel.class, new AvailableAbilityTreeViewModel(character, fullCategoryList, listModel));
		//state.put(AbilityTransferHandler.class, new AbilityTransferHandler(character));
		state.put(InfoHandler.class, new InfoHandler(character, categories));
		state.put(AbilityRenderer.class, new AbilityRenderer(character));
		state.put(QualifiedTreeCellRenderer.class, new QualifiedTreeCellRenderer(character));
		state.put(AddAction.class, new AddAction(character));
		state.put(RemoveAction.class, new RemoveAction(character));
		state.put(CategoryFilterHandler.class, new CategoryFilterHandler(categoryTableModel));
		return state;
	}

	public void storeState(Hashtable<Object, Object> state)
	{
		((InfoHandler) state.get(InfoHandler.class)).uninstall();
		((AvailableAbilityTreeViewModel) state.get(AvailableAbilityTreeViewModel.class)).uninstall();
		categoryTable.setSelectionModel(new DefaultListSelectionModel());
		((CategoryTableModel) state.get(CategoryTableModel.class)).uninstall();
		((AddAction) state.get(AddAction.class)).uninstall();
		((RemoveAction) state.get(RemoveAction.class)).uninstall();
	}

	public void restoreState(Hashtable<?, ?> state)
	{
		//AbilityTransferHandler handler = (AbilityTransferHandler) state.get(AbilityTransferHandler.class);

		categoryTable.setModel((CategoryTableModel) state.get(CategoryTableModel.class));
		categoryTable.setSelectionModel((ListSelectionModel) state.get(ListSelectionModel.class));
		//selectedTreeViewPanel.setTransferHandler(handler);
		//availableTreeViewPanel.setTransferHandler(handler);

		selectedTreeViewPanel.setTreeTableModel((AbilityTreeTableModel) state.get(AbilityTreeTableModel.class));
		((AvailableAbilityTreeViewModel) state.get(AvailableAbilityTreeViewModel.class)).install();
		selectedTreeViewPanel.setTreeCellRenderer((AbilityRenderer) state.get(AbilityRenderer.class));
		availableTreeViewPanel.setTreeCellRenderer((QualifiedTreeCellRenderer) state.get(QualifiedTreeCellRenderer.class));
		addButton.setAction((AddAction) state.get(AddAction.class));
		removeButton.setAction((RemoveAction) state.get(RemoveAction.class));
		((InfoHandler) state.get(InfoHandler.class)).install();
		((CategoryFilterHandler) state.get(CategoryFilterHandler.class)).install();
		((CategoryTableModel) state.get(CategoryTableModel.class)).install();
		((AddAction) state.get(AddAction.class)).install();
		((RemoveAction) state.get(RemoveAction.class)).install();
		
		ensureCategorySelected();
	}


	/**
	 * Ensure that when the tab is displayed a category is selected if any are available.
	 */
	private void ensureCategorySelected()
	{
		if (categoryTable.getSelectedRowCount() == 0)
		{
			CategoryTableModel model = (CategoryTableModel) categoryTable.getModel();
			model.refilter();
			if (model.getRowCount() > 0)
			{
				categoryTable.getSelectionModel().setSelectionInterval(0, 0);
			}
		}
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void adviseTodo(String fieldName)
	{
		CategoryTableModel model = (CategoryTableModel) categoryTable.getModel();
		model.refilter();
		for (int i = 0; i < model.getRowCount(); i++)
		{
			AbilityCategoryFacade category = model.getCategory(i);
			if (category.getName().equals(fieldName))
			{
				categoryTable.getSelectionModel().setSelectionInterval(i, i);
				return;
			}
			
		}
	}
	
	private class AddAction extends AbstractAction
	{

		private CharacterFacade character;

		public AddAction(CharacterFacade character)
		{
			super(LanguageBundle.getString("in_addSelected"));
			this.character = character;
			this.putValue(SMALL_ICON, Icons.Forward16.getImageIcon());
		}

		public void actionPerformed(ActionEvent e)
		{
			Object data = availableTreeViewPanel.getSelectedObject();
			int index = categoryTable.getSelectedRow();
			if (data != null && data instanceof AbilityFacade && index != -1)
			{
				AbilityCategoryFacade category = (AbilityCategoryFacade) categoryTable.getValueAt(index, 0);
				character.addAbility(category, (AbilityFacade) data);
			}
		}
		
		public void install()
		{
			availableTreeViewPanel.addActionListener(this);
		}
		
		public void uninstall()
		{
			availableTreeViewPanel.removeActionListener(this);
		}

	}

	private class RemoveAction extends AbstractAction
	{

		private CharacterFacade character;

		public RemoveAction(CharacterFacade character)
		{
			super(LanguageBundle.getString("in_removeSelected"));
			this.character = character;
			this.putValue(SMALL_ICON, Icons.Back16.getImageIcon());
		}

		public void actionPerformed(ActionEvent e)
		{
			int selectedRow = selectedTreeViewPanel.getSelectedRow();
			if (selectedRow == -1)
			{
				return;
			}
			Object data = selectedTreeViewPanel.getModel().getValueAt(selectedRow, 0);
			if (data != null && data instanceof AbilityFacade)
			{
				TreePath path = selectedTreeViewPanel.getTree().getPathForRow(selectedRow);
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getParentPath().getLastPathComponent();
				Object category = node.getUserObject();
				if (category instanceof AbilityCategoryFacade)
				{
					character.removeAbility((AbilityCategoryFacade) category, (AbilityFacade) data);
				}
			}
		}
		
		public void install()
		{
			selectedTreeViewPanel.addActionListener(this);
		}
		
		public void uninstall()
		{
			selectedTreeViewPanel.removeActionListener(this);
		}

	}

	private class CategoryFilterHandler implements FilterHandler
	{

		private final CategoryTableModel model;

		public CategoryFilterHandler(CategoryTableModel model)
		{
			this.model = model;
		}

		public void install()
		{
			categoryBar.setFilterHandler(this);
			refilter();
		}

		public void refilter()
		{
			model.refilter();
		}

		public void setSearchEnabled(boolean enable)
		{
			//do nothing as there is no search bar
		}

	}

	/**
	 * The Class <code>AbilityRenderer</code> displays the tree cells of the
	 * available and selected ability tables.  
	 */
	private class AbilityRenderer extends DefaultTreeCellRenderer
	{

		private CharacterFacade character;

		/**
		 * Create a new renderer for the ability names for a character. Will
		 * colour the ability names depending on if they are qualified or if 
		 * they are virtual or automatic in nature.
		 * @param character The character being displayed.
		 */
		public AbilityRenderer(CharacterFacade character)
		{
			this.character = character;
			setTextNonSelectionColor(UIPropertyContext.getQualifiedColor());
			setClosedIcon(null);
			setLeafIcon(null);
			setOpenIcon(null);
		}

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value,
													  boolean sel, boolean expanded, boolean leaf, int row,
													  boolean hasFocus)
		{

			super.getTreeCellRendererComponent(tree, value, sel, expanded,
											   leaf, row, hasFocus);
			Object abilityObj = ((DefaultMutableTreeNode) value).getUserObject();
			if (abilityObj instanceof AbilityFacade)
			{
				AbilityFacade ability = (AbilityFacade) abilityObj;
				if (!character.isQualifiedFor(ability))
				{
					setForeground(UIPropertyContext.getNotQualifiedColor());
				}
				else
				{
					Nature nature = character.getAbilityNature(ability);
					if (nature != null)
					{
						switch (nature)
						{
							case AUTOMATIC:
								setForeground(UIPropertyContext.getAutomaticColor());
								break;
							case VIRTUAL:
								setForeground(UIPropertyContext.getVirtualColor());
								break;
						}
					}

				}
			}
			return this;
		}

	}

}
