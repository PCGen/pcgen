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
package pcgen.gui2.tabs.equip;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.EquipmentSetFacade;
import pcgen.facade.core.EquipmentSetFacade.EquipmentTreeEvent;
import pcgen.facade.core.EquipmentSetFacade.EquipmentTreeListener;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.event.ListEvent;
import pcgen.facade.util.event.ListListener;
import pcgen.gui2.facade.EquipNode;
import pcgen.gui2.util.treetable.TreeTableModel;
import pcgen.gui2.util.treetable.TreeTableNode;
import pcgen.util.CollectionMaps;
import pcgen.util.ListMap;

/**
 * The model backing the selected table on the equipping tab. This controls the 
 * tree structure showing the equipment by its equipped location for a 
 * particular character and equipment set.
 * 
 *  
 */
public class EquipmentTreeTableModel implements TreeTableModel, ListListener<EquipNode>, EquipmentTreeListener
{
	private final EventListenerList listenerList = new EventListenerList();
	private final CharacterFacade character;
	private final EquipmentSetFacade equipSet;
	private final Object root = new Object();
	private final ListMap<EquipNode, EquipNode, List<EquipNode>> pathMap;
	private final List<EquipNode> bodySlotNodes;

	public EquipmentTreeTableModel(CharacterFacade character, EquipmentSetFacade equipSet)
	{
		this.character = character;
		this.equipSet = equipSet;
		pathMap = CollectionMaps.createListMap(HashMap.class, ArrayList.class);
		bodySlotNodes = new ArrayList<>();
		initPathMap();
		equipSet.getNodes().addListListener(this);
		equipSet.addEquipmentTreeListener(this);
	}

	private void initPathMap()
	{
		ListFacade<EquipNode> equipNodes = equipSet.getNodes();
		for (EquipNode equipNode : equipNodes)
		{
			EquipNode parent = equipNode.getParent();
			while (parent != null && !pathMap.containsValue(parent, equipNode))
			{
				addNode(parent, equipNode);

				equipNode = parent;
				parent = equipNode.getParent();
			}
			if (parent == null && !bodySlotNodes.contains(equipNode))
			{
				addBodyNode(equipNode);
			}
		}
	}

	@Override
	public boolean isCellEditable(Object node, int column)
	{
		return column == 0;
	}

	@Override
	public Class<?> getColumnClass(int column)
	{
		return switch (column)
				{
					case 0 -> TreeTableNode.class;
					case 1, 2 -> String.class;
					case 3 -> Integer.class;
					case 4 -> Float.class;
					default -> Object.class;
				};
	}

	@Override
	public int getColumnCount()
	{
		return 5;
	}

	@Override
	public String getColumnName(int column)
	{
		return null;
	}

	@Override
	public void setValueAt(Object aValue, Object node, int column)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Object getValueAt(Object node, int column)
	{
		EquipNode pathNode = (EquipNode) node;
		if (column == 0)
		{
			return pathNode;
		}
		switch (pathNode.getNodeType())
		{
			case BODY_SLOT:
				switch (column)
				{
					case 1:
						return "Type";
					case 2:
						return "Located";
					case 3:
						return "Qty";
					case 4:
						return "Wgt";
					default:
						//Case not caught, should this cause an error?
						break;
				}
			case PHANTOM_SLOT:
				if (column == 2)
				{
					return equipSet.getLocation(pathNode);
				}
				return null;
			case EQUIPMENT:
				switch (column)
				{
					case 1:
						return pathNode.getEquipment().getTypes()[0];
					case 2:
						return equipSet.getLocation(pathNode);
					case 3:
						return equipSet.getQuantity(pathNode);
					case 4:
						return character.getInfoFactory().getWeight(pathNode.getEquipment());
					default:
						//Case not caught, should this cause an error?
						break;
				}
			default:
				return null;
		}
	}

	@Override
	public Object getRoot()
	{
		return root;
	}

	@Override
	public Object getChild(Object parent, int index)
	{
		if (parent == root)
		{
			return bodySlotNodes.get(index);
		}
		else
		{
			return pathMap.get(parent, index);
		}
	}

	@Override
	public int getChildCount(Object parent)
	{
		if (parent == root)
		{
			return bodySlotNodes.size();
		}
		else
		{
			return pathMap.size(parent);
		}
	}

