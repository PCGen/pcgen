package pcgen.gui.utils.chooser;

import javax.swing.table.AbstractTableModel;
import javax.swing.event.TableModelEvent;

/**
 * This table model implements those methods required to support
 * the simple needs of the available and selected tables.
 *
 * @author    Matt Woodard
 */
public class ChooserTableModel extends AbstractTableModel
{
	static final long serialVersionUID = -2148735105737308335L;
	String lineTerminator = "";

	/** The column classes */
	Class[] mColumnClasses;

	/** The column names */
	String[] mColumnNames;

	/** The data memory */
	Object[][] mData;

	/** The number of columns */
	int mColumns;

	/** The number of rows */
	int mRows;

	/**
	 * Always returns false
	 *
	 * @param row  Description of Parameter
	 * @param col  Description of Parameter
	 * @return     The CellEditable value
	 * author     Matt Woodard
	 */
	public boolean isCellEditable(int row, int col)
	{
		return false;
	}

	/**
	 * Gets the specified column class
	 *
	 * @param column  Description of Parameter
	 * @return        The ColumnClass value
	 * author        Matt Woodard
	 */
	public Class getColumnClass(int column)
	{
		return mColumnClasses[column];
	}

	/**
	 * Gets the number of columns
	 *
	 * @return   The ColumnCount value
	 * author   Matt Woodard
	 */
	public int getColumnCount()
	{
		return mColumns;
	}

	/**
	 * Gets the specified column name
	 *
	 * @param column  Description of Parameter
	 * @return        The ColumnName value
	 * author        Matt Woodard
	 */
	public String getColumnName(int column)
	{
		return mColumnNames[column];
	}

	/**
	 * Gets the number of rows
	 *
	 * @return   The RowCount value
	 * author   Matt Woodard
	 */
	public int getRowCount()
	{
		return mRows;
	}

	/**
	 * Sets the value in the given location
	 *
	 * @param value   The new ValueAt value
	 * @param row     The new ValueAt value
	 * @param column  The new ValueAt value
	 * author        Matt Woodard
	 */
	public void setValueAt(Object value, int row, int column)
	{
		mData[row][column] = value;
	}

	/**
	 * Gets the value at the given location
	 *
	 * @param row  Description of Parameter
	 * @param col  Description of Parameter
	 * @return     The ValueAt value
	 * author     Matt Woodard
	 */
	public Object getValueAt(int row, int col)
	{
		Object obj = mData[row][col];

		if ((obj instanceof String) && (lineTerminator.length() != 0))
		{
			final int idx = ((String) obj).indexOf(lineTerminator);

			if (idx > -1)
			{
				obj = ((String) obj).substring(0, idx);
			}
		}

		return obj;
	}

	/**
	 * Sets the column names
	 *
	 * @param names  The new ColumnsNames value
	 * author       Matt Woodard
	 */
	public void setColumnsNames(String[] names)
	{
		mColumnNames = names;
		mColumns = mColumnNames.length;

		fireTableStructureChanged();
	}

	/**
	 * Initializes the table's memory structure and notifies listeners
	 * of the insertion.
	 *
	 * @param data  The new Data value
	 * @param lineTerminator
	 */
	public void setData(Object[][] data, String lineTerminator)
	{
		mData = data;

		mRows = 0;

		mColumnClasses = new Class[mColumns];

		this.lineTerminator = lineTerminator;

		if (mData != null)
		{
			mRows = mData.length;

			if (mRows > 0)
			{
				final Object[] row = data[0];

				for (int c = 0; c < mColumns; c++)
				{
					// Assume the row data isn't null
					try
					{
						mColumnClasses[c] = row[c].getClass();
					}
					catch (ArrayIndexOutOfBoundsException aioobe)
					{
						// Ignore
					}
				}
			}
		}

		fireTableChanged(new TableModelEvent(this, 0, mRows - 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
	}
}
