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
package pcgen.gui2.util.treeview;

import java.util.Arrays;

import pcgen.base.util.ArrayUtilities;

import org.apache.commons.lang3.StringUtils;

public class TreeViewPath<E>
{

    private final Object[] path;
    private final int length;

    /**
     * This saves the trouble of saying:<br>
     * new TreePath(new Object[]{string1, string2, pobj})<br>
     * instead you can now say:<br>
     * new TreeViewPath(pobj, string1, string2)
     *
     * @param pobj the last element in the list
     * @param path the string path leading to the last element
     */
    public TreeViewPath(E pobj, Object path)
    {
        this(new Object[]{path, pobj}, 2);
    }

    @SuppressWarnings("unchecked")
    public TreeViewPath(E pobj, Object... path)
    {
        this(path, pobj);
    }

    public TreeViewPath(E pobj)
    {
        this(new Object[]{pobj}, 1);
    }

    @SafeVarargs
    public TreeViewPath(Object[] path, E... pobjs)
    {
        if (path == null || path.length == 0 || pobjs == null || pobjs.length == 0)
        {
            throw new IllegalArgumentException("path in TreePath must be non null and not empty.");
        }
        this.path = ArrayUtilities.mergeArray(Object.class, path, pobjs);
        this.length = this.path.length;
    }

    private TreeViewPath(Object[] path, int length)
    {
        if (path == null || path.length == 0)
        {
            throw new IllegalArgumentException("path in TreePath must be non null and not empty.");
        }
        this.path = path;
        this.length = length;
    }

    /**
     * Returns the number of elements in the path.
     *
     * @return an int giving a count of items the path
     */
    int getPathCount()
    {
        return length;
    }

    /**
     * Returns the path component at the specified index.
     *
     * @param element an int specifying an element in the path, where
     *                0 is the first element in the path
     * @return the Object at that index location
     * @throws IllegalArgumentException if the index is beyond the length
     *                                  of the path
     */
    Object getPathComponent(int element)
    {
        return path[element];
    }

    /**
     * Returns the last component of this path. For a path returned by
     * DefaultTreeModel this will return an instance of TreeNode.
     *
     * @return the Object at the end of the path
     */
    @SuppressWarnings("unchecked")
    private E getLastPathComponent()
    {
        return (E) path[length - 1];
    }

    /**
     * Returns the hashCode for the object. The hash code of a TreePath
     * is defined to be the hash code of the last component in the path.
     *
     * @return the hashCode for the object
     */
    @Override
    public int hashCode()
    {
        return getLastPathComponent().hashCode();
    }

    /**
     * Tests two TreePaths for equality by checking each element of the
     * paths for equality. Two paths are considered equal if they are of
     * the same length, and contain
     * the same elements ({@code .equals}).
     *
     * @param obj the Object to compare
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }

        final TreeViewPath<?> other = (TreeViewPath<?>) obj;
        if (this.length != other.length)
        {
            return false;
        }
        return Arrays.equals(path, other.path);
    }

    @Override
    public String toString()
    {
        return "TreeViewPath " + StringUtils.join(path, ", ");
    }

}
