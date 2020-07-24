/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.testsupport;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import pcgen.base.format.ArrayFormatManager;
import pcgen.base.format.NumberManager;
import pcgen.base.formatmanager.ArrayFormatFactory;
import pcgen.base.formatmanager.CompoundFormatFactory;

/**
 * Support class for running Junit tests
 */
public final class TestSupport
{

	public static final Number[] ARR_N3_4_5 = {Integer.valueOf(-3), Integer.valueOf(4), Integer.valueOf(5)};
	public static final Number[] ARR_N3_4P1_5 = {Integer.valueOf(-3), Double.valueOf(4.1), Integer.valueOf(5)};
	public static final Number[] ARR_1P4 = {Double.valueOf(1.4)};
	public static final Number[] ARR_N3 = {Integer.valueOf(-3)};
	public static final Number[] ARR_1 = {Integer.valueOf(1)};

	public static final Double D0 = Double.valueOf(0.0);
	public static final Double D1 = Double.valueOf(1.0);
	public static final Double D2 = Double.valueOf(2.0);
	public static final Double D3 = Double.valueOf(3.0);
	public static final Double D4 = Double.valueOf(4.0);
	public static final Double D5 = Double.valueOf(5.0);
	public static final Double D6 = Double.valueOf(6.0);

	public static final Double D3_2 = Double.valueOf(3.2);

	public static final Integer I0 = Integer.valueOf(0);
	public static final Integer I1 = Integer.valueOf(1);
	public static final Integer I2 = Integer.valueOf(2);
	public static final Integer I3 = Integer.valueOf(3);
	public static final Integer I4 = Integer.valueOf(4);
	public static final Integer I5 = Integer.valueOf(5);
	public static final Integer I6 = Integer.valueOf(6);

	public static final Long L1 = Long.valueOf(1);
	public static final Long L2 = Long.valueOf(2);

	public static final Character CONST_A = 'A';
	public static final Character CONST_B = 'B';
	public static final Character CONST_C = 'C';
	public static final Character CONST_D = 'D';
	public static final Character CONST_E = 'E';
	public static final Character CONST_F = 'F';
	public static final Character CONST_G = 'G';
	public static final Character CONST_H = 'H';

	public static final String S1 = "S1";
	public static final String S2 = "S2";
	public static final String S3 = "S3";
	public static final String S4 = "S4";
	public static final String S5 = "S5";
	public static final String S6 = "S6";
	public static final String S7 = "S7";
	public static final String S8 = "S8";
	public static final String S9 = "S9";

	public static final ArrayFormatFactory ARRAY_FACTORY = new ArrayFormatFactory('\n', ',');
	public static final ArrayFormatManager<Number> NUMBER_ARRAY_MANAGER =
			new ArrayFormatManager<>(new NumberManager(), '\n', ',');

	public static final CompoundFormatFactory COMPOUND_MANAGER = new CompoundFormatFactory(',', '|');

	private TestSupport()
	{
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
}

