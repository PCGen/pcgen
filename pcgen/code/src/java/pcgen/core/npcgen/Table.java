/*
 * Missing License Header, Copyright 2016 (C) Andrew Maitland <amaitland@users.sourceforge.net>
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
 */
package pcgen.core.npcgen;

import pcgen.base.util.WeightedCollection;
import pcgen.cdom.base.Constants;

public class Table 
{
	private final WeightedCollection<TableEntry> theData = new WeightedCollection<>();
	
	private final String theId;
	private String theName = Constants.EMPTY_STRING;

	public Table( final String anId )
	{
		theId = anId;
	}

	public void setName( final String aName )
	{
		theName = aName;
	}
	
	public String getId()
	{
		return theId;
	}
	
	public TableEntry getEntry()
	{
		return theData.getRandomValue();
	}
	
	public void add( final int aWeight, final TableEntry anEntry )
	{
		theData.add(anEntry, aWeight);
	}
	
	@Override
	public String toString()
	{
		return theName;
	}
}
