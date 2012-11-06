/*
 *  Initiative - A role playing utility to track turns
 *  Copyright (C) 2002 Devon D Jones
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 *  The author of this program grants you the ability to use this code
 *  in conjunction with code that is covered under the Open Gaming License
 *
 *  InitHolderComperator.java
 *
 *  Created on January 16, 2002, 1:13 PM
 */
package gmgen.plugin;

import java.util.Comparator;

/**
 * Compares InitHolder objects
 *@author     devon
 *@since    April 7, 2003
 *@version $Revision$
 */
public class InitHolderComperator implements Comparator<InitHolder>
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
		InitHolder i1 = o1;
		InitHolder i2 = o2;
		SystemInitiative init1 = i1.getInitiative();
		SystemInitiative init2 = i2.getInitiative();
		Integer initval1 = Integer.valueOf(init1.getCurrentInitiative());
		Integer initval2 = Integer.valueOf(init2.getCurrentInitiative());

		int comp = initval2.compareTo(initval1);
		if (comp != 0)
		{
			return comp;
		}
		Integer dexval1 = Integer.valueOf(init1.getAttribute().getValue());
		Integer dexval2 = Integer.valueOf(init2.getAttribute().getValue());

		return dexval1.compareTo(dexval2);
	}
}
