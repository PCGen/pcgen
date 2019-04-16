/*
 * Copyright 2012 Vincent Lhote
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
package plugin.overland.model;

import plugin.overland.util.PairList;
import plugin.overland.util.RBCost;

/**
 * Implementation of RoomBoard. This class is package view on purpose.
 */
class RoomBoardImplementation implements RoomBoard
{
	private final PairList<RBCost> animals; //holds animal costs
	private final PairList<RBCost> foods; //holds inn costs
	private final PairList<RBCost> inns; //holds inn costs

	/**
	 * @param inns
	 * @param foods
	 * @param animals
	 */
	RoomBoardImplementation(PairList<RBCost> inns, PairList<RBCost> foods, PairList<RBCost> animals)
	{
		this.inns = inns;
		// TODO Auto-generated constructor stub
		this.foods = foods;
		this.animals = animals;
	}

	/**
	 * @return the animals
	 */
	@Override
	public PairList<RBCost> getAnimals()
	{
		return animals;
	}

	/**
	 * @return the foods
	 */
	@Override
	public PairList<RBCost> getFoods()
	{
		return foods;
	}

	/**
	 * @return the inns
	 */
	@Override
	public PairList<RBCost> getInns()
	{
		return inns;
	}

}
