/*
 * PurchaseMethodFacade.java
 * Copyright 2008 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Aug 9, 2008, 3:30:17 PM
 */
package pcgen.core.facade.generator;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public interface PurchaseMethodFacade extends StatGenerationFacade
{

	public int getMinScore();

	public int getMaxScore();

	/**
	 * @return the number of points that can be distributed
	 */
	public int getPoints();

	/**
	 * 
	 * @param score
	 * @return the cost for the give score
	 */
	public int getScoreCost(int score);

	public void setMaxScore(int score);

	public void setMinScore(int score);

	public void setPoints(int points);

	public void setScoreCost(int score, int cost);

}
