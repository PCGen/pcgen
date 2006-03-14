/*
 *  RPGeneration - A role playing utility generate interesting things
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
 * VariableHashMap.java
 *
 * Created on November 1, 2002, 1:15 PM
 */
package plugin.doomsdaybook.util;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <code>VariableHashMap</code> is a collection of variables (key/value 
 * pairs) and DataElements optimised for quick lookup and updating. Note
 * there are three structures here, the principle map of variables, a
 * secondary map of DataElement objects and a list of Operation objects.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author devon
 * @version $Revision$
 */
public class VariableHashMap extends HashMap
{
	private ArrayList initialize = new ArrayList();
	private HashMap dataElements;

	/** Creates a new instance of VariableHashMap */
	public VariableHashMap()
	{
		this.dataElements = new HashMap();
	}

	/**
	 * Create a new instance of VariableHashMap using a pre generated 
	 * map of DataElement objects. NB: The passed in map will be 
	 * retained by the new VariableHashMap. 
	 * @param dataElements 
	 */
	public VariableHashMap(HashMap dataElements)
	{
		this.dataElements = dataElements;
	}

	/**
	 * Retrieve the data element for the given key. If no entry is 
	 * present for the key an exception will be thrown.
	 * 
	 * @param key The key of the element to be retrieved. 
	 * @return The data element. 
	 * @throws Exception When no entry exists for the supplied key.
	 */
	public DataElement getDataElement(String key) throws Exception
	{
		DataElement de = (DataElement) dataElements.get(key);

		if (de == null)
		{
			throw new Exception("Data Set " + key + " Does Not Exist");
		}

		return de;
	}

	/**
	 * Retrieve the variable value for the given key. If no entry is 
	 * present for the key an exception will be thrown.
	 * 
	 * @param key The key of the variable to be retrieved. 
	 * @return The value fo the variable.
	 * @throws variableException When no entry exists for the supplied key.
	 */
	public String getVal(String key) throws variableException
	{
		if (get(key) == null)
		{
			throw new variableException("Variable " + key + " does not exist, cannot get value");
		}

		return (String) get(key);
	}

	/**
	 * Set the value for an existing variable. If no entry is 
	 * present for the key, an exception will be thrown.
	 * 
	 * @param key The key of the variable to be set.
	 * @param value The new value for the variable
	 * @throws variableException When no entry exists for the supplied key.
	 */
	public void setVar(String key, String value) throws variableException
	{
		if (get(key) == null)
		{
			throw new variableException("Variable " + key + " does not exist, cannot set value");
		}

		put(key, value);
	}

	/**
	 * Add a new DataElement to the map of DataElement objects. 
	 * The dataElement's Id will be used as the key for the map.
	 * 
	 * @param dataElement The DataElement object to be added.
	 */
	public void addDataElement(DataElement dataElement)
	{
		String key = dataElement.getId();
		dataElement.trimToSize();
		dataElements.put(key, dataElement);
	}

	/**
	 * Add an Operation to the initialize list
	 * 
	 * @param op The Operation to be added.
	 */
	public void addInitialOperations(Operation op)
	{
		initialize.add(op);
	}

	/**
	 * Add an amount to an existing variable. If no entry is 
	 * present for the key, an exception will be thrown.
	 * 
	 * @param key The key of the variable to be updated.
	 * @param add The amount to add to the variable.
	 * @return The new value of the variable.
	 * @throws variableException When no entry exists for the supplied key.
	 */
	public String addVar(String key, int add) throws variableException
	{
		if (get(key) == null)
		{
			throw new variableException("Variable " + key + " does not exist, cannot add to value");
		}

		if (get(key) == "")
		{
			put(key, "0");
		}

		int val = Integer.parseInt((String) get(key));
		val += add;
		put(key, val + "");

		return val + "";
	}

