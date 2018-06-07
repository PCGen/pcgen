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

import java.util.List;

/**
 * <p>
 * Overrides {@code OpposedSkillBasicModel} to provide a column displaying
 * combatant type.
 * </p>
 * 
 * <p>
 * </p>
 * <p>
 * </p>
 * <p>
 * </p>
 * 
 */
public class OpposedSkillTypeModel extends OpposedSkillBasicModel
{

	/**
	 * <p>
	 * Base constructor -- adds columns
	 * </p>
	 */
	public OpposedSkillTypeModel()
	{
		super();
		columns.addColumn("TYPE", String.class, null, false, "Type");
	}

	/**
	 * <p>
	 * Constructor builds the combatant list, adds columns
	 * </p>
	 * 
	 * @param combatantList
	 */
	public OpposedSkillTypeModel(List combatantList)
	{
		super(combatantList);
		columns.addColumn("TYPE", String.class, null, false, "Type");
	}

    @Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		Object returnValue = null;
		if (rowIndex < getRowCount() && columnIndex == 2)
		{
			InitWrapper entry = getRowEntry(rowIndex);
			returnValue = entry.initiative.getCombatantType();
		}
		else
		{
			returnValue = super.getValueAt(rowIndex, columnIndex);
		}
		return returnValue;
	}
}
