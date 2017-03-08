/*
 *  GMGen - A role playing utility
 *  Copyright (C) 2003 Devon D Jones
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
package gmgen.io;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jetbrains.annotations.NotNull;

/**
 * {@code VectorTable} holds 2 dimensional tables.  It is used by GMGen
 * to hold the XML tables that are being loaded
 */
public class VectorTable extends AbstractList
{
	/** The name of the external table that is being held in this class. */
	private String name;

	/** The {@code Vector} that holds the header row of the tables. */
	private final List<Object> header;

	/** The {@code Vector} that holds the data of the tables. */
	private final List<Object> rows;

	/**
	 * Creates an instance of this class with a table's name being passed to it.
	 * @param s the {@code name} of the table that is being held in this
	 *          class.
	 */
	private VectorTable(String s)
	{
		this.name = s;
		rows = new ArrayList<>();
		header = new ArrayList<>();
	}

	/**
	 * Creates an instance of this class with a default {@code String} as
	 * the {@code name} of the table that is being held.
	 */
	VectorTable()
	{
		this("");
	}

	/**
	 * Checks if the table it empty.  If the {@code row} is empty, the
	 * table is empty.
	 * @return <b>{@code true}</b> if the table is empty.
	 */
    @Override
	public boolean isEmpty()
	{
		return rows.isEmpty();
	}

	/**
	 * Sets the name of the table that is stored in the class.
	 * @param s the name of the table.
	 */
	public void setName(String s)
	{
		this.name = s;
	}

	/**
	 * Adds an {@code Object} to the 2 dimensional table.
	 * @param o an entry that will be placed in the table.
	 * @return true or false
	 */
    @Override
	public boolean add(Object o)
	{
		/* if o is a container, adds it to rows and header and returns true, else returns false.    */
		if (o instanceof AbstractList)
		{
			if (((AbstractList) o).isEmpty())
			{
				return false;
			}
			header.add(((AbstractList) o).get(0));
			rows.add(o);

			return true;
		}
		return false;
	}

	/**
	 * Calls clear on every {@code Vector} in {@code rows}.
	 */
    @Override
	public void clear()
	{

		for (int x = rows.size() - 1; x >= 0; x--)
		{
			try
			{
				((Collection) rows.get(x)).clear();
			}
			catch (Exception e)
			{
			    // TODO - Handle Exception			    
			}

			rows.clear();
		}
	}

	/**
	 * Checks to see if the paramater can be found in either {@code rows}
	 * or an {@code element} of {@code rows}.
	 * @param o the {@code Object} that needs to be found in the table.
	 * @return <b>true</b> if the object is found.  Otherwise <b>false</b>.
	 */
    @Override
	public boolean contains(Object o)
	{
		boolean found = false;

		if (rows.contains(o))
		{
			return true;
		}

		for (int x = rows.size() - 1; x >= 0; x--)
		{
			try
			{
				if (((Collection) rows.get(x)).contains(o))
				{
					found = true;

					break;
				}
			}
			catch (Exception e)
			{
			    // TODO - Handle Exception			    
			}
		}

		return found;
	}

	/**
	 * Looks up {@code Object} X  in {@code header}, and the
	 * {@code Object} {@code Y} in {@code rows} 0, and
	 * {@code returns} the resulting cell.
	 * @param X the header to look for.
	 * @param Y the item in the {@code row} to look for.
	 * @return the resulting cell from the lookup.
	 */
	public Object crossReference(Object X, Object Y)
	{
		int x;
		int y;
		List<Object> v;

		if (rows.isEmpty())
		{
			return null;
		}

		if (header.isEmpty())
		{
			return null;
		}

		x = ((List<Object>) rows.get(0)).indexOf(Y);
		y = header.indexOf(X);

		try
		{
			v = (List<Object>) rows.get(y);
		}
		catch (Exception e)
		{
			return null;
		}

		try
		{
			return v.get(x);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	/**
	 * Tests whether the paramater passed in is equal to the {@code String}
	 * representation of this class.
	 * @param o the {@code Object} that needs to be compared.
	 * @return true if the two {@code Strings} are equal.
	 */
	@Override
	public boolean equals(Object o)
	{
		/*
		 * TODO - this seems a bit strange, to have an .equals on a VectorTable that tests
		 * only the name of the vector?  It seems that .equals should behave much more like
		 * the .equals of other objects, and this should be something else?  If there is a
		 * special consideration for a Set or something that VectorTables are stored in, 
		 * then a special comparator should be built, in my opinion - thpr 10/29/06
		 */
		return o != null && o.toString()
				.equals(name);

	}

	/**
	 * Gets an entry from the table given an {@code index}.
	 * @param index the {@code index} value of the item that must be
	 *        retrieved.
	 * @return the {@code Object} that is looked up.
	 */
    @Override
	public Object get(int index)
	{
		return rows.get(index);
	}

	/**
	 * Gets the {@code hashCode} value for the {@code Vector}.
	 * @return the hash code.
	 */
	@Override
	public int hashCode()
	{
		return rows.hashCode();
	}

	/**
	 * Takes an {@code Object} out of the table if it exists in the table.
	 * @param o the {@code Object} that needs to be removed.
	 * @return <b>{@code true}</b> if the {@code Object} is removed successfully.
	 */
    @Override
	public boolean remove(Object o)
	{
		boolean success;

		success = false;

		if (this.contains(o))
		{
			if (rows.contains(o))
			{
				rows.remove(o);
				success = true;
			}
			else
			{
				for (int x = rows.size() - 1; x >= 0; x--)
				{
					try
					{
						if (((Collection<Object>) rows.get(x)).contains(o))
						{
							((Collection<Object>) rows.get(x)).remove(o);
							success = true;
						}
					}
					catch (Exception e)
					{
					    // TODO - Handle Exception					    
					}
				}
			}
		}

		return success;
	}

	/**
	 * Gets the number of items in the {@code row} {@code Vector}.
	 * @return the number of items in the {@code row}.
	 */
    @Override
	public int size()
	{
		return rows.size();
	}

	/**
	 * Stores the {@code row} {@code Vector} as an {@code array}
	 * of {@code Objects}.
	 * @return an {@code array} that consists of the items from the
	 *          {@code row} {@code Vector}.
	 */
	@Override
	public @NotNull Object[] toArray()
	{
		return rows.toArray();
	}

	/**
	 * Doesn't do much
	 * @param x the {@code index} of the item in the table to be stored.
	 * @return an {@code array} that consists of the specified item from
	 *         the table.
	 */
	public Object[] toArray(int x)
	{
		if (rows.isEmpty())
		{
			return null;
		}

		return ((Collection<Object>) rows.get(x)).toArray();
	}

	/**
	 * Gets the {@code name} of the table that this class is holding.
	 * @return the {@code name}.
	 */
	@Override
	public String toString()
	{
		return this.name;
	}
}
