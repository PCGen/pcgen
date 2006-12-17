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
public abstract class Pair<K, V>
{
	private K left; //store one item here
	private V right; //store other item here

	/** Creates a new instance of TravelMethod
	 * @param left - left Object to add
	 * @param right - right Object to add
	 */
	public Pair(K left, V right)
	{
		this.left = left;
		this.right = right;
	}

	public Pair()
	{
		this(null, null);
	}

	protected void setLeft(K left)
	{
		this.left = left;
	}

	protected K getLeft()
	{
		return left;
	}

	protected void setRight(V right)
	{
		this.right = right;
	}

	protected V getRight()
	{
		return right;
	}
}
