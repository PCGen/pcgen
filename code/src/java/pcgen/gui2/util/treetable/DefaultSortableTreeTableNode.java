/*
 * DefaultSortableTreeTableNode.java
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
 * Created on Feb 21, 2008, 3:11:09 PM
 */
package pcgen.gui2.util.treetable;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import pcgen.gui2.util.table.Row;

/**
 *
 * @author Connor Petty &lt;mistercpp2000@gmail.com&gt;
 */
public class DefaultSortableTreeTableNode extends DefaultTreeTableNode
        implements SortableTreeTableNode
{

    public DefaultSortableTreeTableNode()
    {
        this(Collections.emptyList());
    }

    public DefaultSortableTreeTableNode(List<?> data)
    {
        setValues(data);
    }

//    public DefaultSortableTreeTableNode(TreeNode node)
//    {
//        this();
//        if (node instanceof TreeTableNode)
//        {
//            TreeTableNode treeTableNode = (TreeTableNode) node;
//            setValues(treeTableNode.getValues());
//        }
//        for (int x = 0; x < node.getChildCount(); x++)
//        {
//            add(new DefaultSortableTreeTableNode(node.getChildAt(x)));
//        }
//    }

	@Override
    @SuppressWarnings("unchecked")
    public void sortChildren(Comparator<Row> comparator)
    {
        if (children != null)
        {
            children.sort(comparator);
            for (int x = 0; x < children.size(); x++)
            {
                SortableTreeTableNode child = (SortableTreeTableNode) children.get(x);
                child.sortChildren(comparator);
            }
        }
    }

}
