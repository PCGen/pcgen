/*
 * AbilityTreeTableModel.java
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
 * Created on Apr 11, 2011, 4:36:11 PM
 */
package pcgen.gui2.tabs.ability;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.MutableTreeNode;

import pcgen.core.facade.AbilityCategoryFacade;
import pcgen.core.facade.AbilityFacade;
import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.event.ListEvent;
import pcgen.core.facade.event.ListListener;
import pcgen.core.facade.util.ListFacade;
import pcgen.core.facade.util.SortedListFacade;
import pcgen.gui2.tabs.Utilities;
import pcgen.gui2.util.JTreeTable;
import pcgen.gui2.util.treetable.AbstractTreeTableModel;
import pcgen.gui2.util.treetable.DefaultTreeTableNode;
import pcgen.gui2.util.treetable.SortableTreeTableModel;
import pcgen.gui2.util.treetable.TreeTableNode;
import pcgen.util.Comparators;

/**
 * The Class <code>AbilityTreeTableModel</code> is a model for the 
 * selected abilities tree table. It lists the abilities held by the 
 * character in a tree structure by category.
 * 
 * <br/>
 * Last Editor: $Author:  $
 * Last Edited: $Date:  $
 * 
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 * @version $Revision: $
 */
public class AbilityTreeTableModel extends AbstractTreeTableModel implements SortableTreeTableModel
{

	private final CharacterFacade character;
	//private final ListFacade<AbilityCategoryFacade> categories;

	public AbilityTreeTableModel(CharacterFacade character, ListFacade<AbilityCategoryFacade> categories)
	{
		this.character = character;
		//this.categories = categories;
		this.setRoot(new RootTreeTableNode(categories));
	}

	/**
	 * Initialise a tree table that will be backed by an AbilityTreeTableModel.
	 * @param treeTable The tree table to be initialized.
	 */
	public static void initializeTreeTable(JTreeTable treeTable)
	{
		treeTable.getTree().putClientProperty("JTree.lineStyle", "Horizontal");
		treeTable.setAutoCreateColumnsFromModel(false);
		DefaultTableColumnModel model = new DefaultTableColumnModel();
		TableCellRenderer headerRenderer =  treeTable.getTableHeader().getDefaultRenderer();
		model.addColumn(Utilities.createTableColumn(0, "Selected Abilities", headerRenderer, true));
		treeTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		treeTable.setColumnModel(model);
		treeTable.getTableHeader().setResizingAllowed(false);
	}

	@Override
	public Class<?> getColumnClass(int column)
	{
		if (column == 0)
		{
			return TreeTableNode.class;
		}
		return Object.class;
	}

	@Override
	public String getColumnName(int column)
	{
		return "Selected Abilities";
	}

	@Override
	public int getColumnCount()
	{
		return 1;
	}

	@Override
	public void sortModel(Comparator<List<?>> comparator)
	{
	}

	private class RootTreeTableNode extends DefaultTreeTableNode implements ListListener<AbilityCategoryFacade>
	{

		private ListFacade<AbilityCategoryFacade> cats;

		public RootTreeTableNode(ListFacade<AbilityCategoryFacade> cats)
		{
			this.setValues(Collections.singletonList(new Object()));
			this.cats = new SortedListFacade<AbilityCategoryFacade>(Comparators.toStringIgnoreCaseComparator(),
																	cats);
			addChildren();
			cats.addListListener(this);
		}

		private void addChildren()
		{
			for (AbilityCategoryFacade category : cats)
			{
				add(new CategoryTreeTableNode(category));
			}
		}

		@Override
		public void elementAdded(ListEvent<AbilityCategoryFacade> e)
		{
			insertNodeInto(new CategoryTreeTableNode(e.getElement()), this, e.getIndex());
		}

		@Override
		public void elementRemoved(ListEvent<AbilityCategoryFacade> e)
		{
			removeNodeFromParent((MutableTreeNode) getChildAt(e.getIndex()));
		}

		@Override
		public void elementsChanged(ListEvent<AbilityCategoryFacade> e)
		{
			removeAllChildren();
			addChildren();
			AbilityTreeTableModel.this.nodeStructureChanged(this);
		}

		@Override
		public void elementModified(ListEvent<AbilityCategoryFacade> e)
		{
		}

	}

	private class CategoryTreeTableNode extends DefaultTreeTableNode implements ListListener<AbilityFacade>
	{

		private final AbilityCategoryFacade category;
		private ListFacade<AbilityFacade> abilities;

		public CategoryTreeTableNode(AbilityCategoryFacade category)
		{
			this.category = category;
			setUserObject(category);
			setValues(Collections.singletonList(category));
			this.abilities = new SortedListFacade<AbilityFacade>(Comparators.toStringIgnoreCaseComparator(),
																 character.getAbilities(category));
			addChildren();
			abilities.addListListener(this);
		}

		private void addChildren()
		{
			for (AbilityFacade ability : abilities)
			{
				DefaultTreeTableNode node = new DefaultTreeTableNode(Collections.singletonList(ability));
				node.setUserObject(ability);
				add(node);
			}
		}

		@Override
		public void elementAdded(ListEvent<AbilityFacade> e)
		{
			DefaultTreeTableNode node = new DefaultTreeTableNode(Collections.singletonList(e.getElement()));
			node.setUserObject(e.getElement());
			insertNodeInto(node, this, e.getIndex());
		}

		@Override
		public void elementRemoved(ListEvent<AbilityFacade> e)
		{
			removeNodeFromParent((MutableTreeNode) getChildAt(e.getIndex()));
		}

		@Override
		public void elementsChanged(ListEvent<AbilityFacade> e)
		{
			removeAllChildren();
			addChildren();
			AbilityTreeTableModel.this.nodeStructureChanged(this);
		}

		@Override
		public void elementModified(ListEvent<AbilityFacade> e)
		{
		}

	}

}
