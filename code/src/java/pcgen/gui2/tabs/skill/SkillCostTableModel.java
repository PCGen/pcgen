/*
 * SkillCostTableModel.java
 * Copyright 2010 Connor Petty <cpmeister@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Created on Jul 6, 2010, 2:17:07 PM
 */
package pcgen.gui2.tabs.skill;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.core.facade.CharacterFacade;
import pcgen.core.facade.CharacterLevelFacade;
import pcgen.core.facade.CharacterLevelsFacade;
import pcgen.gui2.tabs.Utilities;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class SkillCostTableModel extends AbstractTableModel implements ListSelectionListener
{

	private final CharacterFacade character;
	private final CharacterLevelsFacade levels;
	private ListSelectionModel selectionModel;

	public SkillCostTableModel(CharacterFacade character, ListSelectionModel selectionModel)
	{
		this.character = character;
		this.levels = character.getCharacterLevelsFacade();
		this.selectionModel = selectionModel;
		selectionModel.addListSelectionListener(this);
	}

	public static void initializeTable(JTable table)
	{
		table.setAutoCreateColumnsFromModel(false);
		JTableHeader header = table.getTableHeader();
		TableColumnModel columns = new DefaultTableColumnModel();
		TableCellRenderer headerRenderer = header.getDefaultRenderer();
		columns.addColumn(Utilities.createTableColumn(0, "Skill Cost", headerRenderer, true));
		columns.addColumn(Utilities.createTableColumn(1, "Rank Cost", headerRenderer, false));
		columns.addColumn(Utilities.createTableColumn(2, "Max Ranks", headerRenderer, false));
		table.setColumnModel(columns);
		table.setFocusable(false);
		table.setCellSelectionEnabled(false);
		header.setReorderingAllowed(false);
		header.setResizingAllowed(false);
	}

	public void install(JTable table)
	{
		table.setModel(this);
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
	}

	public int getRowCount()
	{
		return 3;
	}

	public int getColumnCount()
	{
		return 3;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		switch (columnIndex)
		{
			case 0:
				return String.class;
			case 1:
				return Integer.class;
			case 2:
				return Float.class;
			default:
				return Object.class;
		}
	}

	@Override
	public String getColumnName(int column)
	{
		switch (column)
		{
			case 0:
				return "Skill Cost";
			case 1:
				return "Rank Cost";
			case 2:
				return "Max Ranks";
			default:
				throw new IndexOutOfBoundsException();
		}
	}

	public Object getValueAt(int rowIndex, int columnIndex)
	{
		int index = selectionModel.getMinSelectionIndex();
		CharacterLevelFacade level = null;
		if (index != -1)
		{
			level = levels.getElementAt(index);
		}
		SkillCost cost = SkillCost.values()[rowIndex];
		switch (columnIndex)
		{
			case 0:
				return cost;
			case 1:
				if (levels == null)
				{
					return 0;
				}
				return levels.getRankCost(level, cost);
			case 2:
				if (levels == null)
				{
					return 0.0;
				}
				return levels.getMaxRanks(level, cost);
			default:
				throw new IndexOutOfBoundsException();
		}
	}

	public void valueChanged(ListSelectionEvent e)
	{
		if (!e.getValueIsAdjusting())
		{
			fireTableRowsUpdated(0, 2);
		}
	}

}
