/*
 * DataValue.java
 *
 * Created on April 24, 2003, 4:23 PM
 */
package plugin.doomsdaybook.util;


/**
 * @author  devon
 */
public class DataValue
{
	private DataSubValue subvalue;
	private String value;

	/**
	 * Constructor
	 */
	public DataValue()
	{
		value = "";
	}

	/**
	 * Constructor
	 * @param value
	 */
	public DataValue(String value)
	{
		this.value = value;
	}

	/**
	 * Get SubValue
	 * @param key
	 * @return SubValue
	 */
	public String getSubValue(String key)
	{
		if (subvalue != null)
		{
			return subvalue.get(key);
		}
		return null;
	}

	/**
	 * Set value
	 * @param value
	 */
	public void setValue(String value)
	{
		this.value = value;
	}

	/**
	 * Get value
	 * @return value
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * Add sub value
	 * @param key
	 * @param subValue
	 */
	public void addSubValue(String key, String subValue)
	{
		if (subvalue != null)
		{
			subvalue.put(new DataSubValue(key, subValue));
		}
		else
		{
			subvalue = new DataSubValue(key, subValue);
		}
	}
}
