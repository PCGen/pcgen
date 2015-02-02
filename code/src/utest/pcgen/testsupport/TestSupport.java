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

import pcgen.core.Globals;
import pcgen.rules.context.AbstractReferenceContext;
import plugin.lsttokens.testsupport.BuildUtilities;

/**
 * Support class for running Junit tests
 */
public class TestSupport
{

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
		catch (InvocationTargetException ite)
		{
			System.err.println("Instance creation failed with [" + ite.getCause() + "]");
		}
		catch (IllegalAccessException iae)
		{
			System.err.println("Instance creation failed due to access violation.");
		}
		catch (InstantiationException ie)
		{
			System.err.println("Instance creation failed with [" + ie.getCause() + "]");
		}
		
		return instance;
	}

	public static void createAllAlignments()
	{
		AbstractReferenceContext ref = Globals.getContext().getReferenceContext();
		ref.importObject(BuildUtilities.createAlignment("Lawful Good", "LG"));
		ref.importObject(BuildUtilities.createAlignment("Lawful Neutral", "LN"));
		ref.importObject(BuildUtilities.createAlignment("Lawful Evil", "LE"));
		ref.importObject(BuildUtilities.createAlignment("Neutral Good", "NG"));
		ref.importObject(BuildUtilities.createAlignment("True Neutral", "TN"));
		ref.importObject(BuildUtilities.createAlignment("Neutral Evil", "NE"));
		ref.importObject(BuildUtilities.createAlignment("Chaotic Good", "CG"));
		ref.importObject(BuildUtilities.createAlignment("Chaotic Neutral", "CN"));
		ref.importObject(BuildUtilities.createAlignment("Chaotic Evil", "CE"));
		ref.importObject(BuildUtilities.createAlignment("None", "NONE"));
		ref.importObject(BuildUtilities.createAlignment("Deity's", "Deity"));
	}	
}
