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

    protected TreeTableNode createDefaultTreeTableNode()
    {
        return new DefaultTreeTableNode();
    }

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
