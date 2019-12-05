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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 */
public class DefaultTreeTableNode extends DefaultMutableTreeNode implements TreeTableNode
{

    private List<Object> data;

    public DefaultTreeTableNode()
    {
        this(Collections.emptyList());
    }

    public DefaultTreeTableNode(List<?> data)
    {
        setValues(data);
    }

    @Override
    public Object getValueAt(int column)
    {
        if (data.size() > column)
        {
            return data.get(column);
        }
        return null;
    }

    @Override
    public void setValueAt(Object value, int column)
    {
        if (data.isEmpty())
        {
            data = new ArrayList<>(column + 1);
        }
        while (data.size() <= column)
        {
            data.add(null);
        }
        data.set(column, value);
    }

    protected void setValues(List<?> values)
    {
        this.data = new ArrayList<>(values);
    }

    @Override
    public String toString()
    {
        if (!data.isEmpty())
        {
            Object name = data.get(0);
            if (name != null)
            {
                return name.toString();
            }
        }
        return super.toString();
    }

    @Override
    public TreeTableNode getChildAt(int childIndex)
    {
        return (TreeTableNode) super.getChildAt(childIndex);
    }

    @Override
    public TreeTableNode getParent()
    {
        return (TreeTableNode) super.getParent();
    }

}
