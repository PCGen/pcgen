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
public class TravelMethods extends PairList
{
	/**
	 * Constructor
	 */
	public TravelMethods()
	{
		super();
	}

	/**
	 * Get the travel method
	 * @param i
	 * @return the travel method
	 */
	public TravelMethod getMethodAtI(int i)
	{
		return (TravelMethod) super.getElementAtI(i);
	}

	/**
	 * Add a travel method
	 * @param tm
	 */
	public void addTravelMethod(TravelMethod tm)
	{
		super.addPair(tm);
	}
}
