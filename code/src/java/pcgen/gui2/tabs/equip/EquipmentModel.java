/*
 * EquipmentModel.java
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
 * Created on Jul 7, 2010, 3:18:39 PM
 */
package pcgen.gui2.tabs.equip;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.EquipmentFacade;
import pcgen.core.facade.EquipmentSetFacade;
import pcgen.core.facade.EquipmentSetFacade.EquipNode;
import pcgen.core.facade.EquipmentSetFacade.EquipNode.NodeType;
import pcgen.core.facade.event.ListEvent;
import pcgen.core.facade.event.ListListener;
import pcgen.core.facade.event.ReferenceEvent;
import pcgen.core.facade.event.ReferenceListener;
import pcgen.core.facade.util.ListFacade;
import pcgen.gui2.UIPropertyContext;
import pcgen.gui2.util.JTreeTable;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class EquipmentModel implements ListListener<EquipmentSetFacade>, ReferenceListener<EquipmentSetFacade>,
		TableModelListener
{

	private static Font largeFont = new Font("Verdana", Font.BOLD, 20);
	private static Font headerFont = new Font("Verdana", Font.BOLD, 12);
	private static Font normFont = new Font("Verdana", Font.PLAIN, 12);
	private static Font italicFont = new Font("Verdana", Font.ITALIC, 12);
	private final CharacterFacade character;
	private TreeCellRenderer treeRenderer = new TreeRenderer();
	private Map<EquipmentSetFacade, EquipmentTreeTableModel> equipsetMap;
	private ListFacade<EquipmentSetFacade> equipsets;
	private EquipmentTreeTableModel selectedModel;
	private JTreeTable treeTable;

	public EquipmentModel(CharacterFacade character)
	{
		this.character = character;

		equipsetMap = new HashMap<EquipmentSetFacade, EquipmentTreeTableModel>();
		equipsets = character.getEquipmentSets();
		for (EquipmentSetFacade equipset : equipsets)
		{
			equipsetMap.put(equipset, new EquipmentTreeTableModel(character, equipset));
		}
		equipsets.addListListener(this);
	}

	public static void initializeTreeTable(JTreeTable treeTable)
	{
		treeTable.getTree().setRowHeight(0);
		treeTable.setFocusable(false);
		treeTable.getTree().putClientProperty("JTree.lineStyle", "Horizontal");
		treeTable.setAutoCreateColumnsFromModel(false);
		{
			DefaultTableColumnModel model = new DefaultTableColumnModel();
			CellRenderer renderer = new CellRenderer();
			TableColumn column = new TableColumn(0);
			column.setResizable(true);
			model.addColumn(column);
			model.addColumn(createFixedColumn(1, 75, renderer));
			model.addColumn(createFixedColumn(2, 75, renderer));
			model.addColumn(createFixedColumn(3, 50, renderer));
			model.addColumn(createFixedColumn(4, 50, renderer));
			treeTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			treeTable.setColumnModel(model);
			treeTable.getTableHeader().setResizingAllowed(false);
		}

	}

	private static TableColumn createFixedColumn(int index, int width, CellRenderer renderer)
	{
		TableColumn column = new TableColumn(index, width, renderer, null);
		column.setMaxWidth(width);
		column.setResizable(false);
		return column;
	}

	@Override
	public void tableChanged(TableModelEvent e)
	{
		realignRowHeights();
	}

	private static class CellRenderer extends DefaultTableCellRenderer
	{

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			EquipNode node = (EquipNode) table.getValueAt(row, 0);

			if (node != null && node.getParent() == null)
			{
				setFont(headerFont);
				Color line = UIManager.getColor("Tree.line");
				setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, line));
			}
			else
			{
				setFont(normFont);
			}

			return this;
		}

	}

	public void install(JTreeTable treeTable)
	{
		this.treeTable = treeTable;
		treeTable.setTreeCellRenderer(treeRenderer);
		selectedModel = equipsetMap.get(character.getEquipmentSetRef().getReference());
		treeTable.setTreeTableModel(selectedModel);
		treeTable.getModel().addTableModelListener(this);
		realignRowHeights();

		character.getEquipmentSetRef().addReferenceListener(this);
	}

	public void uninstall()
	{
		if (treeTable != null)
		{
			treeTable.getModel().removeTableModelListener(this);
		}
		character.getEquipmentSetRef().removeReferenceListener(this);
	}

	private void realignRowHeights()
	{
		SwingUtilities.invokeLater(new Runnable()
		{

			@Override
			public void run()
			{
				JTree tree = treeTable.getTree();
				for (int row = 0; row < tree.getRowCount(); row++)
				{
					Rectangle bounds = tree.getRowBounds(row);
					if (bounds != null)
					{
						if (treeTable.getRowHeight(row) != bounds.height)
						{
							treeTable.setRowHeight(row, bounds.height);
						}
					}
				}
			}

		});
	}

	@Override
	public void elementAdded(ListEvent<EquipmentSetFacade> e)
	{
		equipsetMap.put(e.getElement(), new EquipmentTreeTableModel(character, e.getElement()));
	}

	@Override
	public void elementRemoved(ListEvent<EquipmentSetFacade> e)
	{
		equipsetMap.remove(e.getElement());
	}

	@Override
	public void elementsChanged(ListEvent<EquipmentSetFacade> e)
	{
		equipsetMap.clear();
		for (EquipmentSetFacade equipset : equipsets)
		{
			equipsetMap.put(equipset, new EquipmentTreeTableModel(character, equipset));
		}
	}

	@Override
	public void referenceChanged(ReferenceEvent<EquipmentSetFacade> e)
	{
		treeTable.getModel().removeTableModelListener(this);
		selectedModel = equipsetMap.get(e.getNewReference());
		treeTable.setTreeTableModel(selectedModel);
		treeTable.getModel().addTableModelListener(this);
		realignRowHeights();
	}

	private class TreeRenderer extends DefaultTreeCellRenderer
	{

		private Map<String, ImageIcon> iconCache = new HashMap<String, ImageIcon>();

		@Override
		public Component getTreeCellRendererComponent(final JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, final int row, boolean hasFocus)
		{
			String text = String.valueOf(value);
			boolean isEquipNode = value instanceof EquipNode;
			boolean isPhantomSlot = isEquipNode && ((EquipNode) value).getNodeType()
					== NodeType.PHANTOM_SLOT;
			if (isPhantomSlot)
			{
				text = "Empty slot";
			}
			super.getTreeCellRendererComponent(tree, text, sel, expanded, leaf, row, hasFocus);

			if (isEquipNode && ((EquipNode) value).getParent() == null)
			{
				setFont(largeFont);
				setIcon(null);
			}
			else if (isPhantomSlot)
			{
				setFont(italicFont);
				setForeground(Color.GRAY);
				setIcon(null);
			}
			else
			{
				setFont(normFont);
				EquipmentFacade equip = null;
				if (!selected)
				{
					setForeground(UIPropertyContext.getQualifiedColor());
				}
				if (isEquipNode && ((EquipNode) value).getNodeType() == NodeType.EQUIPMENT)
				{
					equip = ((EquipNode) value).getEquipment();

					String path = equip.getIcon().getAbsolutePath();
					ImageIcon icon = iconCache.get(path);
					if (icon == null)
					{
						icon = new ImageIcon(path);
						iconCache.put(path, icon);
					}
					setIcon(icon);

					if (!character.isQualifiedFor(equip))
					{
						setForeground(UIPropertyContext.getNotQualifiedColor());
					}
				}
			}
			return this;
		}

	}

}
