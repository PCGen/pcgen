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
 *
 * Created on Mar 4, 2012, 5:01:02 PM
 */
package pcgen.gui2.tabs;

import java.awt.Component;
import java.util.*;
import javax.swing.AbstractCellEditor;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import pcgen.base.util.HashMapToList;
import pcgen.base.util.MapToList;
import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.CompanionFacade;
import pcgen.core.facade.CompanionSupportFacade;
import pcgen.core.facade.event.*;
import pcgen.core.facade.util.DefaultListFacade;
import pcgen.core.facade.util.ListFacade;
import pcgen.core.facade.util.MapFacade;
import pcgen.gui2.tools.FlippingSplitPane;
import pcgen.gui2.util.JTreeTable;
import pcgen.gui2.util.JTreeViewTable;
import pcgen.gui2.util.treetable.AbstractTreeTableModel;
import pcgen.gui2.util.treetable.DefaultTreeTableNode;
import pcgen.gui2.util.treetable.SortableTreeTableModel;
import pcgen.gui2.util.treetable.TreeTableNode;
import pcgen.gui2.util.treeview.DataView;
import pcgen.gui2.util.treeview.DataViewColumn;
import pcgen.gui2.util.treeview.DefaultDataViewColumn;
import pcgen.gui2.util.treeview.TreeView;
import pcgen.gui2.util.treeview.TreeViewModel;
import pcgen.gui2.util.treeview.TreeViewPath;
import pcgen.util.Comparators;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class CompanionInfoTab extends FlippingSplitPane implements CharacterInfoTab
{

	private final JTreeTable companionsTable;
	private final JEditorPane infoPane;

	public CompanionInfoTab()
	{
		this.companionsTable = new JTreeTable();
		this.infoPane = new JEditorPane();
		initComponents();
	}

	private void initComponents()
	{
		setLeftComponent(new JScrollPane(companionsTable));
		setRightComponent(new JScrollPane(infoPane));
	}

	@Override
	public Hashtable<Object, Object> createModels(CharacterFacade character)
	{
		Hashtable<Object, Object> state = new Hashtable<Object, Object>();
		state.put(CompanionsModel.class, new CompanionsModel(character));
		return state;
	}

	@Override
	public void restoreModels(Hashtable<?, ?> state)
	{
		companionsTable.setTreeTableModel((CompanionsModel) state.get(CompanionsModel.class));
	}

	@Override
	public void storeModels(Hashtable<Object, Object> state)
	{
	}

	@Override
	public TabTitle getTabTitle()
	{
		return new TabTitle("Companions");
	}

	private class ButtonCellEditor extends AbstractCellEditor implements TableCellEditor
	{

		@Override
		public Object getCellEditorValue()
		{
			return null;
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
		{
			throw new UnsupportedOperationException("Not supported yet.");
		}

	}

	private class CompanionsModel extends AbstractTreeTableModel
	{

		private CompanionSupportFacade support;
		private MapFacade<String, Integer> maxMap;

		public CompanionsModel(CharacterFacade character)
		{
			this.support = character.getCompanionSupport();
			this.maxMap = support.getMaxCompanionsMap();
			this.setRoot(new RootNode());
		}

		@Override
		public int getColumnCount()
		{
			return 2;
		}

		private class CompanionNode extends DefaultTreeTableNode
		{

			private CompanionFacade companion;

			public CompanionNode(CompanionFacade companion)
			{
				this.companion = companion;
			}

			@Override
			public Object getValueAt(int column)
			{
				if (column == 0)
				{
					return companion.getNameRef().getReference();
				}
				return null;
			}

			@Override
			public String toString()
			{
				return companion.getNameRef().getReference();
			}

		}

		private class CompanionTypeNode extends DefaultTreeTableNode implements ReferenceListener<String>
		{

			private String type;

			public CompanionTypeNode(String type)
			{
				super(Arrays.asList(type, null));
				this.type = type;
			}

			@Override
			public String toString()
			{
				Integer max = maxMap.get(type);
				String maxString = max == -1 ? "*" : max.toString();
				return type + " (" + getChildCount() + "/" + maxString;
			}

			private void addCompanion(CompanionFacade companion, boolean silently)
			{
				companion.getNameRef().addReferenceListener(this);
				CompanionNode child = new CompanionNode(companion);
				if (children == null)
				{
					children = new Vector();
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
				Collections.sort(children, Comparators.toStringIgnoreCaseCollator());
				int[] indexes = new int[getChildCount()];
				for (int i = 0; i < indexes.length; i++)
				{
					indexes[i] = i;
				}
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

		private class RootNode extends DefaultTreeTableNode implements MapListener<String, Integer>, ListListener<CompanionFacade>
		{

			private List<String> types;
			private ListFacade<CompanionFacade> companions;

			public RootNode()
			{
				this.types = new ArrayList<String>();
				this.companions = support.getCompanions();
				maxMap.addMapListener(this);
				companions.addListListener(this);
				initChildren();
			}

			private void initChildren()
			{
				types.clear();
				types.addAll(maxMap.getKeys());
				Collections.sort(types, Comparators.toStringIgnoreCaseCollator());
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
				CompanionTypeNode child = (CompanionTypeNode) getChildAt(index);
				child.addCompanion(companion, silently);
			}

			@Override
			public void keyAdded(MapEvent<String, Integer> e)
			{
				@SuppressWarnings("unchecked")
				int insertIndex = Collections.binarySearch(types, e.getKey(), Comparators.toStringIgnoreCaseCollator());
				types.add(-(insertIndex + 1), e.getKey());
				CompanionTypeNode child = new CompanionTypeNode(e.getKey());
				insertNodeInto(child, this, -(insertIndex + 1));
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

}