	@Override
	public boolean isLeaf(Object node)
	{
		if (root == node)
		{
			return false;
		}
		return !pathMap.containsKey(node);
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int getIndexOfChild(Object parent, Object child)
	{
		if (parent == root)
		{
			EquipNode path = (EquipNode) child;
			return bodySlotNodes.indexOf(path);
		}
		else
		{
			return pathMap.indexOf(parent, child);
		}
	}

	@Override
	public void addTreeModelListener(TreeModelListener l)
	{
		listenerList.add(TreeModelListener.class, l);
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l)
	{
		listenerList.remove(TreeModelListener.class, l);
	}

	private void addBodyNode(EquipNode bodyNode)
	{
		int insertion_index = Collections.binarySearch(bodySlotNodes, bodyNode);
		bodySlotNodes.add(-(insertion_index + 1), bodyNode);
	}

	private int addNode(EquipNode parent, EquipNode child)
	{
		List<EquipNode> children = pathMap.get(parent);
		if (children == null)
		{
			children = Collections.emptyList();
		}
		int insertion_index = 1 + Collections.binarySearch(children, child);
		if (insertion_index < 0)
		{
			// The item wasn't already in the list so the search gave us a negative index of where to add the item. 
			insertion_index *= -1;
		}
		pathMap.add(parent, insertion_index, child);
		return insertion_index;
	}

	@Override
	public void elementAdded(ListEvent<EquipNode> e)
	{
		EquipNode child = e.getElement();
		EquipNode parent = child.getParent();

		int index = addNode(parent, child);
		fireTreeNodesInserted(this, getPathToRoot(parent), new int[]{index}, new Object[]{child});
	}

	@Override
	public void elementRemoved(ListEvent<EquipNode> e)
	{
		EquipNode child = e.getElement();
		EquipNode parent = child.getParent();

		List<EquipNode> children = pathMap.get(parent);

		int index = children.indexOf(child);

		pathMap.remove(parent, index);
		fireTreeNodesRemoved(this, getPathToRoot(parent), new int[]{index}, new Object[]{child});

	}

	@Override
	public void elementsChanged(ListEvent<EquipNode> e)
	{
		pathMap.clear();
		initPathMap();
		fireTreeStructureChanged(this, new Object[]{root}, null, null);
	}

	@Override
	public void elementModified(ListEvent<EquipNode> e)
	{
	}

	@Override
	public void quantityChanged(EquipmentTreeEvent e)
	{
		EquipNode child = e.getNode();
		EquipNode parent = child.getParent();
		List<EquipNode> children = pathMap.get(parent);

		int index = Collections.binarySearch(children, child);
		fireTreeNodesChanged(this, getPathToRoot(parent), new int[]{index}, new Object[]{child});
	}

	private Object[] getPathToRoot(EquipNode node)
	{
		return getPathToRoot(node, 0);
	}

	/**
	 * Builds the parents of node up to and including the root node,
	 * where the original node is the last element in the returned array.
	 * The length of the returned array gives the node's depth in the
	 * tree.
	 *
	 * @param aNode  the TreeNode to get the path for
	 * @param depth  an int giving the number of steps already taken towards
	 *        the root (on recursive calls), used to size the returned array
	 * @return an array of TreeNodes giving the path from the root to the
	 *         specified node
	 */
	private Object[] getPathToRoot(EquipNode aNode, int depth)
	{
		Object[] retNodes;
		// This method recurses, traversing towards the root in order
		// size the array. On the way back, it fills in the nodes,
		// starting from the root and working back to the original node.

		/* Check for null, in case someone passed in a null node, or
		they passed in an element that isn't rooted at root. */
		if (aNode == null)
		{
			if (depth == 0)
			{
				return null;
			}
			else
			{
				retNodes = new Object[depth + 1];
				retNodes[0] = root;
			}
		}
		else
		{
			depth++;
			retNodes = getPathToRoot(aNode.getParent(), depth);
			retNodes[retNodes.length - depth] = aNode;
		}
		return retNodes;
	}

	/**
	 * Notifies all listeners that have registered interest for
	 * notification on this event type.  The event instance
	 * is lazily created using the parameters passed into
	 * the fire method.
	 *
	 * @param source the node being changed
	 * @param path the path to the root node
	 * @param childIndices the indices of the changed elements
	 * @param children the changed elements
	 */
	private void fireTreeNodesChanged(Object source, Object[] path, int[] childIndices, Object[] children)
	{
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == TreeModelListener.class)
			{
				// Lazily create the event:
				if (e == null)
				{
					e = new TreeModelEvent(source, path, childIndices, children);
				}
				((TreeModelListener) listeners[i + 1]).treeNodesChanged(e);
			}
		}
	}

	/**
	 * Notifies all listeners that have registered interest for
	 * notification on this event type.  The event instance
	 * is lazily created using the parameters passed into
	 * the fire method.
	 *
	 * @param source the node where new elements are being inserted
	 * @param path the path to the root node
	 * @param childIndices the indices of the new elements
	 * @param children the new elements
	 */
	protected void fireTreeNodesInserted(Object source, Object[] path, int[] childIndices, Object[] children)
	{
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == TreeModelListener.class)
			{
				// Lazily create the event:
				if (e == null)
				{
					e = new TreeModelEvent(source, path, childIndices, children);
				}
				((TreeModelListener) listeners[i + 1]).treeNodesInserted(e);
			}
		}
	}

	/**
	 * Notifies all listeners that have registered interest for
	 * notification on this event type.  The event instance
	 * is lazily created using the parameters passed into
	 * the fire method.
	 *
	 * @param source the node where elements are being removed
	 * @param path the path to the root node
	 * @param childIndices the indices of the removed elements
	 * @param children the removed elements
	 */
	private void fireTreeNodesRemoved(Object source, Object[] path, int[] childIndices, Object[] children)
	{
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == TreeModelListener.class)
			{
				// Lazily create the event:
				if (e == null)
				{
					e = new TreeModelEvent(source, path, childIndices, children);
				}
				((TreeModelListener) listeners[i + 1]).treeNodesRemoved(e);
			}
		}
	}

	/**
	 * Notifies all listeners that have registered interest for
	 * notification on this event type.  The event instance
	 * is lazily created using the parameters passed into
	 * the fire method.
	 *
	 * @param source the node where the tree model has changed
	 * @param path the path to the root node
	 * @param childIndices the indices of the affected elements
	 * @param children the affected elements
	 */
	private void fireTreeStructureChanged(Object source, Object[] path, int[] childIndices, Object[] children)
	{
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == TreeModelListener.class)
			{
				// Lazily create the event:
				if (e == null)
				{
					e = new TreeModelEvent(source, path, childIndices, children);
				}
				((TreeModelListener) listeners[i + 1]).treeStructureChanged(e);
			}
		}
	}

}
