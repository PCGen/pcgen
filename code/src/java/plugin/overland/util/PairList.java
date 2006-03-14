/*
 * TravelMethods.java
 *
 * Created on July 22, 2003, 10:17 PM
 */
package plugin.overland.util;

import java.util.Vector;

/** Class that holds a set of travel methods and speeds
 *
 * @author  Juliean Galak
 */
public abstract class PairList
{
	private Vector vPairs;

	public PairList()
	{
		vPairs = new Vector();
	}

	public int getCount()
	{
		return vPairs.size();
	}

	protected Pair getElementAtI(int i)
	{
		return (Pair) vPairs.get(i);
	}

	protected void addPair(Pair p)
	{
		vPairs.add(p);
	}
}
