/*
 * Copyright 2004 (C) Ross M. Lodge
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
package plugin.initiative.gui;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * This class assists in managing a set of columns for a custom
 * table model.  It is <strong>not</strong> a {@code TableColumnModel},
 * but a utility class for maintaining column information inside a {@code TableModel}.
 * </p>
 *
 * <p>Current Ver: $Revision$</p>
 * 
 * @author LodgeR
 */
public class TableColumnInformation
{

	/**
	 * <p>
	 * Internal utility for maintaining information about a column.
	 * </p>
	 */
	private static class ColStruct
	{
		/** The runtime class the column stores. */
		public Class<?> columnClass = null;
		/** A string key by which the column is identified. */
		public String columnKey = null;
		/** The default value used when a new row is created. */
		public Object defaultValue = null;
		/** Is the column editable. */
		public boolean editable = false;
		/** The column heading label */
		public String label = null;

		/**
		 * <p>
		 * Default, empty constructor
		 * </p>
		 */
		public ColStruct()
		{
			// Do Nothing
		}

		/**
		 * <p>
		 * A constructor that fills in all the information.
		 * </p>
		 * 
		 * @param pcolumnKey
		 * @param pcolumnClass
		 * @param pdefaultValue
		 * @param peditable
		 * @param plabel
		 */
		public ColStruct(String pcolumnKey, Class<?> pcolumnClass,
			Object pdefaultValue, boolean peditable, String plabel)
		{
			this.columnKey = pcolumnKey;
			this.columnClass = pcolumnClass;
			this.defaultValue = pdefaultValue;
			this.editable = peditable;
			this.label = plabel;
		}
	}

	/** An arraylist of {@code ColStructs} */
	private List<ColStruct> columns = null;

	/**
	 * <p>
	 * A constructor, specifying how many columns the table
	 * will initially have.
	 * </p>
	 * 
	 * @param initialCapacity
	 */
	public TableColumnInformation(int initialCapacity)
	{
		columns = new ArrayList<>(initialCapacity);
	}

	/**
	 * 
	 * <p>
	 * Adds a column at the specified index
	 * </p>
	 * 
	 * @param columnIndex
	 * @param columnKey
	 * @param columnClass
	 * @param defaultValue
	 * @param editable
	 * @param label
	 */
	public void addColumn(int columnIndex, String columnKey,
		Class<?> columnClass, Object defaultValue, boolean editable,
		String label)
	{
		columns.add(columnIndex, new ColStruct(columnKey, columnClass,
			defaultValue, editable, label));
	}

	/**
	 * 
	 * <p>
	 * Adds a column to the end of the list
	 * </p>
	 * 
	 * @param columnKey
	 * @param columnClass
	 * @param defaultValue
	 * @param editable
	 * @param label
	 */
	public void addColumn(String columnKey, Class<?> columnClass,
		Object defaultValue, boolean editable, String label)
	{
		columns.add(new ColStruct(columnKey, columnClass, defaultValue,
			editable, label));
	}

	/**
	 * Shortcut to get the index of a column based on the string
	 * key value.
	 *
	 * @param key The key string
	 * @return The integer index of the column
	 */
	public int columnFromKey(String key)
	{
		int returnValue = -1;

		for (int i = 0; i < columns.size(); i++)
		{
			if (columns.get(i).columnKey.equals(key))
			{
				returnValue = i;
				break;
			}
		}

		return returnValue;
	}

	/**
	 * <p>
	 * Gets the column's class by index
	 * </p>
	 * 
	 * @param column
	 * @return Class
	 */
	public Class<?> getClass(int column)
	{
		return columns.get(column).columnClass;
	}

	/**
	 * 
	 * <p>
	 * Gets the column's class by key
	 * </p>
	 * 
	 * @param key
	 * @return Class
	 */
	public Class<?> getClass(String key)
	{
		return getClass(columnFromKey(key));
	}

	/**
	 * 
	 * <p>
	 * Gets the count of columns
	 * </p>
	 * 
	 * @return count of columns
	 */
	public int getColumCount()
	{
		return columns.size();
	}

	/**
	 * 
	 * <p>
	 * Gets the default value by index.
	 * </p>
	 * 
	 * @param column
	 * @return Object
	 */
	public Object getDefaultValue(int column)
	{
		return columns.get(column).defaultValue;
	}