	/**
	 * Divide the value of an existing variable by an amount. If no entry is 
	 * present for the key, an exception will be thrown. NB: Integer divison 
	 * is used.
	 * 
	 * @param key The key of the variable to be updated.
	 * @param divide The amount to divide the variable's value by.
	 * @return The new value of the variable.
	 * @throws variableException When no entry exists for the supplied key.
	 */
	public String divideVar(String key, int divide) throws variableException
	{
		if (get(key) == null)
		{
			throw new variableException("Variable " + key + " does not exist, cannot divide by value");
		}

		if (get(key) == "")
		{
			put(key, "0");
		}

		int val = Integer.parseInt((String) get(key));
		val /= divide;
		put(key, val + "");

		return val + "";
	}

	/**
	 * Perform the supplied Operations in sequence.
	 * 
	 * @param ops The Operations to be performed.
	 * @throws variableException When no entry exists for an Operation's key.
	 */
	public void doOperation(ArrayList ops) throws variableException
	{
		for (int i = 0; i < ops.size(); i++)
		{
			doOperation((Operation) ops.get(i));
		}
	}

	/**
	 * Perform the Operation. The Operation defines, the action, the variable 
	 * to be affected and the value to be used in the operation. If no entry is 
	 * present for the key expected by the Operation, an exception will be 
	 * thrown.
	 * 
	 * @param op The Operation to be performed.
	 * @throws variableException When no entry exists for the Operation's key.
	 */
	public void doOperation(Operation op) throws variableException
	{
		String type = op.getType();
		String key = op.getKey();
		String value = op.getValue();

		if (type.equals("Set"))
		{
			value = parse(value);
			setVar(key, value);
		}
		else if (type.equals("Add"))
		{
			int val = Integer.parseInt(parse(value));
			addVar(key, val);
		}
		else if (type.equals("Subtract"))
		{
			int val = Integer.parseInt(parse(value));
			subtractVar(key, val);
		}
		else if (type.equals("Multiply"))
		{
			int val = Integer.parseInt(parse(value));
			multiplyVar(key, val);
		}
		else if (type.equals("Divide"))
		{
			int val = Integer.parseInt(parse(value));
			divideVar(key, val);
		}
	}

	/**
	 * Perform the initialization Operations in sequence.
	 * 
	 * @throws variableException When no entry exists for an Operation's key.
	 */
	public void initialize() throws variableException
	{
		doOperation(initialize);
	}

	/**
	 * Multiply the value of an existing variable by an amount. If no entry is 
	 * present for the key, an exception will be thrown. NB: Integer divison 
	 * is used.
	 * 
	 * @param key The key of the variable to be updated.
	 * @param multiply The amount to multiply the variable's value by.
	 * @return The new value of the variable.
	 * @throws variableException When no entry exists for the supplied key.
	 */
	public String multiplyVar(String key, int multiply)
		throws variableException
	{
		if (get(key) == null)
		{
			throw new variableException("Variable " + key + " does not exist, cannot multiply by value");
		}

		if (get(key) == "")
		{
			put(key, "0");
		}

		int val = Integer.parseInt((String) get(key));
		val *= multiply;
		put(key, val + "");

		return val + "";
	}

	/**
	 * Parse a value substituting the first variable referred to as 
	 * ${varname} with the variable's value.
	 *  
	 * @param val The value to be parsed
	 * @return The parsed value.
	 */
	public String parse(String val)
	{
		String retString = val;

		if (val.matches("\\$\\{.*?\\}.*"))
		{
			String var = val.substring(val.indexOf("${") + 2, val.indexOf("}"));
			String value = (String) get(var);

			if (value == null)
			{
				value = "";
			}

			retString = val.replaceFirst("\\$\\{.*?\\}", value);
		}

		return retString;
	}

	/**
	 * Subtract an amount to an existing variable. If no entry is 
	 * present for the key, an exception will be thrown.
	 * 
	 * @param key The key of the variable to be updated.
	 * @param subtract The amount to subtract from the variable.
	 * @return The new value of the variable.
	 * @throws variableException When no entry exists for the supplied key.
	 */
	public String subtractVar(String key, int subtract)
		throws variableException
	{
		if (get(key) == null)
		{
			throw new variableException("Variable " + key + " does not exist, cannot subtract from value");
		}

		if (get(key) == "")
		{
			put(key, "0");
		}

		int val = Integer.parseInt((String) get(key));
		val -= subtract;
		put(key, val + "");

		return val + "";
	}
}
