/*
 *  Initiative - A role playing utility to track turns
 *  Copyright (C) 2002 Devon D Jones
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package gmgen.plugin;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Compares InitHolder objects
 */
public class InitHolderComperator implements Comparator<InitHolder>, Serializable
{
	/**
	 *  Description of the Method
	 *
	 *@param  o1  Object 1 to compare
	 *@param  o2  Object 2 to compare
	 *@return     the comparion between the two (in java.util.Comperator format)
	 */
	@Override
	public int compare(InitHolder o1, InitHolder o2)
	{
		SystemInitiative init1 = o1.getInitiative();
		SystemInitiative init2 = o2.getInitiative();
		Integer initval1 = init1.getCurrentInitiative();
		Integer initval2 = init2.getCurrentInitiative();

		int comp = initval2.compareTo(initval1);
		if (comp != 0)
		{
			return comp;
		}
		Integer dexval1 = init1.getAttribute().getValue();
		Integer dexval2 = init2.getAttribute().getValue();

		return dexval1.compareTo(dexval2);
	}
}
