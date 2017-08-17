/*
 * Copyright 2008 (C) Connor Petty <mistercpp2000@gmail.com>
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
package pcgen.gui2.util.treetable;

import java.util.List;

/**
 *
 */
public class DefaultTreeTableModel extends AbstractTreeTableModel
{

    private List<Class<?>> columnClasses;
    private List<String> columnNames;
    private int columnCount;

    protected DefaultTreeTableModel()
    {
        super();
    }

    protected DefaultTreeTableModel(TreeTableNode root)
    {
        super(root);
    }

//    public DefaultTreeTableModel(TableModel tableModel)
//    {
//        super(null);
//        TreeTableNode rootNode = createDefaultTreeTableNode();
//        columnCount = tableModel.getColumnCount();
//        for (int x = 0; x < tableModel.getRowCount(); x++)
//        {
//            TreeTableNode child = createDefaultTreeTableNode();
//            rootNode.insert(child, x);
//            for (int y = 0; y < columnCount; y++)
//            {
//                child.setValueAt(tableModel.getValueAt(x, y), y);
//            }
//        }
//        columnClasses = new ArrayList<Class<?>>(columnCount);
//        columnNames = new ArrayList<String>(columnCount);
//        for (int x = 0; x < columnCount; x++)
//        {
//            columnClasses.add(tableModel.getColumnClass(x));
//            columnNames.add(tableModel.getColumnName(x));
//        }
//        setRoot(rootNode);
//    }

//    public DefaultTreeTableModel(TreeTableModel treeTableModel)
//    {
//        super(null);
//        TreeTableNode rootNode = createDefaultTreeTableNode((TreeNode) treeTableModel.getRoot());
//        columnCount = treeTableModel.getColumnCount();
//        columnClasses = new ArrayList<Class<?>>(columnCount);
//        columnNames = new ArrayList<String>(columnCount);
//        for (int x = 0; x < columnCount; x++)
//        {
//            columnClasses.add(treeTableModel.getColumnClass(x));
//            columnNames.add(treeTableModel.getColumnName(x));
//        }
//        setRoot(rootNode);
//    }

    protected TreeTableNode createDefaultTreeTableNode()
    {
        return new DefaultTreeTableNode();
    }

//    protected TreeTableNode createDefaultTreeTableNode(TreeNode node)
//    {
//        return new DefaultTreeTableNode(node);
//    }

	@Override
    public int getColumnCount()
    {
        return columnCount;
    }

    @Override
    public Class<?> getColumnClass(int column)
    {
        switch (column)
        {
            case 0:
                return TreeTableNode.class;
            default:
                return columnClasses.get(column);
        }
    }

    @Override
    public String getColumnName(int column)
    {
        return columnNames.get(column);
    }

}
