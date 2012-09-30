/*
 * TravelMethod.java
 *
 * Created on July 22, 2003, 10:22 PM
 */
package plugin.overland.util;

/** 
 * Class that holds a single travel method and its speed.  Note: This is a wrapper for the Pair class
 *
 * @author  Juliean Galak
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
