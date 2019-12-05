/*
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

import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.EquipmentFacade;
import pcgen.facade.core.EquipmentSetFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.event.ListEvent;
import pcgen.facade.util.event.ListListener;
import pcgen.facade.util.event.ReferenceEvent;
import pcgen.facade.util.event.ReferenceListener;
import pcgen.gui2.UIPropertyContext;
import pcgen.gui2.facade.EquipNode;
import pcgen.gui2.tabs.models.CharacterTreeCellRenderer;
import pcgen.gui2.util.FontManipulation;
import pcgen.gui2.util.JTreeTable;
import pcgen.gui3.utilty.ColorUtilty;

/**
 * The parent model for the selected panel. Maps the various equipment sets for
 * a character.
 */
public class EquipmentModel
        implements ListListener<EquipmentSetFacade>, ReferenceListener<EquipmentSetFacade>, TableModelListener
{

    private static Font normFont;
    private static Font headerFont;
    private static Font biggerFont;
    private static Font lessFont;

    private final CharacterFacade character;
    private final TreeRenderer treeRenderer;
    private final Map<EquipmentSetFacade, EquipmentTreeTableModel> equipsetMap;
    private final ListFacade<EquipmentSetFacade> equipsets;
    private EquipmentTreeTableModel selectedModel;
    private final JTreeTable treeTable;

    public EquipmentModel(CharacterFacade character, JTreeTable table)
    {
        this.character = character;
        this.treeTable = table;
        treeRenderer = (TreeRenderer) treeTable.getTreeCellRenderer();

        equipsetMap = new HashMap<>();
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
        normFont = treeTable.getFont();
        headerFont = FontManipulation.title(normFont);
        biggerFont = FontManipulation.title(FontManipulation.xxlarge(normFont));
        lessFont = FontManipulation.less(normFont);
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
        treeTable.setTreeCellRenderer(new TreeRenderer());
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

    @Override
    public void elementModified(ListEvent<EquipmentSetFacade> e)
    {

    }

    private static class CellRenderer extends DefaultTableCellRenderer
    {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column)
        {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            EquipNode node = (EquipNode) table.getValueAt(row, 0);

            if (node != null && node.getParent() == null)
            {
                FontManipulation.title(this);
                setFont(headerFont);
                Color line = UIManager.getColor("Tree.line");
                setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, line));
            } else
            {
                setFont(normFont);
            }

            return this;
        }

        @Override
        protected void setValue(Object value)
        {
            super.setValue(value);
            setToolTipText(getText());
        }

    }

    public void install()
    {
        treeRenderer.setCharacter(character);
        selectedModel = equipsetMap.get(character.getEquipmentSetRef().get());
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
        treeRenderer.setCharacter(null);
    }

    private void realignRowHeights()
    {
        SwingUtilities.invokeLater(() -> {
            JTree tree = treeTable.getTree();
            for (int row = 0;row < tree.getRowCount();row++)
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

    private static class TreeRenderer extends CharacterTreeCellRenderer
    {

        private final Map<String, ImageIcon> iconCache = new HashMap<>();

        @Override
        public Component getTreeCellRendererComponent(final JTree tree, Object value, boolean sel, boolean expanded,
                boolean leaf, final int row, boolean focus)
        {
            String text = String.valueOf(value);
            boolean isEquipNode = value instanceof EquipNode;
            boolean isPhantomSlot = isEquipNode && ((EquipNode) value).getNodeType() == EquipNode.NodeType.PHANTOM_SLOT;
            if (isPhantomSlot)
            {
                text = "Empty slot";
            }
            super.getTreeCellRendererComponent(tree, text, sel, expanded, leaf, row, focus);

            if (isEquipNode && ((EquipNode) value).getParent() == null)
            {
                setFont(biggerFont);
                setIcon(null);
            } else if (isPhantomSlot)
            {
                setFont(lessFont);
                setForeground(Color.GRAY);
                setIcon(null);
            } else
            {
                setFont(normFont);
                EquipmentFacade equip = null;
                if (!selected)
                {
                    setForeground(ColorUtilty.colorToAWTColor(UIPropertyContext.getQualifiedColor()));
                }
                if (isEquipNode && ((EquipNode) value).getNodeType() == EquipNode.NodeType.EQUIPMENT)
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
                        setForeground(ColorUtilty.colorToAWTColor(UIPropertyContext.getNotQualifiedColor()));
                    }
                }
            }
            return this;
        }

    }

}
