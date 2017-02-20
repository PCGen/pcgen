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
 *
 */
 package plugin.overland.util;

/** 
 * Class that holds a single travel method and its speed.  Note: This is a wrapper for the Pair class
 *
 */
public class RBCost extends Pair<String, Float>
{
	/**
	 * Creates a new instance of RMCost
	 * @param name String containing name
	 * @param cost float containing cost
	 */
	public RBCost(String name, float cost)
	{
		super.setLeft(name);
		super.setRight(new Float(cost));
	}

	public RBCost()
	{
		this("", 0);
	}

	public void setCost(float cost)
	{
		super.setRight(new Float(cost));
	}

	public float getCost()
	{
		return super.getRight().floatValue();
	}

	public void setName(String name)
	{
		super.setLeft(name);
	}

	public String getName()
	{
		return super.getLeft();
	}
}
