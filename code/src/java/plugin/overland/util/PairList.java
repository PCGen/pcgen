/*
 * TravelMethods.java
 *
 * Created on July 22, 2003, 10:17 PM
 */
package plugin.overland.util;

import java.util.ArrayList;
import java.util.List;

/** Class that holds a set of travel methods and speeds
 *
 * @author  Juliean Galak
 */
public class PairList<T extends Pair<?,?>>
{
	private List<T> vPairs;

	public PairList()
	{
		vPairs = new ArrayList<T>();
	}

	public int getCount()
	{
		return vPairs.size();
	}

	public T get(int i)
	{
		return vPairs.get(i);
	}

	public void add(T p)
	{
		vPairs.add(p);
	}
}
