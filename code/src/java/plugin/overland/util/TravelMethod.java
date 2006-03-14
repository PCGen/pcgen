/*
 * TravelMethod.java
 *
 * Created on July 22, 2003, 10:22 PM
 */
package plugin.overland.util;


/** Class that holds a single travel method and its speed.  Note: This is a wrapper for the Pair class
 *
 * @author  Juliean Galak
 */
public class TravelMethod extends Pair
{
	/** Creates a new instance of TravelMethod
	 * @param name - String containing name
	 * @param speed - Int containing speed in mpd
	 */
	public TravelMethod(String name, int speed)
	{
		super.setLeft(name);
		super.setRight(new Integer(speed));
	}

	/**
	 * Constructor
	 */
	public TravelMethod()
	{
		this("", 0);
	}

	/**
	 * Set the travel method name
	 * @param name
	 */
	public void setName(String name)
	{
		super.setLeft(name);
	}

	/**
	 * Get the travel method name
	 * @return the travel method name
	 */
	public String getName()
	{
		return (String) super.getLeft();
	}

	/**
	 * Set the travel method speed
	 * @param speed
	 */
	public void setSpeed(int speed)
	{
		super.setRight(new Integer(speed));
	}

	/**
	 * Get the travel method speed
	 * @return the travel method speed
	 */
	public int getSpeed()
	{
		return (((Integer) super.getRight()).intValue());
	}
}
