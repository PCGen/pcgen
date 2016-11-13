/*
 * TreeViewPath.java
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
 * Created on Feb 10, 2008, 5:29:12 PM
 */
package pcgen.gui2.util.treeview;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Connor Petty &lt;mistercpp2000@gmail.com&gt;
 */
public class TreeViewPath<E>
{

    private final Object[] path;
    private final int length;

    /**
     * This saves the trouble of saying:<br>
     * new TreePath(new Object[]{string1, string2, pobj})<br>
     * instead you can now say:<br>
     * new TreeViewPath(pobj, string1, string2)
     * @param pobj the last element in the list
     * @param path the string path leading to the last element
     */
    public TreeViewPath(E pobj, Object path)
    {
        this(new Object[]{path,
                          pobj
     }, 2);
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

    public TreeViewPath(Object[] path, E... pobjs)
    {
        if (path == null || path.length == 0 || pobjs == null || pobjs.length ==
                0)
        {
            throw new IllegalArgumentException("path in TreePath must be non null and not empty.");
        }
        this.length = path.length + pobjs.length;
        this.path = new Object[length];
        System.arraycopy(path, 0, this.path, 0, path.length);
        System.arraycopy(pobjs, 0, this.path, path.length, pobjs.length);
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
     * Returns an ordered array of Objects containing the components of this
     * TreePath. The first element (index 0) is the root.
     *
     * @return an array of Objects representing the TreePath
     */
    public Object[] getPath()
    {
        return path.clone();
    }

    /**
     * Returns the number of elements in the path.
     *
     * @return an int giving a count of items the path
     */
    public int getPathCount()
    {
        return length;
    }

    /**
     * Returns the path component at the specified index.
     *
     * @param element  an int specifying an element in the path, where
     *                 0 is the first element in the path
     * @return the Object at that index location
     * @throws IllegalArgumentException if the index is beyond the length
     *         of the path
     */
    public Object getPathComponent(int element)
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
    public E getLastPathComponent()
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

    /**
     * Returns a path containing all the elements of this object, except
     * the last path component.
	 * @return the parent path
	 */
    public TreeViewPath<E> getParentPath()
    {
        return new TreeViewPath<>(path, length - 1);
    }

    public TreeViewPath<E> getParentPath(int lastElement)
    {
        return new TreeViewPath<>(path, lastElement + 1);
    }

    public TreeViewPath<E> pathByAddingParent(String singlePath)
    {
        Object[] parentPath = new Object[length + 1];
        parentPath[0] = singlePath;
        System.arraycopy(path, 0, parentPath, 1, length);
        return new TreeViewPath<>(parentPath, length + 1);
    }

	@Override
	public String toString()
	{
		return "TreeViewPath " + StringUtils.join(path, ", ");
	}

}
