/* DataSubValue - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package plugin.doomsdaybook.util;

/**
 * DataSubValue 
 */
public class DataSubValue
{
	private DataSubValue next;
	private String key;
	private String value;

	/**
	 * Constructor
	 * @param key
	 * @param value
	 */
	public DataSubValue(String key, String value)
	{
		this.key = key;
		this.value = value;
	}

	/**
	 * Get the value
	 * @param searchKey
	 * @return value
	 */
	public String get(String searchKey)
	{
		if (key.equals(searchKey))
		{
			return value;
		}

		if (next == null)
		{
			return null;
		}

		return next.get(searchKey);
	}

	/**
	 * Put the value
	 * @param sub
	 */
	public void put(DataSubValue sub)
	{
		if (next == null)
		{
			next = sub;
		}
		else
		{
			next.put(sub);
		}
	}
}
