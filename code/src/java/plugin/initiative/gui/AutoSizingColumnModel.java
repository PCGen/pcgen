/*
 * Created on Sep 9, 2003
 *
 */
package plugin.initiative.gui;

import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.util.Enumeration;

/**
 * @author LodgeR
 * <p>GradesViewColumnModel</p>
 * <p>edit method description . . .</p>
 */
public class AutoSizingColumnModel extends DefaultTableColumnModel
{
	private static final int COLUMN_WIDTH_PADDING = 10;
	JTable m_table = null;

	/**
	 * Set preffered column width
	 * @param aColumn
	 */
	public void setColumnPreferredWidth(TableColumn aColumn)
	{
		if (m_table != null)
		{
			TableCellRenderer renderer = null;
			int rowCount = m_table.getRowCount();
			int headerWidth = 0;
			int contentsWidth = 0;

			if (aColumn.getHeaderRenderer() != null)
			{
				renderer = aColumn.getHeaderRenderer();
			}
			else
			{
				renderer = m_table.getTableHeader().getDefaultRenderer();
			}

			if (renderer != null)
			{
				headerWidth =
						renderer.getTableCellRendererComponent(m_table,
							aColumn.getHeaderValue(), false, false, 0, 0)
							.getPreferredSize().width;
			}

			renderer =
					m_table.getDefaultRenderer(m_table.getModel()
						.getColumnClass(getColumnCount()));

			if (renderer != null)
			{
				for (int row = 0; row < rowCount; row++)
				{
					contentsWidth =
							Math.max(contentsWidth,
								renderer
									.getTableCellRendererComponent(
										m_table,
										m_table.getModel().getValueAt(row,
											getColumnCount()), false, false,
										row, 0).getPreferredSize().width);
				}
			}

			aColumn.setPreferredWidth(Math.max(headerWidth, contentsWidth)
				+ getColumnMargin() + COLUMN_WIDTH_PADDING);
		}
		else
		{
			aColumn.setPreferredWidth(150);
		}
	}

	/**
	 * Get total preferred width
	 * @return total preferred width
	 */
	public int getTotalPreferredWidth()
	{
		int returnValue = 0;
		Enumeration<TableColumn> columns = getColumns();

		while (columns.hasMoreElements())
		{
			returnValue += columns.nextElement().getPreferredWidth();
		}

		return returnValue;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableColumnModel#addColumn(javax.swing.table.TableColumn)
	 */
    @Override
	public void addColumn(TableColumn aColumn)
	{
		setColumnPreferredWidth(aColumn);
		super.addColumn(aColumn);
	}

	/**
	 * @param table
	 */
	public void referenceTable(JTable table)
	{
		m_table = table;
	}
}
