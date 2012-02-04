/*
 * WeightedDataValue.java
 *
 * Created on April 24, 2003, 4:35 PM
 */
package pcgen.core.doomsdaybook;

/**
 * <code>WeightedDataValue</code>.
 * 
 * @author  devon
 * @version	$Revision$
 */
public class WeightedDataValue extends DataValue
{
	private int weight;

	/** Creates a new instance of WeightedDataValue */
	public WeightedDataValue()
	{
		super();
		weight = 1;
	}

	/**
	 * Constructor
	 * 
	 * @param value
	 * @param weight
	 */
	public WeightedDataValue(String value, int weight)
	{
		super(value);
		this.weight = weight;
	}

	/**
	 * Set weight.
	 * @param weight
	 */
	public void setWeight(int weight)
	{
		this.weight = weight;
	}

	/**
	 * Get weight.
	 * @return weight
	 */
	public int getWeight()
	{
		return weight;
	}
}
