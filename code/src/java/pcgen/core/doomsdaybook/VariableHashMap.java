/*
 *  RPGeneration - A role playing utility generate interesting things
 *  Copyright (C) 2002 Devon Jones
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
package pcgen.core.doomsdaybook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * {@code VariableHashMap} is a collection of variables (key/value
 * pairs) and DataElements optimised for quick lookup and updating. Note
 * there are three structures here, the principle map of variables, a
 * secondary map of DataElement objects and a list of Operation objects.
 */
public class VariableHashMap extends HashMap<String, String>
{
    private final Collection<Operation> initialize = new ArrayList<>();
    private final Map<String, DataElement> dataElements;

    /**
     * Creates a new instance of VariableHashMap
     */
    public VariableHashMap()
    {
        this.dataElements = new HashMap<>();
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
        DataElement de = dataElements.get(key);

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
    String getVal(String key) throws variableException
    {
        String value = get(key);

        if (value == null)
        {
            throw new variableException("Variable " + key + " does not exist, cannot get value");
        }

        return value;
    }

    /**
     * Set the value for an existing variable. If no entry is
     * present for the key, an exception will be thrown.
     *
     * @param key   The key of the variable to be set.
     * @param value The new value for the variable
     * @throws variableException When no entry exists for the supplied key.
     */
    private void setVar(String key, String value) throws variableException
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
        dataElements.put(key, dataElement);
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
    private String addVar(String key, int add) throws variableException
    {
        String value = get(key);

        if (value == null)
        {
            throw new variableException("Variable " + key + " does not exist, cannot add to value");
        }

        int val;

        if (value.isEmpty())
        {
            val = 0;
        } else
        {
            val = Integer.parseInt(get(key));
        }

        return put(key, Integer.toString(val + add));
    }

    /**
     * Divide the value of an existing variable by an amount. If no entry is
     * present for the key, an exception will be thrown. NB: Integer divison
     * is used.
     *
     * @param key    The key of the variable to be updated.
     * @param divide The amount to divide the variable's value by.
     * @return The new value of the variable.
     * @throws variableException When no entry exists for the supplied key.
     */
    private String divideVar(String key, int divide) throws variableException
    {
        String value = get(key);

        if (value == null)
        {
            throw new variableException("Variable " + key + " does not exist, cannot divide by value");
        }

        int val;

        if (value.isEmpty())
        {
            val = 0;
        } else
        {
            val = Integer.parseInt(get(key));
        }

        return put(key, Integer.toString(val / divide));
    }

    /**
     * Perform the supplied Operations in sequence.
     *
     * @param ops The Operations to be performed.
     * @throws variableException When no entry exists for an Operation's key.
     */
    private void doOperation(Collection<? extends Operation> ops) throws variableException
    {
        for (Operation op : ops)
        {
            doOperation(op);
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
    private void doOperation(Operation op) throws variableException
    {
        String type = op.getType();
        String key = op.getKey();
        String value = op.getValue();

        switch (type)
        {
            case "Set":
                value = parse(value);
                setVar(key, value);
                break;
            case "Add":
            {
                int val = Integer.parseInt(parse(value));
                addVar(key, val);
                break;
            }
            case "Subtract":
            {
                int val = Integer.parseInt(parse(value));
                subtractVar(key, val);
                break;
            }
            case "Multiply":
            {
                int val = Integer.parseInt(parse(value));
                multiplyVar(key, val);
                break;
            }
            case "Divide":
            {
                int val = Integer.parseInt(parse(value));
                divideVar(key, val);
                break;
            }
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
     * @param key      The key of the variable to be updated.
     * @param multiply The amount to multiply the variable's value by.
     * @return The new value of the variable.
     * @throws variableException When no entry exists for the supplied key.
     */
    private String multiplyVar(String key, int multiply) throws variableException
    {
        String value = get(key);

        if (value == null)
        {
            throw new variableException("Variable " + key + " does not exist, cannot multiply by value");
        }

        int val;

        if (value.isEmpty())
        {
            val = 0;
        } else
        {
            val = Integer.parseInt(get(key));
        }

        return put(key, Integer.toString(val * multiply));
    }

    /**
     * Parse a value substituting the first variable referred to as
     * ${varname} with the variable's value.
     *
     * @param val The value to be parsed
     * @return The parsed value.
     */
    private String parse(String val)
    {
        String retString = val;

        if (val.matches("\\$\\{.*?}.*"))
        {
            String var = val.substring(val.indexOf("${") + 2, val.indexOf('}'));
            String value = get(var);

            if (value == null)
            {
                value = "";
            }

            retString = val.replaceFirst("\\$\\{.*?}", value);
        }

        return retString;
    }

    /**
     * Subtract an amount to an existing variable. If no entry is
     * present for the key, an exception will be thrown.
     *
     * @param key      The key of the variable to be updated.
     * @param subtract The amount to subtract from the variable.
     * @return The new value of the variable.
     * @throws variableException When no entry exists for the supplied key.
     */
    private String subtractVar(String key, int subtract) throws variableException
    {
        String value = get(key);

        if (value == null)
        {
            throw new variableException("Variable " + key + " does not exist, cannot subtract from value");
        }

        int val;

        if (value.isEmpty())
        {
            val = 0;
        } else
        {
            val = Integer.parseInt(get(key));
        }

        return put(key, Integer.toString(val - subtract));
    }
}
