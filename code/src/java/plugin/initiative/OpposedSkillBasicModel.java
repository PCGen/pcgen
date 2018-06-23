/*
 *  Initiative - A role playing utility to track turns
 *  Copyright (C) 2004 Ross M. Lodge
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
 *  OpposedSkillAvailableModel.java
 */

package plugin.initiative;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.table.AbstractTableModel;

import gmgen.plugin.PcgCombatant;
import plugin.initiative.gui.TableColumnInformation;

/**
 * <p>
 * Essentialy sets up a list of {@code PcgCombatant} items.
 * </p>
 *
 * <p>
 * </p>
 * <p>
 * </p>
 * <p>
 * </p>
 */
public class OpposedSkillBasicModel extends AbstractTableModel
{

	/**
	 * <p>
	 * A "wrapper" class for the combatants
	 * </p>
	 */
	protected class InitWrapper
	{

		PcgCombatant initiative = null;

		/** 
		 * Constructor 
		 * @param init
		 */
		public InitWrapper(PcgCombatant init)
		{
			initiative = init;
		}

	}

	/** Columns for the table */
	protected TableColumnInformation columns = new TableColumnInformation(10);
	/** Combatants */
	protected TreeMap combatants = new TreeMap();

	/** Constructor -- creates columns */
	public OpposedSkillBasicModel()
	{
		columns.addColumn("COMBATANT", String.class, null, false, "Combatant");
	}

	/**
	 * <p>
	 * Constructs columns and builds the combatant list.
	 * </p>
	 *
	 * @param combatantList
	 */
	public OpposedSkillBasicModel(List combatantList)
	{
		this();
		buildCombatantList(combatantList);
	}

	/**
	 * <p>
	 * Builds the combatant list
	 * </p>
	 *
	 * @param combatantList
	 */
	protected void buildCombatantList(List combatantList)
	{
		for (Iterator i = combatantList.iterator(); i.hasNext();)
		{
			Object o = i.next();
			if (o != null && o instanceof PcgCombatant)
			{
				PcgCombatant cbt = (PcgCombatant) o;
				addCombatant(cbt);
			}
		}
	}

	@Override
	public Class getColumnClass(int columnIndex)
	{
		return columns.getClass(columnIndex);
	}

	@Override
	public int getColumnCount()
	{
		return columns.getColumCount();
	}

	@Override
	public String getColumnName(int column)
	{
		return columns.getLabel(column);
	}

	@Override
	public int getRowCount()
	{
		return Math.max(combatants.size(), 1);
	}

	/**
	 * <p>
	 * Gets a row entry for the specified index.
	 * </p>
	 *
	 * @param rowIndex
	 * @return InitWrapper
	 */
	protected InitWrapper getRowEntry(int rowIndex)
	{
		InitWrapper returnValue = null;
		if (rowIndex < combatants.size())
		{
			returnValue = (InitWrapper) ((Map.Entry) combatants.entrySet().toArray()[rowIndex]).getValue();
		}
		return returnValue;
	}

	/**
	 * <p>
	 * Returns the index for the given name
	 * </p>
	 *
	 * @param name
	 * @return index
	 */
	protected int getIndexOf(String name)
	{
		int returnValue = -1;
		int counter = -1;
		for (Iterator i = combatants.keySet().iterator(); i.hasNext() && returnValue < 0;)
		{
			counter++;
			if (i.next().equals(name))
			{
				returnValue = counter;
			}
		}
		return returnValue;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		Object returnValue = null;
		if (rowIndex < combatants.size())
		{
			InitWrapper entry = getRowEntry(rowIndex);
			switch (columnIndex)
			{
				case 0:
					returnValue = entry.initiative.getName();
					break;
				default:
					//Case not caught, should this cause an error?
					break;
			}
		}
		return returnValue;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		boolean returnValue = false;
		if (rowIndex < getRowCount())
		{
			returnValue = columns.isColumnEditable(columnIndex);
		}
		return returnValue;
	}

	/**
	 * <p>
	 * Adds the specified combatant
	 * </p>
	 *
	 * @param combatant
	 */
	public void addCombatant(PcgCombatant combatant)
	{
		combatants.put(combatant.getName(), new InitWrapper(combatant));
		int rowIndex = getIndexOf(combatant.getName());
		fireTableRowsInserted(rowIndex, rowIndex);
	}

	/**
	 * <p>
	 * Removes the specified combatant
	 * </p>
	 *
	 * @param rowIndex
	 */
	public void removeCombatant(int rowIndex)
	{
		if (rowIndex < combatants.size())
		{
			InitWrapper entry = getRowEntry(rowIndex);
			combatants.remove(entry.initiative.getName());
			fireTableRowsDeleted(rowIndex, rowIndex);
		}
	}

	/**
	 * <p>
	 * Removes the specified combatant by name
	 * </p>
	 *
	 * @param name
	 */
	public void removeCombatant(String name)
	{
		int rowIndex = getIndexOf(name);
		combatants.remove(name);
		fireTableRowsDeleted(rowIndex, rowIndex);
	}

	/**
	 * <p>
	 * Gets the specified combatant.
	 * </p>
	 *
	 * @param rowIndex
	 * @return PcgCombatant
	 */
	public PcgCombatant getCombatant(int rowIndex)
	{
		PcgCombatant returnValue = null;
		if (rowIndex < combatants.size())
		{
			returnValue = getRowEntry(rowIndex).initiative;
		}
		return returnValue;
	}

}