	/**
	 * 
	 * <p>
	 * Gets the default value by key
	 * </p>
	 * 
	 * @param key
	 * @return Object
	 */
	public Object getDefaultValue(String key)
	{
		return getDefaultValue(columnFromKey(key));
	}

	/**
	 * 
	 * <p>
	 * Gets the key for a column by index
	 * </p>
	 * 
	 * @param column
	 * @return key
	 */
	public String getKey(int column)
	{
		return columns.get(column).columnKey;
	}

	/**
	 * <p>
	 * Gets the label for a column by index
	 * </p>
	 * 
	 * @param column
	 * @return label
	 */
	public String getLabel(int column)
	{
		return columns.get(column).label;
	}

	/**
	 * <p>
	 * Gets the label for a column by key
	 * </p>
	 * 
	 * @param key
	 * @return label
	 */
	public String getLabel(String key)
	{
		return getLabel(columnFromKey(key));
	}

	/**
	 * <p>
	 * Gets the editable status by index.
	 * </p>
	 * 
	 * @param column
	 * @return TRUE if editable, else FALSE
	 */
	public boolean isColumnEditable(int column)
	{
		return columns.get(column).editable;
	}

	/**
	 * <p>
	 * Gets the editable status by key
	 * </p>
	 * 
	 * @param key
	 * @return TRUE if editable, else FALSE
	 */
	public boolean isColumnEditable(String key)
	{
		return isColumnEditable(columnFromKey(key));
	}

	/**
	 * <p>
	 * Removes all columns
	 * </p>
	 * 
	 *
	 */
	public void removeAll()
	{
		columns.clear();
	}

	/**
	 * <p>
	 * Removes the column at index.
	 * </p>
	 * 
	 * @param columnIndex
	 */
	public void removeColumn(int columnIndex)
	{
		columns.remove(columnIndex);
	}

	/**
	 * <p>
	 * Removes the column with key.
	 * </p>
	 * 
	 * @param key
	 */
	public void removeColumn(String key)
	{
		columns.remove(columnFromKey(key));
	}

	/**
	 * <p>
	 * Sets the class of column by index
	 * </p>
	 * 
	 * @param column
	 * @param pClass
	 */
	public void setClass(int column, Class<?> pClass)
	{
		columns.get(column).columnClass = pClass;
	}

	/**
	 * <p>
	 * Sets the class of column by key
	 * </p>
	 * 
	 * @param key
	 * @param pClass
	 */
	public void setClass(String key, Class<?> pClass)
	{
		setClass(columnFromKey(key), pClass);
	}

	/**
	 * <p>
	 * Sets the editable status of column by index
	 * </p>
	 * 
	 * @param column
	 * @param editable
	 */
	public void setColumnEditable(int column, boolean editable)
	{
		columns.get(column).editable = editable;
	}

	/**
	 * <p>
	 * Sets the editable status of column by key
	 * </p>
	 * 
	 * @param key
	 * @param editable
	 */
	public void setColumnEditable(String key, boolean editable)
	{
		setColumnEditable(columnFromKey(key), editable);
	}

	/**
	 * <p>
	 * Sets the default value by index
	 * </p>
	 * 
	 * @param column
	 * @param value
	 */
	public void setDefaultValue(int column, Object value)
	{
		columns.get(column).defaultValue = value;
	}

	/**
	 * <p>
	 * Sets the default value of column by key
	 * </p>
	 * 
	 * @param key
	 * @param value
	 */
	public void setDefaultValue(String key, Object value)
	{
		setDefaultValue(columnFromKey(key), value);
	}

	/**
	 * <p>
	 * Sets the key of a column by index
	 * </p>
	 * 
	 * @param column
	 * @param key
	 */
	public void setKey(int column, String key)
	{
		columns.get(column).columnKey = key;
	}

	/**
	 * <p>
	 * Sets the key of a column by key
	 * </p>
	 * 
	 * @param key
	 * @param keyValue
	 */
	public void setKey(String key, String keyValue)
	{
		setKey(columnFromKey(key), keyValue);
	}

	/**
	 * <p>
	 * Sets the label of a column by index
	 * </p>
	 * 
	 * @param column
	 * @param label
	 */
	public void setLabel(int column, String label)
	{
		columns.get(column).label = label;
	}

	/**
	 * <p>
	 * Sets the label of a column by key
	 * </p>
	 * 
	 * @param key
	 * @param label
	 */
	public void setLabel(String key, String label)
	{
		setLabel(columnFromKey(key), label);
	}

}
