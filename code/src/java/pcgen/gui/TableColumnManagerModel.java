package pcgen.gui;

import java.util.List;

/**
 * Interface to manage table columns
 */
public interface TableColumnManagerModel {
	
	/**
	 * Get the column list
	 * @return column list
	 */
	public List getMColumnList();
	
	/**
	 * Returns true if a particular model column is displayed
	 * @param col
	 * @return true if a particular model column is displayed
	 */
	public boolean isMColumnDisplayed(int col);
	
	/**
	 * Set flag for if a particular model column is displayed
	 * @param col
	 * @param disp
	 */
	public void setMColumnDisplayed(int col, boolean disp);
	
	/**
	 * Get the column offset
	 * @return offset
	 */
	public int getMColumnOffset();
}
