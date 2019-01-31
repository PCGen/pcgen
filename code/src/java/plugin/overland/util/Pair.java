/*
 * Copyright 2003 (C) Devon Jones
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
 */
package plugin.overland.util;

/** Class that holds a pair of values
 * Note: THE ITEMS PASSED TO THIS CLASS ARE NOT DUPLICATED
 *    IT OPERATES ON ORIGINAL INSTANCES!  BE CAREFUL!
 */
public abstract class Pair<K, V>
{
	private K left; //store one item here
	private V right; //store other item here

	/** Creates a new instance of TravelMethod
	 * @param left - left Object to add
	 * @param right - right Object to add
	 */
	private Pair(K left, V right)
	{
		this.left = left;
		this.right = right;
	}

	protected Pair()
	{
		this(null, null);
	}

	protected void setLeft(K left)
	{
		this.left = left;
	}

	protected K getLeft()
	{
		return left;
	}

	protected void setRight(V right)
	{
		this.right = right;
	}

	protected V getRight()
	{
		return right;
	}
}
