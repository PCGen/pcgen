package gmgen.io;

import java.util.AbstractList;
import java.util.Vector;

/**
 * <code>VectorTable</code> holds 2 dimensional tables.  It is used by GMGen
 * to hold the XML tables that are being loaded<p>
 * Created on February 26, 2003
 * @author  Expires 2003
 * @version 1.0
 */
public class VectorTable extends AbstractList
{
	/** The name of the external table that is being held in this class. */
	private String name;

	/** The <code>Vector</code> that holds the header row of the tables. */
	private Vector header;

	/** The <code>Vector</code> that holds the data of the tables. */
	private Vector rows;

	/**
	 * Creates an instance of this class with a table's name being passed to it.
	 * @param s the <code>name</code> of the table that is being held in this
	 *          class.
	 */
	public VectorTable(String s)
	{
		this.name = s;
		rows = new Vector();
		header = new Vector();
	}

	/**
	 * Creates an instance of this class with a default <code>String</code> as
	 * the <code>name</code> of the table that is being held.
	 */
	public VectorTable()
	{
		this("");
	}

	/**
	 * Checks if the table it empty.  If the <code>row</code> is empty, the
	 * table is empty.
	 * @return <b><code>true</code></b> if the table is empty.
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
	 * Gets the name of the table that is being stored
	 * @return the name of the table
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * Adds an <code>Object</code> to the 2 dimensional table.
	 * @param o an entry that will be placed in the table.
	 * @return true or false
	 */
    @Override
	public boolean add(Object o)
	{
		/* if o is a container, adds it to rows and header and returns true, else returns false.    */
		if (o instanceof Vector)
		{
			if (((Vector) o).isEmpty())
			{
				return false;
			}
			header.add(((Vector) o).firstElement());
			rows.add(o);

			return true;
		}
		return false;
	}

