/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.testsupport;

import java.io.StringReader;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import junit.framework.TestCase;
import pcgen.base.format.ArrayFormatManager;
import pcgen.base.format.BooleanManager;
import pcgen.base.format.NumberManager;
import pcgen.base.formatmanager.ArrayFormatFactory;
import pcgen.base.formatmanager.CompoundFormatFactory;
import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.ManagerFactory;
import pcgen.base.formula.parse.FormulaParser;
import pcgen.base.formula.parse.ParseException;
import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.util.FormatManager;
import pcgen.base.util.ValueStore;

public final class TestUtilities
{
	public static final double SMALL_ERROR = Math.pow(10, -10);

	public static final Number[] EMPTY_ARRAY = {};

	public static final ManagerFactory EMPTY_MGR_FACTORY = new ManagerFactory()
	{
	};

	public static final ArrayFormatFactory ARRAY_FACTORY =
			new ArrayFormatFactory('\n', ',');
	public static final ArrayFormatManager<Number> NUMBER_ARRAY_MANAGER =
			new ArrayFormatManager<>(new NumberManager(), '\n', ',');
	public static final FormatManager<Boolean[]> BOOLEAN_ARRAY_MANAGER =
			new ArrayFormatManager<>(new BooleanManager(), ',', '|');
	
	public static final CompoundFormatFactory COMPOUND_MANAGER =
			new CompoundFormatFactory(',', '|');

	public static final Class<Float> FLOAT_CLASS = Float.class;
	public static final Class<Integer> INTEGER_CLASS = Integer.class;
	public static final Class<Double> DOUBLE_CLASS = Double.class;

	@SuppressWarnings("unchecked")
	public static final Class<Number[]> NUMBER_ARRAY_CLASS =
			(Class<Number[]>) Array.newInstance(FormatUtilities.NUMBER_CLASS, 0).getClass();
	@SuppressWarnings("unchecked")
	public static final Class<Object[]> OBJECT_ARRAY_CLASS =
			(Class<Object[]>) Array.newInstance(Object.class, 0).getClass();
	@SuppressWarnings("unchecked")
	public static final Class<Boolean[]> BOOLEAN_ARRAY_CLASS =
			(Class<Boolean[]>) Array.newInstance(FormatUtilities.BOOLEAN_CLASS, 0).getClass();
	@SuppressWarnings("unchecked")
	public static final Class<Integer[]> INTEGER_ARRAY_CLASS =
			(Class<Integer[]>) Array.newInstance(INTEGER_CLASS, 0).getClass();

	private TestUtilities()
	{
		//Do not instantiate utility class
	}

	public static SimpleNode doParse(String formula)
	{
		try
		{
			return new FormulaParser(new StringReader(formula)).query();
		}
		catch (ParseException e)
		{
			TestCase
				.fail("Encountered Unexpected Exception: " + e.getMessage());
			return null;
		}
	}

	public static boolean doubleEqual(double d1, double d2, double delta)
	{
		if (delta < 0)
		{
			throw new IllegalArgumentException(
				"Delta for doubleEqual cannot be < 0: " + delta);
		}
		double diff = d1 - d2;
		return ((diff >= 0) && (diff < delta))
			|| ((diff < 0) && (diff > -delta));
	}


	/**
	 * Utility method for Unit tests to invoke private constructors
	 * 
	 * @param clazz The class we're gonig to invoke the constructor on
	 * @return An instance of the class
	 */
	public static Object invokePrivateConstructor(Class<?> clazz)
	{
		Constructor<?> constructor = null; 
		try
		{
			constructor = clazz.getDeclaredConstructor();
		}
		catch (NoSuchMethodException e)
		{
			System.err.println("Constructor for [" + clazz.getName() + "] does not exist");
		}
		
		constructor.setAccessible(true);
		Object instance = null;
		
		try
		{
			instance = constructor.newInstance();
		}
		catch (InvocationTargetException | InstantiationException ite)
		{
			System.err.println("Instance creation failed with [" + ite.getCause() + "]");
		}
		catch (IllegalAccessException iae)
		{
			System.err.println("Instance creation failed due to access violation.");
		}

		return instance;
	}

	public static ValueStore getDefaultValueStore()
	{
		HashMap<String, Object> map = new HashMap<>();
		map.put("NUMBER", 0);
		map.put("STRING", "");
		map.put("BOOLEAN", Boolean.FALSE);
		return map::get;
	}

}
