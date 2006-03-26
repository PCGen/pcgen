package pcgen.gui;

import java.util.List;

import javax.swing.table.TableColumn;

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
	
	/**
	 * Get the column default width
	 * @param col
	 * @return the column default width
	 */
	public int getMColumnDefaultWidth(int col);

	/**
	 * Set the column default width
	 * @param col
	 * @param width
	 */
	public void setMColumnDefaultWidth(int col, int width);
	
	/**
	 * Sets any column specific renderers/editors
	 * @param col
	 * @param width
	 */
	public void resetMColumn(int col, TableColumn column);
}
