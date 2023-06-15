/*
 * Copyright 2011 Connor Petty <cpmeister@users.sourceforge.net>
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
package pcgen.gui2.tabs.spells;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import pcgen.core.PCClass;
import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.SpellFacade;
import pcgen.facade.core.SpellSupportFacade;
import pcgen.facade.core.SpellSupportFacade.RootNode;
import pcgen.facade.core.SpellSupportFacade.SpellNode;
import pcgen.facade.core.SpellSupportFacade.SuperNode;
import pcgen.facade.util.ListFacade;
import pcgen.gui2.filter.Filter;
import pcgen.gui2.filter.FilterBar;
import pcgen.gui2.filter.FilterButton;
import pcgen.gui2.filter.FilterUtilities;
import pcgen.gui2.filter.FilteredTreeViewTable;
import pcgen.gui2.filter.SearchFilterPanel;
import pcgen.gui2.tabs.CharacterInfoTab;
import pcgen.gui2.tabs.TabTitle;
import pcgen.gui2.tabs.models.CharacterComboBoxModel;
import pcgen.gui2.tools.FlippingSplitPane;
import pcgen.gui2.tools.Icons;
import pcgen.gui2.tools.InfoPane;
import pcgen.gui2.util.JTreeViewTable;
import pcgen.gui2.util.table.SortableTableModel;
import pcgen.gui2.util.table.SortableTableRowSorter;
import pcgen.gui2.util.treeview.TreeViewModel;
import pcgen.system.LanguageBundle;
import pcgen.util.enumeration.Tab;

import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("serial")
public class SpellBooksTab extends FlippingSplitPane implements CharacterInfoTab
{

	private final TabTitle tabTitle = new TabTitle(Tab.SPELLBOOKS);
	private final FilteredTreeViewTable<CharacterFacade, SuperNode> availableTable;
	private final JTreeViewTable<SuperNode> selectedTable;
	private final QualifiedSpellTreeCellRenderer spellRenderer;
	private final JButton addButton;
	private final JButton removeButton;
	private final FilterButton<CharacterFacade, SuperNode> qFilterButton;
	private final InfoPane spellsPane;
	private final InfoPane classPane;
	private final JComboBox defaultBookCombo;

	public SpellBooksTab()
	{
		this.availableTable = new FilteredTreeViewTable<>();
		this.selectedTable = new JTreeViewTable<>()
		{

			@Override
			public void setTreeViewModel(TreeViewModel<SuperNode> viewModel)
			{
				super.setTreeViewModel(viewModel);
				sortModel();
			}

		};
		this.spellRenderer = new QualifiedSpellTreeCellRenderer();
		this.addButton = new JButton();
		this.removeButton = new JButton();
		this.qFilterButton = new FilterButton<>("SpellBooksQualified");
		this.spellsPane = new InfoPane(LanguageBundle.getString("InfoSpells.spell.info"));
		this.classPane = new InfoPane(LanguageBundle.getString("InfoSpells.class.info"));
		this.defaultBookCombo = new JComboBox();
		initComponents();
	}

	private void initComponents()
	{
		availableTable.setTreeCellRenderer(spellRenderer);
		selectedTable.setTreeCellRenderer(spellRenderer);
		selectedTable.setRowSorter(new SortableTableRowSorter()
		{

			@Override
			public SortableTableModel getModel()
			{
				return (SortableTableModel) selectedTable.getModel();
			}

		});
		selectedTable.getRowSorter().toggleSortOrder(0);
		FilterBar<CharacterFacade, SuperNode> filterBar = new FilterBar<>();
		filterBar.addDisplayableFilter(new SearchFilterPanel());
		qFilterButton.setText(LanguageBundle.getString("in_igQualFilter")); //$NON-NLS-1$
		filterBar.addDisplayableFilter(qFilterButton);

		FlippingSplitPane upperPane = new FlippingSplitPane();
		JPanel availPanel = FilterUtilities.configureFilteredTreeViewPane(availableTable, filterBar);
		{
			JPanel bottomPanel = new JPanel(new BorderLayout());
			bottomPanel.add(new JLabel(LanguageBundle.getString("InfoSpells.set.auto.book")), BorderLayout.WEST);
			bottomPanel.add(defaultBookCombo, BorderLayout.CENTER);
			bottomPanel.add(addButton, BorderLayout.EAST);
			availPanel.add(bottomPanel, BorderLayout.SOUTH);
		}
		upperPane.setLeftComponent(availPanel);

		JPanel rightPanel = new JPanel(new BorderLayout());
		rightPanel.add(new JScrollPane(selectedTable), BorderLayout.CENTER);
		{
			Box hbox = Box.createHorizontalBox();
			hbox.add(Box.createHorizontalStrut(5));
			hbox.add(removeButton);
			hbox.add(Box.createHorizontalGlue());
			rightPanel.add(hbox, BorderLayout.SOUTH);
		}
		upperPane.setRightComponent(rightPanel);
		upperPane.setResizeWeight(0);
		setTopComponent(upperPane);

		FlippingSplitPane bottomPane = new FlippingSplitPane();
		bottomPane.setLeftComponent(spellsPane);
		bottomPane.setRightComponent(classPane);
		setBottomComponent(bottomPane);
		setOrientation(VERTICAL_SPLIT);
	}

	@Override
	public ModelMap createModels(final CharacterFacade character)
	{
		ModelMap models = new ModelMap();
		models.put(TreeViewModelHandler.class, new TreeViewModelHandler(character));
		models.put(AddSpellAction.class, new AddSpellAction(character));
		models.put(RemoveSpellAction.class, new RemoveSpellAction(character));
		models.put(SpellInfoHandler.class, new SpellInfoHandler(character, availableTable, selectedTable, spellsPane));
		models.put(ClassInfoHandler.class, new ClassInfoHandler(character, availableTable, selectedTable, classPane));
		models.put(SpellBookModel.class, new SpellBookModel(character));
		models.put(SpellFilterHandler.class, new SpellFilterHandler(character));
		return models;
	}

	@Override
	public void restoreModels(ModelMap models)
	{
		models.get(SpellFilterHandler.class).install();
		models.get(TreeViewModelHandler.class).install();
		models.get(SpellInfoHandler.class).install();
		models.get(ClassInfoHandler.class).install();
		models.get(AddSpellAction.class).install();
		models.get(RemoveSpellAction.class).install();
		defaultBookCombo.setModel(models.get(SpellBookModel.class));
	}

	@Override
	public void storeModels(ModelMap models)
	{
		models.get(SpellInfoHandler.class).uninstall();
		models.get(ClassInfoHandler.class).uninstall();
		models.get(AddSpellAction.class).uninstall();
		models.get(RemoveSpellAction.class).uninstall();
		models.get(TreeViewModelHandler.class).uninstall();
	}

	@Override
	public TabTitle getTabTitle()
	{
		return tabTitle;
	}

	/**
	 * Identify the current spell book, being the spell book that spells should
	 * be added to. If no books exist then return an empty string.
	 *
	 * @return The name of the 'current' spell book, or empty string if none
	 *         exist.
	 */
	String getCurrentSpellBookName()
	{
		String spellList = "";
		Object selectedObject = selectedTable.getSelectedObject();
		if (selectedObject != null)
		{
			if (selectedObject instanceof SpellNode)
			{
				spellList = ((SpellNode) selectedObject).getRootNode().getName();
			}
			else if (selectedObject instanceof RootNode)
			{
				spellList = ((RootNode) selectedObject).getName();
			}
			else
			{
				JTree tree = selectedTable.getTree();
				TreePath path = tree.getSelectionPath();
				while (path.getParentPath() != null && (path.getParentPath().getParentPath() != null))
				{
					path = path.getParentPath();
				}
				spellList = path.getLastPathComponent().toString();
			}
		}
		if (StringUtils.isEmpty(spellList))
		{
			ListFacade<?> data = selectedTable.getTreeViewModel().getDataModel();
			if (!data.isEmpty())
			{
				Object firstElem = data.getElementAt(0);
				if (firstElem instanceof SpellNode)
				{
					spellList = ((SpellNode) firstElem).getRootNode().getName();
				}
			}
		}
		return spellList;
	}

	private static class SpellBookModel extends CharacterComboBoxModel<String>
	{

		private final SpellSupportFacade spellSupport;

		public SpellBookModel(CharacterFacade character)
		{
			this.spellSupport = character.getSpellSupport();
			setListFacade(spellSupport.getSpellbooks());
			setReference(spellSupport.getDefaultSpellBookRef());
		}

		@Override
		public void setSelectedItem(Object anItem)
		{
			spellSupport.setDefaultSpellBook((String) anItem);
		}
	}

	private class AddSpellAction extends AbstractAction
	{

		private final CharacterFacade character;

		public AddSpellAction(CharacterFacade character)
		{
			this.character = character;
			putValue(SMALL_ICON, Icons.Forward16.getImageIcon());
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			List<?> data = availableTable.getSelectedData();
			String bookname = getCurrentSpellBookName();
			for (Object object : data)
			{
				if (object instanceof SpellNode)
				{
					character.getSpellSupport().addToSpellBook((SpellNode) object, bookname);
				}
			}
		}

		public void install()
		{
			availableTable.addActionListener(this);
			addButton.setAction(this);
		}

		public void uninstall()
		{
			availableTable.removeActionListener(this);
		}

	}

	private class RemoveSpellAction extends AbstractAction
	{

		private final CharacterFacade character;

		public RemoveSpellAction(CharacterFacade character)
		{
			this.character = character;
			putValue(SMALL_ICON, Icons.Back16.getImageIcon());
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			List<?> data = selectedTable.getSelectedData();
			for (Object object : data)
			{
				if (object instanceof SpellNode node)
				{
					character.getSpellSupport().removeFromSpellBook(node, node.getRootNode().getName());
				}
			}
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

	private class TreeViewModelHandler
	{

		private final SpellTreeViewModel availableModel;
		private final SpellTreeViewModel selectedModel;
		private final CharacterFacade character;

		public TreeViewModelHandler(CharacterFacade character)
		{
			this.character = character;
			availableModel = new SpellTreeViewModel(character.getSpellSupport().getKnownSpellNodes(), false,
				"SpellBooksAva", character.getInfoFactory());
			selectedModel = new SpellTreeViewModel(character.getSpellSupport().getBookSpellNodes(), true,
				"SpellBooksSel", character.getInfoFactory());
		}

		public void install()
		{
			spellRenderer.setCharacter(character);
			availableTable.setTreeViewModel(availableModel);
			selectedTable.setTreeViewModel(selectedModel);
		}

		public void uninstall()
		{
			spellRenderer.setCharacter(null);
		}

	}

	private class SpellFilterHandler implements Filter<CharacterFacade, SuperNode>
	{

		private final CharacterFacade character;

		public SpellFilterHandler(CharacterFacade character)
		{
			this.character = character;
		}

		public void install()
		{
			qFilterButton.setFilter(this);
		}

		@Override
		public boolean accept(CharacterFacade context, SuperNode element)
		{
			if (element instanceof SpellNode spellNode)
			{
				SpellFacade spell = spellNode.getSpell();
				PCClass pcClass = spellNode.getSpellcastingClass();
				return character.isQualifiedFor(spell, pcClass);
			}
			return true;
		}

	}

}
