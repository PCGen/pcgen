/*
 * Pair.java
 *
 * Created on July 22, 2003, 10:22 PM
 */
package plugin.overland.util;


/** Class that holds a pair of values
 * Note: THE ITEMS PASSED TO THIS CLASS ARE NOT DUPLICATED
 *    IT OPERATES ON ORIGINAL INSTANCES!  BE CAREFUL!
 * @author  Juliean Galak
 */
public abstract class Pair
{
	private Object left; //store one item here
	private Object right; //store other item here

	/** Creates a new instance of TravelMethod
	 * @param left - left Object to add
	 * @param right - right Object to add
	 */
	public Pair(Object left, Object right)
	{
		this.left = left;
		this.right = right;
	}

	public Pair()
	{
		this(null, null);
	}

	protected void setLeft(Object left)
	{
		this.left = left;
	}

	protected Object getLeft()
	{
		return left;
	}

	protected void setRight(Object right)
	{
		this.right = right;
	}

	protected Object getRight()
	{
		return right;
	}
}
