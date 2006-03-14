/*
 * DataElement.java
 *
 * Created on April 24, 2003, 4:14 PM
 */
package plugin.doomsdaybook.util;

import java.util.ArrayList;

/**
 *
 * @author  devon
 */
public interface DataElement
{
	/**
	 * Get Data
	 * @return ArrayList
	 * @throws Exception
	 */
	public ArrayList getData() throws Exception;

	/**
	 * Get data
	 * @param choice
	 * @return ArrayList
	 * @throws Exception
	 */
	public ArrayList getData(int choice) throws Exception;

	/**
	 * Get id
	 * @return id
	 */
	public String getId();

	/**
	 * Get last data
	 * @return last data
	 * @throws Exception
	 */
	public ArrayList getLastData() throws Exception;

	/**
	 * Get title
	 * @return title
	 */
	public String getTitle();

	/**
	 * Get weight
	 * @return weight
	 */
	public int getWeight();

	/**
	 * trim to size
	 */
	public void trimToSize();
}
