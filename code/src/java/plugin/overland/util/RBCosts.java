/*
 * TravelMethods.java
 *
 * Created on July 22, 2003, 10:17 PM
 */
package plugin.overland.util;


/** Class that holds a set of travel methods and speeds
 *
 * @author  Juliean Galak
 */
public class RBCosts extends PairList
{
	public RBCosts()
	{
		super();
	}

	public RBCost getRBCostAtI(int i)
	{
		return (RBCost) super.getElementAtI(i);
	}

	public void addRBCost(RBCost rbc)
	{
		super.addPair(rbc);
	}
}
