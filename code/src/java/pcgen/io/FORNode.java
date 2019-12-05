/*
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
package pcgen.io;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code FORNode}.
 */
final class FORNode
{
    private List<Object> children;
    private String max;
    private String min;
    private String step;
    private String var;
    private boolean exists;

    /**
     * Constructor
     *
     * @param _var
     * @param _min
     * @param _max
     * @param _step
     * @param _exists
     */
    FORNode(String _var, String _min, String _max, String _step, boolean _exists)
    {
        children = new ArrayList<>();
        var = _var;
        min = _min;
        max = _max;
        step = _step;
        exists = _exists;
    }

    /**
     * Add a child
     *
     * @param child
     */
    public void addChild(Object child)
    {
        children.add(child);
    }

    /**
     * Return the children of this node
     *
     * @return the children of this node
     */
    public List<?> children()
    {
        return children;
    }

    /**
     * Return TRUE if exists
     *
     * @return TRUE if exists
     */
    public boolean exists()
    {
        return exists;
    }

    /**
     * Return max
     *
     * @return max
     */
    public String max()
    {
        return max;
    }

    /**
     * Return min
     *
     * @return min
     */
    public String min()
    {
        return min;
    }

    /**
     * Return step
     *
     * @return step
     */
    public String step()
    {
        return step;
    }

    /**
     * Return var
     *
     * @return var
     */
    public String var()
    {
        return var;
    }
}