	/**
	 * Gets the correct <code>Object</code> that is referenced by the
	 * paramaters.
	 * @param Y
	 * @param Z
	 * @return the <code>Object</code> that is referenced by the two
	 *         paramaters.
	 */
	public Object backReferenceForX(Object Y, Object Z)
	{
		int x;
		int y;

		if (rows.isEmpty())
		{
			return null;
		}
		y = ((Vector) rows.firstElement()).indexOf(Y);

		try
		{
			x = ((Vector) rows.elementAt(y)).indexOf(Z);
		}
		catch (Exception e)
		{
			return null;
		}
		try
		{
			return ((Vector) rows.elementAt(y)).elementAt(x);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	/**
	 * Calls clear on every <code>Vector</code> in <code>rows</code>.
	 */
    @Override
	public void clear()
	{
		int x;

		for (x = rows.size() - 1; x >= 0; x--)
		{
			try
			{
				((Vector) rows.elementAt(x)).clear();
			}
			catch (Exception e)
			{
			    // TODO - Handle Exception			    
			}

			rows.clear();
		}
	}

	/**
	 * Checks to see if the paramater can be found in either <code>rows</code>
	 * or an <code>element</code> of <code>rows</code>.
	 * @param o the <code>Object</code> that needs to be found in the table.
	 * @return <b>true</b> if the object is found.  Otherwise <b>false</b>.
	 */
    @Override
	public boolean contains(Object o)
	{
		int x;
		boolean found = false;

		if (rows.contains(o))
		{
			return true;
		}

		for (x = rows.size() - 1; x >= 0; x--)
		{
			try
			{
				if (((Vector) rows.elementAt(x)).contains(o))
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
	 * Looks up <code>Object</code> X  in <code>header</code>, and the
	 * <code>Object</code> <code>Y</code> in <code>rows</code> 0, and
	 * <code>returns</code> the resulting cell.
	 * @param X the header to look for.
	 * @param Y the item in the <code>row</code> to look for.
	 * @return the resulting cell from the lookup.
	 */
	public Object crossReference(Object X, Object Y)
	{
		int x;
		int y;
		Vector v;

		if (rows.isEmpty())
		{
			return null;
		}

		if (header.isEmpty())
		{
			return null;
		}

		x = ((Vector) rows.firstElement()).indexOf(Y);
		y = header.indexOf(X);

		try
		{
			v = (Vector) rows.elementAt(y);
		}
		catch (Exception e)
		{
			return null;
		}

		try
		{
			return v.elementAt(x);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	/**
	 * Finds the element at a certain <code>index</code> in the
	 * <code>rows</code>.
	 * @param index the <code>index number of the item to find.
	 * @return the item at the specified <code>index</code>.
	 */
	public Object elementAt(int index)
	{
		return rows.elementAt(index);
	}

	/**
	 * Finds the item at the specified cell in the table given two
	 * <code>index</code> values.
	 * @param x
	 * @param y
	 * @return the item at the specified <code>index</code>.
	 * @exception ArrayIndexOutOfBoundsException if the array index does not
	 *            exist.
	 */
	public Object elementAt(int x, int y) throws ArrayIndexOutOfBoundsException
	{
		return ((Vector) rows.elementAt(x)).elementAt(y);
	}

	/**
	 * Tests whether the paramater passed in is equal to the <code>String</code>
	 * representation of this class.
	 * @param o the <code>Object</code> that needs to be compared.
	 * @return <b>true if the two <code>Strings</code> are equal.
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
		if (o != null && o.toString().equals(name))
		{
			return true;
		}

		return false;
	}

	/**
	 * Gets an entry from the table given an <code>index</code>.
	 * @param index the <code>index</code> value of the item that must be
	 *        retrieved.
	 * @return the <code>Object</code> that is looked up.
	 */
    @Override
	public Object get(int index)
	{
		return rows.get(index);
	}

	/**
	 * Gets the <code>hashCode</code> value for the <code>Vector</code>.
	 * @return the hash code.
	 */
	@Override
	public int hashCode()
	{
		return rows.hashCode();
	}

	/**
	 * Takes an <code>Object</code> out of the table if it exists in the table.
	 * @param o the <code>Object</code> that needs to be removed.
	 * @return <b><code>true</code></b> if the <code>Object</code> is removed
	 *         successfully.
	 */
    @Override
	public boolean remove(Object o)
	{
		int x;
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
				for (x = rows.size() - 1; x >= 0; x--)
				{
					try
					{
						if (((Vector) rows.elementAt(x)).contains(o))
						{
							((Vector) rows.elementAt(x)).remove(o);
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
	 * Gets the number of items in the <code>row</code> <code>Vector</code>.
	 * @return the number of items in the <code>row</code>.
	 */
    @Override
	public int size()
	{
		return rows.size();
	}

	/**
	 * Gets the number of items in the <code>header</code> <code>Vector</code>.
	 * @return the number of items in the <code>header</code>.
	 */
	public int sizeX()
	{
		return ((Vector) header.firstElement()).size();
	}

	/**
	 * Gets the number of items in the <code>row</code> <code>Vector</code>.
	 * It is the same as the <code>size()</code> method in this class.
	 * @return the number of items in the <code>row</code>.
	 */
	public int sizeY()
	{
		return rows.size();
	}

	/**
	 * Stores the <code>row</code> <code>Vector</code> as an <code>array</code>
	 * of <code>Objects</code>.
	 * @return an <code>array</code> that consists of the items from the
	 *          <code>row</code> <code>Vector</code>.
	 */
    @Override
	public Object[] toArray()
	{
		return rows.toArray();
	}

	/**
	 * Doesn't do much
	 * @param x the <code>index</code> of the item in the table to be stored.
	 * @return an <code>array</code> that consists of the specified item from
	 *         the table.
	 */
	public Object[] toArray(int x)
	{
		if (rows.isEmpty())
		{
			return null;
		}

		return ((Vector) rows.elementAt(x)).toArray();
	}

	/**
	 * Gets the <code>name</code> of the table that this class is holding.
	 * @return the <code>name</code>.
	 */
	@Override
	public String toString()
	{
		return this.name;
	}
}
