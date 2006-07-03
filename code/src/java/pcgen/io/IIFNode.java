/*
 * IIFNode.java
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
 * Created on November 5, 2001, 8:00 PM
 */
package pcgen.io;

import java.util.ArrayList;
import java.util.List;

/**
 * <code>IIFNode</code>.
 *
 * @author Mark Hulsman <mark_hulsman@users.sourceforge.net>
 * @version $Revision$
 */
class IIFNode
{
	private List<Object> falseChildren;
	private List<Object> trueChildren;
	private String expr;

	IIFNode(String _expr)
	{
		expr = _expr;
		trueChildren = new ArrayList<Object>();
		falseChildren = new ArrayList<Object>();
	}

	/**
	 * Add a 'true' child to the tree
	 * @param child
	 */
	public void addTrueChild(Object child)
	{
		trueChildren.add(child);
	}

	/**
	 * List the nodes that are truly children
	 * @return the nodes that are truly children
	 */
	public final List<?> trueChildren()
	{
		return trueChildren;
	}

	/**
	 * Add a false child
	 * @param child
	 */
	public void addFalseChild(Object child)
	{
		falseChildren.add(child);
	}

	/**
	 * Returns an expression for the node
	 * @return an expression for the node
	 */
	public final String expr()
	{
		return expr;
	}

	/**
	 * List the false children
	 * @return the false children
	 */
	public final List<?> falseChildren()
	{
		return falseChildren;
	}
}
